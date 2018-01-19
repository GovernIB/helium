package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampRegistreDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.CampService;

/**
 * EJB que implementa la interfície del servei CampService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class CampServiceBean implements CampService {

	@Autowired
	CampService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampAgrupacioDto> agrupacioFindAll(Long expedientTipusId, Long definicioProcesId, boolean herencia)
			throws NoTrobatException, PermisDenegatException {
		return delegate.agrupacioFindAll(expedientTipusId, definicioProcesId, herencia);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampAgrupacioDto agrupacioCreate(Long expedientTipusId, Long definicioProcesId, CampAgrupacioDto agrupacio)
			throws PermisDenegatException {
		return delegate.agrupacioCreate(expedientTipusId, definicioProcesId, agrupacio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampAgrupacioDto agrupacioUpdate(CampAgrupacioDto agrupacio)
			throws NoTrobatException, PermisDenegatException {
		return delegate.agrupacioUpdate(agrupacio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean agrupacioMourePosicio(Long id, int posicio) {
		return delegate.agrupacioMourePosicio(id, posicio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void agrupacioDelete(Long agrupacioCampId) throws NoTrobatException, PermisDenegatException {
		delegate.agrupacioDelete(agrupacioCampId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampAgrupacioDto agrupacioFindAmbCodiPerValidarRepeticio(Long expedientTipusId, Long definicioProcesId,
			String codi) throws NoTrobatException {
		return delegate.agrupacioFindAmbCodiPerValidarRepeticio(expedientTipusId, definicioProcesId, codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampAgrupacioDto agrupacioFindAmbId(Long id) throws NoTrobatException {
		return delegate.agrupacioFindAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<CampAgrupacioDto> agrupacioFindPerDatatable(Long expedientTipusId, Long definicioProcesId,
			String filtre, PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.agrupacioFindPerDatatable(expedientTipusId, definicioProcesId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampDto create(Long expedientTipusId, Long definicioProcesId, CampDto camp) throws PermisDenegatException {
		return delegate.create(expedientTipusId, definicioProcesId, camp);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampDto update(CampDto camp) throws NoTrobatException, PermisDenegatException {
		return delegate.update(camp);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long campCampId) throws NoTrobatException, PermisDenegatException {
		delegate.delete(campCampId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampDto findAmbId(Long expedientTipusId, Long id) throws NoTrobatException {
		return delegate.findAmbId(expedientTipusId, id);
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
		return delegate.findPerDatatable(expedientTipusId, definicioProcesId, totes, agrupacioId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampDto> findTipusData(Long expedientTipusId, Long definicioProcesId) throws NoTrobatException {
		return delegate.findTipusData(expedientTipusId, definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampDto findAmbCodi(Long tipusExpedientId, Long definicioProcesId, String codi, boolean herencia) {
		return delegate.findAmbCodi(tipusExpedientId, definicioProcesId, codi, herencia);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampDto> findAllOrdenatsPerCodi(Long expedientTipusId, Long definicioProcesId) {
		return delegate.findAllOrdenatsPerCodi(expedientTipusId, definicioProcesId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean afegirAgrupacio(Long campId, Long agrupacioId) {
		return delegate.afegirAgrupacio(campId, agrupacioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean remoureAgrupacio(Long campId) {
		return delegate.remoureAgrupacio(campId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean mourePosicio(Long id, int posicio) {
		return delegate.mourePosicio(id, posicio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampRegistreDto registreCreate(Long campId, CampRegistreDto campRegistre) throws PermisDenegatException {
		return delegate.registreCreate(campId, campRegistre);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampRegistreDto registreUpdate(CampRegistreDto campRegistre)
			throws NoTrobatException, PermisDenegatException {
		return delegate.registreUpdate(campRegistre);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void registreDelete(Long id) throws NoTrobatException, PermisDenegatException {
		delegate.registreDelete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampRegistreDto registreFindAmbId(Long id) throws NoTrobatException {
		return delegate.registreFindAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampDto> registreFindMembresAmbRegistreId(Long registreId) {
		return delegate.registreFindMembresAmbRegistreId(registreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<CampRegistreDto> registreFindPerDatatable(Long campId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.registreFindPerDatatable(campId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean registreMourePosicio(Long id, int posicio) {
		return delegate.registreMourePosicio(id, posicio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDto> findTasquesPerCamp(Long campId) {
		return delegate.findTasquesPerCamp(campId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ConsultaDto> findConsultesPerCamp(Long campId) {
		return delegate.findConsultesPerCamp(campId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampDto> findRegistresPerCamp(Long campId) {
		return delegate.findRegistresPerCamp(campId);
	}

}
