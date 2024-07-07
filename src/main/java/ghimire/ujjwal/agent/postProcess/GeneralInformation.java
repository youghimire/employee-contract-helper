package ghimire.ujjwal.agent.postProcess;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

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

    @JsonIgnore
    public String getFullName() {
        StringBuilder nameBuilder = new StringBuilder(firstName);
        nameBuilder.append(" ");
        if(StringUtils.isNotBlank(middleName)) {
            nameBuilder.append(middleName);
            nameBuilder.append(" ");
        }
        nameBuilder.append(lastName);
        return nameBuilder.toString();
    }
}
