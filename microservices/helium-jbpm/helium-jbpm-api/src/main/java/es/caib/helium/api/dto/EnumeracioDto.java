/**
 * 
 */
package es.caib.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO amb informació d'una enumeració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class EnumeracioDto extends HeretableDto implements Serializable {

	private Long id;
	private String codi;
	private String nom;
	
	/** Comptador per mostrar el número de valors. */
	private Integer numValors;

	private static final long serialVersionUID = -5244469246285004899L;
}
