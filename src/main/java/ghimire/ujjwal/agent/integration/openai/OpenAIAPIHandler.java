package ghimire.ujjwal.agent.integration.openai;

import ghimire.ujjwal.agent.integration.MLHandler;
import ghimire.ujjwal.agent.integration.ModelMessage;
import ghimire.ujjwal.agent.integration.openai.dto.OpenAICompletionRequest;
import ghimire.ujjwal.agent.integration.openai.dto.OpenAICompletionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class OpenAIAPIHandler implements MLHandler {

    private static final Logger log = LoggerFactory.getLogger(OpenAIAPIHandler.class);

    @Value("${lmstudio.url}")
    private String URL;
    @Value("${lmstudio.model}")
    private String MODEL;
    private final Boolean STREAM = Boolean.FALSE;


    @Override
    public ModelMessage handleQuery(List<ModelMessage> context) {
        log.debug("Asking to LLM with context {} \n url {} model {}", context, URL, MODEL);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<OpenAICompletionResponse> responseEntity = restTemplate.exchange(URL, HttpMethod.POST, getRequestHttpEntity(context), OpenAICompletionResponse.class);
       assert responseEntity.getStatusCode().is2xxSuccessful();
       assert responseEntity.getBody() != null;
       assert !responseEntity.getBody().getChoices().isEmpty();
       String role = responseEntity.getBody().getChoices().get(0).getMessage().getRole();
       assert "assistant".equals(role);
       String assistantMessage = responseEntity.getBody().getChoices().get(0).getMessage().getContent();
       assert !assistantMessage.isBlank();
        log.debug("LLM response: {}", assistantMessage);
        return new ModelMessage(role, assistantMessage);
    }

    public HttpEntity<OpenAICompletionRequest> getRequestHttpEntity(List<ModelMessage> context) {

        Double TEMPERATURE = 0D;
        Long MAX_TOKENS = -1L;
        OpenAICompletionRequest request = new OpenAICompletionRequest(MODEL, context, TEMPERATURE, MAX_TOKENS, STREAM);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(request, headers);
    }
}
