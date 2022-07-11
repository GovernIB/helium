/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

/**
 * Resposta a una petició Pinbal
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ScspJustificant {
	
	private String documentCodi;
	private String idPeticion;
	private String nom;
	private String contentType;
	private byte[] contingut;
	

	public String getIdPeticion() {
		return idPeticion;
	}

	public void setIdPeticion(String idPeticion) {
		this.idPeticion = idPeticion;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public byte[] getContingut() {
		return contingut;
	}

	public void setContingut(byte[] contingut) {
		this.contingut = contingut;
	}

	public String getDocumentCodi() {
		return documentCodi;
	}

	public void setDocumentCodi(String documentCodi) {
		this.documentCodi = documentCodi;
	}


	
}
