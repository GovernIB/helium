/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.model.dao.AlertaDao;
import net.conselldemallorca.helium.core.model.dao.CampDao;
import net.conselldemallorca.helium.core.model.dao.ConsultaCampDao;
import net.conselldemallorca.helium.core.model.dao.DefinicioProcesDao;
import net.conselldemallorca.helium.core.model.dao.DocumentDao;
import net.conselldemallorca.helium.core.model.dao.DocumentStoreDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientTipusDao;
import net.conselldemallorca.helium.core.model.dao.FormulariExternDao;
import net.conselldemallorca.helium.core.model.dao.LuceneDao;
import net.conselldemallorca.helium.core.model.dao.PlantillaDocumentDao;
import net.conselldemallorca.helium.core.model.dao.PluginCustodiaDao;
import net.conselldemallorca.helium.core.model.dao.PluginGestioDocumentalDao;
import net.conselldemallorca.helium.core.model.dao.PluginSignaturaDao;
import net.conselldemallorca.helium.core.model.dao.RegistreDao;
import net.conselldemallorca.helium.core.model.dao.TascaDao;
import net.conselldemallorca.helium.core.model.dao.TerminiIniciatDao;
import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.dto.TascaLlistatDto;
import net.conselldemallorca.helium.core.model.exception.DominiException;
import net.conselldemallorca.helium.core.model.exception.IllegalArgumentsException;
import net.conselldemallorca.helium.core.model.exception.IllegalStateException;
import net.conselldemallorca.helium.core.model.exception.NotFoundException;
import net.conselldemallorca.helium.core.model.exception.PluginException;
import net.conselldemallorca.helium.core.model.exception.TemplateException;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore.DocumentFont;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.FirmaTasca;
import net.conselldemallorca.helium.core.model.hibernate.FormulariExtern;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.security.acl.AclServiceDao;
import net.conselldemallorca.helium.core.util.DocumentTokenUtils;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.OpenOfficeUtils;
import net.conselldemallorca.helium.integracio.plugins.signatura.RespostaValidacioSignatura;
import net.conselldemallorca.helium.jbpm3.integracio.DelegationInfo;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmDao;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


