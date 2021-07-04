package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.CampAgrupacioDto;
import es.caib.helium.logic.intf.dto.CampDto;
import es.caib.helium.logic.intf.dto.CampRegistreDto;
import es.caib.helium.logic.intf.dto.ConsultaDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.TascaDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * EJB que implementa la interfície del servei CampService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class CampService extends AbstractService<es.caib.helium.logic.intf.service.CampService> implements es.caib.helium.logic.intf.service.CampService {

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampAgrupacioDto> agrupacioFindAll(Long expedientTipusId, Long definicioProcesId, boolean herencia)
			throws NoTrobatException, PermisDenegatException {
		return getDelegateService().agrupacioFindAll(expedientTipusId, definicioProcesId, herencia);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampAgrupacioDto agrupacioCreate(Long expedientTipusId, Long definicioProcesId, CampAgrupacioDto agrupacio)
			throws PermisDenegatException {
		return getDelegateService().agrupacioCreate(expedientTipusId, definicioProcesId, agrupacio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampAgrupacioDto agrupacioUpdate(CampAgrupacioDto agrupacio)
			throws NoTrobatException, PermisDenegatException {
		return getDelegateService().agrupacioUpdate(agrupacio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean agrupacioMourePosicio(Long id, int posicio) {
		return getDelegateService().agrupacioMourePosicio(id, posicio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void agrupacioDelete(Long agrupacioCampId) throws NoTrobatException, PermisDenegatException {
		getDelegateService().agrupacioDelete(agrupacioCampId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampAgrupacioDto agrupacioFindAmbCodiPerValidarRepeticio(Long expedientTipusId, Long definicioProcesId,
			String codi) throws NoTrobatException {
		return getDelegateService().agrupacioFindAmbCodiPerValidarRepeticio(expedientTipusId, definicioProcesId, codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampAgrupacioDto agrupacioFindAmbId(Long id) throws NoTrobatException {
		return getDelegateService().agrupacioFindAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<CampAgrupacioDto> agrupacioFindPerDatatable(Long expedientTipusId, Long definicioProcesId,
			String filtre, PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return getDelegateService().agrupacioFindPerDatatable(expedientTipusId, definicioProcesId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampDto create(Long expedientTipusId, Long definicioProcesId, CampDto camp) throws PermisDenegatException {
		return getDelegateService().create(expedientTipusId, definicioProcesId, camp);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampDto update(CampDto camp) throws NoTrobatException, PermisDenegatException {
		return getDelegateService().update(camp);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long campCampId) throws NoTrobatException, PermisDenegatException {
		getDelegateService().delete(campCampId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampDto findAmbId(Long expedientTipusId, Long id) throws NoTrobatException {
		return getDelegateService().findAmbId(expedientTipusId, id);
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
		return getDelegateService().findPerDatatable(expedientTipusId, definicioProcesId, totes, agrupacioId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampDto> findTipusData(Long expedientTipusId, Long definicioProcesId) throws NoTrobatException {
		return getDelegateService().findTipusData(expedientTipusId, definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampDto findAmbCodi(Long tipusExpedientId, Long definicioProcesId, String codi, boolean herencia) {
		return getDelegateService().findAmbCodi(tipusExpedientId, definicioProcesId, codi, herencia);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampDto> findAllOrdenatsPerCodi(Long expedientTipusId, Long definicioProcesId) {
		return getDelegateService().findAllOrdenatsPerCodi(expedientTipusId, definicioProcesId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean afegirAgrupacio(Long campId, Long agrupacioId) {
		return getDelegateService().afegirAgrupacio(campId, agrupacioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean remoureAgrupacio(Long campId) {
		return getDelegateService().remoureAgrupacio(campId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean mourePosicio(Long id, int posicio) {
		return getDelegateService().mourePosicio(id, posicio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampRegistreDto registreCreate(Long campId, CampRegistreDto campRegistre) throws PermisDenegatException {
		return getDelegateService().registreCreate(campId, campRegistre);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampRegistreDto registreUpdate(CampRegistreDto campRegistre)
			throws NoTrobatException, PermisDenegatException {
		return getDelegateService().registreUpdate(campRegistre);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void registreDelete(Long id) throws NoTrobatException, PermisDenegatException {
		getDelegateService().registreDelete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampRegistreDto registreFindAmbId(Long id) throws NoTrobatException {
		return getDelegateService().registreFindAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampDto> registreFindMembresAmbRegistreId(Long registreId) {
		return getDelegateService().registreFindMembresAmbRegistreId(registreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<CampRegistreDto> registreFindPerDatatable(Long campId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return getDelegateService().registreFindPerDatatable(campId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean registreMourePosicio(Long id, int posicio) {
		return getDelegateService().registreMourePosicio(id, posicio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDto> findTasquesPerCamp(Long campId) {
		return getDelegateService().findTasquesPerCamp(campId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ConsultaDto> findConsultesPerCamp(Long expedientTipusId, Long campId) {
		return getDelegateService().findConsultesPerCamp(expedientTipusId, campId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampDto> findRegistresPerCamp(Long campId) {
		return getDelegateService().findRegistresPerCamp(campId);
	}

}
