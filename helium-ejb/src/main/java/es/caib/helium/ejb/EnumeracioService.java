/**
 * 
 */
package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.EnumeracioDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusEnumeracioValorDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.intf.exception.ValidacioException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Servei per a gestionar les enumeracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class EnumeracioService extends AbstractService<es.caib.helium.logic.intf.service.EnumeracioService> implements es.caib.helium.logic.intf.service.EnumeracioService {

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<EnumeracioDto> findPerDatatable(
			Long entornId,
			Long expedientTipusId,
			boolean incloureGlobals,
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return getDelegateService().findPerDatatable(entornId, expedientTipusId, incloureGlobals, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EnumeracioDto create(Long entornId, Long expedientTipusId, EnumeracioDto enumeracio)
			throws PermisDenegatException {
		return getDelegateService().create(entornId, expedientTipusId, enumeracio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EnumeracioDto findAmbCodi(
			Long entornId,
			Long expedientTipusId, String codi)
			throws NoTrobatException {
		return getDelegateService().findAmbCodi(entornId, expedientTipusId, codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long enumeracioId) throws NoTrobatException, PermisDenegatException {
		getDelegateService().delete(enumeracioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EnumeracioDto findAmbId(Long expedientTipusId, Long enumeracioId) throws NoTrobatException {
		return getDelegateService().findAmbId(expedientTipusId, enumeracioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EnumeracioDto> findGlobals(Long entornId) throws NoTrobatException, PermisDenegatException {
		return getDelegateService().findGlobals(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EnumeracioDto update(EnumeracioDto enumeracio)
			throws NoTrobatException, PermisDenegatException {
		return getDelegateService().update(enumeracio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ExpedientTipusEnumeracioValorDto> valorFindPerDatatable(
			Long enumeracioId, 
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return getDelegateService().valorFindPerDatatable(enumeracioId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusEnumeracioValorDto valorsCreate(Long expedientTipusId, Long enumeracioId,
			Long entornId, ExpedientTipusEnumeracioValorDto enumeracio) throws PermisDenegatException {
		return getDelegateService().valorsCreate(expedientTipusId, enumeracioId, entornId, enumeracio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void valorDelete(Long valorId) throws NoTrobatException, PermisDenegatException {
		getDelegateService().valorDelete(valorId);		
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusEnumeracioValorDto valorFindAmbId(Long valorId) throws NoTrobatException {
		return getDelegateService().valorFindAmbId(valorId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusEnumeracioValorDto valorUpdate(ExpedientTipusEnumeracioValorDto enumeracio)
			throws NoTrobatException, PermisDenegatException {
		return getDelegateService().valorUpdate(enumeracio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusEnumeracioValorDto valorFindAmbCodi(Long expedientTipusId, Long enumeracioId,
			String codi) throws NoTrobatException {
		return getDelegateService().valorFindAmbCodi(expedientTipusId, enumeracioId, codi);
	}

	@Override
	public boolean valorMoure(Long valorId, int posicio) throws NoTrobatException {
		return getDelegateService().valorMoure(valorId, posicio);
	}

	@Override
	public void enumeracioDeleteAllByEnumeracio(Long enumeracioId)
			throws NoTrobatException, PermisDenegatException, ValidacioException {
		getDelegateService().enumeracioDeleteAllByEnumeracio(enumeracioId);
	}

	@Override
	public List<ExpedientTipusEnumeracioValorDto> valorsFind(Long enumeracioId) throws NoTrobatException {
		// TODO Auto-generated method stub
		return getDelegateService().valorsFind(enumeracioId);
	}

}