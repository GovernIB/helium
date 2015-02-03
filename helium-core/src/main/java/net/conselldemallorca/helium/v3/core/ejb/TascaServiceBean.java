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
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaValidacioSignaturaDto;
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
	public List<ExpedientTascaDto> findDadesPerIds(Set<Long> ids) {
		return delegate.findDadesPerIds(ids);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Long> findIdsPerFiltre(Long entornId, Long expedientTipusId, String responsable, String tasca, String expedient, Date dataCreacioInici, Date dataCreacioFi, Date dataLimitInici, Date dataLimitFi, Integer prioritat, boolean mostrarTasquesPersonals, boolean mostrarTasquesGrup) {
		return delegate.findIdsPerFiltre(entornId, expedientTipusId, responsable, tasca, expedient, dataCreacioInici, dataCreacioFi, dataLimitInici, dataLimitFi, prioritat, mostrarTasquesPersonals, mostrarTasquesGrup);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TascaDocumentDto findDocument(String tascaId, Long docId) {
		return delegate.findDocument(tascaId, docId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Long guardarDocumentTasca(Long entornId, String taskInstanceId, String documentCodi, Date documentData, String arxiuNom, byte[] arxiuContingut, String user) {
		return delegate.guardarDocumentTasca(entornId, taskInstanceId, documentCodi, documentData, arxiuNom, arxiuContingut, user);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void esborrarDocument(String taskInstanceId, String documentCodi, String user) {
		delegate.esborrarDocument(taskInstanceId, documentCodi, user);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<RespostaValidacioSignaturaDto> verificarSignatura(String tascaId, Long docId) throws Exception {
		return delegate.verificarSignatura(tascaId, docId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean signarDocumentTascaAmbToken(Long expedientId, Long docId, String tascaId, String token, byte[] signatura) throws Exception {
		return delegate.signarDocumentTascaAmbToken(expedientId, docId, tascaId, token, signatura);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDocumentDto> findDocumentsSignar(String id) {
		return delegate.findDocumentsSignar(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean hasDocuments(String tascaId) {
		return delegate.hasDocuments(tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean hasDocumentsSignar(String tascaId) {
		return delegate.hasDocumentsSignar(tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ParellaCodiValorDto> getTasquesExecucionsMassivesAmbDefinicioProcesId(Long definicioProcesId) {
		return delegate.getTasquesExecucionsMassivesAmbDefinicioProcesId(definicioProcesId);
	}
}
