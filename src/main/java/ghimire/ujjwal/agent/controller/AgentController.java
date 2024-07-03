package ghimire.ujjwal.agent.controller;

import ghimire.ujjwal.agent.rest.dtos.AgentRequest;
import ghimire.ujjwal.agent.rest.dtos.AgentResponse;

public interface AgentController {
    AgentResponse processQuery(String token, AgentRequest agentRequest);
    String getAgentContext();
}
