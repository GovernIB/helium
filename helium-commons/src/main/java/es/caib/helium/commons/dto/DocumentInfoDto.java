/**
 * 
 */
package es.caib.helium.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO amb informació d'un document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentInfoDto {

	private Long id;
	private String codi;
	private String documentNom;

	private boolean plantilla;
	private NtiOrigenEnumDto ntiOrigen;
	private NtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
	private NtiTipoDocumentalEnumDto ntiTipoDocumental;
	private boolean generarNomesTasca;

	@Builder.Default
	private boolean visible = true;
	@Builder.Default
	private boolean editable = true;
	@Builder.Default
	private boolean obligatori = false;
}
