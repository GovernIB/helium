/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura;

/**
 * Informació d'una certificat
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class InfoCertificat {

	private String nif;
	private String fullName;
	private String nifResponsable;
	private String surname;
	private String givenName;
	private String email;
	private boolean personaFisica;

	private String pais;
	private String organitzacio;
	private String departament;
	private String carrec;



	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getNifResponsable() {
		return nifResponsable;
	}
	public void setNifResponsable(String nifResponsable) {
		this.nifResponsable = nifResponsable;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getGivenName() {
		return givenName;
	}
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean isPersonaFisica() {
		return personaFisica;
	}
	public void setPersonaFisica(boolean personaFisica) {
		this.personaFisica = personaFisica;
	}
	public String getPais() {
		return pais;
	}
	public void setPais(String pais) {
		this.pais = pais;
	}
	public String getOrganitzacio() {
		return organitzacio;
	}
	public void setOrganitzacio(String organitzacio) {
		this.organitzacio = organitzacio;
	}
	public String getDepartament() {
		return departament;
	}
	public void setDepartament(String departament) {
		this.departament = departament;
	}
	public String getCarrec() {
		return carrec;
	}
	public void setCarrec(String carrec) {
		this.carrec = carrec;
	}

}
