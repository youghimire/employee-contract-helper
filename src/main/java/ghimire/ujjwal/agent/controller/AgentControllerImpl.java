package ghimire.ujjwal.agent.controller;

import ghimire.ujjwal.agent.llm.MLHandler;
import ghimire.ujjwal.agent.llm.ModelMessage;
import ghimire.ujjwal.agent.message.Message;
import ghimire.ujjwal.agent.message.MessageService;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgentControllerImpl implements AgentController {

    private static final Logger log = LoggerFactory.getLogger(AgentControllerImpl.class);

    private final MLHandler mlHandler;

    private final MessageService messageService;

    private final SessionService sessionService;

    @Autowired
    public AgentControllerImpl(MLHandler mlHandler, MessageService messageService, SessionService sessionService ) {
        this.mlHandler = mlHandler;
        this.messageService = messageService;
        this.sessionService = sessionService;
    }

    @Override
    public MessageDTO processQuery(String appToken, MessageDTO agentRequest) throws IOException {
        Long sessionId = agentRequest.getSessionId();
        if(sessionId == null) {
            log.debug("Creating new session ");
            sessionId = createNewSession(agentRequest);
        }
        List<ModelMessage> sessionHistory = getSessionHistory(sessionId);

        ModelMessage currentQuery = new ModelMessage("user", agentRequest.getContent());
        sessionHistory.add(currentQuery);

        ModelMessage aiResponse = mlHandler.handleQuery(sessionHistory);

        messageService.saveModelMessages(Arrays.asList(currentQuery, aiResponse), sessionId);
        return new MessageDTO(aiResponse.getContent(), sessionId);
    }



    private Long createNewSession(MessageDTO agentRequest) {
        Session session = sessionService.createSession(new Session(agentRequest.getContent()));
        messageService.saveModelMessages(Collections.singletonList(new ModelMessage("assistant", "Can you provide me your employee name?")), session.getId());
        return session.getId();
    }

    public List<ModelMessage> getSessionHistory(Long sessionId) throws IOException {

        List<Message> history = messageService.getAllMessage(sessionId);
        List<ModelMessage> modelMessages = history.stream().map(m -> new ModelMessage(m.getRole(), m.getContent())).collect(Collectors.toList());
        modelMessages.add(0, getInstruction());
        return modelMessages;
    }

    public ModelMessage getInstruction() throws IOException {
        byte[] contextBArray = Files.readAllBytes(Paths.get("src/main/resources/agent1.context.txt"));
        return new ModelMessage("system", new String(contextBArray));
    }
}
