/**
 * 
 */
package es.caib.helium.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DTO amb informació d'un document d'una tasca de la
 * definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DocumentTascaDto extends HeretableDto {

	private static final long serialVersionUID = -8552509655331736854L;

	private Long id;

	private boolean required;
	private boolean readOnly;
	private int order;

	private DocumentDto document;
	
	/** Quan es crea una relació entre un camp i la definició de procés pot ser que el camp sigui 
	 * del tipus d'expedient i la tasca sigui de la definició de procés del tipus expedient pare heretat. Aquest
	 * camp fa referència al tipus d'expedient que posseix la relació camp-tasca.
	 */
	private Long expedientTipusId;

}
