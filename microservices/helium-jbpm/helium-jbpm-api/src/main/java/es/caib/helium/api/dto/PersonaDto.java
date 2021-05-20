/**
 * 
 */
package es.caib.helium.api.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Classe que representa una persona d'un sistema extern
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"codi"})
public class PersonaDto implements Serializable {

	public enum Sexe {
		SEXE_HOME,
		SEXE_DONA}

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
	
	
	public String getNom() {
		return nom == null ? "" : nom;
	}

	public String getLlinatges() {
		if (llinatge2 != null && llinatge2.length() > 0)
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
		StringBuffer nomSencer = new StringBuffer();
		nomSencer.append(getNom());
		if (getLlinatge1() != null && getLlinatge1().length() > 0) {
			nomSencer.append(" ");
			nomSencer.append(getLlinatge1());
		}
		if (getLlinatge2() != null && getLlinatge2().length() > 0) {
			nomSencer.append(" ");
			nomSencer.append(getLlinatge2());
		}
		return nomSencer.toString();
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

	public String getInicials() {
		String[] parts = getNomSencer().split(" ");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].length() > 2)
				sb.append(parts[i].substring(0, 1));
		}
		return sb.toString();
	}

	private static final long serialVersionUID = 1L;
}