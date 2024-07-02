package ghimire.ujjwal.agent.rest;

import ghimire.ujjwal.agent.controller.AgentController;
import ghimire.ujjwal.agent.dtos.AgentRequest;
import ghimire.ujjwal.agent.dtos.AgentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/agent")
public class AgentResources {
    private static final Logger log = LoggerFactory.getLogger(AgentResources.class);

    @Autowired
    AgentController agentController;

    @RequestMapping(value = "/ask", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AgentResponse> getIntegration(@RequestHeader("auth-token") String token,
                                                        @RequestBody AgentRequest agentRequest) {
        log.debug("Agent ask request : {}", agentRequest);
        return ResponseEntity.ok(agentController.processQuery(token, agentRequest));
    }

    @RequestMapping(value = "/echo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AgentResponse> getIntegration(@RequestHeader("auth-token") String token) {
        log.debug("Agent echo request : {}", token);
        return ResponseEntity.ok(new AgentResponse("Echo"));
    }
}