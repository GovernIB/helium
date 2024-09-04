/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.InteressatDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;


/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientInteressatService {


	public InteressatDto create(
			InteressatDto interessat);

	public InteressatDto update(
			InteressatDto interessat);
	
	
	public InteressatDto findOne(
			Long interessatId);

	public InteressatDto delete(
			Long interessatId);

	public List<InteressatDto> findByExpedient(Long expedientId);

	public PaginaDto<InteressatDto> findPerDatatable(
			Long expedientId, 
			String filtre, 
			PaginacioParamsDto paginacioParams);

	public List<String> checkMidaCampsNotificacio(List<Long> idsInteressats);

	InteressatDto findAmbCodiAndExpedientId(
			String codi, 
			Long expedientId); 
	
	InteressatDto findByCodi(String codi);
	
	public InteressatDto createRepresentant(
			Long interessatId,
			InteressatDto representant);
	
	public List<InteressatDto> findRepresentantsExpedient(
			Long expedientId);
	
	public void deleteOrUnassignRepresentant(
			Long representantId,
			Long interessatId);
}
