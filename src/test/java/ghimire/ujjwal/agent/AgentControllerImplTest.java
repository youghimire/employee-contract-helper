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
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void init() {
        agentControllerImplUnderTest = new AgentControllerImpl(mlHandler, messageService, sessionService, contractService);
    }
    @Test
    void testProcessQueryNormal() throws IOException {
        Session session = getSession();
        Mockito.when(sessionService.findById(session.getId())).thenReturn(Optional.of(session));
        AgentControllerImpl agentController = Mockito.spy(agentControllerImplUnderTest);
        Mockito.doReturn(new ModelMessage("system", "provide quick response")).when(agentController).getInstruction(Mockito.anyString());
        when(mlHandler.handleQuery(anyList(), Mockito.any(Session.class) )).thenReturn(new ModelMessage("assistant", "Yes it is."));
        final MessageDTO agentRequest = new MessageDTO("Can you reply Yes?", session.getId());

        final MessageDTO result = agentController.processQuery("appToken", agentRequest);

        assertThat(result.getSessionId()).isEqualTo(session.getId());
        assertThat(result.getContent()).isNotBlank();
        assertThat(result.getContent()).contains("Yes");
    }

    ModelMessage stageOneFinal = new ModelMessage("assistant", "{\"firstName\":\"Smith\", \"middleName\":\"\", \"lastName\":\"Flint\", \"email\":\"smith@gmail.com\", \"countryOfCitizenISOAlpha2\":\"IN\", \"countryOfWorkISOAlpha2\":\"US\", \"jobTitle\":\"Office assistant\", \"scopeOfWork\":\"a scope of work \\r\\n provide office assistant \\r\\n help customer \"}");
    @Test
    void testCompleteFirstStage() throws IOException {
        Session session = getSession();
        Mockito.when(sessionService.findById(session.getId())).thenReturn(Optional.of(session));
        Mockito.when(sessionService.saveSession(Mockito.any(Session.class))).thenAnswer(i -> i.getArguments()[0]);
        when(mlHandler.handleQuery(anyList(), Mockito.any(Session.class) )).thenReturn(stageOneFinal);
        final MessageDTO agentRequest = new MessageDTO("Provide JSON?", session.getId());

        final MessageDTO result = agentControllerImplUnderTest.processQuery("appToken", agentRequest);

        assertThat(result.getSessionId()).isEqualTo(session.getId());
        assertThat(result.getContent()).isNotBlank();
        assertThat(result.getContent()).contains("Is the employee authorized to work from United States?");
    }

    @Test
    void testStartSecondStageWhenVisaComplianceNotRequired() throws IOException {
        Session session = getSession();
        Mockito.when(sessionService.findById(session.getId())).thenReturn(Optional.of(session));
        Mockito.when(sessionService.saveSession(Mockito.any(Session.class))).thenAnswer(i -> i.getArguments()[0]);
        when(mlHandler.handleQuery(anyList(), Mockito.any(Session.class) )).thenReturn(new ModelMessage("assistant", "{\"firstName\":\"Smith\", \"middleName\":\"\", \"lastName\":\"Flint\", \"email\":\"smith@gmail.com\", \"countryOfCitizenISOAlpha2\":\"US\", \"countryOfWorkISOAlpha2\":\"US\", \"jobTitle\":\"Office assistant\", \"scopeOfWork\":\"a scope of work\"}"));
        final MessageDTO agentRequest = new MessageDTO("Provide JSON?", session.getId());

        final MessageDTO result = agentControllerImplUnderTest.processQuery("appToken", agentRequest);

        assertThat(result.getSessionId()).isEqualTo(session.getId());
        assertThat(result.getContent()).isNotBlank();
        assertThat(result.getContent()).contains("What will be the standard weekly work hour of the employee?");
    }

    private Session getSession() {
        Long sessionId = 1L;
        Session session = new Session("test session");
        session.setId(sessionId);
        return session;
    }

    @Test
    void testCompleteSecondStageStage() throws IOException {
        Session session = getSession();
        session.setStatus(Session.STATUS.STAGE2);
        Mockito.when(sessionService.findById(session.getId())).thenReturn(Optional.of(session));
        Mockito.when(sessionService.saveSession(Mockito.any(Session.class))).thenAnswer(i -> i.getArguments()[0]);
        when(mlHandler.handleQuery(anyList(), Mockito.any(Session.class) )).thenReturn(new ModelMessage("assistant", "{\"countryOfWork\":\"Japan\", \"visaCompliance\":true, \"workHourPerWeek\":45, \"contractStartDate\":\"2024-09-15\", \"employmentTerms\":\"Indefinite\", \"contractEndDate\":\"\", \"timeOff\":12, \"probationPeriod\":30, \"noticePeriodDuringProbation\":45, \"noticePeriodAfterProbation\":45, \"compensation\":95000}"));
        when(messageService.getLastMessage(Mockito.anyLong(), Mockito.anyString())).thenReturn(new Message(stageOneFinal, session));
        final MessageDTO agentRequest = new MessageDTO("Provide JSON?", session.getId());

        final MessageDTO result = agentControllerImplUnderTest.processQuery("appToken", agentRequest);

        assertThat(result.getSessionId()).isEqualTo(session.getId());
        assertThat(result.getContent()).isNotBlank();
        assertThat(result.getContent()).isEqualTo("Successfully completed Contract for this Employee. Thank you for your patience.");
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
