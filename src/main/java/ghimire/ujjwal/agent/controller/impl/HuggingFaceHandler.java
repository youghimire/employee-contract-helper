package ghimire.ujjwal.agent.controller.impl;

import ghimire.ujjwal.agent.controller.MLHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class HuggingFaceHandler implements MLHandler {

    private static final Logger log = LoggerFactory.getLogger(HuggingFaceHandler.class);

    @Value("${huggingface.token}")
    private String HFToken;
    @Value("${huggingface.url}")
    private String HFApiURL;
    @Value("${huggingface.model}")
    private String HFModel;

    @Override
    public String handleQuery(String query, String context) {
        return inference(query, context);
    }

    private String inference(String query, String context) {

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new HandleHuggingFaceError());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + HFToken);

        HuggingFaceInput huggingFaceInput = new HuggingFaceInput(query, context);
        HttpEntity<HuggingFaceInput> entity = new HttpEntity<>(huggingFaceInput, headers);
        ResponseEntity<HuggingFaceOutput> responseEntity = restTemplate.exchange(HFApiURL + HFModel, HttpMethod.POST, entity, HuggingFaceOutput.class);
        if(responseEntity.getStatusCode().is5xxServerError()) {
            try {
                Thread.sleep(10000);
                responseEntity = restTemplate.exchange(HFApiURL + HFModel, HttpMethod.POST, entity, HuggingFaceOutput.class);
            } catch (InterruptedException e) {
                log.error("When waiting to load model ", e);
            }
        }
        if(responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
            return responseEntity.getBody().getAnswer();
        }
        return "";
    }

    public static class HandleHuggingFaceError extends DefaultResponseErrorHandler {
        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            HttpStatusCode statusCode = response.getStatusCode();
            if(HttpStatusCode.valueOf(503).equals(statusCode)) {
                log.error("Hugging face Model is currently loading waiting...");
            }else {
                log.error("Atlas returned error {}: {}", response.getStatusCode(), response.getStatusText());
            }
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class HuggingFaceInput {
        String question;
        String context;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class HuggingFaceOutput {
        Double score;
        Integer start;
        Integer end;
        String answer;
    }


}
