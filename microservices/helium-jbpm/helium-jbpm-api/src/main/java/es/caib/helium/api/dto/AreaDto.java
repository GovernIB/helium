/**
 * 
 */
package es.caib.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * DTO amb informació d'una àrea.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class AreaDto {

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private Set<AreaDto> fills;
	private Set<AreaMembreDto> membres;

}
