package es.caib.helium.integracio.service.unitat;

import java.io.IOException;
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

import org.springframework.stereotype.Service;

import es.caib.dir3caib.ws.api.unidad.Dir3CaibObtenerUnidadesWs;
import es.caib.dir3caib.ws.api.unidad.Dir3CaibObtenerUnidadesWsService;
import es.caib.dir3caib.ws.api.unidad.UnidadTF;
import es.caib.helium.integracio.domini.unitat.UnitatOrganica;
import es.caib.helium.integracio.excepcions.unitat.UnitatOrganicaException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UnitatsOrganiquesServiceDir3Impl implements UnitatsOrganiquesService {

	private Dir3CaibObtenerUnidadesWs client;
	
	@Override
	public List<UnitatOrganica> findAmbPare(String pareCodi) throws UnitatOrganicaException {
		try {
			UnidadTF unidadPare = client.obtenerUnidad(pareCodi, null, null);
			if (unidadPare != null) {
				List<UnitatOrganica> unitats = new ArrayList<UnitatOrganica>();
				List<UnidadTF> unidades = client.obtenerArbolUnidades(pareCodi, null, null);//df.format(new Date()));
				if (unidades != null) {
					unidades.add(0, unidadPare);
					for (var unidad: unidades) {
						if ("V".equalsIgnoreCase(unidad.getCodigoEstadoEntidad())) {
							unitats.add(toUnitatOrganitzativa(unidad));
						}
					}
				} else {
					unitats.add(toUnitatOrganitzativa(unidadPare));
				}
				return unitats;
			} else {
				throw new UnitatOrganicaException("No s'han trobat la unitat pare (pareCodi=" + pareCodi + ")");
			}
		} catch (Exception ex) {
			throw new UnitatOrganicaException("No s'han pogut consultar les unitats organitzatives via WS (" + "pareCodi=" + pareCodi + ")",	ex);
		}
	}
	
	@Override
	public UnitatOrganica consultaUnitat(String codi) throws UnitatOrganicaException {
		try {
			var unidad = client.obtenerUnidad(codi, null, 	null);
			if (unidad == null ||  !"V".equalsIgnoreCase(unidad.getCodigoEstadoEntidad())) {
				log.error("La unitat organitzativa no està vigent (" + "codi=" + codi + ")");
				return null;
			} 
			return toUnitatOrganitzativa(unidad);
		} catch (Exception ex) {
			throw new UnitatOrganicaException(
					"No s'ha pogut consultar la unitat organitzativa (" + "codi=" + codi + ")",	ex);
		}
	}
	
	public void crearClient(String urlParcial, String username, String password, Integer timeout, boolean logMissatgeActiu) throws MalformedURLException {
		
		URL url = new URL(urlParcial + "?wsdl");
		
		// TODO Si es posa la última versió de la dependencia peta. Sinó es posa la dependencia el Dir3CaibObtenerUnidadesWsService peta
		/*
		  	<dependency>
				<groupId>com.sun.xml.ws</groupId>
				<artifactId>rt</artifactId>
				<version>2.3.1</version>
			</dependency>
		 */	
		Dir3CaibObtenerUnidadesWsService service = new Dir3CaibObtenerUnidadesWsService(
				url,
				new QName(
						"http://unidad.ws.dir3caib.caib.es/",
						"Dir3CaibObtenerUnidadesWsService"));
		client = service.getDir3CaibObtenerUnidadesWs();
		BindingProvider bp = (BindingProvider)client;
		bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, urlParcial);
		if (username != null && !username.isEmpty()) {
			bp.getRequestContext().put(
					BindingProvider.USERNAME_PROPERTY,
					username);
			bp.getRequestContext().put(
					BindingProvider.PASSWORD_PROPERTY,
					password);
		}
		if (logMissatgeActiu) {
			@SuppressWarnings("rawtypes")
			List<Handler> handlerChain = new ArrayList<Handler>();
			handlerChain.add(new LogMessageHandler());
			bp.getBinding().setHandlerChain(handlerChain);
		}
		if (timeout != null) {
			bp.getRequestContext().put("org.jboss.ws.timeout", timeout);
		}
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
				if (outboundProperty) {
					System.out.print("Missatge SOAP petició: ");
				} else {
					System.out.print("Missatge SOAP resposta: ");
				}
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
}
