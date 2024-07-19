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
public class Pais implements Serializable {

	private String codi;
	private String alfa2;
	private String alfa3;
	private String nom;

	public Pais(
			Long codi,
			String alfa2,
			String alfa3,
			String nom) {
		this.setCodi(codi);
		this.alfa2 = alfa2;
		this.alfa3 = alfa3;
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
		this.codi = ("000" + sCodi).substring(sCodi.length());
	}

	public String getAlfa2() {
		return alfa2;
	}

	public void setAlfa2(String alfa2) {
		this.alfa2 = alfa2;
	}

	public String getAlfa3() {
		return alfa3;
	}

	public void setAlfa3(String alfa3) {
		this.alfa3 = alfa3;
	}

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	private static final long serialVersionUID = -5602898182576627524L;

}
