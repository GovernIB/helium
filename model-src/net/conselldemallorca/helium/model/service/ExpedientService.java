/**
 * 
 */
package net.conselldemallorca.helium.model.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.conselldemallorca.helium.integracio.domini.FilaResultat;
import net.conselldemallorca.helium.integracio.plugins.custodia.SignaturaInfo;
import net.conselldemallorca.helium.integracio.plugins.persones.Persona;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmDao;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmNodePosition;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessDefinition;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmToken;
import net.conselldemallorca.helium.model.dao.ConsultaDao;
import net.conselldemallorca.helium.model.dao.DefinicioProcesDao;
import net.conselldemallorca.helium.model.dao.DocumentDao;
import net.conselldemallorca.helium.model.dao.DocumentStoreDao;
import net.conselldemallorca.helium.model.dao.EntornDao;
import net.conselldemallorca.helium.model.dao.EstatDao;
import net.conselldemallorca.helium.model.dao.ExpedientDao;
import net.conselldemallorca.helium.model.dao.ExpedientTipusDao;
import net.conselldemallorca.helium.model.dao.LuceneDao;
import net.conselldemallorca.helium.model.dao.PluginCustodiaDao;
import net.conselldemallorca.helium.model.dao.PluginPortasignaturesDao;
import net.conselldemallorca.helium.model.dao.RegistreDao;
import net.conselldemallorca.helium.model.dao.TerminiIniciatDao;
import net.conselldemallorca.helium.model.dto.DocumentDto;
import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.model.dto.TascaDto;
import net.conselldemallorca.helium.model.dto.TokenDto;
import net.conselldemallorca.helium.model.exception.DominiException;
import net.conselldemallorca.helium.model.exception.IllegalArgumentsException;
import net.conselldemallorca.helium.model.exception.NotFoundException;
import net.conselldemallorca.helium.model.hibernate.Camp;
import net.conselldemallorca.helium.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.model.hibernate.Document;
import net.conselldemallorca.helium.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.Expedient;
import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.model.hibernate.Registre;
import net.conselldemallorca.helium.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.model.hibernate.Portasignatures.TipusEstat;
import net.conselldemallorca.helium.model.hibernate.Portasignatures.Transicio;
import net.conselldemallorca.helium.util.ExpedientIniciant;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Servei per a gestionar els expedients
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Service
public class ExpedientService {

	private ExpedientDao expedientDao;
	private ExpedientTipusDao expedientTipusDao;
	private DefinicioProcesDao definicioProcesDao;
	private EntornDao entornDao;
	private DocumentDao documentDao;
	private DocumentStoreDao documentStoreDao;
	private EstatDao estatDao;
	private LuceneDao luceneDao;
	private ConsultaDao consultaDao;
	private PluginCustodiaDao pluginCustodiaDao;
	private RegistreDao registreDao;
	private TerminiIniciatDao terminiIniciatDao;
	private PluginPortasignaturesDao pluginPortasignaturesDao;

	private JbpmDao jbpmDao;
	private DtoConverter dtoConverter;



	public ExpedientDto getById(Long id) {
		Expedient expedient = expedientDao.getById(id, false);
		if (expedient != null)
			return dtoConverter.toExpedientDto(expedient, false);
		return null;
	}

	public TascaDto getStartTask(
			Long entornId,
			Long expedientTipusId,
			Long definicioProcesId,
			Map<String, Object> valors) {
		ExpedientTipus expedientTipus = expedientTipusDao.getById(expedientTipusId, false);
		DefinicioProces definicioProces = null;
		if (definicioProcesId != null) {
			definicioProces = definicioProcesDao.getById(definicioProcesId, false);
		} else {
			definicioProces = definicioProcesDao.findDarreraVersioAmbEntornIJbpmKey(
					entornId,
					expedientTipus.getJbpmProcessDefinitionKey());
		}
		String startTaskName = jbpmDao.getStartTaskName(definicioProces.getJbpmId());
		if (startTaskName != null)
			return dtoConverter.toTascaInicialDto(startTaskName, definicioProces.getJbpmId(), valors);
		return null;
	}

