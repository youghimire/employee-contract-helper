package ghimire.ujjwal.agent.controller.impl;

import ghimire.ujjwal.agent.controller.AgentController;
import ghimire.ujjwal.agent.integration.MLHandler;
import ghimire.ujjwal.agent.integration.ModelMessage;
import ghimire.ujjwal.agent.integration.gemma.GemmaMessage;
import ghimire.ujjwal.agent.message.Message;
import ghimire.ujjwal.agent.message.MessageService;
import ghimire.ujjwal.agent.rest.dtos.AgentRequest;
import ghimire.ujjwal.agent.rest.dtos.AgentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgentControllerImpl implements AgentController {

    private static final Logger log = LoggerFactory.getLogger(AgentControllerImpl.class);

    @Autowired
    MLHandler mlHandler;

    @Autowired
    MessageService messageService;

    @Override
    public AgentResponse processQuery(String appToken, AgentRequest agentRequest) {
        List<ModelMessage> previousMessages = getAgentContext(agentRequest.getSessionId());
        previousMessages.add(new GemmaMessage("user", agentRequest.getQuestion()));
        String responseString = mlHandler.handleQuery(previousMessages);
        return new AgentResponse(responseString);
    }

    public List<ModelMessage> getAgentContext(Long sessionId) {

        List<Message> history = messageService.getAllMessage(sessionId);
        List<ModelMessage> modelMessages = history.stream().map(m -> new GemmaMessage(m.getRole(), m.getContent())).collect(Collectors.toList());
        try {
            byte[] contextBArray = Files.readAllBytes(Paths.get("src/main/resources/agent1.context.txt"));
            modelMessages.add(0, new GemmaMessage("model", new String(contextBArray)));
        }catch(Exception e){
            log.error("Error reading context file ", e);
        }
        return modelMessages;
    }
}
