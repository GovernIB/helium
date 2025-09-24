package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.comanda.AppInfo;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.ContextInfo;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.IntegracioInfo;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.SalutInfo;
import net.conselldemallorca.helium.v3.core.api.service.SalutService;

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
	public List<AppInfo> getSubsistemes() {
		return delegate.getSubsistemes();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ContextInfo> getContexts(String baseUrl) {
		return delegate.getContexts(baseUrl);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public SalutInfo checkSalut(String versio, String performanceUrl) {
		return delegate.checkSalut(versio, performanceUrl);
	}

}
