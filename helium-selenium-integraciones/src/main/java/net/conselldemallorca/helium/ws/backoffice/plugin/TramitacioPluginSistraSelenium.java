package net.conselldemallorca.helium.ws.backoffice.plugin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.ws.WsClientUtils;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreNotificacio;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaAnotacioRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantDetallRecepcio;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantRecepcio;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.AutenticacioTipus;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesTramit;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesVistaDocument;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DocumentEvent;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DocumentPresencial;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DocumentTelematic;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DocumentTramit;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.Event;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirDadesTramitRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirVistaDocumentRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarEventRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarExpedientRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ResultatProcesTipus;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ResultatProcesTramitRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.Signatura;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.TramitacioPlugin;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.TramitacioPluginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.caib.bantel.ws.v2.model.documentobte.DocumentoBTE;
import es.caib.bantel.ws.v2.model.firmaws.FirmaWS;
import es.caib.bantel.ws.v2.model.referenciaentrada.ReferenciaEntrada;
import es.caib.bantel.ws.v2.model.tramitebte.TramiteBTE;
import es.caib.redose.ws.v2.model.documentords.DocumentoRDS;
import es.caib.redose.ws.v2.model.referenciards.ReferenciaRDS;
import es.caib.zonaper.ws.v2.model.configuracionavisosexpediente.ConfiguracionAvisosExpediente;
import es.caib.zonaper.ws.v2.model.documentoexpediente.DocumentoExpediente;
import es.caib.zonaper.ws.v2.model.documentoexpediente.DocumentosExpediente;
import es.caib.zonaper.ws.v2.model.eventoexpediente.EventoExpediente;
import es.caib.zonaper.ws.v2.model.eventoexpediente.EventosExpediente;
import es.caib.zonaper.ws.v2.model.expediente.Expediente;

/**
 * Implementació del plugin de tramitacio accedint a la v2
 * dels ws de SISTRA
 * 
 * @author Limit Tecnologies
 */
public class TramitacioPluginSistraSelenium implements TramitacioPlugin {

