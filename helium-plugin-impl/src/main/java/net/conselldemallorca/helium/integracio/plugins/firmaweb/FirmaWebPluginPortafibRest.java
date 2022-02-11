/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.firmaweb;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fundaciobit.apisib.apifirmasimple.v1.ApiFirmaEnServidorSimple;
import org.fundaciobit.apisib.apifirmasimple.v1.ApiFirmaWebSimple;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleAddFileToSignRequest;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleAvailableProfile;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleCommonInfo;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleFile;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleFileInfoSignature;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleGetSignatureResultRequest;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleGetTransactionStatusResponse;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignDocumentRequest;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignatureResult;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignatureStatus;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignedFileInfo;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleStartTransactionRequest;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleStatus;
import org.fundaciobit.apisib.apifirmasimple.v1.jersey.ApiFirmaEnServidorSimpleJersey;
import org.fundaciobit.apisib.apifirmasimple.v1.jersey.ApiFirmaWebSimpleJersey;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.SistemaExternException;
import net.conselldemallorca.helium.integracio.plugins.firma.FirmaPlugin;
import net.conselldemallorca.helium.integracio.plugins.firma.FirmaPluginPortafib;
import net.conselldemallorca.helium.integracio.plugins.firma.FirmaResposta;

/**
 * Implementació del plugin de signatura emprant el Servidor de l'API REST.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FirmaWebPluginPortafibRest {	
	
	private static final String PROPERTIES_BASE = "app.plugin.passarelafirma.plugins.signatureweb.portafib.apifirmawebsimple.";
	private static final Log logger = LogFactory.getLog(FirmaWebPluginPortafibRest.class);
	
	long pluginId;
	private String nom;
	private String descripcioCurta;
	private String classe;
	private Properties properties;

	public FirmaWebPluginPortafibRest() {
		
	}
	
	public FirmaWebPluginPortafibRest(
			String nom,
			String descripcioCurta,
			String classe,
			Properties properties) {
		this.nom = nom;
		this.descripcioCurta = descripcioCurta;
		this.classe = classe;
		this.properties = properties;

	}


	public String firmar(
			String id, 
			String nom, 
			String motiu, 
			byte[] contingut, 
			String mime,
			String tipusDocumental) throws SistemaExternException {
		
		FirmaSimpleGetTransactionStatusResponse resposta = new FirmaSimpleGetTransactionStatusResponse();
		/*mgonzalez*/
		/*ApiFirmaWebSimple api = new ApiFirmaWebSimpleJersey(
				getPropertyApiEndpoint(),
				getPropertyApiUsername(), 
				getPropertyApiPassword());*/
		//Només per test
		ApiFirmaWebSimple api = new ApiFirmaWebSimpleJersey(
				/*"https://proves.caib.es/portafib/common/rest/apifirmawebsimple/v1/"*/
				"https://dev.caib.es/portafib/common/rest/apifirmawebsimple/v1/",
				"$helium_portafibpass", 
				"helium_portafibpass");
		
		FirmaSimpleFile fileToSign = new FirmaSimpleFile(nom, mime, contingut);

		FirmaSimpleSignatureResult result;
		try {
			
//			getAvailableProfiles(api);
			FirmaSimpleAvailableProfile profile = new FirmaSimpleAvailableProfile();
			profile.setCode("PROFILE_WEB");
			profile.setDescription("PROFILE_WEB");
			profile.setName("PROFILE_WEB");
		//String perfil = getPropertyApiPerfil();
		internalSignDocument(
					id,
					api,
					null,
					fileToSign,
					motiu,
					tipusDocumental);
			//CUIDADO RETORNO NULL de moment
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	//protected FirmaSimpleSignatureResult internalSignDocument(
	protected void internalSignDocument(
			String id, 
			ApiFirmaWebSimple api,
			FirmaSimpleAvailableProfile profile,
			FirmaSimpleFile fileToSign,
			String motiu,
			String tipusDocumental) throws Exception, FileNotFoundException, IOException {
		String signID = id;
		String name = fileToSign.getNom();
		String reason = motiu;
		String location = getLocationProperty();

		int signNumber = 1;
		String languageSign = "ca";
		Long tipusDocumentalID = tipusDocumental != null ? Long.valueOf(tipusDocumental.substring(2)) : null;

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
		String username = this.getPropertyApiUsername();
		String administrationID = "12345678C";//Marta: falta provar amb un adminid real!
		String signerEmail = this.getSignerEmailProperty();
		
		FirmaSimpleCommonInfo commonInfo;
	    commonInfo = new FirmaSimpleCommonInfo(/*profile.getCode()*/null, languageUI, /*username*/"$helium_portafib", administrationID,/*email*/ signerEmail);
	    
	    // Enviam la part comu de la transacció
	    String transactionID = api.getTransactionID(commonInfo);

	    System.out.println("TransactionID = |" + transactionID + "|");
	    
	    // Aquí podem afegir més documents a firmar
	    FirmaSimpleFileInfoSignature[] filesToSign;
	    filesToSign = new FirmaSimpleFileInfoSignature[] { fileInfoSignature };
	    
	    for (int i = 0; i < filesToSign.length; i++) {
	        System.out.println("Enviant firma[" + i + "]");
	        FirmaSimpleAddFileToSignRequest newDocument;
	        newDocument = new FirmaSimpleAddFileToSignRequest(transactionID, filesToSign[i]);
	        api.addFileToSign(newDocument);  
	      }
		
		FirmaSimpleSignDocumentRequest signature;
	    signature = new FirmaSimpleSignDocumentRequest(commonInfo, fileInfoSignature);

	   
	 // Aquí especificam la URL de retorn un cop finalitzada la transacció
	    int port = 1989;
	    final String returnUrl = "http://localhost:" + port + "/returnurl/" + transactionID;
	    final String view = FirmaSimpleStartTransactionRequest.VIEW_FULLSCREEN;

	    FirmaSimpleStartTransactionRequest startTransactionInfo;
	    startTransactionInfo = new FirmaSimpleStartTransactionRequest(transactionID,
	         returnUrl, view);

	    String redirectUrl = api.startTransaction(startTransactionInfo);

	    System.out.println("Redirigir la pàgina web del navegador del client a " + redirectUrl);
	    
	    
		FirmaSimpleGetTransactionStatusResponse fullTransactionStatus;
		fullTransactionStatus = api.getTransactionStatus(transactionID);

		FirmaSimpleStatus transactionStatus = fullTransactionStatus.getTransactionStatus();
		int status = transactionStatus.getStatus();
		switch (status) {

		case FirmaSimpleStatus.STATUS_INITIALIZING: // = 0;
			throw new Exception("S'ha rebut un estat inconsistent del proces de firma"
					+ " (inicialitzant). Pot ser el PLugin de Firma no està ben desenvolupat."
					+ " Consulti amb el seu administrador.");

		case FirmaSimpleStatus.STATUS_IN_PROGRESS: // = 1;
			throw new Exception("S'ha rebut un estat inconsistent del proces de firma"
					+ " (En Progrés). Pot ser el PLugin de Firma no està ben desenvolupat."
					+ " Consulti amb el seu administrador.");

		case FirmaSimpleStatus.STATUS_FINAL_ERROR: // = -1;
			System.err.println("Error durant la realització de les firmes: " + transactionStatus.getErrorMessage());
			String desc = transactionStatus.getErrorStackTrace();
			if (desc != null) {
				System.err.println(desc);
			}
			return;

		case FirmaSimpleStatus.STATUS_CANCELLED: // = -2;
			System.err.println("Durant el proces de firmes, l'usuari ha cancelat la transacció.");
			return;

		case FirmaSimpleStatus.STATUS_FINAL_OK: // = 2;
			processStatusFileOfSign(api, transactionID, fullTransactionStatus);
			break;

		} // Final Switch Firma
	    
		
	}
	//FALTA TANCAR LA TRANSACCIÓ
	/*try {
		   ...
		} finally {
		  if (api != null && transactionID != null) {
		    try {
		      api.closeTransaction(transactionID);
		    } catch (Throwable th) {
		      th.printStackTrace();
		    }
		  }
		}*/

	 
	
	protected static void processStatusFileOfSign(ApiFirmaWebSimple api, String transactionID, FirmaSimpleGetTransactionStatusResponse fullTransactionStatus) throws Exception, FileNotFoundException, IOException {

		  List<FirmaSimpleSignatureStatus> ssl;
		  ssl = fullTransactionStatus.getSignaturesStatusList();

		  System.out.println(" ===== RESULTATS [" + ssl.size() + "] =========");

		  for (FirmaSimpleSignatureStatus signatureStatus : ssl) {

		    final String signID = signatureStatus.getSignID();
		    System.out.println(" ---- Signature [ " + signID + " ]");
		    FirmaSimpleStatus fss = signatureStatus.getStatus();
		    int statusSign = fss.getStatus();
		    switch (statusSign) {

		      case FirmaSimpleStatus.STATUS_INITIALIZING: // = 0;
		        System.err.println("  STATUS = " + statusSign + " (STATUS_INITIALIZING)");
		        System.err.println("  ESULT: Incoherent Status");
		      break;

		      case FirmaSimpleStatus.STATUS_IN_PROGRESS: // = 1;
		        System.err.println("  STATUS = " + statusSign + " (STATUS_IN_PROGRESS)");
		        System.err.println("  RESULT: Incoherent Status");
		      break;

		      case FirmaSimpleStatus.STATUS_FINAL_ERROR: // = -1;
		        System.err.println("  STATUS = " + statusSign + " (STATUS_ERROR)");
		        System.err.println("  RESULT: Error en la firma: " + fss.getErrorMessage());
		      break;

		      case FirmaSimpleStatus.STATUS_CANCELLED: // = -2;
		        System.err.println("  STATUS = " + statusSign + " (STATUS_CANCELLED)");
		        System.err.println("  RESULT: L'usuari ha cancel.lat la firma.");
		      break;

		      case FirmaSimpleStatus.STATUS_FINAL_OK: // = 2;
		        FirmaSimpleSignatureResult fssr;
		        fssr = api.getSignatureResult(
		                     new FirmaSimpleGetSignatureResultRequest(transactionID, signID));
		        FirmaSimpleFile fsf = fssr.getSignedFile();
		        final String outFile = signID + "_" + fsf.getNom();

		        FileOutputStream fos = new FileOutputStream(outFile);
		        fos.write(fsf.getData());
		        fos.flush();

		        System.out.println("  RESULT: Fitxer signat guardat en '" + outFile + "'");
		        System.gc();
		        System.out.println( 
		                FirmaSimpleSignedFileInfo.toString(fssr.getSignedFileInfo()));

		      break;
		    }
		  } // Final for de fitxers firmats
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
	 
	 
	private String getLocationProperty() {
		return properties.getProperty(
				PROPERTIES_BASE + "location", "Palma");
	}

	private String getSignerEmailProperty() {
		return properties.getProperty(
				PROPERTIES_BASE + "signer.email", "suport@caib.es");
	}
	 
	private String getPropertyApiEndpoint() {
		return properties.getProperty(
				PROPERTIES_BASE + "endpoint");
	}
	
	private String getPropertyApiUsername() {
		return properties.getProperty(
				PROPERTIES_BASE + "username");
	}
	
	private String getPropertyApiPassword() {
		return properties.getProperty(PROPERTIES_BASE + "password");
	}
	
	private String getPropertyApiPerfil() {
		return properties.getProperty(
				PROPERTIES_BASE +"perfil");
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getDescripcioCurta() {
		return descripcioCurta;
	}

	public void setDescripcioCurta(String descripcioCurta) {
		this.descripcioCurta = descripcioCurta;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public long getPluginId() {
		return (pluginId);
	}
	public void setPluginId(long pluginId) {
		this.pluginId = pluginId;
	}
	

	
}
