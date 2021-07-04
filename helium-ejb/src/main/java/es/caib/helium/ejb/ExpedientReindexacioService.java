/**
 * 
 */
package es.caib.helium.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * EJB que implementa la interfície del servei ExpedientReindexacioService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ExpedientReindexacioService extends AbstractService<es.caib.helium.logic.intf.service.ExpedientReindexacioService> implements es.caib.helium.logic.intf.service.ExpedientReindexacioService {

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Long consultaCountPendentsReindexacio(long expedientTipusId) {
		return getDelegateService().consultaCountPendentsReindexacio(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Long consultaCountErrorsReindexacio(long expedientTipusId) {
		return getDelegateService().consultaCountPendentsReindexacio(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Long> consultaIdsErrorReindexació(long expedientTipusId) {
		return getDelegateService().consultaIdsErrorReindexació(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Long> consultaIdsPendentReindexació(long expedientTipusId) {
		return getDelegateService().consultaIdsPendentReindexació(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Long> consultaIdsExpedients(long expedientTipusId) {
		return getDelegateService().consultaIdsExpedients(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Map<String, Object> getDadesReindexacio() {
		return getDelegateService().getDadesReindexacio();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Long countPendentReindexacioAsincrona() {
		return getDelegateService().countPendentReindexacioAsincrona();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Object[]> getDades(Long entornId) {
		return getDelegateService().getDades(entornId);
	}
}
