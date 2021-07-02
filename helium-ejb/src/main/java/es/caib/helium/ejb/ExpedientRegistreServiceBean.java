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
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.emiserv.logic.intf.exception.NoTrobatException;
import es.caib.emiserv.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.intf.dto.ExpedientTascaDto;
import es.caib.helium.logic.intf.dto.InformacioRetroaccioDto;
import es.caib.helium.logic.intf.dto.InstanciaProcesDto;
import es.caib.helium.logic.intf.service.ExpedientRegistreService;

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
