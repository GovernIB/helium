package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.comanda.ms.salut.model.ContextInfo;
import es.caib.comanda.ms.salut.model.IntegracioInfo;
import es.caib.comanda.ms.salut.model.SalutInfo;
import es.caib.comanda.ms.salut.model.SubsistemaInfo;
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
	public List<SubsistemaInfo> getSubsistemes() {
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
