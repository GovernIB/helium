/**
 * 
 */
package es.caib.helium.api.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO amb informació d'una agrupació de camps.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CampAgrupacioDto extends HeretableDto {

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private int ordre;

	private static final long serialVersionUID = -1156854629101439726L;
}
