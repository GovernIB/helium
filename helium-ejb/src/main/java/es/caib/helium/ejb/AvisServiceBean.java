package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.AvisDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.AvisService;

/**
 * Implementació de AvisService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class AvisServiceBean extends AbstractServiceEjb<AvisService> implements AvisService {

	@Delegate
	AvisService delegateService;

	protected void setDelegateService(AvisService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public AvisDto create(AvisDto avis) {
		return delegateService.create(avis);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public AvisDto update(AvisDto avis) {
		return delegateService.update(avis);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public AvisDto updateActiva(Long id, boolean activa) {
		return delegateService.updateActiva(id, activa);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public AvisDto delete(Long id) {
		return delegateService.delete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public AvisDto findById(Long id) {
		return delegateService.findById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<AvisDto> findPaginat(PaginacioParamsDto paginacioParams) {
		return delegateService.findPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<AvisDto> findActive() {
		return delegateService.findActive();
	}

}
