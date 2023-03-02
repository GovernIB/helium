package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.AvisDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;


/**
 * DeclaraciÃ³ dels mÃ¨todes per a la gestiÃ³ d'avisos.
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
