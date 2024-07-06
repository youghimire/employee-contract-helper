package ghimire.ujjwal.agent.controller;

import ghimire.ujjwal.agent.llm.ModelMessage;
import ghimire.ujjwal.agent.resources.dtos.MessageDTO;

import java.io.IOException;
import java.util.List;

public interface AgentController {
    MessageDTO processQuery(String token, MessageDTO agentRequest) throws IOException;
    List<ModelMessage> getSessionHistory(Long sessionId) throws IOException;
}
