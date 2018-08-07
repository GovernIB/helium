/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers.tipus;

/**
 * Informació de referència d'un enviament retornada per Notifica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EnviamentReferencia {

	private String titularNif;
	private String referencia;

	public String getTitularNif() {
		return titularNif;
	}
	public void setTitularNif(String titularNif) {
		this.titularNif = titularNif;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

}
