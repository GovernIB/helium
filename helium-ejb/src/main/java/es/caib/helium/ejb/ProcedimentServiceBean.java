package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.procediment.ProcedimentDto;
import es.caib.helium.commons.dto.procediment.ProcedimentFiltreDto;
import es.caib.helium.commons.dto.procediment.ProgresActualitzacioDto;
import es.caib.helium.logic.intf.service.ProcedimentService;

/**
 * Implementació de ProcedimentService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */

@Stateless
public class ProcedimentServiceBean extends AbstractServiceEjb<ProcedimentService> implements ProcedimentService {

	@Delegate
	private ProcedimentService delegateService;

	protected void setDelegateService(ProcedimentService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@PermitAll
	public PaginaDto<ProcedimentDto> findAmbFiltre(ProcedimentFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findAmbFiltre(filtre, paginacioParams);
	}

	@Override
	@PermitAll
	public ProcedimentDto findByCodiSia(String codiSia) {
		return delegateService.findByCodiSia(codiSia);
	}

	@Override
	@PermitAll
	public List<ProcedimentDto> findByNomOrCodiSia(String nom) {
		return delegateService.findByNomOrCodiSia(nom);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public void actualitzaProcediments() {
		delegateService.actualitzaProcediments();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public void actualitzaServeis() {
		delegateService.actualitzaServeis();
	}

	@Override
	@PermitAll
	public boolean isUpdatingProcediments() {
		return delegateService.isUpdatingProcediments();
	}

	@Override
	@PermitAll
	public boolean isUpdatingServeis() {
		return delegateService.isUpdatingServeis();
	}

	@Override
	@PermitAll
	public ProgresActualitzacioDto getProgresActualitzacio() {
		return delegateService.getProgresActualitzacio();
	}

	@Override
	@PermitAll
	public ProgresActualitzacioDto getProgresServisActualitzacio() {
		return delegateService.getProgresServisActualitzacio();
	}

}
