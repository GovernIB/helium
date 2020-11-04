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

import net.conselldemallorca.helium.v3.core.api.service.ExpedientReindexacioService;

/**
 * EJB que implementa la interfície del servei ExpedientReindexacioService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ExpedientReindexacioServiceBean implements ExpedientReindexacioService {

	@Autowired
	ExpedientReindexacioService delegate;

	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Long consultaCountPendentsReindexacio(long expedientTipusId) {
		return delegate.consultaCountPendentsReindexacio(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Long consultaCountErrorsReindexacio(long expedientTipusId) {
		return delegate.consultaCountPendentsReindexacio(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Long> consultaIdsErrorReindexació(long expedientTipusId) {
		return delegate.consultaIdsErrorReindexació(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Long> consultaIdsPendentReindexació(long expedientTipusId) {
		return delegate.consultaIdsPendentReindexació(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Long> consultaIdsExpedients(long expedientTipusId) {
		return delegate.consultaIdsExpedients(expedientTipusId);
	}

	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Long countPendentReindexacioAsincrona() {
		return delegate.countPendentReindexacioAsincrona();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Object[]> getDades(Long entornId) {
		return delegate.getDades(entornId);
	}
}
