/**
 * 
 */
package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.helium.logic.intf.dto.CampAgrupacioDto;
import es.caib.helium.logic.intf.dto.ExpedientDadaDto;
import es.caib.helium.logic.intf.service.ExpedientDadaService;

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
}