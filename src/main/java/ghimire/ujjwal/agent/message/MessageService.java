package ghimire.ujjwal.agent.message;

import ghimire.ujjwal.agent.llm.ModelMessage;
import ghimire.ujjwal.agent.session.Session;

import java.util.List;
import java.util.Optional;

public interface MessageService {
    List<Message> getAllMessage(Session session);

    Message saveModelMessage(ModelMessage modelMessage, Session session);

    Optional<Message> getLastMessage(Long sessionId, String status);
}
