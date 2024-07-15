package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import net.conselldemallorca.helium.v3.core.api.dto.InteressatDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientInteressatService;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ExpedientInteressatServiceBean implements ExpedientInteressatService {

	@Autowired ExpedientInteressatService delegate;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PaginaDto<InteressatDto> findPerDatatable(
			Long expedientId,
			String filtre, 
			PaginacioParamsDto paginacioParams){
		return delegate.findPerDatatable(
				expedientId,
				filtre,
				paginacioParams);
	}

	@Override
	public InteressatDto create(InteressatDto interessat) {
		return delegate.create(
				interessat);
	}

	@Override
	public InteressatDto update(InteressatDto interessat) {
		return delegate.update(interessat);
	}

	@Override
	public InteressatDto findOne(Long interessatId) {
		return delegate.findOne(interessatId);
	}

	@Override
	public void delete(Long interessatId) {
		delegate.delete(interessatId);
	}

	@Override
	public List<InteressatDto> findByExpedient(Long expedientId) {
		return delegate.findByExpedient(expedientId);
	}

	@Override
	public InteressatDto findAmbCodiAndExpedientId(String codi, Long expedientId) {
		return delegate.findAmbCodiAndExpedientId(codi, expedientId);
	}

	@Override
	public List<String> checkMidaCampsNotificacio(List<Long> idsInteressats) {
		return delegate.checkMidaCampsNotificacio(idsInteressats);
	}
}