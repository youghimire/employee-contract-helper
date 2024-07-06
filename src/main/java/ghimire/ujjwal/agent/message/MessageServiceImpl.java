package ghimire.ujjwal.agent.message;

import ghimire.ujjwal.agent.llm.ModelMessage;
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
    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public List<Message> saveModelMessages(List<ModelMessage> modelMessages, Long sessionId) {
        List<Message> messages = modelMessages.stream().map(mm -> new Message(mm, sessionId)).toList();
        return messageRepository.saveAll(messages);
    }
}
