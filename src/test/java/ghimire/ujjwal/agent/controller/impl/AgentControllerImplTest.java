package ghimire.ujjwal.agent.controller.impl;

import ghimire.ujjwal.agent.integration.MLHandler;
import ghimire.ujjwal.agent.rest.dtos.AgentRequest;
import ghimire.ujjwal.agent.rest.dtos.AgentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgentControllerImplTest {

    @Mock
    private MLHandler mockMlHandler;

    private AgentControllerImpl agentControllerImplUnderTest;

    @BeforeEach
    void setUp() {
        agentControllerImplUnderTest = new AgentControllerImpl();
        agentControllerImplUnderTest.mlHandler = mockMlHandler;
    }

    @Test
    void testProcessQuery() {
        String context = "My name is ujjwal";
        final AgentRequest agentRequest = new AgentRequest("What's my name?", 1L);
        final AgentResponse expectedResult = new AgentResponse("Ujjwal");
        when(mockMlHandler.handleQuery(anyList())).thenReturn(expectedResult.getResponse());

        final AgentResponse result = agentControllerImplUnderTest.processQuery("appToken", agentRequest);
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetContext() {
        assertThat(agentControllerImplUnderTest.getAgentContext(1L).isEmpty()).isFalse();
    }
}
