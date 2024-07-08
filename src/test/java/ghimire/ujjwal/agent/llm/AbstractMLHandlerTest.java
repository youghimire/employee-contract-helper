package ghimire.ujjwal.agent.llm;

import ghimire.ujjwal.agent.llm.openai.dto.Choice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AbstractMLHandlerTest {

    @Mock
    private LLMIntegration integration;

    private LLMHandler abstractMLHandlerUnderTest;

    @BeforeEach
    void setUp() {
        Mockito.when(integration.vendorName()).thenReturn(LLMIntegration.LLMVendors.LMStudio, LLMIntegration.LLMVendors.HFInference);
        abstractMLHandlerUnderTest = new LLMHandler(List.of(integration, integration));
    }

    @Test
    void testHandleQuery() {
        List<ModelMessage> context = new ArrayList<>();
        ChatCompletionResponse response = getBasicChatResponse();
        ModelMessage message = response.getChoices().get(0).getMessage();
        message.setContent("Hello");

        Mockito.when(integration.queryLLM(context)).thenReturn(ResponseEntity.ok(response));

        final ModelMessage result = abstractMLHandlerUnderTest.handleQuery(new ArrayList<>());

        assertThat(result).isEqualTo(message);
    }

    @Test
    void testHandleQueryWithError() {
        LLMHandler handler = Mockito.spy(abstractMLHandlerUnderTest);

        ChatCompletionResponse response = getBasicChatResponse();
        ModelMessage message = response.getChoices().get(0).getMessage();
        message.setContent("{}");   //Not valid JSON
        final List<ModelMessage> sessionHistory = new ArrayList<>();
        Mockito.doReturn(ResponseEntity.ok(response)).when(integration).queryLLM(sessionHistory);

        final ModelMessage result = handler.handleQuery(sessionHistory);

        assertThat(result.getContent()).isEqualTo("Can not process response. Please Try again.");
    }

    @Test
    void testHandleQuerySecondTry() {
        LLMHandler handler = Mockito.spy(abstractMLHandlerUnderTest);

        ChatCompletionResponse responseWithInvalidJSON = getBasicChatResponse();
        ModelMessage message = responseWithInvalidJSON.getChoices().get(0).getMessage();
        message.setContent("{}");   //Not valid JSON

        ChatCompletionResponse responseWithText = getBasicChatResponse();
        final List<ModelMessage> sessionHistory = new ArrayList<>();
        Mockito.doReturn(ResponseEntity.ok(responseWithInvalidJSON), ResponseEntity.ok(responseWithText)).when(integration).queryLLM(sessionHistory);

        final ModelMessage result = handler.handleQuery(sessionHistory);

        assertThat(result.getContent()).isEqualTo(responseWithText.getChoices().get(0).getMessage().getContent());
    }

    @Test
    void testGetModelMessage() {
        final ResponseEntity<ChatCompletionResponse> badRequestResponse = new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        assertThatThrownBy(() -> LLMHandler.getModelMessage(badRequestResponse)).isInstanceOf(AssertionError.class);

        final ResponseEntity<ChatCompletionResponse> emptyBody = new ResponseEntity<>(null, HttpStatus.OK);
        assertThatThrownBy(() -> LLMHandler.getModelMessage(emptyBody)).isInstanceOf(AssertionError.class);

        ChatCompletionResponse response = getBasicChatResponse();

        List<Choice> choices = response.getChoices();
        response.setChoices(new ArrayList<>());
        final ResponseEntity<ChatCompletionResponse> emptyChoices = new ResponseEntity<>(response, HttpStatus.OK);
        assertThatThrownBy(() -> LLMHandler.getModelMessage(emptyChoices)).isInstanceOf(AssertionError.class);
        response.setChoices(choices);

        ModelMessage message = response.getChoices().get(0).getMessage();
        message.setRole("system");
        final ResponseEntity<ChatCompletionResponse> roleNotCorrect = new ResponseEntity<>(response, HttpStatus.OK);
        assertThatThrownBy(() -> LLMHandler.getModelMessage(roleNotCorrect)).isInstanceOf(AssertionError.class);
        message.setRole("assistant");

        message.setContent(null);
        final ResponseEntity<ChatCompletionResponse> emptyContent = new ResponseEntity<>(response, HttpStatus.OK);
        assertThatThrownBy(() -> LLMHandler.getModelMessage(emptyContent)).isInstanceOf(AssertionError.class);
        message.setContent("Some LLM response");

        final ResponseEntity<ChatCompletionResponse> correctResponse = new ResponseEntity<>(response, HttpStatus.OK);
        final ModelMessage result = LLMHandler.getModelMessage(correctResponse);
        final ModelMessage expectedResult = new ModelMessage(message.getRole(), message.getContent());
        assertThat(result).isEqualTo(expectedResult);
    }

    private ChatCompletionResponse getBasicChatResponse() {
        final ChatCompletionResponse chatCompletionResponse = new ChatCompletionResponse();

        final Choice choice = new Choice();
        final ModelMessage message = new ModelMessage();
        message.setRole("assistant");
        message.setContent("Can you provide me your employee name?");
        choice.setMessage(message);

        chatCompletionResponse.setChoices(List.of(choice));
        return chatCompletionResponse;
    }
}
