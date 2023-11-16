/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.procediment;

/**
 * Informació d'un procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Procediment {

	private String codigo;
	private String codigoSIA;
	private String codigoSia;
	private String nombre;

	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getCodigoSIA() {
		return codigoSIA;
	}
	public void setCodigoSIA(String codigoSIA) {
		this.codigoSIA = codigoSIA;
	}
	public String getCodigoSia() {
		return codigoSia;
	}
	public void setCodigoSia(String codigoSia) {
		this.codigoSia = codigoSia;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

}
