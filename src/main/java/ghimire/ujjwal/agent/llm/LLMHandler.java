package ghimire.ujjwal.agent.llm;

import ghimire.ujjwal.agent.postProcess.ValidateInformation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LLMHandler {
    private static final Logger log = LoggerFactory.getLogger(LLMHandler.class);

    final Map<String, LLMIntegration> llmIntegrationMap;

    @Autowired
    public LLMHandler(List<LLMIntegration> llmIntegrations) {
        llmIntegrationMap = new HashMap<>();
        for (LLMIntegration llmIntegration : llmIntegrations) {
            llmIntegrationMap.putIfAbsent(llmIntegration.vendorName(), llmIntegration);
        }
    }

    public LLMIntegration getIntegrationService(String vendorName) {
        return llmIntegrationMap.get(vendorName);
    }

    public ModelMessage handleQuery(List<ModelMessage> context) {
        return handleWithRetry(context, 0);
    }
    private ModelMessage handleWithRetry(List<ModelMessage> context, int retryCount) {
        LLMIntegration llmIntegration;
        if(retryCount == 0) {
            llmIntegration = getIntegrationService(LLMIntegration.LLMVendors.HFInference);
        } else {
            llmIntegration = getIntegrationService(LLMIntegration.LLMVendors.HFInference);
        }
        return postProcessMessage(getModelMessage(llmIntegration.queryLLM(context)), context, retryCount);
    }

    private ModelMessage postProcessMessage(ModelMessage aiResponse, List<ModelMessage> sessionHistory, int count) {
        log.debug("postProcessMessage message from assistant {}", aiResponse.getContent());
        if(ValidateInformation.doContainsJson(aiResponse.getContent())) {
            Optional<String> validationError = ValidateInformation.validateGeneralInformation(aiResponse.getContent());
            if(validationError.isPresent()) {
                if(count < 3) {
                    sessionHistory.add(aiResponse);
                    sessionHistory.add(new ModelMessage("user", "Last json response contains error: %s \nPlease re-generate a valid JSON response or if you do not have information then ask for that information.".formatted(validationError.get())));
                    return handleWithRetry(sessionHistory, count+1);
                }else {
                    return new ModelMessage("assistant", "Can not process response. Please Try again.");
                }
            }
        }
        return aiResponse;
    }

    public static ModelMessage getModelMessage(ResponseEntity<ChatCompletionResponse> responseEntity) {
        assert responseEntity.getStatusCode().is2xxSuccessful();
        assert responseEntity.getBody() != null;
        assert !responseEntity.getBody().getChoices().isEmpty();
        String role = responseEntity.getBody().getChoices().get(0).getMessage().getRole();
        assert "assistant".equals(role);
        String assistantMessage = responseEntity.getBody().getChoices().get(0).getMessage().getContent();
        assert StringUtils.isNotBlank(assistantMessage);
        return new ModelMessage(role, assistantMessage);
    }
}
