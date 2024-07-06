package ghimire.ujjwal.agent.llm.openai.dto;

import ghimire.ujjwal.agent.llm.ModelMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAICompletionRequest {

    private List<ModelMessage> messages;
    private Double temperature;
    private Long max_tokens;
    private boolean stream;
}
