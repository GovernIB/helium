package es.caib.helium.logic.intf.service;

import es.caib.helium.commons.dto.DocumentNotificacioDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;

/**
 * Servei per a la consulta i gestió Notificacions enviades al NOTIB. Poden veure notificacions i realitzar accions 
 * sobre notificacions  els usuaris administradors d'Helium. Poden veure notificacions els usuaris amb permís de lectura sobre
 * el tipus d'expedient des de la pipella de notificacions de l'expedient.
 * 
 */
public interface NotificacioService {

	/** Mètode per consultar en el llistat les diferents notificacions */
	public PaginaDto<DocumentNotificacioDto> findAmbFiltrePaginat(
			DocumentNotificacioDto filtreDto,
			PaginacioParamsDto paginacioParams);
	
	/** Mètode per consultar una notificació per identificador.
	 * 
	 * @param id
	 * @return
	 */
	public DocumentNotificacioDto findAmbId(Long id);


}