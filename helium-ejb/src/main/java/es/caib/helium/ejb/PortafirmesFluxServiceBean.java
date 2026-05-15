package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

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
public class PortafirmesFluxServiceBean extends AbstractServiceEjb<PortafirmesFluxService> implements PortafirmesFluxService {

	@Delegate
	PortafirmesFluxService delegateService;

	protected void setDelegateService(PortafirmesFluxService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PortafirmesIniciFluxRespostaDto iniciarFluxFirma(
			Long expedientTipusId,
			Long definicioProcesId,
			String usuariCodi,
			String urlReturn,
			boolean isPlantilla) {
		return delegateService.iniciarFluxFirma(
				expedientTipusId,
				definicioProcesId,
				usuariCodi,
				urlReturn,
				isPlantilla);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PortafirmesFluxRespostaDto recuperarFluxFirma(String transactionId) {
		return delegateService.recuperarFluxFirma(transactionId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void tancarTransaccio(String idTransaccio) {
		delegateService.tancarTransaccio(idTransaccio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PortafirmesFluxInfoDto recuperarDetallFluxFirma(String idTransaccio) {
		return delegateService.recuperarDetallFluxFirma(idTransaccio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String recuperarUrlMostrarPlantilla(String plantillaFluxId) {
		return delegateService.recuperarUrlMostrarPlantilla(plantillaFluxId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<PortafirmesFluxRespostaDto> recuperarPlantillesDisponibles(Long expedientTipusId, Long definicioProcesId, String usuari) {
		return delegateService.recuperarPlantillesDisponibles(expedientTipusId, definicioProcesId, usuari);
	}
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String recuperarUrlEdicioPlantilla(String plantillaFluxId, String returnUrl) {
		return delegateService.recuperarUrlEdicioPlantilla(plantillaFluxId, returnUrl);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean esborrarPlantilla(String plantillaFluxId) {
		return delegateService.esborrarPlantilla(plantillaFluxId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String recuperarUrlViewEstatFluxDeFirmes(long portafirmesId) {
		return delegateService.recuperarUrlViewEstatFluxDeFirmes(portafirmesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<PortafirmesCarrecDto> recuperarCarrecs() {
		return delegateService.recuperarCarrecs();
	}

}