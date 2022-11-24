/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


/**
 * DTO amb informació d'un estat de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@NoArgsConstructor
public class EstatDto extends HeretableDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String codi;
	private String nom;
	private int ordre;

	private int permisCount = 0;
	private int reglesCount = 0;

	public EstatDto(Long id, String codi, String nom) {
		super();
		this.id = id;
		this.codi = codi;
		this.nom = nom;
	}

}
