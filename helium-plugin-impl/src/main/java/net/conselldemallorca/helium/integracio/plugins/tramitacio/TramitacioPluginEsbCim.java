/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.tramitacio;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.ws.WsClientUtils;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreNotificacio;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaAnotacioRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantDetallRecepcio;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantRecepcio;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.cim.ws.documentos.v1.model.gestordocumental.ObtenerVistaDocumentoRequest;
import es.cim.ws.documentos.v1.model.gestordocumental.ObtenerVistaDocumentoResponse;
import es.cim.ws.documentos.v1.services.ServicioGestorDocumentalPortType;
import es.cim.ws.tramitacion.v1.model.configuracionavisosexpediente.ConfiguracionAvisosExpediente;
import es.cim.ws.tramitacion.v1.model.documentoexpediente.DocumentoExpediente;
import es.cim.ws.tramitacion.v1.model.documentoexpediente.DocumentosExpediente;
import es.cim.ws.tramitacion.v1.model.documentoexpediente.TypeDocumentoExpediente;
import es.cim.ws.tramitacion.v1.model.eventoexpediente.EventoExpediente;
import es.cim.ws.tramitacion.v1.model.eventoexpediente.EventosExpediente;
import es.cim.ws.tramitacion.v1.model.expediente.Expediente;
import es.cim.ws.tramitacion.v1.model.tramitacion.PublicarEventoRequest;
import es.cim.ws.tramitacion.v1.model.tramitacion.PublicarExpedienteRequest;
import es.cim.ws.tramitacion.v1.services.ServicioTramitacionPortType;

/**
 * Implementació del plugin de tramitacio accedint al ESB del
 * Consell de Mallorca
 * 
 * @author Limit Tecnologies
 */
public class TramitacioPluginEsbCim implements TramitacioPlugin {

	public void publicarExpedient(
			PublicarExpedientRequest request) throws TramitacioPluginException {
		try {
			Expediente expediente = new Expediente();
			expediente.setIdentificadorExpediente(request.getExpedientIdentificador());
			expediente.setUnidadAdministrativa(request.getUnitatAdministrativa());
			expediente.setClaveExpediente(request.getExpedientClau());
			expediente.setIdioma(request.getIdioma());
			expediente.setDescripcion(request.getDescripcio());
			expediente.setAutenticado(request.isAutenticat());
			expediente.setNifRepresentante(
					new JAXBElement<String>(
							new QName("nifRepresentante"),
							String.class,
							request.getRepresentantNif()));
			expediente.setNifRepresentado(
					new JAXBElement<String>(
							new QName("nifRepresentado"),
							String.class,
							request.getRepresentatNif()));
			expediente.setNombreRepresentado(
					new JAXBElement<String>(
							new QName("nombreRepresentado"),
							String.class,
							request.getRepresentatNom()));
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
			PublicarExpedienteRequest req = new PublicarExpedienteRequest();
			req.setExpediente(expediente);
			getTramitacioClient().publicarExpediente(req);
		} catch (Exception ex) {
			logger.error("Error al crear expedient a la zona personal", ex);
			throw new TramitacioPluginException("Error al crear expedient a la zona personal", ex);
		}
	}

	public void publicarEvent(
			PublicarEventRequest request) throws TramitacioPluginException {
		try {
			Event event = request.getEvent();
			if (event != null) {
				PublicarEventoRequest req = new PublicarEventoRequest();
				req.setUnidadAdministrativa(request.getUnitatAdministrativa());
				req.setIdentificadorExpediente(request.getExpedientIdentificador());
				req.setClaveExpediente(request.getExpedientClau());
				req.setEvento(toEvento(event));
				getTramitacioClient().publicarEvento(req);
			} else {
				throw new TramitacioPluginException("Error crear event: l'event es null");
			}
		} catch (Exception ex) {
			logger.error("Error crear event a la zona personal", ex);
			throw new TramitacioPluginException("Error crear event a la zona personal", ex);
		}
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

	public DadesTramit obtenirDadesTramit(ObtenirDadesTramitRequest request) throws TramitacioPluginException {
		throw new TramitacioPluginException("Error al obtenir dades del tràmit: petició no suportada");
	}

	public void comunicarResultatProcesTramit(ResultatProcesTramitRequest request) throws TramitacioPluginException {
		throw new TramitacioPluginException("Error al comunicar resultat de procés del tràmit: petició no suportada");
	}

	public DadesVistaDocument obtenirVistaDocument(ObtenirVistaDocumentRequest request) throws TramitacioPluginException {
		try {
			ObtenerVistaDocumentoRequest req = new ObtenerVistaDocumentoRequest();
			req.setReferenciaDocumento(request.getReferenciaGD());
			ObtenerVistaDocumentoResponse response = getGestorDocumentalClient().obtenerVistaDocumento(req);
			DadesVistaDocument resposta = new DadesVistaDocument();
			resposta.setNom(response.getNombreVista());
			resposta.setArxiuNom(response.getNombreVista());
			resposta.setArxiuContingut(response.getContenidoVista());
			return resposta;
		} catch (Exception ex) {
			logger.error("Error al obtenir la vista del document " + request, ex);
			throw new TramitacioPluginException("Error al obtenir la vista del document " + request, ex);
		}
	}



	private EventoExpediente toEvento(Event event) {
		EventoExpediente evento = new EventoExpediente();
		evento.setTitulo(event.getTitol());
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
				documento.setNombreDocumento(document.getNom());
				documento.setNombreFichero(
						new JAXBElement<String>(
								new QName("nombreFichero"),
								String.class,
								document.getArxiuNom()));
				documento.setContenidoFichero(
						new JAXBElement<byte[]>(
								new QName("contenidoFichero"),
								byte[].class,
								document.getArxiuContingut()));
				documento.setTipoDocumento(TypeDocumentoExpediente.BIN);
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

	private ServicioTramitacionPortType getTramitacioClient() {
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
				ServicioTramitacionPortType.class,
				url,
				userName,
				password,
				getWsClientAuthType(),
				isWsClientGenerateTimestamp(),
				isWsClientLogCalls(),
				isWsClientDisableCnCheck(),
				null);
		return (ServicioTramitacionPortType)wsClientProxy;
	}

	private ServicioGestorDocumentalPortType getGestorDocumentalClient() {
		String url = GlobalProperties.getInstance().getProperty("app.bantel.entrades.url");
		if (url == null)
			url = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.documents.url");
		String userName = GlobalProperties.getInstance().getProperty("app.bantel.entrades.username");
		if (userName == null)
			userName = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.documents.username");
		String password = GlobalProperties.getInstance().getProperty("app.bantel.entrades.password");
		if (password == null)
			password = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.documents.password");
		Object wsClientProxy = WsClientUtils.getWsClientProxy(
				ServicioGestorDocumentalPortType.class,
				url,
				userName,
				password,
				getWsClientAuthType(),
				isWsClientGenerateTimestamp(),
				isWsClientLogCalls(),
				isWsClientDisableCnCheck(),
				null);
		return (ServicioGestorDocumentalPortType)wsClientProxy;
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

	private static final Log logger = LogFactory.getLog(TramitacioPluginEsbCim.class);

}
