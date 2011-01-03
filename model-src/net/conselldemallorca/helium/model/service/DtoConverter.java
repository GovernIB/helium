/**
 * 
 */
package net.conselldemallorca.helium.model.service;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import net.conselldemallorca.helium.integracio.domini.FilaResultat;
import net.conselldemallorca.helium.integracio.domini.ParellaCodiValor;
import net.conselldemallorca.helium.integracio.plugins.persones.Persona;
import net.conselldemallorca.helium.jbpm3.integracio.DelegationInfo;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmDao;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessDefinition;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.jbpm3.integracio.Termini;
import net.conselldemallorca.helium.model.dao.CampAgrupacioDao;
import net.conselldemallorca.helium.model.dao.CampTascaDao;
import net.conselldemallorca.helium.model.dao.DefinicioProcesDao;
import net.conselldemallorca.helium.model.dao.DocumentDao;
import net.conselldemallorca.helium.model.dao.DocumentStoreDao;
import net.conselldemallorca.helium.model.dao.DocumentTascaDao;
import net.conselldemallorca.helium.model.dao.DominiDao;
import net.conselldemallorca.helium.model.dao.ExpedientDao;
import net.conselldemallorca.helium.model.dao.FirmaTascaDao;
import net.conselldemallorca.helium.model.dao.PluginCustodiaDao;
import net.conselldemallorca.helium.model.dao.PluginGestioDocumentalDao;
import net.conselldemallorca.helium.model.dao.PluginPersonaDao;
import net.conselldemallorca.helium.model.dao.TascaDao;
import net.conselldemallorca.helium.model.dto.DocumentDto;
import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.model.dto.TascaDto;
import net.conselldemallorca.helium.model.exception.DominiException;
import net.conselldemallorca.helium.model.hibernate.Camp;
import net.conselldemallorca.helium.model.hibernate.CampTasca;
import net.conselldemallorca.helium.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.model.hibernate.Document;
import net.conselldemallorca.helium.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.model.hibernate.Domini;
import net.conselldemallorca.helium.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.model.hibernate.Expedient;
import net.conselldemallorca.helium.model.hibernate.FirmaTasca;
import net.conselldemallorca.helium.model.hibernate.Tasca;
import net.conselldemallorca.helium.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.model.hibernate.DocumentStore.DocumentFont;
import net.conselldemallorca.helium.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Convertidor de dades cap a DTOs
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Service
public class DtoConverter {

	public static final String VAR_TASCA_TITOLNOU = "H3l1um#tasca.titolnou";
	public static final String VAR_TASCA_EVALUADA = "H3l1um#tasca.evaluada";

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



