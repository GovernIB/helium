/**
 * 
 */
package net.conselldemallorca.helium.model.service;

import java.lang.reflect.Array;
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
import net.conselldemallorca.helium.model.dao.CampAgrupacioDao;
import net.conselldemallorca.helium.model.dao.CampTascaDao;
import net.conselldemallorca.helium.model.dao.DefinicioProcesDao;
import net.conselldemallorca.helium.model.dao.DocumentDao;
import net.conselldemallorca.helium.model.dao.DocumentStoreDao;
import net.conselldemallorca.helium.model.dao.DocumentTascaDao;
import net.conselldemallorca.helium.model.dao.DominiDao;
import net.conselldemallorca.helium.model.dao.ExpedientDao;
import net.conselldemallorca.helium.model.dao.FirmaTascaDao;
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
		if (!starting) {
			JbpmProcessInstance processInstance = jbpmDao.getProcessInstance(expedient.getProcessInstanceId());
			dto.setDataFi(processInstance.getEnd());
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
		TascaDto dto = new TascaDto();
		dto.setId(task.getId());
		dto.setNom(tasca.getNom());
		dto.setMissatgeInfo(tasca.getMissatgeInfo());
		dto.setMissatgeWarn(tasca.getMissatgeWarn());
		dto.setDelegable(tasca.getExpressioDelegacio() != null);
		dto.setTipus(tasca.getTipus());
		dto.setJbpmName(tasca.getJbpmName());
		dto.setValidada(validada);
		dto.setDocumentsComplet(documentsComplet);
		dto.setSignaturesComplet(signaturesComplet);
		dto.setDefinicioProces(tasca.getDefinicioProces());
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
		dto.setOutcomes(jbpmDao.findTaskInstanceOutcomes(task.getId()));
		dto.setExpedient(expedientDao.findAmbProcessInstanceId(
				jbpmDao.getRootProcessInstance(task.getProcessInstanceId()).getId()));
		dto.setValidacions(tasca.getValidacions());
		dto.setProcessInstanceId(task.getProcessInstanceId());
		dto.setRecursForm(tasca.getRecursForm());
		dto.setFormExtern(tasca.getFormExtern());
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
		if (ambVariables || !isTascaEvaluada(task)) {
			Map<String, Object> valors = jbpmDao.getTaskInstanceVariables(task.getId());
			DelegationInfo delegationInfo = obtenirDelegationInfo(valors);
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
			dto.setVariables(valors);
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
			if (!isTascaEvaluada(task))
				evaluarTasca(task, dto.getVarsComText());
		}
		String titolNou = getTitolNouPerTasca(task);
		if (titolNou != null)
			dto.setNom(titolNou);
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
		Hibernate.initialize(documentsTasca);
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
		dto.setExpedient(expedientDao.findAmbProcessInstanceId(processInstanceId));
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
			dto.setVariables(valors);
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
				if (ambContingut) {
					if (document.getFont().equals(DocumentFont.INTERNA)) {
						dto.setArxiuContingut(document.getArxiuContingut());
					} else {
						dto.setArxiuContingut(
								pluginGestioDocumentalDao.retrieveDocument(
										document.getReferenciaFont()));
					}
				}
				dto.setSignat(document.isSignat());
				dto.setAdjunt(document.isAdjunt());
				dto.setAdjuntTitol(document.getAdjuntTitol());
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
				if (document.isRegistrat()) {
					dto.setRegistreData(document.getRegistreData());
					dto.setRegistreNumero(document.getRegistreNumero());
					dto.setRegistreAny(document.getRegistreAny());
					dto.setRegistreOficinaCodi(document.getRegistreOficinaCodi());
					dto.setRegistreOficinaNom(document.getRegistreOficinaNom());
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
					camp);
			if (valorsAddicionals != null) {
				if (params == null)
					params = new HashMap<String, Object>();
				params.putAll(valorsAddicionals);
			}
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
					ParellaCodiValor codiValor = null;
					if (valor instanceof String) {
						codiValor = obtenirValorDomini(
								taskId,
								processInstanceId,
								camp,
								valor);
					} else if (valor instanceof String[]) {
						String[] cv = (String[])valor;
						codiValor = new ParellaCodiValor(cv[0], cv[1]);
					}
					if (codiValor != null)
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
							ParellaCodiValor codiValor = null;
							Object v = Array.get(valor, i);
							if (v instanceof String) {
								codiValor = obtenirValorDomini(
										taskId,
										processInstanceId,
										camp,
										v);
							} else if (v instanceof String[]) {
								String[] cv = (String[])v;
								codiValor = new ParellaCodiValor(cv[0], cv[1]);
							}
							if (codiValor != null)
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
			Camp camp,
			Object valor) throws DominiException {
		ParellaCodiValor resposta = null;
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
									camp));
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
					throw new DominiException("No s'ha pogut consultar el domini", ex);
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
						codi.startsWith(TascaService.PREFIX_ADJUNT))
					codisEsborrar.add(codi);
			}
			for (String codi: codisEsborrar)
				variables.remove(codi);
		}
	}

	private Map<String, Object> obtenirVarsDocumentsTasca(
			String taskId,
			String processInstanceId,
			List<DocumentTasca> documents) {
		Map<String, Object> resposta = new HashMap<String, Object>();
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

	private Map<String, Object> obtenirVarsDocumentsPerSignarTasca(
			String taskId,
			String processInstanceId,
			List<FirmaTasca> signatures) {
		Map<String, Object> resposta = new HashMap<String, Object>();
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

	private DelegationInfo obtenirDelegationInfo(Map<String, Object> variables) {
		return (DelegationInfo)variables.get(TascaService.VAR_TASCA_DELEGACIO);
	}

	private String getTitolNouPerTasca(JbpmTask task) {
		return (String)jbpmDao.getTaskInstanceVariable(task.getId(), VAR_TASCA_TITOLNOU);
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
							if (valor != null) {
								List<String[]> grid = new ArrayList<String[]>();
								for (int i = 0; i < Array.getLength(valor); i++) {
									String[] texts = new String[camp.getRegistreMembres().size()];
									Object valorRegistre = Array.get(valor, i);
									for (int j = 0; j < Array.getLength(valorRegistre); j++) {
										if (j == camp.getRegistreMembres().size())
											break;
										Camp membreRegistre = camp.getRegistreMembres().get(j).getMembre();
										if (membreRegistre.getTipus().equals(TipusCamp.SUGGEST) || membreRegistre.getTipus().equals(TipusCamp.SELECCIO)) {
											ParellaCodiValor codiValor = obtenirValorDomini(
													taskId,
													processInstanceId,
													membreRegistre,
													Array.get(valorRegistre, j));
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
									if (camp.getTipus().equals(TipusCamp.SUGGEST) || camp.getTipus().equals(TipusCamp.SELECCIO))
										t = textPerCamp(camp, Array.get(valor, i), valorsMultiplesDomini.get(key).get(i));
									else
										t = textPerCamp(camp, Array.get(valor, i), null);
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
				text = new DecimalFormat("#,###.00").format((Long)value);
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
				text = terminiComText((String)value);
			} else {
				text = value.toString();
			}
			return text;
		} catch (Exception ex) {
			// Error de conversió de tipus
			return value.toString();
		}
	}

	private String terminiComText(String termini) {
		String[] parts = termini.split("/");
		StringBuffer sb = new StringBuffer();
		int anys;
		int mesos;
		int dies;
		if (parts.length == 0) {
			anys = 0;
			mesos = 0;
			dies = 0;
		} else if (parts.length == 1) {
			anys = 0;
			mesos = 0;
			dies = Integer.parseInt(parts[0]);
		} else if (parts.length == 2) {
			anys = 0;
			mesos = Integer.parseInt(parts[0]);
			dies = Integer.parseInt(parts[1]);
		} else {
			anys = Integer.parseInt(parts[0]);
			mesos = Integer.parseInt(parts[1]);
			dies = Integer.parseInt(parts[2]);
		}
		boolean plural = false;
		if (anys > 0) {
			sb.append(anys);
			plural = anys > 1;
			sb.append((plural) ? " anys": " any");
			if (mesos > 0 && dies > 0)
				sb.append(", ");
			else if (mesos > 0 || dies > 0)
				sb.append(" i ");
		}
		if (mesos > 0) {
			sb.append(mesos);
			plural = mesos > 1;
			sb.append((plural) ? " mesos": " mes");
			if (dies > 0)
				sb.append(" i ");
		}
		if (dies > 0) {
			sb.append(dies);
			plural = dies > 1;
			sb.append((plural) ? " dies": " dia");
		}
		return sb.toString();
	}

	private Map<String, Object> getParamsConsulta(
			String taskId,
			String processInstanceId,
			Camp camp) {
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
				if (taskId != null)
					value = jbpmDao.getTaskInstanceVariable(taskId, campCodi);
				else if (processInstanceId != null)
					value = jbpmDao.getProcessInstanceVariable(processInstanceId, campCodi);
			}
			if (value != null)
				params.put(paramCodi, value);
		}
		return params;
	}

	private String evaluarTasca(JbpmTask task, Map<String, Object> valors) {
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
						valors);
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
	private boolean isTascaEvaluada(JbpmTask task) {
		Object valor = jbpmDao.getTaskInstanceVariable(task.getId(), VAR_TASCA_EVALUADA);
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

	private static final Log logger = LogFactory.getLog(DtoConverter.class);

}
