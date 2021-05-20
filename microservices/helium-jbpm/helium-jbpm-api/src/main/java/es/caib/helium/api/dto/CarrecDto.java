/**
 * 
 */
package es.caib.helium.api.dto;

import es.caib.helium.api.dto.PersonaDto.Sexe;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO amb informació d'un càrrec.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class CarrecDto {

	private Long id;
	private String codi;
	private String nomHome;
	private String nomDona;
	private String tractamentHome;
	private String tractamentDona;
	private String descripcio;
	private String personaCodi;
	
	private Sexe personaSexe;
	private Long areaId;
	private EntornAreaDto area;

}
