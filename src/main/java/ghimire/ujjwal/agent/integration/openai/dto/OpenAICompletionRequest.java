package ghimire.ujjwal.agent.integration.openai.dto;

import ghimire.ujjwal.agent.integration.ModelMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAICompletionRequest {

    private String model;
    private List<ModelMessage> messages;
    private Double temperature;
    private Long max_tokens;
    private boolean stream;
}
