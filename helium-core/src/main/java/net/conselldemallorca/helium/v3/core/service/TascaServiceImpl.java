/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;
import net.conselldemallorca.helium.core.model.dao.RegistreDao;
import net.conselldemallorca.helium.core.model.exception.DominiException;
import net.conselldemallorca.helium.core.model.exception.IllegalStateException;
import net.conselldemallorca.helium.core.model.exception.NotFoundException;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.model.hibernate.FirmaTasca;
import net.conselldemallorca.helium.core.model.hibernate.Registre;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.model.service.DocumentHelper;
import net.conselldemallorca.helium.jbpm3.integracio.DelegationInfo;
import net.conselldemallorca.helium.jbpm3.integracio.DominiCodiDescripcio;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto.TipusDomini;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.SeleccioOpcioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.CampNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TaskInstanceNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.v3.core.helper.DominiHelper;
import net.conselldemallorca.helium.v3.core.helper.DtoConverter;
import net.conselldemallorca.helium.v3.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.v3.core.helper.ExpedientLoggerHelper;
import net.conselldemallorca.helium.v3.core.helper.ServiceUtils;
import net.conselldemallorca.helium.v3.core.helper.TascaHelper;
import net.conselldemallorca.helium.v3.core.helper.VariableHelper;
import net.conselldemallorca.helium.v3.core.repository.AlertaRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.CampTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioValorsRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.RegistreRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiIniciatRepository;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servei per gestionar terminis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("tascaServiceV3")
public class TascaServiceImpl implements TascaService {
	@Resource(name="dtoConverterV3")
	private DtoConverter dtoConverter;
	@Resource
	private EnumeracioValorsRepository enumeracioValorsRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private CampTascaRepository campTascaRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private RegistreRepository registreRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private TascaHelper tascaHelper;
	@Resource
	private VariableHelper variableHelper;
	@Resource
	private DominiHelper dominiHelper;
	@Resource(name="serviceUtilsV3")
	private ServiceUtils serviceUtils;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private ExpedientLoggerHelper expedientLoggerHelper;
	@Resource
	private TerminiIniciatRepository terminiIniciatRepository;
	@Resource
	private AlertaRepository alertaRepository;
	@Resource
	private RegistreDao registreDao;

	@Transactional(readOnly = true)
	@Override
	public List<TascaDadaDto> findDadesPerTasca(
			String tascaId) {
		JbpmTask tasca = tascaHelper.getTascaComprovantAcces(tascaId);
		return variableHelper.findDadesPerInstanciaTasca(tasca);
	}

	@Transactional(readOnly = true)
	@Override
	public List<TascaDadaDto> findDadesPerTascaDto(ExpedientTascaDto tasca) {
		return variableHelper.findDadesPerInstanciaTascaDto(tasca);
	}
	
