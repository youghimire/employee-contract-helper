package ghimire.ujjwal.agent.message;

import ghimire.ujjwal.agent.llm.ModelMessage;
import ghimire.ujjwal.agent.session.Session;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "messages")
public class Message {

    public Message(ModelMessage modelMessage, Long sessionId) {
        session = new Session();
        session.setId(sessionId);
        created = LocalDateTime.now();
        role = modelMessage.getRole();
        content = modelMessage.getContent();
    }
    @Id
    @GeneratedValue(generator="message_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name="message_seq",sequenceName="message_seq", allocationSize=1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private Session session;

    @Column
    LocalDateTime created;

    @Column
    String role;

    @Column
    String content;

}
