package ghimire.ujjwal.agent.contract;

import ghimire.ujjwal.agent.contract.dto.CountryISO;
import ghimire.ujjwal.agent.contract.dto.NoticePeriod;
import ghimire.ujjwal.agent.contract.dto.Probation;
import ghimire.ujjwal.agent.postProcess.EmploymentInformation;
import ghimire.ujjwal.agent.postProcess.GeneralInformation;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class UpdateRequest {
    public UpdateRequest(GeneralInformation generalInformation, EmploymentInformation employmentInformation) {
        contractTitle = "%s - %s".formatted(generalInformation.getFullName(), generalInformation.getJobTitle());
        employeeFirstName = generalInformation.getFirstName();
        employeeMiddleName = generalInformation.getMiddleName();
        employeeLastName = generalInformation.getLastName();
        employeeEmail = generalInformation.getEmail();
        jobTitle = generalInformation.getJobTitle();
        scopeOfWork = generalInformation.getScopeOfWork();
        workLocationCountry = new CountryISO(generalInformation.getCountryOfWorkISOAlpha2());
        countryOfCitizenship = new CountryISO(generalInformation.getCountryOfCitizenISOAlpha2());

        workHoursPerWeek = employmentInformation.getWorkHourPerWeek();
        compensationAmount = employmentInformation.getCompensation();
        contractStartDate = employmentInformation.getContractStartDate() != null ? employmentInformation.getContractStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : "";
        probationPeriod = employmentInformation.getProbationPeriod();
        timeOffDays = employmentInformation.getTimeOff();
        noticePeriod = new NoticePeriod(new Probation(employmentInformation.getNoticePeriodDuringProbation()), new Probation(employmentInformation.getNoticePeriodAfterProbation()));
    }

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
    private String employmentType = "FULL_TIME";
    private Integer workHoursPerWeek;
    private String compensationType = "ANNUAL";
    private Double compensationAmount;
    private Double signingBonus = null;
    private String contractStartDate;
    private Integer probationPeriod;
    private Integer timeOffDays;
    private Boolean isWorkEligibilityDocumentRequired = true;
    private Boolean hasWorkEligibilityDocument = true;
    private NoticePeriod noticePeriod;
    private String employeeMiddleName;
}
