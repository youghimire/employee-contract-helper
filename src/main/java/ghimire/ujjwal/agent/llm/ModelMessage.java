package ghimire.ujjwal.agent.llm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ModelMessage {
    private String role;
    private String content;

    public interface ROLE {
        String ASSISTANT = "assistant";
        String USER = "user";
        String SYSTEM = "system";
    }
}