	public void publicarExpedient(PublicarExpedientRequest request) throws TramitacioPluginException {
		
		try {
			
			Expediente expediente = new Expediente();
			expediente.setIdentificadorExpediente(request.getExpedientIdentificador());
			expediente.setUnidadAdministrativa(request.getUnitatAdministrativa());
			expediente.setClaveExpediente(request.getExpedientClau());
			expediente.setIdioma(request.getIdioma());
			expediente.setDescripcion(request.getDescripcio());
			expediente.setAutenticado(request.isAutenticat());
			/*
			- Amb delegat
			BANTEL			HELIUM			ZONAPER
			Usuario			Tramitador		-
			Representado	Interessat		Representado
			Delegado		Representant	Representante
			- Sense delegat
			BANTEL			HELIUM			ZONAPER
			Usuario			Tramitador		-
			Representado	Interessat		Representado
			Usuario			Representant	Representante
			 */
			if (request.getRepresentantNif() != null)
				expediente.setNifRepresentante(
						new JAXBElement<String>(
								new QName("nifRepresentante"),
								String.class,
								request.getRepresentantNif()));
			if (request.getRepresentatNif() != null)
				expediente.setNifRepresentado(
						new JAXBElement<String>(
								new QName("nifRepresentado"),
								String.class,
								request.getRepresentatNif()));
			if (request.getRepresentatNom() != null)
				expediente.setNombreRepresentado(
						new JAXBElement<String>(
								new QName("nombreRepresentado"),
								String.class,
								request.getRepresentatNom()));
			if (request.getTramitNumero() != null)
				expediente.setNumeroEntradaBTE(
						new JAXBElement<String>(
								new QName("numeroEntradaBTE"),
								String.class,
								request.getTramitNumero()));
			ConfiguracionAvisosExpediente configuracionAvisos = new ConfiguracionAvisosExpediente();
			configuracionAvisos.setHabilitarAvisos(
					new JAXBElement<Boolean>(
							new QName("habilitarAvisos"),
							Boolean.class,
							new Boolean(request.isAvisosHabilitat())));
			configuracionAvisos.setAvisoEmail(
					new JAXBElement<String>(
							new QName("avisoEmail"),
							String.class,
							request.getAvisosEmail()));
			configuracionAvisos.setAvisoSMS(
					new JAXBElement<String>(
							new QName("avisoSMS"),
							String.class,
							request.getAvisosSMS()));
			expediente.setConfiguracionAvisos(
					new JAXBElement<ConfiguracionAvisosExpediente>(
							new QName("configuracionAvisos"),
							ConfiguracionAvisosExpediente.class,
							configuracionAvisos));
			if (request.getEvents() != null) {
				EventosExpediente eventosExpediente = new EventosExpediente();
				for (Event event: request.getEvents())
					eventosExpediente.getEvento().add(toEvento(event));
				expediente.setEventos(
						new JAXBElement<EventosExpediente>(
								new QName("eventos"),
								EventosExpediente.class,
								eventosExpediente));
			}

			System.out.println("ZPers: TramitacioPluginSistrav2 expedient nom : " + expediente.getNombreRepresentado());
			System.out.println("ZPers: TramitacioPluginSistrav2 expedient nif : " + expediente.getNifRepresentado());

			System.out.println("ZPers: TramitacioPluginSistrav2 request : " + request);
			String nifZonaPersonal = request.getRepresentatNif() == null ? request.getRepresentantNif() : request.getRepresentatNif();
			
			if (!getZonaperClient().existeZonaPersonalUsuario(nifZonaPersonal.toUpperCase()) && !getZonaperClient().existeZonaPersonalUsuario(nifZonaPersonal)) {
				if (getZonaperClient().altaZonaPersonalUsuario(
						nifZonaPersonal.toUpperCase(), 
						request.getRepresentatNom() == null ? "" : request.getRepresentatNom(), 
						request.getRepresentatApe1(), 
						request.getRepresentatApe2()) == null) {
					logger.error("Error al crear la zona personal: " + request + " - " + request.getRepresentantNif());
					throw new TramitacioPluginException("Error al crear la zona personal: " + request.getRepresentantNif());
				}
			}
			
			getZonaperClient().altaExpediente(expediente);
			logger.info("Nou expedient creat a la zona personal del ciutadà " + request.getRepresentatNif() + ": [" + request.getExpedientIdentificador() + ", " + request.getExpedientClau() + "]");
		} catch (Exception ex) {
			logger.error("Error al crear expedient a la zona personal: " + request, ex);
			throw new TramitacioPluginException("Error al crear expedient a la zona personal", ex);
		}
	}

	public void publicarEvent(
			PublicarEventRequest request) throws TramitacioPluginException {
		try {
			Event event = request.getEvent();
			if (event != null) {
				if (!getZonaperClient().existeZonaPersonalUsuario(request.getRepresentatNif().toUpperCase())) {
					if (getZonaperClient().altaZonaPersonalUsuario(
							request.getRepresentatNif().toUpperCase(), 
							request.getRepresentatNom() == null ? "" : request.getRepresentatNom(), 
							request.getRepresentatApe1(), 
							request.getRepresentatApe2()) == null) {
						logger.error("Error al crear la zona personal: " + request.getRepresentatNif());
						throw new TramitacioPluginException("Error al crear la zona personal: " + request.getRepresentatNif());
					}
				}
				getZonaperClient().altaEventoExpediente(
						request.getUnitatAdministrativa(),
						request.getExpedientIdentificador(),
						request.getExpedientClau(),
						toEvento(event));
				logger.info("Nou event creat a la zona personal del ciutadà per a l'expedient: [" + request.getExpedientIdentificador() + ", " + request.getExpedientClau() + "]");
			} else {
				throw new TramitacioPluginException("Error crear event: l'event es null");
			}
		} catch (Exception ex) {
			logger.error("Error crear event a la zona personal", ex);
			throw new TramitacioPluginException("Error crear event a la zona personal", ex);
		}
	}

