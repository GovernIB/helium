/**
 * 
 */
package es.caib.helium.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import javax.annotation.Resource;

import org.flowable.common.engine.impl.util.IoUtil;
import org.flowable.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.helium.commons.domini.FilaResultat;
import es.caib.helium.commons.domini.ParellaCodiValor;
import es.caib.helium.commons.dto.AreaDto;
import es.caib.helium.commons.dto.CampDto;
import es.caib.helium.commons.dto.ConsultaCampDto;
import es.caib.helium.commons.dto.ConsultaDto;
import es.caib.helium.commons.dto.DefinicioProcesDto;
import es.caib.helium.commons.dto.DefinicioProcesExpedientDto;
import es.caib.helium.commons.dto.DefinicioProcesVersioDto;
import es.caib.helium.commons.dto.DocumentDto;
import es.caib.helium.commons.dto.DominiDto;
import es.caib.helium.commons.dto.EntornDto;
import es.caib.helium.commons.dto.ExpedientDocumentPinbalDto;
import es.caib.helium.commons.dto.ExpedientDto;
import es.caib.helium.commons.dto.ExpedientTipusDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.ParellaCodiValorDto;
import es.caib.helium.commons.dto.PermisDto;
import es.caib.helium.commons.dto.handlers.HandlerDto;
import es.caib.helium.commons.exception.DeploymentException;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.exception.PermisDenegatException;
import es.caib.helium.commons.exportacio.DefinicioProcesExportacio;
import es.caib.helium.logic.intf.dto.engine.WProcessDefinition;
import es.caib.helium.logic.intf.dto.engine.WProcessDefinitionImpl;
import es.caib.helium.logic.intf.dto.engine.WProcessInstance;
import es.caib.helium.logic.intf.service.DissenyService;
import es.caib.helium.logic.intf.service.WorkflowEngineApi;
import es.caib.helium.persistence.entity.Area;
import es.caib.helium.persistence.entity.Camp;
import es.caib.helium.persistence.entity.CampTasca;
import es.caib.helium.persistence.entity.Consulta;
import es.caib.helium.persistence.entity.ConsultaCamp;
import es.caib.helium.persistence.entity.ConsultaCamp.TipusConsultaCamp;
import es.caib.helium.persistence.entity.DefinicioProces;
import es.caib.helium.persistence.entity.Document;
import es.caib.helium.persistence.entity.Domini;
import es.caib.helium.persistence.entity.Entorn;
import es.caib.helium.persistence.entity.ExpedientTipus;
import es.caib.helium.persistence.entity.ServeiPinbalEntity;
import es.caib.helium.persistence.entity.Tasca;
import es.caib.helium.persistence.repository.AccioRepository;
import es.caib.helium.persistence.repository.AreaRepository;
import es.caib.helium.persistence.repository.CampRepository;
import es.caib.helium.persistence.repository.CampTascaRepository;
import es.caib.helium.persistence.repository.ConsultaCampRepository;
import es.caib.helium.persistence.repository.ConsultaRepository;
import es.caib.helium.persistence.repository.DefinicioProcesRepository;
import es.caib.helium.persistence.repository.DocumentRepository;
import es.caib.helium.persistence.repository.DominiRepository;
import es.caib.helium.persistence.repository.EntornRepository;
import es.caib.helium.persistence.repository.EnumeracioRepository;
import es.caib.helium.persistence.repository.EstatRepository;
import es.caib.helium.persistence.repository.ExpedientTipusRepository;
import es.caib.helium.persistence.repository.ServeiPinbalRepository;
import es.caib.helium.persistence.repository.TascaRepository;
import es.caib.helium.persistence.repository.TerminiIniciatRepository;
import es.caib.helium.service.helper.ConversioTipusHelper;
import es.caib.helium.service.helper.DefinicioProcesHelper;
import es.caib.helium.service.helper.DominiHelper;
import es.caib.helium.service.helper.EntornHelper;
import es.caib.helium.service.helper.ExpedientHelper;
import es.caib.helium.service.helper.ExpedientLoggerHelper;
import es.caib.helium.service.helper.ExpedientTipusHelper;
import es.caib.helium.service.helper.HerenciaHelper;
import es.caib.helium.service.helper.MessageHelper;
import es.caib.helium.service.helper.PaginacioHelper;
import es.caib.helium.service.helper.PermisosHelper;
import es.caib.helium.service.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.helium.service.helpers.MesuresTemporalsHelper;
import es.caib.helium.service.security.ExtendedPermission;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;

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
	private MessageHelper messageHelper;
	@Resource
	private ExpedientLoggerHelper expedientLoggerHelper;
	@Resource
	private TerminiIniciatRepository terminiIniciatRepository;
	@Resource
	private WorkflowEngineApi jbpmHelper;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource
	private EntornHelper entornHelper;
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private CampRepository campRepository;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private EstatRepository estatRepository;
	@Resource
	private ConsultaRepository consultaRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource(name = "permisosHelperV3")
	private PermisosHelper permisosHelper;
	@Resource
	private TascaRepository tascaRepository;
	@Resource
	private CampTascaRepository campTascaRepository;
	@Resource
	private AccioRepository accioRepository;
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
	private EnumeracioRepository enumeracioRepository;
	@Resource
	private DominiRepository dominiRepository;
	@Resource
	private ConsultaCampRepository consultaCampRepository;
	@Resource
	private ServeiPinbalRepository serveiPinbalRepository;
	


	@Transactional(readOnly=true)
	@Override
	public AreaDto findAreaById(Long areaId) {
		
		Area area = areaRepository.findById(areaId).orElse(null);
		
		if (area == null)
			throw new NoTrobatException(Area.class, areaId);
		
		return conversioTipusHelper.convertir(
				area,
				AreaDto.class);
	}

	@Transactional(readOnly=true)
	@Override
	public List<String> findAccionsJbpmOrdenades(Long definicioProcesId) {
		logger.debug("Consulta de les accions JBPM d'una definicio de proces(" +
					"defincioProcesId = " + definicioProcesId + ")");
		DefinicioProces definicioProces = definicioProcesRepository.findById(definicioProcesId).orElse(null);
		List<String> accions = jbpmHelper.listActions(definicioProces.getJbpmId());
		Collections.sort(accions);
		return accions;
	}

    @Override
	@Transactional(readOnly = true)
    public List<String> findHandlersJbpmOrdenats(Long definicioProcesId) {
		Set<String> recursosNom = getHandlersNom(definicioProcesId);
		List<String> handlers = new ArrayList<String>();
		for (String recurs : recursosNom) {
			handlers.add(expedientHelper.resourceToHandler(recurs));
		}
		java.util.Collections.sort(handlers);
		return handlers;
    }
    
    @Override
    public List<ParellaCodiValorDto> findHandlerParams(Long definicioProcesId, String handler) {
		List<ParellaCodiValorDto> parametres = new ArrayList<ParellaCodiValorDto>();

		String recurs = expedientHelper.handlerToResource(handler);
		byte[] handlerContingut = getRecursContingut(definicioProcesId, recurs);

		try {
			ClassPool cp = ClassPool.getDefault();
			CtClass ctClass = cp.makeClass(new ByteArrayInputStream(handlerContingut));
			CtField[] declaredFields = ctClass.getDeclaredFields();
			for (CtField declaredField: declaredFields) {
				parametres.add(new ParellaCodiValorDto(declaredField.getName(), declaredField.getType().getName()));
			}
			ctClass.detach();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return parametres;
    }


    private void getAllDefinicioProcesOrderByVersio (DefinicioProcesDto definicioProcesDto, ExpedientTipus expedientTipus) {
		
		WProcessDefinition jb = jbpmHelper.getProcessDefinition(definicioProcesDto.getJbpmId());
		definicioProcesDto.setEtiqueta(jb.getProcessDefinition().getName()+" v."+jb.getVersion());
		
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
		WProcessInstance pi = jbpmHelper.getProcessInstance(processInstanceId);
		if (pi == null)
			throw new NoTrobatException(WProcessInstance.class, processInstanceId);

		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(pi.getProcessDefinitionId());
		if (definicioProces == null)
			throw new NoTrobatException(DefinicioProces.class, pi.getProcessDefinitionId());
		
		DefinicioProcesVersioDto dto = new DefinicioProcesVersioDto();
		dto.setId(definicioProces.getId());
		dto.setVersio(definicioProces.getVersio());
		dto.setEtiqueta(pi.getProcessInstance().getProcessDefinition().getName() + " v." + definicioProces.getVersio());
		
		return dto;
	}

	@Transactional(readOnly=true)
	@Override
	public DefinicioProcesExpedientDto getDefinicioProcesByTipusExpedientById(Long expedientTipusId) {
		ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId).orElse(null);
		if (expedientTipus != null && expedientTipus.getJbpmProcessDefinitionKey() != null) {
			
			DefinicioProces definicioProces = definicioProcesHelper.findDarreraVersioDefinicioProces(
															expedientTipus, 
															expedientTipus.getJbpmProcessDefinitionKey());
			if (definicioProces != null) {
				WProcessDefinition jb = jbpmHelper.getProcessDefinition(definicioProces.getJbpmId());
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
		DefinicioProces definicioProces = definicioProcesRepository.findById(procesId).orElse(null);
		if (definicioProces != null) {
			WProcessDefinition jb = jbpmHelper.getProcessDefinition(definicioProces.getJbpmId());
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
		ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId).orElse(null);
		
		if (definicioProces == null)
			throw new NoTrobatException(DefinicioProces.class, jbpmId);
		
		List<String> jbpmIds = new ArrayList<String>(); 
		afegirJbpmIdProcesAmbSubprocessos(jbpmHelper.getProcessDefinition(jbpmId), jbpmIds, false);
		List<DefinicioProcesExpedientDto> subprocessos = new ArrayList<DefinicioProcesExpedientDto>();
		DefinicioProcesExpedientDto dp;
		for(String id: jbpmIds){
			WProcessDefinition jb = jbpmHelper.getProcessDefinition(id);
			dp = getDefinicioProcesByEntornIdAmbJbpmId(definicioProces.getEntorn().getId(), jb.getKey(), expedientTipus);
			if (dp != null) {
				subprocessos.add(dp);
			}
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
			definicioProces = definicioProcesRepository.findDarreraVersioAmbEntornTipusIJbpmKey(
					entornId, 
					expedientTipusId == null,
					expedientTipusId != null ? expedientTipusId : 0L,
					jbpmKey);

		if (definicioProces != null) {
			WProcessDefinition jb = jbpmHelper.getProcessDefinition(definicioProces.getJbpmId());			
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
			List<WProcessDefinition> subPds = jbpmHelper.getSubProcessDefinitions(jpd.getId());
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
		DefinicioProces definicioProces = definicioProcesRepository.findById(id).orElse(null);
		if (definicioProces != null) {	
			DefinicioProcesDto dto = conversioTipusHelper.convertir(definicioProces, DefinicioProcesDto.class);
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
		
		ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId).orElse(null);
		if (expedientTipus == null)
			throw new NoTrobatException(ExpedientTipus.class, expedientTipusId);
		
		if (expedientTipus.getJbpmProcessDefinitionKey() != null) {
			DefinicioProces definicioProces = definicioProcesHelper.findDarreraVersioDefinicioProces(
					expedientTipus, 
					expedientTipus.getJbpmProcessDefinitionKey());
			if (definicioProces != null) {
				DefinicioProcesDto dto = conversioTipusHelper.convertir(definicioProces, DefinicioProcesDto.class);
				Map<Long, Boolean> hasStartTask = new HashMap<Long, Boolean>();
				dto.setHasStartTask(hasStartTask(definicioProces, hasStartTask, expedientTipusId));
				getAllDefinicioProcesOrderByVersio(dto, expedientTipus);
				return dto;
			}
		}
		return null;
	}
	
	@Transactional(readOnly=true)
	@Override
	public DefinicioProcesDto findDarreraVersioForExpedientTipusIDefProcCodi(Long expedientTipusId, String defProcCodi)
			throws NoTrobatException {

		ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId).orElse(null);
		DefinicioProces definicioProces = definicioProcesHelper.findDarreraVersioDefinicioProces(
				expedientTipus, 
				defProcCodi);
		if (definicioProces == null) {
			throw new NoTrobatException(DefinicioProces.class, defProcCodi);
		}
		return conversioTipusHelper.convertir(definicioProces, DefinicioProcesDto.class);
	}
	
	private boolean hasStartTask(DefinicioProces definicioProces, Map<Long, Boolean> hasStartTask, Long expedientTipusId) {
		Long definicioProcesId = definicioProces.getId();
		Boolean result = hasStartTask.get(definicioProcesId);
		if (result == null) {
			result = new Boolean(false);
			String startTaskName = jbpmHelper.getStartTaskName(
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
		ExpedientTipus expedientTipus = expedientTipusRepository.findById(id).orElse(null);
		if (expedientTipus == null)
			throw new NoTrobatException(ExpedientTipus.class, id);
		return conversioTipusHelper.convertir(
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
		Entorn entorn = entornRepository.findById(entornId).orElse(null);
		if (entorn == null) {
			throw new NoTrobatException(
					Entorn.class, 
					entornId);
		}
		ExpedientTipus expedientTipus = expedientTipusRepository.findById(
				expedientTipusId).orElse(null);
		if (expedientTipus.getEntorn() != entorn) {
			throw new NoTrobatException(
					ExpedientTipus.class, 
					expedientTipusId);
		}

		boolean permes = false;
		Permission[] permisosRequerits= new Permission[] {
				ExtendedPermission.READ,
				ExtendedPermission.ADMINISTRATION};
		if (!expedientTipus.isProcedimentComu()) {
			permes = permisosHelper.isGrantedAny(
					expedientTipusId,
					ExpedientTipus.class,
					permisosRequerits,
					SecurityContextHolder.getContext().getAuthentication());
		} else {
			List<Long> idsUnitatsOrganitzatives = expedientTipusHelper.findIdsUnitatsOrgAmbPermisos(entornId, expedientTipusId, permisosRequerits);
			if(idsUnitatsOrganitzatives!=null && !idsUnitatsOrganitzatives.isEmpty()) {
				permes = true;
			}
		}
		if (!permes) {
			throw new PermisDenegatException(
					expedientTipusId,
					ExpedientTipus.class,
					permisosRequerits,
					null);
		}
		List<ExpedientTipusDto> dtos = new ArrayList<ExpedientTipusDto>();
		dtos.add(conversioTipusHelper.convertir(
				expedientTipus,
				ExpedientTipusDto.class));
		expedientHelper.omplirPermisosExpedientsTipus(dtos);
		return dtos.get(0);
	}

	private List<ExpedientTipusDto> findExpedientTipusAmbPermisosUsuariActual(
			Long entornId,
			Permission[] permisos) {
		Entorn entorn = entornRepository.findById(entornId).orElse(null);
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
		List<ExpedientTipusDto> dtos = conversioTipusHelper.convertirList(
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
		Entorn entorn = entornRepository.findById(entornId).orElse(null);
		if (entorn == null)
			throw new NoTrobatException(
					Entorn.class, 
					entornId);
		ExpedientTipus expedientTipus = expedientTipusRepository.findById(
				expedientTipusId).orElse(null);
		if (expedientTipus == null)
			throw new NoTrobatException(
					ExpedientTipus.class, 
					expedientTipusId);
		return conversioTipusHelper.convertirList(
				consultaRepository.findConsultesActivesAmbEntornIExpedientTipusOrdenat(entornId, expedientTipusId),
				ConsultaDto.class);
	}
	
	@Transactional(readOnly=true)
	@Override
	public ConsultaDto findConsulteById(
			Long id) {
		Consulta consulta = consultaRepository.findById(id).orElse(null);
		
		if (consulta == null)
			throw new NoTrobatException(Consulta.class, id);
		
		return conversioTipusHelper.convertir(consulta, ConsultaDto.class);
	}

	@Transactional(readOnly=true)
	@Override
	public byte[] getDeploymentResource(
			Long definicioProcesId,
			String resourceName) {
		DefinicioProces definicioProces = definicioProcesRepository.findById(definicioProcesId).orElse(null);
		
		if (definicioProces == null)
			throw new NoTrobatException(DefinicioProces.class, definicioProcesId);
		
		return jbpmHelper.getResourceBytes(
				definicioProces.getJbpmId(),
				resourceName);
	}

	@Transactional(readOnly=true)
	@Override
	public List<ExpedientTipusDto> findExpedientTipusAmbEntorn(EntornDto entornDto) {
		Entorn entorn = conversioTipusHelper.convertir(entornDto, Entorn.class);
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
		ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId).orElse(null);
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
	
	private List<ExpedientTipusDto> getExpedientTipusAmbEntorn(Entorn entorn) {
		List<ExpedientTipusDto> tipus = conversioTipusHelper.convertirList(expedientTipusRepository.findByEntornOrderByCodiAsc(entorn), ExpedientTipusDto.class);
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
			expedientTipus = expedientTipusRepository.findById(expedientTipusId).orElse(null);
			if (expedientTipus == null)
				throw new NoTrobatException(ExpedientTipus.class, expedientTipusId);
		}
		if (definicioProcesId != null) {
			definicioProces = definicioProcesRepository.findById(definicioProcesId).orElse(null);
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
		campsDto = conversioTipusHelper.convertirList(camps, CampDto.class);
		
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
			expedientTipus = expedientTipusRepository.findById(expedientTipusId).orElse(null);
			if (expedientTipus == null)
				throw new NoTrobatException(ExpedientTipus.class, expedientTipusId);
		}
		if (definicioProcesId != null) {
			definicioProces = definicioProcesRepository.findById(definicioProcesId).orElse(null);
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
		documentsDto = conversioTipusHelper.convertirList(documents, DocumentDto.class);

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
		Domini domini = dominiRepository.findById(id).orElse(null);
		return dominiHelper.consultar(domini, codiDomini,parametres);
	}

	@Override
	@Transactional(readOnly = true)
	public DocumentDto documentFindOne(Long documentId) {
		Document document = documentRepository.findById(documentId).orElse(null);
		if (document == null)
			throw new NoTrobatException(Document.class, documentId);
		return conversioTipusHelper.convertir(
				document,
				DocumentDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentDto> documentFindAmbDefinicioProces(Long definicioProcesId) {
		List<Document> documents = documentRepository.findByDefinicioProcesId(definicioProcesId);
		return conversioTipusHelper.convertirList(
				documents,
				DocumentDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public Set<String> getRecursosNom(Long definicioProcesId) {
		Set<String> resposta = null;
		DefinicioProces definicioProces = definicioProcesRepository.findById(definicioProcesId).orElse(null);
		if (definicioProces != null)
			resposta = jbpmHelper.getResourceNames(definicioProces.getJbpmId());
		return resposta;
	}
	
	@Transactional(readOnly = true)
	public Set<String> getHandlersNom(Long definicioProcesId) {
		Set<String> resposta = null;
		DefinicioProces definicioProces = definicioProcesRepository.findById(definicioProcesId).orElse(null);
		if (definicioProces != null)
			resposta = jbpmHelper.getHandlerNames(definicioProces.getJbpmId());
		return resposta;
	}

	@Override
	@Transactional(readOnly = true)
	public byte[] getRecursContingut(Long definicioProcesId, String nom) {
		return this.getRecursContingut(
				definicioProcesRepository.findById(definicioProcesId).orElse(null).getJbpmId(), 
				nom);
		
	}
	
	private byte[] getRecursContingut(String processDefinitionId, String nom) {
		return jbpmHelper.getResourceBytes(
				processDefinitionId, 
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
			DefinicioProces definicioProces = definicioProcesRepository.findById(definicioProcesId).orElse(null);
			for (String recursNom : recursosNoms) {
				recursContingut = this.getRecursContingut(definicioProces.getJbpmId(), recursNom);
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
		
		List<String> noUtilitzades = jbpmHelper.findDefinicionsProcesIdNoUtilitzadesByExpedientTipusId(expedientTipusId);
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
				
		List<String> noUtilitzades = jbpmHelper.findDefinicionsProcesIdNoUtilitzadesByExpedientTipusId(expedientTipusId);
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
				
		ExpedientTipusDto expedientTipus = conversioTipusHelper.convertir(
				expedientTipusRepository.findById(
						expedientTipusId), 
						ExpedientTipusDto.class);
		
		List<es.caib.helium.logic.intf.dto.ExpedientDto> afectats = jbpmHelper.findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
				expedientTipusId,
				jbpmId);
		
		List<ExpedientDto> expedients = new ArrayList<ExpedientDto>();
		
		for (es.caib.helium.logic.intf.dto.ExpedientDto pie : afectats) {
			ExpedientDto exp = new ExpedientDto();
			exp.setId(pie.getId());
			exp.setTipus(expedientTipus);
			exp.setTitol(pie.getTitol());
			exp.setNumero(pie.getNumero());
			exp.setNumeroDefault(pie.getNumeroDefault());
			exp.setDataInici(pie.getDataInici());
			exp.setDataFi(pie.getDataFi());
			exp.setProcessInstanceId(pie.getProcessInstanceId());
			expedients.add(exp);
		}
		
		PaginaDto<ExpedientDto> pagina = paginacioHelper.toPaginaDto(
				expedients,
				expedients.size(),
				paginacioParams);
		
		return pagina;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Long> findIdsExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
			Long entornId,
			Long expedientTipusId,
			Long jbpmId) {
		
		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(expedientTipusId);
				
		List<es.caib.helium.logic.intf.dto.ExpedientDto> afectats = jbpmHelper.findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
				expedientTipusId,
				jbpmId);
		
		List<Long> ids = new ArrayList<Long>();
		
		for (es.caib.helium.logic.intf.dto.ExpedientDto pie : afectats) {
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
		Entorn entorn = entornRepository.findById(entornId).orElse(null);
		Domini domini = dominiRepository.findByEntornAndCodi(entorn, codi);
		if (domini != null)
			ret = conversioTipusHelper.convertir(
					domini,
					DominiDto.class);
		return ret; 
	}

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
		ProcessDefinition processDefinition;
		try {
//			processDefinition = ProcessDefinition.parseParZipInputStream(zipInputStream);
		} catch (Exception e) {
			throw new DeploymentException(
					messageHelper.getMessage("definicio.proces.actualitzar.error.parse"));		
		}
		WProcessDefinition jbpmProcessDefinition = null; //new WProcessDefinition(processDefinition);
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
		Map<String, byte[]> bytesMap = jbpmProcessDefinition.getProcessDefinition().getFileDefinition().getBytesMap();
		Map<String, byte[]> handlers = new HashMap<String, byte[]>();
		for (String nom : bytesMap.keySet()) 
			if (nom.endsWith(".class")) {
				handlers.put(nom, bytesMap.get(nom));
			}
		// Actualitza els handlers de la darrera versió de la definició de procés
		jbpmHelper.updateHandlers(
				Long.parseLong(darrera.getJbpmId()), 
				handlers);
		
		return conversioTipusHelper.convertir(darrera, DefinicioProcesDto.class);

	}	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void propagarHandlers(
			Long idDefinicioProcesOrigen, 
			List<Long> idsDefinicioProcesDesti) {
		
		DefinicioProces definicioProcesOrigen = definicioProcesRepository.findById(idDefinicioProcesOrigen).orElse(null);
		
		// Construeix la llista de handlers a partir del contingut del fitxer .par que acabin amb .class
		WProcessDefinition jbpmProcessDefinition = jbpmHelper.getProcessDefinition(definicioProcesOrigen.getJbpmId());
		@SuppressWarnings("unchecked")
		Map<String, byte[]> bytesMap = jbpmProcessDefinition.getProcessDefinition().getFileDefinition().getBytesMap();
		Map<String, byte[]> handlers = new HashMap<String, byte[]>();
		for (String nom : bytesMap.keySet()) 
			if (nom.endsWith(".class")) {
				handlers.put(nom, bytesMap.get(nom));
			}

		// Actualitza les definicions de procés destí
		DefinicioProces definicioProcesDesti;
		for (Long idDefinicioProcesDesti : idsDefinicioProcesDesti) {
			definicioProcesDesti = definicioProcesRepository.findById(idDefinicioProcesDesti).orElse(null);
			// Actualitza els handlers de la darrera versió de la definició de procés
			jbpmHelper.updateHandlers(
					Long.parseLong(definicioProcesDesti.getJbpmId()), 
					handlers);	
		}
	}

    @Override
	@Transactional
    public List<String> updateHandlersAccions(Long expedientTipusId, String nomArxiu, byte[] contingut) {

    	List<String> nomsHandlers = new ArrayList<String>();
		ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId).orElse(null);
		DefinicioProces definicioProces = definicioProcesRepository.findDarreraVersioAmbTipusExpedientIJbpmKey(expedientTipusId, expedientTipus.getJbpmProcessDefinitionKey());
		if (definicioProces == null) {
			throw new DeploymentException(messageHelper.getMessage("definicio.proces.actualitzar.handlers.error.definicio"));
		}

		try {
			ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(contingut));
			Map<String, byte[]> recursos = processJarHandlersFile(zipInputStream);
			// Actualitza els handlers de la darrera versió de la definició de procés
			if (!recursos.isEmpty()) {
				jbpmHelper.updateHandlers(Long.parseLong(definicioProces.getJbpmId()), recursos);
				for(String recurs : recursos.keySet()) {
					nomsHandlers.add(expedientHelper.resourceToHandler(recurs));
				}
			}
		} catch (Exception e) {
			throw new DeploymentException(messageHelper.getMessage("definicio.proces.actualitzar.error.parse"));
		}
		return nomsHandlers;

    }

    /** Tracta les entrades del .zip i assegura que el nom del recurs sigui del tipus classes/package/nom.class.*/
	private Map<String,byte[]> processJarHandlersFile(ZipInputStream zipInputStream) throws IOException {
		Map<String, byte[]> handlers = new HashMap<String, byte[]>();

		ZipEntry zipEntry = zipInputStream.getNextEntry();
		while (zipEntry != null) {
			String nom = zipEntry.getName();
			if (nom.endsWith(".class")) {
				// Assegura que tingui el nom de recurs
				nom = expedientHelper.handlerToResource(nom);
				byte[] bytes = IoUtil.readInputStream(zipInputStream, nom);
				if (bytes != null) {
					handlers.put(nom, bytes);
				}
			}
			zipEntry = zipInputStream.getNextEntry();
		}

		return handlers;
	}

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
		// Thanks to George Mournos who helped to improve this:
		ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(contingut));
		ProcessDefinition processDefinition;
		try {
			//processDefinition = ProcessDefinition.parseParZipInputStream(zipInputStream);
		} catch (Exception e) {
			throw new DeploymentException(
					messageHelper.getMessage("definicio.proces.actualitzar.error.parse"));		
		}
		exportacio.setNomDeploy(fitxer);
		exportacio.setContingutDeploy(contingut);
		WProcessDefinition jbpmProcessDefinition = null; //new WProcessDefinition(processDefinition);
		DefinicioProcesDto dto = new DefinicioProcesDto();
		dto.setJbpmKey(jbpmProcessDefinition.getKey());
		dto.setJbpmName(jbpmProcessDefinition.getName());
		exportacio.setDefinicioProcesDto(dto);
				
		return exportacio;
	}
	
	@Transactional(readOnly=true)
 	@Override
 	public List<DocumentDto> findDocumentsAmbDefinicioProcesOrdenatsPerCodi(Long definicioProcesId) {
 		DefinicioProces definicioProces = definicioProcesRepository.findById(definicioProcesId).orElse(null);
 		
 		if (definicioProces == null)
 			throw new NoTrobatException(DefinicioProces.class, definicioProcesId);
 		
 		return conversioTipusHelper.convertirList(documentRepository.findByDefinicioProcesId(definicioProces.getId()), DocumentDto.class);
 	}
	
	@Transactional(readOnly=true)
	@Override
	public ConsultaDto getConsultaById(Long id) {
		return conversioTipusHelper.convertir(consultaRepository.findById(id), ConsultaDto.class);
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<ConsultaCampDto> findCampsInformePerCampsConsulta(
			ConsultaDto consulta,
			boolean filtrarValorsPredefinits) {
		List<ConsultaCamp> consultaCamps = consultaCampRepository.findCampsConsulta(consulta.getId(), TipusConsultaCamp.INFORME);		
		
		return conversioTipusHelper.convertirList(consultaCamps, ConsultaCampDto.class);
	}
	
	@Override
	public List<HandlerDto> getHandlersPredefinits() {
		List<HandlerDto> handlers;
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("handlersPredefinits.json");			
		ObjectMapper mapper = new ObjectMapper();
		try {
			handlers = mapper.readValue(in, new TypeReference<List<HandlerDto>>(){});
		} catch (Exception e) {
			logger.error("Error llegint els handlers predefinits : " + e.getMessage(), e);
			handlers = new ArrayList<HandlerDto>();
		}
		return handlers;
	}

	@Override
	@Transactional(readOnly=true)
	public List<DefinicioProcesDto> findByEntornAndExpedientTipusOpcional(Long entornId, Long expedientTipusId) {
//		return conversioTipusHelper.convertirList(
//				definicioProcesHelper.findVersionsDefinicioProces(
//						entornId,
//						expedientTipusRepository.findById(expedientTipusId),
//						jbpmKey), DefinicioProcesDto.class);
		
		return conversioTipusHelper.convertirList(
				definicioProcesRepository.findJbfindDarreresVersionsAmbHerencia(entornId, expedientTipusId==null, expedientTipusId, true, true),
				DefinicioProcesDto.class);
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientServiceImpl.class);

	@Override
	@Transactional(readOnly=true)
	public ExpedientDocumentPinbalDto findDocumentPinbalByExpedient(Long expedientId, Long documentId) {
		/*Long expTipusId = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {ExtendedPermission.DOC_MANAGE}).getTipus().getId();
		//List<Document> documents = documentRepository.findByExpedientTipusAmbHerencia(expTipusId);
		List<Document> documents = findDocumentsOrdenatsPerCodi(expedientTipusId, definicioProcesId, ambHerencia)
		Document doc = null;
		for (Document d: documents) {
			if (d.getId().equals(documentId)) {
				doc = d;
				break;
			}
		}*/
		Document doc = documentRepository.findById(documentId).orElse(null);
		
		ExpedientDocumentPinbalDto resultat = new ExpedientDocumentPinbalDto();
		resultat.setDocumentId(doc.getId());
		resultat.setExpedientId(expedientId);
		resultat.setDocumentNom(doc.getNom());
		resultat.setDocumentCodi(doc.getCodi());
		resultat.setFinalitat(doc.getPinbalFinalitat());
		
		if (doc.getPinbalServei()!=null) {
			ServeiPinbalEntity serveiPinbalEntity = serveiPinbalRepository.findById(doc.getPinbalServei()).orElse(null);
			resultat.setNomServei(serveiPinbalEntity.getNom());
			resultat.setCodiServei(serveiPinbalEntity.getCodi());
			resultat.setPinbalServeiDocPermesCif(serveiPinbalEntity.isPinbalServeiDocPermesCif());
			resultat.setPinbalServeiDocPermesDni(serveiPinbalEntity.isPinbalServeiDocPermesDni());
			resultat.setPinbalServeiDocPermesNif(serveiPinbalEntity.isPinbalServeiDocPermesNif());
			resultat.setPinbalServeiDocPermesNie(serveiPinbalEntity.isPinbalServeiDocPermesNie());
			resultat.setPinbalServeiDocPermesPas(serveiPinbalEntity.isPinbalServeiDocPermesPas());
		}
				
		return resultat;
	}

	@Override
	public List<ExpedientTipusDto> findExpedientTipusBySistraTramitCodi(String sistraTramitCodi) {
		// TODO Auto-generated method stub
		return null;
	}

}