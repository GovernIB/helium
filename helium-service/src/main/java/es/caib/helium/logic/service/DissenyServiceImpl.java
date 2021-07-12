/**
 * 
 */
package es.caib.helium.logic.service;

import es.caib.helium.logic.helper.*;
import es.caib.helium.logic.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.helium.logic.intf.WProcessDefinition;
import es.caib.helium.logic.intf.WProcessInstance;
import es.caib.helium.logic.intf.WorkflowEngineApi;
import es.caib.helium.logic.intf.dto.*;
import es.caib.helium.logic.intf.exception.DeploymentException;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.intf.exportacio.DefinicioProcesExportacio;
import es.caib.helium.logic.intf.extern.domini.FilaResultat;
import es.caib.helium.logic.intf.extern.domini.ParellaCodiValor;
import es.caib.helium.logic.intf.service.DissenyService;
import es.caib.helium.logic.security.ExtendedPermission;
import es.caib.helium.ms.domini.DominiMs;
import es.caib.helium.persist.entity.*;
import es.caib.helium.persist.entity.ConsultaCamp.TipusConsultaCamp;
import es.caib.helium.persist.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Servei per gestionar les tasques de disseny.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("dissenyServiceV3")
public class DissenyServiceImpl implements DissenyService {

	@Resource
	private EntornRepository entornRepository;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private MessageServiceHelper messageHelper;
	@Resource
	private WorkflowEngineApi workflowEngineApi;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private EntornHelper entornHelper;
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private CampRepository campRepository;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private ConsultaRepository consultaRepository;
	@Resource
	private ConversioTipusServiceHelper conversioTipusServiceHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private TascaRepository tascaRepository;
	@Resource
	private CampTascaRepository campTascaRepository;
	@Resource
	private AreaRepository areaRepository;
	@Resource
	private DominiHelper dominiHelper;
	@Resource
	private DefinicioProcesHelper definicioProcesHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private DominiMs dominiMs;
	@Resource
	private ConsultaCampRepository consultaCampRepository;
	@Resource
	private AreaJbpmIdRepository areaJbpmIdRepository;
	
	


