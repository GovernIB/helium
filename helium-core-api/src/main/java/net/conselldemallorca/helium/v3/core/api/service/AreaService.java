package net.conselldemallorca.helium.v3.core.api.service;

import net.conselldemallorca.helium.v3.core.api.dto.AreaJbpmIdDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;

/**
 * Servei per a gestionar les arees jbpm de l'aplicació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AreaService {

	public AreaJbpmIdDto findAmbId(Long id);
	
	public AreaJbpmIdDto findAmbCodi(String codi);
	
	public PaginaDto<AreaJbpmIdDto> findConfigurades(PaginacioParamsDto paginacioParams);
	
	public PaginaDto<AreaJbpmIdDto> findSenseConfigurar(PaginacioParamsDto paginacioParams);
	
	public AreaJbpmIdDto create(AreaJbpmIdDto area);
	
	public AreaJbpmIdDto update(AreaJbpmIdDto area);
	
	public void delete(Long areaId);
	
}
