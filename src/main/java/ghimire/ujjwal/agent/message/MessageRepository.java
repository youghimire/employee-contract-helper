package ghimire.ujjwal.agent.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.session.id = ?1")
    List<Message> getAllBySessionId(Long sessionId);
}
