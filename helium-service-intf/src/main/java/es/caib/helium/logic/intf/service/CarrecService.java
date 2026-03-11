package es.caib.helium.logic.intf.service;

import es.caib.helium.commons.dto.CarrecJbpmIdDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;

/**
 * Servei per a gestionar els càrrecs de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface CarrecService {

	/**
	 * @return la llista de càrrecs configurats.
	 */
	public PaginaDto<CarrecJbpmIdDto> findConfigurats(PaginacioParamsDto params);
	
	public PaginaDto<CarrecJbpmIdDto> findSenseConfigurar(PaginacioParamsDto params);
	
	public CarrecJbpmIdDto findAmbId(Long id);

	public CarrecJbpmIdDto findAmbCodi(String codi);
	
	public CarrecJbpmIdDto findAmbCodiAndGrup(String codi, String grup);
	
	public CarrecJbpmIdDto create(CarrecJbpmIdDto carrec);

	public CarrecJbpmIdDto update(CarrecJbpmIdDto carrec);

	public void delete(Long carrecId);
}
