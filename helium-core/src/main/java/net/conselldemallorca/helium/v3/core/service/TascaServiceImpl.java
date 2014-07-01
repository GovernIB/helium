/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.dao.ExpedientDao;
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
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.FirmaTasca;
import net.conselldemallorca.helium.core.model.hibernate.Registre;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.model.service.DocumentHelper;
import net.conselldemallorca.helium.core.model.service.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.service.PermisosHelper;
import net.conselldemallorca.helium.core.model.service.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.jbpm3.integracio.DelegationInfo;
import net.conselldemallorca.helium.jbpm3.integracio.DominiCodiDescripcio;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.jbpm3.integracio.LlistatIds;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.SeleccioOpcioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDocumentDto;
import net.conselldemallorca.helium.v3.core.api.exception.CampNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EntornNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TascaNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TaskInstanceNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.v3.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.v3.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.v3.core.helper.DtoConverter;
import net.conselldemallorca.helium.v3.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.v3.core.helper.ExpedientLoggerHelper;
import net.conselldemallorca.helium.v3.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.v3.core.helper.ServiceUtils;
import net.conselldemallorca.helium.v3.core.helper.TascaHelper;
import net.conselldemallorca.helium.v3.core.helper.VariableHelper;
import net.conselldemallorca.helium.v3.core.repository.AlertaRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.CampTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.EntornRepository;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioValorsRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.RegistreRepository;
import net.conselldemallorca.helium.v3.core.repository.TascaRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiIniciatRepository;

