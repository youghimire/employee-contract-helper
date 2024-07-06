package ghimire.ujjwal.agent.integration;

import java.util.List;

public interface MLHandler {
    ModelMessage handleQuery(List<ModelMessage> context);
}
