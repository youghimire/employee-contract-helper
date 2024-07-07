package ghimire.ujjwal.agent.session;

import java.util.List;
import java.util.Optional;

public interface SessionService {
    List<Session> getAllSession();

    Session saveSession(Session session);

    Optional<Session> findById(Long sessionId);
}
