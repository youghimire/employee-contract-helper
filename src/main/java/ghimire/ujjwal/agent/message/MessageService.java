package ghimire.ujjwal.agent.message;

import ghimire.ujjwal.agent.integration.ModelMessage;

import java.util.List;

public interface MessageService {
    List<Message> getAllMessage(Long sessionId);

    Message saveMessage(Message message);

    List<Message> saveModelMessages(List<ModelMessage> modelMessages, Long sessionId);
}