	public ExpedientDto iniciar(
			Long entornId,
			Long expedientTipusId,
			Long definicioProcesId,
			String numero,
			String titol,
			Map<String, Object> variables,
			String transitionName,
			IniciadorTipus iniciadorTipus,
			String iniciadorCodi,
			String responsableCodi) {
		ExpedientTipus expedientTipus = expedientTipusDao.getById(expedientTipusId, false);
		if (expedientTipus.getTeNumero().booleanValue() && expedientTipus.getDemanaNumero().booleanValue()) {
			if (numero == null || numero.length() == 0) {
				if (expedientTipus.getExpressioNumero() == null)
					throw new IllegalArgumentsException("És obligatori especificar un número per l'expedient");
			}
		}
		if (expedientTipus.getTeTitol().booleanValue() && expedientTipus.getDemanaTitol().booleanValue()) {
			if (titol == null || titol.length() == 0)
				throw new IllegalArgumentsException("És obligatori especificar un títol per l'expedient");
		}
		Entorn entorn = entornDao.getById(entornId, false);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String iniciadorCodiCalculat = (iniciadorTipus.equals(IniciadorTipus.INTERN)) ? auth.getName() : iniciadorCodi;
		Expedient expedient = new Expedient(
				iniciadorTipus,
				iniciadorCodiCalculat,
				expedientTipus,
				entorn,
				UUID.randomUUID().toString());
		String responsableCodiCalculat = (responsableCodi != null) ? responsableCodi : expedientTipus.getResponsableDefecteCodi();
		if (responsableCodiCalculat == null)
			responsableCodiCalculat = iniciadorCodiCalculat;
		expedient.setResponsableCodi(responsableCodiCalculat);
		expedient.setNumeroDefault(
				expedientTipus.getNumeroExpedientDefaultActual(getNumexpExpression()));
		if (numero != null && numero.length() > 0)
			expedient.setNumero(numero);
		processarNumeroExpedient(expedientTipus, numero, expedient.getNumeroDefault());
		/*} else {
			String numact = expedientTipus.getNumeroExpedientActual();
			expedient.setNumero(numact);
			processarNumeroExpedient(expedientTipus, numact);*/
		if (titol != null && titol.length() > 0)
			expedient.setTitol(titol);
		ExpedientIniciant.setExpedient(expedient);
		DefinicioProces definicioProces = null;
		if (definicioProcesId != null) {
			definicioProces = definicioProcesDao.getById(definicioProcesId, false);
		} else {
			definicioProces = definicioProcesDao.findDarreraVersioAmbEntornIJbpmKey(
					entornId,
					expedientTipus.getJbpmProcessDefinitionKey());
		}
		String startTaskName = jbpmDao.getStartTaskName(definicioProces.getJbpmId());
		if (startTaskName != null && variables == null) {
			throw new IllegalArgumentsException("És obligatori especificar els valors inicials per l'expedient");
		}
		JbpmProcessInstance processInstance = jbpmDao.startProcessInstanceById(
				SecurityContextHolder.getContext().getAuthentication().getName(),
				definicioProces.getJbpmId(),
				variables,
				transitionName);
		expedient.setProcessInstanceId(processInstance.getId());
		expedientDao.saveOrUpdate(expedient);
		luceneDao.createExpedient(
				expedient,
				getMapDefinicionsProces(expedient),
				getMapCamps(expedient),
				getMapValorsJbpm(expedient));
		registreDao.crearRegistreIniciarExpedient(
				expedient.getId(),
				SecurityContextHolder.getContext().getAuthentication().getName());
		return dtoConverter.toExpedientDto(expedient, true);
	}
	public void editar(
			Long entornId,
			Long id,
			String numero,
			String titol,
			String iniciadorCodi,
			String responsableCodi,
			Date dataInici,
			String comentari,
			Long estatId) {
		Expedient expedient = expedientDao.findAmbEntornIId(entornId, id);
		String informacioVella = getInformacioExpedient(expedient);
		expedient.setNumero(numero);
		expedient.setTitol(titol);
		expedient.setIniciadorCodi(iniciadorCodi);
		expedient.setResponsableCodi(responsableCodi);
		expedient.setDataInici(dataInici);
		expedient.setComentari(comentari);
		if (estatId != null)
			expedient.setEstat(estatDao.getById(estatId, false));
		else
			expedient.setEstat(null);
		luceneDao.updateExpedient(
				expedient,
				getMapDefinicionsProces(expedient),
				getMapCamps(expedient),
				getMapValorsJbpm(expedient));
		String informacioNova = getInformacioExpedient(expedient);
		registreDao.crearRegistreModificarExpedient(
				expedient.getId(),
				SecurityContextHolder.getContext().getAuthentication().getName(),
				informacioVella,
				informacioNova);
	}
	public void delete(Long entornId, Long id) {
		Expedient expedient = expedientDao.findAmbEntornIId(entornId, id);
		if (expedient != null) {
			List<JbpmProcessInstance> processInstancesTree = jbpmDao.getProcessInstanceTree(expedient.getProcessInstanceId());
			for (JbpmProcessInstance pi: processInstancesTree)
				for (TerminiIniciat ti: terminiIniciatDao.findAmbProcessInstanceId(pi.getId()))
					terminiIniciatDao.delete(ti);
			jbpmDao.deleteProcessInstance(expedient.getProcessInstanceId());
			for (DocumentStore documentStore: documentStoreDao.findAmbProcessInstanceId(expedient.getProcessInstanceId())) {
				if (documentStore.isSignat()) {
					try {
						pluginCustodiaDao.deleteDocumentAmbSignatura(documentStore.getReferenciaCustodia());
					} catch (Exception ignored) {}
				}
				documentStoreDao.delete(documentStore.getId());
			}
			expedientDao.delete(expedient);
			luceneDao.deleteDocument(expedient);
			registreDao.crearRegistreEsborrarExpedient(
					expedient.getId(),
					SecurityContextHolder.getContext().getAuthentication().getName());
		} else {
			throw new NotFoundException("No existeix l'expedient per l'entorn");
		}
	}
	public List<ExpedientDto> findAmbEntorn(Long entornId) {
		List<ExpedientDto> resposta = new ArrayList<ExpedientDto>();
		for (Expedient expedient: expedientDao.findAmbEntorn(entornId))
			resposta.add(dtoConverter.toExpedientDto(expedient, false));
		return resposta;
	}
	public List<ExpedientDto> findAmbEntornConsultaGeneral(
			Long entornId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Long expedientTipusId,
			Long estatId,
			boolean iniciat,
			boolean finalitzat) {
		List<ExpedientDto> resposta = new ArrayList<ExpedientDto>();
		for (Expedient expedient: expedientDao.findAmbEntornConsultaGeneral(
				entornId,
				titol,
				numero,
				dataInici1,
				dataInici2,
				expedientTipusId,
				estatId,
				iniciat,
				finalitzat))
			resposta.add(dtoConverter.toExpedientDto(expedient, false));
		return resposta;
	}
	public List<ExpedientDto> findAmbEntornConsultaFiltre(
			Long entornId,
			Long consultaId,
			Map<String, Object> valors) {
		List<Camp> camps = consultaDao.findCampsFiltre(consultaId);
		List<Long> idsResultat = luceneDao.find(valors, camps);
		List<ExpedientDto> resposta = new ArrayList<ExpedientDto>();
		for (Long id: idsResultat) {
			Expedient expedient = expedientDao.getById(id, false);
			resposta.add(dtoConverter.toExpedientDto(expedient, false));
		}
		return resposta;
	}
	public List<ExpedientDto> findAmbDefinicioProcesId(Long definicioProcesId) {
		DefinicioProces definicioProces = definicioProcesDao.getById(definicioProcesId, false);
		List<ExpedientDto> resposta = new ArrayList<ExpedientDto>();
		for (JbpmProcessInstance pi: jbpmDao.findProcessInstancesWithProcessDefinitionId(definicioProces.getJbpmId())) {
			Expedient expedient = expedientDao.findAmbProcessInstanceId(pi.getId());
			resposta.add(dtoConverter.toExpedientDto(expedient, false));
		}
		return resposta;
	}
	public ExpedientDto findExpedientAmbEntornTipusITitol(
			Long entornId,
			Long expedientTipusId,
			String titol) {
		Expedient expedient = expedientDao.findAmbEntornTipusITitol(entornId, expedientTipusId, titol);
		if (expedient == null)
			return null;
		return dtoConverter.toExpedientDto(expedient, false);
	}
	public ExpedientDto findExpedientAmbEntornTipusINumero(
			Long entornId,
			Long expedientTipusId,
			String numero) {
		Expedient expedient = expedientDao.findAmbEntornTipusINumero(entornId, expedientTipusId, numero);
		if (expedient == null)
			return null;
		return dtoConverter.toExpedientDto(expedient, false);
	}
	public ExpedientDto findExpedientAmbProcessInstanceId(String processInstanceId) {
		JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(processInstanceId);
		if (pi == null)
			return null;
		Expedient expedient = expedientDao.findAmbProcessInstanceId(pi.getId());
		if (expedient == null)
			return null;
		return dtoConverter.toExpedientDto(expedient, false);
	}

