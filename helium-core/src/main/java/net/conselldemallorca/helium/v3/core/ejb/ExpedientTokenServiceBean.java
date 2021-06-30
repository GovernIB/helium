/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.helium.logic.intf.dto.TokenDto;
import es.caib.helium.logic.intf.service.ExpedientTokenService;

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
			String processInstanceId,
			Long tokenId,
			boolean activar) {
		return delegate.canviarEstatActiu(
				expedientId,
				processInstanceId,
				tokenId,
				activar);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<String> findArrivingNodeNames(
			Long expedientId,
			String processInstanceId,
			String tokenId) {
		return delegate.findArrivingNodeNames(
				expedientId,
				processInstanceId,
				tokenId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TokenDto findById(
			Long expedientId,
			String processInstanceId,
			String tokenId) {
		return delegate.findById(
				expedientId,
				processInstanceId,
				tokenId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void retrocedir(
			Long expedientId,
			String processInstanceId,
			String tokenId,
			String nodeName,
			boolean cancelTasks) {
		delegate.retrocedir(
				expedientId,
				processInstanceId,
				tokenId,
				nodeName,
				cancelTasks);
	}

}
