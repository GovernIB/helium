/**
 * 
 */
package es.caib.helium.ejb;

import es.caib.helium.client.model.ParellaCodiValor;
import es.caib.helium.logic.intf.dto.AreaDto;
import es.caib.helium.logic.intf.dto.CampDto;
import es.caib.helium.logic.intf.dto.ConsultaCampDto;
import es.caib.helium.logic.intf.dto.ConsultaDto;
import es.caib.helium.logic.intf.dto.DefinicioProcesDto;
import es.caib.helium.logic.intf.dto.DefinicioProcesExpedientDto;
import es.caib.helium.logic.intf.dto.DefinicioProcesVersioDto;
import es.caib.helium.logic.intf.dto.DocumentDto;
import es.caib.helium.logic.intf.dto.DominiDto;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exportacio.DefinicioProcesExportacio;
import es.caib.helium.logic.intf.extern.domini.FilaResultat;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Servei que proporciona la funcionalitat de disseny d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class DissenyService extends AbstractService<es.caib.helium.logic.intf.service.DissenyService> implements es.caib.helium.logic.intf.service.DissenyService {

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<String> findAccionsJbpmOrdenades(Long definicioProcesId) {
		return getDelegateService().findAccionsJbpmOrdenades(definicioProcesId);
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
		return getDelegateService().findExpedientTipusAmbPermisReadUsuariActual(entornId);
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
		return getDelegateService().findExpedientTipusAmbPermisDissenyUsuariActual(entornId);
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
		return getDelegateService().findExpedientTipusAmbPermisGestioUsuariActual(entornId);
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
		return getDelegateService().findExpedientTipusAmbPermisCrearUsuariActual(entornId);
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
		return getDelegateService().getDeploymentResource(id, recursForm);
	}

	public ExpedientTipusDto getExpedientTipusById(Long id) {
		return getDelegateService().getExpedientTipusById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto getById(Long id) {
		return getDelegateService().getById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto findDarreraDefinicioProcesForExpedientTipus(Long expedientTipusId) {
		return getDelegateService().findDarreraDefinicioProcesForExpedientTipus(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findExpedientTipusAmbEntorn(EntornDto entornId) {
		return getDelegateService().findExpedientTipusAmbEntorn(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ConsultaDto> findConsultesActivesAmbEntornIExpedientTipusOrdenat(Long entornId, Long expedientTipusId) {
		return getDelegateService().findConsultesActivesAmbEntornIExpedientTipusOrdenat(entornId, expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ConsultaDto findConsulteById(Long id) {
		return getDelegateService().findConsulteById(id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampDto> findCampsOrdenatsPerCodi(Long expedientTipusId, Long definicioProcesId, boolean herencia) {
		return getDelegateService().findCampsOrdenatsPerCodi(expedientTipusId, definicioProcesId, herencia);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesExpedientDto getDefinicioProcesByEntorIdAndProcesId(Long entornId, Long procesId) {
		return getDelegateService().getDefinicioProcesByEntorIdAndProcesId(entornId, procesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto findDarreraVersioForExpedientTipusIDefProcCodi(Long expedientTipusId, String defProcCodi)
			throws NoTrobatException {
		return getDelegateService().findDarreraVersioForExpedientTipusIDefProcCodi(expedientTipusId, defProcCodi);
	}


	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesExpedientDto getDefinicioProcesByTipusExpedientById(Long expedientTipusId) {
		return getDelegateService().getDefinicioProcesByTipusExpedientById(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DefinicioProcesExpedientDto> getSubprocessosByProces(Long expedientTipusId, String jbpmId) {
		return getDelegateService().getSubprocessosByProces(expedientTipusId, jbpmId);
	}

	@Override
	public List<String> findDistinctJbpmGroupsCodis() {
		return getDelegateService().findDistinctJbpmGroupsCodis();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AreaDto findAreaById(Long areaId) {
		return getDelegateService().findAreaById(areaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesVersioDto getByVersionsInstanciaProcesById(String processInstanceId) {
		return getDelegateService().getByVersionsInstanciaProcesById(processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ParellaCodiValor> findTasquesAmbEntornIExpedientTipusPerSeleccio(
			Long entornId,
			Long expedientTipusId) {
		return getDelegateService().findTasquesAmbEntornIExpedientTipusPerSeleccio(
				entornId,
				expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<FilaResultat> consultaDominiIntern(
			String id,
			List<ParellaCodiValor> parametres) throws Exception {
		return getDelegateService().consultaDominiIntern(id, parametres);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentDto documentFindOne(
			Long documentId) throws NoTrobatException {
		return getDelegateService().documentFindOne(documentId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DocumentDto> documentFindAmbDefinicioProces(
			Long definicioProcesId) throws NoTrobatException {
		return getDelegateService().documentFindAmbDefinicioProces(definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Set<String> getRecursosNom(Long definicioProcesId) {
		return getDelegateService().getRecursosNom(definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public byte[] getRecursContingut(Long definicioProcesId, String nom) {
		return getDelegateService().getRecursContingut(definicioProcesId, nom);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public byte[] getParContingut(Long definicioProcesId) {
		return getDelegateService().getParContingut(definicioProcesId);
	}


	public PaginaDto<DefinicioProcesDto> findDefinicionsProcesNoUtilitzadesExpedientTipus(
			Long entornId,
			Long expedientTipusId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		return getDelegateService().findDefinicionsProcesNoUtilitzadesExpedientTipus(
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
		return getDelegateService().findIdsDefinicionsProcesNoUtilitzadesExpedientTipus(
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
		return getDelegateService().findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
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
		return getDelegateService().findIdsExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
				entornId, 
				expedientTipusId, 
				jbpmId);
	}
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DominiDto dominiFindAmbCodi(
			Long entornId, 
			String codiDomini) {
		return getDelegateService().dominiFindAmbCodi(entornId, codiDomini);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto updateHandlers(Long entornId, Long expedientTipusId, String nomArxiu, byte[] contingut) {
		return getDelegateService().updateHandlers(entornId, expedientTipusId, nomArxiu, contingut);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void propagarHandlers(
			Long idDefinicioProcesOrigen, 
			List<Long> idsDefinicioProcesDesti) {
		getDelegateService().propagarHandlers(idDefinicioProcesOrigen, idsDefinicioProcesDesti);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesExportacio getDefinicioProcesExportacioFromContingut(String fitxer, byte[] contingut) {
		return getDelegateService().getDefinicioProcesExportacioFromContingut(fitxer, contingut);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DocumentDto> findDocumentsAmbDefinicioProcesOrdenatsPerCodi(Long definicioProcesId)
			throws NoTrobatException {
		return getDelegateService().findDocumentsAmbDefinicioProcesOrdenatsPerCodi(definicioProcesId);
	}

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public String getPlantillaReport(Long consultaId) {
        return getDelegateService().getPlantillaReport(consultaId);
    }

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DocumentDto> findDocumentsOrdenatsPerCodi(Long expedientTipusId, Long definicioProcesId, boolean herencia) {
		return getDelegateService().findDocumentsOrdenatsPerCodi(expedientTipusId, definicioProcesId, herencia);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ConsultaDto getConsultaById(Long id) {
		return getDelegateService().getConsultaById(id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ConsultaCampDto> findCampsInformePerCampsConsulta(
			ConsultaDto consulta,
			boolean filtrarValorsPredefinits){
		return getDelegateService().findCampsInformePerCampsConsulta(consulta, filtrarValorsPredefinits);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<FilaResultat> consultaDomini(Long id, String codiDomini, Map<String, Object> parametres) {
		return getDelegateService().consultaDomini(id, codiDomini, parametres);
	}
}