	public List<TascaDto> findTasquesPerInstanciaProces(String processInstanceId) {
		List<TascaDto> resposta = new ArrayList<TascaDto>();
		List<JbpmTask> tasks = jbpmDao.findTaskInstancesForProcessInstance(processInstanceId);
		for (JbpmTask task: tasks)
			resposta.add(dtoConverter.toTascaDto(task, true, true, true, true));
		Collections.sort(resposta);
		return resposta;
	}

	public InstanciaProcesDto getInstanciaProcesById(
			String processInstanceId,
			boolean ambVariables) {
		return dtoConverter.toInstanciaProcesDto(processInstanceId, ambVariables);
	}
	public List<InstanciaProcesDto> getArbreInstanciesProces(
			String processInstanceId,
			boolean ambVariables) {
		List<InstanciaProcesDto> resposta = new ArrayList<InstanciaProcesDto>();
		JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(processInstanceId);
		List<JbpmProcessInstance> piTree = jbpmDao.getProcessInstanceTree(rootProcessInstance.getId());
		for (JbpmProcessInstance jpi: piTree) {
			resposta.add(dtoConverter.toInstanciaProcesDto(jpi.getId(), ambVariables));
		}
		return resposta;
	}

	public Entorn getEntornAmbProcessInstanceId(String processInstanceId) {
		Expedient expedient = expedientDao.findAmbProcessInstanceId(processInstanceId);
		if (expedient != null)
			return expedient.getEntorn();
		return null;
	}

