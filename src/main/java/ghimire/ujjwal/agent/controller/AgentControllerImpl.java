package ghimire.ujjwal.agent.controller;

import ghimire.ujjwal.agent.contract.ContractService;
import ghimire.ujjwal.agent.llm.LLMHandler;
import ghimire.ujjwal.agent.llm.ModelMessage;
import ghimire.ujjwal.agent.message.Message;
import ghimire.ujjwal.agent.message.MessageService;
import ghimire.ujjwal.agent.postProcess.EmploymentInformation;
import ghimire.ujjwal.agent.postProcess.GeneralInformation;
import ghimire.ujjwal.agent.postProcess.ValidateInformation;
import ghimire.ujjwal.agent.resources.dtos.MessageDTO;
import ghimire.ujjwal.agent.session.Session;
import ghimire.ujjwal.agent.session.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgentControllerImpl implements AgentController {

    private static final Logger log = LoggerFactory.getLogger(AgentControllerImpl.class);

    private final LLMHandler mlHandler;

    private final MessageService messageService;

    private final SessionService sessionService;

    private final ContractService contractService;

    @Autowired
    public AgentControllerImpl(LLMHandler mlHandler, MessageService messageService, SessionService sessionService, ContractService contractService ) {
        this.mlHandler = mlHandler;
        this.messageService = messageService;
        this.sessionService = sessionService;
        this.contractService = contractService;
    }

    @Override
    public MessageDTO processQuery(String appToken, MessageDTO agentRequest) throws IOException {
        Session session;
        if(agentRequest.getSessionId() == null) {
            return initiateFirstStage(agentRequest);
        }
        session = sessionService.findById(agentRequest.getSessionId()).orElseThrow();
        List<ModelMessage> sessionHistory = getSessionHistory(session);

        checkContextSizeThenAddQuestion(agentRequest, session, sessionHistory);

        ModelMessage aiResponse = mlHandler.handleQuery(sessionHistory, session);

        if(ValidateInformation.doContainsJson(aiResponse.getContent())) {
            return Session.STATUS.STAGE1.equals(session.getStatus()) ?
                    finalizeStageOne(appToken, aiResponse, session) :
                    finalizeStageTwo(appToken, aiResponse, session);
        } else {
            messageService.saveModelMessage(aiResponse, session);
            return new MessageDTO(aiResponse.getContent(), session.getId());
        }
    }

    private MessageDTO finalizeStageTwo(String appToken, ModelMessage aiResponse, Session session) {
        try {
            EmploymentInformation employmentInformation = ValidateInformation.mapTo(aiResponse.getContent(), EmploymentInformation.class);
            Message message = messageService.getLastMessage(session.getId(), Session.STATUS.STAGE1);
            Assert.notNull(message, "Last response about General information did not found");
            GeneralInformation generalInformation = ValidateInformation.mapTo(message.getContent(), GeneralInformation.class);
            String contractId = contractService.processFinalRequest(employmentInformation, generalInformation, appToken, session.getContractId());
            log.info("Contract updated for contract Id {}", contractId);
            session.setStatus(Session.STATUS.COMPLETED);
            session = sessionService.saveSession(session);
            messageService.saveModelMessage(aiResponse, session);
            return new MessageDTO("Successfully completed Contract for this Employee. Thank you for your patience.", session.getId());
        } catch (Exception e) {
            log.error("Error updating contract data ", e);
            session.setStatus(Session.STATUS.ERROR);
            session = sessionService.saveSession(session);
            return new MessageDTO("Error saving the provided information! Please try again.", session.getId());
        }
    }

    private void checkContextSizeThenAddQuestion(MessageDTO agentRequest, Session session, List<ModelMessage> sessionHistory) {
        String question = agentRequest.getContent();
        //If the interaction is larger than expected then ask to force result
        int STAGE1_FORCE_RESULT_AFTER = 12;
        int STAGE2_FORCE_RESULT_AFTER = 24;
        if((Session.STATUS.STAGE1.equals(session.getStatus()) && sessionHistory.size() > STAGE1_FORCE_RESULT_AFTER)
        || Session.STATUS.STAGE2.equals(session.getStatus()) && sessionHistory.size() > STAGE2_FORCE_RESULT_AFTER) {
            question += "\r\nIf you got all the necessary information from user to create json then provide the Json result; other-wise ask the user about specific information needed.";
        }
        ModelMessage currentQuery = new ModelMessage(ModelMessage.ROLE.USER, question);
        messageService.saveModelMessage(currentQuery, session);
        sessionHistory.add(currentQuery);
    }

    private MessageDTO initiateFirstStage(MessageDTO agentRequest) {
        Session session;
        log.debug("Creating new session ");
        session = sessionService.saveSession(new Session(agentRequest.getContent()));
        Message initialMessage = messageService.saveModelMessage(new ModelMessage(ModelMessage.ROLE.ASSISTANT, "Can you provide me your employee name?"), session);
        return new MessageDTO(initialMessage.getContent(), session.getId());
    }

    private MessageDTO finalizeStageOne(String appToken, ModelMessage aiResponse, Session session) {
        try {
            GeneralInformation generalInformation = ValidateInformation.mapTo(aiResponse.getContent(), GeneralInformation.class);
            String contractId = contractService.processInitialRequest(generalInformation, appToken);
            log.info("Contract saved with Id {}", contractId);
            session.setContractId(contractId);
            messageService.saveModelMessage(aiResponse, session);
            return startSecondPhase(session, generalInformation);
        } catch (Exception e) {
            log.error("Error saving contract initial data ", e);
            session.setStatus(Session.STATUS.ERROR);
            session = sessionService.saveSession(session);
            return new MessageDTO("Error saving the provided information! Please try again.", session.getId());
        }
    }

    private MessageDTO startSecondPhase(Session session, GeneralInformation generalInformation) {
        session.setStatus(Session.STATUS.STAGE2);
        session = sessionService.saveSession(session);

        String isoWorkCountry = generalInformation.getCountryOfWorkISOAlpha2();
        String workCountryName = ValidateInformation.getCountryName(isoWorkCountry);
        List<ModelMessage> initialMessage = Arrays.asList(
                new ModelMessage(ModelMessage.ROLE.ASSISTANT, "Can you provide me your employee's work country?"),
                new ModelMessage(ModelMessage.ROLE.USER, "He will be working from %s".formatted(workCountryName)),
                new ModelMessage(ModelMessage.ROLE.ASSISTANT, "Is the employee authorized to work from %s?".formatted(workCountryName)));
        if(isoWorkCountry.equals(generalInformation.getCountryOfCitizenISOAlpha2())) {
            initialMessage = new ArrayList<>(initialMessage);
            initialMessage.add(new ModelMessage(ModelMessage.ROLE.USER, "Yes, he is authorized to work from %s.".formatted(workCountryName)));
            initialMessage.add(new ModelMessage(ModelMessage.ROLE.ASSISTANT, "What will be the standard weekly work hour of the employee?"));
        }
        messageService.saveModelMessages(initialMessage, session);

        return new MessageDTO(("""
                Thank you for your patience. General information of your employee saved to our platform successfully.\r
                Now we need some additional information to complete the contract.\r
                %s""").formatted(initialMessage.get(initialMessage.size() -1).getContent()), session.getId());
    }

    public List<ModelMessage> getSessionHistory(Session session) throws IOException {

        List<Message> history = messageService.getAllMessage(session);
        List<ModelMessage> modelMessages = history.stream().map(m -> new ModelMessage(m.getRole(), m.getContent())).collect(Collectors.toList());
        String instructionFile = Session.STATUS.STAGE2.equals(session.getStatus()) ? "src/main/resources/agent2.context.txt" : "src/main/resources/agent1.context.txt";
        modelMessages.add(0, getInstruction(instructionFile));
        return modelMessages;
    }

    public ModelMessage getInstruction(String fileName) throws IOException {
        byte[] contextBArray = Files.readAllBytes(Paths.get(fileName));
        return new ModelMessage(ModelMessage.ROLE.SYSTEM, new String(contextBArray));
    }
}
