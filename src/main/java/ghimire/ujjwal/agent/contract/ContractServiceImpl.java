package ghimire.ujjwal.agent.contract;

import ghimire.ujjwal.agent.postProcess.EmploymentInformation;
import ghimire.ujjwal.agent.postProcess.GeneralInformation;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Service
public class ContractServiceImpl implements ContractService{
    private static final Logger log = LoggerFactory.getLogger(ContractServiceImpl.class);

    @Value("${contract.post.url}")
    private String URL;

    private static final RestTemplate restTemplate = new RestTemplate();
    static {
        restTemplate.setErrorHandler(new HandleNiuralError());
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    @Override
    public String processInitialRequest(GeneralInformation generalInformation, String token) {
        HttpHeaders headers = createHeader(token);
        log.info("Sending initial employee information to Contract API {}", generalInformation);
        HttpEntity<InitialRequest> entity = new HttpEntity<>(new InitialRequest(generalInformation), headers);
        ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.POST, entity, String.class);
        Assert.isTrue(StringUtils.isNotBlank(response.getBody()), "Request to Contract create API failed.");
        log.info("Response from Niural for initial request {}", response.getBody());
        Assert.isTrue(response.getStatusCode().is2xxSuccessful(), "Request to Contract create API failed.");
        return getContractId(response);

    }

    public static class HandleNiuralError extends DefaultResponseErrorHandler {
        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            HttpStatusCode statusCode = response.getStatusCode();
            String errorMessage = "Niural returned error %s: %s".formatted(statusCode, response.getStatusText());
            log.error(errorMessage);
        }
    }

    @Override
    public String processFinalRequest(EmploymentInformation employmentInformation, GeneralInformation generalInformation, String appToken, String contractId) {
        HttpHeaders headers = createHeader(appToken);
        log.info("Sending final employee information to Contract API {}", employmentInformation);
        HttpEntity<UpdateRequest> entity = new HttpEntity<>(new UpdateRequest(generalInformation, employmentInformation), headers);
        String requestUrl = "%s/%s".formatted(URL, contractId);
        log.info("Request URL {}", requestUrl);
        ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.PATCH, entity, String.class);
        Assert.isTrue(StringUtils.isNotBlank(response.getBody()), "Request to Contract create API failed.");
        log.info("Response from Niural for update request {}", response.getBody());
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

    private String getContractId(ResponseEntity<String> response) {
        JSONObject responseJson = new JSONObject(response.getBody());
        Assert.isTrue(responseJson.has("message") && "Success".equalsIgnoreCase((String) responseJson.get("message")), "Post Contract did not return success message");
        Assert.isTrue(responseJson.has("data"), "Post contract does not return correct response");
        JSONObject data = responseJson.getJSONObject("data");
        Assert.isTrue(data.has("contract_id"), "Post contract does not return correct response");
        return (String) data.get("contract_id");
    }
}
