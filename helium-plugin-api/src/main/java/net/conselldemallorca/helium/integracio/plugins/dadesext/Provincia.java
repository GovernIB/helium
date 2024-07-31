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
public class Provincia implements Serializable {

	private String codi;
	private Long codiComunitat;
	private String nom;

	public Provincia(
			Long codi,
			Long codiComunitat,
			String nom) {
		this.setCodi(codi);
		this.codiComunitat = codiComunitat;
		this.nom = nom;
	}

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public void setCodi(Long lCodi) {
		String sCodi = lCodi.toString();
		this.codi = ("00" + sCodi).substring(sCodi.length());
	}

	public Long getCodiComunitat() {
		return codiComunitat;
	}
	public void setCodiComunitat(Long codiComunitat) {
		this.codiComunitat = codiComunitat;
	}

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	private static final long serialVersionUID = -5602898182576627524L;

}
