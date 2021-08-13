package net.conselldemallorca.helium.jbpm3.api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.api.exception.NoTrobatException;
import net.conselldemallorca.helium.api.dto.*;
import net.conselldemallorca.helium.jbpm3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.jbpm3.integracio.DominiCodiDescripcio;
import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import net.conselldemallorca.helium.jbpm3.handlers.exception.HeliumHandlerException;
import net.conselldemallorca.helium.jbpm3.handlers.exception.ValidationException;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ActionInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesNotificacio;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesRegistreEntrada;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesRegistreNotificacio;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesRegistreSortida;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.EventInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ExpedientInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.FilaResultat;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.Interessat;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.JustificantRecepcioInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.NodeInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ParellaCodiValor;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ProcessDefinitionInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ProcessInstanceInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ReferenciaRDSJustificante;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.RespostaEnviar;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.RespostaRegistre;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.TaskInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.TaskInstanceInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.TerminiInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.TimerInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.TokenInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.TransitionInfo;
import net.conselldemallorca.helium.jbpm3.helper.ConversioTipusInfoHelper;

public class HeliumApiImpl implements HeliumApi {

	public static final String PARAMS_RETROCEDIR_VARIABLE_PREFIX = "H3l1um#params.retroces.";
	public static final String PARAMS_RETROCEDIR_SEPARADOR = "#@#";
	
	private ExecutionContext executionContext;
	
	public HeliumApiImpl(ExecutionContext executionContext) {
		super();
		this.executionContext = executionContext;
	}

	// Funcions de l'ExecutionContext
	@Override
	public TokenInfo getToken() {
		return ConversioTipusInfoHelper.toTokenInfo(executionContext.getToken());
	}
	
	@Override
	public NodeInfo getNode() {
		return ConversioTipusInfoHelper.toNodeInfo(executionContext.getNode());
	}
	
	@Override
	public ProcessDefinitionInfo getProcessDefinition() {
		return ConversioTipusInfoHelper.toProcessDefinitionInfo(executionContext.getProcessDefinition());
	}
	
	@Override
	public ProcessInstanceInfo getProcessInstance() {
		return ConversioTipusInfoHelper.toProcessInstanceInfo(executionContext.getProcessInstance());
	}
	
	@Override
	public ActionInfo getAction() {
		return ConversioTipusInfoHelper.toActionInfo(executionContext.getAction());
	}
	
	@Override
	public EventInfo getEvent() {
		return ConversioTipusInfoHelper.toEventInfo(executionContext.getEvent());
	}
	
	@Override
	public TransitionInfo getTransition() {
		return ConversioTipusInfoHelper.toTransitionInfo(executionContext.getTransition());
	}
	
	@Override
	public TaskInfo getTask() {
		return ConversioTipusInfoHelper.toTaskInfo(executionContext.getTask());
	}
	
	@Override
	public TaskInstanceInfo getTaskInstance() {
		return ConversioTipusInfoHelper.toTaskInstanceInfo(executionContext.getTaskInstance());
	}
	
	@Override
	public ProcessInstanceInfo getSubProcessInstance() {
		return ConversioTipusInfoHelper.toProcessInstanceInfo(executionContext.getSubProcessInstance());
	}
	
	@Override
	public TimerInfo getTimer() {
		return ConversioTipusInfoHelper.toTimerInfo(executionContext.getTimer());
	}
	
	@Override
	public void desarInformacioExecucio(String missatge) throws Exception {
		 ClassLoader surroundingClassLoader = Thread.currentThread().getContextClassLoader();
		  try {
			Long tokenId = executionContext.getToken().getId();
			Long taskId = Jbpm3HeliumBridge.getInstanceService().getTaskInstanceIdByExecutionTokenId(tokenId);
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String dataHandler = df.format(new Date());
			Jbpm3HeliumBridge.getInstanceService().addMissatgeExecucioTascaSegonPla(taskId, new String[]{dataHandler, missatge});
		  } finally {
			  Thread.currentThread().setContextClassLoader(surroundingClassLoader);
		  }
	}
	
	// Funcions Anàlisi
	@Override
	public Object getVariable(String varCodi) {
		return getVarValue(executionContext.getVariable(varCodi));
	}
	
