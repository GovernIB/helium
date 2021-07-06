package es.caib.helium.camunda.controller;

import es.caib.helium.camunda.model.WToken;
import es.caib.helium.camunda.service.ExecutionService;
import es.caib.helium.client.engine.model.RedirectTokenData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(ExecutionController.API_PATH)
public class ExecutionController {

    public static final String API_PATH = "/api/v1";

    private final ExecutionService executionService;


    @GetMapping(value="/executions/{tokenId}")
    public ResponseEntity<WToken> getTokenById(
            @PathVariable("tokenId") String tokenId) {
        return new ResponseEntity<>(executionService.getTokenById(tokenId), HttpStatus.OK);
    }

    @GetMapping(value="/processInstances/{processInstanceId}/executions/active")
    public ResponseEntity<Map<String, WToken>> getActiveTokens(
            @PathVariable("processInstanceId") String processInstanceId) {
        Map<String, WToken> tokens = executionService.getActiveTokens(processInstanceId);

        if (tokens == null || tokens.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(tokens, HttpStatus.OK);
    }

    @GetMapping(value="/processInstances/{processInstanceId}/executions")
    public ResponseEntity<Map<String, WToken>> getAllTokens(
            @PathVariable("processInstanceId") String processInstanceId) {
        Map<String, WToken> tokens = executionService.getAllTokens(processInstanceId);

        if (tokens == null || tokens.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(tokens, HttpStatus.OK);
    }

    @PostMapping(value="/executions/{tokenId}")
    public ResponseEntity<Void> tokenRedirect(
            @PathVariable("tokenId") String tokenId,
            @RequestBody RedirectTokenData redirectToken) {
        executionService.tokenRedirect(
                tokenId,
                redirectToken.getNodeName(),
                redirectToken.isCancelTasks(),
                redirectToken.isEnterNodeIfTask(),
                redirectToken.isExecuteNode());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value="/executions/{tokenId}/activar/{activar}")
    public ResponseEntity<Boolean> tokenActivar(
            @PathVariable("tokenId") String tokenId,
            @PathVariable("activar") boolean activar) {
        return new ResponseEntity<>(executionService.tokenActivar(tokenId, activar), HttpStatus.OK);
    }

    @PostMapping(value="/executions/{tokenId}/signal")
    public ResponseEntity<Void> signalToken(
            @PathVariable("tokenId") String tokenId,
            @RequestBody String signalName) {
        executionService.signalToken(tokenId, signalName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}