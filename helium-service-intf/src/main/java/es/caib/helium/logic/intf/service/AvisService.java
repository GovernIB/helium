package es.caib.helium.logic.intf.service;

import java.util.List;

import es.caib.helium.commons.dto.AvisDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;

/**
 * Declaració dels mètodes per a la gestió d'avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AvisService {
	AvisDto create(AvisDto avis);

	AvisDto update(AvisDto avis);

	AvisDto updateActiva(Long id, boolean activa);

	AvisDto delete(Long id);

	AvisDto findById(Long id);

	PaginaDto<AvisDto> findPaginat(PaginacioParamsDto paginacioParams);

	List<AvisDto> findActive();
}
