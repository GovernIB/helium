package es.caib.helium.logic.helper;

import es.caib.helium.commons.dades.DocumentTipusEnum;
import es.caib.helium.commons.dto.*;
import es.caib.helium.commons.utils.GlobalProperties;
import es.caib.helium.commons.utils.PdfUtils;
import es.caib.helium.persistence.common.jbpm.JbpmVars;
import es.caib.helium.persistence.entity.Document;
import es.caib.helium.persistence.entity.DocumentStore;
import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.entity.ExpedientDocument;
import es.caib.helium.persistence.repository.DocumentRepository;
import es.caib.helium.persistence.repository.DocumentStoreRepository;
import es.caib.helium.persistence.repository.ExpedientDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ExpedientDocumentsHelper {

	@Autowired
	private ExpedientDocumentRepository expedientDocumentsRepository;
	@Autowired
	private DocumentStoreRepository documentStoreRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private ExpedientHelper expedientHelper;
	@Autowired
	private PluginHelper pluginHelper;

	@Transactional
	public ExpedientDocumentDto findDocumentByDocumentStoreId(Long expedientId, Long documentStoreId) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
			expedientId,
			true,
			false,
			false,
			false);
		DocumentStore ds = documentStoreRepository.getReferenceById(documentStoreId);
		Document document =documentRepository.findByExpedientTipusAndCodi(
			expedient.getTipus().getId(),
			ds.getCodi(),
			expedient.getTipus().getExpedientTipusPare() != null);
		return crearDtoPerDocumentExpedient(document, ds, expedient.isArxiuActiu());
	}

	@Transactional
	public List<ExpedientDocumentDto> findDocumentsByExpedient(Long expedientId) {
		List<ExpedientDocumentDto> resultat = new ArrayList<ExpedientDocumentDto>();
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
			expedientId,
			true,
			false,
			false,
			false);
		List<Document> documentsTipusExpedient = documentRepository.findByExpedientTipusId(expedient.getTipus().getId());
		List<DocumentStore> documentStoreList = expedientDocumentsRepository.findByExpedientId(expedientId);

		for(DocumentStore ds : documentStoreList) {
			for(Document d : documentsTipusExpedient) {
				if(d.getCodi().equals(ds.getCodi())) {
					resultat.add(crearDtoPerDocumentExpedient(d, ds, expedient.isArxiuActiu()));
					break;
				}
			}
		}
		return resultat;
	}

	private ExpedientDocumentDto crearDtoPerDocumentExpedient(
		Document document,
		DocumentStore documentStore,
		boolean arxiuActiu) {
		ExpedientDocumentDto dto = new ExpedientDocumentDto();
		dto.setId(documentStore.getId());
		dto.setDataCreacio(documentStore.getDataCreacio());
		dto.setDataModificacio(documentStore.getDataModificacio());
		dto.setDataDocument(documentStore.getDataDocument());
		dto.setArxiuNom(calcularArxiuNom(documentStore, false));
		dto.setProcessInstanceId(documentStore.getProcessInstanceId());
		dto.setDocumentId(document.getId());
		dto.setDocumentCodi(document.getCodi());
		dto.setDocumentNom(document.getNom());
		dto.setPortafirmesActiu(document.isPortafirmesActiu());
		dto.setPlantilla(document.isPlantilla());
		dto.setSignat(documentStore.isSignat());
		if (documentStore.isSignat()) {
			this.setSignautraUrlVerificacio(dto, documentStore, arxiuActiu);
		} else {
			dto.setCustodiaCodi(document.getCustodiaCodi());
		}
		dto.setRegistrat(documentStore.isRegistrat());
		if (documentStore.isRegistrat()) {
			dto.setRegistreEntrada(documentStore.isRegistreEntrada());
			dto.setRegistreNumero(documentStore.getRegistreNumero());
			dto.setRegistreData(documentStore.getRegistreData());
			dto.setRegistreOficinaCodi(documentStore.getRegistreOficinaCodi());
			dto.setRegistreOficinaNom(documentStore.getRegistreOficinaNom());
		}
		dto.setNtiVersion(documentStore.getNtiVersion());
		dto.setNtiIdentificador(documentStore.getNtiIdentificador());
		dto.setNtiOrgano(documentStore.getNtiOrgano());
		dto.setNtiOrigen(documentStore.getNtiOrigen());
		dto.setNtiEstadoElaboracion(documentStore.getNtiEstadoElaboracion());
		dto.setNtiNombreFormato(documentStore.getNtiNombreFormato());
		dto.setNtiTipoDocumental(documentStore.getNtiTipoDocumental());
		dto.setNtiIdOrigen(documentStore.getNtiIdDocumentoOrigen());
		dto.setNtiTipoFirma(documentStore.getNtiTipoFirma());
		dto.setNtiCsv(documentStore.getNtiCsv());
		dto.setNtiDefinicionGenCsv(documentStore.getNtiDefinicionGenCsv());
		dto.setArxiuUuid(documentStore.getArxiuUuid());
		dto.setDocumentValid(documentStore.isDocumentValid());
		dto.setDocumentError(documentStore.getDocumentError());
		dto.setAnotacioAnnexId(documentStore.getAnnexId());
		dto.setReferenciaCustodia(documentStore.getReferenciaCustodia());

		return dto;
	}

	@Transactional
	public List<DocumentStoreDto> findByExpedient(Long expedientId) {
		return conversioTipusHelper.convertirList(
			expedientDocumentsRepository.findByExpedientId(expedientId),
			DocumentStoreDto.class
		);
	}

	public DocumentStore findDocumentStore(
		String codi,
		Long expedientId,
		String processId,
		String taskId) {
		return expedientDocumentsRepository.findByCodi(
			codi,
			expedientId,
			expedientId == null,
			processId,
			processId == null,
			taskId,
			taskId == null);
	}

	public Document findDocument(
		String codi,
		Long expedientId,
		String processId,
		String taskId) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
			expedientId,
			true,
			false,
			false,
			false);
		return documentRepository.findByExpedientTipusAndCodi(
			expedient.getTipus().getId(),
			codi,
			expedient.getTipus().getExpedientTipusPare() != null);
	}

	@Transactional
	public List<DocumentStoreDto> findByExpedientAndTask(Long expedientId, String taskId) {
		return conversioTipusHelper.convertirList(
			expedientDocumentsRepository.findByExpedientIdAndTaskId(expedientId, taskId),
			DocumentStoreDto.class
		);
	}

	@Transactional
	public List<DocumentStoreDto> findByExpedientAndProcess(Long expedientId, String processId) {
		return conversioTipusHelper.convertirList(
			expedientDocumentsRepository.findByExpedientIdAndProcessId(expedientId, processId),
			DocumentStoreDto.class
		);
	}

	@Transactional
	public void create(
		DocumentStore documentStore,
		Expedient expedient,
		String processInstanceId,
		String taskId) {
		ExpedientDocument entity = ExpedientDocument
										.builder()
										.tipus(documentStore.isAdjunt()? DocumentTipusEnum.ADJUNT : DocumentTipusEnum.DOCUMENT)
										.expedient(expedient)
										.documentStore(documentStore)
										.codi(documentStore.getCodi())
										.processInstanceId(processInstanceId)
										.taskId(taskId)
										.build();
		expedientDocumentsRepository.save(entity);
	}

	/**
	 * Actualitza un ExpedientDocument
	 * @param documentStore
	 * @param expedient
	 * @param processInstanceId
	 * @param taskId
	 */
	@Transactional
	public void update(
		DocumentStore documentStore,
		Expedient expedient,
		String processInstanceId,
		String taskId) {
		ExpedientDocument entity = expedientDocumentsRepository.findByDocumentStore(documentStore);
		if(entity != null) {
			entity.setTipus(documentStore.isAdjunt()? DocumentTipusEnum.ADJUNT : DocumentTipusEnum.DOCUMENT);
			entity.setExpedient(expedient);
			entity.setDocumentStore(documentStore);
			entity.setCodi(documentStore.getCodi());
			entity.setProcessInstanceId(processInstanceId);
			entity.setTaskId(taskId);
			expedientDocumentsRepository.save(entity);
		} else {
			create(documentStore, expedient, processInstanceId, taskId);
		}
	}

	@Transactional
	public void delete(Long documentStoreId) {
		expedientDocumentsRepository.deleteByDocumentStoreId(documentStoreId);
	}

	private String calcularArxiuNom(
		DocumentStore documentStore,
		boolean perSignar) {
		String nomOriginal = calcularArxiuNomOriginal(documentStore);
		String extensioDesti = calcularArxiuExtensioDesti(
			nomOriginal,
			documentStore,
			perSignar);
		return getNomArxiuAmbExtensio(
			documentStore.getArxiuNom(),
			extensioDesti);
	}

	/** Mètode per obtenir la URL per verificar la signatura. Si el documentStore té uuid s'asumeix que és a l'Arxiu i si no
	 * a Custòdia. En cas d'error informa de l'error en el DTO i enregistra l'error als logs.
	 *
	 * @param dto
	 * @param documentStore
	 */
	private void setSignautraUrlVerificacio(ExpedientDocumentDto dto, DocumentStore documentStore, boolean arxiuActiu) {
		if (!arxiuActiu) {
			// Custòdia
			try {
				dto.setSignaturaUrlVerificacio(
					pluginHelper.custodiaObtenirUrlComprovacioSignatura(
						documentStore.getReferenciaCustodia()));
			} catch(Exception e) {
				long time = new Date().getTime();
				String errMsg = time + " Error obtenint la url de verificació: " + e.toString();
				if (dto.getError() != null) {
					errMsg = dto.getError() + ". " + errMsg;
				}
				dto.setError(errMsg);
				dto.setSignaturaUrlVerificacio("error_" + time);
			}
		} else {
			// Arxiu
			dto.setSignaturaUrlVerificacio(
				getPropertyArxiuVerificacioBaseUrl() + documentStore.getNtiCsv());
		}
	}

	private String calcularArxiuNomOriginal(
		DocumentStore documentStore) {
		String nomOriginal;
		if (documentStore.isSignat() && isSignaturaFileAttached() && PdfUtils.isArxiuConvertiblePdf(documentStore.getArxiuNom())) {
			nomOriginal = getNomArxiuAmbExtensio(
				documentStore.getArxiuNom(),
				getExtensioArxiuSignat());
		} else {
			nomOriginal = documentStore.getArxiuNom();
		}
		return nomOriginal;
	}

	private String getNomArxiuAmbExtensio(
		String arxiuNomOriginal,
		String extensio) {
		if (!isActiuConversioSignatura())
			return arxiuNomOriginal;
		if (extensio == null)
			extensio = "";
		int indexPunt = arxiuNomOriginal.lastIndexOf(".");
		if (indexPunt != -1) {
			return arxiuNomOriginal.substring(0, indexPunt) + "." + extensio;
		} else {
			return arxiuNomOriginal + "." + extensio;
		}
	}

	private String calcularArxiuExtensioDesti(
		String nomOriginal,
		DocumentStore documentStore,
		boolean perSignar) {
		String extensioActual = null;
		int indexPunt = nomOriginal.lastIndexOf(".");
		if (indexPunt != -1)
			extensioActual = nomOriginal.substring(indexPunt + 1);
		String extensioDesti = extensioActual;
		if (perSignar && isActiuConversioSignatura()) {
			extensioDesti = getExtensioArxiuSignat();
		} else if (documentStore.isRegistrat()) {
			extensioDesti = getExtensioArxiuRegistrat();
		}
		return extensioDesti;
	}

	private String getExtensioArxiuRegistrat() {
		return (String)GlobalProperties.getInstance().get("app.conversio.registre.extension");
	}
	private String getExtensioArxiuSignat() {
		return (String)GlobalProperties.getInstance().get("app.conversio.signatura.extension");
	}
	private String getPropertyArxiuVerificacioBaseUrl() {
		return GlobalProperties.getInstance().getProperty(
			"app.arxiu.verificacio.baseurl");
	}
	private boolean isSignaturaFileAttached() {
		return "true".equalsIgnoreCase((String)GlobalProperties.getInstance().get("app.signatura.plugin.file.attached"));
	}
	private boolean isActiuConversioSignatura() {
		String actiuConversio = (String)GlobalProperties.getInstance().get("app.conversio.actiu");
		if (!"true".equalsIgnoreCase(actiuConversio))
			return false;
		String actiuConversioSignatura = (String)GlobalProperties.getInstance().get("app.conversio.signatura.actiu");
		return "true".equalsIgnoreCase(actiuConversioSignatura);
	}

	private String getDocumentCodiDeVariableJbpm(String varName) {
		return varName.substring(JbpmVars.PREFIX_DOCUMENT.length());
	}

}
