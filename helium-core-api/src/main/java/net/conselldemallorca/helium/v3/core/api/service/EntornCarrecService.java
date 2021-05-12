package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.CarrecDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;

/**
 * Servei per a gestionar els càrrecs de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntornCarrecService {

	public PaginaDto<CarrecDto> findPerDatatable(PaginacioParamsDto paginacioParams);
	
	/**
	 * @return la llista de carrecs segons l'entorn.
	 */
	public List<CarrecDto> findCarrecsByEntorn(Long entornId);

	public List<CarrecDto> findCarrecsByEntornAndArea(Long entornId, Long areaId);
	
	public CarrecDto findByEntornAndCodi(Long entornId, String codi);
	
	public CarrecDto findAmbId(Long entornId, Long id);
	
	public CarrecDto create(Long entornId, CarrecDto entornCarrec);
	
	public CarrecDto update(Long entornId, CarrecDto entornCarrec);
	
	public void delete(Long entornCarrecId);
}
