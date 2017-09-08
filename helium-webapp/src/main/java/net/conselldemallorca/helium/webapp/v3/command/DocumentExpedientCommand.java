/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

/**
 * Command per gestionar els documents d'un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentExpedientCommand {

	private String documentCodi;
	
	private Long docId;
	private Date data;
	private String nomArxiu;
	private MultipartFile arxiu;
	private String nom;
	private String codi;

	public DocumentExpedientCommand() {}

	public Long getDocId() {
		return docId;
	}
	public void setDocId(Long docId) {
		this.docId = docId;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public MultipartFile getArxiu() {
		return arxiu;
	}
	public void setArxiu(MultipartFile arxiu) {
		this.arxiu = arxiu;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNomArxiu() {
		return nomArxiu;
	}
	public void setNomArxiu(String nomArxiu) {
		this.nomArxiu = nomArxiu;
	}
	
	public String getDocumentCodi() {
 		return documentCodi;
 	}

 	public void setDocumentCodi(String documentCodi) {
 		this.documentCodi = documentCodi;
 	}
}
