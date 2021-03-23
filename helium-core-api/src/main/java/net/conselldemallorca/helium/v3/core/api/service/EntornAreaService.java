package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.EntornAreaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;

/**
 * Servei per a gestionar les àres de la aplicació segons l'entorn.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntornAreaService {

	/**
	 * @return la llista d'arees configurades.
	 */
	
	public PaginaDto<EntornAreaDto> findPerDatatable(PaginacioParamsDto paginacioParams);
	
	public List<EntornAreaDto> findAreesByEntorn(Long entornId);

	public List<EntornAreaDto> findPossiblesParesByEntorn(Long entornId, Long id);
	
	public List<PersonaDto> findPersones();
	
	public EntornAreaDto findAmbId(Long entornId, Long id);
	
	public EntornAreaDto findAmbCodiByEntorn(String codi, Long entornId);
	
	public EntornAreaDto create(Long entornId, EntornAreaDto entornArea);
	
	public EntornAreaDto update(Long entornId, EntornAreaDto entornArea);
	
	public void delete(Long entornAreaId);
}
