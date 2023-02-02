/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientLogDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientRegistreService;

/**
 * EJB que implementa la interfície del servei ExpedientRegistreService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ExpedientRegistreServiceBean implements ExpedientRegistreService {

	@Autowired
	ExpedientRegistreService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public SortedSet<Entry<InstanciaProcesDto, List<ExpedientLogDto>>> registreFindLogsOrdenatsPerData(
			Long expedientId,
			boolean detall) throws NoTrobatException, PermisDenegatException {
		return delegate.registreFindLogsOrdenatsPerData(
				expedientId,
				detall);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientLogDto> registreFindExpedientCanvisEstat(
			Long expedientId) throws NoTrobatException {
		return delegate.registreFindExpedientCanvisEstat(expedientId);
	}

	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Map<String, ExpedientTascaDto> registreFindTasquesPerLogExpedient(
			Long expedientId) throws NoTrobatException, PermisDenegatException {
		return delegate.registreFindTasquesPerLogExpedient(
				expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void registreRetrocedir(
			Long expedientId,
			Long logId,
			boolean retrocedirPerTasques) throws NoTrobatException, PermisDenegatException {
		delegate.registreRetrocedir(
				expedientId,
				logId,
				retrocedirPerTasques);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void registreBuidarLog(
			Long expedientId) throws NoTrobatException, PermisDenegatException {
		delegate.registreBuidarLog(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientLogDto> registreFindLogsTascaOrdenatsPerData(
			Long expedientId,
			Long logId) throws NoTrobatException, PermisDenegatException {
		return delegate.registreFindLogsTascaOrdenatsPerData(
				expedientId,
				logId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientLogDto> registreFindLogsRetroceditsOrdenatsPerData(
			Long expedientId,
			Long logId) throws NoTrobatException, PermisDenegatException {
		return delegate.registreFindLogsRetroceditsOrdenatsPerData(
				expedientId,
				logId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientLogDto registreFindLogById(
			Long logId) throws NoTrobatException, PermisDenegatException {
		return delegate.registreFindLogById(logId);
	}

}
