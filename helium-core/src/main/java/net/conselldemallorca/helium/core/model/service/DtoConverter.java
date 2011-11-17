/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;
import net.conselldemallorca.helium.core.model.dao.CampAgrupacioDao;
import net.conselldemallorca.helium.core.model.dao.CampTascaDao;
import net.conselldemallorca.helium.core.model.dao.DefinicioProcesDao;
import net.conselldemallorca.helium.core.model.dao.DocumentDao;
import net.conselldemallorca.helium.core.model.dao.DocumentStoreDao;
import net.conselldemallorca.helium.core.model.dao.DocumentTascaDao;
import net.conselldemallorca.helium.core.model.dao.DominiDao;
import net.conselldemallorca.helium.core.model.dao.EnumeracioValorsDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientDao;
import net.conselldemallorca.helium.core.model.dao.FirmaTascaDao;
import net.conselldemallorca.helium.core.model.dao.PluginCustodiaDao;
import net.conselldemallorca.helium.core.model.dao.PluginGestioDocumentalDao;
import net.conselldemallorca.helium.core.model.dao.PluginPersonaDao;
import net.conselldemallorca.helium.core.model.dao.TascaDao;
import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.core.model.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.exception.DominiException;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore.DocumentFont;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.core.model.hibernate.FirmaTasca;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.util.DocumentTokenUtils;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.PdfUtils;
import net.conselldemallorca.helium.jbpm3.integracio.DelegationInfo;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmDao;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessDefinition;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;


