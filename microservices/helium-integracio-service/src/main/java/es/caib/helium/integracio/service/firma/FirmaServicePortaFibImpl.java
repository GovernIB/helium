package es.caib.helium.integracio.service.firma;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.firma.FirmaPost;
import es.caib.helium.integracio.enums.firma.FirmaTipus;
import es.caib.helium.integracio.excepcions.firma.FirmaException;
import es.caib.helium.integracio.service.monitor.MonitorIntegracionsService;
import es.caib.helium.jms.domini.Parametre;
import es.caib.helium.jms.enums.CodiIntegracio;
import es.caib.helium.jms.enums.EstatAccio;
import es.caib.helium.jms.enums.TipusAccio;
import es.caib.helium.jms.events.IntegracioEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class FirmaServicePortaFibImpl implements FirmaService {
	
	@Setter
	private ISignatureServerPlugin plugin;
	@Setter
	private String username;
	@Setter
	private String location;
	@Setter
	private String email;
	@Autowired
	protected MonitorIntegracionsService monitor;
	
	@Override
	public byte[] firmar(FirmaPost firma, Long entornId) throws FirmaException {
		
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("arxiuNom", firma.getArxiuNom()));
		var t0 = System.currentTimeMillis();
		var descripcio = "Firmant el fitxer " + firma.getArxiuNom();
		File sourceFile = null;
		File destFile = null;
		byte[] firmaContingut = null;
		try {
			
			sourceFile = getArxiuTemporal(firma.getArxiuContingut());
			String source = sourceFile.getAbsolutePath();
			String dest = null;
			IRubricGenerator rubricGenerator = null;
			String signType;
			int signMode;
			if (FirmaTipus.PADES.equals(firma.getFirmaTipus())) {
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
					firma.getMotiu(),
					userRequiresTimeStamp, 
					rubricGenerator,
					plugin);
			// Llegeix la firma del fitxer de destí
			destFile = new File(dest);
			firmaContingut = FileUtils.readFileToByteArray(destFile);
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.FIRMA_SERV)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Fitxer firmat correctament");
			return firmaContingut;
			
		} catch (Exception ex) {
		
			var error = "Error firmant l'arxiu";
			log.error(error, ex);
			
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.FIRMA_SERV) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			
			throw new FirmaException(error, ex);
			
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
				username,
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
				location, 
				email, 
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
				Logger.getLogger(FirmaServicePortaFibImpl.class.getName()).log(Level.SEVERE, errMsg, sss.getErrorException());
				throw new FirmaException(errMsg, sss.getErrorException());
			} else {
				Logger.getLogger(FirmaServicePortaFibImpl.class.getName()).log(Level.SEVERE, errMsg);
				throw new FirmaException(errMsg);				
			}
		} else {
			FileInfoSignature fis = signaturesSet.getFileInfoSignatureArray()[0];
			StatusSignature status = fis.getStatusSignature();
			if (status.getStatus() != StatusSignaturesSet.STATUS_FINAL_OK) {
				// Error a la firma 1
				errMsg = "Error Firma 1: " + status.getStatus() + " " + status.getErrorMsg();
				if (sss.getErrorException() != null) {
					Logger.getLogger(FirmaServicePortaFibImpl.class.getName()).log(Level.SEVERE, errMsg, sss.getErrorException());
					throw new FirmaException(errMsg, sss.getErrorException());
				} else {
					Logger.getLogger(FirmaServicePortaFibImpl.class.getName()).log(Level.SEVERE, errMsg);
					throw new FirmaException(errMsg);				
				}
			} else {
				// Document firmat correctament
				File dest = new File(destPath);
				status.getSignedData().renameTo(dest);
				Logger.getLogger(FirmaServicePortaFibImpl.class.getName()).log(Level.FINER, "Guardada Firma a " + dest.getAbsolutePath());
				Logger.getLogger(FirmaServicePortaFibImpl.class.getName()).log(Level.FINER, "Tamany " + dest);
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
		File fitxerTmp = new File(autofirmaBasePath, new Long(System.currentTimeMillis()).toString());
        fitxerTmp.getParentFile().mkdirs();
        FileUtils.writeByteArrayToFile(fitxerTmp, contingut);
        return fitxerTmp;
	}
}
