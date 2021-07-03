/**
 * 
 */
package es.caib.helium.ejb;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.emiserv.logic.intf.exception.NoTrobatException;
import es.caib.emiserv.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.intf.dto.AccioDto;
import es.caib.helium.logic.intf.dto.AlertaDto;
import es.caib.helium.logic.intf.dto.ArxiuDetallDto;
import es.caib.helium.logic.intf.dto.ArxiuDto;
import es.caib.helium.logic.intf.dto.CampDto;
import es.caib.helium.logic.intf.dto.DadaIndexadaDto;
import es.caib.helium.logic.intf.dto.DadesDocumentDto;
import es.caib.helium.logic.intf.dto.DadesNotificacioDto;
import es.caib.helium.logic.intf.dto.DefinicioProcesExpedientDto;
import es.caib.helium.logic.intf.dto.ExpedientConsultaDissenyDto;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.ExpedientTascaDto;
import es.caib.helium.logic.intf.dto.InstanciaProcesDto;
import es.caib.helium.logic.intf.dto.MostrarAnulatsDto;
import es.caib.helium.logic.intf.dto.NotificacioDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.PersonaDto;
import es.caib.helium.logic.intf.dto.RespostaValidacioSignaturaDto;
import es.caib.helium.logic.intf.dto.TascaDadaDto;
import es.caib.helium.logic.intf.dto.ExpedientDto.EstatTipusDto;
import es.caib.helium.logic.intf.dto.ExpedientDto.IniciadorTipusDto;
import es.caib.helium.logic.intf.service.ExpedientService;

