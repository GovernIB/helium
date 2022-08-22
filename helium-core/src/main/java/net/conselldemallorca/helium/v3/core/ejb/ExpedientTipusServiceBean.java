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
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEstadisticaDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto.TipusMapeig;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.v3.core.api.exception.ExportException;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
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
	public ExpedientTipusDto findAmbId(Long expedientTipusId) throws NoTrobatException {
		return delegate.findAmbId(expedientTipusId);
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
	public List<ExpedientTipusDto> findAmbEntornPermisAnotacio(
			Long entornId) throws NoTrobatException {
		return findAmbEntornPermisAnotacio(entornId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findAmbEntornPermisExecucioScript(
			Long entornId) throws NoTrobatException {
		return findAmbEntornPermisExecucioScript(entornId);
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
	public ExpedientTipusDto findAmbIdPermisDissenyarDelegat(
			Long entornId,
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException {
		return findAmbIdPermisDissenyarDelegat(
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
	public List<ExpedientTipusDto> findAmbEntorn(
			Long entornId) throws NoTrobatException {
		return findAmbEntorn(entornId);
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
	public ExpedientTipusDto findAmbCodi(
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
	public List<ExpedientTipusDto> findHeretables(Long entornId) {
		return delegate.findHeretables(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findHeretats(Long expedientTipusId) {
		return delegate.findHeretats(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void permisUpdate(
			Long entornId,
			Long expedientTipusId,
			PermisDto permis,
			boolean entornAdmin) throws NoTrobatException, PermisDenegatException {
		delegate.permisUpdate(
				entornId,
				expedientTipusId,
				permis,
				entornAdmin);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void permisDelete(
			Long entornId,
			Long expedientTipusId,
			Long permisId,
			boolean entornAdmin) throws NoTrobatException, PermisDenegatException {
		delegate.permisDelete(
				entornId,
				expedientTipusId,
				permisId,
				entornAdmin);
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
	public List<EnumeracioDto> enumeracioFindAll(Long expedientTipusId, boolean incloureGlobals) {
		return delegate.enumeracioFindAll(expedientTipusId, incloureGlobals);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ConsultaDto> consultaFindAll(Long expedientTipusId) {
		return delegate.consultaFindAll(expedientTipusId);
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
	public List<String> definicioProcesFindJbjmKey(Long entornId, Long expedientTipusId, boolean herencia, boolean incloureGlobals) {
		return delegate.definicioProcesFindJbjmKey(entornId, expedientTipusId, herencia, incloureGlobals);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void definicioProcesIncorporar(Long expedientTipusId, Long id, boolean sobreescriure, boolean tasques) throws ExportException {
		delegate.definicioProcesIncorporar(expedientTipusId, id, sobreescriure, tasques);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DominiDto> dominiFindAll(Long expedientTipusId, boolean incloureGlobals) {
		return delegate.dominiFindAll(expedientTipusId, incloureGlobals);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EstatDto> estatFindAll(Long expedientTipusId, boolean ambHerencia)
			throws NoTrobatException, PermisDenegatException {
		return delegate.estatFindAll(expedientTipusId, ambHerencia);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EstatDto estatFindAmbId(Long expedientTipusId, Long estatId) {
		return delegate.estatFindAmbId(expedientTipusId, estatId);
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
	public List<ConsultaDto> consultaFindRelacionadesAmbDefinicioProces(Long entornId, Long expedientTipusId,
			String jbpmKey, int versio) {
		return delegate.consultaFindRelacionadesAmbDefinicioProces(entornId, expedientTipusId, jbpmKey, versio);
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
		return delegate.mapeigFindAmbCodiHeliumPerValidarRepeticio(expedientTipusId, codiHelium);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public MapeigSistraDto mapeigFindAmbCodiSistraPerValidarRepeticio(Long expedientTipusId, TipusMapeig tipusMapeig, String codiSistra) {
		return delegate.mapeigFindAmbCodiSistraPerValidarRepeticio(expedientTipusId, tipusMapeig, codiSistra);
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
			boolean sistraActiu,
			Long entornId,
			Long expedientTipusId,
			String tramitCodi,
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
				sistraActiu,
				entornId, 
				expedientTipusId, 
				tramitCodi, 
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
	public List<PersonaDto> personaFindAll(Long entornId, Long expedientTipusId) throws Exception {
		return delegate.personaFindAll(entornId, expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto updateMetadadesNti(
			Long entornId,
			Long expedientTipusId,
			boolean actiu,
			String organo,
			String clasificacion,
			String serieDocumental,
			boolean arxiuActiu) {
		return delegate.updateMetadadesNti(
				entornId,
				expedientTipusId,
				actiu,
				organo,
				clasificacion,
				serieDocumental,
				arxiuActiu);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto updateIntegracioPinbal(
			Long entornId,
			Long expedientTipusId,
			boolean pinbalActiu,
			String pinbalNifCif) {
		return delegate.updateIntegracioPinbal(
				entornId,
				expedientTipusId,
				pinbalActiu,
				pinbalNifCif);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto updateIntegracioNotib(
			Long expedientTipusId, 
			String notibEmisor, 
			String notibCodiProcediment,
			boolean notibActiu) {

		return delegate.updateIntegracioNotib(
				expedientTipusId, 
				notibEmisor, 
				notibCodiProcediment, 
				notibActiu);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto updateIntegracioDistribucio(
			Long entornId, 
			Long expedientTipusId, 
			boolean actiu, 
			String codiProcediment,
			String codiAssumpte,
			boolean procesAuto,
			boolean sistra) {
		return delegate.updateIntegracioDistribucio(entornId, expedientTipusId, actiu, codiProcediment, codiAssumpte, procesAuto, sistra);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto findPerDistribucio(String codiProcediment, String codiAssumpte) {
		return delegate.findPerDistribucio(codiProcediment, codiAssumpte);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto findPerDistribucioValidacio(String codiProcediment, String codiAssumpte) {
		return delegate.findPerDistribucioValidacio(codiProcediment, codiAssumpte);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusEstadisticaDto> findEstadisticaByFiltre(
			Integer anyInicial, 
			Integer anyFinal,
			Long entornId, 
			Long expedientTipusId, 
			Boolean anulats, 
			String numero, 
			String titol, 
			EstatTipusDto estatTipus,
			Long estatId,
			Boolean aturat) {
		return delegate.findEstadisticaByFiltre(
				anyInicial, 
				anyFinal, 
				entornId, 
				expedientTipusId,
				anulats, 
				numero, 
				titol, 
				estatTipus,
				estatId,
				aturat);
	}

	@Override
	public List<ExpedientTipusDto> findTipologiesByFiltre(String codiSIA, Long entornId) {
		return delegate.findTipologiesByFiltre(codiSIA, entornId);
	}

	
}