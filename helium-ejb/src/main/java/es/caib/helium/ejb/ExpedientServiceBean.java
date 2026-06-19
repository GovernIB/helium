/**
 *
 */
package es.caib.helium.ejb;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.commons.dto.AccioDto;
import es.caib.helium.commons.dto.AlertaDto;
import es.caib.helium.commons.dto.ArxiuDetallDto;
import es.caib.helium.commons.dto.ArxiuDto;
import es.caib.helium.commons.dto.CampDto;
import es.caib.helium.commons.dto.DadesDocumentDto;
import es.caib.helium.commons.dto.DadesNotificacioDto;
import es.caib.helium.commons.dto.DefinicioProcesExpedientDto;
import es.caib.helium.commons.dto.EntornDto;
import es.caib.helium.commons.dto.EstatDto;
import es.caib.helium.commons.dto.ExpedientConsultaDissenyDto;
import es.caib.helium.commons.dto.ExpedientDocumentDto;
import es.caib.helium.commons.dto.ExpedientDto;
import es.caib.helium.commons.dto.ExpedientDto.EstatTipusDto;
import es.caib.helium.commons.dto.ExpedientDto.IniciadorTipusDto;
import es.caib.helium.commons.dto.ExpedientTascaDto;
import es.caib.helium.commons.dto.ExpedientTipusDto;
import es.caib.helium.commons.dto.InstanciaProcesDto;
import es.caib.helium.commons.dto.MostrarAnulatsDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.PersonaDto;
import es.caib.helium.commons.dto.RespostaValidacioSignaturaDto;
import es.caib.helium.commons.dto.TascaDadaDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.exception.PermisDenegatException;
import es.caib.helium.ejb.base.AbstractServiceEjb;
import es.caib.helium.logic.intf.service.ExpedientService;
import lombok.experimental.Delegate;

