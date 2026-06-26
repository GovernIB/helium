package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.comanda.ms.log.model.FitxerContingut;
import es.caib.comanda.ms.log.model.FitxerInfo;
import es.caib.comanda.service.management.ApiException;
import net.conselldemallorca.helium.v3.core.api.service.LogService;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
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
	public List<String> llegitUltimesLinies(String nomFitxer, Long nLinies)
			throws ApiException {
		return delegate.llegitUltimesLinies(nomFitxer, nLinies);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_COM"})
	public List<FitxerInfo> llistarFitxers() throws ApiException {
		return delegate.llistarFitxers();
	}

}
