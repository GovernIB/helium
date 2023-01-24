/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


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

	private Long id;
	private String varCodi;
	private Object varValor;

	private Long campId;
	private CampTipusDto campTipus;
	private String campEtiqueta;
	private boolean campMultiple;
	private boolean campOcult;
	private String[] campParams;

	private String jbpmAction;
	private String observacions;
	private String definicioProcesKey;

	private String text;
	private List<DadaListDto> multipleDades;
	private List<DadaListDto> registreDades;
	private List<ValidacioDto> validacions;

	private String error;

	private boolean required; // Si es obligatori a dins camp tipus registre
	private boolean llistar;  // Si s'ha de llistar dins camp tipus registre
	private int ordre;
	private Long agrupacioId;

	@Builder.Default
	private boolean visible = true;
	@Builder.Default
	private boolean editable = true;
	@Builder.Default
	private boolean obligatori = false;
}
