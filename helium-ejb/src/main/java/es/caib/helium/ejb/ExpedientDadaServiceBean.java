/**
 *
 */
package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.CampAgrupacioDto;
import es.caib.helium.commons.dto.CampInfoDto;
import es.caib.helium.commons.dto.DadaListDto;
import es.caib.helium.commons.dto.ExpedientDadaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.ExpedientDadaService;

/**
 * EJB que implementa la interfície del servei ExpedientDadaService.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ExpedientDadaServiceBean extends AbstractServiceEjb<ExpedientDadaService> implements ExpedientDadaService {

	@Delegate
	ExpedientDadaService delegateService;

	protected void setDelegateService(ExpedientDadaService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void create(
			Long expedientId,
			String processInstanceId,
			String varCodi,
			Object varValor) {
		delegateService.create(
				expedientId,
				processInstanceId,
				varCodi,
				varValor);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void update(
			Long expedientId,
			String processInstanceId,
			String varCodi,
			Object varValor) {
		delegateService.update(
				expedientId,
				processInstanceId,
				varCodi,
				varValor);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(
			Long expedientId,
			String processInstanceId,
			String varCodi) {
		delegateService.delete(
				expedientId,
				processInstanceId,
				varCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientDadaDto findOnePerInstanciaProces(
			Long expedientId,
			String processInstanceId,
			String varCodi) {
		return delegateService.findOnePerInstanciaProces(
				expedientId,
				processInstanceId,
				varCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientDadaDto getDadaBuida(long campId) {
		return delegateService.getDadaBuida(campId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientDadaDto> findAmbInstanciaProces(
			Long expedientId,
			String processInstanceId) {
		return delegateService.findAmbInstanciaProces(
				expedientId,
				processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampAgrupacioDto> agrupacionsFindAmbInstanciaProces(
			Long expedientId,
			String processInstanceId) {
		return delegateService.agrupacionsFindAmbInstanciaProces(
				expedientId,
				processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DadaListDto> findDadesExpedient(Long expedientId, Long estatId, Boolean totes, Boolean ambOcults, Boolean noPendents, PaginacioParamsDto paginacioParams) {
		return delegateService.findDadesExpedient(expedientId, estatId, totes, ambOcults, noPendents, paginacioParams);
	}

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public List<CampInfoDto> getCampsNoUtilitzatsPerEstats(Long expedientId) {
        return delegateService.getCampsNoUtilitzatsPerEstats(expedientId);
    }

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public DadaListDto getDadaList(Long expedientId, String procesId, String varCodi) {
        return delegateService.getDadaList(expedientId, procesId, varCodi);
    }

}
