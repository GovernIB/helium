/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura.afirma;

/**
 * Informació sobre un certificat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesCertificat {

	private String tipoCertificado;
	private String subject;
	private String nombreResponsable;
	private String primerApellidoResponsable;
	private String segundoApellidoResponsable;
	private String nifResponsable;
	private String idEmisor;
	private String nifCif;
	private String email;
	private String fechaNacimiento;
	private String razonSocial;
	private String clasificacion;
	private String numeroSerie;



	public String getTipoCertificado() {
		return tipoCertificado;
	}
	public void setTipoCertificado(String tipoCertificado) {
		this.tipoCertificado = tipoCertificado;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getNombreResponsable() {
		return nombreResponsable;
	}
	public void setNombreResponsable(String nombreResponsable) {
		this.nombreResponsable = nombreResponsable;
	}
	public String getPrimerApellidoResponsable() {
		return primerApellidoResponsable;
	}
	public void setPrimerApellidoResponsable(String primerApellidoResponsable) {
		this.primerApellidoResponsable = primerApellidoResponsable;
	}
	public String getSegundoApellidoResponsable() {
		return segundoApellidoResponsable;
	}
	public void setSegundoApellidoResponsable(String segundoApellidoResponsable) {
		this.segundoApellidoResponsable = segundoApellidoResponsable;
	}
	public String getNifResponsable() {
		return nifResponsable;
	}
	public void setNifResponsable(String nifResponsable) {
		this.nifResponsable = nifResponsable;
	}
	public String getIdEmisor() {
		return idEmisor;
	}
	public void setIdEmisor(String idEmisor) {
		this.idEmisor = idEmisor;
	}
	public String getNifCif() {
		return nifCif;
	}
	public void setNifCif(String nifCif) {
		this.nifCif = nifCif;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFechaNacimiento() {
		return fechaNacimiento;
	}
	public void setFechaNacimiento(String fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}
	public String getRazonSocial() {
		return razonSocial;
	}
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	public String getClasificacion() {
		return clasificacion;
	}
	public void setClasificacion(String clasificacion) {
		this.clasificacion = clasificacion;
	}
	public String getNumeroSerie() {
		return numeroSerie;
	}
	public void setNumeroSerie(String numeroSerie) {
		this.numeroSerie = numeroSerie;
	}

}
