/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.ExpedientLoggerHelper;
import net.conselldemallorca.helium.core.helper.ExpedientRegistreHelper;
import net.conselldemallorca.helium.core.helper.IndexHelper;
import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.core.helper.TascaHelper;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.TipusEstat;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.Transicio;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortasignaturesDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDocumentService;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentStoreRepository;
import net.conselldemallorca.helium.v3.core.repository.RegistreRepository;


/**
 * Implementació dels mètodes del servei ExpedientDocumentService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExpedientDocumentServiceImpl implements ExpedientDocumentService {

	@Resource
	private CampRepository campRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private RegistreRepository registreRepository;
	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private DocumentStoreRepository documentStoreRepository;

	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource(name = "documentHelperV3")
	private DocumentHelperV3 documentHelper;
	@Resource
	private TascaHelper tascaHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private IndexHelper indexHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private ExpedientLoggerHelper expedientLoggerHelper;
	@Resource
	private ExpedientRegistreHelper expedientRegistreHelper;



	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void createOrUpdate(
			Long expedientId,
			String processInstanceId,
			Long documentId,
			Long documentStoreId,
			String titol,
			String arxiuNom,
			byte[] arxiuContingut,
			Date data) {
		logger.debug("Creant o modificant document de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentId=" + documentId + ", " +
				"documentStoreId=" + documentStoreId + ", " +
				"titol=" + titol + ", " +
				"arxiuNom=" + arxiuNom + ", " +
				"arxiuContingut.length=" + ((arxiuContingut != null) ? arxiuContingut.length : "<null>") + ", " +
				"data=" + data + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.DOC_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		boolean creat = false;
		String arxiuNomAntic = null;
		boolean adjunt = false;
		DocumentStore documentStore = null;
		if (documentStoreId == null) {
			creat = true;
			adjunt = true;
		} else {
			documentStore = documentStoreRepository.findById(documentStoreId);
			if (documentStore == null)
				throw new NoTrobatException(DocumentStore.class, documentStoreId);
			
			arxiuNomAntic = documentStore.getArxiuNom();
			adjunt = documentStore.isAdjunt();
		}
		DocumentDto document = null;
		String codi = null;
		if (documentId != null) {
			document = getDocumentDtoPerDocumentId(documentId);
			if (document == null) {
				document = documentHelper.getDocumentSenseContingut(documentStoreId);
			}		
			if (document != null) {
				adjunt = document.isAdjunt();
				codi = document.getCodi();
			}
		}
		if (codi == null && (document == null || document.isAdjunt())) { 
			codi = new Long(new Date().getTime()).toString();
		}
		if (arxiuContingut == null && document != null) {
			arxiuContingut = document.getArxiuContingut();
			arxiuNom = document.getArxiuNom();
		} else if (arxiuContingut == null && documentStore != null) {
			arxiuContingut = documentStore.getArxiuContingut();
			arxiuNom = documentStore.getArxiuNom();
		}
		if (document != null && document.isAdjunt()) {
			expedientLoggerHelper.afegirLogExpedientPerProces(
					processInstanceId,
					ExpedientLogAccioTipus.PROCES_DOCUMENT_ADJUNTAR,
					codi);			
		} else if (creat) {
				expedientLoggerHelper.afegirLogExpedientPerProces(
						processInstanceId,
						ExpedientLogAccioTipus.PROCES_DOCUMENT_AFEGIR,
						codi);
		} else {
			expedientLoggerHelper.afegirLogExpedientPerProces(
					processInstanceId,
					ExpedientLogAccioTipus.PROCES_DOCUMENT_MODIFICAR,
					codi);
		}
		if (documentStoreId != null && adjunt) {
			documentStoreId = documentHelper.actualitzarAdjunt(
					documentStoreId,
					processInstanceId,
					codi,
					titol,
					data,
					arxiuNom,
					arxiuContingut,
					adjunt);
		} else {
			documentStoreId = documentHelper.actualitzarDocument(
					null,
					processInstanceId,
					codi,
					titol,
					data,
					arxiuNom,
					arxiuContingut,
					adjunt);
		}
		String user = SecurityContextHolder.getContext().getAuthentication().getName();
		// Registra l'acció
		if (creat) {
			expedientRegistreHelper.crearRegistreCrearDocumentInstanciaProces(
					expedient.getId(),
					processInstanceId,
					user,
					codi,
					arxiuNom);
		} else {
			expedientRegistreHelper.crearRegistreModificarDocumentInstanciaProces(
					expedient.getId(),
					processInstanceId,
					user,
					codi,
					arxiuNomAntic,
					arxiuNom);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void delete(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId) {
		logger.debug("Esborra un document de l'instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentStoreId=" + documentStoreId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.DOC_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		ExpedientDocumentDto document = documentHelper.findOnePerInstanciaProces(
				processInstanceId,
				documentStoreId);
		if (processInstanceId == null) {
			documentHelper.esborrarDocument(
					null,
					expedient.getProcessInstanceId(),
					documentStoreId);
		} else {
			documentHelper.esborrarDocument(
					null,
					processInstanceId,
					documentStoreId);
		}
		String user = SecurityContextHolder.getContext().getAuthentication().getName();
		if (!document.isAdjunt()) {
			expedientRegistreHelper.crearRegistreEsborrarDocumentInstanciaProces(
					expedient.getId(),
					processInstanceId,
					user,
					document.getDocumentCodi());
		} else {
			expedientRegistreHelper.crearRegistreEsborrarDocumentInstanciaProces(
					expedient.getId(),
					processInstanceId,
					user,
					document.getAdjuntTitol());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ExpedientDocumentDto> findAmbInstanciaProces(
			Long expedientId,
			String processInstanceId) {
		logger.debug("Consulta els documents de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		if (processInstanceId == null) {
			return documentHelper.findDocumentsPerInstanciaProces(
					expedient.getProcessInstanceId());
		} else {
			expedientHelper.comprovarInstanciaProces(
					expedient,
					processInstanceId);
			return documentHelper.findDocumentsPerInstanciaProces(
					processInstanceId);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientDocumentDto findOneAmbInstanciaProces(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId) {
		logger.debug("Consulta un document de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentStoreId=" + documentStoreId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		return documentHelper.findOnePerInstanciaProces(
				processInstanceId,
				documentStoreId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientDocumentDto findOneAmbInstanciaProces(
			Long expedientId,
			String processInstanceId,
			String documentCodi) {
		logger.debug("Consulta un document de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentCodi=" + documentCodi + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		return documentHelper.findOnePerInstanciaProces(
				processInstanceId,
				documentCodi);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ArxiuDto arxiuFindAmbDocument(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId) {
		logger.debug("Consulta de l'arxiu del document de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentStoreId=" + documentStoreId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		if (documentStore == null) {
			throw new NoTrobatException(
					DocumentStore.class,
					documentStoreId);
		}
		expedientHelper.comprovarInstanciaProces(
				expedient,
				documentStore.getProcessInstanceId());
		return documentHelper.getArxiuPerDocumentStoreId(
				documentStoreId,
				false,
				false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PortasignaturesDto> portasignaturesFindPendents(
			Long expedientId,
			String processInstanceId) {
		logger.debug("Consulta dels documents pendents del portafirmes (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ")");
		List<PortasignaturesDto> resposta = new ArrayList<PortasignaturesDto>();
		List<Portasignatures> pendents = pluginHelper.findPendentsPortasignaturesPerProcessInstanceId(processInstanceId);
		for (Portasignatures pendent: pendents) {
			PortasignaturesDto dto = new PortasignaturesDto();
			dto.setId(pendent.getId());
			dto.setDocumentId(pendent.getDocumentId());
			dto.setTokenId(pendent.getTokenId());
			dto.setDataEnviat(pendent.getDataEnviat());
			if (TipusEstat.ERROR.equals(pendent.getEstat())) {
				if (Transicio.SIGNAT.equals(pendent.getTransition()))
					dto.setEstat(TipusEstat.SIGNAT.toString());
				else
					dto.setEstat(TipusEstat.REBUTJAT.toString());
				dto.setError(true);
			} else {
				dto.setEstat(pendent.getEstat().toString());
				dto.setError(false);
			}
			if (pendent.getTransition() != null)
				dto.setTransicio(pendent.getTransition().toString());
			dto.setDocumentStoreId(pendent.getDocumentStoreId());
			dto.setMotiuRebuig(pendent.getMotiuRebuig());
			dto.setTransicioOK(pendent.getTransicioOK());
			dto.setTransicioKO(pendent.getTransicioKO());
			dto.setDataProcessamentPrimer(pendent.getDataProcessamentPrimer());
			dto.setDataProcessamentDarrer(pendent.getDataProcessamentDarrer());
			dto.setDataSignatRebutjat(pendent.getDataSignatRebutjat());
			dto.setDataCustodiaIntent(pendent.getDataCustodiaIntent());
			dto.setDataCustodiaOk(pendent.getDataCustodiaOk());
			dto.setDataSignalIntent(pendent.getDataSignalIntent());
			dto.setDataSignalOk(pendent.getDataSignalOk());
			dto.setErrorProcessant(pendent.getErrorCallbackProcessant());
			dto.setProcessInstanceId(pendent.getProcessInstanceId());
			resposta.add(dto);
		}
		return resposta;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ArxiuDto generarAmbPlantilla(
			Long expedientId,
			String processInstanceId,
			String documentCodi) {
		logger.debug("Generant document del procés amb plantilla (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentCodi=" + documentCodi + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		ExpedientTipus expedientTipus = expedient.getTipus();
		Document document = documentRepository.findByDefinicioProcesOrExpedientTipusAndCodi(
				expedientHelper.findDefinicioProcesByProcessInstanceId(
						processInstanceId),
				expedientTipus,
				documentCodi);
		Date documentData = new Date();
		return documentHelper.generarDocumentAmbPlantillaIConvertir(
				expedient,
				document,
				null,
				(processInstanceId != null) ? processInstanceId : expedient.getProcessInstanceId(),
				documentData);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean isExtensioPermesa(
			Long expedientId,
			String processInstanceId,
			String documentCodi,
			String arxiuNom) throws NoTrobatException, PermisDenegatException {
		logger.debug("Verificant extensions permeses per document (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentCodi=" + documentCodi + ", " +
				"arxiuNom=" + arxiuNom + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		ExpedientTipus expedientTipus = expedient.getTipus();
		Document document = documentRepository.findByDefinicioProcesOrExpedientTipusAndCodi(
				expedientHelper.findDefinicioProcesByProcessInstanceId(
						processInstanceId),
				expedientTipus,
				documentCodi);
		return document.isExtensioPermesa(
				getExtensio(arxiuNom));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ArxiuDto generarAmbPlantillaPerTasca(
			String taskInstanceId,
			String documentCodi) {
		logger.debug("Generant document de la tasca amb plantilla (" +
				"taskInstanceId=" + taskInstanceId + ", " +
				"documentCodi=" + documentCodi + ")");
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				taskInstanceId,
				true,
				true);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(
				task.getProcessInstanceId());
		ExpedientTipus expedientTipus = expedient.getTipus();
		Document document = documentRepository.findByDefinicioProcesOrExpedientTipusAndCodi(
				expedientHelper.findDefinicioProcesByProcessInstanceId(
						task.getProcessInstanceId()),
				expedientTipus,
				documentCodi);
		Date documentData = new Date();
		ArxiuDto arxiu = documentHelper.generarDocumentAmbPlantillaIConvertir(
				expedient,
				document,
				taskInstanceId,
				task.getProcessInstanceId(),
				documentData);
		if (document.isAdjuntarAuto()) {
			documentHelper.actualitzarDocument(
					taskInstanceId,
					task.getProcessInstanceId(),
					document.getCodi(),
					null,
					documentData,
					arxiu.getNom(),
					arxiu.getContingut(),
					false);
			return null;
		} else {
			return arxiu;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean isExtensioPermesaPerTasca(
			String tascaId,
			String documentCodi,
			String arxiuNom) throws NoTrobatException, PermisDenegatException {
		logger.debug("Verificant extensions permeses per document a la tasca (" +
				"tascaId=" + tascaId + ", " +
				"documentCodi=" + documentCodi + ", " +
				"arxiuNom=" + arxiuNom + ")");
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				tascaId,
				true,
				true);
		DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
				task.getProcessInstanceId());
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		ExpedientTipus expedientTipus = expedient.getTipus();
		Document document = documentRepository.findByDefinicioProcesOrExpedientTipusAndCodi(
				definicioProces,
				expedientTipus,
				documentCodi);
		return document.isExtensioPermesa(
				getExtensio(arxiuNom));
	}


	/*@Override
	@Transactional(readOnly = true)
	public ExpedientDocumentDto findDocumentPerDocumentStoreId(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId) {
		logger.debug("Consulta un document de l'instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentStoreId=" + documentStoreId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		
		if (processInstanceId == null) {
			return documentHelper.findDocumentPerDocumentStoreId(
					expedient.getProcessInstanceId(),
					documentStoreId);
		} else {
			return documentHelper.findDocumentPerDocumentStoreId(
					processInstanceId,
					documentStoreId);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentDto> findListDocumentsPerDefinicioProces(
			Long definicioProcesId,
			String processInstanceId,
			String expedientTipusNom) {
		List<Document> documents = documentRepository.findAmbDefinicioProces(definicioProcesId);
		return conversioTipusHelper.convertirList(
				documents,
				DocumentDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public DocumentDto findDocumentsPerId(Long id) {
		Document document = documentRepository.findOne(id);
		if (document == null)
			throw new NoTrobatException(Document.class, id);
		
		return conversioTipusHelper.convertir(
				document,
				DocumentDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto getArxiuPerDocument(
			Long id,
			Long documentStoreId) {
		logger.debug("btenint contingut de l'arxiu per l'expedient (" +
				"id=" + id + ", " +
				"documentStoreId=" + documentStoreId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		if (documentStore == null) {
			throw new NoTrobatException(DocumentStore.class,documentStoreId);
		}
		expedientHelper.comprovarInstanciaProces(
				expedient,
				documentStore.getProcessInstanceId());
		return documentHelper.getArxiuPerDocumentStoreId(
				documentStoreId,
				false,
				false);
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto arxiuDocumentPerMostrar(String token) {
		Long documentStoreId = documentHelper.getDocumentStoreIdPerToken(token);
		DocumentStore document = documentStoreRepository.findById(documentStoreId);
		if (document == null)
			return null;
		DocumentDto dto = null;
		if (document.isSignat() || document.isRegistrat()) {
			dto = documentHelper.getDocumentVista(documentStoreId, false, false);
			if (dto == null)
				return null;
			return new ArxiuDto(dto.getVistaNom(), dto.getVistaContingut());
		} else {
			dto = documentHelper.getDocumentOriginal(documentStoreId, true);
			if (dto == null)
				return null;
			return new ArxiuDto(dto.getArxiuNom(), dto.getArxiuContingut());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto arxiuDocumentPerSignar(String token) {
		Long documentStoreId = documentHelper.getDocumentStoreIdPerToken(token);
		DocumentDto dto = documentHelper.getDocumentVista(documentStoreId, true, true);
		if (dto == null)
			return null;
		return new ArxiuDto(dto.getVistaNom(), dto.getVistaContingut());
	}


	@Override
	public boolean isExtensioDocumentPermesa(String nomArxiu) {
		return (new Document()).isExtensioPermesa(getExtension(nomArxiu));
	}

	


	@Override
	@Transactional(readOnly = true)
	public Long findDocumentStorePerInstanciaProcesAndDocumentCodi(String processInstanceId, String documentCodi) {
		return documentHelper.findDocumentStorePerInstanciaProcesAndDocumentCodi(processInstanceId, documentCodi);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PortasignaturesDto> findDocumentsPendentsPortasignatures(String processInstanceId) {
		List<PortasignaturesDto> resposta = new ArrayList<PortasignaturesDto>();
		List<Portasignatures> pendents = pluginHelper.findPendentsPortasignaturesPerProcessInstanceId(processInstanceId);
		for (Portasignatures pendent: pendents) {
			PortasignaturesDto dto = new PortasignaturesDto();
			dto.setId(pendent.getId());
			dto.setDocumentId(pendent.getDocumentId());
			dto.setTokenId(pendent.getTokenId());
			dto.setDataEnviat(pendent.getDataEnviat());
			if (TipusEstat.ERROR.equals(pendent.getEstat())) {
				if (Transicio.SIGNAT.equals(pendent.getTransition()))
					dto.setEstat(TipusEstat.SIGNAT.toString());
				else
					dto.setEstat(TipusEstat.REBUTJAT.toString());
				dto.setError(true);
			} else {
				dto.setEstat(pendent.getEstat().toString());
				dto.setError(false);
			}
			if (pendent.getTransition() != null)
				dto.setTransicio(pendent.getTransition().toString());
			dto.setDocumentStoreId(pendent.getDocumentStoreId());
			dto.setMotiuRebuig(pendent.getMotiuRebuig());
			dto.setTransicioOK(pendent.getTransicioOK());
			dto.setTransicioKO(pendent.getTransicioKO());
			dto.setDataProcessamentPrimer(pendent.getDataProcessamentPrimer());
			dto.setDataProcessamentDarrer(pendent.getDataProcessamentDarrer());
			dto.setDataSignatRebutjat(pendent.getDataSignatRebutjat());
			dto.setDataCustodiaIntent(pendent.getDataCustodiaIntent());
			dto.setDataCustodiaOk(pendent.getDataCustodiaOk());
			dto.setDataSignalIntent(pendent.getDataSignalIntent());
			dto.setDataSignalOk(pendent.getDataSignalOk());
			dto.setErrorProcessant(pendent.getErrorCallbackProcessant());
			dto.setProcessInstanceId(pendent.getProcessInstanceId());
			resposta.add(dto);
		}
		return resposta;
	}*/



	private DocumentDto getDocumentDtoPerDocumentId(Long documentId) {
		Document document = documentRepository.findOne(documentId);
		if (document == null)
			throw new NoTrobatException(
					Document.class,
					documentId);
		return conversioTipusHelper.convertir(
				document,
				DocumentDto.class);
	}
	private String getExtensio(String arxiuNom) {
		int index = arxiuNom.lastIndexOf('.');
		if (index == -1) {
			return "";
		} else {
			return arxiuNom.substring(index + 1);
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientDocumentServiceImpl.class);


}
