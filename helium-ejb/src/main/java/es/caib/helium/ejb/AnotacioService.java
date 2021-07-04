/**
 * 
 */
package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.AnotacioDto;
import es.caib.helium.logic.intf.dto.AnotacioFiltreDto;
import es.caib.helium.logic.intf.dto.AnotacioListDto;
import es.caib.helium.logic.intf.dto.ArxiuDto;
import es.caib.helium.logic.intf.dto.ArxiuFirmaDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Servei per a gestionar les enumeracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class AnotacioService extends AbstractService<es.caib.helium.logic.intf.service.AnotacioService> implements es.caib.helium.logic.intf.service.AnotacioService {

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<AnotacioListDto> findAmbFiltrePaginat(
			Long entornId,
			AnotacioFiltreDto filtreDto,
			PaginacioParamsDto paginacioParams) {
		return getDelegateService().findAmbFiltrePaginat( entornId, filtreDto, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AnotacioDto findAmbId(Long id) throws NoTrobatException {
		return getDelegateService().findAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void rebutjar(Long anotacioId, String observacions) {
		getDelegateService().rebutjar(anotacioId, observacions);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AnotacioDto updateExpedient(Long anotacioId, Long expedientTipusId, Long expedientId) {
		return getDelegateService().updateExpedient(anotacioId, expedientTipusId, expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AnotacioDto incorporarExpedient(Long anotacioId, Long expedientTipusId, Long expedientId, boolean associarInteressats, boolean comprovarPermis) {
		return getDelegateService().incorporarExpedient(anotacioId, expedientTipusId, expedientId, associarInteressats, comprovarPermis);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long anotacioId) {
		getDelegateService().delete(anotacioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto getAnnexContingut(Long annexId) {
		return getDelegateService().getAnnexContingut(annexId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ArxiuFirmaDto> getAnnexFirmes(Long annexId) {
		return getDelegateService().getAnnexFirmes(annexId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void reintentarAnnex(Long anotacioId, Long annexId) throws Exception {
		getDelegateService().reintentarAnnex(anotacioId, annexId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void esborrarAnotacionsExpedient(Long expedientId) {
		getDelegateService().esborrarAnotacionsExpedient(expedientId);
	}
}