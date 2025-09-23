package net.conselldemallorca.helium.v3.core.ejb;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import net.conselldemallorca.helium.v3.core.api.dto.comanda.DimensioDesc;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.IndicadorDesc;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.RegistresEstadistics;
import net.conselldemallorca.helium.v3.core.api.service.EstadisticaService;

/**
 * EJB per a EstadisticaService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class EstadisticaServiceBean implements EstadisticaService {
	
	@Autowired
	private EstadisticaService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean generarDadesExplotacio() {
		return delegate.generarDadesExplotacio();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean generarDadesExplotacio(Date data) {
		return delegate.generarDadesExplotacio(data);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void generarDadesExplotacio(Date data, Date toDate) {
		delegate.generarDadesExplotacio(data, toDate);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public RegistresEstadistics consultaDarreresEstadistiques() {
		return delegate.consultaDarreresEstadistiques();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public RegistresEstadistics consultaEstadistiques(Date data) {
		return delegate.consultaEstadistiques(data);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<RegistresEstadistics> consultaEstadistiques(Date dataInici, Date dataFi) {
		return delegate.consultaEstadistiques(dataInici, dataFi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DimensioDesc> getDimensions() {
		return delegate.getDimensions();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<IndicadorDesc> getIndicadors() {
		return delegate.getIndicadors();
	}

}
