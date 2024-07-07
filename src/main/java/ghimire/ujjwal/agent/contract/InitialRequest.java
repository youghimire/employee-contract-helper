package ghimire.ujjwal.agent.contract;

import ghimire.ujjwal.agent.contract.dto.Address;
import ghimire.ujjwal.agent.contract.dto.CountryISO;
import ghimire.ujjwal.agent.postProcess.GeneralInformation;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InitialRequest {
    public InitialRequest(GeneralInformation generalInformation) {
        contractTitle = generalInformation.getFullName();
        employeeFirstName = generalInformation.getFirstName();
        employeeMiddleName = generalInformation.getMiddleName();
        employeeLastName = generalInformation.getLastName();
        employeeEmail = generalInformation.getEmail();
        jobTitle = generalInformation.getJobTitle();
        scopeOfWork = generalInformation.getScopeOfWork();
        workLocationCountry = new CountryISO(generalInformation.getCountryOfWorkISOAlpha2());
        countryOfCitizenship = new CountryISO(generalInformation.getCountryOfCitizenISOAlpha2());
    }

    String contractType = "EOR";
    private String contractTitle;
    private String entityName = "Niural Inc";
    private CountryISO countryOfCitizenship;
    private String employeeEmail;
    private String employeeFirstName;
    private String employeeLastName;
    private String employeeMiddleName;
    private Address entityIncorporationLocation = new Address();
    private String entityRepresentativeEmail = "suraj@niural.com";
    private String entityRepresentativeName = "Zeppy Test";
    private String entityType = "Limited liability company (LLC)";
    private String jobTitle;
    private String niuralEntityToUse = "NIURAL_INC";
    private String scopeOfWork;
    private CountryISO workLocationCountry;
}
