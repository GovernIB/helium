/**
 * 
 */
package es.caib.helium.logic.service;

import es.caib.helium.client.engine.model.WTaskInstance;
import es.caib.helium.client.expedient.tasca.TascaClientService;
import es.caib.helium.client.expedient.tasca.model.ConsultaTascaDades;
import es.caib.helium.client.model.PagedList;
import es.caib.helium.logic.helper.ConversioTipusServiceHelper;
import es.caib.helium.logic.helper.DocumentHelper;
import es.caib.helium.logic.helper.EntornHelper;
import es.caib.helium.logic.helper.ExpedientHelper;
import es.caib.helium.logic.helper.ExpedientRegistreHelper;
import es.caib.helium.logic.helper.ExpedientTipusHelper;
import es.caib.helium.logic.helper.FormulariExternHelper;
import es.caib.helium.logic.helper.HerenciaHelper;
import es.caib.helium.logic.helper.IndexHelper;
import es.caib.helium.logic.helper.MsHelper;
import es.caib.helium.logic.helper.PaginacioHelper;
import es.caib.helium.logic.helper.PaginacioHelper.Converter;
import es.caib.helium.logic.helper.PermisosHelper;
import es.caib.helium.logic.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.helium.logic.helper.TascaHelper;
import es.caib.helium.logic.helper.TascaSegonPlaHelper;
import es.caib.helium.logic.helper.TascaSegonPlaHelper.InfoSegonPla;
import es.caib.helium.logic.helper.VariableHelper;
import es.caib.helium.logic.intf.WorkflowEngineApi;
import es.caib.helium.logic.intf.WorkflowRetroaccioApi;
import es.caib.helium.logic.intf.dto.ArxiuDto;
import es.caib.helium.logic.intf.dto.DelegationInfo;
import es.caib.helium.logic.intf.dto.DocumentDto;
import es.caib.helium.logic.intf.dto.ExpedientTascaDto;
import es.caib.helium.logic.intf.dto.FormulariExternDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.ParellaCodiValorDto;
import es.caib.helium.logic.intf.dto.SeleccioOpcioDto;
import es.caib.helium.logic.intf.dto.TascaDadaDto;
import es.caib.helium.logic.intf.dto.TascaDocumentDto;
import es.caib.helium.logic.intf.dto.TascaDto;
import es.caib.helium.logic.intf.dto.TascaLlistatDto;
import es.caib.helium.logic.intf.exception.ExecucioHandlerException;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.SistemaExternException;
import es.caib.helium.logic.intf.exception.TramitacioException;
import es.caib.helium.logic.intf.exception.TramitacioHandlerException;
import es.caib.helium.logic.intf.exception.ValidacioException;
import es.caib.helium.logic.intf.service.TascaService;
import es.caib.helium.logic.security.ExtendedPermission;
import es.caib.helium.persist.entity.Alerta;
import es.caib.helium.persist.entity.Camp;
import es.caib.helium.persist.entity.Camp.TipusCamp;
import es.caib.helium.persist.entity.CampRegistre;
import es.caib.helium.persist.entity.CampTasca;
import es.caib.helium.persist.entity.DefinicioProces;
import es.caib.helium.persist.entity.Document;
import es.caib.helium.persist.entity.DocumentStore;
import es.caib.helium.persist.entity.Entorn;
import es.caib.helium.persist.entity.EnumeracioValors;
import es.caib.helium.persist.entity.Expedient;
import es.caib.helium.persist.entity.ExpedientTipus;
import es.caib.helium.persist.entity.FormulariExtern;
import es.caib.helium.persist.entity.Registre;
import es.caib.helium.persist.entity.Tasca;
import es.caib.helium.persist.entity.TerminiIniciat;
import es.caib.helium.persist.repository.AlertaRepository;
import es.caib.helium.persist.repository.CampRepository;
import es.caib.helium.persist.repository.CampTascaRepository;
import es.caib.helium.persist.repository.DefinicioProcesRepository;
import es.caib.helium.persist.repository.DocumentRepository;
import es.caib.helium.persist.repository.EnumeracioValorsRepository;
import es.caib.helium.persist.repository.ExpedientHeliumRepository;
import es.caib.helium.persist.repository.ExpedientRepository;
import es.caib.helium.persist.repository.ExpedientTipusRepository;
import es.caib.helium.persist.repository.FormulariExternRepository;
import es.caib.helium.persist.repository.RegistreRepository;
import es.caib.helium.persist.repository.TascaRepository;
import es.caib.helium.persist.repository.TerminiIniciatRepository;
import es.caib.helium.persist.util.ThreadLocalInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Servei per gestionar terminis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class TascaServiceImpl implements TascaService {

	@Resource
	private ExpedientHeliumRepository expedientHeliumRepository;
	@Resource
	private EnumeracioValorsRepository enumeracioValorsRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private TascaRepository tascaRepository;
	@Resource
	private RegistreRepository registreRepository;
	@Resource
	private CampTascaRepository campTascaRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private TerminiIniciatRepository terminiIniciatRepository;
	@Resource
	private AlertaRepository alertaRepository;
	@Resource
	private DocumentRepository documentRepository;

	@Resource
	private ExpedientRegistreHelper expedientRegistreHelper;
//	@Resource
//	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private TascaHelper tascaHelper;
	@Resource
	private EntornHelper entornHelper;
	@Resource
	private WorkflowEngineApi workflowEngineApi;
	@Resource
	private WorkflowRetroaccioApi workflowRetroaccioApi;
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private VariableHelper variableHelper;
	@Resource
	private DocumentHelper documentHelper;
	@Resource
	private IndexHelper indexHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private FormulariExternHelper formulariExternHelper;
	@Resource
	private HerenciaHelper herenciaHelper;
	@Resource
	private ConversioTipusServiceHelper conversioTipusServiceHelper;
	@Autowired
	private TascaSegonPlaHelper tascaSegonPlaHelper;
//	@Autowired
//	private MetricRegistry metricRegistry;

	@Resource
	private FormulariExternRepository formulariExternRepository;

	@Resource
	private TascaClientService tascaClientService;

	@Autowired
	private MsHelper msHelper;


	@Override
	@Transactional(readOnly = true)
	public ExpedientTascaDto findAmbIdPerExpedient(
			String id,
			Long expedientId) {
		logger.debug("Consultant tasca per expedient donat el seu taskInstanceId (" +
				"taskInstanceId=" + id + ")");
		Expedient expedient = expedientRepository.findById(expedientId)
				.orElseThrow(() -> new NoTrobatException(Expedient.class, expedientId));
		
		WTaskInstance task = tascaHelper.comprovarTascaPertanyExpedient(
				id,
				expedient);
		return tascaHelper.toExpedientTascaDto(
				task,
				expedient,
				true,
				false);
	}

	@Override
	@Transactional(readOnly = true)
	public ExpedientTascaDto findAmbIdPerTramitacio(
			String id) {
		logger.debug("Consultant tasca per tramitar donat el seu taskInstanceId (" +
				"taskInstanceId=" + id + ")");
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				id,
				true,
				true);
		return tascaHelper.toExpedientTascaDto(
				task,
				null,
				true,
				false);
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> findIdsPerFiltre(
			Long entornId,
			Long expedientTipusId,
			String titol,
			String tasca,
			String responsable,
			String expedient,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Date dataLimitInici,
			Date dataLimitFi,
			Integer prioritat,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesTasquesMeves) {
		logger.debug("Consulta de tasques segons filtre (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"responsable=" + responsable + ", " +
				"titol=" + titol + ", " +
				"tasca=" + tasca + ", " +
				"dataCreacioInici=" + dataCreacioInici + ", " +
				"dataCreacioFi=" + dataCreacioFi + ", " +
				"dataLimitInici=" + dataLimitInici + ", " +
				"dataLimitFi=" + dataLimitFi + ", " +
				"prioritat=" + prioritat + ", " +
				"nomesTasquesPersonals=" + nomesTasquesPersonals + ", " +
				"nomesTasquesGrup=" + nomesTasquesGrup + ", " +
				"nomesTasquesMeves=" + nomesTasquesMeves + ")");
		// Comprova l'accés a l'entorn
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		// TODO: Mètriques
//		final Timer timerTotal = metricRegistry.timer(
//				MetricRegistry.name(
//						TascaService.class,
//						"llistatIds"));
//		final Timer.Context contextTotal = timerTotal.time();
//		Counter countTotal = metricRegistry.counter(
//				MetricRegistry.name(
//						TascaService.class,
//						"llistatIds.count"));
//		countTotal.inc();
//		final Timer timerEntorn = metricRegistry.timer(
//				MetricRegistry.name(
//						TascaService.class,
//						"llistatIds",
//						entorn.getCodi()));
//		final Timer.Context contextEntorn = timerEntorn.time();
//		Counter countEntorn = metricRegistry.counter(
//				MetricRegistry.name(
//						TascaService.class,
//						"llistatIds.count",
//						entorn.getCodi()));
//		countEntorn.inc();
		try {
			// Comprova l'accés al tipus d'expedient
			ExpedientTipus expedientTipus = null;
			if (expedientTipusId != null) {
				expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisLectura(
						expedientTipusId);
			}
			// Si no hi ha tipexp seleccionat o no es te permis SUPERVISION
			// a damunt el tipexp es filtra per l'usuari actual.
			if (nomesTasquesMeves || expedientTipusId == null || !expedientTipusHelper.comprovarPermisSupervisio(expedientTipusId)) {
				responsable = SecurityContextHolder.getContext().getAuthentication().getName();
			}
			// Obté la llista de tipus d'expedient permesos
			List<ExpedientTipus> tipusPermesos = expedientTipusRepository.findByEntorn(entorn);
			permisosHelper.filterGrantedAny(
					tipusPermesos,
					new ObjectIdentifierExtractor<ExpedientTipus>() {
						public Long getObjectIdentifier(ExpedientTipus expedientTipus) {
							return expedientTipus.getId();
						}
					},
					ExpedientTipus.class,
					new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION},
					SecurityContextHolder.getContext().getAuthentication());
//			mesuresTemporalsHelper.mesuraIniciar("CONSULTA TASQUES LLISTAT", "consulta");
			// Calcula la data d'creacio fi pel filtre
			if (dataCreacioFi != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataCreacioFi);
				cal.add(Calendar.DATE, 1);
				dataCreacioFi.setTime(cal.getTime().getTime());
			}
			// Calcula la data limit fi pel filtre
			if (dataLimitFi != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataLimitFi);
				cal.add(Calendar.DATE, 1);
				dataLimitFi.setTime(cal.getTime().getTime());
			}
			
			List<Long> idsExpedients = expedientHeliumRepository.findListExpedients(
					entorn,
					tipusPermesos,
					(expedientTipus == null),
					expedientTipus,
					(expedient == null),
					expedient,
					null);
			
			ConsultaTascaDades consultaTascaDades = ConsultaTascaDades.builder()
					.entornId(entornId)
					.expedientTipusId(expedientTipusId)
					.usuariAssignat(responsable)
					.nom(tasca)
					.titol(titol)
					.expedientId(expedientTipusId)
					.expedientTitol(expedient)
					.expedientNumero(expedient)
					.dataCreacioInici(dataCreacioInici)
					.dataCreacioFi(dataCreacioFi)
					.dataLimitInici(dataLimitInici)
					.dataLimitFi(dataLimitFi)
					.mostrarAssignadesUsuari(nomesTasquesPersonals)
					.mostrarAssignadesGrup(nomesTasquesGrup)
					.nomesPendents(true)
					.build();
			
			
			PagedList<String> page = tascaClientService.findTasquesIdsAmbFiltrePaginatV1(consultaTascaDades);
			
