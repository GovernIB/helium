package es.caib.helium.ejb;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.domini.FilaResultat;
import es.caib.helium.commons.domini.ParellaCodiValor;
import es.caib.helium.commons.dto.AreaDto;
import es.caib.helium.commons.dto.CampDto;
import es.caib.helium.commons.dto.ConsultaCampDto;
import es.caib.helium.commons.dto.ConsultaDto;
import es.caib.helium.commons.dto.DefinicioProcesDto;
import es.caib.helium.commons.dto.DefinicioProcesExpedientDto;
import es.caib.helium.commons.dto.DefinicioProcesVersioDto;
import es.caib.helium.commons.dto.DocumentDto;
import es.caib.helium.commons.dto.DominiDto;
import es.caib.helium.commons.dto.EntornDto;
import es.caib.helium.commons.dto.ExpedientDocumentPinbalDto;
import es.caib.helium.commons.dto.ExpedientDto;
import es.caib.helium.commons.dto.ExpedientTipusDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.ParellaCodiValorDto;
import es.caib.helium.commons.dto.handlers.HandlerDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.exportacio.DefinicioProcesExportacio;
import es.caib.helium.logic.intf.service.DissenyService;

/**
 * Servei que proporciona la funcionalitat de disseny d'expedients.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class DissenyServiceBean extends AbstractServiceEjb<DissenyService> implements DissenyService {

	@Delegate
	DissenyService delegateService;

	protected void setDelegateService(DissenyService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<String> findAccionsJbpmOrdenades(Long definicioProcesId) {
		return delegateService.findAccionsJbpmOrdenades(definicioProcesId);
	}

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public List<String> findHandlersJbpmOrdenats(Long definicioProcesId) {
        return delegateService.findHandlersJbpmOrdenats(definicioProcesId);
    }

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public List<ParellaCodiValorDto> findHandlerParams(Long definicioProcesId, String handler) {
        return delegateService.findHandlerParams(definicioProcesId, handler);
    }

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<String> findHandlersRecursos(Long expedientTipusId) {
		return delegateService.findHandlersRecursos(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ParellaCodiValorDto> findHandlerRecursParams(Long expedientTipusId, String nomClasse) {
		return delegateService.findHandlerRecursParams(expedientTipusId, nomClasse);
	}

    /**
	 * Retorna els tipus d'expedient per als quals l'usuari actual te permisos de lectura.
	 *
	 * @param entornId
	 * @return
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisReadUsuariActual(Long entornId) {
		return delegateService.findExpedientTipusAmbPermisReadUsuariActual(entornId);
	}

	/**
	 * Retorna els tipus d'expedient per als quals l'usuari actual te permisos de disseny.
	 *
	 * @param entornId
	 * @return
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisDissenyUsuariActual(Long entornId) {
		return delegateService.findExpedientTipusAmbPermisDissenyUsuariActual(entornId);
	}

	/**
	 * Retorna els tipus d'expedient per als quals l'usuari actual te permisos de gestió.
	 *
	 * @param entornId
	 * @return
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisGestioUsuariActual(Long entornId) {
		return delegateService.findExpedientTipusAmbPermisGestioUsuariActual(entornId);
	}

	/**
	 * Retorna els tipus d'expedient per als quals l'usuari actual te permisos de creació.
	 *
	 * @param entornId
	 * @return
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisCrearUsuariActual(Long entornId) {
		return delegateService.findExpedientTipusAmbPermisCrearUsuariActual(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto findExpedientTipusAmbPermisReadUsuariActual(
			Long entornId,
			Long expedientTipusId) {
		return findExpedientTipusAmbPermisReadUsuariActual(
				entornId,
				expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public byte[] getDeploymentResource(Long id, String recursForm) {
		return delegateService.getDeploymentResource(id, recursForm);
	}

	public ExpedientTipusDto getExpedientTipusById(Long id) {
		return delegateService.getExpedientTipusById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto getById(Long id) {
		return delegateService.getById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto findDarreraDefinicioProcesForExpedientTipus(Long expedientTipusId) {
		return delegateService.findDarreraDefinicioProcesForExpedientTipus(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findExpedientTipusAmbEntorn(EntornDto entornId) {
		return delegateService.findExpedientTipusAmbEntorn(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ConsultaDto> findConsultesActivesAmbEntornIExpedientTipusOrdenat(Long entornId, Long expedientTipusId) {
		return delegateService.findConsultesActivesAmbEntornIExpedientTipusOrdenat(entornId, expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ConsultaDto findConsulteById(Long id) {
		return delegateService.findConsulteById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampDto> findCampsOrdenatsPerCodi(Long expedientTipusId, Long definicioProcesId, boolean herencia) {
		return delegateService.findCampsOrdenatsPerCodi(expedientTipusId, definicioProcesId, herencia);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesExpedientDto getDefinicioProcesByEntorIdAndProcesId(Long entornId, Long procesId) {
		return delegateService.getDefinicioProcesByEntorIdAndProcesId(entornId, procesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto findDarreraVersioForExpedientTipusIDefProcCodi(Long expedientTipusId, String defProcCodi)
			throws NoTrobatException {
		return delegateService.findDarreraVersioForExpedientTipusIDefProcCodi(expedientTipusId, defProcCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesExpedientDto getDefinicioProcesByTipusExpedientById(Long expedientTipusId) {
		return delegateService.getDefinicioProcesByTipusExpedientById(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DefinicioProcesExpedientDto> getSubprocessosByProces(Long expedientTipusId, String jbpmId) {
		return delegateService.getSubprocessosByProces(expedientTipusId, jbpmId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AreaDto findAreaById(Long areaId) {
		return delegateService.findAreaById(areaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesVersioDto getByVersionsInstanciaProcesById(String processInstanceId) {
		return delegateService.getByVersionsInstanciaProcesById(processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ParellaCodiValorDto> findTasquesAmbEntornIExpedientTipusPerSeleccio(
			Long entornId,
			Long expedientTipusId) {
		return delegateService.findTasquesAmbEntornIExpedientTipusPerSeleccio(
				entornId,
				expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<FilaResultat> consultaDominiIntern(
			String id,
			List<ParellaCodiValor> parametres) throws Exception {
		return delegateService.consultaDominiIntern(id, parametres);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentDto documentFindOne(
			Long documentId) throws NoTrobatException {
		return delegateService.documentFindOne(documentId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DocumentDto> documentFindAmbDefinicioProces(
			Long definicioProcesId) throws NoTrobatException {
		return delegateService.documentFindAmbDefinicioProces(definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Set<String> getRecursosNom(Long definicioProcesId) {
		return delegateService.getRecursosNom(definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public byte[] getRecursContingut(Long definicioProcesId, String nom) {
		return delegateService.getRecursContingut(definicioProcesId, nom);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public byte[] getParContingut(Long definicioProcesId) {
		return delegateService.getParContingut(definicioProcesId);
	}

	public PaginaDto<DefinicioProcesDto> findDefinicionsProcesNoUtilitzadesExpedientTipus(
			Long entornId,
			Long expedientTipusId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findDefinicionsProcesNoUtilitzadesExpedientTipus(
				entornId,
				expedientTipusId,
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Long> findIdsDefinicionsProcesNoUtilitzadesExpedientTipus(
			Long entornId,
			Long expedientTipusId) {
		return delegateService.findIdsDefinicionsProcesNoUtilitzadesExpedientTipus(
				entornId,
				expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ExpedientDto> findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
			Long entornId,
			Long expedientTipusId,
			Long jbpmId,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
				entornId,
				expedientTipusId,
				jbpmId,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Long> findIdsExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
			Long entornId,
			Long expedientTipusId,
			Long jbpmId) {
		return delegateService.findIdsExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
				entornId,
				expedientTipusId,
				jbpmId);
	}
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DominiDto dominiFindAmbCodi(
			Long entornId,
			String codiDomini) {
		return delegateService.dominiFindAmbCodi(entornId, codiDomini);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto updateHandlers(Long entornId, Long expedientTipusId, String nomArxiu, byte[] contingut) {
		return delegateService.updateHandlers(entornId, expedientTipusId, nomArxiu, contingut);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void propagarHandlers(
			Long idDefinicioProcesOrigen,
			List<Long> idsDefinicioProcesDesti) {
		delegateService.propagarHandlers(idDefinicioProcesOrigen, idsDefinicioProcesDesti);
	}

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public List<String> updateHandlersAccions(Long expedientTipusId, String nomArxiu, byte[] contingut) {
        return delegateService.updateHandlersAccions(expedientTipusId, nomArxiu, contingut);
    }

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesExportacio getDefinicioProcesExportacioFromContingut(String fitxer, byte[] contingut) {
		return delegateService.getDefinicioProcesExportacioFromContingut(fitxer, contingut);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DocumentDto> findDocumentsAmbDefinicioProcesOrdenatsPerCodi(Long definicioProcesId)
			throws NoTrobatException {
		return delegateService.findDocumentsAmbDefinicioProcesOrdenatsPerCodi(definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DocumentDto> findDocumentsOrdenatsPerCodi(Long expedientTipusId, Long definicioProcesId, boolean herencia) {
		return delegateService.findDocumentsOrdenatsPerCodi(expedientTipusId, definicioProcesId, herencia);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ConsultaDto getConsultaById(Long id) {
		return delegateService.getConsultaById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ConsultaCampDto> findCampsInformePerCampsConsulta(
			ConsultaDto consulta,
			boolean filtrarValorsPredefinits){
		return delegateService.findCampsInformePerCampsConsulta(consulta, filtrarValorsPredefinits);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<FilaResultat> consultaDomini(Long id, String codiDomini, Map<String, Object> parametres) {
		return delegateService.consultaDomini(id, codiDomini, parametres);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<HandlerDto> getHandlersPredefinits() {
		return delegateService.getHandlersPredefinits();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DefinicioProcesDto> findByEntornAndExpedientTipusOpcional(Long entornId, Long expedientTipusId) {
		return delegateService.findByEntornAndExpedientTipusOpcional(entornId, expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientDocumentPinbalDto findDocumentPinbalByExpedient(Long expedientId, Long documentId) {
		return delegateService.findDocumentPinbalByExpedient(expedientId, documentId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findExpedientTipusBySistraTramitCodi(String sistraTramitCodi) {
		return delegateService.findExpedientTipusBySistraTramitCodi(sistraTramitCodi);
	}

}