	@Transactional(readOnly=true)
	@Override
	public AreaDto findAreaById(Long areaId) {
		
		Area area = areaRepository.getById(areaId);
		
		if (area == null)
			throw new NoTrobatException(Area.class, areaId);
		
		return conversioTipusServiceHelper.convertir(
				area,
				AreaDto.class);
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<String> findDistinctJbpmGroupsCodis() {
		List<String> jbpmGroupCodis = new ArrayList<String>();
		for (AreaJbpmId areaJbpmId : areaJbpmIdRepository.findAll())
			jbpmGroupCodis.add(areaJbpmId.getCodi());
		return jbpmGroupCodis;
	}

	@Transactional(readOnly=true)
	@Override
	public List<String> findAccionsJbpmOrdenades(Long definicioProcesId) {
		logger.debug("Consulta de les accions JBPM d'una definicio de proces(" +
					"defincioProcesId = " + definicioProcesId + ")");
		DefinicioProces definicioProces = definicioProcesRepository.getById(definicioProcesId);
		List<String> accions = workflowEngineApi.listActions(definicioProces.getJbpmId());
		Collections.sort(accions);
		return accions;
	}

	
	private void getAllDefinicioProcesOrderByVersio (DefinicioProcesDto definicioProcesDto, ExpedientTipus expedientTipus) {	
		
		WProcessDefinition jb = workflowEngineApi.getProcessDefinition(null, definicioProcesDto.getJbpmId());
		definicioProcesDto.setEtiqueta(jb.getName()+" v."+jb.getVersion());
		
		List<DefinicioProces> mateixaKeyIEntorn = definicioProcesHelper.findVersionsDefinicioProces(
				definicioProcesDto.getEntorn().getId(),
				expedientTipus, 
				definicioProcesDto.getJbpmKey());
		
		definicioProcesDto.setIdsWithSameKey(new Long[mateixaKeyIEntorn.size()]);
		definicioProcesDto.setIdsMostrarWithSameKey(new String[mateixaKeyIEntorn.size()]);
		definicioProcesDto.setJbpmIdsWithSameKey(new String[mateixaKeyIEntorn.size()]);
		
		for (int i = 0; i < mateixaKeyIEntorn.size(); i++) {
			definicioProcesDto.getIdsWithSameKey()[i] = mateixaKeyIEntorn.get(i).getId();
			definicioProcesDto.getIdsMostrarWithSameKey()[i] = mateixaKeyIEntorn.get(i).getIdPerMostrar();
			definicioProcesDto.getJbpmIdsWithSameKey()[i] = mateixaKeyIEntorn.get(i).getJbpmId();
		}
	}

	@Transactional(readOnly=true)
	@Override
	public DefinicioProcesVersioDto getByVersionsInstanciaProcesById(String processInstanceId) {
		WProcessInstance pi = workflowEngineApi.getProcessInstance(processInstanceId);
		if (pi == null)
			throw new NoTrobatException(WProcessInstance.class, processInstanceId);

		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(pi.getProcessDefinitionId());
		if (definicioProces == null)
			throw new NoTrobatException(DefinicioProces.class, pi.getProcessDefinitionId());
		
		DefinicioProcesVersioDto dto = new DefinicioProcesVersioDto();
		dto.setId(definicioProces.getId());
		dto.setVersio(definicioProces.getVersio());
		dto.setEtiqueta(pi.getProcessDefinitionName() + " v." + definicioProces.getVersio());
		
		return dto;
	}

	@Transactional(readOnly=true)
	@Override
	public DefinicioProcesExpedientDto getDefinicioProcesByTipusExpedientById(Long expedientTipusId) {
		ExpedientTipus expedientTipus = expedientTipusRepository.getById(expedientTipusId);
		if (expedientTipus != null && expedientTipus.getJbpmProcessDefinitionKey() != null) {
			
			DefinicioProces definicioProces = definicioProcesHelper.findDarreraVersioDefinicioProces(
															expedientTipus, 
															expedientTipus.getJbpmProcessDefinitionKey());
			if (definicioProces != null) {
				WProcessDefinition jb = workflowEngineApi.getProcessDefinition(null, definicioProces.getJbpmId());
				return getDefinicioProcesByEntornIdAmbJbpmId(
						definicioProces.getEntorn().getId(), 
						jb.getKey(), 
						expedientTipus);
			}
		}
		return null;
	}
	
	@Transactional(readOnly=true)
	@Override
	public DefinicioProcesExpedientDto getDefinicioProcesByEntorIdAndProcesId(
			Long entornId, 
			Long procesId) {		
		DefinicioProces definicioProces = definicioProcesRepository.getById(procesId);
		if (definicioProces != null) {
			WProcessDefinition jb = workflowEngineApi.getProcessDefinition(null, definicioProces.getJbpmId());
			return getDefinicioProcesByEntornIdAmbJbpmId(
					definicioProces.getEntorn().getId(), 
					jb.getKey(), 
					definicioProces.getExpedientTipus());
		} else
			return null;
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<DefinicioProcesExpedientDto> getSubprocessosByProces(Long expedientTipusId, String jbpmId) {
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(jbpmId);
		ExpedientTipus expedientTipus = expedientTipusRepository.getById(expedientTipusId);
		
		if (definicioProces == null)
			throw new NoTrobatException(DefinicioProces.class, jbpmId);
		
		List<String> jbpmIds = new ArrayList<String>(); 
		afegirJbpmIdProcesAmbSubprocessos(
				workflowEngineApi.getProcessDefinition(null, jbpmId),
				jbpmIds,
				false);
		List<DefinicioProcesExpedientDto> subprocessos = new ArrayList<DefinicioProcesExpedientDto>();
		for(String id: jbpmIds){
			WProcessDefinition jb = workflowEngineApi.getProcessDefinition(null, id);
			subprocessos.add(getDefinicioProcesByEntornIdAmbJbpmId(definicioProces.getEntorn().getId(), jb.getKey(), expedientTipus));
		}
		return subprocessos;
	}

	private DefinicioProcesExpedientDto getDefinicioProcesByEntornIdAmbJbpmId(Long entornId, String jbpmKey, ExpedientTipus expedientTipus) {
		DefinicioProcesExpedientDto dto = new DefinicioProcesExpedientDto();
		DefinicioProces definicioProces;
		Long expedientTipusId = null;
		if (expedientTipus != null) {
			definicioProces = definicioProcesHelper.findDarreraVersioDefinicioProces(
					expedientTipus, 
					jbpmKey);
			expedientTipusId = expedientTipus.getId();
		}
		else
			definicioProces = definicioProcesRepository.findDarreraVersioAmbEntornIJbpmKey(entornId, jbpmKey);

		if (definicioProces != null) {
			WProcessDefinition jb = workflowEngineApi.getProcessDefinition(null, definicioProces.getJbpmId());
			dto.setId(definicioProces.getId());
			dto.setJbpmId(definicioProces.getJbpmId());
			dto.setJbpmKey(definicioProces.getJbpmKey());
			dto.setVersio(definicioProces.getVersio());
			
			List<DefinicioProces> listDefProces = definicioProcesHelper.findVersionsDefinicioProces(
					entornId,
					definicioProces.getExpedientTipus(), 
					definicioProces.getJbpmKey());

			boolean demanaNumeroTitol = false;
			if (expedientTipus != null)
				demanaNumeroTitol = (Boolean.TRUE.equals(expedientTipus.getTeNumero()) && Boolean.TRUE.equals(expedientTipus.getDemanaNumero())) 
									|| (Boolean.TRUE.equals(expedientTipus.getTeTitol()) && Boolean.TRUE.equals(expedientTipus.getDemanaTitol()));
			for (DefinicioProces defProces : listDefProces) {				
				Map<Long, Boolean> hasStartTask = new HashMap<Long, Boolean>();			
				dto.addIdAmbEtiquetaId(
						defProces.getId(), 
						defProces.getJbpmId(),
						jb.getName() + " v." + defProces.getVersio(), 
						hasStartTask(definicioProces, hasStartTask, expedientTipusId), 
						demanaNumeroTitol);
				if (defProces.getVersio() == definicioProces.getVersio()) {
					dto.setEtiqueta(jb.getName() + " v." + defProces.getVersio());
				}
			}
			return dto;
		}
		return null;
	}
	
	private void afegirJbpmIdProcesAmbSubprocessos(
			WProcessDefinition jpd,
			List<String> jbpmIds, 
			Boolean incloure) {
		if (jpd != null) {
			List<WProcessDefinition> subPds = workflowEngineApi.getSubProcessDefinitions(null, jpd.getId());
			if (subPds != null) {
				for (WProcessDefinition subPd: subPds)
					if (!jbpmIds.contains(subPd.getId())) {
						jbpmIds.add(subPd.getId());
						afegirJbpmIdProcesAmbSubprocessos(subPd, jbpmIds, true);
					}
			}
			if (!jbpmIds.contains(jpd.getId()) && incloure)
				jbpmIds.add(jpd.getId());
		}
	}
	
	@Transactional(readOnly=true)
	@Override
	public DefinicioProcesDto getById(Long id) {
		DefinicioProces definicioProces = definicioProcesRepository.getById(id);
		if (definicioProces != null) {	
			DefinicioProcesDto dto = conversioTipusServiceHelper.convertir(definicioProces, DefinicioProcesDto.class);
			Long expedientTipusId = definicioProces.getExpedientTipus() != null ? definicioProces.getExpedientTipus().getId() : null;
			Map<Long, Boolean> hasStartTask = new HashMap<Long, Boolean>();
			dto.setHasStartTask(hasStartTask(definicioProces, hasStartTask, expedientTipusId));
			return dto;
		}
		return null;
	}
	
	@Transactional(readOnly=true)
	@Override
	public DefinicioProcesDto findDarreraDefinicioProcesForExpedientTipus(Long expedientTipusId) {
		
		ExpedientTipus expedientTipus = expedientTipusRepository.getById(expedientTipusId);
		if (expedientTipus == null)
			throw new NoTrobatException(ExpedientTipus.class, expedientTipusId);
		
		if (expedientTipus.getJbpmProcessDefinitionKey() != null) {
			DefinicioProces definicioProces = definicioProcesHelper.findDarreraVersioDefinicioProces(
					expedientTipus, 
					expedientTipus.getJbpmProcessDefinitionKey());
			if (definicioProces != null) {
				DefinicioProcesDto dto = conversioTipusServiceHelper.convertir(definicioProces, DefinicioProcesDto.class);
				Map<Long, Boolean> hasStartTask = new HashMap<Long, Boolean>();
				dto.setHasStartTask(hasStartTask(definicioProces, hasStartTask, expedientTipusId));
				getAllDefinicioProcesOrderByVersio(dto, expedientTipus);
				return dto;
			}
		}
		return null;
	}
	
	private boolean hasStartTask(DefinicioProces definicioProces, Map<Long, Boolean> hasStartTask, Long expedientTipusId) {
		Long definicioProcesId = definicioProces.getId();
		Boolean result = hasStartTask.get(definicioProcesId);
		if (result == null) {
			result = new Boolean(false);
			String startTaskName = workflowEngineApi.getStartTaskName(
					definicioProces.getJbpmId());
			if (startTaskName != null) {
				Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProcesJbpmId(
						startTaskName,
						definicioProces.getJbpmId());
				if (tasca != null) {
					List<CampTasca> camps = campTascaRepository.findAmbTascaOrdenats(tasca.getId(), expedientTipusId);
					result = new Boolean(camps.size() > 0);
				}
			}
			hasStartTask.put(definicioProcesId, result);
		}
		return result.booleanValue();
	}

	@Transactional(readOnly=true)
	@Override
	public ExpedientTipusDto getExpedientTipusById(Long id) {
		ExpedientTipus expedientTipus = expedientTipusRepository.getById(id);
		if (expedientTipus == null)
			throw new NoTrobatException(ExpedientTipus.class, id);
		return conversioTipusServiceHelper.convertir(
				expedientTipus,
				ExpedientTipusDto.class);
	}

	@Transactional(readOnly=true)
	@Override
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisReadUsuariActual(
			Long entornId){
		return findExpedientTipusAmbPermisosUsuariActual(
				entornId,
				new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.SUPERVISION,
						ExtendedPermission.ADMINISTRATION});
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisDissenyUsuariActual(
			Long entornId) {
		return findExpedientTipusAmbPermisosUsuariActual(
				entornId,
				new Permission[] {
						ExtendedPermission.DESIGN,
						ExtendedPermission.ADMINISTRATION});
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisGestioUsuariActual(
			Long entornId) {
		return findExpedientTipusAmbPermisosUsuariActual(
				entornId,
				new Permission[] {
						ExtendedPermission.MANAGE,
						ExtendedPermission.ADMINISTRATION});
	}

	@Transactional(readOnly=true)
	@Override
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisCrearUsuariActual(
			Long entornId) {
		return findExpedientTipusAmbPermisosUsuariActual(
				entornId,
				new Permission[] {
						ExtendedPermission.CREATE,
						ExtendedPermission.ADMINISTRATION});
	}

	@Transactional(readOnly=true)
	@Override
	public ExpedientTipusDto findExpedientTipusAmbPermisReadUsuariActual(
			Long entornId,
			Long expedientTipusId) {
		Entorn entorn = entornRepository.getById(entornId);
		if (entorn == null) {
			throw new NoTrobatException(
					Entorn.class, 
					entornId);
		}
		Permission[] permisosRequerits= new Permission[] {
				ExtendedPermission.READ,
				ExtendedPermission.ADMINISTRATION};
		boolean permes = permisosHelper.isGrantedAny(
				expedientTipusId,
				ExpedientTipus.class,
				permisosRequerits,
				SecurityContextHolder.getContext().getAuthentication());
		if (!permes) {
			throw new PermisDenegatException(
					expedientTipusId,
					ExpedientTipus.class,
					permisosRequerits,
					null);
		}
		ExpedientTipus expedientTipus = expedientTipusRepository.getById(
				expedientTipusId);
		if (expedientTipus.getEntorn() != entorn) {
			throw new NoTrobatException(
					ExpedientTipus.class, 
					expedientTipusId);
		}
		List<ExpedientTipusDto> dtos = new ArrayList<ExpedientTipusDto>();
		dtos.add(conversioTipusServiceHelper.convertir(
				expedientTipus,
				ExpedientTipusDto.class));
		expedientHelper.omplirPermisosExpedientsTipus(dtos);
		return dtos.get(0);
	}

	private List<ExpedientTipusDto> findExpedientTipusAmbPermisosUsuariActual(
			Long entornId,
			Permission[] permisos) {
		Entorn entorn = entornRepository.getById(entornId);
		if (entorn == null)
			throw new NoTrobatException(
					Entorn.class, 
					entornId);
		List<ExpedientTipus> expedientsTipus = expedientTipusRepository.findByEntornOrderByNomAsc(entorn);
		permisosHelper.filterGrantedAny(
				expedientsTipus,
				new ObjectIdentifierExtractor<ExpedientTipus>() {
					public Long getObjectIdentifier(ExpedientTipus expedientTipus) {
						return expedientTipus.getId();
					}
				},
				ExpedientTipus.class,
				permisos,
				SecurityContextHolder.getContext().getAuthentication());
		List<ExpedientTipusDto> dtos = conversioTipusServiceHelper.convertirList(
				expedientsTipus,
				ExpedientTipusDto.class);
		expedientHelper.omplirPermisosExpedientsTipus(dtos);
		return dtos;
	}
	
	@Transactional(readOnly=true)
	@Override
	@Cacheable(value="consultaCache", key="{#entornId, #expedientTipusId}")
	public List<ConsultaDto> findConsultesActivesAmbEntornIExpedientTipusOrdenat(
			Long entornId,
			Long expedientTipusId) throws NoTrobatException {
		Entorn entorn = entornRepository.getById(entornId);
		if (entorn == null)
			throw new NoTrobatException(
					Entorn.class, 
					entornId);
		ExpedientTipus expedientTipus = expedientTipusRepository.getById(
				expedientTipusId);
		if (expedientTipus == null)
			throw new NoTrobatException(
					ExpedientTipus.class, 
					expedientTipusId);
		return conversioTipusServiceHelper.convertirList(
				consultaRepository.findConsultesActivesAmbEntornIExpedientTipusOrdenat(entornId, expedientTipusId),
				ConsultaDto.class);
	}
	
	@Transactional(readOnly=true)
	@Override
	public ConsultaDto findConsulteById(
			Long id) {
		Consulta consulta = consultaRepository.getById(id);
		
		if (consulta == null)
			throw new NoTrobatException(Consulta.class, id);
		
		return conversioTipusServiceHelper.convertir(consulta, ConsultaDto.class);
	}

	@Transactional(readOnly=true)
	@Override
	public byte[] getDeploymentResource(
			Long definicioProcesId,
			String resourceName) {
		DefinicioProces definicioProces = definicioProcesRepository.getById(definicioProcesId);
		
		if (definicioProces == null)
			throw new NoTrobatException(DefinicioProces.class, definicioProcesId);
		
		return workflowEngineApi.getResourceBytes(
				definicioProces.getJbpmId(),
				resourceName);
	}

	@Transactional(readOnly=true)
	@Override
	public List<ExpedientTipusDto> findExpedientTipusAmbEntorn(EntornDto entornDto) {
		Entorn entorn = conversioTipusServiceHelper.convertir(entornDto, Entorn.class);
		if (entorn == null)
			throw new NoTrobatException(Entorn.class, entornDto.getCodi());
		return getExpedientTipusAmbEntorn(entorn);
	}

	@Transactional(readOnly=true)
	@Override
	public List<ParellaCodiValorDto> findTasquesAmbEntornIExpedientTipusPerSeleccio(
			Long entornId,
			Long expedientTipusId) {
		// Identificadors de les darreres versions de definicio de procés per a un tipus d'expedient
		List<Long> ids = new ArrayList<Long>();
		Set<Object[]> tasques = new HashSet<Object[]>();
		if (expedientTipusId.equals(-1L)) {
			// Per tots els tipus d'expedients
			List<ExpedientTipusDto> tipusExpedient = findExpedientTipusAmbPermisReadUsuariActual(entornId);
			for (ExpedientTipusDto tipus : tipusExpedient) {
				ids.addAll(definicioProcesRepository.findIdsDarreraVersioAmbEntornIdIExpedientTipusId(
						entornId,
						tipus.getId()));
			}
			if (ids.size() > 0) {
				tasques.addAll(
						tascaRepository.findIdNomByExpedientTipusOrderByExpedientTipusNomAndNomAsc(ids));
			}
		}
		ExpedientTipus expedientTipus = expedientTipusRepository.getById(expedientTipusId);
		if (expedientTipus != null && expedientTipus.getJbpmProcessDefinitionKey() != null) {
			ids.addAll(definicioProcesRepository.findIdsDarreraVersioAmbEntornIdIExpedientTipusId(
					expedientTipus.getEntorn().getId(),
					expedientTipus.getId()));
			if (ids.isEmpty())
				ids.add(0L);
			tasques.addAll(
					tascaRepository.findIdNomByDefinicioProcesIdsOrderByNomAsc(ids));
		}
		List<ParellaCodiValorDto> lista = new ArrayList<ParellaCodiValorDto>();
		for (Object[] tasca: tasques) {
			lista.add(new ParellaCodiValorDto(tasca[0].toString(), tasca[1]));
		}
		Collections.sort(
				lista, 
				new Comparator<ParellaCodiValorDto>() {
					public int compare(ParellaCodiValorDto p1, ParellaCodiValorDto p2) {
						if (p1 != null && p2 == null)
							return -1;
						if (p1 == null && p2 == null)
							return 0;
						if (p1 == null && p2 != null)
							return 1;
						String val1 = p1.getValor() == null ? "" : (String)p1.getValor();
						String val2 = p2.getValor() == null ? "" : (String)p2.getValor();
						int result = val1.compareTo(val2);
						return result == 0 ? result : p1.getCodi().compareTo(p2.getCodi());
					}
				});
		return lista;
	}
	
//	@Transactional(readOnly=true)
	private List<ExpedientTipusDto> getExpedientTipusAmbEntorn(Entorn entorn) {
		List<ExpedientTipusDto> tipus = conversioTipusServiceHelper.convertirList(expedientTipusRepository.findByEntornOrderByCodiAsc(entorn), ExpedientTipusDto.class);
		permisosHelper.filterGrantedAny(
				tipus,
				new ObjectIdentifierExtractor<ExpedientTipusDto>() {
					public Long getObjectIdentifier(ExpedientTipusDto expedientTipus) {
						return expedientTipus.getId();
					}
				},
				ExpedientTipus.class,
				new Permission[] {
						ExtendedPermission.ADMINISTRATION,
						ExtendedPermission.CREATE
				},
				SecurityContextHolder.getContext().getAuthentication());
		return tipus;
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<CampDto> findCampsOrdenatsPerCodi(
			Long expedientTipusId,
			Long definicioProcesId,
			boolean ambHerencia) {
		
		ExpedientTipus expedientTipus = null;
		DefinicioProces definicioProces = null;

		List<CampDto> campsDto;
		Set<Long> heretatsIds = new HashSet<Long>();
		Set<String> sobreescritsCodis = new HashSet<String>();

		if (expedientTipusId != null) {
			expedientTipus = expedientTipusRepository.getById(expedientTipusId);
			if (expedientTipus == null)
				throw new NoTrobatException(ExpedientTipus.class, expedientTipusId);
		}
		if (definicioProcesId != null) {
			definicioProces = definicioProcesRepository.getById(definicioProcesId);
			if (definicioProces == null)
				throw new NoTrobatException(DefinicioProces.class, definicioProcesId);
		}
		List<Camp> camps;
		if (expedientTipus != null && expedientTipus.isAmbInfoPropia()) {
			ambHerencia = HerenciaHelper.ambHerencia(expedientTipus);
			if (ambHerencia) {
				camps = campRepository.findByExpedientTipusAmbHerencia(expedientTipus.getId());
				for(Camp c : camps)
					if(!expedientTipusId.equals(c.getExpedientTipus().getId()))
						heretatsIds.add(c.getId());
				// Llistat d'elements sobreescrits
				for (Camp c : campRepository.findSobreescrits(expedientTipusId)) 
					sobreescritsCodis.add(c.getCodi());
			} else
				camps = campRepository.findByExpedientTipusOrderByCodiAsc(expedientTipus);
		} else if (definicioProces != null) {
			camps = campRepository.findByDefinicioProcesOrderByCodiAsc(definicioProces);
		} else 
			camps = new ArrayList<Camp>();
		campsDto = conversioTipusServiceHelper.convertirList(camps, CampDto.class);
		
		if (ambHerencia) {
			// Completa l'informació del dto
			for(CampDto dto : campsDto) {
				// Sobreescriu
				if (sobreescritsCodis.contains(dto.getCodi()))
					dto.setSobreescriu(true);
				// Heretat
				if(heretatsIds.contains(dto.getId()))
					dto.setHeretat(true);
			}
		}
		return campsDto;
	}	
	
	@Override
	@Transactional(readOnly=true)
	public List<DocumentDto> findDocumentsOrdenatsPerCodi(
			Long expedientTipusId,
			Long definicioProcesId,
			boolean ambHerencia) {
		
		ExpedientTipus expedientTipus = null;
		DefinicioProces definicioProces = null;

		List<DocumentDto> documentsDto;
		Set<Long> heretatsIds = new HashSet<Long>();
		Set<String> sobreescritsCodis = new HashSet<String>();

		if (expedientTipusId != null) {
			expedientTipus = expedientTipusRepository.getById(expedientTipusId);
			if (expedientTipus == null)
				throw new NoTrobatException(ExpedientTipus.class, expedientTipusId);
		}
		if (definicioProcesId != null) {
			definicioProces = definicioProcesRepository.getById(definicioProcesId);
			if (definicioProces == null)
				throw new NoTrobatException(DefinicioProces.class, definicioProcesId);
		}
		List<Document> documents;
		if (expedientTipus != null && expedientTipus.isAmbInfoPropia()) {
			ambHerencia = ambHerencia && expedientTipus.getExpedientTipusPare() != null;
			if (ambHerencia) {
				documents = documentRepository.findByExpedientTipusAmbHerencia(expedientTipus.getId());
				for(Document d : documents)
					if(!expedientTipusId.equals(d.getExpedientTipus().getId()))
						heretatsIds.add(d.getId());
				// Llistat d'elements sobreescrits
				for (Document d : documentRepository.findSobreescrits(expedientTipusId)) 
					sobreescritsCodis.add(d.getCodi());
			} else
				documents = documentRepository.findByExpedientTipusId(expedientTipus.getId());
		} else if (definicioProces != null) {
			documents = documentRepository.findByDefinicioProcesId(definicioProces.getId());
		} else 
			documents = new ArrayList<Document>();
		documentsDto = conversioTipusServiceHelper.convertirList(documents, DocumentDto.class);

		if (ambHerencia) {
			// Completa l'informació del dto
			for(DocumentDto dto : documentsDto) {
				// Sobreescriu
				if (sobreescritsCodis.contains(dto.getCodi()))
					dto.setSobreescriu(true);
				// Heretat
				if(heretatsIds.contains(dto.getId()))
					dto.setHeretat(true);
			}
		}
		return documentsDto;
	}	

	@Override
	@Transactional(readOnly=true)
	public List<FilaResultat> consultaDominiIntern(
			String id,
			List<ParellaCodiValor> parametres) throws Exception {
		return dominiHelper.consultaDominiIntern(
				id,
				parametres);
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<FilaResultat> consultaDomini(
			Long id,
			String codiDomini,
			Map<String, Object> parametres) {
		return dominiHelper.consultar(id, codiDomini,parametres);
	}

	@Override
	@Transactional(readOnly = true)
	public DocumentDto documentFindOne(Long documentId) {
		Document document = documentRepository.getById(documentId);
		if (document == null)
			throw new NoTrobatException(Document.class, documentId);
		return conversioTipusServiceHelper.convertir(
				document,
				DocumentDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentDto> documentFindAmbDefinicioProces(Long definicioProcesId) {
		List<Document> documents = documentRepository.findByDefinicioProcesId(definicioProcesId);
		return conversioTipusServiceHelper.convertirList(
				documents,
				DocumentDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public Set<String> getRecursosNom(Long definicioProcesId) {
		Set<String> resposta = null;
		DefinicioProces definicioProces = definicioProcesRepository.getById(definicioProcesId);
		if (definicioProces != null)
			resposta = workflowEngineApi.getResourceNames(definicioProces.getJbpmId());
		return resposta;
	}

	@Override
	@Transactional(readOnly = true)
	public byte[] getRecursContingut(Long definicioProcesId, String nom) {
		return workflowEngineApi.getResourceBytes(
				definicioProcesRepository.getById(definicioProcesId).getJbpmId(),
				nom);
	}

	/** Retorna el contingut del .par de la definició de procés. */
	@Override
	@Transactional(readOnly = true)
	public byte[] getParContingut(Long definicioProcesId) {
		
		Set<String> recursosNoms = this.getRecursosNom(definicioProcesId);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(baos);
		byte[] recursContingut;
		ZipEntry ze;
		try {
			for (String recursNom : recursosNoms) {
				recursContingut = this.getRecursContingut(definicioProcesId, recursNom);
				if (recursContingut != null) {
					ze = new ZipEntry(recursNom);
					out.putNextEntry(ze);
					out.write(recursContingut);
					out.closeEntry();
				}
			}
			out.close();
		} catch (Exception e) {
			String errMsg = "Error construint el .par de la definició de procés " + definicioProcesId + ": " + e.getMessage();
			logger.error(errMsg, e);
			throw new RuntimeException(errMsg, e);
		}
		return baos.toByteArray();
	}

	
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<DefinicioProcesDto> findDefinicionsProcesNoUtilitzadesExpedientTipus(
			Long entornId,
			Long expedientTipusId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant definicions de procés no utilitzades del tipus d'expedient (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ")");

		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(expedientTipusId);
		
		List<String> noUtilitzades = workflowEngineApi.findDefinicionsProcesIdNoUtilitzadesByExpedientTipusId(expedientTipusId);
		if (noUtilitzades != null && !noUtilitzades.isEmpty()) {
			PaginaDto<DefinicioProcesDto> pagina = paginacioHelper.toPaginaDto(
					definicioProcesRepository.findAmbExpedientTipusIJbpmIds(
							expedientTipusId, 
							noUtilitzades,
							paginacioHelper.toSpringDataPageable(
									paginacioParams)),
					DefinicioProcesDto.class);
			return pagina;
		}
		return new PaginaDto<DefinicioProcesDto>();
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Long> findIdsDefinicionsProcesNoUtilitzadesExpedientTipus(
			Long entornId,
			Long expedientTipusId) {
		
		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(expedientTipusId);
				
		List<String> noUtilitzades = workflowEngineApi.findDefinicionsProcesIdNoUtilitzadesByExpedientTipusId(expedientTipusId);
		List<Long> result = new ArrayList<Long>();
		if (noUtilitzades != null && !noUtilitzades.isEmpty()) {
			result = definicioProcesRepository.findIdsAmbExpedientTipusIJbpmIds(
					expedientTipusId,
					noUtilitzades);
		}
		return result;
	}
	
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ExpedientDto> findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
			Long entornId,
			Long expedientTipusId,
			Long jbpmId,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant definicions de procés no utilitzades del tipus d'expedient (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ")");
		
		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(expedientTipusId);
				
		ExpedientTipusDto expedientTipus = conversioTipusServiceHelper.convertir(
				expedientTipusRepository.getById(
						expedientTipusId), 
						ExpedientTipusDto.class);
		
		List<ExpedientDto> afectats = workflowEngineApi.findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
				expedientTipusId,
				jbpmId);
		
		Page<ExpedientDto> expedients = new PageImpl(afectats, paginacioHelper.toSpringDataPageable(paginacioParams), afectats.size());
		return paginacioHelper.toPaginaDto(
				afectats,
				ExpedientDto.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Long> findIdsExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
			Long entornId,
			Long expedientTipusId,
			Long jbpmId) {
		
		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(expedientTipusId);
				
		List<ExpedientDto> afectats = workflowEngineApi.findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
				expedientTipusId,
				jbpmId);
		
		List<Long> ids = new ArrayList<Long>();
		
		for (ExpedientDto pie : afectats) {
			ids.add(pie.getId());
		}
		
		return ids;
	}
	//////////////////////
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
	//////////////////////
	
	@Override
	@Transactional(readOnly = true)
	public DominiDto dominiFindAmbCodi(
			Long entornId, 
			String codi) {
		DominiDto ret = null;
		logger.debug(
				"Consultant el domini per codi (" +
				"entornId=" + entornId + ", " +
				"codi = " + codi + ")");
		DominiDto domini = dominiMs.findAmbCodi(entornId, null, codi);
		if (domini != null)
			ret = conversioTipusServiceHelper.convertir(
					domini,
					DominiDto.class);
		return ret; 
	}


	// TODO: Modificar mètode per fer una crida al motor passant el fitxer
	@Override
	@Transactional
	public DefinicioProcesDto updateHandlers(
			Long entornId, 
			Long expedientTipusId,
			String nomArxiu,
			byte[] contingut) {
		// Comprova el nom de l'arxiu
		if (! nomArxiu.endsWith("ar")) {
			throw new RuntimeException(
					messageHelper.getMessage("definicio.proces.actualitzar.error.arxiuNom", new Object[] {nomArxiu}));
		}
		// Obrir el .par i comprovar que és correcte
		// Thanks to George Mournos who helped to improve this:
		ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(contingut));
		//TODO arreglar update handlers
//		ProcessDefinition processDefinition;
//		try {
//			processDefinition = ProcessDefinition.parseParZipInputStream(zipInputStream);
//		} catch (Exception e) {
//			throw new DeploymentException(
//					messageHelper.getMessage("definicio.proces.actualitzar.error.parse"));
//		}
//		JbpmProcessDefinition jbpmProcessDefinition = new JbpmProcessDefinition(processDefinition);
		WProcessDefinition jbpmProcessDefinition = null;
    	// Recuperar la darrera versió de la definició de procés
		DefinicioProces darrera;
		if (expedientTipusId != null) {
			// per expedientTipus
			darrera = definicioProcesRepository.findDarreraVersioAmbTipusExpedientIJbpmKey(expedientTipusId, jbpmProcessDefinition.getKey());
		} else {
			// global
			darrera = definicioProcesRepository.findDarreraVersioGlobalAmbJbpmKey(entornId, jbpmProcessDefinition.getKey());
		}			
		if (darrera == null) {
			throw new DeploymentException(
					messageHelper.getMessage(
							"definicio.proces.actualitzar.error.jbpmKey." + (expedientTipusId != null ? "expedientTipus" : "global"), 
							new Object[] {jbpmProcessDefinition.getKey()}));
		}
		
		// Construeix la llista de handlers a partir del contingut del fitxer .par que acabin amb .class
		@SuppressWarnings("unchecked")
		//TODO: arreglar la optenció dels hanclers
//		Map<String, byte[]> bytesMap = jbpmProcessDefinition.getProcessDefinition().getFileDefinition().getBytesMap();
		Map<String, byte[]> bytesMap = null;
		Map<String, byte[]> handlers = new HashMap<String, byte[]>();
		for (String nom : bytesMap.keySet()) 
			if (nom.endsWith(".class")) {
				handlers.put(nom, bytesMap.get(nom));
			}
		// Actualitza els handlers de la darrera versió de la definició de procés
		workflowEngineApi.updateDeploymentActions(
				Long.parseLong(darrera.getJbpmId()), 
				handlers);
		
		return conversioTipusServiceHelper.convertir(darrera, DefinicioProcesDto.class);

	}	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void propagarHandlers(
			Long idDefinicioProcesOrigen, 
			List<Long> idsDefinicioProcesDesti) {
		
		DefinicioProces definicioProcesOrigen = definicioProcesRepository.getById(idDefinicioProcesOrigen);
		
		// Construeix la llista de handlers a partir del contingut del fitxer .par que acabin amb .class
		WProcessDefinition wProcessDefinition = workflowEngineApi.getProcessDefinition(null, definicioProcesOrigen.getJbpmId());
		@SuppressWarnings("unchecked")
		Map<String, byte[]> bytesMap = wProcessDefinition.getFiles();
		Map<String, byte[]> handlers = new HashMap<String, byte[]>();
		for (String nom : bytesMap.keySet()) 
			if (nom.endsWith(".class")) {
				handlers.put(nom, bytesMap.get(nom));
			}

		// Actualitza les definicions de procés destí
		DefinicioProces definicioProcesDesti;
		for (Long idDefinicioProcesDesti : idsDefinicioProcesDesti) {
			definicioProcesDesti = definicioProcesRepository.getById(idDefinicioProcesDesti);
			// Actualitza els handlers de la darrera versió de la definició de procés
			workflowEngineApi.updateDeploymentActions(
					Long.parseLong(definicioProcesDesti.getJbpmId()), 
					handlers);
		}
	}	

	// TODO: Obtenir les dades de la definició de procés del motor
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DefinicioProcesExportacio getDefinicioProcesExportacioFromContingut(
			String fitxer, 
			byte[] contingut) {
		DefinicioProcesExportacio exportacio = new DefinicioProcesExportacio();

		// Comprova el nom de l'arxiu
		if (! fitxer.endsWith("ar")) {
			throw new RuntimeException(
					messageHelper.getMessage("definicio.proces.actualitzar.error.arxiuNom", new Object[] {fitxer}));
		}
		// Obrir el .par i comprovar que és correcte
		ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(contingut));
		// TODO: Comprovar el fitxer al motor
//		ProcessDefinition processDefinition;
//		try {
//			processDefinition = ProcessDefinition.parseParZipInputStream(zipInputStream);
//		} catch (Exception e) {
//			throw new DeploymentException(
//					messageHelper.getMessage("definicio.proces.actualitzar.error.parse"));
//		}
		exportacio.setNomDeploy(fitxer);
		exportacio.setContingutDeploy(contingut);
		//TODO arreglar aquest mètode
		//JbpmProcessDefinition jbpmProcessDefinition = new JbpmProcessDefinition(processDefinition);
		WProcessDefinition jbpmProcessDefinition = null;
		DefinicioProcesDto dto = new DefinicioProcesDto();
		dto.setJbpmKey(jbpmProcessDefinition.getKey());
		dto.setJbpmName(jbpmProcessDefinition.getName());
		exportacio.setDefinicioProcesDto(dto);
				
		return exportacio;
	}
	
	@Transactional(readOnly=true)
 	@Override
 	public List<DocumentDto> findDocumentsAmbDefinicioProcesOrdenatsPerCodi(Long definicioProcesId) {
 		DefinicioProces definicioProces = definicioProcesRepository.getById(definicioProcesId);
 		
 		if (definicioProces == null)
 			throw new NoTrobatException(DefinicioProces.class, definicioProcesId);
 		
 		return conversioTipusServiceHelper.convertirList(documentRepository.findByDefinicioProcesId(definicioProces.getId()), DocumentDto.class);
 	}

    @Override
    public String getPlantillaReport(Long consultaId) {
		ConsultaDto consulta = getConsultaById(consultaId);
		List<ConsultaCampDto> consultaCamps = findCampsInformePerCampsConsulta(
				consulta,
				false);
		List<Camp> camps = new ArrayList<Camp>();

		ExpedientTipus expedientTipus = new ExpedientTipus();
		expedientTipus.setId(consulta.getExpedientTipus().getId());

		for(ConsultaCampDto consultaCamp: consultaCamps) {
			if(consultaCamp.getDefprocJbpmKey() != null) {
				DefinicioProces dp = definicioProcesRepository.findByJbpmKeyAndVersio(consultaCamp.getDefprocJbpmKey(), consultaCamp.getDefprocVersio());
				Camp camp = campRepository.findByDefinicioProcesAndCodi(dp, consultaCamp.getCampCodi());
				if(camp != null) {
					camp.setExpedientTipus(expedientTipus);
					camps.add(camp);
				} else {
					logger.info("No s'ha trobat el camp amb el codi = [" + consultaCamp.getCampCodi() + "] i la definició de procés amb l'id = [" + dp.getId() + "]");
				}
			}else {
				Camp camp = campRepository.findByExpedientTipusAndCodi(expedientTipus.getId(), consultaCamp.getCampCodi(), expedientTipus.getExpedientTipusPare() != null);
				if(camp != null) {
					camp.setExpedientTipus(expedientTipus);
					camps.add(camp);
				} else {
					logger.info("No s'ha trobat el camp amb el codi = [" + consultaCamp.getCampCodi() + "] i el tipus d'expedient amb l'id = [" + expedientTipus.getId() + "]");
				}
			}
		}


		String jasperReport = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<jasperReport xmlns=\"http://jasperreports.sourceforge.net/jasperreports\" " +
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
				"xsi:schemaLocation=\"http://jasperreports.sourceforge.net/jasperreports " +
				"http://jasperreports.sourceforge.net/xsd/jasperreport.xsd\" " +
				"name=\"report_basic\" language=\"groovy\" pageWidth=\"842\" pageHeight=\"595\" " +
				"orientation=\"Landscape\" columnWidth=\"802\" leftMargin=\"20\" " +
				"rightMargin=\"20\" topMargin=\"20\" bottomMargin=\"20\">" +
				"\n<property name=\"ireport.zoom\" value=\"1.0\"/>" +
				"\n<property name=\"ireport.x\" value=\"0\"/>" +
				"\n<property name=\"ireport.y\" value=\"0\"/>";
		for (Camp camp: camps) {
			jasperReport = jasperReport + "\n<field name=\"" + camp.getCodiPerInforme() +"\" class=\"net.conselldemallorca.helium.report.FieldValue\"/>";
		}
		jasperReport = jasperReport +
				"\n<title>" +
				"\n<band height=\"30\" splitType=\"Stretch\">" +
				"\n<staticText>" +
				"\n<reportElement x=\"0\" y=\"0\" width=\"750\" height=\"26\"/>" +
				"\n<textElement>" +
				"\n<font size=\"18\" isBold=\"true\"/>" +
				"\n</textElement>" +
				"\n<text>" + consulta.getNom() +"</text>" +
				"\n</staticText>" +
				"\n</band>" +
				"\n</title>" +
				"\n<pageHeader>" +
				"\n<band height=\"30\" splitType=\"Stretch\"/> " +
				"\n</pageHeader>" +
				"\n<columnHeader>" +
				"\n<band height=\"25\" splitType=\"Stretch\">";
		int widthField = 0;
		if (camps.size()>0) widthField = 800/camps.size();
		int xPosition = 0;
		for (Camp camp: camps) {
			jasperReport = jasperReport +
					"\n<staticText>" +
					"\n<reportElement x=\""+xPosition+"\" y=\"2\" width=\""+widthField+"\" height=\"20\"/>" +
					"\n<textElement/>" +
					"\n<text><![CDATA[" + camp.getEtiqueta() + "]]></text>" +
					"\n</staticText>";
			xPosition = xPosition + widthField;
		}

		jasperReport = jasperReport +
				"\n</band>" +
				"\n</columnHeader>" +
				"\n<detail>" +
				"\n<band height=\"24\" splitType=\"Stretch\">";

		xPosition = 0;
		for (Camp camp: camps) {
			jasperReport = jasperReport +
					"\n<textField>" +
					"\n<reportElement x=\""+xPosition+"\" y=\"4\" width=\""+widthField+"\" height=\"20\"/>" +
					"\n<textElement/>" +
					"\n<textFieldExpression><![CDATA[$F{"+camp.getCodiPerInforme()+"}]]></textFieldExpression>" +
					"\n</textField>";
			xPosition = xPosition + widthField;
		}
		jasperReport=jasperReport +
				"\n</band>" +
				"\n</detail>" +
				"\n<columnFooter>" +
				"\n<band height=\"25\" splitType=\"Stretch\"/>" +
				"\n</columnFooter>" +
				"\n<pageFooter>" +
				"\n<band height=\"30\" splitType=\"Stretch\"/>" +
				"\n</pageFooter>" +
				"\n</jasperReport>";

        return jasperReport;
    }

    @Transactional(readOnly=true)
	@Override
	public ConsultaDto getConsultaById(Long id) {
		return conversioTipusServiceHelper.convertir(consultaRepository.getById(id), ConsultaDto.class);
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<ConsultaCampDto> findCampsInformePerCampsConsulta(
			ConsultaDto consulta,
			boolean filtrarValorsPredefinits) {
		List<ConsultaCamp> consultaCamps = consultaCampRepository.findCampsConsulta(consulta.getId(), TipusConsultaCamp.INFORME);		
		
		return conversioTipusServiceHelper.convertirList(consultaCamps, ConsultaCampDto.class);
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientServiceImpl.class);
}
