package net.conselldemallorca.helium.integracio.plugins.firmaweb;

import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fundaciobit.apisib.apifirmasimple.v1.ApiFirmaWebSimple;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleAddFileToSignRequest;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleCommonInfo;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleFile;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleFileInfoSignature;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleGetSignatureResultRequest;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleGetTransactionStatusResponse;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignatureResult;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignatureStatus;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleStartTransactionRequest;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleStatus;
import org.fundaciobit.apisib.apifirmasimple.v1.jersey.ApiFirmaWebSimpleJersey;

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.FirmaResultatDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.StatusEnumDto;

public class FirmaSimpleWebPluginPortafib implements FirmaWebPlugin {

	private static final String PROPERTIES_BASE = "app.plugin.passarelafirma.plugins.signatureweb.portafib.apifirmawebsimple.";
	private ApiFirmaWebSimple api = null;
	private Properties properties;

	/** Constructor per guardar les propietats i crear el client.
	 * 
	 * @param properties
	 */
	public FirmaSimpleWebPluginPortafib(Properties properties) {
		this.properties = properties;
		this.getApi();
	}
	

	@Override
	public String firmaSimpleWebStart(
			String signId,
			ArxiuDto arxiu, 
			String motiu,
			String lloc,
			PersonaDto persona, 
			String urlRetorn) 
	{
		ApiFirmaWebSimple api = getApi();

		String transactionID = null;

		try {

			final String username = persona.getCodi();
			final String administrationID = persona.getDni();
			final String signerEmail = persona.getEmail();

			String language = "ca";

			FirmaSimpleCommonInfo commonInfoSignature = new FirmaSimpleCommonInfo(
					null,
					language,
					username,
					administrationID,
					signerEmail);

			transactionID = api.getTransactionID(commonInfoSignature);

			FirmaSimpleFile fileToSign = new FirmaSimpleFile(
					arxiu.getNom(),
					arxiu.getTipusMime(),
					arxiu.getContingut());

			String name = fileToSign.getNom();

			final String reason = motiu;
			final String location = lloc != null ? lloc : this.getPropertyLloc();
			long tipusDocumentalID = 99; // =TD99
			

			FirmaSimpleFileInfoSignature fileInfoSignature = new FirmaSimpleFileInfoSignature(
					fileToSign,
					signId,
					name,
					reason,
					location,
					1,
					language,
					tipusDocumentalID);

				FirmaSimpleAddFileToSignRequest newDocument = new FirmaSimpleAddFileToSignRequest(
						transactionID,
						fileInfoSignature);
				api.addFileToSign(newDocument);

			

			// Aquí especificam la URL de retorn un cop finalitzada la transacció
			urlRetorn = urlRetorn + "?transactionID=" + transactionID;

			FirmaSimpleStartTransactionRequest startTransactionInfo;
			startTransactionInfo = new FirmaSimpleStartTransactionRequest(
					transactionID,
					urlRetorn,
					FirmaSimpleStartTransactionRequest.VIEW_FULLSCREEN);

			String urlRedirectToPortafib = api.startTransaction(startTransactionInfo);
			return urlRedirectToPortafib;

		} catch (Exception e) {

			String msg = "Error processant entrada de dades o inicialitzant el proces de firma simple web: " + e.getMessage();

			if (transactionID != null) {
				try {
					api.closeTransaction(transactionID);
				} catch (Throwable th) {
					th.printStackTrace();
				}
			}
			throw new RuntimeException(msg, e);

		}
	}

