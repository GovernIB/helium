package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.DocumentNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
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
	public PaginaDto<DocumentNotificacioDto> findAmbFiltrePaginat(
			Long entornId,
			List<ExpedientTipusDto> expedientTipusDtoAccessibles,
			DocumentNotificacioDto filtreDto,
			PaginacioParamsDto paginacioParams);
	
	/** Mètode per consultar una notificació per identificador.
	 * 
	 * @param id
	 * @return
	 */
	public DocumentNotificacioDto findAmbId(Long id);


}