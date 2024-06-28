package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalDto;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalFiltreDto;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;

public interface ConsultaPinbalService {

	PaginaDto<PeticioPinbalDto> findAmbFiltrePaginat(PaginacioParamsDto paginacioParams, PeticioPinbalFiltreDto filtreDto);
	
	PeticioPinbalDto findById(Long peticioPinbalId) throws PermisDenegatException;
	
	PeticioPinbalDto findByExpedientAndDocumentStore(Long expedientId, Long documentStoreId);
	
	List<PeticioPinbalDto> findConsultesPinbalPerExpedient(Long expedientId);
}