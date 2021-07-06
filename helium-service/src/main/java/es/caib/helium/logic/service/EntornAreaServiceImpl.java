package es.caib.helium.logic.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.caib.helium.logic.helper.ConversioTipusHelper;
import es.caib.helium.logic.helper.EntornHelper;
import es.caib.helium.logic.helper.PaginacioHelper;
import es.caib.helium.logic.intf.dto.EntornAreaDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.PersonaDto;
import es.caib.helium.logic.intf.service.EntornAreaService;
import es.caib.helium.persist.entity.Area;
import es.caib.helium.persist.entity.AreaTipus;
import es.caib.helium.persist.repository.AreaRepository;
import es.caib.helium.persist.repository.PersonaRepository;
import es.caib.helium.persist.repository.TipusAreaRepository;

@Service("entornAreaServiceV3")
public class EntornAreaServiceImpl implements EntornAreaService {
	
	@Resource
	private AreaRepository areaRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private EntornHelper entornHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private TipusAreaRepository tipusAreaRepository;
	@Resource
	private PersonaRepository personaRepository;

	@Override
	public PaginaDto<EntornAreaDto> findPerDatatable(PaginacioParamsDto paginacioParams) {
		String filtre = paginacioParams.getFiltre();
		logger.debug("Consultant arees per la datatable (filtre= " + filtre + ", paginacioParams=" + paginacioParams + ")");
		return paginacioHelper.toPaginaDto(
				areaRepository.findByFiltrePaginat(
						filtre == null || "".equals(filtre),
						filtre,
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				EntornAreaDto.class);
	}

	@Override
	public List<EntornAreaDto> findAreesByEntorn(Long entornId) {
		return conversioTipusHelper.convertirList(areaRepository.findByEntornId(entornId), EntornAreaDto.class);
	}
	
	@Override
	public List<EntornAreaDto> findPossiblesParesByEntorn(Long entornId, Long id) {
		return conversioTipusHelper.convertirList(areaRepository.findByEntornId(entornId, id), EntornAreaDto.class);
	}
	
	@Override
	public List<PersonaDto> findPersones() {
		return conversioTipusHelper.convertirList(personaRepository.findAll(), PersonaDto.class);
	}


	@Override
	public EntornAreaDto findAmbId(Long entornId, Long id) {
		logger.debug("Consultant l'àrea amb id (id=" + id + ")");
		Area a = areaRepository.findByEntornIdAndId(entornId, id);
		return conversioTipusHelper.convertir(a, EntornAreaDto.class);
	}
	
	@Override
	public EntornAreaDto findAmbCodiByEntorn(String codi, Long entornId) {
		logger.debug("Consultar l'àrea amb codi (cod=" + codi + ") i entorn (entorn=" + entornId);
		return conversioTipusHelper.convertir(areaRepository.findByCodiAndEntornId(codi, entornId), EntornAreaDto.class);
	}
	
	@Override
	public EntornAreaDto create(Long entornId, EntornAreaDto entornArea) {
		logger.debug("Creant nova àrea (àrea=" + entornArea + ")");
		Area entity = new Area();
		entity.setId(entornArea.getId());
		entity.setCodi(entornArea.getCodi());
		entity.setNom(entornArea.getNom());
		entity.setDescripcio(entornArea.getDescripcio());
		Area pare = areaRepository.findByEntornIdAndId(entornId, entornArea.getPareId());
		entity.setPare(pare);
		AreaTipus areaTipus = tipusAreaRepository.findByEntornIdAndId(entornId, entornArea.getTipusId());
		entity.setTipus(areaTipus);
		entity.setEntorn(entornHelper.getEntornComprovantPermisos(entornId, true, true));
		return conversioTipusHelper.convertir(areaRepository.save(entity), EntornAreaDto.class);
	}
	
	@Override
	public EntornAreaDto update(Long entornId, EntornAreaDto entornArea) {
		logger.debug(
				"Modificant àrea (" +
				"entornId=" + entornId + ", " +
				"entornArea=" + entornArea);
		Area entity = areaRepository.findByEntornIdAndId(entornId, entornArea.getId());
		entity.setNom(entornArea.getNom());
		entity.setDescripcio(entornArea.getDescripcio());
		Area pare = areaRepository.findByEntornIdAndId(entornId, entornArea.getPareId());
		entity.setPare(pare);
		AreaTipus tipus = tipusAreaRepository.findByEntornIdAndId(entornId, entornArea.getTipusId());
//		tipus.setId(entornArea.getTipus().getId());
		entity.setTipus(tipus);
		entity.setEntorn(entornHelper.getEntornComprovantPermisos(entornId, true, true));
		
		return conversioTipusHelper.convertir(areaRepository.save(entity), EntornAreaDto.class);
	}

	@Override
	public void delete(Long areaId) {
		areaRepository.delete(areaId);
		
	}

	private static final Logger logger = LoggerFactory.getLogger(EntornAreaServiceImpl.class);

}
