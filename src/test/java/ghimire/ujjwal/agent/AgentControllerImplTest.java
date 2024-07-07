package ghimire.ujjwal.agent;

import ghimire.ujjwal.agent.controller.AgentController;
import ghimire.ujjwal.agent.controller.AgentControllerImpl;
import ghimire.ujjwal.agent.llm.MLHandler;
import ghimire.ujjwal.agent.llm.ModelMessage;
import ghimire.ujjwal.agent.llm.hf.HuggingFaceHandler;
import ghimire.ujjwal.agent.llm.openai.OpenAIAPIHandler;
import ghimire.ujjwal.agent.message.Message;
import ghimire.ujjwal.agent.message.MessageService;
import ghimire.ujjwal.agent.resources.dtos.MessageDTO;
import ghimire.ujjwal.agent.session.SessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgentControllerImplTest {

    @Mock
    private MessageService messageService;
    @Mock
    private SessionService sessionService;
    @Mock
    private MLHandler mlHandler;

    private AgentControllerImpl agentControllerImplUnderTest;

    @Test
    void testMockLLM() throws IOException {
        agentControllerImplUnderTest = new AgentControllerImpl(mlHandler, messageService, sessionService);
        when(mlHandler.handleQuery(anyList())).thenReturn(new ModelMessage("assistant", "Yes it is."));

        testProcessQuery();
    }

    void testProcessQuery() throws IOException {
        Long sessionId = 1L;
        final MessageDTO agentRequest = new MessageDTO("Can you reply Yes?", sessionId);
        AgentControllerImpl agentController = Mockito.spy(agentControllerImplUnderTest);
        Mockito.doReturn(new ModelMessage("system", "provide quick response")).when(agentController).getInstruction();

        final MessageDTO result = agentController.processQuery("appToken", agentRequest);
        assertThat(result.getSessionId()).isEqualTo(sessionId);
        assertThat(result.getContent()).isNotBlank();
        assertThat(result.getContent()).contains("Yes");
    }

    //Need to Run Model locally may be using LM Studio opening at that URL and port
    @Test
    void testProcessQueryLocally() throws IOException {
        OpenAIAPIHandler openAIAPIHandler = new OpenAIAPIHandler();
        ReflectionTestUtils.setField(openAIAPIHandler, "URL", "http://localhost:1234/v1/chat/completions");
        agentControllerImplUnderTest = new AgentControllerImpl(openAIAPIHandler, messageService, sessionService);

        testProcessQuery();
    }

    /** Need to add token to run this test
    @Test
    void testProcessQueryWithHFAPI() throws IOException {
        HuggingFaceHandler huggingFaceHandler = new HuggingFaceHandler();
        ReflectionTestUtils.setField(huggingFaceHandler, "HFApiURL", "https://api-inference.huggingface.co/models/");
        ReflectionTestUtils.setField(huggingFaceHandler, "HFToken", "");
        ReflectionTestUtils.setField(huggingFaceHandler, "HFModel", "microsoft/Phi-3-mini-4k-instruct");
        agentControllerImplUnderTest = new AgentControllerImpl(huggingFaceHandler, messageService, sessionService);

        testProcessQuery();
    }
    **/
    @Test
    void testGetContext() throws IOException {
        Long sessionId = 1L;
        List<Message> history = List.of(new Message(new ModelMessage("assistant", "what is the employee name?"), sessionId));
        when(messageService.getAllMessage(sessionId)).thenReturn(history);
        agentControllerImplUnderTest = new AgentControllerImpl(mlHandler, messageService, sessionService);

        assertThat(agentControllerImplUnderTest.getSessionHistory(sessionId).size()).isEqualTo(history.size() + 1);
    }
}
