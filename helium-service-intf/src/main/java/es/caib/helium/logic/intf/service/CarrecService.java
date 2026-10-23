package es.caib.helium.logic.intf.service;

import es.caib.helium.commons.dto.CarrecBpmIdDto;
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
	public PaginaDto<CarrecBpmIdDto> findConfigurats(PaginacioParamsDto params);

	public PaginaDto<CarrecBpmIdDto> findSenseConfigurar(PaginacioParamsDto params);

	public CarrecBpmIdDto findAmbId(Long id);

	public CarrecBpmIdDto findAmbCodi(String codi);

	public CarrecBpmIdDto findAmbCodiAndGrup(String codi, String grup);

	public CarrecBpmIdDto create(CarrecBpmIdDto carrec);

	public CarrecBpmIdDto update(CarrecBpmIdDto carrec);

	public void delete(Long carrecId);
}
