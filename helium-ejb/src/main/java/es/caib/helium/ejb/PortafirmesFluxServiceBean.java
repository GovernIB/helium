/**
 *
 */
package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.commons.dto.PortafirmesCarrecDto;
import es.caib.helium.commons.dto.PortafirmesFluxInfoDto;
import es.caib.helium.commons.dto.PortafirmesFluxRespostaDto;
import es.caib.helium.commons.dto.PortafirmesIniciFluxRespostaDto;
import es.caib.helium.logic.intf.service.PortafirmesFluxService;


/**
 * Implementació del servei de gestió de meta-documents.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
//@Interceptors(SpringBeanAutowiringInterceptor.class)
public class PortafirmesFluxServiceBean implements PortafirmesFluxService {

	@Autowired
	PortafirmesFluxService delegate;


	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PortafirmesIniciFluxRespostaDto iniciarFluxFirma(
			Long expedientTipusId,
			Long definicioProcesId,
			String usuariCodi,
			String urlReturn, 
			boolean isPlantilla) {
		return delegate.iniciarFluxFirma(
				expedientTipusId,
				definicioProcesId,
				usuariCodi,
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
	public List<PortafirmesFluxRespostaDto> recuperarPlantillesDisponibles(Long expedientTipusId, Long definicioProcesId, String usuari) {
		return delegate.recuperarPlantillesDisponibles(expedientTipusId, definicioProcesId, usuari);
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

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String recuperarUrlViewEstatFluxDeFirmes(long portafirmesId) {
		return delegate.recuperarUrlViewEstatFluxDeFirmes(portafirmesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<PortafirmesCarrecDto> recuperarCarrecs() {
		return delegate.recuperarCarrecs();
	}

}