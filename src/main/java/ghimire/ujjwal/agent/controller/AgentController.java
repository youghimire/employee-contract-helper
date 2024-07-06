package ghimire.ujjwal.agent.controller;

import ghimire.ujjwal.agent.integration.ModelMessage;
import ghimire.ujjwal.agent.rest.dtos.AgentRequest;
import ghimire.ujjwal.agent.rest.dtos.AgentResponse;

import java.util.List;

public interface AgentController {
    AgentResponse processQuery(String token, AgentRequest agentRequest);
    List<ModelMessage> getSessionHistory(Long sessionId);
}
