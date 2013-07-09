/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto.TascaEstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto.TascaPrioritatDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.exception.TaskInstanceNotFoundException;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
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
			resposta.add(toTascaDto(task, expedient));
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
			return toTascaDto(task, expedient);
		} else {
			logger.debug("No s'ha trobat la tasca (expedientId=" + expedient.getId() + ", tascaId=" + tascaId + ", usuariAcces=" + auth.getName() + ")");
			return null;
		}
	}

	public DadesCacheTasca getDadesCacheTasca(
			JbpmTask task) {
		if (!task.isCacheActiu()) {
			Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(
					task.getProcessInstanceId());
			return getDadesCacheTasca(task, expedient);
		} else {
			return getDadesCacheTasca(task, null);
		}
	}
	public DadesCacheTasca getDadesCacheTasca(
			JbpmTask task,
			Expedient expedient) {
		DadesCacheTasca dadesCache = null;
		if (!task.isCacheActiu()) {
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



	private ExpedientTascaDto toTascaDto(
			JbpmTask task,
			Expedient expedient) {
		ExpedientTascaDto dto = new ExpedientTascaDto();
		dto.setId(task.getId());
		DadesCacheTasca dadesCacheTasca = getDadesCacheTasca(
				task,
				expedient);
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
					getResponsableTasca(task.getAssignee()));
			dto.setResponsableCodi(task.getAssignee());
		}
		Set<String> pooledActors = task.getPooledActors();
		if (pooledActors != null && pooledActors.size() > 0) {
			List<PersonaDto> responsables = new ArrayList<PersonaDto>();
			for (String pooledActor: pooledActors)
				responsables.add(
						getResponsableTasca(pooledActor));
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
		dto.setProcessInstanceId(task.getProcessInstanceId());
		return dto;
	}

	private PersonaDto getResponsableTasca(String responsableCodi) {
		try {
			PersonaDto persona = pluginHelper.findPersonaAmbCodi(responsableCodi);
			if (persona == null) {
				persona = new PersonaDto();
				persona.setCodi(responsableCodi);
			}
			return persona;
		} catch (Exception ex) {
			logger.error("Error al obtenir informació de la persona (codi=" + responsableCodi + ")", ex);
			PersonaDto persona = new PersonaDto();
			persona.setCodi(responsableCodi);
			return persona;
		}
	}

	private String getTitolPerTasca(
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
							ExpedientDadaDto expedientDada = variableHelper.getDadaPerInstanciaTasca(
									task.getId(),
									campCodi);
							if (expedientDada != null && expedientDada.getText() != null) {
								textPerCamps.put(
										campCodi,
										expedientDada.getText());
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

	private static final Logger logger = LoggerFactory.getLogger(TascaHelper.class);

}
