/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.integracio.plugins.registre.RegistreDocument;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreFont;
import net.conselldemallorca.helium.integracio.plugins.registre.SeientRegistral;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreDocument.IdiomaRegistre;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesTramit;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirDadesTramitRequest;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.AutenticacioTipus;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesRegistre;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentDisseny;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentPresencial;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentTelematic;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentTramit;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ExpedientInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.FilaResultat;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ParellaCodiValor;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.Signatura;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.Tramit;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ExpedientInfo.IniciadorTipus;
import net.conselldemallorca.helium.jbpm3.integracio.Termini;
import net.conselldemallorca.helium.jbpm3.integracio.ValidationException;
import net.conselldemallorca.helium.model.dao.DaoProxy;
import net.conselldemallorca.helium.model.dao.DominiDao;
import net.conselldemallorca.helium.model.dao.EntornDao;
import net.conselldemallorca.helium.model.dao.MailDao;
import net.conselldemallorca.helium.model.dao.PluginRegistreDao;
import net.conselldemallorca.helium.model.dao.PluginTramitacioDao;
import net.conselldemallorca.helium.model.dto.ArxiuDto;
import net.conselldemallorca.helium.model.dto.DocumentDto;
import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.model.hibernate.Camp;
import net.conselldemallorca.helium.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.model.hibernate.Document;
import net.conselldemallorca.helium.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.model.hibernate.Domini;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.Estat;
import net.conselldemallorca.helium.model.hibernate.Expedient;
import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.model.service.DocumentService;
import net.conselldemallorca.helium.model.service.ExpedientService;
import net.conselldemallorca.helium.model.service.PermissionService;
import net.conselldemallorca.helium.model.service.ServiceProxy;
import net.conselldemallorca.helium.model.service.TascaService;
import net.conselldemallorca.helium.security.permission.ExtendedPermission;
import net.conselldemallorca.helium.util.ExpedientIniciant;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.jbpm.JbpmException;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.springframework.security.acls.Permission;


