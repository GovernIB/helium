package es.caib.helium.logic.intf.service;

import java.util.List;

import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.PeticioPinbalDto;
import es.caib.helium.commons.dto.PeticioPinbalFiltreDto;
import es.caib.helium.commons.dto.ServeiPinbalDto;
import es.caib.helium.commons.exception.PermisDenegatException;

public interface ConsultaPinbalService {

	PaginaDto<PeticioPinbalDto> findAmbFiltrePaginat(PaginacioParamsDto paginacioParams, PeticioPinbalFiltreDto filtreDto);
	
	PeticioPinbalDto findById(Long peticioPinbalId) throws PermisDenegatException;
	
	PeticioPinbalDto findByExpedientAndDocumentStore(Long expedientId, Long documentStoreId);
	
	List<PeticioPinbalDto> findConsultesPinbalPerExpedient(Long expedientId);
	
	PaginaDto<ServeiPinbalDto> findServeisPinbalAmbFiltrePaginat(PaginacioParamsDto paginacioParams);
	
	ServeiPinbalDto findServeiPinbalById(Long id);
	
	ServeiPinbalDto updateServeiPinbal(ServeiPinbalDto serveiPinbalDto);
}