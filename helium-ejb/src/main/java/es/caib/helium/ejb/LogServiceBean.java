package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.comanda.model.server.monitoring.FitxerContingut;
import es.caib.comanda.model.server.monitoring.FitxerInfo;

import es.caib.helium.logic.intf.service.LogService;

@Stateless
public class LogServiceBean extends AbstractServiceEjb<LogService> implements LogService {

	@Delegate
	LogService delegateService;

	protected void setDelegateService(LogService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_COM"})
	public FitxerContingut getFitxerByNom(String nomFitxer) {
		return delegateService.getFitxerByNom(nomFitxer);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_COM"})
	public List<String> llegirDarreresLinies(String nomFitxer, Long nLinies) {
		return delegateService.llegirDarreresLinies(nomFitxer, nLinies);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_COM"})
	public List<FitxerInfo> llistarFitxers() {
		return delegateService.llistarFitxers();
	}

}