/**
 * Handler que pot servir com a base per als handlers que s'hagin
 * d'implementar a dins les definicions de procés.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public abstract class BasicActionHandler implements ActionHandler {

	public abstract void execute(ExecutionContext executionContext) throws Exception;



	/**
	 * Llança un error de validació
	 * 
	 * @param error descripció de l'error
	 */
	public void errorValidacio(String error) {
		throw new ValidationException(error);
	}

	/**
	 * Realitza una consulta a un domini
	 * 
	 * @param executionContext
	 * @param codiDomini
	 * @param parametres
	 * @return
	 */
	public List<FilaResultat> consultaDomini(
			ExecutionContext executionContext,
			String codiDomini,
			String id,
			Map<String, Object> parametres) {
		ExpedientInfo expedient = getExpedient(executionContext);
		if (expedient == null)
			throw new JbpmException("No s'ha trobat cap expedient que correspongui amb aquesta instància de procés (" + executionContext.getProcessInstance().getId() + ")");
		Entorn entorn = getEntornDao().findAmbCodi(expedient.getEntornCodi());
		Domini domini = getDominiDao().findAmbEntornICodi(
				entorn.getId(),
				codiDomini);
		if (domini == null)
			throw new JbpmException("No s'ha trobat el domini amb el codi '" + codiDomini + "'");
		try {
			List<net.conselldemallorca.helium.integracio.domini.FilaResultat> resultatConsulta = getDominiDao().consultar(domini.getId(), id, parametres);
			List<FilaResultat> resposta = new ArrayList<FilaResultat>();
			for (net.conselldemallorca.helium.integracio.domini.FilaResultat fila: resultatConsulta) {
				FilaResultat fres = new FilaResultat();
				for (net.conselldemallorca.helium.integracio.domini.ParellaCodiValor parella: fila.getColumnes()) {
					fres.addColumna(new ParellaCodiValor(
							parella.getCodi(),
							parella.getValor()));
				}
				resposta.add(fres);
			}
			return resposta;
		} catch (Exception ex) {
			throw new JbpmException("Error en la consulta del domini amb el codi '" + codiDomini + "'", ex);
		}
	}

	public List<ExpedientInfo> consultaExpedients(
			ExecutionContext executionContext,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			String expedientTipusCodi,
			String estatCodi,
			boolean iniciat,
			boolean finalitzat) {
		// Obtencio de dades per a fer la consulta
		ExpedientInfo expedient = getExpedient(executionContext);
		Entorn entorn = getEntornDao().findAmbCodi(expedient.getEntornCodi());
		ExpedientTipus expedientTipus = getDissenyService().findExpedientTipusAmbEntornICodi(
				entorn.getId(),
				expedientTipusCodi);
		Long estatId = null;
		if (estatCodi != null) {
			for (Estat estat: expedientTipus.getEstats()) {
				if (estat.getCodi().equals(estatCodi)) {
					estatId = estat.getId();
					break;
				}
			}
		}
		// Consulta d'expedients
		List<ExpedientDto> expedients = getExpedientService().findAmbEntornConsultaGeneral(
				entorn.getId(),
				titol,
				numero,
				dataInici1,
				dataInici2,
				expedientTipus.getId(),
				estatId,
				iniciat,
				finalitzat);
		// Filtre expedients permesos
		List<ExpedientTipus> tipus = getDissenyService().findExpedientTipusAmbEntorn(entorn.getId());
		getPermissionService().filterAllowed(
				tipus,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.READ});
		Iterator<ExpedientDto> it = expedients.iterator();
		while (it.hasNext()) {
			ExpedientDto exp = it.next();
			if (!tipus.contains(exp.getTipus()))
				it.remove();
		}
		// Construcció de la resposta
		List<ExpedientInfo> resposta = new ArrayList<ExpedientInfo>();
		for (ExpedientDto dto: expedients)
			resposta.add(toExpedientInfo(dto));
		return resposta;
	}

	/**
	 * Obté el document al que fa referència una variable
	 * 
	 * @param executionContext
	 * @param varDocument
	 * @return
	 */
	public DocumentInfo getDocument(
			ExecutionContext executionContext,
			String varDocument) {
		String varCodi = TascaService.PREFIX_DOCUMENT + varDocument;
		Object valor = executionContext.getVariable(varCodi);
		if (valor == null)
			return null;
		if (valor instanceof Long) {
			Long id = (Long)valor;
			DocumentDto document = getExpedientService().getDocument(id, true, false);
			if (document == null)
				return null;
			DocumentInfo resposta = new DocumentInfo();
			resposta.setId(id);
			if (document.isAdjunt()) {
				resposta.setTitol(document.getAdjuntTitol());
			} else {
				resposta.setTitol(document.getDocumentNom());
			}
			resposta.setDataCreacio(document.getDataCreacio());
			resposta.setDataDocument(document.getDataDocument());
			resposta.setSignat(document.isSignat());
			if (document.isSignat()) {
				resposta.setArxiuNom(document.getSignatNom());
				resposta.setArxiuContingut(document.getSignatContingut());
			} else {
				resposta.setArxiuNom(document.getArxiuNom());
				resposta.setArxiuContingut(document.getArxiuContingut());
			}
			return resposta;
		} else {
			throw new JbpmException("La variable \"" + varCodi + "\" no es del tipus correcte");
		}
	}

	/**
	 * Obté la informació de disseny del document
	 * 
	 * @param executionContext
	 * @param codiDocument
	 * @return
	 */
	public DocumentDisseny getDocumentDisseny(
			ExecutionContext executionContext,
			String codiDocument) {
		InstanciaProcesDto instanciaProces = getExpedientService().getInstanciaProcesById(
				new Long(executionContext.getProcessInstance().getId()).toString(),
				false);
		Document document = getDissenyService().findDocumentAmbDefinicioProcesICodi(
				instanciaProces.getDefinicioProces().getId(),
				codiDocument);
		if (document == null)
			return null;
		DocumentDisseny resposta = new DocumentDisseny();
		resposta.setId(document.getId());
		resposta.setCodi(document.getCodi());
		resposta.setNom(document.getNom());
		resposta.setDescripcio(document.getDescripcio());
		resposta.setPlantilla(document.isPlantilla());
		resposta.setContentType(document.getContentType());
		resposta.setCustodiaCodi(document.getCustodiaCodi());
		resposta.setTipusDocPortasignatures(document.getTipusDocPortasignatures());
		return resposta;
	}

	/**
	 * Enllaça un document d'una instancia de procés pare. Si el document no existeix no
	 * el copia i no produeix cap error.
	 * 
	 * @param executionContext
	 * @param codiDocument
	 */
	public void crearReferenciaDocumentInstanciaProcesPare(
			ExecutionContext executionContext,
			String varDocument) {
		Token tokenPare = executionContext.getProcessInstance().getRootToken().getParent();
		if (tokenPare != null) {
			String varCodi = TascaService.PREFIX_DOCUMENT + varDocument;
			Object valor = tokenPare.getProcessInstance().getContextInstance().getVariable(varCodi);
			if (valor != null) {
				if (valor instanceof Long) {
					long lv = ((Long)valor).longValue();
					executionContext.setVariable(varCodi, new Long(-lv));
				} else {
					throw new JbpmException("El contingut del document '" + varDocument + "' no és del tipus correcte");
				}
			}
		} else {
			throw new JbpmException("Aquesta instància de procés no té pare");
		}
	}

	/**
	 * Retorna l'expedient associat al procés actual
	 * 
	 * @param executionContext
	 * @return
	 */
	public ExpedientInfo getExpedient(ExecutionContext executionContext) {
		Expedient ex = ExpedientIniciant.getExpedient();
		if (ex != null) {
			ExpedientInfo resposta = new ExpedientInfo();
			resposta.setTitol(ex.getTitol());
			resposta.setNumero(ex.getNumero());
			resposta.setNumeroDefault(ex.getNumeroDefault());
			resposta.setDataInici(ex.getDataInici());
			resposta.setDataFi(ex.getDataFi());
			resposta.setComentari(ex.getComentari());
			resposta.setInfoAturat(ex.getInfoAturat());
			if (ex.getIniciadorTipus().equals(net.conselldemallorca.helium.model.hibernate.Expedient.IniciadorTipus.INTERN))
				resposta.setIniciadorTipus(IniciadorTipus.INTERN);
			else if (ex.getIniciadorTipus().equals(net.conselldemallorca.helium.model.hibernate.Expedient.IniciadorTipus.SISTRA))
				resposta.setIniciadorTipus(IniciadorTipus.SISTRA);
			resposta.setIniciadorCodi(ex.getIniciadorCodi());
			resposta.setResponsableCodi(ex.getResponsableCodi());
			resposta.setRegistreNumero(ex.getRegistreNumero());
			resposta.setRegistreData(ex.getRegistreData());
			resposta.setAvisosHabilitats(ex.isAvisosHabilitats());
			resposta.setAvisosEmail(ex.getAvisosEmail());
			resposta.setAvisosMobil(ex.getAvisosMobil());
			resposta.setNotificacioTelematicaHabilitada(ex.isNotificacioTelematicaHabilitada());
			resposta.setGeoPosX(ex.getGeoPosX());
			resposta.setGeoPosY(ex.getGeoPosY());
			resposta.setGeoReferencia(ex.getGeoReferencia());
			resposta.setExpedientTipusCodi(ex.getTipus().getCodi());
			resposta.setEntornCodi(ex.getEntorn().getCodi());
			if (ex.getEstat() != null)
				resposta.setEstatCodi(ex.getEstat().getCodi());
			return resposta;
		} else {
			ExpedientDto expedient = getExpedientService().findExpedientAmbProcessInstanceId(
					getProcessInstanceId(executionContext));
			return toExpedientInfo(expedient);
		}
	}

	/**
	 * Envia un email amb possibilitat d'adjuntar documents de l'expedient.
	 * 
	 * @param recipients
	 * @param ccRecipients
	 * @param bccRecipients
	 * @param subject
	 * @param text
	 * @param attachments
	 */
	public void enviarEmail(
			List<String> recipients,
			List<String> ccRecipients,
			List<String> bccRecipients,
			String subject,
			String text,
			List<Long> attachments) {
		try {
			List<ArxiuDto> documents = null;
			if (attachments != null) {
				documents = new ArrayList<ArxiuDto>();
				for (Long id: attachments) {
					documents.add(getDocumentService().arxiuDocumentPerMostrar(id));
				}
			}
			getMailDao().send(
					GlobalProperties.getInstance().getProperty("app.correu.remitent"),
					recipients,
					ccRecipients,
					bccRecipients,
					subject,
					text,
					documents);
		} catch (Exception ex) {
			throw new JbpmException("No s'ha pogut enviar el missatge", ex);
		}
	}

	/**
	 * Registra un document d'entrada
	 * 
	 * @param dadesRegistre
	 * @param executionContext
	 * @return
	 */
	public String[] registreEntrada(
			DadesRegistre dadesRegistre,
			ExecutionContext executionContext) {
		try {
			SeientRegistral seient = getSeientRegistral(
					dadesRegistre,
					executionContext);
			String[] numeroAny = getRegistreDao().registrarEntrada(seient);
			guardarInfoRegistre(
					executionContext,
					dadesRegistre.getVarDocument(),
					seient.getData(),
					seient.getHora(),
					seient.getOficina(),
					numeroAny[0],
					numeroAny[1],
					true);
			return numeroAny;
		} catch (Exception ex) {
			throw new JbpmException("No s'ha pogut registrar el document", ex);
		}
	}
	/**
	 * Consulta un document d'entrada
	 * 
	 * @param varDocument
	 * @param executionContext
	 * @return
	 */
	public DadesRegistre registreConsultarEntrada(
			String varDocument,
			ExecutionContext executionContext) {
		DocumentStore docStore = getDocumentRegistrat(varDocument, executionContext);
		if (docStore == null)
			throw new JbpmException("No s'ha trobat el document '" + varDocument + "'");
		if (!docStore.isRegistrat())
			throw new JbpmException("El document '" + varDocument + "' no està registrat");
		try {
			return toDadesRegistre(getRegistreDao().consultarEntrada(
					docStore.getRegistreOficinaCodi(),
					docStore.getRegistreNumero(),
					docStore.getRegistreAny()));
		} catch (Exception ex) {
			throw new JbpmException("No s'ha pogut consultar el registre", ex);
		}
	}
	/**
	 * Registra un document de sortida
	 * 
	 * @param dadesRegistre
	 * @param executionContext
	 * @return
	 */
	public String[] registreSortida(
			DadesRegistre dadesRegistre,
			ExecutionContext executionContext) {
		try {
			SeientRegistral seient = getSeientRegistral(
					dadesRegistre,
					executionContext);
			String[] numeroAny = getRegistreDao().registrarSortida(seient);
			guardarInfoRegistre(
					executionContext,
					dadesRegistre.getVarDocument(),
					seient.getData(),
					seient.getHora(),
					seient.getOficina(),
					numeroAny[0],
					numeroAny[1],
					false);
			return numeroAny;
		} catch (Exception ex) {
			throw new JbpmException("No s'ha pogut registrar el document", ex);
		}
	}
	/**
	 * Consulta un document de sortida
	 * 
	 * @param varDocument
	 * @param executionContext
	 * @return
	 */
	public DadesRegistre registreConsultarSortida(
			String varDocument,
			ExecutionContext executionContext) {
		DocumentStore docStore = getDocumentRegistrat(varDocument, executionContext);
		if (docStore == null)
			throw new JbpmException("No s'ha trobat el document '" + varDocument + "'");
		if (!docStore.isRegistrat())
			throw new JbpmException("El document '" + varDocument + "' no està registrat");
		try {
			return toDadesRegistre(getRegistreDao().consultarSortida(
					docStore.getRegistreOficinaCodi(),
					docStore.getRegistreNumero(),
					docStore.getRegistreAny()));
		} catch (Exception ex) {
			throw new JbpmException("No s'ha pogut consultar el registre", ex);
		}
	}

	/**
	 * Retorna el text que es correspon amb el valor d'una variable
	 * provinent d'una consulta de domini o d'una enumeració.
	 * 
	 * @param executionContext
	 * @param varCodi
	 * @return
	 */
	public String getTextPerVariableAmbDomini(
			ExecutionContext executionContext,
			String varCodi) {
		long processInstanceId = executionContext.getProcessInstance().getId();
		long processDefinitionId = executionContext.getProcessInstance().getProcessDefinition().getId();
		DefinicioProces definicioProces = DaoProxy.getInstance().getDefinicioProcesDao().findAmbJbpmId(new Long(processDefinitionId).toString());
		if (definicioProces != null) {
			Camp camp = DaoProxy.getInstance().getCampDao().findAmbDefinicioProcesICodi(definicioProces.getId(), varCodi);
			if (camp != null) {
				return ServiceProxy.getInstance().getDtoConverter().getTextConsultaDomini(
						null,
						new Long(processInstanceId).toString(),
						camp,
						executionContext.getVariable(varCodi));
			} else {
				throw new JbpmException("No s'ha trobat el camp '" + varCodi + "' per a la definició de procés " + definicioProces.getId());
			}
		} else {
			throw new JbpmException("No s'ha trobat la definició de procés per a la instància de procés " + processInstanceId);
		}
	
	}

	/**
	 * Guarda un termini a dins una variable
	 * 
	 * @param executionContext
	 * @param anys
	 * @param mesos
	 * @param dies
	 */
	public void terminiGuardar(
			ExecutionContext executionContext,
			String varName,
			int anys,
			int mesos,
			int dies) {
		Termini termini = new Termini();
		termini.setAnys(anys);
		termini.setMesos(mesos);
		termini.setDies(dies);
		executionContext.setVariable(varName, termini);
	}

	/**
	 * Consulta les dades d'un tràmit
	 * 
	 * @param executionContext
	 * @param anys
	 * @param mesos
	 * @param dies
	 */
	public Tramit consultaTramit(
			String numero,
			String clau) {
		ObtenirDadesTramitRequest request = new ObtenirDadesTramitRequest();
		request.setNumero(numero);
		request.setClau(clau);
		return toTramit(
				getPluginTramitacioDao().obtenirDadesTramit(request));
	}



	private String getProcessInstanceId(ExecutionContext executionContext) {
		return new Long(executionContext.getProcessInstance().getId()).toString();
	}
	private EntornDao getEntornDao() {
		return DaoProxy.getInstance().getEntornDao();
	}
	private DominiDao getDominiDao() {
		return DaoProxy.getInstance().getDominiDao();
	}
	private MailDao getMailDao() {
		return DaoProxy.getInstance().getMailDao();
	}
	private PluginRegistreDao getRegistreDao() {
		return DaoProxy.getInstance().getPluginRegistreDao();
	}
	private PluginTramitacioDao getPluginTramitacioDao() {
		return DaoProxy.getInstance().getPluginTramitacioDao();
	}
	private ExpedientService getExpedientService() {
		return ServiceProxy.getInstance().getExpedientService();
	}
	private DocumentService getDocumentService() {
		return ServiceProxy.getInstance().getDocumentService();
	}
	private DissenyService getDissenyService() {
		return ServiceProxy.getInstance().getDissenyService();
	}
	private PermissionService getPermissionService() {
		return ServiceProxy.getInstance().getPermissionService();
	}

	private SeientRegistral getSeientRegistral(
			DadesRegistre dades,
			ExecutionContext executionContext) {
		SeientRegistral resposta = new SeientRegistral();
		Date ara = new Date();
		if (dades.getData() == null || "".equals(dades.getData())) {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			resposta.setData(df.format(ara));
		} else {
			resposta.setData(dades.getData());
		}
		if (dades.getHora() == null || "".equals(dades.getHora())) {
			DateFormat df = new SimpleDateFormat("HH:mm");
			resposta.setHora(df.format(ara));
		} else {
			resposta.setHora(dades.getHora());
		}
		resposta.setOficina(dades.getOficina());
		resposta.setOficinaFisica(dades.getOficinaFisica());
		RegistreFont remitent = new RegistreFont();
		remitent.setCodiEntitat(dades.getRemitentCodiEntitat());
		remitent.setNomEntitat(dades.getRemitentNomEntitat());
		remitent.setCodiGeografic(dades.getRemitentCodiGeografic());
		remitent.setNomGeografic(dades.getRemitentNomGeografic());
		remitent.setNumeroRegistre(dades.getRemitentRegistreNumero());
		remitent.setAnyRegistre(dades.getRemitentRegistreAny());
		resposta.setRemitent(remitent);
		RegistreFont destinatari = new RegistreFont();
		destinatari.setCodiEntitat(dades.getDestinatariCodiEntitat());
		destinatari.setNomEntitat(dades.getDestinatariNomEntitat());
		destinatari.setCodiGeografic(dades.getDestinatariCodiGeografic());
		destinatari.setNomGeografic(dades.getDestinatariNomGeografic());
		destinatari.setNumeroRegistre(dades.getDestinatariRegistreNumero());
		destinatari.setAnyRegistre(dades.getDestinatariRegistreAny());
		resposta.setDestinatari(destinatari);
		resposta.setDocument(getRegistreDocument(dades, executionContext));
		return resposta;
	}
	private RegistreDocument getRegistreDocument(
			DadesRegistre dades,
			ExecutionContext executionContext) {
		RegistreDocument resposta = new RegistreDocument();
		resposta.setTipus(dades.getDocumentTipus());
		resposta.setIdiomaDocument(getIdiomaRegistre(dades.getDocumentIdiomaDocument()));
		resposta.setIdiomaExtracte(getIdiomaRegistre(dades.getDocumentIdiomaExtracte()));
		if (dades.getVarDocument() == null || dades.getVarDocument().length() == 0) {
			DocumentStore docStore = getDocumentRegistrat(dades.getVarDocument(), executionContext);
			if (docStore == null)
				throw new JbpmException("No s'ha trobat el document '" + dades.getVarDocument() + "'");
			InstanciaProcesDto instanciaProces = getExpedientService().getInstanciaProcesById(
					new Long(executionContext.getProcessInstance().getId()).toString(),
					false);
			if (docStore.isAdjunt()) {
				resposta.setExtracte(instanciaProces.getExpedient().getIdentificador() + ": " + docStore.getAdjuntTitol());
			} else {
				Document document = getDissenyService().findDocumentAmbDefinicioProcesICodi(
						instanciaProces.getDefinicioProces().getId(),
						docStore.getCodiDocument());
				resposta.setExtracte(instanciaProces.getExpedient().getIdentificador() + ": " + document.getNom());
			}
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			resposta.setData(df.format(docStore.getDataDocument()));
		} else {
			throw new JbpmException("No s'ha especificat el document per registrar");
		}
		return resposta;
	}
	private DocumentStore getDocumentRegistrat(String varDocument, ExecutionContext executionContext) {
		String varCodi = TascaService.PREFIX_DOCUMENT + varDocument;
		Object valor = executionContext.getVariable(varCodi);
		if (valor instanceof Long) {
			return DaoProxy.getInstance().getDocumentStoreDao().getById(
					(Long)valor,
					false);
		}
		return null;
	}
	private IdiomaRegistre getIdiomaRegistre(String idioma) {
		if ("es".equalsIgnoreCase(idioma))
			return IdiomaRegistre.ES;
		return IdiomaRegistre.CA;
	}
	private DadesRegistre toDadesRegistre(SeientRegistral seient) {
		DadesRegistre resposta = new DadesRegistre();
		resposta.setData(seient.getData());
		resposta.setHora(seient.getHora());
		resposta.setOficina(seient.getOficina());
		resposta.setOficinaFisica(seient.getOficinaFisica());
		resposta.setRemitentCodiEntitat(seient.getRemitent().getCodiEntitat());
		resposta.setRemitentNomEntitat(seient.getRemitent().getNomEntitat());
		resposta.setRemitentCodiGeografic(seient.getRemitent().getCodiGeografic());
		resposta.setRemitentNomGeografic(seient.getRemitent().getNomGeografic());
		resposta.setRemitentRegistreNumero(seient.getRemitent().getNumeroRegistre());
		resposta.setRemitentRegistreAny(seient.getRemitent().getAnyRegistre());
		resposta.setDestinatariCodiEntitat(seient.getDestinatari().getCodiEntitat());
		resposta.setDestinatariNomEntitat(seient.getDestinatari().getNomEntitat());
		resposta.setDestinatariCodiGeografic(seient.getDestinatari().getCodiGeografic());
		resposta.setDestinatariNomGeografic(seient.getDestinatari().getNomGeografic());
		resposta.setDestinatariRegistreNumero(seient.getDestinatari().getNumeroRegistre());
		resposta.setDestinatariRegistreAny(seient.getDestinatari().getAnyRegistre());
		resposta.setDocumentTipus(seient.getDocument().getTipus());
		resposta.setDocumentIdiomaDocument(seient.getDocument().getIdiomaDocument().toString());
		resposta.setDocumentIdiomaExtracte(seient.getDocument().getIdiomaDocument().toString());
		resposta.setDocumentData(seient.getDocument().getData());
		resposta.setDocumentExtracte(seient.getDocument().getExtracte());
		return resposta;
	}
	private void guardarInfoRegistre(
			ExecutionContext executionContext,
			String varDocument,
			String data,
			String hora,
			String oficina,
			String numero,
			String any,
			boolean entrada) {
		Long documentId = getDocumentId(varDocument, executionContext);
		if (documentId == null)
			throw new JbpmException("No s'ha trobat el document '" + varDocument + "'");
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date dataRegistre = null;
		try {
			dataRegistre = df.parse(data + " " + hora);
		} catch (Exception ex) {
			dataRegistre = new Date();
		}
		if (entrada)
			DaoProxy.getInstance().getDocumentStoreDao().updateRegistreEntrada(
					documentId,
					dataRegistre,
					numero,
					any,
					oficina,
					getRegistreDao().getNomOficina(oficina));
		else
			DaoProxy.getInstance().getDocumentStoreDao().updateRegistreSortida(
					documentId,
					dataRegistre,
					numero,
					any,
					oficina,
					getRegistreDao().getNomOficina(oficina));
	}
	private Long getDocumentId(String varDocument, ExecutionContext executionContext) {
		String varCodi = TascaService.PREFIX_DOCUMENT + varDocument;
		Object valor = executionContext.getVariable(varCodi);
		if (valor instanceof Long)
			return (Long)valor;
		return null;
	}
	private ExpedientInfo toExpedientInfo(ExpedientDto expedient) {
		if (expedient != null) {
			ExpedientInfo resposta = new ExpedientInfo();
			resposta.setTitol(expedient.getTitol());
			resposta.setNumero(expedient.getNumero());
			resposta.setNumeroDefault(expedient.getNumeroDefault());
			resposta.setDataInici(expedient.getDataInici());
			resposta.setDataFi(expedient.getDataFi());
			resposta.setComentari(expedient.getComentari());
			resposta.setInfoAturat(expedient.getInfoAturat());
			if (expedient.getIniciadorTipus().equals(net.conselldemallorca.helium.model.hibernate.Expedient.IniciadorTipus.INTERN))
				resposta.setIniciadorTipus(IniciadorTipus.INTERN);
			else if (expedient.getIniciadorTipus().equals(net.conselldemallorca.helium.model.hibernate.Expedient.IniciadorTipus.SISTRA))
				resposta.setIniciadorTipus(IniciadorTipus.SISTRA);
			resposta.setIniciadorCodi(expedient.getIniciadorCodi());
			resposta.setResponsableCodi(expedient.getResponsableCodi());
			resposta.setRegistreNumero(expedient.getRegistreNumero());
			resposta.setRegistreData(expedient.getRegistreData());
			resposta.setGeoPosX(expedient.getGeoPosX());
			resposta.setGeoPosY(expedient.getGeoPosY());
			resposta.setGeoReferencia(expedient.getGeoReferencia());
			resposta.setExpedientTipusCodi(expedient.getTipus().getCodi());
			resposta.setEntornCodi(expedient.getEntorn().getCodi());
			if (expedient.getEstat() != null)
				resposta.setEstatCodi(expedient.getEstat().getCodi());
			resposta.setProcessInstanceId(new Long(expedient.getProcessInstanceId()).longValue());
			return resposta;
		}
		return null;
	}

	private Tramit toTramit(DadesTramit dadesTramit) {
		if (dadesTramit == null)
			return null;
		Tramit tramit = new Tramit();
		tramit.setNumero(dadesTramit.getNumero());
		tramit.setClauAcces(dadesTramit.getClauAcces());
		tramit.setIdentificador(dadesTramit.getIdentificador());
		tramit.setUnitatAdministrativa(dadesTramit.getUnitatAdministrativa());
		tramit.setVersio(dadesTramit.getVersio());
		tramit.setData(dadesTramit.getData());
		tramit.setIdioma(dadesTramit.getIdioma());
		tramit.setRegistreNumero(dadesTramit.getRegistreNumero());
		tramit.setRegistreData(dadesTramit.getRegistreData());
		tramit.setPreregistreTipusConfirmacio(dadesTramit.getPreregistreTipusConfirmacio());
		tramit.setPreregistreNumero(dadesTramit.getPreregistreNumero());
		tramit.setPreregistreData(dadesTramit.getPreregistreData());
		if (dadesTramit.getAutenticacioTipus() != null) {
			if (net.conselldemallorca.helium.integracio.plugins.tramitacio.AutenticacioTipus.ANONIMA.equals(dadesTramit.getAutenticacioTipus()))
				tramit.setAutenticacioTipus(AutenticacioTipus.ANONIMA);
			if (net.conselldemallorca.helium.integracio.plugins.tramitacio.AutenticacioTipus.USUARI.equals(dadesTramit.getAutenticacioTipus()))
				tramit.setAutenticacioTipus(AutenticacioTipus.USUARI);
			if (net.conselldemallorca.helium.integracio.plugins.tramitacio.AutenticacioTipus.CERTIFICAT.equals(dadesTramit.getAutenticacioTipus()))
				tramit.setAutenticacioTipus(AutenticacioTipus.CERTIFICAT);
		}
		tramit.setTramitadorNif(dadesTramit.getTramitadorNif());
		tramit.setTramitadorNom(dadesTramit.getTramitadorNom());
		tramit.setInteressatNif(dadesTramit.getInteressatNif());
		tramit.setInteressatNom(dadesTramit.getInteressatNom());
		tramit.setRepresentantNif(dadesTramit.getRepresentantNif());
		tramit.setRepresentantNom(dadesTramit.getRepresentantNom());
		tramit.setSignat(dadesTramit.isSignat());
		tramit.setAvisosHabilitats(dadesTramit.isAvisosHabilitats());
		tramit.setAvisosEmail(dadesTramit.getAvisosEmail());
		tramit.setAvisosSms(dadesTramit.getAvisosSms());
		tramit.setNotificacioTelematicaHabilitada(dadesTramit.isNotificacioTelematicaHabilitada());
		if (dadesTramit.getDocuments() != null) {
			List<DocumentTramit> documents = new ArrayList<DocumentTramit>();
			for (net.conselldemallorca.helium.integracio.plugins.tramitacio.DocumentTramit documento: dadesTramit.getDocuments()) {
				DocumentTramit document = new DocumentTramit();
				document.setNom(documento.getNom());
				document.setIdentificador(documento.getIdentificador());
				document.setInstanciaNumero(documento.getInstanciaNumero());
				if (documento.getDocumentPresencial() != null) {
					DocumentPresencial documentPresencial = new DocumentPresencial();
					documentPresencial.setDocumentCompulsar(
							documento.getDocumentPresencial().getDocumentCompulsar());
					documentPresencial.setSignatura(
							documento.getDocumentPresencial().getSignatura());
					documentPresencial.setFotocopia(
							documento.getDocumentPresencial().getFotocopia());
					documentPresencial.setTipus(
							documento.getDocumentPresencial().getTipus());
					document.setDocumentPresencial(documentPresencial);
				}
				if (documento.getDocumentTelematic() != null) {
					DocumentTelematic documentTelematic = new DocumentTelematic();
					documentTelematic.setArxiuNom(
							documento.getDocumentTelematic().getArxiuNom());
					documentTelematic.setArxiuExtensio(
							documento.getDocumentTelematic().getArxiuExtensio());
					documentTelematic.setArxiuContingut(
							documento.getDocumentTelematic().getArxiuContingut());
					documentTelematic.setReferenciaCodi(
							documento.getDocumentTelematic().getReferenciaCodi());
					documentTelematic.setReferenciaClau(
							documento.getDocumentTelematic().getReferenciaClau());
					if (documento.getDocumentTelematic().getSignatures() != null) {
						List<Signatura> signatures = new ArrayList<Signatura>();
						for (net.conselldemallorca.helium.integracio.plugins.tramitacio.Signatura firma: documento.getDocumentTelematic().getSignatures()) {
							Signatura signatura = new Signatura();
							signatura.setFormat(firma.getFormat());
							signatura.setSignatura(firma.getSignatura());
							signatures.add(signatura);
						}
						documentTelematic.setSignatures(signatures);
					}
					document.setDocumentTelematic(documentTelematic);
				}
				documents.add(document);
			}
			tramit.setDocuments(documents);
		}
		return tramit;
	}

	static final long serialVersionUID = 1L;

}
