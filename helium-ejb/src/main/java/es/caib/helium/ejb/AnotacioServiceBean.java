package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.AnotacioDto;
import es.caib.helium.commons.dto.AnotacioFiltreDto;
import es.caib.helium.commons.dto.AnotacioListDto;
import es.caib.helium.commons.dto.AnotacioMapeigResultatDto;
import es.caib.helium.commons.dto.ArxiuDto;
import es.caib.helium.commons.dto.ArxiuFirmaDto;
import es.caib.helium.commons.dto.ExpedientTipusDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.logic.intf.service.AnotacioService;

/**
 * Servei per a gestionar les enumeracions.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class AnotacioServiceBean extends AbstractServiceEjb<AnotacioService> implements AnotacioService {

	@Delegate
	AnotacioService delegateService;

	protected void setDelegateService(AnotacioService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<AnotacioListDto> findAmbFiltrePaginat(
			Long entornId,
			List<ExpedientTipusDto> expedientTipusDtoAccessibles,
			AnotacioFiltreDto filtreDto,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findAmbFiltrePaginat(entornId, expedientTipusDtoAccessibles, filtreDto, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Long> findIdsAmbFiltre(
			Long entornId,
			List<ExpedientTipusDto> expedientTipusDtoAccessiblesAnotacions,
			AnotacioFiltreDto filtreDto) {
		return delegateService.findIdsAmbFiltre(
				entornId,
				expedientTipusDtoAccessiblesAnotacions,
				filtreDto);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AnotacioDto findAmbId(Long id) throws NoTrobatException {
		return delegateService.findAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void rebutjar(Long anotacioId, String observacions) {
		delegateService.rebutjar(anotacioId, observacions);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AnotacioDto updateExpedient(Long anotacioId, Long expedientTipusId, Long expedientId) {
		return delegateService.updateExpedient(anotacioId, expedientTipusId, expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AnotacioDto incorporarReprocessarExpedient(Long anotacioId, Long expedientTipusId, Long expedientId, boolean associarInteressats, boolean comprovarPermis, boolean reprocessar) {
		return delegateService.incorporarReprocessarExpedient(anotacioId, expedientTipusId, expedientId, associarInteressats, comprovarPermis, reprocessar);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long anotacioId) {
		delegateService.delete(anotacioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Throwable reprocessar(Long anotacioId) {
		return delegateService.reprocessar(anotacioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AnotacioDto marcarPendent(Long anotacioId) throws Exception {
		return delegateService.marcarPendent(anotacioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AnotacioDto reintentarConsulta(Long anotacioId) throws Exception {
		return delegateService.reintentarConsulta(anotacioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto getAnnexContingutVersioOriginal(Long annexId) {
		return delegateService.getAnnexContingutVersioOriginal(annexId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto getAnnexContingutVersioImprimible(Long annexId) {
		return delegateService.getAnnexContingutVersioImprimible(annexId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ArxiuFirmaDto> getAnnexFirmes(Long annexId) {
		return delegateService.getAnnexFirmes(annexId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void reintentarAnnex(Long anotacioId, Long annexId) throws Exception {
		delegateService.reintentarAnnex(anotacioId, annexId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void esborrarAnotacionsExpedient(Long expedientId) {
		delegateService.esborrarAnotacionsExpedient(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AnotacioMapeigResultatDto reprocessarMapeigAnotacioExpedient(Long expedientId, Long anotacioId) {
		return delegateService.reprocessarMapeigAnotacioExpedient(expedientId, anotacioId);

	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Exception reintentarTraspasAnotacio(Long anotacioId) {
		return delegateService.reintentarTraspasAnotacio(anotacioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<String>[] emailAnotacio(long anotacioId) {
		return delegateService.emailAnotacio(anotacioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AnotacioMapeigResultatDto reprocessarMapeigAnotacioExpedient(
			Long expedientId, Long anotacioId,
			boolean reprocessarMapeigVariables,
			boolean reprocessarMapeigDocuments,
			boolean reprocessarMapeigAdjunts,
			boolean reprocessarMapeigInteressats) {
		return delegateService.reprocessarMapeigAnotacioExpedient(expedientId, anotacioId, reprocessarMapeigVariables,
				reprocessarMapeigDocuments, reprocessarMapeigAdjunts, reprocessarMapeigInteressats);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AnotacioMapeigResultatDto processarMapeigAnotacioExpedient(Long expedientTipusId, Long anotacioId) {
		return delegateService.processarMapeigAnotacioExpedient(expedientTipusId, anotacioId);
	}

}