	public Object getVariable(String processInstanceId, String varName) {
		return jbpmDao.getProcessInstanceVariable(processInstanceId, varName);
	}
	public void createVariable(String processInstanceId, String varName, Object value) {
		jbpmDao.setProcessInstanceVariable(processInstanceId, varName, value);
		JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(processInstanceId);
		Expedient expedient = expedientDao.findAmbProcessInstanceId(pi.getId());
		luceneDao.updateExpedient(
				expedient,
				getMapDefinicionsProces(expedient),
				getMapCamps(expedient),
				getMapValorsJbpm(expedient));
		registreDao.crearRegistreCrearVariableInstanciaProces(
				getExpedientPerProcessInstanceId(processInstanceId).getId(),
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				varName,
				value);
	}
	public void updateVariable(String processInstanceId, String varName, Object value) {
		Object valorVell = jbpmDao.getProcessInstanceVariable(processInstanceId, varName);
		jbpmDao.setProcessInstanceVariable(processInstanceId, varName, value);
		JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(processInstanceId);
		Expedient expedient = expedientDao.findAmbProcessInstanceId(pi.getId());
		luceneDao.updateExpedient(
				expedient,
				getMapDefinicionsProces(expedient),
				getMapCamps(expedient),
				getMapValorsJbpm(expedient));
		registreDao.crearRegistreModificarVariableInstanciaProces(
				getExpedientPerProcessInstanceId(processInstanceId).getId(),
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				varName,
				valorVell,
				value);
	}
	public void deleteVariable(String processInstanceId, String varName) {
		jbpmDao.deleteProcessInstanceVariable(processInstanceId, varName);
		JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(processInstanceId);
		Expedient expedient = expedientDao.findAmbProcessInstanceId(pi.getId());
		luceneDao.updateExpedient(
				expedient,
				getMapDefinicionsProces(expedient),
				getMapCamps(expedient),
				getMapValorsJbpm(expedient));
		registreDao.crearRegistreEsborrarVariableInstanciaProces(
				getExpedientPerProcessInstanceId(processInstanceId).getId(),
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				varName);
	}

	public DocumentDto getDocument(Long documentStoreId) {
		return dtoConverter.toDocumentDto(documentStoreId, true);
		
	}
	public void guardarDocument(
			String processInstanceId,
			Long documentId,
			Date data,
			String arxiuNom,
			byte[] arxiuContingut) {
		Document document = documentDao.getById(documentId, false);
		createUpdateDocumentStore(
				processInstanceId,
				document.getCodi(),
				document.getNom(),
				TascaService.PREFIX_DOCUMENT + document.getCodi(),
				data,
				arxiuNom,
				arxiuContingut,
				true);
		
	}
	public void guardarAdjunt(
			String processInstanceId,
			String adjuntId,
			String adjuntTitol,
			Date data,
			String arxiuNom,
			byte[] arxiuContingut) {
		String adId = (adjuntId == null) ? new Long(new Date().getTime()).toString() : adjuntId;
		createUpdateDocumentStore(
				processInstanceId,
				adId,
				adjuntTitol,
				TascaService.PREFIX_ADJUNT + adId,
				data,
				arxiuNom,
				arxiuContingut,
				true);
	}
	public void deleteDocument(String processInstanceId, Long documentStoreId) {
		DocumentStore documentStore = documentStoreDao.getById(documentStoreId, false);
		if (documentStore != null) {
			String jbpmVariable = documentStore.getJbpmVariable();
			if (documentStore.isSignat())
				pluginCustodiaDao.deleteDocumentAmbSignatura(documentStore.getReferenciaCustodia());
			documentStoreDao.delete(documentStoreId);
			jbpmDao.deleteProcessInstanceVariable(processInstanceId, jbpmVariable);
			JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(processInstanceId);
			Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
			registreDao.crearRegistreEsborrarDocumentInstanciaProces(
					expedient.getId(),
					processInstanceId,
					SecurityContextHolder.getContext().getAuthentication().getName(),
					getVarNameFromDocumentStore(documentStore));
		}
	}

	public void deleteSignatura(
			String processInstanceId,
			Long documentStoreId) {
		DocumentStore documentStore = documentStoreDao.getById(documentStoreId, false);
		if (documentStore != null) {
			if (documentStore.isSignat()) {
				String jbpmVariable = documentStore.getJbpmVariable();
				pluginCustodiaDao.deleteDocumentAmbSignatura(documentStore.getReferenciaCustodia());
				documentStore.setReferenciaCustodia(null);
				documentStore.setSignat(false);
				JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(processInstanceId);
				Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
				registreDao.crearRegistreEsborrarSignatura(
						expedient.getId(),
						processInstanceId,
						SecurityContextHolder.getContext().getAuthentication().getName(),
						getVarNameFromDocumentStore(documentStore));
				List<JbpmTask> tasks = jbpmDao.findTaskInstancesForProcessInstance(processInstanceId);
				for (JbpmTask task: tasks) {
					jbpmDao.deleteTaskInstanceVariable(
							task.getId(),
							jbpmVariable);
				}
			}
		}
	}

