package ghimire.ujjwal.agent.integration;

import ghimire.ujjwal.agent.integration.gemma.GemmaRequest;
import ghimire.ujjwal.agent.integration.gemma.Parameters;
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
import java.util.List;

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
    public String handleQuery(List<ModelMessage> context) {
        return inference(context);
    }

    private String inference(List<ModelMessage> context) {

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new HandleHuggingFaceError());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + HFToken);

//        HuggingFaceInput huggingFaceInput = new HuggingFaceInput(query, context);
        GemmaRequest request = new GemmaRequest(100, context, HFModel, new Parameters(200));
        HttpEntity<GemmaRequest> entity = new HttpEntity<>(request, headers);
//        ResponseEntity<HuggingFaceOutput> responseEntity = restTemplate.exchange(HFApiURL + HFModel, HttpMethod.POST, entity, HuggingFaceOutput.class);
        ResponseEntity<String> responseEntity = restTemplate.exchange(HFApiURL + HFModel, HttpMethod.POST, entity, String.class);
        if(responseEntity.getStatusCode().is5xxServerError()) {
            try {
                Thread.sleep(10000);
                responseEntity = restTemplate.exchange(HFApiURL + HFModel, HttpMethod.POST, entity, String.class);
            } catch (InterruptedException e) {
                log.error("When waiting to load model ", e);
            }
        }
        if(responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
            String body = responseEntity.getBody();
            log.debug("HuggingFace response: {}", body);
            return body;
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
                log.error("Hugging face returned error {}: {}", response.getStatusCode(), response.getStatusText());
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
