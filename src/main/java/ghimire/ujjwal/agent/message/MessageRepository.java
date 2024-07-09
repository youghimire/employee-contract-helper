package ghimire.ujjwal.agent.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.session.id = ?1 AND (m.status = ?2 or m.status is null) order by m.created desc")
    List<Message> getAllBySessionId(Long sessionId, String status);

    @Modifying
    @Transactional
    @Query("update Message m set m.status = ?2 where m.session.id = ?1")
    void updateMessageStatus(Long id, String status);

    @Query(value = "SELECT m FROM messages m where m.session_id = ?1 and m.status = ?2 order by m.created desc limit 1", nativeQuery = true)
    Message getLastMessage(Long sessionId, String status);
}
