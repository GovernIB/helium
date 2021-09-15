/**
 * 
 */
package es.caib.helium.logic.intf.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DTO amb informació d'una agrupació de camps.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CampAgrupacioDto extends HeretableDto {

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private int ordre;

	private static final long serialVersionUID = -1156854629101439726L;
}
