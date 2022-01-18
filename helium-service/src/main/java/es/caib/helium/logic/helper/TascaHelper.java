/**
 * 
 */
package es.caib.helium.logic.helper;

import es.caib.helium.client.engine.model.WTaskInstance;
import es.caib.helium.client.expedient.tasca.TascaClientService;
import es.caib.helium.client.expedient.tasca.model.ConsultaTascaDades;
import es.caib.helium.client.expedient.tasca.model.TascaDto;
import es.caib.helium.logic.helper.TascaSegonPlaHelper.InfoSegonPla;
import es.caib.helium.logic.intf.WorkflowEngineApi;
import es.caib.helium.logic.intf.dto.DelegationInfo;
import es.caib.helium.logic.intf.dto.ExpedientDadaDto;
import es.caib.helium.logic.intf.dto.ExpedientTascaDto;
import es.caib.helium.logic.intf.dto.PersonaDto;
import es.caib.helium.logic.intf.dto.PersonaDto.Sexe;
import es.caib.helium.logic.intf.dto.TascaDadaDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.TascaNoDisponibleException;
import es.caib.helium.logic.intf.keycloak.KeycloakHelper;
import es.caib.helium.logic.intf.util.Constants;
import es.caib.helium.logic.util.EntornActual;
import es.caib.helium.persist.entity.Camp;
import es.caib.helium.persist.entity.Camp.TipusCamp;
import es.caib.helium.persist.entity.CampTasca;
import es.caib.helium.persist.entity.DefinicioProces;
import es.caib.helium.persist.entity.DocumentTasca;
import es.caib.helium.persist.entity.Expedient;
import es.caib.helium.persist.entity.ExpedientTipus;
import es.caib.helium.persist.entity.FirmaTasca;
import es.caib.helium.persist.entity.Tasca;
import es.caib.helium.persist.repository.CampTascaRepository;
import es.caib.helium.persist.repository.DefinicioProcesRepository;
import es.caib.helium.persist.repository.TascaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Helper per a gestionar les tasques dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class TascaHelper {

	@Resource
	private TascaRepository tascaRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private VariableHelper variableHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private CampTascaRepository campTascaRepository;
	@Resource
	private WorkflowEngineApi workflowEngineApi;
	@Resource
	private TascaClientService tascaClientService;
	@Resource
	private ConversioTipusServiceHelper conversioTipusServiceHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private TascaSegonPlaHelper tascaSegonPlaHelper;
	@Resource
	private MessageServiceHelper messageServiceHelper;

	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	public WTaskInstance getTascaComprovacionsTramitacio(
			String id,
			boolean comprovarAssignacio,
			boolean comprovarPendent) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		WTaskInstance task = workflowEngineApi.getTaskById(id);
		if (task == null) {
			logger.debug("No s'ha trobat la tasca (" +
					"id=" + id + ")");
			throw new NoTrobatException(WTaskInstance.class, id);
		}
		if (comprovarAssignacio) {
			if (task.getActorId() == null || !task.getActorId().equals(auth.getName())) {
				logger.debug("La persona no té la tasca assignada (" +
						"id=" + id + ", " +
						"personaCodi=" + auth.getName() + ")");
				throw new TascaNoDisponibleException(id, messageServiceHelper.getMessage("error.tascaService.noAssignada"), null);
			}
		}
		if (comprovarPendent) {
			if (!task.isOpen() || task.isCancelled() || task.isSuspended()) {
				logger.debug("La tasca no està en estat pendent (" +
						"id=" + id + ")");
				throw new TascaNoDisponibleException(id, messageServiceHelper.getMessage("error.tascaService.noPendent"), null);
			}
		}
		return task;
	}

	// TODO: Revisar tema caché de tasques
	public DadesCacheTasca getDadesCacheTasca(
			WTaskInstance task,
			Expedient expedient) {
		DadesCacheTasca dadesCache = null;
//		if (!task.isCacheActiu()) {
//			setTascaCache(task, expedient);
//		}
//		try {
//			dadesCache = getDadesCacheTasca(task);
//		} catch (Exception e) {
//			task.setCacheInactiu();
//			setTascaCache(task, expedient);
//			dadesCache = getDadesCacheTasca(task);
//		}
		return dadesCache;
	}
	
	private void setTascaCache(
			WTaskInstance task,
			Expedient expedient) throws Exception {
		if (expedient == null)
			expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(
				task.getProcessDefinitionId());
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProces(
				task.getTaskName(),
				definicioProces);
		if (tasca == null) {
			throw new NoTrobatException(
					Tasca.class,
					"(taskName=" + task.getTaskName() + ", " +
					"definicioProcesId=" + definicioProces.getId() + ", " +
					"definicioProcesJbpmKey=" + definicioProces.getJbpmKey() + ", " +
					"definicioProcesVersio=" + definicioProces.getVersio() + ")");
							
		}

		// TODO: Revisar camps a afegir a la descripció del JPBM --> Info tasca
		String titol = tasca.getNom();
		if (tasca.getNomScript() != null && tasca.getNomScript().length() > 0)
			titol = getTitolPerTasca(task, tasca);
//		task.setEntornId(expedient.getEntorn().getId());
//		task.setTitol(titol);
//		task.setFieldToCache(
//				"identificador",
//				expedient.getIdentificador());
//		task.setFieldToCache(
//				"identificadorOrdenacio",
//				expedient.getIdentificadorOrdenacio());
//		task.setFieldToCache(
//				"numeroIdentificador",
//				expedient.getNumeroIdentificador());
//		task.setFieldToCache(
//				"expedientTipusId",
//				expedient.getTipus().getId().toString());
//		task.setFieldToCache(
//				"expedientTipusNom",
//				expedient.getTipus().getNom());
//		task.setFieldToCache(
//				"processInstanceId",
//				expedient.getProcessInstanceId());
//		task.setFieldToCache(
//				"tramitacioMassiva",
//				new Boolean(tasca.isTramitacioMassiva()).toString());
//		task.setFieldToCache(
//				"definicioProcesJbpmKey",
//				tasca.getDefinicioProces().getJbpmKey());
//		task.setCacheActiu();
//		task.setTramitacioMassiva(tasca.isTramitacioMassiva());
//		task.setDefinicioProcesKey(tasca.getDefinicioProces().getJbpmKey());
//		workflowEngineApi.updateTaskInstanceInfoCache(
//				task.getId(),
//				titol,
//				task.getInfoTasca());
	}
	
	private DadesCacheTasca getDadesCacheTasca(WTaskInstance task) {
		return  new DadesCacheTasca(
//				task.getEntornId(),
				task.getTitol(),
//				task.getFieldFromDescription("identificador"),
//				task.getFieldFromDescription("identificadorOrdenacio"),
//				task.getFieldFromDescription("numeroIdentificador"),
//				new Long(task.getFieldFromDescription("expedientTipusId")),
//				task.getFieldFromDescription("expedientTipusNom"),
//				task.getFieldFromDescription("processInstanceId"),
//				task.getTramitacioMassiva(),
				task.getDefinicioProcesKey());
	}

	public class DadesCacheTasca {
//		private Long entornId;
		private String titol;
//		private String identificador;
//		private String identificadorOrdenacio;
//		private String numeroIdentificador;
//		private Long expedientTipusId;
//		private String expedientTipusNom;
//		private String processInstanceId;
//		private Boolean tramitacioMassiva;
		private String definicioProcesJbpmKey;
		public DadesCacheTasca(
//				Long entornId,
				String titol,
//				String identificador,
//				String identificadorOrdenacio,
//				String numeroIdentificador,
//				Long expedientTipusId,
//				String expedientTipusNom,
//				String processInstanceId,
//				Boolean tramitacioMassiva,
				String definicioProcesJbpmKey) {
//			this.entornId = entornId;
			this.titol = titol;
//			this.identificador = identificador;
//			this.identificadorOrdenacio = identificadorOrdenacio;
//			this.numeroIdentificador = numeroIdentificador;
//			this.expedientTipusId = expedientTipusId;
//			this.expedientTipusNom = expedientTipusNom;
//			this.processInstanceId = processInstanceId;
//			this.tramitacioMassiva = tramitacioMassiva;
			this.definicioProcesJbpmKey = definicioProcesJbpmKey;
		}
//		public Long getEntornId() {
//			return entornId;
//		}
		public String getTitol() {
			return titol;
		}
//		public String getIdentificador() {
//			return identificador;
//		}
//		public String getIdentificadorOrdenacio() {
//			return identificadorOrdenacio;
//		}
//		public String getNumeroIdentificador() {
//			return numeroIdentificador;
//		}
//		public Long getExpedientTipusId() {
//			return expedientTipusId;
//		}
//		public String getExpedientTipusNom() {
//			return expedientTipusNom;
//		}
//		public String getProcessInstanceId() {
//			return processInstanceId;
//		}
//		public boolean isTramitacioMassiva() {
//			return tramitacioMassiva != null ? tramitacioMassiva.booleanValue() : false;
//		}
		public String getDefinicioProcesJbpmKey() {
			return definicioProcesJbpmKey;
		}
	}

	public TascaDto comprovarTascaPertanyExpedient(
			String taskId,
			Expedient expedient) {
		var task = tascaClientService.getTascaV1(taskId);
//		WTaskInstance task = workflowEngineApi.getTaskById(taskId);
		if (task == null) {
			logger.debug("No s'ha trobat la tasca (" +
					"taskId=" + taskId + ")");
			throw new NoTrobatException(WTaskInstance.class, taskId);
		}
//		WProcessInstance rootProcessInstance = workflowEngineApi.getRootProcessInstance(
//				task.getProcessInstanceId());
//		if (!expedient.getProcessInstanceId().equals(rootProcessInstance.getId())) {
		if (!expedient.getId().equals(task.getExpedientId())) {
			logger.debug("La tasca no pertany a l'expedient (" +
					"id=" + taskId + ", " +
					"expedientId=" + expedient.getId() + ")");
			throw new NoTrobatException(WTaskInstance.class, taskId);
		}
		return task;
	}

	public WTaskInstance comprovarWTascaPertanyExpedient(
			String taskId,
			Expedient expedient) {
		comprovarTascaPertanyExpedient(taskId, expedient);
		return workflowEngineApi.getTaskById(taskId);
	}

	private List<String> getCampsExpressioTitol(String expressio) {
		List<String> resposta = new ArrayList<String>();
		String[] parts = expressio.split("\\$\\{");
		for (String part: parts) {
			int index = part.indexOf("}");
			if (index != -1)
				resposta.add(part.substring(0, index));
		}
		return resposta;
	}

	public ExpedientTascaDto toTascaInicialDto(
			String startTaskName,
			String jbpmId,
			Map<String, Object> valors) {
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProcesJbpmId(
				startTaskName,
				jbpmId);
		ExpedientTascaDto dto = new ExpedientTascaDto();
		dto.setTitol(tasca.getNom());
		dto.setTascaTipus(
				conversioTipusServiceHelper.convertir(
						tasca.getTipus(),
						ExpedientTascaDto.TascaTipusDto.class));
		dto.setJbpmName(tasca.getJbpmName());
		dto.setValidada(false);
		dto.setDocumentsComplet(false);
		dto.setTascaId(tasca.getId());
		dto.setSignaturesComplet(false);
		/*dto.setDefinicioProces(
				conversioTipusHelper.convertir(
						tasca.getDefinicioProces(), DefinicioProcesDto.class));*/
		dto.setOutcomes(workflowEngineApi.findStartTaskOutcomes(jbpmId, startTaskName));
		dto.setTascaFormExternCodi(tasca.getFormExtern());
		return dto;
	}

	public String getTitolPerTasca(
			WTaskInstance task,
			Tasca tasca) throws Exception {
		String titol = null;
		if (tasca != null) {
			Map<String, Object> textPerCamps = new HashMap<String, Object>();
			titol = tasca.getNom();
			if (tasca.getNomScript() != null && tasca.getNomScript().length() > 0) {
				List<String> campsExpressio = getCampsExpressioTitol(tasca.getNomScript());
				Map<String, Object> valors = workflowEngineApi.getTaskInstanceVariables(task.getId());
				Map<String, Object> procesInstanceVariables = workflowEngineApi.getProcessInstanceVariables(task.getProcessInstanceId());
				if (procesInstanceVariables != null)
					valors.putAll(procesInstanceVariables);
				for (String campCodi: campsExpressio) {
					Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
					ExpedientTipus expedientTipus = expedient.getTipus();
					
					Set<Camp> camps;
					if (expedientTipus.isAmbInfoPropia())
						camps = expedientTipus.getCamps();
					else
						camps = tasca.getDefinicioProces().getCamps();
					
					for (Camp camp: camps) {
						if (camp.getCodi().equals(campCodi)) {
							TascaDadaDto tascaDada = variableHelper.findDadaPerInstanciaTasca(
									task,
									campCodi);
							if (tascaDada != null && tascaDada.getText() != null) {
								textPerCamps.put(
										campCodi,
										tascaDada.getText());
							} else if (tascaDada == null) {
								ExpedientDadaDto valor = variableHelper.getDadaPerInstanciaProces(task.getProcessInstanceId(), campCodi);
								textPerCamps.put(campCodi,(valor == null) ? null : valor.getText());
							}
							break;
						}
					}
				}
				try {
					titol = (String)workflowEngineApi.evaluateExpression(
							task.getId(),
							task.getProcessInstanceId(),
							tasca.getNomScript(),
							textPerCamps);
				} catch (Exception ex) {
					logger.error("No s'ha pogut evaluar l'script per canviar el titol de la tasca", ex);
				}
			}
		} else {
			titol = task.getTaskName();
		}
		return titol;
	}

	public void createDadesTasca(String taskId) throws Exception {
		WTaskInstance task = workflowEngineApi.getTaskById(taskId);
		setTascaCache(task, null);
	}

	//TODO substituir mètode
	public Tasca findTascaByWTaskInstanceId(
			String jbpmTaskId) {
		return findTascaByWTaskInstance(workflowEngineApi.getTaskById(jbpmTaskId));
	}
	//TODO substituir mètode
	public Tasca findTascaByWTaskInstance(
			WTaskInstance task) {
		return tascaRepository.findByJbpmNameAndDefinicioProcesJbpmId(
				task.getTaskName(),
				task.getProcessDefinitionId());
	}

	/**
	 * Retorna les tasques de l'expedient per instància de procés.
	 * @param expedient
	 * @param processInstanceId
	 * @param completed Inclou o no les tasques completades
	 * @param mostrarDeOtrosUsuarios Mostra o no les dels altres usuaris
	 * @return
	 */
	public List<ExpedientTascaDto> findTasquesPerExpedientPerInstanciaProces(
			Expedient expedient,
			String processInstanceId,
			boolean completed,
			boolean notCompleted,
			boolean mostrarDeOtrosUsuarios) {
		List<ExpedientTascaDto> resposta = new ArrayList<ExpedientTascaDto>();
		List<TascaDto> tasquesMs = tascaClientService.findTasquesAmbFiltrePaginatV1(ConsultaTascaDades.builder()
						.entornId(EntornActual.getEntornId())
						.expedientId(expedient.getId())
						.nomesPendents(false)
						.filtre("proces.procesId==" + processInstanceId)
						.build())
				.getContent();
//		List<WTaskInstance> tasks = workflowEngineApi.findTaskInstancesByProcessInstanceId(processInstanceId);
//		for (WTaskInstance task: tasks) {
		for (TascaDto task: tasquesMs) {
			ExpedientTascaDto tasca = toExpedientTascaDto(
					task,
					expedient,
					false,
					false);
			if (((completed && tasca.isCompleted()) || (notCompleted && !tasca.isCompleted()))
					&& (mostrarDeOtrosUsuarios || tasca.isAssignadaUsuariActual())) {
				resposta.add(tasca);
			}
		}
		final boolean compl = completed;
		Collections.sort(
				resposta, 
				new Comparator<ExpedientTascaDto>() {
					public int compare(ExpedientTascaDto t1, ExpedientTascaDto t2) {
						int order = t1.getCreateTime().compareTo(t2.getCreateTime());
						return compl ? order : order*-1;
					}
				}
			);
		return resposta;
	}

	public ExpedientTascaDto toExpedientTascaDto(
			WTaskInstance task,
			Expedient expedient,
			boolean perTramitacio,
			boolean ambPermisos) {
		ExpedientTascaDto dto = new ExpedientTascaDto();
		DefinicioProces defp = definicioProcesRepository.findByJbpmId(task.getProcessDefinitionId());
		Tasca t = tascaRepository.findByJbpmNameAndDefinicioProces(task.getTaskName(), defp);
		if (t != null) {
			dto.setAmbRepro(t.isAmbRepro());
			dto.setMostrarAgrupacions(t.isMostrarAgrupacions());
		}
		
		dto.setId(task.getId());
		dto.setTitol(task.getTitol());
		dto.setJbpmName(task.getTaskName());
		dto.setDescription(task.getDescription());
		dto.setAssignee(task.getActorId());
		dto.setPooledActors(task.getPooledActors());
		dto.setCreateTime(task.getCreateTime());
		dto.setStartTime(task.getStartTime());
		dto.setEndTime(task.getEndTime());
		dto.setDueDate(task.getDueDate());
		dto.setPriority(task.getPriority());
		dto.setOpen(task.isOpen());
		dto.setCompleted(task.isCompleted());
		dto.setCancelled(task.isCancelled());
		dto.setSuspended(task.isSuspended());
		// TODO: obtenir informació de tramitació massiva
//		dto.setTascaTramitacioMassiva(task.getTramitacioMassiva());
		Tasca tasca = findTascaByWTaskInstance(task);
		if (tasca != null) {
			dto.setTascaFinalitzacioSegonPla(tasca.isFinalitzacioSegonPla());
			if (tasca.isFinalitzacioSegonPla() && 
				tascaSegonPlaHelper.isTasquesSegonPlaLoaded() && 
				tascaSegonPlaHelper.getTasquesSegonPla().containsKey(task.getTaskInstanceId())) {
				InfoSegonPla infoSegonPla = tascaSegonPlaHelper.getTasquesSegonPla().get(task.getTaskInstanceId());
				dto.setMarcadaFinalitzar(infoSegonPla.getMarcadaFinalitzar());
				dto.setIniciFinalitzacio(infoSegonPla.getIniciFinalitzacio());
				dto.setErrorFinalitzacio(infoSegonPla.getError());
			}
		}
		Expedient expedientNoNull = expedient;
		if (expedientNoNull == null) {
			expedientNoNull = expedientHelper.findExpedientByProcessInstanceId(
					task.getProcessInstanceId());
		}
		if (perTramitacio) {
			// Opcional outcomes?
			dto.setOutcomes(workflowEngineApi.findTaskInstanceOutcomes(task.getId()));
			// Opcional dades tasca?
			
			dto.setTascaId(tasca.getId());
			dto.setTascaNom(tasca.getNom());
			dto.setTascaTipus(
					conversioTipusServiceHelper.convertir(
							tasca.getTipus(),
							ExpedientTascaDto.TascaTipusDto.class));
			dto.setTascaMissatgeInfo(tasca.getMissatgeInfo());
			dto.setTascaMissatgeWarn(tasca.getMissatgeWarn());
			dto.setTascaRecursForm(tasca.getRecursForm());
			dto.setTascaFormExternCodi(tasca.getFormExtern());
			dto.setTascaDelegable(tasca.getExpressioDelegacio() != null);
			// Opcional estat tramitació tasca?
			dto.setValidada(isTascaValidada(task));
			dto.setDocumentsComplet(isDocumentsComplet(task));
			dto.setSignaturesComplet(isSignaturesComplet(task));
			// Opcional informació delegacio?
			DelegationInfo delegationInfo = getDelegationInfo(task.getId());
			if (delegationInfo != null) {
				boolean original = task.getId().equals(delegationInfo.getSourceTaskId());
				dto.setDelegada(true);
				dto.setDelegacioOriginal(original);
				dto.setDelegacioData(delegationInfo.getStart());
				dto.setDelegacioComentari(delegationInfo.getComment());
				dto.setDelegacioSupervisada(delegationInfo.isSupervised());
				WTaskInstance tascaDelegacio = null;
				if (original) {
					tascaDelegacio = workflowEngineApi.getTaskById(delegationInfo.getTargetTaskId());
				} else {
					tascaDelegacio = workflowEngineApi.getTaskById(delegationInfo.getSourceTaskId());
				}			
				dto.setDelegacioPersona(
						pluginHelper.personaFindAmbCodi(tascaDelegacio.getActorId()));
			}
			DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(task.getProcessDefinitionId());
			if (definicioProces != null) {
				dto.setDefinicioProcesId(definicioProces.getId());
			}
		}
		dto.setAgafada(task.isAgafada());
		dto.setProcessInstanceId(task.getProcessInstanceId());
		dto.setExpedientId(expedientNoNull.getId());
		dto.setExpedientIdentificador(expedientNoNull.getIdentificador());
		dto.setExpedientNumero(expedientNoNull.getNumero());
		dto.setExpedientTipusNom(expedientNoNull.getTipus().getNom());
		dto.setExpedientTipusId(expedientNoNull.getTipus().getId());
		if (task.getActorId() != null) {
			dto.setResponsable(
					pluginHelper.personaFindAmbCodi(task.getActorId()));
		}
		dto.setAssignadaUsuariActual(false);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (task.getActorId() != null) {
			if (auth != null) {
				dto.setAssignadaUsuariActual(task.getActorId().equals(auth.getName()));
			}
		} else {
			if (task.getPooledActors() != null && !task.getPooledActors().isEmpty()) {
				List<PersonaDto> responsables = new ArrayList<PersonaDto>();
				for (String pooledActor: task.getPooledActors()) {
					PersonaDto persona = this.findPersonaOrDefault(pooledActor);
					if (persona != null) {
						if (auth.getName().equals(pooledActor))
							dto.setAssignadaUsuariActual(true);
						responsables.add(persona);
					}
				}
				Collections.sort(
						responsables,
						new Comparator<PersonaDto>() {
							public int compare(PersonaDto p1, PersonaDto p2) {
								return p1.getNom().compareToIgnoreCase(p2.getNom());
							}
						});
				dto.setResponsables(responsables);
			}
			if (!dto.isAssignadaUsuariActual() && task.getGrups() != null && !task.getGrups().isEmpty()) {
				var grupsUsuariActual = KeycloakHelper.getCurrentUserRols();
				dto.setAssignadaUsuariActual(task.getGrups().stream().anyMatch(grupsUsuariActual::contains));
			}
		}
		if (ambPermisos) {
			permisosHelper.omplirControlPermisosSegonsUsuariActual(
					expedientNoNull.getTipus().getId(),
					dto,
					ExpedientTipus.class);
		}
		return dto;
	}

	public ExpedientTascaDto toExpedientTascaDto(
			es.caib.helium.client.expedient.tasca.model.TascaDto tascaMs,
			Expedient expedient,
			boolean perTramitacio,
			boolean ambPermisos) {
		
		ExpedientTascaDto dto = new ExpedientTascaDto();
//		DefinicioProces defp = definicioProcesRepository.findByJbpmId(tascaMs.getProcessDefinitionId());
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProcesJbpmId(tascaMs.getNom(), tascaMs.getProcessDefinitionId());
		if (tasca != null) {
			dto.setAmbRepro(tasca.isAmbRepro());
			dto.setMostrarAgrupacions(tasca.isMostrarAgrupacions());
		}

		dto.setId(tascaMs.getTascaId());
		dto.setTitol(tascaMs.getTitol());
		dto.setJbpmName(tascaMs.getNom());
		dto.setDescription(tascaMs.getTitol());
		dto.setAssignee(tascaMs.getUsuariAssignat());
		dto.setPooledActors( tascaMs.getResponsables().stream().collect(Collectors.toSet()));
		dto.setGrups( tascaMs.getGrups().stream().collect(Collectors.toSet()));
		dto.setCreateTime(tascaMs.getDataCreacio());
		dto.setEndTime(tascaMs.getDataFi());
		dto.setDueDate(tascaMs.getDataFins());
		dto.setPriority(tascaMs.getPrioritat());
		dto.setOpen(!tascaMs.isCompletada() && !tascaMs.isCancelada());
		dto.setCompleted(tascaMs.isCompletada());
		dto.setCancelled(tascaMs.isCancelada());
		dto.setSuspended(tascaMs.isSuspesa());

		if (tascaSegonPlaHelper.isTasquesSegonPlaLoaded() && 
				tascaSegonPlaHelper.getTasquesSegonPla().containsKey(tascaMs.getTascaId())) {
			InfoSegonPla infoSegonPla = tascaSegonPlaHelper.getTasquesSegonPla().get(tascaMs.getTascaId());
			dto.setMarcadaFinalitzar(infoSegonPla.getMarcadaFinalitzar());
			dto.setIniciFinalitzacio(infoSegonPla.getIniciFinalitzacio());
			dto.setErrorFinalitzacio(infoSegonPla.getError());
		}

		Expedient expedientNoNull = expedient;
		if (expedientNoNull == null) {
			expedientNoNull = expedientHelper.findById(tascaMs.getExpedientId());
		}
		
		if (perTramitacio) {
			DefinicioProces defp = definicioProcesRepository.findByJbpmId(tascaMs.getProcessDefinitionId());
			dto.setOutcomes(workflowEngineApi.findTaskInstanceOutcomes(tascaMs.getTascaId()));

			dto.setTascaId(tasca.getId());
			dto.setTascaNom(tasca.getNom());
			dto.setTascaTipus(
					conversioTipusServiceHelper.convertir(
							tasca.getTipus(),
							ExpedientTascaDto.TascaTipusDto.class));
			dto.setTascaMissatgeInfo(tasca.getMissatgeInfo());
			dto.setTascaMissatgeWarn(tasca.getMissatgeWarn());
			dto.setTascaRecursForm(tasca.getRecursForm());
			dto.setTascaFormExternCodi(tasca.getFormExtern());
			dto.setTascaDelegable(tasca.getExpressioDelegacio() != null);
//			//TODO
//			dto.setValidada(isTascaValidada(task));
//			dto.setDocumentsComplet(isDocumentsComplet(task));
//			dto.setSignaturesComplet(isSignaturesComplet(task));
			// Opcional informació delegacio?
			DelegationInfo delegationInfo = getDelegationInfo(tascaMs.getTascaId());
			if (delegationInfo != null) {
				boolean original = tascaMs.getTascaId().equals(delegationInfo.getSourceTaskId());
				dto.setDelegada(true);
				dto.setDelegacioOriginal(original);
				dto.setDelegacioData(delegationInfo.getStart());
				dto.setDelegacioComentari(delegationInfo.getComment());
				dto.setDelegacioSupervisada(delegationInfo.isSupervised());
				WTaskInstance tascaDelegacio = null;
				if (original) {
					tascaDelegacio = workflowEngineApi.getTaskById(delegationInfo.getTargetTaskId());
				} else {
					tascaDelegacio = workflowEngineApi.getTaskById(delegationInfo.getSourceTaskId());
				}
				dto.setDelegacioPersona(
						pluginHelper.personaFindAmbCodi(tascaDelegacio.getActorId()));
			}
			DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(tascaMs.getProcessDefinitionId());
			if (definicioProces != null) {
				dto.setDefinicioProcesId(definicioProces.getId());
			}
		}
		dto.setAgafada(tascaMs.isAgafada());
		dto.setProcessInstanceId(tascaMs.getProcesId());
		dto.setExpedientId(expedientNoNull.getId());
		dto.setExpedientIdentificador(expedientNoNull.getIdentificador());
		dto.setExpedientNumero(expedientNoNull.getNumero());
		dto.setExpedientTipusNom(expedientNoNull.getTipus().getNom());
		dto.setExpedientTipusId(expedientNoNull.getTipus().getId());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		dto.setAssignadaUsuariActual(false);
		if (tascaMs.getUsuariAssignat() != null) {
			dto.setResponsable(this.findPersonaOrDefault(tascaMs.getUsuariAssignat()));
			if (auth != null) {
				dto.setAssignadaUsuariActual(tascaMs.getUsuariAssignat().equals(auth.getName()));
			}
		} else {
			if (tascaMs.getResponsables() != null && !tascaMs.getResponsables().isEmpty()) {
				List<PersonaDto> responsables = new ArrayList<PersonaDto>();
				for (String pooledActor: tascaMs.getResponsables()) {
					PersonaDto persona = this.findPersonaOrDefault(pooledActor);
					if (persona != null) {
						if (auth.getName().equals(pooledActor))
							dto.setAssignadaUsuariActual(true);
						responsables.add(persona);
					}
				}
				Collections.sort(
						responsables,
						new Comparator<PersonaDto>() {
							public int compare(PersonaDto p1, PersonaDto p2) {
								return p1.getNom().compareToIgnoreCase(p2.getNom());
							}
						});
				dto.setResponsables(responsables);
			}
			if (!dto.isAssignadaUsuariActual() && tascaMs.getGrups() != null && !tascaMs.getGrups().isEmpty()) {
				var grupsUsuariActual = KeycloakHelper.getCurrentUserRols();
				dto.setAssignadaUsuariActual(tascaMs.getGrups().stream().anyMatch(grupsUsuariActual::contains));
			}
		}
		if (ambPermisos) {
			permisosHelper.omplirControlPermisosSegonsUsuariActual(
					expedientNoNull.getTipus().getId(),
					dto,
					ExpedientTipus.class);
		}
		return dto;
	}
	
	/** Mètode per evitar l'error quan l'usuari no es troba i com a mínim
	 * retornar una persona DTO amb el codi informat.
	 * @param personaCodi
	 * @return
	 */
	public PersonaDto findPersonaOrDefault(String personaCodi) {
		PersonaDto persona;
		try {
			persona = pluginHelper.personaFindAmbCodi(personaCodi);
		} catch(Exception e) {
			logger.warn("Error consultant la persona amb codi " + personaCodi + ": " + e.getMessage());
			persona = new PersonaDto(personaCodi, "(" + personaCodi + ")", "", "", Sexe.SEXE_DONA);
		}
		return persona;
	}

	public void validarTasca(String taskId) {
		workflowEngineApi.setTaskInstanceVariable(
				taskId,
				Constants.VAR_TASCA_VALIDADA,
				new Date());
	}
	public void restaurarTasca(String taskId) {
		workflowEngineApi.deleteTaskInstanceVariable(
				taskId,
				Constants.VAR_TASCA_VALIDADA);
	}

	public boolean isTascaValidada(Object task) {
		Tasca tasca = findTascaByWTaskInstance((WTaskInstance)task);
		boolean hiHaCampsModificables = false;
		if (tasca == null)
			return false;
		for (CampTasca camp: tasca.getCamps()) {
			if (!camp.isReadOnly()) {
				hiHaCampsModificables = true;
				break;
			}
		}
		if (!hiHaCampsModificables)
			return true;
		Object valor = workflowEngineApi.getTaskInstanceVariable(
				((WTaskInstance)task).getId(),
				Constants.VAR_TASCA_VALIDADA);
		return isValorTipusDate(valor);
	}
	public boolean isDocumentsComplet(Object task) {
		boolean ok = true;
		Tasca tasca = findTascaByWTaskInstance((WTaskInstance)task);
		for (DocumentTasca docTasca: tasca.getDocuments()) {
			if (docTasca.isRequired()) {
				String codiJbpm = Constants.PREFIX_DOCUMENT + docTasca.getDocument().getCodi();
				Object valor = workflowEngineApi.getTaskInstanceVariable(
						((WTaskInstance)task).getId(),
						codiJbpm);
				if (valor == null) {
					ok = false;
					break;
				}
			}
		}
		return ok;
	}
	public boolean isSignaturesComplet(Object task) {
		boolean ok = true;
		Tasca tasca = findTascaByWTaskInstance((WTaskInstance)task);
		for (FirmaTasca firmaTasca: tasca.getFirmes()) {
			if (firmaTasca.isRequired()) {
				String codiJbpm = Constants.PREFIX_SIGNATURA + firmaTasca.getDocument().getCodi();
				Object valor = workflowEngineApi.getTaskInstanceVariable(((WTaskInstance)task).getId(), codiJbpm);
				if (valor == null)
					ok = false;
			}
		}
		return ok;
	}

	public void processarCampsAmbDominiCacheActivat(
			WTaskInstance task,
			Tasca tasca,
			Map<String, Object> variables) throws Exception {
		Long expedientTipusId = expedientTipusHelper.findIdByProcessInstanceId(task.getProcessInstanceId());
		List<CampTasca> campsTasca = campTascaRepository.findAmbTascaOrdenats(tasca.getId(), expedientTipusId);
		for (CampTasca campTasca: campsTasca) {
			if (campTasca.getCamp().isDominiCacheText()) {
				Object campValor = variables.get(campTasca.getCamp().getCodi());
				if (	campTasca.getCamp().getTipus().equals(TipusCamp.SELECCIO) ||
						campTasca.getCamp().getTipus().equals(TipusCamp.SUGGEST)) {
					// TODO Pensar què fer amb DominiCodiDescripcio
//					if (campValor instanceof DominiCodiDescripcio) {
//						variables.put(
//								campTasca.getCamp().getCodi(),
//								((DominiCodiDescripcio)campValor).getCodi());
//						variables.put(
//								JbpmVars.PREFIX_VAR_DESCRIPCIO + campTasca.getCamp().getCodi(),
//								((DominiCodiDescripcio)campValor).getDescripcio());
//					} else {
					if (1==1) {
						String text = variableHelper.getTextPerCamp(
								campTasca.getCamp(),
								campValor,
								null,
								null,
								task.getProcessInstanceId(),
								null,
								null);
						variables.put(
								Constants.PREFIX_VAR_DESCRIPCIO + campTasca.getCamp().getCodi(),
								text);
					}
				}
			}
		}
	}

	public void createDelegationInfo(
			WTaskInstance task,
			WTaskInstance original,
			WTaskInstance delegada,
			String comentari,
			boolean supervisada) {
		DelegationInfo info = new DelegationInfo();
		info.setSourceTaskId(original.getId());
		info.setTargetTaskId(delegada.getId());
		info.setStart(new Date());
		info.setComment(comentari);
		info.setSupervised(supervisada);
		workflowEngineApi.setTaskInstanceVariable(
				task.getId(), 
				Constants.VAR_TASCA_DELEGACIO,
				info);
	}
	public DelegationInfo getDelegationInfo(String tascaId) {
		return (DelegationInfo)workflowEngineApi.getTaskInstanceVariable(
				tascaId,
				Constants.VAR_TASCA_DELEGACIO);
	}
	public void deleteDelegationInfo(WTaskInstance task) {
		workflowEngineApi.deleteTaskInstanceVariable(
				task.getId(),
				Constants.VAR_TASCA_DELEGACIO);
	}

	private boolean isValorTipusDate(Object valor) {
		if (valor != null) {
			if (valor instanceof String) {
				try {
					valor = sdf.parse((String) valor);
				} catch (ParseException e) {}
			}
			if ((valor instanceof Date))
				return true;
		}
		return false;
	}

	private static final Logger logger = LoggerFactory.getLogger(TascaHelper.class);

}