	@Transactional(readOnly = true)
	@Override
	public Object getVariable(
			Long entornId,
			String taskId,
			String codiVariable) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, true);
		return serviceUtils.getVariableJbpmTascaValor(task.getId(), codiVariable);
	}
	
	private JbpmTask comprovarSeguretatTasca(Long entornId, String taskId, String usuari, boolean comprovarAssignacio) {
		JbpmTask task = jbpmHelper.getTaskById(taskId);
		if (task == null) {
			throw new NotFoundException(
					serviceUtils.getMessage("error.tascaService.noTrobada"));
		}
		
		Long tascaEntornId = null;
		if (!task.isCacheActiu()) {
			Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
			tascaEntornId = expedient.getEntorn().getId();			
		} else {
			tascaEntornId = new Long(task.getFieldFromDescription("entornId"));
		}
		
		if (!tascaEntornId.equals(entornId)) {
			throw new NotFoundException(
					serviceUtils.getMessage("error.tascaService.noTrobada"));
		}
		if (comprovarAssignacio) {
			if (usuari == null) {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				if (!auth.getName().equals(task.getAssignee()))
					throw new NotFoundException(
							serviceUtils.getMessage("error.tascaService.noAssignada"));
			} else {
				if (!usuari.equals(task.getAssignee()))
					throw new NotFoundException(
							serviceUtils.getMessage("error.tascaService.noAssignada"));
			}
		}
		if (task.isSuspended()) {
			throw new IllegalStateException(
					serviceUtils.getMessage("error.tascaService.noDisponible"));
		}
		return task;
	}
	
	public boolean isDocumentsComplet(Object task) {
		boolean ok = true;
		Tasca tasca = tascaHelper.findAmbActivityNameIProcessDefinitionId(
				((JbpmTask)task).getName(),
				((JbpmTask)task).getProcessDefinitionId());
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
		Tasca tasca = tascaHelper.findAmbActivityNameIProcessDefinitionId(
				((JbpmTask)task).getName(),
				((JbpmTask)task).getProcessDefinitionId());
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
	
	@Transactional(readOnly = true)
	public boolean isTascaValidada(Object task) {
		Tasca tasca = tascaHelper.findAmbActivityNameIProcessDefinitionId(
				((JbpmTask)task).getName(),
				((JbpmTask)task).getProcessDefinitionId());
		boolean hiHaCampsModificables = false;
		for (CampTasca camp: tasca.getCamps()) {
			if (!camp.isReadOnly()) {
				hiHaCampsModificables = true;
				break;
			}
		}
		if (!hiHaCampsModificables)
			return true;
		Object valor = jbpmHelper.getTaskInstanceVariable(((JbpmTask)task).getId(), VAR_TASCA_VALIDADA);
		if (valor == null || !(valor instanceof Date))
			return false;
		return true;
	}

	@Transactional(readOnly = true)
	@Override
	public CampDto findCampTasca(Long campId) {
		return dtoConverter.toCampDto(campRepository.findOne(campId));
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<SeleccioOpcioDto> findOpcionsSeleccioPerCampTasca(
			String tascaId,
			Long campId) throws TaskInstanceNotFoundException, CampNotFoundException {
		JbpmTask tasca = tascaHelper.getTascaComprovantAcces(tascaId);
		Camp camp = campRepository.findOne(campId);
		if (camp == null)
			throw new CampNotFoundException();
		if (!tasca.getProcessDefinitionId().equals(camp.getDefinicioProces().getJbpmId()))
			throw new CampNotFoundException();
		List<SeleccioOpcioDto> resposta = new ArrayList<SeleccioOpcioDto>();
		if (camp.getDomini() != null) {
			DominiDto domini = new DominiDto();
			domini.setCacheSegons(camp.getDomini().getCacheSegons());
			domini.setTipus(new ModelMapper().map(camp.getDomini().getTipus(),TipusDomini.class));
			domini.setId(camp.getDomini().getId());
			domini.setSql(camp.getDomini().getSql());
			domini.setJndiDatasource(camp.getDomini().getJndiDatasource());

			try {
				List<FilaResultat> resultatConsultaDomini = dominiHelper.consultar(
						domini,
						camp.getDominiId(),
						variableHelper.getParamsConsulta(
								tascaId,
								tasca.getProcessInstanceId(),
								camp,
								null));

				for (FilaResultat filaResultat: resultatConsultaDomini) {
					if (filaResultat.getColumnes().size() > 1) {
						ParellaCodiValor codi = filaResultat.getColumnes().get(0);
						ParellaCodiValor valor = filaResultat.getColumnes().get(1);
						valor.setValor(((String) valor.getValor()).replaceAll("\\p{Cntrl}", "").trim());
						resposta.add(new SeleccioOpcioDto((String) codi.getValor(),(String) valor.getValor()));
					}					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (camp.getEnumeracio() != null) {
			List<EnumeracioValors> valors = enumeracioValorsRepository.findByEnumeracioOrderByOrdreAsc(
					camp.getEnumeracio());
			for (EnumeracioValors valor: valors) {
				resposta.add(
						new SeleccioOpcioDto(
								valor.getCodi(),
								valor.getNom()));
			}
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public ExpedientTascaDto getById(
			Long entornId,
			String taskId,
			String usuari,
			Map<String, Object> valorsCommand,
			boolean ambVariables,
			boolean ambTexts) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, usuari, true);
		ExpedientTascaDto resposta = dtoConverter.toExpedientTascaDto(task, valorsCommand, ambVariables, ambTexts, false, false, false);
		return resposta;
	}	

	@Transactional(readOnly = true)
	@Override
	public ExpedientTascaDto getByIdSenseComprovacio(String taskId) {
		return getByIdSenseComprovacio(taskId, null, null);
	}
	
	@Transactional(readOnly = true)
	@Override
	public ExpedientTascaDto getByIdSenseComprovacio(String taskId, Map<String, Object> valorsCommand) {
		return getByIdSenseComprovacio(taskId, null, valorsCommand);
	}
	
	@Transactional(readOnly = true)
	@Override
	public ExpedientTascaDto getByIdSenseComprovacio(String taskId, String usuari) {
		return getByIdSenseComprovacio(taskId, usuari, null);
	}
	
	@Transactional(readOnly = true)
	@Override
	public ExpedientTascaDto getByIdSenseComprovacio(String taskId, String usuari, Map<String, Object> valorsCommand) {
		JbpmTask task = jbpmHelper.getTaskById(taskId);
		return dtoConverter.toExpedientTascaDto(task, valorsCommand, true, true, false, false, false);
	}
	
	@Transactional(readOnly = true)
	@Override
	public ExpedientTascaDto getByIdSenseComprovacioIDades(String taskId) {
		JbpmTask task = jbpmHelper.getTaskById(taskId);
		return dtoConverter.toExpedientTascaDto(task, null, false, false, false, false, false);
	}	

	@Transactional
	@Override
	public ExpedientTascaDto guardarVariables(
			Long entornId,
			String taskId,
			Map<String, Object> variables,
			String usuari) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, usuari, true);
		expedientLoggerHelper.afegirLogExpedientPerTasca(
				taskId,
				ExpedientLogAccioTipus.TASCA_FORM_GUARDAR,
				null,
				usuari);
		boolean iniciada = task.getStartTime() == null;
		optimitzarConsultesDomini(task, variables);
		jbpmHelper.startTaskInstance(taskId);
		jbpmHelper.setTaskInstanceVariables(taskId, variables, false);
		ExpedientTascaDto tasca = dtoConverter.toExpedientTascaDto(task, null, true, true, false, false, false);
		if (iniciada) {
			if (usuari == null) {
				usuari = SecurityContextHolder.getContext().getAuthentication().getName();
			}
			
			Registre registre = new Registre(
					new Date(),
					tasca.getExpedient().getId(),
					usuari,
					Registre.Accio.MODIFICAR,
					Registre.Entitat.TASCA,
					taskId);
			registre.setMissatge("Iniciar tasca \"" + tasca.getNom() + "\"");
			
			registreRepository.save(registre);
		}
		return tasca;
	}

	@Transactional
	private void optimitzarConsultesDomini(
			JbpmTask task,
			Map<String, Object> variables) {
		Tasca tasca = tascaHelper.findAmbActivityNameIProcessDefinitionId(
				task.getName(),
				task.getProcessDefinitionId());
		List<CampTasca> campsTasca = campTascaRepository.findAmbTascaOrdenats(tasca.getId());
		for (CampTasca campTasca: campsTasca) {
			if (campTasca.getCamp().isDominiCacheText()) {
				Object campValor = variables.get(campTasca.getCamp().getCodi());
				if (campValor != null) {
					if (	campTasca.getCamp().getTipus().equals(TipusCamp.SELECCIO) ||
							campTasca.getCamp().getTipus().equals(TipusCamp.SUGGEST)) {
						CampDto campDto = dtoConverter.toCampDto(campTasca.getCamp());
						String text = dtoConverter.getCampText(
								task.getId(),
								null,
								campDto,
								campValor);
						variables.put(
								campDto.getCodi(),
								new DominiCodiDescripcio(
										(String)campValor,
										text));
					}
				}
			}
		}
	}
	
	@Transactional
	@Override
	public ExpedientTascaDto validar(
			Long entornId,
			String taskId,
			Map<String, Object> variables,
			boolean comprovarAssignacio) {
		return validar(entornId, taskId, variables, comprovarAssignacio, null);
	}
	
	@Transactional
	@Override
	public ExpedientTascaDto validar(
			Long entornId,
			String taskId,
			Map<String, Object> variables,
			boolean comprovarAssignacio,
			String usuari) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, usuari, comprovarAssignacio);
		expedientLoggerHelper.afegirLogExpedientPerTasca(
				taskId,
				ExpedientLogAccioTipus.TASCA_FORM_VALIDAR,
				null,
				usuari);
		optimitzarConsultesDomini(task, variables);
		jbpmHelper.startTaskInstance(taskId);
		jbpmHelper.setTaskInstanceVariables(taskId, variables, false);
		validarTasca(taskId);
		ExpedientTascaDto tasca = dtoConverter.toExpedientTascaDto(task, null, true, true, false, false, false);
		if (usuari == null)
			usuari = SecurityContextHolder.getContext().getAuthentication().getName();
		
		Registre registre = new Registre(
				new Date(),
				tasca.getExpedient().getId(),
				usuari,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.TASCA,
				taskId);
		registre.setMissatge("Validar \"" + tasca.getNom() + "\"");
		
		registreRepository.save(registre);
		return tasca;
	}

	@Transactional
	private void validarTasca(String taskId) {
		jbpmHelper.setTaskInstanceVariable(
				taskId,
				VAR_TASCA_VALIDADA,
				new Date());
	}

	@Transactional
	@Override
	public ExpedientTascaDto restaurar(
			Long entornId,
			String taskId) {
		return restaurar(entornId, taskId, null);
	}
	
	@Transactional
	@Override
	public ExpedientTascaDto restaurar(
			Long entornId,
			String taskId,
			String user) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, user, true);
		expedientLoggerHelper.afegirLogExpedientPerTasca(
				taskId,
				ExpedientLogAccioTipus.TASCA_FORM_RESTAURAR,
				null,
				user);
		if (!isTascaValidada(task))
			throw new IllegalStateException(
					serviceUtils.getMessage("error.tascaService.noValidada"));
		restaurarTasca(taskId);
		ExpedientTascaDto tasca = dtoConverter.toExpedientTascaDto(task, null, true, true, false, false, false);
		if (user == null) 
			user = SecurityContextHolder.getContext().getAuthentication().getName();
		
		Registre registre = new Registre(
				new Date(),
				tasca.getExpedient().getId(),
				user,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.TASCA,
				taskId);
		registre.setMissatge("Restaurar \"" + tasca.getNom() + "\"");
		
		registreRepository.save(registre);
		
		return tasca;
	}	
	
	private void restaurarTasca(String taskId) {
		jbpmHelper.deleteTaskInstanceVariable(taskId, VAR_TASCA_VALIDADA);
	}

	@Transactional
	@Override
	public void completar(
			Long entornId,
			String taskId,
			boolean comprovarAssignacio,
			String usuari) {
		completar(entornId, taskId, comprovarAssignacio, usuari, null);
	}
	
	@Transactional
	private Map<String, Object> getVariablesDelegacio(JbpmTask task) {
		return jbpmHelper.getTaskInstanceVariables(task.getId());
	}
	
	@Transactional
	@Override
	public void completar(
			Long entornId,
			String taskId,
			boolean comprovarAssignacio,
			String usuari,
			String outcome) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, usuari, comprovarAssignacio);
		if (!isTascaValidada(task))
			throw new IllegalStateException(
					serviceUtils.getMessage("error.tascaService.formNoValidat"));
		if (!isDocumentsComplet(task))
			throw new IllegalStateException(
					serviceUtils.getMessage("error.tascaService.faltenAdjuntar"));
		if (!isSignaturesComplet(task))
			throw new IllegalStateException(
					serviceUtils.getMessage("error.tascaService.faltenSignar"));
		JbpmProcessInstance pi = jbpmHelper.getRootProcessInstance(task.getProcessInstanceId());
		
		try {
			expedientLoggerHelper.afegirLogExpedientPerTasca(
					taskId,
					ExpedientLogAccioTipus.TASCA_COMPLETAR,
					outcome,
					usuari);
			jbpmHelper.startTaskInstance(taskId);
			jbpmHelper.endTaskInstance(task.getId(), outcome);
			// Accions per a una tasca delegada
			DelegationInfo delegationInfo = getDelegationInfo(task);
			if (delegationInfo != null) {
				if (!taskId.equals(delegationInfo.getSourceTaskId())) {
					// Copia les variables de la tasca delegada a la original
					jbpmHelper.setTaskInstanceVariables(
							delegationInfo.getSourceTaskId(),
							getVariablesDelegacio(task),
							false);
					JbpmTask taskOriginal = jbpmHelper.getTaskById(delegationInfo.getSourceTaskId());
					if (!delegationInfo.isSupervised()) {
						// Si no es supervisada també finalitza la tasca original
						completar(entornId, taskOriginal.getId(), false, null, outcome);
					}
					deleteDelegationInfo(taskOriginal);
				}
			}
			List<Expedient> expedients = expedientRepository.findByProcessInstanceId(pi.getId());
			
			for (Expedient expedient : expedients) {
				actualitzarTerminisIAlertes(taskId, expedient);
				verificarFinalitzacioExpedient(expedient, pi);
			}
			serviceUtils.expedientIndexLuceneUpdate(task.getProcessInstanceId());
			ExpedientTascaDto tasca = dtoConverter.toExpedientTascaDto(task, null, true, true, false, false, false);
			if (usuari == null)
				usuari = SecurityContextHolder.getContext().getAuthentication().getName();

			Registre registre = new Registre(
					new Date(),
					tasca.getExpedient().getId(),
					usuari,
					Registre.Accio.FINALITZAR,
					Registre.Entitat.TASCA,
					taskId);
			registre.setMissatge("Finalitzar \"" + tasca.getNom() + "\"");
			
			registreRepository.save(registre);
		} catch (Exception e) {
			throw new IllegalStateException(serviceUtils.getMessage("error.tascaService.noDisponible"));
		}
	}

	@Transactional
	private void verificarFinalitzacioExpedient(
			Expedient expedient,
			JbpmProcessInstance pi) {
		if (pi.getEnd() != null) {
			// Actualitzar data de fi de l'expedient
			expedient.setDataFi(pi.getEnd());
			// Finalitzar terminis actius
			for (TerminiIniciat terminiIniciat: terminiIniciatRepository.findByProcessInstanceId(pi.getId())) {
				if (terminiIniciat.getDataInici() != null) {
					terminiIniciat.setDataCancelacio(new Date());
					long[] timerIds = terminiIniciat.getTimerIdsArray();
					for (int i = 0; i < timerIds.length; i++)
						jbpmHelper.suspendTimer(
								timerIds[i],
								new Date(Long.MAX_VALUE));
				}
			}
		}
	}
	
	@Transactional
	@Override
	public void delegacioCancelar(
			Long entornId,
			String taskId) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, true);
		DelegationInfo delegationInfo = getDelegationInfo(task);
		if (delegationInfo == null || !taskId.equals(delegationInfo.getSourceTaskId())) {
			throw new IllegalStateException(
					serviceUtils.getMessage("error.tascaService.cancelarDelegacio"));
		}
		// Cancelar la tasca delegada
		jbpmHelper.cancelTaskInstance(delegationInfo.getTargetTaskId());
		// Esborram la delegació
		deleteDelegationInfo(task);
	}

	@Transactional
	private void actualitzarTerminisIAlertes(
			String taskId,
			Expedient expedient) {
		List<TerminiIniciat> terminisIniciats = terminiIniciatRepository.findByTaskInstanceId(taskId);
		for (TerminiIniciat terminiIniciat: terminisIniciats) {
			terminiIniciat.setDataCompletat(new Date());
			esborrarAlertesAntigues(terminiIniciat);
			if (terminiIniciat.getTermini().isAlertaCompletat()) {
				JbpmTask task = jbpmHelper.getTaskById(taskId);
				if (task.getAssignee() != null) {
					crearAlertaCompletat(terminiIniciat, task.getAssignee(), expedient);
				} else {
					for (String actor: task.getPooledActors())
						crearAlertaCompletat(terminiIniciat, actor, expedient);
				}
				terminiIniciat.setAlertaCompletat(true);
			}
		}
	}
	
	@Transactional
	private void crearAlertaCompletat(
			TerminiIniciat terminiIniciat,
			String destinatari,
			Expedient expedient) {
		Alerta alerta = new Alerta(
				new Date(),
				destinatari,
				Alerta.AlertaPrioritat.NORMAL,
				terminiIniciat.getTermini().getDefinicioProces().getEntorn());
		alerta.setExpedient(expedient);
		alerta.setTerminiIniciat(terminiIniciat);
		alertaRepository.save(alerta);
	}
	
	@Transactional
	private void esborrarAlertesAntigues(TerminiIniciat terminiIniciat) {
		List<Alerta> antigues = alertaRepository.findActivesAmbTerminiIniciatId(terminiIniciat.getId());
		for (Alerta antiga: antigues)
			antiga.setDataEliminacio(new Date());
	}
	
	@Transactional
	@Override
	public void esborrarRegistre(
			Long entornId,
			String taskId,
			String campCodi,
			int index) {
		esborrarRegistre(entornId, taskId, campCodi, index, null);
	}
	
	@Transactional
	private DelegationInfo getDelegationInfo(JbpmTask task) {
		return (DelegationInfo)jbpmHelper.getTaskInstanceVariable(
				task.getId(),
				VAR_TASCA_DELEGACIO);
	}
	
	@Transactional
	private void deleteDelegationInfo(JbpmTask task) {
		jbpmHelper.deleteTaskInstanceVariable(
				task.getId(),
				VAR_TASCA_DELEGACIO);
	}
	
	@Transactional
	@Override
	public void esborrarRegistre(
			Long entornId,
			String taskId,
			String campCodi,
			int index,
			String usuari) {
		JbpmTask task = jbpmHelper.getTaskById(taskId);
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(task.getProcessDefinitionId());
		Camp camp = campRepository.findByDefinicioProcesAndCodi(definicioProces, campCodi);
		if (camp.isMultiple()) {
			Object valor = jbpmHelper.getTaskInstanceVariable(taskId, campCodi);
			if (valor != null) {
				Object[] valorMultiple = (Object[])valor;
				if (valorMultiple.length > 0) {
					Object[] valorNou = new Object[valorMultiple.length - 1];
					for (int i = 0; i < valorNou.length; i++)
						valorNou[i] = (i < index) ? valorMultiple[i] : valorMultiple[i + 1];
					guardarVariable(
							entornId,
							taskId,
							campCodi,
							valorNou,
							usuari);
				}
			}
		} else {
			guardarVariable(
					entornId,
					taskId,
					campCodi,
					null,
					usuari);
		}
	}

	@Transactional
	@Override
	public ExpedientTascaDto guardarVariable(
			Long entornId,
			String taskId,
			String variable,
			Object valor) {
		return guardarVariable(entornId, taskId, variable, valor, null);
	}
	
	@Transactional
	@Override
	public ExpedientTascaDto guardarVariable(
			Long entornId,
			String taskId,
			String variable,
			Object valor,
			String usuari) {
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(variable, valor);
		return guardarVariables(entornId, taskId, variables, usuari);
	}
	
	@Transactional
	@Override
	public void borrarVariables(
			Long entornId,
			String taskId,
			String varName,
			String usuari) {
		comprovarSeguretatTasca(entornId, taskId, usuari, true);
		jbpmHelper.startTaskInstance(taskId);
		jbpmHelper.deleteTaskInstanceVariable(taskId, varName);
	}

	@Transactional
	@Override
	public void guardarRegistre(
			Long entornId,
			String taskId,
			String campCodi,
			Object[] valors) {
		guardarRegistre(
				entornId,
				taskId,
				campCodi,
				valors,
				-1,
				null);
	}
	
	@Transactional
	@Override
	public void guardarRegistre(
			Long entornId,
			String taskId,
			String campCodi,
			Object[] valors,
			int index) {
		guardarRegistre(
				entornId,
				taskId,
				campCodi,
				valors,
				index,
				null);
	}
	
	@Transactional
	@Override
	public void guardarRegistre(
			Long entornId,
			String taskId,
			String campCodi,
			Object[] valors,
			String usuari) {
		guardarRegistre(
				entornId,
				taskId,
				campCodi,
				valors,
				-1,
				usuari);
	}
	
	@Transactional
	@Override
	public void guardarRegistre(
			Long entornId,
			String taskId,
			String campCodi,
			Object[] valors,
			int index,
			String usuari) {
		JbpmTask task = jbpmHelper.getTaskById(taskId);
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(task.getProcessDefinitionId());
		Camp camp = campRepository.findByDefinicioProcesAndCodi(definicioProces, campCodi);
		if (camp.isMultiple()) {
			Object valor = jbpmHelper.getTaskInstanceVariable(taskId, campCodi);
			if (valor == null) {
				guardarVariable(
						entornId,
						taskId,
						campCodi,
						new Object[]{valors},
						usuari);
			} else {
				Object[] valorMultiple = (Object[])valor;
				if (index != -1) {
					valorMultiple[index] = valors;
					guardarVariable(
							entornId,
							taskId,
							campCodi,
							valor,
							usuari);
				} else {
					Object[] valorNou = new Object[valorMultiple.length + 1];
					for (int i = 0; i < valorMultiple.length; i++)
						valorNou[i] = valorMultiple[i];
					valorNou[valorMultiple.length] = valors;
					guardarVariable(
							entornId,
							taskId,
							campCodi,
							valorNou,
							usuari);
				}
			}
		} else {
			guardarVariable(
					entornId,
					taskId,
					campCodi,
					valors,
					usuari);
		}
	}

	@Transactional
	@Override
	public void executarAccio(
			Long entornId,
			String taskId,
			String accio) throws DominiException {
		executarAccio(entornId, taskId, accio, null);
	}
	
	@Transactional
	@Override
	public void executarAccio(
			Long entornId,
			String taskId,
			String accio,
			String user) throws DominiException {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, user, true);
		expedientLoggerHelper.afegirLogExpedientPerTasca(
				taskId,
				ExpedientLogAccioTipus.TASCA_ACCIO_EXECUTAR,
				accio,
				user);
		jbpmHelper.executeActionInstanciaTasca(taskId, accio);
		serviceUtils.expedientIndexLuceneUpdate(task.getProcessInstanceId());
	}

	@Transactional
	@Override
	public ExpedientTascaDto getTascaPerExpedientId(Long expedientId, String tascaId) {
		return tascaHelper.getTascaPerExpedientId(expedientId, tascaId);
	}

	@Transactional
	@Override
	public ExpedientTascaDto alliberar(Long entornId, String tascaId, boolean comprovarResponsable) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return alliberar(entornId, auth.getName(), tascaId, comprovarResponsable);
	}
	
	@Transactional
	private ExpedientTascaDto alliberar(
			Long entornId,
			String usuari,
			String taskId,
			boolean comprovarResponsable) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, false);
		if (comprovarResponsable) {
			if (!task.getAssignee().equals(usuari)) {
				throw new NotFoundException(
						serviceUtils.getMessage("error.tascaService.noAssignada"));
			}
		}
		String previousActors = expedientLoggerHelper.getActorsPerReassignacioTasca(taskId);
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerTasca(
				taskId,
				ExpedientLogAccioTipus.TASCA_REASSIGNAR,
				previousActors);
		jbpmHelper.releaseTaskInstance(taskId);
		task.setFieldFromDescription(TASKDESC_CAMP_AGAFADA, "false");
		serviceUtils.expedientIndexLuceneUpdate(task.getProcessInstanceId());
		String currentActors = expedientLoggerHelper.getActorsPerReassignacioTasca(taskId);
		expedientLog.setAccioParams(previousActors + "::" + currentActors);
		ExpedientTascaDto tasca = getByIdSenseComprovacio(taskId);
		registreDao.crearRegistreIniciarTasca(
				tasca.getExpedient().getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				"Amollar tasca \"" + tasca.getNom() + "\"");
		return tasca;
	}
}
