/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;


/**
 * Objecte que representa un pais provinent d'una font externa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PaisDto implements Serializable, Comparable<PaisDto>{
	private String codi;
	private String alfa2;
	private String alfa3;
	private String nom;

	
//	public PaisDto() {
//		
//	}
//	
//	public PaisDto(
//			String codi,
//			String alfa2,
//			String alfa3,
//			String nom) {
//		this.codi = codi;
//		this.alfa2 = alfa2;
//		this.alfa3 = alfa3;
//		this.nom = nom;
//	}

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
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
	
	@Override
	public int compareTo(PaisDto o) {
		return nom.compareToIgnoreCase(o.getNom());
	}

	private static final long serialVersionUID = -8710022935513464199L;

}
