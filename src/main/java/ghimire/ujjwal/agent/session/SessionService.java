package ghimire.ujjwal.agent.session;

import java.util.List;

public interface SessionService {
    List<Session> getAllSession();

    Session createSession(Session session);
}
