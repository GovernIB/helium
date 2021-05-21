package es.caib.helium.integracio.domini.persones;

import es.caib.helium.integracio.enums.persones.Sexe;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Persona {

	private String codi;
	private String nom;
	private String llinatge1;
	private String llinatge2;
	private String dni;
	private String email;
	private Sexe sexe;
	private String contrasenya;
	
	public Persona() {
		
	}

	public Persona(String codi, String nomSencer, String email, Sexe sexe) {
		this.codi = codi;
		this.setNomSencer(nomSencer);
		this.email = email;
		this.sexe = sexe;
	}

	public Persona(String codi, String nom, String llinatges, String email, Sexe sexe) {
		this.codi = codi;
		this.setNom(nom);
		this.setLlinatges(llinatges);
		this.email = email;
		this.sexe = sexe;
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
		Persona other = (Persona) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;

}
