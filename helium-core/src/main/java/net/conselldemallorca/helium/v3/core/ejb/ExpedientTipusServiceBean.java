/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampRegistreDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto.TipusConsultaCamp;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEnumeracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEnumeracioValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto.TipusMapeig;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.dto.ValidacioDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;

/**
 * Servei per a gestionar els tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ExpedientTipusServiceBean implements ExpedientTipusService {

	@Autowired
	ExpedientTipusService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto create(
			Long entornId,
			ExpedientTipusDto expedientTipus,
			List<Integer> sequenciesAny, 
			List<Long> sequenciesValor) {
		return delegate.create(
				entornId,
				expedientTipus,
				sequenciesAny,
				sequenciesValor);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto update(
			Long entornId,
			ExpedientTipusDto expedientTipus,
			List<Integer> sequenciesAny, 
			List<Long> sequenciesValor) {
		return delegate.update(
				entornId,
				expedientTipus,
				sequenciesAny,
				sequenciesValor);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto updateIntegracioForms(
			Long entornId, 
			Long expedientTipusId, 
			String url, 
			String usuari,
			String contrasenya) {
		return delegate.updateIntegracioForms(
				entornId, 
				expedientTipusId, 
				url, 
				usuari, 
				contrasenya);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto updateIntegracioTramits(
			Long entornId, 
			Long expedientTipusId, 
			String tramitCodi) {
		return delegate.updateIntegracioTramits(
				entornId, 
				expedientTipusId, 
				tramitCodi);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(
			Long entornId,
			Long expedientTipusId) {
		delegate.delete(entornId, expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findAmbEntornPermisConsultar(
			Long entornId) throws NoTrobatException {
		return findAmbEntornPermisConsultar(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto findAmbIdPermisConsultar(
			Long entornId,
			Long expedientTipusId) {
		return delegate.findAmbIdPermisConsultar(
				entornId,
				expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findAmbEntornPermisDissenyar(
			Long entornId) throws NoTrobatException {
		return findAmbEntornPermisDissenyar(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto findAmbIdPermisDissenyar(
			Long entornId,
			Long expedientTipusId) {
		return delegate.findAmbIdPermisDissenyar(
				entornId,
				expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findAmbEntornPermisCrear(
			Long entornId) throws NoTrobatException {
		return findAmbEntornPermisCrear(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto findAmbIdPermisCrear(
			Long entornId,
			Long expedientTipusId) {
		return delegate.findAmbIdPermisCrear(
				entornId,
				expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto findAmbCodiPerValidarRepeticio(
			Long entornId, 
			String codi) {
		return delegate.findAmbCodiPerValidarRepeticio(
				entornId,
				codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ExpedientTipusDto> findPerDatatable(
			Long entornId, 
			String filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findPerDatatable(
				entornId,
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void permisUpdate(
			Long entornId,
			Long expedientTipusId,
			PermisDto permis) throws NoTrobatException, PermisDenegatException {
		delegate.permisUpdate(
				entornId,
				expedientTipusId,
				permis);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void permisDelete(
			Long entornId,
			Long expedientTipusId,
			Long permisId) throws NoTrobatException, PermisDenegatException {
		delegate.permisDelete(
				entornId,
				expedientTipusId,
				permisId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<PermisDto> permisFindAll(
			Long entornId,
			Long expedientTipusId) {
		return delegate.permisFindAll(
				entornId,
				expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PermisDto permisFindById(
			Long entornId,
			Long expedientTipusId,
			Long permisId) {
		return delegate.permisFindById(
				entornId,
				expedientTipusId,
				permisId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampAgrupacioDto> agrupacioFindAll(
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException {
		
		return null;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampAgrupacioDto agrupacioCreate(
			Long expedientTipusId, 
			CampAgrupacioDto agrupacio) throws PermisDenegatException {
		return delegate.agrupacioCreate(
				expedientTipusId, 
				agrupacio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampAgrupacioDto agrupacioUpdate(
			Long expedientTipusId, 
			CampAgrupacioDto agrupacio) throws NoTrobatException, PermisDenegatException {
		return delegate.agrupacioUpdate(
				expedientTipusId, 
				agrupacio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean agrupacioMourePosicio(Long id, int posicio) {
		return delegate.agrupacioMourePosicio(id, posicio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void agrupacioDelete(
			Long agrupacioCampId) throws NoTrobatException, PermisDenegatException {
		delegate.agrupacioDelete(agrupacioCampId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampAgrupacioDto agrupacioFindAmbCodiPerValidarRepeticio(Long tipusExpedientId, String codi) throws NoTrobatException {
		return delegate.agrupacioFindAmbCodiPerValidarRepeticio(tipusExpedientId, codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampAgrupacioDto agrupacioFindAmbId(Long id) throws NoTrobatException {
		return delegate.agrupacioFindAmbId(id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<CampAgrupacioDto> agrupacioFindPerDatatable(
			Long tipusExpedientId, 
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.agrupacioFindPerDatatable(tipusExpedientId, filtre, paginacioParams);
	}


	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampDto campCreate(Long expedientTipusId, CampDto camp) throws PermisDenegatException {
		return delegate.campCreate(expedientTipusId, camp);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampDto campUpdate(CampDto camp) throws NoTrobatException, PermisDenegatException {
		return delegate.campUpdate(camp);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void campDelete(Long campCampId) throws NoTrobatException, PermisDenegatException {
		delegate.campDelete(campCampId);		
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampDto campFindAmbId(Long id) throws NoTrobatException {
		return delegate.campFindAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampDto campFindAmbCodiPerValidarRepeticio(Long tipusExpedientId, String codi) throws NoTrobatException {
		return delegate.campFindAmbCodiPerValidarRepeticio(tipusExpedientId, codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<CampDto> campFindPerDatatable(
			Long expedientTipusId,
			Long agrupacioId,
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.campFindPerDatatable(
				expedientTipusId,
				agrupacioId,
				filtre, 
				paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean campAfegirAgrupacio(Long id, Long agrupacioId) {
		return delegate.campAfegirAgrupacio(id, agrupacioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean campRemoureAgrupacio(Long id) {
		return delegate.campRemoureAgrupacio(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean campMourePosicio(Long id, int posicio) {
		return delegate.campMourePosicio(id, posicio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampDto> campFindTipusDataPerExpedientTipus(
			Long expedientTipusId){
		return delegate.campFindTipusDataPerExpedientTipus(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampDto> campFindAllOrdenatsPerCodi(Long expedientTipusId) {
		return delegate.campFindAllOrdenatsPerCodi(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EnumeracioDto> enumeracioFindAll(Long expedientTipusId) {
		return delegate.enumeracioFindAll(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DominiDto> dominiFindAll(Long expedientTipusId) {
		return delegate.dominiFindAll(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ConsultaDto> consultaFindAll(Long expedientTipusId) {
		return delegate.consultaFindAll(expedientTipusId);
	}

	/***********************************************/
	/******************DOCUMENTS********************/
	/***********************************************/
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ExpedientTipusDocumentDto> documentFindPerDatatable(
			Long expedientTipusId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.documentFindPerDatatable(
				expedientTipusId,
				filtre, 
				paginacioParams);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDocumentDto documentCreate(
			Long expedientTipusId, 
			ExpedientTipusDocumentDto document) {
		return delegate.documentCreate(expedientTipusId, document);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDocumentDto documentFindAmbCodi(
			Long expedientTipusId, 
			String codi) {
		return delegate.documentFindAmbCodi(expedientTipusId, codi);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void documentDelete(
			Long documentId) {
		delegate.documentDelete(documentId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDocumentDto documentFindAmbId(Long id) {
		return delegate.documentFindAmbId(id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDocumentDto documentUpdate(ExpedientTipusDocumentDto document) {
		return delegate.documentUpdate(document);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto getArxiuPerDocument(
			Long id) {
		return delegate.getArxiuPerDocument(id);
	}

	/***********************************************/
	/*****************ENUMERACIONS******************/
	/***********************************************/
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ExpedientTipusEnumeracioDto> enumeracioFindPerDatatable(Long expedientTipusId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.enumeracioFindPerDatatable(expedientTipusId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusEnumeracioDto enumeracioCreate(Long expedientTipusId, Long entornId, ExpedientTipusEnumeracioDto enumeracio)
			throws PermisDenegatException {
		return delegate.enumeracioCreate(expedientTipusId, entornId, enumeracio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusEnumeracioDto enumeracioFindAmbCodi(Long expedientTipusId, String codi)
			throws NoTrobatException {
		return delegate.enumeracioFindAmbCodi(expedientTipusId, codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void enumeracioDelete(Long enumeracioId) throws NoTrobatException, PermisDenegatException {
		delegate.enumeracioDelete(enumeracioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusEnumeracioDto enumeracioFindAmbId(Long enumeracioId) throws NoTrobatException {
		return delegate.enumeracioFindAmbId(enumeracioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusEnumeracioDto enumeracioUpdate(ExpedientTipusEnumeracioDto enumeracio)
			throws NoTrobatException, PermisDenegatException {
		return delegate.enumeracioUpdate(enumeracio);
	}

	@Override
	public PaginaDto<ExpedientTipusEnumeracioValorDto> enumeracioValorsFindPerDatatable(Long expedientTipusId,
			Long enumeracioId, String filtre, PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.enumeracioValorsFindPerDatatable(expedientTipusId, enumeracioId, filtre, paginacioParams);
	}

	@Override
	public ExpedientTipusEnumeracioValorDto enumeracioValorsCreate(Long expedientTipusId, Long enumeracioId,
			Long entornId, ExpedientTipusEnumeracioValorDto enumeracio) throws PermisDenegatException {
		return delegate.enumeracioValorsCreate(expedientTipusId, enumeracioId, entornId, enumeracio);
	}

	@Override
	public void enumeracioValorDelete(Long valorId) throws NoTrobatException, PermisDenegatException {
		delegate.enumeracioValorDelete(valorId);		
	}

	@Override
	public ExpedientTipusEnumeracioValorDto enumeracioValorFindAmbId(Long valorId) throws NoTrobatException {
		return delegate.enumeracioValorFindAmbId(valorId);
	}

	@Override
	public ExpedientTipusEnumeracioValorDto enumeracioValorUpdate(ExpedientTipusEnumeracioValorDto enumeracio)
			throws NoTrobatException, PermisDenegatException {
		return delegate.enumeracioValorUpdate(enumeracio);
	}

	@Override
	public ExpedientTipusEnumeracioValorDto enumeracioValorFindAmbCodi(Long expedientTipusId, Long enumeracioId,
			String codi) throws NoTrobatException {
		return delegate.enumeracioValorFindAmbCodi(expedientTipusId, enumeracioId, codi);
	}

	/// Validacions
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ValidacioDto validacioCreate(Long campId, ValidacioDto validacio) throws PermisDenegatException {
		return delegate.validacioCreate(campId, validacio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ValidacioDto validacioUpdate(ValidacioDto validacio) throws NoTrobatException, PermisDenegatException {
		return delegate.validacioUpdate(validacio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void validacioDelete(Long id) throws NoTrobatException, PermisDenegatException {
		delegate.validacioDelete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ValidacioDto validacioFindAmbId(Long id) throws NoTrobatException {
		return delegate.validacioFindAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ValidacioDto> validacioFindPerDatatable(Long campId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.validacioFindPerDatatable(campId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean validacioMourePosicio(Long id, int posicio) {
		return delegate.validacioMourePosicio(id, posicio);
	}
	
	/// CampsRegistre
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampRegistreDto campRegistreCreate(Long campId, CampRegistreDto campRegistre) throws PermisDenegatException {
		return delegate.campRegistreCreate(campId, campRegistre);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampRegistreDto campRegistreUpdate(CampRegistreDto campRegistre) throws NoTrobatException, PermisDenegatException {
		return delegate.campRegistreUpdate(campRegistre);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void campRegistreDelete(Long id) throws NoTrobatException, PermisDenegatException {
		delegate.campRegistreDelete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampRegistreDto campRegistreFindAmbId(Long id) throws NoTrobatException {
		return delegate.campRegistreFindAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampDto> campRegistreFindMembresAmbRegistreId(Long registreId) {
		return delegate.campRegistreFindMembresAmbRegistreId(registreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<CampRegistreDto> campRegistreFindPerDatatable(Long campId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.campRegistreFindPerDatatable(campId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean campRegistreMourePosicio(Long id, int posicio) {
		return delegate.campRegistreMourePosicio(id, posicio);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AccioDto accioCreate(Long expedientTipusId, AccioDto accio) throws PermisDenegatException {
		return delegate.accioCreate(expedientTipusId, accio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AccioDto accioUpdate(AccioDto accio) throws NoTrobatException, PermisDenegatException {
		return delegate.accioUpdate(accio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void accioDelete(Long accioAccioId) throws NoTrobatException, PermisDenegatException {
		delegate.accioDelete(accioAccioId);		
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AccioDto accioFindAmbId(Long id) throws NoTrobatException {
		return delegate.accioFindAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AccioDto accioFindAmbCodiPerValidarRepeticio(Long tipusExpedientId, String codi) throws NoTrobatException {
		return delegate.accioFindAmbCodiPerValidarRepeticio(tipusExpedientId, codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<AccioDto> accioFindPerDatatable(
			Long expedientTipusId,			
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.accioFindPerDatatable(
				expedientTipusId,
				filtre, 
				paginacioParams);
	}	

	@Override
	public boolean enumeracioValorMourer(Long valorId, int posicio) throws NoTrobatException {
		return delegate.enumeracioValorMourer(valorId, posicio);
	}

	@Override
	public void enumeracioDeleteAllByEnumeracio(Long enumeracioId)
			throws NoTrobatException, PermisDenegatException, ValidacioException {
		delegate.enumeracioDeleteAllByEnumeracio(enumeracioId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void definicioProcesDelete(Long id) throws NoTrobatException, PermisDenegatException {
		delegate.definicioProcesDelete(id);		
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<DefinicioProcesDto> definicioProcesFindPerDatatable(
			Long entornId,
			Long expedientTipusId,
			boolean incloureGlobals,
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.definicioProcesFindPerDatatable(
				entornId,
				expedientTipusId,
				incloureGlobals,
				filtre, 
				paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean definicioProcesSetInicial(Long expedientTipusId, Long id) {
		return delegate.definicioProcesSetInicial(expedientTipusId, id);
	}	
	
	/***********************************************/
	/*******************TERMINIS********************/
	/***********************************************/
	
	/**
	 * Retorna els terminis per a un tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 * @return
	 * @throws NoTrobatException
	 * @throws PermisDenegatException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TerminiDto> terminiFindAll(
			Long expedientTipusId,
			PaginacioParamsDto paginacioParams) throws NoTrobatException, PermisDenegatException {
		return delegate.terminiFindAll(
				expedientTipusId, 
				paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiDto terminiFindAmbId(Long terminiId) {
		return delegate.terminiFindAmbId(terminiId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiDto terminiCreate(
			Long expedientTipusId, 
			TerminiDto termini) {
		return delegate.terminiCreate(expedientTipusId, termini);
		
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiDto terminiUpdate(TerminiDto termini) {
		return delegate.terminiUpdate(termini);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void terminiDelete(Long terminiId) throws NoTrobatException, PermisDenegatException {
		delegate.terminiDelete(terminiId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<TerminiDto> terminiFindPerDatatable(Long expedientTipusId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.terminiFindPerDatatable(expedientTipusId, filtre, paginacioParams);
	}	

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean definicioProcesImportar(Long expedientTipusId, Long id, boolean sobreescriure) {
		return delegate.definicioProcesImportar(expedientTipusId, id, sobreescriure);
	}

	@Override
	public List<DominiDto> dominiFindAll(Long expedientTipusId, PaginacioParamsDto paginacioParams)
			throws NoTrobatException, PermisDenegatException {
		return delegate.dominiFindAll(expedientTipusId, paginacioParams);
	}

	@Override
	public DominiDto dominiFindAmbId(Long dominiId) {
		return delegate.dominiFindAmbId(dominiId);
	}

	@Override
	public DominiDto dominiCreate(Long expedientTipusId, DominiDto termini) {
		return delegate.dominiCreate(expedientTipusId, termini);
	}

	@Override
	public DominiDto dominiUpdate(DominiDto domini) {
		return delegate.dominiUpdate(domini);
	}

	@Override
	public PaginaDto<DominiDto> dominiFindPerDatatable(Long expedientTipusId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.dominiFindPerDatatable(expedientTipusId, filtre, paginacioParams);
	}

	@Override
	public void dominiDelete(Long dominiId) throws NoTrobatException, PermisDenegatException {
		delegate.dominiDelete(dominiId);
	}

	@Override
	public List<EstatDto> estatFindAll(Long expedientTipusId, PaginacioParamsDto paginacioParams)
			throws NoTrobatException, PermisDenegatException {
		return delegate.estatFindAll(expedientTipusId, paginacioParams);
	}

	@Override
	public EstatDto estatFindAmbId(Long estatId) {
		return delegate.estatFindAmbId(estatId);
	}

	@Override
	public EstatDto estatCreate(Long expedientTipusId, EstatDto estat) {
		return delegate.estatCreate(expedientTipusId, estat);
	}

	@Override
	public EstatDto estatUpdate(EstatDto estat) {
		return delegate.estatUpdate(estat);
	}

	@Override
	public PaginaDto<EstatDto> estatFindPerDatatable(Long expedientTipusId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.estatFindPerDatatable(expedientTipusId, filtre, paginacioParams);
	}

	@Override
	public void estatDelete(Long estatId) throws NoTrobatException, PermisDenegatException {
		delegate.estatDelete(estatId);
		
	}

	@Override
	public boolean estatMoure(Long estatId, int posicio) throws NoTrobatException {
		return delegate.estatMoure(estatId, posicio);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ReassignacioDto reassignacioCreate(Long expedientTipusId, ReassignacioDto reassignacio) throws PermisDenegatException {
		return delegate.reassignacioCreate(expedientTipusId, reassignacio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ReassignacioDto reassignacioUpdate(ReassignacioDto reassignacio) throws NoTrobatException, PermisDenegatException {
		return delegate.reassignacioUpdate(reassignacio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void reassignacioDelete(Long reassignacioReassignacioId) throws NoTrobatException, PermisDenegatException {
		delegate.reassignacioDelete(reassignacioReassignacioId);		
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ReassignacioDto reassignacioFindAmbId(Long id) throws NoTrobatException {
		return delegate.reassignacioFindAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ReassignacioDto> reassignacioFindPerDatatable(
			Long expedientTipusId,			
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.reassignacioFindPerDatatable(
				expedientTipusId,
				filtre, 
				paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ConsultaDto consultaCreate(Long expedientTipusId, ConsultaDto consulta) throws PermisDenegatException {
		return delegate.consultaCreate(expedientTipusId, consulta);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ConsultaDto consultaUpdate(ConsultaDto consulta, boolean actualitzarContingut) throws NoTrobatException, PermisDenegatException {
		return delegate.consultaUpdate(consulta, actualitzarContingut);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void consultaDelete(Long consultaId) throws NoTrobatException, PermisDenegatException {
		delegate.consultaDelete(consultaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ConsultaDto consultaFindAmbId(Long id) throws NoTrobatException {
		return delegate.consultaFindAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ConsultaDto> consultaFindPerDatatable(Long entornId, Long expedientTipusId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.consultaFindPerDatatable(entornId, expedientTipusId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ConsultaDto consultaFindAmbCodiPerValidarRepeticio(Long tipusExpedientId, String codi)
			throws NoTrobatException {
		return delegate.consultaFindAmbCodiPerValidarRepeticio(tipusExpedientId, codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean consultaMourePosicio(Long id, int posicio) {
		return delegate.consultaMourePosicio(id, posicio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ConsultaCampDto consultaCampCreate(Long consultaId, ConsultaCampDto consultaCamp)
			throws PermisDenegatException {
		return delegate.consultaCampCreate(consultaId, consultaCamp);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void consultaCampDelete(Long id) throws NoTrobatException, PermisDenegatException {
		delegate.consultaCampDelete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ConsultaCampDto> consultaCampFindPerDatatable(Long consultaId, TipusConsultaCamp tipus,
			String filtre, PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.consultaCampFindPerDatatable(consultaId, tipus, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean consultaCampMourePosicio(Long id, int posicio) {
		return delegate.consultaCampMourePosicio(id, posicio);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ConsultaCampDto> consultaCampFindCampAmbConsultaIdAndTipus(Long consultaId, TipusConsultaCamp tipus) {
		return delegate.consultaCampFindCampAmbConsultaIdAndTipus(consultaId, tipus);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ConsultaCampDto consultaCampUpdate(ConsultaCampDto consultaCamp)
			throws NoTrobatException, PermisDenegatException {
		return delegate.consultaCampUpdate(consultaCamp);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ConsultaCampDto consultaCampFindAmbTipusICodiPerValidarRepeticio(Long consultaId, TipusConsultaCamp tipus,
			String codi) throws NoTrobatException {
		return delegate.consultaCampFindAmbTipusICodiPerValidarRepeticio(consultaId, tipus, codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<String> mapeigFindCodiHeliumAmbTipus(Long expedientTipusId, TipusMapeig tipus) {
		return delegate.mapeigFindCodiHeliumAmbTipus(expedientTipusId, tipus);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Map<TipusMapeig, Long> mapeigCountsByTipus(Long expedientTipusId) {
		return delegate.mapeigCountsByTipus(expedientTipusId);
	}

	@Override
	public PaginaDto<MapeigSistraDto> mapeigFindPerDatatable(Long expedientTipusId, TipusMapeig tipus,
			PaginacioParamsDto paginacioParams) {
		return delegate.mapeigFindPerDatatable(expedientTipusId, tipus, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public MapeigSistraDto mapeigCreate(Long expedientTipusId, MapeigSistraDto mapeig) throws PermisDenegatException {
		return delegate.mapeigCreate(expedientTipusId, mapeig);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public MapeigSistraDto mapeigUpdate(MapeigSistraDto mapeig) throws NoTrobatException, PermisDenegatException {
		return delegate.mapeigUpdate(mapeig);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mapeigDelete(Long mapeigId) throws NoTrobatException, PermisDenegatException {
		delegate.mapeigDelete(mapeigId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public MapeigSistraDto mapeigFindAmbCodiHeliumPerValidarRepeticio(Long expedientTipusId, String codiHelium) {
		// TODO Auto-generated method stub
		return delegate.mapeigFindAmbCodiHeliumPerValidarRepeticio(expedientTipusId, codiHelium);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public MapeigSistraDto mapeigFindAmbCodiSistraPerValidarRepeticio(Long expedientTipusId, String codiSistra) {
		return delegate.mapeigFindAmbCodiSistraPerValidarRepeticio(expedientTipusId, codiSistra);
	}
}