/**
 * Servei per gestionar les tasques assignades a una persona
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class TascaService {

	public static final String VAR_PREFIX = "H3l1um#";

	public static final String VAR_TASCA_VALIDADA = "H3l1um#tasca.validada";
	public static final String VAR_TASCA_DELEGACIO = "H3l1um#tasca.delegacio";

	public static final String PREFIX_SIGNATURA = "H3l1um#signatura.";
	public static final String PREFIX_DOCUMENT = "H3l1um#document.";
	public static final String PREFIX_ADJUNT = "H3l1um#adjunt.";
	public static final String PREFIX_TEXT_SUGGEST = "H3l1um#suggtext.";

	public static final String DEFAULT_SECRET_KEY = "H3l1umKy";
	public static final String DEFAULT_ENCRYPTION_SCHEME = "DES/ECB/PKCS5Padding";
	public static final String DEFAULT_KEY_ALGORITHM = "DES";

	private ExpedientDao expedientDao;
	private ExpedientTipusDao expedientTipusDao;
	private TascaDao tascaDao;
	private DefinicioProcesDao definicioProcesDao;
	private DocumentDao documentDao;
	private JbpmDao jbpmDao;
	private AclServiceDao aclServiceDao;
	private DocumentStoreDao documentStoreDao;
	private PlantillaDocumentDao plantillaDocumentDao;
	private PluginSignaturaDao pluginSignaturaDao;
	private PluginCustodiaDao pluginCustodiaDao;
	private DtoConverter dtoConverter;
	private LuceneDao luceneDao;
	private RegistreDao registreDao;
	private FormulariExternDao formulariExternDao;
	private PluginGestioDocumentalDao pluginGestioDocumentalDao;
	private TerminiIniciatDao terminiIniciatDao;
	private AlertaDao alertaDao;
	private CampDao campDao;
	private ConsultaCampDao consultaCampDao;
	private MessageSource messageSource;

	private OpenOfficeUtils openOfficeUtils;
	private ServiceUtils serviceUtils;
	private DocumentTokenUtils documentTokenUtils;

	private Map<String, Map<String, Object>> dadesFormulariExternInicial;

	private Map<String, DadesCacheTasca> dadesCacheTasques;



	public TascaDto getById(Long entornId, String taskId) {
		return getById(entornId, taskId, null, null);
	}
	public TascaDto getById(Long entornId, String taskId, Map<String, Object> valorsCommand) {
		return getById(entornId, taskId, null, valorsCommand);
	}
	public TascaDto getById(Long entornId, String taskId, String usuari) {
		return getById(entornId, taskId, usuari, null);
	}
	public TascaDto getById(Long entornId, String taskId, String usuari, Map<String, Object> valorsCommand) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, usuari, true);
		return toTascaDto(task, valorsCommand, true);
	}
	public TascaDto getByIdSenseComprovacio(String taskId) {
		return getByIdSenseComprovacio(taskId, null, null);
	}
	public TascaDto getByIdSenseComprovacio(String taskId, Map<String, Object> valorsCommand) {
		return getByIdSenseComprovacio(taskId, null, valorsCommand);
	}
	public TascaDto getByIdSenseComprovacio(String taskId, String usuari) {
		return getByIdSenseComprovacio(taskId, usuari, null);
	}
	public TascaDto getByIdSenseComprovacio(String taskId, String usuari, Map<String, Object> valorsCommand) {
		JbpmTask task = jbpmDao.getTaskById(taskId);
		return toTascaDto(task, valorsCommand, true);
	}

	public List<TascaLlistatDto> findTasquesPersonalsIndex(Long entornId) {
		return findTasquesPersonalsTramitacio(entornId, null, false);
	}
	public List<TascaLlistatDto> findTasquesPersonalsTramitacio(
			Long entornId,
			String usuari,
			boolean perTramitacio) {
		String usuariBo = usuari;
		if (usuariBo == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuariBo = auth.getName();
		}
		List<JbpmTask> tasques = jbpmDao.findPersonalTasks(usuariBo);
		return tasquesFiltradesPerEntorn(entornId, tasques, perTramitacio);
	}

	public List<TascaLlistatDto> findTasquesPersonalsFiltre(
			Long entornId,
			String tasca,
			String expedient,
			Long tipusExpedient,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Integer prioritat,
			Date dataLimitInici,
			Date dataLimitFi,
			String columna,
			String ordre) {
		return findTasquesPersonalsFiltre(
				entornId,
				null,
				tasca,
				expedient,
				tipusExpedient,
				dataCreacioInici,
				dataCreacioFi,
				prioritat,
				dataLimitInici,
				dataLimitFi,
				columna,
				ordre);
	}
	public List<TascaLlistatDto> findTasquesPersonalsFiltre(
			Long entornId,
			String usuari,
			String tasca,
			String expedient,
			Long tipusExpedient,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Integer prioritat,
			Date dataLimitInici,
			Date dataLimitFi,
			String columna,
			String ordre) {
		String usuariBo = usuari;
		if (usuariBo == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuariBo = auth.getName();
		}
		List<JbpmTask> tasques = jbpmDao.findPersonalTasks(usuariBo);
		List<TascaLlistatDto> resposta = tasquesLlistatFiltradesValors(
				entornId,
				tasques, 
				tasca,
				expedient,
				tipusExpedient,
				dataCreacioInici,
				dataCreacioFi,
				prioritat,
				dataLimitInici,
				dataLimitFi,
				columna,
				ordre);
		return resposta;
	}

	public List<TascaLlistatDto> findTasquesPerTramitacioMassiva(
			Long entornId,
			String usuari,
			String taskId) {
		String usuariBo = usuari;
		if (usuariBo == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuariBo = auth.getName();
		}
		List<JbpmTask> tasques = jbpmDao.findPersonalTasks(usuariBo);
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, usuari, true);
		TascaDto tasca = toTascaDto(task, null, true);
		String codi = task.getName();
		String jbpmKey = tasca.getDefinicioProces().getJbpmKey();
		List<TascaLlistatDto> resposta = tasquesPerTramitacioMasiva(
				entornId,
				tasques, 
				codi,
				jbpmKey);
		return resposta;
	}

	public List<TascaLlistatDto> findTasquesGrupIndex(Long entornId) {
		return findTasquesGrupTramitacio(entornId, null, false);
	}
	public List<TascaLlistatDto> findTasquesGrupTramitacio(
			Long entornId,
			String usuari,
			boolean perTramitacio) {
		String usuariBo = usuari;
		if (usuariBo == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuariBo = auth.getName();
		}
		List<JbpmTask> tasques = jbpmDao.findGroupTasks(usuariBo);
		return tasquesFiltradesPerEntorn(entornId, tasques, perTramitacio);
	}
	public List<TascaLlistatDto> findTasquesGrupFiltre(
			Long entornId,
			String tasca,
			String expedient,
			Long tipusExpedient,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Integer prioritat,
			Date dataLimitInici,
			Date dataLimitFi,
			String columna,
			String ordre) {
		return findTasquesGrupFiltre(
				entornId,
				null,
				tasca,
				expedient,
				tipusExpedient,
				dataCreacioInici,
				dataCreacioFi,
				prioritat,
				dataLimitInici,
				dataLimitFi,
				columna,
				ordre);
	}
	
	public List<TascaLlistatDto> findTasquesGrupFiltre(
			Long entornId,
			String usuari,
			String tasca,
			String expedient,
			Long tipusExpedient,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Integer prioritat,
			Date dataLimitInici,
			Date dataLimitFi,
			String columna,
			String ordre) {
		String usuariBo = usuari;
		if (usuariBo == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuariBo = auth.getName();
		}
		List<JbpmTask> tasques = jbpmDao.findGroupTasks(usuariBo);
		return tasquesLlistatFiltradesValors(
				entornId,
				tasques,
				tasca,
				expedient,
				tipusExpedient,
				dataCreacioInici,
				dataCreacioFi,
				prioritat,
				dataLimitInici,
				dataLimitFi,
				columna,
				ordre);
	}

	public TascaDto agafar(Long entornId, String taskId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return agafar(entornId, auth.getName(), taskId);
	}
	public TascaDto agafar(Long entornId, String usuari, String taskId) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, false);
		jbpmDao.takeTaskInstance(taskId, usuari);
		TascaDto tasca = toTascaDto(task, null, true);
		registreDao.crearRegistreIniciarTasca(
				tasca.getExpedient().getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				"Agafar tasca \"" + tasca.getNom() + "\"");
		return tasca;
	}
	public TascaDto guardarVariables(
			Long entornId,
			String taskId,
			Map<String, Object> variables) {
		return guardarVariables(entornId, taskId, variables, null);
	}
	public TascaDto guardarVariables(
			Long entornId,
			String taskId,
			Map<String, Object> variables,
			String usuari) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, usuari, true);
		boolean iniciada = task.getStartTime() == null;
		jbpmDao.startTaskInstance(taskId);
		jbpmDao.setTaskInstanceVariables(taskId, variables);
		TascaDto tasca = toTascaDto(task, null, true);
		if (iniciada) {
			registreDao.crearRegistreModificarTasca(
					tasca.getExpedient().getId(),
					taskId,
					SecurityContextHolder.getContext().getAuthentication().getName(),
					"Iniciar tasca \"" + tasca.getNom() + "\"");
		}
		/*registreDao.crearRegistreModificarTasca(
				tasca.getExpedient().getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				"Guardar variables \"" + tasca.getNom() + "\"");*/
		return tasca;
	}

	public void guardarRegistre(
			String taskId,
			String campCodi,
			Object[] valors) {
		guardarRegistre(
				taskId,
				campCodi,
				valors,
				-1);
	}
	public void guardarRegistre(
			String taskId,
			String campCodi,
			Object[] valors,
			int index) {
		Object valor = jbpmDao.getTaskInstanceVariable(taskId, campCodi);
		if (valor == null) {
			jbpmDao.setTaskInstanceVariable(
					taskId,
					campCodi,
					new Object[]{valors});
		} else {
			Object[] valorMultiple = (Object[])valor;
			if (index != -1) {
				valorMultiple[index] = valors;
				jbpmDao.setTaskInstanceVariable(
						taskId,
						campCodi,
						valor);
			} else {
				Object[] valorNou = new Object[valorMultiple.length + 1];
				for (int i = 0; i < valorMultiple.length; i++)
					valorNou[i] = valorMultiple[i];
				valorNou[valorMultiple.length] = valors;
				jbpmDao.setTaskInstanceVariable(
						taskId,
						campCodi,
						valorNou);
			}
		}
	}
	public void esborrarRegistre(
			String taskId,
			String campCodi,
			int index) {
		Object valor = jbpmDao.getTaskInstanceVariable(taskId, campCodi);
		if (valor != null) {
			Object[] valorMultiple = (Object[])valor;
			if (valorMultiple.length > 0) {
				Object[] valorNou = new Object[valorMultiple.length - 1];
				for (int i = 0; i < valorNou.length; i++)
					valorNou[i] = (i < index) ? valorMultiple[i] : valorMultiple[i + 1];
				jbpmDao.setTaskInstanceVariable(
						taskId,
						campCodi,
						valorNou);
			}
		}
	}

	public TascaDto validar(
			Long entornId,
			String taskId,
			Map<String, Object> variables,
			boolean comprovarAssignacio) {
		return validar(entornId, taskId, variables, comprovarAssignacio, null);
	}
	public TascaDto validar(
			Long entornId,
			String taskId,
			Map<String, Object> variables,
			boolean comprovarAssignacio,
			String usuari) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, usuari, comprovarAssignacio);
		jbpmDao.startTaskInstance(taskId);
		jbpmDao.setTaskInstanceVariables(taskId, variables);
		validarTasca(taskId);
		TascaDto tasca = toTascaDto(task, null, true);
		registreDao.crearRegistreModificarTasca(
				tasca.getExpedient().getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				"Validar \"" + tasca.getNom() + "\"");
		return tasca;
	}
	public TascaDto restaurar(
			Long entornId,
			String taskId) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, true);
		if (!isTascaValidada(task))
			throw new IllegalStateException(
					getServiceUtils().getMessage("error.tascaService.noValidada"));
		//deleteDocumentsTasca(taskId);
		restaurarTasca(taskId);
		TascaDto tasca = toTascaDto(task, null, true);
		registreDao.crearRegistreModificarTasca(
				tasca.getExpedient().getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				"Restaurar \"" + tasca.getNom() + "\"");
		return tasca;
	}
	public void completar(
			Long entornId,
			String taskId,
			boolean comprovarAssignacio,
			String usuari) {
		completar(entornId, taskId, comprovarAssignacio, usuari, null);
	}
	public void completar(
			Long entornId,
			String taskId,
			boolean comprovarAssignacio,
			String usuari,
			String outcome) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, usuari, comprovarAssignacio);
		if (!isTascaValidada(task))
			throw new IllegalStateException(
					getServiceUtils().getMessage("error.tascaService.formNoValidat"));
		if (!isDocumentsComplet(task))
			throw new IllegalStateException(
					getServiceUtils().getMessage("error.tascaService.faltenAdjuntar"));
		if (!isSignaturesComplet(task))
			throw new IllegalStateException(
					getServiceUtils().getMessage("error.tascaService.faltenSignar"));
		jbpmDao.startTaskInstance(taskId);
		jbpmDao.completeTaskInstance(task.getId(), outcome);
		// Accions per a una tasca delegada
		DelegationInfo delegationInfo = getDelegationInfo(task);
		if (delegationInfo != null) {
			if (!taskId.equals(delegationInfo.getSourceTaskId())) {
				// Copia les variables de la tasca delegada a la original
				jbpmDao.setTaskInstanceVariables(
						delegationInfo.getSourceTaskId(),
						getVariablesDelegacio(task));
				JbpmTask taskOriginal = jbpmDao.getTaskById(delegationInfo.getSourceTaskId());
				if (!delegationInfo.isSupervised()) {
					// Si no es supervisada també finalitza la tasca original
					completar(entornId, taskOriginal.getId(), false, null, outcome);
				}
				deleteDelegationInfo(taskOriginal);
			}
		}
		JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(task.getProcessInstanceId());
		Expedient expedient = expedientDao.findAmbProcessInstanceId(pi.getId());
		Map<String, Set<Camp>> mapCamps = getServiceUtils().getMapCamps(expedient);
		Map<String, Map<String, Object>> mapValors = getServiceUtils().getMapValors(expedient);
		luceneDao.updateExpedient(
				expedient,
				getServiceUtils().getMapDefinicionsProces(expedient),
				mapCamps,
				mapValors,
				getServiceUtils().getMapValorsDomini(mapCamps, mapValors),
				isExpedientFinalitzat(expedient));
		TascaDto tasca = toTascaDto(task, null, true);
		registreDao.crearRegistreFinalitzarTasca(
				tasca.getExpedient().getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				"Finalitzar \"" + tasca.getNom() + "\"");
		actualitzarTerminisIniciatsIAlertes(taskId, expedient);
		dadesCacheTasques.remove(task.getId());
	}

	public Object getVariable(
			Long entornId,
			String taskId,
			String codiVariable) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, true);
		return jbpmDao.getTaskInstanceVariable(task.getId(), codiVariable);
	}
	public void createVariable(
			Long entornId,
			String taskId,
			String codiVariable,
			Object valor) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, true);
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(codiVariable, valor);
		jbpmDao.setTaskInstanceVariables(task.getId(), variables);
		TascaDto tasca = toTascaDto(task, null, true);
		registreDao.crearRegistreCrearVariableTasca(
				tasca.getExpedient().getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				codiVariable,
				valor);
	}
	public void updateVariable(
			Long entornId,
			String taskId,
			String codiVariable,
			Object valor) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, true);
		Object valorVell = jbpmDao.getTaskInstanceVariable(taskId, codiVariable);
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(codiVariable, valor);
		jbpmDao.setTaskInstanceVariables(task.getId(), variables);
		TascaDto tasca = toTascaDto(task, null, true);
		registreDao.crearRegistreModificarVariableTasca(
				tasca.getExpedient().getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				codiVariable,
				valorVell,
				valor);
	}
	public void esborrarVariable(
			Long entornId,
			String taskId,
			String codiVariable) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, true);
		jbpmDao.deleteTaskInstanceVariable(task.getId(), codiVariable);
		TascaDto tasca = toTascaDto(task, null, true);
		registreDao.crearRegistreEsborrarVariableTasca(
				tasca.getExpedient().getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				codiVariable);
	}

	public void guardarDocument(
			Long entornId,
			String taskId,
			String documentCodi,
			String nom,
			Date data,
			byte[] contingut) {
		guardarDocument(
				entornId,
				taskId,
				documentCodi,
				nom,
				data,
				contingut,
				null);
	}
	public void guardarDocument(
			Long entornId,
			String taskId,
			String documentCodi,
			String arxiuNom,
			Date data,
			byte[] arxiuContingut,
			String usuari) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, usuari, true);
		if (!isTascaValidada(task))
			throw new IllegalStateException(
					getServiceUtils().getMessage("error.tascaService.noValidada"));
		JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(task.getProcessInstanceId());
		Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
		DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmId(task.getProcessDefinitionId());
		Document document = documentDao.findAmbDefinicioProcesICodi(
				definicioProces.getId(),
				documentCodi);
		String codiVariableJbpm = PREFIX_DOCUMENT + document.getCodi();
		// Obté la referencia al documentStore del jBPM
		Long docStoreId = (Long)jbpmDao.getProcessInstanceVariable(task.getProcessInstanceId(), codiVariableJbpm);
		String nomArxiuAntic = null;
		boolean creat = (docStoreId == null);
		// Crea el document al store
		if (docStoreId == null) {
			docStoreId = documentStoreDao.create(
					task.getProcessInstanceId(),
					codiVariableJbpm,
					data,
					(pluginGestioDocumentalDao.isGestioDocumentalActiu()) ? DocumentFont.ALFRESCO : DocumentFont.INTERNA,
					arxiuNom,
					(pluginGestioDocumentalDao.isGestioDocumentalActiu()) ? null : arxiuContingut,
					false,
					null);
		} else {
			DocumentStore docStore = documentStoreDao.getById(docStoreId, false);
			if (docStore == null) {
				docStoreId = documentStoreDao.create(
						task.getProcessInstanceId(),
						codiVariableJbpm,
						data,
						(pluginGestioDocumentalDao.isGestioDocumentalActiu()) ? DocumentFont.ALFRESCO : DocumentFont.INTERNA,
						arxiuNom,
						(pluginGestioDocumentalDao.isGestioDocumentalActiu()) ? null : arxiuContingut,
						false,
						null);
			} else {
				nomArxiuAntic = docStore.getArxiuNom();
				documentStoreDao.update(
						docStoreId,
						data,
						arxiuNom,
						(pluginGestioDocumentalDao.isGestioDocumentalActiu()) ? null : arxiuContingut,
						null);
				if (arxiuContingut != null && pluginGestioDocumentalDao.isGestioDocumentalActiu())
					pluginGestioDocumentalDao.deleteDocument(docStore.getReferenciaFont());
			}
		}
		// Crea el document a dins la gestió documental
		if (arxiuContingut != null && pluginGestioDocumentalDao.isGestioDocumentalActiu()) {
			String referenciaFont = pluginGestioDocumentalDao.createDocument(
					expedient,
					docStoreId.toString(),
					document.getNom(),
					data,
					arxiuNom,
					arxiuContingut);
			documentStoreDao.updateReferenciaFont(
					docStoreId,
					referenciaFont);
		}
		// Guarda la referència al nou document a dins el jBPM
		jbpmDao.setTaskInstanceVariable(
				task.getId(),
				codiVariableJbpm,
				docStoreId);
		TascaDto tasca = toTascaDto(task, null, true);
		// Registra l'acció
		if (creat) {
			registreDao.crearRegistreCrearDocumentTasca(
					tasca.getExpedient().getId(),
					taskId,
					SecurityContextHolder.getContext().getAuthentication().getName(),
					document.getCodi(),
					arxiuNom);
		} else {
			registreDao.crearRegistreModificarDocumentTasca(
					tasca.getExpedient().getId(),
					taskId,
					SecurityContextHolder.getContext().getAuthentication().getName(),
					document.getCodi(),
					nomArxiuAntic,
					arxiuNom);
		}
	}
	public void esborrarDocument(
			Long entornId,
			String taskId,
			String documentCodi) {
		esborrarDocument(entornId, taskId, documentCodi, null);
	}
	public void esborrarDocument(
			Long entornId,
			String taskId,
			String documentCodi,
			String usuari) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, usuari, true);
		String codiVariableJbpm = PREFIX_DOCUMENT + documentCodi;
		Long documentJbpm = (Long)jbpmDao.getTaskInstanceVariable(taskId, codiVariableJbpm);
		if (documentJbpm == null)
			documentJbpm = (Long)jbpmDao.getProcessInstanceVariable(task.getProcessInstanceId(), codiVariableJbpm);
		if (documentJbpm != null) {
			DocumentStore documentStore = documentStoreDao.getById(documentJbpm, false);
			if (documentStore != null) {
				if (documentStore.isSignat()) {
					if (pluginCustodiaDao.isCustodiaActiu()) {
						try {
							pluginCustodiaDao.esborrarSignatures(documentStore.getReferenciaCustodia());
						} catch (PluginException ignored) {}
					}
				}
				if (documentStore.getFont().equals(DocumentFont.ALFRESCO))
					pluginGestioDocumentalDao.deleteDocument(documentStore.getReferenciaFont());
				documentStoreDao.delete(documentJbpm);
			}
			if (jbpmDao.getTaskInstanceVariable(taskId, codiVariableJbpm) != null) {
				jbpmDao.deleteTaskInstanceVariable(
						taskId,
						codiVariableJbpm);
				jbpmDao.deleteTaskInstanceVariable(
						taskId,
						PREFIX_SIGNATURA + documentCodi);
			}
			jbpmDao.deleteProcessInstanceVariable(
					task.getProcessInstanceId(),
					codiVariableJbpm);
		}
		TascaDto tasca = toTascaDto(task, null, true);
		registreDao.crearRegistreEsborrarDocumentTasca(
				tasca.getExpedient().getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				documentCodi);
	}
	public DocumentDto getDocument(
			Long entornId,
			String taskId,
			String codiVariable) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, true);
		String codiVariableJbpm = PREFIX_DOCUMENT + codiVariable;
		Long documentStoreId = (Long)jbpmDao.getTaskInstanceVariable(
				taskId,
				codiVariableJbpm);
		if (documentStoreId == null)
			documentStoreId = (Long)jbpmDao.getProcessInstanceVariable(
					task.getProcessInstanceId(),
					PREFIX_DOCUMENT + codiVariable);
		if (documentStoreId == null)
			return null;
		return dtoConverter.toDocumentDto(
				documentStoreId,
				true,
				false,
				false,
				false,
				false);
	}

	public DocumentDto getDocumentAmbToken(
			String token,
			boolean ambContingut) throws Exception {
		String[] tokenDesxifrat = getDocumentTokenUtils().desxifrarTokenMultiple(token);
		Long documentStoreId = Long.parseLong(tokenDesxifrat[0]);
		if (tokenDesxifrat.length == 2) {
			documentStoreId = Long.parseLong(tokenDesxifrat[1]);
			return dtoConverter.toDocumentDto(
					documentStoreId,
					ambContingut,
					false,
					false,
					false,
					false);
		} else {
			throw new IllegalArgumentsException(
					getServiceUtils().getMessage("error.documentService.formatIncorrecte"));
		}
	}
	public boolean signarDocumentAmbToken(
			Long entornId,
			String token,
			byte[] signatura) throws Exception {
		String[] tokenDesxifrat = getDocumentTokenUtils().desxifrarTokenMultiple(token);
		if (tokenDesxifrat.length == 2) {
			JbpmTask task = comprovarSeguretatTasca(entornId, tokenDesxifrat[0], null, true);
			TascaDto tascaDto = toTascaDto(task, null, false);
			Long documentStoreId = Long.parseLong(tokenDesxifrat[1]);
			DocumentDto document = dtoConverter.toDocumentDto(
					documentStoreId,
					false,
					false,
					true,
					true,
					true);
			if (document != null) {
				// Comprova que es tengui accés a signar el document
				boolean trobat = false;
				for (FirmaTasca firmaTasca: tascaDto.getSignatures()) {
					if (document.getDocumentCodi().equals(firmaTasca.getDocument().getCodi())) {
						trobat = true;
						break;
					}
				}
				if (trobat) {
					boolean custodiat = false;
					DocumentStore docst = documentStoreDao.getById(documentStoreId, false);
					JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(docst.getProcessInstanceId());
					Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
					if (pluginCustodiaDao.isCustodiaActiu()) {
						String nomArxiu = nomArxiuAmbExtensio(
								document.getArxiuNom(),
								getExtensioSignatura());
						String referenciaCustodia = null;
						if (pluginCustodiaDao.isValidacioImplicita()) {
							referenciaCustodia = pluginCustodiaDao.afegirSignatura(
									docst.getId(),
									docst.getReferenciaFont(),
									nomArxiu,
									document.getCustodiaCodi(),
									signatura);
							custodiat = true;
						} else {
							RespostaValidacioSignatura resposta = pluginSignaturaDao.verificarSignatura(
									document.getVistaContingut(),
									signatura,
									false);
							if (resposta.isEstatOk()) {
								referenciaCustodia = pluginCustodiaDao.afegirSignatura(
										docst.getId(),
										docst.getReferenciaFont(),
										nomArxiu,
										document.getCustodiaCodi(),
										signatura);
								custodiat = true;
							}
						}
						docst.setReferenciaCustodia(referenciaCustodia);
					}
					if (custodiat) {
						docst.setSignat(true);
						registreDao.crearRegistreSignarDocument(
								expedient.getId(),
								docst.getProcessInstanceId(),
								SecurityContextHolder.getContext().getAuthentication().getName(),
								document.getDocumentCodi());
						jbpmDao.setTaskInstanceVariable(
								tokenDesxifrat[0],
								PREFIX_SIGNATURA + document.getDocumentCodi(),
								documentStoreId);
					}
					return custodiat;
				} else {
					throw new IllegalStateException(
							getServiceUtils().getMessage("error.tascaService.docNoDisponible"));
				}
			} else {
				throw new IllegalStateException(
						getServiceUtils().getMessage("error.tascaService.docNoDisponible"));
			}
		} else {
			throw new IllegalArgumentsException(
					getServiceUtils().getMessage("error.tascaService.tokenIncorrecte"));
		}
	}

	public DocumentDto generarDocumentPlantilla(
			Long entornId,
			Long documentId,
			String taskId,
			Date dataDocument) {
		Document document = documentDao.getById(documentId, false);
		DocumentDto resposta = new DocumentDto();
		resposta.setDataCreacio(new Date());
		resposta.setDataDocument(new Date());
		resposta.setArxiuNom(document.getNom() + ".odt");
		resposta.setAdjuntarAuto(document.isAdjuntarAuto());
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, true);
		if (document.isPlantilla()) {
			JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(task.getProcessInstanceId());
			ExpedientDto expedient = dtoConverter.toExpedientDto(
					expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId()),
					false);
			TascaDto tasca = toTascaDto(task, null, true);
			InstanciaProcesDto instanciaProces = dtoConverter.toInstanciaProcesDto(
					task.getProcessInstanceId(),
					true);
			Map<String, Object> model = new HashMap<String, Object>();
			model.putAll(instanciaProces.getVarsComText());
			model.putAll(tasca.getVarsComText());
			try {
				byte[] resultat = plantillaDocumentDao.generarDocumentAmbPlantilla(
						entornId,
						document,
						task.getAssignee(),
						expedient,
						task.getProcessInstanceId(),
						tasca,
						dataDocument,
						model);
				if (isActiuConversioVista()) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					getOpenOfficeUtils().convertir(
							resposta.getArxiuNom(),
							resultat,
							getExtensioVista(document),
							baos);
					resposta.setArxiuNom(
							nomArxiuAmbExtensio(
									resposta.getArxiuNom(),
									getExtensioVista(document)));
					resposta.setArxiuContingut(baos.toByteArray());
				} else {
					resposta.setArxiuContingut(resultat);
				}
				if (document.isAdjuntarAuto()) {
					guardarDocument(
							entornId,
							taskId,
							document.getCodi(),
							resposta.getArxiuNom(),
							dataDocument,
							resposta.getArxiuContingut());
				}
			} catch (Exception ex) {
				throw new TemplateException(
						getServiceUtils().getMessage("error.tascaService.generarDocument"), ex);
			}
		} else {
			resposta.setArxiuContingut(document.getArxiuContingut());
		}
		return resposta;
	}

	public boolean isExtensioDocumentPermesa(
			Long entornId,
			String taskId,
			String documentCodi,
			String extensio) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, true);
		if (!isTascaValidada(task))
			throw new IllegalStateException(
					getServiceUtils().getMessage("error.tascaService.noValidada"));
		DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmId(task.getProcessDefinitionId());
		Document document = documentDao.findAmbDefinicioProcesICodi(
				definicioProces.getId(),
				documentCodi);
		return document.isExtensioPermesa(extensio);
	}

	public void delegacioCrear(
			Long entornId,
			String taskId,
			String actorId,
			String comentari,
			boolean supervisada) {
		JbpmTask original = comprovarSeguretatTasca(entornId, taskId, null, true);
		JbpmTask delegada = jbpmDao.cloneTaskInstance(
				taskId,
				actorId,
				getVariablesDelegacio(original));
		createDelegationInfo(
				original,
				original,
				delegada,
				comentari,
				supervisada);
		createDelegationInfo(
				delegada,
				original,
				delegada,
				comentari,
				supervisada);
	}
	public void delegacioCancelar(
			Long entornId,
			String taskId) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, true);
		DelegationInfo delegationInfo = getDelegationInfo(task);
		if (delegationInfo == null || !taskId.equals(delegationInfo.getSourceTaskId())) {
			throw new IllegalStateException(
					getServiceUtils().getMessage("error.tascaService.cancelarDelegacio"));
		}
		// Cancelar la tasca delegada
		jbpmDao.cancelTaskInstance(delegationInfo.getTargetTaskId());
		// Esborram la delegació
		deleteDelegationInfo(task);
	}

	public FormulariExtern iniciarFormulariExtern(
			Long entornId,
			String taskId) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, true);
		Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
				task.getName(),
				task.getProcessDefinitionId());
		if (tasca.getFormExtern() == null)
			throw new IllegalStateException(
					getServiceUtils().getMessage("error.tascaService.noFormExtern"));
		Map<String, Object> vars = jbpmDao.getTaskInstanceVariables(task.getId());
		JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(task.getProcessInstanceId());
		Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
		return formulariExternDao.iniciarFormulariExtern(
				expedient.getTipus(),
				taskId,
				tasca.getFormExtern(),
				vars);
	}

	public FormulariExtern iniciarFormulariExtern(
			String taskId,
			Long expedientTipusId,
			Long definicioProcesId) {
		ExpedientTipus expedientTipus = expedientTipusDao.getById(expedientTipusId, false);
		DefinicioProces definicioProces = null;
		if (definicioProcesId != null) {
			definicioProces = definicioProcesDao.getById(definicioProcesId, false);
		} else {
			definicioProces = definicioProcesDao.findDarreraVersioAmbEntornIJbpmKey(
					expedientTipus.getEntorn().getId(),
					expedientTipus.getJbpmProcessDefinitionKey());
		}
		if (definicioProcesId == null && definicioProces == null) {
			logger.error("No s'ha trobat la definició de procés (entorn=" + expedientTipus.getEntorn().getCodi() + ", jbpmKey=" + expedientTipus.getJbpmProcessDefinitionKey() + ")");
		}
		String startTaskName = jbpmDao.getStartTaskName(definicioProces.getJbpmId());
		Tasca tasca = tascaDao.findAmbActivityNameIDefinicioProces(
				startTaskName,
				definicioProces.getId());
		return formulariExternDao.iniciarFormulariExtern(
				expedientTipus,
				taskId,
				tasca.getFormExtern(),
				null);
	}

	public void guardarFormulariExtern(
			String formulariId,
			Map<String, Object> variables) {
		FormulariExtern formExtern = formulariExternDao.findAmbFormulariId(formulariId);
		if (formExtern != null) {
			if (formulariId.startsWith("TIE_")) {
				if (dadesFormulariExternInicial == null)
					dadesFormulariExternInicial = new HashMap<String, Map<String, Object>>();
				dadesFormulariExternInicial.put(formulariId, variables);
			} else {
				Map<String, Object> valors = new HashMap<String, Object>();
				JbpmTask task = jbpmDao.getTaskById(formExtern.getTaskId());
				Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
						task.getName(),
						task.getProcessDefinitionId());
				for (CampTasca camp: tasca.getCamps()) {
					if (!camp.isReadOnly()) {
						String codi = camp.getCamp().getCodi();
						if (variables.keySet().contains(codi))
							valors.put(codi, variables.get(codi));
					}
				}
				validar(
						getDadesCacheTasca(task).getEntornId(),
						formExtern.getTaskId(),
						valors,
						false);
			}
			formExtern.setDataRecepcioDades(new Date());
			logger.info("Les dades del formulari amb id " + formulariId + " han estat guardades");
		} else {
			logger.warn("No s'ha trobat cap tasca amb l'id de formulari " + formulariId);
		}
	}
	public Map<String, Object> obtenirValorsFormulariExternInicial(String formulariId) {
		if (dadesFormulariExternInicial == null)
			return null;
		return dadesFormulariExternInicial.remove(formulariId);
	}

	public List<FilaResultat> getValorsCampSelect(
			String taskId,
			String campCodi,
			String textInicial) throws DominiException {
		JbpmTask task = jbpmDao.getTaskById(taskId);
		DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmId(task.getProcessDefinitionId());
		return dtoConverter.getResultatConsultaDomini(
				definicioProces,
				taskId,
				null,
				campCodi,
				textInicial,
				null);
	}

	public void executarAccio(
			Long entornId,
			String taskId,
			String accio) throws DominiException {
		comprovarSeguretatTasca(entornId, taskId, null, true);
		jbpmDao.executeActionInstanciaTasca(taskId, accio);
	}

	public Integer getTotalTasquesPersona(Long entornId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuariBo = auth.getName();
		List<JbpmTask> tasques = jbpmDao.findPersonalTasks(usuariBo);
		Integer total = 0;
		for (JbpmTask task: tasques) {
			Long currentEntornId = getDadesCacheTasca(task).getEntornId();
			if (currentEntornId != null && entornId.equals(currentEntornId))
				total++;
		}
		return total;
	}
	public Integer getTotalTasquesGrup(Long entornId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuariBo = auth.getName();
		List<JbpmTask> tasques = jbpmDao.findGroupTasks(usuariBo);
		Integer total = 0;
		for (JbpmTask task: tasques) {
			Long currentEntornId = getDadesCacheTasca(task).getEntornId();;
			if (currentEntornId != null && entornId.equals(currentEntornId))
				total++;
		}
		return total;
	}

	@Autowired
	public void setExpedientDao(ExpedientDao expedientDao) {
		this.expedientDao = expedientDao;
	}
	@Autowired
	public void setExpedientTipusDao(ExpedientTipusDao expedientTipusDao) {
		this.expedientTipusDao = expedientTipusDao;
	}
	@Autowired
	public void setTascaDao(TascaDao tascaDao) {
		this.tascaDao = tascaDao;
	}
	@Autowired
	public void setDefinicioProcesDao(DefinicioProcesDao definicioProcesDao) {
		this.definicioProcesDao = definicioProcesDao;
	}
	@Autowired
	public void setDocumentDao(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}
	@Autowired
	public void setJbpmDao(JbpmDao jbpmDao) {
		this.jbpmDao = jbpmDao;
	}
	@Autowired
	public void setAclServiceDao(AclServiceDao aclServiceDao) {
		this.aclServiceDao = aclServiceDao;
	}
	@Autowired
	public void setDocumentStoreDao(DocumentStoreDao documentStoreDao) {
		this.documentStoreDao = documentStoreDao;
	}
	@Autowired
	public void setPlantillaDocumentDao(PlantillaDocumentDao plantillaDocumentDao) {
		this.plantillaDocumentDao = plantillaDocumentDao;
	}
	@Autowired
	public void setPluginCustodiaDao(PluginCustodiaDao pluginCustodiaDao) {
		this.pluginCustodiaDao = pluginCustodiaDao;
	}
	@Autowired
	public void setPluginSignaturaDao(PluginSignaturaDao pluginSignaturaDao) {
		this.pluginSignaturaDao = pluginSignaturaDao;
	}
	@Autowired
	public void setDtoConverter(DtoConverter dtoConverter) {
		this.dtoConverter = dtoConverter;
	}
	@Autowired
	public void setLuceneDao(LuceneDao luceneDao) {
		this.luceneDao = luceneDao;
	}
	@Autowired
	public void setRegistreDao(RegistreDao registreDao) {
		this.registreDao = registreDao;
	}
	@Autowired
	public void setFormulariExternDao(FormulariExternDao formulariExternDao) {
		this.formulariExternDao = formulariExternDao;
	}
	@Autowired
	public void setPluginGestioDocumentalDao(
			PluginGestioDocumentalDao pluginGestioDocumentalDao) {
		this.pluginGestioDocumentalDao = pluginGestioDocumentalDao;
	}
	@Autowired
	public void setTerminiIniciatDao(
			TerminiIniciatDao terminiIniciatDao) {
		this.terminiIniciatDao = terminiIniciatDao;
	}
	@Autowired
	public void setAlertaDao(
			AlertaDao alertaDao) {
		this.alertaDao = alertaDao;
	}
	@Autowired
	public void setCampDao(
			CampDao campDao) {
		this.campDao = campDao;
	}
	@Autowired
	public void setConsultaCampDao(
			ConsultaCampDao consultaCampDao) {
		this.consultaCampDao = consultaCampDao;
	}
	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}



	private JbpmTask comprovarSeguretatTasca(Long entornId, String taskId, String usuari, boolean comprovarAssignacio) {
		JbpmTask task = jbpmDao.getTaskById(taskId);
		if (task == null) {
			throw new NotFoundException(
					getServiceUtils().getMessage("error.tascaService.noTrobada"));
		}
		Long tascaEntornId = getDadesCacheTasca(task).getEntornId();
		if (!tascaEntornId.equals(entornId)) {
			throw new NotFoundException(
					getServiceUtils().getMessage("error.tascaService.noTrobada"));
		}
		if (comprovarAssignacio) {
			if (usuari == null) {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				if (!auth.getName().equals(task.getAssignee()))
					throw new NotFoundException(
							getServiceUtils().getMessage("error.tascaService.noAssignada"));
			} else {
				if (!usuari.equals(task.getAssignee()))
					throw new NotFoundException(
							getServiceUtils().getMessage("error.tascaService.noAssignada"));
			}
		}
		if (task.isSuspended()) {
			throw new IllegalStateException(
					getServiceUtils().getMessage("error.tascaService.noDisponible"));
		}
		return task;
	}
	private TascaDto toTascaDto(JbpmTask task, Map<String, Object> varsCommand, boolean ambVariables) {
		return dtoConverter.toTascaDto(
				task,
				varsCommand,
				ambVariables,
				isTascaValidada(task),
				isDocumentsComplet(task),
				isSignaturesComplet(task));
	}
	private List<TascaLlistatDto> tasquesFiltradesPerEntorn(
			Long entornId,
			List<JbpmTask> tasques,
			boolean complet) {
		// Filtra les tasques per mostrar només les del entorn seleccionat
		List<TascaLlistatDto> filtrades = new ArrayList<TascaLlistatDto>();
		for (JbpmTask task: tasques) {
			DadesCacheTasca dadesCacheTasca = getDadesCacheTasca(task);
			Long currentEntornId = dadesCacheTasca.getEntornId();
			if (currentEntornId != null && entornId.equals(currentEntornId)) {
				TascaLlistatDto dto = toTascaLlistatDto(task, null);
				if (complet) {
					dto.setExpedientNumero(dadesCacheTasca.getNumeroIdentificador());
					Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
							task.getName(),
							task.getProcessDefinitionId());
					dto.setMissatgeInfo(tasca.getMissatgeInfo());
					dto.setMissatgeWarn(tasca.getMissatgeWarn());
					dto.setResultats(
							jbpmDao.findTaskInstanceOutcomes(task.getId()));
				}
				filtrades.add(dto);
			}
		}
		return filtrades;
	}
	private List<TascaLlistatDto> tasquesLlistatFiltradesValors(
			Long entornId,
			List<JbpmTask> tasques,
			String tasca,
			String expedient,
			Long tipusExpedient,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Integer prioritat,
			Date dataLimitInici,
			Date dataLimitFi,
			String columna,
			String ordre) {
		// Filtra les tasques per mostrar només les del entorn seleccionat
		List<TascaLlistatDto> filtrades = new ArrayList<TascaLlistatDto>();
		for (JbpmTask task: tasques) {
			DadesCacheTasca dadesCacheTasca = getDadesCacheTasca(task);
			Long currentEntornId = dadesCacheTasca.getEntornId();
			if ((currentEntornId != null) && (entornId.equals(currentEntornId))) {
				TascaLlistatDto dto = toTascaLlistatDto(task, dadesCacheTasca);
				dto.setTitol(
						dtoConverter.getTitolPerTasca(task));
				Boolean incloure = true;
				if ((tasca != null) && (!tasca.equals(""))) {
					String nomTasca = normalitzaText(dto.getTitol());
					String paramTasca = normalitzaText(tasca);
					incloure = incloure && (nomTasca.indexOf(paramTasca) != -1);
				}
				if ((expedient != null) && (!expedient.equals(""))) {
					String expedientTasca = normalitzaText(dadesCacheTasca.getIdentificador());
					String paramExpedient = normalitzaText(expedient);
					incloure = incloure && (expedientTasca.indexOf(paramExpedient) != -1);
				}
				if (tipusExpedient != null) {
					incloure = incloure && (tipusExpedient.longValue() == dadesCacheTasca.getExpedientTipusId());
				}
				Date dataCreacio = dto.getDataCreacio();
				if ((dataCreacioInici != null) && (dataCreacioFi != null)) {
					incloure = incloure && ((dataCreacio.compareTo(dataCreacioInici) >= 0) && (dataCreacio.compareTo(dataCreacioFi) <= 0));
				} else if (dataCreacioInici != null) {
					incloure = incloure && (dataCreacio.compareTo(dataCreacioInici) >= 0);
				} else if (dataCreacioFi != null) {
					incloure = incloure && (dataCreacio.compareTo(dataCreacioFi) <= 0);
				}
				if (prioritat != null) {
					incloure = incloure && (prioritat.intValue() == dto.getPrioritat());
				}
				Date dataLimit = dto.getDataLimit();
				if ((dataLimitInici != null) && (dataLimitFi != null)) {
					incloure = incloure && ((dataLimit != null) && (dataLimit.compareTo(dataLimitInici) >= 0) && (dataLimit.compareTo(dataLimitFi) <= 0));
				} else if (dataLimitInici != null) {
					incloure = incloure && ((dataLimit != null) && (dataLimit.compareTo(dataLimitInici) >= 0));
				} else if (dataLimitFi != null) {
					incloure = incloure && ((dataLimit != null) && (dataLimit.compareTo(dataLimitFi) <= 0));
				}
				if (incloure)
					filtrades.add(dto);
			}
		}
		final Integer col = Integer.parseInt((columna!=null) ? columna  :"0");
		final Integer ord = Integer.parseInt((ordre!=null) ? ordre : "0");
		Comparator<TascaLlistatDto> comparador = new Comparator<TascaLlistatDto>() {
			public int compare(TascaLlistatDto t1, TascaLlistatDto t2) {
				Integer result = 0;
				switch (col) {
					// Tasca
					case 0:
						if (ord == 1)
							result = t1.getTitol().compareTo(t2.getTitol());
						else if (ord == 2)
							result = t2.getTitol().compareTo(t1.getTitol());
						break;
					// Expedient
					case 1:
						if (ord == 1)
							result = t1.getExpedientTitolOrdenacio().compareTo(t2.getExpedientTitolOrdenacio());
						else if (ord == 2)
							result = t2.getExpedientTitolOrdenacio().compareTo(t1.getExpedientTitolOrdenacio());
						break;
					// Tipus d'expedient
					case 2:
						if (ord == 1)
							result = t1.getExpedientTipusId().compareTo(t2.getExpedientTipusId());
						else if (ord == 2)
							result = t2.getExpedientTipusId().compareTo(t1.getExpedientTipusId());
						break;
					// Data creació
					case 3:
						if (ord == 1)
							result = t1.getDataCreacio().compareTo(t2.getDataCreacio());
						else if (ord == 2)
							result = t2.getDataCreacio().compareTo(t1.getDataCreacio());
						break;
					// Prioritat
					case 4:
						if (ord == 1)
							result = t1.getPrioritat() - t2.getPrioritat();
						else if (ord == 2)
							result = t2.getPrioritat() - t1.getPrioritat();
						break;
					// Data límit
					case 5:
						if ((t1.getDataLimit() != null) && (t2.getDataLimit() != null)) {
							if (ord == 1)
								result = t1.getDataLimit().compareTo(t2.getDataLimit());
							else if (ord == 2)
								result = t2.getDataLimit().compareTo(t1.getDataLimit());
						}
						break;
					default:
						break;
				}
				return result;
			}
		};
		Collections.sort(filtrades, comparador);
		return filtrades;
	}
	private List<TascaLlistatDto> tasquesPerTramitacioMasiva(
			Long entornId,
			List<JbpmTask> tasques,
			String codi,
			String jbpmKey) {
		// Filtra les tasques per mostrar només les del entorn seleccionat
		List<TascaLlistatDto> resposta = new ArrayList<TascaLlistatDto>();
		for (JbpmTask task: tasques) {
			DadesCacheTasca dadesCacheTasca = getDadesCacheTasca(task);
			Long currentEntornId = dadesCacheTasca.getEntornId();
			if (currentEntornId != null && entornId.equals(currentEntornId)) {
				TascaLlistatDto dto = toTascaLlistatDto(task, dadesCacheTasca);
				if (codi.equals(task.getName()) && jbpmKey.equals(dadesCacheTasca.getDefinicioProcesJbpmKey())) {
					resposta.add(dto);
				}
			}
		}
		return resposta;
	}

	private String normalitzaText(String text) {
		return text
			.toUpperCase()
			.replaceAll("Á", "A")
			.replaceAll("À", "A")
			.replaceAll("É", "E")
			.replaceAll("È", "E")
			.replaceAll("Í", "I")
			.replaceAll("Ï", "I")
			.replaceAll("Ó", "O")
			.replaceAll("Ò", "O")
			.replaceAll("Ú", "U")
			.replaceAll("Ü", "U");
	}

	private void validarTasca(String taskId) {
		jbpmDao.setTaskInstanceVariable(
				taskId,
				VAR_TASCA_VALIDADA,
				new Date());
	}
	private void restaurarTasca(String taskId) {
		jbpmDao.deleteTaskInstanceVariable(taskId, VAR_TASCA_VALIDADA);
	}
	private boolean isTascaValidada(JbpmTask task) {
		Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
				task.getName(),
				task.getProcessDefinitionId());
		boolean hiHaCampsModificables = false;
		for (CampTasca camp: tasca.getCamps()) {
			if (!camp.isReadOnly()) {
				hiHaCampsModificables = true;
				break;
			}
		}
		if (!hiHaCampsModificables)
			return true;
		Object valor = jbpmDao.getTaskInstanceVariable(task.getId(), VAR_TASCA_VALIDADA);
		if (valor == null || !(valor instanceof Date))
			return false;
		return true;
	}
	private boolean isDocumentsComplet(JbpmTask task) {
		boolean ok = true;
		Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
				task.getName(),
				task.getProcessDefinitionId());
		for (DocumentTasca docTasca: tasca.getDocuments()) {
			if (docTasca.isRequired()) {
				String codiJbpm = PREFIX_DOCUMENT + docTasca.getDocument().getCodi();
				Object valor = jbpmDao.getTaskInstanceVariable(
						task.getId(),
						codiJbpm);
				if (valor == null) {
					ok = false;
					break;
				}
			}
		}
		return ok;
	}
	private boolean isSignaturesComplet(JbpmTask task) {
		boolean ok = true;
		Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
				task.getName(),
				task.getProcessDefinitionId());
		for (FirmaTasca firmaTasca: tasca.getFirmes()) {
			if (firmaTasca.isRequired()) {
				String codiJbpm = PREFIX_SIGNATURA + firmaTasca.getDocument().getCodi();
				Object valor = jbpmDao.getTaskInstanceVariable(task.getId(), codiJbpm);
				if (valor == null)
					ok = false;
			}
		}
		return ok;
	}

	private void createDelegationInfo(
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
		jbpmDao.setTaskInstanceVariable(
				task.getId(), 
				VAR_TASCA_DELEGACIO,
				info);
	}
	private DelegationInfo getDelegationInfo(JbpmTask task) {
		return (DelegationInfo)jbpmDao.getTaskInstanceVariable(
				task.getId(),
				VAR_TASCA_DELEGACIO);
	}
	private void deleteDelegationInfo(JbpmTask task) {
		jbpmDao.deleteTaskInstanceVariable(
				task.getId(),
				VAR_TASCA_DELEGACIO);
	}
	private Map<String, Object> getVariablesDelegacio(JbpmTask task) {
		return jbpmDao.getTaskInstanceVariables(task.getId());
	}

	private String nomArxiuAmbExtensio(String fileName, String extensio) {
		if (extensio == null || extensio.length() == 0)
			return fileName;
		int indexPunt = fileName.lastIndexOf(".");
		if (indexPunt != -1) {
			String nom = fileName.substring(0, indexPunt);
			return nom + "." + extensio;
		} else {
			return fileName + "." + extensio;
		}
	}

	private boolean isActiuConversioVista() {
		String actiuConversio = (String)GlobalProperties.getInstance().get("app.conversio.actiu");
		if (!"true".equalsIgnoreCase(actiuConversio))
			return false;
		String actiuConversioVista = (String)GlobalProperties.getInstance().get("app.conversio.vista.actiu");
		if (actiuConversioVista == null)
			actiuConversioVista = (String)GlobalProperties.getInstance().get("app.conversio.gentasca.actiu");
		return "true".equalsIgnoreCase(actiuConversioVista);
	}
	private boolean isActiuConversioSignatura() {
		String actiuConversio = (String)GlobalProperties.getInstance().get("app.conversio.actiu");
		if (!"true".equalsIgnoreCase(actiuConversio))
			return false;
		String actiuConversioVista = (String)GlobalProperties.getInstance().get("app.conversio.signatura.actiu");
		return "true".equalsIgnoreCase(actiuConversioVista);
	}

	private String getExtensioVista(Document document) {
		String extensioVista = null;
		if (isActiuConversioVista()) {
			if (document.getConvertirExtensio() != null && document.getConvertirExtensio().length() > 0) {
				extensioVista = document.getConvertirExtensio();
			} else {
				extensioVista = (String)GlobalProperties.getInstance().get("app.conversio.vista.extension");
				if (extensioVista == null)
					extensioVista = (String)GlobalProperties.getInstance().get("app.conversio.gentasca.extension");
			}
		}
		return extensioVista;
	}
	private String getExtensioSignatura() {
		String extensioVista = null;
		if (isActiuConversioSignatura()) {
			extensioVista = (String)GlobalProperties.getInstance().get("app.conversio.signatura.extension");
		}
		return extensioVista;
	}

	private TascaLlistatDto toTascaLlistatDto(
			JbpmTask task,
			DadesCacheTasca dadesCacheTasca) {
		TascaLlistatDto dto = new TascaLlistatDto();
		dto.setId(task.getId());
		dto.setCodi(task.getName());
		dto.setTitol(task.getName());
		dto.setDataCreacio(task.getCreateTime());
		dto.setDataInici(task.getStartTime());
		dto.setDataFi(task.getEndTime());
		dto.setDataLimit(task.getDueDate());
		dto.setPrioritat(task.getPriority());
		dto.setResponsable(task.getAssignee());
		dto.setResponsables(task.getPooledActors());
		dto.setOberta(task.isOpen());
		dto.setCompletada(task.isCompleted());
		dto.setCancelada(task.isCancelled());
		dto.setSuspesa(task.isSuspended());
		dto.setProcessInstanceId(task.getProcessInstanceId());
		if (dadesCacheTasca != null) {
			dto.setExpedientTitol(dadesCacheTasca.getIdentificador());
			dto.setExpedientTitolOrdenacio(dadesCacheTasca.getIdentificadorOrdenacio());
			dto.setExpedientTipusId(dadesCacheTasca.getExpedientTipusId());
			dto.setExpedientTipusNom(dadesCacheTasca.getExpedientTipusNom());
			dto.setExpedientProcessInstanceId(dadesCacheTasca.getProcessInstanceId());
			dto.setTramitacioMassiva(dadesCacheTasca.isTramitacioMassiva());
		}
		return dto;
	}

	private void actualitzarTerminisIniciatsIAlertes(String taskId, Expedient expedient) {
		List<TerminiIniciat> terminisIniciats = terminiIniciatDao.findAmbTaskInstanceId(
				new Long(taskId));
		for (TerminiIniciat terminiIniciat: terminisIniciats) {
			terminiIniciat.setDataCompletat(new Date());
			esborrarAlertesAntigues(terminiIniciat);
			if (terminiIniciat.getTermini().isAlertaCompletat()) {
				JbpmTask task = jbpmDao.getTaskById(taskId);
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
		alertaDao.saveOrUpdate(alerta);
	}
	private void esborrarAlertesAntigues(TerminiIniciat terminiIniciat) {
		List<Alerta> antigues = alertaDao.findActivesAmbTerminiIniciatId(terminiIniciat.getId());
		for (Alerta antiga: antigues)
			antiga.setDataEliminacio(new Date());
	}

	private OpenOfficeUtils getOpenOfficeUtils() {
		if (openOfficeUtils == null)
			openOfficeUtils = new OpenOfficeUtils();
		return openOfficeUtils;
	}
	private ServiceUtils getServiceUtils() {
		if (serviceUtils == null) {
			serviceUtils = new ServiceUtils(
					definicioProcesDao,
					campDao,
					consultaCampDao,
					dtoConverter,
					jbpmDao,
					aclServiceDao,
					messageSource);
		}
		return serviceUtils;
	}
	private DocumentTokenUtils getDocumentTokenUtils() {
		if (documentTokenUtils == null)
			documentTokenUtils = new DocumentTokenUtils(
					(String)GlobalProperties.getInstance().get("app.encriptacio.clau"));
		return documentTokenUtils;
	}

	private boolean isExpedientFinalitzat(Expedient expedient) {
		if (expedient.getProcessInstanceId() != null) {
			JbpmProcessInstance processInstance = jbpmDao.getProcessInstance(expedient.getProcessInstanceId());
			return processInstance.getEnd() != null;
		}
		return false;
	}

	private DadesCacheTasca getDadesCacheTasca(JbpmTask task) {
		if (dadesCacheTasques == null)
			dadesCacheTasques = new HashMap<String, DadesCacheTasca>();
		DadesCacheTasca dades = dadesCacheTasques.get(task.getId());
		if (dades == null) {
			String rootProcessInstanceId = jbpmDao.getRootProcessInstance(task.getProcessInstanceId()).getId();
			Expedient expedientPerTasca = expedientDao.findAmbProcessInstanceId(rootProcessInstanceId);
			Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
					task.getName(),
					task.getProcessDefinitionId());
			dades = new DadesCacheTasca(
					expedientPerTasca.getEntorn().getId(),
					expedientPerTasca.getIdentificador(),
					expedientPerTasca.getIdentificadorOrdenacio(),
					expedientPerTasca.getNumeroIdentificador(),
					expedientPerTasca.getTipus().getId(),
					expedientPerTasca.getTipus().getNom(),
					expedientPerTasca.getProcessInstanceId(),
					tasca.isTramitacioMassiva(),
					tasca.getDefinicioProces().getJbpmKey());
			dadesCacheTasques.put(task.getId(), dades);
		}
		return dades;
	}
	private class DadesCacheTasca {
		private Long entornId;
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
				String identificador,
				String identificadorOrdenacio,
				String numeroIdentificador,
				Long expedientTipusId,
				String expedientTipusNom,
				String processInstanceId,
				boolean tramitacioMassiva,
				String definicioProcesJbpmKey) {
			this.entornId = entornId;
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

	private static final Log logger = LogFactory.getLog(TascaService.class);

}
