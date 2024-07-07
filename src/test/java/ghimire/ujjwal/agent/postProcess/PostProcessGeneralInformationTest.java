package ghimire.ujjwal.agent.postProcess;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PostProcessGeneralInformationTest {

    @Test
    void testDoContainsJsonNegative() {
        assertThat(PostProcessGeneralInformation.doContainsJson("content")).isFalse();
        assertThat(PostProcessGeneralInformation.doContainsJson("{\"content\": \"\", \"role\": \"assistant\"}")).isTrue();
        assertThat(PostProcessGeneralInformation.doContainsJson("{\"content\": \"\", \"role\": \"assistant\"}")).isTrue();
        assertThat(PostProcessGeneralInformation.doContainsJson("{content}")).isTrue();
    }

    private final String emptyName = "{\n  \"firstName\": \"\",\n  \"middleName\": \"\",\n  \"lastName\": \"Kumar\",\n  \"email\": \"kumar@gmail.com\",\n  \"countryOfCitizenISOAlpha2\": \"np\",\n  \"countryOfWorkISOAlpha2\": \"IN\",\n  \"jobTitle\": \"Developer\",\n  \"scopeOfWork\": \"Develop and assist development\"\n}";
    private final String valid = "{\"firstName\":\"Ramesh\", \"middleName\":\"\", \"lastName\":\"Kumar\", \"email\":\"ramesh@gmail.com\", \"countryOfCitizenISOAlpha2\":\"np\", \"countryOfWorkISOAlpha2\":\"AE\", \"jobTitle\":\"Developer\", \"scopeOfWork\":\"develop things\" }";
    @Test
    void testDoContainsJsonWithExtendedCharacters() {
        assertThat(PostProcessGeneralInformation.doContainsJson(emptyName)).isTrue();
        assertThat(PostProcessGeneralInformation.doContainsJson(valid)).isTrue();
        assertThat(PostProcessGeneralInformation.doContainsJson("{}")).isTrue();

    }
    @Test
    void testValidateGeneralInformation() {
        Optional<String> errorForJson1 = PostProcessGeneralInformation.getValidationError(emptyName);
        System.out.println(errorForJson1);
        assertThat(errorForJson1.isPresent()).isTrue();

        Optional<String> errorForJson2 = PostProcessGeneralInformation.getValidationError(valid);
        System.out.println(errorForJson2);
        assertThat(errorForJson2.isEmpty()).isTrue();
    }

    @Test
    void testValidateEmail() {
        String invalidEmail = "{\"firstName\":\"Ramesh\", \"middleName\":\"\", \"lastName\":\"Kumar\", \"email\":\"rameshgmail.com\", \"countryOfCitizenISOAlpha2\":\"NP\", \"countryOfWorkISOAlpha2\":\"AK\", \"jobTitle\":\"Developer\", \"scopeOfWork\":\"develop things\" }";
        Optional<String> errorForInvalidEmail = PostProcessGeneralInformation.getValidationError(invalidEmail);
        System.out.println(errorForInvalidEmail);
        assertThat(errorForInvalidEmail.isPresent()).isTrue();
    }

    @Test
    void testValidateCountry() {
        String invalidCountry = "{\"firstName\":\"Ramesh\", \"middleName\":\"\", \"lastName\":\"Kumar\", \"email\":\"ramesh@gmail.com\", \"countryOfCitizenISOAlpha2\":\"NP\", \"countryOfWorkISOAlpha2\":\"AK\", \"jobTitle\":\"Developer\", \"scopeOfWork\":\"develop things\" }";
        Optional<String> errorForInvalidCountry = PostProcessGeneralInformation.getValidationError(invalidCountry);
        System.out.println(errorForInvalidCountry);
        assertThat(errorForInvalidCountry.isPresent()).isTrue();
    }
}
