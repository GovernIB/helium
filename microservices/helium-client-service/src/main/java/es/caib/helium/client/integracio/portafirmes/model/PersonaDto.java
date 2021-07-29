package es.caib.helium.client.integracio.portafirmes.model;

import es.caib.helium.client.integracio.notificacio.enums.InteressatTipusEnum;
import es.caib.helium.client.integracio.persones.enums.Sexe;
import es.caib.helium.client.integracio.persones.model.Persona;
import lombok.Data;

@Data
public class PersonaDto  {
	
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
	private InteressatTipusEnum tipus;
	private String codiDir3;
	
	private boolean admin;

	public PersonaDto() {

	}

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
		if (llinatge1 != null && !llinatge1.isEmpty()) {
			nomSencer += " " + llinatge1;
		}
		if (llinatge2 != null && !llinatge2.isEmpty()) {
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
}
