package ghimire.ujjwal.agent.llm;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MLHandler {
    ModelMessage handleQuery(List<ModelMessage> context);

    ResponseEntity<ChatCompletionResponse> queryLLM(List<ModelMessage> context);
}
