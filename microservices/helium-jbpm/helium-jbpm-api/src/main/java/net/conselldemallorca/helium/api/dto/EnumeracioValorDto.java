/**
 * 
 */
package net.conselldemallorca.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO amb informació d'un valor d'una enumeració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class EnumeracioValorDto {

	private Long id;
	private String codi;
	private String nom;
	int ordre;

}
