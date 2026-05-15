package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.ParametreDto;
import es.caib.helium.logic.intf.service.ParametreService;

/**
 * Implementació de ParametreService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ParametreServiceBean extends AbstractServiceEjb<ParametreService> implements ParametreService {

	@Delegate
	ParametreService delegateService;

	protected void setDelegateService(ParametreService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public ParametreDto create(ParametreDto parametre) {
		return delegateService.create(parametre);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public ParametreDto update(ParametreDto parametre) {
		return delegateService.update(parametre);
	}


	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public ParametreDto delete(Long id) {
		return delegateService.delete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public ParametreDto findById(Long id) {
		return delegateService.findById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ParametreDto> findAll() {
		return delegateService.findAll();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ParametreDto> findPaginat(PaginacioParamsDto paginacioParams) {
		return delegateService.findPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ParametreDto findByCodi(String codi) {
		return delegateService.findByCodi(codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Long getMidaMaximaFitxerInBytes() {
		return delegateService.getMidaMaximaFitxerInBytes();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String getMidaMaximaFitxer() {
		return delegateService.getMidaMaximaFitxer();
	}

}
