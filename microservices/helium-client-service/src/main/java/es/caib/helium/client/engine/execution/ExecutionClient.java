package es.caib.helium.client.engine.execution;

import java.util.Map;

import org.springframework.stereotype.Service;

import es.caib.helium.client.engine.model.RedirectTokenData;
import es.caib.helium.client.engine.model.WToken;

@Service
public interface ExecutionClient {

	public WToken getTokenById(String tokenId);
	
	public Map<String, WToken> getActiveTokens(String processInstanceId);

	public Map<String, WToken> getAllTokens(String processInstanceId); 

	public void tokenRedirect(String tokenId, RedirectTokenData redirectToken); 
	
	public Boolean tokenActivar(String tokenId, boolean activar);
	
	public void signalToken(String tokenId, String signalName);
}
