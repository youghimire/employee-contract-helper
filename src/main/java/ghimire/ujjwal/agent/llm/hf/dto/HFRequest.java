package ghimire.ujjwal.agent.llm.hf.dto;

import ghimire.ujjwal.agent.llm.ModelMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HFRequest {
    String model;
    List<ModelMessage> messages;
    Parameters parameters;
    Boolean stream;
}
