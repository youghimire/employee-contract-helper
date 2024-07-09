package ghimire.ujjwal.agent.postProcess;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PostProcessGeneralInformationTest {

    @Test
    void testDoContainsJsonNegative() {
        assertThat(ValidateInformation.doContainsJson("content")).isFalse();
        assertThat(ValidateInformation.doContainsJson("{\"content\": \"\", \"role\": \"assistant\"}")).isTrue();
        assertThat(ValidateInformation.doContainsJson("{\"content\": \"\", \"role\": \"assistant\"}")).isTrue();
        assertThat(ValidateInformation.doContainsJson("{content}")).isTrue();
    }

    private final String emptyName = "{\n  \"firstName\": \"\",\n  \"middleName\": \"\",\n  \"lastName\": \"Kumar\",\n  \"email\": \"kumar@gmail.com\",\n  \"countryOfCitizenISOAlpha2\": \"np\",\n  \"countryOfWorkISOAlpha2\": \"IN\",\n  \"jobTitle\": \"Developer\",\n  \"scopeOfWork\": \"Develop and assist development\"\n}";
    private final String valid = "{\"firstName\":\"Ramesh\", \"middleName\":\"\", \"lastName\":\"Kumar\", \"email\":\"ramesh@gmail.com\", \"countryOfCitizenISOAlpha2\":\"np\", \"countryOfWorkISOAlpha2\":\"AE\", \"jobTitle\":\"Developer\", \"scopeOfWork\":\"develop things\" }";
    @Test
    void testDoContainsJsonWithExtendedCharacters() {
        assertThat(ValidateInformation.doContainsJson(emptyName)).isTrue();
        assertThat(ValidateInformation.doContainsJson(valid)).isTrue();
        assertThat(ValidateInformation.doContainsJson("{}")).isTrue();

    }
    @Test
    void testValidateGeneralInformation() {
        Optional<String> errorForJson1 = ValidateInformation.validateGeneralInformation(emptyName);
        System.out.println(errorForJson1);
        assertThat(errorForJson1.isPresent()).isTrue();

        Optional<String> errorForJson2 = ValidateInformation.validateGeneralInformation(valid);
        System.out.println(errorForJson2);
        assertThat(errorForJson2.isEmpty()).isTrue();
    }

    @Test
    void testValidateEmail() {
        String invalidEmail = "{\"firstName\":\"Ramesh\", \"middleName\":\"\", \"lastName\":\"Kumar\", \"email\":\"rameshgmail.com\", \"countryOfCitizenISOAlpha2\":\"NP\", \"countryOfWorkISOAlpha2\":\"AK\", \"jobTitle\":\"Developer\", \"scopeOfWork\":\"develop things\" }";
        Optional<String> errorForInvalidEmail = ValidateInformation.validateGeneralInformation(invalidEmail);
        System.out.println(errorForInvalidEmail);
        assertThat(errorForInvalidEmail.isPresent()).isTrue();
    }

    @Test
    void testValidateCountry() {
        String invalidCountry = "{\"firstName\":\"Ramesh\", \"middleName\":\"\", \"lastName\":\"Kumar\", \"email\":\"ramesh@gmail.com\", \"countryOfCitizenISOAlpha2\":\"NP\", \"countryOfWorkISOAlpha2\":\"AK\", \"jobTitle\":\"Developer\", \"scopeOfWork\":\"develop things\" }";
        Optional<String> errorForInvalidCountry = ValidateInformation.validateGeneralInformation(invalidCountry);
        System.out.println(errorForInvalidCountry);
        assertThat(errorForInvalidCountry.isPresent()).isTrue();
    }

    @Test
    void testNoVisaCompliance() {
        final String noCompliance = "{\"visaCompliance\":false}";
        Optional<String> errorForNoCompliance = ValidateInformation.validateEmploymentInformation(noCompliance);
        assertThat(errorForNoCompliance.isEmpty()).isTrue();
    }

    @Test
    void testEmploymentInformation() {
        final String validInfo = "{\"countryOfWorkISOAlpha2\":\"NP\", \"visaCompliance\":true, \"workHourPerWeek\":45, \"contractStartDate\":\"2024-09-15\", \"employmentTerms\":\"Indefinite\", \"contractEndDate\":\"\", \"timeOff\":12, \"probationPeriod\":30, \"noticePeriodDuringProbation\":45, \"noticePeriodAfterProbation\":45, \"compensation\":95000}";
        Optional<String> errorForNoCompliance = ValidateInformation.validateEmploymentInformation(validInfo);
        System.out.println(errorForNoCompliance);
        assertThat(errorForNoCompliance.isEmpty()).isTrue();
    }

    @Test
    void testEmploymentInformationNotValidWorkHour() {
        final String invalid = "{\"countryOfWorkISOAlpha2\":\"NP\", \"visaCompliance\":true, \"workHourPerWeek\":35, \"contractStartDate\":\"2024-09-15\", \"employmentTerms\":\"Indefinite\", \"contractEndDate\":\"\", \"timeOff\":12, \"probationPeriod\":30, \"noticePeriodDuringProbation\":45, \"noticePeriodAfterProbation\":45, \"compensation\":95000}";
        Optional<String> validationResult = ValidateInformation.validateEmploymentInformation(invalid);
        System.out.println(validationResult);
        assertThat(validationResult.isEmpty()).isFalse();
    }

    @Test
    void testEmploymentInformationNotValidTerm() {
        final String validInfo = "{\"countryOfWorkISOAlpha2\":\"NP\", \"visaCompliance\":true, \"workHourPerWeek\":35, \"contractStartDate\":\"2024-09-15\", \"employmentTerms\":\"Definite\", \"contractEndDate\":\"\", \"timeOff\":12, \"probationPeriod\":30, \"noticePeriodDuringProbation\":45, \"noticePeriodAfterProbation\":45, \"compensation\":95000}";
        Optional<String> errorForNoCompliance = ValidateInformation.validateEmploymentInformation(validInfo);
        System.out.println(errorForNoCompliance);
        assertThat(errorForNoCompliance.isEmpty()).isFalse();
    }

    @Test
    void testEmploymentInformationNotValidNoticePeriod() {
        final String validInfo = "{\"countryOfWorkISOAlpha2\":\"NP\", \"visaCompliance\":true, \"workHourPerWeek\":35, \"contractStartDate\":\"2024-09-15\", \"employmentTerms\":\"Definite\", \"contractEndDate\":\"\", \"timeOff\":12, \"probationPeriod\":30, \"noticePeriodDuringProbation\":45, \"noticePeriodAfterProbation\":35, \"compensation\":95000}";
        Optional<String> errorForNoCompliance = ValidateInformation.validateEmploymentInformation(validInfo);
        System.out.println(errorForNoCompliance);
        assertThat(errorForNoCompliance.isEmpty()).isFalse();
    }


}
