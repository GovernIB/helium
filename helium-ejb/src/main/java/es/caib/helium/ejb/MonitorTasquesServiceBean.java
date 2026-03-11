package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.commons.dto.MonitorTascaInfo;
import es.caib.helium.logic.intf.service.MonitorTasquesService;

/**
 * Implementació de AvisService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
//@Interceptors(SpringBeanAutowiringInterceptor.class)
public class MonitorTasquesServiceBean implements MonitorTasquesService {

	@Autowired
	MonitorTasquesService delegate;
	


	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public MonitorTascaInfo addTasca(String codiTasca) {
		return delegate.addTasca(codiTasca);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void updateProperaExecucio(String codi, Long plusValue) {
		delegate.updateProperaExecucio(codi, plusValue);		
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<MonitorTascaInfo> findAll() {
		return delegate.findAll();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public MonitorTascaInfo findByCodi(String codi) {
		return delegate.findByCodi(codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void inici(String codiTasca) {
		delegate.inici(codiTasca);	
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void fi(String codiTasca) {
		delegate.fi(codiTasca);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void error(String codiTasca, String error) {
		delegate.error(codiTasca, error);		
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void reiniciarTasquesEnSegonPla(String codiTasca) {
		delegate.reiniciarTasquesEnSegonPla(codiTasca);
	}


}
