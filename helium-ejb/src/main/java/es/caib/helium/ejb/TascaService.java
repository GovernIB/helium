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
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.ValidacioException;

/**
 * Servei per a enllaçar les llibreries jBPM 3 amb la funcionalitat de Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class TascaService extends AbstractService<es.caib.helium.logic.intf.service.TascaService> implements es.caib.helium.logic.intf.service.TascaService {

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto findAmbIdPerExpedient(
			String id,
			Long expedientId) {
		return getDelegateService().findAmbIdPerExpedient(id, expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto findAmbIdPerTramitacio(
			String id) {
		return getDelegateService().findAmbIdPerTramitacio(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<String> findIdsPerFiltre(
			Long entornId,
			Long expedientTipusId,
			String titol,
			String tasca,
			String responsable,
			List<String> grups,
			String expedient,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Date dataLimitInici,
			Date dataLimitFi,
			Integer prioritat,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesTasquesMeves) {
		return getDelegateService().findIdsPerFiltre(
				entornId,
				expedientTipusId,
				titol,
				tasca,
				responsable,
				grups,
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
		return getDelegateService().findPerFiltrePaginat(
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
		return getDelegateService().findDades(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDocumentDto> findDocuments(
			String id) throws Exception {
		return getDelegateService().findDocuments(id);
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
			Map<String, Object> valorsFormulari) throws Exception {
		return getDelegateService().findValorsPerCampDesplegable(
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
		return getDelegateService().agafar(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto alliberar(
			String id) {
		return getDelegateService().alliberar(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void guardar(
			String taskId,
			Map<String, Object> variables) throws Exception {
		getDelegateService().guardar(
				taskId,
				variables);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void validar(
			String tascaId,
			Map<String, Object> variables) throws Exception {
		getDelegateService().validar(
				tascaId,
				variables);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void restaurar(String tascaId) {
		getDelegateService().restaurar(tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void completar(
			String tascaId,
			String outcome) {
		getDelegateService().completar(
				tascaId,
				outcome);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void executarAccio(
			String id,
			String accio) {
		getDelegateService().executarAccio(
				id,
				accio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public FormulariExternDto formulariExternObrir(
			String tascaId) {
		return getDelegateService().formulariExternObrir(tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public FormulariExternDto formulariExternObrirTascaInicial(
			String tascaIniciId,
			Long expedientTipusId,
			Long definicioProcesId) {
		return getDelegateService().formulariExternObrirTascaInicial(
				tascaIniciId,
				expedientTipusId,
				definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDadaDto> findDadesPerTascaDto(Long expedientTipusId, ExpedientTascaDto tasca) {
		return getDelegateService().findDadesPerTascaDto(expedientTipusId, tasca);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTascaDto> findAmbIds(Set<String> ids) {
		return getDelegateService().findAmbIds(ids);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TascaDocumentDto findDocument(String tascaId, Long docId, Long expedientTipusId) throws Exception {
		return getDelegateService().findDocument(tascaId, docId, expedientTipusId);
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
			String user) throws Exception {
		return getDelegateService().guardarDocumentTasca(
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
	public void esborrarDocument(String taskInstanceId, String documentCodi, String user) throws Exception {
		getDelegateService().esborrarDocument(taskInstanceId, documentCodi, user);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean signarDocumentTascaAmbToken(String tascaId, String token, byte[] signatura) throws Exception {
		return getDelegateService().signarDocumentTascaAmbToken(tascaId, token, signatura);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDocumentDto> findDocumentsSignar(String id) throws Exception {
		return getDelegateService().findDocumentsSignar(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean hasFormulari(String tascaId) {
		return getDelegateService().hasFormulari(tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean hasDocuments(String tascaId) {
		return getDelegateService().hasDocuments(tascaId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean hasDocumentsNotReadOnly(String tascaId) {
		return getDelegateService().hasDocumentsNotReadOnly(tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean hasSignatures(String tascaId) {
		return getDelegateService().hasSignatures(tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto getArxiuPerDocumentCodi(
			String tascaId,
			String documentCodi) throws Exception {
		return getDelegateService().getArxiuPerDocumentCodi(
				tascaId,
				documentCodi);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentDto getDocumentPerDocumentCodi(
			String tascaId, 
			String documentCodi) throws Exception {
		return getDelegateService().getDocumentPerDocumentCodi(
				tascaId, 
				documentCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean isTascaValidada(String tascaId) {
		return getDelegateService().isTascaValidada(tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean isDocumentsComplet(String tascaId) {
		return getDelegateService().isDocumentsComplet(tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean isSignaturesComplet(String tascaId) {
		return getDelegateService().isSignaturesComplet(tascaId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void comprovarTasquesSegonPla() {
		getDelegateService().comprovarTasquesSegonPla();
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void carregaTasquesSegonPla() {
		getDelegateService().carregaTasquesSegonPla();
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void completaTascaSegonPla(String tascaId, Date iniciFinalitzacio) {
		getDelegateService().completaTascaSegonPla(tascaId, iniciFinalitzacio);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void guardarErrorFinalitzacio(String tascaId, String errorFinalitzacio) {
		getDelegateService().guardarErrorFinalitzacio(tascaId, errorFinalitzacio);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Map<String, Object> obtenirEstatsPerIds(List<String> tasquesSegonPlaIds) {
		return getDelegateService().obtenirEstatsPerIds(tasquesSegonPlaIds);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean isEnSegonPla(String tascaSegonPlaId) {
		return getDelegateService().isEnSegonPla(tascaSegonPlaId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<String[]> getMissatgesExecucioSegonPla(String tascaSegonPlaId) {
		return getDelegateService().getMissatgesExecucioSegonPla(tascaSegonPlaId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void updateVariable(Long expedientId, String taskId, String codiVariable, Object valor){
		getDelegateService().updateVariable(expedientId, taskId, codiVariable, valor);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void completarMassiu(String tascaId, String outcome) throws NoTrobatException, ValidacioException {
		getDelegateService().completarMassiu(tascaId, outcome);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TascaDto findTascaById(Long id) {
		return getDelegateService().findTascaById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void guardarFormulariExtern(String formulariId, Map<String, Object> valorsTasca) throws Exception {
		getDelegateService().guardarFormulariExtern(formulariId, valorsTasca);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Map<String, Object> obtenirValorsFormulariExternInicial(String formulariId) {
		return getDelegateService().obtenirValorsFormulariExternInicial(formulariId);
	}

}
