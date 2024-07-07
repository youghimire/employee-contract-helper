package ghimire.ujjwal.agent.contract;

import ghimire.ujjwal.agent.postProcess.GeneralInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class ContractServiceImpl implements ContractService{
    private static final Logger log = LoggerFactory.getLogger(ContractServiceImpl.class);

    @Value("${contract.url}")
    private String URL;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String processInitialRequest(GeneralInformation generalInformation, String token) {
        HttpHeaders headers = createHeader(token);
        log.debug("Sending initial employee information to Contract API {}", generalInformation);
        HttpEntity<InitialRequest> entity = new HttpEntity<>(new InitialRequest(generalInformation), headers);
        ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.POST, entity, String.class);
        Assert.isTrue(response.getStatusCode().is2xxSuccessful(), "Request to Contract create API failed.");
        return getContractId(response);

    }

    private HttpHeaders createHeader(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.ALL));
        headers.set("Authorization", "{\"token\":\"%s\"}".formatted(token));
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("Referer", "\"https://qa.niural.com/");
        return headers;
    }

    //ToDo need to implement logic
    private String getContractId(ResponseEntity<String> response) {
        return "";
    }
}
