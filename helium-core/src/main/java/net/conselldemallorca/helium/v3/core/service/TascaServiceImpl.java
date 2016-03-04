/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.core.helper.EntornHelper;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.ExpedientLoggerHelper;
import net.conselldemallorca.helium.core.helper.ExpedientRegistreHelper;
import net.conselldemallorca.helium.core.helper.ExpedientTipusHelper;
import net.conselldemallorca.helium.core.helper.FormulariExternHelper;
import net.conselldemallorca.helium.core.helper.MessageHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper.Converter;
import net.conselldemallorca.helium.core.helper.ServiceUtils;
import net.conselldemallorca.helium.core.helper.TascaHelper;
import net.conselldemallorca.helium.core.helper.TascaSegonPlaHelper;
import net.conselldemallorca.helium.core.helper.TascaSegonPlaHelper.InfoSegonPla;
import net.conselldemallorca.helium.core.helper.VariableHelper;
import net.conselldemallorca.helium.core.helperv26.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.helperv26.PermisosHelper;
import net.conselldemallorca.helium.core.helperv26.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Registre;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.jbpm3.integracio.DelegationInfo;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.jbpm3.integracio.LlistatIds;
import net.conselldemallorca.helium.jbpm3.integracio.ResultatConsultaPaginadaJbpm;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.FormulariExternDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.SeleccioOpcioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDocumentDto;
import net.conselldemallorca.helium.v3.core.api.exception.IllegalStateException;
import net.conselldemallorca.helium.v3.core.api.exception.NotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.v3.core.repository.AlertaRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.CampTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioValorsRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientHeliumRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.RegistreRepository;
import net.conselldemallorca.helium.v3.core.repository.TascaRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiIniciatRepository;

