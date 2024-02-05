package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParametreDto;


/**
 * Declaració dels mètodes per a la gestió de paràmetres
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ParametreService {
	
	ParametreDto create(ParametreDto parametre);

	ParametreDto update(ParametreDto parametre);

	ParametreDto delete(Long id);

	ParametreDto findById(Long id);
	
	ParametreDto findByCodi(String codi);
	
	List<ParametreDto> findAll();

	PaginaDto<ParametreDto> findPaginat(PaginacioParamsDto paginacioParams);

}
