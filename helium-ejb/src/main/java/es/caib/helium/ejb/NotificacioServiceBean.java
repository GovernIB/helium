package es.caib.helium.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

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
public class NotificacioServiceBean extends AbstractServiceEjb<NotificacioService> implements NotificacioService {

	@Delegate
	NotificacioService delegateService;

	protected void setDelegateService(NotificacioService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<DocumentNotificacioDto> findAmbFiltrePaginat(
			DocumentNotificacioDto filtreDto,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findAmbFiltrePaginat(filtreDto, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentNotificacioDto findAmbId(Long id) {
		return delegateService.findAmbId(id);
	}

}