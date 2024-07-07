package ghimire.ujjwal.agent.llm;

import ghimire.ujjwal.agent.llm.openai.dto.Choice;
import ghimire.ujjwal.agent.llm.openai.dto.Usage;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class ChatCompletionResponse {

    private String id;
    private String object;
    private Date created;
    private String model;
    private List<Choice> choices;
    private Usage usage;

}
