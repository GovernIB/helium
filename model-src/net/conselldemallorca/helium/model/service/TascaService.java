/**
 * 
 */
package net.conselldemallorca.helium.model.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.conselldemallorca.helium.integracio.domini.FilaResultat;
import net.conselldemallorca.helium.jbpm3.integracio.DelegationInfo;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmDao;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.model.dao.DefinicioProcesDao;
import net.conselldemallorca.helium.model.dao.DocumentDao;
import net.conselldemallorca.helium.model.dao.DocumentStoreDao;
import net.conselldemallorca.helium.model.dao.ExpedientDao;
import net.conselldemallorca.helium.model.dao.FormulariExternDao;
import net.conselldemallorca.helium.model.dao.LuceneDao;
import net.conselldemallorca.helium.model.dao.PlantillaDocumentDao;
import net.conselldemallorca.helium.model.dao.PluginCustodiaDao;
import net.conselldemallorca.helium.model.dao.RegistreDao;
import net.conselldemallorca.helium.model.dao.TascaDao;
import net.conselldemallorca.helium.model.dto.DocumentDto;
import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.model.dto.TascaDto;
import net.conselldemallorca.helium.model.exception.DominiException;
import net.conselldemallorca.helium.model.exception.IllegalArgumentsException;
import net.conselldemallorca.helium.model.exception.IllegalStateException;
import net.conselldemallorca.helium.model.exception.NotFoundException;
import net.conselldemallorca.helium.model.exception.TemplateException;
import net.conselldemallorca.helium.model.hibernate.Camp;
import net.conselldemallorca.helium.model.hibernate.CampTasca;
import net.conselldemallorca.helium.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.model.hibernate.Document;
import net.conselldemallorca.helium.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.Expedient;
import net.conselldemallorca.helium.model.hibernate.FirmaTasca;
import net.conselldemallorca.helium.model.hibernate.FormulariExtern;
import net.conselldemallorca.helium.model.hibernate.Tasca;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


