/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.integracio.plugins.registre.DadesAssumpte;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesExpedient;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesInteressat;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesNotificacio;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesOficina;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesRepresentat;
import net.conselldemallorca.helium.integracio.plugins.registre.DocumentRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreEntrada;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreNotificacio;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreSortida;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaAnotacioRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaConsulta;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantRecepcio;
import net.conselldemallorca.helium.integracio.plugins.registre.TramitSubsanacio;
import net.conselldemallorca.helium.integracio.plugins.registre.TramitSubsanacioParametre;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesTramit;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirDadesTramitRequest;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.AutenticacioTipus;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesRegistreEntrada;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesRegistreNotificacio;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesRegistreSortida;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentDisseny;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentPresencial;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentTelematic;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentTramit;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ExpedientInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.FilaResultat;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ParellaCodiValor;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.RespostaRegistre;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.Signatura;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.Tramit;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ExpedientInfo.IniciadorTipus;
import net.conselldemallorca.helium.jbpm3.integracio.Termini;
import net.conselldemallorca.helium.jbpm3.integracio.ValidationException;
import net.conselldemallorca.helium.model.dao.DaoProxy;
import net.conselldemallorca.helium.model.dao.DominiDao;
import net.conselldemallorca.helium.model.dao.EntornDao;
import net.conselldemallorca.helium.model.dao.MailDao;
import net.conselldemallorca.helium.model.dao.PluginGestioDocumentalDao;
import net.conselldemallorca.helium.model.dao.PluginRegistreDao;
import net.conselldemallorca.helium.model.dao.PluginTramitacioDao;
import net.conselldemallorca.helium.model.dto.ArxiuDto;
import net.conselldemallorca.helium.model.dto.DocumentDto;
import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.model.hibernate.Camp;
import net.conselldemallorca.helium.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.model.hibernate.Document;
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
			String documentCodi) {
		String varCodi = TascaService.PREFIX_DOCUMENT + documentCodi;
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
			return toExpedientInfo(ex);
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
	public RespostaRegistre registreEntrada(
			ExecutionContext executionContext,
			DadesRegistreEntrada dadesEntrada,
			List<DocumentInfo> documentsEntrada) {
		try {
			RegistreEntrada registreEntrada = new RegistreEntrada();
			DadesOficina dadesOficina = new DadesOficina();
			dadesOficina.setOrganCodi(dadesEntrada.getOrganCodi());
			dadesOficina.setOficinaCodi(dadesEntrada.getOficinaCodi());
			registreEntrada.setDadesOficina(dadesOficina);
			DadesInteressat dadesInteressat = new DadesInteressat();
			dadesInteressat.setEntitatCodi(dadesEntrada.getInteressatEntitatCodi());
			dadesInteressat.setNif(dadesEntrada.getInteressatNif());
			dadesInteressat.setNomAmbCognoms(dadesEntrada.getInteressatNomAmbCognoms());
			dadesInteressat.setPaisCodi(dadesEntrada.getInteressatPaisCodi());
			dadesInteressat.setPaisNom(dadesEntrada.getInteressatPaisNom());
			dadesInteressat.setProvinciaCodi(dadesEntrada.getInteressatProvinciaCodi());
			dadesInteressat.setProvinciaNom(dadesEntrada.getInteressatProvinciaNom());
			dadesInteressat.setMunicipiCodi(dadesEntrada.getInteressatMunicipiCodi());
			dadesInteressat.setMunicipiNom(dadesEntrada.getInteressatMunicipiNom());
			registreEntrada.setDadesInteressat(dadesInteressat);
			DadesRepresentat dadesRepresentat = new DadesRepresentat();
			dadesRepresentat.setNif(dadesEntrada.getRepresentatNif());
			dadesRepresentat.setNomAmbCognoms(dadesEntrada.getRepresentatNomAmbCognoms());
			registreEntrada.setDadesRepresentat(dadesRepresentat);
			DadesAssumpte dadesAssumpte = new DadesAssumpte();
			dadesAssumpte.setUnitatAdministrativa(dadesEntrada.getAnotacioUnitatAdministrativa());
			dadesAssumpte.setIdiomaCodi(dadesEntrada.getAnotacioIdiomaCodi());
			dadesAssumpte.setTipus(dadesEntrada.getAnotacioTipusAssumpte());
			dadesAssumpte.setAssumpte(dadesEntrada.getAnotacioAssumpte());
			registreEntrada.setDadesAssumpte(dadesAssumpte);
			List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
			for (DocumentInfo document: documentsEntrada) {
				DocumentRegistre doc = new DocumentRegistre();
				doc.setNom(document.getTitol());
				doc.setData(document.getDataDocument());
				doc.setIdiomaCodi("ca");
				doc.setArxiuNom(document.getArxiuNom());
				doc.setArxiuContingut(document.getArxiuContingut());
				documents.add(doc);
			}
			registreEntrada.setDocuments(documents);
			RespostaAnotacioRegistre respostaAnotacio = getPluginRegistreDao().registrarEntrada(
					registreEntrada);
			if (respostaAnotacio.isOk()) {
				for (DocumentInfo document: documentsEntrada) {
					DaoProxy.getInstance().getDocumentStoreDao().updateRegistreEntrada(
							document.getId(),
							respostaAnotacio.getData(),
							respostaAnotacio.getNumero(),
							dadesEntrada.getOrganCodi(),
							dadesEntrada.getOficinaCodi(),
							"Oficina Helium");
				}
				RespostaRegistre resposta = new RespostaRegistre();
				resposta.setNumero(respostaAnotacio.getNumero());
				resposta.setData(respostaAnotacio.getData());
				return resposta;
			} else {
				throw new JbpmException("No s'ha pogut registrar l'entrada: " + respostaAnotacio.getErrorDescripcio());
			}
		} catch (Exception ex) {
			throw new JbpmException("No s'ha pogut registrar l'entrada", ex);
		}
	}
	/**
	 * Consulta les dades d'una anotació al registre d'entrada
	 * 
	 * @param varDocument
	 * @param executionContext
	 * @return
	 */
	public DadesRegistreEntrada registreConsultarEntrada(
			ExecutionContext executionContext,
			String organCodi,
			String oficinaCodi,
			String numero) {
		try {
			RespostaConsulta dades = getPluginRegistreDao().consultarEntrada(
					organCodi,
					oficinaCodi,
					numero);
			DadesRegistreEntrada resposta = new DadesRegistreEntrada();
			if (dades.getDadesOficina() != null) {
				resposta.setOrganCodi(dades.getDadesOficina().getOrganCodi());
				resposta.setOficinaCodi(dades.getDadesOficina().getOficinaCodi());
			}
			if (dades.getDadesInteressat() != null) {
				resposta.setInteressatEntitatCodi(dades.getDadesInteressat().getEntitatCodi());
				resposta.setInteressatNif(dades.getDadesInteressat().getNif());
				resposta.setInteressatNomAmbCognoms(dades.getDadesInteressat().getNomAmbCognoms());
				resposta.setInteressatPaisCodi(dades.getDadesInteressat().getPaisCodi());
				resposta.setInteressatPaisNom(dades.getDadesInteressat().getPaisNom());
				resposta.setInteressatProvinciaCodi(dades.getDadesInteressat().getProvinciaCodi());
				resposta.setInteressatProvinciaNom(dades.getDadesInteressat().getProvinciaNom());
				resposta.setInteressatMunicipiCodi(dades.getDadesInteressat().getMunicipiCodi());
				resposta.setInteressatMunicipiNom(dades.getDadesInteressat().getMunicipiNom());
			}
			if (dades.getDadesRepresentat() != null) {
				resposta.setRepresentatNif(dades.getDadesRepresentat().getNif());
				resposta.setRepresentatNomAmbCognoms(dades.getDadesRepresentat().getNomAmbCognoms());
			}
			if (dades.getDadesAssumpte() != null) {
				resposta.setAnotacioUnitatAdministrativa(dades.getDadesAssumpte().getUnitatAdministrativa());
				resposta.setAnotacioIdiomaCodi(dades.getDadesAssumpte().getIdiomaCodi());
				resposta.setAnotacioTipusAssumpte(dades.getDadesAssumpte().getTipus());
				resposta.setAnotacioAssumpte(dades.getDadesAssumpte().getAssumpte());
			}
			return resposta;
		} catch (Exception ex) {
			throw new JbpmException("No s'ha pogut consultar l'entrada", ex);
		}
	}
	/**
	 * Registra un document de sortida
	 * 
	 * @param dadesRegistre
	 * @param executionContext
	 * @return
	 */
	public RespostaRegistre registreSortida(
			ExecutionContext executionContext,
			DadesRegistreSortida dadesSortida,
			List<DocumentInfo> documentsSortida) {
		try {
			RegistreSortida registreSortida = new RegistreSortida();
			DadesOficina dadesOficina = new DadesOficina();
			dadesOficina.setOrganCodi(dadesSortida.getOrganCodi());
			dadesOficina.setOficinaCodi(dadesSortida.getOficinaCodi());
			registreSortida.setDadesOficina(dadesOficina);
			DadesInteressat dadesInteressat = new DadesInteressat();
			dadesInteressat.setEntitatCodi(dadesSortida.getInteressatEntitatCodi());
			dadesInteressat.setNif(dadesSortida.getInteressatNif());
			dadesInteressat.setNomAmbCognoms(dadesSortida.getInteressatNomAmbCognoms());
			dadesInteressat.setPaisCodi(dadesSortida.getInteressatPaisCodi());
			dadesInteressat.setPaisNom(dadesSortida.getInteressatPaisNom());
			dadesInteressat.setProvinciaCodi(dadesSortida.getInteressatProvinciaCodi());
			dadesInteressat.setProvinciaNom(dadesSortida.getInteressatProvinciaNom());
			dadesInteressat.setMunicipiCodi(dadesSortida.getInteressatMunicipiCodi());
			dadesInteressat.setMunicipiNom(dadesSortida.getInteressatMunicipiNom());
			registreSortida.setDadesInteressat(dadesInteressat);
			DadesRepresentat dadesRepresentat = new DadesRepresentat();
			dadesRepresentat.setNif(dadesSortida.getRepresentatNif());
			dadesRepresentat.setNomAmbCognoms(dadesSortida.getRepresentatNomAmbCognoms());
			registreSortida.setDadesRepresentat(dadesRepresentat);
			DadesAssumpte dadesAssumpte = new DadesAssumpte();
			dadesAssumpte.setUnitatAdministrativa(dadesSortida.getAnotacioUnitatAdministrativa());
			dadesAssumpte.setIdiomaCodi(dadesSortida.getAnotacioIdiomaCodi());
			dadesAssumpte.setTipus(dadesSortida.getAnotacioTipusAssumpte());
			dadesAssumpte.setAssumpte(dadesSortida.getAnotacioAssumpte());
			registreSortida.setDadesAssumpte(dadesAssumpte);
			List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
			for (DocumentInfo document: documentsSortida) {
				DocumentRegistre doc = new DocumentRegistre();
				doc.setNom(document.getTitol());
				doc.setData(document.getDataDocument());
				doc.setIdiomaCodi("ca");
				doc.setArxiuNom(document.getArxiuNom());
				doc.setArxiuContingut(document.getArxiuContingut());
				documents.add(doc);
			}
			registreSortida.setDocuments(documents);
			RespostaAnotacioRegistre respostaAnotacio = getPluginRegistreDao().registrarSortida(
					registreSortida);
			if (respostaAnotacio.isOk()) {
				for (DocumentInfo document: documentsSortida) {
					DaoProxy.getInstance().getDocumentStoreDao().updateRegistreSortida(
							document.getId(),
							respostaAnotacio.getData(),
							respostaAnotacio.getNumero(),
							dadesSortida.getOrganCodi(),
							dadesSortida.getOficinaCodi(),
							"Oficina Helium");
				}
				RespostaRegistre resposta = new RespostaRegistre();
				resposta.setNumero(respostaAnotacio.getNumero());
				resposta.setData(respostaAnotacio.getData());
				return resposta;
			} else {
				throw new JbpmException("No s'ha pogut registrar la sortida: " + respostaAnotacio.getErrorDescripcio());
			}
		} catch (Exception ex) {
			throw new JbpmException("No s'ha pogut registrar la sortida", ex);
		}
	}
	/**
	 * Consulta un document de sortida
	 * 
	 * @param varDocument
	 * @param executionContext
	 * @return
	 */
	public DadesRegistreSortida registreConsultarSortida(
			ExecutionContext executionContext,
			String organCodi,
			String oficinaCodi,
			String numero) {
		try {
			RespostaConsulta dades = getPluginRegistreDao().consultarEntrada(
					organCodi,
					oficinaCodi,
					numero);
			DadesRegistreSortida resposta = new DadesRegistreSortida();
			if (dades.getDadesOficina() != null) {
				resposta.setOrganCodi(dades.getDadesOficina().getOrganCodi());
				resposta.setOficinaCodi(dades.getDadesOficina().getOficinaCodi());
			}
			if (dades.getDadesInteressat() != null) {
				resposta.setInteressatEntitatCodi(dades.getDadesInteressat().getEntitatCodi());
				resposta.setInteressatNif(dades.getDadesInteressat().getNif());
				resposta.setInteressatNomAmbCognoms(dades.getDadesInteressat().getNomAmbCognoms());
				resposta.setInteressatPaisCodi(dades.getDadesInteressat().getPaisCodi());
				resposta.setInteressatPaisNom(dades.getDadesInteressat().getPaisNom());
				resposta.setInteressatProvinciaCodi(dades.getDadesInteressat().getProvinciaCodi());
				resposta.setInteressatProvinciaNom(dades.getDadesInteressat().getProvinciaNom());
				resposta.setInteressatMunicipiCodi(dades.getDadesInteressat().getMunicipiCodi());
				resposta.setInteressatMunicipiNom(dades.getDadesInteressat().getMunicipiNom());
			}
			if (dades.getDadesRepresentat() != null) {
				resposta.setRepresentatNif(dades.getDadesRepresentat().getNif());
				resposta.setRepresentatNomAmbCognoms(dades.getDadesRepresentat().getNomAmbCognoms());
			}
			if (dades.getDadesAssumpte() != null) {
				resposta.setAnotacioIdiomaCodi(dades.getDadesAssumpte().getIdiomaCodi());
				resposta.setAnotacioTipusAssumpte(dades.getDadesAssumpte().getTipus());
				resposta.setAnotacioAssumpte(dades.getDadesAssumpte().getAssumpte());
			}
			return resposta;
		} catch (Exception ex) {
			throw new JbpmException("No s'ha pogut consultar la sortida", ex);
		}
	}

	/**
	 * Registra un document de sortida
	 * 
	 * @param dadesRegistre
	 * @param executionContext
	 * @return
	 */
	public RespostaRegistre registreNotificacio(
			ExecutionContext executionContext,
			DadesRegistreNotificacio dadesNotificacio,
			List<DocumentInfo> documentsNotificacio) {
		try {
			RegistreNotificacio registreNotificacio = new RegistreNotificacio();
			DadesExpedient dadesExpedient = new DadesExpedient();
			dadesExpedient.setIdentificador(dadesNotificacio.getExpedientIdentificador());
			dadesExpedient.setClau(dadesNotificacio.getExpedientClau());
			dadesExpedient.setUnitatAdministrativa(dadesNotificacio.getExpedientUnitatAdministrativa());
			registreNotificacio.setDadesExpedient(dadesExpedient);
			DadesOficina dadesOficina = new DadesOficina();
			dadesOficina.setOrganCodi(dadesNotificacio.getOrganCodi());
			dadesOficina.setOficinaCodi(dadesNotificacio.getOficinaCodi());
			registreNotificacio.setDadesOficina(dadesOficina);
			DadesInteressat dadesInteressat = new DadesInteressat();
			dadesInteressat.setAutenticat(dadesNotificacio.isInteressatAutenticat());
			dadesInteressat.setEntitatCodi(dadesNotificacio.getInteressatEntitatCodi());
			dadesInteressat.setNif(dadesNotificacio.getInteressatNif());
			dadesInteressat.setNomAmbCognoms(dadesNotificacio.getInteressatNomAmbCognoms());
			dadesInteressat.setPaisCodi(dadesNotificacio.getInteressatPaisCodi());
			dadesInteressat.setPaisNom(dadesNotificacio.getInteressatPaisNom());
			dadesInteressat.setProvinciaCodi(dadesNotificacio.getInteressatProvinciaCodi());
			dadesInteressat.setProvinciaNom(dadesNotificacio.getInteressatProvinciaNom());
			dadesInteressat.setMunicipiCodi(dadesNotificacio.getInteressatMunicipiCodi());
			dadesInteressat.setMunicipiNom(dadesNotificacio.getInteressatMunicipiNom());
			registreNotificacio.setDadesInteressat(dadesInteressat);
			if (dadesNotificacio.getRepresentatNif() != null || dadesNotificacio.getRepresentatNomAmbCognoms() != null) {
				DadesRepresentat dadesRepresentat = new DadesRepresentat();
				dadesRepresentat.setNif(dadesNotificacio.getRepresentatNif());
				dadesRepresentat.setNomAmbCognoms(dadesNotificacio.getRepresentatNomAmbCognoms());
				registreNotificacio.setDadesRepresentat(dadesRepresentat);
			}
			DadesNotificacio dadesNotifi = new DadesNotificacio();
			dadesNotifi.setIdiomaCodi(dadesNotificacio.getAnotacioIdiomaCodi());
			dadesNotifi.setTipus(dadesNotificacio.getAnotacioTipusAssumpte());
			dadesNotifi.setAssumpte(dadesNotificacio.getAnotacioAssumpte());
			dadesNotifi.setJustificantRecepcio(dadesNotificacio.isNotificacioJustificantRecepcio());
			dadesNotifi.setAvisTitol(dadesNotificacio.getNotificacioAvisTitol());
			dadesNotifi.setAvisText(dadesNotificacio.getNotificacioAvisText());
			dadesNotifi.setAvisTextSms(dadesNotificacio.getNotificacioAvisTextSms());
			dadesNotifi.setOficiTitol(dadesNotificacio.getNotificacioOficiTitol());
			dadesNotifi.setOficiText(dadesNotificacio.getNotificacioOficiText());
			if (dadesNotificacio.getNotificacioSubsanacioTramitIdentificador() != null) {
				TramitSubsanacio tramitSubsanacio = new TramitSubsanacio();
				tramitSubsanacio.setIdentificador(
						dadesNotificacio.getNotificacioSubsanacioTramitIdentificador());
				tramitSubsanacio.setVersio(
						dadesNotificacio.getNotificacioSubsanacioTramitVersio());
				tramitSubsanacio.setDescripcio(
						dadesNotificacio.getNotificacioSubsanacioTramitDescripcio());
				if (dadesNotificacio.getNotificacioSubsanacioParametres() != null) {
					List<TramitSubsanacioParametre> parametres = new ArrayList<TramitSubsanacioParametre>();
					for (String key: dadesNotificacio.getNotificacioSubsanacioParametres().keySet()) {
						TramitSubsanacioParametre parametre = new TramitSubsanacioParametre();
						parametre.setParametre(key);
						parametre.setValor(
								dadesNotificacio.getNotificacioSubsanacioParametres().get(key));
						parametres.add(parametre);
					}
					tramitSubsanacio.setParametres(parametres);
				}
				dadesNotifi.setOficiTramitSubsanacio(tramitSubsanacio);
			}
			registreNotificacio.setDadesNotificacio(dadesNotifi);
			List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
			for (DocumentInfo document: documentsNotificacio) {
				DocumentRegistre doc = new DocumentRegistre();
				doc.setNom(document.getTitol());
				doc.setData(document.getDataDocument());
				doc.setIdiomaCodi("ca");
				doc.setArxiuNom(document.getArxiuNom());
				doc.setArxiuContingut(document.getArxiuContingut());
				documents.add(doc);
			}
			registreNotificacio.setDocuments(documents);
			RespostaAnotacioRegistre respostaAnotacio = getPluginRegistreDao().registrarNotificacio(
					registreNotificacio);
			if (respostaAnotacio.isOk()) {
				for (DocumentInfo document: documentsNotificacio) {
					DaoProxy.getInstance().getDocumentStoreDao().updateRegistreSortida(
							document.getId(),
							respostaAnotacio.getData(),
							respostaAnotacio.getNumero(),
							dadesNotificacio.getOrganCodi(),
							dadesNotificacio.getOficinaCodi(),
							"Oficina Helium");
				}
				RespostaRegistre resposta = new RespostaRegistre();
				resposta.setNumero(respostaAnotacio.getNumero());
				resposta.setData(respostaAnotacio.getData());
				return resposta;
			} else {
				throw new JbpmException("No s'ha pogut registrar la sortida: " + respostaAnotacio.getErrorDescripcio());
			}
		} catch (Exception ex) {
			throw new JbpmException("No s'ha pogut registrar la sortida", ex);
		}
	}
	public Date registreObtenirJustificantRecepcio(String registreNumero) {
		try {
			RespostaJustificantRecepcio resposta = getPluginRegistreDao().obtenirJustificantRecepcio(registreNumero);
			if (resposta.isOk()) {
				return resposta.getData();
			} else {
				throw new JbpmException("Error al obtenir el justificant de recepcio: " + resposta.getErrorDescripcio());
			}
		} catch (Exception ex) {
			throw new JbpmException("No s'ha pogut obtenir el justificant de recepció", ex);
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

	public byte[] obtenirArxiuGestorDocumental(String id) {
		return getPluginGestioDocumentalDao().retrieveDocument(id);
	}

	public void documentGuardar(
			ExecutionContext executionContext,
			String documentCodi,
			Date data,
			String arxiuNom,
			byte[] arxiuContingut) {
		long processInstanceId = executionContext.getProcessInstance().getId();
		InstanciaProcesDto instanciaProces = getExpedientService().getInstanciaProcesById(
				new Long(processInstanceId).toString(),
				false);
		Document document = getDissenyService().findDocumentAmbDefinicioProcesICodi(
				instanciaProces.getDefinicioProces().getId(),
				documentCodi);
		getExpedientService().guardarDocument(
				new Long(processInstanceId).toString(),
				document.getId(),
				data,
				arxiuNom,
				arxiuContingut);
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
	private PluginRegistreDao getPluginRegistreDao() {
		return DaoProxy.getInstance().getPluginRegistreDao();
	}
	private PluginTramitacioDao getPluginTramitacioDao() {
		return DaoProxy.getInstance().getPluginTramitacioDao();
	}
	private PluginGestioDocumentalDao getPluginGestioDocumentalDao() {
		return DaoProxy.getInstance().getPluginGestioDocumentalDao();
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

	private ExpedientInfo toExpedientInfo(Expedient expedient) {
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
			resposta.setGeoPosX(expedient.getGeoPosX());
			resposta.setGeoPosY(expedient.getGeoPosY());
			resposta.setGeoReferencia(expedient.getGeoReferencia());
			resposta.setRegistreNumero(expedient.getRegistreNumero());
			resposta.setRegistreData(expedient.getRegistreData());
			resposta.setUnitatAdministrativa(expedient.getUnitatAdministrativa());
			resposta.setIdioma(expedient.getIdioma());
			resposta.setAutenticat(expedient.isAutenticat());
			resposta.setTramitadorNif(expedient.getTramitadorNif());
			resposta.setTramitadorNom(expedient.getTramitadorNom());
			resposta.setInteressatNif(expedient.getInteressatNif());
			resposta.setInteressatNom(expedient.getInteressatNom());
			resposta.setRepresentantNif(expedient.getRepresentantNif());
			resposta.setRepresentantNom(expedient.getRepresentantNom());
			resposta.setAvisosHabilitats(expedient.isAvisosHabilitats());
			resposta.setAvisosEmail(expedient.getAvisosEmail());
			resposta.setAvisosMobil(expedient.getAvisosMobil());
			resposta.setNotificacioTelematicaHabilitada(expedient.isNotificacioTelematicaHabilitada());
			resposta.setTramitExpedientIdentificador(expedient.getTramitExpedientIdentificador());
			resposta.setTramitExpedientClau(expedient.getTramitExpedientClau());
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
