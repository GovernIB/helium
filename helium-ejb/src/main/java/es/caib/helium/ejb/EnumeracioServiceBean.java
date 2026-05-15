/**
 *
 */
package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.EnumeracioDto;
import es.caib.helium.commons.dto.ExpedientTipusEnumeracioValorDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.exception.PermisDenegatException;
import es.caib.helium.logic.intf.service.EnumeracioService;

/**
 * Servei per a gestionar les enumeracions.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class EnumeracioServiceBean extends AbstractServiceEjb<EnumeracioService> implements EnumeracioService {

	@Delegate
	EnumeracioService delegateService;

	protected void setDelegateService(EnumeracioService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<EnumeracioDto> findPerDatatable(
			Long entornId,
			Long expedientTipusId,
			boolean incloureGlobals,
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegateService.findPerDatatable(entornId, expedientTipusId, incloureGlobals, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EnumeracioDto create(Long entornId, Long expedientTipusId, EnumeracioDto enumeracio)
			throws PermisDenegatException {
		return delegateService.create(entornId, expedientTipusId, enumeracio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EnumeracioDto findAmbCodi(
			Long entornId,
			Long expedientTipusId, String codi)
			throws NoTrobatException {
		return delegateService.findAmbCodi(entornId, expedientTipusId, codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long enumeracioId) throws NoTrobatException, PermisDenegatException {
		delegateService.delete(enumeracioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EnumeracioDto findAmbId(Long expedientTipusId, Long enumeracioId) throws NoTrobatException {
		return delegateService.findAmbId(expedientTipusId, enumeracioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EnumeracioDto> findGlobals(Long entornId) throws NoTrobatException, PermisDenegatException {
		return delegateService.findGlobals(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EnumeracioDto update(EnumeracioDto enumeracio)
			throws NoTrobatException, PermisDenegatException {
		return delegateService.update(enumeracio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ExpedientTipusEnumeracioValorDto> valorFindPerDatatable(
			Long enumeracioId,
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegateService.valorFindPerDatatable(enumeracioId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusEnumeracioValorDto valorsCreate(Long expedientTipusId, Long enumeracioId,
			Long entornId, ExpedientTipusEnumeracioValorDto enumeracio) throws PermisDenegatException {
		return delegateService.valorsCreate(expedientTipusId, enumeracioId, entornId, enumeracio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusEnumeracioValorDto valorsUpsert(Long expedientTipusId, Long enumeracioId,
			Long entornId, ExpedientTipusEnumeracioValorDto enumeracio) throws PermisDenegatException {
		return delegateService.valorsUpsert(expedientTipusId, enumeracioId, entornId, enumeracio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void valorDelete(Long valorId) throws NoTrobatException, PermisDenegatException {
		delegateService.valorDelete(valorId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusEnumeracioValorDto valorFindAmbId(Long valorId) throws NoTrobatException {
		return delegateService.valorFindAmbId(valorId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusEnumeracioValorDto valorUpdate(ExpedientTipusEnumeracioValorDto enumeracio)
			throws NoTrobatException, PermisDenegatException {
		return delegateService.valorUpdate(enumeracio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusEnumeracioValorDto valorFindAmbCodi(Long expedientTipusId, Long enumeracioId,
			String codi) throws NoTrobatException {
		return delegateService.valorFindAmbCodi(expedientTipusId, enumeracioId, codi);
	}

	@Override
	public boolean valorMoure(Long valorId, int posicio) throws NoTrobatException {
		return delegateService.valorMoure(valorId, posicio);
	}

	@Override
	public List<ExpedientTipusEnumeracioValorDto> valorsFind(Long enumeracioId) throws NoTrobatException {
		// TODO Auto-generated method stub
		return delegateService.valorsFind(enumeracioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean valorInUse(Long valorId) throws NoTrobatException, PermisDenegatException {
		return delegateService.valorInUse(valorId);
	}

}