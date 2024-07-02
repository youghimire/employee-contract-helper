package ghimire.ujjwal.agent.controller.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HuggingFaceHandlerTest {

    @Autowired
    private HuggingFaceHandler huggingFaceHandlerUnderTest;

    @Test
    void testHandleQuery() {
        assertThat(huggingFaceHandlerUnderTest.handleQuery("What's my name?", "My name is ujwal and I live in Earth")).isEqualToIgnoringCase("Ujwal");
    }
}
