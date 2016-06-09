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

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;

/**
 * Servei per a enllaçar les llibreries jBPM 3 amb la funcionalitat de Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ExpedientTipusServiceBean implements ExpedientTipusService {

	@Autowired
	ExpedientTipusService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto create(
			Long entornId, 
			String codi, 
			String nom, 
			boolean teTitol, 
			boolean demanaTitol, 
			boolean teNumero,
			boolean demanaNumero, 
			String expressioNumero, 
			boolean reiniciarCadaAny, 
			List<Integer> sequenciesAny, 
			List<Long> sequenciesValor, 
			Long sequencia,
			String responsableDefecteCodi, 
			boolean restringirPerGrup, 
			boolean seleccionarAny, 
			boolean ambRetroaccio) {
		return delegate.create(entornId, codi, nom, teTitol, demanaTitol, teNumero, demanaNumero, expressioNumero, reiniciarCadaAny, sequenciesAny, sequenciesValor, sequencia, responsableDefecteCodi, restringirPerGrup, seleccionarAny, ambRetroaccio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void update(
			Long entornId,
			Long expedientTipusId,
			String nom, 
			boolean teTitol, 
			boolean demanaTitol, 
			boolean teNumero,
			boolean demanaNumero, 
			String expressioNumero, 
			boolean reiniciarCadaAny, 
			List<Integer> sequenciesAny, 
			List<Long> sequenciesValor, 
			Long sequencia,
			String responsableDefecteCodi, 
			boolean restringirPerGrup, 
			boolean seleccionarAny, 
			boolean ambRetroaccio) {
		delegate.update(entornId, expedientTipusId, nom, teTitol, demanaTitol, teNumero, demanaNumero, expressioNumero, reiniciarCadaAny, sequenciesAny, sequenciesValor, sequencia, responsableDefecteCodi, restringirPerGrup, seleccionarAny, ambRetroaccio);
	}

	@Override
	public void delete(
			Long entornId,
			Long expedientTipusId) {
		delegate.delete(entornId, expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ExpedientTipusDto> findTipusAmbFiltrePaginat(
			Long entornId, 
			String filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findTipusAmbFiltrePaginat(entornId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto findTipusAmbId(
			Long entornId,
			Long expedientTipusId) {
		return delegate.findTipusAmbId(entornId, expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto findTipusAmbCodi(
			Long entornId, 
			String codi) {
		return delegate.findTipusAmbCodi(entornId, codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean potEscriure(Long entornId, Long id) {
		return delegate.potEscriure(entornId, id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean potEsborrar(Long entornId, Long id) {
		return delegate.potEsborrar(entornId, id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean potAdministrar(Long entornId, Long id) {
		return delegate.potAdministrar(entornId, id);
	}
}
