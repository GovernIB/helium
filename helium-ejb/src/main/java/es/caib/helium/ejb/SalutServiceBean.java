package es.caib.helium.ejb;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.comanda.model.server.monitoring.*;

import es.caib.helium.logic.intf.service.SalutService;

import java.util.List;

@Stateless
public class SalutServiceBean extends AbstractServiceEjb<SalutService> implements SalutService {

	@Delegate
	private SalutService delegateService;

	protected void setDelegateService(SalutService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_COM"})
	public List<IntegracioInfo> getIntegracions() {
		return delegateService.getIntegracions();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_COM"})
	public List<SubsistemaInfo> getSubsistemes() {
		return delegateService.getSubsistemes();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_COM"})
	public List<ContextInfo> getContexts() {
		return delegateService.getContexts();
	}

	@Override
	@PermitAll
	public SalutInfo checkSalut(String versio, String performanceUrl) {
		return delegateService.checkSalut(versio, performanceUrl);
	}

}