/**
 * Convertidor de dades cap a DTOs
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class DtoConverter {

	private ExpedientDao expedientDao;
	private DocumentTascaDao documentTascaDao;
	private DocumentDao documentDao;
	private DocumentStoreDao documentStoreDao;
	private CampTascaDao campTascaDao;
	private CampAgrupacioDao campAgrupacioDao;
	private FirmaTascaDao firmaTascaDao;
	private TascaDao tascaDao;
	private DefinicioProcesDao definicioProcesDao;
	private DominiDao dominiDao;
	private PluginPersonaDao pluginPersonaDao;
	private PluginGestioDocumentalDao pluginGestioDocumentalDao;
	private PluginCustodiaDao pluginCustodiaDao;
	private JbpmDao jbpmDao;
	private EnumeracioValorsDao enumeracioValorsDao;
	private MessageSource messageSource;

	private PdfUtils pdfUtils;
	private DocumentTokenUtils documentTokenUtils;



	public ExpedientDto toExpedientDto(Expedient expedient, boolean starting) {
		ExpedientDto dto = new ExpedientDto();
		dto.setId(expedient.getId());
		dto.setProcessInstanceId(expedient.getProcessInstanceId());
		dto.setTitol(expedient.getTitol());
		dto.setNumero(expedient.getNumero());
		dto.setNumeroDefault(expedient.getNumeroDefault());
		dto.setComentari(expedient.getComentari());
		dto.setInfoAturat(expedient.getInfoAturat());
		dto.setAnulat(expedient.isAnulat());
		dto.setDataInici(expedient.getDataInici());
		dto.setIniciadorCodi(expedient.getIniciadorCodi());
		dto.setIniciadorTipus(expedient.getIniciadorTipus());
		dto.setResponsableCodi(expedient.getResponsableCodi());
		if (expedient.getIniciadorTipus().equals(IniciadorTipus.INTERN)) {
			dto.setIniciadorPersona(pluginPersonaDao.findAmbCodiPlugin(expedient.getIniciadorCodi()));
			if (expedient.getResponsableCodi() != null)
				dto.setResponsablePersona(pluginPersonaDao.findAmbCodiPlugin(expedient.getResponsableCodi()));
		}
		if (expedient.getIniciadorTipus().equals(IniciadorTipus.SISTRA))
			dto.setBantelEntradaNum(expedient.getNumeroEntradaSistra());
		dto.setTipus(expedient.getTipus());
		dto.setEntorn(expedient.getEntorn());
		dto.setEstat(expedient.getEstat());
		dto.setGeoPosX(expedient.getGeoPosX());
		dto.setGeoPosY(expedient.getGeoPosY());
		dto.setGeoReferencia(expedient.getGeoReferencia());
		dto.setRegistreNumero(expedient.getRegistreNumero());
		dto.setRegistreData(expedient.getRegistreData());
		dto.setUnitatAdministrativa(expedient.getUnitatAdministrativa());
		dto.setIdioma(expedient.getIdioma());
		dto.setAutenticat(expedient.isAutenticat());
		dto.setTramitadorNif(expedient.getTramitadorNif());
		dto.setTramitadorNom(expedient.getTramitadorNom());
		dto.setInteressatNif(expedient.getInteressatNif());
		dto.setInteressatNom(expedient.getInteressatNom());
		dto.setRepresentantNif(expedient.getRepresentantNif());
		dto.setRepresentantNom(expedient.getRepresentantNom());
		dto.setAvisosHabilitats(expedient.isAvisosHabilitats());
		dto.setAvisosEmail(expedient.getAvisosEmail());
		dto.setAvisosMobil(expedient.getAvisosMobil());
		dto.setNotificacioTelematicaHabilitada(expedient.isNotificacioTelematicaHabilitada());
		dto.setTramitExpedientIdentificador(expedient.getTramitExpedientIdentificador());
		dto.setTramitExpedientClau(expedient.getTramitExpedientClau());
		if (!starting) {
			JbpmProcessInstance processInstance = jbpmDao.getProcessInstance(expedient.getProcessInstanceId());
			dto.setDataFi(processInstance.getEnd());
		}
		return dto;
	}

	public String getTitolPerTasca(
			JbpmTask task) {
		String titol = null;
		if (!isTascaEvaluada(task)) {
			Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
					task.getName(),
					task.getProcessDefinitionId());
			if (tasca != null) {
				Map<String, Object> valorsTasca = jbpmDao.getTaskInstanceVariables(task.getId());
				List<CampTasca> cts = campTascaDao.findAmbTascaOrdenats(tasca.getId());
				List<Camp> campsTasca = new ArrayList<Camp>();
				for (CampTasca campTasca: cts)
					campsTasca.add(campTasca.getCamp());
				Map<String, ParellaCodiValorDto> valorsDominiTasca = obtenirValorsDomini(
						task.getId(),
						null,
						campsTasca,
						valorsTasca);
				Map<String, List<ParellaCodiValorDto>> valorsMultiplesDominiTasca = obtenirValorsMultiplesDomini(
						task.getId(),
						null,
						campsTasca,
						valorsTasca);
				Map<String, Object> textPerCamps = textPerCamps(
						task.getId(),
						null,
						campsTasca,
						valorsTasca,
						valorsDominiTasca,
						valorsMultiplesDominiTasca);
				Set<Camp> campsProces = tasca.getDefinicioProces().getCamps();
				Map<String, Object> valorsProces = jbpmDao.getProcessInstanceVariables(task.getProcessInstanceId());
				Map<String, ParellaCodiValorDto> valorsDominiProces = obtenirValorsDomini(
						null,
						task.getProcessInstanceId(),
						campsProces,
						valorsProces);
				Map<String, List<ParellaCodiValorDto>> valorsMultiplesDominiProces = obtenirValorsMultiplesDomini(
						null,
						task.getProcessInstanceId(),
						campsProces,
						valorsProces);
				textPerCamps.putAll(textPerCamps(
						null,
						task.getProcessInstanceId(),
						campsProces,
						valorsProces,
						valorsDominiProces,
						valorsMultiplesDominiProces));
				titol = evaluarTasca(task, tasca, textPerCamps);
			} else {
				titol = task.getName();
			}
		} else {
			titol = task.getDescription().substring(1);
		}
		return titol;
	}

	public TascaDto toTascaDtoPerOrdenacio(
			JbpmTask task,
			Tasca tasca,
			Expedient expedient,
			boolean processar) {
		//System.out.println(">>> Tasca: " + task.getId());
		//long t1 = System.currentTimeMillis();
		TascaDto dto = new TascaDto();
		dto.setId(task.getId());
		dto.setDescription(task.getDescription());
		dto.setAssignee(task.getAssignee());
		dto.setPooledActors(task.getPooledActors());
		dto.setCreateTime(task.getCreateTime());
		dto.setPersonesMap(getPersonesMap(task.getAssignee(), task.getPooledActors()));
		dto.setStartTime(task.getStartTime());
		dto.setEndTime(task.getEndTime());
		dto.setDueDate(task.getDueDate());
		dto.setPriority(task.getPriority());
		dto.setOpen(task.isOpen());
		dto.setCompleted(task.isCompleted());
		dto.setCancelled(task.isCancelled());
		dto.setSuspended(task.isSuspended());
		dto.setProcessInstanceId(task.getProcessInstanceId());
		if (tasca != null) {
			dto.setNom(tasca.getNom());
			dto.setMissatgeInfo(tasca.getMissatgeInfo());
			dto.setMissatgeWarn(tasca.getMissatgeWarn());
			dto.setDelegable(tasca.getExpressioDelegacio() != null);
			dto.setTipus(tasca.getTipus());
			dto.setJbpmName(tasca.getJbpmName());
			dto.setDefinicioProces(tasca.getDefinicioProces());
			dto.setValidacions(tasca.getValidacions());
			dto.setRecursForm(tasca.getRecursForm());
			dto.setFormExtern(tasca.getFormExtern());
		} else {
			dto.setNom(task.getName());
		}
		if (expedient != null)
			dto.setExpedient(expedient);
		else
			dto.setExpedient(expedientDao.findAmbProcessInstanceId(
					jbpmDao.getRootProcessInstance(task.getProcessInstanceId()).getId()));
		//System.out.println("Temps 1: " + (System.currentTimeMillis() - t1) + "ms");
		//long t2 = System.currentTimeMillis();
		//System.out.println("Temps 2: " + (System.currentTimeMillis() - t2) + "ms");
		//long t3 = System.currentTimeMillis();
		if (processar) {
			dto.setOutcomes(jbpmDao.findTaskInstanceOutcomes(task.getId()));
			Map<String, Object> valorsTasca = jbpmDao.getTaskInstanceVariables(task.getId());
			DelegationInfo delegationInfo = (DelegationInfo)valorsTasca.get(
					TascaService.VAR_TASCA_DELEGACIO);
			if (delegationInfo != null) {
				boolean original = task.getId().equals(delegationInfo.getSourceTaskId());
				dto.setDelegada(true);
				dto.setDelegacioOriginal(original);
				dto.setDelegacioData(delegationInfo.getStart());
				dto.setDelegacioSupervisada(delegationInfo.isSupervised());
				dto.setDelegacioComentari(delegationInfo.getComment());
				if (original) {
					JbpmTask tascaDelegacio = jbpmDao.getTaskById(delegationInfo.getTargetTaskId());
					dto.setDelegacioPersona(pluginPersonaDao.findAmbCodiPlugin(tascaDelegacio.getAssignee()));
				} else {
					JbpmTask tascaDelegacio = jbpmDao.getTaskById(delegationInfo.getSourceTaskId());
					dto.setDelegacioPersona(pluginPersonaDao.findAmbCodiPlugin(tascaDelegacio.getAssignee()));
				}
			}
			//System.out.println("Temps 3: " + (System.currentTimeMillis() - t3) + "ms");
			//long t4 = System.currentTimeMillis();
			if (tasca != null) {
				if (!isTascaEvaluada(task)) {
					List<CampTasca> cts = campTascaDao.findAmbTascaOrdenats(tasca.getId());
					List<Camp> campsTasca = new ArrayList<Camp>();
					for (CampTasca campTasca: cts)
						campsTasca.add(campTasca.getCamp());
					Map<String, ParellaCodiValorDto> valorsDominiTasca = obtenirValorsDomini(
							task.getId(),
							null,
							campsTasca,
							valorsTasca);
					Map<String, List<ParellaCodiValorDto>> valorsMultiplesDominiTasca = obtenirValorsMultiplesDomini(
							task.getId(),
							null,
							campsTasca,
							valorsTasca);
					Map<String, Object> textPerCamps = textPerCamps(
							task.getId(),
							null,
							campsTasca,
							valorsTasca,
							valorsDominiTasca,
							valorsMultiplesDominiTasca);
					Set<Camp> campsProces = tasca.getDefinicioProces().getCamps();
					Map<String, Object> valorsProces = jbpmDao.getProcessInstanceVariables(task.getProcessInstanceId());
					Map<String, ParellaCodiValorDto> valorsDominiProces = obtenirValorsDomini(
							null,
							task.getProcessInstanceId(),
							campsProces,
							valorsProces);
					Map<String, List<ParellaCodiValorDto>> valorsMultiplesDominiProces = obtenirValorsMultiplesDomini(
							null,
							task.getProcessInstanceId(),
							campsProces,
							valorsProces);
					textPerCamps.putAll(textPerCamps(
							null,
							task.getProcessInstanceId(),
							campsProces,
							valorsProces,
							valorsDominiProces,
							valorsMultiplesDominiProces));
					String titolNou = evaluarTasca(task, tasca, textPerCamps);
					if (titolNou != null)
						dto.setNom(titolNou);
				} else {
					dto.setNom(task.getDescription().substring(1));
				}
			}
			//System.out.println("Temps 4: " + (System.currentTimeMillis() - t4) + "ms");
		}
		return dto;
	}

	public TascaDto toTascaDto(
			JbpmTask task,
			Map<String, Object> varsCommand,
			boolean ambVariables,
			boolean validada,
			boolean documentsComplet,
			boolean signaturesComplet) {
		Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
				task.getName(),
				task.getProcessDefinitionId());
		TascaDto dto = toTascaDtoPerOrdenacio(task, tasca, null, true);
		if (tasca != null) {
			dto.setValidada(validada);
			dto.setDocumentsComplet(documentsComplet);
			dto.setSignaturesComplet(signaturesComplet);
			List<CampTasca> campsTasca = campTascaDao.findAmbTascaOrdenats(tasca.getId());
			// (1) Per evitar error de lazy initialization en la validació del formulari de tasca
			for (CampTasca camp: campsTasca)
				camp.getCamp().getValidacions().size();
			// (/1)
			dto.setCamps(campsTasca);
			List<DocumentTasca> documentsTasca = documentTascaDao.findAmbTascaOrdenats(tasca.getId());
			dto.setDocuments(documentsTasca);
			List<FirmaTasca> signaturesTasca = firmaTascaDao.findAmbTascaOrdenats(tasca.getId());
			dto.setSignatures(signaturesTasca);
			if (ambVariables) {
				Map<String, Object> valors = jbpmDao.getTaskInstanceVariables(task.getId());
				dto.setVarsDocuments(
						obtenirVarsDocumentsTasca(
								task.getId(),
								task.getProcessInstanceId(),
								documentsTasca));
				dto.setVarsDocumentsPerSignar(
						obtenirVarsDocumentsPerSignarTasca(
								task.getId(),
								task.getProcessInstanceId(),
								signaturesTasca));
				filtrarVariablesTasca(valors);
				if (varsCommand != null)
					valors.putAll(varsCommand);
				List<Camp> camps = new ArrayList<Camp>();
				for (CampTasca campTasca: campsTasca)
					camps.add(campTasca.getCamp());
				Map<String, ParellaCodiValorDto> valorsDomini = obtenirValorsDomini(
						task.getId(),
						null,
						camps,
						valors);
				dto.setValorsDomini(valorsDomini);
				Map<String, List<ParellaCodiValorDto>> valorsMultiplesDomini = obtenirValorsMultiplesDomini(
						task.getId(),
						null,
						camps,
						valors);
				dto.setValorsMultiplesDomini(valorsMultiplesDomini);
				dto.setVarsComText(textPerCamps(task.getId(), null, camps, valors, valorsDomini, valorsMultiplesDomini));
				dto.setVariables(valors);
			}
		}
		return dto;
	}

	public TascaDto toTascaInicialDto(
			String startTaskName,
			String jbpmId,
			Map<String, Object> valors) {
		Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
				startTaskName,
				jbpmId);
		TascaDto dto = new TascaDto();
		dto.setNom(tasca.getNom());
		dto.setTipus(tasca.getTipus());
		dto.setJbpmName(tasca.getJbpmName());
		dto.setValidada(false);
		dto.setDocumentsComplet(false);
		dto.setSignaturesComplet(false);
		dto.setDefinicioProces(tasca.getDefinicioProces());
		dto.setOutcomes(jbpmDao.findStartTaskOutcomes(jbpmId, startTaskName));
		dto.setValidacions(tasca.getValidacions());
		dto.setFormExtern(tasca.getFormExtern());
		//Camps
		List<CampTasca> campsTasca = tasca.getCamps();
		for (CampTasca campTasca: campsTasca) {
			if (campTasca.getCamp().getTipus().equals(TipusCamp.REGISTRE)) {
				campTasca.getCamp().getRegistreMembres().size();
				/*for (CampRegistre membre: campTasca.getCamp().getRegistreMembres()) {
					System.out.println(">>> " + campTasca.getCamp().getCodi() + ": " + membre.getMembre().getCodi());
				}*/
			}
		}
		dto.setCamps(campsTasca);
		//Documents
		List<DocumentTasca> documentsTasca = tasca.getDocuments();
		for (DocumentTasca document: documentsTasca)
			Hibernate.initialize(document.getDocument());
		dto.setDocuments(documentsTasca);
		//Configuració de valors
		if (valors != null) {
			List<Camp> camps = new ArrayList<Camp>();
			for (CampTasca campTasca: campsTasca) {
				campTasca.getCamp().getRegistreMembres().size();
				camps.add(campTasca.getCamp());
			}
			dto.setValorsDomini(obtenirValorsDomini(
					null,
					null,
					camps,
					valors));
			dto.setValorsMultiplesDomini(obtenirValorsMultiplesDomini(
					null,
					null,
					camps,
					valors));
			dto.setVarsComText(
					textPerCamps(null, null, camps, valors, dto.getValorsDomini(), dto.getValorsMultiplesDomini()));
		}
		return dto;
	}

	public InstanciaProcesDto toInstanciaProcesDto(String processInstanceId, boolean ambVariables) {
		//MesurarTemps.reiniciarInstant("IPDTO");
		JbpmProcessInstance pi = jbpmDao.getProcessInstance(processInstanceId);
		JbpmProcessDefinition jpd = jbpmDao.findProcessDefinitionWithProcessInstanceId(processInstanceId);
		DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmId(jpd.getId());
		InstanciaProcesDto dto = new InstanciaProcesDto();
		dto.setId(processInstanceId);
		dto.setInstanciaProcesPareId(pi.getParentProcessInstanceId());
		JbpmProcessInstance jpi = jbpmDao.getRootProcessInstance(processInstanceId);
		dto.setExpedient(expedientDao.findAmbProcessInstanceId(jpi.getId()));
		dto.setDefinicioProces(definicioProces);
		if (pi.getDescription() != null && pi.getDescription().length() > 0)
			dto.setTitol(pi.getDescription());
		//MesurarTemps.imprimirInstantStdoutIReiniciar("IPDTO", "1");
		Set<String> resourceNames = jbpmDao.getResourceNames(jpd.getId());
		dto.setImatgeDisponible(resourceNames.contains("processimage.jpg"));
		Set<Camp> camps = definicioProces.getCamps();
		dto.setCamps(camps);
		//MesurarTemps.imprimirInstantStdoutIReiniciar("IPDTO", "2");
		List<Document> documents = documentDao.findAmbDefinicioProces(definicioProces.getId());
		dto.setDocuments(documents);
		//MesurarTemps.imprimirInstantStdoutIReiniciar("IPDTO", "3");
		dto.setAgrupacions(campAgrupacioDao.findAmbDefinicioProcesOrdenats(definicioProces.getId()));
		//MesurarTemps.imprimirInstantStdoutIReiniciar("IPDTO", "4");
		if (ambVariables) {
			//MesurarTemps.reiniciarInstant("IPDTO2");
			Map<String, Object> valors = jbpmDao.getProcessInstanceVariables(processInstanceId);
			dto.setVarsDocuments(obtenirVarsDocumentsProces(
					documents,
					valors));
			filtrarVariablesTasca(valors);
			//MesurarTemps.imprimirInstantStdoutIReiniciar("IPDTO2", "1");
			Map<String, ParellaCodiValorDto> valorsDomini = obtenirValorsDomini(
					null,
					processInstanceId,
					camps,
					valors);
			dto.setValorsDomini(valorsDomini);
			//MesurarTemps.imprimirInstantStdoutIReiniciar("IPDTO2", "2");
			Map<String, List<ParellaCodiValorDto>> valorsMultiplesDomini = obtenirValorsMultiplesDomini(
					null,
					processInstanceId,
					camps,
					valors);
			//MesurarTemps.imprimirInstantStdoutIReiniciar("IPDTO2", "3");
			dto.setValorsMultiplesDomini(valorsMultiplesDomini);
			dto.setVarsComText(textPerCamps(null, processInstanceId, camps, valors, valorsDomini, valorsMultiplesDomini));
			dto.setVariables(valors);
			//MesurarTemps.imprimirInstantStdoutIReiniciar("IPDTO2", "4");
		}
		//MesurarTemps.imprimirInstantStdoutIReiniciar("IPDTO", "5");
		return dto;
	}

	public DocumentDto toDocumentDto(
			Long documentStoreId,
			boolean ambContingutOriginal,
			boolean ambContingutSignat,
			boolean ambContingutVista,
			boolean perSignar,
			boolean ambSegellSignatura) {
		if (documentStoreId != null) {
			DocumentStore document = documentStoreDao.getById(documentStoreId, false);
			if (document != null) {
				DocumentDto dto = new DocumentDto();
				dto.setId(document.getId());
				dto.setDataCreacio(document.getDataCreacio());
				dto.setDataDocument(document.getDataDocument());
				dto.setArxiuNom(document.getArxiuNom());
				dto.setProcessInstanceId(document.getProcessInstanceId());
				dto.setSignat(document.isSignat());
				dto.setAdjunt(document.isAdjunt());
				dto.setAdjuntTitol(document.getAdjuntTitol());
				try {
					dto.setTokenSignatura(getDocumentTokenUtils().xifrarToken(documentStoreId.toString()));
				} catch (Exception ex) {
					logger.error("No s'ha pogut generar el token pel document " + documentStoreId, ex);
				}
				if (document.isSignat()) {
					dto.setUrlVerificacioCustodia(
							pluginCustodiaDao.getUrlComprovacioSignatura(
									documentStoreId.toString()));
				}
				String codiDocument;
				if (document.isAdjunt()) {
					dto.setAdjuntId(document.getJbpmVariable().substring(TascaService.PREFIX_ADJUNT.length()));
				} else {
					codiDocument = document.getJbpmVariable().substring(TascaService.PREFIX_DOCUMENT.length());
					JbpmProcessDefinition jpd = jbpmDao.findProcessDefinitionWithProcessInstanceId(document.getProcessInstanceId());
					DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmKeyIVersio(jpd.getKey(), jpd.getVersion());
					Document doc = documentDao.findAmbDefinicioProcesICodi(definicioProces.getId(), codiDocument);
					if (doc != null) {
						dto.setContentType(doc.getContentType());
						dto.setCustodiaCodi(doc.getCustodiaCodi());
						dto.setDocumentId(doc.getId());
						dto.setDocumentCodi(doc.getCodi());
						dto.setDocumentNom(doc.getNom());
						dto.setTipusDocPortasignatures(doc.getTipusDocPortasignatures());
						dto.setAdjuntarAuto(doc.isAdjuntarAuto());
					}
				}
				if (ambContingutOriginal) {
					dto.setArxiuContingut(
							getContingutDocumentAmbFont(document));
				}
				if (ambContingutSignat && document.isSignat() && isSignaturaFileAttached()) {
					dto.setSignatNom(
							getNomArxiuAmbExtensio(
									document.getArxiuNom(),
									getExtensioArxiuSignat()));
					byte[] signatura = pluginCustodiaDao.obtenirSignaturesAmbArxiu(document.getReferenciaCustodia());
					dto.setSignatContingut(signatura);
				}
				if (ambContingutVista) {
					String arxiuOrigenNom;
					byte[] arxiuOrigenContingut;
					// Obtenim l'origen per a generar la vista o bé del document original
					// o bé del document signat
					if (document.isSignat() && isSignaturaFileAttached()) {
						if (ambContingutSignat) {
							arxiuOrigenNom = dto.getSignatNom();
							arxiuOrigenContingut = dto.getSignatContingut();
						} else {
							arxiuOrigenNom = getNomArxiuAmbExtensio(
									document.getArxiuNom(),
									getExtensioArxiuSignat());
							arxiuOrigenContingut = pluginCustodiaDao.obtenirSignaturesAmbArxiu(document.getReferenciaCustodia());
						}
					} else {
						arxiuOrigenNom = dto.getArxiuNom();
						if (ambContingutOriginal) {
							arxiuOrigenContingut = dto.getArxiuContingut();
						} else {
							if (document.getFont().equals(DocumentFont.INTERNA)) {
								arxiuOrigenContingut = document.getArxiuContingut();
							} else {
								arxiuOrigenContingut = pluginGestioDocumentalDao.retrieveDocument(
												document.getReferenciaFont());
							}
						}
					}
					// Calculam l'extensió del document final de la vista
					String extensioActual = null;
					int indexPunt = arxiuOrigenNom.indexOf(".");
					if (indexPunt != -1)
						extensioActual = arxiuOrigenNom.substring(0, indexPunt);
					String extensioDesti = extensioActual;
					if (perSignar && isActiuConversioSignatura()) {
						extensioDesti = getExtensioArxiuSignat();
					} else if (document.isRegistrat()) {
						extensioDesti = getExtensioArxiuRegistrat();
					}
					dto.setVistaNom(dto.getArxiuNomSenseExtensio() + "." + extensioDesti);
					if ("pdf".equalsIgnoreCase(extensioDesti)) {
						// Si és un PDF podem estampar
						try {
							ByteArrayOutputStream vistaContingut = new ByteArrayOutputStream();
							DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
							String dataRegistre = null;
							if (document.getRegistreData() != null)
								dataRegistre = df.format(document.getRegistreData());
							String numeroRegistre = document.getRegistreNumero();
							getPdfUtils().estampar(
									arxiuOrigenNom,
									arxiuOrigenContingut,
									(ambSegellSignatura) ? !document.isSignat() : false,
									(ambSegellSignatura) ? getUrlComprovacioSignatura(documentStoreId, dto.getTokenSignatura()): null,
									document.isRegistrat(),
									numeroRegistre,
									dataRegistre,
									document.getRegistreOficinaNom(),
									document.isRegistreEntrada(),
									vistaContingut,
									extensioDesti);
							dto.setVistaContingut(vistaContingut.toByteArray());
						} catch (Exception ex) {
							logger.error("No s'ha pogut generar la vista pel document '" + document.getCodiDocument() + "'", ex);
						}
					} else {
						// Si no és un pdf retornam la vista directament
						dto.setVistaNom(arxiuOrigenNom);
						dto.setVistaContingut(arxiuOrigenContingut);
					}
				}
				if (document.isRegistrat()) {
					dto.setRegistreData(document.getRegistreData());
					dto.setRegistreNumero(document.getRegistreNumero());
					dto.setRegistreOficinaCodi(document.getRegistreOficinaCodi());
					dto.setRegistreOficinaNom(document.getRegistreOficinaNom());
					dto.setRegistreEntrada(document.isRegistreEntrada());
					dto.setRegistrat(true);
				}
				return dto;
			}
		}
		return null;
	}

	public List<FilaResultat> getResultatConsultaEnumeracio(
			DefinicioProces definicioProces,
			String campCodi,
			String textInicial) {
		Camp camp = null;
		for (Camp c: definicioProces.getCamps()) {
			if (c.getCodi().equals(campCodi)) {
				camp = c;
				break;
			}
		}
		if (camp != null && camp.getEnumeracio() != null) {
			Enumeracio enumeracio = camp.getEnumeracio();
			List<FilaResultat> resultat = new ArrayList<FilaResultat>();
			for (ParellaCodiValor parella: enumeracioValorsDao.getLlistaValors(enumeracio.getId())) {
				if (textInicial == null || ((String)parella.getValor()).toLowerCase().startsWith(textInicial.toLowerCase())) {
					FilaResultat fila = new FilaResultat();
					fila.addColumna(new ParellaCodiValor("codi", parella.getCodi()));
					fila.addColumna(new ParellaCodiValor("valor", parella.getValor()));
					resultat.add(fila);
				}
			}
			return resultat;
		}
		return new ArrayList<FilaResultat>();
	}

	public List<FilaResultat> getResultatConsultaDomini(
			DefinicioProces definicioProces,
			String taskId,
			String processInstanceId,
			String campCodi,
			String textInicial,
			Map<String, Object> valorsAddicionals) throws DominiException {
		Camp camp = null;
		for (Camp c: definicioProces.getCamps()) {
			if (c.getCodi().equals(campCodi)) {
				camp = c;
				break;
			}
		}
		if (camp != null && camp.getDomini() != null) {
			Map<String, Object> params = getParamsConsulta(
					taskId,
					processInstanceId,
					camp,
					valorsAddicionals);
			/*if (valorsAddicionals != null) {
				if (params == null)
					params = new HashMap<String, Object>();
				params.putAll(valorsAddicionals);
			}*/
			return getResultatConsultaDominiPerCamp(camp, params, textInicial);
		}
		return new ArrayList<FilaResultat>();
	}

	public List<FilaResultat> getResultatConsultaDominiPerCamp(
			Camp camp,
			Map<String, Object> params,
			String textInicial) throws DominiException {
		if (camp != null && camp.getDomini() != null) {
			Domini domini = camp.getDomini();
			try {
				List<FilaResultat> resultat = dominiDao.consultar(
						domini.getId(),
						camp.getDominiId(),
						params);
				// Filtra els resultats amb el textInicial (si n'hi ha)
				if (textInicial != null) {
					String columna = camp.getDominiCampText();
					Iterator<FilaResultat> it = resultat.iterator();
					while (it.hasNext()) {
						FilaResultat fr = it.next();
						for (ParellaCodiValor parella: fr.getColumnes()) {
							if (parella.getCodi().equals(columna) && !parella.getValor().toString().toUpperCase().contains(textInicial.toUpperCase())) {
								it.remove();
								break;
							}
						}
					}
				}
				return resultat;
			} catch (Exception ex) {
				throw new DominiException(getMessage("error.dtoConverter.consultarDomini"), ex);
			}
		}
		return new ArrayList<FilaResultat>();
	}

	public String getTextConsultaDomini(
			String taskId,
			String processInstanceId,
			Camp camp,
			Object valor) {
		ParellaCodiValor resultat = obtenirValorDomini(
				taskId,
				processInstanceId,
				null,
				camp,
				valor,
				null,
				false);
		if (resultat != null && resultat.getValor() != null)
			return resultat.getValor().toString();
		return null;
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
	public void setDocumentTascaDao(DocumentTascaDao documentTascaDao) {
		this.documentTascaDao = documentTascaDao;
	}
	@Autowired
	public void setDocumentDao(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}
	@Autowired
	public void setDocumentStoreDao(DocumentStoreDao documentStoreDao) {
		this.documentStoreDao = documentStoreDao;
	}
	@Autowired
	public void setCampTascaDao(CampTascaDao campTascaDao) {
		this.campTascaDao = campTascaDao;
	}
	@Autowired
	public void setCampAgrupacioDao(CampAgrupacioDao campAgrupacioDao) {
		this.campAgrupacioDao = campAgrupacioDao;
	}
	@Autowired
	public void setFirmaTascaDao(FirmaTascaDao firmaTascaDao) {
		this.firmaTascaDao = firmaTascaDao;
	}
	@Autowired
	public void setDefinicioProcesDao(DefinicioProcesDao definicioProcesDao) {
		this.definicioProcesDao = definicioProcesDao;
	}
	@Autowired
	public void setDominiDao(DominiDao dominiDao) {
		this.dominiDao = dominiDao;
	}
	@Autowired
	public void setPluginPersonaDao(PluginPersonaDao pluginPersonaDao) {
		this.pluginPersonaDao = pluginPersonaDao;
	}
	@Autowired
	public void setPluginGestioDocumentalDao(
			PluginGestioDocumentalDao pluginGestioDocumentalDao) {
		this.pluginGestioDocumentalDao = pluginGestioDocumentalDao;
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
	public void setEnumeracioValorsDao(EnumeracioValorsDao enumeracioValorsDao) {
		this.enumeracioValorsDao = enumeracioValorsDao;
	}
	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}



	private Map<String, ParellaCodiValorDto> obtenirValorsDomini(
			String taskId,
			String processInstanceId,
			Collection<Camp> camps,
			Map<String, Object> valors) throws DominiException {
		Map<String, ParellaCodiValor> resposta = new HashMap<String, ParellaCodiValor>();
		if (valors != null) {
			for (Camp camp: camps) {
				if (!camp.isMultiple() && (camp.getTipus().equals(TipusCamp.SELECCIO) || camp.getTipus().equals(TipusCamp.SUGGEST))) {
					Object valor = valors.get(camp.getCodi());
					ParellaCodiValor codiValor = obtenirValorDomini(
							taskId,
							processInstanceId,
							null,
							camp,
							valor,
							valors.get(TascaService.PREFIX_TEXT_SUGGEST + camp.getCodi()),
							true);
					resposta.put(camp.getCodi(), codiValor);
				}
			}
		}
		Map<String, ParellaCodiValorDto> respostaDto = new HashMap<String, ParellaCodiValorDto>();
		for (String clau: resposta.keySet()) {
			ParellaCodiValor parella = resposta.get(clau);
			ParellaCodiValorDto parellaDto = null;
			if (parella != null) {
				parellaDto = new ParellaCodiValorDto(
						parella.getCodi(),
						parella.getValor());
			}
			respostaDto.put(clau, parellaDto);
		}
		return respostaDto;
	}
	private Map<String, List<ParellaCodiValorDto>> obtenirValorsMultiplesDomini(
			String taskId,
			String processInstanceId,
			Collection<Camp> camps,
			Map<String, Object> valors) throws DominiException {
		Map<String, List<ParellaCodiValorDto>> resposta = new HashMap<String, List<ParellaCodiValorDto>>();
		if (valors != null) {
			for (Camp camp: camps) {
				if (camp.isMultiple()) {
					Object valor = valors.get(camp.getCodi());
					if (valor instanceof Object[]) {
						List<ParellaCodiValorDto> codisValor = new ArrayList<ParellaCodiValorDto>();
						for (int i = 0; i < Array.getLength(valor); i++) {
							ParellaCodiValor codiValor = obtenirValorDomini(
									taskId,
									processInstanceId,
									null,
									camp,
									Array.get(valor, i),
									valors.get(TascaService.PREFIX_TEXT_SUGGEST + camp.getCodi()),
									true);
							ParellaCodiValorDto parellaDto = null;
							if (codiValor != null) {
								parellaDto = new ParellaCodiValorDto(
									codiValor.getCodi(),
									codiValor.getValor());
							}
							codisValor.add(parellaDto);
						}
						resposta.put(camp.getCodi(), codisValor);
					}
				}
			}
		}
		return resposta;
	}
	private ParellaCodiValor obtenirValorDomini(
			String taskId,
			String processInstanceId,
			Map<String, Object> valorsAddicionals,
			Camp camp,
			Object valor,
			Object valorText,
			boolean actualitzarJbpm) throws DominiException {
		if (valor == null)
			return null;
		ParellaCodiValor resposta = null;
		if (valorText != null) {
			resposta = new ParellaCodiValor(
					(String)valor,
					valorText);
		}
		TipusCamp tipus = camp.getTipus();
		if (tipus.equals(TipusCamp.SELECCIO) || tipus.equals(TipusCamp.SUGGEST)) {
			if (camp.getDomini() != null) {
				Domini domini = camp.getDomini();
				try {
					List<FilaResultat> resultat = dominiDao.consultar(
							domini.getId(),
							camp.getDominiId(),
							getParamsConsulta(
									taskId,
									processInstanceId,
									camp,
									valorsAddicionals));
					String columnaCodi = camp.getDominiCampValor();
					String columnaValor = camp.getDominiCampText();
					Iterator<FilaResultat> it = resultat.iterator();
					while (it.hasNext()) {
						FilaResultat fr = it.next();
						for (ParellaCodiValor parellaCodi: fr.getColumnes()) {
							if (parellaCodi.getCodi().equals(columnaCodi) && parellaCodi.getValor().toString().equals(valor)) {
								for (ParellaCodiValor parellaValor: fr.getColumnes()) {
									if (parellaValor.getCodi().equals(columnaValor)) {
										ParellaCodiValor codiValor = new ParellaCodiValor(
												parellaCodi.getValor().toString(),
												parellaValor.getValor());
										resposta = codiValor;
										break;
									}
								}
								break;
							}
						}
					}
				} catch (Exception ex) {
					//throw new DominiException("No s'ha pogut consultar el domini", ex);
					logger.error("No s'ha pogut consultar el domini", ex);
				}
			} else if (camp.getEnumeracio() != null) {
				Enumeracio enumeracio = camp.getEnumeracio();
				for (ParellaCodiValor parella: enumeracioValorsDao.getLlistaValors(enumeracio.getId())) {
					if (valor.equals(parella.getCodi())) {
						resposta = new ParellaCodiValor(
								parella.getCodi(),
								parella.getValor());
					}
				}
			}
			/*if (isOptimitzarConsultesDomini() && actualitzarJbpm && valor != null && resposta != null) {
				// Per evitar fer consultes de domini innecessàries
				String[] valorPerGuardar = new String[] {
						resposta.getCodi(),
						resposta.getValor().toString()};
				if (taskId != null)
					jbpmDao.setTaskInstanceVariable(
							taskId,
							camp.getCodi(),
							valorPerGuardar);
				else if (processInstanceId != null)
					jbpmDao.setProcessInstanceVariable(
							processInstanceId,
							camp.getCodi(),
							valorPerGuardar);
			}*/
		}
		return resposta;
	}

	private void filtrarVariablesTasca(Map<String, Object> variables) {
		if (variables != null) {
			variables.remove(TascaService.VAR_TASCA_VALIDADA);
			variables.remove(TascaService.VAR_TASCA_DELEGACIO);
			List<String> codisEsborrar = new ArrayList<String>();
			for (String codi: variables.keySet()) {
				if (	codi.startsWith(TascaService.PREFIX_DOCUMENT) ||
						codi.startsWith(TascaService.PREFIX_SIGNATURA) ||
						codi.startsWith(TascaService.PREFIX_ADJUNT) ||
						codi.startsWith(TascaService.PREFIX_TEXT_SUGGEST))
					codisEsborrar.add(codi);
			}
			for (String codi: codisEsborrar)
				variables.remove(codi);
		}
	}

	private Map<String, DocumentDto> obtenirVarsDocumentsTasca(
			String taskId,
			String processInstanceId,
			List<DocumentTasca> documents) {
		Map<String, DocumentDto> resposta = new HashMap<String, DocumentDto>();
		for (DocumentTasca document: documents) {
			Long documentStoreId = (Long)jbpmDao.getTaskInstanceVariable(
					taskId,
					TascaService.PREFIX_DOCUMENT + document.getDocument().getCodi());
			if (documentStoreId == null)
				documentStoreId = (Long)jbpmDao.getProcessInstanceVariable(
						processInstanceId,
						TascaService.PREFIX_DOCUMENT + document.getDocument().getCodi());
			if (documentStoreId != null) {
				resposta.put(
						document.getDocument().getCodi(),
						toDocumentDto(
								documentStoreId,
								false,
								false,
								false,
								false,
								false));
			}
		}
		return resposta;
	}

	private Map<String, DocumentDto> obtenirVarsDocumentsProces(
			List<Document> documents,
			Map<String, Object> valors) {
		Map<String, DocumentDto> resposta = new HashMap<String, DocumentDto>();
		if (valors != null) {
			// Afegeix els documents
			for (Document document: documents) {
				Long documentStoreId = (Long)valors.get(
						TascaService.PREFIX_DOCUMENT + document.getCodi());
				if (documentStoreId != null) {
					resposta.put(
							document.getCodi(),
							toDocumentDto(
									documentStoreId,
									false,
									false,
									false,
									false,
									false));
				}
			}
			// Afegeix els adjunts
			for (String var: valors.keySet()) {
				if (var.startsWith(TascaService.PREFIX_ADJUNT)) {
					Long documentStoreId = (Long)valors.get(var);
					resposta.put(
							var.substring(TascaService.PREFIX_ADJUNT.length()),
							toDocumentDto(
									documentStoreId,
									false,
									false,
									false,
									false,
									false));
				}
			}
		}
		return resposta;
	}

	private Map<String, DocumentDto> obtenirVarsDocumentsPerSignarTasca(
			String taskId,
			String processInstanceId,
			List<FirmaTasca> signatures) {
		Map<String, DocumentDto> resposta = new HashMap<String, DocumentDto>();
		if (signatures != null) {
			for (FirmaTasca signatura: signatures) {
				Long documentStoreId = (Long)jbpmDao.getTaskInstanceVariable(
						taskId,
						TascaService.PREFIX_DOCUMENT + signatura.getDocument().getCodi());
				if (documentStoreId == null)
					documentStoreId = (Long)jbpmDao.getProcessInstanceVariable(
							processInstanceId,
							TascaService.PREFIX_DOCUMENT + signatura.getDocument().getCodi());
				if (documentStoreId != null) {
					DocumentDto dto = toDocumentDto(
							documentStoreId,
							false,
							false,
							false,
							false,
							false);
					if (dto != null) {
						try {
							dto.setTokenSignatura(getDocumentTokenUtils().xifrarToken(documentStoreId.toString()));
							dto.setTokenSignaturaMultiple(getDocumentTokenUtils().xifrarTokenMultiple(
									new String[] {
											taskId,
											documentStoreId.toString()}));
						} catch (Exception ex) {
							logger.error("No s'ha pogut generar el token pel document " + documentStoreId, ex);
						}
						if (dto.isSignat()) {
							Object signatEnTasca = jbpmDao.getTaskInstanceVariable(taskId, TascaService.PREFIX_SIGNATURA + dto.getDocumentCodi());
							dto.setSignatEnTasca(signatEnTasca != null);
						} else {
							dto.setSignatEnTasca(false);
						}
						resposta.put(
								signatura.getDocument().getCodi(),
								dto);
					}
				}
			}
		}
		return resposta;
	}

	private Map<String, PersonaDto> getPersonesMap(String assignee, Set<String> pooledActors) {
		Map<String, PersonaDto> resposta = new HashMap<String, PersonaDto>();
		if (assignee != null)
			resposta.put(assignee, pluginPersonaDao.findAmbCodiPlugin(assignee));
		if (pooledActors != null) {
			for (String actorId: pooledActors)
				resposta.put(actorId, pluginPersonaDao.findAmbCodiPlugin(actorId));
		}
		return resposta;
	}

	private Map<String, Object> textPerCamps(
			String taskId,
			String processInstanceId,
			Collection<Camp> camps,
			Map<String, Object> valors,
			Map<String, ParellaCodiValorDto> valorsDomini,
			Map<String, List<ParellaCodiValorDto>> valorsMultiplesDomini) {
		Map<String, Object> resposta = new HashMap<String, Object>();
		if (valors != null) {
			for (String key: valors.keySet()) {
				boolean found = false;
				for (Camp camp: camps) {
					if (camp.getCodi().equals(key)) {
						if (camp.getTipus().equals(TipusCamp.REGISTRE)) {
							Object valor = valors.get(key);
							if (valor != null && valor instanceof Object[]) {
								List<String[]> grid = new ArrayList<String[]>();
								for (int i = 0; i < Array.getLength(valor); i++) {
									Object valorRegistre = Array.get(valor, i);
									if (valorRegistre != null) {
										String[] texts = new String[camp.getRegistreMembres().size()];
										Map<String, Object> valorsAddicionalsConsulta = new HashMap<String, Object>();
										for (int j = 0; j < camp.getRegistreMembres().size(); j++) {
											if (j < Array.getLength(valorRegistre)) {
												valorsAddicionalsConsulta.put(
														camp.getRegistreMembres().get(j).getMembre().getCodi(),
														Array.get(valorRegistre, j));
											}
										}
										for (int j = 0; j < Array.getLength(valorRegistre); j++) {
											if (j == camp.getRegistreMembres().size())
												break;
											Camp membreRegistre = camp.getRegistreMembres().get(j).getMembre();
											if (membreRegistre.getTipus().equals(TipusCamp.SUGGEST) || membreRegistre.getTipus().equals(TipusCamp.SELECCIO)) {
												ParellaCodiValor codiValor = obtenirValorDomini(
														taskId,
														processInstanceId,
														valorsAddicionalsConsulta,
														membreRegistre,
														Array.get(valorRegistre, j),
														null,
														true);
												ParellaCodiValorDto parellaDto = null;
												if (codiValor != null) {
													parellaDto = new ParellaCodiValorDto(
															codiValor.getCodi(),
															codiValor.getValor());
												}
												texts[j] = textPerCamp(
														membreRegistre,
														Array.get(valorRegistre, j),
														parellaDto);
											} else {
												texts[j] = textPerCamp(
														membreRegistre,
														Array.get(valorRegistre, j),
														null);
											}
										}
										grid.add(texts);
									}
								}
								resposta.put(key, grid);
							} else {
								resposta.put(key, null);
							}
						} else if (camp.isMultiple()) {
							Object valor = valors.get(key);
							if (valor != null) {
								if (valor instanceof Object[]) {
									List<String> texts = new ArrayList<String>();
									for (int i = 0; i < Array.getLength(valor); i++) {
										String t = null;
										if (camp.getTipus().equals(TipusCamp.SUGGEST) || camp.getTipus().equals(TipusCamp.SELECCIO)) {
											if (valorsMultiplesDomini.get(key).size() > i)
												t = textPerCamp(camp, Array.get(valor, i), valorsMultiplesDomini.get(key).get(i));
											else
												t = "!" + Array.get(valor, i) + "!";
										} else {
											t = textPerCamp(camp, Array.get(valor, i), null);
										}
										if (t != null)
											texts.add(t);
									}
									resposta.put(key, texts);
								} else {
									logger.warn("No s'ha pogut convertir el camp " + camp.getCodi() + "a text: El camp és múltiple però el seu valor no és un array (" + valor.getClass().getName() + ")");
								}
							} else {
								resposta.put(key, null);
							}
						} else {
							resposta.put(
									key,
									textPerCamp(camp, valors.get(key), valorsDomini.get(key)));
						}
						found = true;
						break;
					}
				}
				if (!found) {
					// Si no hi ha cap camp associat el mostra com un String
					Object valor = valors.get(key);
					if (valor != null)
						resposta.put(key, valor.toString());
				}
			}
		}
		return resposta;
	}
	private String textPerCamp(
			Camp camp,
			Object valor,
			ParellaCodiValorDto valorDomini) {
		if (valor == null) return null;
		if (camp == null)
			return valor.toString();
		else
			return Camp.getComText(
					camp.getTipus(),
					valor,
					(valorDomini != null) ? (String)valorDomini.getValor() : null);
	}

	private Map<String, Object> getParamsConsulta(
			String taskId,
			String processInstanceId,
			Camp camp,
			Map<String, Object> valorsAddicionals) {
		String dominiParams = camp.getDominiParams();
		if (dominiParams == null || dominiParams.length() == 0)
			return null;
		Map<String, Object> params = new HashMap<String, Object>();
		String[] pairs = dominiParams.split(";");
		for (String pair: pairs) {
			String[] parts = pair.split(":");
			String paramCodi = parts[0];
			String campCodi = parts[1];
			Object value = null;
			if (campCodi.startsWith("#{")) {
				if (processInstanceId != null) {
					value = jbpmDao.evaluateExpression(taskId, processInstanceId, campCodi, null);
				} else if (taskId != null) {
					JbpmTask task = jbpmDao.getTaskById(taskId);
					value = jbpmDao.evaluateExpression(taskId, task.getProcessInstanceId(), campCodi, null);
				}
			} else {
				if (valorsAddicionals != null && valorsAddicionals.size() > 0)
					value = valorsAddicionals.get(campCodi);
				if (value == null && taskId != null)
					value = jbpmDao.getTaskInstanceVariable(taskId, campCodi);
				if (value == null && processInstanceId != null)
					value = jbpmDao.getProcessInstanceVariable(processInstanceId, campCodi);
			}
			if (value != null)
				params.put(paramCodi, value);
		}
		return params;
	}

	private String evaluarTasca(JbpmTask task, Tasca tasca, Map<String, Object> textos) {
		String titolTasca = null;
		if (tasca.getNomScript() != null && tasca.getNomScript().length() > 0) {
			try {
				titolTasca = (String)jbpmDao.evaluateExpression(
						task.getId(),
						task.getProcessInstanceId(),
						tasca.getNomScript(),
						textos);
			} catch (Exception ex) {
				logger.error("No s'ha pogut evaluar l'script per canviar el titol de la tasca", ex);
			}
		} else {
			titolTasca = tasca.getNom();
		}
		jbpmDao.describeTaskInstance(task.getId(), "@" + titolTasca);
		return titolTasca;
	}
	private boolean isTascaEvaluada(JbpmTask task) {
		return task.getDescription() != null && task.getDescription().startsWith("@");
	}

	private boolean isSignaturaFileAttached() {
		return "true".equalsIgnoreCase((String)GlobalProperties.getInstance().get("app.signatura.plugin.file.attached"));
	}

	private boolean isActiuConversioSignatura() {
		String actiuConversio = (String)GlobalProperties.getInstance().get("app.conversio.actiu");
		if (!"true".equalsIgnoreCase(actiuConversio))
			return false;
		String actiuConversioSignatura = (String)GlobalProperties.getInstance().get("app.conversio.signatura.actiu");
		return "true".equalsIgnoreCase(actiuConversioSignatura);
	}

	private String getNomArxiuAmbExtensio(
			String arxiuNomOriginal,
			String extensio) {
		if (!isActiuConversioSignatura())
			return arxiuNomOriginal;
		if (extensio == null)
			extensio = "";
		int indexPunt = arxiuNomOriginal.indexOf(".");
		if (indexPunt != -1) {
			return arxiuNomOriginal.substring(0, indexPunt) + "." + extensio;
		} else {
			return arxiuNomOriginal + "." + extensio;
		}
	}
	private String getExtensioArxiuSignat() {
		return (String)GlobalProperties.getInstance().get("app.conversio.signatura.extension");
	}
	private String getExtensioArxiuRegistrat() {
		return (String)GlobalProperties.getInstance().get("app.conversio.registre.extension");
	}

	private byte[] getContingutDocumentAmbFont(DocumentStore document) {
		if (document.getFont().equals(DocumentFont.INTERNA))
			return document.getArxiuContingut();
		else
			return pluginGestioDocumentalDao.retrieveDocument(
							document.getReferenciaFont());
	}

	private String getUrlComprovacioSignatura(Long documentStoreId, String token) {
		String urlCustodia = pluginCustodiaDao.getUrlComprovacioSignatura(documentStoreId.toString());
		if (urlCustodia != null) {
			return urlCustodia;
		} else {
			String baseUrl = (String)GlobalProperties.getInstance().get("app.base.verificacio.url");
			if (baseUrl == null)
				baseUrl = (String)GlobalProperties.getInstance().get("app.base.url");
			return baseUrl + "/signatura/verificarExtern.html?token=" + token;
		}
	}

	private PdfUtils getPdfUtils() {
		if (pdfUtils == null)
			pdfUtils = new PdfUtils();
		return pdfUtils;
	}

	private DocumentTokenUtils getDocumentTokenUtils() {
		if (documentTokenUtils == null)
			documentTokenUtils = new DocumentTokenUtils(
					(String)GlobalProperties.getInstance().get("app.encriptacio.clau"));
		return documentTokenUtils;
	}

	private String getMessage(String key, Object[] vars) {
		try {
			return messageSource.getMessage(
					key,
					vars,
					null);
		} catch (NoSuchMessageException ex) {
			return "???" + key + "???";
		}
	}
	private String getMessage(String key) {
		return getMessage(key, null);
	}

	private static final Log logger = LogFactory.getLog(DtoConverter.class);

}