//			LlistatIds ids = workflowEngineApi.tascaIdFindByFiltrePaginat(
//					responsable,
//					titol,
//					tasca,
//TODO DANIEL:		idsExpedients, resoldre què s'informa aquí
//					dataCreacioInici,
//					dataCreacioFi,
//					prioritat,
//					dataLimitInici,
//					dataLimitFi,
//					new PaginacioParamsDto(),
//					nomesTasquesPersonals, 
//					nomesTasquesGrup,
//					true);
			return page.getContent();
		} finally {
//			contextTotal.stop();
//			contextEntorn.stop();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<TascaLlistatDto> findPerFiltrePaginat(
			Long entornId,
			String tramitacioMassivaTascaId,
			Long expedientTipusId,
			String titol,
			String tasca,
			String responsable,
			String expedient,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Date dataLimitInici,
			Date dataLimitFi,
			Integer prioritat,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesTasquesMeves,
			final PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta de tasques segons filtre (" +
				"entornId=" + entornId + ", " +
				"tramitacioMassivaTascaId=" + tramitacioMassivaTascaId + ", " + 
				"expedientTipusId=" + expedientTipusId + ", " +
				"responsable=" + responsable + ", " +
				"titol=" + titol + ", " +
				"tasca=" + tasca + ", " +
				"dataCreacioInici=" + dataCreacioInici + ", " +
				"dataCreacioFi=" + dataCreacioFi + ", " +
				"dataLimitInici=" + dataLimitInici + ", " +
				"dataLimitFi=" + dataLimitFi + ", " +
				"prioritat=" + prioritat + ", " +
				"nomesTasquesPersonals=" + nomesTasquesPersonals + ", " +
				"nomesTasquesGrup=" + nomesTasquesGrup + ", " +
				"nomesTasquesMeves=" + nomesTasquesMeves + ", " +
				"paginacioParams=" + paginacioParams + ")");
		// Comprova l'accés a l'entorn
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		// TODO: Mètriques
//		final Timer timerTotal = metricRegistry.timer(
//				MetricRegistry.name(
//						TascaService.class,
//						"llistat"));
//		final Timer.Context contextTimerTotal = timerTotal.time();
//		Counter countTotal = metricRegistry.counter(
//				MetricRegistry.name(
//						TascaService.class,
//						"llistat.count"));
//		countTotal.inc();
//		final Timer timerEntorn = metricRegistry.timer(
//				MetricRegistry.name(
//						TascaService.class,
//						"llistat",
//						entorn.getCodi()));
//		final Timer.Context contextTimerEntorn = timerEntorn.time();
//		Counter countEntorn = metricRegistry.counter(
//				MetricRegistry.name(
//						TascaService.class,
//						"llistat.count",
//						entorn.getCodi()));
//		countEntorn.inc();
		try {
//			final Timer timerConsultaTotal = metricRegistry.timer(
//					MetricRegistry.name(
//							TascaService.class,
//							"llistat.consulta"));
//			final Timer.Context contextTimerConsultaTotal = timerConsultaTotal.time();
//			final Timer timerConsultaEntorn = metricRegistry.timer(
//					MetricRegistry.name(
//							TascaService.class,
//							"llistat.consulta",
//							entorn.getCodi()));
//			final Timer.Context contextTimerConsultaEntorn = timerConsultaEntorn.time();
			PagedList<es.caib.helium.client.expedient.tasca.model.TascaDto> page;
			try {
				// Comprova l'accés al tipus d'expedient
				if (expedientTipusId != null) {
					expedientTipusHelper.getExpedientTipusComprovantPermisLectura(
							expedientTipusId);
				}
				// Si no hi ha tipexp seleccionat o no es te permis SUPERVISION
				// a damunt el tipexp es filtra per l'usuari actual.
				if (nomesTasquesMeves || expedientTipusId == null || !expedientTipusHelper.comprovarPermisSupervisio(expedientTipusId)) {
					responsable = SecurityContextHolder.getContext().getAuthentication().getName();
				}
				if (tramitacioMassivaTascaId != null) {
					WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
							tramitacioMassivaTascaId,
							true,
							true);
					tasca = task.getTaskName();
				}
				// Calcula la data d'creacio fi pel filtre
				if (dataCreacioFi != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataCreacioFi);
					cal.add(Calendar.DATE, 1);
					dataCreacioFi.setTime(cal.getTime().getTime());
				}
				// Calcula la data limit fi pel filtre
				if (dataLimitFi != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataLimitFi);
					cal.add(Calendar.DATE, 1);
					dataLimitFi.setTime(cal.getTime().getTime());
				}
				boolean mostrarAssignadesUsuari = (nomesTasquesPersonals && !nomesTasquesGrup) || (!nomesTasquesPersonals && !nomesTasquesGrup);
				boolean mostrarAssignadesGrup = (nomesTasquesGrup && !nomesTasquesPersonals) || (!nomesTasquesPersonals && !nomesTasquesGrup);


				ConsultaTascaDades consultaTascaDades = ConsultaTascaDades.builder()
						.entornId(entornId)
						.expedientTipusId(expedientTipusId)
						.usuariAssignat(responsable)
						.nom(tasca)
						.titol(titol)
						.expedientTitol(expedient)
						.expedientNumero(expedient)
						.dataCreacioInici(dataCreacioInici)
						.dataCreacioFi(dataCreacioFi)
						.dataLimitInici(dataLimitInici)
						.dataLimitFi(dataLimitFi)
						.mostrarAssignadesUsuari(mostrarAssignadesUsuari)
						.mostrarAssignadesGrup(mostrarAssignadesGrup)
						.nomesPendents(true)
						.page(paginacioParams.getPaginaNum())
						.size(paginacioParams.getPaginaTamany())
						.sort(msHelper.getSortList(paginacioParams))
						.build();
				
				page = tascaClientService.findTasquesAmbFiltrePaginatV1(consultaTascaDades);
				
			} finally {
//				contextTimerConsultaTotal.stop();
//				contextTimerConsultaEntorn.stop();
			}
