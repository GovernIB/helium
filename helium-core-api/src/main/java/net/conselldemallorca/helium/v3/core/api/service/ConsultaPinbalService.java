package net.conselldemallorca.helium.v3.core.api.service;

import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalDto;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalFiltreDto;

public interface ConsultaPinbalService {

	PaginaDto<PeticioPinbalDto> findAmbFiltrePaginat(PaginacioParamsDto paginacioParams, PeticioPinbalFiltreDto filtreDto);
	
	PeticioPinbalDto findById(Long peticioPinbalId);
}
