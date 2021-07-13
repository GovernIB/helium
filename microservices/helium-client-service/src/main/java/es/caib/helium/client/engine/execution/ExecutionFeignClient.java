package es.caib.helium.client.engine.execution;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.helium.client.engine.model.RedirectTokenData;
import es.caib.helium.client.engine.model.WToken;

public interface ExecutionFeignClient {
	
	@RequestMapping(method = RequestMethod.GET, value = ExecutionApiPath.GET_TOKEN_BY_ID)
	public ResponseEntity<WToken> getTokenById(@PathVariable("tokenId") String tokenId);
	
	@RequestMapping(method = RequestMethod.GET, value = ExecutionApiPath.GET_ACTIVE_TOKENS)
	public ResponseEntity<Map<String, WToken>> getActiveTokens(@PathVariable("processInstanceId") String processInstanceId);

	@RequestMapping(method = RequestMethod.GET, value = ExecutionApiPath.GET_ALL_TOKENS)
	public ResponseEntity<Map<String, WToken>> getAllTokens(@PathVariable("processInstanceId") String processInstanceId); 

	@RequestMapping(method = RequestMethod.POST, value = ExecutionApiPath.TOKEN_REDIRECT)
	public ResponseEntity<Void> tokenRedirect(
            @PathVariable("tokenId") String tokenId,
            @RequestBody RedirectTokenData redirectToken); 
	
	@RequestMapping(method = RequestMethod.POST, value = ExecutionApiPath.TOKEN_ACTIVAR)
	public ResponseEntity<Boolean> tokenActivar(
            @PathVariable("tokenId") String tokenId,
            @PathVariable("activar") boolean activar);
	
	@RequestMapping(method = RequestMethod.POST, value = ExecutionApiPath.SIGNAL_TOKEN)
	public ResponseEntity<Void> signalToken(
            @PathVariable("tokenId") String tokenId,
            @RequestBody String signalName);
}
