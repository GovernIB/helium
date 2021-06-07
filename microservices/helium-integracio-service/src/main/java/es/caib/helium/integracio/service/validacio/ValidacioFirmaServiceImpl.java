package es.caib.helium.integracio.service.validacio;

import java.util.ArrayList;
import java.util.List;

import org.fundaciobit.plugins.validatesignature.api.CertificateInfo;
import org.fundaciobit.plugins.validatesignature.api.IValidateSignaturePlugin;
import org.fundaciobit.plugins.validatesignature.api.SignatureDetailInfo;
import org.fundaciobit.plugins.validatesignature.api.SignatureRequestedInformation;
import org.fundaciobit.plugins.validatesignature.api.TimeStampInfo;
import org.fundaciobit.plugins.validatesignature.api.ValidateSignatureRequest;

import es.caib.helium.integracio.domini.arxiu.ArxiuFirma;
import es.caib.helium.integracio.domini.arxiu.ArxiuFirmaDetall;
import es.caib.helium.integracio.domini.validacio.RespostaValidacioSignatura;
import es.caib.helium.integracio.domini.validacio.VerificacioFirma;
import es.caib.helium.integracio.enums.arxiu.ArxiuFirmaPerfilEnum;
import es.caib.helium.integracio.enums.arxiu.NtiTipoFirmaEnum;
import es.caib.helium.integracio.excepcions.validacio.ValidacioFirmaException;
import lombok.Setter;

public class ValidacioFirmaServiceImpl implements ValidacioFirmaService {
	
	@Setter
	private IValidateSignaturePlugin apiValidate;
	@Setter
	private SignaturaPlugin signaturaPlugin;
	
	@Override
	public RespostaValidacioSignatura verificarFirma(VerificacioFirma verificacio) throws ValidacioFirmaException {
		
		try {
			return signaturaPlugin.verificarSignatura(verificacio.getDocumentContingut(), verificacio.getFirmaContingut(), verificacio.isObtenirDadesCertificat());
		} catch(Exception ex) {
			throw new ValidacioFirmaException("Error verificant la firma", ex);
		}
	}
	
	@Override
	public List<ArxiuFirma> validarFirma(VerificacioFirma validacio) throws ValidacioFirmaException {
	
		try {
			var validationRequest = new ValidateSignatureRequest();
			if (validacio.getFirmaContingut() != null) {
				validationRequest.setSignedDocumentData(validacio.getDocumentContingut());
				validationRequest.setSignatureData(validacio.getFirmaContingut());
			} else {
				validationRequest.setSignatureData(validacio.getDocumentContingut());
			}
			var sri = new SignatureRequestedInformation();
			sri.setReturnSignatureTypeFormatProfile(true);
			sri.setReturnCertificateInfo(true);
			sri.setReturnValidationChecks(false);
			sri.setValidateCertificateRevocation(false);
			sri.setReturnCertificates(false);
			sri.setReturnTimeStampInfo(false);
			validationRequest.setSignatureRequestedInformation(sri);
			var validateSignatureResponse = apiValidate.validateSignature(validationRequest);
			List<ArxiuFirmaDetall> detalls = new ArrayList<ArxiuFirmaDetall>();
			List<ArxiuFirma> firmes = new ArrayList<ArxiuFirma>();
			var firma = new ArxiuFirma();
			if (validateSignatureResponse.getSignatureDetailInfo() != null) {
				for (var signatureInfo: validateSignatureResponse.getSignatureDetailInfo()) {
					var detall = new ArxiuFirmaDetall();
					TimeStampInfo timeStampInfo = signatureInfo.getTimeStampInfo();
					if (timeStampInfo != null) {
						detall.setData(timeStampInfo.getCreationTime());
					} else {
						detall.setData(signatureInfo.getSignDate());
					}
					CertificateInfo certificateInfo = signatureInfo.getCertificateInfo();
					if (certificateInfo != null) {
						detall.setResponsableNif(certificateInfo.getNifResponsable());
						detall.setResponsableNom(certificateInfo.getNombreApellidosResponsable());
						detall.setEmissorCertificat(certificateInfo.getOrganizacionEmisora());
					}
					detalls.add(detall);
				}
				firma.setAutofirma(false);
				if (validacio.getFirmaContingut() != null) {
					firma.setContingut(validacio.getFirmaContingut());
				} else {
					firma.setContingut(validacio.getFirmaContingut());
				}
				firma.setDetalls(detalls);
				firma.setPerfil(toArxiuFirmaPerfilEnum(validateSignatureResponse.getSignProfile()));
				firma.setTipus(toArxiuFirmaTipusEnum(
						validateSignatureResponse.getSignType(),
						validateSignatureResponse.getSignFormat()));
				firma.setTipusMime(validacio.getContentType());
				firmes.add(firma);
			}			
//			monitorIntegracioHelper.addAccioOk(
//					MonitorIntegracioHelper.INTCODI_VALIDASIG,
//					accioDescripcio,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0,
//					IntegracioParametreDto.toIntegracioParametres(accioParams));
//			
			return firmes;
		} catch (Exception ex) {
//			String errorDescripcio = ex.getMessage();
//			monitorIntegracioHelper.addAccioError(
//					MonitorIntegracioHelper.INTCODI_VALIDASIG,
//					accioDescripcio,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0,
//					errorDescripcio,
//					ex,
//					IntegracioParametreDto.toIntegracioParametres(accioParams));
			throw new ValidacioFirmaException("Error validant la firma", ex);
		}
	}
	
