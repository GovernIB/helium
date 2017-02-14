/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.DefinicioProcesHelper;
import net.conselldemallorca.helium.core.helper.EntornHelper;
import net.conselldemallorca.helium.core.helper.ExpedientTipusHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.FirmaTasca;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.FirmaTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exportacio.DefinicioProcesExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.DefinicioProcesExportacioCommandDto;
import net.conselldemallorca.helium.v3.core.api.service.DefinicioProcesService;
import net.conselldemallorca.helium.v3.core.api.service.Jbpm3HeliumService;
import net.conselldemallorca.helium.v3.core.repository.AccioRepository;
import net.conselldemallorca.helium.v3.core.repository.CampAgrupacioRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRegistreRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.CampTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.ConsultaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.DominiRepository;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.FirmaTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.TascaRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiRepository;

/**
 * Implementació del servei per a gestionar definicions de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class DefinicioProcesServiceImpl implements DefinicioProcesService {
	
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private DominiRepository dominiRepository;
	@Resource
	private TascaRepository tascaRepository;
	@Resource
	private ConsultaRepository consultaRepository;
	@Resource
	private CampTascaRepository campTascaRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private DocumentTascaRepository documentTascaRepository;
	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private FirmaTascaRepository firmaTascaRepository;
	@Resource
	private TerminiRepository terminiRepository;
	@Resource
	private CampAgrupacioRepository campAgrupacioRepository;
	@Resource
	private AccioRepository accioRepository;
	@Resource
	private EnumeracioRepository enumeracioRepository;
	@Resource
	private CampRegistreRepository campRegistreRepository;
	@Autowired
	protected Jbpm3HeliumService jbpm3HeliumService;
	@Autowired
	private ExpedientTipusRepository expedientTipusRepository;

	@Resource
	private DefinicioProcesHelper definicioProcesHelper;
	@Resource
	private EntornHelper entornHelper;
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public DefinicioProcesDto findById(
			Long definicioProcesId) {
		logger.debug(
				"Consultant la definicio proces amb id (" +
				"definicioProcesId = " + definicioProcesId + ")");
		DefinicioProces definicioProces = definicioProcesRepository.findById(definicioProcesId);

		return conversioTipusHelper.convertir(
				definicioProces,
				DefinicioProcesDto.class);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public DefinicioProcesDto findByEntornIdAndJbpmKey(
			Long entornId, 
			String jbpmKey) {
		logger.debug(
				"Consultant darrera versió definicio proces amb entornId i jbpmKey (" +
				"entornId=" + entornId + ", " +
				"jbmpKey = " + jbpmKey + ")");

		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		
		DefinicioProces definicioProces = definicioProcesRepository.findDarreraVersioByEntornAndJbpmKey(
				entorn, 
				jbpmKey);
		
		DefinicioProcesDto dto = null;
		if (definicioProces != null) {
			// Comprova l'accés
			if (definicioProces.getExpedientTipus() != null)			
				expedientTipusHelper.getExpedientTipusComprovantPermisDissenyDelegat(
						definicioProces.getExpedientTipus().getId());
			else
				entornHelper.getEntornComprovantPermisos(EntornActual.getEntornId(), true, true);

			dto = conversioTipusHelper.convertir(
					definicioProces,
					DefinicioProcesDto.class);
		}
		return dto;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<DefinicioProcesDto> findSubDefinicionsProces(Long definicioProcesId) {
		logger.debug(
				"Consultant la llista de sub processos per a una definició de procés (" +
				"definicioProcesId = " + definicioProcesId + ")");
		List<DefinicioProcesDto> resposta = 
				jbpm3HeliumService.findSubDefinicionsProces(
						definicioProcesId);
		return resposta;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<DefinicioProcesDto> findAll(
			Long entornId, 
			Long expedientTipusId) {
		logger.debug(
				"Consultant la llista de definicions de processos (" +
				"entornId = " + entornId + ", " + 
				"expedientTipusId = " + expedientTipusId + ")");
		List<DefinicioProces> definicions = definicioProcesRepository.findByAll(
				entornId, 
				expedientTipusId == null,
				expedientTipusId);
		return conversioTipusHelper.convertirList(
									definicions, 
									DefinicioProcesDto.class);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<DefinicioProcesDto> findPerDatatable(
			Long entornId, 
			Long expedientTipusId, 
			boolean incloureGlobals,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant les definicions de processos per datatable (" +
				"entornId=" + entornId + ", " +
				"filtre=" + filtre + ")");

		PaginaDto<DefinicioProcesDto> pagina = paginacioHelper.toPaginaDto(
				definicioProcesRepository.findByFiltrePaginat(
						entornId,
						expedientTipusId == null,
						expedientTipusId,
						incloureGlobals,
						filtre == null || "".equals(filtre), 
						filtre, 
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				DefinicioProcesDto.class);
		// Consulta els valors pels comptadors 
		// List< Object[String jbpmKey, Long count]>
		List<String> paginaJbpmKeys = new ArrayList<String>();
		for (DefinicioProcesDto d : pagina.getContingut()) {
					paginaJbpmKeys.add(d.getJbpmKey());
		}
		if (paginaJbpmKeys.size() > 0) {
			List<Object[]> countVersions = definicioProcesRepository.countVersions(
					entornId,
					paginaJbpmKeys); 
			// Omple els comptadors de tipus de camps
			String jbpmKey;
			Long count;
			List<Object[]> processats = new ArrayList<Object[]>();	// per esborrar la informació processada i reduir la cerca
			for (DefinicioProcesDto definicio: pagina.getContingut()) {
				for (Object[] countVersio: countVersions) {
					jbpmKey = (String) countVersio[0];
					if (definicio.getJbpmKey().equals(jbpmKey)) {
						count = (Long) countVersio[1];
						definicio.setVersioCount(count);
						processats.add(countVersio);
					}
				}
				countVersions.removeAll(processats);
				processats.clear();
			}		
		}
		return pagina;
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public DefinicioProcesExportacio exportar(
			Long entornId, 
			Long definicioProcesId,
			DefinicioProcesExportacioCommandDto command) {
		logger.debug(
				"Exportant la definició de proces (" +
				"entornId=" + entornId + ", " +
				"definicioProcesId = " + definicioProcesId + ", " + 
				"command = " + command + ")");
		// Control d'accés
		entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		// Obté l'objecte d'exportació
		DefinicioProcesExportacio definicioExortacio = 
				definicioProcesHelper.getExportacio(
						definicioProcesId, 
						command);
		return definicioExortacio;
	}	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public DefinicioProcesDto importar(
			Long entornId, 
			Long expedientTipusId,
			Long definicioProcesId,
			DefinicioProcesExportacioCommandDto command, 
			DefinicioProcesExportacio importacio) {
		DefinicioProcesDto ret = null;
		logger.debug(
				"Important una definicio de proces (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId = " + expedientTipusId + ", " + 
				"definicioProcesId = " + definicioProcesId + ", " + 
				"command = " + command + ", " + 
				"importacio = " + importacio + ")");
		// Control d'accés
		entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		
		DefinicioProces importat = definicioProcesHelper.importar(
				entornId, 
				expedientTipusId, 
				importacio, 
				command,
				command != null ? command.isSobreEscriure() : true);
		
		if (importat != null)
			ret = conversioTipusHelper.convertir(importat, DefinicioProcesDto.class);
		return ret;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void delete(
			Long entornId,
			Long definicioProcesId) throws Exception {
		logger.debug(
				"Esborrant una definicio de proces (" +
				"entornId=" + entornId + ", " +
				"definicioProcesId = " + definicioProcesId + ")");
		DefinicioProces definicioProces = definicioProcesRepository.findById(definicioProcesId);
		// Control d'accés
		if (definicioProces.getExpedientTipus() != null)			
			expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(
					definicioProces.getExpedientTipus().getId());
		else
			entornHelper.getEntornComprovantPermisos(EntornActual.getEntornId(), true, true);

		jbpmHelper.esborrarDesplegament(
				definicioProces.getJbpmId());
		// Si era la definició de procés inicial del tipus d'expedient actualitza el tipus d'expedient
		if (definicioProces.getExpedientTipus() != null
				&& definicioProces.getJbpmKey().equals(definicioProces.getExpedientTipus().getJbpmProcessDefinitionKey())) {
			ExpedientTipus expedientTipus = expedientTipusRepository.findOne(definicioProces.getExpedientTipus().getId());
			expedientTipus.setJbpmProcessDefinitionKey(null);
			expedientTipusRepository.save(expedientTipus);
		}
		definicioProcesRepository.delete(definicioProces);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void copiarDefinicioProces(Long origenId, Long destiId) {
		definicioProcesHelper.copiarDefinicioProces(origenId, destiId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<TascaDto> tascaFindPerDatatable(
			Long entornId, 
			Long definicioProcesId, 
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		logger.debug(
				"Consultant les tasques de la definicio de proces pel datatable (" +
				"entornId=" + entornId + ", " +
				"definicioProcesId=" + definicioProcesId + ", " +
				"filtre=" + filtre + ")");
								
		PaginaDto<TascaDto> pagina = paginacioHelper.toPaginaDto(
				tascaRepository.findByFiltrePaginat(
						definicioProcesId,
						filtre == null || "".equals(filtre), 
						filtre, 
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				TascaDto.class);
		return pagina;		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<TascaDto> tascaFindAll(
			Long definicioProcesId) {
		logger.debug(
				"Consultant totes les tasques de la definicio de procés(" +
				"definicioProcesId=" + definicioProcesId + ")");
								
		return conversioTipusHelper.convertirList(
				tascaRepository.findByDefinicioProcesIdOrderByNomAsc(
						definicioProcesId), 
				TascaDto.class);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public DefinicioProcesDto tascaFindDefinicioProcesDeTasca(Long tascaId) {
		logger.debug(
				"Consultant la definicio de proces d'una tasca (" +
				"tascaId=" + tascaId + ")");
								
		return conversioTipusHelper.convertir(
				tascaRepository.findOne(tascaId).getDefinicioProces(), 
				DefinicioProcesDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public TascaDto tascaFindAmbId(Long id) throws NoTrobatException {
		Tasca tasca = tascaRepository.findOne(id);
		if (tasca == null) {
			throw new NoTrobatException(Tasca.class, id);
		}
		return conversioTipusHelper.convertir(
				tasca, 
				TascaDto.class);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public TascaDto tascaUpdate(TascaDto tasca) 
			throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Modificant la tasca de la definició de procés (" +
				"tasca.id=" + tasca.getId() + ", " +
				"tasca =" + tasca + ")");
		Tasca entity = tascaRepository.findOne(tasca.getId());

		entity.setId(tasca.getId());
		entity.setNom(tasca.getNom());
		
		entity.setMissatgeInfo(tasca.getMissatgeInfo());
		entity.setMissatgeWarn(tasca.getMissatgeWarn());
		entity.setNomScript(tasca.getNomScript());
		entity.setExpressioDelegacio(tasca.getExpressioDelegacio());//""/"on"
		entity.setRecursForm(tasca.getRecursForm());
		entity.setFormExtern(tasca.getFormExtern());
		entity.setTramitacioMassiva(tasca.isTramitacioMassiva());
		entity.setFinalitzacioSegonPla(tasca.isFinalitzacioSegonPla());
		
		return conversioTipusHelper.convertir(
				tascaRepository.save(entity),
				TascaDto.class);
	}		
	
	@Override
	@Transactional
	public CampTascaDto tascaCampCreate(
			Long tascaId, 
			CampTascaDto tascaCamp) throws PermisDenegatException {

		logger.debug(
				"Creant nou camp per una tasca de la definició de procés (" +
				"tascaId =" + tascaId + ", " +
				"tascaCamp=" + tascaCamp + ")");

		CampTasca entity = new CampTasca();
		
		entity.setOrder(campTascaRepository.getNextOrdre(tascaId));		
		entity.setReadFrom(tascaCamp.isReadFrom());
		entity.setWriteTo(tascaCamp.isWriteTo());
		entity.setRequired(tascaCamp.isRequired());
		entity.setReadOnly(tascaCamp.isReadOnly());
		entity.setAmpleCols(12);
		entity.setBuitCols(0);
		entity.setTasca(tascaRepository.findOne(tascaId));
		entity.setCamp(campRepository.findOne(tascaCamp.getCamp().getId()));
		
		return conversioTipusHelper.convertir(
				campTascaRepository.save(entity),
				CampTascaDto.class);
	}
	
	

	@Override
	@Transactional
	public void tascaCampDelete(Long tascaCampId) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Esborrant la tascaCamp de la definició de procés (" +
				"tascaCampId=" + tascaCampId +  ")");
		
		CampTasca tascaCamp = campTascaRepository.findOne(tascaCampId);
		tascaCamp.getTasca().removeCamp(tascaCamp);
		campTascaRepository.delete(tascaCamp);	
		campTascaRepository.flush();
		reordenarCampsTasca(tascaCamp.getTasca().getId());
	}

	/** Funció per reasignar el valor d'ordre dins dels camps d'una tasca de tipus registre. */
	private void reordenarCampsTasca(
			Long tascaId) {
		List<CampTasca> tascaCamps = campTascaRepository.findAmbTascaIdOrdenats(tascaId);		
		int i = 0;
		for (CampTasca c : tascaCamps) {
			c.setOrder(i);
			campTascaRepository.saveAndFlush(c);
			i++;
		}
	}

	@Override
	@Transactional
	public boolean tascaCampMourePosicio(
			Long id, 
			int posicio) {
		
		boolean ret = false;
		CampTasca tascaCamp = campTascaRepository.findOne(id);
		if (tascaCamp != null) {
			List<CampTasca> campsTasca = campTascaRepository.findAmbTascaIdOrdenats(
					tascaCamp.getTasca().getId());
			int index = campsTasca.indexOf(tascaCamp);
			if(posicio != index) {	
				tascaCamp = campsTasca.get(index);
				campsTasca.remove(tascaCamp);
				campsTasca.add(posicio, tascaCamp);
				int i=-1;
				for (CampTasca c : campsTasca) {
					c.setOrder(i);
					campTascaRepository.saveAndFlush(c);
					i--;
				}
				i = 0;
				for (CampTasca c : campsTasca) {
					c.setOrder(i);
					campTascaRepository.saveAndFlush(c);
					i++;
				}
			}
		}
		return ret;				
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<CampTascaDto> tascaCampFindPerDatatable(
			Long tascaId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant els camps de la tasca d'una definició de proces per datatable (" +
				"tascaId=" + tascaId + ", " +
				"filtre=" + filtre + ")");
		
		return paginacioHelper.toPaginaDto(
				campTascaRepository.findByFiltrePaginat(
						tascaId,
						filtre == null,
						filtre,
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				CampTascaDto.class);		
	}		
	
	@Override
	@Transactional(readOnly = true)
	public List<CampTascaDto> tascaCampFindCampAmbTascaId(
			Long tascaId) {
		logger.debug(
				"Consultant els camps per una tasca (" +
				"tascaId=" + tascaId + ")");
		return conversioTipusHelper.convertirList(
				campTascaRepository.findCampsTasca(tascaId), 
				CampTascaDto.class);
	}
	
	@Override
	@Transactional
	public CampTascaDto tascaCampUpdate(CampTascaDto tascaCamp) 
						throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Modificant el parametre de la tasca de la definició de procés existent (" +
				"tascaCamp.id=" + tascaCamp.getId() + ", " +
				"tascaCamp =" + tascaCamp + ")");
		
		CampTasca entity = campTascaRepository.findOne(tascaCamp.getId());
				
		entity.setReadFrom(tascaCamp.isReadFrom());
		entity.setWriteTo(tascaCamp.isWriteTo());
		entity.setRequired(tascaCamp.isRequired());
		entity.setReadOnly(tascaCamp.isReadOnly());
		
		definirAmpleBuit(entity, tascaCamp);
		
		return conversioTipusHelper.convertir(
				campTascaRepository.save(entity),
				CampTascaDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public CampTascaDto tascaCampFindById(
			Long campTascaId) {
		logger.debug(
				"Consultant el camp tasca amb id (" +
				"campTascaId = " + campTascaId + ")");
		CampTasca campTasca = campTascaRepository.findOne(campTascaId);

		return conversioTipusHelper.convertir(
				campTasca,
				CampTascaDto.class);
	}
	

	
	@Override
	@Transactional
	public DocumentTascaDto tascaDocumentCreate(
			Long tascaId, 
			DocumentTascaDto tascaDocument) throws PermisDenegatException {

		logger.debug(
				"Creant nou document per una tasca de la definició de procés (" +
				"tascaId =" + tascaId + ", " +
				"tascaDocument=" + tascaDocument + ")");

		DocumentTasca entity = new DocumentTasca();
		
		entity.setOrder(documentTascaRepository.getNextOrdre(tascaId));		
		entity.setRequired(tascaDocument.isRequired());
		entity.setReadOnly(tascaDocument.isReadOnly());
		entity.setTasca(tascaRepository.findOne(tascaId));
		entity.setDocument(documentRepository.findOne(tascaDocument.getDocument().getId()));
		
		return conversioTipusHelper.convertir(
				documentTascaRepository.save(entity),
				DocumentTascaDto.class);
	}

	@Override
	@Transactional
	public void tascaDocumentDelete(Long tascaDocumentId) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Esborrant la tascaDocument de la definició de procés (" +
				"tascaDocumentId=" + tascaDocumentId +  ")");
		
		DocumentTasca tascaDocument = documentTascaRepository.findOne(tascaDocumentId);
		tascaDocument.getTasca().removeDocument(tascaDocument);
		documentTascaRepository.delete(tascaDocument);	
		documentTascaRepository.flush();
		reordenarDocumentsTasca(tascaDocument.getTasca().getId());
	}

	/** Funció per reasignar el valor d'ordre dins dels documents d'una tasca de tipus registre. */
	private void reordenarDocumentsTasca(
			Long tascaId) {
		List<DocumentTasca> tascaDocuments = documentTascaRepository.findAmbTascaOrdenats(tascaId);		
		int i = 0;
		for (DocumentTasca c : tascaDocuments) {
			c.setOrder(i);
			documentTascaRepository.saveAndFlush(c);
			i++;
		}
	}

	@Override
	@Transactional
	public boolean tascaDocumentMourePosicio(
			Long id, 
			int posicio) {
		
		boolean ret = false;
		DocumentTasca tascaDocument = documentTascaRepository.findOne(id);
		if (tascaDocument != null) {
			List<DocumentTasca> documentsTasca = documentTascaRepository.findAmbTascaOrdenats(
					tascaDocument.getTasca().getId());
			int index = documentsTasca.indexOf(tascaDocument);
			if(posicio != index) {	
				tascaDocument = documentsTasca.get(index);
				documentsTasca.remove(tascaDocument);
				documentsTasca.add(posicio, tascaDocument);
				int i=-1;
				for (DocumentTasca c : documentsTasca) {
					c.setOrder(i);
					documentTascaRepository.saveAndFlush(c);
					i--;
				}
				i = 0;
				for (DocumentTasca c : documentsTasca) {
					c.setOrder(i);
					documentTascaRepository.saveAndFlush(c);
					i++;
				}
			}
		}
		return ret;				
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<DocumentTascaDto> tascaDocumentFindPerDatatable(
			Long tascaId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant els documents de la tasca d'una definició de proces per datatable (" +
				"tascaId=" + tascaId + ", " +
				"filtre=" + filtre + ")");
		
		return paginacioHelper.toPaginaDto(
				documentTascaRepository.findByFiltrePaginat(
						tascaId,
						filtre == null,
						filtre,
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				DocumentTascaDto.class);		
	}		
	
	@Override
	@Transactional(readOnly = true)
	public List<DocumentTascaDto> tascaDocumentFindDocumentAmbTascaId(
			Long tascaId) {
		logger.debug(
				"Consultant els documents per una tasca (" +
				"tascaId=" + tascaId + ")");
		return conversioTipusHelper.convertirList(
				documentTascaRepository.findDocumentsTasca(tascaId), 
				DocumentTascaDto.class);
	}
	
	@Override
	@Transactional
	public DocumentTascaDto tascaDocumentUpdate(DocumentTascaDto tascaDocument) 
						throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Modificant el parametre de la tasca de la definició de procés existent (" +
				"tascaDocument.id=" + tascaDocument.getId() + ", " +
				"tascaDocument =" + tascaDocument + ")");
		
		DocumentTasca entity = documentTascaRepository.findOne(tascaDocument.getId());
				
		entity.setRequired(tascaDocument.isRequired());
		entity.setReadOnly(tascaDocument.isReadOnly());
		
		return conversioTipusHelper.convertir(
				documentTascaRepository.save(entity),
				DocumentTascaDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public DocumentTascaDto tascaDocumentFindById(
			Long documentTascaId) {
		logger.debug(
				"Consultant el document tasca amb id (" +
				"documentTascaId = " + documentTascaId + ")");
		DocumentTasca documentTasca = documentTascaRepository.findOne(documentTascaId);

		return conversioTipusHelper.convertir(
				documentTasca,
				DocumentTascaDto.class);
	}
	
	@Override
	@Transactional
	public FirmaTascaDto tascaFirmaCreate(
			Long tascaId, 
			FirmaTascaDto tascaFirma) throws PermisDenegatException {

		logger.debug(
				"Creant nova firma per una tasca de la definició de procés (" +
				"tascaId =" + tascaId + ", " +
				"tascaFirma=" + tascaFirma + ")");

		FirmaTasca entity = new FirmaTasca();
		
		entity.setOrder(firmaTascaRepository.getNextOrdre(tascaId));		
		entity.setRequired(tascaFirma.isRequired());
		entity.setTasca(tascaRepository.findOne(tascaId));
		entity.setDocument(documentRepository.findOne(tascaFirma.getDocument().getId()));
		
		return conversioTipusHelper.convertir(
				firmaTascaRepository.save(entity),
				FirmaTascaDto.class);
	}

	@Override
	@Transactional
	public void tascaFirmaDelete(Long tascaFirmaId) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Esborrant la tascaFirma de la definició de procés (" +
				"tascaFirmaId=" + tascaFirmaId +  ")");
		
		FirmaTasca tascaFirma = firmaTascaRepository.findOne(tascaFirmaId);
		firmaTascaRepository.delete(tascaFirma);	
		firmaTascaRepository.flush();
		reordenarFirmesTasca(tascaFirma.getTasca().getId());
	}

	/** Funció per reasignar el valor d'ordre dins de les firmes d'una tasca de tipus registre. */
	private void reordenarFirmesTasca(
			Long tascaId) {
		List<FirmaTasca> tascaFirmes = firmaTascaRepository.findAmbTascaIdOrdenats(tascaId);		
		int i = 0;
		for (FirmaTasca c : tascaFirmes) {
			c.setOrder(i);
			firmaTascaRepository.saveAndFlush(c);
			i++;
		}
	}

	@Override
	@Transactional
	public boolean tascaFirmaMourePosicio(
			Long id, 
			int posicio) {
		
		boolean ret = false;
		FirmaTasca tascaFirma = firmaTascaRepository.findOne(id);
		if (tascaFirma != null) {
			List<FirmaTasca> firmesTasca = firmaTascaRepository.findAmbTascaIdOrdenats(
					tascaFirma.getTasca().getId());
			int index = firmesTasca.indexOf(tascaFirma);
			if(posicio != index) {	
				tascaFirma = firmesTasca.get(index);
				firmesTasca.remove(tascaFirma);
				firmesTasca.add(posicio, tascaFirma);
				int i=-1;
				for (FirmaTasca c : firmesTasca) {
					c.setOrder(i);
					firmaTascaRepository.saveAndFlush(c);
					i--;
				}
				i = 0;
				for (FirmaTasca c : firmesTasca) {
					c.setOrder(i);
					firmaTascaRepository.saveAndFlush(c);
					i++;
				}
			}
		}
		return ret;				
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<FirmaTascaDto> tascaFirmaFindPerDatatable(
			Long tascaId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant les firmes de la tasca d'una definició de proces per datatable (" +
				"tascaId=" + tascaId + ", " +
				"filtre=" + filtre + ")");
		
		return paginacioHelper.toPaginaDto(
				firmaTascaRepository.findByFiltrePaginat(
						tascaId,
						filtre == null,
						filtre,
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				FirmaTascaDto.class);		
	}		
	
	@Override
	@Transactional(readOnly = true)
	public List<FirmaTascaDto> tascaFirmaFindAmbTascaId(
			Long tascaId) {
		logger.debug(
				"Consultant les firmes per una tasca (" +
				"tascaId=" + tascaId + ")");
		return conversioTipusHelper.convertirList(
				firmaTascaRepository.findFirmesTasca(tascaId), 
				FirmaTascaDto.class);
	}
	
	@Override
	@Transactional
	public FirmaTascaDto tascaFirmaUpdate(FirmaTascaDto tascaFirma) 
						throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Modificant el parametre de la firma tasca de la definicio de proces (" +
				"tascaFirma.id=" + tascaFirma.getId() + ", " +
				"tascaFirma =" + tascaFirma + ")");
		
		FirmaTasca entity = firmaTascaRepository.findOne(tascaFirma.getId());
				
		entity.setRequired(tascaFirma.isRequired());
		
		return conversioTipusHelper.convertir(
				firmaTascaRepository.save(entity),
				FirmaTascaDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public FirmaTascaDto tascaFirmaFindById(
			Long firmaTascaId) {
		logger.debug(
				"Consultant el document tasca amb id (" +
				"firmaTascaId = " + firmaTascaId + ")");
		FirmaTasca firmaTasca = firmaTascaRepository.findOne(firmaTascaId);

		return conversioTipusHelper.convertir(
				firmaTasca,
				FirmaTascaDto.class);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public DefinicioProcesDto findAmbIdAndEntorn(
			Long entornId,
			Long definicioProcesId) {
		logger.debug(
				"Consultant definicioProces amb id i amb permisos de disseny (" +
				"entornId=" + entornId + ", " +
				"definicioProcesId = " + definicioProcesId + ")");
		DefinicioProces definicioProces = definicioProcesRepository.findByIdAndEntornId(
					definicioProcesId,
					entornId);
		return conversioTipusHelper.convertir(
				definicioProces,
				DefinicioProcesDto.class);
	}

	// MANTENIMENT DE CONSULTES

	@Override
	@Transactional(readOnly = true)
	public List<ConsultaDto> consultaFindByEntorn(
			Long entornId) {
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		List<Consulta> consultans = consultaRepository.findByEntorn(entorn);
		return conversioTipusHelper.convertirList(
									consultans, 
									ConsultaDto.class);
	}

	@Transactional(readOnly = true)
	public List<CampDto> campFindAllOrdenatsPerCodi(Long definicioProcesId) {
		logger.debug(
				"Consultant tots els camps de la definicio de proces per al desplegable " +
				" de camps del registre (definicioProcesId=" + definicioProcesId + ")");
		
		DefinicioProces definicioProces = definicioProcesRepository.findOne(definicioProcesId);
		if (definicioProces == null)
			throw new NoTrobatException(DefinicioProces.class, definicioProcesId);
		
		List<Camp> camps = campRepository.findByDefinicioProcesOrderByCodiAsc(definicioProces);
		
		return conversioTipusHelper.convertirList(
				camps, 
				CampDto.class);
	}	

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<TerminiDto> terminiFindAll(
			Long definicioProcesId) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Consultant tots els terminis de la definicio de proces per al desplegable " +
				" de documents de la tasca a la definicio de processos (definicioProcesId=" + definicioProcesId + ")");
		
		List<Termini> terminis = terminiRepository.findByDefinicioProcesId(definicioProcesId);
		
		return conversioTipusHelper.convertirList(
									terminis, 
									TerminiDto.class);
	}	
	
	private void definirAmpleBuit(CampTasca entity, CampTascaDto tascaCamp) {
		int ample = tascaCamp.getAmpleCols();
		int buit = tascaCamp.getBuitCols();
		
		if (ample > 12)
			ample = 12;
		else if (ample <= 0)
			ample = 1;
			
		if (buit > 12)
			buit = 12;
		else if (buit < -12)
			buit = -12;
		
		int absAmple = Math.abs(ample);
		int absBuit = Math.abs(buit);
		int totalCols = absAmple + absBuit;
		if (totalCols > 12) {
			int diff = totalCols - 12;
			if (buit >= 0)
				buit -= diff;
			else
				buit += diff;
		}
		
		entity.setAmpleCols(ample);
		entity.setBuitCols(buit);
	}
	private static final Logger logger = LoggerFactory.getLogger(DefinicioProcesServiceImpl.class);
}