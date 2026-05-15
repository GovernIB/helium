package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.PeticioPinbalDto;
import es.caib.helium.commons.dto.PeticioPinbalFiltreDto;
import es.caib.helium.commons.dto.ScspRespostaPinbal;
import es.caib.helium.commons.dto.ServeiPinbalDto;
import es.caib.helium.logic.intf.service.ConsultaPinbalService;

@Stateless
public class ConsultaPinbalServiceBean extends AbstractServiceEjb<ConsultaPinbalService> implements ConsultaPinbalService {

	@Delegate ConsultaPinbalService delegateService;

	protected void setDelegateService(ConsultaPinbalService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<PeticioPinbalDto> findAmbFiltrePaginat(PaginacioParamsDto paginacioParams, PeticioPinbalFiltreDto filtreDto) {
		return delegateService.findAmbFiltrePaginat(paginacioParams, filtreDto);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PeticioPinbalDto findById(Long peticioPinbalId) {
		return delegateService.findById(peticioPinbalId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<PeticioPinbalDto> findConsultesPinbalPerExpedient(Long expedientId) {
		return delegateService.findConsultesPinbalPerExpedient(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PeticioPinbalDto findByExpedientAndDocumentStore(Long expedientId, Long documentStoreId) {
		return delegateService.findByExpedientAndDocumentStore(expedientId, documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public PaginaDto<ServeiPinbalDto> findServeisPinbalAmbFiltrePaginat(PaginacioParamsDto paginacioParams) {
		return delegateService.findServeisPinbalAmbFiltrePaginat(paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public ServeiPinbalDto findServeiPinbalById(Long id) {
		return delegateService.findServeiPinbalById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public ServeiPinbalDto updateServeiPinbal(ServeiPinbalDto serveiPinbalDto) {
		return delegateService.updateServeiPinbal(serveiPinbalDto);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ScspRespostaPinbal tractamentPeticioAsincronaPendentPinbal(Long peticioPinbalId) {
		return delegateService.tractamentPeticioAsincronaPendentPinbal(peticioPinbalId);
	}

}