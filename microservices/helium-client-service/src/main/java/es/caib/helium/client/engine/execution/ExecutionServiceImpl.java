package es.caib.helium.client.engine.execution;

import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import es.caib.helium.client.engine.model.RedirectTokenData;
import es.caib.helium.client.engine.model.WToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExecutionServiceImpl implements ExecutionService {

	private final String missatgeLog = "Cridant Engine Service - Execution - ";

	private ExecutionFeignClient executionClient;

	@Override
	public WToken getTokenById(String tokenId) {
		
		log.debug(missatgeLog + " token amb id " + tokenId);
		var responseEntity = executionClient.getTokenById(tokenId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public Map<String, WToken> getActiveTokens(String processInstanceId) {
		
		log.debug(missatgeLog + " get active tokens token pel proccesInstanceId " + processInstanceId);
		var responseEntity = executionClient.getActiveTokens(processInstanceId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public Map<String, WToken> getAllTokens(String processInstanceId) {

		log.debug(missatgeLog + " get all tokens token pel proccesInstanceId " + processInstanceId);
		var responseEntity = executionClient.getAllTokens(processInstanceId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public void tokenRedirect(String tokenId, RedirectTokenData redirectToken) {
		
		log.debug(missatgeLog + " token redirect tokenId " + tokenId + " redirectToken " + redirectToken);
		executionClient.tokenRedirect(tokenId, redirectToken);
	}

	@Override
	public Boolean tokenActivar(String tokenId, boolean activar) {
		
		log.debug(missatgeLog + " token activar tokenId " + tokenId + " activar " + activar);
		var responseEntity = executionClient.tokenActivar(tokenId, activar);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public void signalToken(String tokenId, String signalName) {

		log.debug(missatgeLog + " token redirect tokenId " + tokenId + " signalName " + signalName);
		executionClient.signalToken(tokenId, signalName);
	}
}