import org.jbpm.graph.exe.ProcessInstanceExpedient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

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
	private ExpedientRegistreHelper expedientRegistreHelper;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private TascaHelper tascaHelper;
	@Resource
	private EntornHelper entornHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private VariableHelper variableHelper;
	@Resource(name="documentHelperV3")
	private DocumentHelperV3 documentHelper;
	@Resource(name="serviceUtilsV3")
	private ServiceUtils serviceUtils;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private FormulariExternHelper formulariExternHelper;
	@Resource
	private ExpedientLoggerHelper expedientLoggerHelper;
	
	@Autowired
	private TascaSegonPlaHelper tascaSegonPlaHelper;
	@Autowired
	private MetricRegistry metricRegistry;



	@Override
	@Transactional(readOnly = true)
	public ExpedientTascaDto findAmbIdPerExpedient(
			String id,
			Long expedientId) {
		logger.debug("Consultant tasca per expedient donat el seu id (" +
				"id=" + id + ")");
		Expedient expedient = expedientRepository.findOne(expedientId);
		JbpmTask task = tascaHelper.getTascaComprovacionsExpedient(
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
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
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
				true,
				false,
				false);
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
				expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
						expedientTipusId,
						true,
						false,
						false);
			}
			// Si no hi ha tipexp seleccionat o no es te permis SUPERVISION
			// a damunt el tipexp es filtra per l'usuari actual.
			if (expedientTipusId == null || expedientTipusHelper.getExpedientTipusComprovantPermisSupervisio(expedientTipusId) == null) {
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
						ExtendedPermission.ADMINISTRATION});
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
			
			LlistatIds ids = jbpmHelper.findListTasks(
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
			return ids.getIds();
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
				true,
				false,
				false);
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TascaService.class,
						"llistat"));
		final Timer.Context contextTotal = timerTotal.time();
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
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TascaService.class,
						"llistat.count",
						entorn.getCodi()));
		countEntorn.inc();
		try {
			// Comprova l'accés al tipus d'expedient
			if (expedientTipusId != null) {
				expedientTipusHelper.getExpedientTipusComprovantPermisos(
						expedientTipusId,
						true,
						false,
						false);
			}
			// Si no hi ha tipexp seleccionat o no es te permis SUPERVISION
			// a damunt el tipexp es filtra per l'usuari actual.
			if (expedientTipusId == null || expedientTipusHelper.getExpedientTipusComprovantPermisSupervisio(expedientTipusId) == null) {
				responsable = SecurityContextHolder.getContext().getAuthentication().getName();
			}
			if (tramitacioMassivaTascaId != null) {
				JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
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
			ResultatConsultaPaginadaJbpm<JbpmTask> paginaTasks = jbpmHelper.tascaFindByFiltrePaginat(
					entornId,
					responsable,
					tasca,
					titol,
					null,
					expedient,
					null, //expedientNumero,
					expedientTipusId,
					dataCreacioInici,
					dataCreacioFi,
					prioritat,
					dataLimitInici,
					dataLimitFi,
					mostrarAssignadesUsuari,
					mostrarAssignadesGrup,
					true,
					paginacioParams);
			return paginacioHelper.toPaginaDto(
					paginaTasks.getLlista(),
					paginaTasks.getCount(),
					paginacioParams,
					new Converter<JbpmTask, ExpedientTascaDto>() {
						public ExpedientTascaDto convert(JbpmTask task) {
							return tascaHelper.toExpedientTascaDto(
									task,
									null,
									false,
									true);
						}
					});
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<TascaDadaDto> findDades(
			String id) {
		logger.debug("Consultant dades de la tasca (" +
				"id=" + id + ")");
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
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
			JbpmTask task = jbpmHelper.getTaskById(String.valueOf(id));
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
	public List<TascaDadaDto> findDadesPerTascaDto(ExpedientTascaDto tasca) {
		return variableHelper.findDadesPerInstanciaTascaDto(tasca);
	}

	@Override
	@Transactional(readOnly = true)
	public TascaDocumentDto findDocument(
			String tascaId,
			Long docId) {
		logger.debug("Consultant document de la tasca (" +
				"docId=" + docId + ")");
		return documentHelper.findDocumentPerId(tascaId, docId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TascaDocumentDto> findDocuments(
			String id) {
		logger.debug("Consultant documents de la tasca (" +
				"id=" + id + ")");
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
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
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				id,
				true,
				true);
		Tasca tasca = tascaHelper.findTascaByJbpmTask(task);
		return campTascaRepository.countAmbTasca(
				tasca.getId()) > 0;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean hasDocuments(
			String id) {
		logger.debug("Consultant si la tasca disposa de documents (" +
				"id=" + id + ")");
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				id,
				true,
				true);
		return documentHelper.hasDocumentsPerInstanciaTasca(task);
	}

	public ArxiuDto getArxiuPerDocumentCodi(
			String tascaId,
			String documentCodi) {
		logger.debug("Obtenint contingut de l'arxiu per l'tasca (" +
				"tascaId=" + tascaId + ", " +
				"documentCodi=" + documentCodi + ")");
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
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
			Document document = documentRepository.findByDefinicioProcesAndCodi(
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
	public boolean hasSignatures(
			String id) {
		logger.debug("Consultant si la tasca disposa de signatures (" +
				"id=" + id + ")");
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
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
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
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
		Camp camp = campRepository.findOne(campId);
		Camp registreCamp = null;
		if (registreCampId != null) {
			registreCamp = campRepository.findOne(registreCampId);
		}
		String pidCalculat = processInstanceId;
		if (processInstanceId == null && tascaId != null) {
			JbpmTask task = null;
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
						new NotFoundException(camp.getId(),	Camp.class));
				// Aquest cas no s'hauria de donar. 
				// En cas que es doni, ara per ara, l'unic que feim és no retornar valors.
				return resposta;
			}
			pidCalculat = task.getProcessInstanceId();
		}
		// Consulta els valors possibles
		if (camp.getDomini() != null) {
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
			String id) {
		logger.debug("Agafant tasca (id=" + id + ")");
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				id,
				false,
				true);
//		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		
		// TODO contemplar el cas que no faci falta que l'usuari
		// estigui als pooledActors
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Set<String> pooledActors = task.getPooledActors();
		if (!pooledActors.contains(auth.getName())) {
			logger.debug("L'usuari no s'ha trobat com a pooledActor de la tasca (" +
					"id=" + id + ", " +
					"personaCodi=" + auth.getName() + ")");
			throw new NotFoundException(
					id,
					JbpmTask.class);
		}
		String previousActors = expedientLoggerHelper.getActorsPerReassignacioTasca(id);
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerTasca(
				id,
				ExpedientLogAccioTipus.TASCA_REASSIGNAR,
				previousActors);
		jbpmHelper.takeTaskInstance(id, auth.getName());
		serviceUtils.expedientIndexLuceneUpdate(task.getProcessInstanceId());
		String currentActors = expedientLoggerHelper.getActorsPerReassignacioTasca(id);
		expedientLog.setAccioParams(previousActors + "::" + currentActors);
		ExpedientTascaDto tasca = tascaHelper.toExpedientTascaDto(
				task,
				null,
				false,
				false);
		expedientRegistreHelper.crearRegistreIniciarTasca(
				tasca.getExpedientId(),
				id,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				"Agafar tasca \"" + tasca.getTitol() + "\"");
		return tasca;
	}

	@Override
	@Transactional
	public ExpedientTascaDto alliberar(
			String id) {
		logger.debug("Alliberant tasca (id=" + id + ")");
		// TODO contemplar el cas que no faci falta que l'usuari
		// de la tasca sigui l'usuari actual
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				id,
				false,
				true);
//		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		String previousActors = expedientLoggerHelper.getActorsPerReassignacioTasca(
				id);
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerTasca(
				id,
				ExpedientLogAccioTipus.TASCA_REASSIGNAR,
				previousActors);
		jbpmHelper.releaseTaskInstance(id);
		serviceUtils.expedientIndexLuceneUpdate(task.getProcessInstanceId());
		String currentActors = expedientLoggerHelper.getActorsPerReassignacioTasca(id);
		expedientLog.setAccioParams(previousActors + "::" + currentActors);
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
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
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
			Long expedientId, 
			Long docId, 
			String tascaId,
			String token,
			byte[] signatura) throws Exception {
		boolean signat = false;
		DocumentDto dto = documentHelper.signarDocumentTascaAmbToken(docId, tascaId, token, signatura);
		if (dto != null) {
			expedientRegistreHelper.crearRegistreSignarDocument(
					expedientId,
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
			String user) {
		JbpmTask task = jbpmHelper.getTaskById(taskInstanceId);
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
		Long documentStoreId = documentHelper.actualitzarDocument(
				taskInstanceId,
				task.getProcessInstanceId(),
				documentCodi,
				null,
				documentData,
				arxiuNom,
				arxiuContingut,
				false);
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
			Long expedientId,
			Map<String, Object> variables) {
		logger.debug("Guardant les dades del formulari de la tasca (" +
				"taskId=" + taskId + ", " +
				"variables= "+variables+")");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuari = auth.getName();
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				taskId,
				true,
				true);
		expedientLoggerHelper.afegirLogExpedientPerTasca(
				taskId,
				expedientId,
				ExpedientLogAccioTipus.TASCA_FORM_GUARDAR,
				null,
				usuari);
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProcesJbpmId(
				task.getTaskName(),
				task.getProcessDefinitionId());
		tascaHelper.processarCampsAmbDominiCacheActivat(task, tasca, variables);
		jbpmHelper.startTaskInstance(taskId);
		jbpmHelper.setTaskInstanceVariables(taskId, variables, false);		
		
		if (task.getStartTime() == null) {
			Registre registre = new Registre(
					new Date(),
					expedientId,
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
			Long expedientId,
			Map<String, Object> variables) {
		logger.debug("Validant el formulari de la tasca (" +
				"tascaId=" + tascaId + ", " +
				"variables= "+variables+")");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuari = auth.getName();
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				tascaId,
				true,
				true);
		expedientLoggerHelper.afegirLogExpedientPerTasca(
				tascaId,
				expedientId,
				ExpedientLogAccioTipus.TASCA_FORM_VALIDAR,
				null,
				usuari);
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProcesJbpmId(
				task.getTaskName(),
				task.getProcessDefinitionId());
		tascaHelper.processarCampsAmbDominiCacheActivat(task, tasca, variables);
		jbpmHelper.startTaskInstance(tascaId);
		jbpmHelper.setTaskInstanceVariables(tascaId, variables, false);
		tascaHelper.validarTasca(tascaId);
		
		Registre registre = new Registre(
				new Date(),
				expedientId,
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
			String tascaId,
			Long expedientId) {
		logger.debug("Restaurant el formulari de la tasca (" +
				"tascaId=" + tascaId + ", " +
				"variables=...)");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuari = auth.getName();
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				tascaId,
				true,
				true);
		expedientLoggerHelper.afegirLogExpedientPerTasca(
				tascaId,
				expedientId,
				ExpedientLogAccioTipus.TASCA_FORM_RESTAURAR,
				null,
				usuari);
		tascaHelper.restaurarTasca(tascaId);
		
//		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProcesJbpmId(
//				task.getTaskName(),
//				task.getProcessDefinitionId());
		Registre registre = new Registre(
				new Date(),
				expedientId,
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
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				tascaId,
				true,
				true);
		return tascaHelper.isTascaValidada(task);
	}	

	@Override
	@Transactional(readOnly = true)
	public boolean isDocumentsComplet(String tascaId) {
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				tascaId,
				true,
				true);
		return tascaHelper.isDocumentsComplet(task);
	}	

	@Override
	@Transactional(readOnly = true)
	public boolean isSignaturesComplet(String tascaId) {
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				tascaId,
				true,
				true);
		return tascaHelper.isSignaturesComplet(task);
	}	

	@Override
	@Transactional
	public void completar(
			String tascaId,
			Long expedientId,
			String outcome) {
		logger.debug("Completant la tasca (" +
				"tascaId=" + tascaId + ", " +
				"variables=...)");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuari = auth.getName();
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				tascaId,
				true,
				true);
		if (!tascaHelper.isTascaValidada(task)) {
			throw new IllegalStateException(
					tascaId,
					JbpmTask.class,
					"validada");
		}
		if (!tascaHelper.isDocumentsComplet(task)) {
			throw new IllegalStateException(
					tascaId,
					JbpmTask.class,
					"documents_ok");
		}
		if (!tascaHelper.isSignaturesComplet(task)) {
			throw new IllegalStateException(
					tascaId,
					JbpmTask.class,
					"firmes_ok");
		}
		
		//A partir d'aquí distingirem si la tasca s'ha d'executar en segon pla o no
		Tasca tasca = tascaHelper.findTascaByJbpmTask(task);
		if (tasca.isFinalitzacioSegonPla()) {
			//cridar command per a marcar la tasca per a finalitzar en segón pla
			Date marcadaFinalitzar = new Date();
			jbpmHelper.marcarFinalitzar(tascaId, marcadaFinalitzar, outcome);
			checkFinalitzarSegonPla(tascaId, marcadaFinalitzar);
			
			expedientLoggerHelper.afegirLogExpedientPerTasca(
					tascaId,
					expedientId,
					ExpedientLogAccioTipus.TASCA_MARCAR_FINALITZAR,
					outcome,
					usuari);
		} else {
			completarTasca(
					tascaId,
					expedientId,
					task,
					outcome,
					usuari);
		}
	}
	
	/**
	 * Es comprova si tenim carregades les dades de les tasques en segon
	 * pla en memòria. Si ja tenim la tasca (s'ha provat d'executar algún cop)
	 * s'actualitzaran les dades.
	 * Si no, es crea una entrada al Map per aquesta tasca que s'ha d'executar en segón pla.
	 */
	private void checkFinalitzarSegonPla(String id, Date marcadaFinalitzar) {
		Long taskId = Long.parseLong(id);
		if (!tascaSegonPlaHelper.isTasquesSegonPlaLoaded())
			tascaSegonPlaHelper.loadTasquesSegonPla();
		
		Map<Long,InfoSegonPla> map = tascaSegonPlaHelper.getTasquesSegonPla();
		if (map.containsKey(taskId)) {
			InfoSegonPla infoSegonPla = map.get(taskId);
			infoSegonPla.setMarcadaFinalitzar(marcadaFinalitzar);
			infoSegonPla.setIniciFinalitzacio(null);
			infoSegonPla.setError(null);
			infoSegonPla.setMessages(new ArrayList<String>());
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
//			Long taskId = Long.parseLong(id);
//			TascaSegonPlaHelper.eliminarTasca(taskId);
			tascaSegonPlaHelper.completarTasca(Long.parseLong(id));
		}
	}

	private void completarTasca(
			String tascaId,
			Long expedientId,
			JbpmTask task,
			String outcome,
			String usuari) {
		ProcessInstanceExpedient piexp = jbpmHelper.expedientFindByProcessInstanceId(
				task.getProcessInstanceId());
		Expedient expedient = expedientRepository.findOne(piexp.getId());
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
		try {
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
					JbpmTask taskOriginal = jbpmHelper.getTaskById(
							delegationInfo.getSourceTaskId());
					if (!delegationInfo.isSupervised()) {
						// Si no es supervisada també finalitza la tasca original
						completar(taskOriginal.getId(), expedientId, outcome);
					}
					tascaHelper.deleteDelegationInfo(taskOriginal);
				}
			}
			
			JbpmProcessInstance pi = jbpmHelper.getRootProcessInstance(expedientLog.getExpedient().getProcessInstanceId());		
			actualitzarTerminisIAlertes(tascaId, expedientLog.getExpedient());
			verificarFinalitzacioExpedient(expedientLog.getExpedient(), pi);
			serviceUtils.expedientIndexLuceneUpdate(expedientLog.getExpedient().getProcessInstanceId());
			
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
		} finally {
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
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
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
		jbpmHelper.executeActionInstanciaTasca(tascaId, accio);
		serviceUtils.expedientIndexLuceneUpdate(task.getProcessInstanceId());
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
		FormulariExternDto dto = formulariExternHelper.iniciar(
				tascaId,
				variableHelper.getVariablesJbpmTascaValor(tascaId));
		return dto;
	}
	
	@Override
	@Scheduled(fixedRate = 30000)
	public void comprovarTasquesSegonPla() {
//		//// PROVES NO BBDD ///////////////
//		System.out.println("===> THREAD " + Thread.currentThread().getId() + " AMD DATA " + (new Date()).toString());
//		if(!TascaSegonPlaHelper.isTasquesSegonPlaLoaded()) {
//			TascaSegonPlaHelper.loadTasquesSegonPla();
//			System.out.println("Esperant un minut...");
////			try {
////				Thread.sleep(60000);
////			} catch (InterruptedException e) {
////				e.printStackTrace();
////			}
//			Thread.yield();
//			System.out.println("Fi d'espera...");
//		}
//		///////////////////////////////////
		
		System.out.println("Comprovant si hi ha tasques pendents d'executar en segon pla. (" + Thread.currentThread().getId() + ")");
//		Si encara no hem inicialitzat la variable en memòria ho feim i li carregam les tasques
		tascaSegonPlaHelper.carregaTasquesSegonPla();
		
		if (tascaSegonPlaHelper.isTasquesSegonPlaLoaded() && tascaSegonPlaHelper.getTasquesSegonPla().size() > 0) {
//			for (Map.Entry<Long, InfoSegonPla> entry : TascaSegonPlaHelper.getTasquesSegonPla().entrySet()) {
//			for (Long key : TascaSegonPlaHelper.getTasquesSegonPla().keySet()) {
			Iterator<Map.Entry<Long, InfoSegonPla>> iter = tascaSegonPlaHelper.getTasquesSegonPla().entrySet().iterator();
			while (iter.hasNext()) {
			    Map.Entry<Long, InfoSegonPla> entry = iter.next();
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
						tascaSegonPlaHelper.guardarErrorFinalitzacio(tascaId, infoSegonPla.getError());
						if (ex.getCause() != null) {
							System.out.println(">>> Excepció segon pla: " + ex.getCause().getMessage());
						}
			        }

				} else if (infoSegonPla.getError() == null && infoSegonPla.isCompletada()) {
					try {
						iter.remove();
					} catch (Exception ex) {
						if (ex.getCause() != null) {
							System.out.println(">>> No es pot eliminar l'iteració: " + ex.getCause().getMessage());
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
			List<Object[]> tasquesSegonPlaIds = jbpmHelper.getTasquesSegonPlaPendents();
			tascaSegonPlaHelper.loadTasquesSegonPla();
			if(tasquesSegonPlaIds.size() > 0) {
				for(Object[] taskResult: tasquesSegonPlaIds) {
					tascaSegonPlaHelper.afegirTasca((Long)taskResult[0], (Date)taskResult[1], (Date)taskResult[2], (String)taskResult[3]);
				}
			}
		}
	}
	
	@Override
	@Transactional
	public void completaTascaSegonPla(String tascaId, Date iniciFinalitzacio) {
		JbpmTask task = jbpmHelper.getTaskById(tascaId);
		jbpmHelper.marcarIniciFinalitzacioSegonPla(tascaId, iniciFinalitzacio);
		
		completarTasca(
				tascaId, 
				task.getTask().getProcessInstance().getExpedient().getId(), 
				task, 
				task.getTask().getSelectedOutcome(), 
				task.getTask().getActorId());
	}
	
	@Override
	@Transactional
	public void guardarErrorFinalitzacio(String tascaId, String errorFinalitzacio) {
		jbpmHelper.guardarErrorFinalitzacio(tascaId, errorFinalitzacio);
	}
	
	public FormulariExternDto formulariExternObrirTascaInicial(
			String tascaIniciId,
			Long expedientTipusId,
			Long definicioProcesId) {
		ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId);
		DefinicioProces definicioProces = null;
		if (definicioProcesId != null) {
			definicioProces = definicioProcesRepository.findById(definicioProcesId);
		} else {
			definicioProces = definicioProcesRepository.findDarreraVersioAmbEntornIJbpmKey(
					expedientTipus.getEntorn().getId(), 
					expedientTipus.getJbpmProcessDefinitionKey());
		}
		if (definicioProcesId == null && definicioProces == null) {
			logger.error("No s'ha trobat la definició de procés (entorn=" + expedientTipus.getEntorn().getCodi() + ", jbpmKey=" + expedientTipus.getJbpmProcessDefinitionKey() + ")");
		}
		String starTaskName = jbpmHelper.getStartTaskName(definicioProces.getJbpmId());
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProces(starTaskName, definicioProces);
		FormulariExternDto dto = formulariExternHelper.iniciar(
				tascaIniciId,
				tasca,
				expedientTipus,
				true);
		return dto;
	}

	private void verificarFinalitzacioExpedient(
			Expedient expedient,
			JbpmProcessInstance pi) {
		if (pi.getEnd() != null) {
			// Actualitzar data de fi de l'expedient
			expedient.setDataFi(pi.getEnd());
			// Finalitzar terminis actius
			for (TerminiIniciat terminiIniciat: terminiIniciatRepository.findByProcessInstanceId(pi.getId())) {
				if (terminiIniciat.getDataInici() != null) {
					terminiIniciat.setDataCancelacio(new Date());
					long[] timerIds = terminiIniciat.getTimerIdsArray();
					for (int i = 0; i < timerIds.length; i++)
						jbpmHelper.suspendTimer(
								timerIds[i],
								new Date(Long.MAX_VALUE));
				}
			}
		}
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
				JbpmTask task = jbpmHelper.getTaskById(taskId);
				if (task.getAssignee() != null) {
					crearAlertaCompletat(terminiIniciat, task.getAssignee(), expedient);
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
				terminiIniciat.getTermini().getDefinicioProces().getEntorn());
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

	private static final Logger logger = LoggerFactory.getLogger(TascaServiceImpl.class);

}
