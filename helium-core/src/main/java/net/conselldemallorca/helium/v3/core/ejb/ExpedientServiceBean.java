/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientConsultaDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientLogDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.EntornNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EnumeracioNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EstatNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientTipusNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TaskInstanceNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

/**
 * Servei per a enllaçar les llibreries jBPM 3 amb la funcionalitat de Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ExpedientServiceBean implements ExpedientService {

	@Autowired
	ExpedientService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void modificar(Long id, String numero, String titol, String responsableCodi, Date dataInici, String comentari, String estatCodi, Double geoPosX, Double geoPosY, String geoReferencia, String grupCodi, boolean execucioDinsHandler) {
		delegate.modificar(id, numero, titol, responsableCodi, dataInici, comentari, estatCodi, geoPosX, geoPosY, geoReferencia, grupCodi, execucioDinsHandler);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void aturar(Long expedientId, String motiu) throws ExpedientNotFoundException {
		delegate.aturar(expedientId, motiu);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void reprendre(Long expedientId) throws ExpedientNotFoundException {
		delegate.reprendre(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientDto findById(Long expedientId) throws ExpedientNotFoundException {
		return delegate.findById(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientDto findAmbEntornITipusINumero(Long entornId, String expedientTipusCodi, String numero) throws EntornNotFoundException, ExpedientTipusNotFoundException {
		return delegate.findAmbEntornITipusINumero(entornId, expedientTipusCodi, numero);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientDto findAmbProcessInstanceId(String processInstanceId) {
		return delegate.findAmbProcessInstanceId(processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ExpedientDto> findPerConsultaGeneralPaginat(Long entornId, Long expedientTipusId, String titol, String numero, Date dataInici1, Date dataInici2, Date dataFi1, Date dataFi2, EstatTipusDto estatTipus, Long estatId, Double geoPosX, Double geoPosY, String geoReferencia, boolean nomesAmbTasquesActives, boolean nomesAlertes, boolean mostrarAnulats, PaginacioParamsDto paginacioParams) throws EntornNotFoundException, ExpedientTipusNotFoundException, EstatNotFoundException {
		return delegate.findPerConsultaGeneralPaginat(entornId, expedientTipusId, titol, numero, dataInici1, dataInici2, dataFi1, dataFi2, estatTipus, estatId, geoPosX, geoPosY, geoReferencia, nomesAmbTasquesActives, nomesAlertes, mostrarAnulats, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void createRelacioExpedient(Long expedientOrigenId, Long expedientDestiId) {
		delegate.createRelacioExpedient(expedientOrigenId, expedientDestiId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void processInstanceTokenRedirect(long tokenId, String nodeName, boolean cancelarTasques) {
		delegate.processInstanceTokenRedirect(tokenId, nodeName, cancelarTasques);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void alertaCrear(Long entornId, Long expedientId, Date data, String usuariCodi, String text) throws EntornNotFoundException, ExpedientNotFoundException {
		delegate.alertaCrear(entornId, expedientId, data, usuariCodi, text);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void alertaEsborrarAmbTaskInstanceId(long taskInstanceId) throws TaskInstanceNotFoundException {
		delegate.alertaEsborrarAmbTaskInstanceId(taskInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientDadaDto> findDadesPerExpedient(Long expedientId) {
		return delegate.findDadesPerExpedient(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientDadaDto getDadaPerProcessInstance(String processInstanceId, String variableCodi) {
		return delegate.getDadaPerProcessInstance(processInstanceId, variableCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientDocumentDto> findDocumentsPerExpedient(Long expedientId) {
		return delegate.findDocumentsPerExpedient(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto getArxiuExpedient(Long expedientId, Long documentStoreId) {
		return delegate.getArxiuExpedient(expedientId, documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampAgrupacioDto> findAgrupacionsDadesExpedient(Long expedientId) {
		return delegate.findAgrupacionsDadesExpedient(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTascaDto> findTasquesPerExpedient(Long expedientId) {
		return delegate.findTasquesPerExpedient(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto getTascaPerExpedient(Long expedientId, String tascaId) {
		return delegate.getTascaPerExpedient(expedientId, tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<PersonaDto> findParticipantsPerExpedient(Long expedientId) {
		return delegate.findParticipantsPerExpedient(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EnumeracioValorDto> enumeracioConsultar(String processInstanceId, String enumeracioCodi) throws EnumeracioNotFoundException {
		return delegate.enumeracioConsultar(processInstanceId, enumeracioCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientDto getExpedientIniciant() {
		return delegate.getExpedientIniciant();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void editar(Long entornId, Long id, String numero, String titol, String responsableCodi, Date dataInici, String comentari, Long estatId, Double geoPosX, Double geoPosY, String geoReferencia, String grupCodi) {
		delegate.editar(entornId, id, numero, titol, responsableCodi, dataInici, comentari, estatId, geoPosX, geoPosY, geoReferencia, grupCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void editar(Long entornId, Long id, String numero, String titol, String responsableCodi, Date dataInici, String comentari, Long estatId, Double geoPosX, Double geoPosY, String geoReferencia, String grupCodi, boolean executatEnHandler) {
		delegate.editar(entornId, id, numero, titol, responsableCodi, dataInici, comentari, estatId, geoPosX, geoPosY, geoReferencia, grupCodi, executatEnHandler);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<InstanciaProcesDto> getArbreInstanciesProces(Long processInstanceId) {
		return delegate.getArbreInstanciesProces(processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public InstanciaProcesDto getInstanciaProcesById(String processInstanceId) {
		return delegate.getInstanciaProcesById(processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<RegistreDto> getRegistrePerExpedient(Long expedientId) {
		return delegate.getRegistrePerExpedient(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientLogDto> getLogsPerTascaOrdenatsPerData(ExpedientDto expedient, String piId) {
		return delegate.getLogsPerTascaOrdenatsPerData(expedient, piId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientLogDto> getLogsOrdenatsPerData(ExpedientDto expedient, String piId) {
		return delegate.getLogsOrdenatsPerData(expedient, piId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Map<String, ExpedientTascaDto> getTasquesPerLogExpedient(Long expedientId) {
		return delegate.getTasquesPerLogExpedient(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientLogDto> findLogsTascaOrdenatsPerData(Long targetId) {
		return delegate.findLogsTascaOrdenatsPerData(targetId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void retrocedirFinsLog(Long logId, boolean b) {
		delegate.retrocedirFinsLog(logId, b);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientLogDto> findLogsRetroceditsOrdenatsPerData(Long logId) {
		return delegate.findLogsRetroceditsOrdenatsPerData(logId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void deleteConsulta(Long expedientId) {
		delegate.deleteConsulta(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long id, Long expedientId) {
		delegate.delete(id, expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientDto> findAmbEntornLikeIdentificador(Long id, String text) {
		return delegate.findAmbEntornLikeIdentificador(id, text);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void deleteRelacioExpedient(Long expedientIdOrigen, Long expedientIdDesti) {
		delegate.deleteRelacioExpedient(expedientIdOrigen, expedientIdDesti);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto getStartTask(Long entornId, Long expedientTipusId, Long definicioProcesId, Map<String, Object> valors) {
		return delegate.getStartTask(entornId, expedientTipusId, definicioProcesId, valors);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientDto iniciar(Long entornId, String usuari, Long expedientTipusId, Long definicioProcesId, Integer any, String numero, String titol, String registreNumero, Date registreData, Long unitatAdministrativa, String idioma, boolean autenticat, String tramitadorNif, String tramitadorNom, String interessatNif, String interessatNom, String representantNif, String representantNom, boolean avisosHabilitats, String avisosEmail, String avisosMobil, boolean notificacioTelematicaHabilitada, Map<String, Object> variables, String transitionName, IniciadorTipusDto iniciadorTipus, String iniciadorCodi, String responsableCodi, Map<String, DadesDocumentDto> documents, List<DadesDocumentDto> adjunts) {
		return delegate.iniciar(entornId, usuari, expedientTipusId, definicioProcesId, any, numero, titol, registreNumero, registreData, unitatAdministrativa, idioma, autenticat, tramitadorNif, tramitadorNom, interessatNif, interessatNom, representantNif, representantNom, avisosHabilitats, avisosEmail, avisosMobil, notificacioTelematicaHabilitada, variables, transitionName, iniciadorTipus, iniciadorCodi, responsableCodi, documents, adjunts);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String getNumeroExpedientActual(Long id, ExpedientTipusDto expedientTipus, Integer any) {
		return delegate.getNumeroExpedientActual(id, expedientTipus, any);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean existsExpedientAmbEntornTipusITitol(Long entornId, Long expedientTipusId, String titol) {
		return delegate.existsExpedientAmbEntornTipusITitol(entornId, expedientTipusId, titol);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void anular(Long id, Long expedientId, String motiu) {
		delegate.anular(id, expedientId, motiu);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void cancelarTasca(Long entornId, String taskId) {
		delegate.cancelarTasca(entornId, taskId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void suspendreTasca(Long entornId, String taskId) {
		delegate.suspendreTasca(entornId, taskId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void reprendreTasca(Long entornId, String taskId) {
		delegate.reprendreTasca(entornId, taskId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void reassignarTasca(Long id, String taskId, String expression) {
		delegate.reassignarTasca(id, taskId, expression);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Object> findLogIdTasquesById(List<ExpedientTascaDto> tasques) {
		return delegate.findLogIdTasquesById(tasques);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientConsultaDissenyDto> findAmbEntornConsultaDisseny(Long entornId, Long consultaId, Map<String, Object> valors, PaginacioParamsDto paginacioParams) {
		return delegate.findAmbEntornConsultaDisseny(entornId, consultaId, valors, paginacioParams);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDadaDto> findConsultaFiltre(Long consultaId) {
		return delegate.findConsultaFiltre(consultaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDadaDto> findConsultaInforme(Long consultaId) {
		return delegate.findConsultaInforme(consultaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ExpedientConsultaDissenyDto> findPerConsultaInformePaginat(Long id, Long consultaId, Long expedientTipusId, Map<String, Object> valorsPerService, String expedientCampId, Boolean nomesPendents, Boolean nomesAlertes, Boolean mostrarAnulats, PaginacioParamsDto paginacioParamsDto) {
		return delegate.findPerConsultaInformePaginat(id, consultaId, expedientTipusId, valorsPerService, expedientCampId, nomesPendents, nomesAlertes, mostrarAnulats, paginacioParamsDto);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTascaDto> findTasquesPendentsPerExpedient(Long expedientId) {
		return delegate.findTasquesPendentsPerExpedient(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientDadaDto> findDadesPerProcessInstance(String processInstanceId) {
		return delegate.findDadesPerProcessInstance(processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientDto> getExpedientsRelacionats(Long expedientId) {
		return delegate.getExpedientsRelacionats(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Long> findIdsPerConsultaGeneral(Long entornId,
			Long expedientTipusId, String titol, String numero,
			Date dataInici1, Date dataInici2, Date dataFi1, Date dataFi2,
			EstatTipusDto estatTipus, Long estatId, Double geoPosX,
			Double geoPosY, String geoReferencia,
			boolean nomesAmbTasquesActives, boolean nomesAlertes,
			boolean mostrarAnulats) throws EntornNotFoundException,
			ExpedientTipusNotFoundException, EstatNotFoundException {
		return delegate.findIdsPerConsultaGeneral(entornId, expedientTipusId, titol, numero, dataInici1, dataInici2, dataFi1, dataFi2, estatTipus, estatId, geoPosX, geoPosY, geoReferencia, nomesAmbTasquesActives, nomesAlertes, mostrarAnulats);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Long> findIdsPerConsultaInformePaginat(Long id,
			Long consultaId, Long expedientTipusId,
			Map<String, Object> valorsPerService, String expedientCampId,
			Boolean nomesPendents, Boolean nomesAlertes, Boolean mostrarAnulats) {
		return delegate.findIdsPerConsultaInformePaginat(id, consultaId, expedientTipusId, valorsPerService, expedientCampId, nomesPendents, nomesAlertes, mostrarAnulats);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void changeProcessInstanceVersion(String id, int versio) {
		delegate.changeProcessInstanceVersion(id, versio);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Object evaluateScript(String processInstanceId, String script, String outputVar) {
		return delegate.evaluateScript(processInstanceId, script, outputVar);
	}
}
