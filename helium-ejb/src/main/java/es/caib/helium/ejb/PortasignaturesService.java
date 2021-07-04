/**
 * 
 */
package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.PortasignaturesDto;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * Servei per a gestionar els callbacks del portasignatures.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class PortasignaturesService extends AbstractService<es.caib.helium.logic.intf.service.PortasignaturesService> implements es.caib.helium.logic.intf.service.PortasignaturesService {

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean processarDocumentCallback(Integer documentId, boolean rebujat, String motiuRebuig) {
		return getDelegateService().processarDocumentCallback(documentId, rebujat, motiuRebuig);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PortasignaturesDto getByDocumentId(Integer documentId) {
		return getDelegateService().getByDocumentId(documentId);
	}

}
