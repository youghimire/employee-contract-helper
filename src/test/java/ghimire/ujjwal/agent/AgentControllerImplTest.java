package ghimire.ujjwal.agent;

import ghimire.ujjwal.agent.contract.ContractService;
import ghimire.ujjwal.agent.controller.AgentControllerImpl;
import ghimire.ujjwal.agent.llm.LLMHandler;
import ghimire.ujjwal.agent.llm.ModelMessage;
import ghimire.ujjwal.agent.message.Message;
import ghimire.ujjwal.agent.message.MessageService;
import ghimire.ujjwal.agent.resources.dtos.MessageDTO;
import ghimire.ujjwal.agent.session.Session;
import ghimire.ujjwal.agent.session.SessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
    private LLMHandler mlHandler;
    @Mock
    private ContractService contractService;

    private AgentControllerImpl agentControllerImplUnderTest;

    @Test
    void testProcessQueryNormal() throws IOException {
        Long sessionId = 1L;
        Session session = new Session();
        session.setId(sessionId);

        agentControllerImplUnderTest = new AgentControllerImpl(mlHandler, messageService, sessionService, contractService);
        when(mlHandler.handleQuery(anyList(), Mockito.any(Session.class) )).thenReturn(new ModelMessage("assistant", "Yes it is."));
        AgentControllerImpl agentController = Mockito.spy(agentControllerImplUnderTest);
        Mockito.doReturn(new ModelMessage("system", "provide quick response")).when(agentController).getInstruction(Mockito.anyString());
        Mockito.when(sessionService.findById(sessionId)).thenReturn(Optional.of(session));

        final MessageDTO agentRequest = new MessageDTO("Can you reply Yes?", sessionId);

        final MessageDTO result = agentController.processQuery("appToken", agentRequest);

        assertThat(result.getSessionId()).isEqualTo(sessionId);
        assertThat(result.getContent()).isNotBlank();
        assertThat(result.getContent()).contains("Yes");
    }

    @Test
    void testProcessQueryJSON() throws IOException {
        Long sessionId = 1L;
        Session session = new Session();
        session.setId(sessionId);

        agentControllerImplUnderTest = new AgentControllerImpl(mlHandler, messageService, sessionService, contractService);
        when(mlHandler.handleQuery(anyList(), Mockito.any(Session.class) )).thenReturn(new ModelMessage("assistant", "Yes it is."));
        AgentControllerImpl agentController = Mockito.spy(agentControllerImplUnderTest);
        Mockito.doReturn(new ModelMessage("system", "provide quick response")).when(agentController).getInstruction(Mockito.anyString());
        Mockito.when(sessionService.findById(sessionId)).thenReturn(Optional.of(session));

        final MessageDTO agentRequest = new MessageDTO("Can you reply Yes?", sessionId);

        final MessageDTO result = agentController.processQuery("appToken", agentRequest);

        assertThat(result.getSessionId()).isEqualTo(sessionId);
        assertThat(result.getContent()).isNotBlank();
        assertThat(result.getContent()).contains("Yes");
    }

    @Test
    void testGetContext() throws IOException {
        Long sessionId = 1L;
        Session session = new Session();
        session.setId(sessionId);
        List<Message> history = List.of(new Message(new ModelMessage("assistant", "what is the employee name?"), session));
        when(messageService.getAllMessage(session)).thenReturn(history);
        agentControllerImplUnderTest = new AgentControllerImpl(mlHandler, messageService, sessionService, contractService);

        assertThat(agentControllerImplUnderTest.getSessionHistory(session).size()).isEqualTo(history.size() + 1);
    }
}
