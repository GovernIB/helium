/**
 * 
 */
package es.caib.helium.logic.service;

import es.caib.helium.logic.helper.ConversioTipusServiceHelper;
import es.caib.helium.logic.helper.ExpedientTipusHelper;
import es.caib.helium.logic.helper.HerenciaHelper;
import es.caib.helium.logic.helper.NotificacioHelper;
import es.caib.helium.logic.helper.PaginacioHelper;
import es.caib.helium.logic.intf.dto.ArxiuDto;
import es.caib.helium.logic.intf.dto.DocumentDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.service.DocumentService;
import es.caib.helium.logic.util.PdfUtils;
import es.caib.helium.persist.entity.Document;
import es.caib.helium.persist.entity.DocumentTasca;
import es.caib.helium.persist.entity.ExpedientTipus;
import es.caib.helium.persist.repository.CampRepository;
import es.caib.helium.persist.repository.DefinicioProcesRepository;
import es.caib.helium.persist.repository.DocumentRepository;
import es.caib.helium.persist.repository.ExpedientTipusRepository;
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
 * Implementació del servei per a gestionar documents dels tipus d'expedients o definicions de procés.
 * 
 */
@Service
public class DocumentServiceImpl implements DocumentService {

	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private CampRepository campRepository;

	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private ConversioTipusServiceHelper conversioTipusServiceHelper;
	@Resource
	private NotificacioHelper notificacioHelper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<DocumentDto> findPerDatatable(
			Long expedientTipusId,
			Long definicioProcesId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant els documents per al tipus d'expedient per datatable (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"definicioProcesId =" + definicioProcesId + ", " +
				"filtre=" + filtre + ")");

		ExpedientTipus expedientTipus = expedientTipusId != null? expedientTipusHelper.getExpedientTipusComprovantPermisDissenyDelegat(expedientTipusId) : null;
		// Determina si hi ha herència 
		boolean ambHerencia = HerenciaHelper.ambHerencia(expedientTipus);

		Page<Document> page = documentRepository.findByFiltrePaginat(
				expedientTipusId,
				definicioProcesId,
				filtre == null || "".equals(filtre),
				filtre,
				ambHerencia,
				paginacioHelper.toSpringDataPageable(
						paginacioParams));
		
