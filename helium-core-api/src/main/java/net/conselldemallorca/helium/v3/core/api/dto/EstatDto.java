/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * DTO amb informació d'un estat de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstatDto extends HeretableDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String codi;
	private String nom;
	private int ordre;
	
	/** En el cas dels tipus per estats es pot consultar la taula d'estats de sortida per informar quins són
	 * 
	 */
	private List<EstatDto> estatsSortida = new ArrayList<EstatDto>();

	private int permisCount = 0;
	private int reglesCount = 0;
	private int accionsEntradaCount = 0;
	private int accionsSortidaCount = 0;

	public EstatDto(Long id, String codi, String nom) {
		super();
		this.id = id;
		this.codi = codi;
		this.nom = nom;
	}

}