/**
 * EJB que implementa la interfície del servei ExpedientService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ExpedientServiceBean implements ExpedientService {

	@Autowired
	ExpedientService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientDto create(
			Long entornId,
			String usuari,
			Long expedientTipusId,
			Long definicioProcesId,
			Integer any,
			String numero,
			String titol,
			String registreNumero,
			Date registreData,
			Long unitatAdministrativa,
			String idioma,
			boolean autenticat,
			String tramitadorNif,
			String tramitadorNom,
			String interessatNif,
			String interessatNom,
			String representantNif,
			String representantNom,
			boolean avisosHabilitats,
			String avisosEmail,
			String avisosMobil,
			boolean notificacioTelematicaHabilitada,
			Map<String, Object> variables,
			String transitionName,
			IniciadorTipusDto iniciadorTipus,
			String iniciadorCodi,
			String responsableCodi,
			Map<String, DadesDocumentDto> documents,
			List<DadesDocumentDto> adjunts,
			Long anotacioId,
			boolean anotacioInteressatsAssociar) throws Exception {
		return delegate.create(
				entornId,
				usuari,
				expedientTipusId,
				definicioProcesId,
				any,
				numero,
				titol,
				registreNumero,
				registreData,
				unitatAdministrativa,
				idioma,
				autenticat,
				tramitadorNif,
				tramitadorNom,
				interessatNif,
				interessatNom,
				representantNif,
				representantNom,
				avisosHabilitats,
				avisosEmail,
				avisosMobil,
				notificacioTelematicaHabilitada,
				variables,
				transitionName,
				iniciadorTipus,
				iniciadorCodi,
				responsableCodi,
				documents,
				adjunts,
				anotacioId,
				anotacioInteressatsAssociar);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void update(
			Long id,
			String numero,
			String titol,
			String responsableCodi,
			Date dataInici,
			String comentari,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			String grupCodi,
			boolean execucioDinsHandler) {
		delegate.update(
				id,
				numero,
				titol,
				responsableCodi,
				dataInici,
				comentari,
				estatId,
				geoPosX,
				geoPosY,
				geoReferencia,
				grupCodi,
				execucioDinsHandler);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long id) {
		delegate.delete(id);
	}


	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Long findIdAmbProcessInstanceId(String processInstanceId) {
		return delegate.findIdAmbProcessInstanceId(processInstanceId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientDto findAmbIdAmbPermis(Long id) {
		return delegate.findAmbIdAmbPermis(id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientDto findAmbId(Long id) {
		return delegate.findAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ExpedientDto> findAmbFiltrePaginat(
			Long entornId,
			Long expedientTipusId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Date dataFi1,
			Date dataFi2,
			EstatTipusDto estatTipus,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesAlertes,
			boolean nomesErrors,
			MostrarAnulatsDto mostrarAnulats,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltrePaginat(
				entornId,
				expedientTipusId,
				titol,
				numero,
				dataInici1,
				dataInici2,
				dataFi1,
				dataFi2,
				estatTipus,
				estatId,
				geoPosX,
				geoPosY,
				geoReferencia,
				nomesTasquesPersonals,
				nomesTasquesGrup,
				nomesAlertes,
				nomesErrors,
				mostrarAnulats,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Long> findIdsAmbFiltre(
			Long entornId,
			Long expedientTipusId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Date dataFi1,
			Date dataFi2,
			EstatTipusDto estatTipus,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesAlertes,
			boolean nomesErrors,
			MostrarAnulatsDto mostrarAnulats) {
		return delegate.findIdsAmbFiltre(
				entornId,
				expedientTipusId,
				titol,
				numero,
				dataInici1,
				dataInici2,
				dataFi1,
				dataFi2,
				estatTipus,
				estatId,
				geoPosX,
				geoPosY,
				geoReferencia,
				nomesTasquesPersonals,
				nomesTasquesGrup,
				nomesAlertes,
				nomesErrors,
				mostrarAnulats);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientDto> findPerSuggest(Long expedientTipusId, String text) {
		return delegate.findPerSuggest(expedientTipusId, text);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto getImatgeDefinicioProces(
			Long id,
			String processInstanceId) {
		return delegate.getImatgeDefinicioProces(
				id,
				processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<PersonaDto> findParticipants(Long id) {
		return delegate.findParticipants(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTascaDto> findTasquesPendents(
			Long expedientId,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup) {
		return delegate.findTasquesPendents(
				expedientId,
				nomesTasquesPersonals,
				nomesTasquesGrup);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void aturar(
			Long id,
			String motiu) {
		delegate.aturar(id, motiu);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void reprendre(Long id) {
		delegate.reprendre(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void anular(
			Long id,
			String motiu) {
		delegate.anular(id, motiu);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void desanular(Long id) {
		delegate.desanular(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void relacioCreate(
			Long origenId,
			Long destiId) {
		delegate.relacioCreate(origenId, destiId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void relacioDelete(
			Long origenId,
			Long destiId) {
		delegate.relacioDelete(origenId, destiId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientDto> relacioFindAmbExpedient(Long id) {
		return delegate.relacioFindAmbExpedient(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void procesScriptExec(
			Long expedientId,
			String processInstanceId,
			String script) {
		delegate.procesScriptExec(
				expedientId,
				processInstanceId,
				script);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void procesDefinicioProcesActualitzar(
			String processInstanceId,
			int versio) {
		delegate.procesDefinicioProcesActualitzar(
				processInstanceId,
				versio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void procesDefinicioProcesCanviVersio(
			Long expedientId, 
			Long definicioProcesId, 
			Long[] subProcesIds,
			List<DefinicioProcesExpedientDto> subDefinicioProces) {
		delegate.procesDefinicioProcesCanviVersio(
				expedientId, 
				definicioProcesId, 
				subProcesIds, 
				subDefinicioProces);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AccioDto accioFindAmbId(
			Long expedientId,
			String processInstanceId,
			Long accioId) {
		return delegate.accioFindAmbId(
				expedientId,
				processInstanceId,
				accioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void accioExecutar(
			Long expedientId,
			String processInstanceId,
			Long accioId) {
		delegate.accioExecutar(
				expedientId,
				processInstanceId,
				accioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<AccioDto> accioFindVisiblesAmbProcessInstanceId(
			Long expedientId,
			String processInstanceId) {
		return delegate.accioFindVisiblesAmbProcessInstanceId(
				expedientId,
				processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<AlertaDto> findAlertes(Long id) {
		return delegate.findAlertes(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Object[] findErrorsExpedient(Long id) {
		return delegate.findErrorsExpedient(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientDto> findSuggestAmbEntornLikeIdentificador(Long entornid, String text) {
		return delegate.findSuggestAmbEntornLikeIdentificador(entornid, text);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<InstanciaProcesDto> getArbreInstanciesProces(Long processInstanceId) {
		return delegate.getArbreInstanciesProces(processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ExpedientConsultaDissenyDto> consultaFindPaginat(
			Long consultaId,
			Map<String, Object> filtreValors,
			Set<Long> expedientIdsSeleccio,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean nomesErrors,
			MostrarAnulatsDto mostrarAnulats,
			PaginacioParamsDto paginacioParams) {
		return consultaFindPaginat(
				consultaId,
				filtreValors,
				expedientIdsSeleccio,
				nomesTasquesPersonals,
				nomesTasquesGrup,
				nomesMeves,
				nomesAlertes,
				nomesErrors,
				mostrarAnulats,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<Long> consultaFindNomesIdsPaginat(
			Long consultaId,
			Map<String, Object> filtreValors,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean nomesErrors,
			MostrarAnulatsDto mostrarAnulats,
			PaginacioParamsDto paginacioParams) {
		return consultaFindNomesIdsPaginat(
				consultaId,
				filtreValors,
				nomesTasquesPersonals,
				nomesTasquesGrup,
				nomesMeves,
				nomesAlertes,
				nomesErrors,
				mostrarAnulats,
				paginacioParams);
	}	

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public InstanciaProcesDto getInstanciaProcesById(String processInstanceId) {
		return delegate.getInstanciaProcesById(processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDadaDto> findConsultaFiltre(Long consultaId) {
		return delegate.findConsultaFiltre(consultaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDadaDto> findConsultaInforme(Long consultaId) {
		return delegate.findConsultaInforme(consultaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String getNumeroExpedientActual(Long entornId, Long expedientTipusId, Integer any) {
		return delegate.getNumeroExpedientActual(entornId, expedientTipusId, any);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto getStartTask(Long entornId, Long expedientTipusId, Long definicioProcesId, Map<String, Object> valors) {
		return delegate.getStartTask(entornId, expedientTipusId, definicioProcesId, valors);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean existsExpedientAmbEntornTipusITitol(Long entornId, Long expedientTipusId, String titol) {
		return delegate.existsExpedientAmbEntornTipusITitol(entornId, expedientTipusId, titol);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDadaDto> findConsultaInformeParams(Long consultaId) {
		return delegate.findConsultaInformeParams(consultaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientDto> findAmbIds(Set<Long> ids) {
		return delegate.findAmbIds(ids);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampDto> getCampsInstanciaProcesById(
			Long expedientTipusId,
			String processInstanceId) {
		return delegate.getCampsInstanciaProcesById(expedientTipusId, processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<RespostaValidacioSignaturaDto> verificarSignatura(Long documentStoreId) {
		return delegate.verificarSignatura(documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void deleteSignatura(Long expedientId, Long documentStoreId) {
		delegate.deleteSignatura(expedientId, documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean isDiferentsTipusExpedients(Set<Long> ids) {
		return delegate.isDiferentsTipusExpedients(ids);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void desfinalitzar(Long id) {
		delegate.desfinalitzar(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientConsultaDissenyDto> findConsultaDissenyPaginat(Long consultaId, Map<String, Object> valors, PaginacioParamsDto paginacioParams, boolean nomesMeves, boolean nomesAlertes, boolean mostrarAnulats, boolean nomesTasquesPersonals, boolean nomesTasquesGrup, Set<Long> ids) {
		return delegate.findConsultaDissenyPaginat(consultaId, valors, paginacioParams, nomesMeves, nomesAlertes, mostrarAnulats, nomesTasquesPersonals, nomesTasquesGrup, ids);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Long> findIdsPerConsultaInforme(Long consultaId, Map<String, Object> valors, boolean nomesMeves, boolean nomesAlertes, boolean mostrarAnulats, boolean nomesTasquesPersonals, boolean nomesTasquesGrup) {
		return delegate.findIdsPerConsultaInforme(consultaId, valors, nomesMeves, nomesAlertes, mostrarAnulats, nomesTasquesPersonals, nomesTasquesGrup);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ExpedientConsultaDissenyDto> findConsultaInformePaginat(Long consultaId, Map<String, Object> valorsPerService, boolean nomesMeves, boolean nomesAlertes, boolean mostrarAnulats, boolean nomesTasquesPersonals, boolean nomesTasquesGrup, PaginacioParamsDto paginacioParams) {
		return delegate.findConsultaInformePaginat(consultaId, valorsPerService, nomesMeves, nomesAlertes, mostrarAnulats, nomesTasquesPersonals, nomesTasquesGrup, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean luceneReindexarExpedient(Long expedientId) {
		return delegate.luceneReindexarExpedient(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Map<String, DadaIndexadaDto>> luceneGetDades(long expedientId) {
		return delegate.luceneGetDades(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean existsExpedientAmbEntornTipusINumero(Long entornId, Long expedientTipusId, String numero) {
		return delegate.existsExpedientAmbEntornTipusINumero(entornId, expedientTipusId, numero);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<String> findProcesInstanceIdsAmbEntornAndProcessDefinitionName(Long entornId, String jbpmKey) {
		return delegate.findProcesInstanceIdsAmbEntornAndProcessDefinitionName(entornId, jbpmKey);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<String> findAmbDefinicioProcesId(Long definicioProcesId) {
		return delegate.findAmbDefinicioProcesId(definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<NotificacioDto> findNotificacionsPerExpedientId(Long expedientId) throws NoTrobatException {
		return delegate.findNotificacionsPerExpedientId(expedientId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DadesNotificacioDto> findNotificacionsNotibPerExpedientId(Long expedientId) throws NoTrobatException {
		return delegate.findNotificacionsNotibPerExpedientId(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public NotificacioDto findNotificacioPerId(Long notificacioId) throws NoTrobatException {
		return delegate.findNotificacioPerId(notificacioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void notificacioReprocessar(Long notificacioId) throws NoTrobatException {
		delegate.notificacioReprocessar(notificacioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void finalitzar(Long id) throws NoTrobatException, PermisDenegatException {
		delegate.finalitzar(id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void migrarArxiu(Long id) throws NoTrobatException, PermisDenegatException {
		delegate.migrarArxiu(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void executarCampAccio(Long expedientId, String processInstanceId, String accioCamp) {
		delegate.executarCampAccio(expedientId, processInstanceId, accioCamp);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDetallDto getArxiuDetall(Long expedientId) {
		return delegate.getArxiuDetall(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientDto> findAmbIniciadorCodi(String responsableCodi) {
		return delegate.findAmbIniciadorCodi(responsableCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public byte[] getZipDocumentacio(Long expedientId) {
		return delegate.getZipDocumentacio(expedientId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void arreglarMetadadesNti(Long expedientId) {
		delegate.arreglarMetadadesNti(expedientId);
	}
}