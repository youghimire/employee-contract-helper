package ghimire.ujjwal.agent.llm;

import java.util.List;

public interface MLHandler {
    ModelMessage handleQuery(List<ModelMessage> context);
}
