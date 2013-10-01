/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;


/**
 * DTO amb informació d'una definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DefinicioProcesDto {

	private Long id;
	private EntornDto entorn;
	private String jbpmId;
	private String jbpmKey;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public EntornDto getEntorn() {
		return entorn;
	}
	public void setEntorn(EntornDto entorn) {
		this.entorn = entorn;
	}
	public String getJbpmId() {
		return jbpmId;
	}
	public void setJbpmId(String jbpmId) {
		this.jbpmId = jbpmId;
	}
	public String getJbpmKey() {
		return jbpmKey;
	}
	public void setJbpmKey(String jbpmKey) {
		this.jbpmKey = jbpmKey;
	}

}
