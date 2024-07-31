/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.dadesext;

import java.io.Serializable;

/**
 * Informació d'una unitat organitzativa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntitatGeografica implements Serializable {

	private String codi;
	private String descripcio;

	public EntitatGeografica(
			String codi,
			String descripcio) {
		this.codi = codi;
		this.descripcio = descripcio;
	}

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}

	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	private static final long serialVersionUID = -5602898182576627524L;

}
