package ghimire.ujjwal.agent.message;

import java.util.List;

public interface MessageService {
    List<Message> getAllMessage(Long sessionId);

    Message createMessage(Message message);
}
