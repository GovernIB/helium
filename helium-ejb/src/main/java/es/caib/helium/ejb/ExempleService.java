/**
 * 
 */
package es.caib.helium.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * Implementació de ExempleService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ExempleService extends AbstractService<es.caib.helium.logic.intf.service.ExempleService> implements es.caib.helium.logic.intf.service.ExempleService {


	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public String hola(
			String text) {
		return getDelegateService().hola(text);
	}

}
