package ghimire.ujjwal.agent.contract;

import ghimire.ujjwal.agent.contract.dto.CountryISO;
import ghimire.ujjwal.agent.postProcess.GeneralInformation;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class UpdateRequestNoCompliance {
    public UpdateRequestNoCompliance(GeneralInformation generalInformation) {
        contractTitle = "%s - %s".formatted(generalInformation.getFullName(), generalInformation.getJobTitle());
        employeeFirstName = generalInformation.getFirstName();
        employeeMiddleName = generalInformation.getMiddleName();
        employeeLastName = generalInformation.getLastName();
        employeeEmail = generalInformation.getEmail();
        jobTitle = generalInformation.getJobTitle();
        scopeOfWork = generalInformation.getScopeOfWork();
        workLocationCountry = new CountryISO(generalInformation.getCountryOfWorkISOAlpha2());
        countryOfCitizenship = new CountryISO(generalInformation.getCountryOfCitizenISOAlpha2());
        employeeCurrentlyResidingCountry = countryOfCitizenship;
    }

    private Boolean niuralVisaSupportRequired = true;
    private String highestEducationLevel = "MASTER_DEGREE";
    private String contractStartDate = LocalDate.now().plusDays(6).format(DateTimeFormatter.ISO_LOCAL_DATE);

    String contractType = "EOR";
    private String niuralEntityToUse = "NIURAL_INC";
    private String eorEmployeeId = "7a633c91-fcef-4b4f-a128-fd90ad8d9fb6";
    private String contractTitle;
    private String entityRepresentativeName = "Zeppy Test";
    private String employeeFirstName;
    private String employeeLastName;
    private String employeeEmail;
    private String entityRepresentativeEmail = "suraj@niural.com";
    private String jobTitle;
    private String scopeOfWork;
    private CountryISO workLocationCountry;
    private CountryISO countryOfCitizenship;
    private CountryISO employeeCurrentlyResidingCountry;

    private Integer workHoursPerWeek = 40;
    private Integer timeOffDays = 10;
    private Integer probationPeriod = 0;

    private String employmentType = "FULL_TIME";
    private String compensationType = "ANNUAL";
    private Double signingBonus = null;
    private Boolean isWorkEligibilityDocumentRequired = true;
    private Boolean hasWorkEligibilityDocument = false;
    private String employeeMiddleName;
}
