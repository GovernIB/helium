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

import net.conselldemallorca.helium.core.model.dao.PluginPersonaDao;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.FirmaTasca;
import net.conselldemallorca.helium.core.model.hibernate.Registre;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.service.DocumentHelper;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.jbpm3.integracio.DelegationInfo;
import net.conselldemallorca.helium.jbpm3.integracio.DominiCodiDescripcio;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.jbpm3.integracio.LlistatIds;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto.TascaEstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto.TascaPrioritatDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TaskInstanceNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.v3.core.helper.DtoConverter.DadesCacheTasca;
import net.conselldemallorca.helium.v3.core.repository.CampTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.RegistreRepository;
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

	//private static final String VAR_PREFIX = "H3l1um#";

	public static final String VAR_TASCA_VALIDADA = "H3l1um#tasca.validada";
	public static final String VAR_TASCA_DELEGACIO = "H3l1um#tasca.delegacio";

	//private static final String DEFAULT_SECRET_KEY = "H3l1umKy";
	//private static final String DEFAULT_ENCRYPTION_SCHEME = "DES/ECB/PKCS5Padding";
	//private static final String DEFAULT_KEY_ALGORITHM = "DES";

	@Resource
	TascaRepository tascaRepository;
	@Resource(name="tascaServiceV3")
	private TascaService tascaService;
	@Resource
	DefinicioProcesRepository definicioProcesRepository;
	@Resource
	RegistreRepository registreRepository;
	@Resource
	private VariableHelper variableHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private ExpedientLoggerHelper expedientLoggerHelper;
	@Resource
	private CampTascaRepository campTascaRepository;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource(name = "permisosHelperV3")
	private PermisosHelper permisosHelper;
	@Resource(name="dtoConverterV3")
	private DtoConverter dtoConverter;
	@Resource
	private PluginPersonaDao pluginPersonaDao;



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
				(isPermisSupervision) ? null : auth.getName(), 
				null,
				idsPIExpedients, 
				null, 
				null,
				null, 
				null, 
				null, 
				paginacioParams,
				true,
				true,
				false);
		List<JbpmTask> tasks = jbpmHelper.findTasks(ids.getIds());
		for (JbpmTask task: tasks) {
			resposta.add(
					toExpedientTascaCompleteDto(
							task,
							expedient));
		}
		return resposta;
	}

	public void createDadesTasca(Long taskId) {
		JbpmTask task = jbpmHelper.getTaskById(String.valueOf(taskId));
		Expedient expedientPerTasca = expedientHelper.findExpedientByProcessInstanceId(
				task.getProcessInstanceId());
		Tasca tasca = tascaRepository.findAmbActivityNameIProcessDefinitionId(
				task.getName(),
				task.getProcessDefinitionId());
		String titol = tasca.getNom();
		if (tasca.getNomScript() != null && tasca.getNomScript().length() > 0)
			titol = dtoConverter.getTitolPerTasca(task, tasca);
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

	/*public ExpedientTascaDto getTascaPerExpedient(
			Expedient expedient,
			String taskId,
			boolean comprovarExpedient,
			boolean comprovarUsuari) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		JbpmTask task = jbpmHelper.getTaskById(taskId);
		if (task != null) {
			if (comprovarExpedient) {
				JbpmProcessInstance processInstance = jbpmHelper.getRootProcessInstance(
						task.getProcessInstanceId());
				if (!processInstance.getId().equals(expedient.getProcessInstanceId())) {
					logger.debug("La tasca no pertany a l'expedient (expedientId=" + expedient.getId() + ", taskId=" + taskId + ")");
					throw new TaskInstanceNotFoundException();
				}
			}
			if (comprovarUsuari) {
				if (!auth.getName().equals(task.getAssignee())) {
					logger.debug("L'usuari no te la tasca assignada (expedientId=" + expedient.getId() + ", taskId=" + taskId + ", usuariAcces=" + auth.getName() + ", usuariTasca=" + task.getAssignee() + ")");
					throw new TaskInstanceNotFoundException();
				}
			}
			return toExpedientTascaCompleteDto(task, expedient);
		} else {
			logger.debug("No s'ha trobat la tasca (expedientId=" + expedient.getId() + ", taskId=" + taskId + ", usuariAcces=" + auth.getName() + ")");
			return null;
		}
	}*/

	public Tasca findTascaByJbpmTask(
			JbpmTask task) {
		return tascaRepository.findAmbActivityNameIProcessDefinitionId(
				task.getName(),
				task.getProcessDefinitionId());
	}

	/*public Tasca findAmbActivityNameIProcessDefinitionId(
			String name,
			String processDefinitionId) {
		return tascaRepository.findAmbActivityNameIProcessDefinitionId(name, processDefinitionId);
	}

	public ExpedientTascaDto getTascaPerExpedientId(Long expedientId, String tascaId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		JbpmTask task = jbpmHelper.getTaskById(tascaId);
		Expedient expedient = expedientRepository.findById(expedientId);
		if (task != null) {
			return dtoConverter.toExpedientTascaDto(task, expedient);
		} else {
			logger.debug("No s'ha trobat la tasca (expedientId=" + expedient.getId() + ", tascaId=" + tascaId + ", usuariAcces=" + auth.getName() + ")");
			return null;
		}
	}*/

	public List<ExpedientTascaDto> findTasquesPendentsPerExpedient(Expedient expedient) {
		return findTasquesPerExpedientPerInstanciaProces(expedient, expedient.getProcessInstanceId());
	}

	public List<ExpedientTascaDto> findTasquesPerExpedientPerInstanciaProces(Expedient expedient, String processInstanceId) {
		List<ExpedientTascaDto> resposta = new ArrayList<ExpedientTascaDto>();
		List<JbpmTask> tasks = jbpmHelper.findTaskInstancesForProcessInstance(processInstanceId);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		for (JbpmTask task: tasks) {
			ExpedientTascaDto tasca = toExpedientTascaCompleteDto(task, expedient);
			if (tasca.isAssignadaPersonaAmbCodi(auth.getName())) {
				resposta.add(tasca);
			}
		}
		return resposta;
	}

	public ExpedientTascaDto toExpedientTascaCompleteDto(
			JbpmTask task,
			Expedient expedient) {
		ExpedientTascaDto dto = getExpedientTascaDto(task, expedient);

		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(
				task.getProcessDefinitionId());
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProces(
				task.getName(),
				definicioProces);
		if (task.isCacheActiu()) {
			dto.setNom(task.getFieldFromDescription("titol"));
		} else {
			if (tasca != null)
				dto.setNom(tasca.getNom());
			else
				dto.setNom(task.getName());
		}

		List<String> outcomes = jbpmHelper.findTaskInstanceOutcomes(
				task.getId());
		if (outcomes != null && !outcomes.isEmpty()) {
			if (outcomes.size() == 1) {
				String primeraTransicio = outcomes.get(0);
				dto.setTransicioPerDefecte(primeraTransicio == null || "".equals(primeraTransicio));
			} else {
				dto.setTransicions(outcomes);
			}
		}
		dto.setExpedientId(expedient.getId());
		dto.setExpedientIdentificador(expedient.getIdentificador());
		dto.setExpedientTipusNom(expedient.getTipus().getNom());
		dto.setProcessInstanceId(task.getProcessInstanceId());
		
		if(tasca != null)
			dto.setFormExtern(tasca.getFormExtern());		
		
		dto.setDefinicioProces(conversioTipusHelper.convertir(tasca.getDefinicioProces(), DefinicioProcesDto.class));
		
		dto.setDocumentsComplet(isDocumentsComplet(task));
		dto.setSignaturesComplet(isSignaturesComplet(task));
		dto.setTramitacioMassiva(tasca.isTramitacioMassiva());
		
		dto.setOutcomes(jbpmHelper.findTaskInstanceOutcomes(task.getId()));
		
		DelegationInfo delegationInfo = getDelegationInfo(task);
		
		if (delegationInfo != null) {
			boolean original = task.getId().equals(delegationInfo.getSourceTaskId());
			dto.setDelegada(true);
			dto.setDelegacioOriginal(original);
			dto.setDelegacioData(delegationInfo.getStart());
			dto.setDelegacioSupervisada(delegationInfo.isSupervised());
			dto.setDelegacioComentari(delegationInfo.getComment());
			JbpmTask tascaDelegacio = null;
			if (original) {
				tascaDelegacio = jbpmHelper.getTaskById(delegationInfo.getTargetTaskId());
			} else {
				tascaDelegacio = jbpmHelper.getTaskById(delegationInfo.getSourceTaskId());
			}			
			
			dto.setDelegacioPersona(conversioTipusHelper.convertir(pluginPersonaDao.findAmbCodiPlugin(tascaDelegacio.getAssignee()), PersonaDto.class));
		}
				
		return dto;
	}

	public ExpedientTascaDto getExpedientTascaCompleteDto(JbpmTask task) {
		ExpedientTascaDto dto = getExpedientTascaDto(task, null);
		dto.setOutcomes(jbpmHelper.findTaskInstanceOutcomes(task.getId()));
		DelegationInfo delegationInfo = getDelegationInfo(task);
		if (delegationInfo != null) {
			boolean original = task.getId().equals(delegationInfo.getSourceTaskId());
			dto.setDelegada(true);
			dto.setDelegacioOriginal(original);
			dto.setDelegacioData(delegationInfo.getStart());
			dto.setDelegacioSupervisada(delegationInfo.isSupervised());
			dto.setDelegacioComentari(delegationInfo.getComment());
			JbpmTask tascaDelegacio = null;
			if (original) {
				tascaDelegacio = jbpmHelper.getTaskById(delegationInfo.getTargetTaskId());
			} else {
				tascaDelegacio = jbpmHelper.getTaskById(delegationInfo.getSourceTaskId());
			}			
			
			dto.setDelegacioPersona(conversioTipusHelper.convertir(pluginPersonaDao.findAmbCodiPlugin(tascaDelegacio.getAssignee()), PersonaDto.class));
		}
		return dto;
	}
	
	public ExpedientTascaDto getExpedientTascaCacheDto(JbpmTask task, DadesCacheTasca dadesCacheTasca, boolean complete) {
		ExpedientTascaDto dto = new ExpedientTascaDto();
		dto.setId(task.getId());
		dto.setTramitacioMassiva(dadesCacheTasca.isTramitacioMassiva());
		dto.setTitol(dadesCacheTasca.getTitol());
		dto.setDescripcio(task.getDescription());
		if (task.isCancelled()) {
			dto.setEstat(TascaEstatDto.CANCELADA);
		} else if (task.isSuspended()) {
			dto.setEstat(TascaEstatDto.SUSPESA);
		} else {
			if (task.isCompleted())
				dto.setEstat(TascaEstatDto.FINALITZADA);
			else
				dto.setEstat(TascaEstatDto.PENDENT);
		}
		dto.setDataLimit(task.getDueDate());
		dto.setDataCreacio(task.getCreateTime());
		dto.setDataInici(task.getStartTime());
		dto.setDataFi(task.getEndTime());
		
		if (complete) {
			if (task.getAssignee() != null) {
				dto.setResponsable(
						dtoConverter.getResponsableTasca(task.getAssignee()));
				dto.setResponsableCodi(task.getAssignee());
			}
			Set<String> pooledActors = task.getPooledActors();
			if (pooledActors != null && pooledActors.size() > 0) {
				List<PersonaDto> responsables = new ArrayList<PersonaDto>();
				for (String pooledActor: pooledActors)
					responsables.add(
							dtoConverter.getResponsableTasca(pooledActor));
				dto.setResponsables(responsables);
			}
		}
		switch (task.getPriority()) {
		case -2:
			dto.setPrioritat(TascaPrioritatDto.MOLT_BAIXA);
			break;
		case -1:
			dto.setPrioritat(TascaPrioritatDto.BAIXA);
			break;
		case 0:
			dto.setPrioritat(TascaPrioritatDto.NORMAL);
			break;
		case 1:
			dto.setPrioritat(TascaPrioritatDto.ALTA);
			break;
		case 2:
			dto.setPrioritat(TascaPrioritatDto.MOLT_ALTA);
			break;
		}
		dto.setOberta(task.isOpen());
		dto.setCancelada(task.isCancelled());
		dto.setSuspesa(task.isSuspended());
		dto.setCompleted(task.isCompleted());
		dto.setExpedientIdentificador(dadesCacheTasca.getIdentificador());
		dto.setExpedientTipusNom(dadesCacheTasca.getExpedientTipusNom());
		dto.setProcessInstanceId(task.getProcessInstanceId());
		dto.setAgafada(task.isAgafada());
		
		return dto;
	}

	public ExpedientTascaDto getExpedientTascaDto(JbpmTask task) {
		return getExpedientTascaDto(task, null);
	}
	public ExpedientTascaDto getExpedientTascaDto(
			JbpmTask task,
			Expedient expedient) {
		ExpedientTascaDto dto = new ExpedientTascaDto();
		dto.setId(task.getId());
		if (expedient == null) {
			expedient = expedientHelper.findExpedientByProcessInstanceId(
					task.getProcessInstanceId());
		}
		DadesCacheTasca dadesCacheTasca = dtoConverter.getDadesCacheTasca(
				task,
				expedient);
		dto.setTramitacioMassiva(dadesCacheTasca.isTramitacioMassiva());
		dto.setTitol(dadesCacheTasca.getTitol());
		dto.setDescripcio(task.getDescription());
		if (task.isCancelled()) {
			dto.setEstat(TascaEstatDto.CANCELADA);
		} else if (task.isSuspended()) {
			dto.setEstat(TascaEstatDto.SUSPESA);
		} else {
			if (task.isCompleted())
				dto.setEstat(TascaEstatDto.FINALITZADA);
			else
				dto.setEstat(TascaEstatDto.PENDENT);
		}
		dto.setDataLimit(task.getDueDate());
		dto.setDataCreacio(task.getCreateTime());
		dto.setDataInici(task.getStartTime());
		dto.setDataFi(task.getEndTime());
		if (task.getAssignee() != null) {
			dto.setResponsable(
					dtoConverter.getResponsableTasca(task.getAssignee()));
			dto.setResponsableCodi(task.getAssignee());
		}
		Set<String> pooledActors = task.getPooledActors();
		if (pooledActors != null && pooledActors.size() > 0) {
			List<PersonaDto> responsables = new ArrayList<PersonaDto>();
			for (String pooledActor: pooledActors)
				responsables.add(
						dtoConverter.getResponsableTasca(pooledActor));
			dto.setResponsables(responsables);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null)
			dto.setAssignadaPersona(dto.isAssignadaPersonaAmbCodi(auth.getName()));
		switch (task.getPriority()) {
		case -2:
			dto.setPrioritat(TascaPrioritatDto.MOLT_BAIXA);
			break;
		case -1:
			dto.setPrioritat(TascaPrioritatDto.BAIXA);
			break;
		case 0:
			dto.setPrioritat(TascaPrioritatDto.NORMAL);
			break;
		case 1:
			dto.setPrioritat(TascaPrioritatDto.ALTA);
			break;
		case 2:
			dto.setPrioritat(TascaPrioritatDto.MOLT_ALTA);
			break;
		}
		dto.setOberta(task.isOpen());
		dto.setCancelada(task.isCancelled());
		dto.setSuspesa(task.isSuspended());
		dto.setCompleted(task.isCompleted());
		dto.setExpedientId(expedient.getId());
		dto.setExpedientIdentificador(expedient.getIdentificador());
		dto.setExpedientTipusNom(expedient.getTipus().getNom());
		dto.setProcessInstanceId(task.getProcessInstanceId());
		dto.setAgafada(task.isAgafada());
		dto.setValidada(isTascaValidada(task));
		Tasca tasca = findTascaByJbpmTask((JbpmTask)task);
		if (tasca != null)
			dto.setFormExtern(tasca.getFormExtern());
		
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

	public ExpedientTascaDto guardarVariable(
			JbpmTask task,
			String variableCodi,
			Object variableValor) {
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(variableCodi,  variableValor);
		return guardarVariables(task, variables);
	}
	public ExpedientTascaDto guardarVariables(
			JbpmTask task,
			Map<String, Object> variables) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuari = auth.getName();
		expedientLoggerHelper.afegirLogExpedientPerTasca(
				task.getId(),
				ExpedientLogAccioTipus.TASCA_FORM_GUARDAR,
				null,
				usuari);
		boolean iniciada = task.getStartTime() == null;
		processarCampsAmbDominiCacheActivat(
				task,
				variables);
		jbpmHelper.startTaskInstance(task.getId());
		jbpmHelper.setTaskInstanceVariables(task.getId(), variables, false);
		ExpedientTascaDto tasca = getExpedientTascaDto(task);
		if (iniciada) {
			Registre registre = new Registre(
					new Date(),
					tasca.getExpedientId(),
					usuari,
					Registre.Accio.MODIFICAR,
					Registre.Entitat.TASCA,
					task.getId());
			registre.setMissatge("Iniciar tasca \"" + tasca.getTitol() + "\"");
			registreRepository.save(registre);
		}
		return tasca;
	}

	public void processarCampsAmbDominiCacheActivat(
			JbpmTask task,
			Map<String, Object> variables) {
		Tasca tasca = findTascaByJbpmTask(task);
		List<CampTasca> campsTasca = campTascaRepository.findAmbTascaOrdenats(tasca.getId());
		for (CampTasca campTasca: campsTasca) {
			if (campTasca.getCamp().isDominiCacheText()) {
				Object campValor = variables.get(campTasca.getCamp().getCodi());
				if (campValor != null) {
					if (	campTasca.getCamp().getTipus().equals(TipusCamp.SELECCIO) ||
							campTasca.getCamp().getTipus().equals(TipusCamp.SUGGEST)) {
						String text = variableHelper.getTextVariableSimple(
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