//			final Timer timerConversionTotal = metricRegistry.timer(
//					MetricRegistry.name(
//							TascaService.class,
//							"llistat.conversio"));
//			final Timer.Context contextTimerConversioTotal = timerConversionTotal.time();
//			final Timer timerConversioEntorn = metricRegistry.timer(
//					MetricRegistry.name(
//							TascaService.class,
//							"llistat.conversio",
//							entorn.getCodi()));
//			final Timer.Context contextTimerConversioEntorn = timerConversioEntorn.time();
			try {
//				return paginacioHelper.toPaginaDto(
//						page, 
//						ExpedientTascaDto.class);
				return paginacioHelper.toPaginaDto(
						page.getContent(),
						page.getTotalElements(),
						paginacioParams,
						new Converter<es.caib.helium.client.expedient.tasca.model.TascaDto, TascaLlistatDto>() {
							public TascaLlistatDto convert(es.caib.helium.client.expedient.tasca.model.TascaDto tascaMs) {
								return tascaHelper.toTascaLlistatDto(
										tascaMs,
										null,
										false,
										true);
							}
						});
			} finally {
//				contextTimerConversioTotal.stop();
//				contextTimerConversioEntorn.stop();
			}
		} finally {
//			contextTimerTotal.stop();
//			contextTimerEntorn.stop();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<TascaDadaDto> findDades(
			String id) {
		logger.debug("Consultant dades de la tasca (" +
				"taskInstanceId=" + id + ")");
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				id,
				true,
				true);
		return variableHelper.findDadesPerInstanciaTasca(task);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExpedientTascaDto> findAmbIds(Set<String> ids) {
		logger.debug("Consultant expedients de las tascas (" +
				"ids=" + ids + ")");

		List<ExpedientTascaDto> expedientTasques = new ArrayList<ExpedientTascaDto>();
		for (String id : ids) {
			WTaskInstance task = workflowEngineApi.getTaskById(id);
			expedientTasques.add(tascaHelper.toExpedientTascaDto(
					task,
					null,
					false,
					false));
		}
		return expedientTasques;
	}

	@Override
	@Transactional(readOnly = true)
	public List<TascaDadaDto> findDadesPerTascaDto(Long expedientTipusId, ExpedientTascaDto tasca) {
		return variableHelper.findDadesPerInstanciaTascaDto(expedientTipusId, tasca);
	}

	@Override
	@Transactional(readOnly = true)
	public TascaDocumentDto findDocument(
			String tascaId,
			Long docId,
			Long expedientTipusId) {
		logger.debug("Consultant document de la tasca (" +
				"docId=" + docId + ")");
		return documentHelper.findDocumentPerId(tascaId, docId, expedientTipusId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TascaDocumentDto> findDocuments(
			String id) {
		logger.debug("Consultant documents de la tasca (" +
				"taskInstanceId=" + id + ")");
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				id,
				true,
				true);
		return documentHelper.findDocumentsPerInstanciaTasca(task);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean hasFormulari(
			String id) {
		logger.debug("Consultant si la tasca disposa de formulari (" +
				"taskInstanceId=" + id + ")");
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				id,
				true,
				true);
		Tasca tasca = tascaHelper.findTascaByWTaskInstance(task);
		Long expedientTipusId = expedientTipusHelper.findIdByProcessInstanceId(task.getProcessInstanceId());

		return campTascaRepository.countAmbTasca(
				tasca.getId(),
				expedientTipusId) > 0;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean hasDocuments(
			String id) {
		logger.debug("Consultant si la tasca disposa de documents (" +
				"taskInstanceId=" + id + ")");
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				id,
				true,
				true);
		return documentHelper.hasDocumentsPerInstanciaTasca(task);
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean hasDocumentsNotReadOnly(
			String id) {
		logger.debug("Consultant si la tasca disposa de documents només de lectura (" +
				"taskInstanceId=" + id + ")");
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				id,
				true,
				true);
		return documentHelper.hasDocumentsNotReadOnlyPerInstanciaTasca(task);
	}

	@Transactional(readOnly = true)
	public ArxiuDto getArxiuPerDocumentCodi(
			String tascaId,
			String documentCodi) {
		logger.debug("Obtenint contingut de l'arxiu per l'tasca (" +
				"tascaId=" + tascaId + ", " +
				"documentCodi=" + documentCodi + ")");
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				tascaId,
				true,
				true);
		DocumentStore documentStore = documentHelper.getDocumentStore(task, documentCodi);
		if (documentStore != null) {
			return documentHelper.getArxiuPerDocumentStoreId(
					documentStore.getId(),
					false,
					false);
		} else {
			Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(
					task.getProcessInstanceId());
			ExpedientTipus expedientTipus = expedient.getTipus();
			Document document;
			if (expedientTipus.isAmbInfoPropia()) {
				document = documentRepository.findByExpedientTipusAndCodi(
						expedientTipus.getId(),
						documentCodi,
						expedientTipus.getExpedientTipusPare() != null);
			} else
				document = documentRepository.findByDefinicioProcesAndCodi(
						expedientHelper.findDefinicioProcesByProcessInstanceId(
								task.getProcessInstanceId()), 
						documentCodi);
			Date documentData = new Date();
			return documentHelper.generarDocumentAmbPlantillaIConvertir(
					expedient,
					document,
					tascaId,
					task.getProcessInstanceId(),
					documentData);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public DocumentDto getDocumentPerDocumentCodi(
			String tascaId,
			String documentCodi) {
		logger.debug("Obtenint contingut de l'arxiu per la tasca (" +
				"tascaId=" + tascaId + ", " +
				"documentCodi=" + documentCodi + ")");
		DocumentDto document = null;
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				tascaId,
				true,
				true);
		DocumentStore documentStore = documentHelper.getDocumentStore(task, documentCodi);
		if (documentStore != null) {
			document = documentHelper.toDocumentDto(
					documentStore.getId(),
					false,
					false,
					true,
					true,
					false, // Per notificar
					(documentStore.getArxiuUuid() == null));
		}
		return document;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean hasSignatures(
			String id) {
		logger.debug("Consultant si la tasca disposa de signatures (" +
				"taskInstanceId=" + id + ")");
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				id,
				true,
				true);
		return documentHelper.hasDocumentsPerInstanciaTascaSignar(task);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TascaDocumentDto> findDocumentsSignar(
			String id) {
		logger.debug("Consultant documents per signar de la tasca (" +
				"taskInstanceId=" + id + ")");
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				id,
				true,
				true);
		return documentHelper.findDocumentsPerInstanciaTascaSignar(task);
	}

	@Override
	@Transactional(readOnly = true)
	public List<SeleccioOpcioDto> findValorsPerCampDesplegable(
			String tascaId,
			String processInstanceId,
			Long campId,
			String codiFiltre,
			String textFiltre,
			Long registreCampId,
			Integer registreIndex,
			Map<String, Object> valorsFormulari) {
		logger.debug("Consultant llista de valors per camp selecció (" +
				"tascaId=" + tascaId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"campId=" + campId + ", " +
				"codiFiltre=" + codiFiltre + ", " +
				"textFiltre=" + textFiltre + ", " +
				"registreCampId=" + registreCampId + ", " +
				"registreIndex=" + registreIndex + ", " +
				"valorsFormulari=...)");
		List<SeleccioOpcioDto> resposta = new ArrayList<SeleccioOpcioDto>();
		Camp camp = campRepository.getById(campId);
		Camp registreCamp = null;
		if (registreCampId != null) {
			registreCamp = campRepository.getById(registreCampId);
		}
		String pidCalculat = processInstanceId;
		if (processInstanceId == null && tascaId != null) {
			WTaskInstance task = null;
			task = tascaHelper.getTascaComprovacionsTramitacio(
					tascaId,
					true,
					true);
			// Comprova si el camp pertany a la tasca
			Tasca tasca = tascaHelper.findTascaByWTaskInstance(task);
			boolean trobat = false;
			for (CampTasca campTasca: tasca.getCamps()) {
				if (campTasca.getCamp().equals(camp)) {
					trobat = true;
				} else if (campTasca.getCamp().getTipus().equals(TipusCamp.REGISTRE)) {
					for (CampRegistre campReg: campTasca.getCamp().getRegistreMembres()) {
						if (campReg.getMembre().equals(camp)) {
							trobat = true;
							break;
						}
					}
				}
				if (trobat)
					break;
			}
			if (!trobat) {
				logger.error("El camp consultat no pertany a la tasca " + tascaId, 
						new NoTrobatException(Camp.class,camp.getId()));
				// Aquest cas no s'hauria de donar. 
				// En cas que es doni, ara per ara, l'unic que feim és no retornar valors.
				return resposta;
			}
			pidCalculat = task.getProcessInstanceId();
		}
		// Consulta els valors possibles
		if (camp.getDomini() != null || camp.getDominiIntern()) {
			List<ParellaCodiValorDto> parellaCodiValorDto = variableHelper.getPossiblesValorsCamp(
						camp,
						registreCamp,
						registreIndex,
						null,
						valorsFormulari,
						tascaId,
						pidCalculat,
						null,
						null);
			for (ParellaCodiValorDto parella: parellaCodiValorDto) {
				boolean afegir = 
						(codiFiltre == null && textFiltre == null) ||
						(codiFiltre != null && parella.getCodi().equals(codiFiltre)) ||
						(textFiltre != null && parella.getValor().toString().toUpperCase().contains(textFiltre.toUpperCase()));
				if (afegir) {
					resposta.add(
							new SeleccioOpcioDto(
									parella.getCodi(),
									(String)parella.getValor()));
				}
			}
		} else if (camp.getEnumeracio() != null) {
			List<EnumeracioValors> valors = enumeracioValorsRepository.findByEnumeracioOrdenat(
					camp.getEnumeracio().getId());
			for (EnumeracioValors valor: valors) {
				boolean afegir = 
						(codiFiltre == null && textFiltre == null) ||
						(codiFiltre != null && valor.getCodi().equals(codiFiltre)) ||
						(textFiltre != null && valor.getNom().toString().toUpperCase().contains(textFiltre.toUpperCase()));
				if (afegir) {
					resposta.add(
							new SeleccioOpcioDto(
									valor.getCodi(),
									valor.getNom()));
				}
			}
		}
		return resposta;
	}

	@Override
	@Transactional
	public ExpedientTascaDto agafar(
			String taskInstanceId) {
		logger.debug("Agafant tasca (taskInstanceId=" + taskInstanceId + ")");
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				taskInstanceId,
				false,
				true);
		//	Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		
		// TODO contemplar el cas que no faci falta que l'usuari
		// estigui als pooledActors
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Set<String> pooledActors = task.getPooledActors();
		if (!pooledActors.contains(auth.getName())) {
			logger.debug("L'usuari no s'ha trobat com a pooledActor de la tasca (" +
					"taskInstanceId=" + taskInstanceId + ", " +
					"personaCodi=" + auth.getName() + ")");
			throw new NoTrobatException(WTaskInstance.class, taskInstanceId);
		}
		String previousActors = task.getStringActors();
		Long informacioRetroaccioId = workflowRetroaccioApi.afegirInformacioRetroaccioPerTasca(
				taskInstanceId,
				WorkflowRetroaccioApi.ExpedientRetroaccioTipus.TASCA_REASSIGNAR,
				previousActors);
		workflowEngineApi.takeTaskInstance(taskInstanceId, auth.getName());
		indexHelper.expedientIndexLuceneUpdate(task.getProcessInstanceId());
		task = workflowEngineApi.getTaskById(taskInstanceId);
		String currentActors = task.getStringActors();
		workflowRetroaccioApi.actualitzaParametresAccioInformacioRetroaccio(
				informacioRetroaccioId,
				previousActors + "::" + currentActors);
		ExpedientTascaDto tasca = tascaHelper.toExpedientTascaDto(
				task,
				null,
				false,
				false);
		expedientRegistreHelper.crearRegistreIniciarTasca(
				tasca.getExpedientId(),
				taskInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				"Agafar tasca \"" + tasca.getTitol() + "\"");
		return tasca;
	}

	@Override
	@Transactional
	public ExpedientTascaDto alliberar(
			String id) {
		logger.debug("Alliberant tasca (taskInstanceId=" + id + ")");
		// TODO contemplar el cas que no faci falta que l'usuari
		// de la tasca sigui l'usuari actual
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				id,
				false,
				true);
