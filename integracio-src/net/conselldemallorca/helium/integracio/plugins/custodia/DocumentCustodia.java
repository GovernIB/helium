/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.custodia;

import java.io.Serializable;

/**
 * Classe que representa un document emmagatzemat a la
 * custódia documental
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class DocumentCustodia implements Serializable {

	private String id;
	private String entornCodi;
	private String expedientTipusCodi;
	private String processDefinitionKey;
	private String documentCodi;
	private String originalFileName;
	private byte[] originalFileContent;
	private String signedFileName;
	private byte[] signedFileContent;
	private String contentType;
	private String custodiaCodi;
	private byte[] signature;



	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEntornCodi() {
		return entornCodi;
	}
	public void setEntornCodi(String entornCodi) {
		this.entornCodi = entornCodi;
	}
	public String getExpedientTipusCodi() {
		return expedientTipusCodi;
	}
	public void setExpedientTipusCodi(String expedientTipusCodi) {
		this.expedientTipusCodi = expedientTipusCodi;
	}
	public String getProcessDefinitionKey() {
		return processDefinitionKey;
	}
	public void setProcessDefinitionKey(String processDefinitionKey) {
		this.processDefinitionKey = processDefinitionKey;
	}
	public String getDocumentCodi() {
		return documentCodi;
	}
	public void setDocumentCodi(String documentCodi) {
		this.documentCodi = documentCodi;
	}
	public String getOriginalFileName() {
		return originalFileName;
	}
	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}
	public byte[] getOriginalFileContent() {
		return originalFileContent;
	}
	public void setOriginalFileContent(byte[] originalFileContent) {
		this.originalFileContent = originalFileContent;
	}
	public String getSignedFileName() {
		return signedFileName;
	}
	public void setSignedFileName(String signedFileName) {
		this.signedFileName = signedFileName;
	}
	public byte[] getSignedFileContent() {
		return signedFileContent;
	}
	public void setSignedFileContent(byte[] signedFileContent) {
		this.signedFileContent = signedFileContent;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getCustodiaCodi() {
		return custodiaCodi;
	}
	public void setCustodiaCodi(String custodiaCodi) {
		this.custodiaCodi = custodiaCodi;
	}
	public byte[] getSignature() {
		return signature;
	}
	public void setSignature(byte[] signature) {
		this.signature = signature;
	}

	private static final long serialVersionUID = 1L;

}
