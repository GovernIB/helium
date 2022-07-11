/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers.tipus;


/**
 * Informació d'una persona per a un enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Titular {

	private String nombreCompleto;
	private String nombre;
	private String apellido1;
	private String apellido2;
	private String documentacion;
	private String tipoDocumentacion;
	public enum ScspTipoDocumentacion {
		CIF,
		CSV,
		DNI,
		NIE,
		NIF,
		Pasaporte,
		NumeroIdentificacion,
		Otros
	}

	public String getDocumentacion() {
		return documentacion;
	}
	
	public void setDocumentacion(String documentacion) {
		this.documentacion = documentacion;
	}
	
	public String getNombreCompleto() {
		return nombreCompleto;
	}
	
	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getApellido1() {
		return apellido1;
	}
	
	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
	}
	
	public String getApellido2() {
		return apellido2;
	}
	
	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
	}
	
	public void setScspTipoDocumentacion(ScspTipoDocumentacion tipo) {
		this.tipoDocumentacion = tipo.toString();
	}

	public void setTipoDocumentacion(String tipoDocumentacion) {
		this.tipoDocumentacion = tipoDocumentacion;
	}

	public String getTipoDocumentacion() {
		return tipoDocumentacion;
	}
		

}
