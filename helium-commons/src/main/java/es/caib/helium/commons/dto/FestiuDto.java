/**
 * 
 */
package es.caib.helium.commons.dto;

import java.util.Date;

/**
 * DTO amb informació d'un dia festiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FestiuDto {

	private Long id;
	private Date data;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}

}
