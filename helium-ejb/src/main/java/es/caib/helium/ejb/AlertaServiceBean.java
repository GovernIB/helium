package es.caib.helium.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.AlertaDto;
import es.caib.helium.logic.intf.service.AlertaService;

@Stateless
public class AlertaServiceBean extends AbstractServiceEjb<AlertaService> implements AlertaService {

	@Delegate
	AlertaService delegateService;

	protected void setDelegateService(AlertaService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AlertaDto marcarLlegida(Long alertaId) {
		return delegateService.marcarLlegida(alertaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AlertaDto marcarNoLlegida(Long alertaId) {
		return delegateService.marcarNoLlegida(alertaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AlertaDto marcarEsborrada(Long alertaId) {
		return delegateService.marcarEsborrada(alertaId);
	}

}
