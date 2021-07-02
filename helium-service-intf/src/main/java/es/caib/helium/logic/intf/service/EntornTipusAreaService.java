package es.caib.helium.logic.intf.service;

import java.util.List;

import es.caib.helium.logic.intf.dto.EntornTipusAreaDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;

/**
 * Servei per a gestionar els tipus d'àrea de la aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntornTipusAreaService {
	
	public PaginaDto<EntornTipusAreaDto> findPerDatatable(PaginacioParamsDto paginacioParams);

	/**
	 * @return la llista d'arees configurades.
	 */
	public List<EntornTipusAreaDto> findTipusAreaByEntorn(Long entornId);
	
	public EntornTipusAreaDto findAmbCodi(String codi);
	
	public EntornTipusAreaDto findAmbId(Long entornId, Long id);
	
	public EntornTipusAreaDto create(Long entornId, EntornTipusAreaDto entornTipusArea);
	
	public EntornTipusAreaDto update(Long entornId, EntornTipusAreaDto entornTipusArea);
	
	public void delete(Long entornTipusAreaId);
}
