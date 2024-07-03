package ghimire.ujjwal.agent.session;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Session {

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
