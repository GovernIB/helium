package net.conselldemallorca.helium.v3.core.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.helper.ParametreHelper;
import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.core.helper.UnitatOrganitzativaHelper;
import net.conselldemallorca.helium.core.model.hibernate.UnitatOrganitzativa;
import net.conselldemallorca.helium.v3.core.api.dto.ArbreDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ProvinciaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TipusViaDto;
import net.conselldemallorca.helium.v3.core.api.dto.UnitatOrganitzativaDto;
import net.conselldemallorca.helium.v3.core.api.dto.UnitatOrganitzativaFiltreDto;
import net.conselldemallorca.helium.v3.core.api.service.UnitatOrganitzativaService;
import net.conselldemallorca.helium.v3.core.repository.UnitatOrganitzativaRepository;

/**
 * ImplementaciÃ³ del servei de gestiÃ³ d'avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class UnitatOrganitzativaServiceImpl implements UnitatOrganitzativaService {
	
	@Resource
	private ParametreHelper parametreHelper;
	@Resource
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
	@Resource
	private PluginHelper pluginHelper;
	
	@Transactional
	@Override
	public UnitatOrganitzativaDto create(UnitatOrganitzativaDto unitatDto) {
		logger.debug("Creant una nova unitat organitzativa (" + unitatDto + ")");
		UnitatOrganitzativa entity = UnitatOrganitzativa.getBuilder(
				unitatDto.getCodi(), // codi,
				unitatDto.getDenominacio(),//String denominacio,
				unitatDto.getNifCif(),//String nifCif,
				unitatDto.getCodiUnitatSuperior(),//String codiUnitatSuperior,
				unitatDto.getCodiUnitatArrel(),//String codiUnitatArrel,
				unitatDto.getDataCreacioOficial(),//Date dataCreacioOficial,
				unitatDto.getDataSupressioOficial(),//Date dataSupressioOficial,
				unitatDto.getDataExtincioFuncional(),//Date dataExtincioFuncional,
				unitatDto.getDataAnulacio(),//Date dataAnulacio,
				unitatDto.getEstat(),//String estat,
				unitatDto.getCodiPais(),//String codiPais,
				unitatDto.getCodiComunitat(),//String codiComunitat,
				unitatDto.getCodiProvincia(),//String codiProvincia,
				unitatDto.getCodiPostal(),//String codiPostal,
				unitatDto.getNomLocalitat(),//String nomLocalitat,
				unitatDto.getLocalitat(),//String localitat,
				unitatDto.getAdressa(),//String adressa,
				unitatDto.getTipusVia(),//Long tipusVia,
				unitatDto.getNomVia(),//String nomVia,
				unitatDto.getNumVia())//String numVia)
				.build();
		
		unitatOrganitzativaRepository.save(entity);
		unitatOrganitzativaRepository.flush();
		return unitatDto;
	}
	

	@Transactional
	@Override
	public UnitatOrganitzativaDto update(
			UnitatOrganitzativaDto unitatDto) {
		logger.debug("Actualitzant unitat organitzativa existent (" + unitatDto + ")");

		UnitatOrganitzativa unitatOrganitzativaEntity = unitatOrganitzativaRepository.findOne(unitatDto.getId());
		unitatOrganitzativaEntity.update(
				unitatDto.getCodi(),
				unitatDto.getDenominacio(),
				unitatDto.getNifCif(),
				unitatDto.getCodiUnitatSuperior(),
				unitatDto.getCodiUnitatArrel(), 
				unitatDto.getDataCreacioOficial(),
				unitatDto.getDataSupressioOficial(),
				unitatDto.getDataExtincioFuncional(),
				unitatDto.getDataAnulacio(),
				unitatDto.getEstat(), 
				unitatDto.getCodiPais(),
				unitatDto.getCodiComunitat(),
				unitatDto.getCodiProvincia() ,
				unitatDto.getCodiPostal() ,
				unitatDto.getNomLocalitat() ,
				unitatDto.getLocalitat() ,
				unitatDto.getAdressa() ,
				unitatDto.getTipusVia() ,
				unitatDto.getNomVia() ,
				unitatDto.getNumVia());
		return conversioTipusHelper.convertir(
				unitatOrganitzativaEntity,
				UnitatOrganitzativaDto.class);
	}
	

	@Transactional
	@Override
	public UnitatOrganitzativaDto delete(
			Long id) {
		logger.debug("Esborrant unitat organitzativa (" +
				"id=" + id +  ")");
		
		UnitatOrganitzativa unitatOrganitzativaEntity = unitatOrganitzativaRepository.findOne(id);
		unitatOrganitzativaRepository.delete(unitatOrganitzativaEntity);

		return conversioTipusHelper.convertir(
				unitatOrganitzativaEntity,
				UnitatOrganitzativaDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public UnitatOrganitzativaDto findById(Long id) {
		logger.debug("Consulta de l'unitat organitzativa (" +
				"id=" + id + ")");
		
		UnitatOrganitzativa unitatOrganitzativaEntity = unitatOrganitzativaRepository.findOne(id);
		UnitatOrganitzativaDto dto = conversioTipusHelper.convertir(
				unitatOrganitzativaEntity,
				UnitatOrganitzativaDto.class);
		return dto;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<UnitatOrganitzativaDto> findAmbFiltrePaginat(
			UnitatOrganitzativaFiltreDto filtreDto,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consultant unitats organitzatives per la datatable (" +
				"filtre=" + filtreDto + ", " +
				"paginacioParams=" + paginacioParams + ")");
		String estatBBDD = null;
		if(filtreDto.getEstat()!=null)
			 estatBBDD = unitatOrganitzativaHelper.getEstatBBDD(filtreDto.getEstat());
		PaginaDto<UnitatOrganitzativaDto> pagina = paginacioHelper.toPaginaDto(
				unitatOrganitzativaRepository.findByFiltrePaginat(
						filtreDto.getCodi() == null || filtreDto.getCodi().isEmpty(),
						filtreDto.getCodi(),
						filtreDto.getDenominacio() == null || filtreDto.getDenominacio().isEmpty(),
						filtreDto.getDenominacio(),
						filtreDto.getCif() == null || filtreDto.getCif().isEmpty(),
						filtreDto.getCif(),
						filtreDto.getCodiUnitatSuperior() == null || filtreDto.getCodiUnitatSuperior().isEmpty(),
						filtreDto.getCodiUnitatSuperior(),
						estatBBDD==null,
						estatBBDD,
						paginacioParams.getFiltre() == null || paginacioParams.getFiltre().isEmpty(),
						paginacioParams.getFiltre(),
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				UnitatOrganitzativaDto.class);
		
		return pagina;
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public List<UnitatOrganitzativaDto> findByEntitat(
			String entitatCodi) { 
		UnitatOrganitzativa entitat = unitatOrganitzativaRepository.findByCodi(entitatCodi);
		return conversioTipusHelper.convertirList(
				unitatOrganitzativaRepository.findByCodiOrderByDenominacioAsc(entitat.getCodi()),
				UnitatOrganitzativaDto.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public UnitatOrganitzativaDto findByCodi(
			String unitatOrganitzativaCodi) {
	
		return unitatOrganitzativaHelper.toDto(
				unitatOrganitzativaRepository.findByCodi(unitatOrganitzativaCodi));
	}
	
	@Override
	@Transactional
	public void synchronize(Long entitatId) {
		UnitatOrganitzativa unitatOrganitzativa = unitatOrganitzativaRepository.findOne(entitatId);
		unitatOrganitzativaHelper.sincronizarOActualizar(unitatOrganitzativa);		
		// if this is first synchronization set current date as a date of first
		// sinchronization and the last actualization
		if (parametreHelper.getDataSincronitzacioUos() == null) {
			parametreHelper.setDataActualitzacioUos(new Date());
			parametreHelper.setDataSincronitzacioUos(new Date());
		} else {
			// if this is not the first synchronization only change date of actualization
			parametreHelper.setDataActualitzacioUos(new Date());
		}
//		TODO després mirar lo de evict cache
//		cacheHelper.evictUnitatsOrganitzativesFindArbreByPare(unitatOrganitzativa.getCodiDir3Entitat());
	}
	
	@Override
	@Transactional
	public ArbreDto<UnitatOrganitzativaDto> findTree(Long id) {
		UnitatOrganitzativa entitat = unitatOrganitzativaRepository.findOne(id);
		return unitatOrganitzativaHelper.unitatsOrganitzativesFindArbreByPareAndEstatVigent(entitat.getCodi());
	}

	@Override
	@Transactional
	public List<UnitatOrganitzativaDto> getObsoletesFromWS(Long entitatId) {
		return unitatOrganitzativaHelper.getObsoletesFromWS(entitatId);		
	}


	@Override
	@Transactional
	public List<UnitatOrganitzativaDto> getVigentsFromWebService(Long entidadId) {
		return unitatOrganitzativaHelper.getVigentsFromWebService(entidadId);
	}

	@Override
	@Transactional
	public boolean isFirstSincronization(Long entidadId) {
		String codiArrel = parametreHelper.getArrelUos();
		UnitatOrganitzativa entitat = codiArrel != null ?
				unitatOrganitzativaRepository.findByCodi(codiArrel)
				:null;
		if (entitat != null && parametreHelper.getDataSincronitzacioUos() == null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	@Transactional
	public List<UnitatOrganitzativaDto> predictFirstSynchronization(Long entitatId) {
		return unitatOrganitzativaHelper.predictFirstSynchronization(entitatId);		
	}

	@Override
	@Transactional(readOnly = true)
	public List<UnitatOrganitzativaDto> findByEntitatAndFiltre(
			String entitatCodi, 
			String filtre, 
			boolean ambArrel, 
			boolean nomesAmbBusties) {
		UnitatOrganitzativa entitat = unitatOrganitzativaRepository.findByCodi(entitatCodi);
		List<UnitatOrganitzativa> unitats = null;
		
		if (nomesAmbBusties) {
			unitats = unitatOrganitzativaRepository.findByCodiUnitatAndCodiAndDenominacioFiltreNomesAmbBusties(
					entitat.getCodi(),
					filtre == null || filtre.isEmpty(), 
					filtre != null ? filtre : "",
					ambArrel);		
		} else {
			unitats = unitatOrganitzativaRepository.findByCodiUnitatAndCodiAndDenominacioFiltre(
					entitat.getCodi(),
					filtre == null || filtre.isEmpty(), 
					filtre != null ? filtre : "",
					ambArrel);
		}	
		return conversioTipusHelper.convertirList(
				unitats,
				UnitatOrganitzativaDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UnitatOrganitzativaDto> findByEntitatAndCodiUnitatSuperiorAndFiltre(
			String entitatCodi, 
			String codiUnitatSuperior, 
			String filtre, 
			boolean ambArrel, 
			boolean nomesAmbBusties) {
		UnitatOrganitzativa entitat = unitatOrganitzativaRepository.findByCodi(entitatCodi);
		List<UnitatOrganitzativa> unitats = null;
		
		if (nomesAmbBusties) {
			unitats = unitatOrganitzativaRepository.findByCodiUnitatAmbCodiUnitatSuperiorAndCodiAndDenominacioFiltreNomesAmbBusties(
					entitat.getCodi(),
					codiUnitatSuperior,
					filtre == null || filtre.isEmpty(), 
					filtre != null ? filtre : "",
					ambArrel);
			
		} else {
			unitats = unitatOrganitzativaRepository.findByCodiUnitatAmbCodiUnitatSuperiorAndCodiAndDenominacioFiltre(
					entitat.getCodi(),
					codiUnitatSuperior,
					filtre == null || filtre.isEmpty(), 
					filtre != null ? filtre : "",
					ambArrel);
		}

		return conversioTipusHelper.convertirList(
				unitats,
				UnitatOrganitzativaDto.class);
	}
	
	@Override
	@Transactional
	public UnitatOrganitzativaDto getLastHistoricos(UnitatOrganitzativaDto uo) {
		return unitatOrganitzativaHelper.getLastHistoricos(uo);
	}

	@Override
	@Transactional
	public List<UnitatOrganitzativaDto> getNewFromWS(Long entitatId) {
		return unitatOrganitzativaHelper.getNewFromWS(entitatId);		

	}	
	
	@Override
	@Transactional(readOnly = true)
	public List<UnitatOrganitzativaDto> findByCodiAndDenominacioFiltre(
			String filtre) {
		
		List<UnitatOrganitzativa> unitats = null;
		
		unitats = unitatOrganitzativaRepository.findByCodiAndDenominacioFiltre(
				filtre == null || filtre.isEmpty(), 
				filtre != null ? filtre : "");		
		
		return conversioTipusHelper.convertirList(
				unitats,
				UnitatOrganitzativaDto.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<UnitatOrganitzativaDto> findAll() {
		
		return conversioTipusHelper.convertirList(
				unitatOrganitzativaHelper.findAll(),
				UnitatOrganitzativaDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public void populateDadesExternesUO(UnitatOrganitzativaDto unitat, List<TipusViaDto> tipusViaList, List<ProvinciaDto> provincies) {
		unitatOrganitzativaHelper.populateDadesExternesUO(unitat, tipusViaList, provincies);
	}

	private static final Logger logger = LoggerFactory.getLogger(CarrecServiceImpl.class);
}
