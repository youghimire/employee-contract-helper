package ghimire.ujjwal.agent.contract.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Probation {

    private String noticePeriodMethod = "STANDARD";
    private Integer value;
}
