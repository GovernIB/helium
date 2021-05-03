/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.InformacioRetroaccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientRegistreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;

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
	public SortedSet<Entry<InstanciaProcesDto, List<InformacioRetroaccioDto>>> findInformacioRetroaccioExpedientOrdenatPerData(
			Long expedientId,
			boolean detall) throws NoTrobatException, PermisDenegatException {
		return delegate.findInformacioRetroaccioExpedientOrdenatPerData(
				expedientId,
				detall);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Map<String, ExpedientTascaDto> findTasquesExpedientPerRetroaccio(
			Long expedientId) throws NoTrobatException, PermisDenegatException {
		return delegate.findTasquesExpedientPerRetroaccio(
				expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void executaRetroaccio(
			Long expedientId,
			Long logId,
			boolean retrocedirPerTasques) throws NoTrobatException, PermisDenegatException {
		delegate.executaRetroaccio(
				expedientId,
				logId,
				retrocedirPerTasques);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void eliminaInformacioRetroaccio(
			Long expedientId) throws NoTrobatException, PermisDenegatException {
		delegate.eliminaInformacioRetroaccio(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<InformacioRetroaccioDto> findInformacioRetroaccioTascaOrdenatPerData(
			Long expedientId,
			Long logId) throws NoTrobatException, PermisDenegatException {
		return delegate.findInformacioRetroaccioTascaOrdenatPerData(
				expedientId,
				logId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<InformacioRetroaccioDto> findInformacioRetroaccioAccioRetrocesOrdenatsPerData(
			Long expedientId,
			Long logId) throws NoTrobatException, PermisDenegatException {
		return delegate.findInformacioRetroaccioAccioRetrocesOrdenatsPerData(
				expedientId,
				logId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public InformacioRetroaccioDto findInformacioRetroaccioById(
			Long logId) throws NoTrobatException, PermisDenegatException {
		return delegate.findInformacioRetroaccioById(logId);
	}

}
