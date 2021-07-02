/**
 * 
 */
package es.caib.helium.logic.intf.dto;

import java.util.Date;

/**
 * DTO amb informació d'un document d'una tasca de la
 * definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentNotificacioDto {

	private Long id;
	private String processInstanceId;
	private String documentNom;
	private String arxiuNom;
	private String arxiuExtensio;
	private Date dataCreacio;
	private Date dataModificacio;
	private Date dataDocument;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getDocumentNom() {
		return documentNom;
	}
	public void setDocumentNom(String documentNom) {
		this.documentNom = documentNom;
	}
	public String getArxiuNom() {
		return arxiuNom;
	}
	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
	}
	public String getArxiuExtensio() {
		return arxiuExtensio;
	}
	public void setArxiuExtensio(String arxiuExtensio) {
		this.arxiuExtensio = arxiuExtensio;
	}
	public Date getDataCreacio() {
		return dataCreacio;
	}
	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
	}
	public Date getDataModificacio() {
		return dataModificacio;
	}
	public void setDataModificacio(Date dataModificacio) {
		this.dataModificacio = dataModificacio;
	}
	public Date getDataDocument() {
		return dataDocument;
	}
	public void setDataDocument(Date dataDocument) {
		this.dataDocument = dataDocument;
	}
}
