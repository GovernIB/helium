package es.caib.helium.logic.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import es.caib.helium.commons.dto.AreaBpmIdDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.AreaService;
import es.caib.helium.persistence.entity.AreaBpmId;
import es.caib.helium.persistence.repository.AreaBpmIdRepository;
import es.caib.helium.logic.helper.ConversioTipusHelper;
import es.caib.helium.logic.helper.PaginacioHelper;

@Service("areaServiceV3")
public class AreaServiceImpl implements AreaService {

	@Resource
	private AreaBpmIdRepository areaBpmIdRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;

	@Override
	public AreaBpmIdDto findAmbId(Long id) {
		return conversioTipusHelper.convertir(areaBpmIdRepository.findById(id), AreaBpmIdDto.class);
	}

	@Override
	public AreaBpmIdDto findAmbCodi(String codi) {
		return conversioTipusHelper.convertir(areaBpmIdRepository.findByCodi(codi), AreaBpmIdDto.class);
	}

	@Override
	public PaginaDto<AreaBpmIdDto> findConfigurades(PaginacioParamsDto paginacioParams) {
		logger.debug("Consultat les arees configurades");
		String filtre = paginacioParams.getFiltre();
		return paginacioHelper.toPaginaDto(
				areaBpmIdRepository.findByFiltrePaginat(
						filtre == null || "".equals(filtre),
						filtre,
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				AreaBpmIdDto.class);
	}

	@Override
	public PaginaDto<AreaBpmIdDto> findSenseConfigurar(PaginacioParamsDto paginacioParams) {
		logger.debug("Consultat les arees sense configurar");
		String filtre = paginacioParams.getFiltre();
		List<String> noConfigurades = areaBpmIdRepository.findSenseConfigurar(
				filtre == null || "".equals(filtre),
				filtre
				);
		List<AreaBpmId> arees = new ArrayList<AreaBpmId>();
		int paginaNum = paginacioParams.getPaginaNum();
		int tamany = paginacioParams.getPaginaTamany();
		int inici = paginaNum * tamany;
		int fi = Math.min(noConfigurades.size(), inici + tamany);
		for (int foo=inici; foo < fi; foo++) {
			AreaBpmId area = new AreaBpmId();
			area.setCodi(noConfigurades.get(foo));
			arees.add(area);
		}

		Page<AreaBpmIdDto> page = new PageImpl(arees, paginacioHelper.toSpringDataPageable(paginacioParams), noConfigurades.size());
		PaginaDto<AreaBpmIdDto> pagina = paginacioHelper.toPaginaDto(
				page,
				AreaBpmIdDto.class);

		return pagina;
	}

	@Override
	public AreaBpmIdDto create(AreaBpmIdDto area) {
		logger.debug("Configurant area");
		AreaBpmId entity = new AreaBpmId();
		entity.setId(area.getId());
		entity.setCodi(area.getCodi());
		entity.setDescripcio(area.getDescripcio());
		entity.setNom(area.getNom());
		return conversioTipusHelper.convertir(areaBpmIdRepository.save(entity), AreaBpmIdDto.class);
	}

	@Override
	public AreaBpmIdDto update(AreaBpmIdDto area) {
		AreaBpmId entity = areaBpmIdRepository.findById(area.getId()).orElse(null);
		entity.setDescripcio(area.getDescripcio());
		entity.setNom(area.getNom());
		return conversioTipusHelper.convertir(areaBpmIdRepository.save(entity), AreaBpmIdDto.class);
	}

	@Override
	public void delete(Long areaId) {
		logger.debug("Esborrant àrea (areaId=" + areaId + ")");
		areaBpmIdRepository.deleteById(areaId);
	}

	private static final Logger logger = LoggerFactory.getLogger(CarrecServiceImpl.class);
}