		PaginaDto<DocumentDto> pagina =  paginacioHelper.toPaginaDto(
						page,
						DocumentDto.class);
		// completa la informació d'herència
		if (ambHerencia) {
			// Llista d'heretats
			Set<Long> heretatsIds = new HashSet<Long>();
			for (Document d : page.getContent())
				if ( !expedientTipusId.equals(d.getExpedientTipus().getId()))
					heretatsIds.add(d.getId());
			// Llistat d'elements sobreescrits
			Set<String> sobreescritsCodis = new HashSet<String>();
			if (ambHerencia)
				for (Document d : documentRepository.findSobreescrits(expedientTipus.getId())) 
					sobreescritsCodis.add(d.getCodi());
			// Completa l'informació del dto
			for (DocumentDto dto : pagina.getContingut()) {
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
	
	@Override
	@Transactional
	public DocumentDto create(
					
			Long expedientTipusId, 
			Long definicioProcesId, 
			DocumentDto document) {

		logger.debug(
				"Creant nou document per un tipus d'expedient (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"definicioProcesId =" + definicioProcesId + ", " +
				"document=" + document + ")");		
		Document entity = new Document();
		entity.setCodi(document.getCodi());
		entity.setNom(document.getNom());
		entity.setDescripcio(document.getDescripcio());
		entity.setPlantilla(document.isPlantilla());
		entity.setNotificable(document.isNotificable());
		entity.setArxiuNom(document.getArxiuNom());
		entity.setArxiuContingut(document.getArxiuContingut());
		entity.setConvertirExtensio(document.getConvertirExtensio());
		entity.setAdjuntarAuto(document.isAdjuntarAuto());
		if (document.getCampData() != null) {
			entity.setCampData(campRepository.getById(document.getCampData().getId()));
		}
		entity.setExtensionsPermeses(document.getExtensionsPermeses());
		entity.setContentType(document.getContentType());
		entity.setCustodiaCodi(document.getCustodiaCodi());
		entity.setTipusDocPortasignatures(document.getTipusDocPortasignatures());
		entity.setIgnored(document.isIgnored());
  
				
		entity.setNtiOrigen(document.getNtiOrigen());
		entity.setNtiEstadoElaboracion(document.getNtiEstadoElaboracion());
		entity.setNtiTipoDocumental(document.getNtiTipoDocumental());
													  
   
  
		if (expedientTipusId != null)
			entity.setExpedientTipus(expedientTipusRepository.getById(expedientTipusId));
		if (definicioProcesId != null)
			entity.setDefinicioProces(definicioProcesRepository.getById(definicioProcesId));

		return conversioTipusServiceHelper.convertir(
				documentRepository.save(entity),
				DocumentDto.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public DocumentDto findAmbCodi(
			Long expedientTipusId, 
			Long definicioProcesId, 
			String codi,
			boolean ambHerencia) {
		DocumentDto ret = null; 
		logger.debug(
				"Consultant el document del tipus d'expedient per codi (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"definicioProcesId =" + definicioProcesId + ", " +
				"codi = " + codi + ")");
		Document document = null;
		if (expedientTipusId != null)
			document = documentRepository.findByExpedientTipusAndCodi(
											expedientTipusId, 
											codi,
											ambHerencia);
		else if(definicioProcesId != null)
			document = documentRepository.findByDefinicioProcesAndCodi(
											definicioProcesRepository.getById(definicioProcesId),
											codi);

		if (document != null) {
			ret = conversioTipusServiceHelper.convertir(
					document,
					DocumentDto.class);
			ret.setArxiuContingut(document.getArxiuContingut());
		}
		return ret;
	}
	
	@Override
	@Transactional
	public void delete(Long documentId) {
		logger.debug(
				"Esborrant el document del tipus d'expedient (" +
				"documentId=" + documentId +  ")");
		Document entity = documentRepository.getById(documentId);

		
		if (entity != null) {
			for (DocumentTasca documentTasca: entity.getTasques()) {
				documentTasca.getTasca().removeDocument(documentTasca);
				int i = 0;
				for (DocumentTasca dt: documentTasca.getTasca().getDocuments())
					dt.setOrder(i++);
			}
		} else {
			throw new NoTrobatException(Document.class);
		}
		documentRepository.delete(entity);	
	}
	
	@Override
	@Transactional
	public DocumentDto findAmbId(
			Long expedientTipusId, 
			Long id) throws NoTrobatException {
		logger.debug(
				"Consultant el document del tipus d'expedient amb id (" +
				"expedientTiusId=" + expedientTipusId + "," +
				"documentId=" + id +  ")");
		Document document = documentRepository.getById(id);
		if (document == null) {
			throw new NoTrobatException(Document.class, id);
		}
		DocumentDto dto = conversioTipusServiceHelper.convertir(
				document,
				DocumentDto.class);
		dto.setArxiuContingut(document.getArxiuContingut());
		// Herencia
		ExpedientTipus tipus = expedientTipusId != null? expedientTipusRepository.getById(expedientTipusId) : null;
		if (tipus != null && tipus.getExpedientTipusPare() != null) {
			if (tipus.getExpedientTipusPare().getId().equals(document.getExpedientTipus().getId()))
				dto.setHeretat(true);
			else
				dto.setSobreescriu(documentRepository.findByExpedientTipusAndCodi(
						tipus.getExpedientTipusPare().getId(), 
						document.getCodi(),
						false) != null);					
		}
		return dto;
	}
	
	@Override
	@Transactional
	public DocumentDto update(
					
			DocumentDto document,
			boolean actualitzarContingut) {
		logger.debug(
				"Modificant el document del tipus d'expedient existent (" +
				"document.id=" + document.getId() + ", " +
				"document =" + document + ", " +
				"actualitzarContingut=" + actualitzarContingut + ")");
		Document entity = documentRepository.getById(document.getId());
		entity.setCodi(document.getCodi());
		entity.setNom(document.getNom());
		entity.setDescripcio(document.getDescripcio());
		entity.setPlantilla(document.isPlantilla());
		entity.setNotificable(document.isNotificable());
		entity.setArxiuNom(document.getArxiuNom());
		if (actualitzarContingut) {
			entity.setArxiuContingut(document.getArxiuContingut());
		}
		entity.setConvertirExtensio(document.getConvertirExtensio());
		entity.setAdjuntarAuto(document.isAdjuntarAuto());
		if (document.getCampData() != null) {
			entity.setCampData(campRepository.getById(document.getCampData().getId()));
		} else {
			entity.setCampData(null);
		}
		entity.setIgnored(document.isIgnored());

		entity.setExtensionsPermeses(document.getExtensionsPermeses());
		entity.setContentType(document.getContentType());
		entity.setCustodiaCodi(document.getCustodiaCodi());
		entity.setTipusDocPortasignatures(document.getTipusDocPortasignatures());
  
				
		entity.setNtiOrigen(document.getNtiOrigen());
		entity.setNtiEstadoElaboracion(document.getNtiEstadoElaboracion());
		entity.setNtiTipoDocumental(document.getNtiTipoDocumental());
													  
   

		return conversioTipusServiceHelper.convertir(
				documentRepository.save(entity),
				DocumentDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto getArxiu(
			Long id) {
		logger.debug("obtenint contingut de l'arxiu pel document (" +
				"id=" + id + ")");
  
		Document document = documentRepository.getById(id);
		if (document == null) {
			throw new NoTrobatException(Document.class,id);
		}
  
		ArxiuDto resposta = new ArxiuDto();
  
		resposta.setNom(document.getArxiuNom());
		resposta.setContingut(document.getArxiuContingut());
  
		return resposta;
	}

	@Override
	public boolean isArxiuConvertiblePdf(String arxiuNom) {
		return PdfUtils.isArxiuConvertiblePdf(arxiuNom);
	}

    @Override
    public String getExtensionsConvertiblesPdf() {
        return PdfUtils.getExtensionsConvertiblesPdf();
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
	public List<DocumentDto> findAll(
			Long expedientTipusId,
			Long definicioProcesId) {
		logger.debug("Consultant tots els documents (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"definicioProcesId =" + definicioProcesId + ")");
						
		return conversioTipusServiceHelper.convertirList(
				expedientTipusId != null ?
						documentRepository.findByExpedientTipusId(expedientTipusId)
						: documentRepository.findByDefinicioProcesId(definicioProcesId), 
				DocumentDto.class);
	}		

	private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

}