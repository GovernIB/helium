package es.caib.helium.logic.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.caib.helium.logic.helper.ConversioTipusHelper;
import es.caib.helium.logic.helper.PaginacioHelper;
import es.caib.helium.logic.intf.dto.AreaMembreDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.helium.logic.intf.service.EntornAreaMembreService;
import es.caib.helium.persist.entity.Area;
import es.caib.helium.persist.entity.AreaMembre;
import es.caib.helium.persist.entity.Carrec;
import es.caib.helium.persist.repository.AreaMembreRepository;
import es.caib.helium.persist.repository.AreaRepository;
import es.caib.helium.persist.repository.CarrecRepository;

@Service("entornAreaMembreServiceV3")
public class EntornAreaMembreServiceImpl implements EntornAreaMembreService {

	@Resource
	private AreaMembreRepository areaMembreRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private AreaRepository areaRepository;
	@Resource
	private CarrecRepository carrecRepository;
	@Resource
	private PaginacioHelper paginacioHelper;
	
	@Override
	public PaginaDto<AreaMembreDto> findPerDatatable(Long entornAreaId, PaginacioParamsDto paginacioParams) {
		String filtre = paginacioParams.getFiltre();
		paginacioParams.getOrdres().remove(0);
		paginacioParams.afegirOrdre("codi", OrdreDireccioDto.ASCENDENT);
		logger.debug("Consultant arees per la datatable (filtre= " + filtre + ", paginacioParams=" + paginacioParams + ")");
		PaginaDto<AreaMembreDto>  pagina = paginacioHelper.toPaginaDto(
				areaMembreRepository.findByFiltrePaginat(
						entornAreaId, 
						filtre == null || "".equals(filtre),
						filtre,
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				AreaMembreDto.class);
		pagina.getContingut();
		
		for (AreaMembreDto membre : pagina.getContingut()) {
			List<Carrec> carrecs = carrecRepository.findByPersonaCodi(membre.getCodi());
			if (carrecs.size() > 0) {
				membre.setCarrec(carrecs.get(0).getCodi());
			}
		}
		return pagina;
	}

	@Override
	public AreaMembreDto findAmbCodiAndAreaId(String codi, Long areaId) {
		return conversioTipusHelper.convertir(areaMembreRepository.findByCodiAndAreaId(codi, areaId),
				AreaMembreDto.class);
	}

	@Override
	public AreaMembreDto create(Long entornId, Long carrecId, AreaMembreDto areaMembre) {
		logger.debug("Creant nou membre per l'àrea (àrea membre=" + areaMembre + ")");
		AreaMembre entity = new AreaMembre();
		entity.setId(areaMembre.getAreaId());
		entity.setCodi(areaMembre.getCodi());
		Area area = areaRepository.findByEntornIdAndId(entornId, areaMembre.getAreaId());
		entity.setArea(area);

		// Assignar la persona al càrrec.
		if (carrecId != null) {
			Carrec carrec = carrecRepository.findByEntornIdAndId(entornId, carrecId);
			carrec.setPersonaCodi(areaMembre.getCodi());
			carrecRepository.save(carrec);
		}
		return conversioTipusHelper.convertir(areaMembreRepository.save(entity), AreaMembreDto.class);
	}

	@Override
	public void delete(Long entornAreaId, Long id) {
		logger.debug("Esborrant membre amb id " + id + " de l'àrea " + entornAreaId);
		areaMembreRepository.delete(id);
	}

	private static final Logger logger = LoggerFactory.getLogger(EntornAreaMembreServiceImpl.class);

}
