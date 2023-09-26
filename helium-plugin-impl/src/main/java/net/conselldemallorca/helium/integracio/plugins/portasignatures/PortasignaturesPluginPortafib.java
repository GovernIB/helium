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

import es.caib.portafib.ws.api.v1.AnnexBean;
import es.caib.portafib.ws.api.v1.BlocDeFirmesWs;
import es.caib.portafib.ws.api.v1.FirmaBean;
import es.caib.portafib.ws.api.v1.FitxerBean;
import es.caib.portafib.ws.api.v1.FluxDeFirmesWs;
import es.caib.portafib.ws.api.v1.PeticioDeFirmaWs;
import es.caib.portafib.ws.api.v1.PortaFIBPeticioDeFirmaWs;
import es.caib.portafib.ws.api.v1.PortaFIBPeticioDeFirmaWsService;
import es.caib.portafib.ws.api.v1.PortaFIBUsuariEntitatWs;
import es.caib.portafib.ws.api.v1.PortaFIBUsuariEntitatWsService;
import es.caib.portafib.ws.api.v1.TipusDocumentInfoWs;
import es.caib.portafib.ws.api.v1.WsI18NException;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.OpenOfficeUtils;
import net.conselldemallorca.helium.integracio.plugins.SistemaExternException;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl.ImportanceEnum;

/**
 * Implementació del plugin de portasignatures per la CAIB.
 * @deprecated Aquesta implementació és per invocar el WS SOAP que ja no s'ha d'utilitzar.
 * @author Limit Tecnologies <limit@limit.es>
 */
@Deprecated
public class PortasignaturesPluginPortafib implements PortasignaturesPlugin {

	private OpenOfficeUtils openOfficeUtils;

	public Integer uploadDocument (
			DocumentPortasignatures document,
			List<DocumentPortasignatures> annexos,
			boolean isSignarAnnexos,
			List<PortafirmesFluxBloc> blocList,
			String remitent,
			String importancia,
			Date dataLimit,
			String fluxId) throws PortasignaturesPluginException {
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
			requestPeticioDeFirmaWs.setRemitentNom(remitent);
			requestPeticioDeFirmaWs.setDescripcio(document.getDescripcio());
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
							blocList,
							null));
			requestPeticioDeFirmaWs.setFitxerAFirmar(
					toFitxerBean(document));
			
			requestPeticioDeFirmaWs.setModeDeFirma(
					new Boolean(false));
			requestPeticioDeFirmaWs.setIdiomaID("ca");

			// Afegeix els annexos
			if (annexos != null) {
				AnnexBean a;
				FitxerBean f;
				for (DocumentPortasignatures annex: annexos) {
					a = new AnnexBean();
					a.setAdjuntar(false);
					a.setFirmar(isSignarAnnexos);
					f = new FitxerBean();
					f.setNom(annex.getArxiuNom());
					f.setDescripcio(annex.getTitol());
					f.setMime(getOpenOfficeUtils().getArxiuMimeType(annex.getArxiuNom()));
					f.setData(annex.getArxiuContingut());
					f.setTamany(annex.getArxiuContingut().length);
					a.setFitxer(f);
					requestPeticioDeFirmaWs.getAnnexs().add(a);
				}
			}

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
			Integer document) throws PortasignaturesPluginException {
		if (document == null) {
			throw new PortasignaturesPluginException(
					"S'ha d'especificar un únic document a esborrar");
		}
		Long portafirmesId = new Long(document).longValue();
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
			List<PortafirmesFluxBloc> blocs,
			String plantillaFluxId) throws Exception {
		if (blocs == null && plantillaFluxId == null) {
			throw new PortasignaturesPluginException(
					"És necessari configurar algun responsable de firmar el document");
		}
		FluxDeFirmesWs fluxWs;
		if (plantillaFluxId != null && blocs == null) {
			fluxWs = getPeticioDeFirmaWs().instantiatePlantillaFluxDeFirmes(
					new Long(plantillaFluxId).longValue());
		} else {
			PortaFIBUsuariEntitatWs usuariEntitatAPI = getUsuariEntitatWs();
			fluxWs = new FluxDeFirmesWs();
			fluxWs.setNom("Flux Helium " + System.nanoTime());

			PortafirmesFluxBloc fluxBloc;
		    BlocDeFirmesWs bloc;
		    String signatari;
		    String usuariEntitat;
			for(int i = 0; i < blocs.size(); i++) {
				fluxBloc = blocs.get(i);
				bloc = new BlocDeFirmesWs();
			    bloc.setMinimDeFirmes(fluxBloc.getMinSignataris());
			    bloc.setOrdre(i);
			    for (int j = 0; j<fluxBloc.getDestinataris().length; j++) {
			    	signatari = fluxBloc.getDestinataris()[j];
			    	// Cercar usuariEntitat associat al nif
			    	usuariEntitat = usuariEntitatAPI.getUsuariEntitatIDInMyEntitatByAdministrationID(signatari);
				    FirmaBean firma = new FirmaBean();
				    firma.setDestinatariID(usuariEntitat);
				    bloc.getFirmes().add(firma);
			    }
			    fluxWs.getBlocsDeFirmes().add(bloc);
			}
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
	
	private OpenOfficeUtils getOpenOfficeUtils() {
		if (openOfficeUtils == null) {
			openOfficeUtils = new OpenOfficeUtils();
		}
		return openOfficeUtils;
	}

	@Override
	public PortafirmesIniciFluxResposta iniciarFluxDeFirma(String idioma, boolean isPlantilla, String nom,
			String descripcio, boolean descripcioVisible, String returnUrl) throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PortafirmesFluxResposta recuperarFluxDeFirmaByIdTransaccio(String idTransaccio)
			throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PortafirmesFluxResposta> recuperarPlantillesDisponibles(String idioma) throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PortafirmesFluxInfo recuperarFluxDeFirmaByIdPlantilla(String idTransaccio, String idioma)
			throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String recuperarUrlViewEditPlantilla(String idPlantilla, String idioma, String urlReturn, boolean edicio)
			throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean esborrarPlantillaFirma(String idioma, String plantillaFluxId) throws SistemaExternException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void tancarTransaccioFlux(String idTransaccio) throws SistemaExternException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<PortafirmesFluxResposta> recuperarPlantillesPerFiltre(String idioma, String descripcio)
			throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String recuperarUrlViewEstatFluxDeFirmes(long portafirmesId, String idioma) throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PortafirmesCarrec> recuperarCarrecs() throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PortafirmesCarrec recuperarCarrec(String carrecId) throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}


}
