package ghimire.ujjwal.agent.llm;

import ghimire.ujjwal.agent.llm.openai.dto.OpenAICompletionResponse;
import org.springframework.http.ResponseEntity;

public abstract class AbstractMLHandler implements MLHandler{

    public static ModelMessage getModelMessage(ResponseEntity<OpenAICompletionResponse> responseEntity) {
        assert responseEntity.getStatusCode().is2xxSuccessful();
        assert responseEntity.getBody() != null;
        assert !responseEntity.getBody().getChoices().isEmpty();
        String role = responseEntity.getBody().getChoices().get(0).getMessage().getRole();
        assert "assistant".equals(role);
        String assistantMessage = responseEntity.getBody().getChoices().get(0).getMessage().getContent();
        assert !assistantMessage.isBlank();
        return new ModelMessage(role, assistantMessage);
    }
}
