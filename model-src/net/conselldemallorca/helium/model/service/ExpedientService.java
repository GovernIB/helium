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

import net.conselldemallorca.helium.integracio.backoffice.DadesDocument;
import net.conselldemallorca.helium.integracio.domini.FilaResultat;
import net.conselldemallorca.helium.integracio.plugins.persones.Persona;
import net.conselldemallorca.helium.integracio.plugins.signatura.RespostaValidacioSignatura;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarEventRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarExpedientRequest;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmDao;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmNodePosition;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessDefinition;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmToken;
import net.conselldemallorca.helium.model.dao.AccioDao;
import net.conselldemallorca.helium.model.dao.ConsultaDao;
import net.conselldemallorca.helium.model.dao.DefinicioProcesDao;
import net.conselldemallorca.helium.model.dao.DocumentDao;
import net.conselldemallorca.helium.model.dao.DocumentStoreDao;
import net.conselldemallorca.helium.model.dao.EntornDao;
import net.conselldemallorca.helium.model.dao.EstatDao;
import net.conselldemallorca.helium.model.dao.ExpedientDao;
import net.conselldemallorca.helium.model.dao.ExpedientTipusDao;
import net.conselldemallorca.helium.model.dao.LuceneDao;
import net.conselldemallorca.helium.model.dao.PlantillaDocumentDao;
import net.conselldemallorca.helium.model.dao.PluginCustodiaDao;
import net.conselldemallorca.helium.model.dao.PluginGestioDocumentalDao;
import net.conselldemallorca.helium.model.dao.PluginPersonaDao;
import net.conselldemallorca.helium.model.dao.PluginSignaturaDao;
import net.conselldemallorca.helium.model.dao.PluginTramitacioDao;
import net.conselldemallorca.helium.model.dao.RegistreDao;
import net.conselldemallorca.helium.model.dao.TerminiIniciatDao;
import net.conselldemallorca.helium.model.dto.DocumentDto;
import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.model.dto.TascaDto;
import net.conselldemallorca.helium.model.dto.TokenDto;
import net.conselldemallorca.helium.model.exception.DominiException;
import net.conselldemallorca.helium.model.exception.ExpedientRepetitException;
import net.conselldemallorca.helium.model.exception.IllegalArgumentsException;
import net.conselldemallorca.helium.model.exception.NotFoundException;
import net.conselldemallorca.helium.model.exception.TemplateException;
import net.conselldemallorca.helium.model.hibernate.Accio;
import net.conselldemallorca.helium.model.hibernate.Camp;
import net.conselldemallorca.helium.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.model.hibernate.Document;
import net.conselldemallorca.helium.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.Expedient;
import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.model.hibernate.Registre;
import net.conselldemallorca.helium.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.model.hibernate.DocumentStore.DocumentFont;
import net.conselldemallorca.helium.model.hibernate.Expedient.IniciadorTipus;
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
	private PlantillaDocumentDao plantillaDocumentDao;
	private DocumentStoreDao documentStoreDao;
	private EstatDao estatDao;
	private LuceneDao luceneDao;
	private ConsultaDao consultaDao;
	private PluginCustodiaDao pluginCustodiaDao;
	private RegistreDao registreDao;
	private AccioDao accioDao;
	private TerminiIniciatDao terminiIniciatDao;
	private PluginGestioDocumentalDao pluginGestioDocumentalDao;
	private PluginTramitacioDao pluginTramitacioDao;
	private PluginSignaturaDao pluginSignaturaDao;
	private PluginPersonaDao pluginPersonaDao;

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
		if (definicioProcesId == null && definicioProces == null) {
			logger.error("No s'ha trobat la definició de procés (entorn=" + entornId + ", jbpmKey=" + expedientTipus.getJbpmProcessDefinitionKey() + ")");
		}
		String startTaskName = jbpmDao.getStartTaskName(definicioProces.getJbpmId());
		if (startTaskName != null) {
			return dtoConverter.toTascaInicialDto(startTaskName, definicioProces.getJbpmId(), valors);
		}
		return null;
	}

	public ExpedientDto iniciar(
			Long entornId,
			Long expedientTipusId,
			Long definicioProcesId,
			String numero,
			String titol,
			String registreNumero,
			Date registreData,
			Long unitatAdministrativa,
			String idioma,
			boolean autenticat,
			String tramitadorNif,
			String tramitadorNom,
			String interessatNif,
			String interessatNom,
			String representantNif,
			String representantNom,
			boolean avisosHabilitats,
			String avisosEmail,
			String avisosMobil,
			boolean notificacioTelematicaHabilitada,
			Map<String, Object> variables,
			String transitionName,
			IniciadorTipus iniciadorTipus,
			String iniciadorCodi,
			String responsableCodi,
			Map<String, DadesDocument> documents,
			List<DadesDocument> adjunts) {
		return iniciar(
				entornId,
				null,
				expedientTipusId,
				definicioProcesId,
				numero,
				titol,
				registreNumero,
				registreData,
				unitatAdministrativa,
				idioma,
				autenticat,
				tramitadorNif,
				tramitadorNom,
				interessatNif,
				interessatNom,
				representantNif,
				representantNom,
				avisosHabilitats,
				avisosEmail,
				avisosMobil,
				notificacioTelematicaHabilitada,
				variables,
				transitionName,
				iniciadorTipus,
				iniciadorCodi,
				responsableCodi,
				documents,
				adjunts);
	}
	public synchronized ExpedientDto iniciar(
			Long entornId,
			String usuari,
			Long expedientTipusId,
			Long definicioProcesId,
			String numero,
			String titol,
			String registreNumero,
			Date registreData,
			Long unitatAdministrativa,
			String idioma,
			boolean autenticat,
			String tramitadorNif,
			String tramitadorNom,
			String interessatNif,
			String interessatNom,
			String representantNif,
			String representantNom,
			boolean avisosHabilitats,
			String avisosEmail,
			String avisosMobil,
			boolean notificacioTelematicaHabilitada,
			Map<String, Object> variables,
			String transitionName,
			IniciadorTipus iniciadorTipus,
			String iniciadorCodi,
			String responsableCodi,
			Map<String, DadesDocument> documents,
			List<DadesDocument> adjunts) {
		if (usuari != null)
			comprovarUsuari(usuari);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuariBo = (usuari != null) ? usuari : auth.getName();
		ExpedientTipus expedientTipus = expedientTipusDao.getById(expedientTipusId, false);
		/*if (expedientTipus.getTeNumero().booleanValue()) {
			if (numero == null || numero.length() == 0) {
				if (expedientTipus.getExpressioNumero() == null)
					throw new IllegalArgumentsException("És obligatori especificar un número per a l'expedient");
			}
		}
		if (expedientTipus.getTeTitol().booleanValue()) {
			if (titol == null || titol.length() == 0)
				throw new IllegalArgumentsException("És obligatori especificar un títol per a l'expedient");
		}*/
		Entorn entorn = entornDao.getById(entornId, false);
		String iniciadorCodiCalculat = (iniciadorTipus.equals(IniciadorTipus.INTERN)) ? usuariBo : iniciadorCodi;
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
		expedient.setRegistreNumero(registreNumero);
		expedient.setRegistreData(registreData);
		expedient.setUnitatAdministrativa(unitatAdministrativa);
		expedient.setIdioma(idioma);
		expedient.setAutenticat(autenticat);
		expedient.setTramitadorNif(tramitadorNif);
		expedient.setTramitadorNom(tramitadorNom);
		expedient.setInteressatNif(interessatNif);
		expedient.setInteressatNom(interessatNom);
		expedient.setRepresentantNif(representantNif);
		expedient.setRepresentantNom(representantNom);
		expedient.setAvisosHabilitats(avisosHabilitats);
		expedient.setAvisosEmail(avisosEmail);
		expedient.setAvisosMobil(avisosMobil);
		expedient.setNotificacioTelematicaHabilitada(notificacioTelematicaHabilitada);
		expedient.setNumeroDefault(
				expedientTipus.getNumeroExpedientDefaultActual(getNumexpExpression()));
		if (expedientTipus.getTeNumero()) {
			if (numero != null && numero.length() > 0)
				expedient.setNumero(numero);
			else
				expedient.setNumero(
						expedientTipus.getNumeroExpedientActual());
		}
		processarNumeroExpedient(
				expedientTipus,
				expedient.getNumero(),
				expedient.getNumeroDefault());
		if (expedientDao.findAmbEntornTipusINumero(entornId, expedientTipusId, expedient.getNumero()) != null) {
			throw new ExpedientRepetitException("Ja existeix un altre expedient amb el mateix número (" + expedient.getNumero() + ")");
		}
		if (expedientTipus.getTeTitol()) {
			if (titol != null && titol.length() > 0)
				expedient.setTitol(titol);
			else
				expedient.setTitol("[Sense títol]");
		}
		ExpedientIniciant.setExpedient(expedient);
		DefinicioProces definicioProces = null;
		if (definicioProcesId != null) {
			definicioProces = definicioProcesDao.getById(definicioProcesId, false);
		} else {
			definicioProces = definicioProcesDao.findDarreraVersioAmbEntornIJbpmKey(
					entornId,
					expedientTipus.getJbpmProcessDefinitionKey());
		}
		JbpmProcessInstance processInstance = jbpmDao.startProcessInstanceById(
				usuariBo,
				definicioProces.getJbpmId(),
				variables);
		expedient.setProcessInstanceId(processInstance.getId());
		expedientDao.saveOrUpdate(expedient);
		// Afegim els documents
		if (documents != null){
			for (Map.Entry<String, DadesDocument> doc: documents.entrySet()) {
				if (doc.getValue() != null) {
					guardarDocument(
							expedient.getProcessInstanceId(), 
							doc.getValue().getIdDocument(), 
							doc.getValue().getData(), 
							doc.getValue().getArxiuNom(), 
							doc.getValue().getArxiuContingut());
				}
			}
		}
		// Afegim els adjunts
		if (adjunts != null) {
			for (DadesDocument adjunt: adjunts) {
				guardarAdjunt(
						expedient.getProcessInstanceId(), 
						null, 
						adjunt.getTitol(),
						adjunt.getData(), 
						adjunt.getArxiuNom(), 
						adjunt.getArxiuContingut());
			}
		}
		jbpmDao.signalProcessInstance(expedient.getProcessInstanceId(), transitionName);
		luceneDao.createExpedient(
				expedient,
				getMapDefinicionsProces(expedient),
				getMapCamps(expedient),
				getMapValorsJbpm(expedient));
		registreDao.crearRegistreIniciarExpedient(
				expedient.getId(),
				usuariBo);
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
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia) {
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
		expedient.setGeoPosX(geoPosX);
		expedient.setGeoPosY(geoPosY);
		expedient.setGeoReferencia(geoReferencia);
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
						pluginCustodiaDao.esborrarSignatures(documentStore.getReferenciaCustodia());
					} catch (Exception ignored) {}
				}
				if (documentStore.getFont().equals(DocumentFont.ALFRESCO))
					pluginGestioDocumentalDao.deleteDocument(documentStore.getReferenciaFont());
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
			resposta.add(dtoConverter.toTascaDto(task, null, true, true, true, true));
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

	public void guardarRegistre(
			String processInstanceId,
			String campCodi,
			Object[] valors) {
		guardarRegistre(
				processInstanceId,
				campCodi,
				valors,
				-1);
	}
	public void guardarRegistre(
			String processInstanceId,
			String campCodi,
			Object[] valors,
			int index) {
		Object valor = jbpmDao.getProcessInstanceVariable(processInstanceId, campCodi);
		if (valor == null) {
			jbpmDao.setProcessInstanceVariable(
					processInstanceId,
					campCodi,
					new Object[]{valors});
		} else {
			Object[] valorMultiple = (Object[])valor;
			if (index != -1) {
				valorMultiple[index] = valors;
				jbpmDao.setProcessInstanceVariable(
						processInstanceId,
						campCodi,
						valor);
			} else {
				Object[] valorNou = new Object[valorMultiple.length + 1];
				for (int i = 0; i < valorMultiple.length; i++)
					valorNou[i] = valorMultiple[i];
				valorNou[valorMultiple.length] = valors;
				jbpmDao.setProcessInstanceVariable(
						processInstanceId,
						campCodi,
						valorNou);
			}
		}
	}
	public void esborrarRegistre(
			String processInstanceId,
			String campCodi,
			int index) {
		Object valor = jbpmDao.getProcessInstanceVariable(processInstanceId, campCodi);
		if (valor != null) {
			Object[] valorMultiple = (Object[])valor;
			if (valorMultiple.length > 0) {
				Object[] valorNou = new Object[valorMultiple.length - 1];
				for (int i = 0; i < valorNou.length; i++)
					valorNou[i] = (i < index) ? valorMultiple[i] : valorMultiple[i + 1];
				jbpmDao.setProcessInstanceVariable(
						processInstanceId,
						campCodi,
						valorNou);
			}
		}
	}

	public DocumentDto getDocument(Long documentStoreId, boolean ambContingut, boolean ambVista) {
		return dtoConverter.toDocumentDto(documentStoreId, ambContingut, ambVista, false, false);
		
	}
	public Long guardarDocument(
			String processInstanceId,
			Long documentId,
			Date data,
			String arxiuNom,
			byte[] arxiuContingut) {
		Document document = documentDao.getById(documentId, false);
		return createUpdateDocument(
				processInstanceId,
				document.getCodi(),
				document.getNom(),
				TascaService.PREFIX_DOCUMENT + document.getCodi(),
				data,
				arxiuNom,
				arxiuContingut,
				false);
		
	}
	public DocumentDto generarDocumentPlantilla(
			Long documentId,
			String processInstanceId,
			Date dataDocument) {
		Document document = documentDao.getById(documentId, false);
		DocumentDto resposta = new DocumentDto();
		resposta.setDataCreacio(new Date());
		resposta.setDataDocument(new Date());
		resposta.setArxiuNom(document.getNom() + ".odt");
		if (document.isPlantilla()) {
			JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(processInstanceId);
			ExpedientDto expedient = dtoConverter.toExpedientDto(
					expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId()),
					false);
			InstanciaProcesDto instanciaProces = dtoConverter.toInstanciaProcesDto(
					processInstanceId,
					true);
			Map<String, Object> model = new HashMap<String, Object>();
			model.putAll(instanciaProces.getVarsComText());
			try {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				resposta.setArxiuContingut(plantillaDocumentDao.generarDocumentAmbPlantilla(
						expedient.getEntorn().getId(),
						document,
						auth.getName(),
						expedient,
						processInstanceId,
						null,
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
	public Long guardarAdjunt(
			String processInstanceId,
			String adjuntId,
			String adjuntTitol,
			Date data,
			String arxiuNom,
			byte[] arxiuContingut) {
		String adId = (adjuntId == null) ? new Long(new Date().getTime()).toString() : adjuntId;
		return createUpdateDocument(
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
				try {
					pluginCustodiaDao.esborrarSignatures(documentStore.getReferenciaCustodia());
				} catch (Exception ignored) {}
			if (documentStore.getFont().equals(DocumentFont.ALFRESCO))
				pluginGestioDocumentalDao.deleteDocument(documentStore.getReferenciaFont());
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
				try {
					pluginCustodiaDao.esborrarSignatures(documentStore.getReferenciaCustodia());
				} catch (Exception ignored) {}
				String jbpmVariable = documentStore.getJbpmVariable();
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
		if (withNodePosition) {
			positions = getNodePositions(processInstanceId);
			int[] dimensions = getImageDimensions(processInstanceId);
			int minX = 0;
			int maxX = dimensions[0];
			int minY = 0;
			int maxY = dimensions[1];
			for (JbpmNodePosition position: positions.values()) {
				if (position.getPosX() < minX)
					minX = position.getPosX();
				if (position.getPosX() + position.getWidth() > maxX)
					maxX = position.getPosX() + position.getWidth();
				if (position.getPosY() < minY)
					minY = position.getPosY();
				if (position.getPosY() + position.getHeight() > maxY)
					maxY = position.getPosY() + position.getHeight();
			}
			if (minX < 0 || minY < 0) {
				for (JbpmNodePosition position: positions.values()) {
					position.setPosX(position.getPosX() - minX);
					position.setPosY(position.getPosY() - minY);
				}
			}
		}
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

	public List<RespostaValidacioSignatura> verificarSignatura(Long id) {
		DocumentStore documentStore = documentStoreDao.getById(id, false);
		DocumentDto document = dtoConverter.toDocumentDto(id, true, false, false, false);
		if (pluginCustodiaDao.potObtenirInfoSignatures()) {
			return pluginCustodiaDao.dadesValidacioSignatura(
					documentStore.getReferenciaCustodia());
		} else if (isSignaturaFileAttached()) {
			List<byte[]> signatures = pluginCustodiaDao.obtenirSignatures(
					documentStore.getReferenciaCustodia());
			List<RespostaValidacioSignatura> resposta = new ArrayList<RespostaValidacioSignatura>();
			RespostaValidacioSignatura res = pluginSignaturaDao.verificarSignatura(
					null,
					signatures.get(0),
					true);
			resposta.add(res);
			return resposta;
		} else {
			List<RespostaValidacioSignatura> resposta = new ArrayList<RespostaValidacioSignatura>();
			List<byte[]> signatures = pluginCustodiaDao.obtenirSignatures(
					documentStore.getReferenciaCustodia());
			for (byte[] signatura: signatures) {
				RespostaValidacioSignatura res = pluginSignaturaDao.verificarSignatura(
						document.getArxiuContingut(),
						signatura,
						true);
				resposta.add(res);
			}
			return resposta;
		}
	}

	/*public DocumentDto descarregarSignatura(Long id) {
		DocumentStore documentStore = documentStoreDao.getById(id, false);
		if (isSignaturaFileAttached()) {
			List<byte[]> signatures = pluginCustodiaDao.obtenirSignatures(
					documentStore.getReferenciaCustodia());
			DocumentDto resposta = new DocumentDto();
			String arxiuNom = documentStore.getArxiuNom();
			int indexPunt = arxiuNom.indexOf(".");
			if (indexPunt == -1) {
				resposta.setArxiuNom(arxiuNom + ".pdf");
			} else {
				resposta.setArxiuNom(arxiuNom.substring(0, indexPunt) + ".pdf");
			}
			resposta.setArxiuContingut(Base64.decode(signatures.get(0)));
			return resposta;
		}
		return null;
	}*/

	public void changeProcessInstanceVersion(
			String processInstanceId,
			int newVersion) {
		jbpmDao.changeProcessInstanceVersion(processInstanceId, newVersion);
	}
	public void changeProcessInstanceVersion(String processInstanceId) {
		jbpmDao.changeProcessInstanceVersion(processInstanceId, -1);
	}

	public void executarAccio(
			String processInstanceId,
			String accioCodi) {
		JbpmProcessInstance processInstance = jbpmDao.getProcessInstance(processInstanceId);
		DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmId(processInstance.getProcessDefinitionId());
		Accio accio = accioDao.findAmbDefinicioProcesICodi(definicioProces.getId(), accioCodi);
		jbpmDao.executeActionInstanciaProces(
				processInstanceId,
				accio.getJbpmAction());
	}

	public void publicarExpedient(
			Expedient expedient,
			PublicarExpedientRequest request) {
		String identificador = expedient.getNumeroIdentificador();
		String clau = new Long(System.currentTimeMillis()).toString();
		request.setExpedientIdentificador(identificador);
		request.setExpedientClau(clau);
		pluginTramitacioDao.publicarExpedient(request);
		Expedient ex = expedientDao.getById(expedient.getId(), false);
		ex.setTramitExpedientIdentificador(identificador);
		ex.setTramitExpedientClau(clau);
	}
	public void publicarEvent(
			PublicarEventRequest request) {
		pluginTramitacioDao.publicarEvent(request);
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
	public void setPlantillaDocumentDao(PlantillaDocumentDao plantillaDocumentDao) {
		this.plantillaDocumentDao = plantillaDocumentDao;
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
	public void setAccioDao(AccioDao accioDao) {
		this.accioDao = accioDao;
	}
	@Autowired
	public void setTerminiIniciatDao(TerminiIniciatDao terminiIniciatDao) {
		this.terminiIniciatDao = terminiIniciatDao;
	}
	@Autowired
	public void setPluginGestioDocumentalDao(
			PluginGestioDocumentalDao pluginGestioDocumentalDao) {
		this.pluginGestioDocumentalDao = pluginGestioDocumentalDao;
	}
	@Autowired
	public void setPluginTramitacioDao(PluginTramitacioDao pluginTramitacioDao) {
		this.pluginTramitacioDao = pluginTramitacioDao;
	}
	@Autowired
	public void setPluginSignaturaDao(
			PluginSignaturaDao pluginSignaturaDao) {
		this.pluginSignaturaDao = pluginSignaturaDao;
	}
	@Autowired
	public void setPluginPersonaDao(PluginPersonaDao pluginPersonaDao) {
		this.pluginPersonaDao = pluginPersonaDao;
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
	private int[] getImageDimensions(String processInstanceId) {
		JbpmProcessDefinition jpd = jbpmDao.findProcessDefinitionWithProcessInstanceId(processInstanceId);
		byte[] gpdBytes = jbpmDao.getResourceBytes(jpd.getId(), "gpd.xml");
		if (gpdBytes != null) {
			try {
				Element root = DocumentHelper.parseText(new String(gpdBytes)).getRootElement();
				return new int[] {
						Integer.parseInt(root.attributeValue("width")),
						Integer.parseInt(root.attributeValue("height"))};
			} catch (Exception ex) {
				logger.error("No s'ha pogut desxifrar l'arxiu gpd.xml", ex);
			}
		}
		return null;
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

	private Long createUpdateDocument(
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
		String nomArxiuAntic = null;
		// Obté la referencia al documentStore del jBPM
		Long docStoreId = (Long)jbpmDao.getProcessInstanceVariable(
				processInstanceId,
				jbpmVariable);
		boolean creat = (docStoreId == null);
		// Crea el document al store
		if (docStoreId == null) {
			docStoreId = documentStoreDao.create(
					processInstanceId,
					jbpmVariable,
					data,
					(pluginGestioDocumentalDao.isGestioDocumentalActiu()) ? DocumentFont.ALFRESCO : DocumentFont.INTERNA,
					arxiuNom,
					(pluginGestioDocumentalDao.isGestioDocumentalActiu()) ? null : arxiuContingut,
					adjunt,
					documentNom);
		} else {
			DocumentStore docStore = documentStoreDao.getById(docStoreId, false);
			nomArxiuAntic = docStore.getArxiuNom();
			documentStoreDao.update(
					docStoreId,
					data,
					arxiuNom,
					(pluginGestioDocumentalDao.isGestioDocumentalActiu()) ? null : arxiuContingut,
					documentNom);
			if (arxiuContingut != null && pluginGestioDocumentalDao.isGestioDocumentalActiu())
				pluginGestioDocumentalDao.deleteDocument(docStore.getReferenciaFont());
		}
		// Crea el document a dins la gestió documental
		if (arxiuContingut != null && pluginGestioDocumentalDao.isGestioDocumentalActiu()) {
			String referenciaFont = pluginGestioDocumentalDao.createDocument(
					expedient,
					docStoreId.toString(),
					documentNom,
					data,
					arxiuNom,
					arxiuContingut);
			documentStoreDao.updateReferenciaFont(
					docStoreId,
					referenciaFont);
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
		return docStoreId;
	}

	private String getNumexpExpression() {
		return GlobalProperties.getInstance().getProperty("app.numexp.expression");
	}

	private boolean isSignaturaFileAttached() {
		String fileAttached = GlobalProperties.getInstance().getProperty("app.signatura.plugin.file.attached");
		return "true".equalsIgnoreCase(fileAttached);
	}

	private String getVarNameFromDocumentStore(DocumentStore documentStore) {
		String jbpmVariable = documentStore.getJbpmVariable();
		if (documentStore.isAdjunt())
			return jbpmVariable.substring(TascaService.PREFIX_ADJUNT.length());
		else
			return jbpmVariable.substring(TascaService.PREFIX_DOCUMENT.length());
	}

	private void comprovarUsuari(String usuari) {
		Persona persona = pluginPersonaDao.findAmbCodiPlugin(usuari);
		if (persona == null)
			throw new IllegalArgumentsException("No s'ha trobat la persona amb codi '" + usuari + "'");
	}

	private static final Log logger = LogFactory.getLog(ExpedientService.class);

}
