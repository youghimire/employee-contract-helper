package ghimire.ujjwal.agent;

import ghimire.ujjwal.agent.llm.hf.HuggingFaceHandler;
import ghimire.ujjwal.agent.llm.ModelMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HuggingFaceHandlerTest {

    @Autowired
    private HuggingFaceHandler huggingFaceHandlerUnderTest;

    @Test
    void testHandleQuery() {
        // Setup
        final List<ModelMessage> context = List.of(new ModelMessage("system", "Answer based on the context provided below. Your name is John."), new ModelMessage("user", "What's your name?"));
        final ModelMessage expectedResult = new ModelMessage("assistant", "John");

        // Run the test
        final ModelMessage result = huggingFaceHandlerUnderTest.handleQuery(context);

        // Verify the results
        assertThat(result.getRole()).isEqualTo(expectedResult.getRole());
        assertThat(result.getContent()).contains(expectedResult.getContent());
    }
}