	@Override
	public String getVariableText(String varCodi) {
		String text = null;
		if (isTaskInstanceExecution()) {
			text = getVariableInstanciaTascaText(varCodi);
		} 
		if (!isTaskInstanceExecution() || text == null) {
			text= getVariableInstanciaProcesText(varCodi);
		}
		return text;
	}
	
	@Override
	public Object getVariableInstanciaTasca(String varCodi) throws HeliumHandlerException {
		if (!isTaskInstanceExecution()) {
			throw new HeliumHandlerException("No taskInstance execution context.");
		}
		return getVarValue(executionContext.getTaskInstance().getVariable(varCodi));
	}
	
	@Override
	public String getVariableInstanciaTascaText(String varCodi) throws HeliumHandlerException {
		if (!isTaskInstanceExecution()) {
			throw new HeliumHandlerException("No taskInstance execution context.");
		}
		return Jbpm3HeliumBridge.getInstanceService().getDadaPerTaskInstance(
				getProcessInstanceId(),
				getTaskInstanceId(),
				varCodi);
	}
	
	@Override
	public void setVariableInstanciaTasca(String varCodi, Object varValor) throws HeliumHandlerException {
		if (!isTaskInstanceExecution()) {
			throw new HeliumHandlerException("No taskInstance execution context.");
		}
		if (varValor instanceof TerminiInfo) {
			TerminiInfo term = (TerminiInfo)varValor;
			Termini termini = new Termini();
			termini.setAnys(term.getAnys());
			termini.setMesos(term.getMesos());
			termini.setDies(term.getDies());
			varValor = termini;
		}
		executionContext.getTaskInstance().setVariable(varCodi, varValor);
	}
	
	@Override
	public Object getVariableInstanciaProces(String varCodi) {
		return getVarValue(executionContext.getContextInstance().getVariable(varCodi));
	}
	
	@Override
	public String getVariableInstanciaProcesText(String varCodi) {
		return Jbpm3HeliumBridge.getInstanceService().getDadaPerProcessInstance(
				getProcessInstanceId(),
				varCodi);
	}
	
	@Override
	public void setVariableInstanciaProces(String varCodi, Object varValor) {
		if (varValor instanceof TerminiInfo) {
			TerminiInfo term = (TerminiInfo)varValor;
			Termini termini = new Termini();
			termini.setAnys(term.getAnys());
			termini.setMesos(term.getMesos());
			termini.setDies(term.getDies());
			varValor = termini;
		}
		executionContext.getContextInstance().setVariable(varCodi, varValor);
	}
	
//	@Override
//	public void terminiGuardar(
//			String varName,
//			int anys,
//			int mesos,
//			int dies) throws HeliumHandlerException {
//		throw new HeliumHandlerException("Deprecated method. Use setVariableInstanciaTasca(varName, new TerminiInfo(anys, mesos, dies)) or setVariableInstanciaProces(varName, new TerminiInfo(anys, mesos, dies)) instead");
//	}
	
	@Override
	public ExpedientInfo getExpedient() {
		return ConversioTipusHelper.toExpedientInfo(getExpedientActual());
	}
	
	@Override
	public void errorValidacioInstanciaTasca(String error) throws HeliumHandlerException {
		if (!isTaskInstanceExecution()) {
			throw new HeliumHandlerException("No taskInstance execution context.");
		}
		throw new ValidationException(error);
	}
	
	@Override
	public List<FilaResultat> consultaDomini(
			String codiDomini,
			String id,
			Map<String, Object> parametres) throws HeliumHandlerException {
		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
		try {
			List<DominiRespostaFilaDto> files = Jbpm3HeliumBridge.getInstanceService().dominiConsultar(
					getProcessInstanceId(),
					codiDomini,
					id,
					parametres);
			if (files != null) {
				for (DominiRespostaFilaDto fila: files) {
					FilaResultat fres = new FilaResultat();
					for (DominiRespostaColumnaDto columna: fila.getColumnes()) {
						fres.addColumna(
								new ParellaCodiValor(
										columna.getCodi(),
										columna.getValor()));
					}
					resposta.add(fres);
				}
			}
			return resposta;
		} catch (NoTrobatException ex) {
			throw new HeliumHandlerException("No s'ha trobat el domini (codi=" + codiDomini + ")", ex);
		}
	}
	
