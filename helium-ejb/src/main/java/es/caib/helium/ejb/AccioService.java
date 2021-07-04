/**
 * 
 */
package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.AccioDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
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
public class AccioService extends AbstractService<es.caib.helium.logic.intf.service.AccioService> implements es.caib.helium.logic.intf.service.AccioService {

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AccioDto create(
			Long expedientTipusId,
			Long definicioProcesId, 
			AccioDto accio) throws PermisDenegatException {
		return getDelegateService().create(expedientTipusId, definicioProcesId, accio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AccioDto update(AccioDto accio) throws NoTrobatException, PermisDenegatException {
		return getDelegateService().update(accio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long accioAccioId) throws NoTrobatException, PermisDenegatException {
		getDelegateService().delete(accioAccioId);		
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AccioDto findAmbId(Long expedientTipusId, Long id) throws NoTrobatException {
		return getDelegateService().findAmbId(expedientTipusId, id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<AccioDto> findAll(
			Long expedientTipusId,
			Long definicioProcesId) throws NoTrobatException, PermisDenegatException {
		return getDelegateService().findAll(expedientTipusId, definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AccioDto findAmbCodi(
			Long tipusExpedientId,
			Long definicioProcesId,
			String codi) throws NoTrobatException {
		return getDelegateService().findAmbCodi(tipusExpedientId, definicioProcesId, codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<AccioDto> findPerDatatable(
			Long expedientTipusId,
			Long definicioProcesId,			
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return getDelegateService().findPerDatatable(
				expedientTipusId,
				expedientTipusId,
				filtre, 
				paginacioParams);
	}
}