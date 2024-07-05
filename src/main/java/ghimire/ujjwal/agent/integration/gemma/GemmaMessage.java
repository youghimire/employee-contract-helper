package ghimire.ujjwal.agent.integration.gemma;

import ghimire.ujjwal.agent.integration.ModelMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GemmaMessage extends ModelMessage {
    private String Role;
    private String content;
}
