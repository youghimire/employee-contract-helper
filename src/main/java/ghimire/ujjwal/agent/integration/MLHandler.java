package ghimire.ujjwal.agent.integration;

import java.util.List;

public interface MLHandler {
    String handleQuery(List<ModelMessage> context);
}
