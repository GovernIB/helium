/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

/**
 * DTO amb informació d'un membre d'una àrea.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AreaMembreDto {

	private Long id;
	private String codi;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}

}
