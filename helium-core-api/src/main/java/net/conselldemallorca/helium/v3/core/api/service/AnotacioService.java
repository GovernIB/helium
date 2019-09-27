package net.conselldemallorca.helium.v3.core.api.service;

import net.conselldemallorca.helium.v3.core.api.dto.AnotacioFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioListDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;;

/**
 * Servei per a la consulta i gestió d'anotacions de distribució.
 * 
 */
public interface AnotacioService {

	/** Mètode per consultar en el llistat les diferents anotacions */
	public PaginaDto<AnotacioListDto> findAmbFiltrePaginat(
			Long entornId, 
			AnotacioFiltreDto filtreDto,
			PaginacioParamsDto paginacioParams);	
}