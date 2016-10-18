/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

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
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.model.hibernate.Accio;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampAgrupacio;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.FirmaTasca;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioDto;
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
	private DominiRepository dominiRepository;
	@Resource
	private TascaRepository tascaRepository;
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

	@Resource
	private DefinicioProcesHelper definicioProcesHelper;
	@Resource
	private EntornHelper entornHelper;
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
		return conversioTipusHelper.convertir(
				definicioProces,
				DefinicioProcesDto.class);
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
				"expedientTipusId = " + command.getExpedientTipusId() + ", " + 
				"definicioProcesId = " + command.getId() + ", " + 
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
				command.isSobreEscriure());
		
		if (importat != null)
			ret = conversioTipusHelper.convertir(importat, DefinicioProcesDto.class);
		return ret;
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
	public PaginaDto<CampDto> campFindPerDatatable(
			Long entornId, 
			Long definicioProcesId, 
			Long agrupacioId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		
		logger.debug(
				"Consultant els camps per la definició de procés per datatable (" +
				"entornId=" + entornId + ", " +
				"definicioProcesId=" + definicioProcesId + ", " +
				"agrupacioId=" + agrupacioId + ", " +
				"filtre=" + filtre + ")");
						
		
		PaginaDto<CampDto> pagina = paginacioHelper.toPaginaDto(
				campRepository.findByFiltrePaginat(
						null,
						definicioProcesId,
						agrupacioId == null,
						agrupacioId != null ? agrupacioId : 0L,
						filtre == null || "".equals(filtre), 
						filtre, 
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				CampDto.class);
		
		// Omple els comptador de validacions i de membres
		List<Object[]> countValidacions = campRepository.countValidacions(
				null,
				definicioProcesId,
				agrupacioId == null,
				agrupacioId); 
		List<Object[]> countMembres= campRepository.countMembres(
				null,
				definicioProcesId,
				agrupacioId == null,
				agrupacioId); 
		for (CampDto dto: pagina.getContingut()) {
			for (Object[] reg: countValidacions) {
				Long campId = (Long)reg[0];
				if (campId.equals(dto.getId())) {
					Integer count = (Integer)reg[1];
					dto.setValidacioCount(count.intValue());
					countValidacions.remove(reg);
					break;
				}
			}
			if (dto.getTipus() == CampTipusDto.REGISTRE) {
				for (Object[] reg: countMembres) {
					Long campId = (Long)reg[0];
					if (campId.equals(dto.getId())) {
						Integer count = (Integer)reg[1];
						dto.setCampRegistreCount(count.intValue());
						countMembres.remove(reg);
						break;
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
	
	
	// MANTENIMENT DE CAMPS
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public CampDto campCreate(
			Long definicioProcesId, 
			CampDto camp) throws PermisDenegatException {

		logger.debug(
				"Creant nou camp per una definicio de procés (" +
				"definicioProcesId =" + definicioProcesId + ", " +
				"camp=" + camp + ")");
		DefinicioProces definicioProces = definicioProcesRepository.findOne(definicioProcesId);
		
		Camp entity = new Camp();
		entity.setCodi(camp.getCodi());
		entity.setTipus(conversioTipusHelper.convertir(camp.getTipus(), Camp.TipusCamp.class));
		entity.setEtiqueta(camp.getEtiqueta());
		entity.setObservacions(camp.getObservacions());
		entity.setMultiple(camp.isMultiple());
		entity.setOcult(camp.isOcult());
		entity.setIgnored(camp.isIgnored());
		CampAgrupacio agrupacio = null;
		if (camp.getAgrupacio() != null) 
			agrupacio = campAgrupacioRepository.findOne(camp.getAgrupacio().getId());
		entity.setAgrupacio(agrupacio);
		if (agrupacio != null && entity.getOrdre() == null) {
			// Informa de l'ordre dins de la agrupació
			entity.setOrdre(
					campRepository.getNextOrdre(
							agrupacio.getId()));
		}		
		// Camp associat a la definicio de procés
		entity.setDefinicioProces(definicioProces);		
		
		// Dades consulta
		Enumeracio enumeracio = null;
		if (camp.getEnumeracio() != null) {
			enumeracio = enumeracioRepository.findOne(camp.getEnumeracio().getId());
		}
		entity.setEnumeracio(enumeracio);
		Domini domini = null;
		if (camp.getDomini() != null) {
			domini = dominiRepository.findOne(camp.getDomini().getId());
		}
		entity.setDomini(domini);
		Consulta consulta = null;
		if (camp.getConsulta() != null) {
			consulta = consultaRepository.findOne(camp.getConsulta().getId());
		}
		entity.setConsulta(consulta);		
		entity.setDominiIntern(camp.isDominiIntern());

		// Paràmetres del domini
		entity.setDominiId(camp.getDominiIdentificador());
		entity.setDominiParams(camp.getDominiParams());
		entity.setDominiCampValor(camp.getDominiCampValor());
		entity.setDominiCampText(camp.getDominiCampText());
		
		// Paràmetres de la consulta
		entity.setConsultaParams(camp.getConsultaParams());
		entity.setConsultaCampValor(camp.getConsultaCampValor());
		entity.setConsultaCampText(camp.getConsultaCampText());
		
		// Dades de la acció
		entity.setDefprocJbpmKey(camp.getDefprocJbpmKey());
		entity.setJbpmAction(camp.getJbpmAction());
		
		entity.setDominiCacheText(camp.isDominiCacheText());

		return conversioTipusHelper.convertir(
				campRepository.save(entity),
				CampDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public CampDto campUpdate(CampDto camp) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Modificant el camp de la definició de procés existent (" +
				"camp.id=" + camp.getId() + ", " +
				"camp =" + camp + ")");
		Camp entity = campRepository.findOne(camp.getId());
		entity.setCodi(camp.getCodi());
		entity.setTipus(conversioTipusHelper.convertir(camp.getTipus(), Camp.TipusCamp.class));
		entity.setEtiqueta(camp.getEtiqueta());
		entity.setObservacions(camp.getObservacions());
		entity.setMultiple(camp.isMultiple());
		entity.setOcult(camp.isOcult());
		entity.setIgnored(camp.isIgnored());
		CampAgrupacio agrupacio = null;
		if (camp.getAgrupacio() != null) 
			agrupacio = campAgrupacioRepository.findOne(camp.getAgrupacio().getId());
		entity.setAgrupacio(agrupacio);
		if (agrupacio != null && entity.getOrdre() == null) {
			// Informa de l'ordre dins de la agrupació
			entity.setOrdre(
					campRepository.getNextOrdre(
							agrupacio.getId()));
		}		
		
		// Dades consulta
		Enumeracio enumeracio = null;
		if (camp.getEnumeracio() != null) {
			enumeracio = enumeracioRepository.findOne(camp.getEnumeracio().getId());
		}
		entity.setEnumeracio(enumeracio);
		Domini domini = null;
		if (camp.getDomini() != null) {
			domini = dominiRepository.findOne(camp.getDomini().getId());
		}
		entity.setDomini(domini);
		Consulta consulta = null;
		if (camp.getConsulta() != null) {
			consulta = consultaRepository.findOne(camp.getConsulta().getId());
		}
		entity.setConsulta(consulta);		
		entity.setDominiIntern(camp.isDominiIntern());

		// Paràmetres del domini
		entity.setDominiId(camp.getDominiIdentificador());
		entity.setDominiParams(camp.getDominiParams());
		entity.setDominiCampValor(camp.getDominiCampValor());
		entity.setDominiCampText(camp.getDominiCampText());
		
		// Paràmetres de la consulta
		entity.setConsultaParams(camp.getConsultaParams());
		entity.setConsultaCampValor(camp.getConsultaCampValor());
		entity.setConsultaCampText(camp.getConsultaCampText());
		
		// Dades de la acció
		entity.setDefprocJbpmKey(camp.getDefprocJbpmKey());
		entity.setJbpmAction(camp.getJbpmAction());

		entity.setDominiCacheText(camp.isDominiCacheText());
		
		return conversioTipusHelper.convertir(
				campRepository.save(entity),
				CampDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void campDelete(Long campCampId) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Esborrant el camp de la definició de procés (" +
				"campId=" + campCampId +  ")");
		Camp entity = campRepository.findOne(campCampId);

		if (entity != null) {
			for (CampRegistre campRegistre : entity.getRegistrePares()) {
				campRegistre.getRegistre().getRegistreMembres().remove(campRegistre);
				campRegistreRepository.delete(campRegistre);	
				campRegistreRepository.flush();
				reordenarCampsRegistre(campRegistre.getRegistre().getId());						
			}
			campRepository.delete(entity);	
			campRepository.flush();
			if (entity.getAgrupacio() != null) {
				reordenarCamps(entity.getAgrupacio().getId());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CampDto campFindAmbId(Long id) throws NoTrobatException {
		logger.debug(
				"Consultant el camp de la definició de procés amb id (" +
				"campId=" + id +  ")");
		Camp camp = campRepository.findOne(id);
		if (camp == null) {
			throw new NoTrobatException(Camp.class, id);
		}
		return conversioTipusHelper.convertir(
				camp,
				CampDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CampDto campFindAmbCodiPerValidarRepeticio(Long definicioProcesId, String codi) throws NoTrobatException {
		logger.debug(
				"Consultant el camp de la definició de procés per codi per validar repetició (" +
				"definicioProcesId=" + definicioProcesId + ", " +
				"codi = " + codi + ")");
		DefinicioProces definicioProces = definicioProcesRepository.findOne(definicioProcesId);
		return conversioTipusHelper.convertir(
				campRepository.findByDefinicioProcesAndCodi(definicioProces, codi),
				CampDto.class);
	}	
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<CampDto> campFindPerDatatable(
			Long definicioProcesId,
			Long agrupacioId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant els camps per la definicio de procés per datatable (" +
				"definicioProcesId=" + definicioProcesId + ", " +
				"agrupacioId=" + agrupacioId + ", " +
				"filtre=" + filtre + ")");
						
		
		PaginaDto<CampDto> pagina = paginacioHelper.toPaginaDto(
				campRepository.findByFiltrePaginat(
						null,
						definicioProcesId,
						agrupacioId == null,
						agrupacioId != null ? agrupacioId : 0L,
						filtre == null || "".equals(filtre), 
						filtre, 
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				CampDto.class);
		
		// Omple els comptador de validacions i de membres
		List<Object[]> countValidacions = campRepository.countValidacions(
				null,
				definicioProcesId,
				agrupacioId == null,
				agrupacioId); 
		List<Object[]> countMembres= campRepository.countMembres(
				null,
				definicioProcesId,
				agrupacioId == null,
				agrupacioId); 
		for (CampDto dto: pagina.getContingut()) {
			for (Object[] reg: countValidacions) {
				Long campId = (Long)reg[0];
				if (campId.equals(dto.getId())) {
					Integer count = (Integer)reg[1];
					dto.setValidacioCount(count.intValue());
					countValidacions.remove(reg);
					break;
				}
			}
			if (dto.getTipus() == CampTipusDto.REGISTRE) {
				for (Object[] reg: countMembres) {
					Long campId = (Long)reg[0];
					if (campId.equals(dto.getId())) {
						Integer count = (Integer)reg[1];
						dto.setCampRegistreCount(count.intValue());
						countMembres.remove(reg);
						break;
					}
				}
			}
		}		
		
		return pagina;		
		
	}
	
	@Override
	@Transactional
	public boolean campMourePosicio(
			Long id, 
			int posicio) {
		boolean ret = false;
		Camp camp = campRepository.findOne(id);
		if (camp != null && camp.getAgrupacio() != null) {
			List<Camp> camps = campRepository.findByAgrupacioIdOrderByOrdreAsc(camp.getAgrupacio().getId());
			if(posicio != camps.indexOf(camp)) {
				camps.remove(camp);
				camps.add(posicio, camp);
				int i = 0;
				for (Camp c : camps) {
					c.setOrdre(i++);
				}
			}
		}
		return ret;
	}
	
	@Transactional(readOnly = true)
	public List<CampDto> campFindTipusDataPerDefinicioProces(
			Long definicioProcesId) {
		logger.debug(
				"Consultant els camps del tipus data" +
				" per la definicio de procés desplegable");
		
		DefinicioProces definicioProces = definicioProcesRepository.findOne(definicioProcesId);
		
		List<Camp> camps = campRepository.findByDefinicioProcesAndTipus(definicioProces, TipusCamp.DATE);
		
		return conversioTipusHelper.convertirList(
				camps, 
				CampDto.class);
	}

	
	// MANTENIMENT D'AGRUPACIONS DE CAMPS

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<CampAgrupacioDto> agrupacioFindAll(Long definicioProcesId){
		List<CampAgrupacio> agrupacions = campAgrupacioRepository.findAmbDefinicioProcesOrdenats(definicioProcesId);
		return conversioTipusHelper.convertirList(
									agrupacions, 
									CampAgrupacioDto.class);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CampAgrupacioDto agrupacioFindAmbId(Long id) throws NoTrobatException {
		logger.debug(
				"Consultant la agrupacio de camps del la definició de procés amb id (" +
				"campAgrupacioId=" + id +  ")");
		CampAgrupacio  agrupacio = campAgrupacioRepository.findOne(id);
		if (agrupacio == null) {
			throw new NoTrobatException(CampAgrupacio.class, id);
		}
		return conversioTipusHelper.convertir(
				agrupacio,
				CampAgrupacioDto.class);
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public CampAgrupacioDto agrupacioCreate(
			Long definicioProcesId, 
			CampAgrupacioDto agrupacio) throws PermisDenegatException {

		logger.debug(
				"Creant nova agrupació de camp per una definicio de procés (" +
				"definicioProcesId =" + definicioProcesId + ", " +
				"agrupacio=" + agrupacio + ")");
		DefinicioProces definicioProces = definicioProcesRepository.findOne(definicioProcesId);
		
		CampAgrupacio entity = new CampAgrupacio();
		entity.setCodi(agrupacio.getCodi());
		entity.setNom(agrupacio.getNom());
		entity.setDescripcio(agrupacio.getDescripcio());
		entity.setOrdre(campAgrupacioRepository.getNextOrdre(null, definicioProcesId));

		// Camp associat a la definicio de procés
		entity.setDefinicioProces(definicioProces);

		return conversioTipusHelper.convertir(
				campAgrupacioRepository.save(entity),
				CampAgrupacioDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public CampAgrupacioDto agrupacioUpdate(
			CampAgrupacioDto agrupacio) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Modificant la agrupacio de camp de la definició de procés existent (" +
				"agrupacio.id=" + agrupacio.getId() + ", " +
				"agrupacio =" + agrupacio + ")");
		CampAgrupacio entity = campAgrupacioRepository.findOne(agrupacio.getId());
		entity.setCodi(agrupacio.getCodi());
		entity.setNom(agrupacio.getNom());
		entity.setDescripcio(agrupacio.getDescripcio());
		
		return conversioTipusHelper.convertir(
				campAgrupacioRepository.save(entity),
				CampAgrupacioDto.class);
	}
	
	@Override
	@Transactional
	public boolean agrupacioMourePosicio(
			Long id, 
			int posicio) {
		boolean ret = false;
		CampAgrupacio agrupacio = campAgrupacioRepository.findOne(id);
		if (agrupacio != null) {
			List<CampAgrupacio> agrupacions = campAgrupacioRepository.findAmbDefinicioProcesOrdenats(agrupacio.getDefinicioProces().getId());
			if(posicio != agrupacions.indexOf(agrupacio)) {
				agrupacions.remove(agrupacio);
				agrupacions.add(posicio, agrupacio);
				int i = 0;
				for (CampAgrupacio c : agrupacions) {
					c.setOrdre(i++);
				}
			}
		}
		return ret;
	}	

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void agrupacioDelete(Long agrupacioCampId) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Esborrant la agrupacio de camp de la definició de procés (" +
				"agrupacioCampId=" + agrupacioCampId +  ")");
		CampAgrupacio entity = campAgrupacioRepository.findOne(agrupacioCampId);
		if (entity != null) {			
			for (Camp camp : entity.getCamps()) {
				camp.setAgrupacio(null);
				camp.setOrdre(null);
				campRepository.save(camp);
			}			
			campAgrupacioRepository.delete(entity);
			campAgrupacioRepository.flush();
		}
		reordenarAgrupacions(entity.getDefinicioProces().getId());
	}
	
	/** Funció per reasignar el valor d'ordre per a les agrupacions d'una definicio de procés */
	@Transactional
	private int reordenarAgrupacions(Long definicioProcesId) {
		DefinicioProces definicioProces = definicioProcesRepository.findOne(definicioProcesId);
		List<CampAgrupacio> campsAgrupacio = definicioProces.getAgrupacions();
		int i = 0;
		for (CampAgrupacio campAgrupacio: campsAgrupacio)
			campAgrupacio.setOrdre(i++);
		return campsAgrupacio.size();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CampAgrupacioDto agrupacioFindAmbCodiPerValidarRepeticio(
								Long definicioProcesId, 
								String codi) throws NoTrobatException {
		logger.debug(
				"Consultant la agrupacio de camps de la definició de procés per codi per validar repetició (" +
				"definicioProcesId=" + definicioProcesId + ", " +
				"codi = " + codi + ")");
		DefinicioProces definicioProces = definicioProcesRepository.findOne(definicioProcesId);
		return conversioTipusHelper.convertir(
				campAgrupacioRepository.findByDefinicioProcesAndCodi(definicioProces, codi),
				CampAgrupacioDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<CampAgrupacioDto> agrupacioFindPerDatatable(
			Long definicioProcesId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant les agrupacions per la definicio de procés per datatable (" +
				"definicioProcesId=" + definicioProcesId + ", " +
				"filtre=" + filtre + ")");
						
		
		return paginacioHelper.toPaginaDto(
				campAgrupacioRepository.findByFiltrePaginat(
						null,
						definicioProcesId,
						filtre == null || "".equals(filtre), 
						filtre, 
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				CampAgrupacioDto.class);		
	}	
	
	@Override
	@Transactional
	public boolean campAfegirAgrupacio(
			Long campId, 
			Long agrupacioId) {
		boolean ret = false;
		logger.debug(
				"Afegint camp de definicio de proces a la agrupació (" +
				"campId=" + campId + ", " +
				"agrupacioId = " + agrupacioId + ")");
		Camp camp = campRepository.findOne(campId);
		CampAgrupacio agrupacio = campAgrupacioRepository.findOne(agrupacioId);
		if (camp != null && agrupacio != null && camp.getDefinicioProces().getId().equals(agrupacio.getDefinicioProces().getId())) {
			camp.setAgrupacio(agrupacio);
			reordenarCamps(agrupacioId);
			ret = true;
		}
		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public boolean campRemoureAgrupacio(Long campId) {
		boolean ret = false;
		logger.debug(
				"Remoguent el camp de tipus de definició de procés de la seva agrupació(" +
				"campId=" + campId + ")");
		Camp camp = campRepository.findOne(campId);
		if (camp != null && camp.getAgrupacio() != null) {
			Long agrupacioId = camp.getAgrupacio().getId();
			camp.setAgrupacio(null);
			camp.setOrdre(null);
			reordenarCamps(agrupacioId);
			ret = true;
		}
		return ret;
	}
	/////////////////
	/////////////////
	
	
	/** Funció per reasignar el valor d'ordre dins d'una agrupació de variables. */
	private void reordenarCamps(Long agrupacioId) {
		List<Camp> camps = campRepository.findByAgrupacioIdOrderByOrdreAsc(agrupacioId);		
		int i = 0;
		for (Camp camp: camps)
			camp.setOrdre(i++);
	}
	
	/** Funció per reasignar el valor d'ordre dins dels camps d'una variable de tipus registre. */
	private void reordenarCampsRegistre(Long campId) {
		List<CampRegistre> campRegistres = campRegistreRepository.findAmbCampOrdenats(campId);		
		int i=-1;
		for (CampRegistre c : campRegistres) {
			c.setOrdre(i);
			campRegistreRepository.saveAndFlush(c);
			i--;
		}
		i = 0;
		for (CampRegistre c : campRegistres) {
			c.setOrdre(i);
			campRegistreRepository.saveAndFlush(c);
			i++;
		}
	}
	
	
	// MANTENIMENT D'ENUMERACIONS
	@Override
	@Transactional(readOnly = true)
	public List<EnumeracioDto> enumeracioFindByEntorn(
			Long entornId) {
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		List<Enumeracio> enumeracions = enumeracioRepository.findByEntorn(entorn);
		return conversioTipusHelper.convertirList(
									enumeracions, 
									EnumeracioDto.class);
	}
	
	// MANTENIMENT DE DOMINIS

	@Override
	@Transactional(readOnly = true)
	public List<DominiDto> dominiFindByEntorn(
			Long entornId) {
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		List<Domini> dominins = dominiRepository.findByEntorn(entorn);
		return conversioTipusHelper.convertirList(
									dominins, 
									DominiDto.class);
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

	@Override
	@Transactional(readOnly = true)
	public List<DocumentDto> documentFindAllOrdenatsPerCodi(Long definicioProcesId) {
		logger.debug(
				"Consultant tots els documents de la definicio de proces per al desplegable " +
				" de documents de la tasca a la definicio de processos (definicioProcesId=" + definicioProcesId + ")");
				
		List<Document> documents = documentRepository.findAmbDefinicioProces(definicioProcesId);
		
		return conversioTipusHelper.convertirList(
				documents, 
				DocumentDto.class);
	}	
	
	@Override
	@Transactional(readOnly = true)
	public DocumentDto documentFindAmbCodi(Long definicioProcesId, String codi) {
		DocumentDto ret = null; 
		logger.debug(
				"Consultant el document del tipus d'expedient per codi (" +
				"definicioProcesId=" + definicioProcesId + ", " +
				"codi = " + codi + ")");
		DefinicioProces definicioProces = definicioProcesRepository.findOne(definicioProcesId);
		Document document = documentRepository.findByDefinicioProcesAndCodi(definicioProces, codi);
		if (document != null)
		ret = conversioTipusHelper.convertir(
				document,
				DocumentDto.class);
		return ret;
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
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AccioDto> accioFindAll(
			Long definicioProcesId) throws NoTrobatException, PermisDenegatException {
		List<Accio> accions = accioRepository.findAmbDefinicioProces(definicioProcesId);
		return conversioTipusHelper.convertirList(
									accions, 
									AccioDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CampDto campFindAmbCodi(Long definicioProcesId, String codi) {
		CampDto ret = null;
		logger.debug(
				"Consultant el camp de la definicio de proces per codi (" +
				"definicioProcesId=" + definicioProcesId + ", " +
				"codi = " + codi + ")");
		DefinicioProces definicioProces = definicioProcesRepository.findOne(definicioProcesId);
		Camp camp = campRepository.findByDefinicioProcesAndCodi(definicioProces, codi);
		if (camp != null)
			ret = conversioTipusHelper.convertir(
				camp,
				CampDto.class);
		return ret;
	}	
	
	private static final Logger logger = LoggerFactory.getLogger(DefinicioProcesServiceImpl.class);
}