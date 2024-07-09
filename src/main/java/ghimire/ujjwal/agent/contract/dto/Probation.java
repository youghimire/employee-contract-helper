package ghimire.ujjwal.agent.contract.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Probation {
    public Probation(Integer value) {
        this.value = value;
    }

    private String noticePeriodMethod = "STANDARD";
    private Integer value;
}
