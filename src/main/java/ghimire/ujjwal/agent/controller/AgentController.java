package ghimire.ujjwal.agent.controller;

import ghimire.ujjwal.agent.dtos.AgentRequest;
import ghimire.ujjwal.agent.dtos.AgentResponse;

public interface AgentController {
    AgentResponse processQuery(String token, AgentRequest agentRequest);
    String getAgentContext();
}
