package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.MunicipiDto;
import es.caib.helium.commons.dto.PaisDto;
import es.caib.helium.commons.dto.ProvinciaDto;
import es.caib.helium.commons.dto.TipusViaDto;
import es.caib.helium.commons.exception.SistemaExternException;
import es.caib.helium.logic.intf.service.DadesExternesService;

/**
 * Implementació de DadesExternesService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class DadesExternesServiceBean extends AbstractServiceEjb<DadesExternesService> implements DadesExternesService {

	@Delegate
	DadesExternesService delegateService;

	protected void setDelegateService(DadesExternesService delegateService) {
		this.delegateService = delegateService;
	}


	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<PaisDto> findPaisos()  throws SistemaExternException {
		return delegateService.findPaisos();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ProvinciaDto> findProvincies() throws SistemaExternException {
		return delegateService.findProvincies();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ProvinciaDto> findProvinciesPerComunitat(String comunitatCodi) throws SistemaExternException {
		return delegateService.findProvinciesPerComunitat(comunitatCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<MunicipiDto> findMunicipisPerProvincia(String provinciaCodi) throws SistemaExternException {
		return delegateService.findMunicipisPerProvincia(provinciaCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<MunicipiDto> findMunicipisPerProvinciaPinbal(String provinciaCodi) {
		return delegateService.findMunicipisPerProvinciaPinbal(provinciaCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TipusViaDto> findTipusVia() throws SistemaExternException {
		return delegateService.findTipusVia();
	}

}
