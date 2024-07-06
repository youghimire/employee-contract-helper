package ghimire.ujjwal.agent.integration.openai.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class OpenAICompletionResponse {

    private String id;
    private String object;
    private Date created;
    private String model;
    private List<Choice> choices;
    private Usage usage;

}
