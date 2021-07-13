package es.caib.helium.logic.service;

import es.caib.helium.logic.helper.ConversioTipusServiceHelper;
import es.caib.helium.logic.helper.PaginacioHelper;
import es.caib.helium.logic.intf.WorkflowEngineApi;
import es.caib.helium.logic.intf.dto.AreaJbpmIdDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.AreaService;
import es.caib.helium.persist.entity.AreaJbpmId;
import es.caib.helium.persist.repository.AreaJbpmIdRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service("areaServiceV3")
public class AreaServiceImpl implements AreaService {
	
	@Resource
	private AreaJbpmIdRepository areaJbpmIdRepository;
	@Resource
	private ConversioTipusServiceHelper conversioTipusServiceHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private WorkflowEngineApi workflowEngineApi;
	
	@Override
	public AreaJbpmIdDto findAmbId(Long id) {
		return conversioTipusServiceHelper.convertir(areaJbpmIdRepository.findById(id), AreaJbpmIdDto.class);
	}

	@Override
	public AreaJbpmIdDto findAmbCodi(String codi) {
		return conversioTipusServiceHelper.convertir(areaJbpmIdRepository.findByCodi(codi), AreaJbpmIdDto.class);
	}
	
	@Override
	public PaginaDto<AreaJbpmIdDto> findConfigurades(PaginacioParamsDto paginacioParams) {
		logger.debug("Consultat les arees configurades");
		String filtre = paginacioParams.getFiltre();
		return paginacioHelper.toPaginaDto(
				areaJbpmIdRepository.findByFiltrePaginat(
						filtre == null || "".equals(filtre),
						filtre,
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				AreaJbpmIdDto.class);
	}
	
	@Override
	public PaginaDto<AreaJbpmIdDto> findSenseConfigurar(PaginacioParamsDto paginacioParams) {
		logger.debug("Consultat les arees sense configurar");
		String filtre = paginacioParams.getFiltre();
		List<String> noConfigurades = workflowEngineApi.findAreesByFiltre(filtre);
		noConfigurades.removeAll(areaJbpmIdRepository.findAllCodis());

//		List<String> noConfigurades = areaJbpmIdRepository.findSenseConfigurar(
//				filtre == null || "".equals(filtre),
//				filtre
//				);
		List<AreaJbpmId> arees = new ArrayList<AreaJbpmId>();
		int paginaNum = paginacioParams.getPaginaNum();
		int tamany = paginacioParams.getPaginaTamany();
		int inici = paginaNum * tamany;
		int fi = Math.min(noConfigurades.size(), inici + tamany);
		for (int foo=inici; foo < fi; foo++) {
			AreaJbpmId area = new AreaJbpmId();
			area.setCodi(noConfigurades.get(foo));
			arees.add(area);
		}
		
		Page<AreaJbpmIdDto> page = new PageImpl(arees, paginacioHelper.toSpringDataPageable(paginacioParams), noConfigurades.size());
		PaginaDto<AreaJbpmIdDto> pagina = paginacioHelper.toPaginaDto(
				page,
				AreaJbpmIdDto.class);
		
		return pagina;
	}

	@Override
	public AreaJbpmIdDto create(AreaJbpmIdDto area) {
		logger.debug("Configurant area");
		AreaJbpmId entity = new AreaJbpmId();
		entity.setId(area.getId());
		entity.setCodi(area.getCodi());
		entity.setDescripcio(area.getDescripcio());
		entity.setNom(area.getNom());
		return conversioTipusServiceHelper.convertir(areaJbpmIdRepository.save(entity), AreaJbpmIdDto.class);
	}
	
	@Override
	public AreaJbpmIdDto update(AreaJbpmIdDto area) {
		AreaJbpmId entity = areaJbpmIdRepository.findById(area.getId()).get();
		entity.setDescripcio(area.getDescripcio());
		entity.setNom(area.getNom());
		return conversioTipusServiceHelper.convertir(areaJbpmIdRepository.save(entity), AreaJbpmIdDto.class);
	}

	@Override
	public void delete(Long areaId) {
		logger.debug("Esborrant àrea (areaId=" + areaId + ")");
		areaJbpmIdRepository.deleteById(areaId);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(CarrecServiceImpl.class);
}
