/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exportacio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.conselldemallorca.helium.v3.core.api.dto.PermisEstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.regles.EstatReglaDto;

import java.io.Serializable;
import java.util.List;


/**
 * DTO amb informació d'un estat de l'expedient per a l'exportació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor @AllArgsConstructor
public class EstatExportacio implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@EqualsAndHashCode.Include
	private String codi;
	private String nom;
	private int ordre;

	private List<EstatReglaDto> regles;
	private List<PermisEstatDto> permisos;
	// TODO:
//	private List<EstatAccioDto> accions;
	
}
