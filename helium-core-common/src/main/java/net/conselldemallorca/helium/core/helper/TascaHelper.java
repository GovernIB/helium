/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import edu.emory.mathcs.backport.java.util.Collections;
import net.conselldemallorca.helium.core.api.WProcessInstance;
import net.conselldemallorca.helium.core.api.WTaskInstance;
import net.conselldemallorca.helium.core.api.WorkflowEngineApi;
import net.conselldemallorca.helium.core.common.JbpmVars;
import net.conselldemallorca.helium.core.helper.TascaSegonPlaHelper.InfoSegonPla;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.FirmaTasca;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.jbpm3.integracio.DelegationInfo;
import net.conselldemallorca.helium.jbpm3.integracio.DominiCodiDescripcio;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.TascaNoDisponibleException;
import net.conselldemallorca.helium.v3.core.repository.CampTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.TascaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private ConversioTipusHelper conversioTipusHelper;
	@Resource(name = "permisosHelperV3")
	private PermisosHelper permisosHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private TascaSegonPlaHelper tascaSegonPlaHelper;
	@Resource
	private MessageHelper messageHelper;



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
				throw new TascaNoDisponibleException(id, messageHelper.getMessage("error.tascaService.noAssignada"), null);
			}
		}
		if (comprovarPendent) {
			if (!task.isOpen() || task.isCancelled() || task.isSuspended()) {
				logger.debug("La tasca no està en estat pendent (" +
						"id=" + id + ")");
				throw new TascaNoDisponibleException(id, messageHelper.getMessage("error.tascaService.noPendent"), null);
			}
		}
		return task;
	}
	
	public DadesCacheTasca getDadesCacheTasca(
			WTaskInstance task,
			Expedient expedient) {
		DadesCacheTasca dadesCache = null;
		if (!task.isCacheActiu()) {
			setTascaCache(task, expedient);
		}
		try {
			dadesCache = getDadesCacheTasca(task);
		} catch (Exception e) {
			task.setCacheInactiu();
			setTascaCache(task, expedient);
			dadesCache = getDadesCacheTasca(task);
		}
		return dadesCache;
	}
	
	private void setTascaCache(
			WTaskInstance task,
			Expedient expedient) {
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
		task.setEntornId(expedient.getEntorn().getId());
		task.setTitol(titol);
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
		task.setCacheActiu();
		task.setTramitacioMassiva(new Boolean(tasca.isTramitacioMassiva()));
		task.setDefinicioProcesKey(tasca.getDefinicioProces().getJbpmKey());
		workflowEngineApi.updateTaskInstanceInfoCache(
				task.getId(),
				titol,
				task.getInfoTasca());
	}
	
	private DadesCacheTasca getDadesCacheTasca(WTaskInstance task) {
		return  new DadesCacheTasca(
				task.getEntornId(),
				task.getTitol(),
//				task.getFieldFromDescription("identificador"),
//				task.getFieldFromDescription("identificadorOrdenacio"),
//				task.getFieldFromDescription("numeroIdentificador"),
//				new Long(task.getFieldFromDescription("expedientTipusId")),
//				task.getFieldFromDescription("expedientTipusNom"),
//				task.getFieldFromDescription("processInstanceId"),
				task.getTramitacioMassiva(),
				task.getDefinicioProcesKey());
	}

	public class DadesCacheTasca {
		private Long entornId;
		private String titol;
//		private String identificador;
//		private String identificadorOrdenacio;
//		private String numeroIdentificador;
//		private Long expedientTipusId;
//		private String expedientTipusNom;
//		private String processInstanceId;
		private boolean tramitacioMassiva;
		private String definicioProcesJbpmKey;
		public DadesCacheTasca(
				Long entornId,
				String titol,
//				String identificador,
//				String identificadorOrdenacio,
//				String numeroIdentificador,
//				Long expedientTipusId,
//				String expedientTipusNom,
//				String processInstanceId,
				boolean tramitacioMassiva,
				String definicioProcesJbpmKey) {
			this.entornId = entornId;
			this.titol = titol;
//			this.identificador = identificador;
//			this.identificadorOrdenacio = identificadorOrdenacio;
//			this.numeroIdentificador = numeroIdentificador;
//			this.expedientTipusId = expedientTipusId;
//			this.expedientTipusNom = expedientTipusNom;
//			this.processInstanceId = processInstanceId;
			this.tramitacioMassiva = tramitacioMassiva;
			this.definicioProcesJbpmKey = definicioProcesJbpmKey;
		}
		public Long getEntornId() {
			return entornId;
		}
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
		public boolean isTramitacioMassiva() {
			return tramitacioMassiva;
		}
		public String getDefinicioProcesJbpmKey() {
			return definicioProcesJbpmKey;
		}
	}

	public WTaskInstance comprovarTascaPertanyExpedient(
			String taskId,
			Expedient expedient) {
		WTaskInstance task = workflowEngineApi.getTaskById(taskId);
		if (task == null) {
			logger.debug("No s'ha trobat la tasca (" +
					"taskId=" + taskId + ")");
			throw new NoTrobatException(WTaskInstance.class, taskId);
		}
		WProcessInstance rootProcessInstance = workflowEngineApi.getRootProcessInstance(
				task.getProcessInstanceId());
		if (!expedient.getProcessInstanceId().equals(rootProcessInstance.getId())) {
			logger.debug("La tasca no pertany a l'expedient (" +
					"id=" + taskId + ", " +
					"expedientId=" + expedient.getId() + ")");
			throw new NoTrobatException(WTaskInstance.class, taskId);
		}
		return task;
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
				conversioTipusHelper.convertir(
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
			Tasca tasca) {
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

	public void createDadesTasca(Long taskId) {
		WTaskInstance task = workflowEngineApi.getTaskById(String.valueOf(taskId));
		setTascaCache(task, null);
	}

	public Tasca findTascaByWTaskInstanceId(
			String jbpmTaskId) {
		return findTascaByWTaskInstance(workflowEngineApi.getTaskById(jbpmTaskId));
	}
	public Tasca findTascaByWTaskInstance(
			WTaskInstance task) {
		return tascaRepository.findByJbpmNameAndDefinicioProcesJbpmId(
				task.getTaskName(),
				task.getProcessDefinitionId());
	}

	public List<ExpedientTascaDto> findTasquesPerExpedientPerInstanciaProces(
			String processInstanceId,
			Expedient expedient,
			boolean permisosVerOtrosUsuarios,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup) {
		List<ExpedientTascaDto> resposta = new ArrayList<ExpedientTascaDto>();
		List<WTaskInstance> tasks = workflowEngineApi.findTaskInstancesByProcessInstanceId(processInstanceId);
		for (WTaskInstance task: tasks) {
			if (!task.isCompleted()) {
				ExpedientTascaDto tasca = toExpedientTascaDto(
						task,
						expedient,
						true,
						false);
				if (permisosVerOtrosUsuarios || tasca.isAssignadaUsuariActual()) {
					boolean esTareaGrupo = !tasca.isAgafada() && tasca.getResponsables() != null && !tasca.getResponsables().isEmpty();
					if (nomesTasquesGrup && esTareaGrupo) {						
						resposta.add(tasca);
					} else if (nomesTasquesPersonals && !esTareaGrupo) {
						resposta.add(tasca);
					} else if (!nomesTasquesPersonals && !nomesTasquesGrup) {
						resposta.add(tasca);
					}
				}
			}
		}
		return resposta;
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
		List<WTaskInstance> tasks = workflowEngineApi.findTaskInstancesByProcessInstanceId(processInstanceId);
		for (WTaskInstance task: tasks) {
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
		dto.setTascaTramitacioMassiva(task.getTramitacioMassiva());
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
					conversioTipusHelper.convertir(
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
			DelegationInfo delegationInfo = getDelegationInfo(task);
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
		dto.setExpedientTipusNom(expedientNoNull.getTipus().getNom());
		dto.setExpedientTipusId(expedientNoNull.getTipus().getId());
		if (task.getActorId() != null) {
			dto.setResponsable(
					pluginHelper.personaFindAmbCodi(task.getActorId()));
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (task.getActorId() != null) {
			if (auth != null) {
				dto.setAssignadaUsuariActual(task.getActorId().equals(auth.getName()));
			}
		} else if (task.getPooledActors() != null && !task.getPooledActors().isEmpty()) {
			List<PersonaDto> responsables = new ArrayList<PersonaDto>();
			for (String pooledActor: task.getPooledActors()) {
				PersonaDto persona = pluginHelper.personaFindAmbCodi(pooledActor);
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
		if (ambPermisos) {
			permisosHelper.omplirControlPermisosSegonsUsuariActual(
					expedientNoNull.getTipus().getId(),
					dto,
					ExpedientTipus.class);
		}
		return dto;
	}

	public void validarTasca(String taskId) {
		workflowEngineApi.setTaskInstanceVariable(
				taskId,
				JbpmVars.VAR_TASCA_VALIDADA,
				new Date());
	}
	public void restaurarTasca(String taskId) {
		workflowEngineApi.deleteTaskInstanceVariable(
				taskId,
				JbpmVars.VAR_TASCA_VALIDADA);
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
				JbpmVars.VAR_TASCA_VALIDADA);
		if (valor == null || !(valor instanceof Date))
			return false;
		return true;
	}
	public boolean isDocumentsComplet(Object task) {
		boolean ok = true;
		Tasca tasca = findTascaByWTaskInstance((WTaskInstance)task);
		for (DocumentTasca docTasca: tasca.getDocuments()) {
			if (docTasca.isRequired()) {
				String codiJbpm = JbpmVars.PREFIX_DOCUMENT + docTasca.getDocument().getCodi();
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
				String codiJbpm = JbpmVars.PREFIX_SIGNATURA + firmaTasca.getDocument().getCodi();
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
			Map<String, Object> variables) {
		Long expedientTipusId = expedientTipusHelper.findIdByProcessInstanceId(task.getProcessInstanceId());
		List<CampTasca> campsTasca = campTascaRepository.findAmbTascaOrdenats(tasca.getId(), expedientTipusId);
		for (CampTasca campTasca: campsTasca) {
			if (campTasca.getCamp().isDominiCacheText()) {
				Object campValor = variables.get(campTasca.getCamp().getCodi());
				if (	campTasca.getCamp().getTipus().equals(TipusCamp.SELECCIO) ||
						campTasca.getCamp().getTipus().equals(TipusCamp.SUGGEST)) {
					if (campValor instanceof DominiCodiDescripcio) {
						variables.put(
								campTasca.getCamp().getCodi(),
								((DominiCodiDescripcio)campValor).getCodi());
						variables.put(
								JbpmVars.PREFIX_VAR_DESCRIPCIO + campTasca.getCamp().getCodi(),
								((DominiCodiDescripcio)campValor).getDescripcio());
					} else {
						String text = variableHelper.getTextPerCamp(
								campTasca.getCamp(),
								campValor,
								null,
								null,
								task.getProcessInstanceId());
						variables.put(
								JbpmVars.PREFIX_VAR_DESCRIPCIO + campTasca.getCamp().getCodi(),
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
				JbpmVars.VAR_TASCA_DELEGACIO,
				info);
	}
	public DelegationInfo getDelegationInfo(WTaskInstance task) {
		return (DelegationInfo)workflowEngineApi.getTaskInstanceVariable(
				task.getId(),
				JbpmVars.VAR_TASCA_DELEGACIO);
	}
	public void deleteDelegationInfo(WTaskInstance task) {
		workflowEngineApi.deleteTaskInstanceVariable(
				task.getId(),
				JbpmVars.VAR_TASCA_DELEGACIO);
	}

	private static final Logger logger = LoggerFactory.getLogger(TascaHelper.class);

}
