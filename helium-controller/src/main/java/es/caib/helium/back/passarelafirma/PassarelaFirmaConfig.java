package es.caib.helium.back.passarelafirma;

import org.fundaciobit.plugins.signature.api.CommonInfoSignature;
import org.fundaciobit.plugins.signature.api.FileInfoSignature;
import org.fundaciobit.plugins.signatureweb.api.SignaturesSetWeb;

import java.util.Date;

/**
 * Bean amb informació sobre un o varis documents a firmar amb
 * la passarel·la.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PassarelaFirmaConfig extends SignaturesSetWeb {

	protected Long pluginId = null;
	protected final String urlFinalHelium;
	protected String documentId;

	public PassarelaFirmaConfig(
			String signaturesSetId,
			Date expiryDate,
			CommonInfoSignature commonInfoSignature,
			FileInfoSignature[] fileInfoSignatureArray,
			String urlFinal,
			String urlFinalHelium,
			String documentId) {
		super(	signaturesSetId,
				expiryDate,
				commonInfoSignature,
				fileInfoSignatureArray,
				urlFinal);
		this.urlFinalHelium = urlFinalHelium;
		this.documentId = documentId;
	}

	public Long getPluginId() {
		return pluginId;
	}
	public void setPluginId(Long pluginId) {
		this.pluginId = pluginId;
	}
	public String getUrlFinalHelium() {
		return urlFinalHelium;
	}
	public String getDocumentId() {
		return documentId;
	}

}
