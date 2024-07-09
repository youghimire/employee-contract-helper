package ghimire.ujjwal.agent.contract.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NoticePeriod {
    public NoticePeriod(Probation duringProbation, Probation afterProbation) {
        this.duringProbation = duringProbation;
        this.afterProbation = afterProbation;
    }

    private String periodType = "CUSTOM";
    private Probation afterProbation;
    private String noticePeriodUnit = "DAY";
    private Probation duringProbation;
}
