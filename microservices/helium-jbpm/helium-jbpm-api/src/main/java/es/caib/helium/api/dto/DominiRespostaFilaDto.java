/**
 * 
 */
package es.caib.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO amb informació d'una fila de resultat d'una consulta
 * de domini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class DominiRespostaFilaDto {

	private List<DominiRespostaColumnaDto> columnes = new ArrayList<DominiRespostaColumnaDto>();

}