//		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		String previousActors = task.getStringActors();
		Long informacioRetroaccioId = workflowRetroaccioApi.afegirInformacioRetroaccioPerTasca(
				id,
				WorkflowRetroaccioApi.ExpedientRetroaccioTipus.TASCA_REASSIGNAR,
				previousActors);
		workflowEngineApi.releaseTaskInstance(id);
		indexHelper.expedientIndexLuceneUpdate(task.getProcessInstanceId());
		task = workflowEngineApi.getTaskById(id);
		String currentActors = task.getStringActors();
		workflowRetroaccioApi.actualitzaParametresAccioInformacioRetroaccio(
				informacioRetroaccioId,
				previousActors + "::" + currentActors);
		ExpedientTascaDto tasca = tascaHelper.toExpedientTascaDto(
				task,
				null,
				false,
				false);
		expedientRegistreHelper.crearRegistreIniciarTasca(
				tasca.getExpedientId(),
				id,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				"Amollar tasca \"" + tasca.getTitol() + "\"");
		return tasca;
	}

	@Override
	@Transactional
	public void esborrarDocument(
			String taskInstanceId,
			String documentCodi,
			String user) {		
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				taskInstanceId,
				true,
				true);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		
		if (taskInstanceId != null) {
			workflowRetroaccioApi.afegirInformacioRetroaccioPerTasca(
					taskInstanceId,
					WorkflowRetroaccioApi.ExpedientRetroaccioTipus.TASCA_DOCUMENT_ESBORRAR,
					documentCodi);
		} else {
			workflowRetroaccioApi.afegirInformacioRetroaccioPerProces(
					task.getProcessInstanceId(),
					WorkflowRetroaccioApi.ExpedientRetroaccioTipus.PROCES_DOCUMENT_ESBORRAR,
					documentCodi);
		}
		
		documentHelper.esborrarDocument(
				taskInstanceId,
				task.getProcessInstanceId(),
				documentCodi);
		if (user == null) {
			user = SecurityContextHolder.getContext().getAuthentication().getName();
		}
		if (taskInstanceId != null) {
			expedientRegistreHelper.crearRegistreEsborrarDocumentTasca(
					expedient.getId(),
					taskInstanceId,
					user,
					documentCodi);
		} else {
			expedientRegistreHelper.crearRegistreEsborrarDocumentInstanciaProces(
					expedient.getId(),
					task.getProcessInstanceId(),
					user,
					documentCodi);
		}
	}

	@Override
	@Transactional
	public boolean signarDocumentTascaAmbToken(
			String tascaId,
			String token,
			byte[] signatura) throws Exception {
		boolean signat = false;
		DocumentDto dto = documentHelper.signarDocumentTascaAmbToken(tascaId, token, signatura);
		if (dto != null) {
			expedientRegistreHelper.crearRegistreSignarDocument(
					dto.getProcessInstanceId(),
					SecurityContextHolder.getContext().getAuthentication().getName(),
					dto.getDocumentCodi());
			signat = true;
		}
		return signat;
	}

	@Override
	@Transactional
	public Long guardarDocumentTasca(
			Long entornId,
			String taskInstanceId,
			String documentCodi,
			Date documentData,
			String arxiuNom,
			byte[] arxiuContingut,
			String arxiuContentType, 
			boolean ambFirma,
			boolean firmaSeparada,
			byte[] firmaContingut,
			String user) {
		logger.debug("Crear document a dins la tasca (" +
				"entornId=" + entornId + ", " +
				"taskInstanceId=" + taskInstanceId + ", " +
				"documentCodi=" + documentCodi + ", " +
				"documentData=" + documentData + ", " +
				"arxiuNom=" + arxiuNom + ", " +
				"arxiuContingut=" + arxiuContingut + ", " +
				"arxiuContentType=" + arxiuContentType + ", " +
				"ambFirma=" + ambFirma + ", " +
				"firmaSeparada=" + firmaSeparada + ", " +
				"firmaContingut=" + firmaContingut + ", " +
				"user=" + user + ")");
		WTaskInstance task = workflowEngineApi.getTaskById(taskInstanceId);
		DocumentStore documentStore = documentHelper.getDocumentStore(task, documentCodi);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		boolean creat = (documentStore == null);
		if (creat) {
			workflowRetroaccioApi.afegirInformacioRetroaccioPerTasca(
					taskInstanceId,
					WorkflowRetroaccioApi.ExpedientRetroaccioTipus.TASCA_DOCUMENT_AFEGIR,
					documentCodi);
		} else {
			workflowRetroaccioApi.afegirInformacioRetroaccioPerTasca(
					taskInstanceId,
					WorkflowRetroaccioApi.ExpedientRetroaccioTipus.TASCA_DOCUMENT_MODIFICAR,
					documentCodi);
		}
		String arxiuNomAntic = (documentStore != null) ? documentStore.getArxiuNom() : null;
		Long documentStoreId = documentHelper.crearActualitzarDocument(
				taskInstanceId,
				task.getProcessInstanceId(),
				documentCodi,
				documentData,
				arxiuNom,
				arxiuContingut,
				arxiuContentType,
				ambFirma,
				firmaSeparada,
				firmaContingut,
				null,
				null,
				null,
				null);
		// Registra l'acció
		if (user == null) {
			user = SecurityContextHolder.getContext().getAuthentication().getName();
		}
		if (creat) {
			expedientRegistreHelper.crearRegistreCrearDocumentTasca(
					expedient.getId(),
					taskInstanceId,
					user,
					documentCodi,
					arxiuNom);
		} else {
			expedientRegistreHelper.crearRegistreModificarDocumentTasca(
					expedient.getId(),
					taskInstanceId,
					user,
					documentCodi,
					arxiuNomAntic,
					arxiuNom);
		}
		return documentStoreId;
	}

	@Override
	@Transactional
	public void guardar(
			String taskId,
			Map<String, Object> variables) {
		logger.debug("Guardant les dades del formulari de la tasca (" +
				"taskId=" + taskId + ", " +
				"variables= "+variables+")");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuari = auth.getName();
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				taskId,
				true,
				true);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		workflowRetroaccioApi.afegirInformacioRetroaccioPerTasca(
				taskId,
				expedient.getId(),
				WorkflowRetroaccioApi.ExpedientRetroaccioTipus.TASCA_FORM_GUARDAR,
				null,
				usuari);
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProcesJbpmId(
				task.getTaskName(),
				task.getProcessDefinitionId());
		Map<String, Object> variablesProcessades = new HashMap<String, Object>(variables);
		tascaHelper.processarCampsAmbDominiCacheActivat(task, tasca, variablesProcessades);
		workflowEngineApi.startTaskInstance(taskId);
		workflowEngineApi.setTaskInstanceVariables(taskId, variablesProcessades, false);
		
		if (task.getStartTime() == null) {
			Registre registre = new Registre(
					new Date(),
					expedient.getId(),
					usuari,
					Registre.Accio.MODIFICAR,
					Registre.Entitat.TASCA,
					taskId);
//			registre.setMissatge("Iniciar tasca \"" + tascaHelper.getTitolPerTasca(task, tasca) + "\"");
			registre.setMissatge("Iniciar tasca \"" + task.getTaskName() + "\"");
			registreRepository.save(registre);
		}
	}

	@Override
	@Transactional
	public void validar(
			String tascaId,
			Map<String, Object> variables) {
		logger.debug("Validant el formulari de la tasca (" +
				"tascaId=" + tascaId + ", " +
				"variables= "+variables+")");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuari = auth.getName();
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				tascaId,
				true,
				true);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		workflowRetroaccioApi.afegirInformacioRetroaccioPerTasca(
				tascaId,
				expedient.getId(),
				WorkflowRetroaccioApi.ExpedientRetroaccioTipus.TASCA_FORM_VALIDAR,
				null,
				usuari);
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProcesJbpmId(
				task.getTaskName(),
				task.getProcessDefinitionId());
		tascaHelper.processarCampsAmbDominiCacheActivat(task, tasca, variables);
		workflowEngineApi.startTaskInstance(tascaId);
		workflowEngineApi.setTaskInstanceVariables(tascaId, variables, false);
		tascaHelper.validarTasca(tascaId);
		
		Registre registre = new Registre(
				new Date(),
				expedient.getId(),
				usuari,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.TASCA,
				tascaId);
