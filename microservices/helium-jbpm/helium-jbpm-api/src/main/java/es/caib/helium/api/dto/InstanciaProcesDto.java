/**
 * 
 */
package es.caib.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * DTO amb informació d'una instància de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class InstanciaProcesDto {

	private String id;
	private String instanciaProcesPareId;
	private String titol;
	private Date dataFi;
	private DefinicioProcesDto definicioProces;
	private boolean imatgeDisponible;
	
	private Set<CampDto> camps;
	private Map<String, Object> variables;
	private Map<String, DocumentDto> varsDocuments;
	public Map<String, Object> getVariables() {
		return variables;
	}

	public String getTitol() {
		if (titol != null)
			return titol;
		return definicioProces.getJbpmKey() + " " + id;
	}
	public Object getVariable(String varName) {
		if (variables == null) 
			return null;
		return variables.get(varName);
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

	public boolean isFinalitzat() {
		return dataFi != null;
	}

	public String getVariableClassAsString(String var) {
		Object valor = variables == null ? null : variables.get(var);
		if (valor != null) {
			StringBuilder sb = new StringBuilder();
			getClassAsString(sb, variables.get(var));
			return sb.toString();
		} else {
			return "<null>";
		}
	}

	private static void getClassAsString(StringBuilder sb, Object o) {
		if (o.getClass().isArray()) {
			sb.append("[");
			int length = Array.getLength(o);
			for (int i = 0; i < length; i++) {
				getClassAsString(sb, Array.get(o, i));
				if (i < length - 1)
					sb.append(", ");
			}
			sb.append("]");
		} else {
			sb.append(o.getClass().getName());
		}
	}

}
