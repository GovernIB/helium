/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
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
import net.conselldemallorca.helium.v3.core.api.exception.EntornNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EnumeracioNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EstatNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientTipusNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TaskInstanceNotFoundException;


/**
 * Servei per a enllaçar les llibreries jBPM 3 amb la funcionalitat
 * de Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientService {

	public enum FiltreAnulat {
		ACTIUS		("expedient.consulta.anulats.actius"),
		ANUL_LATS	("expedient.consulta.anulats.anulats"),
		TOTS		("expedient.consulta.anulats.tots");
				
		private final String codi;
		private final String id;
		
		FiltreAnulat(String codi){
			this.codi = codi;
			this.id = this.name();
		}
		
		public String getCodi(){
			return this.codi;
		}

		public String getId() {
			return id;
		}
	}
	
	public void modificar(
			Long id,
			String numero,
			String titol,
			String responsableCodi,
			Date dataInici,
			String comentari,
			String estatCodi,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			String grupCodi,
			boolean execucioDinsHandler);

	public void aturar(
			Long expedientId,
			String motiu) throws ExpedientNotFoundException;
	public void reprendre(Long expedientId) throws ExpedientNotFoundException;

	public ExpedientDto findById(Long expedientId) throws ExpedientNotFoundException;

	public ExpedientDto findAmbEntornITipusINumero(
			Long entornId,
			String expedientTipusCodi,
			String numero) throws EntornNotFoundException, ExpedientTipusNotFoundException;

	public ExpedientDto findAmbProcessInstanceId(
			String processInstanceId);

	public PaginaDto<ExpedientDto> findPerConsultaGeneralPaginat(
			Long entornId,
			Long expedientTipusId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Date dataFi1,
			Date dataFi2,
			EstatTipusDto estatTipus,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			boolean nomesAmbTasquesActives,
			boolean nomesAlertes,
			boolean mostrarAnulats,
			PaginacioParamsDto paginacioParams) throws EntornNotFoundException, ExpedientTipusNotFoundException, EstatNotFoundException;
	
	public void createRelacioExpedient(
			Long expedientOrigenId,
			Long expedientDestiId);
	
	public void processInstanceTokenRedirect(
			long tokenId,
			String nodeName,
			boolean cancelarTasques);

	public void alertaCrear(
			Long entornId,
			Long expedientId,
			Date data,
			String usuariCodi,
			String text) throws EntornNotFoundException, ExpedientNotFoundException;

	public void alertaEsborrarAmbTaskInstanceId(
			long taskInstanceId) throws TaskInstanceNotFoundException;

	public List<ExpedientDadaDto> findDadesPerExpedient(Long expedientId);

	public ExpedientDadaDto getDadaPerProcessInstance(
			String processInstanceId,
			String variableCodi);

	public List<ExpedientDocumentDto> findDocumentsPerExpedient(Long expedientId);

	public ArxiuDto getArxiuExpedient(
			Long expedientId,
			Long documentStoreId);
	
	public List<CampAgrupacioDto> findAgrupacionsDadesExpedient(Long expedientId);

	public List<ExpedientTascaDto> findTasquesPerExpedient(Long expedientId);

	public List<ExpedientTascaDto> findTasquesPendentsPerExpedient(Long expedientId);

	public ExpedientTascaDto getTascaPerExpedient(
			Long expedientId,
			String tascaId);

	public List<PersonaDto> findParticipantsPerExpedient(Long expedientId);

	public List<EnumeracioValorDto> enumeracioConsultar(
			String processInstanceId,
			String enumeracioCodi) throws EnumeracioNotFoundException;

	public ExpedientDto getExpedientIniciant();

	public void editar(
			Long entornId,
			Long id,
			String numero,
			String titol,
			String responsableCodi,
			Date dataInici,
			String comentari,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			String grupCodi);
	
	public void editar(
			Long entornId,
			Long id,
			String numero,
			String titol,
			String responsableCodi,
			Date dataInici,
			String comentari,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			String grupCodi,
			boolean executatEnHandler);

	public List<InstanciaProcesDto> getArbreInstanciesProces(
			Long processInstanceId);

	public InstanciaProcesDto getInstanciaProcesById(
			String processInstanceId,
			boolean ambImatgeProces,
			boolean ambVariables,
			boolean ambDocuments);

	public List<RegistreDto> getRegistrePerExpedient(Long expedientId);

	public List<ExpedientLogDto> getLogsPerTascaOrdenatsPerData(ExpedientDto expedient);

	public List<ExpedientLogDto> getLogsOrdenatsPerData(ExpedientDto expedient);

	public Map<String, ExpedientTascaDto> getTasquesPerLogExpedient(Long expedientId);

	public List<ExpedientLogDto> findLogsTascaOrdenatsPerData(Long targetId);

	public void retrocedirFinsLog(Long logId, boolean b);

	public List<ExpedientLogDto> findLogsRetroceditsOrdenatsPerData(Long logId);

	public void deleteConsulta(Long expedientId);

	public void delete(Long id, Long expedientId);

	public List<ExpedientDto> findAmbEntornLikeIdentificador(Long id, String text);

	public void deleteRelacioExpedient(Long expedientIdOrigen, Long expedientIdDesti);
	
	public ExpedientTascaDto getStartTask(Long entornId, Long expedientTipusId, Long definicioProcesId, Map<String, Object> valors);

	public ExpedientDto iniciar(Long entornId, String usuari, Long expedientTipusId, Long definicioProcesId, Integer any, String numero, String titol, String registreNumero, Date registreData, Long unitatAdministrativa, String idioma, boolean autenticat, String tramitadorNif, String tramitadorNom, String interessatNif, String interessatNom, String representantNif, String representantNom, boolean avisosHabilitats, String avisosEmail, String avisosMobil, boolean notificacioTelematicaHabilitada, Map<String, Object> variables, String transitionName, IniciadorTipusDto iniciadorTipus, String iniciadorCodi, String responsableCodi, Map<String, DadesDocumentDto> documents, List<DadesDocumentDto> adjunts);

	public String getNumeroExpedientActual(Long id, ExpedientTipusDto expedientTipus, Integer any);

	public boolean existsExpedientAmbEntornTipusITitol(Long entornId, Long expedientTipusId, String titol);

	public void anular(Long id, Long expedientId, String motiu);

	public void cancelarTasca(Long entornId, String taskId);

	public void suspendreTasca(Long entornId, String taskId);

	public void reprendreTasca(Long entornId, String taskId);

	public void reassignarTasca(Long id, String taskId, String expression);

	public List<Object> findLogIdTasquesById(List<ExpedientTascaDto> tasques);

	public List<ExpedientConsultaDissenyDto> findAmbEntornConsultaDisseny(Long entornId, Long consultaId, Map<String, Object> valors, String sort, boolean asc, int firstRow, int maxResults);

	public List<ExpedientConsultaDissenyDto> findAmbEntornConsultaDisseny(Long entornId, Long consultaId, Map<String, Object> valors, String sort, boolean asc);
	
	public List<CampDto> findConsultaFiltre(Long consultaId);

	public List<CampDto> findConsultaInforme(Long consultaId);

	public List<ExpedientDto> findPerConsultaInformePaginat(Long id, Long consultaId, Long expedientTipusId, Map<String, Object> valorsPerService, String expedientCampId, Boolean nomesPendents, Boolean nomesAlertes, Boolean mostrarAnulats, PaginacioParamsDto paginacioDtoFromDatatable);

	public List<ExpedientDadaDto> findDadesPerProcessInstance(String processInstanceId);

	public List<ExpedientDocumentDto> findDocumentsPerExpedientTasca(Long expedientId, String tascaId);

	public List<ExpedientDto> getExpedientsRelacionats(Long expedientId);
}
