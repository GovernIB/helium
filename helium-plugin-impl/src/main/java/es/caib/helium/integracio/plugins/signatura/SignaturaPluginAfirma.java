/**
 *
 */
package es.caib.helium.integracio.plugins.signatura;

import java.util.ArrayList;

import es.caib.helium.commons.config.PropertyConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fundaciobit.pluginsib.validatecertificate.InformacioCertificat;
import org.fundaciobit.pluginsib.validatesignature.api.SignatureDetailInfo;
import org.fundaciobit.pluginsib.validatesignature.api.SignatureRequestedInformation;
import org.fundaciobit.pluginsib.validatesignature.api.TimeStampInfo;
import org.fundaciobit.pluginsib.validatesignature.api.ValidateSignatureRequest;
import org.fundaciobit.pluginsib.validatesignature.api.ValidateSignatureResponse;
import org.fundaciobit.pluginsib.validatesignature.api.ValidationStatus;

import es.caib.helium.commons.exception.SistemaExternException;
import es.caib.helium.commons.utils.GlobalProperties;

/**
 * Implementació del plugin de signatura emprant els
 * serveis de @Firma.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SignaturaPluginAfirma implements SignaturaPlugin {

	@Override
	public RespostaValidacioSignatura verificarSignatura(
			byte[] documentContingut,
			byte[] firmaContingut,
			boolean obtenirDadesCertificat) throws SignaturaPluginException {

		ValidateSignatureRequest validationRequest = new ValidateSignatureRequest();
		RespostaValidacioSignatura resposta = new RespostaValidacioSignatura();

		if (documentContingut != null && firmaContingut == null) {
			firmaContingut = documentContingut;
			documentContingut = null;
		}
		if (firmaContingut != null) {
			validationRequest.setSignedDocumentData(documentContingut);
			validationRequest.setSignatureData(firmaContingut);
		} else {
			validationRequest.setSignatureData(documentContingut);
		}
		SignatureRequestedInformation sri = new SignatureRequestedInformation();
		sri.setReturnSignatureTypeFormatProfile(true);
		sri.setReturnCertificateInfo(true);
		sri.setReturnValidationChecks(false);
		sri.setValidateCertificateRevocation(false);
		sri.setReturnCertificates(false);
		sri.setReturnTimeStampInfo(true);
		validationRequest.setSignatureRequestedInformation(sri);
		ValidateSignatureResponse validateSignatureResponse;
		try {

			validateSignatureResponse = new org.fundaciobit.pluginsib.validatesignature.afirmacxf.AfirmaCxfValidateSignaturePlugin(
				PropertyConfig.PROP_BASE_PREFIX_SIGNATURA_PLUGIN,
				GlobalProperties.getInstance().toPropertiesWithPrefix(PropertyConfig.PROP_BASE_PREFIX_SIGNATURA_PLUGIN)).
			validateSignature(validationRequest);
		} catch (Exception e) {
			logger.error("Error validant signatura", e);
			throw new SistemaExternException(e);
		}

		// Completa la resposta
		String estat = RespostaValidacioSignatura.ESTAT_INVALID;
		switch(validateSignatureResponse.getValidationStatus().getStatus()) {
		case ValidationStatus.SIGNATURE_ERROR:
			estat = RespostaValidacioSignatura.ESTAT_ERROR;
			break;
		case ValidationStatus.SIGNATURE_VALID:
			estat = RespostaValidacioSignatura.ESTAT_VALID;
			break;
		case ValidationStatus.SIGNATURE_INVALID:
			estat = RespostaValidacioSignatura.ESTAT_INVALID;
			break;
		}

		resposta.setEstat(estat);
		resposta.setErrorMsg(validateSignatureResponse.getValidationStatus().getErrorMsg());
		resposta.setErrorException(validateSignatureResponse.getValidationStatus().getErrorException());
		resposta.setDadesCertificat(new ArrayList<DadesCertificat>());
		if (validateSignatureResponse.getSignatureDetailInfo() != null) {
			for (SignatureDetailInfo signatureInfo: validateSignatureResponse.getSignatureDetailInfo()) {
				DadesCertificat detall = new DadesCertificat();
				TimeStampInfo timeStampInfo = signatureInfo.getTimeStampInfo();
				if (timeStampInfo != null) {
					detall.setData(timeStampInfo.getCreationTime());
				} else {
					detall.setData(signatureInfo.getSignDate());
				}
				InformacioCertificat certificateInfo = signatureInfo.getCertificateInfo();
				if (certificateInfo != null) {
					detall.setNifResponsable(certificateInfo.getNifResponsable());
					detall.setNomResponsable(certificateInfo.getNomResponsable());
					detall.setPrimerLlinatgeResponsable(certificateInfo.getPrimerLlinatgeResponsable());
					detall.setSegonLlinatgeResponsable(certificateInfo.getSegonLlinatgeResponsable());
					detall.setSubject(certificateInfo.getSubject());
					detall.setNifResponsable(certificateInfo.getNifResponsable());
					detall.setIdEmisor(certificateInfo.getEmissorID());
					detall.setNifCif(certificateInfo.getUnitatOrganitzativaNifCif());
					detall.setEmail(certificateInfo.getEmail());
					detall.setDataNaixement(certificateInfo.getDataNaixement());
					detall.setRazonSocial(certificateInfo.getRaoSocial());
					detall.setClasificacio(certificateInfo.getClassificacioEidas());
					detall.setNumeroSerie(certificateInfo.getNumeroSerie().toString());
				}
				resposta.getDadesCertificat().add(detall);
			}
//			resposta.setPerfil(ArxiuConversions.toPerfilFirmaArxiu(validateSignatureResponse.getSignProfile()));
//			resposta.setTipus(ArxiuConversions.toFirmaTipus(
//					validateSignatureResponse.getSignType(),
//					validateSignatureResponse.getSignFormat()));
		}
		return resposta;
	}

	private static final Log logger = LogFactory.getLog(SignaturaPluginAfirma.class);
}
