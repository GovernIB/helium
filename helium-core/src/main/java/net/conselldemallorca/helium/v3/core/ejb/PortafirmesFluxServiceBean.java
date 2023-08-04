/**
 *
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesCarrecDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesFluxInfoDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesFluxRespostaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesIniciFluxRespostaDto;
import net.conselldemallorca.helium.v3.core.api.service.PortafirmesFluxService;


/**
 * Implementació del servei de gestió de meta-documents.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class PortafirmesFluxServiceBean implements PortafirmesFluxService {

	@Autowired
	PortafirmesFluxService delegate;


	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PortafirmesIniciFluxRespostaDto iniciarFluxFirma(
			String urlReturn, 
			boolean isPlantilla) {
		return delegate.iniciarFluxFirma(
				urlReturn, 
				isPlantilla);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PortafirmesFluxRespostaDto recuperarFluxFirma(String transactionId) {
		return delegate.recuperarFluxFirma(transactionId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void tancarTransaccio(String idTransaccio) {
		delegate.tancarTransaccio(idTransaccio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PortafirmesFluxInfoDto recuperarDetallFluxFirma(String idTransaccio) {
		return delegate.recuperarDetallFluxFirma(idTransaccio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String recuperarUrlMostrarPlantilla(String plantillaFluxId) {
		return delegate.recuperarUrlMostrarPlantilla(plantillaFluxId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<PortafirmesFluxRespostaDto> recuperarPlantillesDisponibles(boolean filtrar) {
		return delegate.recuperarPlantillesDisponibles(filtrar);
	}
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String recuperarUrlEdicioPlantilla(String plantillaFluxId, String returnUrl) {
		return delegate.recuperarUrlEdicioPlantilla(plantillaFluxId, returnUrl);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean esborrarPlantilla(String plantillaFluxId) {
		return delegate.esborrarPlantilla(plantillaFluxId);
	}

}