	public DadesTramit obtenirDadesTramit(
			ObtenirDadesTramitRequest request) throws TramitacioPluginException {
		try {
			ReferenciaEntrada referenciaEntrada = new ReferenciaEntrada();
			referenciaEntrada.setNumeroEntrada(request.getNumero());
			referenciaEntrada.setClaveAcceso(request.getClau());
			return toDadesTramit(
					getBantelClient().obtenerEntrada(referenciaEntrada));
		} catch (Exception ex) {
			logger.error("Error al obtenir dades del tràmit", ex);
			throw new TramitacioPluginException("Error al obtenir dades del tràmit", ex);
		}
	}

	public void comunicarResultatProcesTramit(ResultatProcesTramitRequest request) throws TramitacioPluginException {
		try {
			ReferenciaEntrada referenciaEntrada = new ReferenciaEntrada();
			referenciaEntrada.setNumeroEntrada(request.getNumeroEntrada());
			referenciaEntrada.setClaveAcceso(request.getClauAcces());
			String resultat = null;
			if (ResultatProcesTipus.PROCESSAT.equals(request.getResultatProces())) {
				resultat = "S";
			} else if (ResultatProcesTipus.NO_PROCESSAT.equals(request.getResultatProces())) {
				resultat = "N";
			} else if (ResultatProcesTipus.ERROR.equals(request.getResultatProces())) {
				resultat = "X";
			}
			getBantelClient().establecerResultadoProceso(
					referenciaEntrada,
					resultat,
					request.getErrorDescripcio());
		} catch (Exception ex) {
			logger.error("Error al comunicar el resultat de processar el tràmit", ex);
			throw new TramitacioPluginException("Error al obtenir dades del tràmit", ex);
		}
	}

	public DadesVistaDocument obtenirVistaDocument(ObtenirVistaDocumentRequest request) throws TramitacioPluginException {
		try {
			ReferenciaRDS referencia = new ReferenciaRDS();
			referencia.setCodigo(request.getReferenciaCodi());
			referencia.setClave(request.getReferenciaClau());
			DocumentoRDS documento = getRedoseClient().consultarDocumentoFormateado(
					referencia,
					request.getPlantillaTipus(),
					request.getIdioma());
			DadesVistaDocument resposta = new DadesVistaDocument();
			resposta.setNom(documento.getNombreFichero());
			resposta.setArxiuNom(documento.getNombreFichero());
			resposta.setArxiuContingut(documento.getDatosFichero());
			return resposta;
		} catch (Exception ex) {
			logger.error("Error al obtenir la vista del document " + request, ex);
			throw new TramitacioPluginException("Error al obtenir la vista del document " + request, ex);
		}
	}



