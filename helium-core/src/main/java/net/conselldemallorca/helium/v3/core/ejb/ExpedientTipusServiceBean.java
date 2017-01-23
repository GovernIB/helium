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

import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto.TipusConsultaCamp;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEnumeracioValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto.TipusMapeig;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitSistraDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.v3.core.api.exportacio.ExpedientTipusExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.ExpedientTipusExportacioCommandDto;
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
	public void delete(
			Long entornId,
			Long expedientTipusId) {
		delegate.delete(entornId, expedientTipusId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusExportacio exportar(
			Long entornId, 
			Long expedientTipusId,
			ExpedientTipusExportacioCommandDto command) {
		return delegate.exportar(entornId, expedientTipusId, command);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto importar(
			Long entornId, 
			Long expedientTipusId, 
			ExpedientTipusExportacioCommandDto command,
			ExpedientTipusExportacio importacio) {
		return delegate.importar(entornId, expedientTipusId, command, importacio);
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
	public List<EnumeracioDto> enumeracioFindAll(Long expedientTipusId) {
		return delegate.enumeracioFindAll(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ConsultaDto> consultaFindAll(Long expedientTipusId) {
		return delegate.consultaFindAll(expedientTipusId);
	}

	/***********************************************/
	/*****************ENUMERACIONS******************/
	/***********************************************/
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<EnumeracioDto> enumeracioFindPerDatatable(Long expedientTipusId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.enumeracioFindPerDatatable(expedientTipusId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EnumeracioDto enumeracioCreate(Long expedientTipusId, Long entornId, EnumeracioDto enumeracio)
			throws PermisDenegatException {
		return delegate.enumeracioCreate(expedientTipusId, entornId, enumeracio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EnumeracioDto enumeracioFindAmbCodi(Long expedientTipusId, String codi)
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
	public EnumeracioDto enumeracioFindAmbId(Long enumeracioId) throws NoTrobatException {
		return delegate.enumeracioFindAmbId(enumeracioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EnumeracioDto enumeracioUpdate(EnumeracioDto enumeracio)
			throws NoTrobatException, PermisDenegatException {
		return delegate.enumeracioUpdate(enumeracio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ExpedientTipusEnumeracioValorDto> enumeracioValorsFindPerDatatable(Long expedientTipusId,
			Long enumeracioId, String filtre, PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.enumeracioValorsFindPerDatatable(expedientTipusId, enumeracioId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusEnumeracioValorDto enumeracioValorsCreate(Long expedientTipusId, Long enumeracioId,
			Long entornId, ExpedientTipusEnumeracioValorDto enumeracio) throws PermisDenegatException {
		return delegate.enumeracioValorsCreate(expedientTipusId, enumeracioId, entornId, enumeracio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void enumeracioValorDelete(Long valorId) throws NoTrobatException, PermisDenegatException {
		delegate.enumeracioValorDelete(valorId);		
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusEnumeracioValorDto enumeracioValorFindAmbId(Long valorId) throws NoTrobatException {
		return delegate.enumeracioValorFindAmbId(valorId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusEnumeracioValorDto enumeracioValorUpdate(ExpedientTipusEnumeracioValorDto enumeracio)
			throws NoTrobatException, PermisDenegatException {
		return delegate.enumeracioValorUpdate(enumeracio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusEnumeracioValorDto enumeracioValorFindAmbCodi(Long expedientTipusId, Long enumeracioId,
			String codi) throws NoTrobatException {
		return delegate.enumeracioValorFindAmbCodi(expedientTipusId, enumeracioId, codi);
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
	public List<DefinicioProcesDto> definicioFindAll(Long expedientTipusId)
			throws NoTrobatException, PermisDenegatException {
		return delegate.definicioFindAll(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean definicioProcesSetInicial(Long expedientTipusId, Long id) {
		return delegate.definicioProcesSetInicial(expedientTipusId, id);
	}	
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<String> definicioProcesFindJbjmKey(Long entornId, Long expedientTipusId, boolean incloureGlobals) {
		return delegate.definicioProcesFindJbjmKey(entornId, expedientTipusId, incloureGlobals);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean definicioProcesIncorporar(Long expedientTipusId, Long id, boolean sobreescriure) {
		return delegate.definicioProcesIncorporar(expedientTipusId, id, sobreescriure);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DominiDto> dominiFindAll(Long expedientTipusId, PaginacioParamsDto paginacioParams)
			throws NoTrobatException, PermisDenegatException {
		return delegate.dominiFindAll(expedientTipusId, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DominiDto> dominiFindAll(Long expedientTipusId) {
		return delegate.dominiFindAll(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DominiDto dominiFindAmbCodi(Long expedientTipusId, String codi) {
		return delegate.dominiFindAmbCodi(expedientTipusId, codi);
	}
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DominiDto dominiFindAmbId(Long dominiId) {
		return delegate.dominiFindAmbId(dominiId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DominiDto dominiCreate(Long expedientTipusId, DominiDto termini) {
		return delegate.dominiCreate(expedientTipusId, termini);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DominiDto dominiUpdate(DominiDto domini) {
		return delegate.dominiUpdate(domini);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<DominiDto> dominiFindPerDatatable(Long expedientTipusId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.dominiFindPerDatatable(expedientTipusId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void dominiDelete(Long dominiId) throws NoTrobatException, PermisDenegatException {
		delegate.dominiDelete(dominiId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EstatDto> estatFindAll(Long expedientTipusId, PaginacioParamsDto paginacioParams)
			throws NoTrobatException, PermisDenegatException {
		return delegate.estatFindAll(expedientTipusId, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EstatDto> estatFindAll(Long expedientTipusId)
			throws NoTrobatException, PermisDenegatException {
		return delegate.estatFindAll(expedientTipusId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EstatDto estatFindAmbId(Long estatId) {
		return delegate.estatFindAmbId(estatId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EstatDto estatFindAmbCodi(Long expedientTipusId, String codi) {
		return delegate.estatFindAmbCodi(expedientTipusId, codi);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EstatDto estatCreate(Long expedientTipusId, EstatDto estat) {
		return delegate.estatCreate(expedientTipusId, estat);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EstatDto estatUpdate(EstatDto estat) {
		return delegate.estatUpdate(estat);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<EstatDto> estatFindPerDatatable(Long expedientTipusId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.estatFindPerDatatable(expedientTipusId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void estatDelete(Long estatId) throws NoTrobatException, PermisDenegatException {
		delegate.estatDelete(estatId);
		
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
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
	public List<ReassignacioDto> reassignacioFindAll(Long expedientTipusId)
			throws NoTrobatException, PermisDenegatException {
		return delegate.reassignacioFindAll(expedientTipusId);
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
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<MapeigSistraDto> mapeigFindPerDatatable(Long expedientTipusId, Long tramitSistraId, TipusMapeig tipus,
			PaginacioParamsDto paginacioParams) {
		return delegate.mapeigFindPerDatatable(expedientTipusId, tramitSistraId, tipus, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public MapeigSistraDto mapeigCreate(Long expedientTipusId, Long tramitSistraId, MapeigSistraDto mapeig) throws PermisDenegatException {
		return delegate.mapeigCreate(expedientTipusId, tramitSistraId, mapeig);
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
	public MapeigSistraDto mapeigFindAmbCodiHeliumPerValidarRepeticio(Long expedientTipusId, String codiHelium, Long tramitSistraId) {
		return delegate.mapeigFindAmbCodiHeliumPerValidarRepeticio(expedientTipusId, codiHelium, tramitSistraId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public MapeigSistraDto mapeigFindAmbCodiSistraPerValidarRepeticio(Long expedientTipusId, String codiSistra, Long tramitSistraId) {
		return delegate.mapeigFindAmbCodiSistraPerValidarRepeticio(expedientTipusId, codiSistra, tramitSistraId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<MapeigSistraDto> mapeigFindAll(Long expedientTipusId) throws NoTrobatException, PermisDenegatException {
		return delegate.mapeigFindAll(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void consultaCampCols(Long id, String propietat, int valor)
			throws NoTrobatException, PermisDenegatException {
		delegate.consultaCampCols(id, propietat, valor);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto updateIntegracioTramits(
			Long entornId, 
			Long expedientTipusId, 
			boolean notificacionsActivades, 
			String notificacioOrganCodi, 
			String notificacioOficinaCodi,
			String notificacioUnitatAdministrativa, 
			String notificacioCodiProcediment, 
			String notificacioAvisTitol,
			String notificacioAvisText,
			String notificacioAvisTextSms, 
			String notificacioOficiTitol,
			String notificacioOficiText) {
		return delegate.updateIntegracioTramits(
				entornId, 
				expedientTipusId, 
				notificacionsActivades, 
				notificacioOrganCodi, 
				notificacioOficinaCodi, 
				notificacioUnitatAdministrativa, 
				notificacioCodiProcediment, 
				notificacioAvisTitol, 
				notificacioAvisText, 
				notificacioAvisTextSms, 
				notificacioOficiTitol, 
				notificacioOficiText);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TramitSistraDto> consultaTramitsSistra(Long expedientTipusId)
			throws NoTrobatException, PermisDenegatException {
		return delegate.consultaTramitsSistra(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<TramitSistraDto> tramitSistraFindPerDatatable(Long expedientTipusId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException, PermisDenegatException {
		return delegate.tramitSistraFindPerDatatable(expedientTipusId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TramitSistraDto tramitSistraCreate(Long expedientTipusId, TramitSistraDto mapeig)
			throws NoTrobatException, PermisDenegatException {
		return delegate.tramitSistraCreate(expedientTipusId, mapeig);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Map<TipusMapeig, Long> mapeigCountsByTipusAndTramitSistra(Long expedientTipusId, Long tramitSistraId) {
		return delegate.mapeigCountsByTipusAndTramitSistra(expedientTipusId, tramitSistraId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TramitSistraDto tramitSistraFindAmbId(Long tramitSistraId) throws NoTrobatException {
		return delegate.tramitSistraFindAmbId(tramitSistraId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TramitSistraDto tramitSistraUpdate(Long expedientTipusId, TramitSistraDto tramitSistra)
			throws NoTrobatException, PermisDenegatException {
		return delegate.tramitSistraUpdate(expedientTipusId, tramitSistra);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void tramitSistraDelete(Long expedientTipusId, Long tramitSistraId)
			throws NoTrobatException, PermisDenegatException, ValidacioException {
		delegate.tramitSistraDelete(expedientTipusId, tramitSistraId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TramitSistraDto> tramitSistraFindAmbSistraTramitCodi(String sistraTramitCodi) throws NoTrobatException {
		return delegate.tramitSistraFindAmbSistraTramitCodi(sistraTramitCodi);
	}
}