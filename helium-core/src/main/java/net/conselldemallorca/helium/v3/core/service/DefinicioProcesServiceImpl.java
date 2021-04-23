/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import net.conselldemallorca.helium.core.api.WorkflowEngineApi;
import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.DefinicioProcesHelper;
import net.conselldemallorca.helium.core.helper.EntornHelper;
import net.conselldemallorca.helium.core.helper.ExpedientTipusHelper;
import net.conselldemallorca.helium.core.helper.HerenciaHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.model.hibernate.*;
import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.v3.core.api.dto.*;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exportacio.DefinicioProcesExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.DefinicioProcesExportacioCommandDto;
import net.conselldemallorca.helium.v3.core.api.service.DefinicioProcesService;
import net.conselldemallorca.helium.v3.core.api.service.Jbpm3HeliumService;
import net.conselldemallorca.helium.v3.core.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private WorkflowEngineApi workflowEngineApi;
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
		
		DefinicioProces definicioProces = definicioProcesRepository.findDarreraVersioAmbEntornIJbpmKey(
				entorn.getId(), 
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
			Long expedientTipusId, 
			boolean incloureGlobals) {
		logger.debug(
				"Consultant la llista de definicions de processos (" +
				"entornId = " + entornId + ", " + 
				"expedientTipusId = " + expedientTipusId + ", " + 
				"incloureGlobals = " + incloureGlobals + ")");
		List<DefinicioProces> definicions = definicioProcesRepository.findListDarreraVersioByAll(
				entornId, 
				expedientTipusId == null,
				expedientTipusId,
				incloureGlobals);
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

		ExpedientTipus expedientTipus = expedientTipusId != null? expedientTipusHelper.getExpedientTipusComprovantPermisDissenyDelegat(expedientTipusId) : null;
		
		// Determina si hi ha herència 
		boolean ambHerencia = HerenciaHelper.ambHerencia(expedientTipus);

		Page<DefinicioProces> page = definicioProcesRepository.findByFiltrePaginat(
				entornId,
				expedientTipusId == null,
				expedientTipusId,
				incloureGlobals,
				filtre == null || "".equals(filtre), 
				filtre != null? filtre : "", 
				ambHerencia,
				paginacioHelper.toSpringDataPageable(
						paginacioParams));
		
		PaginaDto<DefinicioProcesDto> pagina = paginacioHelper.toPaginaDto(
				page,
				DefinicioProcesDto.class);
		
		// Consulta els valors pels comptadors 
		// List< Object[String jbpmKey, Long count]>
		List<String> paginaJbpmKeys = new ArrayList<String>();
		for (DefinicioProcesDto d : pagina.getContingut()) {
					paginaJbpmKeys.add(d.getJbpmKey());
		}
		if (paginaJbpmKeys.size() > 0) {
			// <[jbpmKey, expedientTipusId, count]>
			List<Object[]> countVersions = definicioProcesRepository.countVersions(
					entornId,
					paginaJbpmKeys); 
			// Omple els comptadors de tipus de camps
			String jbpmKey;
			Long etId;
			Long count;
			List<Object[]> processats = new ArrayList<Object[]>();	// per esborrar la informació processada i reduir la cerca
			for (DefinicioProcesDto definicio: pagina.getContingut()) {
				for (Object[] countVersio: countVersions) {
					jbpmKey = (String) countVersio[0];
					etId = (Long) countVersio[1];
					if (definicio.getJbpmKey().equals(jbpmKey) 
							&& (
								(definicio.getExpedientTipus() == null && etId == null)
								|| (definicio.getExpedientTipus() != null && definicio.getExpedientTipus().getId().equals(etId)))) {
						count = (Long) countVersio[2];
						definicio.setVersioCount(count);
						processats.add(countVersio);
					}
				}
				countVersions.removeAll(processats);
				processats.clear();

				// Herencia				
				if (ambHerencia 
						&& definicio.getExpedientTipus() != null
						&& !expedientTipusId.equals(definicio.getExpedientTipus().getId()))
					definicio.setHeretat(true);
				if (ambHerencia) {
					// Llista d'heretats
					Set<Long> heretatsIds = new HashSet<Long>();
					for (DefinicioProces dp : page.getContent())
						if ( dp.getExpedientTipus() != null && !expedientTipusId.equals(dp.getExpedientTipus().getId()))
							heretatsIds.add(dp.getId());
					// Llistat d'elements sobreescrits
					Set<String> sobreescritsCodis = new HashSet<String>();
					for (DefinicioProces dp : definicioProcesRepository.findSobreescrits(
							expedientTipus.getId()))
						sobreescritsCodis.add(dp.getJbpmKey());
					// Completa l'informació del dto
					for (DefinicioProcesDto dto: pagina.getContingut()) {
						// Sobreescriu, les globals no es marquen sobreescrites 
						if (dto.getExpedientTipus() != null && sobreescritsCodis.contains(dto.getJbpmKey()))
							dto.setSobreescriu(true);
						// Heretat
						if (heretatsIds.contains(dto.getId()) && ! dto.isSobreescriu())
							dto.setHeretat(true);			
					}				
				}
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

		workflowEngineApi.esborrarDesplegament(
				definicioProces.getJbpmId());
		// 
		definicioProcesRepository.delete(definicioProces);
		// Si era darrera versió de la definició de procés inicial del tipus d'expedient posa la propietat a null
		if (definicioProces.getExpedientTipus() != null
				&& definicioProces.getJbpmKey().equals(definicioProces.getExpedientTipus().getJbpmProcessDefinitionKey())) {
			ExpedientTipus expedientTipus = expedientTipusRepository.findOne(definicioProces.getExpedientTipus().getId());
			// Troba la darrera definició de procés
			definicioProces = definicioProcesRepository.findDarreraVersioAmbEntornIJbpmKey(
					expedientTipus.getEntorn().getId(), 
					definicioProces.getJbpmKey());
			if (definicioProces == null) {
				expedientTipus.setJbpmProcessDefinitionKey(null);
				expedientTipusRepository.save(expedientTipus);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void copiarDefinicioProces(Long origenId, Long destiId) {
		definicioProcesHelper.copiarDefinicioProces(origenId, destiId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public String consultarStartTaskName(Long definicioProcesId) {
		logger.debug(
				"Consultant el nom de la tasca inicial de la definició de procés (" +
				"definicioProcesId = " + definicioProcesId + ")");

		DefinicioProces definicioProces = definicioProcesRepository.findById(definicioProcesId);
		if (definicioProces == null)
			throw new NoTrobatException(DefinicioProces.class, definicioProcesId);
		return workflowEngineApi.getStartTaskName(definicioProces.getJbpmId());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<TascaDto> tascaFindPerDatatable(
			Long entornId, 
			Long expedientTipusId,
			Long definicioProcesId, 
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		logger.debug(
				"Consultant les tasques de la definicio de proces pel datatable (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
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
		
		// Marca la tasca com a incicial
		DefinicioProces definicioProces = definicioProcesRepository.findOne(definicioProcesId);		
		String startTaskName = workflowEngineApi.getStartTaskName(definicioProces.getJbpmId());
		if (startTaskName != null)
			for (TascaDto tasca : pagina.getContingut())
				if(tasca.getNom().equals(startTaskName)) {
					tasca.setInicial(true);
					break;
				}

		ExpedientTipus expedientTipus = null;
		if ( expedientTipusId != null)
			expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisDissenyDelegat(expedientTipusId);
		else if (definicioProces.getExpedientTipus() != null) {
			expedientTipus = definicioProces.getExpedientTipus();
			expedientTipusId = expedientTipus.getId();
		}
		
		// Determina si les tasques són heretades 
		boolean definicioProcesHeretada = definicioProcesHelper.isDefinicioProcesHeretada(definicioProces, expedientTipus);
		if (definicioProcesHeretada) {
			for(TascaDto tasca : pagina.getContingut())
				tasca.setHeretat(true);
		}
		// S'han de llevar els camps, documents i firmes afegides per altres tipus d'expedients
		if (expedientTipusId != null) {
			for (TascaDto tasca : pagina.getContingut())
				this.filtrarDadesPerTipusExpedientId(tasca, expedientTipusId);
		}			
		return pagina;		
	}

	/** Mètode per treure de la llista de camps, documents i firmes aquelles que estiguin incloses per un tipus d'expedient diferent
	 * al de la definició de procés o que no estigui heretada. Filtra aquells no relacionats amb el tipus d'expedient o que no siguin
	 * heretats del tipus d'expedient pare.
	 * @param tasca
	 * @param expedientTipusId
	 */
	private void filtrarDadesPerTipusExpedientId(TascaDto tasca, Long expedientTipusId) {
		
		// Filtra els camps d'altres tipus d'expedients
		boolean campCorrecte;
		List<CampTascaDto> campsEsborrar = new ArrayList<CampTascaDto>();
		for (CampTascaDto camp : tasca.getCamps()) {
			// Camp directament relacionat a la definició de procés de la tasca o afegit pel tipus d'expedient
			campCorrecte = camp.getExpedientTipusId() == null || camp.getExpedientTipusId().equals(expedientTipusId);
			if (!campCorrecte)
				campsEsborrar.add(camp);
		}
		tasca.getCamps().removeAll(campsEsborrar);
		
		// Filtra els documents d'altres tipus d'expedients
		boolean documentCorrecte;
		List<DocumentTascaDto> documentsEsborrar = new ArrayList<DocumentTascaDto>();
		for (DocumentTascaDto document : tasca.getDocuments()) {
			// Camp directament relacionat a la definició de procés de la tasca o afegit pel tipus d'expedient
			documentCorrecte = document.getExpedientTipusId() == null || document.getExpedientTipusId().equals(expedientTipusId);
			if (!documentCorrecte)
				documentsEsborrar.add(document);
		}
		tasca.getDocuments().removeAll(documentsEsborrar);

		// Filtra les firmes d'altres tipus d'expedients
		boolean firmaCorrecte;
		List<FirmaTascaDto> firmesEsborrar = new ArrayList<FirmaTascaDto>();
		for (FirmaTascaDto firma : tasca.getFirmes()) {
			// Camp directament relacionat a la definició de procés de la tasca o afegit pel tipus d'expedient
			firmaCorrecte = firma.getExpedientTipusId() == null || firma.getExpedientTipusId().equals(expedientTipusId);
			if (!firmaCorrecte)
				firmesEsborrar.add(firma);
		}
		tasca.getFirmes().removeAll(firmesEsborrar);

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
	public TascaDto tascaFindAmbId(Long expedientTipusId, Long tascaId) throws NoTrobatException {
		Tasca tasca = tascaRepository.findOne(tascaId);
		if (tasca == null) {
			throw new NoTrobatException(Tasca.class, tascaId);
		}
		TascaDto tascaDto = conversioTipusHelper.convertir(
				tasca, 
				TascaDto.class);
		if (tasca.getDefinicioProces() != null)
			tascaDto.setDefinicioProcesId(tasca.getDefinicioProces().getId());

		// Determina si la tasca és heretada
		ExpedientTipus expedientTipus = expedientTipusId != null? expedientTipusHelper.getExpedientTipusComprovantPermisDissenyDelegat(expedientTipusId) : null;
		boolean tascaHeretada = definicioProcesHelper.isTascaHeretada(tasca, expedientTipus);
		if (tascaHeretada) {
			tascaDto.setHeretat(true);
		} else {
			// La tasca no està heretada, s'han de llevar els camps, documents i firmes afegides per altres tipus d'expedients
			this.filtrarDadesPerTipusExpedientId(tascaDto, expedientTipusId);
		}
		return tascaDto;
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
		entity.setAmbRepro(tasca.isAmbRepro());
		entity.setMostrarAgrupacions(tasca.isMostrarAgrupacions());
		
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
		
		entity.setOrder(campTascaRepository.getNextOrdre(tascaId, tascaCamp.getExpedientTipusId()));		
		entity.setReadFrom(tascaCamp.isReadFrom());
		entity.setWriteTo(tascaCamp.isWriteTo());
		entity.setRequired(tascaCamp.isRequired());
		entity.setReadOnly(tascaCamp.isReadOnly());
		entity.setAmpleCols(12);
		entity.setBuitCols(0);
		Tasca tasca = tascaRepository.findOne(tascaId);
		entity.setTasca(tasca);
		entity.setCamp(campRepository.findOne(tascaCamp.getCamp().getId()));
		if (tascaCamp.getExpedientTipusId() != null) {
			ExpedientTipus expedientTipus = expedientTipusRepository.findById(tascaCamp.getExpedientTipusId());
			// Només relaciona el camp tasca amb el tipus d'expedient si la tasca està heretada
			if (definicioProcesHelper.isTascaHeretada(tasca, expedientTipus))
				entity.setExpedientTipus(expedientTipus);
		}
		
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
		Long expedientTipusId = tascaCamp.getExpedientTipus() != null? tascaCamp.getExpedientTipus().getId() : null;
		tascaCamp.getTasca().removeCamp(tascaCamp);
		campTascaRepository.delete(tascaCamp);	
		campTascaRepository.flush();
		reordenarCampsTasca(tascaCamp.getTasca().getId(), expedientTipusId);
	}

	/** Funció per reasignar el valor d'ordre dins dels camps d'una tasca de tipus registre. */
	private void reordenarCampsTasca(
			Long tascaId,
			Long expedientTipusId) {
		List<CampTasca> tascaCamps = campTascaRepository.findAmbTascaIdOrdenats(
				tascaId,
				expedientTipusId);		
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
			Long campTascaId,
			Long expedientTipusId,
			int posicio) {
		
		boolean ret = false;
		CampTasca tascaCamp = campTascaRepository.findOne(campTascaId);
		if (tascaCamp != null) {
			List<CampTasca> campsTasca = campTascaRepository.findAmbTascaIdOrdenats(
					tascaCamp.getTasca().getId(), 
					expedientTipusId);
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
			Long expedientTipusId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant els camps de la tasca d'una definició de proces per datatable (" +
				"tascaId=" + tascaId + ", " +
				"filtre=" + filtre + ")");
		
		Tasca tasca = tascaRepository.findOne(tascaId);

		// Consulta el tipus d'expedient
		ExpedientTipus expedientTipus = null;
		if (expedientTipusId != null)
			expedientTipus = expedientTipusRepository.findById(expedientTipusId);
		else {
			expedientTipus = tasca.getDefinicioProces().getExpedientTipus();
			if (expedientTipus != null)
				expedientTipusId = expedientTipus.getId();
		}

		Page<CampTasca> page = campTascaRepository.findByFiltrePaginat(
				tascaId,
				expedientTipusId,
				filtre == null,
				filtre,
				paginacioHelper.toSpringDataPageable(
						paginacioParams));

		PaginaDto<CampTascaDto> pagina = paginacioHelper.toPaginaDto(
				page,
				CampTascaDto.class);
		// Herencia		
		if (HerenciaHelper.ambHerencia(expedientTipus)) {
			// Consulta els sobreescrits
			Map<String, Camp> sobreescrits = new HashMap<String, Camp>();
			for (Camp c : campRepository.findSobreescrits(expedientTipusId))
				sobreescrits.put(c.getCodi(), c);
			// Construeix la llista d'heretats
			Set<Long> heretatsIds = new HashSet<Long>();
			Set<Long> campsHeretatsIds = new HashSet<Long>();
			for (CampTasca ct : page.getContent()) {
				if (definicioProcesHelper.isCampTascaHeretat(ct, expedientTipus))
					heretatsIds.add(ct.getId());
				if (definicioProcesHelper.isCampHeretat(ct.getCamp(), expedientTipus))
					campsHeretatsIds.add(ct.getCamp().getId());
			}
			// Escriu les propietats als dtos
			for (CampTascaDto dto : pagina.getContingut()) {
				// Determina si el campTasca és heretat segons si el tipus d'expedient de la tasca és igual al tipus d'expedient pare del tipus d'expedient passat com a paràmetre
				if (heretatsIds.contains(dto.getId()))
					dto.setHeretat(true);
				// Completa l'informació del camp del campTasca
				// Sobreescriu
				if (sobreescrits.containsKey(dto.getCamp().getCodi())) {
					dto.setCamp(conversioTipusHelper.convertir(sobreescrits.get(dto.getCamp().getCodi()), CampDto.class));
					dto.getCamp().setSobreescriu(true);
				}
				// Heretat
				if (campsHeretatsIds.contains(dto.getCamp().getId()) && ! dto.getCamp().isSobreescriu())
					dto.getCamp().setHeretat(true);
			}
		}
		return pagina;
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
			Long expedientTipusId,
			Long campTascaId) {
		logger.debug(
				"Consultant el camp tasca amb id (" +
				"campTascaId = " + campTascaId + ")");
		CampTasca campTasca = campTascaRepository.findOne(campTascaId);
		if (campTasca == null) {
			throw new NoTrobatException(CampTasca.class, campTascaId);
		}
		CampTascaDto dto = conversioTipusHelper.convertir(
				campTasca, 
				CampTascaDto.class);

		if (expedientTipusId != null) {
			// Consulta el tipus d'expedient
			ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId);
			// Herència
			dto.setHeretat(definicioProcesHelper.isCampTascaHeretat(campTasca, expedientTipus));
		}
		return dto;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CampTascaDto> tascaCampFindAll(Long expedientTipusId, Long tascaId) {
		logger.debug(
				"Consultant tots els camps de la tasca amb id (" +
				"tascaId = " + tascaId + ")");

		List<CampTasca> campsTasca = campTascaRepository.findAmbTascaIdOrdenats(tascaId, expedientTipusId);
		
		List<CampTascaDto> dtos = new ArrayList<CampTascaDto>();

		// Consulta la tasca i el tipus d'expedient
		ExpedientTipus expedientTipus = expedientTipusId != null ? 
				expedientTipus = expedientTipusRepository.findById(expedientTipusId) 
				: null;

		CampTascaDto dto;
		for (CampTasca campTasca : campsTasca) {
			dto = conversioTipusHelper.convertir(
					campTasca, 
					CampTascaDto.class);
			// Herencia
			dto.setHeretat(definicioProcesHelper.isCampTascaHeretat(campTasca, expedientTipus)); 
			dtos.add(dto);
		}
		return dtos;
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
		
		entity.setOrder(documentTascaRepository.getNextOrdre(tascaId, tascaDocument.getExpedientTipusId()));		
		entity.setRequired(tascaDocument.isRequired());
		entity.setReadOnly(tascaDocument.isReadOnly());
		Tasca tasca = tascaRepository.findOne(tascaId);
		entity.setTasca(tasca);
		entity.setDocument(documentRepository.findOne(tascaDocument.getDocument().getId()));
		if (tascaDocument.getExpedientTipusId() != null) {
			ExpedientTipus expedientTipus = expedientTipusRepository.findById(tascaDocument.getExpedientTipusId());
			// Només relaciona el camp tasca amb el tipus d'expedient si la tasca està heretada
			if (definicioProcesHelper.isTascaHeretada(tasca, expedientTipus))
				entity.setExpedientTipus(expedientTipus);
		}
		
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
		Long expedientTipusId = tascaDocument.getExpedientTipus() != null? tascaDocument.getExpedientTipus().getId() : null;
		tascaDocument.getTasca().removeDocument(tascaDocument);
		documentTascaRepository.delete(tascaDocument);	
		documentTascaRepository.flush();
		reordenarDocumentsTasca(
				tascaDocument.getTasca().getId(),
				expedientTipusId);
	}

	/** Funció per reasignar el valor d'ordre dins dels documents d'una tasca de tipus registre. */
	private void reordenarDocumentsTasca(
			Long tascaId,
			Long expedientTipusId) {
		List<DocumentTasca> tascaDocuments = documentTascaRepository.findAmbTascaOrdenats(tascaId, expedientTipusId);		
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
			Long expedientTipusId, 
			int posicio) {
		
		boolean ret = false;
		DocumentTasca tascaDocument = documentTascaRepository.findOne(id);
		if (tascaDocument != null) {
			if (expedientTipusId == null) {
				Tasca tasca = tascaRepository.findById(tascaDocument.getTasca().getId());
				if (tasca.getDefinicioProces().getExpedientTipus() != null)
					expedientTipusId = tasca.getDefinicioProces().getExpedientTipus().getId();
			}
			List<DocumentTasca> documentsTasca = documentTascaRepository.findAmbTascaOrdenats(
					tascaDocument.getTasca().getId(),
					expedientTipusId);
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
			Long expedientTipusId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant els documents de la tasca d'una definició de proces per datatable (" +
				"tascaId=" + tascaId + ", " +
				"filtre=" + filtre + ")");

		Tasca tasca = tascaRepository.findOne(tascaId);
		
		// Consulta el tipus d'expedient
		ExpedientTipus expedientTipus = null;
		if (expedientTipusId != null)
			expedientTipus = expedientTipusRepository.findById(expedientTipusId);
		else {
			expedientTipus = tasca.getDefinicioProces().getExpedientTipus();
			if (expedientTipus != null)
				expedientTipusId = expedientTipus.getId();
		}
		Page<DocumentTasca> page = documentTascaRepository.findByFiltrePaginat(
				tascaId,
				expedientTipusId,
				filtre == null,
				filtre,
				paginacioHelper.toSpringDataPageable(
						paginacioParams)); 
		PaginaDto<DocumentTascaDto> pagina = paginacioHelper.toPaginaDto(
				page,
				DocumentTascaDto.class);
		// Herencia
		if (HerenciaHelper.ambHerencia(expedientTipus)) {
			// Consulta els sobreescrits
			Map<String, Document> sobreescrits = new HashMap<String, Document>();
			for (Document d : documentRepository.findSobreescrits(expedientTipusId))
				sobreescrits.put(d.getCodi(), d);
			// Construeix la llista d'heretats
			Set<Long> heretatsIds = new HashSet<Long>();
			Set<Long> documentsHeretatsIds = new HashSet<Long>();
			for (DocumentTasca ct : page.getContent()) {
				if (definicioProcesHelper.isDocumentTascaHeretat(ct, expedientTipus))
					heretatsIds.add(ct.getId());
				if (definicioProcesHelper.isDocumentHeretat(ct.getDocument(), expedientTipus))
					documentsHeretatsIds.add(ct.getDocument().getId());
			}
			Set<String> documentsSobreescritsCodis = new HashSet<String>();
			for (Document d : documentRepository.findSobreescrits(expedientTipusId)) {
				documentsSobreescritsCodis.add(d.getCodi());
			}
			// Escriu les propietats als dtos
			for (DocumentTascaDto dto : pagina.getContingut()) {
				// Determina si el documentTasca és heretat segons si el tipus d'expedient de la tasca és igual al tipus d'expedient pare del tipus d'expedient passat com a paràmetre
				if (heretatsIds.contains(dto.getId()))
					dto.setHeretat(true);
				// Completa l'informació del document del campTasca
				// Sobreescriu
				if (sobreescrits.containsKey(dto.getDocument().getCodi())) {
					dto.setDocument(conversioTipusHelper.convertir(sobreescrits.get(dto.getDocument().getCodi()), DocumentDto.class));
					dto.getDocument().setSobreescriu(true);
				}
				// Heretat
				if (documentsHeretatsIds.contains(dto.getDocument().getId()) && ! dto.getDocument().isSobreescriu())
					dto.getDocument().setHeretat(true);				
			}
		}
		return pagina;		
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
			Long expedientTipusId,
			Long documentTascaId) {
		logger.debug(
				"Consultant el document tasca amb id (" +
				"documentTascaId = " + documentTascaId + ")");
		DocumentTasca documentTasca = documentTascaRepository.findOne(documentTascaId);
		if (documentTasca == null) {
			throw new NoTrobatException(DocumentTasca.class, documentTascaId);
		}
		DocumentTascaDto dto = conversioTipusHelper.convertir(
				documentTasca, 
				DocumentTascaDto.class);

		// Herencia
		if (expedientTipusId != null) {
			// Consulta el tipus d'expedient
			ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId);
			// Herència
			dto.setHeretat(definicioProcesHelper.isDocumentTascaHeretat(documentTasca, expedientTipus));
		}
		return dto;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<DocumentTascaDto> tascaDocumentFindAll(Long expedientTipusId, Long tascaId) {
		logger.debug(
				"Consultant tots els documents de la tasca amb id (" +
				"tascaId = " + tascaId + ")");

		List<DocumentTasca> documentsTasca = documentTascaRepository.findAmbTascaIdOrdenats(tascaId, expedientTipusId);
		
		List<DocumentTascaDto> dtos = new ArrayList<DocumentTascaDto>();
		
		// Consulta la tasca i el tipus d'expedient
		ExpedientTipus expedientTipus = expedientTipusId != null ? 
				expedientTipus = expedientTipusRepository.findById(expedientTipusId) 
				: null;

		DocumentTascaDto dto;
		for (DocumentTasca documentTasca : documentsTasca) {
			dto = conversioTipusHelper.convertir(
					documentTasca, 
					DocumentTascaDto.class);
			// Herencia
			dto.setHeretat(definicioProcesHelper.isDocumentTascaHeretat(documentTasca, expedientTipus)); 
			dtos.add(dto);
		}
		return dtos;
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
		
		entity.setOrder(firmaTascaRepository.getNextOrdre(tascaId, tascaFirma.getExpedientTipusId()));		
		entity.setRequired(tascaFirma.isRequired());
		Tasca tasca = tascaRepository.findOne(tascaId);
		entity.setTasca(tasca);
		entity.setDocument(documentRepository.findOne(tascaFirma.getDocument().getId()));
		if (tascaFirma.getExpedientTipusId() != null) {
			ExpedientTipus expedientTipus = expedientTipusRepository.findById(tascaFirma.getExpedientTipusId());
			// Només relaciona el camp tasca amb el tipus d'expedient si la tasca està heretada
			if (definicioProcesHelper.isTascaHeretada(tasca, expedientTipus))
				entity.setExpedientTipus(expedientTipus);
		}
		
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
		Long expedientTipusId = tascaFirma.getExpedientTipus() != null? tascaFirma.getExpedientTipus().getId() : null;
		firmaTascaRepository.delete(tascaFirma);	
		firmaTascaRepository.flush();
		reordenarFirmesTasca(
				tascaFirma.getTasca().getId(), 
				expedientTipusId);
	}

	/** Funció per reasignar el valor d'ordre dins de les firmes d'una tasca de tipus registre. */
	private void reordenarFirmesTasca(
			Long tascaId,
			Long expedientTipusId) {
		List<FirmaTasca> tascaFirmes = firmaTascaRepository.findAmbTascaIdOrdenats(tascaId, expedientTipusId);		
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
			Long expedientTipusId, 
			int posicio) {
		
		boolean ret = false;
		FirmaTasca tascaFirma = firmaTascaRepository.findOne(id);
		if (tascaFirma != null) {
			if (expedientTipusId == null) {
				Tasca tasca = tascaRepository.findById(tascaFirma.getTasca().getId());
				if (tasca.getDefinicioProces().getExpedientTipus() != null)
					expedientTipusId = tasca.getDefinicioProces().getExpedientTipus().getId();
			}
			List<FirmaTasca> firmesTasca = firmaTascaRepository.findAmbTascaIdOrdenats(
					tascaFirma.getTasca().getId(),
					expedientTipusId);
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
			Long expedientTipusId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant les firmes de la tasca d'una definició de proces per datatable (" +
				"tascaId=" + tascaId + ", " +
				"filtre=" + filtre + ")");

		Tasca tasca = tascaRepository.findOne(tascaId);

		// Consulta el tipus d'expedient
		ExpedientTipus expedientTipus = null;
		if (expedientTipusId != null)
			expedientTipus = expedientTipusRepository.findById(expedientTipusId);
		else {
			expedientTipus = tasca.getDefinicioProces().getExpedientTipus();
			if (expedientTipus != null)
				expedientTipusId = expedientTipus.getId();
		}
		Page<FirmaTasca> page = firmaTascaRepository.findByFiltrePaginat(
				tascaId,
				expedientTipusId,
				filtre == null,
				filtre,
				paginacioHelper.toSpringDataPageable(
						paginacioParams));
		PaginaDto<FirmaTascaDto> pagina = paginacioHelper.toPaginaDto(
				page,
				FirmaTascaDto.class);		// Herencia
		if (HerenciaHelper.ambHerencia(expedientTipus)) {
			// Consulta els sobreescrits
			Map<String, Document> sobreescrits = new HashMap<String, Document>();
			for (Document d : documentRepository.findSobreescrits(expedientTipusId))
				sobreescrits.put(d.getCodi(), d);
			// Construeix la llista d'heretats
			Set<Long> heretatsIds = new HashSet<Long>();
			Set<Long> documentsHeretatsIds = new HashSet<Long>();
			for (FirmaTasca ct : page.getContent()) {
				if (definicioProcesHelper.isFirmaTascaHeretada(ct, expedientTipus))
					heretatsIds.add(ct.getId());
				if (definicioProcesHelper.isDocumentHeretat(ct.getDocument(), expedientTipus))
					documentsHeretatsIds.add(ct.getDocument().getId());
			}
			// Escriu les propietats als dtos
			for (FirmaTascaDto dto : pagina.getContingut()) {
				// Determina si el documentTasca és heretat segons si el tipus d'expedient de la tasca és igual al tipus d'expedient pare del tipus d'expedient passat com a paràmetre
				if (heretatsIds.contains(dto.getId()))
					dto.setHeretat(true);
				// Completa l'informació del document del campTasca
				// Sobreescriu
				if (sobreescrits.containsKey(dto.getDocument().getCodi())) {
					dto.setDocument(conversioTipusHelper.convertir(sobreescrits.get(dto.getDocument().getCodi()), DocumentDto.class));
					dto.getDocument().setSobreescriu(true);
				}
				// Heretat
				if (documentsHeretatsIds.contains(dto.getDocument().getId()) && ! dto.getDocument().isSobreescriu())
					dto.getDocument().setHeretat(true);				
			}
		}
		return pagina;
	}
	
	@Override
	@Transactional(readOnly = true)
	public FirmaTascaDto tascaFirmaFindAmbTascaDocument(
			Long tascaId,
			Long documentId,
			Long expedientTipusId) {
		logger.debug(
				"Consultant la firma per una tasca i document(" +
				"tascaId=" + tascaId +
				", documentId=" + documentId + ")");
		return conversioTipusHelper.convertir(
				firmaTascaRepository.findAmbDocumentTasca(documentId, tascaId, expedientTipusId), 
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
			Long expedientTipusId,
			Long firmaTascaId) {
		logger.debug(
				"Consultant el document tasca amb id (" +
				"firmaTascaId = " + firmaTascaId + ")");
		FirmaTasca firmaTasca = firmaTascaRepository.findOne(firmaTascaId);
		if (firmaTasca == null) {
			throw new NoTrobatException(FirmaTasca.class, firmaTascaId);
		}
		FirmaTascaDto dto = conversioTipusHelper.convertir(
				firmaTasca, 
				FirmaTascaDto.class);

		// Herencia
		if (expedientTipusId != null) {
			// Consulta el tipus d'expedient
			ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId);
			// Herència
			dto.setHeretat(definicioProcesHelper.isFirmaTascaHeretada(firmaTasca, expedientTipus));			
		}
		return dto;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<FirmaTascaDto> tascaFirmaFindAll(Long expedientTipusId, Long tascaId) {
		logger.debug(
				"Consultant tots els firmas de la tasca amb id (" +
				"tascaId = " + tascaId + ")");

		List<FirmaTasca> firmesTasca = firmaTascaRepository.findAmbTascaIdOrdenats(tascaId, expedientTipusId);
		
		List<FirmaTascaDto> dtos = new ArrayList<FirmaTascaDto>();
		
		// Consulta la tasca i el tipus d'expedient
		ExpedientTipus expedientTipus = expedientTipusId != null ? 
				expedientTipus = expedientTipusRepository.findById(expedientTipusId) 
				: null;

		FirmaTascaDto dto;
		for (FirmaTasca firmaTasca : firmesTasca) {
			dto = conversioTipusHelper.convertir(
					firmaTasca, 
					FirmaTascaDto.class);
			// Herencia
			dto.setHeretat(definicioProcesHelper.isFirmaTascaHeretada(firmaTasca, expedientTipus)); 
			dtos.add(dto);
		}
		return dtos;
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
		// Recupera la definició de procés per id
		DefinicioProces definicioProces = definicioProcesRepository.findById(
				definicioProcesId);
		// Control d'accés
		if (definicioProces.getExpedientTipus() != null)			
			expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(
					definicioProces.getExpedientTipus().getId());
		else
			entornHelper.getEntornComprovantPermisos(EntornActual.getEntornId(), true, true);

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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void relacionarDarreresVersions(Long expedientTipusId) {
		logger.debug(
				"Relacionant les darreres versions de les definicions de procés del tipus d'expedient (" +
				"expedientTipusId = " + expedientTipusId + ")");
		
		ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId);
		definicioProcesHelper.relacionarDarreresVersionsDefinicionsProces(expedientTipus.getDefinicionsProces());
	}
	
	private static final Logger logger = LoggerFactory.getLogger(DefinicioProcesServiceImpl.class);

}