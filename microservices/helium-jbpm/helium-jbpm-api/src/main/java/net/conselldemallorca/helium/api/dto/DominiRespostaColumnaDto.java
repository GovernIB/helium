/**
 * 
 */
package net.conselldemallorca.helium.api.dto;


import lombok.Getter;
import lombok.Setter;

/**
 * DTO amb informació d'una columna de resultat d'una consulta
 * de domini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class DominiRespostaColumnaDto {

	private String codi;
	private Object valor;

}
