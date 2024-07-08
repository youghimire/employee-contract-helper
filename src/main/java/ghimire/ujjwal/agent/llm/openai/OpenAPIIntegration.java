package ghimire.ujjwal.agent.llm.openai;

import ghimire.ujjwal.agent.llm.LLMIntegration;
import ghimire.ujjwal.agent.llm.ModelMessage;
import ghimire.ujjwal.agent.llm.openai.dto.OpenAICompletionRequest;
import ghimire.ujjwal.agent.llm.ChatCompletionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service

public class OpenAPIIntegration implements LLMIntegration {

    private static final Logger log = LoggerFactory.getLogger(OpenAPIIntegration.class);

    @Value("${lmstudio.url}")
    private String URL;
    private final Boolean STREAM = Boolean.FALSE;

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<ChatCompletionResponse> queryLLM(List<ModelMessage> context) {
        log.debug("Asking to LLM with question {} \n URL {}", context.isEmpty() ? "" : context.get(context.size()-1), URL);
        return restTemplate.exchange(URL, HttpMethod.POST, getRequestHttpEntity(context), ChatCompletionResponse.class);
    }

    @Override
    public String vendorName() {
        return LLMVendors.LMStudio;
    }

    public HttpEntity<OpenAICompletionRequest> getRequestHttpEntity(List<ModelMessage> context) {

        Double TEMPERATURE = 0.1D;
        Long MAX_TOKENS = 250L;
        OpenAICompletionRequest request = new OpenAICompletionRequest(context, TEMPERATURE, MAX_TOKENS, STREAM);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(request, headers);
    }
}
