/**
 *
 */
package es.caib.helium.commons.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Classe que representa una persona d'un sistema extern
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class PersonaDto implements Serializable {
	public PersonaDto() {
		super();
	}

	private String codi;
	private String nom;
	private String llinatge1;
	private String llinatge2;
	private String dni;
	private String email;
	private String telefon;
	private Sexe sexe;
	private String relleu;
	private String contrasenya;
	private InteressatTipusEnumDto tipus;
	private String codiDir3;
	private String raoSocial;
	private boolean admin;

	public PersonaDto(String codi, String nomSencer, String email, Sexe sexe) {
		this.codi = codi;
		this.setNomSencer(nomSencer);
		this.email = email;
		this.sexe = sexe;
	}
	public PersonaDto(String codi, String nom, String llinatges, String email, Sexe sexe) {
		this.codi = codi;
		this.setNom(nom);
		this.setLlinatges(llinatges);
		this.email = email;
		this.sexe = sexe;
	}


	public String getLlinatges() {
		if (llinatge2 != null && !llinatge2.isEmpty())
			return getLlinatge1() + " " + getLlinatge2();
		else
			return getLlinatge1();
	}
	public void setLlinatges(String llinatges) {
		if (llinatges != null) {
			int index = llinatges.indexOf(" ");
			if (index == -1) {
				setLlinatge1(llinatges);
				setLlinatge2(null);
			} else {
				setLlinatge1(llinatges.substring(0, index));
				setLlinatge2(llinatges.substring(index + 1));
			}
		}
	}

	public String getNomSencer() {
		StringBuilder nomSencer = new StringBuilder();
		nomSencer.append(getNom());
		if (getLlinatge1() != null && !getLlinatge1().isEmpty()) {
			nomSencer.append(" ");
			nomSencer.append(getLlinatge1());
		}
		if (getLlinatge2() != null && !getLlinatge2().isEmpty()) {
			nomSencer.append(" ");
			nomSencer.append(getLlinatge2());
		}
		return nomSencer.toString();
	}

	public String getNomSencerCodi() {
		StringBuilder codiObfuscate = new StringBuilder(" (");
		if (codi == null || codi.isEmpty()) {
			codiObfuscate.append("****");
		} else {
			int lastIndex = codi.length() > 4? codi.length() - 4 : codi.length() / 2;
			for(int i = 0; i < lastIndex; i++) {
				codiObfuscate.append("*");
			}
			codiObfuscate.append(codi.substring(lastIndex));
		}
		codiObfuscate.append(")");
		return getNomSencer() + codiObfuscate.toString();
	}

	@Override
	public String toString() {
		return getNomSencer();
	}
	public void setNomSencer(String nomSencer) {
		if (nomSencer != null) {
			int index = nomSencer.indexOf(" ");
			if (index != -1) {
				setNom(nomSencer.substring(0, index));
				setLlinatges(nomSencer.substring(index + 1));
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersonaDto other = (PersonaDto) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;
}
