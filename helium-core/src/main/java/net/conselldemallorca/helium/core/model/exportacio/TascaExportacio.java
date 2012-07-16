/**
 * 
 */
package net.conselldemallorca.helium.core.model.exportacio;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import net.conselldemallorca.helium.core.model.hibernate.Tasca.TipusTasca;



/**
 * DTO amb informació d'una tasca per exportar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TascaExportacio implements Serializable {

	private String nom;
	private TipusTasca tipus;
	private String missatgeInfo;
	private String missatgeWarn;
	private String nomScript;
	private String jbpmName;
	private String expressioDelegacio;
	private String recursForm;
	private String formExtern;
	private boolean tramitacioMassiva;

	private Set<CampTascaExportacio> camps = new HashSet<CampTascaExportacio>();
	private Set<DocumentTascaExportacio> documents = new HashSet<DocumentTascaExportacio>();
	private Set<FirmaTascaExportacio> firmes = new HashSet<FirmaTascaExportacio>();
	private Set<ValidacioExportacio> validacions = new HashSet<ValidacioExportacio>();



	public TascaExportacio(
			String nom,
			TipusTasca tipus,
			String jbpmName) {
		this.nom = nom;
		this.tipus = tipus;
		this.jbpmName = jbpmName;
	}

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public TipusTasca getTipus() {
		return tipus;
	}
	public void setTipus(TipusTasca tipus) {
		this.tipus = tipus;
	}
	public String getMissatgeInfo() {
		return missatgeInfo;
	}
	public void setMissatgeInfo(String missatgeInfo) {
		this.missatgeInfo = missatgeInfo;
	}
	public String getMissatgeWarn() {
		return missatgeWarn;
	}
	public void setMissatgeWarn(String missatgeWarn) {
		this.missatgeWarn = missatgeWarn;
	}
	public String getNomScript() {
		return nomScript;
	}
	public void setNomScript(String nomScript) {
		this.nomScript = nomScript;
	}
	public String getJbpmName() {
		return jbpmName;
	}
	public void setJbpmName(String jbpmName) {
		this.jbpmName = jbpmName;
	}
	public String getExpressioDelegacio() {
		return expressioDelegacio;
	}
	public void setExpressioDelegacio(String expressioDelegacio) {
		this.expressioDelegacio = expressioDelegacio;
	}
	public String getRecursForm() {
		return recursForm;
	}
	public void setRecursForm(String recursForm) {
		this.recursForm = recursForm;
	}
	public String getFormExtern() {
		return formExtern;
	}
	public void setFormExtern(String formExtern) {
		this.formExtern = formExtern;
	}
	public boolean isTramitacioMassiva() {
		return tramitacioMassiva;
	}
	public void setTramitacioMassiva(boolean tramitacioMassiva) {
		this.tramitacioMassiva = tramitacioMassiva;
	}
	public Set<CampTascaExportacio> getCamps() {
		return camps;
	}
	public void setCamps(Set<CampTascaExportacio> camps) {
		this.camps = camps;
	}
	public void addCamp(CampTascaExportacio camp) {
		getCamps().add(camp);
	}
	public Set<DocumentTascaExportacio> getDocuments() {
		return documents;
	}
	public void setDocuments(Set<DocumentTascaExportacio> documents) {
		this.documents = documents;
	}
	public void addDocument(DocumentTascaExportacio document) {
		getDocuments().add(document);
	}
	public Set<FirmaTascaExportacio> getFirmes() {
		return firmes;
	}
	public void setFirmes(Set<FirmaTascaExportacio> firmes) {
		this.firmes = firmes;
	}
	public void addFirma(FirmaTascaExportacio firma) {
		getFirmes().add(firma);
	}
	public Set<ValidacioExportacio> getValidacions() {
		return validacions;
	}
	public void setValidacions(Set<ValidacioExportacio> validacions) {
		this.validacions = validacions;
	}
	public void addValidacio(ValidacioExportacio validacio) {
		getValidacions().add(validacio);
	}



	private static final long serialVersionUID = 1L;

}
