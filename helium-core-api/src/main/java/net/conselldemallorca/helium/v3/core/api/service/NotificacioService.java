package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;

/**
 * Servei per a la consulta i gestió Notificacions enviades al NOTIB. Poden veure notificacions i realitzar accions 
 * sobre notificacions  els usuaris administradors d'Helium. Poden veure notificacions els usuaris amb permís de lectura sobre
 * el tipus d'expedient des de la pipella de notificacions de l'expedient.
 * 
 */
public interface NotificacioService {

	/** Mètode per consultar en el llistat les diferents notificacions */
	public PaginaDto<NotificacioDto> findAmbFiltrePaginat(
			Long entornId,
			List<ExpedientTipusDto> expedientTipusDtoAccessibles,
			NotificacioFiltreDto filtreDto,
			PaginacioParamsDto paginacioParams);
	
	/** Mètode per consultar el llistat d'identificadors de les notificacions a partir d'un filtre. */
	public List<Long> findIdsAmbFiltre(
			Long entornId, 
			List<ExpedientTipusDto> expedientTipusDtoAccessibles,
			NotificacioFiltreDto filtreDto);

	/** Mètode per consultar una notificació per identificador.
	 * 
	 * @param id
	 * @return
	 */
	public NotificacioDto findAmbId(Long id);


}