	public List<ArxiuFirmaDetall> validarSignaturaObtenirDetalls(VerificacioFirma validacio) throws ValidacioFirmaException {
		
		try {
			var validationRequest = new ValidateSignatureRequest();
			if (validacio.getFirmaContingut() != null) {
				validationRequest.setSignedDocumentData(validacio.getDocumentContingut());
				validationRequest.setSignatureData(validacio.getFirmaContingut());
			} else {
				validationRequest.setSignatureData(validacio.getDocumentContingut());
			}
			var sri = new SignatureRequestedInformation();
			sri.setReturnSignatureTypeFormatProfile(true);
			sri.setReturnCertificateInfo(true);
			sri.setReturnValidationChecks(false);
			sri.setValidateCertificateRevocation(false);
			sri.setReturnCertificates(false);
			sri.setReturnTimeStampInfo(true);
			validationRequest.setSignatureRequestedInformation(sri);
			var validateSignatureResponse = apiValidate.validateSignature(validationRequest);
			List<ArxiuFirmaDetall> detalls = new ArrayList<ArxiuFirmaDetall>();
			if (validateSignatureResponse.getSignatureDetailInfo() != null) {
				for (SignatureDetailInfo signatureInfo: validateSignatureResponse.getSignatureDetailInfo()) {
					ArxiuFirmaDetall detall = new ArxiuFirmaDetall();
					TimeStampInfo timeStampInfo = signatureInfo.getTimeStampInfo();
					if (timeStampInfo != null) {
						detall.setData(timeStampInfo.getCreationTime());
					} else {
						detall.setData(signatureInfo.getSignDate());
					}
					CertificateInfo certificateInfo = signatureInfo.getCertificateInfo();
					if (certificateInfo != null) {
						detall.setResponsableNif(certificateInfo.getNifResponsable());
						detall.setResponsableNom(certificateInfo.getNombreApellidosResponsable());
						detall.setEmissorCertificat(certificateInfo.getOrganizacionEmisora());
					}
					detalls.add(detall);
				}
			}			
//			monitorIntegracioHelper.addAccioOk(
//					MonitorIntegracioHelper.INTCODI_VALIDASIG,
//					accioDescripcio,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0,
//					IntegracioParametreDto.toIntegracioParametres(accioParams));
//			
			return detalls;
		} catch (Exception ex) {
//			String errorDescripcio = ex.getMessage();
//			monitorIntegracioHelper.addAccioError(
//					MonitorIntegracioHelper.INTCODI_VALIDASIG,
//					accioDescripcio,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0,
//					errorDescripcio,
//					ex,
//					IntegracioParametreDto.toIntegracioParametres(accioParams));
			throw new ValidacioFirmaException("Error al validar signatura obtenir detalls", ex);
		}
	}
	
	private ArxiuFirmaPerfilEnum toArxiuFirmaPerfilEnum(String perfil) {		
		
		ArxiuFirmaPerfilEnum perfilFirma = null;
		if("AdES-BES".equals(perfil)) {
			perfilFirma = ArxiuFirmaPerfilEnum.BES;
		} else if("AdES-EPES".equals(perfil)) {
			perfilFirma = ArxiuFirmaPerfilEnum.EPES;
		} else if("PAdES-LTV".equals(perfil)) {
			perfilFirma = ArxiuFirmaPerfilEnum.LTV;
		} else if("AdES-T".equals(perfil)) {
			perfilFirma = ArxiuFirmaPerfilEnum.T;
		} else if("AdES-C".equals(perfil)) {
			perfilFirma = ArxiuFirmaPerfilEnum.C;
		} else if("AdES-X".equals(perfil)) {
			perfilFirma = ArxiuFirmaPerfilEnum.X;
		} else if("AdES-XL".equals(perfil)) {
			perfilFirma = ArxiuFirmaPerfilEnum.XL;
		} else if("AdES-A".equals(perfil)) {
			perfilFirma = ArxiuFirmaPerfilEnum.A;
		}
		return perfilFirma;
	}
	
	private NtiTipoFirmaEnum toArxiuFirmaTipusEnum(String tipus, String format) {		
		
		NtiTipoFirmaEnum tipusFirma = null;
		if (tipus.equals("PAdES") || format.equals("implicit_enveloped/attached")) {
			tipusFirma = NtiTipoFirmaEnum.PADES;
		} else if (tipus.equals("XAdES") && format.equals("explicit/detached")) {
			tipusFirma = NtiTipoFirmaEnum.XADES_DET;
		} else if (tipus.equals("XAdES") && format.equals("implicit_enveloping/attached")) {
			tipusFirma = NtiTipoFirmaEnum.XADES_ENV;
		} else if (tipus.equals("CAdES") && format.equals("explicit/detached")) {
			tipusFirma = NtiTipoFirmaEnum.CADES_DET;
		} else if (tipus.equals("CAdES") && format.equals("implicit_enveloping/attached")) {
			tipusFirma = NtiTipoFirmaEnum.CADES_ATT;
		}
		return tipusFirma;
	}
}
