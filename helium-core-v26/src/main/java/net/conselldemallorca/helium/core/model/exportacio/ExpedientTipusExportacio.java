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
	private String formextUrl;
	private String formextUsuari;
	private String formextContrasenya;
	private String jbpmProcessDefinitionKey;
	private List<EstatExportacio> estats;
	private List<MapeigSistraExportacio> mapeigSistras;
	private List<DominiExportacio> dominis;
	private List<EnumeracioExportacio> enumeracions;
	private List<DefinicioProcesExportacio> definicionsProces;
	private List<ConsultaExportacio> consultes;
	private boolean restringirPerGrup;
	private boolean seleccionarAny;
	private boolean ambRetroaccio;
	private boolean reindexacioAsincrona;
	private String responsableDefecteCodi;
	private String diesNoLaborables;
	
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
	public String getJbpmProcessDefinitionKey() {
		return jbpmProcessDefinitionKey;
	}
	public void setJbpmProcessDefinitionKey(String jbpmProcessDefinitionKey) {
		this.jbpmProcessDefinitionKey = jbpmProcessDefinitionKey;
	}
	public List<EstatExportacio> getEstats() {
		return estats;
	}
	public void setEstats(List<EstatExportacio> estats) {
		this.estats = estats;
	}
	public List<MapeigSistraExportacio> getMapeigSistras() {
		return mapeigSistras;
	}
	public void setMapeigSistras(List<MapeigSistraExportacio> mapeigSistras) {
		this.mapeigSistras = mapeigSistras;
	}
	public List<DominiExportacio> getDominis() {
		return dominis;
	}
	public void setDominis(List<DominiExportacio> dominis) {
		this.dominis = dominis;
	}
	public List<EnumeracioExportacio> getEnumeracions() {
		return enumeracions;
	}
	public void setEnumeracions(List<EnumeracioExportacio> enumeracions) {
		this.enumeracions = enumeracions;
	}
	public List<DefinicioProcesExportacio> getDefinicionsProces() {
		return definicionsProces;
	}
	public void setDefinicionsProces(
			List<DefinicioProcesExportacio> definicionsProces) {
		this.definicionsProces = definicionsProces;
	}
	public List<ConsultaExportacio> getConsultes() {
		return consultes;
	}
	public void setConsultes(List<ConsultaExportacio> consultes) {
		this.consultes = consultes;
	}
	public boolean isRestringirPerGrup() {
		return restringirPerGrup;
	}
	public void setRestringirPerGrup(boolean restringirPerGrup) {
		this.restringirPerGrup = restringirPerGrup;
	}
	public boolean isSeleccionarAny() {
		return seleccionarAny;
	}
	public void setSeleccionarAny(boolean seleccionarAny) {
		this.seleccionarAny = seleccionarAny;
	}
	public boolean isAmbRetroaccio() {
		return ambRetroaccio;
	}
	public void setAmbRetroaccio(boolean ambRetroaccio) {
		this.ambRetroaccio = ambRetroaccio;
	}
	public boolean isReindexacioAsincrona() {
		return reindexacioAsincrona;
	}

	public void setReindexacioAsincrona(boolean reindexacioAsincrona) {
		this.reindexacioAsincrona = reindexacioAsincrona;
	}

	public String getResponsableDefecteCodi() {
		return responsableDefecteCodi;
	}
	public void setResponsableDefecteCodi(String responsableDefecteCodi) {
		this.responsableDefecteCodi = responsableDefecteCodi;
	}

	public String getDiesNoLaborables() {
		return diesNoLaborables;
	}

	public void setDiesNoLaborables(String diesNoLaborables) {
		this.diesNoLaborables = diesNoLaborables;
	}

	private static final long serialVersionUID = 1L;
}