	private DadesTramit toDadesTramit(TramiteBTE entrada) {
		DadesTramit tramit = new DadesTramit();
		tramit.setNumero(entrada.getNumeroEntrada());
		tramit.setClauAcces(entrada.getCodigoEntrada());
		tramit.setIdentificador(entrada.getIdentificadorTramite());
		tramit.setUnitatAdministrativa(entrada.getUnidadAdministrativa());
		tramit.setVersio(entrada.getVersionTramite());
		if (entrada.getFecha() != null)
			tramit.setData(entrada.getFecha().toGregorianCalendar().getTime());
		tramit.setIdioma(entrada.getIdioma());
		tramit.setRegistreNumero(entrada.getNumeroRegistro());
		if (entrada.getFechaRegistro() != null)
			tramit.setRegistreData(
					entrada.getFechaRegistro().toGregorianCalendar().getTime());
		if (entrada.getTipoConfirmacionPreregistro() != null)
			tramit.setPreregistreTipusConfirmacio(entrada.getTipoConfirmacionPreregistro().getValue());
		if (entrada.getNumeroPreregistro() != null)
			tramit.setPreregistreNumero(entrada.getNumeroPreregistro().getValue());
		if (entrada.getFechaPreregistro() != null && entrada.getFechaPreregistro().getValue() != null)
			tramit.setPreregistreData(
					entrada.getFechaPreregistro().getValue().toGregorianCalendar().getTime());
		if (entrada.getNivelAutenticacion() != null) {
			if ("A".equalsIgnoreCase(entrada.getNivelAutenticacion()))
				tramit.setAutenticacioTipus(AutenticacioTipus.ANONIMA);
			if ("U".equalsIgnoreCase(entrada.getNivelAutenticacion()))
				tramit.setAutenticacioTipus(AutenticacioTipus.USUARI);
			if ("C".equalsIgnoreCase(entrada.getNivelAutenticacion()))
				tramit.setAutenticacioTipus(AutenticacioTipus.CERTIFICAT);
		}
		if (entrada.getUsuarioNif() != null)
			tramit.setTramitadorNif(entrada.getUsuarioNif().getValue());
		if (entrada.getUsuarioNombre() != null)
			tramit.setTramitadorNom(entrada.getUsuarioNombre().getValue());
		if (entrada.getRepresentadoNif() != null)
			tramit.setInteressatNif(entrada.getRepresentadoNif().getValue());
		if (entrada.getRepresentadoNombre() != null)
			tramit.setInteressatNom(entrada.getRepresentadoNombre().getValue());
		/* */
		/*if (entrada.getDelegadoNif() != null && entrada.getDelegadoNif().getValue() != null) {
			tramit.setRepresentantNif(entrada.getDelegadoNif().getValue());
			if (entrada.getDelegadoNombre() != null)
				tramit.setRepresentantNom(entrada.getDelegadoNombre().getValue());
		} else {
			if (entrada.getUsuarioNif() != null)
				tramit.setRepresentantNif(entrada.getUsuarioNif().getValue());
			if (entrada.getUsuarioNombre() != null)
				tramit.setRepresentantNom(entrada.getUsuarioNombre().getValue());
		}*/
		if (entrada.getUsuarioNif() != null)
			tramit.setRepresentantNif(entrada.getUsuarioNif().getValue());
		if (entrada.getUsuarioNombre() != null)
			tramit.setRepresentantNom(entrada.getUsuarioNombre().getValue());
		/* */
		tramit.setSignat(entrada.isFirmadaDigitalmente());
		if (entrada.getHabilitarAvisos() != null)
			tramit.setAvisosHabilitats(
					"S".equalsIgnoreCase(entrada.getHabilitarAvisos().getValue()));
		if (entrada.getAvisoSMS() != null)
			tramit.setAvisosSms(entrada.getAvisoSMS().getValue());
		if (entrada.getAvisoEmail() != null)
			tramit.setAvisosEmail(entrada.getAvisoEmail().getValue());
		if (entrada.getHabilitarNotificacionTelematica() != null)
			tramit.setNotificacioTelematicaHabilitada(
					"S".equalsIgnoreCase(entrada.getHabilitarNotificacionTelematica().getValue()));
		if (entrada.getDocumentos() != null) {
			List<DocumentTramit> documents = new ArrayList<DocumentTramit>();
			for (DocumentoBTE documento: entrada.getDocumentos().getDocumento()) {
				DocumentTramit document = new DocumentTramit();
				document.setNom(documento.getNombre());
				document.setIdentificador(documento.getIdentificador());
				document.setInstanciaNumero(documento.getNumeroInstancia());
				if (documento.getPresentacionPresencial() != null && documento.getPresentacionPresencial().getValue() != null) {
					DocumentPresencial documentPresencial = new DocumentPresencial();
					documentPresencial.setDocumentCompulsar(
							documento.getPresentacionPresencial().getValue().getCompulsarDocumento());
					documentPresencial.setSignatura(
							documento.getPresentacionPresencial().getValue().getFirma());
					documentPresencial.setFotocopia(
							documento.getPresentacionPresencial().getValue().getFotocopia());
					documentPresencial.setTipus(
							documento.getPresentacionPresencial().getValue().getTipoDocumento());
					document.setDocumentPresencial(documentPresencial);
				}
				if (documento.getPresentacionTelematica() != null && documento.getPresentacionTelematica().getValue() != null) {
					DocumentTelematic documentTelematic = new DocumentTelematic();
					documentTelematic.setArxiuNom(
							documento.getPresentacionTelematica().getValue().getNombre());
					documentTelematic.setArxiuExtensio(
							documento.getPresentacionTelematica().getValue().getExtension());
					documentTelematic.setArxiuContingut(
							documento.getPresentacionTelematica().getValue().getContent());
					documentTelematic.setReferenciaCodi(
							documento.getPresentacionTelematica().getValue().getCodigoReferenciaRds());
					documentTelematic.setReferenciaClau(
							documento.getPresentacionTelematica().getValue().getClaveReferenciaRds());
					if (documento.getPresentacionTelematica().getValue().getFirmas() != null && documento.getPresentacionTelematica().getValue().getFirmas() != null) {
						List<Signatura> signatures = new ArrayList<Signatura>();
						for (FirmaWS firma: documento.getPresentacionTelematica().getValue().getFirmas().getFirmas()) {
							Signatura signatura = new Signatura();
							if (firma.getFormato() != null)
								signatura.setFormat(firma.getFormato().getValue());
							signatura.setSignatura(firma.getFirma());
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
	
	private EventoExpediente toEvento(Event event) {
		EventoExpediente evento = new EventoExpediente();
		evento.setTitulo(event.getTitol());
		evento.setFecha(
				new JAXBElement<String>(
						new QName("fecha"),
						String.class,
						new SimpleDateFormat("dd/MM/yyyy").format(new Date())));
		evento.setTexto(event.getText());
		evento.setTextoSMS(
				new JAXBElement<String>(
						new QName("textoSMS"),
						String.class,
						event.getTextSMS()));
		evento.setEnlaceConsulta(
				new JAXBElement<String>(
						new QName("enlaceConsulta"),
						String.class,
						event.getEnllasConsulta()));
		if (event.getDocuments() != null) {
			DocumentosExpediente documentos = new DocumentosExpediente();
			for (DocumentEvent document: event.getDocuments()) {
				DocumentoExpediente documento = new DocumentoExpediente();
				documento.setTitulo(
						new JAXBElement<String>(
								new QName("titulo"),
								String.class,
								document.getNom()));
				documento.setNombre(
						new JAXBElement<String>(
								new QName("nombre"),
								String.class,
								document.getArxiuNom()));
				documento.setContenidoFichero(
						new JAXBElement<byte[]>(
								new QName("contenidoFichero"),
								byte[].class,
								document.getArxiuContingut()));
				documento.setEstructurado(
						new JAXBElement<Boolean>(
								new QName("estructurado"),
								Boolean.class,
								new Boolean(false)));
				documentos.getDocumento().add(documento);
			}
			evento.setDocumentos(
					new JAXBElement<DocumentosExpediente>(
							new QName("documentos"),
							DocumentosExpediente.class,
							documentos));
		}
		return evento;
	}

	private es.caib.bantel.ws.v2.services.BackofficeFacade getBantelClient() {
		String url = GlobalProperties.getInstance().getProperty("app.bantel.entrades.url");
		if (url == null)
			url = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.bantel.url");
		String userName = GlobalProperties.getInstance().getProperty("app.bantel.entrades.username");
		if (userName == null)
			userName = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.bantel.username");
		String password = GlobalProperties.getInstance().getProperty("app.bantel.entrades.password");
		if (password == null)
			password = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.bantel.password");
		Object wsClientProxy = WsClientUtils.getWsClientProxy(
				es.caib.bantel.ws.v2.services.BackofficeFacade.class,
				url,
				userName,
				password,
				getWsClientAuthType(),
				isWsClientGenerateTimestamp(),
				isWsClientLogCalls(),
				isWsClientDisableCnCheck());
		return (es.caib.bantel.ws.v2.services.BackofficeFacade)wsClientProxy;
	}

	private es.caib.zonaper.ws.v2.services.BackofficeFacade getZonaperClient() {
		String url = GlobalProperties.getInstance().getProperty("app.zonaper.service.url");
		if (url == null)
			url = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.zonaper.url");
		String userName = GlobalProperties.getInstance().getProperty("app.zonaper.service.username");
		if (userName == null)
			userName = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.zonaper.username");
		String password = GlobalProperties.getInstance().getProperty("app.zonaper.service.password");
		if (password == null)
			password = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.zonaper.password");
		Object wsClientProxy = WsClientUtils.getWsClientProxy(
				es.caib.zonaper.ws.v2.services.BackofficeFacade.class,
				url,
				userName,
				password,
				getWsClientAuthType(),
				isWsClientGenerateTimestamp(),
				isWsClientLogCalls(),
				isWsClientDisableCnCheck());
		return (es.caib.zonaper.ws.v2.services.BackofficeFacade)wsClientProxy;
	}

	private es.caib.redose.ws.v2.services.BackofficeFacade getRedoseClient() {
		String url = GlobalProperties.getInstance().getProperty("app.redose.service.url");
		if (url == null)
			url = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.redose.url");
		String userName = GlobalProperties.getInstance().getProperty("app.redose.service.username");
		if (userName == null)
			userName = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.redose.username");
		String password = GlobalProperties.getInstance().getProperty("app.redose.service.password");
		if (password == null)
			password = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.redose.password");
		Object wsClientProxy = WsClientUtils.getWsClientProxy(
				es.caib.redose.ws.v2.services.BackofficeFacade.class,
				url,
				userName,
				password,
				getWsClientAuthType(),
				isWsClientGenerateTimestamp(),
				isWsClientLogCalls(),
				isWsClientDisableCnCheck());
		return (es.caib.redose.ws.v2.services.BackofficeFacade)wsClientProxy;
	}

	@Override
	public RespostaJustificantRecepcio obtenirJustificantRecepcio(String numeroRegistre) throws TramitacioPluginException {
		throw new TramitacioPluginException("Error al obtenir justificant de recepció: petició no suportada");
	}

	@Override
	public RespostaAnotacioRegistre registrarNotificacio(RegistreNotificacio registreNotificacio) throws TramitacioPluginException {
		throw new TramitacioPluginException("Error al registrar notificació: petició no suportada");
		}

	@Override
	public RespostaJustificantDetallRecepcio obtenirJustificantDetallRecepcio(String numeroRegistre) throws TramitacioPluginException {
		throw new TramitacioPluginException("Error al obtenir justificant detall de recepció: petició no suportada");
	}

	private String getWsClientAuthType() {
		String authType = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.auth");
		if (authType == null)
			authType = GlobalProperties.getInstance().getProperty("app.ws.client.auth");
		return authType;
	}
	private boolean isWsClientGenerateTimestamp() {
		String authType = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.generate.timestamp");
		if (authType == null)
			authType = GlobalProperties.getInstance().getProperty("app.ws.client.generate.timestamp");
		return "true".equalsIgnoreCase(authType);
	}
	private boolean isWsClientLogCalls() {
		String logCalls = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.log.calls");
		if (logCalls == null)
			logCalls = GlobalProperties.getInstance().getProperty("app.ws.client.log.calls");
		return "true".equalsIgnoreCase(logCalls);
	}
	private boolean isWsClientDisableCnCheck() {
		String disableCnCheck = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.disable.cn.check");
		if (disableCnCheck == null)
			disableCnCheck = GlobalProperties.getInstance().getProperty("app.ws.client.disable.cn.check");
		return "true".equalsIgnoreCase(disableCnCheck);
	}

	private static final Log logger = LogFactory.getLog(TramitacioPluginSistrav3.class);

}
