package net.conselldemallorca.helium.integracio.plugins.procediment;

import java.util.List;

/** Classe per rebre el JSON sobre la consutla de procediments a Rolsac.
 * 
 */
public class ServiciosResponse {
	
	private String numeroElementos;
	private String status;
	List<ServeiRolsac> resultado;
	
	public String getNumeroElementos() {
		return numeroElementos;
	}
	public void setNumeroElementos(String numeroElementos) {
		this.numeroElementos = numeroElementos;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<ServeiRolsac> getResultado() {
		return resultado;
	}
	public void setResultado(List<ServeiRolsac> resultado) {
		this.resultado = resultado;
	}
}