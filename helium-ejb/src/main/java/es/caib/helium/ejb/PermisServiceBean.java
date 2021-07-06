package es.caib.helium.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.helium.logic.intf.dto.PermisRolDto;
import es.caib.helium.logic.intf.service.PermisService;

/**
 * EJB que implementa la interfície del servei PermisService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class PermisServiceBean implements PermisService {

	@Autowired
	PermisService delegate;

	@Override
	public List<PermisRolDto> findAll() {
		return delegate.findAll();
	}

}
