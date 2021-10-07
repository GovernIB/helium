/**
 * 
 */
package net.conselldemallorca.helium.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * DTO amb informació d'una àrea.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AreaDto {

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private Set<AreaDto> fills;
	private Set<AreaMembreDto> membres;

}
