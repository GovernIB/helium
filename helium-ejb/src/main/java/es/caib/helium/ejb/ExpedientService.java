/**
 * 
 */
package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.*;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.ExpedientDto.EstatTipusDto;
import es.caib.helium.logic.intf.dto.ExpedientDto.IniciadorTipusDto;
import es.caib.helium.logic.intf.dto.expedient.ExpedientIniciDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * EJB que implementa la interfície del servei ExpedientService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ExpedientService extends AbstractService<es.caib.helium.logic.intf.service.ExpedientService> implements es.caib.helium.logic.intf.service.ExpedientService {

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientIniciDto create(
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
		return getDelegateService().create(
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
		getDelegateService().update(
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
		getDelegateService().delete(id);
	}


	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Long findIdAmbProcessInstanceId(String processInstanceId) {
		return getDelegateService().findIdAmbProcessInstanceId(processInstanceId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientDto findAmbIdAmbPermis(Long id) {
		return getDelegateService().findAmbIdAmbPermis(id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientDto findAmbId(Long id) {
		return getDelegateService().findAmbId(id);
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
		return getDelegateService().findAmbFiltrePaginat(
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
		return getDelegateService().findIdsAmbFiltre(
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
		return getDelegateService().findPerSuggest(expedientTipusId, text);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto getImatgeDefinicioProces(
			Long id,
			String processInstanceId) {
		return getDelegateService().getImatgeDefinicioProces(
				id,
				processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<PersonaDto> findParticipants(Long id) {
		return getDelegateService().findParticipants(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTascaDto> findTasquesPendents(
			Long expedientId,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup) {
		return getDelegateService().findTasquesPendents(
				expedientId,
				nomesTasquesPersonals,
				nomesTasquesGrup);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void aturar(
			Long id,
			String motiu) {
		getDelegateService().aturar(id, motiu);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void reprendre(Long id) {
		getDelegateService().reprendre(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void anular(
			Long id,
			String motiu) {
		getDelegateService().anular(id, motiu);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void desanular(Long id) {
		getDelegateService().desanular(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void relacioCreate(
			Long origenId,
			Long destiId) {
		getDelegateService().relacioCreate(origenId, destiId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void relacioDelete(
			Long origenId,
			Long destiId) {
		getDelegateService().relacioDelete(origenId, destiId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientDto> relacioFindAmbExpedient(Long id) {
		return getDelegateService().relacioFindAmbExpedient(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void procesScriptExec(
			Long expedientId,
			String processInstanceId,
			String script) {
		getDelegateService().procesScriptExec(
				expedientId,
				processInstanceId,
				script);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void procesDefinicioProcesActualitzar(
			String processInstanceId,
			int versio) {
		getDelegateService().procesDefinicioProcesActualitzar(
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
		getDelegateService().procesDefinicioProcesCanviVersio(
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
		return getDelegateService().accioFindAmbId(
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
		getDelegateService().accioExecutar(
				expedientId,
				processInstanceId,
				accioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<AccioDto> accioFindVisiblesAmbProcessInstanceId(
			Long expedientId,
			String processInstanceId) {
		return getDelegateService().accioFindVisiblesAmbProcessInstanceId(
				expedientId,
				processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<AlertaDto> findAlertes(Long id) {
		return getDelegateService().findAlertes(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Object[] findErrorsExpedient(Long id) {
		return getDelegateService().findErrorsExpedient(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientDto> findSuggestAmbEntornLikeIdentificador(Long entornid, String text) {
		return getDelegateService().findSuggestAmbEntornLikeIdentificador(entornid, text);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<InstanciaProcesDto> getArbreInstanciesProces(Long processInstanceId) {
		return getDelegateService().getArbreInstanciesProces(processInstanceId);
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
		return getDelegateService().getInstanciaProcesById(processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDadaDto> findConsultaFiltre(Long consultaId) {
		return getDelegateService().findConsultaFiltre(consultaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDadaDto> findConsultaInforme(Long consultaId) {
		return getDelegateService().findConsultaInforme(consultaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String getNumeroExpedientActual(Long entornId, Long expedientTipusId, Integer any) {
		return getDelegateService().getNumeroExpedientActual(entornId, expedientTipusId, any);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto getStartTask(Long entornId, Long expedientTipusId, Long definicioProcesId, Map<String, Object> valors) {
		return getDelegateService().getStartTask(entornId, expedientTipusId, definicioProcesId, valors);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean existsExpedientAmbEntornTipusITitol(Long entornId, Long expedientTipusId, String titol) {
		return getDelegateService().existsExpedientAmbEntornTipusITitol(entornId, expedientTipusId, titol);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDadaDto> findConsultaInformeParams(Long consultaId) {
		return getDelegateService().findConsultaInformeParams(consultaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientDto> findAmbIds(Set<Long> ids) {
		return getDelegateService().findAmbIds(ids);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampDto> getCampsInstanciaProcesById(
			Long expedientTipusId,
			String processInstanceId) {
		return getDelegateService().getCampsInstanciaProcesById(expedientTipusId, processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<RespostaValidacioSignaturaDto> verificarSignatura(Long documentStoreId) {
		return getDelegateService().verificarSignatura(documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void deleteSignatura(Long expedientId, Long documentStoreId) {
		getDelegateService().deleteSignatura(expedientId, documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean isDiferentsTipusExpedients(Set<Long> ids) {
		return getDelegateService().isDiferentsTipusExpedients(ids);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void desfinalitzar(Long id) {
		getDelegateService().desfinalitzar(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientConsultaDissenyDto> findConsultaDissenyPaginat(Long consultaId, Map<String, Object> valors, PaginacioParamsDto paginacioParams, boolean nomesMeves, boolean nomesAlertes, boolean mostrarAnulats, boolean nomesTasquesPersonals, boolean nomesTasquesGrup, Set<Long> ids) {
		return getDelegateService().findConsultaDissenyPaginat(consultaId, valors, paginacioParams, nomesMeves, nomesAlertes, mostrarAnulats, nomesTasquesPersonals, nomesTasquesGrup, ids);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Long> findIdsPerConsultaInforme(Long consultaId, Map<String, Object> valors, boolean nomesMeves, boolean nomesAlertes, boolean mostrarAnulats, boolean nomesTasquesPersonals, boolean nomesTasquesGrup) {
		return getDelegateService().findIdsPerConsultaInforme(consultaId, valors, nomesMeves, nomesAlertes, mostrarAnulats, nomesTasquesPersonals, nomesTasquesGrup);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ExpedientConsultaDissenyDto> findConsultaInformePaginat(Long consultaId, Map<String, Object> valorsPerService, boolean nomesMeves, boolean nomesAlertes, boolean mostrarAnulats, boolean nomesTasquesPersonals, boolean nomesTasquesGrup, PaginacioParamsDto paginacioParams) {
		return getDelegateService().findConsultaInformePaginat(consultaId, valorsPerService, nomesMeves, nomesAlertes, mostrarAnulats, nomesTasquesPersonals, nomesTasquesGrup, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean luceneReindexarExpedient(Long expedientId) {
		return getDelegateService().luceneReindexarExpedient(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Map<String, DadaIndexadaDto>> luceneGetDades(long expedientId) {
		return getDelegateService().luceneGetDades(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean existsExpedientAmbEntornTipusINumero(Long entornId, Long expedientTipusId, String numero) {
		return getDelegateService().existsExpedientAmbEntornTipusINumero(entornId, expedientTipusId, numero);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<String> findProcesInstanceIdsAmbEntornAndProcessDefinitionName(Long entornId, String jbpmKey) {
		return getDelegateService().findProcesInstanceIdsAmbEntornAndProcessDefinitionName(entornId, jbpmKey);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<String> findAmbDefinicioProcesId(Long definicioProcesId) {
		return getDelegateService().findAmbDefinicioProcesId(definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<NotificacioDto> findNotificacionsPerExpedientId(Long expedientId) throws NoTrobatException {
		return getDelegateService().findNotificacionsPerExpedientId(expedientId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DadesNotificacioDto> findNotificacionsNotibPerExpedientId(Long expedientId) throws NoTrobatException {
		return getDelegateService().findNotificacionsNotibPerExpedientId(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public NotificacioDto findNotificacioPerId(Long notificacioId) throws NoTrobatException {
		return getDelegateService().findNotificacioPerId(notificacioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void notificacioReprocessar(Long notificacioId) throws NoTrobatException {
		getDelegateService().notificacioReprocessar(notificacioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void finalitzar(Long id) throws NoTrobatException, PermisDenegatException {
		getDelegateService().finalitzar(id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void migrarArxiu(Long id) throws NoTrobatException, PermisDenegatException {
		getDelegateService().migrarArxiu(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void executarCampAccio(Long expedientId, String processInstanceId, String accioCamp) {
		getDelegateService().executarCampAccio(expedientId, processInstanceId, accioCamp);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDetallDto getArxiuDetall(Long expedientId) {
		return getDelegateService().getArxiuDetall(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientDto> findAmbIniciadorCodi(String responsableCodi) {
		return getDelegateService().findAmbIniciadorCodi(responsableCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public byte[] getZipDocumentacio(Long expedientId) {
		return getDelegateService().getZipDocumentacio(expedientId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void arreglarMetadadesNti(Long expedientId) {
		getDelegateService().arreglarMetadadesNti(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void clearExpedientIniciant() {
		getDelegateService().clearExpedientIniciant();
	}
}
