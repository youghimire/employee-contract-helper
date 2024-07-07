package ghimire.ujjwal.agent.llm.openai;

import ghimire.ujjwal.agent.llm.AbstractMLHandler;
import ghimire.ujjwal.agent.llm.ModelMessage;
import ghimire.ujjwal.agent.llm.openai.dto.OpenAICompletionRequest;
import ghimire.ujjwal.agent.llm.ChatCompletionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Primary
public class OpenAIAPIHandler extends AbstractMLHandler {

    private static final Logger log = LoggerFactory.getLogger(OpenAIAPIHandler.class);

    @Value("${lmstudio.url}")
    private String URL;
    private final Boolean STREAM = Boolean.FALSE;

    RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<ChatCompletionResponse> queryLLM(List<ModelMessage> context) {
        log.debug("Asking to LLM with context {} \n URL {}", context, URL);
        return restTemplate.exchange(URL, HttpMethod.POST, getRequestHttpEntity(context), ChatCompletionResponse.class);
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
