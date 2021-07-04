/**
 * 
 */
package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.InteressatDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;

import javax.ejb.Stateless;
import java.util.List;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ExpedientInteressatService extends AbstractService<es.caib.helium.logic.intf.service.ExpedientInteressatService> implements es.caib.helium.logic.intf.service.ExpedientInteressatService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PaginaDto<InteressatDto> findPerDatatable(
			Long expedientId,
			String filtre, 
			PaginacioParamsDto paginacioParams){
		return getDelegateService().findPerDatatable(
				expedientId,
				filtre,
				paginacioParams);
	}

	@Override
	public InteressatDto create(InteressatDto interessat) {
		return getDelegateService().create(
				interessat);
	}

	@Override
	public InteressatDto update(InteressatDto interessat) {
		return getDelegateService().update(interessat);
	}

	@Override
	public InteressatDto findOne(Long interessatId) {
		return getDelegateService().findOne(interessatId);
	}

	@Override
	public void delete(Long interessatId) {
		getDelegateService().delete(interessatId);
	}

	@Override
	public List<InteressatDto> findByExpedient(Long expedientId) {
		return getDelegateService().findByExpedient(expedientId);
	}

	@Override
	public InteressatDto findAmbCodiAndExpedientId(String codi, Long expedientId) {
		return getDelegateService().findAmbCodiAndExpedientId(codi, expedientId);
	}

}
