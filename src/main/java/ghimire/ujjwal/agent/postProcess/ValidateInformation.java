package ghimire.ujjwal.agent.postProcess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

public class ValidateInformation {
    private static final Logger log = LoggerFactory.getLogger(ValidateInformation.class);

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

    private static final ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
    private static final Pattern emailPattern = Pattern.compile("^.+@.+\\..+$");

    public static Optional<String> validateGeneralInformation(String content) {
        try {
            GeneralInformation generalInformation = mapTo(content, GeneralInformation.class);
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

    public static Optional<String> validateEmploymentInformation(String content) {
        try {
            EmploymentInformation info = mapTo(content, EmploymentInformation.class);
            if(!info.getVisaCompliance()) return Optional.empty();
            Assert.isTrue(info.getWorkHourPerWeek() > 40 && info.getWorkHourPerWeek() < 60, "Work hour must be between 40 to 60");
            Assert.notNull(info.getContractStartDate(), "Contract start date can not be empty");
            Assert.isTrue(info.getContractStartDate().isAfter(LocalDate.now().plusDays(4)), "Contract start date should be at least 5 days ahead from today’s date");
            Assert.isTrue(List.of(EmploymentInformation.EmploymentTerms.DEFINITE, EmploymentInformation.EmploymentTerms.INDEFINITE).contains(info.getEmploymentTerms()), "Employment terms can be either Definite or Indefinite");
            if(EmploymentInformation.EmploymentTerms.DEFINITE.equals(info.getEmploymentTerms())) {
                Assert.notNull(info.getContractEndDate(), "Contract End date can not be empty for Definite Employment terms");
                Assert.isTrue(info.getContractEndDate().isAfter(info.getContractStartDate()), "Contract end date should be after the contract start date");
            }
            Assert.isTrue(info.getTimeOff() != null && info.getTimeOff() >= 9, "Time Off should not be less then 9");
            Assert.isTrue(info.getProbationPeriod() != null && info.getProbationPeriod() <= 30, "Probation period should not be greater then 30");
            Assert.isTrue(info.getNoticePeriodAfterProbation() >= info.getNoticePeriodDuringProbation(), "Notice period after probation should be equal to or greater than during probation");
            Assert.isTrue(info.getCompensation() != null && info.getCompensation() > 0, "Gross annual compensation should not be zero");

            return Optional.empty();
        } catch (JsonProcessingException e) {
            log.error("Error validating information", e);
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
