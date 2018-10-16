/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.Firma;
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
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDetallDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaPerfilEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiEstadoElaboracionEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiOrigenEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoDocumentalEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoFirmaEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortasignaturesDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaValidacioSignaturaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDocumentService;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentStoreRepository;
import net.conselldemallorca.helium.v3.core.repository.PortasignaturesRepository;
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
	private PortasignaturesRepository portasignaturesRepository;

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



	@Override
	@Transactional
	public void create(
			Long expedientId,
			String processInstanceId,
			String documentCodi,
			Date data,
			String arxiuNom,
			byte[] arxiuContingut,
			NtiOrigenEnumDto ntiOrigen,
			NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			NtiTipoDocumentalEnumDto ntiTipoDocumental,
			String ntiIdOrigen) {
		logger.debug("Crear document a dins l'expedient (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentCodi=" + documentCodi + ", " +
				"data=" + data + ", " +
				"arxiuNom=" + arxiuNom + ", " +
				"arxiuContingut=" + arxiuContingut + ", " +
				"ntiOrigen=" + ntiOrigen + ", " +
				"ntiEstadoElaboracion=" + ntiEstadoElaboracion + ", " +
				"ntiTipoDocumental=" + ntiTipoDocumental + ", " +
				"ntiIdOrigen=" + ntiIdOrigen + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.DOC_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_DOCUMENT_AFEGIR,
				documentCodi);
		documentHelper.crearDocument(
				null,
				processInstanceId,
				documentCodi,
				data,
				false,
				null,
				arxiuNom,
				arxiuContingut,
				ntiOrigen,
				ntiEstadoElaboracion,
				ntiTipoDocumental,
				ntiIdOrigen);
		indexHelper.expedientIndexLuceneUpdate(processInstanceId);
		expedientRegistreHelper.crearRegistreCrearDocumentInstanciaProces(
				expedient.getId(),
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				documentCodi,
				arxiuNom);
	}

	@Override
	@Transactional
	public void update(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId,
			Date data,
			String arxiuNom,
			byte[] arxiuContingut,
			NtiOrigenEnumDto ntiOrigen,
			NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			NtiTipoDocumentalEnumDto ntiTipoDocumental,
			String ntiIdOrigen) {
		logger.debug("Modificar document de l'expedient (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentStoreId=" + documentStoreId + ", " +
				"data=" + data + ", " +
				"arxiuNom=" + arxiuNom + ", " +
				"arxiuContingut=" + arxiuContingut + ", " +
				"ntiOrigen=" + ntiOrigen + ", " +
				"ntiEstadoElaboracion=" + ntiEstadoElaboracion + ", " +
				"ntiTipoDocumental=" + ntiTipoDocumental + ", " +
				"ntiIdOrigen=" + ntiIdOrigen + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.DOC_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		DocumentStore documentStore = documentStoreRepository.findByIdAndProcessInstanceId(
				documentStoreId,
				processInstanceId);
		if (documentStore == null) {
			throw new NoTrobatException(
					DocumentStore.class, 
					documentStoreId);
		}
		String documentCodi = documentStore.getCodiDocument();
		String arxiuNomAntic = documentStore.getArxiuNom();
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_DOCUMENT_MODIFICAR,
				documentCodi);
		documentHelper.actualitzarDocument(
				documentStoreId,
				null,
				processInstanceId,
				data,
				null,
				arxiuNom,
				arxiuContingut,
				ntiOrigen,
				ntiEstadoElaboracion,
				ntiTipoDocumental,
				ntiIdOrigen);
		indexHelper.expedientIndexLuceneUpdate(processInstanceId);
		expedientRegistreHelper.crearRegistreModificarDocumentInstanciaProces(
				expedient.getId(),
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				documentCodi,
				arxiuNomAntic,
				arxiuNom);
	}

	@Override
	@Transactional
	public void createAdjunt(
			Long expedientId,
			String processInstanceId,
			Date data,
			String adjuntTitol,
			String arxiuNom,
			byte[] arxiuContingut,
			NtiOrigenEnumDto ntiOrigen,
			NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			NtiTipoDocumentalEnumDto ntiTipoDocumental,
			String ntiIdOrigen) {
		logger.debug("Crear document adjunt a dins l'expedient (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"data=" + data + ", " +
				"adjuntTitol=" + adjuntTitol + ", " +
				"arxiuNom=" + arxiuNom + ", " +
				"arxiuContingut=" + arxiuContingut + ", " +
				"ntiOrigen=" + ntiOrigen + ", " +
				"ntiEstadoElaboracion=" + ntiEstadoElaboracion + ", " +
				"ntiTipoDocumental=" + ntiTipoDocumental + ", " +
				"ntiIdOrigen=" + ntiIdOrigen + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.DOC_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		String documentCodi = new Long(new Date().getTime()).toString();
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_DOCUMENT_ADJUNTAR,
				documentCodi);
		documentHelper.crearDocument(
				null,
				processInstanceId,
				documentCodi,
				data,
				true,
				adjuntTitol,
				arxiuNom,
				arxiuContingut,
				ntiOrigen,
				ntiEstadoElaboracion,
				ntiTipoDocumental,
				ntiIdOrigen);
		indexHelper.expedientIndexLuceneUpdate(processInstanceId);
		expedientRegistreHelper.crearRegistreCrearDocumentInstanciaProces(
				expedient.getId(),
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				documentCodi,
				arxiuNom);
	}

	@Override
	@Transactional
	public void updateAdjunt(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId,
			Date data,
			String adjuntTitol,
			String arxiuNom,
			byte[] arxiuContingut,
			NtiOrigenEnumDto ntiOrigen,
			NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			NtiTipoDocumentalEnumDto ntiTipoDocumental,
			String ntiIdOrigen) {
		logger.debug("Modificar document adjunt de l'expedient (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentStoreId=" + documentStoreId + ", " +
				"data=" + data + ", " +
				"adjuntTitol=" + adjuntTitol + ", " +
				"arxiuNom=" + arxiuNom + ", " +
				"arxiuContingut=" + arxiuContingut + ", " +
				"ntiOrigen=" + ntiOrigen + ", " +
				"ntiEstadoElaboracion=" + ntiEstadoElaboracion + ", " +
				"ntiTipoDocumental=" + ntiTipoDocumental + ", " +
				"ntiIdOrigen=" + ntiIdOrigen + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.DOC_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		DocumentStore documentStore = documentStoreRepository.findByIdAndProcessInstanceId(
				documentStoreId,
				processInstanceId);
		if (documentStore == null) {
			throw new NoTrobatException(
					DocumentStore.class, 
					documentStoreId);
		}
		String documentCodi = documentStore.getCodiDocument();
		String arxiuNomAntic = documentStore.getArxiuNom();
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_DOCUMENT_MODIFICAR,
				documentCodi);
		documentHelper.actualitzarDocument(
				documentStoreId,
				null,
				processInstanceId,
				data,
				adjuntTitol,
				arxiuNom,
				arxiuContingut,
				ntiOrigen,
				ntiEstadoElaboracion,
				ntiTipoDocumental,
				ntiIdOrigen);
		indexHelper.expedientIndexLuceneUpdate(processInstanceId);
		expedientRegistreHelper.crearRegistreModificarDocumentInstanciaProces(
				expedient.getId(),
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				documentCodi,
				arxiuNomAntic,
				arxiuNom);
	}

	/*@Override
	@Transactional
	public void createOrUpdate(
			Long expedientId,
			String processInstanceId,
			Long documentId,
			Long documentStoreId,
			String adjuntTitol,
			String arxiuNom,
			byte[] arxiuContingut,
			Date data,
			NtiOrigenEnumDto ntiOrigen,
			NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			NtiTipoDocumentalEnumDto ntiTipoDocumental,
			String ntiIdOrigen) {
		logger.debug("Creant o modificant document de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentId=" + documentId + ", " +
				"documentStoreId=" + documentStoreId + ", " +
				"titol=" + titol + ", " +
				"arxiuNom=" + arxiuNom + ", " +
				"arxiuContingut.length=" + ((arxiuContingut != null) ? arxiuContingut.length : "<null>") + ", " +
				"data=" + data + ", " +
				"ntiOrigen=" + ntiOrigen + ", " +
				"ntiEstadoElaboracion=" + ntiEstadoElaboracion + ", " +
				"ntiTipoDocumental=" + ntiTipoDocumental + ", " +
				"ntiIdOrigen=" + ntiIdOrigen + ")");
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
			documentHelper.actualitzarAdjunt(
					documentStoreId,
					processInstanceId,
					codi,
					titol,
					data,
					arxiuNom,
					arxiuContingut,
					adjunt);
		} else {
			documentHelper.actualitzarDocumenta(
					null,
					processInstanceId,
					codi,
					data,
					arxiuNom,
					arxiuContingut,
					adjunt,
					adjuntTitol,
					ntiOrigen,
					ntiEstadoElaboracion,
					ntiTipoDocumental,
					ntiIdOrigen);			
		}
		// Registra l'acció
		if (creat) {
			expedientRegistreHelper.crearRegistreCrearDocumentInstanciaProces(
					expedient.getId(),
					processInstanceId,
					SecurityContextHolder.getContext().getAuthentication().getName(),
					codi,
					arxiuNom);
		} else {
			expedientRegistreHelper.crearRegistreModificarDocumentInstanciaProces(
					expedient.getId(),
					processInstanceId,
					SecurityContextHolder.getContext().getAuthentication().getName(),
					codi,
					arxiuNomAntic,
					arxiuNom);
		}
	}*/

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
			} else if (TipusEstat.PROCESSAT.equals(pendent.getEstat()) && Transicio.REBUTJAT.equals(pendent.getTransition())) {
				 dto.setEstat(TipusEstat.PROCESSAT.toString());
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
		Document document;
		if (expedientTipus.isAmbInfoPropia())
			document = documentRepository.findByExpedientTipusAndCodi(
					expedientTipus,
					documentCodi);
		else
			document = documentRepository.findByDefinicioProcesAndCodi(
					expedientHelper.findDefinicioProcesByProcessInstanceId(
							processInstanceId), 
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
		Document document;
		if (expedientTipus.isAmbInfoPropia())
			document = documentRepository.findByExpedientTipusAndCodi(
					expedientTipus,
					documentCodi);
		else
			document = documentRepository.findByDefinicioProcesAndCodi(
					expedientHelper.findDefinicioProcesByProcessInstanceId(
							processInstanceId), 
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
		Document document = documentHelper.findDocumentPerInstanciaProcesICodi(
				task.getProcessInstanceId(),
				documentCodi);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(
				task.getProcessInstanceId());
		Date documentData = new Date();
		ArxiuDto arxiu = documentHelper.generarDocumentAmbPlantillaIConvertir(
				expedient,
				document,
				taskInstanceId,
				task.getProcessInstanceId(),
				documentData);
		if (document.isAdjuntarAuto()) {
			documentHelper.crearDocument(
					taskInstanceId,
					task.getProcessInstanceId(),
					document.getCodi(),
					documentData,
					false,
					null,
					arxiu.getNom(),
					arxiu.getContingut(),
					null,
					null,
					null,
					null);
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
			Long documentId,
			String arxiuNom) throws NoTrobatException, PermisDenegatException {
		logger.debug("Verificant extensions permeses per document a la tasca (" +
				"tascaId=" + tascaId + ", " +
				"documentId=" + documentId + ", " +
				"arxiuNom=" + arxiuNom + ")");
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				tascaId,
				true,
				true);
		DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
				task.getProcessInstanceId());
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		ExpedientTipus expedientTipus = expedient.getTipus();
		Document document = documentRepository.findOne(documentId);
		if ((document.getExpedientTipus() == null || document.getExpedientTipus().getId() != expedientTipus.getId()) && 
			(document.getDefinicioProces() == null || document.getDefinicioProces().getId() != definicioProces.getId()))
			throw new NoTrobatException(Document.class);
		return document.isExtensioPermesa(
				getExtensio(arxiuNom));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<RespostaValidacioSignaturaDto> verificarSignatura(Long documentStoreId) {
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		if (documentStore == null) {
			throw new NoTrobatException(DocumentStore.class, documentStoreId);
		}
		return documentHelper.getRespostasValidacioSignatura(documentStore);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Object findPortasignaturesInfo(Long expedientId, String processInstanceId, Long documentStoreId) {
		expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		Portasignatures portasignatures = portasignaturesRepository.findByProcessInstanceIdAndDocumentStoreId(processInstanceId, documentStoreId);
		SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		JSONObject resposta = new JSONObject();
		resposta.put("id", portasignatures.getId());
		resposta.put("documentId", portasignatures.getDocumentId());
		resposta.put("tokenId", portasignatures.getTokenId());
		resposta.put("dataEnviat", dt.format(portasignatures.getDataEnviat()));
		resposta.put("estat", portasignatures.getEstat());
		resposta.put("transicio", portasignatures.getTransition().toString());
		resposta.put("documentStoreId", portasignatures.getDocumentStoreId());
		resposta.put("motiuRebuig", portasignatures.getMotiuRebuig());
		resposta.put("transicioOK", portasignatures.getTransicioOK());
		resposta.put("transicioKO", portasignatures.getTransicioKO());
		resposta.put("dataProcessamentPrimer", dt.format(portasignatures.getDataProcessamentPrimer()));
		resposta.put("dataProcessamentDarrer", dt.format(portasignatures.getDataProcessamentDarrer()));
		resposta.put("dataSignatRebutjat", dt.format(portasignatures.getDataSignatRebutjat()));
		resposta.put("dataCustodiaIntent", dt.format(portasignatures.getDataCustodiaIntent()));
		resposta.put("dataCustodiaOk", dt.format(portasignatures.getDataCustodiaOk()));
		resposta.put("dataSignalIntent", dt.format(portasignatures.getDataSignalIntent()));
		resposta.put("dataSignalOk", dt.format(portasignatures.getDataSignalOk()));
		resposta.put("processInstanceId", portasignatures.getProcessInstanceId());
		resposta.put("errorProcessant", portasignatures.getErrorCallbackProcessant());
		if (TipusEstat.ERROR.equals(portasignatures.getEstat())) {
			resposta.put("error", true);
		} else {
			resposta.put("error", false);
		}
		if (TipusEstat.PENDENT.equals(portasignatures.getEstat())) { 
			resposta.put("pendent", true);
		} else {
			resposta.put("pendent", false);
		}
		return resposta;
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto findArxiuAmbTokenPerMostrar(String token) {
		Long documentStoreId = documentHelper.getDocumentStoreIdPerToken(token);
		DocumentStore document = documentStoreRepository.findOne(documentStoreId);
		if (document == null) {
			return null;
		}
		DocumentDto dto = null;
		if (document.isSignat() || document.isRegistrat()) {
			dto = documentHelper.toDocumentDto(
					documentStoreId,
					false,
					false,
					true,
					false,
					false);
		} else {
			dto = documentHelper.toDocumentDto(
					documentStoreId,
					true,
					false,
					false,
					false,
					false);
		}
		if (dto == null) {
			return null;
		}
		return new ArxiuDto(dto.getVistaNom(), dto.getVistaContingut());
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto findArxiuAmbTokenPerSignar(String token) {
		Long documentStoreId = documentHelper.getDocumentStoreIdPerToken(token);
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		DocumentDto dto = documentHelper.toDocumentDto(
				documentStoreId,
				false,
				false,
				true,
				true,
				(documentStore == null || documentStore.getArxiuUuid() == null));
		if (dto == null) {
			return null;
		}
		return new ArxiuDto(dto.getVistaNom(), dto.getVistaContingut());
	}

	@Override
	@Transactional(readOnly = true)
	public DocumentDto findDocumentAmbId(Long documentStoreId) {
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		DocumentDto dto = documentHelper.toDocumentDto(
				documentStoreId,
				false,
				false,
				true,
				true,
				(documentStore == null || documentStore.getArxiuUuid() == null));
		if (dto == null) {
			throw new NoTrobatException(DocumentDto.class, documentStoreId);
		}
		return dto;
	}

	@Transactional
	@Override
	public ArxiuDetallDto getArxiuDetall(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId) {
		logger.debug("Obtenint informació de l'arxiu pel document (" +
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
		DocumentStore documentStore = documentStoreRepository.findByIdAndProcessInstanceId(
				documentStoreId,
				processInstanceId);
		if (documentStore == null) {
			throw new NoTrobatException(
					DocumentStore.class, 
					documentStoreId);
		}
		ArxiuDetallDto arxiuDetall = null;
		if (expedient.isArxiuActiu()) {
			arxiuDetall = new ArxiuDetallDto();
			es.caib.plugins.arxiu.api.Document arxiuDocument = pluginHelper.arxiuDocumentInfo(
					documentStore.getArxiuUuid(),
					null,
					false,
					true);
			documentHelper.actualitzarNtiFirma(documentStore, arxiuDocument);
			arxiuDetall.setIdentificador(arxiuDocument.getIdentificador());
			arxiuDetall.setNom(arxiuDocument.getNom());
			List<Firma> firmes = arxiuDocument.getFirmes();
			DocumentMetadades metadades = arxiuDocument.getMetadades();
			if (metadades != null) {
				arxiuDetall.setEniVersio(metadades.getVersioNti());
				arxiuDetall.setEniIdentificador(metadades.getIdentificador());
				arxiuDetall.setEniDataCaptura(metadades.getDataCaptura());
				if (metadades.getOrigen() != null) {
					switch (metadades.getOrigen()) {
					case CIUTADA:
						arxiuDetall.setEniOrigen(NtiOrigenEnumDto.CIUTADA);
						break;
					case ADMINISTRACIO:
						arxiuDetall.setEniOrigen(NtiOrigenEnumDto.ADMINISTRACIO);
						break;
					}
				}
				if (metadades.getEstatElaboracio() != null) {
					switch (metadades.getEstatElaboracio()) {
					case ORIGINAL:
						arxiuDetall.setEniEstatElaboracio(NtiEstadoElaboracionEnumDto.ORIGINAL);
						break;
					case COPIA_CF:
						arxiuDetall.setEniEstatElaboracio(NtiEstadoElaboracionEnumDto.COPIA_CF);
						break;
					case COPIA_DP:
						arxiuDetall.setEniEstatElaboracio(NtiEstadoElaboracionEnumDto.COPIA_DP);
						break;
					case COPIA_PR:
						arxiuDetall.setEniEstatElaboracio(NtiEstadoElaboracionEnumDto.COPIA_PR);
						break;
					case ALTRES:
						arxiuDetall.setEniEstatElaboracio(NtiEstadoElaboracionEnumDto.ALTRES);
						break;
					}
				}
				if (metadades.getTipusDocumental() != null) {
					switch (metadades.getTipusDocumental()) {
					case RESOLUCIO:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.RESOLUCIO);
						break;
					case ACORD:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.ACORD);
						break;
					case CONTRACTE:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.CONTRACTE);
						break;
					case CONVENI:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.CONVENI);
						break;
					case DECLARACIO:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.DECLARACIO);
						break;
					case COMUNICACIO:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.COMUNICACIO);
						break;
					case NOTIFICACIO:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.NOTIFICACIO);
						break;
					case PUBLICACIO:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.PUBLICACIO);
						break;
					case JUSTIFICANT_RECEPCIO:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.JUSTIFICANT_RECEPCIO);
						break;
					case ACTA:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.ACTA);
						break;
					case CERTIFICAT:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.CERTIFICAT);
						break;
					case DILIGENCIA:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.DILIGENCIA);
						break;
					case INFORME:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.INFORME);
						break;
					case SOLICITUD:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.SOLICITUD);
						break;
					case DENUNCIA:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.DENUNCIA);
						break;
					case ALEGACIO:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.ALEGACIO);
						break;
					case RECURS:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.RECURS);
						break;
					case COMUNICACIO_CIUTADA:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.COMUNICACIO_CIUTADA);
						break;
					case FACTURA:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.FACTURA);
						break;
					case ALTRES_INCAUTATS:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.ALTRES_INCAUTATS);
						break;
					case ALTRES:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.ALTRES);
						break;
					}
				}
				arxiuDetall.setEniOrgans(metadades.getOrgans());
				if (metadades.getFormat() != null) {
					arxiuDetall.setEniFormat(metadades.getFormat().toString());
				}
				if (metadades.getExtensio() != null) {
					arxiuDetall.setEniExtensio(metadades.getExtensio().toString());
				}
				arxiuDetall.setEniDocumentOrigenId(metadades.getIdentificadorOrigen());
				arxiuDetall.setMetadadesAddicionals(metadades.getMetadadesAddicionals());
				if (arxiuDocument.getContingut() != null) {
					arxiuDetall.setContingutArxiuNom(
							arxiuDocument.getContingut().getArxiuNom());
					arxiuDetall.setContingutTipusMime(
							arxiuDocument.getContingut().getTipusMime());
				}
			}
			if (firmes != null) {
				List<ArxiuFirmaDto> dtos = new ArrayList<ArxiuFirmaDto>();
				for (Firma firma: firmes) {
					ArxiuFirmaDto dto = new ArxiuFirmaDto();
					if (firma.getTipus() != null) {
						switch (firma.getTipus()) {
						case CSV:
							dto.setTipus(NtiTipoFirmaEnumDto.CSV);
							break;
						case XADES_DET:
							dto.setTipus(NtiTipoFirmaEnumDto.XADES_DET);
							break;
						case XADES_ENV:
							dto.setTipus(NtiTipoFirmaEnumDto.XADES_ENV);
							break;
						case CADES_DET:
							dto.setTipus(NtiTipoFirmaEnumDto.CADES_DET);
							break;
						case CADES_ATT:
							dto.setTipus(NtiTipoFirmaEnumDto.CADES_ATT);
							break;
						case PADES:
							dto.setTipus(NtiTipoFirmaEnumDto.PADES);
							break;
						case SMIME:
							dto.setTipus(NtiTipoFirmaEnumDto.SMIME);
							break;
						case ODT:
							dto.setTipus(NtiTipoFirmaEnumDto.ODT);
							break;
						case OOXML:
							dto.setTipus(NtiTipoFirmaEnumDto.OOXML);
							break;
						}
					}
					if (firma.getPerfil() != null) {
						switch (firma.getPerfil()) {
						case BES:
							dto.setPerfil(ArxiuFirmaPerfilEnumDto.BES);
							break;
						case EPES:
							dto.setPerfil(ArxiuFirmaPerfilEnumDto.EPES);
							break;
						case LTV:
							dto.setPerfil(ArxiuFirmaPerfilEnumDto.LTV);
							break;
						case T:
							dto.setPerfil(ArxiuFirmaPerfilEnumDto.T);
							break;
						case C:
							dto.setPerfil(ArxiuFirmaPerfilEnumDto.C);
							break;
						case X:
							dto.setPerfil(ArxiuFirmaPerfilEnumDto.X);
							break;
						case XL:
							dto.setPerfil(ArxiuFirmaPerfilEnumDto.XL);
							break;
						case A:
							dto.setPerfil(ArxiuFirmaPerfilEnumDto.A);
							break;
						}
					}
					dto.setFitxerNom(firma.getFitxerNom());
					if (NtiTipoFirmaEnumDto.CSV.equals(dto.getTipus())) {
						dto.setContingut(firma.getContingut());
					}
					dto.setTipusMime(firma.getTipusMime());
					dto.setCsvRegulacio(firma.getCsvRegulacio());
					dtos.add(dto);
				}
				arxiuDetall.setFirmes(dtos);
			}
		}
		return arxiuDetall;
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
