package ghimire.ujjwal.agent.controller;

import ghimire.ujjwal.agent.contract.ContractService;
import ghimire.ujjwal.agent.llm.LLMHandler;
import ghimire.ujjwal.agent.llm.ModelMessage;
import ghimire.ujjwal.agent.message.Message;
import ghimire.ujjwal.agent.message.MessageService;
import ghimire.ujjwal.agent.postProcess.GeneralInformation;
import ghimire.ujjwal.agent.postProcess.ValidateInformation;
import ghimire.ujjwal.agent.resources.dtos.MessageDTO;
import ghimire.ujjwal.agent.session.Session;
import ghimire.ujjwal.agent.session.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
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
        List<ModelMessage> sessionHistory = getSessionHistory(session.getId());

        addQuestionToContext(agentRequest, session, sessionHistory);

        ModelMessage aiResponse = mlHandler.handleQuery(sessionHistory);

        if(ValidateInformation.doContainsJson(aiResponse.getContent())) {
            return finalizeStageOne(appToken, aiResponse, session);
        } else {
            messageService.saveModelMessages(List.of(aiResponse), session.getId());
            return new MessageDTO(aiResponse.getContent(), session.getId());
        }

    }

    private void addQuestionToContext(MessageDTO agentRequest, Session session, List<ModelMessage> sessionHistory) {
        ModelMessage currentQuery = new ModelMessage("user", agentRequest.getContent());
        //May be hallucinating ask to force result
        int STAGE1_FORCE_RESULT_AFTER = 12;
        if(Session.STATUS.STAGE1.equals(session.getStatus()) && sessionHistory.size() > STAGE1_FORCE_RESULT_AFTER) {
            currentQuery.setContent(currentQuery.getContent() + "; If you have all the information then please provide the Json response.");
        }
        messageService.saveModelMessages(List.of(currentQuery), session.getId());
        sessionHistory.add(currentQuery);
    }

    private MessageDTO initiateFirstStage(MessageDTO agentRequest) {
        Session session;
        log.debug("Creating new session ");
        session = sessionService.saveSession(new Session(agentRequest.getContent()));
        List<Message> initialMessage = messageService.saveModelMessages(Collections.singletonList(new ModelMessage("assistant", "Can you provide me your employee name?")), session.getId());
        return new MessageDTO(initialMessage.get(0).getContent(), session.getId());
    }

    private MessageDTO finalizeStageOne(String appToken, ModelMessage aiResponse, Session session) {
        try {
            GeneralInformation generalInformation = ValidateInformation.mapTo(aiResponse.getContent(), GeneralInformation.class);
            String contractId = contractService.processInitialRequest(generalInformation, appToken);
            log.info("Contract saved with Id {}", contractId);
            session.setContractId(contractId);
            session.setStatus(Session.STATUS.STAGE2);
            session = sessionService.saveSession(session);
            return startSecondPhase(session, generalInformation);
        } catch (Exception e) {
            log.error("Error saving contract initial data ", e);
            session.setStatus(Session.STATUS.ERROR);
            session = sessionService.saveSession(session);
            return new MessageDTO("Error saving the provided information! Please try again.", session.getId());
        }
    }

    private MessageDTO startSecondPhase(Session session, GeneralInformation generalInformation) {
        return new MessageDTO("Thank you for your assistance. First Stage completed successfully.", session.getId());
    }

    public List<ModelMessage> getSessionHistory(Long sessionId) throws IOException {

        List<Message> history = messageService.getAllMessage(sessionId);
        List<ModelMessage> modelMessages = history.stream().map(m -> new ModelMessage(m.getRole(), m.getContent())).collect(Collectors.toList());
        modelMessages.add(0, getInstruction("src/main/resources/agent1.context.txt"));
        return modelMessages;
    }

    public ModelMessage getInstruction(String fileName) throws IOException {
        byte[] contextBArray = Files.readAllBytes(Paths.get(fileName));
        return new ModelMessage("system", new String(contextBArray));
    }
}
