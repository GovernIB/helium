package es.caib.helium.logic.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.helium.logic.helper.ConversioTipusHelper;
import es.caib.helium.logic.helper.EntornHelper;
import es.caib.helium.logic.helper.PaginacioHelper;
import es.caib.helium.logic.intf.dto.CarrecDto;
import es.caib.helium.logic.intf.dto.EntornTipusAreaDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.helium.logic.intf.service.EntornTipusAreaService;
import es.caib.helium.persist.entity.AreaTipus;
import es.caib.helium.persist.entity.Entorn;
import es.caib.helium.persist.repository.TipusAreaRepository;

@Service("entornTipusAreaServiceV3")
public class EntornTipusAreaServiceImpl implements EntornTipusAreaService {

	@Resource
	private TipusAreaRepository tipusAreaRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private EntornHelper entornHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	
	@Override
	public PaginaDto<EntornTipusAreaDto> findPerDatatable(PaginacioParamsDto paginacioParams) {
		String filtre = paginacioParams.getFiltre();
		paginacioParams.afegirOrdre("codi", OrdreDireccioDto.ASCENDENT);
		logger.debug("Consultant tipus d'àrea per la datatable (filtre= " + filtre + ", paginacioParams=" + paginacioParams + ")");
		return paginacioHelper.toPaginaDto(
				tipusAreaRepository.findByFiltrePaginat(filtre == null || "".equals(filtre),
						filtre, paginacioHelper.toSpringDataPageable(paginacioParams)), EntornTipusAreaDto.class);
	}

	@Override
	public List<EntornTipusAreaDto> findTipusAreaByEntorn(Long entornId) {

		logger.debug("Consultat els tipus d'àrea en funció de l'entorn");
		return conversioTipusHelper.convertirList(tipusAreaRepository.findByEntornId(entornId),
				EntornTipusAreaDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public EntornTipusAreaDto findAmbCodi(String entornTipusAreaCodi) {
		
		logger.debug("Consultant tipus area amb codi (codi=" + entornTipusAreaCodi + ")");
		return conversioTipusHelper.convertir(tipusAreaRepository.findByCodi(entornTipusAreaCodi), EntornTipusAreaDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public EntornTipusAreaDto create(Long entornId, EntornTipusAreaDto entornTipusArea) {
		
		logger.debug("Creant nou tipus d'àrea (tipusArea=" + entornTipusArea + ")");
		AreaTipus entity = new AreaTipus();
		entity.setId(entornTipusArea.getId());
		entity.setCodi(entornTipusArea.getCodi());
		entity.setNom(entornTipusArea.getNom());
		entity.setDescripcio(entornTipusArea.getDescripcio());
		Entorn entorn = entornHelper.getEntornComprovantPermisos(entornId, true, true);
		entity.setEntorn(entorn);
		return conversioTipusHelper.convertir(tipusAreaRepository.save(entity), EntornTipusAreaDto.class);
	}

	@Override
	public EntornTipusAreaDto update(Long entornId, EntornTipusAreaDto entornTipusArea) {

		logger.debug(
				"Modificant tipus d'àrea (" +
				"entornId=" + entornId + ", " +
				"entornTipusArea=" + entornTipusArea);
		AreaTipus entity = tipusAreaRepository.findByEntornIdAndId(entornId, entornTipusArea.getId());
		entity.setNom(entornTipusArea.getNom());
		entity.setDescripcio(entornTipusArea.getDescripcio());
		return conversioTipusHelper.convertir(tipusAreaRepository.save(entity), EntornTipusAreaDto.class);
	}

	@Override
	public void delete(Long entornTipusAreaId) {
		tipusAreaRepository.delete(entornTipusAreaId);
	}

	@Override
	public EntornTipusAreaDto findAmbId(Long entornId, Long id) {
		return conversioTipusHelper.convertir(tipusAreaRepository.findByEntornIdAndId(entornId, id),
				EntornTipusAreaDto.class);
	}

	private static final Logger logger = LoggerFactory.getLogger(EntornTipusAreaServiceImpl.class);

}
