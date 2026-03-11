package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.comanda.model.v1.log.FitxerContingut;
import es.caib.comanda.model.v1.log.FitxerInfo;
import es.caib.comanda.service.v1.avis.ApiException;
import es.caib.helium.logic.intf.service.LogService;

@Stateless
//@Interceptors(SpringBeanAutowiringInterceptor.class)
public class LogServiceBean implements LogService {

	@Autowired
	LogService delegate;
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_COM"})
	public FitxerContingut getFitxerByNom(String nomFitxer) throws ApiException {
		return delegate.getFitxerByNom(nomFitxer);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_COM"})
	public FitxerContingut llegitUltimesLinies(String nomFitxer, Long nLinies)
			throws ApiException {
		return delegate.llegitUltimesLinies(nomFitxer, nLinies);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_COM"})
	public List<FitxerInfo> llistarFitxers() throws ApiException {
		return delegate.llistarFitxers();
	}

}
