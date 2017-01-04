/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.model.hibernate.Validacio;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ValidacioDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.ValidacioService;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.CampValidacioRepository;

/**
 * Servei per gestionar les validacions
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ValidacioServiceImpl implements ValidacioService{
	
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private CampValidacioRepository campValidacioRepository;
	@Resource
	private CampRepository campRepository;

	@Override
	@Transactional
	public ValidacioDto validacioCreate(
			Long campId, 
			ValidacioDto validacio) throws PermisDenegatException {

		logger.debug(
				"Creant nova validacio per un camp de tipus d'expedient (" +
				"campId =" + campId + ", " +
				"validacio=" + validacio + ")");

		Validacio entity = new Validacio();
		entity.setCamp(campRepository.findOne(campId));
		entity.setExpressio(validacio.getExpressio());
		entity.setMissatge(validacio.getMissatge());
		entity.setOrdre(campValidacioRepository.getNextOrdre(campId));
		
		return conversioTipusHelper.convertir(
				campValidacioRepository.save(entity),
				ValidacioDto.class);
	}

	@Override
	@Transactional
	public ValidacioDto validacioUpdate(ValidacioDto validacio) 
						throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Modificant la validacio del camp del tipus d'expedient existent (" +
				"validacio.id=" + validacio.getId() + ", " +
				"validacio =" + validacio + ")");
		
		Validacio entity = campValidacioRepository.findOne(validacio.getId());
		entity.setExpressio(validacio.getExpressio());
		entity.setMissatge(validacio.getMissatge());
		
		return conversioTipusHelper.convertir(
				campValidacioRepository.save(entity),
				ValidacioDto.class);
	}

	@Override
	@Transactional
	public void validacioDelete(Long validacioValidacioId) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Esborrant la validacio del tipus d'expedient (" +
				"validacioId=" + validacioValidacioId +  ")");
		
		Validacio validacio = campValidacioRepository.findOne(validacioValidacioId);
		campValidacioRepository.delete(validacio);	
		campValidacioRepository.flush();
		
		reordenarValidacions(validacio.getCamp().getId());
	}

	/** Funció per reasignar el valor d'ordre dins d'una agrupació de variables. */
	private void reordenarValidacions(Long campId) {
		List<Validacio> validacios = campValidacioRepository.findAmbCampOrdenats(campId);		
		int i = 0;
		for (Validacio validacio: validacios)
			validacio.setOrdre(i++);
	}

	@Override
	@Transactional
	public boolean validacioMourePosicio(
			Long id, 
			int posicio) {
		boolean ret = false;
		Validacio validacio = campValidacioRepository.findOne(id);
		if (validacio != null) {
			List<Validacio> validacions = campValidacioRepository.findAmbCampOrdenats(validacio.getCamp().getId());
			if(posicio != validacions.indexOf(validacio)) {
				validacions.remove(validacio);
				validacions.add(posicio, validacio);
				int i = 0;
				for (Validacio c : validacions) {
					c.setOrdre(i++);
				}
			}
		}
		return ret;
	}

	@Override
	public ValidacioDto validacioFindAmbId(Long id) throws NoTrobatException {
		logger.debug(
				"Consultant la validacio del camp del tipus d'expedient amb id (" +
				"validacioId=" + id +  ")");
		Validacio validacio = campValidacioRepository.findOne(id);
		if (validacio == null) {
			throw new NoTrobatException(Validacio.class, id);
		}
		return conversioTipusHelper.convertir(
				validacio,
				ValidacioDto.class);
	}
		
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ValidacioDto> validacioFindPerDatatable(
			Long campId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant els validacios per al tipus d'expedient per datatable (" +
				"entornId=" + campId + ", " +
				"filtre=" + filtre + ")");
						
		
		return paginacioHelper.toPaginaDto(
				campValidacioRepository.findByFiltrePaginat(
						campId,
						filtre == null || "".equals(filtre), 
						filtre, 
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				ValidacioDto.class);		
	}	
	
	private static final Logger logger = LoggerFactory.getLogger(ValidacioServiceImpl.class);
}