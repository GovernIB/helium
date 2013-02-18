/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.Date;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.model.service.ExpedientLogHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessDefinition;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.exception.DefinicioProcesNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.DocumentGenerarException;
import net.conselldemallorca.helium.v3.core.api.exception.DocumentNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TaskInstanceNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.DocumentService;
import net.conselldemallorca.helium.v3.core.helper.ConversioDocumentHelper;
import net.conselldemallorca.helium.v3.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.v3.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.v3.core.helper.PlantillaHelper;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentStoreRepository;

import org.springframework.stereotype.Service;

/**
 * Servei per gestionar els documents dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("documentServiceV3")
public class DocumentServiceImpl implements DocumentService {

	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private DocumentStoreRepository documentStoreRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;

	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private DocumentHelperV3 documentHelperV3;
	@Resource
	private PlantillaHelper plantillaHelper;
	@Resource
	private ExpedientLogHelper expedientLogHelper;
	@Resource
	private ConversioDocumentHelper conversioDocumentHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;



	public DocumentDto getInfo(Long documentId) {
		net.conselldemallorca.helium.core.model.dto.DocumentDto document = getDocumentInfo(
				documentId);
		return conversioTipusHelper.convertir(
				document,
				DocumentDto.class);
	}

	public ArxiuDto getArxiuPerMostrar(Long documentId) {
		net.conselldemallorca.helium.core.model.dto.DocumentDto document = getDocumentInfo(documentId);
		if (document == null)
			return null;
		if (document.isSignat() || document.isRegistrat()) {
			document = documentHelperV3.getDocumentVista(documentId, false, false);
		} else {
			document = documentHelperV3.getDocumentOriginal(documentId, true);
		}
		if (document == null)
			return null;
		return new ArxiuDto(
				document.getArxiuNom(),
				document.getArxiuContingut());
	}

	public Long guardarDocument(
			String processInstanceId,
			String documentCodi,
			Date documentData,
			String arxiuNom,
			byte[] arxiuContingut) {
		return guardarDocumentProces(
				processInstanceId,
				documentCodi,
				documentData,
				arxiuNom,
				arxiuContingut,
				false,
				null);
	}

	public Long guardarAdjunt(
			String processInstanceId,
			String adjuntId,
			String adjuntTitol,
			Date adjuntData,
			String arxiuNom,
			byte[] arxiuContingut) {
		String documentCodi = (adjuntId == null) ? new Long(new Date().getTime()).toString() : adjuntId;
		return guardarDocumentProces(
				processInstanceId,
				documentCodi,
				adjuntData,
				arxiuNom,
				arxiuContingut,
				true,
				adjuntTitol);
	}

	public void esborrarDocument(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi) {
		String piid = processInstanceId;
		if (piid == null && taskInstanceId != null) {
			JbpmTask task = jbpmHelper.getTaskById(taskInstanceId);
			piid = task.getProcessInstanceId();
		}
		if (taskInstanceId != null) {
			expedientLogHelper.afegirLogExpedientPerTasca(
					taskInstanceId,
					ExpedientLogAccioTipus.TASCA_DOCUMENT_ESBORRAR,
					documentCodi);
		} else {
			expedientLogHelper.afegirLogExpedientPerProces(
					piid,
					ExpedientLogAccioTipus.PROCES_DOCUMENT_ESBORRAR,
					documentCodi);
		}
		documentHelperV3.esborrarDocument(
				taskInstanceId,
				piid,
				documentCodi);
	}

	public void guardarDadesRegistre(
			Long documentStoreId,
			String registreNumero,
			Date registreData,
			String registreOficinaCodi,
			String registreOficinaNom,
			boolean registreEntrada) {
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		documentStore.setRegistreNumero(registreNumero);
		documentStore.setRegistreData(registreData);
		documentStore.setRegistreOficinaCodi(registreOficinaCodi);
		documentStore.setRegistreOficinaNom(registreOficinaNom);
		documentStore.setRegistreEntrada(registreEntrada);
	}

	public ArxiuDto generarAmbPlantilla(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi,
			Date dataDocument,
			boolean forsarAdjuntarAuto) throws DefinicioProcesNotFoundException, DocumentNotFoundException, DocumentGenerarException, TaskInstanceNotFoundException {
		JbpmProcessDefinition jpd = jbpmHelper.findProcessDefinitionWithProcessInstanceId(processInstanceId);
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(jpd.getId());
		if (definicioProces == null)
			throw new DefinicioProcesNotFoundException();
		Document document = documentRepository.findByDefinicioProcesAndCodi(
				definicioProces,
				documentCodi);
		if (document == null)
			throw new DocumentNotFoundException();
		JbpmTask task = jbpmHelper.getTaskById(taskInstanceId);
		if (task == null)
			throw new TaskInstanceNotFoundException();
		try {
			ArxiuDto arxiuGenerat = plantillaHelper.generarDocumentPlantilla(
					definicioProces.getEntorn(),
					document.getId(),
					null,
					processInstanceId,
					dataDocument);
			conversioDocumentHelper.convertirArxiuPerVista(
					document,
					arxiuGenerat);
			if (forsarAdjuntarAuto || document.isAdjuntarAuto()) {
				documentHelperV3.actualitzarDocument(
						taskInstanceId,
						(processInstanceId != null) ? processInstanceId : task.getProcessInstanceId(),
						document.getCodi(),
						null,
						dataDocument,
						arxiuGenerat.getNom(),
						arxiuGenerat.getContingut(),
						false);
			}
			return arxiuGenerat;
		} catch (Exception ex) {
			throw new DocumentGenerarException(ex);
		}
	}



	private net.conselldemallorca.helium.core.model.dto.DocumentDto getDocumentInfo(
			Long documentId) {
		return documentHelperV3.getDocumentOriginal(documentId, false);
	}

	private Long guardarDocumentProces(
			String processInstanceId,
			String documentCodi,
			Date documentData,
			String arxiuNom,
			byte[] arxiuContingut,
			boolean adjunt,
			String adjuntTitol) {
		DocumentStore documentStore = documentHelperV3.getDocumentStore(
				null,
				processInstanceId,
				documentCodi);
		boolean creat = (documentStore == null);
		if (!adjunt) {
			if (creat) {
				expedientLogHelper.afegirLogExpedientPerProces(
						processInstanceId,
						ExpedientLogAccioTipus.PROCES_DOCUMENT_AFEGIR,
						documentCodi);
			} else {
				expedientLogHelper.afegirLogExpedientPerProces(
						processInstanceId,
						ExpedientLogAccioTipus.PROCES_DOCUMENT_MODIFICAR,
						documentCodi);
			}
		} else {
			expedientLogHelper.afegirLogExpedientPerProces(
					processInstanceId,
					ExpedientLogAccioTipus.PROCES_DOCUMENT_ADJUNTAR,
					adjuntTitol);
		}
		Long documentStoreId = documentHelperV3.actualitzarDocument(
				null,
				processInstanceId,
				documentCodi,
				adjuntTitol,
				documentData,
				arxiuNom,
				arxiuContingut,
				adjunt);
		return documentStoreId;
	}

}
