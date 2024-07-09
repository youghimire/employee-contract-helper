package ghimire.ujjwal.agent.session;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
public class Session {

    public interface STATUS {
        String STAGE1 = "ACTIVE";
        String STAGE2 = "IN_PROGRESS";
        String ERROR = "ERROR";
        String COMPLETED = "COMPLETED";
        String DELETED = "DELETED";
    }
    public Session(String title) {
        this.created = LocalDateTime.now();
        this.status = STATUS.STAGE1;
        this.title = title;
    }
    @Id
    @GeneratedValue(generator="session_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name="session_seq",sequenceName="session_seq", allocationSize=1)
    private Long id;

    @Column
    LocalDateTime created;

    @Column
    String status;

    @Column
    String title;

    @Column
    String contractId;

}
