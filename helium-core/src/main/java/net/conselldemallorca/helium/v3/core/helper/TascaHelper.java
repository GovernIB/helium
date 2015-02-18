/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TaskInstanceNotFoundException;
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

	public static final String VAR_TASCA_VALIDADA = "H3l1um#tasca.validada";
	public static final String VAR_TASCA_DELEGACIO = "H3l1um#tasca.delegacio";

	@Resource
	TascaRepository tascaRepository;
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
					getExpedientTascaDto(
							task,
							expedient,
							true));
		}
		return resposta;
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

	public Tasca findTascaByJbpmTask(
			JbpmTask task) {
		return tascaRepository.findByJbpmNameAndDefinicioProcesJbpmId(
				task.getName(),
				task.getProcessDefinitionId());
	}

	public List<ExpedientTascaDto> findTasquesPendentsPerExpedient(
			Expedient expedient,
			boolean mostrarDeOtrosUsuarios,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup) {
		List<ExpedientTascaDto> resposta = new ArrayList<ExpedientTascaDto>();
		List<JbpmTask> tasks = jbpmHelper.findTaskInstancesForProcessInstance(expedient.getProcessInstanceId());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		for (JbpmTask task: tasks) {
			if (!task.isCompleted()) {
				ExpedientTascaDto tasca = getExpedientTascaDto(
						task,
						expedient,
						true);
				if (mostrarDeOtrosUsuarios || tasca.isAssignadaPersonaAmbCodi(auth.getName())) {
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
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		for (JbpmTask task: tasks) {
			ExpedientTascaDto tasca = getExpedientTascaDto(
					task,
					expedient,
					true);
			if ((tasca.isCompleted() == completed) && (mostrarDeOtrosUsuarios || tasca.isAssignadaPersonaAmbCodi(auth.getName()))) {
				resposta.add(tasca);
			}
		}
		return resposta;
	}

	/*public ExpedientTascaDto toExpedientTascaCompleteDto(
			JbpmTask task,
			Expedient expedient) {
		ExpedientTascaDto dto = getExpedientTascaDto(task, expedient);

		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(
				task.getProcessDefinitionId());
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProces(
				task.getName(),
				definicioProces);
		if (task.isCacheActiu()) {
			dto.setTitol(task.getFieldFromDescription("titol"));
		} else {
			if (tasca != null)
				dto.setTitol(tasca.getNom());
			else
				dto.setTitol(task.getName());
		}
		dto.setOutcomes(
				jbpmHelper.findTaskInstanceOutcomes(task.getId()));
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
		ExpedientTascaDto dto = getExpedientTascaDto(
				task,
				null,
				false);
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
	
	public ExpedientTascaDto getExpedientTascaCacheDto(
			JbpmTask task,
			DadesCacheTasca dadesCacheTasca) {
		ExpedientTascaDto dto = new ExpedientTascaDto();
		dto.setId(task.getId());
		dto.setTramitacioMassiva(dadesCacheTasca.isTramitacioMassiva());
		dto.setTitol(dadesCacheTasca.getTitol());
		dto.setDescripcio(task.getDescription());
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
		dto.setOberta(task.isOpen());
		dto.setCancelada(task.isCancelled());
		dto.setSuspesa(task.isSuspended());
		dto.setCompleted(task.isCompleted());
		dto.setExpedientIdentificador(dadesCacheTasca.getIdentificador());
		dto.setExpedientTipusNom(dadesCacheTasca.getExpedientTipusNom());
		dto.setProcessInstanceId(task.getProcessInstanceId());
		dto.setAgafada(task.isAgafada());
		return dto;
	}*/

	public ExpedientTascaDto getExpedientTascaDto(
			JbpmTask task,
			Expedient expedient,
			boolean perTramitacio) {
		ExpedientTascaDto dto = new ExpedientTascaDto();
		dto.setId(task.getId());
		DadesCacheTasca dadesCacheTasca = dtoConverter.getDadesCacheTasca(
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
			dto.setTascaTramitacioMassiva(tasca.isTramitacioMassiva());
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
						conversioTipusHelper.convertir(
								pluginPersonaDao.findAmbCodiPlugin(
										tascaDelegacio.getAssignee()),
										PersonaDto.class));
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
					dtoConverter.getResponsableTasca(task.getAssignee()));
		}
		if (task.getPooledActors() != null && task.getPooledActors().size() > 0) {
			List<PersonaDto> responsables = new ArrayList<PersonaDto>();
			for (String pooledActor: task.getPooledActors())
				responsables.add(
						dtoConverter.getResponsableTasca(pooledActor));
			dto.setResponsables(responsables);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			dto.setAssignadaUsuariActual(task.getAssignee().equals(auth.getName()));
		} else {
			dto.setAssignadaUsuariActual(false);
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
		ExpedientTascaDto tasca = getExpedientTascaDto(
				task,
				null,
				false);
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
