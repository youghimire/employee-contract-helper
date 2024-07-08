package ghimire.ujjwal.agent.llm;

import ghimire.ujjwal.agent.llm.openai.OpenAPIIntegration;
import ghimire.ujjwal.agent.llm.openai.dto.OpenAICompletionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class OpenAIAPIHandlerTest {

    private OpenAPIIntegration openAIAPIHandlerUnderTest;

    @BeforeEach
    void setUp() {
        openAIAPIHandlerUnderTest = new OpenAPIIntegration();
        ReflectionTestUtils.setField(openAIAPIHandlerUnderTest, "URL", "http://localhost:1234/v1/chat/completions");
    }
    @Test
    void testQueryLLM() {
        // Setup
        final List<ModelMessage> context = List.of(new ModelMessage("system", "Answer based on the context provided below. For now Your given name is John."), new ModelMessage("user", "What's your given name?"));

        // Run the test
        final ResponseEntity<ChatCompletionResponse> result = openAIAPIHandlerUnderTest.queryLLM(context);

        // Verify the results
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getChoices()).isNotEmpty();
        assertThat(result.getBody().getChoices().get(0).getMessage()).isNotNull();
        assertThat(result.getBody().getChoices().get(0).getMessage().getRole()).isEqualTo("assistant");
        assertThat(result.getBody().getChoices().get(0).getMessage().getContent()).isNotBlank();
        assertThat(result.getBody().getChoices().get(0).getMessage().getContent()).contains("John");
    }

    @Test
    void testVendorName() {
        assertThat(openAIAPIHandlerUnderTest.vendorName()).isEqualTo("LMStudio");
    }

    @Test
    void testGetRequestHttpEntity() {
        // Test context
        final List<ModelMessage> context = List.of(new ModelMessage("system", "This is context"));

        //Expected Result
        final OpenAICompletionRequest request = new OpenAICompletionRequest();
        final ModelMessage modelMessage = new ModelMessage();
        modelMessage.setRole("system");
        modelMessage.setContent("This is context");
        request.setMessages(List.of(modelMessage));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        final HttpEntity<OpenAICompletionRequest> expectedResult = new HttpEntity<>(request, headers);

        // Run the test
        final HttpEntity<OpenAICompletionRequest> result = openAIAPIHandlerUnderTest.getRequestHttpEntity(context);

        // Verify the results
        assertThat(Objects.requireNonNull(result.getBody()).getMessages()).isEqualTo(Objects.requireNonNull(expectedResult.getBody()).getMessages());
    }
}
