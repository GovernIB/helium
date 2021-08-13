/**
 * 
 */
package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.logic.intf.dto.ExpedientTascaDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;

/**
 * EJB que implementa la interfície del servei ExpedientTascaService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ExpedientTascaService extends AbstractService<es.caib.helium.logic.intf.service.ExpedientTascaService> implements es.caib.helium.logic.intf.service.ExpedientTascaService {

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTascaDto> findAmbInstanciaProces(
			Long expedientId,
			String processInstanceId)
			throws NoTrobatException, PermisDenegatException {
		return getDelegateService().findAmbInstanciaProces(
				expedientId,
				processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void cancelar(Long expedientId, String tascaId) {
		getDelegateService().cancelar(
				expedientId,
				tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void suspendre(Long expedientId, String tascaId) {
		getDelegateService().suspendre(
				expedientId,
				tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void reprendre(Long expedientId, String tascaId) {
		getDelegateService().reprendre(
				expedientId,
				tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void reassignar(
			Long expedientId,
			String tascaId,
			String expressio) {
		getDelegateService().reassignar(
				expedientId,
				tascaId,
				expressio);
	}

}
