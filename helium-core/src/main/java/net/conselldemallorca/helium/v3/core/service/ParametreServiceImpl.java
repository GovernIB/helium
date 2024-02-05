package net.conselldemallorca.helium.v3.core.service;


import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.model.hibernate.Parametre;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParametreDto;
import net.conselldemallorca.helium.v3.core.api.service.ParametreService;
import net.conselldemallorca.helium.v3.core.repository.ParametreRepository;


/**
 * Implementació del servei de gestió de paràmetres.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ParametreServiceImpl implements ParametreService {
	
	@Resource
	private ParametreRepository parametreRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	
	
	@Transactional
	@Override
	public ParametreDto create(ParametreDto parametre) {
		logger.debug("Creant un nou paràmetre (" +
				"parametre=" + parametre + ")");
		Parametre entity = Parametre.getBuilder(
					parametre.getCodi(), 
					parametre.getNom(),
					parametre.getDescripcio(), 
					parametre.getValor()).build();
				
		return conversioTipusHelper.convertir(
				parametreRepository.save(entity),
				ParametreDto.class);
	}

	
	@Transactional
	@Override
	public ParametreDto update(
			ParametreDto parametre) {
		logger.debug("Actualitzant paràmetre existent (" +
				"paràmetre=" + parametre + ")");

		Parametre parametreEntity = parametreRepository.findOne(parametre.getId());
		parametreEntity.update(
				parametre.getCodi(),
				parametre.getNom(),
				parametre.getDescripcio(),
				parametre.getValor());
		return conversioTipusHelper.convertir(
				parametreEntity,
				ParametreDto.class);
	}


	@Transactional
	@Override
	public ParametreDto delete(
			Long id) {
		logger.debug("Esborrant paràmetre (" +
				"id=" + id +  ")");
		
		Parametre parametreEntity = parametreRepository.findOne(id);
		parametreRepository.delete(parametreEntity);
		return conversioTipusHelper.convertir(
				parametreEntity,
				ParametreDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public ParametreDto findById(Long id) {
		logger.debug("Consulta del paràmetre (" +
				"id=" + id + ")");
		
		Parametre parametreEntity = parametreRepository.findOne(id);
		ParametreDto dto = conversioTipusHelper.convertir(
				parametreEntity,
				ParametreDto.class);
		return dto;
	}
	
	@Override
	public List<ParametreDto> findAll() {
		List<Parametre> parametres = parametreRepository.findAll();
		return conversioTipusHelper.convertirList(
				parametres, 
				ParametreDto.class);
	
	}
	
	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ParametreDto> findPaginat(PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta de totes els paràmetres paginats (" +
				"paginacioParams=" + paginacioParams + ")");
		PaginaDto<ParametreDto> resposta;
		if (paginacioHelper.esPaginacioActivada(paginacioParams)) {
			resposta = paginacioHelper.toPaginaDto(
					parametreRepository.findAll(
							paginacioHelper.toSpringDataPageable(paginacioParams)),
					ParametreDto.class);
		} else {
			resposta = paginacioHelper.toPaginaDto(
					parametreRepository.findAll(
							paginacioHelper.toSpringDataSort(paginacioParams)),
					ParametreDto.class);
		}
		return resposta;
	}
	

	@Transactional
	@Override
	public ParametreDto findByCodi(String codi) {
		logger.debug("Consulta del paràmetre (" +
				"codi=" + codi + ")");
		
		Parametre parametreEntity = parametreRepository.findByCodi(codi);
		ParametreDto dto = conversioTipusHelper.convertir(
				parametreEntity,
				ParametreDto.class);
		return dto;
	}

	
	private static final Logger logger = LoggerFactory.getLogger(ParametreServiceImpl.class);

}