	@Override
	public List<FilaResultat> consultaDominiIntern(
			String id,
			Map<String, Object> parametres) throws HeliumHandlerException {
		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
		try {
			List<DominiRespostaFilaDto> files = Jbpm3HeliumBridge.getInstanceService().dominiInternConsultar(
					getProcessInstanceId(),
					id,
					parametres);
			if (files != null) {
				for (DominiRespostaFilaDto fila: files) {
					FilaResultat fres = new FilaResultat();
					for (DominiRespostaColumnaDto columna: fila.getColumnes()) {
						fres.addColumna(
								new ParellaCodiValor(
										columna.getCodi(),
										columna.getValor()));
					}
					resposta.add(fres);
				}
			}
			return resposta;
		} catch (Exception ex) {
			throw new HeliumHandlerException("Error al executar el domini intern (id=" + id + ")", ex);
		}
	}
	
	@Override
	public List<ParellaCodiValor> consultaEnumeracio(String codiEnumeracio) throws HeliumHandlerException {
		try {
			List<ParellaCodiValor> resposta = new ArrayList<ParellaCodiValor>();
			List<EnumeracioValorDto> valors = Jbpm3HeliumBridge.getInstanceService().enumeracioConsultar(
					getProcessInstanceId(),
					codiEnumeracio);
			if (valors != null) {
				for (EnumeracioValorDto valor: valors) {
					resposta.add(
							new ParellaCodiValor(
									valor.getCodi(),
									valor.getNom()));
				}
			}
			return resposta;
		} catch (NoTrobatException ex) {
			throw new HeliumHandlerException("No s'ha trobat l'enumeració (codi=" + codiEnumeracio + ")", ex);
		}
	}
	
	@Override
	public String enumeracioGetValor(
			String codiEnumeracio,
			String codi) throws HeliumHandlerException {
			
		String valor = null;
		List<ParellaCodiValor> valorsEnumeracio = this.consultaEnumeracio(codiEnumeracio);
		for (ParellaCodiValor p : valorsEnumeracio) {
			if (p.getCodi().equals(codi)) {
				valor = p.getValor() != null ? p.getValor().toString() : null;
				break;
			}
		}
		return valor;
	}
	
	/** Modifica el valor text per a un codi d'una enumeració.
	 * 
	 * @param codiEnumeracio
	 * 			Codi de l'enumeració per trobar l'enumeració.
	 * @param codi
	 * 			Codi de la parella codi-valor de l'enumeració.
	 * @param valor
	 * 			Cadena de text pel nom de l'enumeració corresponent al codi.
	 */
	public void enumeracioSetValor(
			String codiEnumeracio,
			String codi,
			String valor) {
		Jbpm3HeliumBridge.getInstanceService().enumeracioSetValor(
						getProcessInstanceId(),
						codiEnumeracio,
						codi,
						valor);
	}
	
