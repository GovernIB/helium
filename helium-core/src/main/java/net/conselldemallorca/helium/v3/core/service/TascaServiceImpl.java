/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.dao.RegistreDao;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
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
import net.conselldemallorca.helium.core.model.service.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.service.PermisosHelper;
import net.conselldemallorca.helium.core.model.service.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.jbpm3.integracio.DelegationInfo;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.jbpm3.integracio.LlistatIds;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.SeleccioOpcioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDocumentDto;
import net.conselldemallorca.helium.v3.core.api.exception.IllegalStateException;
import net.conselldemallorca.helium.v3.core.api.exception.NotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.v3.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.v3.core.helper.EntornHelper;
import net.conselldemallorca.helium.v3.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.v3.core.helper.ExpedientLoggerHelper;
import net.conselldemallorca.helium.v3.core.helper.ExpedientTipusHelper;
import net.conselldemallorca.helium.v3.core.helper.MessageHelper;
import net.conselldemallorca.helium.v3.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.v3.core.helper.ServiceUtils;
import net.conselldemallorca.helium.v3.core.helper.TascaHelper;
import net.conselldemallorca.helium.v3.core.helper.VariableHelper;
import net.conselldemallorca.helium.v3.core.repository.AlertaRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.CampTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioValorsRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientHeliumRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.RegistreRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiIniciatRepository;

