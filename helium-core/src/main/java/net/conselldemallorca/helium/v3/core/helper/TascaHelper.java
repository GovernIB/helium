/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.FirmaTasca;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.service.DocumentHelper;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.jbpm3.integracio.DelegationInfo;
import net.conselldemallorca.helium.jbpm3.integracio.DominiCodiDescripcio;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.jbpm3.integracio.LlistatIds;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TaskInstanceNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.AdminService;
import net.conselldemallorca.helium.v3.core.repository.CampTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.TascaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Helper per a gestionar les tasques dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class TascaHelper {

	public static final String VAR_TASCA_VALIDADA = "H3l1um#tasca.validada";
	public static final String VAR_TASCA_DELEGACIO = "H3l1um#tasca.delegacio";

	@Resource
	private TascaRepository tascaRepository;
	@Resource
	private AdminService adminService;
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
			if (expedient == null)
				expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
			DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(
					task.getProcessDefinitionId());
			Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProces(
					task.getName(),
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
		dadesCache = new DadesCacheTasca(
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
		return dadesCache;
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
		/*List<JbpmTask> tasks = jbpmHelper.findTaskInstancesForProcessInstance(
				expedient.getProcessInstanceId());*/
		List<JbpmProcessInstance> pis = jbpmHelper.getProcessInstanceTree(
				expedient.getProcessInstanceId());
		List<Long> idsPIExpedients = new ArrayList<Long>();
		for (JbpmProcessInstance pi: pis) {
			idsPIExpedients.add(new Long(pi.getId()));
		}
		PaginacioParamsDto paginacioParams = new PaginacioParamsDto();
		paginacioParams.afegirOrdre(
				"dataCreacio",
				OrdreDireccioDto.DESCENDENT);
		paginacioParams.setPaginaNum(0);
		paginacioParams.setPaginaTamany(-1);
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
		final LlistatIds ids = jbpmHelper.findListTasks(
				auth.getName(),
				(isPermisSupervision) ? null : auth.getName(), 
				null,
				null,
				idsPIExpedients, 
				null, 
				null,
				null, 
				null, 
				null, 
				paginacioParams,
				false,
				true,
				true,
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
			titol = task.getName();
		}
		return titol;
	}

	public void createDadesTasca(Long taskId) {
		JbpmTask task = jbpmHelper.getTaskById(String.valueOf(taskId));
		Expedient expedientPerTasca = expedientHelper.findExpedientByProcessInstanceId(
				task.getProcessInstanceId());
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProcesJbpmId(
				task.getName(),
				task.getProcessDefinitionId());
		String titol = tasca.getNom();
		if (tasca.getNomScript() != null && tasca.getNomScript().length() > 0)
			titol = getTitolPerTasca(task, tasca);
		task.setFieldFromDescription(
				"entornId",
				expedientPerTasca.getEntorn().getId().toString());
		task.setFieldFromDescription(
				"titol",
				titol);
		task.setFieldFromDescription(
				"identificador",
				expedientPerTasca.getIdentificador());
		task.setFieldFromDescription(
				"identificadorOrdenacio",
				expedientPerTasca.getIdentificadorOrdenacio());
		task.setFieldFromDescription(
				"numeroIdentificador",
				expedientPerTasca.getNumeroIdentificador());
		task.setFieldFromDescription(
				"expedientTipusId",
				expedientPerTasca.getTipus().getId().toString());
		task.setFieldFromDescription(
				"expedientTipusNom",
				expedientPerTasca.getTipus().getNom());
		task.setFieldFromDescription(
				"processInstanceId",
				expedientPerTasca.getProcessInstanceId());
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

	public Tasca findTascaByJbpmTaskId(
			String jbpmTaskId) {
		return findTascaByJbpmTask(jbpmHelper.getTaskById(jbpmTaskId));
	}
	public Tasca findTascaByJbpmTask(
			JbpmTask task) {
		return tascaRepository.findByJbpmNameAndDefinicioProcesJbpmId(
				task.getName(),
				task.getProcessDefinitionId());
	}

	public List<ExpedientTascaDto> findTasquesPendentsPerExpedient(
			Expedient expedient,
			boolean permisosVerOtrosUsuarios,
			boolean nomesMeves,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup) {
		List<ExpedientTascaDto> resposta = new ArrayList<ExpedientTascaDto>();
		List<JbpmTask> tasks = jbpmHelper.findTaskInstancesForProcessInstance(expedient.getProcessInstanceId());
		if (nomesMeves) {
			permisosVerOtrosUsuarios = false;
			nomesTasquesGrup = false;
		}
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
		dto.setJbpmName(task.getName());
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
				dto.setDelegacioPersona(adminService.findPersonaAmbCodi(tascaDelegacio.getAssignee()));
			}
			DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(task.getProcessDefinitionId());
			if (definicioProces != null) {
				dto.setDefinicioProcesId(definicioProces.getId());
			}
		}
		dto.setAgafada(task.isAgafada());
		dto.setProcessInstanceId(task.getProcessInstanceId());
		Expedient expedientNoNull = expedient;
		if (expedientNoNull == null) {
			expedientNoNull = expedientHelper.findExpedientByProcessInstanceId(
					task.getProcessInstanceId());
		}
		dto.setExpedientId(expedientNoNull.getId());
		dto.setExpedientIdentificador(expedientNoNull.getIdentificador());
		dto.setExpedientTipusNom(expedientNoNull.getTipus().getNom());
		if (task.getAssignee() != null) {
			dto.setResponsable(
					adminService.findPersonaAmbCodi(task.getAssignee()));
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (task.getPooledActors() != null && task.getPooledActors().size() > 0) {
			List<PersonaDto> responsables = new ArrayList<PersonaDto>();
			for (String pooledActor: task.getPooledActors()) {
				PersonaDto persona = adminService.findPersonaAmbCodi(pooledActor);
				if (persona != null) {
					if (auth.getName().equals(pooledActor))
						dto.setAssignadaUsuariActual(true);
					responsables.add(persona);
				}
			}
			dto.setResponsables(responsables);
		}
		if (auth != null && task.getPooledActors().isEmpty() && task.getAssignee() != null) {
			dto.setAssignadaUsuariActual(task.getAssignee().equals(auth.getName()));
		} 
		return dto;
	}

	public void validarTasca(String taskId) {
		jbpmHelper.setTaskInstanceVariable(
				taskId,
				VAR_TASCA_VALIDADA,
				new Date());
	}
	public void restaurarTasca(String taskId) {
		jbpmHelper.deleteTaskInstanceVariable(
				taskId,
				VAR_TASCA_VALIDADA);
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
				VAR_TASCA_VALIDADA);
		if (valor == null || !(valor instanceof Date))
			return false;
		return true;
	}
	public boolean isDocumentsComplet(Object task) {
		boolean ok = true;
		Tasca tasca = findTascaByJbpmTask((JbpmTask)task);
		for (DocumentTasca docTasca: tasca.getDocuments()) {
			if (docTasca.isRequired()) {
				String codiJbpm = DocumentHelper.PREFIX_VAR_DOCUMENT + docTasca.getDocument().getCodi();
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
				String codiJbpm = DocumentHelper.PREFIX_SIGNATURA + firmaTasca.getDocument().getCodi();
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

	public void filtreVariablesReadOnlyOcult(Tasca tasca, Map<String, Object> variables) {
		for (CampTasca campTasca: tasca.getCamps()) {
			if (variables.containsKey(campTasca.getCamp().getCodi())) {
				if (campTasca.isReadOnly() || campTasca.getCamp().isOcult())
					variables.remove(campTasca.getCamp().getCodi());
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
				TascaHelper.VAR_TASCA_DELEGACIO,
				info);
	}
	public DelegationInfo getDelegationInfo(JbpmTask task) {
		return (DelegationInfo)jbpmHelper.getTaskInstanceVariable(
				task.getId(),
				VAR_TASCA_DELEGACIO);
	}
	public void deleteDelegationInfo(JbpmTask task) {
		jbpmHelper.deleteTaskInstanceVariable(
				task.getId(),
				VAR_TASCA_DELEGACIO);
	}

	private static final Logger logger = LoggerFactory.getLogger(TascaHelper.class);
}
