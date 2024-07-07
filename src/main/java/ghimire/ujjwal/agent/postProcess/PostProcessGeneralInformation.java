package ghimire.ujjwal.agent.postProcess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class PostProcessGeneralInformation {

    private static final Map<String, String> countries = new HashMap<>();
    static {
        for (String iso : Locale.getISOCountries()) {
            Locale l = new Locale("", iso);
            countries.put(iso.toUpperCase(), l.getDisplayCountry());
        }
    }

    public static String getCountryName(String iso) {
        return countries.get(iso.toUpperCase());
    }

    public static Boolean doContainsJson(String content) {
        int start = content.indexOf('{');
        int end = content.indexOf('}');
        return start > -1 || end > -1;
    }

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Pattern emailPattern = Pattern.compile("^.+@.+\\..+$");

    public static Optional<String> getValidationError(String content) {
        try {
            String jsonString = content.substring(content.indexOf('{'), content.indexOf('}') + 1);
            GeneralInformation generalInformation = mapper.readValue(jsonString, GeneralInformation.class);
            Assert.isTrue(!generalInformation.getFirstName().isBlank(), "First Name can not be empty");
            Assert.isTrue(!generalInformation.getLastName().isBlank(), "Last Name can not be empty");
            Assert.isTrue(!generalInformation.getEmail().isBlank(), "Email can not be empty");
            Assert.isTrue(emailPattern.matcher(generalInformation.getEmail()).matches(), "Email validation failed");
            Assert.isTrue(!generalInformation.getCountryOfCitizenISOAlpha2().isBlank(), "Country Of Citizen can not be empty");
            Assert.isTrue(countries.containsKey(generalInformation.getCountryOfCitizenISOAlpha2().toUpperCase()), "Country Of Citizen Alpha not found on country list");
            Assert.isTrue(!generalInformation.getCountryOfWorkISOAlpha2().isBlank(), "Country Of Work can not be empty");
            Assert.isTrue(countries.containsKey(generalInformation.getCountryOfWorkISOAlpha2().toUpperCase()), "Country Of Work Alpha not found on country list");
            Assert.isTrue(!generalInformation.getJobTitle().isBlank(), "Job Title can not be empty");
            Assert.isTrue(!generalInformation.getScopeOfWork().isBlank(), "Scope of Work can not be empty");

            return Optional.empty();
        } catch (JsonProcessingException e) {
            return Optional.of("Not a valid Json format: %s. Please follow the given instruction.".formatted(e.getMessage()));
        } catch (Exception e) {
            return Optional.of(e.getMessage());
        }
    }

    public static <T> T mapTo(String content, Class<T> valueType) throws JsonProcessingException {
        String jsonString = content.substring(content.indexOf('{'), content.indexOf('}') + 1);
        return mapper.readValue(jsonString, valueType);
    }
}
