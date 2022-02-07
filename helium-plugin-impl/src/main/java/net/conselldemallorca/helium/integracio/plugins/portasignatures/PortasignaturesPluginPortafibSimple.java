package net.conselldemallorca.helium.integracio.plugins.portasignatures;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.fundaciobit.apisib.apifirmaasyncsimple.v2.ApiFirmaAsyncSimple;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleAnnex;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleDocumentTypeInformation;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleFile;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignature;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignatureBlock;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignatureRequestInfo;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignatureRequestWithSignBlockList;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignedFile;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSigner;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.jersey.ApiFirmaAsyncSimpleJersey;

import es.caib.portafib.ws.api.v1.WsI18NException;
import net.conselldemallorca.helium.core.util.GlobalProperties;

/**
 * Implementació del plugin de portasignatures per l'API REST Simple del PortaFIB.
 * Data: 31/01/2022
 * Després de la implementació per passarela WS SOAP es crea el plugin
 * per a les peticions per API REST i callback per API REST.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortasignaturesPluginPortafibSimple implements PortasignaturesPlugin {

	@Override
	public Integer uploadDocument(
			DocumentPortasignatures document, 
			List<DocumentPortasignatures> annexos,
			boolean isSignarAnnexos, 
			PasSignatura[] passesSignatura, 
			String remitent, 
			String importancia,
			Date dataLimit) throws PortasignaturesPluginException {

		try {
			long peticioDeFirmaId = 0;
			FirmaAsyncSimpleSignatureRequestWithSignBlockList signatureRequest = new FirmaAsyncSimpleSignatureRequestWithSignBlockList();

			signatureRequest.setTitle(document.getTitol());
			signatureRequest.setDescription(document.getDescripcio());
			signatureRequest.setReason("Firma de document");
			signatureRequest.setSenderName(remitent);
			if (importancia != null) {
				if ("hight".equals(importancia)) {
					signatureRequest.setPriority(9);
				} else if ("low".equals(importancia)) {
					signatureRequest.setPriority(0);
				} else {
					// normal
					signatureRequest.setPriority(5);
				}
			}
			if (dataLimit != null) {
				signatureRequest.setAdditionalInformation("Data límit: " + new SimpleDateFormat("dd/MM/yyyy").format(dataLimit));
			} else {
				signatureRequest.setAdditionalInformation(null);
			}

			signatureRequest.setFileToSign(toFirmaAsyncSimpleFile(document));

			if (document.getTipus() != null) {
				signatureRequest.setDocumentType(
						new Long(document.getTipus().intValue()).longValue());
			} else {
				throw new PortasignaturesPluginException(
						"El tipus de document not pot estar buit." +
						" Els tipus permesos son: " + getDocumentTipusStr());
			}
			signatureRequest.setLanguageUI("ca");
			signatureRequest.setLanguageDoc("ca");
			signatureRequest.setProfileCode(getPerfil());
			
			if (annexos != null) {
				List<FirmaAsyncSimpleAnnex> portafirmesAnnexos = new ArrayList<FirmaAsyncSimpleAnnex>();
				
				for (DocumentPortasignatures annex : annexos) {
					FirmaAsyncSimpleAnnex portafirmesAnnex = new FirmaAsyncSimpleAnnex();
					portafirmesAnnex.setAnnex(toFirmaAsyncSimpleFile(annex));
					portafirmesAnnex.setAttach(false);
					portafirmesAnnex.setSign(false);
					portafirmesAnnexos.add(portafirmesAnnex);
				}
				signatureRequest.setAnnexs(portafirmesAnnexos);
			}
			FirmaAsyncSimpleSignatureBlock[] signatureBlocks  = toFluxDeFirmes(
					Arrays.asList(passesSignatura));
			signatureRequest.setSignatureBlocks(signatureBlocks);

			peticioDeFirmaId = getFirmaAsyncSimpleApi().createAndStartSignatureRequestWithSignBlockList(signatureRequest);
			return new Long(peticioDeFirmaId).intValue();
		} catch (Exception ex) {
			throw new PortasignaturesPluginException(
					"No s'ha pogut pujar el document al portafirmes (" +
					"titol=" + document.getTitol() + ", " +
					"descripcio=" + document.getDescripcio() + ", " +
					"arxiuNom=" + document.getArxiuNom() + ")" +
					" error= " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public List<byte[]> obtenirSignaturesDocument(Integer documentId) throws PortasignaturesPluginException {
		
		List<byte[]> contingut = new ArrayList<byte[]>();
		try {
			FirmaAsyncSimpleSignatureRequestInfo requestInfo = new FirmaAsyncSimpleSignatureRequestInfo(new Long(documentId).longValue(), "ca");
			FirmaAsyncSimpleSignedFile fitxerDescarregat = getFirmaAsyncSimpleApi().getSignedFileOfSignatureRequest(requestInfo);
			contingut.add(fitxerDescarregat.getSignedFile().getData());			
			return contingut;
		} catch (Exception ex) {
			throw new PortasignaturesPluginException(
					"No s'ha pogut descarregar el document del portafirmes (documentId=" + documentId + ")",
					ex);
		}
	}

	@Override
	public void deleteDocuments(Integer document) throws PortasignaturesPluginException {
		try {
			FirmaAsyncSimpleSignatureRequestInfo requestInfo = new FirmaAsyncSimpleSignatureRequestInfo(new Long(document).longValue(), "ca");
			getFirmaAsyncSimpleApi().deleteSignatureRequest(requestInfo);
		} catch (Exception ex) {
			throw new PortasignaturesPluginException(
					"No s'ha pogut esborrar el document del portafirmes (id=" + document + ")",
					ex);
		}
	}


	private ApiFirmaAsyncSimple getFirmaAsyncSimpleApi() throws MalformedURLException {
		String apiRestUrl = getBaseUrl() + "/common/rest/apifirmaasyncsimple/v2";
		ApiFirmaAsyncSimple api = new ApiFirmaAsyncSimpleJersey(
				apiRestUrl,
				getUserName(),
				getPassword());
		return api;
	}

	private String getBaseUrl() {
		return (String)GlobalProperties.getInstance().getProperty(
				"app.portasignatures.plugin.portafib.base.url");
	}

	private String getUserName() {
		return GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.portafib.username");
	}
	private String getPassword() {
		return GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.portafib.password");
	}
	private String getPerfil() {
		return GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.portafib.perfil");
	}

	private FirmaAsyncSimpleFile toFirmaAsyncSimpleFile(
			DocumentPortasignatures document) throws Exception {
		if (!document.getArxiuNom().endsWith(".pdf")) {
			throw new PortasignaturesPluginException(
					"Els arxius per firmar han de ser de tipus PDF");
		}
		FirmaAsyncSimpleFile fitxer = new FirmaAsyncSimpleFile();
		fitxer.setNom(document.getArxiuNom());
		fitxer.setMime("application/pdf");
		fitxer.setData(document.getArxiuContingut());
		return fitxer;
	}


	public String getDocumentTipusStr() throws MalformedURLException, WsI18NException {
		StringBuilder sb = new StringBuilder();
		
		try {
			List<FirmaAsyncSimpleDocumentTypeInformation> tipusLlistat = getFirmaAsyncSimpleApi().getAvailableTypesOfDocuments("ca");
			for (FirmaAsyncSimpleDocumentTypeInformation tipusDocumentWs: tipusLlistat) {
				sb.append("[tipusId=");
				sb.append(tipusDocumentWs.getDocumentType());
				sb.append(", nom=");
				sb.append(tipusDocumentWs.getName());
				sb.append("]");
			}
		} catch (Exception ex) {
			sb.append("[No s'han pogut consultar els diferents tipus de documents. Error: " + ex.getMessage() + "]");
		}
		return sb.toString();
	}
	
	private FirmaAsyncSimpleSignatureBlock[] toFluxDeFirmes(
			List<PasSignatura> passes) throws Exception {
		FirmaAsyncSimpleSignatureBlock[] blocsAsyncs = null;
		if (passes == null || passes.isEmpty()) {
			throw new PortasignaturesPluginException(
					"És necessari configurar algun responsable de firmar el document");
		}
		try {
			blocsAsyncs = new FirmaAsyncSimpleSignatureBlock[passes.size()];
			for (int i = 0; i < passes.size(); i++) {
				PasSignatura pas = passes.get(i);
				FirmaAsyncSimpleSignatureBlock blocAsync = new FirmaAsyncSimpleSignatureBlock();
				
				blocAsync.setMinimumNumberOfSignaturesRequired(pas.getMinSignataris());
			    String signatari;
				List<FirmaAsyncSimpleSignature> signatures = new ArrayList<FirmaAsyncSimpleSignature>();
			    for (int j = 0; j<pas.getSignataris().length; j++) {
					FirmaAsyncSimpleSignature signature = new FirmaAsyncSimpleSignature();
					signature.setRequired(true);
					
			    	signatari = pas.getSignataris()[j];
					//Firmant
					FirmaAsyncSimpleSigner signer = new FirmaAsyncSimpleSigner();
					if (signatari.startsWith("CARREC")) {
						String carrecName = signatari.substring(signatari.indexOf("[") + 1, signatari.indexOf("]"));
						signer.setPositionInTheCompany(carrecName);
					} else {
						signer.setUsername(signatari);
					}
					signature.setSigner(signer);
					signatures.add(signature);
			    }
				blocAsync.setSigners(signatures);
				blocsAsyncs[i] = blocAsync;
			}
		} catch (Exception e) {
			throw new PortasignaturesPluginException("Hi ha hagut un error construint el flux", e);
		}
		return blocsAsyncs;
	}


}