	@Override
	public FirmaResultatDto firmaSimpleWebEnd(String transactionID) {
		FirmaResultatDto firmaResultat = null;

		ApiFirmaWebSimple api = null;

		try {

			api = getApi();
			
			FirmaSimpleGetTransactionStatusResponse fullTransactionStatus = api.getTransactionStatus(transactionID);

			FirmaSimpleStatus transactionStatus = fullTransactionStatus.getTransactionStatus();
			int status = transactionStatus.getStatus();
			switch (status) {

			case FirmaSimpleStatus.STATUS_INITIALIZING: // = 0;
				firmaResultat = new FirmaResultatDto(
						StatusEnumDto.ERROR,
						"S'ha rebut un estat inconsistent del proces de firma (inicialitzant). Pot ser el Plugin de Firma no està ben desenvolupat. Consulti amb el seu administrador.");
				break;

			case FirmaSimpleStatus.STATUS_IN_PROGRESS: // = 1;
				firmaResultat = new FirmaResultatDto(
						StatusEnumDto.ERROR,
						"S'ha rebut un estat inconsistent del proces de firma (En Progrés). Pot ser el Plugin de Firma no està ben desenvolupat. Consulti amb el seu administrador.");
				break;

			case FirmaSimpleStatus.STATUS_FINAL_ERROR: // = -1;
				firmaResultat = new FirmaResultatDto(
						StatusEnumDto.ERROR,
						"Error durant la realització de les firmes: " + transactionStatus.getErrorMessage() + " \n " + transactionStatus.getErrorStackTrace());
				break;

			case FirmaSimpleStatus.STATUS_CANCELLED: // = -2;
				firmaResultat = new FirmaResultatDto(
						StatusEnumDto.WARNING,
						"Durant el proces de firmes, l'usuari ha cancelat la transacció.");
				break;

			case FirmaSimpleStatus.STATUS_FINAL_OK: // = 2;
				firmaResultat = processStatusFileOfSign(
						api,
						transactionID,
						fullTransactionStatus);
				break;

			}

		} catch (Exception e) {

			String msg = "Error firma simple web: " + e.getMessage();
			logger.error(msg, e);
			firmaResultat = new FirmaResultatDto(
					StatusEnumDto.ERROR,
					msg);

		} finally {
			if (api != null && transactionID != null) {
				try {
					api.closeTransaction(transactionID);
				} catch (Throwable th) {
					th.printStackTrace();
				}
			}
		}

		return firmaResultat;
	}
	
	
	private FirmaResultatDto processStatusFileOfSign(
			ApiFirmaWebSimple api,
			String transactionID,
			FirmaSimpleGetTransactionStatusResponse fullTransactionStatus) throws Exception {

		FirmaResultatDto firmaResultat = null;

		List<FirmaSimpleSignatureStatus> ssl = fullTransactionStatus.getSignaturesStatusList();

		for (FirmaSimpleSignatureStatus signatureStatus : ssl) {

			final String signID = signatureStatus.getSignID();
			FirmaSimpleStatus fss = signatureStatus.getStatus();
			int statusSign = fss.getStatus();
			switch (statusSign) {

			case FirmaSimpleStatus.STATUS_INITIALIZING: // = 0;

				firmaResultat = new FirmaResultatDto(
						StatusEnumDto.ERROR,
						"Incoherent Status (STATUS_INITIALIZING)");
				break;
			case FirmaSimpleStatus.STATUS_IN_PROGRESS: // = 1;

				firmaResultat = new FirmaResultatDto(
						StatusEnumDto.ERROR,
						"Incoherent Status (STATUS_IN_PROGRESS)");
				break;

			case FirmaSimpleStatus.STATUS_FINAL_ERROR: // = -1;

				firmaResultat = new FirmaResultatDto(
						StatusEnumDto.ERROR,
						"Error en la firma: " + fss.getErrorMessage() + " (STATUS_ERROR)");
				break;

			case FirmaSimpleStatus.STATUS_CANCELLED: // = -2;
				firmaResultat = new FirmaResultatDto(
						StatusEnumDto.WARNING,
						"L'usuari ha cancel.lat la firma. (STATUS_CANCELLED)");
				break;

			case FirmaSimpleStatus.STATUS_FINAL_OK: // = 2;
				
				FirmaSimpleSignatureResult fssr = api.getSignatureResult(
						new FirmaSimpleGetSignatureResultRequest(
								transactionID,
								signID));
				
				FirmaSimpleFile fsf = fssr.getSignedFile();
				String outFile = fsf.getNom();
				
				// Corregeix l'extensió a partir del tipus mime
				int punt = outFile.lastIndexOf(".");
				if (punt > 0) {
					outFile = outFile.substring(0, punt) + ".pdf" ;
				}
				firmaResultat = new FirmaResultatDto(
						StatusEnumDto.OK,
						outFile,
						fsf.getData());
				break;
			}
		}

		return firmaResultat;
	}

	private ApiFirmaWebSimple getApi() {
		if (api == null) {
			this.api = new ApiFirmaWebSimpleJersey(
					getPropertyApiEndpoint(),
					getPropertyApiUsername(), 
					getPropertyApiPassword());
		}
		return api;
		
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
	
	private String getPropertyLloc() {
		return properties.getProperty(PROPERTIES_BASE + "location");
	}


	private static final Log logger = LogFactory.getLog(FirmaSimpleWebPluginPortafib.class);
}