	public ExpedientDto toExpedientDto(Expedient expedient, boolean starting) {
		ExpedientDto dto = new ExpedientDto();
		dto.setId(expedient.getId());
		dto.setProcessInstanceId(expedient.getProcessInstanceId());
		dto.setTitol(expedient.getTitol());
		dto.setNumero(expedient.getNumero());
		dto.setNumeroDefault(expedient.getNumeroDefault());
		dto.setComentari(expedient.getComentari());
		dto.setInfoAturat(expedient.getInfoAturat());
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
		dto.setRegistreNumero(expedient.getRegistreNumero());
		dto.setRegistreData(expedient.getRegistreData());
		dto.setGeoPosX(expedient.getGeoPosX());
		dto.setGeoPosY(expedient.getGeoPosY());
		dto.setGeoReferencia(expedient.getGeoReferencia());
		if (!starting) {
			JbpmProcessInstance processInstance = jbpmDao.getProcessInstance(expedient.getProcessInstanceId());
			dto.setDataFi(processInstance.getEnd());
		}
		return dto;
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
				if (!isTascaEvaluada(task.getId())) {
					List<CampTasca> cts = campTascaDao.findAmbTascaOrdenats(tasca.getId());
					List<Camp> campsTasca = new ArrayList<Camp>();
					for (CampTasca campTasca: cts)
						campsTasca.add(campTasca.getCamp());
					Map<String, ParellaCodiValor> valorsDominiTasca = obtenirValorsDomini(
							task.getId(),
							null,
							campsTasca,
							valorsTasca);
					Map<String, List<ParellaCodiValor>> valorsMultiplesDominiTasca = obtenirValorsMultiplesDomini(
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
					Map<String, ParellaCodiValor> valorsDominiProces = obtenirValorsDomini(
							null,
							task.getProcessInstanceId(),
							campsProces,
							valorsProces);
					Map<String, List<ParellaCodiValor>> valorsMultiplesDominiProces = obtenirValorsMultiplesDomini(
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
					String titolNou = evaluarTasca(task, textPerCamps);
					if (titolNou != null)
						dto.setNom(titolNou);
				} else {
					String titolNou = (String)valorsTasca.get(VAR_TASCA_TITOLNOU);
					if (titolNou != null)
						dto.setNom(titolNou);
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
				Map<String, ParellaCodiValor> valorsDomini = obtenirValorsDomini(
						task.getId(),
						null,
						camps,
						valors);
				dto.setValorsDomini(valorsDomini);
				Map<String, List<ParellaCodiValor>> valorsMultiplesDomini = obtenirValorsMultiplesDomini(
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
		//Camps
		List<CampTasca> campsTasca = tasca.getCamps();
		dto.setCamps(campsTasca);
		//Documents
		List<DocumentTasca> documentsTasca = tasca.getDocuments();
		for (DocumentTasca document: documentsTasca)
			Hibernate.initialize(document.getDocument());
		dto.setDocuments(documentsTasca);
		//Configuració de valors
		if (valors != null) {
			List<Camp> camps = new ArrayList<Camp>();
			for (CampTasca campTasca: campsTasca)
				camps.add(campTasca.getCamp());
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
		Set<String> resourceNames = jbpmDao.getResourceNames(jpd.getId());
		dto.setImatgeDisponible(resourceNames.contains("processimage.jpg"));
		Set<Camp> camps = definicioProces.getCamps();
		dto.setCamps(camps);
		List<Document> documents = documentDao.findAmbDefinicioProces(definicioProces.getId());
		dto.setDocuments(documents);
		dto.setAgrupacions(campAgrupacioDao.findAmbDefinicioProcesOrdenats(definicioProces.getId()));
		if (ambVariables) {
			Map<String, Object> valors = jbpmDao.getProcessInstanceVariables(processInstanceId);
			dto.setVarsDocuments(obtenirVarsDocumentsProces(
					documents,
					valors));
			filtrarVariablesTasca(valors);
			Map<String, ParellaCodiValor> valorsDomini = obtenirValorsDomini(
					null,
					processInstanceId,
					camps,
					valors);
			dto.setValorsDomini(valorsDomini);
			Map<String, List<ParellaCodiValor>> valorsMultiplesDomini = obtenirValorsMultiplesDomini(
					null,
					processInstanceId,
					camps,
					valors);
			dto.setValorsMultiplesDomini(valorsMultiplesDomini);
			dto.setVarsComText(textPerCamps(null, processInstanceId, camps, valors, valorsDomini, valorsMultiplesDomini));
			dto.setVariables(valors);
		}
		return dto;
	}

	public DocumentDto toDocumentDto(Long documentStoreId, boolean ambContingut) {
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
				dto.setTokenSignatura(xifrarToken("0001#" + documentStoreId.toString()));
				String codiDocument;
				if (document.isAdjunt()) {
					dto.setAdjuntId(document.getJbpmVariable().substring(TascaService.PREFIX_ADJUNT.length()));
				} else {
					codiDocument = document.getJbpmVariable().substring(TascaService.PREFIX_DOCUMENT.length());
					JbpmProcessDefinition jpd = jbpmDao.findProcessDefinitionWithProcessInstanceId(document.getProcessInstanceId());
					DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmId(jpd.getId());
					Document doc = documentDao.findAmbDefinicioProcesICodi(definicioProces.getId(), codiDocument);
					if (doc != null) {
						dto.setContentType(doc.getContentType());
						dto.setCustodiaCodi(doc.getCustodiaCodi());
						dto.setDocumentId(doc.getId());
						dto.setDocumentCodi(doc.getCodi());
						dto.setDocumentNom(doc.getNom());
						dto.setTipusDocPortasignatures(doc.getTipusDocPortasignatures());
					}
				}
				if (ambContingut) {
					if (document.getFont().equals(DocumentFont.INTERNA)) {
						dto.setArxiuContingut(document.getArxiuContingut());
					} else {
						dto.setArxiuContingut(
								pluginGestioDocumentalDao.retrieveDocument(
										document.getReferenciaFont()));
					}
					if (document.isSignat() && isSignaturaFileAttached()) {
						String arxiuNom = document.getArxiuNom();
						int indexPunt = arxiuNom.indexOf(".");
						if (indexPunt != -1) {
							String extensio = (String)GlobalProperties.getInstance().get("app.conversio.signatura.extension");
							if (extensio != null && extensio.length() > 0)
								dto.setSignatNom(arxiuNom.substring(0, indexPunt) + "." + extensio);
						}
						byte[] signatura = pluginCustodiaDao.obtenirSignaturesAmbArxiu(document.getReferenciaCustodia());
						if ("afirma".equalsIgnoreCase((String)GlobalProperties.getInstance().get("app.signatura.tipus")))
							dto.setSignatContingut(Base64.decodeBase64(signatura));
						else
							dto.setSignatContingut(signatura);
					}
				}
				if (document.isRegistrat()) {
					dto.setRegistreData(document.getRegistreData());
					dto.setRegistreNumero(document.getRegistreNumero());
					dto.setRegistreAny(document.getRegistreAny());
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
			for (ParellaCodiValor parella: enumeracio.getLlistaValors()) {
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
				throw new DominiException("No s'ha pogut consultar el domini", ex);
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



	private Map<String, ParellaCodiValor> obtenirValorsDomini(
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
		return resposta;
	}
	private Map<String, List<ParellaCodiValor>> obtenirValorsMultiplesDomini(
			String taskId,
			String processInstanceId,
			Collection<Camp> camps,
			Map<String, Object> valors) throws DominiException {
		Map<String, List<ParellaCodiValor>> resposta = new HashMap<String, List<ParellaCodiValor>>();
		if (valors != null) {
			for (Camp camp: camps) {
				if (camp.isMultiple()) {
					Object valor = valors.get(camp.getCodi());
					if (valor instanceof Object[]) {
						List<ParellaCodiValor> codisValor = new ArrayList<ParellaCodiValor>();
						for (int i = 0; i < Array.getLength(valor); i++) {
							ParellaCodiValor codiValor = obtenirValorDomini(
									taskId,
									processInstanceId,
									null,
									camp,
									Array.get(valor, i),
									valors.get(TascaService.PREFIX_TEXT_SUGGEST + camp.getCodi()),
									true);
							codisValor.add(codiValor);
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
				for (ParellaCodiValor parella: enumeracio.getLlistaValors()) {
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
			variables.remove(VAR_TASCA_EVALUADA);
			variables.remove(VAR_TASCA_TITOLNOU);
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
						toDocumentDto(documentStoreId, false));
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
							toDocumentDto(documentStoreId, false));
				}
			}
			// Afegeix els adjunts
			for (String var: valors.keySet()) {
				if (var.startsWith(TascaService.PREFIX_ADJUNT)) {
					Long documentStoreId = (Long)valors.get(var);
					resposta.put(
							var.substring(TascaService.PREFIX_ADJUNT.length()),
							toDocumentDto(documentStoreId, false));
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
					DocumentDto dto = toDocumentDto(documentStoreId, false);
					if (dto != null) {
						dto.setTokenSignatura(xifrarToken(taskId + "#" + documentStoreId.toString()));
						Object signatEnTasca = jbpmDao.getTaskInstanceVariable(taskId, TascaService.PREFIX_SIGNATURA + dto.getDocumentCodi());
						dto.setSignatEnTasca(signatEnTasca != null);
						resposta.put(
								signatura.getDocument().getCodi(),
								dto);
					}
				}
			}
		}
		return resposta;
	}

	private Map<String, Persona> getPersonesMap(String assignee, Set<String> pooledActors) {
		Map<String, Persona> resposta = new HashMap<String, Persona>();
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
			Map<String, ParellaCodiValor> valorsDomini,
			Map<String, List<ParellaCodiValor>> valorsMultiplesDomini) {
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
									String[] texts = new String[camp.getRegistreMembres().size()];
									Object valorRegistre = Array.get(valor, i);
									Map<String, Object> valorsAddicionalsConsulta = new HashMap<String, Object>();
									for (int j = 0; j < camp.getRegistreMembres().size(); j++) {
										valorsAddicionalsConsulta.put(
												camp.getRegistreMembres().get(j).getMembre().getCodi(),
												Array.get(valorRegistre, j));
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
											texts[j] = textPerCamp(
													membreRegistre,
													Array.get(valorRegistre, j),
													codiValor);
										} else {
											texts[j] = textPerCamp(
													membreRegistre,
													Array.get(valorRegistre, j),
													null);
										}
									}
									grid.add(texts);
								}
								resposta.put(key, grid);
							} else {
								resposta.put(key, null);
							}
						} else if (camp.isMultiple()) {
							Object valor = valors.get(key);
							if (valor != null) {
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
			Object value,
			ParellaCodiValor valorDomini) {
		if (value == null) return null;
		try {
			String text = null;
			if (camp == null) {
				text = value.toString();
			} else if (camp.getTipus().equals(TipusCamp.INTEGER)) {
				text = new DecimalFormat("#").format((Long)value);
			} else if (camp.getTipus().equals(TipusCamp.FLOAT)) {
				text = new DecimalFormat("#.#").format((Long)value);
			} else if (camp.getTipus().equals(TipusCamp.PRICE)) {
				text = new DecimalFormat("#,###.00").format((BigDecimal)value);
			} else if (camp.getTipus().equals(TipusCamp.DATE)) {
				text = new SimpleDateFormat("dd/MM/yyyy").format((Date)value);
			} else if (camp.getTipus().equals(TipusCamp.BOOLEAN)) {
				text = (((Boolean)value).booleanValue()) ? "Si" : "No";
			} else if (camp.getTipus().equals(TipusCamp.TEXTAREA)) {
				text = (String)value;
			} else if (camp.getTipus().equals(TipusCamp.SELECCIO)) {
				text = (String)valorDomini.getValor();
			} else if (camp.getTipus().equals(TipusCamp.SUGGEST)) {
				text = (String)valorDomini.getValor();
			} else if (camp.getTipus().equals(TipusCamp.TERMINI)) {
				text = ((Termini)value).toString();
			} else {
				text = value.toString();
			}
			return text;
		} catch (Exception ex) {
			// Error de conversió de tipus
			return value.toString();
		}
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
				else if (taskId != null)
					value = jbpmDao.getTaskInstanceVariable(taskId, campCodi);
				else if (processInstanceId != null)
					value = jbpmDao.getProcessInstanceVariable(processInstanceId, campCodi);
			}
			if (value != null)
				params.put(paramCodi, value);
		}
		return params;
	}

	private String evaluarTasca(JbpmTask task, Map<String, Object> textos) {
		Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
				task.getName(),
				task.getProcessDefinitionId());
		String titolNou = null;
		if (tasca.getNomScript() != null && tasca.getNomScript().length() > 0) {
			try {
				titolNou = (String)jbpmDao.evaluateExpression(
						task.getId(),
						task.getProcessInstanceId(),
						tasca.getNomScript(),
						textos);
			} catch (Exception ex) {
				logger.error("No s'ha pogut evaluar l'script per canviar el titol de la tasca", ex);
			}
		}
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(VAR_TASCA_EVALUADA, new Date());
		if (titolNou != null)
			variables.put(VAR_TASCA_TITOLNOU, titolNou);
		jbpmDao.setTaskInstanceVariables(task.getId(), variables);
		return titolNou;
	}
	private boolean isTascaEvaluada(String taskId) {
		Object valor = jbpmDao.getTaskInstanceVariable(taskId, VAR_TASCA_EVALUADA);
		if (valor == null || !(valor instanceof Date))
			return false;
		return true;
	}

	private String xifrarToken(String token) {
		try {
			String secretKey = GlobalProperties.getInstance().getProperty("app.signatura.secret");
			if (secretKey == null)
				secretKey = TascaService.DEFAULT_SECRET_KEY;
			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(TascaService.DEFAULT_KEY_ALGORITHM);
			Cipher cipher = Cipher.getInstance(TascaService.DEFAULT_ENCRYPTION_SCHEME);
			cipher.init(
					Cipher.ENCRYPT_MODE,
					secretKeyFactory.generateSecret(new DESKeySpec(secretKey.getBytes())));
			byte[] encryptedText = cipher.doFinal(token.getBytes());
			byte[] base64Bytes = Base64.encodeBase64(encryptedText);
			return new String(Hex.encodeHex(base64Bytes));
		} catch (Exception ex) {
			logger.error("No s'ha pogut xifrar el token", ex);
			return token;
		}
	}

	/*private boolean isOptimitzarConsultesDomini() {
		return "true".equalsIgnoreCase((String)GlobalProperties.getInstance().get("app.optimitzar.consultes.domini"));
	}*/

	private boolean isSignaturaFileAttached() {
		return "true".equalsIgnoreCase((String)GlobalProperties.getInstance().get("app.signatura.plugin.file.attached"));
	}

	private static final Log logger = LogFactory.getLog(DtoConverter.class);

}
