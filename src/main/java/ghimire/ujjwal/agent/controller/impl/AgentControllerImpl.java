package ghimire.ujjwal.agent.controller.impl;

import ghimire.ujjwal.agent.controller.AgentController;
import ghimire.ujjwal.agent.controller.MLHandler;
import ghimire.ujjwal.agent.dtos.AgentRequest;
import ghimire.ujjwal.agent.dtos.AgentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class AgentControllerImpl implements AgentController {

    private static final Logger log = LoggerFactory.getLogger(AgentControllerImpl.class);

    @Autowired
    MLHandler mlHandler;

    @Override
    public AgentResponse processQuery(String appToken, AgentRequest agentRequest) {
//        @ToDo need to add context retention logic
        String responseString = mlHandler.handleQuery(agentRequest.getQuestion(), getAgentContext());
        return new AgentResponse(responseString);
    }

    public String getAgentContext() {
        try {
            byte[] contextBArray = Files.readAllBytes(Paths.get("src/main/resources/agent1.context.txt"));
            return new String(contextBArray);
        }catch(Exception e){
            log.error("Error reading context file ", e);
        }
        return "";
    }
}
