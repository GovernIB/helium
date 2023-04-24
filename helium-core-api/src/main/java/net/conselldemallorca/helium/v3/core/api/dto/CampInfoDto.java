/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * DTO amb informació d'una dada de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampInfoDto {

	private String id;
	private String codi;
	private String etiqueta;

	@Builder.Default
	private boolean visible = true;
	@Builder.Default
	private boolean editable = true;
	@Builder.Default
	private boolean obligatori = false;
}
