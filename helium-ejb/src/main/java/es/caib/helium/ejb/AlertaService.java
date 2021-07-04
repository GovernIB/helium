package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.AlertaDto;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
public class AlertaService extends AbstractService<es.caib.helium.logic.intf.service.AlertaService> implements es.caib.helium.logic.intf.service.AlertaService {
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AlertaDto marcarLlegida(Long alertaId) {
		return getDelegateService().marcarLlegida(alertaId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AlertaDto marcarNoLlegida(Long alertaId) {
		return getDelegateService().marcarNoLlegida(alertaId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AlertaDto marcarEsborrada(Long alertaId) {
		return getDelegateService().marcarEsborrada(alertaId);
	}
}
