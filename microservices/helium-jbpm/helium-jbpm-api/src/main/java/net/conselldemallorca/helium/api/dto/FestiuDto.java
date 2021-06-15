/**
 * 
 */
package net.conselldemallorca.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * DTO amb informació d'un dia festiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class FestiuDto {

	private Long id;
	private Date data;

}
