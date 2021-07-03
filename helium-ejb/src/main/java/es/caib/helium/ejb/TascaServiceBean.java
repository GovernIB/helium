/**
 * 
 */
package es.caib.helium.ejb;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.emiserv.logic.intf.exception.NoTrobatException;
import es.caib.emiserv.logic.intf.exception.ValidacioException;
import es.caib.helium.logic.intf.dto.ArxiuDto;
import es.caib.helium.logic.intf.dto.DocumentDto;
import es.caib.helium.logic.intf.dto.ExpedientTascaDto;
import es.caib.helium.logic.intf.dto.FormulariExternDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.SeleccioOpcioDto;
import es.caib.helium.logic.intf.dto.TascaDadaDto;
import es.caib.helium.logic.intf.dto.TascaDocumentDto;
import es.caib.helium.logic.intf.dto.TascaDto;
import es.caib.helium.logic.intf.service.TascaService;

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
	public List<Long> findIdsPerFiltre(
			Long entornId,
			Long expedientTipusId,
			String titol,
			String tasca,
			String responsable,
			String expedient,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Date dataLimitInici,
			Date dataLimitFi,
			Integer prioritat,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesTasquesMeves) {
		return delegate.findIdsPerFiltre(
				entornId,
				expedientTipusId,
				titol,
				tasca,
				responsable,
				expedient,
				dataCreacioInici,
				dataCreacioFi,
				dataLimitInici,
				dataLimitFi,
				prioritat,
				nomesTasquesPersonals,
				nomesTasquesGrup, 
				nomesTasquesMeves);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ExpedientTascaDto> findPerFiltrePaginat(
			Long entornId,
			String tramitacioMassivaTascaId,
			Long expedientTipusId,
			String titulo,
			String tasca,
			String responsable,
			String expedient,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Date dataLimitInici,
			Date dataLimitFi,
			Integer prioritat,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesTasquesMeves,
			PaginacioParamsDto paginacioParams) {
		return delegate.findPerFiltrePaginat(
				entornId,
				tramitacioMassivaTascaId,
				expedientTipusId,
				titulo,
				tasca,
				responsable,
				expedient,
				dataCreacioInici,
				dataCreacioFi,
				dataLimitInici,
				dataLimitFi,
				prioritat,
				nomesTasquesPersonals, 
				nomesTasquesGrup, 
				nomesTasquesMeves,
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
	public List<TascaDocumentDto> findDocuments(
			String id) {
		return delegate.findDocuments(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<SeleccioOpcioDto> findValorsPerCampDesplegable(
			String id,
			String processInstanceId,
			Long campId,
			String codiFiltre,
			String textFiltre,
			Long registreCampId,
			Integer registreIndex,
			Map<String, Object> valorsFormulari) {
		return delegate.findValorsPerCampDesplegable(
				id,
				processInstanceId,
				campId,
				codiFiltre,
				textFiltre,
				registreCampId,
				registreIndex,
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
	public void guardar(
			String taskId,
			Map<String, Object> variables) {
		delegate.guardar(
				taskId,
				variables);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void validar(
			String tascaId,
			Map<String, Object> variables) {
		delegate.validar(
				tascaId,
				variables);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void restaurar(String tascaId) {
		delegate.restaurar(tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void completar(
			String tascaId,
			String outcome) {
		delegate.completar(
				tascaId,
				outcome);
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
	public FormulariExternDto formulariExternObrir(
			String tascaId) {
		return delegate.formulariExternObrir(tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public FormulariExternDto formulariExternObrirTascaInicial(
			String tascaIniciId,
			Long expedientTipusId,
			Long definicioProcesId) {
		return delegate.formulariExternObrirTascaInicial(
				tascaIniciId,
				expedientTipusId,
				definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDadaDto> findDadesPerTascaDto(Long expedientTipusId, ExpedientTascaDto tasca) {
		return delegate.findDadesPerTascaDto(expedientTipusId, tasca);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTascaDto> findAmbIds(Set<Long> ids) {
		return delegate.findAmbIds(ids);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TascaDocumentDto findDocument(String tascaId, Long docId, Long expedientTipusId) {
		return delegate.findDocument(tascaId, docId, expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Long guardarDocumentTasca(
			Long entornId, 
			String taskInstanceId, 
			String documentCodi, 
			Date documentData, 
			String arxiuNom, 
			byte[] arxiuContingut, 
			String arxiuContentType, 
			boolean ambFirma,
			boolean firmaSeparada,
			byte[] firmaContingut,
			String user) {
		return delegate.guardarDocumentTasca(
				entornId, 
				taskInstanceId,
				documentCodi, 
				documentData, 
				arxiuNom, 
				arxiuContingut, 
				arxiuContentType, 
				ambFirma,
				firmaSeparada,
				firmaContingut,
				user);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void esborrarDocument(String taskInstanceId, String documentCodi, String user) {
		delegate.esborrarDocument(taskInstanceId, documentCodi, user);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean signarDocumentTascaAmbToken(String tascaId, String token, byte[] signatura) throws Exception {
		return delegate.signarDocumentTascaAmbToken(tascaId, token, signatura);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDocumentDto> findDocumentsSignar(String id) {
		return delegate.findDocumentsSignar(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean hasFormulari(String tascaId) {
		return delegate.hasFormulari(tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean hasDocuments(String tascaId) {
		return delegate.hasDocuments(tascaId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean hasDocumentsNotReadOnly(String tascaId) {
		return delegate.hasDocumentsNotReadOnly(tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean hasSignatures(String tascaId) {
		return delegate.hasSignatures(tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto getArxiuPerDocumentCodi(
			String tascaId,
			String documentCodi) {
		return delegate.getArxiuPerDocumentCodi(
				tascaId,
				documentCodi);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentDto getDocumentPerDocumentCodi(
			String tascaId, 
			String documentCodi) {
		return delegate.getDocumentPerDocumentCodi(
				tascaId, 
				documentCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean isTascaValidada(String tascaId) {
		return delegate.isTascaValidada(tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean isDocumentsComplet(String tascaId) {
		return delegate.isDocumentsComplet(tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean isSignaturesComplet(String tascaId) {
		return delegate.isSignaturesComplet(tascaId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void comprovarTasquesSegonPla() {
		delegate.comprovarTasquesSegonPla();
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void carregaTasquesSegonPla() {
		delegate.carregaTasquesSegonPla();
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void completaTascaSegonPla(String tascaId, Date iniciFinalitzacio) {
		delegate.completaTascaSegonPla(tascaId, iniciFinalitzacio);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void guardarErrorFinalitzacio(String tascaId, String errorFinalitzacio) {
		delegate.guardarErrorFinalitzacio(tascaId, errorFinalitzacio);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Map<Long, Object> obtenirEstatsPerIds(List<String> tasquesSegonPlaIds) {
		return delegate.obtenirEstatsPerIds(tasquesSegonPlaIds);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean isEnSegonPla(String tascaSegonPlaId) {
		return delegate.isEnSegonPla(tascaSegonPlaId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<String[]> getMissatgesExecucioSegonPla(String tascaSegonPlaId) {
		return delegate.getMissatgesExecucioSegonPla(tascaSegonPlaId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void updateVariable(Long expedientId, String taskId, String codiVariable, Object valor){
		delegate.updateVariable(expedientId, taskId, codiVariable, valor);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void completarMassiu(String tascaId, String outcome) throws NoTrobatException, ValidacioException {
		delegate.completarMassiu(tascaId, outcome);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TascaDto findTascaById(Long id) {
		return delegate.findTascaById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void guardarFormulariExtern(String formulariId, Map<String, Object> valorsTasca) {
		delegate.guardarFormulariExtern(formulariId, valorsTasca);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Map<String, Object> obtenirValorsFormulariExternInicial(String formulariId) {
		return delegate.obtenirValorsFormulariExternInicial(formulariId);
	}

}
