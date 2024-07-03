package ghimire.ujjwal.agent.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public List<Message> getAllMessage(Long sessionId) {
        return messageRepository.getAllBySessionId(sessionId);
    }

    @Override
    public Message createMessage(Message message) {
        return messageRepository.save(message);
    }
}
