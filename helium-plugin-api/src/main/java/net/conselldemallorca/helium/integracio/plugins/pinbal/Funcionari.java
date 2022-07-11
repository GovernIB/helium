/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.pinbal;


/**
 * Informació d'una persona per a un enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Funcionari {

	private String nombreCompletoFuncionario;
	private String nifFuncionario;
	private String seudonimo;
	
	public Funcionari() {
		
	}
	
	public Funcionari (
			String nombreCompletoFuncionario,
			String nifFuncionario,
			String seudonimo) {
		this.nombreCompletoFuncionario=nombreCompletoFuncionario;
		this.nifFuncionario=nifFuncionario;
		this.seudonimo=seudonimo;
		
	}
	public String getNombreCompletoFuncionario() {
		return nombreCompletoFuncionario;
	}
	public void setNombreCompletoFuncionario(String nombreCompletoFuncionario) {
		this.nombreCompletoFuncionario = nombreCompletoFuncionario;
	}
	public String getNifFuncionario() {
		return nifFuncionario;
	}
	public void setNifFuncionario(String nifFuncionario) {
		this.nifFuncionario = nifFuncionario;
	}
	public String getSeudonimo() {
		return seudonimo;
	}
	public void setSeudonimo(String seudonimo) {
		this.seudonimo = seudonimo;
	}
	
	
}
