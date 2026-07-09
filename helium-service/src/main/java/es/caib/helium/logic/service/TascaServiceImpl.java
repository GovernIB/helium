/**
 *
 */
package es.caib.helium.logic.service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import es.caib.helium.commons.dto.*;
import es.caib.helium.logic.helper.*;
import es.caib.helium.persistence.entity.*;
import es.caib.helium.persistence.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.exception.SistemaExternException;
import es.caib.helium.commons.exception.TramitacioException;
import es.caib.helium.commons.exception.TramitacioHandlerException;
import es.caib.helium.commons.exception.ValidacioException;
import es.caib.helium.commons.utils.MessageHelper;
import es.caib.helium.logic.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.helium.logic.helper.TascaSegonPlaHelper.InfoSegonPla;
import es.caib.helium.logic.helpers.MesuresTemporalsHelper;
import es.caib.helium.logic.intf.dto.engine.WProcessInstance;
import es.caib.helium.logic.intf.dto.engine.WTaskInstance;
import es.caib.helium.logic.intf.service.TascaService;
import es.caib.helium.logic.intf.service.WorkflowEngineApi;
import es.caib.helium.logic.security.ExtendedPermission;
import es.caib.helium.persistence.common.ThreadLocalInfo;
import es.caib.helium.persistence.entity.ExpedientLog.ExpedientLogAccioTipus;

