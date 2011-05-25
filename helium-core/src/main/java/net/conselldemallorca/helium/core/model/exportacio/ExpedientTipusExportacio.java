/**
 * 
 */
package net.conselldemallorca.helium.core.model.exportacio;

import java.io.Serializable;
import java.util.List;



/**
 * DTO amb informació d'un tipus d'expedient per exportar
 * 
 * @author Limit Tecnologies <limit@limit.es>
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
	private String sistraTramitCodi;
	private String sistraTramitMapeigCamps;
	private String sistraTramitMapeigDocuments;
	private String sistraTramitMapeigAdjunts;
	private String formextUrl;
	private String formextUsuari;
	private String formextContrasenya;
	private List<EstatExportacio> estats;



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
	public String getSistraTramitCodi() {
		return sistraTramitCodi;
	}
	public void setSistraTramitCodi(String sistraTramitCodi) {
		this.sistraTramitCodi = sistraTramitCodi;
	}
	public String getSistraTramitMapeigCamps() {
		return sistraTramitMapeigCamps;
	}
	public void setSistraTramitMapeigCamps(String sistraTramitMapeigCamps) {
		this.sistraTramitMapeigCamps = sistraTramitMapeigCamps;
	}
	public String getSistraTramitMapeigDocuments() {
		return sistraTramitMapeigDocuments;
	}
	public void setSistraTramitMapeigDocuments(String sistraTramitMapeigDocuments) {
		this.sistraTramitMapeigDocuments = sistraTramitMapeigDocuments;
	}
	public String getSistraTramitMapeigAdjunts() {
		return sistraTramitMapeigAdjunts;
	}
	public void setSistraTramitMapeigAdjunts(String sistraTramitMapeigAdjunts) {
		this.sistraTramitMapeigAdjunts = sistraTramitMapeigAdjunts;
	}
	public String getFormextUrl() {
		return formextUrl;
	}
	public void setFormextUrl(String formextUrl) {
		this.formextUrl = formextUrl;
	}
	public String getFormextUsuari() {
		return formextUsuari;
	}
	public void setFormextUsuari(String formextUsuari) {
		this.formextUsuari = formextUsuari;
	}
	public String getFormextContrasenya() {
		return formextContrasenya;
	}
	public void setFormextContrasenya(String formextContrasenya) {
		this.formextContrasenya = formextContrasenya;
	}
	public List<EstatExportacio> getEstats() {
		return estats;
	}
	public void setEstats(List<EstatExportacio> estats) {
		this.estats = estats;
	}



	private static final long serialVersionUID = 1L;

}
