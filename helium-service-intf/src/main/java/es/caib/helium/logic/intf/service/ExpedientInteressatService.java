/**
 * 
 */
package es.caib.helium.logic.intf.service;

import java.util.List;

import es.caib.helium.logic.intf.dto.InteressatDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;


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

	public void delete(
			Long interessatId);

	public List<InteressatDto> findByExpedient(Long expedientId);

	public PaginaDto<InteressatDto> findPerDatatable(
			Long expedientId, 
			String filtre, 
			PaginacioParamsDto paginacioParams);


	InteressatDto findAmbCodiAndExpedientId(
			String codi, 
			Long expedientId); 

}
