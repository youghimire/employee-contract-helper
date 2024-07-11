package ghimire.ujjwal.agent.contract;

import com.fasterxml.jackson.core.JsonProcessingException;
import ghimire.ujjwal.agent.postProcess.EmploymentInformation;
import ghimire.ujjwal.agent.postProcess.GeneralInformation;
import ghimire.ujjwal.agent.postProcess.ValidateInformation;
import ghimire.ujjwal.agent.session.Session;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class ContractServiceImplTest {


    private static final String TOKEN = "eyJraWQiOiJzeUw5N3pEWm9jMldQaThGQmpLV1hpWnlMTUpGMlRQOW4zVlpwUHJzTmh3PSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJkYTJhY2EwNy0xNmY1LTRjOTMtODdlOS0zZWEwNGZiNGNlZDEiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLnVzLWVhc3QtMS5hbWF6b25hd3MuY29tXC91cy1lYXN0LTFfS1RmVDlvZHFMIiwicGhvbmVfbnVtYmVyX3ZlcmlmaWVkIjpmYWxzZSwiY29nbml0bzp1c2VybmFtZSI6ImRhMmFjYTA3LTE2ZjUtNGM5My04N2U5LTNlYTA0ZmI0Y2VkMSIsImdpdmVuX25hbWUiOiJaZXBweSIsIm9yaWdpbl9qdGkiOiJjNzgzM2MxYi05MDgxLTRlNjYtOWRjYS0yM2FmMjM1ZjE5ZWEiLCJhdWQiOiI3bXZzZm1ibXM5amkzdDZlZWZuYmwwcTRoMSIsImV2ZW50X2lkIjoiODFiMzNjNzYtNWRlYi00ZmNiLWExMWEtMmI3YWFkMWVkMDI2IiwidG9rZW5fdXNlIjoiaWQiLCJhdXRoX3RpbWUiOjE3MjA3MjIzMzgsIm5hbWUiOiJOaXVyYWwgSW5jIiwicGhvbmVfbnVtYmVyIjoiKzk3Nzk4Njg4NTM1MDkiLCJleHAiOjE3MjA3MjU5MzgsImlhdCI6MTcyMDcyMjMzOCwiZmFtaWx5X25hbWUiOiJUZXN0IiwianRpIjoiOTc4ODQzNjktZDE3Ni00ZjVkLWFkMGUtYmY5NjBhNTc5Y2VkIiwiZW1haWwiOiJzdXJhakBuaXVyYWwuY29tIiwiY3VzdG9tOmNvdW50cnlfY29kZSI6Iis5NzcifQ.HOYHeOsnfSVAxHdpCWniEzXyOTUiR4ld2X51CDSbt815kkkic56ph5KgxPJebaSq9nU-h2O_BEnSsqNICvETRlxFDQNoSRcsPVR9uhR0mAtBdY5dG9r74PV3n80FT5t5hUoTjskCL9MY0wz2SuM9XxR6qEG-krrVlviFI4dnEFmzsn6QyLN2oTdjvfEqbJko5l3VBjIEFJE2kCsb63_MYN9UZovb23_BEcfU8yxsgwG39iRMq-aLH0bFUGaQPDEMgS2l6n6W3q4hb4febqvdvLvXib1NqUkf4MPSSUf4rBjKH3vIyv-y-xXisR0YvVJJ2yDY-ymI4m-80Nh0yTyeAQ";
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
        Session session = new Session("test");
        session = contractServiceImplUnderTest.processInitialRequest(generalInformation, TOKEN, session);
        String initialContractId = session.getContractId();
        System.out.println("Contract Id from initial request " + initialContractId);
        // Verify the results
        assertThat(initialContractId).isNotBlank();
        assertThat(session.getEmployeeId()).isNotBlank();

        final EmploymentInformation employmentInformation = ValidateInformation.mapTo("{\"countryOfWork\":\"Japan\", \"visaCompliance\":true, \"workHourPerWeek\":45, \"contractStartDate\":\"2024-09-15\", \"employmentTerms\":\"Indefinite\", \"contractEndDate\":\"\", \"timeOff\":12, \"probationPeriod\":0, \"noticePeriodDuringProbation\":45, \"noticePeriodAfterProbation\":45, \"compensation\":95000}", EmploymentInformation.class);

        session = contractServiceImplUnderTest.processFinalRequest(employmentInformation, generalInformation, TOKEN, session);

        assertThat(initialContractId).isEqualTo(session.getContractId());
    }

    @Test
    void testVisaComplianceFalse() throws JsonProcessingException {
        // Setup
        final GeneralInformation generalInformation = ValidateInformation.mapTo("{\"firstName\":\"Smith\", \"middleName\":\"\", \"lastName\":\"Flint\", \"email\":\"smith@gmail.com\", \"countryOfCitizenISOAlpha2\":\"IN\", \"countryOfWorkISOAlpha2\":\"US\", \"jobTitle\":\"Office assistant\", \"scopeOfWork\":\"a scope of work \\r\\n provide office assistant \\r\\n help customer \"}", GeneralInformation.class);
        assertThat(URL).isNotBlank();
        assertThat(TOKEN).isNotBlank();
        ReflectionTestUtils.setField(contractServiceImplUnderTest, "URL", URL);
        Session session = new Session("test");
        // Run the test
        session = contractServiceImplUnderTest.processInitialRequest(generalInformation, TOKEN, session);
        String initialContractId = session.getContractId();
        System.out.println("Contract Id from initial request " + initialContractId);
        // Verify the results
        assertThat(initialContractId).isNotBlank();
        assertThat(session.getEmployeeId()).isNotBlank();

        final EmploymentInformation employmentInformation = ValidateInformation.mapTo("{\"countryOfWork\":\"\", \"visaCompliance\":false, \"workHourPerWeek\":null, \"contractStartDate\":\"\", \"employmentTerms\":\"\", \"contractEndDate\":\"\", \"timeOff\":null, \"probationPeriod\":null, \"noticePeriodDuringProbation\":null, \"noticePeriodAfterProbation\":null, \"compensation\":null}", EmploymentInformation.class);

        session = contractServiceImplUnderTest.processFinalRequest(employmentInformation, generalInformation, TOKEN, session);

        assertThat(initialContractId).isEqualTo(session.getContractId());
    }
}
