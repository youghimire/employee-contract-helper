package ghimire.ujjwal.agent.postProcess;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class EmploymentInformation {

    private String countryOfWork;
    private Boolean visaCompliance;
    private Integer workHourPerWeek;
    private LocalDate contractStartDate;
    private String employmentTerms;
    private LocalDate contractEndDate;
    private Integer timeOff;
    private Integer probationPeriod;
    private Integer noticePeriodDuringProbation;
    private Integer noticePeriodAfterProbation;
    private Double compensation;

    public interface EmploymentTerms {
        String DEFINITE = "Definite";
        String INDEFINITE = "Indefinite";
    }
}
