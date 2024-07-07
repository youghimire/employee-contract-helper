package ghimire.ujjwal.agent.contract.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticePeriod {

    private String periodType = "CUSTOM";
    private Probation afterProbation;
    private String noticePeriodUnit = "DAY";
    private Probation duringProbation;
}
