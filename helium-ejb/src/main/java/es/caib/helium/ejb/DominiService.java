/**
 * 
 */
package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.DominiDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.intf.extern.domini.FilaResultat;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * Servei per a gestionar els dominins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class DominiService extends AbstractService<es.caib.helium.logic.intf.service.DominiService> implements es.caib.helium.logic.intf.service.DominiService {

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<DominiDto> findPerDatatable(
			Long entornId,
			Long expedientTipusId,
			boolean incloureGlobals,
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return getDelegateService().findPerDatatable(entornId, expedientTipusId, incloureGlobals, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DominiDto create(Long entornId, Long expedientTipusId, DominiDto domini)
			throws PermisDenegatException {
		return getDelegateService().create(entornId, expedientTipusId, domini);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DominiDto findAmbCodi(
			Long entornId,
			Long expedientTipusId, String codi)
			throws NoTrobatException {
		return getDelegateService().findAmbCodi(entornId, expedientTipusId, codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long dominiId) throws NoTrobatException, PermisDenegatException {
		getDelegateService().delete(dominiId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DominiDto findAmbId(Long expedientTipusId, Long dominiId) throws NoTrobatException {
		return getDelegateService().findAmbId(expedientTipusId, dominiId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DominiDto> findGlobals(Long entornId) throws NoTrobatException, PermisDenegatException {
		return getDelegateService().findGlobals(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DominiDto update(DominiDto domini)
			throws NoTrobatException, PermisDenegatException {
		return getDelegateService().update(domini);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<FilaResultat> consultaDomini(Long entornId, Long dominiId, String dominiWsId,
											 Map<String, Object> params) {
		return getDelegateService().consultaDomini(entornId, dominiId, dominiWsId, params);
	}
}