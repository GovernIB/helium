package es.caib.helium.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.commons.dto.DocumentNotificacioDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.NotificacioService;

/**
 * Servei per a gestionar les notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
//@Interceptors(SpringBeanAutowiringInterceptor.class)
public class NotificacioServiceBean implements NotificacioService {

	@Autowired
	NotificacioService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<DocumentNotificacioDto> findAmbFiltrePaginat(
			DocumentNotificacioDto filtreDto,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltrePaginat(filtreDto, paginacioParams);
	}


	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentNotificacioDto findAmbId(Long id) {
		return delegate.findAmbId(id);
	}
}