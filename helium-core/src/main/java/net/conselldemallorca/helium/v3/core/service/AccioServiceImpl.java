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
import net.conselldemallorca.helium.core.model.hibernate.Accio;
import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.AccioService;
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
import net.conselldemallorca.helium.v3.core.repository.ReassignacioRepository;
import net.conselldemallorca.helium.v3.core.repository.SequenciaAnyRepository;
import net.conselldemallorca.helium.v3.core.repository.TascaRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiRepository;

/**
 * Implementació del servei per a gestionar tipus d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AccioServiceImpl implements AccioService {

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
	private ReassignacioRepository reassignacioRepository;
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
	public AccioDto findAmbId(Long id) throws NoTrobatException {
		logger.debug(
				"Consultant la accio del tipus d'expedient amb id (" +
				"accioId=" + id +  ")");
		Accio accio = accioRepository.findOne(id);
		if (accio == null) {
			throw new NoTrobatException(Accio.class, id);
		}
		return conversioTipusHelper.convertir(
				accio,
				AccioDto.class);
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
		
		PaginaDto<AccioDto> pagina = paginacioHelper.toPaginaDto(
				accioRepository.findByFiltrePaginat(
						expedientTipusId,
						definicioProcesId,
						filtre == null || "".equals(filtre), 
						filtre, 
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				AccioDto.class);		
		return pagina;		
	}
	
	private static final Logger logger = LoggerFactory.getLogger(AccioServiceImpl.class);
}