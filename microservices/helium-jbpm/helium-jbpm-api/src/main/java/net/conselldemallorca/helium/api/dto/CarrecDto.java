/**
 * 
 */
package net.conselldemallorca.helium.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO amb informació d'un càrrec.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CarrecDto {

	private Long id;
	private String codi;
	private String nomHome;
	private String nomDona;
	private String tractamentHome;
	private String tractamentDona;
	private String descripcio;
	private String personaCodi;
	
	private PersonaDto.Sexe personaSexe;
	private Long areaId;
	private EntornAreaDto area;

}