/**
 * Servei per gestionar terminis.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("tascaServiceV3")
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
	private ExpedientTascaRepository expedientTascaRepository;

	@Resource
	private ExpedientRegistreHelper expedientRegistreHelper;
	@Autowired
	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource(name = "permisosHelperV3")
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
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private VariableHelper variableHelper;
	@Resource(name="documentHelperV3")
	private DocumentHelperV3 documentHelper;
	@Autowired
	private ExpedientDadaHelper expedientDadaHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private FormulariExternHelper formulariExternHelper;
	@Resource
	private ExpedientLoggerHelper expedientLoggerHelper;
	@Resource
	private HerenciaHelper herenciaHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private TascaSegonPlaHelper tascaSegonPlaHelper;
	@Autowired
	private MetricRegistry metricRegistry;
	@Resource
	private UsuariActualHelper usuariActualHelper;
	@Resource
	private PluginHelper pluginHelper;


	@Override
	@Transactional(readOnly = true)
	public ExpedientTascaDto findAmbIdPerExpedient(
			String id,
			Long expedientId) {
		logger.debug("Consultant tasca per expedient donat el seu id (" +
				"id=" + id + ")");
		Expedient expedient = expedientRepository.findById(expedientId).orElse(null);
		if (expedient == null)
			throw new NoTrobatException(Expedient.class, expedientId);

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
		logger.debug("Consultant tasca per tramitar donat el seu id (" +
				"id=" + id + ")");
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
	public List<Long> findIdsPerFiltre(
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
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TascaService.class,
						"llistatIds"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TascaService.class,
						"llistatIds.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TascaService.class,
						"llistatIds",
						entorn.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TascaService.class,
						"llistatIds.count",
						entorn.getCodi()));
		countEntorn.inc();
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
			mesuresTemporalsHelper.mesuraIniciar("CONSULTA TASQUES LLISTAT", "consulta");
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

			return workflowEngineApi.findListTasks(
					responsable,
					titol,
					tasca,
					idsExpedients,
					dataCreacioInici,
					dataCreacioFi,
					prioritat,
					dataLimitInici,
					dataLimitFi,
					new PaginacioParamsDto(),
					nomesTasquesPersonals,
					nomesTasquesGrup,
					true);
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ExpedientTascaDto> findPerFiltrePaginat(
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
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TascaService.class,
						"llistat"));
		final Timer.Context contextTimerTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TascaService.class,
						"llistat.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TascaService.class,
						"llistat",
						entorn.getCodi()));
		final Timer.Context contextTimerEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TascaService.class,
						"llistat.count",
						entorn.getCodi()));
		countEntorn.inc();
		//Map<Long,List<Long>> unitatsPerTipusComu = new HashMap<Long, List<Long>>();
		try {
			final Timer timerConsultaTotal = metricRegistry.timer(
					MetricRegistry.name(
							TascaService.class,
							"llistat.consulta"));
			final Timer.Context contextTimerConsultaTotal = timerConsultaTotal.time();
			final Timer timerConsultaEntorn = metricRegistry.timer(
					MetricRegistry.name(
							TascaService.class,
							"llistat.consulta",
							entorn.getCodi()));
			final Timer.Context contextTimerConsultaEntorn = timerConsultaEntorn.time();
			Page<ExpedientTasca> page = null;
			try {
				// Si no hi ha tipexp seleccionat o no es te permis SUPERVISION
				// a damunt el tipexp es filtra per l'usuari actual.
				//if (nomesTasquesMeves || expedientTipusId == null || !expedientTipusHelper.comprovarPermisSupervisio(expedientTipusId)) {
				if (responsable == null && (nomesTasquesMeves || expedientTipusId == null || !expedientTipusHelper.comprovarPermisSupervisio(expedientTipusId))) {
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

				for(PaginacioParamsDto.OrdreDto order : paginacioParams.getOrdres()) {
					switch (order.getCamp()) {
					case "responsableString":
						order.setCamp("assignee");
						break;
					case "expedientIdentificador":
						order.setCamp("expedient.numeroDefault");
						break;
					case "unitatOrganitzativaCodiNom":
						order.setCamp("expedient.unitatOrganitzativa");
						break;
					case "expedientTipusNom":
						order.setCamp("expedient.tipus.nom");
						break;
					}
				}

				page = expedientTascaRepository.tascaFindByFiltrePaginat(
					entornId,
					responsable,
					responsable == null,
					tasca,
					tasca == null,
					titol,
					titol == null,
					expedient,
					expedient == null,
					expedientTipusId,
					expedientTipusId == null,
					dataCreacioInici,
					dataCreacioInici == null,
					dataCreacioFi,
					dataCreacioFi == null,
					dataLimitInici,
					dataLimitInici == null,
					dataLimitFi,
					dataLimitFi == null,
					mostrarAssignadesUsuari,
					mostrarAssignadesGrup,
					usuariActualHelper.isAdministrador(),
					paginacioHelper.toSpringDataPageable(paginacioParams));
			} finally {
				contextTimerConsultaTotal.stop();
				contextTimerConsultaEntorn.stop();
			}
			final Timer timerConversionTotal = metricRegistry.timer(
					MetricRegistry.name(
							TascaService.class,
							"llistat.conversio"));
			final Timer.Context contextTimerConversioTotal = timerConversionTotal.time();
			final Timer timerConversioEntorn = metricRegistry.timer(
					MetricRegistry.name(
							TascaService.class,
							"llistat.conversio",
							entorn.getCodi()));
			final Timer.Context contextTimerConversioEntorn = timerConversioEntorn.time();
			try {
				List<ExpedientTascaDto> resultat = conversioTipusHelper.convertirList(
																		page.getContent(),
																		ExpedientTascaDto.class);

				String usuariActual = usuariActualHelper.getUsuariActual();
				resultat.forEach(et -> {
					et.setAssignadaUsuariActual(
						et.getAssignee() != null && usuariActualHelper.getUsuariActual().equals(et.getAssignee()));

					if (et.getAssignee() != null) {
						et.setResponsable(
							tascaHelper.findPersonaOrDefault(et.getAssignee()));

						if (usuariActual != null)
							et.setAssignadaUsuariActual(et.getAssignee().equals(usuariActual));
					} else if (et.getPooledActors() != null && !et.getPooledActors().isEmpty()) {
						List<PersonaDto> responsables = new ArrayList<PersonaDto>();
						for (String pooledActor: et.getPooledActors()) {
							PersonaDto persona = tascaHelper.findPersonaOrDefault(pooledActor);
							if (persona != null) {
								if (usuariActual.equals(pooledActor))
									et.setAssignadaUsuariActual(true);
								responsables.add(persona);
							}
						}
						Collections.sort(
							responsables,
							new Comparator<PersonaDto>() {
								public int compare(PersonaDto p1, PersonaDto p2) {
									return p1.getNom().compareToIgnoreCase(p2.getNom());
								}
							});
						et.setResponsables(responsables);
					}

					if (tascaSegonPlaHelper.isTasquesSegonPlaLoaded() &&
						tascaSegonPlaHelper.getTasquesSegonPla().containsKey(et.getId())) {
						InfoSegonPla infoSegonPla = tascaSegonPlaHelper.getTasquesSegonPla().get(et.getId());
						et.setMarcadaFinalitzar(infoSegonPla.getMarcadaFinalitzar());
						et.setIniciFinalitzacio(infoSegonPla.getIniciFinalitzacio());
						et.setErrorFinalitzacio(infoSegonPla.getError());
					}

					permisosHelper.omplirControlPermisosSegonsUsuariActual(
						et.getExpedientTipusId(),
						et,
						ExpedientTipus.class);
				});
				return paginacioHelper.toPaginaDto(
					resultat,
					page.getSize(),
					paginacioParams);
			} finally {
				contextTimerConversioTotal.stop();
				contextTimerConversioEntorn.stop();
			}
		} finally {
			contextTimerTotal.stop();
			contextTimerEntorn.stop();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<TascaDadaDto> findDades(
			String id) {
		logger.debug("Consultant dades de la tasca (" +
				"id=" + id + ")");
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				id,
				true,
				true);
		return variableHelper.findDadesPerInstanciaTasca(task);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExpedientTascaDto> findAmbIds(Set<Long> ids) {
		logger.debug("Consultant expedients de las tascas (" +
				"ids=" + ids + ")");

		List<ExpedientTascaDto> expedientTasques = new ArrayList<ExpedientTascaDto>();
		for (Long id : ids) {
			WTaskInstance task = workflowEngineApi.getTaskById(String.valueOf(id));
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
				"id=" + id + ")");
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
				"id=" + id + ")");
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				id,
				true,
				true);
		Tasca tasca = tascaHelper.findTascaByJbpmTask(task);
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
				"id=" + id + ")");
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
				"id=" + id + ")");
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
					false,
					null);
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
				"id=" + id + ")");
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
				"id=" + id + ")");
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
		Camp camp = campRepository.findById(campId).orElse(null);
		Camp registreCamp = null;
		if (registreCampId != null) {
			registreCamp = campRepository.findById(registreCampId).orElse(null);
		}
		String pidCalculat = processInstanceId;
		if (processInstanceId == null && tascaId != null) {
			WTaskInstance task = null;
			task = tascaHelper.getTascaComprovacionsTramitacio(
					tascaId,
					true,
					true);
			// Comprova si el camp pertany a la tasca
			Tasca tasca = tascaHelper.findTascaByJbpmTask(task);
			boolean trobat = false;
			for (CampTasca campTasca: tasca.getCamps()) {
				if (campTasca.getCamp().equals(camp)) {
					trobat = true;
				} else if (campTasca.getCamp().getTipus().equals(CampTipusDto.REGISTRE)) {
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
						pidCalculat);
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
			String taskInstanceid) {
		logger.debug("Agafant tasca (id=" + taskInstanceid + ")");
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				taskInstanceid,
				false,
				true);
		//	Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());

		// TODO contemplar el cas que no faci falta que l'usuari
		// estigui als pooledActors
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Set<String> pooledActors = task.getPooledActors();
		if (!pooledActors.contains(auth.getName())) {
			logger.debug("L'usuari no s'ha trobat com a pooledActor de la tasca (" +
					"id=" + taskInstanceid + ", " +
					"personaCodi=" + auth.getName() + ")");
			throw new NoTrobatException(WTaskInstance.class,taskInstanceid);
		}
		String previousActors = expedientLoggerHelper.getActorsPerReassignacioTasca(taskInstanceid);
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerTasca(
				taskInstanceid,
				ExpedientLogAccioTipus.TASCA_REASSIGNAR,
				previousActors);
		task = workflowEngineApi.takeTaskInstance(taskInstanceid, auth.getName());
		String currentActors = expedientLoggerHelper.getActorsPerReassignacioTasca(taskInstanceid);
		expedientLog.setAccioParams(previousActors + "::" + currentActors);
		ExpedientTascaDto tasca = tascaHelper.toExpedientTascaDto(
				task,
				null,
				false,
				false);
		expedientRegistreHelper.crearRegistreIniciarTasca(
				tasca.getExpedientId(),
				taskInstanceid,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				"Agafar tasca \"" + tasca.getTitol() + "\"");
		tascaHelper.refreshExpedientTasca(taskInstanceid);
		return tasca;
	}

	@Override
	@Transactional
	public ExpedientTascaDto alliberar(
			String taskInstanceId) {
		logger.debug("Alliberant tasca (id=" + taskInstanceId + ")");
		// TODO contemplar el cas que no faci falta que l'usuari
		// de la tasca sigui l'usuari actual
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				taskInstanceId,
				false,
				true);
//		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		String previousActors = expedientLoggerHelper.getActorsPerReassignacioTasca(
				taskInstanceId);
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerTasca(
				taskInstanceId,
				ExpedientLogAccioTipus.TASCA_REASSIGNAR,
				previousActors);
		task = workflowEngineApi.releaseTaskInstance(taskInstanceId);
		String currentActors = expedientLoggerHelper.getActorsPerReassignacioTasca(taskInstanceId);
		expedientLog.setAccioParams(previousActors + "::" + currentActors);
		ExpedientTascaDto tasca = tascaHelper.toExpedientTascaDto(
				task,
				null,
				false,
				false);
		expedientRegistreHelper.crearRegistreIniciarTasca(
				tasca.getExpedientId(),
				taskInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				"Amollar tasca \"" + tasca.getTitol() + "\"");
		tascaHelper.refreshExpedientTasca(taskInstanceId);
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
			expedientLoggerHelper.afegirLogExpedientPerTasca(
					taskInstanceId,
					ExpedientLogAccioTipus.TASCA_DOCUMENT_ESBORRAR,
					documentCodi);
		} else {
			expedientLoggerHelper.afegirLogExpedientPerProces(
					task.getProcessInstanceId(),
					ExpedientLogAccioTipus.PROCES_DOCUMENT_ESBORRAR,
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
			expedientLoggerHelper.afegirLogExpedientPerTasca(
					taskInstanceId,
					ExpedientLogAccioTipus.TASCA_DOCUMENT_AFEGIR,
					documentCodi);
		} else {
			expedientLoggerHelper.afegirLogExpedientPerTasca(
					taskInstanceId,
					ExpedientLogAccioTipus.TASCA_DOCUMENT_MODIFICAR,
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
				null).getId();
		//Al actualitzar un document desde una tasca, s'ha de associar la tasca a la variable JBMP
		//En cas de crear, no fa falta perque ja s'haurà assignat al metode DocumentHelperV3.postProcessarDocument
		if (documentStore!=null) {
			workflowEngineApi.setTaskInstanceVariable(
					taskInstanceId,
					documentStore.getCodi(),
					documentStore.getId());
		}
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
			String taskInstanceId,
			Map<String, Object> variables) {
		logger.debug("Guardant les dades del formulari de la tasca (" +
				"taskInstanceId=" + taskInstanceId + ", " +
				"variables= "+variables+")");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuari = auth.getName();
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				taskInstanceId,
				true,
				true);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		expedientLoggerHelper.afegirLogExpedientPerTasca(
				taskInstanceId,
				expedient.getId(),
				ExpedientLogAccioTipus.TASCA_FORM_GUARDAR,
				null,
				usuari);
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProcesJbpmId(
				task.getTaskName(),
				task.getProcessDefinitionId());
		Map<String, Object> variablesProcessades = new HashMap<String, Object>(variables);
		tascaHelper.processarCampsAmbDominiCacheActivat(task, tasca, variablesProcessades);
		task = workflowEngineApi.getTaskById(taskInstanceId);
		workflowEngineApi.setTaskInstanceVariables(taskInstanceId, variablesProcessades, false);
		tascaHelper.refreshExpedientTasca(taskInstanceId);
		this.expedientDadaHelper.setDades(expedient, task.getProcessInstanceId(), taskInstanceId, variablesProcessades);
		if (task.getStartTime() == null) {
			Registre registre = new Registre(
					new Date(),
					expedient.getId(),
					usuari,
					Registre.Accio.MODIFICAR,
					Registre.Entitat.TASCA,
					taskInstanceId);
//			registre.setMissatge("Iniciar tasca \"" + tascaHelper.getTitolPerTasca(task, tasca) + "\"");
			registre.setMissatge("Iniciar tasca \"" + task.getTaskName() + "\"");
			registreRepository.save(registre);
		}
	}

	@Override
	@Transactional
	public void validar(
			String taskInstanceId,
			Map<String, Object> variables) {
		logger.debug("Validant el formulari de la tasca (" +
				"tascaId=" + taskInstanceId + ", " +
				"variables= "+variables+")");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuari = auth.getName();
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				taskInstanceId,
				true,
				true);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		expedientLoggerHelper.afegirLogExpedientPerTasca(
				taskInstanceId,
				expedient.getId(),
				ExpedientLogAccioTipus.TASCA_FORM_VALIDAR,
				null,
				usuari);
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProcesJbpmId(
				task.getTaskName(),
				task.getProcessDefinitionId());
		tascaHelper.processarCampsAmbDominiCacheActivat(task, tasca, variables);
		workflowEngineApi.startTaskInstance(taskInstanceId);
		workflowEngineApi.setTaskInstanceVariables(taskInstanceId, variables, false);
		tascaHelper.refreshExpedientTasca(taskInstanceId);
		tascaHelper.validarTasca(taskInstanceId);

		Registre registre = new Registre(
				new Date(),
				expedient.getId(),
				usuari,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.TASCA,
				taskInstanceId);
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
		expedientLoggerHelper.afegirLogExpedientPerTasca(
				tascaId,
				expedient.getId(),
				ExpedientLogAccioTipus.TASCA_FORM_RESTAURAR,
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
			String taskInstanceId,
			String outcome) {
		logger.debug("Completant la tasca (" +
				"tascaId=" + taskInstanceId + ", " +
				"variables=...)");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuari = auth.getName();
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				taskInstanceId,
				true,
				true);
		if (!tascaHelper.isTascaValidada(task)) {
			throw new ValidacioException("La tasca amb id '" + taskInstanceId + "' no està validada");
		}
		if (!tascaHelper.isDocumentsComplet(task)) {
			throw new ValidacioException("Falten documents per la tasca amb id '" + taskInstanceId + "'.");
		}
		if (!tascaHelper.isSignaturesComplet(task)) {
			throw new ValidacioException("Falten signatures per la tasca amb id '" + taskInstanceId + "'.");
		}

		//A partir d'aquí distingirem si la tasca s'ha d'executar en segon pla o no
		Tasca tasca = tascaHelper.findTascaByJbpmTask(task);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		if (tasca.isFinalitzacioSegonPla()) {
			//cridar command per a marcar la tasca per a finalitzar en segón pla

			//data de marca de finalització
			Date marcadaFinalitzar = new Date();

			//recollim els rols del tipus de l'expedient
			String rols = expedientTipusHelper.getRolsTipusExpedient(auth, expedient.getTipus());

			workflowEngineApi.marcarFinalitzar(taskInstanceId, marcadaFinalitzar, outcome, rols);
			checkFinalitzarSegonPla(taskInstanceId, marcadaFinalitzar);
			tascaHelper.refreshExpedientTasca(taskInstanceId);
			expedientLoggerHelper.afegirLogExpedientPerTasca(
					taskInstanceId,
					expedient.getId(),
					ExpedientLogAccioTipus.TASCA_MARCAR_FINALITZAR,
					outcome,
					usuari);
		} else {
			completarTasca(
					taskInstanceId,
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
			throw new ValidacioException("La tasca amb id '" + tascaId + "' no està validada");
		}
		if (!tascaHelper.isDocumentsComplet(task)) {
			throw new ValidacioException("Falten documents per la tasca amb id '" + tascaId + "'.");
		}
		if (!tascaHelper.isSignaturesComplet(task)) {
			throw new ValidacioException("Falten signatures per la tasca amb id '" + tascaId + "'.");
		}

		//A partir d'aquí distingirem si la tasca s'ha d'executar en segon pla o no
		completarTasca(
				tascaId,
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

	private void completarTasca(
			String tascaId,
			WTaskInstance task,
			String outcome,
			String usuari) {
		ExpedientDto piexp = workflowEngineApi.expedientFindByProcessInstanceId(
				task.getProcessInstanceId());
		Expedient expedient = expedientRepository.findById(piexp.getId()).orElse(null);

		mesuresTemporalsHelper.tascaCompletarIniciar(expedient, tascaId, task.getTaskName());

		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TascaService.class,
						"completar"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TascaService.class,
						"completar.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TascaService.class,
						"completar",
						expedient.getEntorn().getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TascaService.class,
						"completar.count",
						expedient.getEntorn().getCodi()));
		countEntorn.inc();
		final Timer timerTipexp = metricRegistry.timer(
				MetricRegistry.name(
						TascaService.class,
						"completar",
						expedient.getEntorn().getCodi(),
						expedient.getTipus().getCodi()));
		final Timer.Context contextTipexp = timerTipexp.time();
		Counter countTipexp = metricRegistry.counter(
				MetricRegistry.name(
						TascaService.class,
						"completar.count",
						expedient.getEntorn().getCodi(),
						expedient.getTipus().getCodi()));
		countTipexp.inc();
		ThreadLocalInfo.clearProcessInstanceFinalitzatIds();
		try {
			/*
			ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerTasca(
					tascaId,
					expedientId,
					ExpedientLogAccioTipus.TASCA_COMPLETAR,
					outcome,
					usuari);
			jbpmHelper.startTaskInstance(tascaId);
			jbpmHelper.endTaskInstance(tascaId, outcome);
			checkCompletarTasca(tascaId);
			// Accions per a una tasca delegada
			DelegationInfo delegationInfo = tascaHelper.getDelegationInfo(task);
			if (delegationInfo != null) {
				if (!tascaId.equals(delegationInfo.getSourceTaskId())) {
					// Copia les variables de la tasca delegada a la original
					jbpmHelper.setTaskInstanceVariables(
							delegationInfo.getSourceTaskId(),
							jbpmHelper.getTaskInstanceVariables(tascaId),
							false);
					WTaskInstance taskOriginal = jbpmHelper.getTaskById(
							delegationInfo.getSourceTaskId());
					if (!delegationInfo.isSupervised()) {
						// Si no es supervisada també finalitza la tasca original
						completar(taskOriginal.getId(), outcome);
					}
					tascaHelper.deleteDelegationInfo(taskOriginal);
				}
			}
			actualitzarTerminisIAlertes(tascaId, expedientLog.getExpedient());
			expedientHelper.verificarFinalitzacioExpedient(
					expedientLog.getExpedient());
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

			comandaHelper.upsertTasca(tascaId, tasca.getNom(), expedient.getNumero(), task, ComandaTascaEstat.FINALITZADA);

			registre.setMissatge("Finalitzar \"" + tascaHelper.getTitolPerTasca(task, tasca) + "\"");
			registreRepository.save(registre);
			*/
