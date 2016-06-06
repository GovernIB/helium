/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.service.EntornService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

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

}
