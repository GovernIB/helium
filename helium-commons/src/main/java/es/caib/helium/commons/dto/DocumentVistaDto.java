/**
 *
 */
package es.caib.helium.commons.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO amb informació d'una vista d'un document.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class DocumentVistaDto implements Serializable {

	private String arxiuNom;
	private byte[] arxiuContingut;

}
