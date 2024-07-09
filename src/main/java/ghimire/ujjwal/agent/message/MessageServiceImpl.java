package ghimire.ujjwal.agent.message;

import ghimire.ujjwal.agent.llm.ModelMessage;
import ghimire.ujjwal.agent.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public List<Message> getAllMessage(Session session) {
        return messageRepository.getAllBySessionId(session.getId(), session.getStatus());
    }

    @Override
    public Message saveModelMessage(ModelMessage modelMessage, Session session) {
        return messageRepository.save(new Message(modelMessage, session));
    }

    @Override
    public void saveModelMessages(List<ModelMessage> modelMessages, Session session) {
        List<Message> messages = modelMessages.stream().map(mm -> new Message(mm, session)).toList();
        messageRepository.saveAll(messages);
    }

    @Override
    public Message getLastMessage(Long sessionId, String status) {
        return messageRepository.getLastMessage(sessionId, status);
    }
}
