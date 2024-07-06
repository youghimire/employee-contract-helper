package ghimire.ujjwal.agent.llm.openai;

import ghimire.ujjwal.agent.llm.AbstractMLHandler;
import ghimire.ujjwal.agent.llm.MLHandler;
import ghimire.ujjwal.agent.llm.ModelMessage;
import ghimire.ujjwal.agent.llm.openai.dto.OpenAICompletionRequest;
import ghimire.ujjwal.agent.llm.openai.dto.OpenAICompletionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OpenAIAPIHandler extends AbstractMLHandler {

    private static final Logger log = LoggerFactory.getLogger(OpenAIAPIHandler.class);

    @Value("${lmstudio.url}")
    private String URL;
    private final Boolean STREAM = Boolean.FALSE;


    @Override
    public ModelMessage handleQuery(List<ModelMessage> context) {
        log.debug("Asking to LLM with context {} \n url {}", context, URL);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<OpenAICompletionResponse> responseEntity = restTemplate.exchange(URL, HttpMethod.POST, getRequestHttpEntity(context), OpenAICompletionResponse.class);
        return getModelMessage(responseEntity);
    }

    public HttpEntity<OpenAICompletionRequest> getRequestHttpEntity(List<ModelMessage> context) {

        Double TEMPERATURE = 0D;
        Long MAX_TOKENS = -1L;
        OpenAICompletionRequest request = new OpenAICompletionRequest(context, TEMPERATURE, MAX_TOKENS, STREAM);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(request, headers);
    }
}