/**
 * EJB que implementa la interfície del servei ExpedientService.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ExpedientServiceBean extends AbstractServiceEjb<ExpedientService> implements ExpedientService {

	@Delegate
	ExpedientService delegateService;

	protected void setDelegateService(ExpedientService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientDto create(
			Long entornId,
			String usuari,
			Long expedientTipusId,
			Long definicioProcesId,
			Integer any,
			String numero,
			String unitatOrganitzativaCodi,
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
			List<DadesDocumentDto> documents,
			List<DadesDocumentDto> adjunts,
			Long anotacioId,
			boolean anotacioInteressatsAssociar) throws Exception {
		return delegateService.create(
				entornId,
				usuari,
				expedientTipusId,
				definicioProcesId,
				any,
				numero,
				unitatOrganitzativaCodi,
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
	public boolean update(
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
		return delegateService.update(
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
		delegateService.delete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Long findIdAmbProcessInstanceId(String processInstanceId) {
		return delegateService.findIdAmbProcessInstanceId(processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientDto findAmbIdAmbPermis(Long id) {
		return delegateService.findAmbIdAmbPermis(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientDto findAmbId(Long id) {
		return delegateService.findAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientDto findExpedientAmbProcessInstanceId(String processInstanceId) {
		return delegateService.findExpedientAmbProcessInstanceId(processInstanceId);
	}


	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ExpedientDto> findAmbFiltrePaginat(
			List<ExpedientTipusDto> expedientTipusDtoAccessibles,
			Long entornId,
			Long expedientTipusId,
			String titol,
			String numero,
			String unitatOrganitzativaCodi,
			Date dataInici1,
			Date dataInici2,
			Date dataFi1,
			Date dataFi2,
			EstatTipusDto estatTipus,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			String registreNumero,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesAlertes,
			boolean nomesErrors,
			boolean nomesErrorsArxiu,
			MostrarAnulatsDto mostrarAnulats,
			Set<Long> idsSeleccionats,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findAmbFiltrePaginat(
				expedientTipusDtoAccessibles,
				entornId,
				expedientTipusId,
				titol,
				numero,
				unitatOrganitzativaCodi,
				dataInici1,
				dataInici2,
				dataFi1,
				dataFi2,
				estatTipus,
				estatId,
				geoPosX,
				geoPosY,
				geoReferencia,
				registreNumero,
				nomesTasquesPersonals,
				nomesTasquesGrup,
				nomesAlertes,
				nomesErrors,
				nomesErrorsArxiu,
				mostrarAnulats,
				idsSeleccionats,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Long> findIdsAmbFiltre(
			Long entornId,
			Long expedientTipusId,
			String titol,
			String numero,
			String unitatOrganitzativaCodi,
			Date dataInici1,
			Date dataInici2,
			Date dataFi1,
			Date dataFi2,
			EstatTipusDto estatTipus,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			String registreNumero,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesAlertes,
			boolean nomesErrors,
			boolean nomesErrorsArxiu,
			MostrarAnulatsDto mostrarAnulats) {
		return delegateService.findIdsAmbFiltre(
				entornId,
				expedientTipusId,
				titol,
				numero,
				unitatOrganitzativaCodi,
				dataInici1,
				dataInici2,
				dataFi1,
				dataFi2,
				estatTipus,
				estatId,
				geoPosX,
				geoPosY,
				geoReferencia,
				registreNumero,
				nomesTasquesPersonals,
				nomesTasquesGrup,
				nomesAlertes,
				nomesErrors,
				nomesErrorsArxiu,
				mostrarAnulats);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientDto> findPerSuggest(Long expedientTipusId, String text) {
		return delegateService.findPerSuggest(expedientTipusId, text);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto getImatgeDefinicioProces(
			Long id,
			String processInstanceId) throws IOException {
		return delegateService.getImatgeDefinicioProces(
				id,
				processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<PersonaDto> findParticipants(Long id) {
		return delegateService.findParticipants(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTascaDto> findTasquesPendents(
			Long expedientId,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup) {
		return delegateService.findTasquesPendents(
				expedientId,
				nomesTasquesPersonals,
				nomesTasquesGrup);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void aturar(
			Long id,
			String motiu) {
		delegateService.aturar(id, motiu);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void reprendre(Long id) {
		delegateService.reprendre(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void anular(
			Long id,
			String motiu) {
		delegateService.anular(id, motiu);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void desanular(Long id) {
		delegateService.desanular(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void relacioCreate(
			Long origenId,
			Long destiId) {
		delegateService.relacioCreate(origenId, destiId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void relacioDelete(
			Long origenId,
			Long destiId) {
		delegateService.relacioDelete(origenId, destiId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientDto> relacioFindAmbExpedient(Long id) {
		return delegateService.relacioFindAmbExpedient(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void procesScriptExec(
			Long expedientId,
			String processInstanceId,
			String script) {
		delegateService.procesScriptExec(
				expedientId,
				processInstanceId,
				script);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void procesDefinicioProcesActualitzar(
			String processInstanceId,
			int versio) {
		delegateService.procesDefinicioProcesActualitzar(
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
		delegateService.procesDefinicioProcesCanviVersio(
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
		return delegateService.accioFindAmbId(
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
		delegateService.accioExecutar(
				expedientId,
				processInstanceId,
				accioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<AccioDto> accioFindVisiblesAmbProcessInstanceId(
			Long expedientId,
			String processInstanceId) {
		return delegateService.accioFindVisiblesAmbProcessInstanceId(
				expedientId,
				processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<AlertaDto> findAlertes(Long id) {
		return delegateService.findAlertes(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Object[] findErrorsExpedient(Long id) {
		return delegateService.findErrorsExpedient(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void netejarErrorsExp(Long id) throws NoTrobatException {
		delegateService.netejarErrorsExp(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientDto> findSuggestAmbEntornLikeIdentificador(Long entornid, String text) {
		return delegateService.findSuggestAmbEntornLikeIdentificador(entornid, text);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<InstanciaProcesDto> getArbreInstanciesProces(String processInstanceId) {
		return delegateService.getArbreInstanciesProces(processInstanceId);
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
			boolean nomesErrorsArxiu,
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
				nomesErrorsArxiu,
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
			boolean nomesErrorsArxiu,
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
				nomesErrorsArxiu,
				mostrarAnulats,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public InstanciaProcesDto getInstanciaProcesById(String processInstanceId) {
		return delegateService.getInstanciaProcesById(processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDadaDto> findConsultaFiltre(Long consultaId) {
		return delegateService.findConsultaFiltre(consultaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDadaDto> findConsultaInforme(Long consultaId) {
		return delegateService.findConsultaInforme(consultaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String getNumeroExpedientActual(Long entornId, Long expedientTipusId, Integer any) {
		return delegateService.getNumeroExpedientActual(entornId, expedientTipusId, any);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto getStartTask(Long entornId, Long expedientTipusId, Long definicioProcesId, Map<String, Object> valors) {
		return delegateService.getStartTask(entornId, expedientTipusId, definicioProcesId, valors);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean existsExpedientAmbEntornTipusITitol(Long entornId, Long expedientTipusId, String titol) {
		return delegateService.existsExpedientAmbEntornTipusITitol(entornId, expedientTipusId, titol);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDadaDto> findConsultaInformeParams(Long consultaId) {
		return delegateService.findConsultaInformeParams(consultaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientDto> findAmbIds(Set<Long> ids) {
		return delegateService.findAmbIds(ids);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampDto> getCampsInstanciaProcesById(
			Long expedientTipusId,
			String processInstanceId) {
		return delegateService.getCampsInstanciaProcesById(expedientTipusId, processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<RespostaValidacioSignaturaDto> verificarSignatura(Long documentStoreId) {
		return delegateService.verificarSignatura(documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void deleteSignatura(Long expedientId, Long documentStoreId) {
		delegateService.deleteSignatura(expedientId, documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean isDiferentsTipusExpedients(Set<Long> ids) {
		return delegateService.isDiferentsTipusExpedients(ids);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void desfinalitzar(Long id) {
		delegateService.desfinalitzar(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean potDesfinalitzar(
			Long id) throws NoTrobatException, PermisDenegatException {
		return delegateService.potDesfinalitzar(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientConsultaDissenyDto> findConsultaDissenyPaginat(Long consultaId, Map<String, Object> valors, PaginacioParamsDto paginacioParams, boolean nomesMeves, boolean nomesAlertes, boolean mostrarAnulats, boolean nomesTasquesPersonals, boolean nomesTasquesGrup, Set<Long> ids) {
		return delegateService.findConsultaDissenyPaginat(consultaId, valors, paginacioParams, nomesMeves, nomesAlertes, mostrarAnulats, nomesTasquesPersonals, nomesTasquesGrup, ids);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Long> findIdsPerConsultaInforme(Long consultaId, Map<String, Object> valors, boolean nomesMeves, boolean nomesAlertes, boolean mostrarAnulats, boolean nomesTasquesPersonals, boolean nomesTasquesGrup) {
		return delegateService.findIdsPerConsultaInforme(consultaId, valors, nomesMeves, nomesAlertes, mostrarAnulats, nomesTasquesPersonals, nomesTasquesGrup);
	}

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public List<ExpedientConsultaDissenyDto> findExpedientsExportacio(List<Long> ids, EntornDto entornActual) {
        return delegateService.findExpedientsExportacio(ids, entornActual);
    }

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public String getExpedientProcessInstanceId(Long expedientId) {
        return delegateService.getExpedientProcessInstanceId(expedientId);
    }

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ExpedientConsultaDissenyDto> findConsultaInformePaginat(Long consultaId, Map<String, Object> valorsPerService, boolean nomesMeves, boolean nomesAlertes, boolean mostrarAnulats, boolean nomesTasquesPersonals, boolean nomesTasquesGrup, PaginacioParamsDto paginacioParams) {
		return delegateService.findConsultaInformePaginat(consultaId, valorsPerService, nomesMeves, nomesAlertes, mostrarAnulats, nomesTasquesPersonals, nomesTasquesGrup, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean existsExpedientAmbEntornTipusINumero(Long entornId, Long expedientTipusId, String numero) {
		return delegateService.existsExpedientAmbEntornTipusINumero(entornId, expedientTipusId, numero);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<String> findProcesInstanceIdsAmbEntornTipusAndProcessDefinitionName(Long entornId, Long expedientTipusId, String jbpmKey) {
		return delegateService.findProcesInstanceIdsAmbEntornTipusAndProcessDefinitionName(entornId, expedientTipusId, jbpmKey);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public long countAmbDefinicioProcesId(Long definicioProcesId) {
		return delegateService.countAmbDefinicioProcesId(definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DadesNotificacioDto> findNotificacionsNotibPerExpedientId(Long expedientId) throws NoTrobatException {
		return delegateService.findNotificacionsNotibPerExpedientId(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void finalitzar(Long id) throws NoTrobatException, PermisDenegatException {
		delegateService.finalitzar(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void finalitzar(Long id, boolean firmaDocumentsServidor) throws NoTrobatException, PermisDenegatException {
		delegateService.finalitzar(id, firmaDocumentsServidor);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void sincronitzarArxiu(Long id, boolean esborrarExpSiError) throws NoTrobatException, PermisDenegatException {
		delegateService.sincronitzarArxiu(id, esborrarExpSiError);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void trySincronitzarArxiu(Long id) throws NoTrobatException {
		delegateService.trySincronitzarArxiu(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void executarCampAccio(Long expedientId, String processInstanceId, String accioCamp) {
		delegateService.executarCampAccio(expedientId, processInstanceId, accioCamp);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDetallDto getArxiuDetall(Long expedientId) {
		return delegateService.getArxiuDetall(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientDto> findAmbIniciadorCodi(String responsableCodi) {
		return delegateService.findAmbIniciadorCodi(responsableCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public byte[] getZipDocumentacio(Long expedientId) {
		return delegateService.getZipDocumentacio(expedientId);
	}

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public byte[] getZipDocumentacio(Long expedientId, Set<Long> seleccio) {
        return delegateService.getZipDocumentacio(expedientId, seleccio);
    }

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void arreglarMetadadesNti(Long expedientId) {
		delegateService.arreglarMetadadesNti(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public byte[] getZipPerNotificar(Long expedientId, List<ExpedientDocumentDto> documentsPerAfegir) {
		return delegateService.getZipPerNotificar(expedientId, documentsPerAfegir);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EstatDto estatCanviar(Long expedientId, Long estatId, boolean retrocedir) {
		return delegateService.estatCanviar(expedientId, estatId, retrocedir);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void syncArxiu(Long id, boolean esborrarExpSiError) {
		delegateService.syncArxiu(id, esborrarExpSiError);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void syncDocumentsArxiu(Long id, boolean esborrarExpSiError) {
		delegateService.syncDocumentsArxiu(id, esborrarExpSiError);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void firmarDocumentServidorPerArxiuFiExpedient(Long documentStoreId) {
		delegateService.firmarDocumentServidorPerArxiuFiExpedient(documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Long countByTipus(Long expedientTipusId) {
		return delegateService.countByTipus(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Long> findIdsPerTipus(Long expedientTipusId) {
		return delegateService.findIdsPerTipus(expedientTipusId);
	}

}
