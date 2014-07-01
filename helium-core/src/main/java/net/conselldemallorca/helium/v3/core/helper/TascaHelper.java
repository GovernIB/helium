/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.dao.PluginPersonaDao;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.service.MesuresTemporalsHelper;
import net.conselldemallorca.helium.jbpm3.integracio.DelegationInfo;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto.TascaEstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto.TascaPrioritatDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.exception.TaskInstanceNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.v3.core.helper.DtoConverter.DadesCacheTasca;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.TascaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	@Resource
	TascaRepository tascaRepository;
	@Resource
	ExpedientRepository expedientRepository;
	@Resource(name="tascaServiceV3")
	private TascaService tascaService;
	@Resource
	DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private VariableHelper variableHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource(name="serviceUtilsV3")
	private ServiceUtils serviceUtils;
	@Resource
	private DominiHelper dominiHelper;
	@Resource
	private EnumeracioValorsHelper enumeracioValorsHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource(name="dtoConverterV3")
	private DtoConverter dtoConverter;
	@Resource
	private PluginPersonaDao pluginPersonaDao;

	public JbpmTask getTascaComprovantAcces(
			String tascaId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		JbpmTask task = jbpmHelper.getTaskById(tascaId);
		if (task == null) {
			logger.debug("No s'ha trobat la tasca (id=" + tascaId + ")");
			throw new TaskInstanceNotFoundException();
		}
		if (task.getAssignee() == null || !task.getAssignee().equals(auth.getName())) {
			logger.debug("La persona no té la tasca assignada (id=" + tascaId + ", personaCodi=" + auth.getName() + ")");
			throw new TaskInstanceNotFoundException();
		}
		return task;
	}
	
	public JbpmTask getTascaComprovantExpedient(
			String tascaId,
			Expedient expedient) {
		JbpmTask task = jbpmHelper.getTaskById(tascaId);
		if (task == null) {
			logger.debug("No s'ha trobat la tasca (id=" + tascaId + ")");
			throw new TaskInstanceNotFoundException();
		}
		JbpmProcessInstance rootProcessInstance = jbpmHelper.getRootProcessInstance(
				task.getProcessInstanceId());
		if (!expedient.getProcessInstanceId().equals(rootProcessInstance.getId())) {
			logger.debug("La tasca no pertany a l'expedient (" +
					"tascaId=" + tascaId + ", " +
					"expedientId=" + expedient.getId() + ")");
			throw new TaskInstanceNotFoundException();
		}
		return task;
	}

	public List<ExpedientTascaDto> findTasquesPerExpedient(
			Expedient expedient) {
		List<ExpedientTascaDto> resposta = new ArrayList<ExpedientTascaDto>();
		List<JbpmTask> tasks = jbpmHelper.findTaskInstancesForProcessInstance(expedient.getProcessInstanceId());
		for (JbpmTask task: tasks)
			resposta.add(toExpedientTascaCompleteDto(task, expedient));
		return resposta;
	}

	public void createDadesTasca(Long taskId) {
		tascaService.createDadesTasca(taskId);
	}
	
	public ExpedientTascaDto getTascaPerExpedient(
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
	}

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
		List<ExpedientTascaDto> resposta = new ArrayList<ExpedientTascaDto>();
		List<JbpmTask> tasks = jbpmHelper.findTaskInstancesForProcessInstance(expedient.getProcessInstanceId());
		for (JbpmTask task: tasks) {
			// Sólo las pendientes
			if (task.isOpen() && !task.isCancelled() && !task.isSuspended() && !task.isCompleted()) {
				resposta.add(toExpedientTascaCompleteDto(task, expedient));
			}
		}
		return resposta;
	}
	
	public ExpedientTascaDto toExpedientTascaCompleteDto(
			JbpmTask task,
			Expedient expedient) {
		ExpedientTascaDto dto = getExpedientTascaLlistatDto(task, expedient);

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
		
		dto.setFormExtern(tasca.getFormExtern());		
		
		dto.setDefinicioProces(conversioTipusHelper.convertir(tasca.getDefinicioProces(), DefinicioProcesDto.class));
		
		dto.setDocumentsComplet(tascaService.isDocumentsComplet(task));
		dto.setSignaturesComplet(tascaService.isSignaturesComplet(task));
		
		dto.setOutcomes(jbpmHelper.findTaskInstanceOutcomes(task.getId()));

		Map<String, Object> valors = jbpmHelper.getTaskInstanceVariables(task.getId());
		
		DelegationInfo delegationInfo = (DelegationInfo)valors.get(
				TascaService.VAR_TASCA_DELEGACIO);
		
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
	
	public ExpedientTascaDto getExpedientTascaLlistatDto(JbpmTask task) {
		return getExpedientTascaLlistatDto(task, null);
	}
	
	public ExpedientTascaDto getExpedientTascaLlistatDto(
			JbpmTask task, Expedient expedient) {
		ExpedientTascaDto dto = new ExpedientTascaDto();
		dto.setId(task.getId());
		
		if (expedient == null) {
			expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		}
		
		DadesCacheTasca dadesCacheTasca = dtoConverter.getDadesCacheTasca(task, expedient);
		
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
				
		return dto;
	}

	private static final Logger logger = LoggerFactory.getLogger(TascaHelper.class);
}
