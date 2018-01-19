/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEnumeracioValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.v3.core.api.service.EnumeracioService;

/**
 * Servei per a gestionar les enumeracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class EnumeracioServiceBean implements EnumeracioService {

	@Autowired
	EnumeracioService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<EnumeracioDto> findPerDatatable(
			Long entornId,
			Long expedientTipusId,
			boolean incloureGlobals,
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.findPerDatatable(entornId, expedientTipusId, incloureGlobals, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EnumeracioDto create(Long entornId, Long expedientTipusId, EnumeracioDto enumeracio)
			throws PermisDenegatException {
		return delegate.create(entornId, expedientTipusId, enumeracio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EnumeracioDto findAmbCodi(
			Long entornId,
			Long expedientTipusId, String codi)
			throws NoTrobatException {
		return delegate.findAmbCodi(entornId, expedientTipusId, codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long enumeracioId) throws NoTrobatException, PermisDenegatException {
		delegate.delete(enumeracioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EnumeracioDto findAmbId(Long expedientTipusId, Long enumeracioId) throws NoTrobatException {
		return delegate.findAmbId(expedientTipusId, enumeracioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EnumeracioDto> findGlobals(Long entornId) throws NoTrobatException, PermisDenegatException {
		return delegate.findGlobals(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EnumeracioDto update(EnumeracioDto enumeracio)
			throws NoTrobatException, PermisDenegatException {
		return delegate.update(enumeracio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ExpedientTipusEnumeracioValorDto> valorFindPerDatatable(
			Long enumeracioId, 
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.valorFindPerDatatable(enumeracioId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusEnumeracioValorDto valorsCreate(Long expedientTipusId, Long enumeracioId,
			Long entornId, ExpedientTipusEnumeracioValorDto enumeracio) throws PermisDenegatException {
		return delegate.valorsCreate(expedientTipusId, enumeracioId, entornId, enumeracio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void valorDelete(Long valorId) throws NoTrobatException, PermisDenegatException {
		delegate.valorDelete(valorId);		
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusEnumeracioValorDto valorFindAmbId(Long valorId) throws NoTrobatException {
		return delegate.valorFindAmbId(valorId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusEnumeracioValorDto valorUpdate(ExpedientTipusEnumeracioValorDto enumeracio)
			throws NoTrobatException, PermisDenegatException {
		return delegate.valorUpdate(enumeracio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusEnumeracioValorDto valorFindAmbCodi(Long expedientTipusId, Long enumeracioId,
			String codi) throws NoTrobatException {
		return delegate.valorFindAmbCodi(expedientTipusId, enumeracioId, codi);
	}

	@Override
	public boolean valorMoure(Long valorId, int posicio) throws NoTrobatException {
		return delegate.valorMoure(valorId, posicio);
	}

	@Override
	public void enumeracioDeleteAllByEnumeracio(Long enumeracioId)
			throws NoTrobatException, PermisDenegatException, ValidacioException {
		delegate.enumeracioDeleteAllByEnumeracio(enumeracioId);
	}

}