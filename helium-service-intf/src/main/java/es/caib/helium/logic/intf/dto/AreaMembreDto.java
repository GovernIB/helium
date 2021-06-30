/**
 * 
 */
package es.caib.helium.logic.intf.dto;

/**
 * DTO amb informació d'un membre d'una àrea.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AreaMembreDto {

	private Long id;
	private String codi;
	private Long areaId;
	
	private String carrec;

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
	public Long getAreaId() {
		return areaId;
	}
	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}
	public String getCarrec() {
		return carrec;
	}
	public void setCarrec(String carrec) {
		this.carrec = carrec;
	}
}
