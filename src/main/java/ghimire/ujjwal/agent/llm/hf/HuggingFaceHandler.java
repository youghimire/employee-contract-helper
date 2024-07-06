package ghimire.ujjwal.agent.llm.hf;

import ghimire.ujjwal.agent.llm.AbstractMLHandler;
import ghimire.ujjwal.agent.llm.ModelMessage;
import ghimire.ujjwal.agent.llm.hf.dto.HFRequest;
import ghimire.ujjwal.agent.llm.hf.dto.Parameters;
import ghimire.ujjwal.agent.llm.openai.dto.OpenAICompletionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Service
@Primary
public class HuggingFaceHandler extends AbstractMLHandler {

    private static final Logger log = LoggerFactory.getLogger(HuggingFaceHandler.class);

    @Value("${huggingface.token}")
    private String HFToken;
    @Value("${huggingface.url}")
    private String HFApiURL;
    @Value("${huggingface.model}")
    private String HFModel;

    @Override
    public ModelMessage handleQuery(List<ModelMessage> context) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new HandleHuggingFaceError());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + HFToken);
        HttpEntity<HFRequest> entity = new HttpEntity<>(new HFRequest(HFModel, context, new Parameters(0D), Boolean.FALSE), headers);
        String url = "%s%s/v1/chat/completions".formatted(HFApiURL, HFModel);
        ResponseEntity<OpenAICompletionResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, OpenAICompletionResponse.class);
        if(responseEntity.getStatusCode().is5xxServerError()) {
            try {
                Thread.sleep(10000);
                responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, OpenAICompletionResponse.class);
            } catch (InterruptedException e) {
                log.error("When waiting to load model ", e);
            }
        }
        return getModelMessage(responseEntity);
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
}