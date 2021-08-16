/**
 * 
 */
package es.caib.helium.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DTO amb informació d'un dia festiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FestiuDto {

	private Long id;
	private Date data;

}
