package ghimire.ujjwal.agent.contract;

import com.fasterxml.jackson.core.JsonProcessingException;
import ghimire.ujjwal.agent.postProcess.EmploymentInformation;
import ghimire.ujjwal.agent.postProcess.GeneralInformation;
import ghimire.ujjwal.agent.postProcess.ValidateInformation;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class ContractServiceImplTest {


    private static final String TOKEN = "eyJraWQiOiJzeUw5N3pEWm9jMldQaThGQmpLV1hpWnlMTUpGMlRQOW4zVlpwUHJzTmh3PSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJkYTJhY2EwNy0xNmY1LTRjOTMtODdlOS0zZWEwNGZiNGNlZDEiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLnVzLWVhc3QtMS5hbWF6b25hd3MuY29tXC91cy1lYXN0LTFfS1RmVDlvZHFMIiwicGhvbmVfbnVtYmVyX3ZlcmlmaWVkIjpmYWxzZSwiY29nbml0bzp1c2VybmFtZSI6ImRhMmFjYTA3LTE2ZjUtNGM5My04N2U5LTNlYTA0ZmI0Y2VkMSIsImdpdmVuX25hbWUiOiJaZXBweSIsIm9yaWdpbl9qdGkiOiI3NzFhYTUxNC1kMDA0LTRjYzktOTMyMy0wN2RhMGMyNDJhMTIiLCJhdWQiOiI3bXZzZm1ibXM5amkzdDZlZWZuYmwwcTRoMSIsImV2ZW50X2lkIjoiMTQ1MzkzYjItMTVhYS00NDg2LTg0ZTktZjY0NGVjZDgzZGRjIiwidG9rZW5fdXNlIjoiaWQiLCJhdXRoX3RpbWUiOjE3MjA2MTA3NDEsIm5hbWUiOiJOaXVyYWwgSW5jIiwicGhvbmVfbnVtYmVyIjoiKzk3Nzk4Njg4NTM1MDkiLCJleHAiOjE3MjA2MTQzNDEsImlhdCI6MTcyMDYxMDc0MSwiZmFtaWx5X25hbWUiOiJUZXN0IiwianRpIjoiNGNkZDQ0MWYtMmZiZS00MDhmLTk3MmQtNGEzZjYwMWE1MjliIiwiZW1haWwiOiJzdXJhakBuaXVyYWwuY29tIiwiY3VzdG9tOmNvdW50cnlfY29kZSI6Iis5NzcifQ.pDtPX3f-vET9Vfdy5Yn1kiYt0CKCZ9w1dX8GFmyGqHjUvDpkWq_BDeywL5n9KpaaVFyBFEVnVxQUdgrSNHEH1tRGlCoZnNtG2BVGH2qb4crfv8e0cMYgyYDNuvAQ3dAhGmoMVir3HLm0kJCP8UXOx9LVhheHM-oCqvz2_KMEesmMF4et3qJnSGb-TSuP-7dFlFhBZa4pKIhlUJKEvGohd9ko24B0wdxe_ifgS73PVFUf8R7wHxxGKSz-JVRuazQDR--9T0ZAHcK421NkCewnzj8WGlmJqNgwJEdcRP4_EWDfs6I705w7qsJrPOI77AjyNzMAAboUXTrAn6TcM4z9DQ";
    private static final String URL = "https://q6iy3e4rdf.execute-api.us-east-1.amazonaws.com/qa/contracts";

    private final ContractService contractServiceImplUnderTest = new ContractServiceImpl();

    @Test
    void testProcessSuccessRequest() throws JsonProcessingException {
        // Setup
        final GeneralInformation generalInformation = ValidateInformation.mapTo("{\"firstName\":\"Smith\", \"middleName\":\"\", \"lastName\":\"Flint\", \"email\":\"smith+2@gmail.com\", \"countryOfCitizenISOAlpha2\":\"IN\", \"countryOfWorkISOAlpha2\":\"US\", \"jobTitle\":\"Office assistant\", \"scopeOfWork\":\"a scope of work\"}", GeneralInformation.class);
        assertThat(URL).isNotBlank();
        assertThat(TOKEN).isNotBlank();
        ReflectionTestUtils.setField(contractServiceImplUnderTest, "URL", URL);
        // Run the test
        final String contractId = contractServiceImplUnderTest.processInitialRequest(generalInformation, TOKEN);
        System.out.println("Contract Id from initial request " + contractId);
        // Verify the results
        assertThat(contractId).isNotBlank();

        final EmploymentInformation employmentInformation = ValidateInformation.mapTo("{\"countryOfWork\":\"Japan\", \"visaCompliance\":true, \"workHourPerWeek\":45, \"contractStartDate\":\"2024-09-15\", \"employmentTerms\":\"Indefinite\", \"contractEndDate\":\"\", \"timeOff\":12, \"probationPeriod\":0, \"noticePeriodDuringProbation\":45, \"noticePeriodAfterProbation\":45, \"compensation\":95000}", EmploymentInformation.class);

        final String contractId2 = contractServiceImplUnderTest.processFinalRequest(employmentInformation, generalInformation, TOKEN, contractId);

        assertThat(contractId2).isEqualTo(contractId);
    }

    @Test
    void testVisaComplianceFalse() throws JsonProcessingException {
        // Setup
        final GeneralInformation generalInformation = ValidateInformation.mapTo("{\"firstName\":\"Smith\", \"middleName\":\"\", \"lastName\":\"Flint\", \"email\":\"smith@gmail.com\", \"countryOfCitizenISOAlpha2\":\"IN\", \"countryOfWorkISOAlpha2\":\"US\", \"jobTitle\":\"Office assistant\", \"scopeOfWork\":\"a scope of work \\r\\n provide office assistant \\r\\n help customer \"}", GeneralInformation.class);
        assertThat(URL).isNotBlank();
        assertThat(TOKEN).isNotBlank();
        ReflectionTestUtils.setField(contractServiceImplUnderTest, "URL", URL);
        // Run the test
        final String contractId = contractServiceImplUnderTest.processInitialRequest(generalInformation, TOKEN);
        System.out.println("Contract Id from initial request " + contractId);
        // Verify the results
        assertThat(contractId).isNotBlank();

        final EmploymentInformation employmentInformation = ValidateInformation.mapTo("{\"countryOfWork\":\"\", \"visaCompliance\":false, \"workHourPerWeek\":null, \"contractStartDate\":\"\", \"employmentTerms\":\"\", \"contractEndDate\":\"\", \"timeOff\":null, \"probationPeriod\":null, \"noticePeriodDuringProbation\":null, \"noticePeriodAfterProbation\":null, \"compensation\":null}", EmploymentInformation.class);

        final String contractId2 = contractServiceImplUnderTest.processFinalRequest(employmentInformation, generalInformation, TOKEN, contractId);

        assertThat(contractId2).isEqualTo(contractId);
    }
}
