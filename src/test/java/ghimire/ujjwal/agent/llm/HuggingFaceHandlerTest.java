package ghimire.ujjwal.agent.llm;

import ghimire.ujjwal.agent.llm.hf.HuggingFaceIntegration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HuggingFaceHandlerTest {

    @Autowired
    private HuggingFaceIntegration huggingFaceHandlerUnderTest;

//    @BeforeEach
//    void setUp() {
//        huggingFaceHandlerUnderTest = new HuggingFaceHandler();
//        ReflectionTestUtils.setField(huggingFaceHandlerUnderTest, "HFToken", "HFToken");
//        ReflectionTestUtils.setField(huggingFaceHandlerUnderTest, "HFApiURL", "HFApiURL");
//        ReflectionTestUtils.setField(huggingFaceHandlerUnderTest, "HFModel", "microsoft/Phi-3-mini-4k-instruct");
//    }

    @Test
    void testQueryLLM() {
        // Setup
        final List<ModelMessage> context = List.of(new ModelMessage("system", "Answer based on the context provided below. For now Your given name is John."), new ModelMessage("user", "What's your given name?"));

        // Run the test
        final ResponseEntity<ChatCompletionResponse> result = huggingFaceHandlerUnderTest.queryLLM(context);

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
        assertThat(huggingFaceHandlerUnderTest.vendorName()).isEqualTo("HFInference");
    }
}
