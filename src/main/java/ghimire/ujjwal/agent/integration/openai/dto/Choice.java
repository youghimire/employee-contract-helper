package ghimire.ujjwal.agent.integration.openai.dto;

import ghimire.ujjwal.agent.integration.ModelMessage;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Choice {
    private int index;
    private ModelMessage message;
    private String finishReason;
}
