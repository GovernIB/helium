/**
 * 
 */
package es.caib.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO amb informació d'un entorn.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class EntornDto extends ControlPermisosDto implements Serializable {

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private boolean actiu;

	private int permisCount = 0;

	// Opcions de visualització de la capçalera
	private String colorFons;
	private String colorLletra;

	private static final long serialVersionUID = 2677183498182144912L;

}
