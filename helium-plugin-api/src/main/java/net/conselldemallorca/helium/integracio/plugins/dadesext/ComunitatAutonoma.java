/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.dadesext;

import java.io.Serializable;

/**
 * Informació d'una comunitat autònoma.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ComunitatAutonoma implements Serializable {

	private Long codi;
	private Long codiPais;
	private String nom;

	public ComunitatAutonoma(
			Long codi,
			Long codiPais,
			String nom) {
		this.codi = codi;
		this.codiPais = codiPais;
		this.nom = nom;
	}

	public Long getCodi() {
		return codi;
	}
	public void setCodi(Long codi) {
		this.codi = codi;
	}

	public Long getCodiPais() {
		return codiPais;
	}
	public void setCodiPais(Long codiPais) {
		this.codiPais = codiPais;
	}

	public String getNom() {
		return nom;
	}
	public void setDescripcio(String nom) {
		this.nom = nom;
	}

	private static final long serialVersionUID = -5602898182576627524L;

}
