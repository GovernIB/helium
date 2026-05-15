package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.EntornDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.PermisDto;
import es.caib.helium.logic.intf.service.EntornService;

/**
 * EJB per a EntornService.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class EntornServiceBean extends AbstractServiceEjb<EntornService> implements EntornService {

	@Delegate
	EntornService delegateService;

	protected void setDelegateService(EntornService delegateService) {
		this.delegateService = delegateService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public EntornDto create(EntornDto entorn) {
		return delegateService.create(entorn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public EntornDto update(EntornDto entorn) {
		return delegateService.update(entorn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public void delete(Long entornId) {
		delegateService.delete(entornId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EntornDto> findActiusAmbPermisAcces() {
		return delegateService.findActiusAmbPermisAcces();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public List<EntornDto> findActiusAll() {
		return delegateService.findActiusAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public PaginaDto<EntornDto> findPerDatatable(
			String filtre,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findPerDatatable(
				filtre,
				paginacioParams);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public EntornDto findOne(Long entornId) {
		return delegateService.findOne(entornId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public EntornDto findAmbCodi(String entornCodi) {
		return delegateService.findAmbCodi(entornCodi);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public void permisUpdate(
			Long entornId,
			PermisDto permis) {
		delegateService.permisUpdate(
				entornId,
				permis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public void permisDelete(
			Long entornId,
			Long permisId) {
		delegateService.permisDelete(
				entornId,
				permisId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public List<PermisDto> permisFindAll(Long entornId) {
		return delegateService.permisFindAll(entornId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public PermisDto permisFindById(Long entornId, Long permisId) {
		return delegateService.permisFindById(
				entornId,
				permisId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EntornDto> findActiusAmbPermisAdmin() {
		return delegateService.findActiusAmbPermisAdmin();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EntornDto getEntornComprovantPermisos(Long entornId, boolean comprovarPermisAcces) {
		return delegateService.getEntornComprovantPermisos(entornId, comprovarPermisAcces);
	}

}
