/**
 * 
 */
package es.caib.helium.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * DTO amb informació d'una dada de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DadaValorDto {

	private boolean registre;
	private boolean multiple;
	private int files;
	private int columnes;

	// Valor simple
	String valorSimple;

	// Valor multiple
	List<String> valorMultiple;

	// Valor registre

	Map<String, Boolean> valorHeader;
	List<List<String>> valorBody;

}
