/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import net.conselldemallorca.helium.v3.core.api.dto.TokenDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

/**
 * EJB que implementa la interfície del servei ExpedientTokenService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ExpedientTokenServiceBean implements ExpedientTokenService {

	@Autowired
	ExpedientTokenService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TokenDto> findAmbInstanciaProces(
			Long expedientId,
			String processInstanceId) {
		return delegate.findAmbInstanciaProces(
				expedientId,
				processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean canviarEstatActiu(
			Long expedientId,
			Long tokenId,
			boolean activar) {
		return delegate.canviarEstatActiu(
				expedientId,
				tokenId,
				activar);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<String> findArrivingNodeNames(
			Long expedientId,
			String tokenId) {
		return delegate.findArrivingNodeNames(
				expedientId,
				tokenId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TokenDto findById(
			Long expedientId,
			String tokenId) {
		return delegate.findById(
				expedientId,
				tokenId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void retrocedir(
			Long expedientId,
			String tokenId,
			String nodeName,
			boolean cancelTasks) {
		delegate.retrocedir(
				expedientId,
				tokenId,
				nodeName,
				cancelTasks);
	}

}
