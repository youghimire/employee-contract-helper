package ghimire.ujjwal.agent.controller.impl;

import ghimire.ujjwal.agent.integration.hf.HuggingFaceHandler;
import ghimire.ujjwal.agent.integration.ModelMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HuggingFaceHandlerTest {

    @Autowired
    private HuggingFaceHandler huggingFaceHandlerUnderTest;

    @Test
    void testHandleQuery() {
        List<ModelMessage> messages = new ArrayList<>();
        messages.add(new ModelMessage("model", "My name is ujwal and I live in Earth"));
        messages.add(new ModelMessage("user", "What's my name?"));
        assertThat(huggingFaceHandlerUnderTest.handleQuery(messages).getContent()).isEqualToIgnoringCase("Ujwal");
    }
}
