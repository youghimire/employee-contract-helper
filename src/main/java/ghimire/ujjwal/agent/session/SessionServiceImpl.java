package ghimire.ujjwal.agent.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionServiceImpl implements SessionService{

    @Autowired
    private SessionRepository sessionRepository;

    @Override
    public List<Session> getAllSession() {
        return sessionRepository.findAll();
    }

    @Override
    public Session createSession(Session session) {
        return sessionRepository.save(session);
    }
}