/**
 * Servei per gestionar les tasques assignades a una persona
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Service
public class TascaService {

	public static final String VAR_PREFIX = "H3l1um#";

	public static final String VAR_TASCA_TITOLNOU = "H3l1um#tasca.titolnou";
	public static final String VAR_TASCA_EVALUADA = "H3l1um#tasca.evaluada";
	public static final String VAR_TASCA_VALIDADA = "H3l1um#tasca.validada";
	public static final String VAR_TASCA_DELEGACIO = "H3l1um#tasca.delegacio";

	public static final String PREFIX_SIGNATURA = "H3l1um#signatura.";
	public static final String PREFIX_DOCUMENT = "H3l1um#document.";
	public static final String PREFIX_ADJUNT = "H3l1um#adjunt.";

	private ExpedientDao expedientDao;
	private TascaDao tascaDao;
	private DefinicioProcesDao definicioProcesDao;
	private DocumentDao documentDao;
	private JbpmDao jbpmDao;
	private DocumentStoreDao documentStoreDao;
	private PlantillaDocumentDao plantillaDocumentDao;
	private PluginCustodiaDao pluginCustodiaDao;
	private DtoConverter dtoConverter;
	private LuceneDao luceneDao;
	private RegistreDao registreDao;
	private FormulariExternDao formulariExternDao;



	public TascaDto getById(Long entornId, String taskId) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, true);
		return toTascaDto(task, true);
	}
	public List<TascaDto> findTasquesPersonals(Long entornId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<JbpmTask> tasques = jbpmDao.findPersonalTasks(auth.getName());
		return tasquesFiltrades(entornId, tasques);
	}
	public List<TascaDto> findTasquesGrup(Long entornId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<JbpmTask> tasques = jbpmDao.findGroupTasks(auth.getName());
		return tasquesFiltrades(entornId, tasques);
	}
	public TascaDto agafar(Long entornId, String taskId) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, false);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		jbpmDao.takeTaskInstance(taskId, auth.getName());
		TascaDto tasca = toTascaDto(task, true);
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
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, true);
		boolean iniciada = task.getStartTime() == null;
		jbpmDao.startTaskInstance(taskId);
		jbpmDao.setTaskInstanceVariables(taskId, variables);
		TascaDto tasca = toTascaDto(task, true);
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
	public TascaDto validar(
			Long entornId,
			String taskId,
			Map<String, Object> variables,
			boolean comprovarAssignacio) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, comprovarAssignacio);
		jbpmDao.startTaskInstance(taskId);
		jbpmDao.setTaskInstanceVariables(taskId, variables);
		validarTasca(taskId);
		TascaDto tasca = toTascaDto(task, true);
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
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, true);
		if (!isTascaValidada(task))
			throw new IllegalStateException("La tasca no ha estat validada");
		//deleteDocumentsTasca(taskId);
		restaurarTasca(taskId);
		TascaDto tasca = toTascaDto(task, true);
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
			boolean comprovarAssignacio) {
		completar(entornId, taskId, comprovarAssignacio, null);
	}
	public void completar(
			Long entornId,
			String taskId,
			boolean comprovarAssignacio,
			String outcome) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, comprovarAssignacio);
		if (!isTascaValidada(task))
			throw new IllegalStateException("El formulari no ha estat validat");
		if (!isDocumentsComplet(task))
			throw new IllegalStateException("Falten documents per adjuntar");
		if (!isSignaturesComplet(task))
			throw new IllegalStateException("Falten documents per signar");
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
					completar(entornId, taskOriginal.getId(), false, outcome);
				}
				deleteDelegationInfo(taskOriginal);
			}
		}
		JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(task.getProcessInstanceId());
		Expedient expedient = expedientDao.findAmbProcessInstanceId(pi.getId());
		luceneDao.updateExpedient(
				expedient,
				getMapDefinicionsProces(expedient),
				getMapCamps(expedient),
				getMapValorsJbpm(expedient));
		TascaDto tasca = toTascaDto(task, true);
		registreDao.crearRegistreFinalitzarTasca(
				tasca.getExpedient().getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				"Finalitzar \"" + tasca.getNom() + "\"");
	}

	public Object getVariable(
			Long entornId,
			String taskId,
			String codiVariable) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, true);
		return jbpmDao.getTaskInstanceVariable(task.getId(), codiVariable);
	}
	public void createVariable(
			Long entornId,
			String taskId,
			String codiVariable,
			Object valor) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, true);
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(codiVariable, valor);
		jbpmDao.setTaskInstanceVariables(task.getId(), variables);
		TascaDto tasca = toTascaDto(task, true);
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
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, true);
		Object valorVell = jbpmDao.getTaskInstanceVariable(taskId, codiVariable);
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(codiVariable, valor);
		jbpmDao.setTaskInstanceVariables(task.getId(), variables);
		TascaDto tasca = toTascaDto(task, true);
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
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, true);
		jbpmDao.deleteTaskInstanceVariable(task.getId(), codiVariable);
		TascaDto tasca = toTascaDto(task, true);
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
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, true);
		if (!isTascaValidada(task))
			throw new IllegalStateException("La tasca no ha estat validada");
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
		if (!creat) {
			nomArxiuAntic = documentStoreDao.getById(docStoreId, false).getArxiuNom();
			documentStoreDao.update(
					docStoreId,
					expedient,
					document.getNom(),
					data,
					nom,
					contingut);
		} else {
			docStoreId = documentStoreDao.create(
					expedient,
					task.getProcessInstanceId(),
					codiVariableJbpm,
					document.getNom(),
					data,
					nom,
					contingut,
					false);
		}
		// Guarda la referència al nou document a dins el jBPM
		jbpmDao.setTaskInstanceVariable(
				task.getId(),
				codiVariableJbpm,
				docStoreId);
		TascaDto tasca = toTascaDto(task, true);
		// Registra l'acció
		if (creat) {
			registreDao.crearRegistreCrearDocumentTasca(
					tasca.getExpedient().getId(),
					taskId,
					SecurityContextHolder.getContext().getAuthentication().getName(),
					document.getCodi(),
					nom);
		} else {
			registreDao.crearRegistreModificarDocumentTasca(
					tasca.getExpedient().getId(),
					taskId,
					SecurityContextHolder.getContext().getAuthentication().getName(),
					document.getCodi(),
					nomArxiuAntic,
					nom);
		}
	}
	public void esborrarDocument(
			Long entornId,
			String taskId,
			String documentCodi) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, true);
		String codiVariableJbpm = PREFIX_DOCUMENT + documentCodi;
		Long documentJbpm = (Long)jbpmDao.getTaskInstanceVariable(taskId, codiVariableJbpm);
		if (documentJbpm == null)
			documentJbpm = (Long)jbpmDao.getProcessInstanceVariable(task.getProcessInstanceId(), codiVariableJbpm);
		if (documentJbpm != null) {
			DocumentStore documentStore = documentStoreDao.getById(documentJbpm, false);
			if (documentStore != null) {
				if (documentStore.isSignat())
					pluginCustodiaDao.deleteDocumentAmbSignatura(documentStore.getReferenciaCustodia());
				documentStoreDao.delete(documentJbpm);
			}
			if (jbpmDao.getTaskInstanceVariable(taskId, codiVariableJbpm) != null)
				jbpmDao.deleteTaskInstanceVariable(
						taskId,
						codiVariableJbpm);
			else
				jbpmDao.deleteProcessInstanceVariable(
						task.getProcessInstanceId(),
						codiVariableJbpm);
		}
		TascaDto tasca = toTascaDto(task, true);
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
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, true);
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
		return dtoConverter.toDocumentDto(documentStoreId, true);
	}

	public String getTokenPerDocument(
			Long entornId,
			String taskId,
			String codiVariable) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, true);
		Long documentStoreId = (Long)jbpmDao.getProcessInstanceVariable(
				task.getProcessInstanceId(),
				PREFIX_DOCUMENT + codiVariable);
		if (documentStoreId == null)
			return null;
		DocumentStore document = documentStoreDao.getById(documentStoreId, false);
		return document.getId().toString();
	}
	public DocumentDto getDocumentAmbToken(
			String token,
			boolean ambContingut) {
		String[] parts = token.split("#");
		if (parts.length == 2) {
			Long documentStoreId = Long.parseLong(parts[1]);
			return dtoConverter.toDocumentDto(documentStoreId, ambContingut);
		}
		throw new IllegalArgumentsException("El format del token és incorrecte");
	}
	public void signarDocumentAmbToken(
			String token,
			String nomArxiu,
			Object signatura) {
		String[] parts = token.split("#");
		if (parts.length != 2)
			throw new IllegalArgumentsException("El format del token és incorrecte");
		Long documentStoreId = Long.parseLong(parts[1]);
		DocumentDto document = dtoConverter.toDocumentDto(documentStoreId, true);
		if (document != null) {
			DocumentStore docst = documentStoreDao.getById(documentStoreId, false);
			JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(docst.getProcessInstanceId());
			Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
			String varDocumentCodi = docst.getJbpmVariable().substring(PREFIX_DOCUMENT.length());
			JbpmProcessInstance processInstance = jbpmDao.getProcessInstance(docst.getProcessInstanceId());
			String referenciaCustodia = pluginCustodiaDao.afegirDocumentAmbSignatura(
					expedient,
					definicioProcesDao.findAmbJbpmId(processInstance.getProcessDefinitionId()),
					varDocumentCodi,
					document,
					nomArxiu,
					signatura);
			docst.setReferenciaCustodia(referenciaCustodia);
			docst.setSignat(true);
			registreDao.crearRegistreSignarDocument(
					expedient.getId(),
					docst.getProcessInstanceId(),
					SecurityContextHolder.getContext().getAuthentication().getName(),
					varDocumentCodi);
			jbpmDao.setTaskInstanceVariable(
					parts[0],
					PREFIX_SIGNATURA + varDocumentCodi,
					documentStoreId);
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
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, true);
		if (document.isPlantilla()) {
			JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(task.getProcessInstanceId());
			ExpedientDto expedient = dtoConverter.toExpedientDto(
					expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId()),
					false);
			TascaDto tasca = toTascaDto(task, true);
			InstanciaProcesDto instanciaProces = dtoConverter.toInstanciaProcesDto(
					task.getProcessInstanceId(),
					true);
			Map<String, Object> model = new HashMap<String, Object>();
			model.putAll(instanciaProces.getVarsComText());
			/*for (String key: model.keySet())
				System.out.println(">P [" + key + "]" + model.get(key));*/
			model.putAll(tasca.getVarsComText());
			/*for (String key: model.keySet())
				System.out.println(">PiT [" + key + "]" + model.get(key));*/
			try {
				resposta.setArxiuContingut(plantillaDocumentDao.generarDocumentAmbPlantilla(
						entornId,
						document,
						task.getAssignee(),
						expedient,
						task.getProcessInstanceId(),
						tasca,
						dataDocument,
						model));
			} catch (Exception ex) {
				throw new TemplateException("No s'ha pogut generar el document", ex);
			}
		} else {
			resposta.setArxiuContingut(document.getArxiuContingut());
		}
		return resposta;
	}

	public void delegacioCrear(
			Long entornId,
			String taskId,
			String actorId,
			String comentari,
			boolean supervisada) {
		JbpmTask original = comprovarSeguretatTasca(entornId, taskId, true);
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
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, true);
		DelegationInfo delegationInfo = getDelegationInfo(task);
		if (delegationInfo == null || !taskId.equals(delegationInfo.getSourceTaskId())) {
			throw new IllegalStateException("No es pot cancel·lar la delegació d'aquesta tasca");
		}
		// Cancelar la tasca delegada
		jbpmDao.cancelTaskInstance(delegationInfo.getTargetTaskId());
		// Esborram la delegació
		deleteDelegationInfo(task);
	}

	public FormulariExtern iniciarFormulariExtern(
			Long entornId,
			String taskId) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, true);
		Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
				task.getName(),
				task.getProcessDefinitionId());
		if (tasca.getFormExtern() == null)
			throw new IllegalStateException("Aquesta tasca no té definit cap formulari extern");
		Map<String, Object> vars = jbpmDao.getTaskInstanceVariables(task.getId());
		return formulariExternDao.iniciarFormulariExtern(
				taskId,
				tasca.getFormExtern(),
				vars);
	}

	public void guardarFormulariExtern(
			String formulariId,
			Map<String, Object> variables) {
		FormulariExtern formExtern = formulariExternDao.findAmbFormulariId(formulariId);
		if (formExtern != null) {
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
					entornPerTasca(task).getId(),
					formExtern.getTaskId(),
					valors,
					false);
			formExtern.setDataRecepcioDades(new Date());
		}
	}

	public List<FilaResultat> getValorsCampSelect(
			String taskId,
			String campCodi,
			String textInicial) throws DominiException {
		return dtoConverter.getResultatConsultaDomini(taskId, null, campCodi, textInicial);
	}

	@Autowired
	public void setExpedientDao(ExpedientDao expedientDao) {
		this.expedientDao = expedientDao;
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



	private JbpmTask comprovarSeguretatTasca(Long entornId, String taskId, boolean comprovarAssignacio) {
		JbpmTask task = jbpmDao.getTaskById(taskId);
		if (task == null) {
			throw new NotFoundException("No s'ha trobat la tasca");
		}
		Entorn entorn = entornPerTasca(task);
		if (entorn == null || !entorn.getId().equals(entornId)) {
			throw new NotFoundException("No s'ha trobat la tasca");
		}
		if (comprovarAssignacio) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (!auth.getName().equals(task.getAssignee())) {
				throw new NotFoundException("Aquest usuari no té aquesta tasca assignada");
			}
		}
		if (task.isSuspended()) {
			throw new IllegalStateException("Aquesta tasca no està disponible");
		}
		return task;
	}
	private TascaDto toTascaDto(JbpmTask task, boolean ambVariables) {
		if (!isTascaEvaluada(task))
			evaluarTasca(task);
		return dtoConverter.toTascaDto(
				task,
				ambVariables,
				isTascaValidada(task),
				isDocumentsComplet(task),
				isSignaturesComplet(task));
	}
	private List<TascaDto> tasquesFiltrades(
			Long entornId,
			List<JbpmTask> tasques) {
		// Filtra les tasques per mostrar només les del entorn seleccionat
		List<TascaDto> filtrades = new ArrayList<TascaDto>();
		for (JbpmTask task: tasques) {
			Long currentEntornId = entornPerTasca(task).getId();
			if (currentEntornId != null && entornId.equals(currentEntornId))
				filtrades.add(toTascaDto(task, false));
		}
		return filtrades;
	}
	private Entorn entornPerTasca(JbpmTask tasca) {
		Expedient expedient = expedientDao.findAmbProcessInstanceId(
				jbpmDao.getRootProcessInstance(tasca.getProcessInstanceId()).getId());
		if (expedient != null)
			return expedient.getEntorn();
		return null;
	}

	private void validarTasca(String taskId) {
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(VAR_TASCA_VALIDADA, new Date());
		jbpmDao.setTaskInstanceVariables(taskId, variables);
	}
	private void restaurarTasca(String taskId) {
		jbpmDao.deleteTaskInstanceVariable(taskId, VAR_TASCA_VALIDADA);
	}
	private String evaluarTasca(JbpmTask task) {
		Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
				task.getName(),
				task.getProcessDefinitionId());
		String titolNou = null;
		if (tasca.getNomScript() != null) {
			try {
				Set<String> outputVars = new HashSet<String>();
				outputVars.add("titol");
				Map<String,Object> resultat = jbpmDao.evaluateScript(
						task.getProcessInstanceId(),
						tasca.getNomScript(),
						outputVars);
				if (resultat.get("titol") != null)
					titolNou = resultat.get("titol").toString();
			} catch (Exception ignored) {}
		}
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(VAR_TASCA_EVALUADA, new Date());
		if (titolNou != null)
			variables.put(VAR_TASCA_TITOLNOU, titolNou);
		jbpmDao.setTaskInstanceVariables(task.getId(), variables);
		return titolNou;
	}
	private boolean isTascaEvaluada(JbpmTask task) {
		Object valor = jbpmDao.getTaskInstanceVariable(task.getId(), VAR_TASCA_EVALUADA);
		if (valor == null || !(valor instanceof Date))
			return false;
		return true;
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
	/*private void deleteDocumentsTasca(String taskId) {
		Map<String, Object> valors = jbpmDao.getTaskInstanceVariables(taskId);
		for (String codi: valors.keySet()) {
			if (codi.startsWith(PREFIX_DOCUMENT))
				jbpmDao.deleteTaskInstanceVariable(taskId, codi);
		}
	}*/

	private Map<String, DefinicioProces> getMapDefinicionsProces(Expedient expedient) {
		Map<String, DefinicioProces> resposta = new HashMap<String, DefinicioProces>();
		List<JbpmProcessInstance> tree = jbpmDao.getProcessInstanceTree(expedient.getProcessInstanceId());
		for (JbpmProcessInstance pi: tree)
			resposta.put(
					pi.getId(),
					definicioProcesDao.findAmbJbpmId(pi.getProcessDefinitionId()));
		return resposta;
	}
	private Map<String, Set<Camp>> getMapCamps(Expedient expedient) {
		Map<String, Set<Camp>> resposta = new HashMap<String, Set<Camp>>();
		List<JbpmProcessInstance> tree = jbpmDao.getProcessInstanceTree(expedient.getProcessInstanceId());
		for (JbpmProcessInstance pi: tree) {
			resposta.put(
					pi.getId(),
					definicioProcesDao.findAmbJbpmId(pi.getProcessDefinitionId()).getCamps());
		}
		return resposta;
	}
	private Map<String, Map<String, Object>> getMapValorsJbpm(Expedient expedient) {
		Map<String, Map<String, Object>> resposta = new HashMap<String, Map<String, Object>>();
		List<JbpmProcessInstance> tree = jbpmDao.getProcessInstanceTree(expedient.getProcessInstanceId());
		for (JbpmProcessInstance pi: tree)
			resposta.put(
					pi.getId(),
					jbpmDao.getProcessInstanceVariables(pi.getId()));
		return resposta;
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

}
