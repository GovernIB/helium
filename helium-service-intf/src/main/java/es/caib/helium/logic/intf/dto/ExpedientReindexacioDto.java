/**
 * 
 */
package es.caib.helium.logic.intf.dto;

import java.util.Date;

/**
 * DTO amb informació d'una entrada a la cua de reindexacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientReindexacioDto {

	private Long id;
	private Long expedientId;
	private Date dataReindexacio;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public Date getDataReindexacio() {
		return dataReindexacio;
	}
	public void setDataReindexacio(Date dataReindexacio) {
		this.dataReindexacio = dataReindexacio;
	}
}
