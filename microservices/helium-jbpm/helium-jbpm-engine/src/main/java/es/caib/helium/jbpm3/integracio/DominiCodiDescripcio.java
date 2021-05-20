/**
 * 
 */
package es.caib.helium.jbpm3.integracio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Classe per guardar el codi i el text associat a un registre
 * d'una consulta de domini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@AllArgsConstructor
public class DominiCodiDescripcio implements Serializable {

	private String codi;
	private String descripcio;

	public String toString() {
		return descripcio;
	}

	private static final long serialVersionUID = -2242121239302343489L;
}
