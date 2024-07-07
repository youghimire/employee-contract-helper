package ghimire.ujjwal.agent.postProcess;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GeneralInformation implements EmployeeInformation{
    String firstName;
    String middleName;
    String lastName;
    String email;
    String countryOfCitizenISOAlpha2;
    String countryOfWorkISOAlpha2;
    String jobTitle;
    String scopeOfWork;
}
