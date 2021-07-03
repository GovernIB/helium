package net.conselldemallorca.helium.integracio.plugins.unitat;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import es.caib.dir3caib.ws.api.unidad.Dir3CaibObtenerUnidadesWs;
import es.caib.dir3caib.ws.api.unidad.Dir3CaibObtenerUnidadesWsService;
import es.caib.dir3caib.ws.api.unidad.UnidadTF;
import es.caib.helium.logic.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.SistemaExternException;


/**
 * Implementació de proves del plugin d'unitats organitzatives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UnitatsOrganiquesPluginDir3 implements UnitatsOrganiquesPlugin {

	@Override
	public List<UnitatOrganica> findAmbPare(String pareCodi) throws SistemaExternException {
		try {
			UnidadTF unidadPare = getObtenerUnidadesService().obtenerUnidad(
					pareCodi,
					null,
					null);
			if (unidadPare != null) {
				List<UnitatOrganica> unitats = new ArrayList<UnitatOrganica>();
				List<UnidadTF> unidades = getObtenerUnidadesService().obtenerArbolUnidades(
						pareCodi,
						null,
						null);//df.format(new Date()));
				if (unidades != null) {
					unidades.add(0, unidadPare);
					for (UnidadTF unidad: unidades) {
						if ("V".equalsIgnoreCase(unidad.getCodigoEstadoEntidad())) {
							unitats.add(toUnitatOrganitzativa(unidad));
						}
					}
				} else {
					unitats.add(toUnitatOrganitzativa(unidadPare));
				}
				return unitats;
			} else {
				throw new SistemaExternException(
						"No s'han trobat la unitat pare (pareCodi=" + pareCodi + ")");
			}
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar les unitats organitzatives via WS (" +
					"pareCodi=" + pareCodi + ")",
					ex);
		}
	}

	@Override
	public UnitatOrganica findAmbCodi(String codi) throws SistemaExternException {
		try {
			UnitatOrganica unitat = null;
			UnidadTF unidad = getObtenerUnidadesService().obtenerUnidad(
					codi,
					null,
					null);
			if (unidad != null && "V".equalsIgnoreCase(unidad.getCodigoEstadoEntidad())) {
				unitat = toUnitatOrganitzativa(unidad);
			} else {
				throw new SistemaExternException(
						"La unitat organitzativa no està vigent (" +
						"codi=" + codi + ")");
			}
			return unitat;
		} catch (SistemaExternException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut consultar la unitat organitzativa (" +
					"codi=" + codi + ")",
					ex);
		}
	}

	public List<UnitatOrganica> cercaUnitats(
			String codi, 
			String denominacio,
			Long nivellAdministracio, 
			Long comunitatAutonoma, 
			Boolean ambOficines, 
			Boolean esUnitatArrel,
			Long provincia, 
			String municipi) throws SistemaExternException {
		List<UnitatOrganica> unitats = new ArrayList<UnitatOrganica>();
		try {
			URL url = new URL(getServiceCercaUrl()
					+ "?codigo=" + codi
					+ "&denominacion=" + denominacio
					+ "&codNivelAdministracion=" + (nivellAdministracio != null ? nivellAdministracio : "-1")
					+ "&codComunidadAutonoma=" + (comunitatAutonoma != null ? comunitatAutonoma : "-1")
					+ "&conOficinas=" + (ambOficines != null && ambOficines ? "true" : "false")
					+ "&unidadRaiz=" + (esUnitatArrel != null && esUnitatArrel ? "true" : "false")
					+ "&provincia="+ (provincia != null ? provincia : "-1")
					+ "&localidad=" + (municipi != null ? municipi : "-1")
					+ "&vigentes=true");
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			unitats = mapper.readValue(
					httpConnection.getInputStream(), 
					TypeFactory.defaultInstance().constructCollectionType(
							List.class,  
							UnitatOrganica.class));
			Collections.sort(unitats);
			return unitats;
		} catch ( JsonMappingException e) {
			 // No results
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar les unitats organitzatives via REST (" +
					"codi=" + codi + ", " +
					"denominacio=" + denominacio + ", " +
					"nivellAdministracio=" + nivellAdministracio + ", " +
					"comunitatAutonoma=" + comunitatAutonoma + ", " +
					"ambOficines=" + ambOficines + ", " +
					"esUnitatArrel=" + esUnitatArrel + ", " +
					"provincia=" + provincia + ", " +
					"municipi=" + municipi + ")",
					ex);
		}
		return unitats;
	}



	private Dir3CaibObtenerUnidadesWs getObtenerUnidadesService() throws MalformedURLException {
		Dir3CaibObtenerUnidadesWs client = null;
		URL url = new URL(getServiceUrl() + "?wsdl");
		Dir3CaibObtenerUnidadesWsService service = new Dir3CaibObtenerUnidadesWsService(
				url,
				new QName(
						"http://unidad.ws.dir3caib.caib.es/",
						"Dir3CaibObtenerUnidadesWsService"));
		client = service.getDir3CaibObtenerUnidadesWs();
		BindingProvider bp = (BindingProvider)client;
		bp.getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				getServiceUrl());
		String username = getServiceUsername();
		if (username != null && !username.isEmpty()) {
			bp.getRequestContext().put(
					BindingProvider.USERNAME_PROPERTY,
					username);
			bp.getRequestContext().put(
					BindingProvider.PASSWORD_PROPERTY,
					getServicePassword());
		}
		if (isLogMissatgesActiu()) {
			@SuppressWarnings("rawtypes")
			List<Handler> handlerChain = new ArrayList<Handler>();
			handlerChain.add(new LogMessageHandler());
			bp.getBinding().setHandlerChain(handlerChain);
		}
		Integer connectTimeout = getServiceTimeout();
		if (connectTimeout != null) {
			bp.getRequestContext().put(
					"org.jboss.ws.timeout",
					connectTimeout);
		}
		return client;
	}

	private UnitatOrganica toUnitatOrganitzativa(UnidadTF unidad) {
		UnitatOrganica unitat = new UnitatOrganica(
				unidad.getCodigo(),
				unidad.getDenominacion(),
				unidad.getCodigo(), // CifNif
				unidad.getFechaAltaOficial(),
				unidad.getCodigoEstadoEntidad(),
				unidad.getCodUnidadSuperior(),
				unidad.getCodUnidadRaiz(),
				unidad.getCodigoAmbPais(),
				unidad.getCodAmbComunidad(),
				unidad.getCodAmbProvincia(),
				unidad.getCodPostal(),
				unidad.getDescripcionLocalidad(),
				unidad.getCodigoTipoVia(), 
				unidad.getNombreVia(), 
				unidad.getNumVia());
		return unitat;
	}

	private class LogMessageHandler implements SOAPHandler<SOAPMessageContext> {
		public boolean handleMessage(SOAPMessageContext messageContext) {
			log(messageContext);
			return true;
		}
		public Set<QName> getHeaders() {
			return Collections.emptySet();
		}
		public boolean handleFault(SOAPMessageContext messageContext) {
			log(messageContext);
			return true;
		}
		public void close(MessageContext context) {
		}
		private void log(SOAPMessageContext messageContext) {
			SOAPMessage msg = messageContext.getMessage();
			try {
				Boolean outboundProperty = (Boolean)messageContext.get(
						MessageContext.MESSAGE_OUTBOUND_PROPERTY);
				if (outboundProperty)
					System.out.print("Missatge SOAP petició: ");
				else
					System.out.print("Missatge SOAP resposta: ");
				msg.writeTo(System.out);
				System.out.println();
			} catch (SOAPException ex) {
				Logger.getLogger(LogMessageHandler.class.getName()).log(
						Level.SEVERE,
						null,
						ex);
			} catch (IOException ex) {
				Logger.getLogger(LogMessageHandler.class.getName()).log(
						Level.SEVERE,
						null,
						ex);
			}
		}
	}
	
	private String getServiceUrl() {
		return GlobalProperties.getInstance().getProperty(
				"app.unitats.organiques.dir3.plugin.service.url");
	}
	private String getServiceUsername() {
		return GlobalProperties.getInstance().getProperty(
				"app.unitats.organiques.dir3.plugin.service.username");
	}
	private String getServicePassword() {
		return GlobalProperties.getInstance().getProperty(
				"app.unitats.organiques.dir3.plugin.service.password");
	}
	private boolean isLogMissatgesActiu() {
		return GlobalProperties.getInstance().getAsBoolean(
				"app.unitats.organiques.dir3.plugin.service.log.actiu");
	}
	private Integer getServiceTimeout() {
		String key = "app.unitats.organiques.dir3.plugin.service.timeout";
		if (GlobalProperties.getInstance().getProperty(key) != null)
			return GlobalProperties.getInstance().getAsInt(key);
		else
			return null;
	}
	private String getServiceCercaUrl() {
		String serviceUrl = GlobalProperties.getInstance().getProperty(
				"app.unitats.organiques.dir3.plugin.service.url");
		if (serviceUrl == null) {
			serviceUrl = GlobalProperties.getInstance().getProperty(
					"app.unitats.organiques.dir3.plugin.service.url");
		}
		return serviceUrl;
	}

}
