package net.conselldemallorca.helium.integracio.plugins.procediment;

import java.util.List;

/** Classe per rebre el JSON sobre la consutla de procediments a Rolsac.
 * 
 */
public class ProcedimientosResponse {
	
	private String numeroElementos;
	private String status;
	List<ProcedimentRolsac> resultado;
	
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
	public List<ProcedimentRolsac> getResultado() {
		return resultado;
	}
	public void setResultado(List<ProcedimentRolsac> resultado) {
		this.resultado = resultado;
	}
}