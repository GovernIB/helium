/**
 * 
 */
package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.PermisDto;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * EJB per a EntornService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class EntornService extends AbstractService<es.caib.helium.logic.intf.service.EntornService> implements es.caib.helium.logic.intf.service.EntornService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public EntornDto create(EntornDto entorn) {
		return getDelegateService().create(entorn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public EntornDto update(EntornDto entorn) {
		return getDelegateService().update(entorn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public void delete(Long entornId) {
		getDelegateService().delete(entornId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EntornDto findAmbIdPermisAcces(Long entornId) {
		return getDelegateService().findAmbIdPermisAcces(entornId);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EntornDto> findActiusAmbPermisAcces() {
		return getDelegateService().findActiusAmbPermisAcces();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public List<EntornDto> findActiusAll() {
		return getDelegateService().findActiusAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public PaginaDto<EntornDto> findPerDatatable(
			String filtre,
			PaginacioParamsDto paginacioParams) {
		return getDelegateService().findPerDatatable(
				filtre,
				paginacioParams);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public EntornDto findOne(Long entornId) {
		return getDelegateService().findOne(entornId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public EntornDto findAmbCodi(String entornCodi) {
		return getDelegateService().findAmbCodi(entornCodi);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public void permisUpdate(
			Long entornId,
			PermisDto permis) {
		getDelegateService().permisUpdate(
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
		getDelegateService().permisDelete(
				entornId,
				permisId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public List<PermisDto> permisFindAll(Long entornId) {
		return getDelegateService().permisFindAll(entornId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public PermisDto permisFindById(Long entornId, Long permisId) {
		return getDelegateService().permisFindById(
				entornId,
				permisId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EntornDto> findActiusAmbPermisAdmin() {
		return getDelegateService().findActiusAmbPermisAdmin();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Long getEntornActualId() {
		return getDelegateService().getEntornActualId();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void setEntornActualId(Long entornActualId) {
		getDelegateService().setEntornActualId(entornActualId);
	}

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public boolean isAdminEntornActual() {
        return getDelegateService().isAdminEntornActual();
    }

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public boolean isDissenyadorEntorn(Long id) {
        return getDelegateService().isDissenyadorEntorn(id);
    }

}
