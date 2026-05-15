package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.CampAgrupacioDto;
import es.caib.helium.commons.dto.CampDto;
import es.caib.helium.commons.dto.CampRegistreDto;
import es.caib.helium.commons.dto.ConsultaDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.TascaDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.exception.PermisDenegatException;
import es.caib.helium.logic.intf.service.CampService;

/**
 * EJB que implementa la interfície del servei CampService.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class CampServiceBean extends AbstractServiceEjb<CampService> implements CampService {

	@Delegate
	CampService delegateService;

	protected void setDelegateService(CampService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampAgrupacioDto> agrupacioFindAll(Long expedientTipusId, Long definicioProcesId, boolean herencia)
			throws NoTrobatException, PermisDenegatException {
		return delegateService.agrupacioFindAll(expedientTipusId, definicioProcesId, herencia);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampAgrupacioDto agrupacioCreate(Long expedientTipusId, Long definicioProcesId, CampAgrupacioDto agrupacio)
			throws PermisDenegatException {
		return delegateService.agrupacioCreate(expedientTipusId, definicioProcesId, agrupacio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampAgrupacioDto agrupacioUpdate(CampAgrupacioDto agrupacio)
			throws NoTrobatException, PermisDenegatException {
		return delegateService.agrupacioUpdate(agrupacio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean agrupacioMourePosicio(Long id, int posicio) {
		return delegateService.agrupacioMourePosicio(id, posicio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void agrupacioDelete(Long agrupacioCampId) throws NoTrobatException, PermisDenegatException {
		delegateService.agrupacioDelete(agrupacioCampId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampAgrupacioDto agrupacioFindAmbCodiPerValidarRepeticio(Long expedientTipusId, Long definicioProcesId,
			String codi) throws NoTrobatException {
		return delegateService.agrupacioFindAmbCodiPerValidarRepeticio(expedientTipusId, definicioProcesId, codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampAgrupacioDto agrupacioFindAmbId(Long id) throws NoTrobatException {
		return delegateService.agrupacioFindAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<CampAgrupacioDto> agrupacioFindPerDatatable(Long expedientTipusId, Long definicioProcesId,
			String filtre, PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegateService.agrupacioFindPerDatatable(expedientTipusId, definicioProcesId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampDto create(Long expedientTipusId, Long definicioProcesId, CampDto camp) throws PermisDenegatException {
		return delegateService.create(expedientTipusId, definicioProcesId, camp);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampDto update(CampDto camp) throws NoTrobatException, PermisDenegatException {
		return delegateService.update(camp);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long campCampId) throws NoTrobatException, PermisDenegatException {
		delegateService.delete(campCampId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampDto findAmbId(Long expedientTipusId, Long id) throws NoTrobatException {
		return delegateService.findAmbId(expedientTipusId, id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<CampDto> findPerDatatable(
			Long expedientTipusId,
			Long definicioProcesId,
			boolean totes,
			Long agrupacioId,
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegateService.findPerDatatable(expedientTipusId, definicioProcesId, totes, agrupacioId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampDto> findTipusData(Long expedientTipusId, Long definicioProcesId) throws NoTrobatException {
		return delegateService.findTipusData(expedientTipusId, definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampDto findAmbCodi(Long tipusExpedientId, Long definicioProcesId, String codi, boolean herencia) {
		return delegateService.findAmbCodi(tipusExpedientId, definicioProcesId, codi, herencia);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampDto> findAllOrdenatsPerCodi(Long expedientTipusId, Long definicioProcesId) {
		return delegateService.findAllOrdenatsPerCodi(expedientTipusId, definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean afegirAgrupacio(Long campId, Long agrupacioId) {
		return delegateService.afegirAgrupacio(campId, agrupacioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean remoureAgrupacio(Long campId) {
		return delegateService.remoureAgrupacio(campId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean mourePosicio(Long id, int posicio) {
		return delegateService.mourePosicio(id, posicio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampRegistreDto registreCreate(Long campId, CampRegistreDto campRegistre) throws PermisDenegatException {
		return delegateService.registreCreate(campId, campRegistre);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampRegistreDto registreUpdate(CampRegistreDto campRegistre)
			throws NoTrobatException, PermisDenegatException {
		return delegateService.registreUpdate(campRegistre);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void registreDelete(Long id) throws NoTrobatException, PermisDenegatException {
		delegateService.registreDelete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampRegistreDto registreFindAmbId(Long id) throws NoTrobatException {
		return delegateService.registreFindAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampDto> registreFindMembresAmbRegistreId(Long registreId) {
		return delegateService.registreFindMembresAmbRegistreId(registreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<CampRegistreDto> registreFindPerDatatable(Long campId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegateService.registreFindPerDatatable(campId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean registreMourePosicio(Long id, int posicio) {
		return delegateService.registreMourePosicio(id, posicio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDto> findTasquesPerCamp(Long campId) {
		return delegateService.findTasquesPerCamp(campId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ConsultaDto> findConsultesPerCamp(Long expedientTipusId, Long campId) {
		return delegateService.findConsultesPerCamp(expedientTipusId, campId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampDto> findRegistresPerCamp(Long campId) {
		return delegateService.findRegistresPerCamp(campId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampRegistreDto> findRegistresByCampId(Long campId) {
		return delegateService.findRegistresByCampId(campId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampDto findById(Long campId) {
		return delegateService.findById(campId);
	}

}
