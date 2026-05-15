/**
 *
 */
package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.TokenDto;
import es.caib.helium.logic.intf.service.ExpedientTokenService;

/**
 * EJB que implementa la interfície del servei ExpedientTokenService.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ExpedientTokenServiceBean extends AbstractServiceEjb<ExpedientTokenService> implements ExpedientTokenService {

	@Delegate
	ExpedientTokenService delegateService;

	protected void setDelegateService(ExpedientTokenService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TokenDto> findAmbInstanciaProces(
			Long expedientId,
			String processInstanceId) {
		return delegateService.findAmbInstanciaProces(
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
		return delegateService.canviarEstatActiu(
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
		return delegateService.findArrivingNodeNames(
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
		return delegateService.findById(
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
		delegateService.retrocedir(
				expedientId,
				processInstanceId,
				tokenId,
				nodeName,
				cancelTasks);
	}

}
