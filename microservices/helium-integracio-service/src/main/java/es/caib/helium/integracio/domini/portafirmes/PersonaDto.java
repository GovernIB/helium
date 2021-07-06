package es.caib.helium.integracio.domini.portafirmes;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.netflix.servo.util.Strings;

import es.caib.helium.integracio.domini.persones.Persona;
import es.caib.helium.integracio.enums.notificacio.InteressatTipusEnum;
import es.caib.helium.integracio.enums.persones.Sexe;
import lombok.Data;

@Data
public class PersonaDto implements Serializable {
	
	public PersonaDto() {
		super();
	}

//	public enum Sexe {
//		SEXE_HOME,
//		SEXE_DONA}

	@NotNull
	private String codi;
	private String nom;
	private String llinatge1;
	private String llinatge2;
	@NotNull
	private String dni;
	@NotNull
	private String email;
	private String telefon;
	@NotNull
	private Sexe sexe;
	private String relleu;
	private String contrasenya;
	private InteressatTipusEnum tipus;
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
		this.nom = nom;
		this.setLlinatges(llinatges);
		this.email = email;
		this.sexe = sexe;
	}
	
	public PersonaDto(Persona persona) {
		
		codi = persona.getCodi();
		dni = persona.getDni();
		email = persona.getEmail();
		nom = persona.getNom();
		llinatge1 = persona.getLlinatge1();
		llinatge2 = persona.getLlinatge2();
		sexe = persona.getSexe();
		contrasenya = persona.getContrasenya();
	}

	public String getLlinatges() {
		
		if (llinatge2 != null && llinatge2.length() > 0) {
			return llinatge1 + " " + llinatge2;
		} 
		return llinatge1;
	}
	
	public void setLlinatges(String llinatges) {
		
		if (llinatges == null) {
			return;
		}

		int index = llinatges.indexOf(" ");
		if (index == -1) {
			llinatge1 = llinatges;
			llinatge2 = null;
			return; 
		}
		
		llinatge1 = llinatges.substring(0, index);
		llinatge2 = llinatges.substring(index + 1);			
	}
	
	public String getNomSencer() {
		
		var nomSencer = nom;
		if (!Strings.isNullOrEmpty(llinatge1)) {
			nomSencer += " " + llinatge1;
		}
		if (!Strings.isNullOrEmpty(llinatge2)) {
			nomSencer += " " + llinatge2;
		}
		return nomSencer;
	}
	
	@Override
	public String toString() {
		return getNomSencer();
	}

	public void setNomSencer(String nomSencer) {
		
		if (nomSencer == null) {
			return;
		}
		int index = nomSencer.indexOf(" ");
		if (index != -1) {
			nom = nomSencer.substring(0, index);
			setLlinatges(nomSencer.substring(index + 1));
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
