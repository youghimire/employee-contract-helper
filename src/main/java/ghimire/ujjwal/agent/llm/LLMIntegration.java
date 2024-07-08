package ghimire.ujjwal.agent.llm;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LLMIntegration {

    ResponseEntity<ChatCompletionResponse> queryLLM(List<ModelMessage> context);

    String vendorName();

    interface LLMVendors {
        String LMStudio = "LMStudio";
        String HFInference = "HFInference";
    }
}
