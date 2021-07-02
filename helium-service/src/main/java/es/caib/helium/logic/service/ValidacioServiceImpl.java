/**
 * 
 */
package es.caib.helium.logic.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.emiserv.logic.intf.exception.NoTrobatException;
import es.caib.emiserv.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.helper.ConversioTipusHelper;
import es.caib.helium.logic.helper.PaginacioHelper;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.ValidacioDto;
import es.caib.helium.logic.intf.service.ValidacioService;
import es.caib.helium.persist.entity.Validacio;
import es.caib.helium.persist.repository.CampRepository;
import es.caib.helium.persist.repository.CampValidacioRepository;

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
		entity.setCamp(campRepository.findById(campId));
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
		
		Validacio entity = campValidacioRepository.findById(validacio.getId());
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
		
		Validacio validacio = campValidacioRepository.findById(validacioValidacioId);
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
		Validacio validacio = campValidacioRepository.findById(id);
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
		Validacio validacio = campValidacioRepository.findById(id);
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
