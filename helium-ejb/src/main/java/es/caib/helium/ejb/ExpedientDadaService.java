/**
 * 
 */
package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.CampAgrupacioDto;
import es.caib.helium.logic.intf.dto.ExpedientDadaDto;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * EJB que implementa la interfície del servei ExpedientDadaService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ExpedientDadaService extends AbstractService<es.caib.helium.logic.intf.service.ExpedientDadaService> implements es.caib.helium.logic.intf.service.ExpedientDadaService {

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void create(
			Long expedientId,
			String processInstanceId,
			String varCodi,
			Object varValor) {
		getDelegateService().create(
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
		getDelegateService().update(
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
		getDelegateService().delete(
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
		return getDelegateService().findOnePerInstanciaProces(
				expedientId,
				processInstanceId,
				varCodi);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientDadaDto getDadaBuida(long campId) {
		return getDelegateService().getDadaBuida(campId);
	}


	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientDadaDto> findAmbInstanciaProces(
			Long expedientId,
			String processInstanceId) {
		return getDelegateService().findAmbInstanciaProces(
				expedientId,
				processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampAgrupacioDto> agrupacionsFindAmbInstanciaProces(
			Long expedientId,
			String processInstanceId) {
		return getDelegateService().agrupacionsFindAmbInstanciaProces(
				expedientId,
				processInstanceId);
	}
}
