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
import net.conselldemallorca.helium.core.helper.EntornHelper;
import net.conselldemallorca.helium.core.helper.ExpedientTipusHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.helper.PermisosHelper;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.CampAgrupacio;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.SequenciaAny;
import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.v3.core.repository.CampAgrupacioRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.SequenciaAnyRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiRepository;

/**
 * Implementació del servei per a gestionar tipus d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("expedientTipusServiceV3")
public class ExpedientTipusServiceImpl implements ExpedientTipusService {

	@Resource
	private EntornHelper entornHelper;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private SequenciaAnyRepository sequenciaRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private CampAgrupacioRepository campAgrupacioRepository;
	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private TerminiRepository terminiRepository;
	
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource(name = "permisosHelperV3") 
	private PermisosHelper permisosHelper;
	@Resource
	private PaginacioHelper paginacioHelper;


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
		// Els tipus d'expedient creats amb la interfície nova es marquen amb el flag a true 
		entity.setAmbInfoPropia(true);
		return conversioTipusHelper.convertir(
				expedientTipusRepository.save(entity),
				ExpedientTipusDto.class);
	}

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

	@Override
	@Transactional(readOnly = true)
	public ExpedientTipusDto findAmbIdPerDissenyar(
			Long entornId,
			Long expedientTipusId) {
		logger.debug(
				"Consultant tipus d'expedient amb id (" +
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
					true);			
		}		
		return conversioTipusHelper.convertir(
				tipus,
				ExpedientTipusDto.class);
	}

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

	@Override
	@Transactional
	public PermisDto permisUpdate(
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
		return null;
	}

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

		return conversioTipusHelper.convertir(
				campRepository.save(entity),
				CampDto.class);
	}

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

		return conversioTipusHelper.convertir(
				campRepository.save(entity),
				CampDto.class);
	}

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


	@Override
	public CampDto campFindAmbId(Long id) throws NoTrobatException {
		logger.debug(
				"Consultant el camp del tipus d'expedient amb id (" +
				"campId=" + id +  ")");

		return conversioTipusHelper.convertir(
				campRepository.findOne(id),
				CampDto.class);
	}

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
				
		return paginacioHelper.toPaginaDto(
				campRepository.findByFiltrePaginat(
						expedientTipusId,
						agrupacioId == null,
						agrupacioId != null ? agrupacioId : 0L,
						filtre == null || "".equals(filtre), 
						filtre, 
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				CampDto.class);
	}

	// MANTENIMENT D'AGRUPACIONS DE CAMPS

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
	
	@Override
	public CampAgrupacioDto agrupacioFindAmbId(Long id) throws NoTrobatException {
		logger.debug(
				"Consultant la agrupacio de camps del tipus d'expedient amb id (" +
				"campAgrupacioId=" + id +  ")");

		return conversioTipusHelper.convertir(
				campAgrupacioRepository.findOne(id),
				CampAgrupacioDto.class);
	}
	

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
	
	/***********************************************/
	/******************DOCUMENTS********************/
	/***********************************************/
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<DocumentDto> documentFindPerDatatable(
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
						DocumentDto.class);
	}
	
	/***********************************************/
	/******************TERMINIS*********************/
	/***********************************************/
	
	@Override
	@Transactional(readOnly = true)
	public List<TerminiDto> terminiFindAll(Long expedientTipusId, PaginacioParamsDto paginacioParams) throws NoTrobatException, PermisDenegatException {
		// Recupera el tipus d'expedient
		ExpedientTipus expedientTipus =	
				expedientTipusHelper.getExpedientTipusComprovantPermisos(
						expedientTipusId, 
						true);
		List<Termini> terminis = null;
		if (expedientTipus.isAmbInfoPropia()) {
			// Recupera la informació de les agrupacions de l'expedient
			terminis = terminiRepository.findByExpedientTipusId(
					expedientTipusId, 
					paginacioHelper.toSpringDataPageable(paginacioParams));
		} else {
			// Recupera la informació de les agrupacions per a la definició de procés
//			Set<DefinicioProces> definicionsProces = expedientTipus.getDefinicionsProces();
//			terminis = terminiRepository.findByDefinicioProcesId(definicionsProces.iterator().next().getId());
		}
		return conversioTipusHelper.convertirList(
									terminis, 
									TerminiDto.class);
	}
}