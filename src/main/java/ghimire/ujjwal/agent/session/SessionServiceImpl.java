package ghimire.ujjwal.agent.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SessionServiceImpl implements SessionService{

    private final SessionRepository sessionRepository;
    @Autowired
    public SessionServiceImpl(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public List<Session> getAllSession() {
        return sessionRepository.findAll();
    }

    @Override
    public Session saveSession(Session session) {
        return sessionRepository.save(session);
    }

    @Override
    public Optional<Session> findById(Long sessionId) {
        return sessionRepository.findById(sessionId);
    }
}
