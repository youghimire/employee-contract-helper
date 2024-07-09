package ghimire.ujjwal.agent.contract;

import com.fasterxml.jackson.core.JsonProcessingException;
import ghimire.ujjwal.agent.postProcess.EmploymentInformation;
import ghimire.ujjwal.agent.postProcess.GeneralInformation;
import ghimire.ujjwal.agent.postProcess.ValidateInformation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ContractServiceImplTest {

    @Autowired
    private ContractService contractServiceImplUnderTest;

    @Value("${niural.token}")
    private static String TOKEN;

    @Test
    void testProcessSuccessRequest() throws JsonProcessingException {
        // Setup
        final GeneralInformation generalInformation = ValidateInformation.mapTo("{\"firstName\":\"Smith\", \"middleName\":\"\", \"lastName\":\"Flint\", \"email\":\"smith@gmail.com\", \"countryOfCitizenISOAlpha2\":\"IN\", \"countryOfWorkISOAlpha2\":\"US\", \"jobTitle\":\"Office assistant\", \"scopeOfWork\":\"a scope of work \\r\\n provide office assistant \\r\\n help customer \"}", GeneralInformation.class);

        // Run the test
        final String contractId = contractServiceImplUnderTest.processInitialRequest(generalInformation, TOKEN);

        // Verify the results
        assertThat(contractId).isNotBlank();

        final EmploymentInformation employmentInformation = ValidateInformation.mapTo("{\"countryOfWork\":\"Japan\", \"visaCompliance\":true, \"workHourPerWeek\":45, \"contractStartDate\":\"2024-09-15\", \"employmentTerms\":\"Indefinite\", \"contractEndDate\":\"\", \"timeOff\":12, \"probationPeriod\":30, \"noticePeriodDuringProbation\":45, \"noticePeriodAfterProbation\":45, \"compensation\":95000}", EmploymentInformation.class);

        final String contractId2 = contractServiceImplUnderTest.processFinalRequest(employmentInformation, generalInformation, TOKEN, contractId);

        assertThat(contractId2).isEqualTo(contractId);
    }

    @Test
    void testVisaComplianceFalse() throws JsonProcessingException {
        // Setup
        final GeneralInformation generalInformation = ValidateInformation.mapTo("{\"firstName\":\"Smith\", \"middleName\":\"\", \"lastName\":\"Flint\", \"email\":\"smith@gmail.com\", \"countryOfCitizenISOAlpha2\":\"IN\", \"countryOfWorkISOAlpha2\":\"US\", \"jobTitle\":\"Office assistant\", \"scopeOfWork\":\"a scope of work \\r\\n provide office assistant \\r\\n help customer \"}", GeneralInformation.class);

        // Run the test
        final String contractId = contractServiceImplUnderTest.processInitialRequest(generalInformation, TOKEN);

        // Verify the results
        assertThat(contractId).isNotBlank();

        final EmploymentInformation employmentInformation = ValidateInformation.mapTo("{\"countryOfWork\":\"\", \"visaCompliance\":false, \"workHourPerWeek\":, \"contractStartDate\":\"\", \"employmentTerms\":\"\", \"contractEndDate\":\"\", \"timeOff\":, \"probationPeriod\":, \"noticePeriodDuringProbation\":, \"noticePeriodAfterProbation\":, \"compensation\":}", EmploymentInformation.class);

        final String contractId2 = contractServiceImplUnderTest.processFinalRequest(employmentInformation, generalInformation, TOKEN, contractId);

        assertThat(contractId2).isEqualTo(contractId);
    }
}
