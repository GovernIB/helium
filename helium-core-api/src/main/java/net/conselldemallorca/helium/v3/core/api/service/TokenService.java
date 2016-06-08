package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.TokenDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;

public interface TokenService {
	public List<TokenDto> findTokensPerExpedient(Long expedientId, String processInstanceId);
	public boolean activar(Long expedientId, Long tokenId, boolean activar);
	public List<String> findArrivingNodeNames(Long expedientId, String tokenId);
	public TokenDto findById(Long expedientId, String tokenId);
	public void tokenRetrocedir(Long expedientId, String tokenId,String nodeName,boolean cancelTasks) throws NoTrobatException;
}