//		} catch (ExecucioHandlerException ex) {
//			throw new TramitacioHandlerException(
//					(expedient != null) ? expedient.getEntorn().getId() : null,
//					(expedient != null) ? expedient.getEntorn().getCodi() : null,
//					(expedient != null) ? expedient.getEntorn().getNom() : null,
//					(expedient != null) ? expedient.getId() : null,
//					(expedient != null) ? expedient.getTitol() : null,
//					(expedient != null) ? expedient.getNumero() : null,
//					(expedient != null) ? expedient.getTipus().getId() : null,
//					(expedient != null) ? expedient.getTipus().getCodi() : null,
//					(expedient != null) ? expedient.getTipus().getNom() : null,
//					ex.getProcessInstanceId(),
//					ex.getTaskInstanceId(),
//					ex.getTokenId(),
//					ex.getClassName(),
//					ex.getMethodName(),
//					ex.getFileName(),
//					ex.getLineNumber(),
//					"",
//					ex.getCause());
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
			mesuresTemporalsHelper.tascaCompletarFinalitzar(tascaId);
			contextTotal.stop();
			contextEntorn.stop();
			contextTipexp.stop();
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
		expedientLoggerHelper.afegirLogExpedientPerTasca(
				task,
				ExpedientLogAccioTipus.TASCA_ACCIO_EXECUTAR,
				accio,
				usuari);
		// Executa l'acció de la instància de la tasca
		workflowEngineApi.executeActionInstanciaTasca(
				tascaId,
				accio,
				herenciaHelper.getProcessDefinitionIdHeretadaAmbTaskId(tascaId)
				);
		// Actualitza les dades de l'expedient per si canvien.
		WProcessInstance pi = workflowEngineApi.getProcessInstance(task.getProcessInstanceId());
		expedientDadaHelper.setExpedientDades(expedientRepository.findByProcessInstanceId(pi.getRootProcessInstanceId()));
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

		Tasca tasca = tascaHelper.findTascaByJbpmTaskId(tascaId);
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
				String tascaId = entry.getKey().toString();
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
			if(tasquesSegonPlaIds.size() > 0) {
				for(Object[] taskResult: tasquesSegonPlaIds) {
					tascaSegonPlaHelper.afegirTasca((String)taskResult[0], (Date)taskResult[1], (Date)taskResult[2], (String)taskResult[3]);
				}
			}
		}
	}

	@Override
	@Transactional
	public void completaTascaSegonPla(String taskInstanceId, Date iniciFinalitzacio) {
        WTaskInstance task = workflowEngineApi.getTaskById(taskInstanceId);

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

        workflowEngineApi.marcarIniciFinalitzacioSegonPla(taskInstanceId, iniciFinalitzacio);
		tascaHelper.refreshExpedientTasca(taskInstanceId);

        completarTasca(
                taskInstanceId,
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
	public Map<String,Object> obtenirEstatsPerIds(List<String> tasquesSegonPlaIds){
		Map<String,Object> result = new LinkedHashMap<String, Object>();
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
			if (tascaSegonPlaHelper.getTasquesSegonPla().containsKey(tascaSegonPlaId)) {
				InfoSegonPla infoSegonPla = tascaSegonPlaHelper.getTasquesSegonPla().get(tascaSegonPlaId);
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
	public List<String[]> getMissatgesExecucioSegonPla(String taskId) {
		List<String[]> result = new ArrayList<String[]>();
		if (tascaSegonPlaHelper.isTasquesSegonPlaLoaded()) {
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
		ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId).orElse(null);
		DefinicioProces definicioProces = null;
		if (definicioProcesId != null) {
			definicioProces = definicioProcesRepository.findById(definicioProcesId).orElse(null);
		} else {
			definicioProces = definicioProcesRepository.findDarreraVersioAmbEntornTipusIJbpmKey(
					expedientTipus.getEntorn().getId(),
					false,
					expedientTipusId,
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

	@Transactional
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

	@Transactional
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

	@Transactional
	private void esborrarAlertesAntigues(TerminiIniciat terminiIniciat) {
		List<Alerta> antigues = alertaRepository.findActivesAmbTerminiIniciatId(terminiIniciat.getId());
		for (Alerta antiga: antigues)
			antiga.setDataEliminacio(new Date());
	}

	private List<GrantedAuthority> getAuthenticationRoles(Collection<String> rols) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		if (rols != null && !rols.isEmpty()) {
			for (String rol: rols) {
				authorities.add(new SimpleGrantedAuthority(rol));
			}
		}
		return authorities;
	}

	private static final Logger logger = LoggerFactory.getLogger(TascaServiceImpl.class);

	@Override
	public TascaDto findTascaById(Long id) {
		return conversioTipusHelper.convertir(tascaRepository.findById(id), TascaDto.class);
	}

}
