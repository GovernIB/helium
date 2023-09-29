/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import net.conselldemallorca.helium.v3.core.api.dto.CampInfoDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadaListDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDadaService;

/**
 * EJB que implementa la interfície del servei ExpedientDadaService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ExpedientDadaServiceBean implements ExpedientDadaService {

	@Autowired
	ExpedientDadaService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void create(
			Long expedientId,
			String processInstanceId,
			String varCodi,
			Object varValor) {
		delegate.create(
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
		delegate.update(
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
		delegate.delete(
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
		return delegate.findOnePerInstanciaProces(
				expedientId,
				processInstanceId,
				varCodi);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientDadaDto getDadaBuida(long campId) {
		return delegate.getDadaBuida(campId);
	}


	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientDadaDto> findAmbInstanciaProces(
			Long expedientId,
			String processInstanceId) {
		return delegate.findAmbInstanciaProces(
				expedientId,
				processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampAgrupacioDto> agrupacionsFindAmbInstanciaProces(
			Long expedientId,
			String processInstanceId) {
		return delegate.agrupacionsFindAmbInstanciaProces(
				expedientId,
				processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DadaListDto> findDadesExpedient(Long expedientId, Boolean totes, Boolean ambOcults, Boolean noPendents, PaginacioParamsDto paginacioParams) {
		return delegate.findDadesExpedient(expedientId, totes, ambOcults, noPendents, paginacioParams);
	}

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public List<CampInfoDto> getCampsNoUtilitzatsPerEstats(Long expedientId) {
        return delegate.getCampsNoUtilitzatsPerEstats(expedientId);
    }

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public DadaListDto getDadaList(Long expedientId, String procesId, String varCodi) {
        return delegate.getDadaList(expedientId, procesId, varCodi);
    }
}
