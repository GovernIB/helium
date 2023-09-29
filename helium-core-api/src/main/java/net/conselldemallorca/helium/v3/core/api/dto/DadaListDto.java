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
public class DadaListDto {

	private String id;
	private String nom;
	private DadaValorDto valor;

	private Long campId;
	private String campCodi;
	private CampTipusDto tipus;
	private boolean registre;
	private boolean multiple;
	private boolean ocult;

	private String jbpmAction;
	private String observacions;

	private String error;

	private int agrupacioOrdre;
	private String agrupacioNom;

	private String processInstanceId;
	private Long expedientId;

	@Builder.Default
	private boolean visible = true;
	@Builder.Default
	private boolean editable = true;
	@Builder.Default
	private boolean obligatori = false;
}
