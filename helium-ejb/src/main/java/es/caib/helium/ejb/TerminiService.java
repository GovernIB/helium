/**
 * 
 */
package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.TerminiDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Servei per a gestionar els tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class TerminiService extends AbstractService<es.caib.helium.logic.intf.service.TerminiService> implements es.caib.helium.logic.intf.service.TerminiService {

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiDto findAmbId(Long expedientTipusId, Long terminiId) {
		return getDelegateService().findAmbId(expedientTipusId, terminiId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiDto findAmbCodi(
			Long expedientTipusId, 
			Long definicioProcesId, 
			String codi) {
		return getDelegateService().findAmbCodi(expedientTipusId, definicioProcesId, codi);
	}

	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TerminiDto> findAll(
			Long expedientTipusId,
			Long definicioProcesId) throws NoTrobatException, PermisDenegatException {
		return getDelegateService().findAll(expedientTipusId, definicioProcesId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiDto create(
			Long expedientTipusId,
			Long definicioProcesId, 
			TerminiDto termini) {
		return getDelegateService().create(expedientTipusId, definicioProcesId, termini);
		
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiDto update(TerminiDto termini) {
		return getDelegateService().update(termini);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long terminiId) throws NoTrobatException, PermisDenegatException {
		getDelegateService().delete(terminiId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<TerminiDto> findPerDatatable(
			Long expedientTipusId,
			Long definicioProcesId, 
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return getDelegateService().findPerDatatable(expedientTipusId, definicioProcesId, filtre, paginacioParams);
	}
}