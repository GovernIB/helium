/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.io.Serializable;

/**
 * Classe per guardar el codi i el text associat a un registre
 * d'una consulta de domini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DominiCodiDescripcio implements Serializable {

	private String codi;
	private String descripcio;



	public DominiCodiDescripcio(String codi, String descripcio) {
		super();
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

	public String toString() {
		return descripcio;
	}

	private static final long serialVersionUID = -2242121239302343489L;

}
