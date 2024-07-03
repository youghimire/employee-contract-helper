package ghimire.ujjwal.agent.message;

import ghimire.ujjwal.agent.session.Session;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
