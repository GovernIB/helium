/**
 * 
 */
package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.TokenDto;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * EJB que implementa la interfície del servei ExpedientTokenService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ExpedientTokenService extends AbstractService<es.caib.helium.logic.intf.service.ExpedientTokenService> implements es.caib.helium.logic.intf.service.ExpedientTokenService {

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TokenDto> findAmbInstanciaProces(
			Long expedientId,
			String processInstanceId) {
		return getDelegateService().findAmbInstanciaProces(
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
		return getDelegateService().canviarEstatActiu(
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
		return getDelegateService().findArrivingNodeNames(
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
		return getDelegateService().findById(
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
		getDelegateService().retrocedir(
				expedientId,
				processInstanceId,
				tokenId,
				nodeName,
				cancelTasks);
	}

}
