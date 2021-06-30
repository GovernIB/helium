package es.caib.helium.logic.intf.service;

import es.caib.helium.logic.intf.dto.AreaMembreDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;

/**
 * Servei per a gestionar els membres d'una àrea de la aplicació segons l'entorn.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntornAreaMembreService {
	
	public PaginaDto<AreaMembreDto> findPerDatatable(Long entornAreaId, PaginacioParamsDto paginacioParams); 
	
	public AreaMembreDto findAmbCodiAndAreaId(String codi, Long areaId);

	public AreaMembreDto create(Long entornId, Long carrecId, AreaMembreDto areaMembre);
	
	public void delete(Long entornAreaId, Long id);
}
