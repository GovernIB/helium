/**
 * 
 */
package es.caib.helium.ejb;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.commons.dto.ExpedientLogDto;
import es.caib.helium.commons.dto.ExpedientTascaDto;
import es.caib.helium.commons.dto.InstanciaProcesDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.exception.PermisDenegatException;
import es.caib.helium.logic.intf.service.ExpedientRegistreService;

/**
 * EJB que implementa la interfície del servei ExpedientRegistreService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
//@Interceptors(SpringBeanAutowiringInterceptor.class)
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
			Long expedientId,
			boolean detall) throws NoTrobatException {
		return delegate.registreFindExpedientCanvisEstat(expedientId, detall);
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
