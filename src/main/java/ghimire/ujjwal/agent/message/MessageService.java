package ghimire.ujjwal.agent.message;

import ghimire.ujjwal.agent.llm.ModelMessage;
import ghimire.ujjwal.agent.session.Session;

import java.util.List;

public interface MessageService {
    List<Message> getAllMessage(Session session);

    Message saveMessage(Message message);

    Message saveModelMessage(ModelMessage modelMessage, Session session);
    void saveModelMessages(List<ModelMessage> modelMessages, Session session);

    void updateMessageStatus(Session session);

    Message getLastMessage(Long sessionId, String status);
}
