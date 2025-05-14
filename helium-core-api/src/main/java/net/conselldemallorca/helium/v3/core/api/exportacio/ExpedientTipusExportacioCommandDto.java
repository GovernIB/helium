/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exportacio;

import java.util.List;
import java.util.Map;


/**
 * DTO amb informació de la comanda d'exportació on s'indica què exportar del tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientTipusExportacioCommandDto {

	private Long id = null;

	// Camps per a la importació
	private boolean dadesBasiques;
	private boolean sobreEscriure;
	private boolean desplegarDefinicions;
	private boolean actualitzarExistents;
	/** Codi per a la nova importació. */
	private String codi;
	
	// Camps comuns per exportació i importació
	private List<String> estats;
	private List<String> variables;
	private List<String> agrupacions;
	private List<String> definicionsProces;
	private Map<String, Integer> definicionsVersions;
	private boolean integracioSistra;
	private boolean integracioForms;
	private List<String> enumeracions;
	private List<String> documents;
	private List<String> terminis;
	private List<String> accions;
	private List<String> dominis;
	private List<String> redireccions;
	private List<String> consultes;
	
	/** Indica el codi de l'expedient tipus pare del qual hereta */
	private String expedientTipusPare;
	/** Indica si exportar les dades de relación amb tasques heretades i variables*/
	private boolean tasquesHerencia;

	// Dades de la integració amb arxiu
	private boolean ntiActiu;
	private String ntiSerieDocumental;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public boolean isDadesBasiques() {
		return dadesBasiques;
	}
	public void setDadesBasiques(boolean dadesBasiques) {
		this.dadesBasiques = dadesBasiques;
	}
	public boolean isSobreEscriure() {
		return sobreEscriure;
	}
	public void setSobreEscriure(boolean sobreEscriure) {
		this.sobreEscriure = sobreEscriure;
	}
	public boolean isDesplegarDefinicions() {
		return desplegarDefinicions;
	}
	public void setDesplegarDefinicions(boolean desplegarDefinicions) {
		this.desplegarDefinicions = desplegarDefinicions;
	}
	public boolean isActualitzarExistents() {
		return actualitzarExistents;
	}
	public void setActualitzarExistents(boolean actualitzarExistents) {
		this.actualitzarExistents = actualitzarExistents;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public List<String> getEstats() {
		return estats;
	}
	public void setEstats(List<String> estats) {
		this.estats = estats;
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
	public List<String> getDefinicionsProces() {
		return definicionsProces;
	}
	public void setDefinicionsProces(List<String> definicionsProces) {
		this.definicionsProces = definicionsProces;
	}
	public Map<String, Integer> getDefinicionsVersions() {
		return definicionsVersions;
	}
	public void setDefinicionsVersions(Map<String, Integer> definicionsVersions) {
		this.definicionsVersions = definicionsVersions;
	}
	public boolean isIntegracioSistra() {
		return integracioSistra;
	}
	public void setIntegracioSistra(boolean integracioSistra) {
		this.integracioSistra = integracioSistra;
	}
	public boolean isIntegracioForms() {
		return integracioForms;
	}
	public void setIntegracioForms(boolean integracioForms) {
		this.integracioForms = integracioForms;
	}
	public List<String> getEnumeracions() {
		return enumeracions;
	}
	public void setEnumeracions(List<String> enumeracions) {
		this.enumeracions = enumeracions;
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
	public List<String> getDominis() {
		return dominis;
	}
	public void setDominis(List<String> dominis) {
		this.dominis = dominis;
	}
	public List<String> getRedireccions() {
		return redireccions;
	}
	public void setRedireccions(List<String> redireccions) {
		this.redireccions = redireccions;
	}
	public List<String> getConsultes() {
		return consultes;
	}
	public void setConsultes(List<String> consultes) {
		this.consultes = consultes;
	}
	public boolean isTasquesHerencia() {
		return tasquesHerencia;
	}
	public void setTasquesHerencia(boolean tasquesHerencia) {
		this.tasquesHerencia = tasquesHerencia;
	}

	public String getExpedientTipusPare() {
		return expedientTipusPare;
	}

	public void setExpedientTipusPare(String expedientTipusPare) {
		this.expedientTipusPare = expedientTipusPare;
	}
	public boolean isNtiActiu() {
		return ntiActiu;
	}
	public void setNtiActiu(boolean ntiActiu) {
		this.ntiActiu = ntiActiu;
	}
	public String getNtiSerieDocumental() {
		return ntiSerieDocumental;
	}
	public void setNtiSerieDocumental(String ntiSerieDocumental) {
		this.ntiSerieDocumental = ntiSerieDocumental;
	}
	
}
