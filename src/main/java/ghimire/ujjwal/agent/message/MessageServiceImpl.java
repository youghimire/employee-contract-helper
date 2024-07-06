package ghimire.ujjwal.agent.message;

import ghimire.ujjwal.agent.integration.ModelMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        return messageRepository.saveAll(modelMessages.stream().map(mm -> new Message(mm, sessionId)).collect(Collectors.toSet()));
    }
}
