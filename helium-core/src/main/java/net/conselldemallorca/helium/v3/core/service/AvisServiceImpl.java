package net.conselldemallorca.helium.v3.core.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.model.hibernate.Avis;
import net.conselldemallorca.helium.v3.core.api.dto.AvisDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.service.AvisService;
import net.conselldemallorca.helium.v3.core.repository.AvisRepository;

/**
 * Implementació del servei de gestió d'avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AvisServiceImpl implements AvisService {
	
	@Resource
	private AvisRepository avisRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	
	

	@Transactional
	@Override
	public AvisDto create(AvisDto avis) {
		logger.debug("Creant una nova avis (" +
				"avis=" + avis + ")");
		Avis entity = Avis.getBuilder(
				avis.getAssumpte(),
				avis.getMissatge(),
				avis.getDataInici(),
				avis.getDataFinal(),
				avis.getHoraInici(), 
				avis.getHoraFi(), 
				avis.getAvisNivell()).build();
		return conversioTipusHelper.convertir(
				avisRepository.save(entity),
				AvisDto.class);
	}

	@Transactional
	@Override
	public AvisDto update(
			AvisDto avis) {
		logger.debug("Actualitzant avis existent (" +
				"avis=" + avis + ")");

		Avis avisEntity = avisRepository.findOne(avis.getId());
		avisEntity.update(
				avis.getAssumpte(),
				avis.getMissatge(),
				avis.getDataInici(),
				avis.getDataFinal(),
				avis.getHoraInici(), 
				avis.getHoraFi(), 
				avis.getAvisNivell());
		return conversioTipusHelper.convertir(
				avisEntity,
				AvisDto.class);
	}

	@Transactional
	@Override
	public AvisDto updateActiva(
			Long id,
			boolean activa) {
		logger.debug("Actualitzant propietat activa d'una avis existent (" +
				"id=" + id + ", " +
				"activa=" + activa + ")");
		Avis avisEntity = avisRepository.findOne(id);
		
		avisEntity.updateActiva(activa);
		return conversioTipusHelper.convertir(
				avisEntity,
				AvisDto.class);
	}
	
	

	@Transactional
	@Override
	public AvisDto delete(
			Long id) {
		logger.debug("Esborrant avis (" +
				"id=" + id +  ")");
		
		Avis avisEntity = avisRepository.findOne(id);
		avisRepository.delete(avisEntity);

		return conversioTipusHelper.convertir(
				avisEntity,
				AvisDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public AvisDto findById(Long id) {
		logger.debug("Consulta de l'avis (" +
				"id=" + id + ")");
		
		Avis avisEntity = avisRepository.findOne(id);
		AvisDto dto = conversioTipusHelper.convertir(
				avisEntity,
				AvisDto.class);
		return dto;
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public PaginaDto<AvisDto> findPaginat(PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta de totes les avisos paginades (" +
				"paginacioParams=" + paginacioParams + ")");
		PaginaDto<AvisDto> resposta;
		if (paginacioHelper.esPaginacioActivada(paginacioParams)) {
			resposta = paginacioHelper.toPaginaDto(
					avisRepository.findAll(
							paginacioHelper.toSpringDataPageable(paginacioParams)),
					AvisDto.class);
		} else {
			resposta = paginacioHelper.toPaginaDto(
					avisRepository.findAll(
							paginacioHelper.toSpringDataSort(paginacioParams)),
					AvisDto.class);
		}

		return resposta;
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public List<AvisDto> findActive() {
		return conversioTipusHelper.convertirList(
				avisRepository.findActive(DateUtils.truncate(new Date(), Calendar.DATE)), 
				AvisDto.class);
	}	

	
	private static final Logger logger = LoggerFactory.getLogger(AvisServiceImpl.class);


}
