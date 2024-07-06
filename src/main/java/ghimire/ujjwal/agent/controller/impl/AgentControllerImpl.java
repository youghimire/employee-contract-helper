package ghimire.ujjwal.agent.controller.impl;

import ghimire.ujjwal.agent.controller.AgentController;
import ghimire.ujjwal.agent.integration.MLHandler;
import ghimire.ujjwal.agent.integration.ModelMessage;
import ghimire.ujjwal.agent.message.Message;
import ghimire.ujjwal.agent.message.MessageService;
import ghimire.ujjwal.agent.rest.dtos.AgentRequest;
import ghimire.ujjwal.agent.rest.dtos.AgentResponse;
import ghimire.ujjwal.agent.session.Session;
import ghimire.ujjwal.agent.session.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgentControllerImpl implements AgentController {

    private static final Logger log = LoggerFactory.getLogger(AgentControllerImpl.class);

    @Autowired
    MLHandler mlHandler;

    @Autowired
    MessageService messageService;

    @Autowired
    SessionService sessionService;

    @Override
    public AgentResponse processQuery(String appToken, AgentRequest agentRequest) {
        Long sessionId = agentRequest.getSessionId();
        if(sessionId == null) {
            sessionId = createNewSession(agentRequest);
        }
        List<ModelMessage> previousMessages = getSessionHistory(sessionId);
        ModelMessage currentQuery = new ModelMessage("user", agentRequest.getQuestion());
        previousMessages.add(currentQuery);
        ModelMessage aiResponse = mlHandler.handleQuery(previousMessages);
        messageService.saveModelMessages(Arrays.asList(currentQuery, aiResponse), sessionId);
        return new AgentResponse(aiResponse.getContent());
    }

    private Long createNewSession(AgentRequest agentRequest) {
        Session session = sessionService.createSession(new Session(agentRequest.getQuestion()));
        messageService.saveModelMessages(Collections.singletonList(new ModelMessage("assistant", "Can you provide me your employee name?")), session.getId());
        return session.getId();
    }

    public List<ModelMessage> getSessionHistory(Long sessionId) {

        List<Message> history = messageService.getAllMessage(sessionId);
        List<ModelMessage> modelMessages = history.stream().map(m -> new ModelMessage(m.getRole(), m.getContent())).collect(Collectors.toList());
        try {
            byte[] contextBArray = Files.readAllBytes(Paths.get("src/main/resources/agent1.context.txt"));
            modelMessages.add(0, new ModelMessage("system", new String(contextBArray)));
        }catch(Exception e){
            log.error("Error reading context file ", e);
        }
        return modelMessages;
    }
}
