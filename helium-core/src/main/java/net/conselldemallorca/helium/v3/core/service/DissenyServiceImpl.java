/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.model.service.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.service.PermisosHelper;
import net.conselldemallorca.helium.core.model.service.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessDefinition;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.AreaDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesVersioDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
import net.conselldemallorca.helium.v3.core.api.exception.EntornNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientTipusNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.v3.core.helper.DtoConverter;
import net.conselldemallorca.helium.v3.core.helper.ExpedientLoggerHelper;
import net.conselldemallorca.helium.v3.core.helper.ServiceUtils;
import net.conselldemallorca.helium.v3.core.helper.VariableHelper;
import net.conselldemallorca.helium.v3.core.repository.AccioRepository;
import net.conselldemallorca.helium.v3.core.repository.AreaRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.CampTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.ConsultaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.EntornRepository;
import net.conselldemallorca.helium.v3.core.repository.EstatRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.TascaRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiIniciatRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiRepository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servei per gestionar les tasques de disseny.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("dissenyServiceV3")
public class DissenyServiceImpl implements DissenyService {

	@Resource
	private EntornRepository entornRepository;
	@Resource(name="dtoConverterV3")
	private DtoConverter dtoConverter;
	@Resource
	private ExpedientLoggerHelper expedientLoggerHelper;
	@Resource
	private TerminiIniciatRepository terminiIniciatRepository;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private VariableHelper variableHelper;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource
	private CampRepository campRepository;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private EstatRepository estatRepository;
	@Resource
	private ConsultaRepository consultaRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource(name="serviceUtilsV3")
	private ServiceUtils serviceUtils;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private TascaRepository tascaRepository;
	@Resource
	private CampTascaRepository campTascaRepository;
	@Resource
	private AccioRepository accioRepository;
	@Resource
	private TerminiRepository terminiRepository;
	@Resource
	private AreaRepository areaRepository;

	@Transactional(readOnly=true)
	@Override
	public AreaDto findAreaById(Long areaId) {
		return conversioTipusHelper.convertir(
				areaRepository.findById(areaId),
				AreaDto.class);
	}

	@Transactional(readOnly=true)
	@Override
	public List<EstatDto> findEstatByExpedientTipus(
			Long expedientTipusId) throws ExpedientTipusNotFoundException {
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(
				expedientTipusId);
		if (expedientTipus == null)
			throw new ExpedientTipusNotFoundException();
		return conversioTipusHelper.convertirList(
				estatRepository.findByExpedientTipus(expedientTipus),
				EstatDto.class);
	}
	
