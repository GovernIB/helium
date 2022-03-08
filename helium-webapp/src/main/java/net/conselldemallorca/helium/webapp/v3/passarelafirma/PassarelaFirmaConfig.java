package net.conselldemallorca.helium.webapp.v3.passarelafirma;

import java.util.Date;

import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleStatus;
import org.fundaciobit.plugins.signature.api.CommonInfoSignature;
import org.fundaciobit.plugins.signature.api.FileInfoSignature;
import org.fundaciobit.plugins.signatureweb.api.SignaturesSetWeb;

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
	private String transactionId;
	private byte[] signedData;
	private FirmaSimpleStatus firmaSimpleStatus;

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

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public byte[] getSignedData() {
		return signedData;
	}

	public void setSignedData(byte[] signedData) {
		this.signedData = signedData;
	}

	public FirmaSimpleStatus getFirmaSimpleStatus() {
		return firmaSimpleStatus;
	}

	public void setFirmaSimpleStatus(FirmaSimpleStatus firmaSimpleStatus) {
		this.firmaSimpleStatus = firmaSimpleStatus;
	}
	
	


}
