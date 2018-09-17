/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.ExpedientTipusHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.model.hibernate.Accio;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.AccioService;
import net.conselldemallorca.helium.v3.core.repository.AccioRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;

/**
 * Implementació del servei per a gestionar tipus d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AccioServiceImpl implements AccioService {

	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private AccioRepository accioRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;

	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public AccioDto create(
			Long expedientTipusId, 
			Long definicioProcesId,
			AccioDto accio) throws PermisDenegatException {

		logger.debug(
				"Creant nova accio per un tipus d'expedient (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"definicioProcesId =" + definicioProcesId + ", " +
				"accio=" + accio + ")");
		
		Accio entity = new Accio();
				
		entity.setCodi(accio.getCodi());
		entity.setNom(accio.getNom());
		entity.setDefprocJbpmKey(accio.getDefprocJbpmKey());
		entity.setJbpmAction(accio.getJbpmAction());
		entity.setDescripcio(accio.getDescripcio());
		entity.setPublica(accio.isPublica());
		entity.setOculta(accio.isOculta());
		entity.setRols(accio.getRols());		
		if (expedientTipusId != null)
			entity.setExpedientTipus(expedientTipusRepository.findOne(expedientTipusId));		
		if (definicioProcesId != null)
			entity.setDefinicioProces(definicioProcesRepository.findOne(definicioProcesId));		

		return conversioTipusHelper.convertir(
				accioRepository.save(entity),
				AccioDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public AccioDto update(AccioDto accio) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Modificant la accio del tipus d'expedient existent (" +
				"accio.id=" + accio.getId() + ", " +
				"accio =" + accio + ")");
		Accio entity = accioRepository.findOne(accio.getId());

		entity.setCodi(accio.getCodi());
		entity.setNom(accio.getNom());
		entity.setDefprocJbpmKey(accio.getDefprocJbpmKey());
		entity.setJbpmAction(accio.getJbpmAction());
		entity.setDescripcio(accio.getDescripcio());
		entity.setPublica(accio.isPublica());
		entity.setOculta(accio.isOculta());
		entity.setRols(accio.getRols());		
				
		return conversioTipusHelper.convertir(
				accioRepository.save(entity),
				AccioDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void delete(Long accioAccioId) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Esborrant la accio del tipus d'expedient (" +
				"accioId=" + accioAccioId +  ")");
		Accio entity = accioRepository.findOne(accioAccioId);
		accioRepository.delete(entity);	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AccioDto findAmbId(
			Long expedientTipusId, 
			Long id) throws NoTrobatException {
		logger.debug(
				"Consultant la accio del tipus d'expedient amb id (" +
				"accioId=" + id +  ")");
		Accio accio = accioRepository.findOne(id);
		if (accio == null) {
			throw new NoTrobatException(Accio.class, id);
		}
		AccioDto dto = conversioTipusHelper.convertir(
				accio,
				AccioDto.class);
		// Herencia
		ExpedientTipus tipus = expedientTipusId != null? expedientTipusRepository.findOne(expedientTipusId) : null;
		if (tipus != null && tipus.getExpedientTipusPare() != null) {
			if (tipus.getExpedientTipusPare().getId().equals(accio.getExpedientTipus().getId()))
				dto.setHeretat(true);
			else
				dto.setSobreescriu(accioRepository.findByExpedientTipusIdAndCodi(
						tipus.getExpedientTipusPare().getId(), 
						accio.getCodi()) != null);					
		}
		return dto;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AccioDto> findAll(
			Long expedientTipusId,
			Long definicioProcesId) throws NoTrobatException, PermisDenegatException {
		List<Accio> accions;
		if (expedientTipusId != null)
			accions = accioRepository.findAmbExpedientTipus(expedientTipusId);
		else
			accions = accioRepository.findAmbDefinicioProces(definicioProcesId);
		
		return conversioTipusHelper.convertirList(
									accions, 
									AccioDto.class);
	}		

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AccioDto findAmbCodi(
			Long expedientTipusId, 
			Long definicioProcesId, 
			String codi) throws NoTrobatException {
		logger.debug(
				"Consultant la accio del tipus d'expedient per codi per validar repetició (" +
				"expedientTipusId=" + expedientTipusId + ", " +
				"definicioProcesId=" + definicioProcesId + ", " +
				"codi = " + codi + ")");
		Accio accio = null;
		if (expedientTipusId != null)
			accio = accioRepository.findByExpedientTipusIdAndCodi(expedientTipusId, codi);
		else if(definicioProcesId != null)
			accio = accioRepository.findByDefinicioProcesIdAndCodi(definicioProcesId, codi);
		if (accio != null)
			return conversioTipusHelper.convertir(
					accio,
					AccioDto.class);
		else
			return null;
	}	
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<AccioDto> findPerDatatable(
			Long expedientTipusId,
			Long definicioProcesId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant les accions per al tipus d'expedient per datatable (" +
				"expedientTipusId=" + expedientTipusId + ", " +
				"definicioProcesId=" + definicioProcesId + ", " +
				"filtre=" + filtre + ")");

		ExpedientTipus expedientTipus = expedientTipusId != null? expedientTipusHelper.getExpedientTipusComprovantPermisDissenyDelegat(expedientTipusId) : null;
		// Determina si hi ha herència 
		boolean herencia = expedientTipus != null && expedientTipus.isAmbInfoPropia() && expedientTipus.getExpedientTipusPare() != null;

		Page<Accio> page = accioRepository.findByFiltrePaginat(
				expedientTipusId,
				definicioProcesId,
				filtre == null || "".equals(filtre), 
				filtre, 
				herencia,
				paginacioHelper.toSpringDataPageable(
						paginacioParams));		
		PaginaDto<AccioDto> pagina = paginacioHelper.toPaginaDto(
				page,
				AccioDto.class);				
		if (herencia) {
			// Llista d'heretats
			Set<Long> heretatsIds = new HashSet<Long>();
			for (Accio a : page.getContent())
				if ( !expedientTipusId.equals(a.getExpedientTipus().getId()))
					heretatsIds.add(a.getId());
			// Llistat d'elements sobreescrits
			Set<String> sobreescritsCodis = new HashSet<String>();
			for (Accio a : accioRepository.findSobreescrits(expedientTipus.getId())) 
				sobreescritsCodis.add(a.getCodi());
			// Completa l'informació del dto
			for (AccioDto dto : pagina.getContingut()) {
				// Sobreescriu
				if (sobreescritsCodis.contains(dto.getCodi()))
					dto.setSobreescriu(true);
				// Heretat
				if (heretatsIds.contains(dto.getId()) && ! dto.isSobreescriu())
					dto.setHeretat(true);								
			}
		}
		return pagina;		
	}
	
	private static final Logger logger = LoggerFactory.getLogger(AccioServiceImpl.class);
}