	private void getAllDefinicioProcesOrderByVersio (DefinicioProcesDto definicioProcesDto) {	
		JbpmProcessDefinition jb = jbpmHelper.getProcessDefinition(definicioProcesDto.getJbpmId());
		definicioProcesDto.setEtiqueta(jb.getProcessDefinition().getName()+" v."+jb.getVersion());
		List<DefinicioProces> mateixaKeyIEntorn = definicioProcesRepository.findByEntornIdAndJbpmKeyOrderByVersioDesc(
				definicioProcesDto.getEntorn().getId(),
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
	public DefinicioProcesDto getByInstanciaProcesById(String processInstanceId) {
		InstanciaProcesDto instanciaProces = dtoConverter.toInstanciaProcesDto(processInstanceId);;
		if (instanciaProces.getDefinicioProces() != null) {	
			return getById(instanciaProces.getDefinicioProces().getId());
		}
		return null;
	}

	@Transactional(readOnly=true)
	@Override
	public DefinicioProcesVersioDto getByVersionsInstanciaProcesById(String processInstanceId) {
		JbpmProcessInstance pi = jbpmHelper.getProcessInstance(processInstanceId);
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(pi.getProcessDefinitionId());
		DefinicioProcesVersioDto dto = new DefinicioProcesVersioDto();
		dto.setId(definicioProces.getId());
		dto.setVersio(definicioProces.getVersio());
		List<DefinicioProces> listDefProces = definicioProcesRepository.findByEntornIdAndJbpmKeyOrderByVersioDesc(definicioProces.getEntorn().getId(), definicioProces.getJbpmKey());
		for (DefinicioProces defProces : listDefProces) {
			dto.addVersioAmbEtiqueta(defProces.getVersio(), pi.getProcessInstance().getProcessDefinition().getName() + " v." + defProces.getVersio());
			if (defProces.getVersio() == definicioProces.getVersio()) {
				dto.setEtiqueta(pi.getProcessInstance().getProcessDefinition().getName() + " v." + defProces.getVersio());
			}
		}
		return dto;
	}
	
	@Transactional(readOnly=true)
	@Override
	public DefinicioProcesDto findDefinicioProcesAmbJbpmId(String jbpmId) {
		String processDefinitionId = jbpmHelper.getProcessDefinition(jbpmId).getId();
		return getById((definicioProcesRepository.findByJbpmId(processDefinitionId).getId()));
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<DefinicioProcesDto> getSubprocessosByProces(String jbpmId) {
		List<String> ids = new ArrayList<String>(); 
		afegirJbpmIdProcesAmbSubprocessos(jbpmHelper.getProcessDefinition(jbpmId), ids, false);

		List<DefinicioProcesDto> subprocessos = new ArrayList<DefinicioProcesDto>();
		for(String id: ids){
			subprocessos.add(findDefinicioProcesAmbJbpmId(id));
		}
		return subprocessos;
	}
	
	private void afegirJbpmIdProcesAmbSubprocessos(
			JbpmProcessDefinition jpd,
			List<String> jbpmIds, 
			Boolean incloure) {
		if (jpd != null) {
			List<JbpmProcessDefinition> subPds = jbpmHelper.getSubProcessDefinitions(jpd.getId());
			if (subPds != null) {
				for (JbpmProcessDefinition subPd: subPds) {
					afegirJbpmIdProcesAmbSubprocessos(subPd, jbpmIds, true);
					if (!jbpmIds.contains(subPd.getId()))
						jbpmIds.add(subPd.getId());
				}
			}
			if (!jbpmIds.contains(jpd.getId()) && incloure)
				jbpmIds.add(jpd.getId());
		}
	}
	
	@Transactional(readOnly=true)
	@Override
	public DefinicioProcesDto getById(Long id) {
		DefinicioProces definicioProces = definicioProcesRepository.findById(id);
		if (definicioProces != null) {	
			return conversioTipusHelper.convertir(definicioProces, DefinicioProcesDto.class);
		}
		return null;
	}
	
	@Transactional(readOnly=true)
	@Override
	public DefinicioProcesDto findDarreraDefinicioProcesForExpedientTipus(Long expedientTipusId) {
		ExpedientTipusDto expedientTipus = getExpedientTipusById(expedientTipusId);
		if (expedientTipus.getJbpmProcessDefinitionKey() != null) {
			DefinicioProces definicioProces = definicioProcesRepository.findDarreraVersioAmbEntornIJbpmKey(
					expedientTipus.getEntorn().getId(),
					expedientTipus.getJbpmProcessDefinitionKey());
			if (definicioProces != null) {
				DefinicioProcesDto dto = conversioTipusHelper.convertir(definicioProces, DefinicioProcesDto.class);
				Map<Long, Boolean> hasStartTask = new HashMap<Long, Boolean>();
				dto.setHasStartTask(hasStartTask(definicioProces, hasStartTask));
				getAllDefinicioProcesOrderByVersio(dto);
				return dto;
			}
		}
		return null;
	}
	
	private boolean hasStartTask(DefinicioProces definicioProces, Map<Long, Boolean> hasStartTask) {
		Long definicioProcesId = definicioProces.getId();
		Boolean result = hasStartTask.get(definicioProcesId);
		if (result == null) {
			result = new Boolean(false);
			String startTaskName = jbpmHelper.getStartTaskName(
					definicioProces.getJbpmId());
			if (startTaskName != null) {
				Tasca tasca = tascaRepository.findAmbActivityNameIProcessDefinitionId(
						startTaskName,
						definicioProces.getJbpmId());
				if (tasca != null) {
					List<CampTasca> camps = campTascaRepository.findAmbTascaOrdenats(tasca.getId());
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
		return conversioTipusHelper.convertir(expedientTipusRepository.findById(id), ExpedientTipusDto.class);
	}

	@Transactional(readOnly=true)
	@Override
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisReadUsuariActual(
			Long entornId) throws EntornNotFoundException {
		return findExpedientTipusAmbPermisosUsuariActual(
				entornId,
				new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION});
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisDissenyUsuariActual(
			Long entornId) throws EntornNotFoundException {
		return findExpedientTipusAmbPermisosUsuariActual(
				entornId,
				new Permission[] {
						ExtendedPermission.DESIGN,
						ExtendedPermission.ADMINISTRATION});
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisGestioUsuariActual(
			Long entornId) throws EntornNotFoundException {
		return findExpedientTipusAmbPermisosUsuariActual(
				entornId,
				new Permission[] {
						ExtendedPermission.MANAGE,
						ExtendedPermission.ADMINISTRATION});
	}
	
	@Transactional(readOnly=true)
	@Override
	public AccioDto findAccioAmbId(Long idAccio) {
		return conversioTipusHelper.convertir(accioRepository.findOne(idAccio), AccioDto.class);
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisCrearUsuariActual(
			Long entornId) throws EntornNotFoundException {
		return findExpedientTipusAmbPermisosUsuariActual(
				entornId,
				new Permission[] {
						ExtendedPermission.CREATE,
						ExtendedPermission.ADMINISTRATION});
	}

	private List<ExpedientTipusDto> findExpedientTipusAmbPermisosUsuariActual(
			Long entornId,
			Permission[] permisos) {
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new EntornNotFoundException();
		List<ExpedientTipus> expedientsTipus = expedientTipusRepository.findByEntornOrderByNomAsc(entorn);
		permisosHelper.filterGrantedAny(
				expedientsTipus,
				new ObjectIdentifierExtractor<ExpedientTipus>() {
					public Long getObjectIdentifier(ExpedientTipus expedientTipus) {
						return expedientTipus.getId();
					}
				},
				ExpedientTipus.class,
				permisos);
		return conversioTipusHelper.convertirList(
				expedientsTipus,
				ExpedientTipusDto.class);
	}
	
	@Transactional(readOnly=true)
	@Override
	@Cacheable(value="consultaCache", key="{#entornId, #expedientTipusId}")
	public List<ConsultaDto> findConsultesActivesAmbEntornIExpedientTipusOrdenat(
			Long entornId,
			Long expedientTipusId) throws EntornNotFoundException {
		return conversioTipusHelper.convertirList(
				consultaRepository.findConsultesActivesAmbEntornIExpedientTipusOrdenat(entornId, expedientTipusId),
				ConsultaDto.class);
	}
	
	@Transactional(readOnly=true)
	@Override
	public ConsultaDto findConsulteById(
			Long id) {
		return conversioTipusHelper.convertir(consultaRepository.findById(id), ConsultaDto.class);
	}

	@Transactional(readOnly=true)
	@Override
	public byte[] getDeploymentResource(
			Long definicioProcesId,
			String resourceName) {
		DefinicioProces definicioProces = definicioProcesRepository.findById(definicioProcesId);
		return jbpmHelper.getResourceBytes(
				definicioProces.getJbpmId(),
				resourceName);
	}

	@Transactional(readOnly=true)
	@Override
	public List<ExpedientTipusDto> findExpedientTipusAmbEntorn(EntornDto entornDto) {
		Entorn entorn = conversioTipusHelper.convertir(entornDto, Entorn.class);
		return conversioTipusHelper.convertirList(expedientTipusRepository.findByEntornOrderByCodiAsc(entorn), ExpedientTipusDto.class);
	}

	@Transactional(readOnly=true)
	@Override
	public CampDto findCampAmbDefinicioProcesICodiSimple(Long definicioProcesId, String campCodi) {
		DefinicioProces definicioProces = definicioProcesRepository.findById(definicioProcesId);
		return conversioTipusHelper.convertir(campRepository.findByDefinicioProcesAndCodi(definicioProces, campCodi), CampDto.class);
	}
	
	@Transactional
	@Override
	public void executarAccio(AccioDto accio, ExpedientDto expedient) {
		if (MesuresTemporalsHelper.isActiu()) { 
			mesuresTemporalsHelper.mesuraIniciar("Executar ACCIO" + accio.getNom(), "expedient", expedient.getTipus().getNom());
		}
		expedientLoggerHelper.afegirLogExpedientPerProces(
				expedient.getProcessInstanceId(),
				ExpedientLogAccioTipus.EXPEDIENT_ACCIO,
				accio.getJbpmAction());
		jbpmHelper.executeActionInstanciaProces(
				expedient.getProcessInstanceId(),
				accio.getJbpmAction());
		verificarFinalitzacioExpedient(expedient);
		serviceUtils.expedientIndexLuceneUpdate(expedient.getProcessInstanceId());
		if (MesuresTemporalsHelper.isActiu())
			mesuresTemporalsHelper.mesuraCalcular("Executar ACCIO" + accio.getNom(), "expedient", expedient.getTipus().getNom());
	}

	private void verificarFinalitzacioExpedient(ExpedientDto expedient) {
		JbpmProcessInstance pi = jbpmHelper.getRootProcessInstance(expedient.getProcessInstanceId());
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

	@Transactional(readOnly=true)
	@Override
	public List<?> getResultatConsultaCamp(String taskId, String processInstanceId, CampDto campDto, String textInicial, Map<String, Object> mapDelsValors) {
		String taskInstanceId = null;
		if (taskId != null) {
			taskInstanceId = String.valueOf(jbpmHelper.getTaskById(taskId).getTask().getId());
		}
		Camp camp = campRepository.findById(campDto.getId());
		try {			
			return variableHelper.getTextVariablesSimpleFontExterna(camp, textInicial, mapDelsValors, taskInstanceId, processInstanceId);
		} catch (Exception e) {
			return null;
		}
	}

	@Transactional(readOnly=true)
	@Override
	public List<TerminiDto> findTerminisAmbExpedientId(Long expedientId, String instanciaProcesId) {
		InstanciaProcesDto instanciaProces = dtoConverter.toInstanciaProcesDto(instanciaProcesId);
		return conversioTipusHelper.convertirList(
				terminiRepository.findByDefinicioProcesId(instanciaProces.getDefinicioProces().getId()),
				TerminiDto.class);
	}

	@Transactional(readOnly=true)
	@Override
	public List<TerminiIniciatDto> findIniciatsAmbExpedientId(Long expedientId, String instanciaProcesId) {
		List<TerminiIniciat> terminiIniciats = terminiIniciatRepository.findByProcessInstanceId(instanciaProcesId);
		return conversioTipusHelper.convertirList(terminiIniciats, TerminiIniciatDto.class);
	}

	@Transactional(readOnly=true)
	@Override
	public TerminiIniciatDto findIniciatAmbId(Long id) {
		return conversioTipusHelper.convertir(terminiIniciatRepository.findById(id), TerminiIniciatDto.class);
	}
}
