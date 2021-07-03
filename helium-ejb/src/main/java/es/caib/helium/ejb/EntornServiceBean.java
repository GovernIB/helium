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

import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.PermisDto;
import es.caib.helium.logic.intf.service.EntornService;

/**
 * EJB per a EntornService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class EntornServiceBean implements EntornService {

	@Autowired
	EntornService delegate;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public EntornDto create(EntornDto entorn) {
		return delegate.create(entorn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public EntornDto update(EntornDto entorn) {
		return delegate.update(entorn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public void delete(Long entornId) {
		delegate.delete(entornId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EntornDto findAmbIdPermisAcces(Long entornId) {
		return delegate.findAmbIdPermisAcces(entornId);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EntornDto> findActiusAmbPermisAcces() {
		return delegate.findActiusAmbPermisAcces();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public List<EntornDto> findActiusAll() {
		return delegate.findActiusAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public PaginaDto<EntornDto> findPerDatatable(
			String filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findPerDatatable(
				filtre,
				paginacioParams);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public EntornDto findOne(Long entornId) {
		return delegate.findOne(entornId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public EntornDto findAmbCodi(String entornCodi) {
		return delegate.findAmbCodi(entornCodi);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public void permisUpdate(
			Long entornId,
			PermisDto permis) {
		delegate.permisUpdate(
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
		delegate.permisDelete(
				entornId,
				permisId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public List<PermisDto> permisFindAll(Long entornId) {
		return delegate.permisFindAll(entornId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed("HEL_ADMIN")
	public PermisDto permisFindById(Long entornId, Long permisId) {
		return delegate.permisFindById(
				entornId,
				permisId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EntornDto> findActiusAmbPermisAdmin() {
		return delegate.findActiusAmbPermisAdmin();
	}

}
