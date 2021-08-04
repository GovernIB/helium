package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.PermisRolDto;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import java.util.List;

/**
 * EJB que implementa la interfície del servei PermisService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class PermisService extends AbstractService<es.caib.helium.logic.intf.service.PermisService> implements es.caib.helium.logic.intf.service.PermisService {

	@Override
	@PermitAll
	public List<PermisRolDto> findAll() {
		return getDelegateService().findAll();
	}

}
