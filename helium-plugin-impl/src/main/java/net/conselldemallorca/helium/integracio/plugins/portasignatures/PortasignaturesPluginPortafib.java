package net.conselldemallorca.helium.integracio.plugins.portasignatures;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.commons.lang.StringUtils;

import es.caib.portafib.ws.api.v1.FitxerBean;
import es.caib.portafib.ws.api.v1.FluxDeFirmesWs;
import es.caib.portafib.ws.api.v1.PeticioDeFirmaWs;
import es.caib.portafib.ws.api.v1.PortaFIBPeticioDeFirmaWs;
import es.caib.portafib.ws.api.v1.PortaFIBPeticioDeFirmaWsService;
import es.caib.portafib.ws.api.v1.PortaFIBUsuariEntitatWs;
import es.caib.portafib.ws.api.v1.PortaFIBUsuariEntitatWsService;
import es.caib.portafib.ws.api.v1.TipusDocumentInfoWs;
import es.caib.portafib.ws.api.v1.WsI18NException;
import es.caib.portafib.ws.api.v1.utils.PeticioDeFirmaUtils;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl.ImportanceEnum;

/**
 * Implementació del plugin de portasignatures per la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortasignaturesPluginPortafib implements PortasignaturesPlugin {

	public Integer uploadDocument (
			DocumentPortasignatures document,
			List<DocumentPortasignatures> annexos,
			boolean isSignarAnnexos,
			PasSignatura[] passesSignatura,
			String remitent,
			String importancia,
			Date dataLimit) throws PortasignaturesPluginException {
		try {
			PeticioDeFirmaWs requestPeticioDeFirmaWs = new PeticioDeFirmaWs();
			requestPeticioDeFirmaWs.setTitol(
					StringUtils.abbreviate(document.getTitol(), 100));
			/*requestPeticioDeFirmaWs.setDescripcio(
					document.getDescripcio());*/
			requestPeticioDeFirmaWs.setMotiu("Firma de document");
			if (document.getTipus() != null) {
				requestPeticioDeFirmaWs.setTipusDocumentID(
						new Long(document.getTipus().intValue()).longValue());
			} else {
				throw new PortasignaturesPluginException(
						"El tipus de document not pot estar buit." +
						" Els tipus permesos son: " + getDocumentTipusStr());
			}
			// requestPeticioDeFirmaWs.setMotiu("Firma de document HELIUM");
			requestPeticioDeFirmaWs.setRemitentNom(remitent);
			if (importancia != null) {
				ImportanceEnum importance = ImportanceEnum.fromString(importancia);
				if (ImportanceEnum.low.equals(importance)) {
					requestPeticioDeFirmaWs.setPrioritatID(0);
				} else if (ImportanceEnum.normal.equals(importance)) {
					requestPeticioDeFirmaWs.setPrioritatID(5);
				} else if (ImportanceEnum.high.equals(importance)) {
					requestPeticioDeFirmaWs.setPrioritatID(9);
				}
			}
			if (dataLimit != null) {
				GregorianCalendar gcal = new GregorianCalendar();
				gcal.setTime(dataLimit);
				DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
				requestPeticioDeFirmaWs.setDataCaducitat(
						new java.sql.Timestamp(dataLimit.getTime()));
			} else {
				GregorianCalendar gcal = new GregorianCalendar();
				gcal.add(Calendar.DAY_OF_MONTH, 5);
				DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
				requestPeticioDeFirmaWs.setDataCaducitat(
						new java.sql.Timestamp(gcal.getTime().getTime()));
			}
			requestPeticioDeFirmaWs.setFluxDeFirmes(
					toFluxDeFirmes(
							Arrays.asList(passesSignatura),
							null));
			requestPeticioDeFirmaWs.setFitxerAFirmar(
					toFitxerBean(document));
			
			requestPeticioDeFirmaWs.setModeDeFirma(
					new Boolean(false));
			requestPeticioDeFirmaWs.setIdiomaID("ca");
			PeticioDeFirmaWs responsePeticioDeFirmaWs = getPeticioDeFirmaWs().createAndStartPeticioDeFirma(
					requestPeticioDeFirmaWs);
			return new Long(responsePeticioDeFirmaWs.getPeticioDeFirmaID()).intValue();
		} catch (Exception ex) {
			if (ex instanceof PortasignaturesPluginException) {
				throw (PortasignaturesPluginException)ex;
			} else {
				throw new PortasignaturesPluginException(
						"Error al enviar el document al portasignatures",
						ex);
			}
		}
	}

	public void deleteDocuments (
			List<Integer> documents) throws PortasignaturesPluginException {
		if (documents == null || documents.size() != 1) {
			throw new PortasignaturesPluginException(
					"S'ha d'especificar un únic document a esborrar");
		}
		Long portafirmesId = new Long(documents.get(0)).longValue();
		try {
			getPeticioDeFirmaWs().deletePeticioDeFirma(portafirmesId);
		} catch (Exception ex) {
			throw new PortasignaturesPluginException(
					"No s'ha pogut esborrar el document del portafirmes (id=" + portafirmesId + ")",
					ex);
		}
	}

	public List<byte[]> obtenirSignaturesDocument(
			Integer portafirmesId) throws PortasignaturesPluginException {
		try {
			FitxerBean fitxerFirmat = getPeticioDeFirmaWs().getLastSignedFileOfPeticioDeFirma(
					new Long(portafirmesId).longValue());
			FitxerBean fitxerDescarregat = getPeticioDeFirmaWs().downloadFileUsingEncryptedFileID(
					fitxerFirmat.getEncryptedFileID());
			return Arrays.asList(fitxerDescarregat.getData());
		} catch (Exception ex) {
			throw new PortasignaturesPluginException(
					"No s'ha pogut descarregar el document del portafirmes (id=" + portafirmesId + ")",
					ex);
		}
	}



	private String getDocumentArxiuExtensio(String arxiuNom) {
		int index = arxiuNom.lastIndexOf(".");
		if (index != -1) {
			return arxiuNom.substring(index + 1);
		} else {
			return "";
		}
	}

	private FitxerBean toFitxerBean(
			DocumentPortasignatures document) throws Exception {
		String arxiuExtensio = getDocumentArxiuExtensio(document.getArxiuNom());
		if (!"pdf".equalsIgnoreCase(arxiuExtensio)) {
			throw new PortasignaturesPluginException(
					"Els arxius per firmar han de ser de tipus PDF");
		}
		FitxerBean fitxer = new FitxerBean();
		fitxer.setNom(document.getArxiuNom());
		fitxer.setMime("application/pdf");
		fitxer.setTamany(document.getArxiuContingut().length);
		fitxer.setData(document.getArxiuContingut());
		return fitxer;
	}

	private FluxDeFirmesWs toFluxDeFirmes(
			List<PasSignatura> passes,
			String plantillaFluxId) throws Exception {
		if (passes == null && plantillaFluxId == null) {
			throw new PortasignaturesPluginException(
					"És necessari configurar algun responsable de firmar el document");
		}
		FluxDeFirmesWs fluxWs;
		if (plantillaFluxId != null && passes == null) {
			fluxWs = getPeticioDeFirmaWs().instantiatePlantillaFluxDeFirmes(
					new Long(plantillaFluxId).longValue());
		} else {
			int numNifs = 0;
			if (passes.size() > 0) {
				numNifs = passes.get(0).getSignataris().length;
			}
			String[][] nifs = new String[numNifs][passes.size()];
			for (int i = 0; i < passes.size(); i++) {
				PasSignatura fluxBloc = passes.get(i);
				for (int j = 0; j < fluxBloc.getSignataris().length; j++) {
					nifs[j][i] = fluxBloc.getSignataris()[j];
				}
			}
			fluxWs = PeticioDeFirmaUtils.constructFluxDeFirmesWsUsingBlocDeFirmes(
					getUsuariEntitatWs(),
					nifs);
		}
		return fluxWs;
	}

	public String getDocumentTipusStr() throws MalformedURLException, WsI18NException {
		StringBuilder sb = new StringBuilder();
		List<TipusDocumentInfoWs> tipusLlistat = getPeticioDeFirmaWs().getTipusDeDocuments("ca");
		for (TipusDocumentInfoWs tipusDocumentWs: tipusLlistat) {
			sb.append("[tipusId=");
			sb.append(tipusDocumentWs.getTipusDocumentID());
			sb.append(", nom=");
			sb.append(tipusDocumentWs.getNom());
			sb.append("]");
		}
		return sb.toString();
	}

	private PortaFIBPeticioDeFirmaWs getPeticioDeFirmaWs() throws MalformedURLException {
		String webServiceUrl = getBaseUrl() + "/ws/v1/PortaFIBPeticioDeFirma";
		URL wsdlUrl = new URL(webServiceUrl + "?wsdl");
		PortaFIBPeticioDeFirmaWsService service = new PortaFIBPeticioDeFirmaWsService(wsdlUrl);
		PortaFIBPeticioDeFirmaWs api = service.getPortaFIBPeticioDeFirmaWs();
		BindingProvider bp = (BindingProvider)api;
		Map<String, Object> reqContext = bp.getRequestContext();
		reqContext.put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				webServiceUrl);
		reqContext.put(
				BindingProvider.USERNAME_PROPERTY,
				getUsername());
		reqContext.put(
				BindingProvider.PASSWORD_PROPERTY,
				getPassword());
		if (isLogMissatgesActiu()) {
			@SuppressWarnings("rawtypes")
			List<Handler> handlerChain = new ArrayList<Handler>();
			handlerChain.add(new LogMessageHandler());
			bp.getBinding().setHandlerChain(handlerChain);
		}
		return api;
	}

	private PortaFIBUsuariEntitatWs getUsuariEntitatWs() throws MalformedURLException {
		String webServiceUrl = getBaseUrl() + "/ws/v1/PortaFIBUsuariEntitat";
		URL wsdlUrl = new URL(webServiceUrl + "?wsdl");
		PortaFIBUsuariEntitatWsService service = new PortaFIBUsuariEntitatWsService(wsdlUrl);
		PortaFIBUsuariEntitatWs api = service.getPortaFIBUsuariEntitatWs();
		BindingProvider bp = (BindingProvider)api;
		Map<String, Object> reqContext = bp.getRequestContext();
		reqContext.put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				webServiceUrl);
		reqContext.put(
				BindingProvider.USERNAME_PROPERTY,
				getUsername());
		reqContext.put(
				BindingProvider.PASSWORD_PROPERTY,
				getPassword());
		if (isLogMissatgesActiu()) {
			@SuppressWarnings("rawtypes")
			List<Handler> handlerChain = new ArrayList<Handler>();
			handlerChain.add(new LogMessageHandler());
			bp.getBinding().setHandlerChain(handlerChain);
		}
		return api;
	}

	private String getBaseUrl() {
		return GlobalProperties.getInstance().getProperty(
				"app.portasignatures.plugin.portafib.base.url");
	}
	private String getUsername() {
		return GlobalProperties.getInstance().getProperty(
				"app.portasignatures.plugin.portafib.username");
	}
	private String getPassword() {
		return GlobalProperties.getInstance().getProperty(
				"app.portasignatures.plugin.portafib.password");
	}
	private boolean isLogMissatgesActiu() {
		String logActiu = GlobalProperties.getInstance().getProperty(
				"app.portasignatures.plugin.portafib.log.actiu");
		return "true".equalsIgnoreCase(logActiu);
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

}