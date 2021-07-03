package es.caib.helium.logic.intf.service;

import java.util.List;

import es.caib.helium.logic.intf.dto.TokenDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;

public interface TokenService {
	public List<TokenDto> findTokensPerExpedient(Long expedientId, String processInstanceId);
	public boolean activar(Long expedientId, Long tokenId, boolean activar);
	public List<String> findArrivingNodeNames(Long expedientId, String tokenId);
	public TokenDto findById(Long expedientId, String tokenId);
	public void tokenRetrocedir(Long expedientId, String tokenId,String nodeName,boolean cancelTasks) throws NoTrobatException;
}
