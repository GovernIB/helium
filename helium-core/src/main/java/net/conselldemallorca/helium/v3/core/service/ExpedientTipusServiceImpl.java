/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.core.helper.EntornHelper;
import net.conselldemallorca.helium.core.helper.ExpedientTipusHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.helper.PermisosHelper;
import net.conselldemallorca.helium.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.CampAgrupacio;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.SequenciaAny;
import net.conselldemallorca.helium.core.model.hibernate.Validacio;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisDto;
import net.conselldemallorca.helium.v3.core.api.dto.ValidacioDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.v3.core.repository.CampAgrupacioRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.CampValidacioRepository;
import net.conselldemallorca.helium.v3.core.repository.ConsultaRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.DominiRepository;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.SequenciaAnyRepository;

/**
 * Implementació del servei per a gestionar tipus d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExpedientTipusServiceImpl implements ExpedientTipusService {

	@Resource
	private EntornHelper entornHelper;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private SequenciaAnyRepository sequenciaRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private CampAgrupacioRepository campAgrupacioRepository;
	@Resource
	private CampValidacioRepository campValidacioRepository;
	@Resource
	private EnumeracioRepository enumeracioRepository;
	@Resource
	private DominiRepository dominiRepository;
	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private ConsultaRepository consultaRepository;
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource(name = "permisosHelperV3") 
	private PermisosHelper permisosHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource(name="documentHelperV3")
	private DocumentHelperV3 documentHelper;


	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ExpedientTipusDto create(
			Long entornId,
			ExpedientTipusDto expedientTipus,
			List<Integer> sequenciesAny, 
			List<Long> sequenciesValor) {
		logger.debug(
				"Creant nou tipus d'expedient (" +
				"entornId=" + entornId + ", " +
				"expedientTipus=" + expedientTipus + ", " +
				"sequenciesAny=" + sequenciesAny + ", " +
				"sequenciesValor=" + sequenciesValor + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true,
				true);
		ExpedientTipus entity = new ExpedientTipus();
		entity.setEntorn(entorn);
		entity.setCodi(expedientTipus.getCodi());
		entity.setNom(expedientTipus.getNom());
		entity.setTeTitol(expedientTipus.isTeTitol());
		entity.setDemanaTitol(expedientTipus.isDemanaTitol());
		entity.setTeNumero(expedientTipus.isTeNumero());
		entity.setDemanaNumero(expedientTipus.isDemanaNumero());
		entity.setExpressioNumero(expedientTipus.getExpressioNumero());
		entity.setReiniciarCadaAny(expedientTipus.isReiniciarCadaAny());
		entity.setSequencia(expedientTipus.getSequencia());
		entity.setResponsableDefecteCodi(expedientTipus.getResponsableDefecteCodi());
		entity.setRestringirPerGrup(expedientTipus.isRestringirPerGrup());
		entity.setSeleccionarAny(expedientTipus.isSeleccionarAny());
		entity.setAmbRetroaccio(expedientTipus.isAmbRetroaccio());
		entity.setReindexacioAsincrona(expedientTipus.isReindexacioAsincrona());
		if (expedientTipus.isReiniciarCadaAny()) {
			for (int i = 0; i < sequenciesAny.size(); i++) {
				SequenciaAny anyEntity = new SequenciaAny(
						entity, 
						sequenciesAny.get(i), 
						sequenciesValor.get(i));
				entity.getSequenciaAny().put(anyEntity.getAny(), anyEntity);
			}
		}
		return conversioTipusHelper.convertir(
				expedientTipusRepository.save(entity),
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ExpedientTipusDto update(
			Long entornId,
			ExpedientTipusDto expedientTipus,
			List<Integer> sequenciesAny,
			List<Long> sequenciesValor) {
		logger.debug(
				"Modificant tipus d'expedient existent (" +
				"entornId=" + entornId + ", " +
				"expedientTipus=" + expedientTipus + ", " +
				"sequenciesAny=" + sequenciesAny + ", " +
				"sequenciesValor=" + sequenciesValor + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		expedientTipusHelper.comprovarPermisDissenyEntornITipusExpedient(
				entornId,
				expedientTipus.getId());
		ExpedientTipus entity = expedientTipusRepository.findByEntornAndId(
				entorn,
				expedientTipus.getId());
		entity.setNom(expedientTipus.getNom());
		entity.setTeTitol(expedientTipus.isTeTitol());
		entity.setDemanaTitol(expedientTipus.isDemanaTitol());
		entity.setTeNumero(expedientTipus.isTeNumero());
		entity.setDemanaNumero(expedientTipus.isDemanaNumero());
		entity.setExpressioNumero(expedientTipus.getExpressioNumero());
		entity.setReiniciarCadaAny(expedientTipus.isReiniciarCadaAny());
		entity.setSequencia(expedientTipus.getSequencia());
		entity.setResponsableDefecteCodi(expedientTipus.getResponsableDefecteCodi());
		entity.setRestringirPerGrup(expedientTipus.isRestringirPerGrup());
		entity.setSeleccionarAny(expedientTipus.isSeleccionarAny());
		while (entity.getSequenciaAny().size() > 0) {
			SequenciaAny s = entity.getSequenciaAny().get(entity.getSequenciaAny().firstKey());			
			entity.getSequenciaAny().remove(s.getAny());
			sequenciaRepository.delete(s);
		}
		if (expedientTipus.isReiniciarCadaAny()) {
			for (int i = 0; i < sequenciesAny.size(); i++) {
				SequenciaAny sequenciaEntity = new SequenciaAny(
						entity,
						sequenciesAny.get(i),
						sequenciesValor.get(i));
				entity.getSequenciaAny().put(sequenciaEntity.getAny(), sequenciaEntity);
				sequenciaRepository.save(sequenciaEntity);
			}
		}
		// Només poden configurar la retroacció els dissenyadors de l'entorn
		if (entornHelper.potDissenyarEntorn(entornId)) {
			entity.setAmbRetroaccio(expedientTipus.isAmbRetroaccio());
			entity.setReindexacioAsincrona(expedientTipus.isReindexacioAsincrona());
		}
		return conversioTipusHelper.convertir(
				expedientTipusRepository.save(entity),
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void delete(
			Long entornId,
			Long expedientTipusId) {
		logger.debug(
				"Esborrant el tipus d'expedient (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true,
				true);
		ExpedientTipus entity = expedientTipusRepository.findByEntornAndId(
				entorn,
				expedientTipusId);
		expedientTipusRepository.delete(entity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public List<ExpedientTipusDto> findAmbEntornPermisConsultar(
			Long entornId) {
		logger.debug(
				"Consultant tipus d'expedient per un entorn i amb permisos de consulta (" +
				"entornId=" + entornId + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		List<ExpedientTipus> tipuss = expedientTipusRepository.findByEntorn(entorn);
		if (!entornHelper.potDissenyarEntorn(entornId)) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			permisosHelper.filterGrantedAny(
					tipuss,
					new ObjectIdentifierExtractor<ExpedientTipus>() {
						@Override
						public Long getObjectIdentifier(ExpedientTipus expedientTipus) {
							return expedientTipus.getId();
						}
					},
					ExpedientTipus.class,
					new Permission[] {
							ExtendedPermission.DESIGN_ADMIN,
							ExtendedPermission.DESIGN_DELEG,
							ExtendedPermission.ADMINISTRATION},
					auth);
		}
		return conversioTipusHelper.convertirList(
				tipuss,
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientTipusDto findAmbIdPermisConsultar(
			Long entornId,
			Long expedientTipusId) {
		logger.debug(
				"Consultant tipus d'expedient amb id i amb permisos de consulta (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId = " + expedientTipusId + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		ExpedientTipus tipus;
		if (entornHelper.potDissenyarEntorn(entornId)) {
			// Si te permisos de disseny a damunt l'entorn pot veure tots els tipus
			tipus = expedientTipusRepository.findByEntornAndId(
					entorn,
					expedientTipusId);
		} else {
			// Si no te permisos de disseny a damunt l'entorn només es poden veure
			// els tipus amb permisos de disseny
			tipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
					expedientTipusId,
					false,
					false,
					false,
					false,
					true);
		}
		return conversioTipusHelper.convertir(
				tipus,
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public List<ExpedientTipusDto> findAmbEntornPermisDissenyar(
			Long entornId) {
		logger.debug(
				"Consultant tipus d'expedient per un entorn i amb permisos de disseny (" +
				"entornId=" + entornId + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		List<ExpedientTipus> tipuss = expedientTipusRepository.findByEntorn(entorn);
		if (!entornHelper.potDissenyarEntorn(entornId)) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			permisosHelper.filterGrantedAny(
					tipuss,
					new ObjectIdentifierExtractor<ExpedientTipus>() {
						@Override
						public Long getObjectIdentifier(ExpedientTipus expedientTipus) {
							return expedientTipus.getId();
						}
					},
					ExpedientTipus.class,
					new Permission[] {
							ExtendedPermission.DESIGN_ADMIN,
							ExtendedPermission.DESIGN_DELEG,
							ExtendedPermission.ADMINISTRATION},
					auth);
		}
		return conversioTipusHelper.convertirList(
				tipuss,
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientTipusDto findAmbIdPermisDissenyar(
			Long entornId,
			Long expedientTipusId) {
		logger.debug(
				"Consultant tipus d'expedient amb id i amb permisos de disseny (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId = " + expedientTipusId + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		ExpedientTipus tipus;
		if (entornHelper.potDissenyarEntorn(entornId)) {
			// Si te permisos de disseny a damunt l'entorn pot veure tots els tipus
			tipus = expedientTipusRepository.findByEntornAndId(
					entorn,
					expedientTipusId);
		} else {
			// Si no te permisos de disseny a damunt l'entorn només es poden veure
			// els tipus amb permisos de disseny
			tipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
					expedientTipusId,
					false,
					false,
					false,
					false,
					true);
		}
		return conversioTipusHelper.convertir(
				tipus,
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public List<ExpedientTipusDto> findAmbEntornPermisCrear(
			Long entornId) {
		logger.debug(
				"Consultant tipus d'expedient per un entorn i amb permis de creació (" +
				"entornId=" + entornId + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		List<ExpedientTipus> tipuss = expedientTipusRepository.findByEntorn(entorn);
		if (!entornHelper.potDissenyarEntorn(entornId)) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			permisosHelper.filterGrantedAny(
					tipuss,
					new ObjectIdentifierExtractor<ExpedientTipus>() {
						@Override
						public Long getObjectIdentifier(ExpedientTipus expedientTipus) {
							return expedientTipus.getId();
						}
					},
					ExpedientTipus.class,
					new Permission[] {
							ExtendedPermission.CREATE,
							ExtendedPermission.ADMINISTRATION},
					auth);
		}
		return conversioTipusHelper.convertirList(
				tipuss,
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientTipusDto findAmbIdPermisCrear(
			Long entornId,
			Long expedientTipusId) {
		logger.debug(
				"Consultant tipus d'expedient amb id i amb permis de creació (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId = " + expedientTipusId + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		ExpedientTipus tipus;
		if (entornHelper.potDissenyarEntorn(entornId)) {
			// Si te permisos de disseny a damunt l'entorn pot veure tots els tipus
			tipus = expedientTipusRepository.findByEntornAndId(
					entorn,
					expedientTipusId);
		} else {
			// Si no te permisos de disseny a damunt l'entorn només es poden veure
			// els tipus amb permisos de disseny
			tipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
					expedientTipusId,
					false,
					false,
					true,
					false,
					false);
		}
		return conversioTipusHelper.convertir(
				tipus,
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientTipusDto findAmbCodiPerValidarRepeticio(
			Long entornId,
			String codi) {
		logger.debug(
				"Consultant tipus d'expedient amb codi (" +
				"entornId=" + entornId + ", " +
				"codi = " + codi + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		return conversioTipusHelper.convertir(
				expedientTipusRepository.findByEntornAndCodi(entorn, codi),
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ExpedientTipusDto> findPerDatatable(
			Long entornId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant tipus d'expedient per datatable (" +
				"entornId=" + entornId + ", " +
				"filtre=" + filtre + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		List<Long> tipusPermesosIds = expedientTipusHelper.findIdsAmbEntorn(entorn);
		// Filtra els expedients deixant només els permesos
		if (!entornHelper.potDissenyarEntorn(entornId)) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			Iterator<Long> it = tipusPermesosIds.iterator();
			while (it.hasNext()) {
				Long id = it.next();
				if (!permisosHelper.isGrantedAny(
						id,
						ExpedientTipus.class,
						new Permission[] {
								ExtendedPermission.DESIGN,
								ExtendedPermission.MANAGE,
								ExtendedPermission.ADMINISTRATION},
						auth)) {
					it.remove();
				}
			}
		}
		PaginaDto<ExpedientTipusDto> pagina = paginacioHelper.toPaginaDto(
				expedientTipusRepository.findByFiltreGeneralPaginat(
						entorn, 
						tipusPermesosIds,
						filtre == null || "".equals(filtre),
						filtre,
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				ExpedientTipusDto.class);
		// Afegeix el contador de permisos
		List<Long> ids = new ArrayList<Long>();
		for (ExpedientTipusDto dto: pagina.getContingut()) {
			ids.add(dto.getId());
		}
		Map<Long, List<PermisDto>> permisos = permisosHelper.findPermisos(
				ids,
				ExpedientTipus.class);
		for (ExpedientTipusDto dto: pagina.getContingut()) {
			if (permisos.get(dto.getId()) != null) {
				dto.setPermisCount(permisos.get(dto.getId()).size());
			}
		}
		return pagina;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void permisUpdate(
			Long entornId,
			Long expedientTipusId,
			PermisDto permis) {
		logger.debug(
				"Creant permis per al tipus d'expedient (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"permis=" + permis + ")");
		entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		expedientTipusHelper.comprovarPermisDissenyEntornITipusExpedient(
				entornId,
				expedientTipusId);
		permisosHelper.updatePermis(
				expedientTipusId,
				ExpedientTipus.class,
				permis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void permisDelete(
			Long entornId,
			Long expedientTipusId,
			Long permisId) {
		logger.debug(
				"Esborrant permis per al tipus d'expedient (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"permisId=" + permisId + ")");
		entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		expedientTipusHelper.comprovarPermisDissenyEntornITipusExpedient(
				entornId,
				expedientTipusId);
		permisosHelper.deletePermis(
				expedientTipusId,
				ExpedientTipus.class,
				permisId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PermisDto> permisFindAll(
			Long entornId,
			Long expedientTipusId) {
		logger.debug(
				"Consultant permisos del tipus d'expedient (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ")");
		entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		expedientTipusHelper.comprovarPermisDissenyEntornITipusExpedient(
				entornId,
				expedientTipusId);
		return permisosHelper.findPermisos(
				expedientTipusId,
				ExpedientTipus.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PermisDto permisFindById(
			Long entornId,
			Long expedientTipusId,
			Long permisId) {
		logger.debug(
				"Consultant un permis donat el seu id (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"permisId=" + permisId + ")");
		entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		expedientTipusHelper.comprovarPermisDissenyEntornITipusExpedient(
				entornId,
				expedientTipusId);
		List<PermisDto> permisos = permisosHelper.findPermisos(
				expedientTipusId,
				ExpedientTipus.class);
		for (PermisDto permis: permisos) {
			if (permis.getId().equals(permisId)) {
				return permis;
			}
		}
		throw new NoTrobatException(PermisDto.class, permisId);
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientServiceImpl.class);


	// MANTENIMENT DE CAMPS
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public CampDto campCreate(
			Long expedientTipusId, 
			CampDto camp) throws PermisDenegatException {

		logger.debug(
				"Creant nou camp per un tipus d'expedient (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"camp=" + camp + ")");
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		
		Camp entity = new Camp();
		entity.setCodi(camp.getCodi());
		entity.setTipus(conversioTipusHelper.convertir(camp.getTipus(), Camp.TipusCamp.class));
		entity.setEtiqueta(camp.getEtiqueta());
		entity.setObservacions(camp.getObservacions());
		entity.setMultiple(camp.isMultiple());
		entity.setOcult(camp.isOcult());
		entity.setIgnored(camp.isIgnored());
		CampAgrupacio agrupacio = null;
		if (camp.getAgrupacio() != null) 
			agrupacio = campAgrupacioRepository.findOne(camp.getAgrupacio().getId());
		entity.setAgrupacio(agrupacio);
		if (agrupacio != null && entity.getOrdre() == null) {
			// Informa de l'ordre dins de la agrupació
			entity.setOrdre(
					campRepository.getNextOrdre(
							agrupacio.getId()));
		}		
		// Camp associat a l'expedient
		entity.setExpedientTipus(expedientTipus);		
		
		// Dades consulta
		Enumeracio enumeracio = null;
		if (camp.getEnumeracio() != null) {
			enumeracio = enumeracioRepository.findOne(camp.getEnumeracio().getId());
		}
		entity.setEnumeracio(enumeracio);
		Domini domini = null;
		if (camp.getDomini() != null) {
			domini = dominiRepository.findOne(camp.getDomini().getId());
		}
		entity.setDomini(domini);
		Consulta consulta = null;
		if (camp.getConsulta() != null) {
			consulta = consultaRepository.findOne(camp.getConsulta().getId());
		}
		entity.setConsulta(consulta);		
		entity.setDominiIntern(camp.isDominiIntern());

		// Paràmetres del domini
		entity.setDominiId(camp.getDominiIdentificador());
		entity.setDominiParams(camp.getDominiParams());
		entity.setDominiCampValor(camp.getDominiCampValor());
		entity.setDominiCampText(camp.getDominiCampText());
		
		// Paràmetres de la consulta
		entity.setConsultaParams(camp.getConsultaParams());
		entity.setConsultaCampValor(camp.getConsultaCampValor());
		entity.setConsultaCampText(camp.getConsultaCampText());
		
		entity.setDominiCacheText(camp.isDominiCacheText());

		return conversioTipusHelper.convertir(
				campRepository.save(entity),
				CampDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public CampDto campUpdate(CampDto camp) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Modificant el camp del tipus d'expedient existent (" +
				"camp.id=" + camp.getId() + ", " +
				"camp =" + camp + ")");
		Camp entity = campRepository.findOne(camp.getId());
		entity.setCodi(camp.getCodi());
		entity.setTipus(conversioTipusHelper.convertir(camp.getTipus(), Camp.TipusCamp.class));
		entity.setEtiqueta(camp.getEtiqueta());
		entity.setObservacions(camp.getObservacions());
		entity.setMultiple(camp.isMultiple());
		entity.setOcult(camp.isOcult());
		entity.setIgnored(camp.isIgnored());
		CampAgrupacio agrupacio = null;
		if (camp.getAgrupacio() != null) 
			agrupacio = campAgrupacioRepository.findOne(camp.getAgrupacio().getId());
		entity.setAgrupacio(agrupacio);
		if (agrupacio != null && entity.getOrdre() == null) {
			// Informa de l'ordre dins de la agrupació
			entity.setOrdre(
					campRepository.getNextOrdre(
							agrupacio.getId()));
		}		
		
		// Dades consulta
		Enumeracio enumeracio = null;
		if (camp.getEnumeracio() != null) {
			enumeracio = enumeracioRepository.findOne(camp.getEnumeracio().getId());
		}
		entity.setEnumeracio(enumeracio);
		Domini domini = null;
		if (camp.getDomini() != null) {
			domini = dominiRepository.findOne(camp.getDomini().getId());
		}
		entity.setDomini(domini);
		Consulta consulta = null;
		if (camp.getConsulta() != null) {
			consulta = consultaRepository.findOne(camp.getConsulta().getId());
		}
		entity.setConsulta(consulta);		
		entity.setDominiIntern(camp.isDominiIntern());

		// Paràmetres del domini
		entity.setDominiId(camp.getDominiIdentificador());
		entity.setDominiParams(camp.getDominiParams());
		entity.setDominiCampValor(camp.getDominiCampValor());
		entity.setDominiCampText(camp.getDominiCampText());
		
		// Paràmetres de la consulta
		entity.setConsultaParams(camp.getConsultaParams());
		entity.setConsultaCampValor(camp.getConsultaCampValor());
		entity.setConsultaCampText(camp.getConsultaCampText());
		
		entity.setDominiCacheText(camp.isDominiCacheText());
		
		return conversioTipusHelper.convertir(
				campRepository.save(entity),
				CampDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void campDelete(Long campCampId) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Esborrant el camp del tipus d'expedient (" +
				"campId=" + campCampId +  ")");
		Camp entity = campRepository.findOne(campCampId);

		if (entity != null) {
			for (CampTasca campTasca: entity.getCampsTasca()) {
				campTasca.getTasca().removeCamp(campTasca);
				int i = 0;
				for (CampTasca ct: campTasca.getTasca().getCamps())
					ct.setOrder(i++);
			}
			if (entity.getAgrupacio() != null)
				reordenarCamps(entity.getAgrupacio().getId());
		}
		campRepository.delete(entity);	
	}

	/** Funció per reasignar el valor d'ordre dins d'una agrupació de variables. */
	private void reordenarCamps(Long agrupacioId) {
		List<Camp> camps = campRepository.findByAgrupacioIdOrderByOrdreAsc(agrupacioId);		
		int i = 0;
		for (Camp camp: camps)
			camp.setOrdre(i++);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public CampDto campFindAmbId(Long id) throws NoTrobatException {
		logger.debug(
				"Consultant el camp del tipus d'expedient amb id (" +
				"campId=" + id +  ")");

		return conversioTipusHelper.convertir(
				campRepository.findOne(id),
				CampDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CampDto campFindAmbCodiPerValidarRepeticio(Long expedientTipusId, String codi) throws NoTrobatException {
		logger.debug(
				"Consultant el camp del tipus d'expedient per codi per validar repetició (" +
				"expedientTipusId=" + expedientTipusId + ", " +
				"codi = " + codi + ")");
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		return conversioTipusHelper.convertir(
				campRepository.findByExpedientTipusAndCodi(expedientTipus, codi),
				CampDto.class);
	}	
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<CampDto> campFindPerDatatable(
			Long expedientTipusId,
			Long agrupacioId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant els camps per al tipus d'expedient per datatable (" +
				"entornId=" + expedientTipusId + ", " +
				"agrupacioId=" + agrupacioId + ", " +
				"filtre=" + filtre + ")");
						
		
		PaginaDto<CampDto> pagina = paginacioHelper.toPaginaDto(
				campRepository.findByFiltrePaginat(
						expedientTipusId,
						agrupacioId == null,
						agrupacioId != null ? agrupacioId : 0L,
						filtre == null || "".equals(filtre), 
						filtre, 
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				CampDto.class);
		
		// Omple els comptador de validacions 
		List<Object[]> countValidacions = campRepository.countValidacions(
				expedientTipusId,
				agrupacioId == null,
				agrupacioId); 
		for (CampDto dto: pagina.getContingut()) {
			for (Object[] reg: countValidacions) {
				Long campId = (Long)reg[0];
				if (campId.equals(dto.getId())) {
					Integer count = (Integer)reg[1];
					dto.setValidacioCount(count.intValue());
					break;
				}
			}
		}		
		
		return pagina;		
		
	}
	
	@Override
	@Transactional
	public boolean campMourePosicio(
			Long id, 
			int posicio) {
		boolean ret = false;
		Camp camp = campRepository.findOne(id);
		if (camp != null && camp.getAgrupacio() != null) {
			List<Camp> camps = campRepository.findByAgrupacioIdOrderByOrdreAsc(camp.getAgrupacio().getId());
			if(posicio != camps.indexOf(camp)) {
				camps.remove(camp);
				camps.add(posicio, camp);
				int i = 0;
				for (Camp c : camps) {
					c.setOrdre(i++);
				}
			}
		}
		return ret;
	}
	

	// MANTENIMENT D'AGRUPACIONS DE CAMPS

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<CampAgrupacioDto> agrupacioFindAll(
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException {
		// Recupera el tipus d'expedient
		ExpedientTipus expedientTipus = 
				expedientTipusHelper.getExpedientTipusComprovantPermisos(
						expedientTipusId, 
						true);
		List<CampAgrupacio> agrupacions = null;
		if (expedientTipus.isAmbInfoPropia()) {
			// Recupera la informació de les agrupacions de l'expedient
			agrupacions = campAgrupacioRepository.findAmbExpedientTipusOrdenats(expedientTipusId);
		} else {
			// Recupera la informació de les agrupacions per a la definició de procés
		}
		return conversioTipusHelper.convertirList(
									agrupacions, 
									CampAgrupacioDto.class);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CampAgrupacioDto agrupacioFindAmbId(Long id) throws NoTrobatException {
		logger.debug(
				"Consultant la agrupacio de camps del tipus d'expedient amb id (" +
				"campAgrupacioId=" + id +  ")");

		return conversioTipusHelper.convertir(
				campAgrupacioRepository.findOne(id),
				CampAgrupacioDto.class);
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public CampAgrupacioDto agrupacioCreate(
			Long expedientTipusId, 
			CampAgrupacioDto agrupacio) throws PermisDenegatException {

		logger.debug(
				"Creant nova agrupació de camp per un tipus d'expedient (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"agrupacio=" + agrupacio + ")");
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		
		CampAgrupacio entity = new CampAgrupacio();
		entity.setCodi(agrupacio.getCodi());
		entity.setNom(agrupacio.getNom());
		entity.setDescripcio(agrupacio.getDescripcio());
		entity.setOrdre(campAgrupacioRepository.getNextOrdre(expedientTipusId));

		// Camp associat a l'expedient
		entity.setExpedientTipus(expedientTipus);

		return conversioTipusHelper.convertir(
				campAgrupacioRepository.save(entity),
				CampAgrupacioDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public CampAgrupacioDto agrupacioUpdate(
			Long expedientTipusId, 
			CampAgrupacioDto agrupacio) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Modificant la agrupacio de camp del tipus d'expedient existent (" +
				"agrupacio.id=" + agrupacio.getId() + ", " +
				"agrupacio =" + agrupacio + ")");
		CampAgrupacio entity = campAgrupacioRepository.findOne(agrupacio.getId());
		entity.setCodi(agrupacio.getCodi());
		entity.setNom(agrupacio.getNom());
		entity.setDescripcio(agrupacio.getDescripcio());
		
		return conversioTipusHelper.convertir(
				campAgrupacioRepository.save(entity),
				CampAgrupacioDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void agrupacioDelete(Long agrupacioCampId) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Esborrant la agrupacio de camp del tipus d'expedient (" +
				"agrupacioCampId=" + agrupacioCampId +  ")");
		CampAgrupacio entity = campAgrupacioRepository.findOne(agrupacioCampId);
		if (entity != null) {			
			for (Camp camp : entity.getCamps()) {
				camp.setAgrupacio(null);
				camp.setOrdre(null);
				campRepository.save(camp);
			}			
			campAgrupacioRepository.delete(entity);
			campAgrupacioRepository.flush();
			reordenarAgrupacions(entity.getExpedientTipus().getId());
		}
	}
	
	/** Funció per reasignar el valor d'ordre per a les agrupacions d'un tipus d'expedient */
	@Transactional
	private void reordenarAgrupacions(Long expedientTipusId) {
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		List<CampAgrupacio> campsAgrupacio = expedientTipus.getAgrupacions();
		int i = 0;
		for (CampAgrupacio campAgrupacio: campsAgrupacio)
			campAgrupacio.setOrdre(i++);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CampAgrupacioDto agrupacioFindAmbCodiPerValidarRepeticio(
								Long expedientTipusId, 
								String codi) throws NoTrobatException {
		logger.debug(
				"Consultant la agrupacio de camps del tipus d'expedient per codi per validar repetició (" +
				"expedientTipusId=" + expedientTipusId + ", " +
				"codi = " + codi + ")");
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		return conversioTipusHelper.convertir(
				campAgrupacioRepository.findByExpedientTipusAndCodi(expedientTipus, codi),
				CampAgrupacioDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<CampAgrupacioDto> agrupacioFindPerDatatable(
			Long tipusExpedientId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant les agrupacions per al tipus d'expedient per datatable (" +
				"entornId=" + tipusExpedientId + ", " +
				"filtre=" + filtre + ")");
						
		
		return paginacioHelper.toPaginaDto(
				campAgrupacioRepository.findByFiltrePaginat(
						tipusExpedientId,
						filtre == null || "".equals(filtre), 
						filtre, 
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				CampAgrupacioDto.class);		
	}	
	
	@Override
	@Transactional
	public boolean campAfegirAgrupacio(
			Long campId, 
			Long agrupacioId) {
		boolean ret = false;
		logger.debug(
				"Afegint camp de tipus d'expedient a la agrupació (" +
				"campId=" + campId + ", " +
				"agrupacioId = " + agrupacioId + ")");
		Camp camp = campRepository.findOne(campId);
		CampAgrupacio agrupacio = campAgrupacioRepository.findOne(agrupacioId);
		if (camp != null && agrupacio != null && camp.getExpedientTipus().getId().equals(agrupacio.getExpedientTipus().getId())) {
			camp.setAgrupacio(agrupacio);
			ret = true;
		}
		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public boolean campRemoureAgrupacio(Long campId) {
		boolean ret = false;
		logger.debug(
				"Remoguent el camp de tipus d'expedient de la seva agrupació(" +
				"campId=" + campId + ")");
		Camp camp = campRepository.findOne(campId);
		if (camp != null && camp.getAgrupacio() != null) {
			camp.setAgrupacio(null);
			ret = true;
		}
		return ret;
	}
	
	// MANTENIMENT DE VALIDACIONS DE CAMPS DEL TIPUS D'EXPEDIENT
	
	@Override
	@Transactional
	public ValidacioDto validacioCreate(
			Long campId, 
			ValidacioDto validacio) throws PermisDenegatException {

		logger.debug(
				"Creant nova validacio per un camp de tipus d'expedient (" +
				"campId =" + campId + ", " +
				"validacio=" + validacio + ")");

		Validacio entity = new Validacio();
		entity.setCamp(campRepository.findOne(campId));
		entity.setExpressio(validacio.getExpressio());
		entity.setMissatge(validacio.getMissatge());
		validacio.setOrdre(campValidacioRepository.getNextOrdre(campId));
		
		return conversioTipusHelper.convertir(
				campValidacioRepository.save(entity),
				ValidacioDto.class);
	}

	@Override
	@Transactional
	public ValidacioDto validacioUpdate(ValidacioDto validacio) 
						throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Modificant la validacio del camp del tipus d'expedient existent (" +
				"validacio.id=" + validacio.getId() + ", " +
				"validacio =" + validacio + ")");
		
		Validacio entity = campValidacioRepository.findOne(validacio.getId());
		entity.setExpressio(validacio.getExpressio());
		entity.setMissatge(validacio.getMissatge());
		
		return conversioTipusHelper.convertir(
				campValidacioRepository.save(entity),
				ValidacioDto.class);
	}

	@Override
	@Transactional
	public void validacioDelete(Long validacioValidacioId) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Esborrant la validacio del tipus d'expedient (" +
				"validacioId=" + validacioValidacioId +  ")");
		
		Validacio validacio = campValidacioRepository.findOne(validacioValidacioId);
		campValidacioRepository.delete(validacio);	
		campValidacioRepository.flush();
		
		reordenarValidacions(validacio.getCamp().getId());
	}

	/** Funció per reasignar el valor d'ordre dins d'una agrupació de variables. */
	private void reordenarValidacions(Long campId) {
		List<Validacio> validacios = campValidacioRepository.findAmbCampOrdenats(campId);		
		int i = 0;
		for (Validacio validacio: validacios)
			validacio.setOrdre(i++);
	}

	@Override
	@Transactional
	public boolean validacioMourePosicio(
			Long id, 
			int posicio) {
		boolean ret = false;
		Validacio validacio = campValidacioRepository.findOne(id);
		if (validacio != null) {
			List<Validacio> validacions = campValidacioRepository.findAmbCampOrdenats(validacio.getCamp().getId());
			if(posicio != validacions.indexOf(validacio)) {
				validacions.remove(validacio);
				validacions.add(posicio, validacio);
				int i = 0;
				for (Validacio c : validacions) {
					c.setOrdre(i++);
				}
			}
		}
		return ret;
	}

	@Override
	public ValidacioDto validacioFindAmbId(Long id) throws NoTrobatException {
		logger.debug(
				"Consultant la validacio del camp del tipus d'expedient amb id (" +
				"validacioId=" + id +  ")");

		return conversioTipusHelper.convertir(
				campValidacioRepository.findOne(id),
				ValidacioDto.class);
	}
		
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ValidacioDto> validacioFindPerDatatable(
			Long campId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant els validacios per al tipus d'expedient per datatable (" +
				"entornId=" + campId + ", " +
				"filtre=" + filtre + ")");
						
		
		return paginacioHelper.toPaginaDto(
				campValidacioRepository.findByFiltrePaginat(
						campId,
						filtre == null || "".equals(filtre), 
						filtre, 
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				ValidacioDto.class);		
	}	
	
	// MANTENIMENT D'ENUMERACIONS

	@Override
	@Transactional(readOnly = true)
	public List<EnumeracioDto> enumeracioFindAll(
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException {
		List<Enumeracio> enumeracions = enumeracioRepository.findAmbExpedientTipus(expedientTipusId);
		return conversioTipusHelper.convertirList(
									enumeracions, 
									EnumeracioDto.class);
	}
	
	// MANTENIMENT DE DOMINIS

	@Override
	@Transactional(readOnly = true)
	public List<DominiDto> dominiFindAll(
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException {
		List<Domini> dominins = dominiRepository.findAmbExpedientTipus(expedientTipusId);
		return conversioTipusHelper.convertirList(
									dominins, 
									DominiDto.class);
	}

	// MANTENIMENT DE CONSULTES

	@Override
	@Transactional(readOnly = true)
	public List<ConsultaDto> consultaFindAll(
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException {
		List<Consulta> consultans = consultaRepository.findAmbExpedientTipus(expedientTipusId);
		return conversioTipusHelper.convertirList(
									consultans, 
									ConsultaDto.class);
	}
	
	/***********************************************/
	/******************DOCUMENTS********************/
	/***********************************************/
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ExpedientTipusDocumentDto> documentFindPerDatatable(
			Long expedientTipusId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant els documents per al tipus d'expedient per datatable (" +
				"entornId=" + expedientTipusId + ", " +
				"filtre=" + filtre + ")");
				
		return paginacioHelper.toPaginaDto(
				documentRepository.findByFiltrePaginat(
						expedientTipusId,
						filtre == null || "".equals(filtre),
						filtre,
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
						ExpedientTipusDocumentDto.class);
	}
	
	@Override
	@Transactional
	public ExpedientTipusDocumentDto documentCreate(
			Long expedientTipusId, 
			ExpedientTipusDocumentDto document) {

		logger.debug(
				"Creant nou document per un tipus d'expedient (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"document=" + document + ")");
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		
		Document entity = new Document();
		entity.setCodi(document.getCodi());
		entity.setNom(document.getNom());
		entity.setDescripcio(document.getDescripcio());
		entity.setPlantilla(document.isPlantilla());
		entity.setArxiuNom(document.getArxiuNom());
		entity.setArxiuContingut(document.getArxiuContingut());
		entity.setConvertirExtensio(document.getConvertirExtensio());
		entity.setAdjuntarAuto(document.isAdjuntarAuto());
		if (document.getCampData() != null)
			entity.setCampData(campRepository.findOne(document.getCampData().getId()));
		entity.setExtensionsPermeses(document.getExtensionsPermeses());
		entity.setContentType(document.getContentType());
		entity.setCustodiaCodi(document.getCustodiaCodi());
		entity.setTipusDocPortasignatures(document.getTipusDocPortasignatures());
		
		// Camp associat a l'expedient
		entity.setExpedientTipus(expedientTipus);

		return conversioTipusHelper.convertir(
				documentRepository.save(entity),
				ExpedientTipusDocumentDto.class);
	}
	
	@Override
	@Transactional
	public ExpedientTipusDocumentDto documentFindAmbCodi(Long expedientTipusId, String codi) {
		logger.debug(
				"Consultant el document del tipus d'expedient per codi (" +
				"expedientTipusId=" + expedientTipusId + ", " +
				"codi = " + codi + ")");
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		return conversioTipusHelper.convertir(
				documentRepository.findByExpedientTipusAndCodi(expedientTipus, codi),
				ExpedientTipusDocumentDto.class);
	}
	
	@Override
	@Transactional
	public void documentDelete(Long documentId) {
		logger.debug(
				"Esborrant el document del tipus d'expedient (" +
				"documentId=" + documentId +  ")");
		Document entity = documentRepository.findOne(documentId);

		if (entity != null) {
			for (DocumentTasca documentTasca: entity.getTasques()) {
				documentTasca.getTasca().removeDocument(documentTasca);
				int i = 0;
				for (DocumentTasca dt: documentTasca.getTasca().getDocuments())
					dt.setOrder(i++);
			}
		} else {
			throw new NoTrobatException(Document.class);
		}
		documentRepository.delete(entity);	
	}
	
	@Override
	@Transactional
	public ExpedientTipusDocumentDto documentFindAmbId(Long id) {
		logger.debug(
				"Consultant el document del tipus d'expedient amb id (" +
				"documentId=" + id +  ")");

		return conversioTipusHelper.convertir(
				documentRepository.findOne(id),
				ExpedientTipusDocumentDto.class);
	}
	
	@Override
	@Transactional
	public ExpedientTipusDocumentDto documentUpdate(ExpedientTipusDocumentDto document) {
		logger.debug(
				"Modificant el document del tipus d'expedient existent (" +
				"document.id=" + document.getId() + ", " +
				"document =" + document + ")");
		Document entity = documentRepository.findOne(document.getId());
		entity.setCodi(document.getCodi());
		entity.setNom(document.getNom());
		entity.setDescripcio(document.getDescripcio());
		entity.setPlantilla(document.isPlantilla());
		entity.setArxiuNom(document.getArxiuNom());
		entity.setArxiuContingut(document.getArxiuContingut());
		entity.setConvertirExtensio(document.getConvertirExtensio());
		entity.setAdjuntarAuto(document.isAdjuntarAuto());
		if (document.getCampData() != null)
			entity.setCampData(campRepository.findOne(document.getCampData().getId()));
		entity.setExtensionsPermeses(document.getExtensionsPermeses());
		entity.setContentType(document.getContentType());
		entity.setCustodiaCodi(document.getCustodiaCodi());
		entity.setTipusDocPortasignatures(document.getTipusDocPortasignatures());

		return conversioTipusHelper.convertir(
				documentRepository.save(entity),
				ExpedientTipusDocumentDto.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public ArxiuDto getArxiuPerDocument(
			Long id) {
		logger.debug("obtenint contingut de l'arxiu pel document (" +
				"id=" + id + ")");
		
		Document document = documentRepository.findOne(id);
		if (document == null) {
			throw new NoTrobatException(Document.class,id);
		}
		
		ArxiuDto resposta = new ArxiuDto();
		
//		return documentHelper.getArxiuPerDocumentStoreId(
//				id,
//				false,
//				false);
		
		resposta.setNom(document.getArxiuNom());
		resposta.setContingut(document.getArxiuContingut());
		
		return resposta;
	}

}
