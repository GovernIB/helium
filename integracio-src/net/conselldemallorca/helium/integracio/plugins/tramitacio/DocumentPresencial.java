/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.tramitacio;


/**
 * 
 * @author Limit Tecnologies
 */
public class DocumentPresencial {

	protected String tipus;
	protected String documentCompulsar;
	protected String fotocopia;
	protected String signatura;

	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	public String getDocumentCompulsar() {
		return documentCompulsar;
	}
	public void setDocumentCompulsar(String documentCompulsar) {
		this.documentCompulsar = documentCompulsar;
	}
	public String getFotocopia() {
		return fotocopia;
	}
	public void setFotocopia(String fotocopia) {
		this.fotocopia = fotocopia;
	}
	public String getSignatura() {
		return signatura;
	}
	public void setSignatura(String signatura) {
		this.signatura = signatura;
	}

}
