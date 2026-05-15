package es.caib.helium.ejb;

import java.util.List;

import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.InteressatDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.ExpedientInteressatService;

/**
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ExpedientInteressatServiceBean extends AbstractServiceEjb<ExpedientInteressatService> implements ExpedientInteressatService {

	@Delegate ExpedientInteressatService delegateService;

	protected void setDelegateService(ExpedientInteressatService delegateService) {
		this.delegateService = delegateService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PaginaDto<InteressatDto> findPerDatatable(
			Long expedientId,
			String filtre,
			PaginacioParamsDto paginacioParams){
		return delegateService.findPerDatatable(
				expedientId,
				filtre,
				paginacioParams);
	}

	@Override
	public InteressatDto create(InteressatDto interessat) {
		return delegateService.create(
				interessat);
	}

	@Override
	public InteressatDto update(InteressatDto interessat) {
		return delegateService.update(interessat);
	}

	@Override
	public InteressatDto findOne(Long interessatId) {
		return delegateService.findOne(interessatId);
	}

	@Override
	public InteressatDto delete(Long interessatId) {
		return delegateService.delete(interessatId);
	}

	@Override
	public List<InteressatDto> findByExpedient(Long expedientId) {
		return delegateService.findByExpedient(expedientId);
	}

	@Override
	public InteressatDto findAmbCodiAndExpedientId(String codi, Long expedientId) {
		return delegateService.findAmbCodiAndExpedientId(codi, expedientId);
	}

	@Override
	public List<String> checkMidaCampsNotificacio(List<Long> idsInteressats) {
		return delegateService.checkMidaCampsNotificacio(idsInteressats);
	}

	@Override
	public InteressatDto findByCodi(String codi) {
		return delegateService.findByCodi(codi);
	}

	@Override
	public InteressatDto createRepresentant(Long interessatId, InteressatDto representant) {
		return delegateService.createRepresentant(interessatId, representant);
	}

	@Override
	public List<InteressatDto> findRepresentantsExpedient(Long expedientId) {
		return delegateService.findRepresentantsExpedient(expedientId);
	}

	@Override
	public void deleteOrUnassignRepresentant(Long representantId, Long interessatId) {
		delegateService.deleteOrUnassignRepresentant(representantId, interessatId);

	}

}