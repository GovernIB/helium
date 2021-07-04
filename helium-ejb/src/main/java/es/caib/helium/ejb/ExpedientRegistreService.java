/**
 * 
 */
package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.ExpedientTascaDto;
import es.caib.helium.logic.intf.dto.InformacioRetroaccioDto;
import es.caib.helium.logic.intf.dto.InstanciaProcesDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
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
public class ExpedientRegistreService extends AbstractService<es.caib.helium.logic.intf.service.ExpedientRegistreService> implements es.caib.helium.logic.intf.service.ExpedientRegistreService {

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public SortedSet<Entry<InstanciaProcesDto, List<InformacioRetroaccioDto>>> findInformacioRetroaccioExpedientOrdenatPerData(
			Long expedientId,
			boolean detall) throws NoTrobatException, PermisDenegatException {
		return getDelegateService().findInformacioRetroaccioExpedientOrdenatPerData(
				expedientId,
				detall);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Map<String, ExpedientTascaDto> findTasquesExpedientPerRetroaccio(
			Long expedientId) throws NoTrobatException, PermisDenegatException {
		return getDelegateService().findTasquesExpedientPerRetroaccio(
				expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void executaRetroaccio(
			Long expedientId,
			Long logId,
			boolean retrocedirPerTasques) throws NoTrobatException, PermisDenegatException {
		getDelegateService().executaRetroaccio(
				expedientId,
				logId,
				retrocedirPerTasques);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void eliminaInformacioRetroaccio(
			Long expedientId) throws NoTrobatException, PermisDenegatException {
		getDelegateService().eliminaInformacioRetroaccio(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<InformacioRetroaccioDto> findInformacioRetroaccioTascaOrdenatPerData(
			Long expedientId,
			Long logId) throws NoTrobatException, PermisDenegatException {
		return getDelegateService().findInformacioRetroaccioTascaOrdenatPerData(
				expedientId,
				logId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<InformacioRetroaccioDto> findInformacioRetroaccioAccioRetrocesOrdenatsPerData(
			Long expedientId,
			Long logId) throws NoTrobatException, PermisDenegatException {
		return getDelegateService().findInformacioRetroaccioAccioRetrocesOrdenatsPerData(
				expedientId,
				logId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public InformacioRetroaccioDto findInformacioRetroaccioById(
			Long logId) throws NoTrobatException, PermisDenegatException {
		return getDelegateService().findInformacioRetroaccioById(logId);
	}

}
