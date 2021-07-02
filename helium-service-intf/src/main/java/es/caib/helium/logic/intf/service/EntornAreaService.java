package es.caib.helium.logic.intf.service;

import java.util.List;

import es.caib.helium.logic.intf.dto.EntornAreaDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.PersonaDto;

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
