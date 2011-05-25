/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers.tipus;


/**
 * 
 * @author Limit Tecnologies
 */
public class DocumentTramit {

	protected String nom;
	protected String identificador;
	protected int instanciaNumero;
	protected DocumentTelematic documentTelematic;
    protected DocumentPresencial documentPresencial;


	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	public int getInstanciaNumero() {
		return instanciaNumero;
	}
	public void setInstanciaNumero(int instanciaNumero) {
		this.instanciaNumero = instanciaNumero;
	}
	public DocumentTelematic getDocumentTelematic() {
		return documentTelematic;
	}
	public void setDocumentTelematic(DocumentTelematic documentTelematic) {
		this.documentTelematic = documentTelematic;
	}
	public DocumentPresencial getDocumentPresencial() {
		return documentPresencial;
	}
	public void setDocumentPresencial(DocumentPresencial documentPresencial) {
		this.documentPresencial = documentPresencial;
	}

}
