/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.conselldemallorca.helium.core.common.JbpmVars;
import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;
import net.conselldemallorca.helium.core.helper.DominiHelper;
import net.conselldemallorca.helium.core.helperv26.DocumentHelper;
import net.conselldemallorca.helium.core.model.dao.CampAgrupacioDao;
import net.conselldemallorca.helium.core.model.dao.CampDao;
import net.conselldemallorca.helium.core.model.dao.CampTascaDao;
import net.conselldemallorca.helium.core.model.dao.ConsultaCampDao;
import net.conselldemallorca.helium.core.model.dao.DefinicioProcesDao;
import net.conselldemallorca.helium.core.model.dao.DocumentDao;
import net.conselldemallorca.helium.core.model.dao.DocumentTascaDao;
import net.conselldemallorca.helium.core.model.dao.EnumeracioValorsDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientDao;
import net.conselldemallorca.helium.core.model.dao.FirmaTascaDao;
import net.conselldemallorca.helium.core.model.dao.LuceneDao;
import net.conselldemallorca.helium.core.model.dao.PluginPersonaDao;
import net.conselldemallorca.helium.core.model.dao.TascaDao;
import net.conselldemallorca.helium.core.model.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientConsultaDissenyDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.core.model.dto.OperacioMassivaDto;
import net.conselldemallorca.helium.core.model.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassivaExpedient;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.core.model.hibernate.FirmaTasca;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.security.AclServiceDao;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.jbpm3.handlers.BasicActionHandler;
import net.conselldemallorca.helium.jbpm3.integracio.DelegationInfo;
import net.conselldemallorca.helium.jbpm3.integracio.DominiCodiDescripcio;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessDefinition;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.codahale.metrics.MetricRegistry;


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
	private CampTascaDao campTascaDao;
	private CampAgrupacioDao campAgrupacioDao;
	private FirmaTascaDao firmaTascaDao;
	private TascaDao tascaDao;
	private DefinicioProcesDao definicioProcesDao;
	private PluginPersonaDao pluginPersonaDao;
	private JbpmHelper jbpmDao;
	private EnumeracioValorsDao enumeracioValorsDao;
	private LuceneDao luceneDao;
	private CampDao campDao;
	private ConsultaCampDao consultaCampDao;
	private AclServiceDao aclServiceDao;
	private MessageSource messageSource;
	private DominiHelper dominiHelper;

	private DocumentHelper documentHelper;
	private ServiceUtils serviceUtils;
	private ExpedientService expedientService;
	private MetricRegistry metricRegistry;



	public ExpedientDto toExpedientDto(Expedient expedient, boolean starting) {
		ExpedientDto dto = new ExpedientDto();
		dto.setId(expedient.getId());
		dto.setProcessInstanceId(expedient.getProcessInstanceId());
		dto.setTitol(expedient.getTitol());
		dto.setNumero(expedient.getNumero());
		dto.setNumeroDefault(expedient.getNumeroDefault());
		dto.setComentari(expedient.getComentari());
		dto.setInfoAturat(expedient.getInfoAturat());
		dto.setComentariAnulat(expedient.getComentariAnulat());
		dto.setAnulat(expedient.isAnulat());
		dto.setDataInici(expedient.getDataInici());
		dto.setIniciadorCodi(expedient.getIniciadorCodi());
		dto.setIniciadorTipus(expedient.getIniciadorTipus());
		dto.setResponsableCodi(expedient.getResponsableCodi());
		dto.setGrupCodi(expedient.getGrupCodi());
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
		dto.setErrorDesc(expedient.getErrorDesc());
		dto.setErrorFull(expedient.getErrorFull());
		dto.setNotificacioTelematicaHabilitada(expedient.isNotificacioTelematicaHabilitada());
		dto.setTramitExpedientIdentificador(expedient.getTramitExpedientIdentificador());
		dto.setTramitExpedientClau(expedient.getTramitExpedientClau());
		dto.setErrorsIntegracions(expedient.isErrorsIntegracions());
		if (!starting) {
			JbpmProcessInstance processInstance = jbpmDao.getProcessInstance(expedient.getProcessInstanceId());
			dto.setDataFi(processInstance.getEnd());
			
			// Actualizamos la fecha de fin
			if (processInstance.getEnd() != null && !processInstance.getEnd().equals(expedient.getDataFi())) {
				expedient.setDataFi(processInstance.getEnd());
				expedientDao.saveOrUpdate(expedient);
				expedientDao.flush();
			} else if (processInstance.getEnd() == null && expedient.getDataFi() != null) {
				expedient.setDataFi(processInstance.getEnd());
				expedientDao.saveOrUpdate(expedient);
				expedientDao.flush();
			}
		}
		dto.setDataFi(expedient.getDataFi());
		dto.setAmbRetroaccio(expedient.isAmbRetroaccio());
		dto.setReindexarData(expedient.getReindexarData());
		dto.setReindexarError(expedient.isReindexarError());
		for (Expedient relacionat: expedient.getRelacionsOrigen()) {
			ExpedientDto relacionatDto = new ExpedientDto();
			relacionatDto.setId(relacionat.getId());
			relacionatDto.setTitol(relacionat.getTitol());
			relacionatDto.setNumero(relacionat.getNumero());
			relacionatDto.setDataInici(relacionat.getDataInici());
			relacionatDto.setTipus(relacionat.getTipus());
			relacionatDto.setEstat(relacionat.getEstat());
			relacionatDto.setProcessInstanceId(relacionat.getProcessInstanceId());
			dto.addExpedientRelacionat(relacionatDto);
		}
		return dto;
	}

	public String getTitolPerTasca(
			JbpmTask task,
			Tasca tasca) {
		String titol = null;
		if (tasca != null) {
			Map<String, Object> textPerCamps = new HashMap<String, Object>();
			titol = tasca.getNom();
			if (tasca.getNomScript() != null && tasca.getNomScript().length() > 0) {
				List<String> campsExpressio = getCampsExpressioTitol(tasca.getNomScript());
				Map<String, Object> valors = jbpmDao.getTaskInstanceVariables(task.getId());
				valors.putAll(jbpmDao.getProcessInstanceVariables(task.getProcessInstanceId()));
				for (String campCodi: campsExpressio) {
					Set<Camp> campsDefinicioProces = tasca.getDefinicioProces().getCamps();
					for (Camp camp: campsDefinicioProces) {
						if (camp.getCodi().equals(campCodi)) {
							textPerCamps.put(
									campCodi,
									getCampText(
											task.getId(),
											task.getProcessInstanceId(),
											camp,
											valors.get(campCodi)));
							break;
						}
					}
				}
				try {
					titol = (String)jbpmDao.evaluateExpression(
							task.getId(),
							task.getProcessInstanceId(),
							tasca.getNomScript(),
							textPerCamps);
				} catch (Exception ex) {
					logger.error("No s'ha pogut evaluar l'script per canviar el titol de la tasca", ex);
				}
			}
		} else {
			titol = task.getTaskName();
		}
		return titol;
	}

	public TascaDto toTascaDto(
			JbpmTask task,
			Map<String, Object> varsCommand,
			boolean ambVariables,
			boolean ambTexts,
			boolean validada,
			boolean documentsComplet,
			boolean signaturesComplet) {
		Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
				task.getTaskName(),
				task.getProcessDefinitionId());
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
		dto.setAgafada(task.isAgafada());
		dto.setExpedient(
				expedientDao.findAmbProcessInstanceId(
						jbpmDao.getRootProcessInstance(task.getProcessInstanceId()).getId()));
		dto.setOutcomes(jbpmDao.findTaskInstanceOutcomes(task.getId()));
		DelegationInfo delegationInfo = (DelegationInfo)jbpmDao.getTaskInstanceVariable(
				task.getId(),
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
		if (task.isCacheActiu()) {
			dto.setNom(task.getFieldFromDescription("titol"));
		} else {
			if (tasca != null)
				dto.setNom(tasca.getNom());
			else
				dto.setNom(task.getTaskName());
		}
		if (tasca != null) {
			dto.setTascaId(tasca.getId());
			dto.setMissatgeInfo(tasca.getMissatgeInfo());
			dto.setMissatgeWarn(tasca.getMissatgeWarn());
			dto.setDelegable(tasca.getExpressioDelegacio() != null);
			dto.setTipus(tasca.getTipus());
			dto.setJbpmName(tasca.getJbpmName());
			dto.setDefinicioProces(tasca.getDefinicioProces());
			dto.setValidacions(tasca.getValidacions());
			dto.setRecursForm(tasca.getRecursForm());
			dto.setFormExtern(tasca.getFormExtern());
			dto.setValidada(validada);
			dto.setDocumentsComplet(documentsComplet);
			dto.setSignaturesComplet(signaturesComplet);
			List<CampTasca> campsTasca = campTascaDao.findAmbTascaOrdenats(tasca.getId());
			dto.setCamps(campsTasca);
			List<DocumentTasca> documentsTasca = documentTascaDao.findAmbTascaOrdenats(tasca.getId());
			dto.setDocuments(documentsTasca);
			List<FirmaTasca> signaturesTasca = firmaTascaDao.findAmbTascaOrdenats(tasca.getId());
			dto.setSignatures(signaturesTasca);
			if (ambVariables) {
				Map<String, Object> valors = jbpmDao.getTaskInstanceVariables(task.getId());
				
//		Aquesta funcionalitat s'ha llevat p.o. de la DGTIC en conversa amb en Andreu Font, el 17/10/2012
//				
//				for (CampTasca variableCamp: campsTasca) {
//					if (variableCamp.isReadOnly()) {
//						Object valor = getServiceUtils().getVariableJbpmProcesValor(
//								task.getProcessInstanceId(),
//								variableCamp.getCamp().getCodi());
//						if (valor != null)
//							valors.put(
//								variableCamp.getCamp().getCodi(),
//								valor);
//						else
//							valors.remove(variableCamp.getCamp().getCodi());
//					}
//				}

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
				if (ambTexts) {
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
					getServiceUtils().revisarVariablesJbpm(valors);
					dto.setVarsComText(textPerCamps(task.getId(), null, camps, valors, valorsDomini, valorsMultiplesDomini));
				}
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

	public InstanciaProcesDto toInstanciaProcesDto(String processInstanceId , boolean ambImatgeProces, boolean ambVariables, boolean ambDocuments) {
		return toInstanciaProcesDto(processInstanceId , ambImatgeProces, ambVariables, ambDocuments, null, null);
	}
	public InstanciaProcesDto toInstanciaProcesDto(String processInstanceId , boolean ambImatgeProces, boolean ambVariables, boolean ambDocuments, String varRegistre, Object[] valorsRegistre) {
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
		dto.setDataInici(pi.getStart());
		dto.setDataFi(pi.getEnd());
		if (ambImatgeProces) {
			Set<String> resourceNames = jbpmDao.getResourceNames(jpd.getId());
			dto.setImatgeDisponible(resourceNames.contains("processimage.jpg"));
		}
		Set<Camp> camps = definicioProces.getCamps();
		dto.setCamps(camps);
		List<Document> documents = new ArrayList<Document>();
		if (ambDocuments) {
			Map<String, Object> valors = jbpmDao.getProcessInstanceVariables(processInstanceId);
			documents = documentDao.findAmbDefinicioProces(definicioProces.getId());
			dto.setDocuments(documents);
			dto.setVarsDocuments(obtenirVarsDocumentsProces(
					processInstanceId,
					documents,
					valors));
		}
		dto.setAgrupacions(campAgrupacioDao.findAmbDefinicioProcesOrdenats(definicioProces.getId()));
		if (ambVariables) {
			Map<String, Object> valors = jbpmDao.getProcessInstanceVariables(processInstanceId);
			if (valors == null)
				valors = new HashMap<String, Object>();
			if (varRegistre != null) 
				valors.put(varRegistre, valorsRegistre);
			filtrarVariablesTasca(valors);
			Map<String, ParellaCodiValorDto> valorsDomini = obtenirValorsDomini(
					null,
					processInstanceId,
					camps,
					valors);
			dto.setValorsDomini(valorsDomini);
			Map<String, List<ParellaCodiValorDto>> valorsMultiplesDomini = obtenirValorsMultiplesDomini(
					null,
					processInstanceId,
					camps,
					valors);
			dto.setValorsMultiplesDomini(valorsMultiplesDomini);
			getServiceUtils().revisarVariablesJbpm(valors);
			dto.setVarsComText(
					textPerCamps(
							null,
							processInstanceId,
							camps,
							valors,
							valorsDomini,
							valorsMultiplesDomini));
			dto.setVarsOcultes(obtenirVarsOcultes(camps));
			dto.setVariables(valors);
		}
		return dto;
	}

	public ExecucioMassivaDto toExecucioMassivaDto(ExecucioMassiva massiva) {
		ExecucioMassivaDto dto = new ExecucioMassivaDto();
		dto.setId(massiva.getId());
		dto.setUsuari(massiva.getUsuari());
		dto.setTipus(massiva.getTipus());
		dto.setDataInici(massiva.getDataInici());
		dto.setDataFi(massiva.getDataFi());
		dto.setParam1(massiva.getParam1());
		dto.setParam2(massiva.getParam2());
		dto.setEnviarCorreu(massiva.getEnviarCorreu());
		dto.setExpedientTipusId(massiva.getExpedientTipus().getId());
		for (ExecucioMassivaExpedient expedient: massiva.getExpedients()) {
			OperacioMassivaDto expedientDto = new OperacioMassivaDto();
			expedientDto.setDataInici(expedient.getDataInici());
			expedientDto.setDataFi(expedient.getDataFi());
			expedientDto.setEstat(expedient.getEstat());
			expedientDto.setError(expedient.getError());
			expedientDto.setOrdre(expedient.getOrdre());
		}
		return dto;
	}
	
	public OperacioMassivaDto toOperacioMassiva(ExecucioMassivaExpedient expedient) {
		OperacioMassivaDto dto = null;
		if (expedient != null) {
			dto = new OperacioMassivaDto();
			dto.setId(expedient.getId());
			dto.setDataInici(expedient.getDataInici());
			dto.setDataFi(expedient.getDataFi());
			dto.setEstat(expedient.getEstat());
			dto.setOrdre(expedient.getOrdre());
			dto.setError(expedient.getError());
			if (expedient.getExpedient() != null) dto.setExpedient(toExpedientDto(expedient.getExpedient(), false));
			dto.setParam1(expedient.getExecucioMassiva().getParam1());
			dto.setParam2(expedient.getExecucioMassiva().getParam2());
			dto.setEnviarCorreu(expedient.getExecucioMassiva().getEnviarCorreu());
			dto.setExecucioMassivaId(expedient.getExecucioMassiva().getId());
			if (expedient.getExecucioMassiva().getExpedientTipus() != null) dto.setExpedientTipusId(expedient.getExecucioMassiva().getExpedientTipus().getId());
			dto.setUltimaOperacio(expedient.getExecucioMassiva().getExpedients().size() == expedient.getOrdre() + 1);
			dto.setTipus(expedient.getExecucioMassiva().getTipus());
			dto.setUsuari(expedient.getExecucioMassiva().getUsuari());
			dto.setTascaId(expedient.getTascaId());
			dto.setProcessInstanceId(expedient.getProcessInstanceId());
		}
		return dto;
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
	public List<FilaResultat> getResultatConsultaConsulta(
			DefinicioProces definicioProces,
			String taskId,
			String processInstanceId,
			String campCodi,
			String textInicial,
			Map<String, Object> valorsAddicionals) {
		List<FilaResultat> resultat = new ArrayList<FilaResultat>();
		Camp camp = null;
		for (Camp c: definicioProces.getCamps()) {
			if (c.getCodi().equals(campCodi)) {
				camp = c;
				break;
			}
		}
		if (camp != null && camp.getConsulta() != null) {
			Consulta consulta = camp.getConsulta();
			List<Camp> campsFiltre = getServiceUtils().findCampsPerCampsConsulta(
					consulta,
					TipusConsultaCamp.FILTRE);
			List<Camp> campsInforme = getServiceUtils().findCampsPerCampsConsulta(
					consulta,
					TipusConsultaCamp.INFORME);
			afegirValorsPredefinits(consulta, valorsAddicionals, campsFiltre);
			List<Map<String, DadaIndexadaDto>> dadesExpedients = luceneDao.findAmbDadesExpedient(
					consulta.getEntorn(),
					consulta.getExpedientTipus(),
					campsFiltre,
					valorsAddicionals,
					campsInforme,
					null,
					true,
					0,
					-1);
			for (Map<String, DadaIndexadaDto> dadesExpedient: dadesExpedients) {
				FilaResultat fila = new FilaResultat();
				revisarDadesExpedientAmbValorsEnumeracionsODominis(
						dadesExpedient,
						campsInforme);
				for (String clau: dadesExpedient.keySet()) {
					// Les claus son de la forma [TipusExpedient]/[campCodi] i hem
					// de llevar el tipus d'expedient.
					int indexBarra = clau.indexOf("/");
					String clauSenseBarra = (indexBarra != -1) ? clau.substring(indexBarra + 1) : clau;
					fila.addColumna(
							new ParellaCodiValor(
									clauSenseBarra,
									dadesExpedient.get(clau)));
				}
				resultat.add(fila);
			}
		}
		return resultat;
	}
	
	private void afegirValorsPredefinits(
			Consulta consulta,
			Map<String, Object> valors,
			List<Camp> camps) {
		if (consulta.getValorsPredefinits() != null && consulta.getValorsPredefinits().length() > 0) {
			String[] parelles = consulta.getValorsPredefinits().split(",");
			for (int i = 0; i < parelles.length; i++) {
				String[] parella = (parelles[i].contains(":")) ? parelles[i].split(":") : parelles[i].split("=");
				if (parella.length == 2) {
					String campCodi = parella[0];
					String valor = parella[1];
					for (Camp camp: camps) {
						if (camp.getCodi().equals(campCodi)) {
							valors.put(
									camp.getDefinicioProces().getJbpmKey() + "." + campCodi,
									Camp.getComObject(
											camp.getTipus(),
											valor));
							break;
						}
					}
				}
			}
		}
	}
	
	public List<FilaResultat> getResultatConsultaDomini(
			DefinicioProces definicioProces,
			String taskId,
			String processInstanceId,
			String campCodi,
			String textInicial,
			Map<String, Object> valorsAddicionals) {
		Camp camp = null;
		for (Camp c: definicioProces.getCamps()) {
			if (c.getCodi().equals(campCodi)) {
				camp = c;
				break;
			}
		}
		if (camp != null && (camp.getDomini() != null || camp.isDominiIntern())) {
			Map<String, Object> params = getParamsConsulta(
					taskId,
					processInstanceId,
					camp,
					valorsAddicionals);
			return getResultatConsultaDominiPerCamp(
					definicioProces,
					camp,
					params,
					textInicial);
		}
		return new ArrayList<FilaResultat>();
	}

	public List<FilaResultat> getResultatConsultaDominiPerCamp(
			DefinicioProces definicioProces,
			Camp camp,
			Map<String, Object> params,
			String textInicial) {
		if (camp != null && (camp.getDomini() != null || camp.isDominiIntern())) {
			List<FilaResultat> resultat;
			if (camp.getDomini() != null) {
				resultat = dominiHelper.consultar(
						camp.getDomini(),
						camp.getDominiId(),
						params);
			} else {
				resultat = dominiHelper.consultarIntern(
						definicioProces.getEntorn(),
						definicioProces.getExpedientTipus(),
						camp.getDominiId(),
						params);
			}
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
		}
		return new ArrayList<FilaResultat>();
	}

	public String getCampText(
			String taskId,
			String processInstanceId,
			Camp camp,
			Object valor) {
		if (valor == null) return null;
		if (camp == null) {
			return valor.toString();
		} else {
			String textDomini = null;
			if (	camp.getTipus().equals(TipusCamp.SELECCIO) ||
					camp.getTipus().equals(TipusCamp.SUGGEST)) {
				if (valor instanceof DominiCodiDescripcio) {
					textDomini = ((DominiCodiDescripcio)valor).getDescripcio();
				} else {
					ParellaCodiValor resultat = obtenirValorDomini(
							taskId,
							processInstanceId,
							null,
							camp,
							valor,
							false);
					if (resultat != null && resultat.getValor() != null)
						textDomini = resultat.getValor().toString();
				}
			}
			return Camp.getComText(
					camp.getTipus(),
					valor,
					textDomini);
		}
	}

	public void revisarDadesExpedientAmbValorsEnumeracionsODominis(
			Map<String, DadaIndexadaDto> dadesExpedient,
			List<Camp> campsInforme) {
		for (Camp camp: campsInforme) {
			if (!camp.isDominiCacheText() && (TipusCamp.SELECCIO.equals(camp.getTipus()) || TipusCamp.SUGGEST.equals(camp.getTipus()))) {
				if (camp.getEnumeracio() != null) {
					String dadaIndexadaClau = camp.getDefinicioProces().getJbpmKey() + "/" + camp.getCodi();
					DadaIndexadaDto dadaIndexada = dadesExpedient.get(dadaIndexadaClau);
					if (dadaIndexada != null) {
						String text = getCampText(
								null,
								null,
								camp,
								dadaIndexada.getValorIndex());
						dadaIndexada.setValorMostrar(text);
					}
				}
			}
		}
	}
	
	@Autowired
	public void setDominiHelper(DominiHelper dominiHelper) {
		this.dominiHelper = dominiHelper;
	}
	@Autowired
	public void setExpedientService(ExpedientService expedientService) {
		this.expedientService = expedientService;
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
	public void setPluginPersonaDao(PluginPersonaDao pluginPersonaDao) {
		this.pluginPersonaDao = pluginPersonaDao;
	}
	@Autowired
	public void setLuceneDao(LuceneDao luceneDao) {
		this.luceneDao = luceneDao;
	}
	@Autowired
	public void setJbpmHelper(JbpmHelper jbpmDao) {
		this.jbpmDao = jbpmDao;
	}
	@Autowired
	public void setEnumeracioValorsDao(EnumeracioValorsDao enumeracioValorsDao) {
		this.enumeracioValorsDao = enumeracioValorsDao;
	}
	@Autowired
	public void setCampDao(CampDao campDao) {
		this.campDao = campDao;
	}
	@Autowired
	public void setConsultaCampDao(ConsultaCampDao consultaCampDao) {
		this.consultaCampDao = consultaCampDao;
	}
	@Autowired
	public void setAclServiceDao(AclServiceDao aclServiceDao) {
		this.aclServiceDao = aclServiceDao;
	}
	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	@Autowired
	public void setDocumentHelper(DocumentHelper documentHelper) {
		this.documentHelper = documentHelper;
	}
	@Autowired
	public void setMetricRegistry(MetricRegistry metricRegistry) {
		this.metricRegistry = metricRegistry;
	}



	private Map<String, ParellaCodiValorDto> obtenirValorsDomini(
			String taskId,
			String processInstanceId,
			Collection<Camp> camps,
			Map<String, Object> valors) {
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
			Map<String, Object> valors) {
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
			boolean actualitzarJbpm) {
		if (valor == null)
			return null;
		if (valor instanceof DominiCodiDescripcio) {
			return new ParellaCodiValor(
					((DominiCodiDescripcio)valor).getCodi(),
					((DominiCodiDescripcio)valor).getDescripcio());
		}
		ParellaCodiValor resposta = null;
		TipusCamp tipus = camp.getTipus();
		if (tipus.equals(TipusCamp.SELECCIO) || tipus.equals(TipusCamp.SUGGEST)) {
			if (camp.getDomini() != null || camp.isDominiIntern()) {
				Map<String, Object> paramsConsulta = null;
				paramsConsulta = getParamsConsulta(
						taskId,
						processInstanceId,
						camp,
						valorsAddicionals);
				List<FilaResultat> resultat;
				if (camp.getDomini() != null) {
					resultat = dominiHelper.consultar(
							camp.getDomini(),
							camp.getDominiId(),
							paramsConsulta);
				} else {
					resultat = dominiHelper.consultarIntern(
							camp.getDefinicioProces().getEntorn(),
							camp.getDefinicioProces().getExpedientTipus(),
							camp.getDominiId(),
							paramsConsulta);
				}
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
			} else if (camp.getEnumeracio() != null) {
				Enumeracio enumeracio = camp.getEnumeracio();
				for (ParellaCodiValor parella: enumeracioValorsDao.getLlistaValors(enumeracio.getId())) {
					// Per a evitar problemes amb caràcters estranys al codi (EXSANCI)
					String codiBo = null;
					if (parella.getCodi() != null)
						codiBo = parella.getCodi().replaceAll("\\p{Cntrl}", "").trim();
					String valorBo = valor.toString().replaceAll("\\p{Cntrl}", "").trim();
					if (valorBo.equals(codiBo)) {
						resposta = new ParellaCodiValor(
								parella.getCodi(),
								parella.getValor());
					}
				}
			} else if (camp.getConsulta() != null) {
				Consulta consulta = camp.getConsulta();
				List<ExpedientConsultaDissenyDto> dadesExpedients = expedientService.findAmbEntornConsultaDisseny(
						consulta.getEntorn().getId(),
						consulta.getId(),
						new HashMap<String, Object>(),
						null,
						true);
				
				Iterator<ExpedientConsultaDissenyDto> it = dadesExpedients.iterator();
				while(it.hasNext()){
					ExpedientConsultaDissenyDto exp = it.next();
					DadaIndexadaDto valorDto = exp.getDadesExpedient().get(camp.getConsultaCampValor());
					if(valorDto == null){
						valorDto = exp.getDadesExpedient().get(consulta.getExpedientTipus().getJbpmProcessDefinitionKey()+"/"+camp.getConsultaCampValor());
					}
					if(valorDto != null){
						if(valorDto.getValor().toString().equals(valor)){
							DadaIndexadaDto textDto = exp.getDadesExpedient().get(camp.getConsultaCampText());
							if(textDto == null){
								textDto = exp.getDadesExpedient().get(consulta.getExpedientTipus().getJbpmProcessDefinitionKey()+"/"+camp.getConsultaCampText());
							}
							resposta = new ParellaCodiValor(
									valorDto.getValorMostrar(),
									textDto.getValorMostrar()
									);
							break;
						}
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
				if (	codi.startsWith(JbpmVars.PREFIX_DOCUMENT) ||
						codi.startsWith(JbpmVars.PREFIX_SIGNATURA) ||
						codi.startsWith(JbpmVars.PREFIX_ADJUNT) ||
						codi.startsWith(BasicActionHandler.PARAMS_RETROCEDIR_VARIABLE_PREFIX))
					codisEsborrar.add(codi);
			}
			for (String codi: codisEsborrar) {
				variables.remove(codi);
			}
			for (String codi: variables.keySet()) {
				if (variables.get(codi) instanceof DominiCodiDescripcio) {
					DominiCodiDescripcio dcd = (DominiCodiDescripcio)variables.get(codi);
					variables.put(codi, dcd.getCodi());
				}
			}
		}
	}

	private Map<String, DocumentDto> obtenirVarsDocumentsTasca(
			String taskId,
			String processInstanceId,
			List<DocumentTasca> documents) {
		Map<String, DocumentDto> resposta = new HashMap<String, DocumentDto>();
		for (DocumentTasca document: documents) {
			DocumentDto dto = documentHelper.getDocumentSenseContingut(
					taskId,
					processInstanceId,
					document.getDocument().getCodi(),
					false,
					false);
			if (dto != null)
				resposta.put(document.getDocument().getCodi(), dto);
		}
		return resposta;
	}

	private Map<String, DocumentDto> obtenirVarsDocumentsProces(
			String processInstanceId,
			List<Document> documents,
			Map<String, Object> valors) {
		Map<String, DocumentDto> resposta = new HashMap<String, DocumentDto>();
		if (valors != null) {
			// Afegeix els documents
			for (Document document: documents) {
				DocumentDto dto = documentHelper.getDocumentSenseContingut(
						null,
						processInstanceId,
						document.getCodi(),
						false,
						true);
				if (dto != null)
					resposta.put(
							document.getCodi(),
							dto);
			}
			// Afegeix els adjunts
			for (String var: valors.keySet()) {
				if (var.startsWith(JbpmVars.PREFIX_ADJUNT)) {
					Long documentStoreId = (Long)valors.get(var);
					resposta.put(
							var.substring(JbpmVars.PREFIX_ADJUNT.length()),
							documentHelper.getDocumentSenseContingut(documentStoreId));
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
				DocumentDto dto = documentHelper.getDocumentSenseContingut(
						taskId,
						processInstanceId,
						signatura.getDocument().getCodi(),
						true,
						false);
				if (dto != null)
					resposta.put(
							signatura.getDocument().getCodi(),
							dto);
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
								if (camp.isMultiple()) {
									for (int i = 0; i < Array.getLength(valor); i++) {
										Object valorRegistre = Array.get(valor, i);
										if (valorRegistre != null) {
											grid.add(textsPerCampTipusRegistre(
													taskId,
													processInstanceId,
													camp,
													valorRegistre));
										}
									}
								} else {
									grid.add(textsPerCampTipusRegistre(
											taskId,
											processInstanceId,
											camp,
											valor));
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
												t = textPerCampDonatValorDomini(camp, Array.get(valor, i), valorsMultiplesDomini.get(key).get(i));
											else
												t = "!" + Array.get(valor, i) + "!";
										} else {
											t = textPerCampDonatValorDomini(camp, Array.get(valor, i), null);
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
									textPerCampDonatValorDomini(camp, valors.get(key), valorsDomini.get(key)));
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
	private String textPerCampDonatValorDomini(
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
	private String[] textsPerCampTipusRegistre(
			String taskId,
			String processInstanceId,
			Camp camp,
			Object valorRegistre) {
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
						true);
				ParellaCodiValorDto parellaDto = null;
				if (codiValor != null) {
					parellaDto = new ParellaCodiValorDto(
							codiValor.getCodi(),
							codiValor.getValor());
				}
				texts[j] = textPerCampDonatValorDomini(
						membreRegistre,
						Array.get(valorRegistre, j),
						parellaDto);
			} else {
				texts[j] = textPerCampDonatValorDomini(
						membreRegistre,
						Array.get(valorRegistre, j),
						null);
			}
		}
		return texts;
	}
	private Map<String, Boolean> obtenirVarsOcultes(
			Collection<Camp> camps) {
		Map<String, Boolean> resposta = new HashMap<String, Boolean>();
		for (Camp camp: camps)
			resposta.put(
					camp.getCodi(),
					new Boolean(camp.isOcult()));
		return resposta;
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
			if (campCodi.startsWith("@")) {
				value = (String)GlobalProperties.getInstance().get(campCodi.substring(1));
			} else if (campCodi.startsWith("#{")) {
				if (processInstanceId != null) {
					value = jbpmDao.evaluateExpression(taskId, processInstanceId, campCodi, null);
				} else if (taskId != null) {
					JbpmTask task = jbpmDao.getTaskById(taskId);
					value = jbpmDao.evaluateExpression(taskId, task.getProcessInstanceId(), campCodi, null);
				} else if (campCodi.startsWith("#{'")) {
					int index = campCodi.lastIndexOf("'");
					if (index != -1 && index > 2)
						value = campCodi.substring(3, campCodi.lastIndexOf("'"));
				}
			} else {
				if (valorsAddicionals != null && valorsAddicionals.size() > 0)
					value = valorsAddicionals.get(campCodi);
				if (value == null && taskId != null)
					value = getServiceUtils().getVariableJbpmTascaValor(taskId, campCodi);
				if (value == null && processInstanceId != null)
					value = getServiceUtils().getVariableJbpmProcesValor(processInstanceId, campCodi);
			}
			if (value != null)
				params.put(paramCodi, value);
		}
		return params;
	}

	private List<String> getCampsExpressioTitol(String expressio) {
		List<String> resposta = new ArrayList<String>();
		String[] parts = expressio.split("\\$\\{");
		for (String part: parts) {
			int index = part.indexOf("}");
			if (index != -1)
				resposta.add(part.substring(0, index));
		}
		return resposta;
	}

	private ServiceUtils getServiceUtils() {
		if (serviceUtils == null) {
			serviceUtils = new ServiceUtils(
					expedientDao,
					definicioProcesDao,
					campDao,
					consultaCampDao,
					luceneDao,
					this,
					jbpmDao,
					aclServiceDao,
					messageSource,
					metricRegistry);
		}
		return serviceUtils;
	}
	
	private static final Log logger = LogFactory.getLog(DtoConverter.class);
}
