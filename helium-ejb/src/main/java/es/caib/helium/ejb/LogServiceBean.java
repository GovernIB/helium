package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.comanda.model.server.monitoring.FitxerContingut;
import es.caib.comanda.model.server.monitoring.FitxerInfo;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.logic.intf.service.LogService;

@Stateless
public class LogServiceBean implements LogService {

	@Autowired
	LogService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_COM"})
	public FitxerContingut getFitxerByNom(String nomFitxer) {
		return delegate.getFitxerByNom(nomFitxer);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_COM"})
	public List<String> llegirDarreresLinies(String nomFitxer, Long nLinies) {
		return delegate.llegirDarreresLinies(nomFitxer, nLinies);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_COM"})
	public List<FitxerInfo> llistarFitxers() {
		return delegate.llistarFitxers();
	}

}
