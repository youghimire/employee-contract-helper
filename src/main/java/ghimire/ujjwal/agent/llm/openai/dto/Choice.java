package ghimire.ujjwal.agent.llm.openai.dto;

import ghimire.ujjwal.agent.llm.ModelMessage;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Choice {
    private int index;
    private ModelMessage message;
    private String finishReason;
}