import org.apache.commons.collections.comparators.NullComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private CampRepository campRepository;
	@Resource
	private CampTascaRepository campTascaRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private RegistreRepository registreRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
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
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private VariableHelper variableHelper;
	@Resource(name="documentHelperV3")
	private DocumentHelperV3 documentHelper;
	@Resource(name="serviceUtilsV3")
	private ServiceUtils serviceUtils;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private ExpedientLoggerHelper expedientLoggerHelper;
	@Resource
	private TerminiIniciatRepository terminiIniciatRepository;
	@Resource
	private AlertaRepository alertaRepository;
	@Resource
	private RegistreDao registreDao;

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
		return tascaHelper.getExpedientTascaDto(
				task,
				null,
				true);
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
		return tascaHelper.getExpedientTascaDto(
				task,
				null,
				true);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Long> findIdsPerFiltre(
			Long entornId,
			Long expedientTipusId,
			String usuari,
			String titol,
			String tasca,
			String responsable,
			String expedient,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Date dataLimitInici,
			Date dataLimitFi,
			Integer prioritat,
			boolean mostrarTasquesPersonals,
			boolean mostrarTasquesGrup) {
		logger.debug("Consulta de tasques segons filtre (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"usuari=" + usuari + ", " +
				"responsable=" + responsable + ", " +
				"titol=" + titol + ", " +
				"tasca=" + tasca + ", " +
				"dataCreacioInici=" + dataCreacioInici + ", " +
				"dataCreacioFi=" + dataCreacioFi + ", " +
				"dataLimitInici=" + dataLimitInici + ", " +
				"dataLimitFi=" + dataLimitFi + ", " +
				"prioritat=" + prioritat + ", " +
				"mostrarTasquesPersonals=" + mostrarTasquesPersonals + ", " +
				"mostrarTasquesGrup=" + mostrarTasquesGrup + ")");
		// Comprova l'accés a l'entorn
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true,
				false,
				false);
		// Comprova l'accés al tipus d'expedient
		ExpedientTipus expedientTipus = null;
		if (expedientTipusId != null) {
			expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
					expedientTipusId,
					true,
					false,
					false);
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
				mostrarTasquesPersonals,
				mostrarTasquesGrup,
				true);
		return ids.getIds();
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ExpedientTascaDto> findPerFiltrePaginat(
			Long entornId,
			String consultaTramitacioMassivaTascaId,
			Long expedientTipusId,
			String usuari,
			String titol,
			String tasca,
			String responsable,
			String expedient,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Date dataLimitInici,
			Date dataLimitFi,
			Integer prioritat,
			boolean mostrarTasquesPersonals,
			boolean mostrarTasquesGrup,
			final PaginacioParamsDto paginacioParams) throws Exception {
		logger.debug("Consulta de tasques segons filtre (" +
				"entornId=" + entornId + ", " +
				"consultaTramitacioMassivaTascaId=" + consultaTramitacioMassivaTascaId + ", " + 
				"expedientTipusId=" + expedientTipusId + ", " +
				"usuari=" + usuari + ", " +
				"responsable=" + responsable + ", " +
				"titol=" + titol + ", " +
				"tasca=" + tasca + ", " +
				"dataCreacioInici=" + dataCreacioInici + ", " +
				"dataCreacioFi=" + dataCreacioFi + ", " +
				"dataLimitInici=" + dataLimitInici + ", " +
				"dataLimitFi=" + dataLimitFi + ", " +
				"prioritat=" + prioritat + ", " +
				"mostrarTasquesPersonals=" + mostrarTasquesPersonals + ", " +
				"mostrarTasquesGrup=" + mostrarTasquesGrup + ")");
		// Comprova l'accés a l'entorn
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true,
				false,
				false);
		// Comprova l'accés al tipus d'expedient
		ExpedientTipus expedientTipus = null;
		if (expedientTipusId != null) {
			expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
					expedientTipusId,
					true,
					false,
					false);
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
		if (tipusPermesos.isEmpty())
			throw new Exception(messageHelper.getMessage("error.expedientService.noExisteix.tipus"));
		
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA TASQUES LLISTAT", "consulta");
		
		Pageable paginacio = null;
		if (!paginacioParams.getOrdres().isEmpty()) {
			OrdreDto order = paginacioParams.getOrdres().get(0);
			if ("expedientIdentificador".equals(order.getCamp()) || "expedientTipusNom".equals(order.getCamp())) {
				if ("expedientIdentificador".equals(order.getCamp())) {
					order.setCamp("titol");
				} else {
					order.setCamp("tipus.nom");
				}
				paginacio=new PageRequest(0,99999999, order.getDireccio().equals(OrdreDireccioDto.ASCENDENT) ? Direction.ASC : Direction.DESC, order.getCamp());
			}
		}

		List<Long> idsExpedients = expedientHeliumRepository.findListExpedients(
				entorn,
				tipusPermesos,
				(expedientTipus == null),
				expedientTipus,
				(expedient == null),
				expedient,
				paginacio);
		if (consultaTramitacioMassivaTascaId != null) {
			JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
					consultaTramitacioMassivaTascaId,
					true,
					true);
			tasca = task.getName();
		}
		List<ExpedientTascaDto> expedientTasques = new ArrayList<ExpedientTascaDto>();
		
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
				paginacioParams,
				mostrarTasquesPersonals,
				mostrarTasquesGrup,
				true);
		if (consultaTramitacioMassivaTascaId != null) {			
			// Filtra les tasques per mostrar només les del entorn seleccionat
			for (JbpmTask task: jbpmHelper.findPersonalTasks(usuari)) {
				//DadesCacheTasca dadesCacheTasca = dtoConverter.getDadesCacheTasca(task, null);
				if (ids.getIds().contains(Long.parseLong(task.getId())))			
					expedientTasques.add(
							tascaHelper.getExpedientTascaDto(
									task,
									null,
									false));
			}
			ids.setCount(expedientTasques.size());
		}
		return getPaginaExpedientTascaDto(ids, expedientTasques, paginacioParams);
	}

	private PaginaDto<ExpedientTascaDto> getPaginaExpedientTascaDto(final LlistatIds ids, final List<ExpedientTascaDto> expedientTasques, final PaginacioParamsDto paginacioParams) {
		Page<ExpedientTascaDto> paginaResultats = new Page<ExpedientTascaDto>() {
			@Override
			public Iterator<ExpedientTascaDto> iterator() {
				return getContent().iterator();
			}
			@Override
			public boolean isLastPage() {
				return false;
			}
			@Override
			public boolean isFirstPage() {
				return paginacioParams.getPaginaNum() == 0;
			}
			@Override
			public boolean hasPreviousPage() {
				return paginacioParams.getPaginaNum() > 0;
			}
			@Override
			public boolean hasNextPage() {
				return false;
			}
			@Override
			public boolean hasContent() {
				return !ids.getIds().isEmpty();
			}
			@Override
			public int getTotalPages() {
				return 0;
			}
			@Override
			public long getTotalElements() {
				return ids.getCount();
			}
			@Override
			public Sort getSort() {
				List<Order> orders = new ArrayList<Order>();
				for (OrdreDto or : paginacioParams.getOrdres()) {
					orders.add(new Order(or.getDireccio().equals(OrdreDireccioDto.ASCENDENT) ? Direction.ASC : Direction.DESC, or.getCamp()));
				}
				return new Sort(orders);
			}
			@Override
			public int getSize() {
				return Math.max(paginacioParams.getPaginaTamany(),ids.getCount());
			}
			@Override
			public int getNumberOfElements() {
				return 0;
			}
			@Override
			public int getNumber() {
				return 0;
			}
			@Override
			public List<ExpedientTascaDto> getContent() {
				List<ExpedientTascaDto> tasques = expedientTasques;
				if (tasques.isEmpty()) {
					for (JbpmTask tasca : jbpmHelper.findTasks(ids.getIds())) {
						tasques.add(
								tascaHelper.getExpedientTascaDto(
										tasca,
										null,
										false));
					}
				}
				Collections.sort(tasques, comparador);
				return tasques;
			}
			Comparator<ExpedientTascaDto> comparador = new Comparator<ExpedientTascaDto>() {				
				public int compare(ExpedientTascaDto t1, ExpedientTascaDto t2) {				
					String finalSort = null;
					boolean finalAsc = false;
					for (OrdreDto or : paginacioParams.getOrdres()) {
						finalAsc = or.getDireccio().equals(OrdreDireccioDto.ASCENDENT);
						finalSort = or.getCamp();
						break;
					}
					int result = 0;
					NullComparator nullComparator = new NullComparator();
					if ("titol".equals(finalSort)) {
						if (finalAsc)
							result = nullComparator.compare(t1.getExpedientIdentificador(), t2.getExpedientIdentificador());
						else
							result = nullComparator.compare(t2.getExpedientIdentificador(), t1.getExpedientIdentificador());
					} else if ("tipus.nom".equals(finalSort)) {
						if (finalAsc)
							result = nullComparator.compare(t1.getExpedientTipusNom(), t2.getExpedientTipusNom());
						else
							result = nullComparator.compare(t2.getExpedientTipusNom(), t1.getExpedientTipusNom());
					} else if ("createTime".equals(finalSort)) {
						if (finalAsc)
							result = nullComparator.compare(t1.getCreateTime(), t2.getCreateTime());
						else
							result = nullComparator.compare(t2.getCreateTime(), t1.getCreateTime());
					} else if ("prioritat".equals(finalSort)) {
						if (finalAsc)
							result = t1.getPriority() - t2.getPriority();
						else
							result = t2.getPriority() - t1.getPriority();
					} else if ("dueDate".equals(finalSort)) {
						if (finalAsc)
							result = nullComparator.compare(t1.getDueDate(), t2.getDueDate());
						else
							result = nullComparator.compare(t2.getDueDate(), t1.getDueDate());
					}
					return result;
				}
			};
		};
		PaginaDto<ExpedientTascaDto> resposta = paginacioHelper.toPaginaDto(
				paginaResultats,
				ExpedientTascaDto.class);
		return resposta;
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
	public List<ExpedientTascaDto> findDadesPerIds(Set<Long> ids) {
		logger.debug("Consultant expedients de las tascas (" +
				"ids=" + ids + ")");

		List<ExpedientTascaDto> expedientTasques = new ArrayList<ExpedientTascaDto>();
		for (Long id : ids) {
			JbpmTask task = jbpmHelper.getTaskById(String.valueOf(id));
			expedientTasques.add(tascaHelper.getExpedientTascaDto(
					task,
					null,
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

	public ArxiuDto getArxiuPerDocumentIdCodi(
			String tascaId,
			Long documentId,
			String documentCodi) {
		logger.debug("btenint contingut de l'arxiu per l'tasca (" +
				"tascaId=" + tascaId + ", " +
				"documentCodi=" + documentCodi + ", " +
				"documentId=" + documentId + ")");
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				tascaId,
				true,
				true);
		DocumentStore documentStore = documentHelper.getDocumentStore(task, documentCodi);
		if (documentStore != null) {
			return documentHelper.getArxiuPerDocumentStoreId(
					documentStore.getId(),
					false,
					false,
					false);
		}
		return null;
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
			String id,
			String processInstanceId,
			Long campId,
			String codiFiltre,
			String textFiltre,
			Map<String, Object> valorsFormulari) {
		logger.debug("Consultant llista de valors per camp selecció (" +
				"id=" + id + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"campId=" + campId + ", " +
				"codiFiltre=" + codiFiltre + ", " +
				"textFiltre=" + textFiltre + ", " +
				"valorsFormulari=...)");
		Camp camp = campRepository.findOne(campId);
		String pidCalculat = processInstanceId;
		if (processInstanceId == null && id != null) {
			JbpmTask task = null;
			task = tascaHelper.getTascaComprovacionsTramitacio(
					id,
					true,
					true);
			// Comprova si el camp pertany a la tasca
			Tasca tasca = tascaHelper.findTascaByJbpmTask(task);
			boolean trobat = false;
			for (CampTasca campTasca: tasca.getCamps()) {
				if (campTasca.getCamp().equals(camp)) {
					trobat = true;
					break;
				}
			}
			if (!trobat) {
				throw new NotFoundException(
						camp.getId(),
						Camp.class);
			}
			pidCalculat = task.getProcessInstanceId();
		}
		// Consulta els valors possibles
		List<SeleccioOpcioDto> resposta = new ArrayList<SeleccioOpcioDto>();
		if (camp.getDominiId() != null) {
			List<ParellaCodiValorDto> parellaCodiValorDto = variableHelper.getPossiblesValorsCamp(
						camp,
						null,
						valorsFormulari,
						id,
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
		String previousActors = expedientLoggerHelper.getActorsPerReassignacioTasca(
				id);
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerTasca(
				id,
				ExpedientLogAccioTipus.TASCA_REASSIGNAR,
				previousActors);
		jbpmHelper.takeTaskInstance(id, auth.getName());
		serviceUtils.expedientIndexLuceneUpdate(task.getProcessInstanceId());
		String currentActors = expedientLoggerHelper.getActorsPerReassignacioTasca(
				id);
		expedientLog.setAccioParams(previousActors + "::" + currentActors);
		ExpedientTascaDto tasca = tascaHelper.getExpedientTascaDto(
				task,
				null,
				false);
		registreDao.crearRegistreIniciarTasca(
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
				true,
				true);
		String previousActors = expedientLoggerHelper.getActorsPerReassignacioTasca(
				id);
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerTasca(
				id,
				ExpedientLogAccioTipus.TASCA_REASSIGNAR,
				previousActors);
		jbpmHelper.releaseTaskInstance(id);
		serviceUtils.expedientIndexLuceneUpdate(task.getProcessInstanceId());
		String currentActors = expedientLoggerHelper.getActorsPerReassignacioTasca(
				id);
		expedientLog.setAccioParams(previousActors + "::" + currentActors);
		ExpedientTascaDto tasca = tascaHelper.getExpedientTascaDto(
				task,
				null,
				false);
		registreDao.crearRegistreIniciarTasca(
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
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		if (taskInstanceId != null) {
			registreDao.crearRegistreEsborrarDocumentTasca(
					expedient.getId(),
					taskInstanceId,
					user,
					documentCodi);
		} else {
			registreDao.crearRegistreEsborrarDocumentInstanciaProces(
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
		net.conselldemallorca.helium.v3.core.api.dto.DocumentDto dto = documentHelper.signarDocumentTascaAmbToken(docId, tascaId, token, signatura);
		if (dto != null) {
			registreDao.crearRegistreSignarDocument(
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
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		if (creat) {
			registreDao.crearRegistreCrearDocumentTasca(
					expedient.getId(),
					taskInstanceId,
					user,
					documentCodi,
					arxiuNom);
		} else {
			registreDao.crearRegistreModificarDocumentTasca(
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
	public ExpedientTascaDto guardar(
			String id,
			Map<String, Object> variables) {
		logger.debug("Guardant les dades del formulari de la tasca (" +
				"id=" + id + ", " +
				"variables=...)");
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				id,
				true,
				true);
		ExpedientTascaDto tasca = tascaHelper.guardarVariables(
				task,
				variables);
		return tasca;
	}

	@Override
	@Transactional
	public ExpedientTascaDto validar(
			String id,
			Map<String, Object> variables) {
		logger.debug("Validant el formulari de la tasca (" +
				"id=" + id + ", " +
				"variables=...)");
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				id,
				true,
				true);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuari = auth.getName();
		expedientLoggerHelper.afegirLogExpedientPerTasca(
				id,
				ExpedientLogAccioTipus.TASCA_FORM_VALIDAR,
				null,
				usuari);
		tascaHelper.processarCampsAmbDominiCacheActivat(task, variables);
		jbpmHelper.startTaskInstance(id);
		jbpmHelper.setTaskInstanceVariables(id, variables, false);
		tascaHelper.validarTasca(id);
		ExpedientTascaDto tasca = tascaHelper.getExpedientTascaDto(
				task,
				null,
				false);
		Registre registre = new Registre(
				new Date(),
				tasca.getExpedientId(),
				usuari,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.TASCA,
				id);
		registre.setMissatge("Validar \"" + tasca.getTitol() + "\"");
		registreRepository.save(registre);
		return tasca;
	}

	@Override
	@Transactional
	public ExpedientTascaDto restaurar(
			String id) {
		logger.debug("Restaurant el formulari de la tasca (" +
				"id=" + id + ", " +
				"variables=...)");
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				id,
				true,
				true);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuari = auth.getName();
		expedientLoggerHelper.afegirLogExpedientPerTasca(
				id,
				ExpedientLogAccioTipus.TASCA_FORM_RESTAURAR,
				null,
				usuari);
		if (!tascaHelper.isTascaValidada(task)) {
			throw new IllegalStateException(
					id,
					JbpmTask.class,
					"validada");
		}
		tascaHelper.restaurarTasca(id);
		ExpedientTascaDto tasca = tascaHelper.getExpedientTascaDto(
				task,
				null,
				false);
		Registre registre = new Registre(
				new Date(),
				tasca.getExpedientId(),
				usuari,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.TASCA,
				id);
		registre.setMissatge("Restaurar \"" + tasca.getTitol() + "\"");
		registreRepository.save(registre);
		return tasca;
	}

	@Override
	@Transactional
	public void completar(
			String id,
			String outcome) {
		logger.debug("Completant la tasca (" +
				"id=" + id + ", " +
				"variables=...)");
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				id,
				true,
				true);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuari = auth.getName();
		if (!tascaHelper.isTascaValidada(task)) {
			throw new IllegalStateException(
					id,
					JbpmTask.class,
					"validada");
		}
		if (!tascaHelper.isDocumentsComplet(task)) {
			throw new IllegalStateException(
					id,
					JbpmTask.class,
					"documents_ok");
		}
		if (!tascaHelper.isSignaturesComplet(task)) {
			throw new IllegalStateException(
					id,
					JbpmTask.class,
					"firmes_ok");
		}
		JbpmProcessInstance pi = jbpmHelper.getRootProcessInstance(task.getProcessInstanceId());
		expedientLoggerHelper.afegirLogExpedientPerTasca(
				id,
				ExpedientLogAccioTipus.TASCA_COMPLETAR,
				outcome,
				usuari);
		jbpmHelper.startTaskInstance(id);
		jbpmHelper.endTaskInstance(id, outcome);
		// Accions per a una tasca delegada
		DelegationInfo delegationInfo = tascaHelper.getDelegationInfo(task);
		if (delegationInfo != null) {
			if (!id.equals(delegationInfo.getSourceTaskId())) {
				// Copia les variables de la tasca delegada a la original
				jbpmHelper.setTaskInstanceVariables(
						delegationInfo.getSourceTaskId(),
						jbpmHelper.getTaskInstanceVariables(task.getId()),
						false);
				JbpmTask taskOriginal = jbpmHelper.getTaskById(
						delegationInfo.getSourceTaskId());
				if (!delegationInfo.isSupervised()) {
					// Si no es supervisada també finalitza la tasca original
					completar(taskOriginal.getId(), outcome);
				}
				tascaHelper.deleteDelegationInfo(taskOriginal);
			}
		}
		List<Expedient> expedients = expedientRepository.findByProcessInstanceId(
				pi.getId());
		for (Expedient expedient : expedients) {
			actualitzarTerminisIAlertes(id, expedient);
			verificarFinalitzacioExpedient(expedient, pi);
		}
		serviceUtils.expedientIndexLuceneUpdate(task.getProcessInstanceId());
		ExpedientTascaDto tasca = tascaHelper.getExpedientTascaDto(
				task,
				null,
				false);
		Registre registre = new Registre(
				new Date(),
				tasca.getExpedientId(),
				usuari,
				Registre.Accio.FINALITZAR,
				Registre.Entitat.TASCA,
				id);
		registre.setMissatge("Finalitzar \"" + tasca.getTitol() + "\"");
		registreRepository.save(registre);
	}

	@Override
	@Transactional
	public void executarAccio(
			String id,
			String accio) {
		logger.debug("Executant acció de la tasca (" +
				"id=" + id + ", " +
				"accio=" + accio + ")");
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				id,
				true,
				true);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuari = auth.getName();
		expedientLoggerHelper.afegirLogExpedientPerTasca(
				id,
				ExpedientLogAccioTipus.TASCA_ACCIO_EXECUTAR,
				accio,
				usuari);
		jbpmHelper.executeActionInstanciaTasca(id, accio);
		serviceUtils.expedientIndexLuceneUpdate(task.getProcessInstanceId());
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
