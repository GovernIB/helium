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
import net.conselldemallorca.helium.model.dao.PluginCustodiaDao;
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
import net.conselldemallorca.helium.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.util.GlobalProperties;

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
		if (expedient.getIniciadorTipus().equals(IniciadorTipus.INTERN))
			dto.setIniciadorPersona(pluginPersonaDao.findAmbCodiPlugin(expedient.getIniciadorCodi()));
		if (expedient.getResponsableCodi() != null)
			dto.setResponsablePersona(pluginPersonaDao.findAmbCodiPlugin(expedient.getResponsableCodi()));
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
		String titolNou = getTitolNouPerTasca(task);
		if (titolNou != null)
			dto.setNom(titolNou);
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
		dto.setCamps(campsTasca);
		List<DocumentTasca> documentsTasca = documentTascaDao.findAmbTascaOrdenats(tasca.getId());
		dto.setDocuments(documentsTasca);
		List<FirmaTasca> signaturesTasca = firmaTascaDao.findAmbTascaOrdenats(tasca.getId());
		dto.setSignatures(signaturesTasca);
		if (ambVariables) {
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
			dto.setVarsComText(textPerCamps(camps, valors, valorsDomini, valorsMultiplesDomini));
		}
		return dto;
	}

	public TascaDto toTascaInicialDto(String startTaskName, String jbpmId, Map<String, Object> valors) {
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
		dto.setOutcomes(jbpmDao.findTaskOutcomes(jbpmId, startTaskName));
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
			dto.setVarsComText(textPerCamps(camps, valors, valorsDomini, valorsMultiplesDomini));
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
					if (document.isSignat()) {
						DocumentDto docSignat = pluginCustodiaDao.getDocumentAmbSignatura(document.getReferenciaCustodia());
						if ("true".equalsIgnoreCase((String)GlobalProperties.getInstance().getProperty("app.conversio.signatura.actiu")))
							dto.setArxiuNom(nomArxiuAmbExtensio(
									document.getArxiuNom(),
									GlobalProperties.getInstance().getProperty("app.conversio.signatura.extension")));
						dto.setArxiuContingut(docSignat.getArxiuContingut());
					} else {
						dto.setArxiuContingut(documentStoreDao.retrieveContingut(documentStoreId));
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
						dto.setTipusDocPortasignatures(doc.getTipusDocPortasignatures());
					}
				}
				return dto;
			}
		}
		return null;
	}

	public List<FilaResultat> getResultatConsultaDomini(
			String taskId,
			String processInstanceId,
			String campCodi,
			String textInicial) throws DominiException {
		Camp camp = null;
		if (taskId != null) {
			JbpmTask task = jbpmDao.getTaskById(taskId);
			Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
					task.getName(),
					task.getProcessDefinitionId());
			for (CampTasca ct: tasca.getCamps()) {
				if (ct.getCamp().getCodi().equals(campCodi)) {
					camp = ct.getCamp();
					break;
				}
			}
		} else if (processInstanceId != null) {
			JbpmProcessDefinition jpd = jbpmDao.findProcessDefinitionWithProcessInstanceId(processInstanceId);
			DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmId(jpd.getId());
			for (Camp c: definicioProces.getCamps()) {
				if (c.getCodi().equals(campCodi)) {
					camp = c;
					break;
				}
			}
		}
		if (camp != null) {
			return getResultatConsultaDominiPerCamp(
					camp,
					getParamsConsulta(
							taskId,
							processInstanceId,
							camp),
					textInicial);
		}
		return new ArrayList<FilaResultat>();
	}

	public List<FilaResultat> getResultatConsultaDominiPerCamp(
			Camp camp,
			Map<String, Object> params,
			String textInicial) throws DominiException {
		if (camp != null) {
			if (camp.getDomini() != null) {
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
			} else if (camp.getEnumeracio() != null) {
				Enumeracio enumeracio = camp.getEnumeracio();
				List<FilaResultat> resultat = new ArrayList<FilaResultat>();
				for (String key: enumeracio.getLlistaValors().keySet()) {
					String valor = enumeracio.getLlistaValors().get(key);
					if (textInicial == null || valor.toLowerCase().startsWith(textInicial.toLowerCase())) {
						FilaResultat fila = new FilaResultat();
						fila.addColumna(new ParellaCodiValor("codi", key));
						fila.addColumna(new ParellaCodiValor("valor", valor));
						resultat.add(fila);
					}
				}
				return resultat;
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
				if (!camp.isMultiple()) {
					Object valor = valors.get(camp.getCodi());
					ParellaCodiValor codiValor = null;
					if (valor instanceof String) {
						codiValor = obtenirValorDomini(
								camp,
								getParamsConsulta(
										taskId,
										processInstanceId,
										camp),
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
										camp,
										getParamsConsulta(
												taskId,
												processInstanceId,
												camp),
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
			Camp camp,
			Map<String, Object> params,
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
							params);
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
				Map<String, String> mapaValors = enumeracio.getLlistaValors();
				resposta = new ParellaCodiValor(
								(String)valor,
								mapaValors.get(valor));
			}
		}
		return resposta;
	}

	private void filtrarVariablesTasca(Map<String, Object> variables) {
		if (variables != null) {
			variables.remove(TascaService.VAR_TASCA_EVALUADA);
			variables.remove(TascaService.VAR_TASCA_TITOLNOU);
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
						dto.setTokenSignatura(taskId + "#" + documentStoreId);
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
		return (String)jbpmDao.getTaskInstanceVariable(task.getId(), TascaService.VAR_TASCA_TITOLNOU);
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
						if (!camp.isMultiple()) {
							resposta.put(
									key,
									textPerCamp(camp, valors.get(key), valorsDomini.get(key)));
						} else {
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
		if (dominiParams == null)
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
					value = jbpmDao.evaluateExpression(processInstanceId, campCodi);
				} else {
					JbpmTask task = jbpmDao.getTaskById(taskId);
					value = jbpmDao.evaluateExpression(task.getProcessInstanceId(), campCodi);
				}
			} else {
				if (taskId != null)
					value = jbpmDao.getTaskInstanceVariable(taskId, campCodi);
				else if (processInstanceId != null)
					value = jbpmDao.getProcessInstanceVariable(processInstanceId, campCodi);
			}
			params.put(paramCodi, value);
		}
		return params;
	}

	private String nomArxiuAmbExtensio(String fileName, String extensio) {
		int indexPunt = fileName.lastIndexOf(".");
		if (indexPunt != -1) {
			String nom = fileName.substring(0, indexPunt);
			return nom + "." + extensio;
		} else {
			return fileName + "." + extensio;
		}
	}

}
