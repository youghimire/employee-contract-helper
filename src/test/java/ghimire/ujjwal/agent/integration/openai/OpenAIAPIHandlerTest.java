package ghimire.ujjwal.agent.integration.openai;

import ghimire.ujjwal.agent.integration.ModelMessage;
import ghimire.ujjwal.agent.integration.openai.dto.OpenAICompletionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OpenAIAPIHandlerTest {

    private OpenAIAPIHandler openAIAPIHandlerUnderTest;

    @BeforeEach
    void setUp() {
        openAIAPIHandlerUnderTest = new OpenAIAPIHandler();
        ReflectionTestUtils.setField(openAIAPIHandlerUnderTest, "URL", "http://localhost:1234/v1/chat/completions");
        ReflectionTestUtils.setField(openAIAPIHandlerUnderTest, "MODEL", "bartowski/Phi-3.1-mini-4k-instruct-GGUF");
    }

    @Test
    void testHandleQuery() {
        // Setup
        final List<ModelMessage> context = List.of(new ModelMessage("system", "Answer based on the context provided below. Your name is John."), new ModelMessage("user", "What's your name?"));
        final ModelMessage expectedResult = new ModelMessage("assistant", "John");

        // Run the test
        final ModelMessage result = openAIAPIHandlerUnderTest.handleQuery(context);

        // Verify the results
        assertThat(result.getRole()).isEqualTo(expectedResult.getRole());
        assertThat(result.getContent()).contains(expectedResult.getContent());
    }

    @Test
    void testGetRequestHttpEntity() {
        // Test context
        final List<ModelMessage> context = List.of(new ModelMessage("system", "This is context"));

        //Expected Result
        final OpenAICompletionRequest request = new OpenAICompletionRequest();
        request.setModel("bartowski/Phi-3.1-mini-4k-instruct-GGUF");
        final ModelMessage modelMessage = new ModelMessage();
        modelMessage.setRole("system");
        modelMessage.setContent("This is context");
        request.setMessages(List.of(modelMessage));
        request.setTemperature(0D);
        request.setMax_tokens(-1L);
        request.setStream(false);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        final HttpEntity<OpenAICompletionRequest> expectedResult = new HttpEntity<>(request, headers);

        // Run the test
        final HttpEntity<OpenAICompletionRequest> result = openAIAPIHandlerUnderTest.getRequestHttpEntity(context);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }
}
