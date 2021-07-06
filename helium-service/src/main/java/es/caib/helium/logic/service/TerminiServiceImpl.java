/**
 * 
 */
package es.caib.helium.logic.service;

import es.caib.helium.logic.helper.ConversioTipusHelper;
import es.caib.helium.logic.helper.ExpedientTipusHelper;
import es.caib.helium.logic.helper.HerenciaHelper;
import es.caib.helium.logic.helper.PaginacioHelper;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.TerminiDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.intf.service.TerminiService;
import es.caib.helium.persist.entity.ExpedientTipus;
import es.caib.helium.persist.entity.Termini;
import es.caib.helium.persist.repository.DefinicioProcesRepository;
import es.caib.helium.persist.repository.ExpedientTipusRepository;
import es.caib.helium.persist.repository.TerminiRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementació del servei per a gestionar tipus d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class TerminiServiceImpl implements TerminiService {

	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private TerminiRepository terminiRepository;

	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;

	@Override
	@Transactional(readOnly = true)
	public TerminiDto findAmbId(
			Long expedientTipusId, 
			Long terminiId) throws NoTrobatException {
		logger.debug(
				"Consultant el termini amb id (" +
				"expedientTiusId=" + expedientTipusId + "," +
				"terminiId=" + terminiId +  ")");
		Termini termini = terminiRepository.findById(terminiId)
				.orElseThrow(() -> new NoTrobatException(Termini.class, terminiId));
		TerminiDto dto = conversioTipusHelper.convertir(
				termini,
				TerminiDto.class);
		// Herencia
		ExpedientTipus tipus = expedientTipusId != null? expedientTipusRepository.getById(expedientTipusId) : null;
		if (tipus != null && tipus.getExpedientTipusPare() != null) {
			if (tipus.getExpedientTipusPare().getId().equals(termini.getExpedientTipus().getId()))
				dto.setHeretat(true);
			else
				dto.setSobreescriu(terminiRepository.findByExpedientTipusAndCodi(
						tipus.getExpedientTipusPare(), 
						termini.getCodi()) != null);					
		}
		return dto;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public TerminiDto findAmbCodi(
			Long expedientTipusId, 
			Long definicioProcesId, 
			String codi) {
		logger.debug(
				"Consultant el termini amb codi (" +
				"expedientTipusId=" + expedientTipusId + ", " + 
				"definicioProcesId=" + definicioProcesId + ", " + 
				"codi=" + codi +  ")");
		Termini termini = null;
		TerminiDto ret = null;
		if (expedientTipusId != null)
			termini = terminiRepository.findByExpedientTipusAndCodi(
					expedientTipusRepository.getById(expedientTipusId),
					codi);
		else if (definicioProcesId != null)
			termini = terminiRepository.findByDefinicioProcesAndCodi(
					definicioProcesRepository.getById(definicioProcesId),
					codi);
		if (termini != null)
			ret = conversioTipusHelper.convertir(
					termini,
					TerminiDto.class);
		return ret;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<TerminiDto> findAll(
			Long expedientTipusId, 
			Long definicioProcesId) throws NoTrobatException, PermisDenegatException {
		List<Termini> terminis;
		if (expedientTipusId != null)
			terminis = terminiRepository.findByExpedientTipus(expedientTipusId);
		else
			terminis = terminiRepository.findByDefinicioProcesId(definicioProcesId);
		return conversioTipusHelper.convertirList(
									terminis, 
									TerminiDto.class);
	}	

	@Override
	@Transactional
	public TerminiDto create(
			Long expedientTipusId, 
			Long definicioProcesId,
			TerminiDto dto) {
		Termini termini = new Termini();
		termini.setCodi(dto.getCodi());
		termini.setNom(dto.getNom());
		termini.setDescripcio(dto.getDescripcio());
		termini.setDuradaPredefinida(dto.isDuradaPredefinida());
		termini.setAnys(dto.getAnys());
		termini.setMesos(dto.getMesos());
		termini.setDies(dto.getDies());
		termini.setLaborable(dto.isLaborable());
		termini.setManual(dto.isManual());
		termini.setDiesPrevisAvis(dto.getDiesPrevisAvis());
		termini.setAlertaPrevia(dto.isAlertaPrevia());
		termini.setAlertaFinal(dto.isAlertaFinal());
		termini.setAlertaCompletat(dto.isAlertaCompletat());
		
		if (expedientTipusId != null)
			termini.setExpedientTipus(
					expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(
												expedientTipusId));
		if (definicioProcesId != null)
			termini.setDefinicioProces(definicioProcesRepository.getById(definicioProcesId));
		
		return conversioTipusHelper.convertir(
				terminiRepository.save(termini),
				TerminiDto.class);
	}

	@Override
	@Transactional
	public TerminiDto update(TerminiDto dto) {
		Termini termini = terminiRepository.findById(dto.getId())
				.orElseThrow(() -> new NoTrobatException(Termini.class, dto.getId()));
		if (termini.getExpedientTipus() != null)
			expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(
					termini.getExpedientTipus().getId());
		
		termini.setCodi(dto.getCodi());
		termini.setNom(dto.getNom());
		termini.setDescripcio(dto.getDescripcio());
		termini.setDuradaPredefinida(dto.isDuradaPredefinida());
		termini.setAnys(dto.getAnys());
		termini.setMesos(dto.getMesos());
		termini.setDies(dto.getDies());
		termini.setLaborable(dto.isLaborable());
		termini.setManual(dto.isManual());
		termini.setDiesPrevisAvis(dto.getDiesPrevisAvis());
		termini.setAlertaPrevia(dto.isAlertaPrevia());
		termini.setAlertaFinal(dto.isAlertaFinal());
		termini.setAlertaCompletat(dto.isAlertaCompletat());
		
		return conversioTipusHelper.convertir(
				terminiRepository.save(termini),
				TerminiDto.class);
	}

	@Override
	@Transactional
	public void delete(Long terminiId) throws NoTrobatException, PermisDenegatException {
		Termini termini = terminiRepository.findById(terminiId)
				.orElseThrow(() -> new NoTrobatException(Termini.class, terminiId));

		Long expedientTipusId;
		if (termini.getExpedientTipus() != null) {
			expedientTipusId = termini.getExpedientTipus().getId();
		} else  {
			if (termini.getDefinicioProces().getExpedientTipus() != null) 
				expedientTipusId = termini.getDefinicioProces().getExpedientTipus().getId();
			else 
				expedientTipusId = null;
		}
		if (expedientTipusId != null)
			expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(
					expedientTipusId);		
		terminiRepository.delete(termini);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<TerminiDto> findPerDatatable(
			Long expedientTipusId,
			Long definicioProcesId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant els terminis per al tipus d'expedient per datatable (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"definicioProcesId =" + definicioProcesId + ", " +
				"filtre=" + filtre + ")");
						
		ExpedientTipus expedientTipus = expedientTipusId != null? expedientTipusHelper.getExpedientTipusComprovantPermisDissenyDelegat(expedientTipusId) : null;
		
		// Determina si hi ha herència 
		boolean ambHerencia = HerenciaHelper.ambHerencia(expedientTipus);
		
		Page<Termini> page = terminiRepository.findByFiltrePaginat(
				expedientTipusId,
				definicioProcesId,
				filtre == null || "".equals(filtre), 
				filtre, 
				ambHerencia,
				paginacioHelper.toSpringDataPageable(
						paginacioParams)); 
		
		PaginaDto<TerminiDto> pagina = paginacioHelper.toPaginaDto(
				page,
				TerminiDto.class);
		
		if (ambHerencia) {
			// Llista d'heretats
			Set<Long> heretatsIds = new HashSet<Long>();
			for (Termini t : page.getContent())
				if ( !expedientTipusId.equals(t.getExpedientTipus().getId()))
					heretatsIds.add(t.getId());
			// Llistat d'elements sobreescrits
			Set<String> sobreescritsCodis = new HashSet<String>();
			for (Termini t : terminiRepository.findSobreescrits(expedientTipus.getId())) 
			{
				sobreescritsCodis.add(t.getCodi());
			}
			// Completa l'informació del dto
			for (TerminiDto dto : pagina.getContingut()) {
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
		
	private static final Logger logger = LoggerFactory.getLogger(TerminiServiceImpl.class);
}
