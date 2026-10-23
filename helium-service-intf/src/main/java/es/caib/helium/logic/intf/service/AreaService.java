package es.caib.helium.logic.intf.service;

import es.caib.helium.commons.dto.AreaBpmIdDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;

/**
 * Servei per a gestionar les arees bpm de l'aplicació
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AreaService {

	public AreaBpmIdDto findAmbId(Long id);

	public AreaBpmIdDto findAmbCodi(String codi);

	public PaginaDto<AreaBpmIdDto> findConfigurades(PaginacioParamsDto paginacioParams);

	public PaginaDto<AreaBpmIdDto> findSenseConfigurar(PaginacioParamsDto paginacioParams);

	public AreaBpmIdDto create(AreaBpmIdDto area);

	public AreaBpmIdDto update(AreaBpmIdDto area);

	public void delete(Long areaId);

}
