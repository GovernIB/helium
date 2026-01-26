/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.firma;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fundaciobit.apisib.apifirmasimple.v1.ApiFirmaEnServidorSimple;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleAvailableProfile;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleCommonInfo;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleFile;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleFileInfoSignature;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignDocumentRequest;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignatureResult;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignedFileInfo;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleStatus;
import org.fundaciobit.apisib.apifirmasimple.v1.jersey.ApiFirmaEnServidorSimpleJersey;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.SistemaExternException;

/**
 * Implementació del plugin de signatura emprant el portafirmes
 * de la CAIB desenvolupat per l'IBIT (PortaFIB).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FirmaPluginPortafib implements FirmaPlugin {	
	
	private static final String PROPERTIES_BASE = "app.plugin.firma.portafib.";

	@Override
	public FirmaResposta firmar(
			String id, 
			String nom, 
			String motiu, 
			byte[] contingut, 
			String mime,
			String tipusDocumental) throws SistemaExternException {
		
		FirmaResposta resposta = new FirmaResposta();

		ApiFirmaEnServidorSimple api = new ApiFirmaEnServidorSimpleJersey(
				getPropertyApiEndpoint(), 
				getPropertyApiUsername(),
				getPropertyApiPassword());
		
		logger.debug("Firma simple en servidor. URL API REST Jersey: " + getPropertyApiEndpoint());
		
		FirmaSimpleFile fileToSign = new FirmaSimpleFile(nom, mime, contingut);

		FirmaSimpleSignatureResult result;
		try {
			
//			getAvailableProfiles(api);
			String perfil = getPropertyApiPerfil();
			result = internalSignDocument(
					id,
					api,
					perfil,
					fileToSign,
					motiu,
					tipusDocumental);
			
			resposta.setContingut(result.getSignedFile().getData());
			if (result.getSignedFile() != null) {
				resposta.setNom(result.getSignedFile().getNom());
				resposta.setMime(result.getSignedFile().getMime());
			}
			if (result.getSignedFileInfo() != null) {
				resposta.setTipusFirma(result.getSignedFileInfo().getSignType());
				resposta.setTipusFirmaEni(result.getSignedFileInfo().getEniTipoFirma());
				resposta.setPerfilFirmaEni(result.getSignedFileInfo().getEniPerfilFirma());
			}
			
			return resposta;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	protected FirmaSimpleSignatureResult internalSignDocument(
			String id, 
			ApiFirmaEnServidorSimple api,
			final String perfil,
			FirmaSimpleFile fileToSign,
			String motiu,
			String tipusDocumental) throws Exception, FileNotFoundException, IOException {
		String signID = id;
		String name = fileToSign.getNom();
		String reason = motiu;
		String location = getLocationProperty();

		int signNumber = 1;
		String languageSign = "ca";
		Long tipusDocumentalID = tipusDocumental != null && !"".equals(tipusDocumental) ? Long.valueOf(tipusDocumental.substring(2)) : null;

		FirmaSimpleFileInfoSignature fileInfoSignature = new FirmaSimpleFileInfoSignature(
				fileToSign,
				signID,
				name,
				reason,
				location,
				signNumber,
				languageSign,
				tipusDocumentalID);

		String languageUI = "ca";
		String username = this.getPropertyUsername();
		String administrationID = null;
		String signerEmail = this.getSignerEmailProperty();

		FirmaSimpleCommonInfo commonInfo;
		commonInfo = new FirmaSimpleCommonInfo(perfil, languageUI, username, administrationID, signerEmail);

		logger.debug("languageUI = |" + languageUI + "|");

		FirmaSimpleSignDocumentRequest signature;
		signature = new FirmaSimpleSignDocumentRequest(commonInfo, fileInfoSignature);

		FirmaSimpleSignatureResult fullResults = api.signDocument(signature);

		FirmaSimpleStatus transactionStatus = fullResults.getStatus();

		int status = transactionStatus.getStatus();

		switch (status) {

		case FirmaSimpleStatus.STATUS_INITIALIZING: // = 0;
			throw new SistemaExternException("API de firma simple ha tornat status erroni: Initializing ...Unknown Error (???)");

		case FirmaSimpleStatus.STATUS_IN_PROGRESS: // = 1;
			throw new SistemaExternException("API de firma simple ha tornat status erroni: In PROGRESS ...Unknown Error (???)");

		case FirmaSimpleStatus.STATUS_FINAL_ERROR: // = -1;
			throw new SistemaExternException("Error durant la realització de les firmes: " + transactionStatus.getErrorMessage() +"\r\n" +transactionStatus.getErrorStackTrace());

		case FirmaSimpleStatus.STATUS_CANCELLED: // = -2;
			throw new SistemaExternException("S'ha cancel·lat el procés de firmat.");

		case FirmaSimpleStatus.STATUS_FINAL_OK: // = 2;
		{
			logger.debug(" ===== RESULTAT  =========");
			logger.debug(" ---- Signature [ " + fullResults.getSignID() + " ]");
			logger.debug(FirmaSimpleSignedFileInfo.toString(fullResults.getSignedFileInfo()));

			return fullResults;
		}
		default:
			throw new SistemaExternException("Status de firma desconegut");
		}
	}

	 public void getAvailableProfiles(ApiFirmaEnServidorSimple api) throws Exception {

		    final String languagesUI[] = new String[] { "ca", "es" };

		    for (String languageUI : languagesUI) {
		      logger.debug(" ==== LanguageUI : " + languageUI + " ===========");

		      List<FirmaSimpleAvailableProfile> listProfiles = api.getAvailableProfiles(languageUI);
		      if (listProfiles.size() == 0) {
		        logger.debug("NO HI HA PERFILS PER AQUEST USUARI APLICACIÓ");
		      } else {
		        for (FirmaSimpleAvailableProfile ap : listProfiles) {
		          logger.debug("  + " + ap.getName() + ":");
		          logger.debug("      * Codi: " + ap.getCode());
		          logger.debug("      * Desc: " + ap.getDescription());
		        }
		      }
		    }
	 }
	 
	private String getPropertyUsername() {
		return GlobalProperties.getInstance().getProperty(
				PROPERTIES_BASE + "username");
	}
	 
	private String getLocationProperty() {
		return GlobalProperties.getInstance().getProperty(
				PROPERTIES_BASE + "location", "Palma");
	}

	private String getSignerEmailProperty() {
		return GlobalProperties.getInstance().getProperty(
				PROPERTIES_BASE + "signer.email", "suport@caib.es");
	}
	 
	private String getPropertyApiEndpoint() {
		return GlobalProperties.getProperties().getProperty(
				"app.plugin.firma.portafib.plugins.signatureserver.portafib.api_passarela_url");
	}
	
	private String getPropertyApiUsername() {
		return GlobalProperties.getProperties().getProperty(
				"app.plugin.firma.portafib.plugins.signatureserver.portafib.api_passarela_username");
	}
	
	private String getPropertyApiPassword() {
		return GlobalProperties.getProperties().getProperty(
				"app.plugin.firma.portafib.plugins.signatureserver.portafib.api_passarela_password");
	}
	
	private String getPropertyApiPerfil() {
		return GlobalProperties.getProperties().getProperty(
				"app.plugin.firma.portafib.plugins.signatureserver.portafib.api_passarela_perfil");
	}

	private static final Log logger = LogFactory.getLog(FirmaPluginPortafib.class);
}
