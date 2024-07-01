package ghimire.ujjwal.agent.rest;

import ghimire.ujjwal.agent.dtos.AgentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/agent")
public class AgentResources {
    private static final Logger log = LoggerFactory.getLogger(AgentResources.class);
    @RequestMapping(value = "/echo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AgentResponse> getIntegration(@RequestHeader("auth-token") String token) {
        log.debug("Agent echo requeset : {}", token);
        return ResponseEntity.ok(new AgentResponse("Echo"));
    }
}