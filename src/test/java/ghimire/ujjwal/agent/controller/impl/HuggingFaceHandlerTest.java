package ghimire.ujjwal.agent.controller.impl;

import ghimire.ujjwal.agent.integration.HuggingFaceHandler;
import ghimire.ujjwal.agent.integration.ModelMessage;
import ghimire.ujjwal.agent.integration.gemma.GemmaMessage;
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
        messages.add(new GemmaMessage("model", "My name is ujwal and I live in Earth"));
        messages.add(new GemmaMessage("user", "What's my name?"));
        assertThat(huggingFaceHandlerUnderTest.handleQuery(messages)).isEqualToIgnoringCase("Ujwal");
    }
}
