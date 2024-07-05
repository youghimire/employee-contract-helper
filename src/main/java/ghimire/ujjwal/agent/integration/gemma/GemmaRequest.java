package ghimire.ujjwal.agent.integration.gemma;

import ghimire.ujjwal.agent.integration.ModelMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GemmaRequest {
    Integer max_new_tokens;
    List<ModelMessage> messages;
    String model;
    Parameters parameters;
}
