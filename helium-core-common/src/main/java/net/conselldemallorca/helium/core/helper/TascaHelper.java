/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import edu.emory.mathcs.backport.java.util.Collections;
import net.conselldemallorca.helium.core.common.JbpmVars;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.FirmaTasca;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.jbpm3.integracio.DelegationInfo;
import net.conselldemallorca.helium.jbpm3.integracio.DominiCodiDescripcio;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.jbpm3.integracio.LlistatIds;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TaskInstanceNotFoundException;
import net.conselldemallorca.helium.v3.core.repository.CampTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.TascaRepository;

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
	private CampTascaRepository campTascaRepository;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource(name = "permisosHelperV3")
	private PermisosHelper permisosHelper;
	@Resource
	private PluginHelper pluginHelper;



	public JbpmTask getTascaComprovacionsTramitacio(
			String id,
			boolean comprovarAssignacio,
			boolean comprovarPendent) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		JbpmTask task = jbpmHelper.getTaskById(id);
		if (task == null) {
			logger.debug("No s'ha trobat la tasca (" +
					"id=" + id + ")");
			throw new NotFoundException(
					id,
					JbpmTask.class);
		}
		if (comprovarAssignacio) {
			if (task.getAssignee() == null || !task.getAssignee().equals(auth.getName())) {
				logger.debug("La persona no té la tasca assignada (" +
						"id=" + id + ", " +
						"personaCodi=" + auth.getName() + ")");
				throw new NotFoundException(
						id,
						JbpmTask.class);
			}
		}
		if (comprovarPendent) {
			if (!task.isOpen() || task.isCancelled() || task.isSuspended()) {
				logger.debug("La tasca no està en estat pendent (" +
						"id=" + id + ")");
				throw new NotFoundException(
						id,
						JbpmTask.class);
			}
		}
		return task;
	}
	
	public DadesCacheTasca getDadesCacheTasca(
			JbpmTask task,
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
			JbpmTask task,
			Expedient expedient) {
		if (expedient == null)
			expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(
				task.getProcessDefinitionId());
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProces(
				task.getTaskName(),
				definicioProces);
		String titol = tasca.getNom();
		if (tasca.getNomScript() != null && tasca.getNomScript().length() > 0)
			titol = getTitolPerTasca(task, tasca);
		task.setFieldFromDescription(
				"entornId",
				expedient.getEntorn().getId().toString());
		task.setFieldFromDescription(
				"titol",
				titol);
		task.setFieldFromDescription(
				"identificador",
				expedient.getIdentificador());
		task.setFieldFromDescription(
				"identificadorOrdenacio",
				expedient.getIdentificadorOrdenacio());
		task.setFieldFromDescription(
				"numeroIdentificador",
				expedient.getNumeroIdentificador());
		task.setFieldFromDescription(
				"expedientTipusId",
				expedient.getTipus().getId().toString());
		task.setFieldFromDescription(
				"expedientTipusNom",
				expedient.getTipus().getNom());
		task.setFieldFromDescription(
				"processInstanceId",
				expedient.getProcessInstanceId());
		task.setFieldFromDescription(
				"tramitacioMassiva",
				new Boolean(tasca.isTramitacioMassiva()).toString());
		task.setFieldFromDescription(
				"definicioProcesJbpmKey",
				tasca.getDefinicioProces().getJbpmKey());
		task.setCacheActiu();
		jbpmHelper.describeTaskInstance(
				task.getId(),
				titol,
				task.getDescriptionWithFields());
	}
	
	private DadesCacheTasca getDadesCacheTasca(JbpmTask task) {
		return  new DadesCacheTasca(
				new Long(task.getFieldFromDescription("entornId")),
				task.getFieldFromDescription("titol"),
				task.getFieldFromDescription("identificador"),
				task.getFieldFromDescription("identificadorOrdenacio"),
				task.getFieldFromDescription("numeroIdentificador"),
				new Long(task.getFieldFromDescription("expedientTipusId")),
				task.getFieldFromDescription("expedientTipusNom"),
				task.getFieldFromDescription("processInstanceId"),
				new Boolean(task.getFieldFromDescription("tramitacioMassiva")).booleanValue(),
				task.getFieldFromDescription("definicioProcesJbpmKey"));
	}

	public class DadesCacheTasca {
		private Long entornId;
		private String titol;
		private String identificador;
		private String identificadorOrdenacio;
		private String numeroIdentificador;
		private Long expedientTipusId;
		private String expedientTipusNom;
		private String processInstanceId;
		private boolean tramitacioMassiva;
		private String definicioProcesJbpmKey;
		public DadesCacheTasca(
				Long entornId,
				String titol,
				String identificador,
				String identificadorOrdenacio,
				String numeroIdentificador,
				Long expedientTipusId,
				String expedientTipusNom,
				String processInstanceId,
				boolean tramitacioMassiva,
				String definicioProcesJbpmKey) {
			this.entornId = entornId;
			this.titol = titol;
			this.identificador = identificador;
			this.identificadorOrdenacio = identificadorOrdenacio;
			this.numeroIdentificador = numeroIdentificador;
			this.expedientTipusId = expedientTipusId;
			this.expedientTipusNom = expedientTipusNom;
			this.processInstanceId = processInstanceId;
			this.tramitacioMassiva = tramitacioMassiva;
			this.definicioProcesJbpmKey = definicioProcesJbpmKey;
		}
		public Long getEntornId() {
			return entornId;
		}
		public String getTitol() {
			return titol;
		}
		public String getIdentificador() {
			return identificador;
		}
		public String getIdentificadorOrdenacio() {
			return identificadorOrdenacio;
		}
		public String getNumeroIdentificador() {
			return numeroIdentificador;
		}
		public Long getExpedientTipusId() {
			return expedientTipusId;
		}
		public String getExpedientTipusNom() {
			return expedientTipusNom;
		}
		public String getProcessInstanceId() {
			return processInstanceId;
		}
		public boolean isTramitacioMassiva() {
			return tramitacioMassiva;
		}
		public String getDefinicioProcesJbpmKey() {
			return definicioProcesJbpmKey;
		}
	}

	public JbpmTask getTascaComprovacionsExpedient(
			String id,
			Expedient expedient) {
		JbpmTask task = jbpmHelper.getTaskById(id);
		if (task == null) {
			logger.debug("No s'ha trobat la tasca (" +
					"id=" + id + ")");
			throw new NotFoundException(
					id,
					JbpmTask.class);
		}
		JbpmProcessInstance rootProcessInstance = jbpmHelper.getRootProcessInstance(
				task.getProcessInstanceId());
		if (!expedient.getProcessInstanceId().equals(rootProcessInstance.getId())) {
			logger.debug("La tasca no pertany a l'expedient (" +
					"id=" + id + ", " +
					"expedientId=" + expedient.getId() + ")");
			throw new TaskInstanceNotFoundException();
		}
		return task;
	}

	public List<ExpedientTascaDto> findTasquesPerExpedient(
			Expedient expedient) {
		List<ExpedientTascaDto> resposta = new ArrayList<ExpedientTascaDto>();
		// Si l'usuari te permis de supervisio mostra totes les tasques de
		// l'expedient de qualsevol usuari
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean isPermisSupervision = permisosHelper.isGrantedAny(
				expedient.getTipus().getId(),
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.SUPERVISION,
					ExtendedPermission.ADMINISTRATION},
				auth);
		final LlistatIds ids = jbpmHelper.tascaFindByFiltre(
				expedient.getEntorn().getId(),
				(isPermisSupervision) ? null : auth.getName(),
				null,
				null,
				expedient.getId(),
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				true,
				true,
				false,
				0,
				-1,
				"dataCreacio",
				false,
				false);
		List<JbpmTask> tasks = jbpmHelper.findTasks(ids.getIds());
		for (JbpmTask task: tasks) {
			resposta.add(
					getExpedientTascaDto(
							task,
							expedient,
							true));
		}
		return resposta;
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
		dto.setOutcomes(jbpmHelper.findStartTaskOutcomes(jbpmId, startTaskName));
		dto.setTascaFormExternCodi(tasca.getFormExtern());
		return dto;
	}

	public String getTitolPerTasca(
			JbpmTask task,
			Tasca tasca) {
		String titol = null;
		if (tasca != null) {
			Map<String, Object> textPerCamps = new HashMap<String, Object>();
			titol = tasca.getNom();
			if (tasca.getNomScript() != null && tasca.getNomScript().length() > 0) {
				List<String> campsExpressio = getCampsExpressioTitol(tasca.getNomScript());
				Map<String, Object> valors = jbpmHelper.getTaskInstanceVariables(task.getId());
				valors.putAll(jbpmHelper.getProcessInstanceVariables(task.getProcessInstanceId()));
				for (String campCodi: campsExpressio) {
					Set<Camp> campsDefinicioProces = tasca.getDefinicioProces().getCamps();
					for (Camp camp: campsDefinicioProces) {
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
					titol = (String)jbpmHelper.evaluateExpression(
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
		JbpmTask task = jbpmHelper.getTaskById(String.valueOf(taskId));
		setTascaCache(task, null);
	}

	public Tasca findTascaByJbpmTaskId(
			String jbpmTaskId) {
		return findTascaByJbpmTask(jbpmHelper.getTaskById(jbpmTaskId));
	}
	public Tasca findTascaByJbpmTask(
			JbpmTask task) {
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
		List<JbpmTask> tasks = jbpmHelper.findTaskInstancesForProcessInstance(processInstanceId);
		for (JbpmTask task: tasks) {
			if (!task.isCompleted()) {
				ExpedientTascaDto tasca = getExpedientTascaDto(task, expedient, true);
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

	public List<ExpedientTascaDto> findTasquesPerExpedientPerInstanciaProces(
			Expedient expedient,
			String processInstanceId,
			boolean completed,
			boolean mostrarDeOtrosUsuarios) {
		List<ExpedientTascaDto> resposta = new ArrayList<ExpedientTascaDto>();
		List<JbpmTask> tasks = jbpmHelper.findTaskInstancesForProcessInstance(processInstanceId);
		for (JbpmTask task: tasks) {
			ExpedientTascaDto tasca = getExpedientTascaDto(
					task,
					expedient,
					true);
			if ((tasca.isCompleted() == completed) && (mostrarDeOtrosUsuarios || tasca.isAssignadaUsuariActual())) {
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

	public ExpedientTascaDto getExpedientTascaDto(
			JbpmTask task,
			Expedient expedient,
			boolean perTramitacio) {
		ExpedientTascaDto dto = new ExpedientTascaDto();
		dto.setId(task.getId());
		DadesCacheTasca dadesCacheTasca = getDadesCacheTasca(
				task,
				expedient);
		dto.setTitol(dadesCacheTasca.getTitol());
		dto.setJbpmName(task.getTaskName());
		dto.setDescription(task.getDescription());
		dto.setAssignee(task.getAssignee());
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
		dto.setTascaTramitacioMassiva(dadesCacheTasca.isTramitacioMassiva());
		Expedient expedientNoNull = expedient;
		if (expedientNoNull == null) {
			expedientNoNull = expedientHelper.findExpedientByProcessInstanceId(
					task.getProcessInstanceId());
		}
		if (perTramitacio) {
			// Opcional outcomes?
			dto.setOutcomes(jbpmHelper.findTaskInstanceOutcomes(task.getId()));
			// Opcional dades tasca?
			Tasca tasca = findTascaByJbpmTask(task);
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
				JbpmTask tascaDelegacio = null;
				if (original) {
					tascaDelegacio = jbpmHelper.getTaskById(delegationInfo.getTargetTaskId());
				} else {
					tascaDelegacio = jbpmHelper.getTaskById(delegationInfo.getSourceTaskId());
				}			
				dto.setDelegacioPersona(
						pluginHelper.personaFindAmbCodi(tascaDelegacio.getAssignee()));
			}
			DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(task.getProcessDefinitionId());
			if (definicioProces != null) {
				dto.setDefinicioProcesId(definicioProces.getId());
			}
			permisosHelper.omplirControlPermisosSegonsUsuariActual(
					expedientNoNull.getTipus().getId(),
					dto,
					ExpedientTipus.class);
		}
		dto.setAgafada(task.isAgafada());
		dto.setProcessInstanceId(task.getProcessInstanceId());
		dto.setExpedientId(expedientNoNull.getId());
		dto.setExpedientIdentificador(expedientNoNull.getIdentificador());
		dto.setExpedientTipusNom(expedientNoNull.getTipus().getNom());
		if (task.getAssignee() != null) {
			dto.setResponsable(
					pluginHelper.personaFindAmbCodi(task.getAssignee()));
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (task.getAssignee() != null) {
			if (auth != null) {
				dto.setAssignadaUsuariActual(task.getAssignee().equals(auth.getName()));
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
			dto.setResponsables(responsables);
		}
		return dto;
	}

	public void validarTasca(String taskId) {
		jbpmHelper.setTaskInstanceVariable(
				taskId,
				JbpmVars.VAR_TASCA_VALIDADA,
				new Date());
	}
	public void restaurarTasca(String taskId) {
		jbpmHelper.deleteTaskInstanceVariable(
				taskId,
				JbpmVars.VAR_TASCA_VALIDADA);
	}

	public boolean isTascaValidada(Object task) {
		Tasca tasca = findTascaByJbpmTask((JbpmTask)task);
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
		Object valor = jbpmHelper.getTaskInstanceVariable(
				((JbpmTask)task).getId(),
				JbpmVars.VAR_TASCA_VALIDADA);
		if (valor == null || !(valor instanceof Date))
			return false;
		return true;
	}
	public boolean isDocumentsComplet(Object task) {
		boolean ok = true;
		Tasca tasca = findTascaByJbpmTask((JbpmTask)task);
		for (DocumentTasca docTasca: tasca.getDocuments()) {
			if (docTasca.isRequired()) {
				String codiJbpm = JbpmVars.PREFIX_DOCUMENT + docTasca.getDocument().getCodi();
				Object valor = jbpmHelper.getTaskInstanceVariable(
						((JbpmTask)task).getId(),
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
		Tasca tasca = findTascaByJbpmTask((JbpmTask)task);
		for (FirmaTasca firmaTasca: tasca.getFirmes()) {
			if (firmaTasca.isRequired()) {
				String codiJbpm = JbpmVars.PREFIX_SIGNATURA + firmaTasca.getDocument().getCodi();
				Object valor = jbpmHelper.getTaskInstanceVariable(((JbpmTask)task).getId(), codiJbpm);
				if (valor == null)
					ok = false;
			}
		}
		return ok;
	}

	public void processarCampsAmbDominiCacheActivat(
			JbpmTask task,
			Tasca tasca,
			Map<String, Object> variables) {
		List<CampTasca> campsTasca = campTascaRepository.findAmbTascaOrdenats(tasca.getId());
		for (CampTasca campTasca: campsTasca) {
			if (campTasca.getCamp().isDominiCacheText()) {
				Object campValor = variables.get(campTasca.getCamp().getCodi());
				if (campValor != null) {
					if (	campTasca.getCamp().getTipus().equals(TipusCamp.SELECCIO) ||
							campTasca.getCamp().getTipus().equals(TipusCamp.SUGGEST)) {
						String text = variableHelper.getTextPerCamp(
								campTasca.getCamp(), 
								campValor, null, 
								task.getId(), 
								task.getProcessInstanceId());
						variables.put(
								campTasca.getCamp().getCodi(),
								new DominiCodiDescripcio(
										(String)campValor,
										text));
					}
				}
			}
		}
	}

	public void createDelegationInfo(
			JbpmTask task,
			JbpmTask original,
			JbpmTask delegada,
			String comentari,
			boolean supervisada) {
		DelegationInfo info = new DelegationInfo();
		info.setSourceTaskId(original.getId());
		info.setTargetTaskId(delegada.getId());
		info.setStart(new Date());
		info.setComment(comentari);
		info.setSupervised(supervisada);
		jbpmHelper.setTaskInstanceVariable(
				task.getId(), 
				JbpmVars.VAR_TASCA_DELEGACIO,
				info);
	}
	public DelegationInfo getDelegationInfo(JbpmTask task) {
		return (DelegationInfo)jbpmHelper.getTaskInstanceVariable(
				task.getId(),
				JbpmVars.VAR_TASCA_DELEGACIO);
	}
	public void deleteDelegationInfo(JbpmTask task) {
		jbpmHelper.deleteTaskInstanceVariable(
				task.getId(),
				JbpmVars.VAR_TASCA_DELEGACIO);
	}

	private static final Logger logger = LoggerFactory.getLogger(TascaHelper.class);

}
