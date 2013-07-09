/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;



/**
 * DTO amb informació d'una persona.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PersonaDto {

	public enum Sexe {
		SEXE_HOME,
		SEXE_DONA}

	private String codi;
	private String nom;
	private String llinatge1;
	private String llinatge2;
	private String dni;
	private String email;
	private Sexe sexe;
	private String relleu;
	private String contrasenya;

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getLlinatge1() {
		return llinatge1;
	}
	public void setLlinatge1(String llinatge1) {
		this.llinatge1 = llinatge1;
	}
	public String getLlinatge2() {
		return llinatge2;
	}
	public void setLlinatge2(String llinatge2) {
		this.llinatge2 = llinatge2;
	}
	public String getDni() {
		return dni;
	}
	public void setDni(String dni) {
		this.dni = dni;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Sexe getSexe() {
		return sexe;
	}
	public void setSexe(Sexe sexe) {
		this.sexe = sexe;
	}
	public String getRelleu() {
		return relleu;
	}
	public void setRelleu(String relleu) {
		this.relleu = relleu;
	}
	public String getContrasenya() {
		return contrasenya;
	}
	public void setContrasenya(String contrasenya) {
		this.contrasenya = contrasenya;
	}

	public String getNomSencer() {
		if (getNom() == null)
			return "[" + getCodi() + "]";
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

}