	@Override
	public List<ExpedientInfo> consultaExpedients(
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			String expedientTipusCodi,
			String estatCodi,
			boolean iniciat,
			boolean finalitzat) {
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
				ExpedientDto expedient = getExpedientActual();
				EstatDto estat = null;
				if (estatCodi != null && !"".equals(estatCodi)) {
					estat = Jbpm3HeliumBridge.getInstanceService().findEstatAmbEntornIExpedientTipusICodi(
						expedient.getEntorn().getId(),
						expedientTipusCodi,
						estatCodi);
				}
				// Consulta d'expedients
				List<ExpedientInfoDto> resultats =  Jbpm3HeliumBridge.getInstanceService().findExpedientsConsultaGeneral(
						expedient.getEntorn().getId(),
						titol,
						numero,
						dataInici1,
						dataInici2,
						expedient.getTipus().getId(),
						estat == null ? null : estat.getId(),
						iniciat,
						finalitzat);
				List<ExpedientInfo> expedients = new ArrayList<ExpedientInfo>();
				if (resultats != null) {
					for (ExpedientInfoDto expedientInfoDto: resultats) {
						expedients.add(ConversioTipusHelper.toExpedientInfo(expedientInfoDto));
					}
				}
				return expedients;
			} else {
				throw new HeliumHandlerException("No hi ha usuari autenticat");
			}
		} catch (Exception ex) {
			throw new HeliumHandlerException("Error en la consulta d'expedients", ex);
		}
	}
	
	/**
	 * Retorna el resultat d'una consulta d'expedients a partir del filtre de dades de Lucene.
	 * 
	 * @param expedientTipusCodi
	 * @param filtreValors
	 * @return
	 * @throws HeliumHandlerException En cas de validació errònia de paràmetres.
	 */
	@Override
	public List<ExpedientInfo> consultaExpedientsDadesIndexades(
			String expedientTipusCodi,
			Map<String, Object> filtreValors) {
		// Validacions
		if(expedientTipusCodi == null) throw new HeliumHandlerException("El paràmetre expedientTipusCodi no pot ser null");
		if(filtreValors == null || filtreValors.isEmpty()) throw new JbpmException("Els paràmetres del filtre no poden ser null o estar buits");
		
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
				ExpedientDto expedient = getExpedientActual();

				// Consulta d'expedients
				List<ExpedientInfoDto> resultats = Jbpm3HeliumBridge.getInstanceService().findExpedientsConsultaDadesIndexades(
						expedient.getEntorn().getId(),
						expedientTipusCodi,
						filtreValors);
				List<ExpedientInfo> expedients = new ArrayList<ExpedientInfo>();
				if (resultats != null) {
					for (ExpedientInfoDto expedientInfoDto: resultats) {
						expedients.add(ConversioTipusHelper.toExpedientInfo(expedientInfoDto));
					}
				}
				return expedients;
			} else {
				throw new HeliumHandlerException("No hi ha usuari autenticat");
			}
		} catch (Exception ex) {
			throw new HeliumHandlerException("Error en la consulta d'expedients", ex);
		}
	}

	@Override
	public void enviarEmail(
			List<String> recipients,
			List<String> ccRecipients,
			List<String> bccRecipients,
			String subject,
			String text,
			List<String> attachments) throws HeliumHandlerException {
		try {
			List<ArxiuDto> documents = null;
			if (attachments != null) {
				documents = new ArrayList<ArxiuDto>();
				for (String documentCodi: attachments) {
					String varCodi = Jbpm3HeliumBridge.getInstanceService().getCodiVariablePerDocumentCodi(documentCodi);
					Object valor = executionContext.getVariable(varCodi);
					if (valor instanceof Long) {
						Long documentStoreId = (Long) valor;
						ArxiuDto arxiu = Jbpm3HeliumBridge.getInstanceService().getArxiuPerMostrar(documentStoreId);
						if (arxiu != null) {
							documents.add(arxiu);
						}
					}
				}
			}
			Jbpm3HeliumBridge.getInstanceService().emailSend(
					Jbpm3HeliumBridge.getInstanceService().getHeliumProperty("es.caib.helium.correu.remitent"),
					recipients,
					ccRecipients,
					bccRecipients,
					subject,
					text,
					documents);
		} catch (Exception ex) {
			throw new HeliumHandlerException("No s'ha pogut enviar el missatge", ex);
		}
	}
	
	@Override
	public DocumentInfo getDocument(String documentCodi) {
		String varCodi = Jbpm3HeliumBridge.getInstanceService().getCodiVariablePerDocumentCodi(documentCodi);
		Object valor = executionContext.getVariable(varCodi);
		if (valor == null)
			return null;
		if (valor instanceof Long) {
			Long documentStoreId = (Long) valor;
			DocumentDto document = Jbpm3HeliumBridge.getInstanceService().getDocumentInfo(documentStoreId);
			if (document == null)
				return null;
			DocumentInfo resposta = new DocumentInfo();
			resposta.setId(documentStoreId);
			if (document.isAdjunt()) {
				resposta.setTitol(document.getAdjuntTitol());
			} else {
				resposta.setTitol(document.getDocumentNom());
			}
			resposta.setDataCreacio(document.getDataCreacio());
			resposta.setDataDocument(document.getDataDocument());
			resposta.setSignat(document.isSignat());
			if (document.isRegistrat()) {
				resposta.setRegistrat(true);
				resposta.setRegistreNumero(document.getRegistreNumero());
				resposta.setRegistreData(document.getRegistreData());
				resposta.setRegistreOficinaCodi(document.getRegistreOficinaCodi());
				resposta.setRegistreOficinaNom(document.getRegistreOficinaNom());
				resposta.setRegistreEntrada(document.isRegistreEntrada());
			}
//			if (ambArxiu) {
			ArxiuDto arxiu = Jbpm3HeliumBridge.getInstanceService().getArxiuPerMostrar(documentStoreId);
			resposta.setArxiuNom(arxiu.getNom());
			resposta.setArxiuContingut(arxiu.getContingut());
//			}
			return resposta;
		} else {
			throw new HeliumHandlerException("La referencia al document \"" + documentCodi + "\" no es del tipus correcte");
		}
	}
	
	@Override
	public RespostaRegistre registreEntrada(
			DadesRegistreEntrada dadesEntrada,
			List<String> documentsEntrada) {
		RegistreAnotacioDto anotacio = new RegistreAnotacioDto();
		anotacio.setOrganCodi(dadesEntrada.getOrganCodi());
		anotacio.setOficinaCodi(dadesEntrada.getOficinaCodi());
		anotacio.setEntitatCodi(dadesEntrada.getInteressatEntitatCodi());
		anotacio.setUnitatAdministrativa(dadesEntrada.getAnotacioUnitatAdministrativa());
		anotacio.setInteressatAutenticat(true);
		anotacio.setInteressatNif(dadesEntrada.getInteressatNif());
		anotacio.setInteressatNomAmbCognoms(dadesEntrada.getInteressatNomAmbCognoms());
		anotacio.setInteressatPaisCodi(dadesEntrada.getInteressatPaisCodi());
		anotacio.setInteressatPaisNom(dadesEntrada.getInteressatPaisNom());
		anotacio.setInteressatProvinciaCodi(dadesEntrada.getInteressatProvinciaCodi());
		anotacio.setInteressatProvinciaNom(dadesEntrada.getInteressatProvinciaNom());
		anotacio.setInteressatMunicipiCodi(dadesEntrada.getInteressatMunicipiCodi());
		anotacio.setInteressatMunicipiNom(dadesEntrada.getInteressatMunicipiNom());
		anotacio.setRepresentatNif(dadesEntrada.getRepresentatNif());
		anotacio.setRepresentatNomAmbCognoms(dadesEntrada.getRepresentatNomAmbCognoms());
		anotacio.setAssumpteIdiomaCodi(dadesEntrada.getAnotacioIdiomaCodi());
		anotacio.setAssumpteTipus(dadesEntrada.getAnotacioTipusAssumpte());
		anotacio.setAssumpteExtracte(dadesEntrada.getAnotacioAssumpte());
		List<RegistreAnnexDto> annexos = new ArrayList<RegistreAnnexDto>();
		for (String documentCodi: documentsEntrada) {
			DocumentInfo document = getDocument(documentCodi);
			RegistreAnnexDto annex = new RegistreAnnexDto();
			annex.setNom(document.getTitol());
			annex.setData(document.getDataDocument());
			annex.setIdiomaCodi("ca");
			annex.setArxiuNom(document.getArxiuNom());
			annex.setArxiuContingut(document.getArxiuContingut());
			annexos.add(annex);
		}
		anotacio.setAnnexos(annexos);
		try {
			Long expedientId = executionContext.getProcessInstance().getExpedient().getId();
			RegistreIdDto anotacioId = Jbpm3HeliumBridge.getInstanceService().registreAnotacioEntrada(anotacio,expedientId);
			RespostaRegistre resposta = new RespostaRegistre();
			resposta.setNumero(anotacioId.getNumero());
			resposta.setData(anotacioId.getData());
			return resposta;
		} catch (NoTrobatException ex) {
			throw new HeliumHandlerException("No s'ha pogut registrar l'anotació d'entrada", ex);
		}
	}
	
	@Override
	public RespostaRegistre registreSortida(
			DadesRegistreSortida dadesSortida,
			List<String> documentsSortida) {
		RegistreAnotacioDto anotacio = new RegistreAnotacioDto();
		anotacio.setOrganCodi(dadesSortida.getOrganCodi());
		anotacio.setOficinaCodi(dadesSortida.getOficinaCodi());
		anotacio.setEntitatCodi(dadesSortida.getInteressatEntitatCodi());
		anotacio.setUnitatAdministrativa(dadesSortida.getAnotacioUnitatAdministrativa());
		anotacio.setInteressatAutenticat(true);
		anotacio.setInteressatNif(dadesSortida.getInteressatNif());
		anotacio.setInteressatNomAmbCognoms(dadesSortida.getInteressatNomAmbCognoms());
		anotacio.setInteressatPaisCodi(dadesSortida.getInteressatPaisCodi());
		anotacio.setInteressatPaisNom(dadesSortida.getInteressatPaisNom());
		anotacio.setInteressatProvinciaCodi(dadesSortida.getInteressatProvinciaCodi());
		anotacio.setInteressatProvinciaNom(dadesSortida.getInteressatProvinciaNom());
		anotacio.setInteressatMunicipiCodi(dadesSortida.getInteressatMunicipiCodi());
		anotacio.setInteressatMunicipiNom(dadesSortida.getInteressatMunicipiNom());
		anotacio.setRepresentatNif(dadesSortida.getRepresentatNif());
		anotacio.setRepresentatNomAmbCognoms(dadesSortida.getRepresentatNomAmbCognoms());
		anotacio.setAssumpteIdiomaCodi(dadesSortida.getAnotacioIdiomaCodi());
		anotacio.setAssumpteTipus(dadesSortida.getAnotacioTipusAssumpte());
		anotacio.setAssumpteExtracte(dadesSortida.getAnotacioAssumpte());
		List<RegistreAnnexDto> annexos = new ArrayList<RegistreAnnexDto>();
		for (String documentCodi: documentsSortida) {
			DocumentInfo document = getDocument(documentCodi);
			RegistreAnnexDto annex = new RegistreAnnexDto();
			annex.setNom(document.getTitol());
			annex.setData(document.getDataDocument());
			annex.setIdiomaCodi("ca");
			annex.setArxiuNom(document.getArxiuNom());
			annex.setArxiuContingut(document.getArxiuContingut());
			annexos.add(annex);
		}
		anotacio.setAnnexos(annexos);
		try {
			Long expedientId = executionContext.getProcessInstance().getExpedient().getId();
			RegistreIdDto anotacioId = Jbpm3HeliumBridge.getInstanceService().registreAnotacioSortida(anotacio,expedientId);
			RespostaRegistre resposta = new RespostaRegistre();
			resposta.setNumero(anotacioId.getNumero());
			resposta.setData(anotacioId.getData());
			return resposta;
		} catch (NoTrobatException ex) {
			throw new HeliumHandlerException("No s'ha pogut registrar l'anotació de sortida", ex);
		}
	}
	
	@Override
	public RespostaRegistre registreNotificacio(
			DadesRegistreNotificacio dadesNotificacio,
			List<String> documentsNotificacio,
			boolean crearExpedient) {
		RegistreNotificacioDto notificacio = new RegistreNotificacioDto();
		notificacio.setExpedientIdentificador(dadesNotificacio.getExpedientIdentificador());
		notificacio.setExpedientClau(dadesNotificacio.getExpedientClau());
		notificacio.setExpedientUnitatAdministrativa(dadesNotificacio.getExpedientUnitatAdministrativa());
		notificacio.setOrganCodi(dadesNotificacio.getOrganCodi());
		notificacio.setOficinaCodi(dadesNotificacio.getOficinaCodi());
		notificacio.setEntitatCodi(dadesNotificacio.getInteressatEntitatCodi());
		notificacio.setInteressatAutenticat(dadesNotificacio.isInteressatAutenticat());
		notificacio.setInteressatNif(dadesNotificacio.getInteressatNif());
		notificacio.setInteressatNomAmbCognoms(dadesNotificacio.getInteressatNomAmbCognoms());
		notificacio.setInteressatPaisCodi(dadesNotificacio.getInteressatPaisCodi());
		notificacio.setInteressatPaisNom(dadesNotificacio.getInteressatPaisNom());
		notificacio.setInteressatProvinciaCodi(dadesNotificacio.getInteressatProvinciaCodi());
		notificacio.setInteressatProvinciaNom(dadesNotificacio.getInteressatProvinciaNom());
		notificacio.setInteressatMunicipiCodi(dadesNotificacio.getInteressatMunicipiCodi());
		notificacio.setInteressatMunicipiNom(dadesNotificacio.getInteressatMunicipiNom());
		if (dadesNotificacio.getRepresentatNif() != null || dadesNotificacio.getRepresentatNomAmbCognoms() != null) {
			notificacio.setRepresentatNif(dadesNotificacio.getRepresentatNif());
			notificacio.setRepresentatNomAmbCognoms(dadesNotificacio.getRepresentatNomAmbCognoms());
		}
		notificacio.setAssumpteIdiomaCodi(dadesNotificacio.getAnotacioIdiomaCodi());
		notificacio.setAssumpteTipus(dadesNotificacio.getAnotacioTipusAssumpte());
		notificacio.setAssumpteExtracte(dadesNotificacio.getAnotacioAssumpte());
		notificacio.setNotificacioJustificantRecepcio(dadesNotificacio.isNotificacioJustificantRecepcio());
		notificacio.setNotificacioAvisTitol(dadesNotificacio.getNotificacioAvisTitol());
		notificacio.setNotificacioAvisText(dadesNotificacio.getNotificacioAvisText());
		notificacio.setNotificacioAvisTextSms(dadesNotificacio.getNotificacioAvisTextSms());
		notificacio.setNotificacioOficiTitol(dadesNotificacio.getNotificacioOficiTitol());
		notificacio.setNotificacioOficiText(dadesNotificacio.getNotificacioOficiText());
		if (dadesNotificacio.getNotificacioSubsanacioTramitIdentificador() != null) {
			notificacio.setTramitSubsanacioIdentificador(
					dadesNotificacio.getNotificacioSubsanacioTramitIdentificador());
			notificacio.setTramitSubsanacioVersio(
					dadesNotificacio.getNotificacioSubsanacioTramitVersio());
			notificacio.setTramitSubsanacioDescripcio(
					dadesNotificacio.getNotificacioSubsanacioTramitDescripcio());
			if (dadesNotificacio.getNotificacioSubsanacioParametres() != null) {
				for (String key: dadesNotificacio.getNotificacioSubsanacioParametres().keySet()) {
					notificacio.afegirTramitSubsanacioParametre(
							key,
							dadesNotificacio.getNotificacioSubsanacioParametres().get(key));
				}
			}
		}
		List<RegistreAnnexDto> annexos = new ArrayList<RegistreAnnexDto>();
		for (String documentCodi: documentsNotificacio) {
			DocumentInfo document = getDocument(documentCodi);
			RegistreAnnexDto annex = new RegistreAnnexDto();
			annex.setNom(document.getTitol());
			annex.setData(document.getDataDocument());
			annex.setIdiomaCodi("ca");
			annex.setArxiuNom(document.getArxiuNom());
			annex.setArxiuContingut(document.getArxiuContingut());
			annexos.add(annex);
		}
		notificacio.setAnnexos(annexos);
		try {
			Long expedientId = executionContext.getProcessInstance().getExpedient().getId();
			RegistreIdDto anotacioId = Jbpm3HeliumBridge.getInstanceService().notificacioCrear(notificacio,expedientId,crearExpedient);
			RespostaRegistre resposta = new RespostaRegistre();
			resposta.setNumero(anotacioId.getNumero());
			resposta.setData(anotacioId.getData());
			ReferenciaRDSJustificante referenciaRDSJustificante = new ReferenciaRDSJustificante();
			referenciaRDSJustificante.setClave(anotacioId.getReferenciaRDSJustificante().getClave());
			referenciaRDSJustificante.setCodigo(anotacioId.getReferenciaRDSJustificante().getCodigo());
			resposta.setReferenciaRDSJustificante(referenciaRDSJustificante);
			return resposta;
		} catch (NoTrobatException ex) {
			throw new HeliumHandlerException("S'ha produït un error en enviar la notificació: " + ex.getLocalizedMessage(), ex);
		}
	}
	
	@Override
	public JustificantRecepcioInfo obtenirJustificantRecepcio(String registreNumero) {
		try {
			return ConversioTipusHelper.toJustificantRecepcioInfo(
					Jbpm3HeliumBridge.getInstanceService().notificacioElectronicaJustificantDetall(registreNumero));
		} catch (Exception ex) {
			throw new HeliumHandlerException("No s'ha pogut obtenir el justificant de recepció", ex);
		}
	}
	@Override
	public void expedientRelacionar(Long expedientId) {
		ExpedientInfo expedient = getExpedient();
		Jbpm3HeliumBridge.getInstanceService().expedientRelacionar(
				expedient.getId(),
				expedientId);
	}
	
	@Override
	public void expedientAturar(String motiu) {
		try {
			Jbpm3HeliumBridge.getInstanceService().expedientAturar(getProcessInstanceId(), motiu);
		} catch (Exception ex) {
			throw new HeliumHandlerException("No s'ha pogut aturar l'expedient", ex);
		}
	}
	
	@Override
	public void expedientReprendre() {
		try {
			//Jbpm3HeliumBridge.getInstanceService().reprendreExpedient(getProcessInstanceId());
			Jbpm3HeliumBridge.getInstanceService().expedientReprendre(getProcessInstanceId());
		} catch (Exception ex) {
			throw new HeliumHandlerException("No s'ha pogut reprendre l'expedient", ex);
		}
	}
	
	@Override
	public boolean expedientReindexar() {
		boolean success;
		try {
			success = Jbpm3HeliumBridge.getInstanceService().expedientReindexar(getProcessInstanceId());
		} catch (Exception ex) {
			throw new HeliumHandlerException("No s'ha pogut reindexar l'expedient", ex);
		}
		return success;
	}
	
	@Override
	public void expedientTokenRedirigir(
			Long tokenId,
			String nodeName,
			boolean cancelarTasques) {
		Jbpm3HeliumBridge.getInstanceService().tokenRedirigir(
				tokenId,
				nodeName,
				cancelarTasques);
	}
	
	@Override
	public void retrocedirGuardarParametres(List<String> parametres) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < parametres.size(); i++) {
			sb.append(parametres.get(i));
			if (i < parametres.size() - 1)
				sb.append(PARAMS_RETROCEDIR_SEPARADOR);
		}
		executionContext.setVariable(
				PARAMS_RETROCEDIR_VARIABLE_PREFIX + executionContext.getAction().getId(),
				sb.toString());
	}
	
	private ExpedientDto getExpedientActual() {
		ExpedientDto expedient = Jbpm3HeliumBridge.getInstanceService().getExpedientIniciant();
		if (expedient == null) {
			expedient = Jbpm3HeliumBridge.getInstanceService().getExpedientArrelAmbProcessInstanceId(getProcessInstanceId());
		}
		return expedient;
	}
	private String getProcessInstanceId() {
		return new Long(executionContext.getProcessInstance().getId()).toString();
	}
	private String getTaskInstanceId() {
		return new Long(executionContext.getTaskInstance().getId()).toString();
	}
	private Boolean isTaskInstanceExecution() {
		return executionContext.getTaskInstance() != null;
	}
	private Object getVarValue(Object valor) {
		if (valor instanceof DominiCodiDescripcio) {
			return ((DominiCodiDescripcio)valor).getCodi();
		} else {
			return valor;
		}
	}

	@Override
	public RespostaEnviar altaNotificacio(DadesNotificacio dadesNotificacio, Long expedientId) throws JbpmException {

		DadesNotificacioDto notificacio = ConversioTipusHelper.toDadesNotificacioDto(expedientId, dadesNotificacio);
		
		RespostaNotificacio respostaNotificacio = Jbpm3HeliumBridge.getInstanceService().altaNotificacio(notificacio);
		
		return ConversioTipusHelper.toRespostaEnviar(respostaNotificacio);		
	}
	
	public void interessatCrear(
			Interessat interessat) {
		
		InteressatDto interessatDto = ConversioTipusHelper.toInteressatDto(interessat);
		Jbpm3HeliumBridge.getInstanceService().interessatCrear(interessatDto);	
	}
	
	
	public void interessatModificar(
			Interessat interessat) {
		
		InteressatDto interessatDto = ConversioTipusHelper.toInteressatDto(interessat);
		Jbpm3HeliumBridge.getInstanceService().interessatModificar(interessatDto);
		
	}
	
	
	public void interessatEliminar(
			String codi,
			Long expedientId) {
		
		Jbpm3HeliumBridge.getInstanceService().interessatEliminar(codi, expedientId);
		
	}
	
	

	
	
	
}