package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.comanda.model.v1.salut.ContextInfo;
import es.caib.comanda.model.v1.salut.IntegracioInfo;
import es.caib.comanda.model.v1.salut.SalutInfo;
import es.caib.comanda.model.v1.salut.SubsistemaInfo;
import es.caib.helium.logic.intf.service.SalutService;

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
