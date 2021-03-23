package net.conselldemallorca.helium.v3.core.api.service;

import net.conselldemallorca.helium.v3.core.api.dto.AreaMembreDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;

/**
 * Servei per a gestionar els membres d'una àrea de la aplicació segons l'entorn.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntornAreaMembreService {
	
	public PaginaDto<AreaMembreDto> findPerDatatable(PaginacioParamsDto paginacioParams); 
	
	public AreaMembreDto findAmbCodiAndAreaId(String codi, Long areaId);

	public AreaMembreDto create(Long entornId, Long carrecId, AreaMembreDto areaMembre);
	
	public void delete(Long entornAreaId, Long id);
}
