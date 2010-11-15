/**
 * 
 */
package net.conselldemallorca.helium.model.exportacio;

import java.io.Serializable;



/**
 * DTO amb informació d'un tipus d'expedient per exportar
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class ExpedientTipusExportacio implements Serializable {

	private String codi;
	private String nom;
	private Boolean teNumero;
	private Boolean teTitol;
	private Boolean demanaNumero;
	private Boolean demanaTitol;
	private String expressioNumero;
	private boolean reiniciarCadaAny;



	public ExpedientTipusExportacio(
			String codi,
			String nom) {
		this.codi = codi;
		this.nom = nom;
	}

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
	public Boolean getTeNumero() {
		return teNumero;
	}
	public void setTeNumero(Boolean teNumero) {
		this.teNumero = teNumero;
	}
	public Boolean getTeTitol() {
		return teTitol;
	}
	public void setTeTitol(Boolean teTitol) {
		this.teTitol = teTitol;
	}
	public Boolean getDemanaNumero() {
		return demanaNumero;
	}
	public void setDemanaNumero(Boolean demanaNumero) {
		this.demanaNumero = demanaNumero;
	}
	public Boolean getDemanaTitol() {
		return demanaTitol;
	}
	public void setDemanaTitol(Boolean demanaTitol) {
		this.demanaTitol = demanaTitol;
	}
	public String getExpressioNumero() {
		return expressioNumero;
	}
	public void setExpressioNumero(String expressioNumero) {
		this.expressioNumero = expressioNumero;
	}
	public boolean isReiniciarCadaAny() {
		return reiniciarCadaAny;
	}
	public void setReiniciarCadaAny(boolean reiniciarCadaAny) {
		this.reiniciarCadaAny = reiniciarCadaAny;
	}



	private static final long serialVersionUID = 1L;

}
