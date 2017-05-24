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
import net.conselldemallorca.helium.core.helper.DefinicioProcesHelper;
import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.core.helper.EntornHelper;
import net.conselldemallorca.helium.core.helper.ExpedientTipusHelper;
import net.conselldemallorca.helium.core.helper.MessageHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.helper.PermisosHelper;
import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.TerminiService;
import net.conselldemallorca.helium.v3.core.repository.AccioRepository;
import net.conselldemallorca.helium.v3.core.repository.CampAgrupacioRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRegistreRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.CampTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.CampValidacioRepository;
import net.conselldemallorca.helium.v3.core.repository.ConsultaCampRepository;
import net.conselldemallorca.helium.v3.core.repository.ConsultaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.DominiRepository;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioRepository;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioValorsRepository;
import net.conselldemallorca.helium.v3.core.repository.EstatRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.FirmaTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.MapeigSistraRepository;
import net.conselldemallorca.helium.v3.core.repository.SequenciaAnyRepository;
import net.conselldemallorca.helium.v3.core.repository.TascaRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiRepository;

/**
 * Implementació del servei per a gestionar tipus d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class TerminiServiceImpl implements TerminiService {

	@Resource
	private EntornHelper entornHelper;
	@Resource
	private DefinicioProcesHelper definicioProcesHelper;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private SequenciaAnyRepository sequenciaRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private CampAgrupacioRepository campAgrupacioRepository;
	@Resource
	private CampValidacioRepository campValidacioRepository;
	@Resource
	private CampRegistreRepository campRegistreRepository;
	@Resource
	private EnumeracioRepository enumeracioRepository;
	@Resource
	private EnumeracioValorsRepository enumeracioValorsRepository;	
	@Resource
	private DominiRepository dominiRepository;
	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private ConsultaRepository consultaRepository;
	@Resource
	private ConsultaCampRepository consultaCampRepository;
	@Resource
	private AccioRepository accioRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private TerminiRepository terminiRepository;
	@Resource
	private EstatRepository estatRepository;
	@Resource
	private MapeigSistraRepository mapeigSistraRepository;
	@Resource
	private TascaRepository tascaRepository;
	@Resource
	private CampTascaRepository campTascaRepository;
	@Resource
	private DocumentTascaRepository documentTascaRepository;
	@Resource
	private FirmaTascaRepository firmaTascaRepository;
//	
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource(name = "permisosHelperV3") 
	private PermisosHelper permisosHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource(name="documentHelperV3")
	private DocumentHelperV3 documentHelper;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private JbpmHelper jbpmHelper;

	@Override
	@Transactional(readOnly = true)
	public TerminiDto findAmbId(Long terminiId) {
		logger.debug(
				"Consultant el termini amb id (" +
				"terminiId=" + terminiId +  ")");
		Termini termini = terminiRepository.findOne(terminiId);
		if (termini == null) {
			throw new NoTrobatException(Termini.class, terminiId);
		}
		return conversioTipusHelper.convertir(
				termini, 
				TerminiDto.class);
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
					expedientTipusRepository.findOne(expedientTipusId), 
					codi);
		else if (definicioProcesId != null)
			termini = terminiRepository.findByDefinicioProcesAndCodi(
					definicioProcesRepository.findById(definicioProcesId), 
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
			terminis = terminiRepository.findAmbExpedientTipus(expedientTipusId);
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
			termini.setDefinicioProces(definicioProcesRepository.findOne(definicioProcesId));
		
		return conversioTipusHelper.convertir(
				terminiRepository.save(termini),
				TerminiDto.class);
	}

	@Override
	@Transactional
	public TerminiDto update(TerminiDto dto) {
		Termini termini = terminiRepository.findOne(dto.getId());
		if (termini == null) {
			throw new NoTrobatException(Termini.class, dto.getId());
		}
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
		Termini termini = terminiRepository.findOne(terminiId);
		if (termini == null) {
			throw new NoTrobatException(Termini.class, terminiId);
		}
		
		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(
				termini.getExpedientTipus().getId());
		
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
						
		
		PaginaDto<TerminiDto> pagina = paginacioHelper.toPaginaDto(
				terminiRepository.findByFiltrePaginat(
						expedientTipusId,
						definicioProcesId,
						filtre == null || "".equals(filtre), 
						filtre, 
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				TerminiDto.class);		
		return pagina;		
	}
		
	private static final Logger logger = LoggerFactory.getLogger(TerminiServiceImpl.class);
}
