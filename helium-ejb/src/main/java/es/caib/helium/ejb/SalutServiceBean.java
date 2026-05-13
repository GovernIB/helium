package es.caib.helium.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.comanda.model.server.monitoring.*;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.logic.intf.service.SalutService;

import java.util.List;

@Stateless
public class SalutServiceBean implements SalutService {
	@Autowired
	private SalutService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<IntegracioInfo> getIntegracions() {
		return delegate.getIntegracions();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<SubsistemaInfo> getSubsistemes() {
		return delegate.getSubsistemes();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ContextInfo> getContexts() {
		return delegate.getContexts();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public SalutInfo checkSalut(String versio, String performanceUrl) {
		return delegate.checkSalut(versio, performanceUrl);
	}

}
