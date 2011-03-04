/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import java.util.Date;



/**
 * Classe amb informació sobre un seient registral de sortida.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class RespostaRegistre {

	private String numero;
	private Date data;

	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}

}
