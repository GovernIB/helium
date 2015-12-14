/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO amb informació d'una fila de resultat d'una consulta
 * de domini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DominiRespostaFilaDto {

	private List<DominiRespostaColumnaDto> columnes = new ArrayList<DominiRespostaColumnaDto>();

	public List<DominiRespostaColumnaDto> getColumnes() {
		return columnes;
	}
	public void setColumnes(List<DominiRespostaColumnaDto> columnes) {
		this.columnes = columnes;
	}
	
}
