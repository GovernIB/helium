/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.firma;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.fundaciobit.plugins.signature.api.CommonInfoSignature;
import org.fundaciobit.plugins.signature.api.FileInfoSignature;
import org.fundaciobit.plugins.signature.api.IRubricGenerator;
import org.fundaciobit.plugins.signature.api.ITimeStampGenerator;
import org.fundaciobit.plugins.signature.api.PdfRubricRectangle;
import org.fundaciobit.plugins.signature.api.PdfVisibleSignature;
import org.fundaciobit.plugins.signature.api.PolicyInfoSignature;
import org.fundaciobit.plugins.signature.api.SecureVerificationCodeStampInfo;
import org.fundaciobit.plugins.signature.api.SignaturesSet;
import org.fundaciobit.plugins.signature.api.SignaturesTableHeader;
import org.fundaciobit.plugins.signature.api.StatusSignature;
import org.fundaciobit.plugins.signature.api.StatusSignaturesSet;
import org.fundaciobit.plugins.signatureserver.api.ISignatureServerPlugin;
import org.fundaciobit.plugins.signatureserver.portafib.PortaFIBSignatureServerPlugin;

import es.caib.helium.logic.util.GlobalProperties;
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
	public byte[] firmar(
			FirmaTipus firmaTipus,
			String motiu,
			String arxiuNom,
			byte[] arxiuContingut) throws SistemaExternException {
		File sourceFile = null;
		File destFile = null;
		byte[] firmaContingut = null;
		try {
			ISignatureServerPlugin plugin = new PortaFIBSignatureServerPlugin(
					PROPERTIES_BASE,
					GlobalProperties.getInstance().findAll());
			sourceFile = getArxiuTemporal(arxiuContingut);
			String source = sourceFile.getAbsolutePath();
			String dest = null;
			IRubricGenerator rubricGenerator = null;
			String signType;
			int signMode;
			if (FirmaTipus.PADES.equals(firmaTipus)) {
				dest = source + "_PADES.pdf";
				signType = FileInfoSignature.SIGN_TYPE_PADES; // PADES
				signMode = FileInfoSignature.SIGN_MODE_IMPLICIT; // IMPLICIT
			} else {
				dest = source + "_cades_detached.csig";
				signType = FileInfoSignature.SIGN_TYPE_CADES; // CAdES
				signMode = FileInfoSignature.SIGN_MODE_EXPLICIT; // Detached
			}
			boolean userRequiresTimeStamp = false;
			signFile(
					source, 
					dest, 
					signType,
					signMode, 
					motiu,
					userRequiresTimeStamp, 
					rubricGenerator,
					plugin);
			// Llegeix la firma del fitxer de destí
			destFile = new File(dest);
			firmaContingut = FileUtils.readFileToByteArray(destFile);
			return firmaContingut;
		} catch (Exception e) {
			throw new SistemaExternException(e);
		} finally {
			// Esborra els arxius temporals
			if (sourceFile != null && sourceFile.exists()) {
				sourceFile.delete();
			}
			if (destFile != null && destFile.exists()) {
				destFile.delete();
			}
		}
	}

	private void signFile(
			String sourcePath, 
			String destPath, 
			String signType, 
			int signMode,
			String reason,
			boolean userRequiresTimeStamp, 
			IRubricGenerator rubricGenerator, 
			ISignatureServerPlugin plugin) throws Exception, FileNotFoundException, IOException {
		// Informació comú per a totes les signatures
		String languageUI = "ca";
		String filtreCertificats = "";
		String administrationID = null; // No te sentit en API Firma En Servidor
		PolicyInfoSignature policyInfoSignature = null;
		CommonInfoSignature commonInfoSignature = new CommonInfoSignature(
				languageUI, 
				filtreCertificats, 
				getUsernameProperty(),
				administrationID, 
				policyInfoSignature);
		String signID = "999";
		File source = new File(sourcePath);
		String fileName = source.getName();
		int signNumber = 1;
		String languageSign = "ca";
		String signAlgorithm = FileInfoSignature.SIGN_ALGORITHM_SHA1;
		int signaturesTableLocation = FileInfoSignature.SIGNATURESTABLELOCATION_WITHOUT;
		PdfVisibleSignature pdfInfoSignature = null;
		if (FileInfoSignature.SIGN_TYPE_PADES.equals(signType) && rubricGenerator != null) {
			signaturesTableLocation = FileInfoSignature.SIGNATURESTABLELOCATION_LASTPAGE;
			PdfRubricRectangle pdfRubricRectangle = new PdfRubricRectangle(106, 650, 555, 710);
			pdfInfoSignature = new PdfVisibleSignature(pdfRubricRectangle, rubricGenerator);
		}
		final ITimeStampGenerator timeStampGenerator = null;
		final SignaturesTableHeader signaturesTableHeader = null;
		final SecureVerificationCodeStampInfo csvStampInfo = null;
		FileInfoSignature fileInfo = new FileInfoSignature(
				signID, 
				source, 
				FileInfoSignature.PDF_MIME_TYPE, 
				fileName,
				reason, 
				getLocationProperty(), 
				getSignerEmailProperty(), 
				signNumber, 
				languageSign, 
				signType, 
				signAlgorithm, 
				signMode,
				signaturesTableLocation, 
				signaturesTableHeader, 
				pdfInfoSignature, 
				csvStampInfo, 
				userRequiresTimeStamp,
				timeStampGenerator);
		final String signaturesSetID = String.valueOf(System.currentTimeMillis());
		SignaturesSet signaturesSet = new SignaturesSet(
				signaturesSetID + "_" + new Long(System.currentTimeMillis()).toString(), 
				commonInfoSignature,
				new FileInfoSignature[] { fileInfo });
		String timestampUrlBase = null;
		signaturesSet = plugin.signDocuments(signaturesSet, timestampUrlBase);
		StatusSignaturesSet sss = signaturesSet.getStatusSignaturesSet();
		String errMsg;
		if (sss.getStatus() != StatusSignaturesSet.STATUS_FINAL_OK) {
			// Error General
			errMsg = "Resultat de la signatura erroni: " + sss.getStatus() + " " + sss.getErrorMsg();
			if (sss.getErrorException() != null) {
				Logger.getLogger(FirmaPluginPortafib.class.getName()).log(Level.SEVERE, errMsg, sss.getErrorException());
				throw new SistemaExternException(errMsg, sss.getErrorException());
			} else {
				Logger.getLogger(FirmaPluginPortafib.class.getName()).log(Level.SEVERE, errMsg);
				throw new SistemaExternException(errMsg);				
			}
		} else {
			FileInfoSignature fis = signaturesSet.getFileInfoSignatureArray()[0];
			StatusSignature status = fis.getStatusSignature();
			if (status.getStatus() != StatusSignaturesSet.STATUS_FINAL_OK) {
				// Error a la firma 1
				errMsg = "Error Firma 1: " + status.getStatus() + " " + status.getErrorMsg();
				if (sss.getErrorException() != null) {
					Logger.getLogger(FirmaPluginPortafib.class.getName()).log(Level.SEVERE, errMsg, sss.getErrorException());
					throw new SistemaExternException(errMsg, sss.getErrorException());
				} else {
					Logger.getLogger(FirmaPluginPortafib.class.getName()).log(Level.SEVERE, errMsg);
					throw new SistemaExternException(errMsg);				
				}
			} else {
				// Document firmat correctament
				File dest = new File(destPath);
				status.getSignedData().renameTo(dest);
				Logger.getLogger(FirmaPluginPortafib.class.getName()).log(Level.FINER, "Guardada Firma a " + dest.getAbsolutePath());
				Logger.getLogger(FirmaPluginPortafib.class.getName()).log(Level.FINER, "Tamany " + dest);
			}
		}
	}

	private static final String autofirmaBasePath;
	static {
		String tempDir = System.getProperty("java.io.tmpdir");
		final File base = new File(tempDir, "HELIUM_FIRMA_TMP");
		base.mkdirs();
		autofirmaBasePath = base.getAbsolutePath();
	}

	private File getArxiuTemporal(
			byte[] contingut) throws IOException {
		File fitxerTmp = new File(
				autofirmaBasePath,
				new Long(System.currentTimeMillis()).toString());
        fitxerTmp.getParentFile().mkdirs();
        FileUtils.writeByteArrayToFile(fitxerTmp, contingut);
        return fitxerTmp;
	}

	private String getUsernameProperty() {
		return GlobalProperties.getInstance().getProperty(
				"app.plugin.firma.portafib.username");
	}
	private String getLocationProperty() {
		return GlobalProperties.getInstance().getProperty(
				"app.plugin.firma.portafib.location");
	}
	private String getSignerEmailProperty() {
		return GlobalProperties.getInstance().getProperty(
				"app.plugin.firma.portafib.signer.email");
	}

}
