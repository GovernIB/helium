/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.util.Date;

/**
 * Command per gestionar els documents d'un expedient
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class DocumentExpedientCommand {

	private Long docId;
	private Date data;
	private byte[] contingut;
	private String nom;



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
	public byte[] getContingut() {
		return contingut;
	}
	public void setContingut(byte[] contingut) {
		this.contingut = contingut;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

}
