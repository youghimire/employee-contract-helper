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
        String ACTIVE = "ACTIVE";
        String DELETE = "DELETED";
    }
    public Session(String title) {
        this.created = LocalDateTime.now();
        this.status = STATUS.ACTIVE;
        this.title = title;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    LocalDateTime created;

    @Column
    String status;

    @Column
    String title;

}
