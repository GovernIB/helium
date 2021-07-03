package es.caib.helium.logic.intf.service;

import es.caib.helium.logic.intf.dto.AreaJbpmIdDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;

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
