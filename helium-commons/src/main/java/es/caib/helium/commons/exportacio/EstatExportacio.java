/**
 * 
 */
package es.caib.helium.commons.exportacio;

import java.io.Serializable;
import java.util.List;

import es.caib.helium.commons.dto.EstatDto;
import es.caib.helium.commons.dto.PermisEstatDto;
import es.caib.helium.commons.dto.regles.EstatAccioDto;
import es.caib.helium.commons.dto.regles.EstatReglaDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


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
	private List<EstatAccioDto> accionsEntrada;
	private List<EstatAccioDto> accionsSortida;
	private List<EstatDto> estatsSortida;
}
