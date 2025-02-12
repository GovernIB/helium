package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import net.conselldemallorca.helium.v3.core.api.service.DadesExternesService;
import net.conselldemallorca.helium.v3.core.api.dto.ComunitatAutonomaDto;
import net.conselldemallorca.helium.v3.core.api.dto.MunicipiDto;
import net.conselldemallorca.helium.v3.core.api.dto.NivellAdministracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaisDto;
import net.conselldemallorca.helium.v3.core.api.dto.ProvinciaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TipusViaDto;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;



/**
 * Implementació de DadesExternesService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class DadesExternesServiceBean implements DadesExternesService {

	@Autowired
	DadesExternesService delegate;
	

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<PaisDto> findPaisos()  throws SistemaExternException {
		return delegate.findPaisos();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ProvinciaDto> findProvincies() throws SistemaExternException {
		return delegate.findProvincies();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ProvinciaDto> findProvinciesPerComunitat(String comunitatCodi) throws SistemaExternException {
		return delegate.findProvinciesPerComunitat(comunitatCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<MunicipiDto> findMunicipisPerProvincia(String provinciaCodi) throws SistemaExternException {
		return delegate.findMunicipisPerProvincia(provinciaCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<MunicipiDto> findMunicipisPerProvinciaPinbal(String provinciaCodi) {
		return delegate.findMunicipisPerProvinciaPinbal(provinciaCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TipusViaDto> findTipusVia() throws SistemaExternException {
		return delegate.findTipusVia();
	}	


}
