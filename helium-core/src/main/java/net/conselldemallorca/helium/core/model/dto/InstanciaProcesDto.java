/**
 * 
 */
package net.conselldemallorca.helium.core.model.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.CampAgrupacio;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;


/**
 * DTO amb informació d'una instància de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InstanciaProcesDto {

	private String id;
	private String instanciaProcesPareId;
	private String titol;
	private boolean imatgeDisponible;

	private DefinicioProces definicioProces;
	private Expedient expedient;

	private Set<Camp> camps;
	private List<Document> documents;
	private List<CampAgrupacio> agrupacions;

	private Map<String, Object> variables;
	private Map<String, DocumentDto> varsDocuments;
	private Map<String, ParellaCodiValorDto> valorsDomini;
	private Map<String, List<ParellaCodiValorDto>> valorsMultiplesDomini;
	private Map<String, Object> varsComText;



	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getInstanciaProcesPareId() {
		return instanciaProcesPareId;
	}
	public void setInstanciaProcesPareId(String instanciaProcesPareId) {
		this.instanciaProcesPareId = instanciaProcesPareId;
	}
	public String getTitol() {
		if (titol != null)
			return titol;
		return definicioProces.getJbpmKey() + " " + id;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public boolean isImatgeDisponible() {
		return imatgeDisponible;
	}
	public void setImatgeDisponible(boolean imatgeDisponible) {
		this.imatgeDisponible = imatgeDisponible;
	}
	public DefinicioProces getDefinicioProces() {
		return definicioProces;
	}
	public void setDefinicioProces(DefinicioProces definicioProces) {
		this.definicioProces = definicioProces;
	}
	public Expedient getExpedient() {
		return expedient;
	}
	public void setExpedient(Expedient expedient) {
		this.expedient = expedient;
	}
	public Set<Camp> getCamps() {
		return camps;
	}
	public void setCamps(Set<Camp> camps) {
		this.camps = camps;
	}
	public List<Document> getDocuments() {
		return documents;
	}
	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
	public List<CampAgrupacio> getAgrupacions() {
		return agrupacions;
	}
	public void setAgrupacions(List<CampAgrupacio> agrupacions) {
		this.agrupacions = agrupacions;
	}
	public Map<String, Object> getVariables() {
		return variables;
	}
	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}
	public Object getVariable(String varName) {
		return variables.get(varName);
	}
	public Map<String, DocumentDto> getVarsDocuments() {
		return varsDocuments;
	}
	public void setVarsDocuments(Map<String, DocumentDto> varsDocuments) {
		this.varsDocuments = varsDocuments;
	}
	public Map<String, ParellaCodiValorDto> getValorsDomini() {
		return valorsDomini;
	}
	public void setValorsDomini(Map<String, ParellaCodiValorDto> valorsDomini) {
		this.valorsDomini = valorsDomini;
	}
	public Map<String, List<ParellaCodiValorDto>> getValorsMultiplesDomini() {
		return valorsMultiplesDomini;
	}
	public void setValorsMultiplesDomini(
			Map<String, List<ParellaCodiValorDto>> valorsMultiplesDomini) {
		this.valorsMultiplesDomini = valorsMultiplesDomini;
	}
	public Map<String, Object> getVarsComText() {
		return varsComText;
	}
	public void setVarsComText(Map<String, Object> varsComText) {
		this.varsComText = varsComText;
	}

	public Set<String> getVariableKeys() {
		if (variables == null)
			return new HashSet<String>();
		return variables.keySet();
	}
	public Set<String> getDocumentKeys() {
		if (varsDocuments == null)
			return new HashSet<String>();
		return varsDocuments.keySet();
	}

	public List<String> getSortedDocumentKeys() {
		if (varsDocuments == null || varsDocuments.size() == 0)
			return new ArrayList<String>();
		List<String> resposta = new ArrayList<String>(varsDocuments.keySet());
		Collections.sort(
				resposta,
				new Comparator<String>() {
					public int compare(String s1, String s2) {
						DocumentDto d1 = varsDocuments.get(s1);
						DocumentDto d2 = varsDocuments.get(s2);
						if (d1 != null && d2 == null)
							return -1;
						if (d1 == null && d2 == null)
							return 0;
						if (d1 == null && d2 != null)
							return 1;
						return d1.getDataDocument().compareTo(d2.getDataDocument());
					}
				});
		return resposta;
	}

}
