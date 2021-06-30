/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.emiserv.logic.intf.exception.NoTrobatException;
import es.caib.emiserv.logic.intf.exportacio.DefinicioProcesExportacio;
import es.caib.emiserv.logic.intf.extern.domini.FilaResultat;
import es.caib.emiserv.logic.intf.extern.domini.ParellaCodiValor;
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
import es.caib.helium.logic.intf.dto.ParellaCodiValorDto;
import es.caib.helium.logic.intf.service.DissenyService;


/**
 * Servei que proporciona la funcionalitat de disseny d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class DissenyServiceBean implements DissenyService {

	@Autowired
	DissenyService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<String> findAccionsJbpmOrdenades(Long definicioProcesId) {
		return delegate.findAccionsJbpmOrdenades(definicioProcesId);
	}

	/**
	 * Retorna els tipus d'expedient per als quals l'usuari actual te permisos de lectura.
	 * 
	 * @param entornId
	 * @return
	 * @throws EntornNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisReadUsuariActual(Long entornId) {
		return delegate.findExpedientTipusAmbPermisReadUsuariActual(entornId);
	}

	/**
	 * Retorna els tipus d'expedient per als quals l'usuari actual te permisos de disseny.
	 * 
	 * @param entornId
	 * @return
	 * @throws EntornNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisDissenyUsuariActual(Long entornId) {
		return delegate.findExpedientTipusAmbPermisDissenyUsuariActual(entornId);
	}

	/**
	 * Retorna els tipus d'expedient per als quals l'usuari actual te permisos de gestió.
	 * 
	 * @param entornId
	 * @return
	 * @throws EntornNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisGestioUsuariActual(Long entornId) {
		return delegate.findExpedientTipusAmbPermisGestioUsuariActual(entornId);
	}

	/**
	 * Retorna els tipus d'expedient per als quals l'usuari actual te permisos de creació.
	 * 
	 * @param entornId
	 * @return
	 * @throws EntornNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisCrearUsuariActual(Long entornId) {
		return delegate.findExpedientTipusAmbPermisCrearUsuariActual(entornId);
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
		return delegate.getDeploymentResource(id, recursForm);
	}

	public ExpedientTipusDto getExpedientTipusById(Long id) {
		return delegate.getExpedientTipusById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto getById(Long id) {
		return delegate.getById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto findDarreraDefinicioProcesForExpedientTipus(Long expedientTipusId) {
		return delegate.findDarreraDefinicioProcesForExpedientTipus(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findExpedientTipusAmbEntorn(EntornDto entornId) {
		return delegate.findExpedientTipusAmbEntorn(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ConsultaDto> findConsultesActivesAmbEntornIExpedientTipusOrdenat(Long entornId, Long expedientTipusId) {
		return delegate.findConsultesActivesAmbEntornIExpedientTipusOrdenat(entornId, expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ConsultaDto findConsulteById(Long id) {
		return delegate.findConsulteById(id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampDto> findCampsOrdenatsPerCodi(Long expedientTipusId, Long definicioProcesId, boolean herencia) {
		return delegate.findCampsOrdenatsPerCodi(expedientTipusId, definicioProcesId, herencia);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesExpedientDto getDefinicioProcesByEntorIdAndProcesId(Long entornId, Long procesId) {
		return delegate.getDefinicioProcesByEntorIdAndProcesId(entornId, procesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesExpedientDto getDefinicioProcesByTipusExpedientById(Long expedientTipusId) {
		return delegate.getDefinicioProcesByTipusExpedientById(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DefinicioProcesExpedientDto> getSubprocessosByProces(Long expedientTipusId, String jbpmId) {
		return delegate.getSubprocessosByProces(expedientTipusId, jbpmId);
	}

	@Override
	public List<String> findDistinctJbpmGroupsCodis() {
		return delegate.findDistinctJbpmGroupsCodis();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AreaDto findAreaById(Long areaId) {
		return delegate.findAreaById(areaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesVersioDto getByVersionsInstanciaProcesById(String processInstanceId) {
		return delegate.getByVersionsInstanciaProcesById(processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ParellaCodiValorDto> findTasquesAmbEntornIExpedientTipusPerSeleccio(
			Long entornId,
			Long expedientTipusId) {
		return delegate.findTasquesAmbEntornIExpedientTipusPerSeleccio(
				entornId,
				expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<FilaResultat> consultaDominiIntern(
			String id,
			List<ParellaCodiValor> parametres) throws Exception {
		return delegate.consultaDominiIntern(id, parametres);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentDto documentFindOne(
			Long documentId) throws NoTrobatException {
		return delegate.documentFindOne(documentId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DocumentDto> documentFindAmbDefinicioProces(
			Long definicioProcesId) throws NoTrobatException {
		return delegate.documentFindAmbDefinicioProces(definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Set<String> getRecursosNom(Long definicioProcesId) {
		return delegate.getRecursosNom(definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public byte[] getRecursContingut(Long definicioProcesId, String nom) {
		return delegate.getRecursContingut(definicioProcesId, nom);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public byte[] getParContingut(Long definicioProcesId) {
		return delegate.getParContingut(definicioProcesId);
	}


	public PaginaDto<DefinicioProcesDto> findDefinicionsProcesNoUtilitzadesExpedientTipus(
			Long entornId,
			Long expedientTipusId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findDefinicionsProcesNoUtilitzadesExpedientTipus(
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
		return delegate.findIdsDefinicionsProcesNoUtilitzadesExpedientTipus(
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
		return delegate.findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
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
		return delegate.findIdsExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
				entornId, 
				expedientTipusId, 
				jbpmId);
	}
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DominiDto dominiFindAmbCodi(
			Long entornId, 
			String codiDomini) {
		return delegate.dominiFindAmbCodi(entornId, codiDomini);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto updateHandlers(Long entornId, Long expedientTipusId, String nomArxiu, byte[] contingut) {
		return delegate.updateHandlers(entornId, expedientTipusId, nomArxiu, contingut);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void propagarHandlers(
			Long idDefinicioProcesOrigen, 
			List<Long> idsDefinicioProcesDesti) {
		delegate.propagarHandlers(idDefinicioProcesOrigen, idsDefinicioProcesDesti);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesExportacio getDefinicioProcesExportacioFromContingut(String fitxer, byte[] contingut) {
		return delegate.getDefinicioProcesExportacioFromContingut(fitxer, contingut);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DocumentDto> findDocumentsAmbDefinicioProcesOrdenatsPerCodi(Long definicioProcesId)
			throws NoTrobatException {
		return delegate.findDocumentsAmbDefinicioProcesOrdenatsPerCodi(definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DocumentDto> findDocumentsOrdenatsPerCodi(Long expedientTipusId, Long definicioProcesId, boolean herencia) {
		return delegate.findDocumentsOrdenatsPerCodi(expedientTipusId, definicioProcesId, herencia);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ConsultaDto getConsultaById(Long id) {
		return delegate.getConsultaById(id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ConsultaCampDto> findCampsInformePerCampsConsulta(
			ConsultaDto consulta,
			boolean filtrarValorsPredefinits){
		return delegate.findCampsInformePerCampsConsulta(consulta, filtrarValorsPredefinits);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<FilaResultat> consultaDomini(Long id, String codiDomini, Map<String, Object> parametres) {
		return delegate.consultaDomini(id, codiDomini, parametres);
	}
}
