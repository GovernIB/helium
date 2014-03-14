/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.service.MesuresTemporalsHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.exception.TaskInstanceNotFoundException;
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
	@Resource(name="dtoConverterV3")
	private DtoConverter dtoConverter;

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
			resposta.add(dtoConverter.toExpedientTascaDto(task, expedient));
		return resposta;
	}

	public ExpedientTascaDto getTascaPerExpedient(
			Expedient expedient,
			String tascaId,
			boolean comprovarExpedient,
			boolean comprovarUsuari) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		JbpmTask task = jbpmHelper.getTaskById(tascaId);
		if (task != null) {
			if (comprovarExpedient) {
				JbpmProcessInstance processInstance = jbpmHelper.getRootProcessInstance(
						task.getProcessInstanceId());
				if (!processInstance.getId().equals(expedient.getProcessInstanceId())) {
					logger.debug("La tasca no pertany a l'expedient (expedientId=" + expedient.getId() + ", tascaId=" + tascaId + ")");
					throw new TaskInstanceNotFoundException();
				}
			}
			if (comprovarUsuari) {
				if (!auth.getName().equals(task.getAssignee())) {
					logger.debug("L'usuari no te la tasca assignada (expedientId=" + expedient.getId() + ", tascaId=" + tascaId + ", usuariAcces=" + auth.getName() + ", usuariTasca=" + task.getAssignee() + ")");
					throw new TaskInstanceNotFoundException();
				}
			}
			return dtoConverter.toExpedientTascaDto(task, expedient);
		} else {
			logger.debug("No s'ha trobat la tasca (expedientId=" + expedient.getId() + ", tascaId=" + tascaId + ", usuariAcces=" + auth.getName() + ")");
			return null;
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(TascaHelper.class);

	public Tasca findAmbActivityNameIProcessDefinitionId(String name, String processDefinitionId) {
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
	}

	public List<ExpedientTascaDto> findTasquesPendentsPerExpedient(Expedient expedient) {
	List<ExpedientTascaDto> resposta = new ArrayList<ExpedientTascaDto>();
	List<JbpmTask> tasks = jbpmHelper.findTaskInstancesForProcessInstance(expedient.getProcessInstanceId());
	for (JbpmTask task: tasks) {
		// Sólo las pendientes
		if (task.isOpen() && !task.isCancelled() && !task.isSuspended() && !task.isCompleted()) {
			resposta.add(dtoConverter.toExpedientTascaDto(task, expedient));
		}
	}
	return resposta;
}
}
