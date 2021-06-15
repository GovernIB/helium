/**
 * 
 */
package net.conselldemallorca.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO amb informació d'un membre d'una àrea.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class AreaMembreDto {

	private Long id;
	private String codi;
	private Long areaId;
	private String carrec;

}
