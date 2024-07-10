package ghimire.ujjwal.agent.message;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.session.id = ?1 AND (m.status = ?2 or m.status is null) order by m.created asc")
    List<Message> getAllBySessionId(Long sessionId, String status);

    @Query("SELECT m FROM Message m where m.session.id = ?1 and m.status = ?2")
    List<Message> getLastMessage(Long sessionId, String status, Pageable pageable);
}
