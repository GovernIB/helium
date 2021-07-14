package es.caib.helium.integracio.service.validacio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.fundaciobit.plugins.validatesignature.api.CertificateInfo;
import org.fundaciobit.plugins.validatesignature.api.IValidateSignaturePlugin;
import org.fundaciobit.plugins.validatesignature.api.SignatureDetailInfo;
import org.fundaciobit.plugins.validatesignature.api.SignatureRequestedInformation;
import org.fundaciobit.plugins.validatesignature.api.TimeStampInfo;
import org.fundaciobit.plugins.validatesignature.api.ValidateSignatureRequest;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.integracio.domini.arxiu.ArxiuFirma;
import es.caib.helium.integracio.domini.arxiu.ArxiuFirmaDetall;
import es.caib.helium.integracio.domini.validacio.RespostaValidacioSignatura;
import es.caib.helium.integracio.domini.validacio.VerificacioFirma;
import es.caib.helium.integracio.enums.arxiu.ArxiuFirmaPerfilEnum;
import es.caib.helium.integracio.enums.arxiu.NtiTipoFirmaEnum;
import es.caib.helium.integracio.excepcions.validacio.ValidacioFirmaException;
import es.caib.helium.integracio.service.monitor.MonitorIntegracionsService;
import es.caib.helium.jms.domini.Parametre;
import es.caib.helium.jms.enums.CodiIntegracio;
import es.caib.helium.jms.enums.EstatAccio;
import es.caib.helium.jms.enums.TipusAccio;
import es.caib.helium.jms.events.IntegracioEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidacioFirmaServiceImpl implements ValidacioFirmaService {
	
	@Setter
	private IValidateSignaturePlugin apiValidate;
	@Setter
	private SignaturaPlugin signaturaPlugin;
	@Autowired
	private MonitorIntegracionsService monitor;
	
	@Override
	public RespostaValidacioSignatura verificarFirma(VerificacioFirma verificacio) throws ValidacioFirmaException {
		
		var t0 = System.currentTimeMillis();
		var descripcio = "Verificació de firma";
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("document.length", (verificacio.getDocumentContingut() != null ? verificacio.getDocumentContingut() : 0) + "bytes"));
		parametres.add(new Parametre("signatura.length", (verificacio.getFirmaContingut() != null ? verificacio.getFirmaContingut() : 0) + "bytes"));
		parametres.add(new Parametre("obtenirDadesCertificat", verificacio.isObtenirDadesCertificat() + ""));

		try {
			var resposta = signaturaPlugin.verificarSignatura(verificacio.getDocumentContingut(), verificacio.getFirmaContingut(), verificacio.isObtenirDadesCertificat());
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.VALIDASIG)
					.entornId(verificacio.getEntornId())
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Firma verificada correctament");
			return resposta;
		} catch(Exception ex) {
			
			var error = "Error verificant la firma";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.VALIDASIG) 
					.entornId(verificacio.getEntornId()) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new ValidacioFirmaException(error, ex);
		}
	}
	
	@Override
	public List<ArxiuFirma> validarFirma(VerificacioFirma validacio) throws ValidacioFirmaException {
	
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("DocumentId", validacio.getDocumentStoreId() + ""));
		var t0 = System.currentTimeMillis();
		var descripcio = "Validar firma, obtenir informació de document firmat";
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
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.VALIDASIG)
					.entornId(validacio.getEntornId())
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Firma validada correctament");
			return firmes;
			
		} catch (Exception ex) {
			
			var error = "Error en el validar firma";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.VALIDASIG) 
					.entornId(validacio.getEntornId()) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new ValidacioFirmaException("Error validant la firma", ex);
		}
	}
	
	public List<ArxiuFirmaDetall> validarSignaturaObtenirDetalls(VerificacioFirma validacio) throws ValidacioFirmaException {
		
		
		var t0 = System.currentTimeMillis();
		var descripcio = "Obtenir informació detallada del document firmat";
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("documentContingut.length", (validacio.getDocumentContingut() != null ? validacio.getDocumentContingut().length : -1) + ""));
		parametres.add(new Parametre("firmaContingut.length", (validacio.getFirmaContingut() != null ? validacio.getFirmaContingut().length : -1) + ""));
		
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
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.VALIDASIG)
					.entornId(validacio.getEntornId())
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());

			log.debug("Informació detallada del document firmat obtinguda correctament");
			return detalls;
		} catch (Exception ex) {
			
			var error = "Error al validar signatura obtenir detalls";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.VALIDASIG) 
					.entornId(validacio.getEntornId()) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new ValidacioFirmaException(error, ex);
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
