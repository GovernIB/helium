package es.caib.helium.logic.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.caib.helium.logic.helper.ConversioTipusHelper;
import es.caib.helium.logic.helper.EntornHelper;
import es.caib.helium.logic.helper.PaginacioHelper;
import es.caib.helium.logic.intf.dto.CarrecDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.helium.logic.intf.service.EntornCarrecService;
import es.caib.helium.persist.entity.Area;
import es.caib.helium.persist.entity.Carrec;
import es.caib.helium.persist.repository.AreaRepository;
import es.caib.helium.persist.repository.CarrecRepository;

@Service("entornCarrecServiceV3")
public class EntornCarrecServiceImpl implements EntornCarrecService {
	
	@Resource
	private CarrecRepository carrecRepository;
	@Resource
	private AreaRepository areaRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource 
	private EntornHelper entornHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	
	@Override
	public PaginaDto<CarrecDto> findPerDatatable(PaginacioParamsDto paginacioParams) {
		String filtre = paginacioParams.getFiltre();
		paginacioParams.getOrdres().remove(0);
		paginacioParams.afegirOrdre("codi", OrdreDireccioDto.ASCENDENT);
		logger.debug("Consultant càrrecs per la datatable (filtre= " + filtre + ", paginacioParams=" + paginacioParams + ")");
		return paginacioHelper.toPaginaDto(
				carrecRepository.findByFiltrePaginat(
						filtre == null || "".equals(filtre),
						filtre,
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				CarrecDto.class);
	}

	@Override
	public List<CarrecDto> findCarrecsByEntorn(Long entornId) {
		return conversioTipusHelper.convertirList(carrecRepository.findByEntornId(entornId), CarrecDto.class);
	}
	
	@Override
	public List<CarrecDto> findCarrecsByEntornAndArea(Long entornId, Long areaId) {
		return conversioTipusHelper.convertirList(carrecRepository.findByEntornIdAndAreaId(entornId, areaId), CarrecDto.class);
	}
	
	@Override
	public CarrecDto findAmbId(Long entornId, Long id) {
		logger.debug("Consultant càrrec amb id (id=" + id + ")");
		Carrec c = carrecRepository.findByEntornIdAndId(entornId, id);
		return conversioTipusHelper.convertir(c, CarrecDto.class);
	}
	
	@Override
	public CarrecDto findByEntornAndCodi(Long entornId, String codi) {
		logger.debug("Consultant càrrec amb codi (codi=" + codi + ") en l'entorn (entornId=" + entornId + ")" );
		Carrec c = carrecRepository.findByEntornIdAndCodi(entornId, codi);
		return conversioTipusHelper.convertir(c, CarrecDto.class);
	}

	@Override
	public CarrecDto create(Long entornId, CarrecDto entornCarrec) {
		logger.debug("Creant nou càrrec (càrrec=" + entornCarrec + ")");
		Carrec entity = new Carrec();
		entity.setId(entornCarrec.getId());
		entity.setCodi(entornCarrec.getCodi());
		entity.setDescripcio(entornCarrec.getDescripcio());
		entity.setNomDona(entornCarrec.getNomDona());
		entity.setNomHome(entornCarrec.getNomHome());
		entity.setTractamentDona(entornCarrec.getTractamentDona());
		entity.setTractamentHome(entornCarrec.getTractamentHome());
		Area area = areaRepository.findByEntornIdAndId(entornId, entornCarrec.getAreaId());
		entity.setArea(area);
		entity.setEntorn(entornHelper.getEntornComprovantPermisos(entornId, true, true));
		return conversioTipusHelper.convertir(carrecRepository.save(entity), CarrecDto.class);
	}
	
	@Override
	public CarrecDto update(Long entornId, CarrecDto entornCarrec) {
		logger.debug(
				"Modificant càrrec (" +
				"entornId=" + entornId + ", " +
				"entornCarrec=" + entornCarrec);
		Carrec entity = carrecRepository.findByEntornIdAndId(entornId, entornCarrec.getId());
		entity.setDescripcio(entornCarrec.getDescripcio());
		entity.setNomDona(entornCarrec.getNomDona());
		entity.setNomHome(entornCarrec.getNomHome());
		entity.setTractamentDona(entornCarrec.getTractamentDona());
		entity.setTractamentHome(entornCarrec.getTractamentHome());
		Area area = areaRepository.findByEntornIdAndId(entornId, entornCarrec.getAreaId());
		entity.setArea(area);
		entity.setEntorn(entornHelper.getEntornComprovantPermisos(entornId, true, true));
		return conversioTipusHelper.convertir(carrecRepository.save(entity), CarrecDto.class);
	}

	@Override
	public void delete(Long carrecId) {
		logger.debug("Esborrant càrrec (id=" + carrecId);
		carrecRepository.delete(carrecId);
	}

	private static final Logger logger = LoggerFactory.getLogger(EntornCarrecServiceImpl.class);

}