//		registre.setMissatge("Validar \"" + tascaHelper.getTitolPerTasca(task, tasca) + "\"");
		registre.setMissatge("Validar \"" + task.getTaskName() + "\"");
		registreRepository.save(registre);
	}

	@Override
	@Transactional
	public void restaurar(
			String tascaId) {
		logger.debug("Restaurant el formulari de la tasca (" +
				"tascaId=" + tascaId + ", " +
				"variables=...)");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuari = auth.getName();
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				tascaId,
				true,
				true);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		workflowRetroaccioApi.afegirInformacioRetroaccioPerTasca(
				tascaId,
				expedient.getId(),
				WorkflowRetroaccioApi.ExpedientRetroaccioTipus.TASCA_FORM_RESTAURAR,
				null,
				usuari);
		tascaHelper.restaurarTasca(tascaId);
		
//		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProcesJbpmId(
//				task.getTaskName(),
//				task.getProcessDefinitionId());
		Registre registre = new Registre(
				new Date(),
				expedient.getId(),
				usuari,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.TASCA,
				tascaId);
//		registre.setMissatge("Restaurar \"" + tascaHelper.getTitolPerTasca(task, tasca) + "\"");
		registre.setMissatge("Restaurar \"" + task.getTaskName() + "\"");
		registreRepository.save(registre);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isTascaValidada(String tascaId) {
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				tascaId,
				true,
				true);
		return tascaHelper.isTascaValidada(task);
	}	

	@Override
	@Transactional(readOnly = true)
	public boolean isDocumentsComplet(String tascaId) {
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				tascaId,
				true,
				true);
		return tascaHelper.isDocumentsComplet(task);
	}	

	@Override
	@Transactional(readOnly = true)
	public boolean isSignaturesComplet(String tascaId) {
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				tascaId,
				true,
				true);
		return tascaHelper.isSignaturesComplet(task);
	}	

	@Override
	@Transactional
	public void completar(
			String tascaId,
			String outcome) {
		logger.debug("Completant la tasca (" +
				"tascaId=" + tascaId + ", " +
				"variables=...)");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuari = auth.getName();
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				tascaId,
				true,
				true);
		if (!tascaHelper.isTascaValidada(task)) {
			throw new ValidacioException("La tasca amb taskInstanceId '" + tascaId + "' no està validada");
		}
		if (!tascaHelper.isDocumentsComplet(task)) {
			throw new ValidacioException("Falten documents per la tasca amb taskInstanceId '" + tascaId + "'.");
		}
		if (!tascaHelper.isSignaturesComplet(task)) {
			throw new ValidacioException("Falten signatures per la tasca amb taskInstanceId '" + tascaId + "'.");
		}
		
		//A partir d'aquí distingirem si la tasca s'ha d'executar en segon pla o no
		Tasca tasca = tascaHelper.findTascaByWTaskInstance(task);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		if (tasca.isFinalitzacioSegonPla()) {
			//cridar command per a marcar la tasca per a finalitzar en segón pla
			
			//data de marca de finalització
			Date marcadaFinalitzar = new Date();
			
			//recollim els rols del tipus de l'expedient
			String rols = expedientTipusHelper.getRolsTipusExpedient(auth, expedient.getTipus());

			workflowEngineApi.marcarFinalitzar(tascaId, marcadaFinalitzar, outcome, rols);
			checkFinalitzarSegonPla(tascaId, marcadaFinalitzar);

			workflowRetroaccioApi.afegirInformacioRetroaccioPerTasca(
					tascaId,
					expedient.getId(),
					WorkflowRetroaccioApi.ExpedientRetroaccioTipus.TASCA_MARCAR_FINALITZAR,
					outcome,
					usuari);
		} else {
			completarTasca(
					tascaId,
					expedient.getId(),
					task,
					outcome,
					usuari);
		}
	}
	
	
	@Override
	@Transactional
	public void completarMassiu(
			String tascaId,
			String outcome) {
		logger.debug("Completant la tasca (" +
				"tascaId=" + tascaId + ", " +
				"variables=...)");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuari = auth.getName();
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				tascaId,
				true,
				true);
		if (!tascaHelper.isTascaValidada(task)) {
			throw new ValidacioException("La tasca amb taskInstanceId '" + tascaId + "' no està validada");
		}
		if (!tascaHelper.isDocumentsComplet(task)) {
			throw new ValidacioException("Falten documents per la tasca amb taskInstanceId '" + tascaId + "'.");
		}
		if (!tascaHelper.isSignaturesComplet(task)) {
			throw new ValidacioException("Falten signatures per la tasca amb taskInstanceId '" + tascaId + "'.");
		}
		
		//A partir d'aquí distingirem si la tasca s'ha d'executar en segon pla o no
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		completarTasca(
				tascaId,
				expedient.getId(),
				task,
				outcome,
				usuari);
	}
	
	/**
	 * Es comprova si tenim carregades les dades de les tasques en segon
	 * pla en memòria. Si ja tenim la tasca (s'ha provat d'executar algún cop)
	 * s'actualitzaran les dades.
	 * Si no, es crea una entrada al Map per aquesta tasca que s'ha d'executar en segón pla.
	 */
	private void checkFinalitzarSegonPla(String taskId, Date marcadaFinalitzar) {
		if (!tascaSegonPlaHelper.isTasquesSegonPlaLoaded())
			tascaSegonPlaHelper.loadTasquesSegonPla();
		
		Map<String,InfoSegonPla> map = tascaSegonPlaHelper.getTasquesSegonPla();
		if (map.containsKey(taskId)) {
			InfoSegonPla infoSegonPla = map.get(taskId);
			infoSegonPla.setMarcadaFinalitzar(marcadaFinalitzar);
			infoSegonPla.setIniciFinalitzacio(null);
			infoSegonPla.setError(null);
			infoSegonPla.setMessages(new ArrayList<String[]>());
		} else { 
			tascaSegonPlaHelper.afegirTasca(taskId, marcadaFinalitzar);
		}
	}
	
	/**
	 * Si tenim carregades les tasques en segon pla
	 * s'elimina aquesta tasca del Map en momòria
	 */
	private void checkCompletarTasca(String id) {
		if (tascaSegonPlaHelper.isTasquesSegonPlaLoaded()) {
//			Long taskId = Long.parseLong(taskInstanceId);
//			TascaSegonPlaHelper.eliminarTasca(taskId);
			tascaSegonPlaHelper.completarTasca(id);
		}
	}

	private void completarTasca(
			String tascaId,
			Long expedientId,
			WTaskInstance task,
			String outcome,
			String usuari) {
		Long expId = workflowEngineApi.findExpedientIdByProcessInstanceId(
				task.getProcessInstanceId());
		Expedient expedient = expedientRepository.getById(expId);

		// TODO: Mètriques
//		mesuresTemporalsHelper.tascaCompletarIniciar(expedient, tascaId, task.getTaskName());
//
//		final Timer timerTotal = metricRegistry.timer(
//				MetricRegistry.name(
//						TascaService.class,
//						"completar"));
//		final Timer.Context contextTotal = timerTotal.time();
//		Counter countTotal = metricRegistry.counter(
//				MetricRegistry.name(
//						TascaService.class,
//						"completar.count"));
//		countTotal.inc();
//		final Timer timerEntorn = metricRegistry.timer(
//				MetricRegistry.name(
//						TascaService.class,
//						"completar",
//						expedient.getEntorn().getCodi()));
//		final Timer.Context contextEntorn = timerEntorn.time();
//		Counter countEntorn = metricRegistry.counter(
//				MetricRegistry.name(
//						TascaService.class,
//						"completar.count",
//						expedient.getEntorn().getCodi()));
//		countEntorn.inc();
//		final Timer timerTipexp = metricRegistry.timer(
//				MetricRegistry.name(
//						TascaService.class,
//						"completar",
//						expedient.getEntorn().getCodi(),
//						expedient.getTipus().getCodi()));
//		final Timer.Context contextTipexp = timerTipexp.time();
//		Counter countTipexp = metricRegistry.counter(
//				MetricRegistry.name(
//						TascaService.class,
//						"completar.count",
//						expedient.getEntorn().getCodi(),
//						expedient.getTipus().getCodi()));
//		countTipexp.inc();
		ThreadLocalInfo.clearProcessInstanceFinalitzatIds();
		try {
			workflowRetroaccioApi.afegirInformacioRetroaccioPerTasca(
					tascaId,
					expedientId,
					WorkflowRetroaccioApi.ExpedientRetroaccioTipus.TASCA_COMPLETAR,
					outcome,
					usuari);

			workflowEngineApi.startTaskInstance(tascaId);
			workflowEngineApi.endTaskInstance(tascaId, outcome);
//			workflowEngineApi.completeTaskInstance(task, outcome);
			checkCompletarTasca(tascaId);
			// Accions per a una tasca delegada
			DelegationInfo delegationInfo = tascaHelper.getDelegationInfo(task);
			if (delegationInfo != null) {
				if (!tascaId.equals(delegationInfo.getSourceTaskId())) {
					// Copia les variables de la tasca delegada a la original
					workflowEngineApi.setTaskInstanceVariables(
							delegationInfo.getSourceTaskId(),
							workflowEngineApi.getTaskInstanceVariables(tascaId),
							false);
					WTaskInstance taskOriginal = workflowEngineApi.getTaskById(
							delegationInfo.getSourceTaskId());
					if (!delegationInfo.isSupervised()) {
						// Si no es supervisada també finalitza la tasca original
						completar(taskOriginal.getId(), outcome);
					}
					tascaHelper.deleteDelegationInfo(taskOriginal);
				}
			}
			actualitzarTerminisIAlertes(tascaId, expedient);
			expedientHelper.verificarFinalitzacioExpedient(expedient);
			indexHelper.expedientIndexLuceneUpdate(expedient.getProcessInstanceId());
			Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProcesJbpmId(
					task.getTaskName(),
					task.getProcessDefinitionId());
			Registre registre = new Registre(
					new Date(),
					expedientId,
					usuari,
					Registre.Accio.FINALITZAR,
					Registre.Entitat.TASCA,
					tascaId);
			registre.setMissatge("Finalitzar \"" + tascaHelper.getTitolPerTasca(task, tasca) + "\"");
			registreRepository.save(registre);
		} catch (ExecucioHandlerException ex) {
			throw new TramitacioHandlerException(
					(expedient != null) ? expedient.getEntorn().getId() : null, 
					(expedient != null) ? expedient.getEntorn().getCodi() : null,
					(expedient != null) ? expedient.getEntorn().getNom() : null, 
					(expedient != null) ? expedient.getId() : null, 
					(expedient != null) ? expedient.getTitol() : null,
					(expedient != null) ? expedient.getNumero() : null,
					(expedient != null) ? expedient.getTipus().getId() : null, 
					(expedient != null) ? expedient.getTipus().getCodi() : null,
					(expedient != null) ? expedient.getTipus().getNom() : null,
					ex.getProcessInstanceId(),
					ex.getTaskInstanceId(),
					ex.getTokenId(),
					ex.getClassName(),
					ex.getMethodName(),
					ex.getFileName(),
					ex.getLineNumber(),
					"", 
					ex.getCause());
		} catch (Exception ex) {
			throw new TramitacioException(
					expedient.getEntorn().getId(),
					expedient.getEntorn().getCodi(), 
					expedient.getEntorn().getNom(), 
					expedient.getId(), 
					expedient.getTitol(), 
					expedient.getNumero(), 
					expedient.getTipus().getId(), 
					expedient.getTipus().getCodi(), 
					expedient.getTipus().getNom(), 
					"", 
					ex);
		} finally {
//			mesuresTemporalsHelper.tascaCompletarFinalitzar(tascaId);
//			contextTotal.stop();
//			contextEntorn.stop();
//			contextTipexp.stop();
		}
	}

	@Override
	@Transactional
	public void executarAccio(
			String tascaId,
			String accio) {
		logger.debug("Executant acció de la tasca (" +
				"tascaId=" + tascaId + ", " +
				"accio=" + accio + ")");
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				tascaId,
				true,
				true);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuari = auth.getName();
		workflowRetroaccioApi.afegirInformacioRetroaccioPerTasca(
				task.getId(),
				WorkflowRetroaccioApi.ExpedientRetroaccioTipus.TASCA_ACCIO_EXECUTAR,
				accio,
				usuari);
		// Executa l'acció de la instància de la tasca
		workflowEngineApi.executeActionInstanciaTasca(
				tascaId, 
				accio, 
				herenciaHelper.getProcessDefinitionIdHeretadaAmbTaskId(tascaId)
				);
		indexHelper.expedientIndexLuceneUpdate(task.getProcessInstanceId());
	}

	@Override
	@Transactional
	public FormulariExternDto formulariExternObrir(
			String tascaId) {
		logger.debug("Iniciant formulari extern per la tasca (" +
				"tascaId=" + tascaId + ")");
		tascaHelper.getTascaComprovacionsTramitacio(
				tascaId,
				true,
				true);
		
		ExpedientTipus expedientTipus = expedientTipusHelper.findAmbTaskId(
				tascaId);
		
		Tasca tasca = tascaHelper.findTascaByWTaskInstanceId(tascaId);
		return formulariExternHelper.iniciar(
				tascaId,
				tasca,
				expedientTipus,
				false);
	}
	
	@Override
	@Scheduled(fixedRate = 30000)
	@Async
	public void comprovarTasquesSegonPla() {
		
//		Si encara no hem inicialitzat la variable en memòria ho feim i li carregam les tasques
		tascaSegonPlaHelper.carregaTasquesSegonPla();
		
		if (tascaSegonPlaHelper.isTasquesSegonPlaLoaded() && tascaSegonPlaHelper.getTasquesSegonPla().size() > 0) {
//			for (Map.Entry<Long, InfoSegonPla> entry : TascaSegonPlaHelper.getTasquesSegonPla().entrySet()) {
//			for (Long key : TascaSegonPlaHelper.getTasquesSegonPla().keySet()) {
			Iterator<Map.Entry<String, InfoSegonPla>> iter = tascaSegonPlaHelper.getTasquesSegonPla().entrySet().iterator();
			while (iter.hasNext()) {
			    Map.Entry<String, InfoSegonPla> entry = iter.next();
				String tascaId = entry.getKey();
				InfoSegonPla infoSegonPla = entry.getValue();
				if (infoSegonPla.getMarcadaFinalitzar() != null &&
					infoSegonPla.getIniciFinalitzacio() == null &&
					infoSegonPla.getError() == null &&
					!infoSegonPla.isCompletada()) {
					
					Date iniciFinalitzacio = new Date();
					infoSegonPla.setIniciFinalitzacio(iniciFinalitzacio);
					
					try {
						tascaSegonPlaHelper.completaTascaSegonPla(tascaId, iniciFinalitzacio);
					} catch (Exception ex) {
						if (infoSegonPla.getError() == null || infoSegonPla.getError() == "") {
							String nouError;
							String logError;
							if (ex instanceof TramitacioException) {
								nouError = ((TramitacioException)ex).getPublicMessage();
								logError = ex.getMessage();
							} else if (ex instanceof TramitacioHandlerException) {
								nouError = ((TramitacioHandlerException)ex).getPublicMessage();
								logError = ex.getMessage();
							} else if (ex instanceof SistemaExternException) {
								nouError = ((SistemaExternException)ex).getPublicMessage();
								logError = ex.getMessage();
							} else if (ex.getCause() != null && ex.getCause().getMessage() != null && ex.getCause().getMessage() != "") {
								logError = ex.getCause().getMessage();
								nouError = logError;
							} else if (ex.toString() != null && ex.toString() != ""){
								logError = ex.toString();
								nouError = logError;
							} else{
								logError = "Error desconegut.";
								nouError = logError;
							}
							
							infoSegonPla.setError(nouError);
							logger.error("ERROR SEGON PLA: ",ex);
						}
						tascaSegonPlaHelper.guardarErrorFinalitzacio(tascaId, infoSegonPla.getError());
			        }

				} else if (infoSegonPla.getError() == null && infoSegonPla.isCompletada()) {
					try {
						iter.remove();
					} catch (Exception ex) {
						if (ex.getCause() != null) {
							logger.error(">>> No es pot eliminar l'iteració: " + ex.getCause().getMessage());
						}
					}
				}
			}
		}
	}
	
	@Override
	@Transactional
	public void carregaTasquesSegonPla() {
		if (!tascaSegonPlaHelper.isTasquesSegonPlaLoaded()) {
			//Primer carregam les ids de les tasques pendents d'executar en segon pla
			List<Object[]> tasquesSegonPlaIds = workflowEngineApi.getTasquesSegonPlaPendents();
			tascaSegonPlaHelper.loadTasquesSegonPla();
			if(tasquesSegonPlaIds != null && tasquesSegonPlaIds.size() > 0) {
				for(Object[] taskResult: tasquesSegonPlaIds) {
					tascaSegonPlaHelper.afegirTasca((String)taskResult[0], (Date)taskResult[1], (Date)taskResult[2], (String)taskResult[3]);
				}
			}
		}
	}
	
	@Override
	@Transactional
	public void completaTascaSegonPla(String tascaId, Date iniciFinalitzacio) {
        WTaskInstance task = workflowEngineApi.getTaskById(tascaId);
       
        Authentication orgAuth = SecurityContextHolder.getContext().getAuthentication();
        if (orgAuth == null) {
           
            final String user = task.getActorId();
           
            Principal principal = new Principal() {
                public String getName() {
                    return user;
                }
            };
           
            Authentication authentication =  new UsernamePasswordAuthenticationToken (
            		principal,
					"N/A",
					getAuthenticationRoles(task.getRols()));
			
	        SecurityContextHolder.getContext().setAuthentication(authentication);
        }

		workflowEngineApi.marcarIniciFinalitzacioSegonPla(tascaId, iniciFinalitzacio);
        // TODO - MS: obtenir l'expedientId a partir del processInstanceId --> MS Exp. Així només serveix si la tasca està al proces arrel
		Expedient expedient = expedientRepository.findByProcessInstanceId(task.getProcessInstanceId());
        completarTasca(
                tascaId,
//                task.getExpedientId(),
				expedient.getId(),
                task,
                task.getSelectedOutcome(),
                task.getActorId());
       
        SecurityContextHolder.getContext().setAuthentication(orgAuth);
    }
	
	@Override
	@Transactional
	public void guardarErrorFinalitzacio(String tascaId, String errorFinalitzacio) {
		workflowEngineApi.guardarErrorFinalitzacio(tascaId, errorFinalitzacio);
	}
	
	@Override
	@Transactional
	public void updateVariable(
			Long expedientId,
			String taskId,
			String codiVariable,
			Object valor) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		WTaskInstance task = workflowEngineApi.getTaskById(taskId);
		Object valorVell = variableHelper.getVariableJbpmTascaValor(task.getId(), codiVariable);
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(codiVariable, valor);
		workflowEngineApi.setTaskInstanceVariables(task.getId(), variables, false);
		
		Registre registre = new Registre(
				new Date(),
				expedientId,
				auth.getName(),
				Registre.Accio.MODIFICAR,
				Registre.Entitat.TASCA,
				task.getProcessInstanceId());
		registreRepository.save(registre);
		registre.setMissatge("Modificar variable '" + codiVariable + "'");
		if (valorVell != null)
			registre.setValorVell(valorVell.toString());
		if (valor != null)
			registre.setValorNou(valor.toString());
	}
	
	@Override
	public Map<String, Object> obtenirEstatsPerIds(List<String> tasquesSegonPlaIds){
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		for (String taskId: tasquesSegonPlaIds) {
			if (tascaSegonPlaHelper.getTasquesSegonPla().containsKey(taskId)) {
				result.put(taskId, tascaSegonPlaHelper.getTasquesSegonPla().get(taskId));
			}
		}
		return result;
	}
	
	@Override
	public boolean isEnSegonPla(String tascaSegonPlaId){
		if (tascaSegonPlaHelper.isTasquesSegonPlaLoaded()) {
			Long taskId = Long.parseLong(tascaSegonPlaId);
			if (tascaSegonPlaHelper.getTasquesSegonPla().containsKey(taskId)) {
				InfoSegonPla infoSegonPla = tascaSegonPlaHelper.getTasquesSegonPla().get(taskId);
				if (!infoSegonPla.isCompletada() && 
					infoSegonPla.getError() == null && 
					(infoSegonPla.getMarcadaFinalitzar() !=null || infoSegonPla.getIniciFinalitzacio() != null)) {
						return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public List<String[]> getMissatgesExecucioSegonPla(String tascaSegonPlaId) {
		List<String[]> result = new ArrayList<String[]>();
		if (tascaSegonPlaHelper.isTasquesSegonPlaLoaded()) {
			Long taskId = Long.parseLong(tascaSegonPlaId);
			if (tascaSegonPlaHelper.getTasquesSegonPla().containsKey(taskId)) {
				InfoSegonPla infoSegonPla = tascaSegonPlaHelper.getTasquesSegonPla().get(taskId);
				result = infoSegonPla.getMessages();
			}
		}
		
		return result;
	}
	
	public FormulariExternDto formulariExternObrirTascaInicial(
			String tascaIniciId,
			Long expedientTipusId,
			Long definicioProcesId) {
		ExpedientTipus expedientTipus = expedientTipusRepository.getById(expedientTipusId);
		DefinicioProces definicioProces = null;
		if (definicioProcesId != null) {
			definicioProces = definicioProcesRepository.getById(definicioProcesId);
		} else {
			definicioProces = definicioProcesRepository.findDarreraVersioAmbEntornIJbpmKey(
					expedientTipus.getEntorn().getId(), 
					expedientTipus.getJbpmProcessDefinitionKey());
		}
		if (definicioProcesId == null && definicioProces == null) {
			logger.error("No s'ha trobat la definició de procés (entorn=" + expedientTipus.getEntorn().getCodi() + ", jbpmKey=" + expedientTipus.getJbpmProcessDefinitionKey() + ")");
		}
		String starTaskName = workflowEngineApi.getStartTaskName(definicioProces.getJbpmId());
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProces(starTaskName, definicioProces);
		FormulariExternDto dto = formulariExternHelper.iniciar(
				tascaIniciId,
				tasca,
				expedientTipus,
				true);
		return dto;
	}

//	@Transactional
	private void actualitzarTerminisIAlertes(
			String taskId,
			Expedient expedient) {
		List<TerminiIniciat> terminisIniciats = terminiIniciatRepository.findByTaskInstanceId(taskId);
		for (TerminiIniciat terminiIniciat: terminisIniciats) {
			terminiIniciat.setDataCompletat(new Date());
			esborrarAlertesAntigues(terminiIniciat);
			if (terminiIniciat.getTermini().isAlertaCompletat()) {
				WTaskInstance task = workflowEngineApi.getTaskById(taskId);
				if (task.getActorId() != null) {
					crearAlertaCompletat(terminiIniciat, task.getActorId(), expedient);
				} else {
					for (String actor: task.getPooledActors())
						crearAlertaCompletat(terminiIniciat, actor, expedient);
				}
				terminiIniciat.setAlertaCompletat(true);
			}
		}
	}

//	@Transactional
	private void crearAlertaCompletat(
			TerminiIniciat terminiIniciat,
			String destinatari,
			Expedient expedient) {
		Alerta alerta = new Alerta(
				new Date(),
				destinatari,
				Alerta.AlertaPrioritat.NORMAL,
				terminiIniciat.getTermini().getDefinicioProces() != null ?
						terminiIniciat.getTermini().getDefinicioProces().getEntorn()
						: terminiIniciat.getTermini().getExpedientTipus().getEntorn());
		alerta.setExpedient(expedient);
		alerta.setTerminiIniciat(terminiIniciat);
		alertaRepository.save(alerta);
	}

//	@Transactional
	private void esborrarAlertesAntigues(TerminiIniciat terminiIniciat) {
		List<Alerta> antigues = alertaRepository.findActivesAmbTerminiIniciatId(terminiIniciat.getId());
		for (Alerta antiga: antigues)
			antiga.setDataEliminacio(new Date());
	}
	
	private List<GrantedAuthority> getAuthenticationRoles(String rols) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		if (rols != null && !rols.isEmpty()) {
			for (String rol: rols.split(",")) {
				authorities.add(new SimpleGrantedAuthority(rol));
			}
		}
		return authorities;
	}

	@Override
	public TascaDto findTascaById(Long id) {
		return conversioTipusServiceHelper.convertir(tascaRepository.getById(id), TascaDto.class);
	}

	/** Per guardar la consulta de dades incials*/
	private Map<String, Map<String, Object>> dadesFormulariExternInicial;

	@Override
	public void guardarFormulariExtern(
			String formulariId, 
			Map<String, Object> variables) {

		logger.debug("Guardant formulari extern (" +
				"formulariId=" + formulariId + ", " +
				"valorsTasca=" + variables + ")");

		
		FormulariExtern formExtern = formulariExternRepository.getById(Long.parseLong(formulariId));
		if (formExtern != null) {
			if (formulariId.startsWith("TIE_")) {
				if (dadesFormulariExternInicial == null)
					dadesFormulariExternInicial = new HashMap<String, Map<String, Object>>();
				dadesFormulariExternInicial.put(formulariId, variables);
			} else {
				Map<String, Object> valors = new HashMap<String, Object>();
				WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(formExtern.getTaskId(), false, false);
				Tasca tasca = tascaHelper.findTascaByWTaskInstance(task);
				for (CampTasca camp: tasca.getCamps()) {
					if (!camp.isReadOnly()) {
						String codi = camp.getCamp().getCodi();
						if (variables.keySet().contains(codi))
							valors.put(codi, variables.get(codi));
					}
				}
				validar(formExtern.getTaskId(),
						valors);
			}
			formExtern.setDataRecepcioDades(new Date());
			logger.info("Les dades del formulari amb id " + formulariId + " han estat guardades");
		} else {
			logger.warn("No s'ha trobat cap tasca amb l'id de formulari " + formulariId);
		}
	}

	@Override
	public Map<String, Object> obtenirValorsFormulariExternInicial(String formulariId) {
		if (dadesFormulariExternInicial == null)
			return null;
		return dadesFormulariExternInicial.remove(formulariId);
	}

	private static final Logger logger = LoggerFactory.getLogger(TascaServiceImpl.class);
}