import org.apache.commons.collections.comparators.NullComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.acls.model.Permission;
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
	private ExpedientDao expedientDao;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private CampRepository campRepository;
	@Resource
	private CampTascaRepository campTascaRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private TascaRepository tascaRepository;
	@Resource
	private RegistreRepository registreRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private TascaHelper tascaHelper;
	@Resource
	private EntornRepository entornRepository;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private VariableHelper variableHelper;
	@Resource(name="documentHelperV3")
	private DocumentHelperV3 documentHelper;
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
		JbpmTask task = tascaHelper.getTascaComprovantAcces(tascaId);
		return variableHelper.findDadesPerInstanciaTasca(task);
	}

	@Transactional(readOnly = true)
	@Override
	public List<TascaDocumentDto> findDocumentsPerTasca(
			String tascaId) {
		JbpmTask task = tascaHelper.getTascaComprovantAcces(tascaId);
		return documentHelper.findDocumentsPerInstanciaTasca(task);
	}

	@Transactional(readOnly = true)
	@Override
	public List<TascaDadaDto> findDadesPerTascaDto(ExpedientTascaDto tasca) {
		return variableHelper.findDadesPerInstanciaTascaDto(tasca);
	}
	
	@Transactional
	@Override
	public void createDadesTasca(Long taskId) {
		JbpmTask task = jbpmHelper.getTaskById(String.valueOf(taskId));
		if (task == null)
			throw new NotFoundException(
					serviceUtils.getMessage("error.tascaService.noTrobada"));
		createDadesTasca(task);
	}
	
	private void createDadesTasca(JbpmTask task) {
		String rootProcessInstanceId = jbpmHelper.getRootProcessInstance(task.getProcessInstanceId()).getId();
		Expedient expedientPerTasca = expedientDao.findAmbProcessInstanceId(rootProcessInstanceId);
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
			throw new TascaNotFoundException(
					serviceUtils.getMessage("error.tascaService.noTrobada"));
		}
		if (comprovarAssignacio) {
			if (usuari == null) {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				if (!auth.getName().equals(task.getAssignee()))
					throw new TascaNotFoundException(
							serviceUtils.getMessage("error.tascaService.noAssignada"));
			} else {
				if (!usuari.equals(task.getAssignee()))
					throw new TascaNotFoundException(
							serviceUtils.getMessage("error.tascaService.noAssignada"));
			}
		}
		if (task.isSuspended()) {
			throw new TascaNotFoundException(
					serviceUtils.getMessage("error.tascaService.noDisponible"));
		}
		return task;
	}

	public boolean isDocumentsComplet(Object task) {
		boolean ok = true;
		Tasca tasca = tascaHelper.findTascaByJbpmTask((JbpmTask)task);
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
		Tasca tasca = tascaHelper.findTascaByJbpmTask((JbpmTask)task);
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
		Tasca tasca = tascaHelper.findTascaByJbpmTask((JbpmTask)task);
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
		return new ConversioTipusHelper().convertir(campRepository.findOne(campId), CampDto.class);
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<SeleccioOpcioDto> findOpcionsSeleccioPerCampTasca(
			String tascaId,
			Long campId) throws TaskInstanceNotFoundException, CampNotFoundException {
		Camp camp = campRepository.findOne(campId);
		List<SeleccioOpcioDto> resposta = new ArrayList<SeleccioOpcioDto>();
		JbpmTask tasca = tascaHelper.getTascaComprovantAcces(tascaId);
		if (camp.getDominiId() != null) {
			try {
				List<ParellaCodiValorDto> parellaCodiValorDto = variableHelper.getTextVariablesSimpleFontExterna(
							camp,
							null,
							null,
							tascaId,
							tasca.getProcessInstanceId());
				for (ParellaCodiValorDto parella: parellaCodiValorDto) {
					resposta.add(new SeleccioOpcioDto(parella.getCodi(), (String) parella.getValor()));
				}
			} catch (Exception ex) {
				throw new DominiException("No s'ha pogut consultar el domini: " + camp.getDomini().getCodi(), ex);
			}
		} else if (camp.getEnumeracio() != null) {
			List<EnumeracioValors> valors = enumeracioValorsRepository.findByEnumeracioOrdenat(
					camp.getEnumeracio().getId());
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
			boolean ambTexts) throws TascaNotFoundException{
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, usuari, true);		
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		return tascaHelper.getTascaPerExpedient(expedient, taskId, true, true);
	}	

	@Transactional(readOnly = true)
	@Override
	public ExpedientTascaDto getByIdSenseComprovacio(String taskId) {
		JbpmTask task = jbpmHelper.getTaskById(taskId);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		return tascaHelper.getTascaPerExpedient(expedient, taskId, false, false);
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
		
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		ExpedientTascaDto tasca = tascaHelper.getTascaPerExpedient(expedient, taskId, false, false);
		if (iniciada) {
			if (usuari == null) {
				usuari = SecurityContextHolder.getContext().getAuthentication().getName();
			}
			
			Registre registre = new Registre(
					new Date(),
					tasca.getExpedientId(),
					usuari,
					Registre.Accio.MODIFICAR,
					Registre.Entitat.TASCA,
					taskId);
			registre.setMissatge("Iniciar tasca \"" + tasca.getTitol() + "\"");
			
			registreRepository.save(registre);
		}
		return tasca;
	}

	@Transactional
	private void optimitzarConsultesDomini(
			JbpmTask task,
			Map<String, Object> variables) {
		Tasca tasca = tascaHelper.findTascaByJbpmTask(task);
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
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		ExpedientTascaDto tasca = tascaHelper.getTascaPerExpedient(expedient, taskId, false, false);
		if (usuari == null)
			usuari = SecurityContextHolder.getContext().getAuthentication().getName();
		
		Registre registre = new Registre(
				new Date(),
				tasca.getExpedientId(),
				usuari,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.TASCA,
				taskId);
		registre.setMissatge("Validar \"" + tasca.getTitol() + "\"");
		
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
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		ExpedientTascaDto tasca = tascaHelper.getTascaPerExpedient(expedient, taskId, false, false);
		if (user == null) 
			user = SecurityContextHolder.getContext().getAuthentication().getName();
		
		Registre registre = new Registre(
				new Date(),
				tasca.getExpedientId(),
				user,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.TASCA,
				taskId);
		registre.setMissatge("Restaurar \"" + tasca.getTitol() + "\"");
		
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
			if (usuari == null)
				usuari = SecurityContextHolder.getContext().getAuthentication().getName();
			
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
			Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
			ExpedientTascaDto tasca = tascaHelper.getTascaPerExpedient(expedient, taskId, false, false);

			Registre registre = new Registre(
					new Date(),
					expedient.getId(),
					usuari,
					Registre.Accio.FINALITZAR,
					Registre.Entitat.TASCA,
					taskId);
			registre.setMissatge("Finalitzar \"" + tasca.getTitol() + "\"");
			
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
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		JbpmTask task = jbpmHelper.getTaskById(tascaId);
		Expedient expedient = expedientRepository.findById(expedientId);
		if (task != null) {
			return tascaHelper.getTascaPerExpedient(expedient, task.getId(), false, false);
		} else {
			logger.debug("No s'ha trobat la tasca (expedientId=" + expedient.getId() + ", tascaId=" + tascaId + ", usuariAcces=" + auth.getName() + ")");
			return null;
		}
	}
	
	@Transactional
	@Override
	public ExpedientTascaDto agafar(Long entornId, String taskId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return agafar(entornId, auth.getName(), taskId);
	}
	
	@Transactional
	private ExpedientTascaDto agafar(Long entornId, String usuari, String taskId) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, false);
		String previousActors = expedientLoggerHelper.getActorsPerReassignacioTasca(taskId);
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerTasca(
				taskId,
				ExpedientLogAccioTipus.TASCA_REASSIGNAR,
				previousActors);
		jbpmHelper.takeTaskInstance(taskId, usuari);
		serviceUtils.expedientIndexLuceneUpdate(task.getProcessInstanceId());
		String currentActors = expedientLoggerHelper.getActorsPerReassignacioTasca(taskId);
		expedientLog.setAccioParams(previousActors + "::" + currentActors);
		ExpedientTascaDto tasca = getByIdSenseComprovacio(taskId);
		registreDao.crearRegistreIniciarTasca(
				tasca.getExpedientId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				"Agafar tasca \"" + tasca.getTitol() + "\"");
		return tasca;
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
		serviceUtils.expedientIndexLuceneUpdate(task.getProcessInstanceId());
		String currentActors = expedientLoggerHelper.getActorsPerReassignacioTasca(taskId);
		expedientLog.setAccioParams(previousActors + "::" + currentActors);
		ExpedientTascaDto tasca = getByIdSenseComprovacio(taskId);
		registreDao.crearRegistreIniciarTasca(
				tasca.getExpedientId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				"Amollar tasca \"" + tasca.getTitol() + "\"");
		return tasca;
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ExpedientTascaDto> findTasquesConsultaFiltre(
			Long entornId,
			Long expedientTipusId,
			String responsable,
			String tasca,
			String expedient,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Date dataLimitInici,
			Date dataLimitFi,
			int prioritat,
			boolean mostrarTasquesPersonals,
			boolean mostrarTasquesGrup,
			final PaginacioParamsDto paginacioParams) {

		if (responsable == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			responsable = auth.getName();
		}
		
		// Comprova l'accés a l'entorn
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null) {
			logger.debug("No s'ha trobat l'entorn (entornId=" + entornId + ")");
			throw new EntornNotFoundException();
		} else {
			boolean ambPermis = permisosHelper.isGrantedAny(
					entorn.getId(),
					Entorn.class,
					new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION});
			if (!ambPermis) {
				logger.debug("No es tenen permisos per accedir a l'entorn (entornId=" + entornId + ")");
				throw new EntornNotFoundException();
			}
		}
		
		// Obté la llista de tipus d'expedient permesos
		List<ExpedientTipus> tipusPermesos = expedientTipusRepository.findByEntorn(entorn);
		permisosHelper.filterGrantedAny(
				tipusPermesos,
				new ObjectIdentifierExtractor<ExpedientTipus>() {
					public Long getObjectIdentifier(ExpedientTipus expedientTipus) {
						return expedientTipus.getId();
					}
				},
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.READ,
					ExtendedPermission.ADMINISTRATION});
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA TASQUES LLISTAT", "consulta");
		
		String sort = null;
		boolean asc = false;
		for (OrdreDto or : paginacioParams.getOrdres()) {
			asc = or.getDireccio().equals(OrdreDireccioDto.ASCENDENT);
			sort = or.getCamp();
			break;
		}
		
		List<Long> idsExpedients = expedientDao.findListExpedients(
				entornId, 
				responsable,
				expedient, 
				null,
				expedientTipusId,
				sort,
				asc);	
		

		// Calcula la data d'creacio fi pel filtre
		if (dataCreacioFi != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataCreacioFi);
			cal.add(Calendar.DATE, 1);
			dataCreacioFi.setTime(cal.getTime().getTime());
		}

		// Calcula la data limit fi pel filtre
		if (dataLimitFi != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataLimitFi);
			cal.add(Calendar.DATE, 1);
			dataLimitFi.setTime(cal.getTime().getTime());
		}
		
		final LlistatIds ids = jbpmHelper.findListTasks(
				responsable, 
				tasca,
				idsExpedients, 
				dataCreacioInici, 
				dataCreacioFi,
				prioritat, 
				dataLimitInici, 
				dataLimitFi, 
				paginacioParams,
				mostrarTasquesPersonals,
				mostrarTasquesGrup);
		
		final List<JbpmTask> tasques = jbpmHelper.findTasks(ids.getIds());
		
		Page<ExpedientTascaDto> paginaResultats = new Page<ExpedientTascaDto>() {
			
			@Override
			public Iterator<ExpedientTascaDto> iterator() {
				return getContent().iterator();
			}
			
			@Override
			public boolean isLastPage() {
				return false;
			}
			
			@Override
			public boolean isFirstPage() {
				return paginacioParams.getPaginaNum() == 0;
			}
			
			@Override
			public boolean hasPreviousPage() {
				return paginacioParams.getPaginaNum() > 0;
			}
			
			@Override
			public boolean hasNextPage() {
				return false;
			}
			
			@Override
			public boolean hasContent() {
				return !tasques.isEmpty();
			}
			
			@Override
			public int getTotalPages() {
				return 0;
			}
			
			@Override
			public long getTotalElements() {
				return ids.getCount();
			}
			
			@Override
			public Sort getSort() {
				List<Order> orders = new ArrayList<Order>();
				for (OrdreDto or : paginacioParams.getOrdres()) {
					orders.add(new Order(or.getDireccio().equals(OrdreDireccioDto.ASCENDENT) ? Direction.ASC : Direction.DESC, or.getCamp()));
				}
				return new Sort(orders);
			}
			
			@Override
			public int getSize() {
				return paginacioParams.getPaginaTamany();
			}
			
			@Override
			public int getNumberOfElements() {
				return 0;
			}
			
			@Override
			public int getNumber() {
				return 0;
			}
			
			@Override
			public List<ExpedientTascaDto> getContent() {
				List<ExpedientTascaDto> expedientTasques = new ArrayList<ExpedientTascaDto>();
				for (JbpmTask tasca : tasques) {
					expedientTasques.add(tascaHelper.getExpedientTascaLlistatDto(tasca));
				}
				Collections.sort(expedientTasques, comparador);
				return expedientTasques;
			}
			
			Comparator<ExpedientTascaDto> comparador = new Comparator<ExpedientTascaDto>() {				
				public int compare(ExpedientTascaDto t1, ExpedientTascaDto t2) {				
					String finalSort = null;
					boolean finalAsc = false;
					for (OrdreDto or : paginacioParams.getOrdres()) {
						finalAsc = or.getDireccio().equals(OrdreDireccioDto.ASCENDENT);
						finalSort = or.getCamp();
						break;
					}
					int result = 0;
					NullComparator nullComparator = new NullComparator();
					if ("titol".equals(finalSort)) {
						if (finalAsc)
							result = nullComparator.compare(t1.getTitol(), t2.getTitol());
						else
							result = nullComparator.compare(t2.getTitol(), t1.getTitol());
					} else if ("expedientIdentificador".equals(finalSort)) {
						if (finalAsc)
							result = nullComparator.compare(t1.getExpedientIdentificador(), t2.getExpedientIdentificador());
						else 
							result = nullComparator.compare(t2.getExpedientIdentificador(), t1.getExpedientIdentificador());
					} else if ("expedientTipusNom".equals(finalSort)) {
						if (finalAsc)
							result = nullComparator.compare(t1.getExpedientTipusNom(), t2.getExpedientTipusNom());
						else
							result = nullComparator.compare(t2.getExpedientTipusNom(), t1.getExpedientTipusNom());
					} else if ("dataCreacio".equals(finalSort)) {
						if (finalAsc)
							result = nullComparator.compare(t1.getDataCreacio(), t2.getDataCreacio());
						else
							result = nullComparator.compare(t2.getDataCreacio(), t1.getDataCreacio());
					} else if ("prioritat".equals(finalSort)) {
						if (finalAsc)
							result = t1.getPriority() - t2.getPriority();
						else
							result = t2.getPriority() - t1.getPriority();
					} else if ("dataLimit".equals(finalSort)) {
						if (finalAsc)
							result = nullComparator.compare(t1.getDataLimit(), t2.getDataLimit());
						else
							result = nullComparator.compare(t2.getDataLimit(), t1.getDataLimit());
					}
					return result;
				}
			};
		};

		PaginaDto<ExpedientTascaDto> resposta = paginacioHelper.toPaginaDto(
				paginaResultats,
				ExpedientTascaDto.class);
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA TASQUES LLISTAT", "consulta");
		return resposta;
	}

	private static final Logger logger = LoggerFactory.getLogger(TascaServiceImpl.class);	
}