	public List<TokenDto> getActiveTokens(String processInstanceId, boolean withNodePosition) {
		List<TokenDto> resposta = new ArrayList<TokenDto>();
		Map<String, JbpmToken> activeTokens = jbpmDao.getActiveTokens(processInstanceId);
		Map<String, JbpmNodePosition> positions = null;
		if (withNodePosition)
			positions = getNodePositions(processInstanceId);
		for (String tokenName: activeTokens.keySet()) {
			JbpmToken token = activeTokens.get(tokenName);
			TokenDto dto = toTokenDto(token);
			if (withNodePosition) {
				JbpmNodePosition position = positions.get(token.getNodeName());
				if (position != null) {
					dto.setNodePosX(position.getPosX());
					dto.setNodePosY(position.getPosY());
					dto.setNodeWidth(position.getWidth());
					dto.setNodeHeight(position.getHeight());
				}
			}
			resposta.add(dto);
		}
		return resposta;
	}

	public List<TokenDto> getAllTokens(String processInstanceId) {
		List<TokenDto> resposta = new ArrayList<TokenDto>();
		Map<String, JbpmToken> activeTokens = jbpmDao.getActiveTokens(processInstanceId);
		for (String tokenName: activeTokens.keySet()) {
			JbpmToken token = activeTokens.get(tokenName);
			TokenDto dto = toTokenDto(token);
			resposta.add(dto);
		}
		return resposta;
	}

	public void reassignarTasca(
			Long entornId,
			String taskId,
			String expression) {
		jbpmDao.reassignTaskInstance(taskId, expression);
		registreDao.crearRegistreRedirigirTasca(
				getExpedientPerTaskInstanceId(taskId).getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				expression);
	}
	public void suspendreTasca(
			Long entornId,
			String taskId) {
		jbpmDao.suspendTaskInstance(taskId);
		registreDao.crearRegistreSuspendreTasca(
				getExpedientPerTaskInstanceId(taskId).getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName());
	}
	public void reprendreTasca(
			Long entornId,
			String taskId) {
		jbpmDao.resumeTaskInstance(taskId);
		registreDao.crearRegistreReprendreTasca(
				getExpedientPerTaskInstanceId(taskId).getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName());
	}
	public void cancelarTasca(
			Long entornId,
			String taskId) {
		jbpmDao.cancelTaskInstance(taskId);
		registreDao.crearRegistreCancelarTasca(
				getExpedientPerTaskInstanceId(taskId).getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName());
	}

	public List<FilaResultat> getResultatConsultaDomini(
			String processInstanceId,
			String campCodi,
			String textInicial) throws DominiException {
		JbpmProcessDefinition jpd = jbpmDao.findProcessDefinitionWithProcessInstanceId(processInstanceId);
		DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmId(jpd.getId());
		return dtoConverter.getResultatConsultaDomini(
				definicioProces,
				null,
				processInstanceId,
				campCodi,
				textInicial,
				null);
	}

	public List<Registre> getRegistrePerExpedient(Long expedientId) {
		return registreDao.findAmbExpedientId(expedientId);	
	}

	public void aturar(
			String processInstanceId,
			String motiu) {
		JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(processInstanceId);
		List<JbpmProcessInstance> processInstancesTree = jbpmDao.getProcessInstanceTree(rootProcessInstance.getId());
		String[] ids = new String[processInstancesTree.size()];
		int i = 0;
		for (JbpmProcessInstance pi: processInstancesTree)
			ids[i++] = pi.getId();
		jbpmDao.suspendProcessInstances(ids);
		Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
		expedient.setInfoAturat(motiu);
		registreDao.crearRegistreAturarExpedient(
				expedient.getId(),
				SecurityContextHolder.getContext().getAuthentication().getName(),
				motiu);
	}
	public void reprendre(
			String processInstanceId) {
		JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(processInstanceId);
		List<JbpmProcessInstance> processInstancesTree = jbpmDao.getProcessInstanceTree(rootProcessInstance.getId());
		String[] ids = new String[processInstancesTree.size()];
		int i = 0;
		for (JbpmProcessInstance pi: processInstancesTree)
			ids[i++] = pi.getId();
		jbpmDao.resumeProcessInstances(ids);
		Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
		expedient.setInfoAturat(null);
		registreDao.crearRegistreReprendreExpedient(
				expedient.getId(),
				SecurityContextHolder.getContext().getAuthentication().getName());
	}

	public List<String> findArrivingNodeNames(String tokenId) {
		return jbpmDao.findArrivingNodeNames(tokenId);
	}
	public void tokenRetrocedir(
			String tokenId,
			String nodeName,
			boolean cancelTasks) {
		JbpmToken token = jbpmDao.getTokenById(tokenId);
		String nodeNameVell = token.getNodeName();
		JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(token.getProcessInstanceId());
		Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
		jbpmDao.tokenRedirect(tokenId, nodeName, cancelTasks);
		registreDao.crearRegistreRedirigirToken(
				expedient.getId(),
				token.getProcessInstanceId(),
				SecurityContextHolder.getContext().getAuthentication().getName(),
				token.getFullName(),
				nodeNameVell,
				nodeName);
	}

