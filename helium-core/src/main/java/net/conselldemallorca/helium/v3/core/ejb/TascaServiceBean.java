/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.SeleccioOpcioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDocumentDto;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

/**
 * Servei per a enllaçar les llibreries jBPM 3 amb la funcionalitat de Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class TascaServiceBean implements TascaService {

	@Autowired
	TascaService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto findAmbIdPerExpedient(
			String id,
			Long expedientId) {
		return delegate.findAmbIdPerExpedient(id, expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto findAmbIdPerTramitacio(
			String id) {
		return delegate.findAmbIdPerTramitacio(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ExpedientTascaDto> findPerFiltrePaginat(
			Long entornId,
			String consultaTramitacioMassivaTascaId,
			Long expedientTipusId,
			String responsable,
			String tasca,
			String expedient,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Date dataLimitInici,
			Date dataLimitFi,
			Integer prioritat,
			boolean mostrarTasquesPersonals,
			boolean mostrarTasquesGrup,
			PaginacioParamsDto paginacioParams) {
		return delegate.findPerFiltrePaginat(
				entornId,
				consultaTramitacioMassivaTascaId,
				expedientTipusId,
				responsable,
				tasca,
				expedient,
				dataCreacioInici,
				dataCreacioFi,
				dataLimitInici,
				dataLimitFi,
				prioritat,
				mostrarTasquesPersonals,
				mostrarTasquesGrup,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDadaDto> findDades(
			String id) {
		return delegate.findDades(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TascaDadaDto findDada(
			String id,
			String variableCodi) {
		return delegate.findDada(id, variableCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDocumentDto> findDocuments(
			String id) {
		return delegate.findDocuments(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<SeleccioOpcioDto> findllistaValorsPerCampDesplegable(
			String id,
			Long campId,
			String textFiltre,
			Map<String, Object> valorsFormulari) {
		return delegate.findllistaValorsPerCampDesplegable(
				id,
				campId,
				textFiltre,
				valorsFormulari);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto agafar(
			String id) {
		return delegate.agafar(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto alliberar(
			String id) {
		return delegate.alliberar(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto guardar(
			String id,
			Map<String, Object> variables) {
		return delegate.guardar(
				id,
				variables);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto validar(
			String id,
			Map<String, Object> variables) {
		return delegate.validar(
				id,
				variables);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto restaurar(String id) {
		return delegate.restaurar(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void completar(
			String id,
			String outcome) {
		delegate.completar(
				id,
				outcome);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delegacioCrear(
			String id,
			String usuariDesti,
			String comentari,
			boolean supervisada) {
		delegate.delegacioCrear(
				id,
				usuariDesti,
				comentari,
				supervisada);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delegacioCancelar(
			String id) {
		delegate.delegacioCancelar(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void executarAccio(
			String id,
			String accio) {
		delegate.executarAccio(
				id,
				accio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void guardarFilaRegistre(
			String id,
			String campCodi,
			int index,
			Object[] valors) {
		delegate.guardarFilaRegistre(
				id,
				campCodi,
				index,
				valors);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void esborrarFilaRegistre(
			String id,
			String campCodi,
			int index) {
		delegate.esborrarFilaRegistre(
				id,
				campCodi,
				index);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDadaDto> findDadesPerTascaDto(ExpedientTascaDto tasca) {
		return delegate.findDadesPerTascaDto(tasca);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Long> findIdsAmbFiltre(
			Long entornId,
			String consultaTramitacioMassivaTascaId,
			Long expedientTipusId,
			String responsable) {
		return delegate.findIdsAmbFiltre(entornId, consultaTramitacioMassivaTascaId, expedientTipusId, responsable);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTascaDto> findDadesPerIds(Set<Long> ids) {
		return delegate.findDadesPerIds(ids);
	}
}
