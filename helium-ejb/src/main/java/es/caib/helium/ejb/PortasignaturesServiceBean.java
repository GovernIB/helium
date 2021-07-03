/**
 * 
 */
package es.caib.helium.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.helium.logic.intf.dto.PortasignaturesDto;
import es.caib.helium.logic.intf.service.PortasignaturesService;

/**
 * Servei per a gestionar els callbacks del portasignatures.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class PortasignaturesServiceBean implements PortasignaturesService {

	@Autowired
	PortasignaturesService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean processarDocumentCallback(Integer documentId, boolean rebujat, String motiuRebuig) {
		return delegate.processarDocumentCallback(documentId, rebujat, motiuRebuig);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PortasignaturesDto getByDocumentId(Integer documentId) {
		return delegate.getByDocumentId(documentId);
	}

}