	public Object evaluateScript(
			String processInstanceId,
			String script,
			String outputVar) {
		Set<String> outputVars = new HashSet<String>();
		if (outputVar != null)
			outputVars.add(outputVar);
		Map<String, Object> output =  jbpmDao.evaluateScript(processInstanceId, script, outputVars);
		return output.get(outputVar);
	}

	public List<SignaturaInfo> verificarDocument(Long id) {
		DocumentStore documentStore = documentStoreDao.getById(id, false);
		return pluginCustodiaDao.verificarDocumentAmbSignatura(documentStore.getReferenciaCustodia());
	}

	public void changeProcessInstanceVersion(
			String processInstanceId,
			int newVersion) {
		jbpmDao.changeProcessInstanceVersion(processInstanceId, newVersion);
	}
	public void changeProcessInstanceVersion(String processInstanceId) {
		jbpmDao.changeProcessInstanceVersion(processInstanceId, -1);
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
	public void setDefinicioProcesDao(DefinicioProcesDao definicioProcesDao) {
		this.definicioProcesDao = definicioProcesDao;
	}
	@Autowired
	public void setEntornDao(EntornDao entornDao) {
		this.entornDao = entornDao;
	}
	@Autowired
	public void setDocumentDao(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}
	@Autowired
	public void setEstatDao(EstatDao estatDao) {
		this.estatDao = estatDao;
	}
	@Autowired
	public void setLuceneDao(LuceneDao luceneDao) {
		this.luceneDao = luceneDao;
	}
	@Autowired
	public void setConsultaDao(ConsultaDao consultaDao) {
		this.consultaDao = consultaDao;
	}
	@Autowired
	public void setPluginCustodiaDao(PluginCustodiaDao pluginCustodiaDao) {
		this.pluginCustodiaDao = pluginCustodiaDao;
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
	public void setDtoConverter(DtoConverter dtoConverter) {
		this.dtoConverter = dtoConverter;
	}
	@Autowired
	public void setRegistreDao(RegistreDao registreDao) {
		this.registreDao = registreDao;
	}
	@Autowired
	public void setTerminiIniciatDao(TerminiIniciatDao terminiIniciatDao) {
		this.terminiIniciatDao = terminiIniciatDao;
	}
	@Autowired
	public void setPluginPortasignaturesDao(PluginPortasignaturesDao pluginPortasignaturesDao) {
		this.pluginPortasignaturesDao = pluginPortasignaturesDao;
	}

	@SuppressWarnings("unchecked")
	private Map<String, JbpmNodePosition> getNodePositions(String processInstanceId) {
		Map<String, JbpmNodePosition> resposta = new HashMap<String, JbpmNodePosition>();
		JbpmProcessDefinition jpd = jbpmDao.findProcessDefinitionWithProcessInstanceId(processInstanceId);
		byte[] gpdBytes = jbpmDao.getResourceBytes(jpd.getId(), "gpd.xml");
		if (gpdBytes != null) {
			try {
				Element root = DocumentHelper.parseText(new String(gpdBytes)).getRootElement();
				Iterator it = root.elementIterator("node");
				while (it.hasNext()) {
					Element node = (Element)it.next();
					String nodeName = node.attributeValue("name");
					JbpmNodePosition nodePosition = new JbpmNodePosition();
					nodePosition.setPosX(new Integer(node.attributeValue("x")).intValue());
					nodePosition.setPosY(new Integer(node.attributeValue("y")).intValue());
					nodePosition.setWidth(new Integer(node.attributeValue("width")).intValue());
					nodePosition.setHeight(new Integer(node.attributeValue("height")).intValue());
					resposta.put(nodeName, nodePosition);
				}
			} catch (Exception ex) {
				logger.error("No s'ha pogut desxifrar l'arxiu gpd.xml", ex);
			}
		}
		return resposta;
	}

	private void processarNumeroExpedient(ExpedientTipus expedientTipus, String numero, String numeroDefault) {
		int any = Calendar.getInstance().get(Calendar.YEAR);
		if (expedientTipus.getAnyActual() == 0) {
			expedientTipus.setAnyActual(any);
		} else if (expedientTipus.getAnyActual() != any) {
			if (expedientTipus.isReiniciarCadaAny())
				expedientTipus.setSequencia(1);
			expedientTipus.setSequenciaDefault(1);
			expedientTipus.setAnyActual(any);
		}
		if (expedientTipus.getExpressioNumero() != null && !"".equals(expedientTipus.getExpressioNumero())) {
			if (numero != null && numero.equals(expedientTipus.getNumeroExpedientActual()))
				expedientTipus.setSequencia(expedientTipus.getSequencia() + 1);
		}
		if (numeroDefault.equals(expedientTipus.getNumeroExpedientDefaultActual(getNumexpExpression())))
			expedientTipus.setSequenciaDefault(expedientTipus.getSequenciaDefault() + 1);
	}

	private String getInformacioExpedient(Expedient expedient) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (expedient.getTitol() != null)
			sb.append("titol: \"" + expedient.getTitol() + "\", ");
		if (expedient.getNumero() != null)
			sb.append("numero: \"" + expedient.getNumero() + "\", ");
		if (expedient.getEstat() != null)
			sb.append("estat: \"" + expedient.getEstat().getNom() + "\", ");
		sb.append("dataInici: \"" + expedient.getDataInici() + "\", ");
		if (expedient.getDataFi() != null)
			sb.append("dataFi: \"" + expedient.getDataFi() + "\", ");
		if (expedient.getComentari() != null && expedient.getComentari().length() > 0)
			sb.append("comentari: \"" + expedient.getComentari() + "\", ");
		if (expedient.getResponsableCodi() != null)
			sb.append("responsableCodi: \"" + expedient.getResponsableCodi() + "\", ");
		sb.append("iniciadorCodi: \"" + expedient.getIniciadorCodi() + "\"");
		sb.append("]");
		return sb.toString();
	}

	private TokenDto toTokenDto(JbpmToken token) {
		TokenDto dto = new TokenDto();
		dto.setId(token.getId());
		dto.setName(token.getName());
		dto.setFullName(token.getFullName());
		dto.setParentName(token.getParentTokenName());
		dto.setParentFullName(token.getParentTokenFullName());
		dto.setNodeName(token.getNodeName());
		dto.setStart(token.getStart());
		dto.setEnd(token.getEnd());
		dto.setAbleToReactivateParent(token.isAbleToReactivateParent());
		dto.setTerminationImplicit(token.isTerminationImplicit());
		dto.setSuspended(token.isSuspended());
		dto.setNodeEnter(token.getNodeEnter());
		dto.setRoot(token.isRoot());
		return dto;
	}

	private Expedient getExpedientPerProcessInstanceId(String processInstanceId) {
		JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(processInstanceId);
		return expedientDao.findAmbProcessInstanceId(pi.getId());
	}
	private Expedient getExpedientPerTaskInstanceId(String taskId) {
		JbpmTask task = jbpmDao.getTaskById(taskId);
		JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(
				task.getProcessInstanceId());
		return expedientDao.findAmbProcessInstanceId(pi.getId());
	}

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

	private void createUpdateDocumentStore(
				String processInstanceId,
				String documentCodi,
				String documentNom,
				String jbpmVariable,
				Date data,
				String arxiuNom,
				byte[] arxiuContingut,
				boolean adjunt) {
			JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(processInstanceId);
			Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
			// Obté la referencia al documentStore del jBPM
			Long docStoreId = (Long)jbpmDao.getProcessInstanceVariable(
					processInstanceId,
					jbpmVariable);
			String nomArxiuAntic = null;
			boolean creat = (docStoreId == null);
			if (!creat) {
				nomArxiuAntic = documentStoreDao.getById(docStoreId, false).getArxiuNom();
				documentStoreDao.update(
						docStoreId,
						expedient,
						documentNom,
						data,
						arxiuNom,
						arxiuContingut);
			} else {
				docStoreId = documentStoreDao.create(
						expedient,
						processInstanceId,
						jbpmVariable,
						documentNom,
						data,
						arxiuNom,
						arxiuContingut,
						adjunt);
			}
			// Guarda la referència al nou document a dins el jBPM
			jbpmDao.setProcessInstanceVariable(processInstanceId, jbpmVariable, docStoreId);
			// Registra l'acció
			if (creat) {
				registreDao.crearRegistreCrearDocumentInstanciaProces(
						expedient.getId(),
						processInstanceId,
						SecurityContextHolder.getContext().getAuthentication().getName(),
						documentCodi,
						arxiuNom);
			} else {
				registreDao.crearRegistreModificarDocumentInstanciaProces(
						expedient.getId(),
						processInstanceId,
						SecurityContextHolder.getContext().getAuthentication().getName(),
						documentCodi,
						nomArxiuAntic,
						arxiuNom);
			}
		}

	private String getNumexpExpression() {
		return GlobalProperties.getInstance().getProperty("app.numexp.expression");
	}
	
	public void enviarPortasignatures(
			Persona persona,
			DocumentDto documentDto,
			Expedient expedient,
			String importancia,
			Date dataLimit,
			Long tokenId,
			String transicioOK,
			String transicioKO) throws Exception {
		
		Integer doc = pluginPortasignaturesDao.UploadDocument(
						persona,
						documentDto,
						expedient,
						importancia,
						dataLimit);
		
		Calendar cal = Calendar.getInstance();
		Portasignatures portasignatures = new Portasignatures();
		portasignatures.setDocumentId(doc);
		portasignatures.setTokenId(tokenId);
		portasignatures.setDataEnviat(cal.getTime());
		portasignatures.setEstat(TipusEstat.PENDENT);
		portasignatures.setDocumentStoreId(documentDto.getId());
		portasignatures.setTransicioOK(transicioOK);
		portasignatures.setTransicioKO(transicioKO);
		pluginPortasignaturesDao.saveOrUpdate(portasignatures);
		
		Document document = documentDao.getById(documentDto.getDocumentId(), false);
		document.setPortasignaturesId(portasignatures.getId());
		document.setTipusDocPortasignatures(documentDto.getTipusDocPortasignatures());
		documentDao.saveOrUpdate(document);
	}
	
	public byte[] obtenirDocumentPortasignatures(
			Integer documentId) {
		try {
			return pluginPortasignaturesDao.DownloadDocument(documentId);
		} catch (Exception e) {
			logger.error("Error obtenint el document del Portasignatures", e);
			return null;
		}
	}
	
	public void afegirDocumentCustodia(
			Integer documentId,
			Long documentStoreId) throws Exception {
		DocumentDto document = dtoConverter.toDocumentDto(documentStoreId, true);
		
		if (document != null) {
			DocumentStore docst = documentStoreDao.getById(documentStoreId, false);
			JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(docst.getProcessInstanceId());
			Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
			JbpmProcessInstance processInstance = jbpmDao.getProcessInstance(docst.getProcessInstanceId());
			String varDocumentCodi = docst.getJbpmVariable().substring(TascaService.PREFIX_DOCUMENT.length());
			
			byte[] documentPortasignatures = obtenirDocumentPortasignatures(documentId);
			
			if ((documentPortasignatures != null) && !documentPortasignatures.equals("")) {
				String referenciaCustodia = pluginCustodiaDao.afegirDocumentAmbSignatura(
						expedient,
						definicioProcesDao.findAmbJbpmId(processInstance.getProcessDefinitionId()),
						varDocumentCodi,
						document,
						document.getArxiuNom(),
						documentPortasignatures);
				docst.setReferenciaCustodia(referenciaCustodia);
				docst.setSignat(true);
				
				registreDao.crearRegistreSignarDocument(
						expedient.getId(),
						docst.getProcessInstanceId(),
						SecurityContextHolder.getContext().getAuthentication().getName(),
						varDocumentCodi);
			} else {
				throw new Exception("Error obtenint el document del Portasignautes.");
			}
		}
	}
	
	public Double processarDocumentSignat(Integer id) throws Exception {
		Double resposta = -1D;
		String transicio = "";
		
		try {
			Portasignatures portasignatures = pluginPortasignaturesDao.findByDocument(id);
			
			if (portasignatures != null) {
				DocumentStore documentStore = documentStoreDao.getById(portasignatures.getDocumentStoreId(), false);
				
				transicio = portasignatures.getTransicioOK();
				
				if ((portasignatures.getEstat() != TipusEstat.SIGNAT)
						&& (portasignatures.getTransition() != Transicio.SIGNAT)
						&& (!documentStore.isSignat())
						) {
					afegirDocumentCustodia(
							portasignatures.getDocumentId(),
							portasignatures.getDocumentStoreId());
				}
				
				portasignatures.setEstat(TipusEstat.SIGNAT);
				portasignatures.setTransition(Transicio.SIGNAT);
				pluginPortasignaturesDao.saveOrUpdate(portasignatures);
				
				Long token = portasignatures.getTokenId();
				jbpmDao.signalToken(token.longValue(), transicio);
				
				resposta = 1D;
			}
			
		} catch (Exception e) {
			logger.error("Error processant el document signat.", e);
			resposta = -1D;
		}
		
		return resposta;
	}
	
	public Double processarDocumentRebutjat(Integer id, String motiuRebuig) throws Exception {
		Double resposta = -1D;
		String transicio = "";
		
		try {
			Portasignatures portasignatures = pluginPortasignaturesDao.findByDocument(id);
			
			if (portasignatures != null) {
				transicio = portasignatures.getTransicioKO();
				
				portasignatures.setEstat(TipusEstat.REBUTJAT);
				portasignatures.setTransition(Transicio.REBUTJAT);
				portasignatures.setMotiuRebuig(motiuRebuig);
				pluginPortasignaturesDao.saveOrUpdate(portasignatures);
				
				Long token = portasignatures.getTokenId();
				jbpmDao.signalToken(token.longValue(), transicio);
				
				resposta = 1D;
			}
			
		} catch (Exception e) {
			logger.error("Error processant el document rebutjat.", e);
		}
		
		return resposta;
	}

	private String getVarNameFromDocumentStore(DocumentStore documentStore) {
		String jbpmVariable = documentStore.getJbpmVariable();
		if (documentStore.isAdjunt())
			return jbpmVariable.substring(TascaService.PREFIX_ADJUNT.length());
		else
			return jbpmVariable.substring(TascaService.PREFIX_DOCUMENT.length());
	}

	private static final Log logger = LogFactory.getLog(ExpedientService.class);

}
