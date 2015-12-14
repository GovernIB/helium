package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import net.conselldemallorca.helium.v3.core.api.dto.TokenDto;
import net.conselldemallorca.helium.v3.core.api.service.TokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class TokenServiceBean implements TokenService {
	
	@Autowired
	TokenService delegate;
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TokenDto> findTokensPerExpedient(Long expedientId, String processInstanceId) {
		return delegate.findTokensPerExpedient(expedientId,processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean activar(Long expedientId, Long tokenId, boolean activar) {
		return delegate.activar(expedientId,tokenId, activar);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<String> findArrivingNodeNames(Long expedientId, String tokenId) {
		return delegate.findArrivingNodeNames(expedientId,tokenId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TokenDto findById(Long expedientId, String tokenId) {
		return delegate.findById(expedientId,tokenId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void tokenRetrocedir(Long expedientId, String tokenId, String nodeName, boolean cancelTasks) {
		delegate.tokenRetrocedir(expedientId, tokenId, nodeName, cancelTasks);
	}
}
