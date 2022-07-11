/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;


/**
 * Informació d'una persona per a un enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FuncionariDto {

	private String nifFuncionari;
	private String nombreCompletFuncionari;
	private String pseudonim;
	public String getNifFuncionari() {
		return nifFuncionari;
	}
	public void setNifFuncionari(String nifFuncionari) {
		this.nifFuncionari = nifFuncionari;
	}
	public String getNombreCompletFuncionari() {
		return nombreCompletFuncionari;
	}
	public void setNombreCompletFuncionari(String nombreCompletFuncionari) {
		this.nombreCompletFuncionari = nombreCompletFuncionari;
	}
	public String getPseudonim() {
		return pseudonim;
	}
	public void setPseudonim(String pseudonim) {
		this.pseudonim = pseudonim;
	}


}
