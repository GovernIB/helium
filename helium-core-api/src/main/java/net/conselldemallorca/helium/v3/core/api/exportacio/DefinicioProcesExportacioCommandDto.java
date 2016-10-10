/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exportacio;

import java.util.List;


/**
 * DTO amb informació de la comanda d'exportació on s'indica què exportar de la definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DefinicioProcesExportacioCommandDto {

	private Long id = null;
	private Long expedientTipusId = null;

	// Camps per a la importació
	private boolean sobreEscriure;
	/** Codi per a la nova importació. */
	private String codi;
	
	// Camps comuns per exportació i importació
	private List<String> tasques;
	private List<String> variables;
	private List<String> agrupacions;
	private List<String> documents;
	private List<String> terminis;
	private List<String> accions;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	public boolean isSobreEscriure() {
		return sobreEscriure;
	}
	public void setSobreEscriure(boolean sobreEscriure) {
		this.sobreEscriure = sobreEscriure;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public List<String> getTasques() {
		return tasques;
	}
	public void setTasques(List<String> tasques) {
		this.tasques = tasques;
	}
	public List<String> getVariables() {
		return variables;
	}
	public void setVariables(List<String> variables) {
		this.variables = variables;
	}

	public List<String> getAgrupacions() {
		return agrupacions;
	}
	public void setAgrupacions(List<String> agrupacions) {
		this.agrupacions = agrupacions;
	}
	public List<String> getDocuments() {
		return documents;
	}
	public void setDocuments(List<String> documents) {
		this.documents = documents;
	}
	public List<String> getTerminis() {
		return terminis;
	}
	public void setTerminis(List<String> terminis) {
		this.terminis = terminis;
	}
	public List<String> getAccions() {
		return accions;
	}
	public void setAccions(List<String> accions) {
		this.accions = accions;
	}
}
