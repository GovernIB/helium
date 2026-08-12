package es.caib.helium.logic.helper;

import es.caib.helium.commons.config.PropertyConfig;
import es.caib.helium.commons.dades.DocumentTipusEnum;
import es.caib.helium.commons.dto.*;
import es.caib.helium.commons.exception.SistemaExternConversioDocumentException;
import es.caib.helium.commons.exception.SistemaExternException;
import es.caib.helium.commons.exception.ValidacioException;
import es.caib.helium.commons.utils.GlobalProperties;
import es.caib.helium.commons.utils.OpenOfficeUtils;
import es.caib.helium.commons.utils.PdfUtils;
import es.caib.helium.disseny.engine.WProcessDefinition;
import es.caib.helium.logic.intf.service.WorkflowEngineApi;
import es.caib.helium.logic.security.ExtendedPermission;
import es.caib.helium.logic.utils.DocumentTokenUtils;
import es.caib.helium.persistence.entity.*;
import es.caib.helium.persistence.repository.*;
import es.caib.pluginsib.arxiu.api.ContingutArxiu;
import es.caib.pluginsib.arxiu.api.Firma;
import es.caib.pluginsib.arxiu.api.FirmaTipus;
import es.caib.pluginsib.arxiu.caib.ArxiuConversioHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Component;

import javax.activation.MimetypesFileTypeMap;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class ExpedientDocumentHelper {

	public static final String VERSIO_NTI = "http://administracionelectronica.gob.es/ENI/XSD/v1.0/expediente-e";

	@Autowired
	private DocumentNotificacioRepository documentNotificacioRepository;
	@Autowired
	private ExpedientDocumentRepository expedientDocumentRepository;
	@Autowired
	private PortasignaturesRepository portasignaturesRepository;
	@Autowired
	private PeticioPinbalRepository peticioPinbalRepository;
	@Autowired
	private DocumentStoreRepository documentStoreRepository;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private DefinicioProcesRepository definicioProcesRepository;
	@Autowired
	private ExpedientHelper expedientHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private PlantillaHelper plantillaHelper;
	@Autowired
	private OpenOfficeUtils openOfficeUtils;
	@Autowired
	private WorkflowEngineApi workflowEngineApi;

	private DocumentTokenUtils documentTokenUtils;
	private PdfUtils pdfUtils;

	public List<DocumentStore> findByExpedient(Long expedientId) {
		return expedientDocumentRepository.findDocumentStoreByExpedientId(expedientId);
	}

	public DocumentStore findDocumentStore(
		Long expedientId,
		String processId,
		String taskId,
		String codi) {
		return expedientDocumentRepository.findDocumentStoreByCodi(
			codi,
			expedientId,
			expedientId == null,
			processId,
			processId == null,
			taskId,
			taskId == null);
	}

	public Document findDocument(
		Long expedientId,
		String processId,
		String taskId,
		String codi) {
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

	public List<DocumentStore> findByExpedientAndTask(Long expedientId, String taskId) {
		return expedientDocumentRepository.findDocumentStoreByExpedientIdAndTaskId(expedientId, taskId);
	}

	public List<DocumentStore> findByExpedientAndProcess(Long expedientId, String processId) {
		return expedientDocumentRepository.findDocumentStoreByExpedientIdAndProcessId(expedientId, processId);
	}

	public DocumentStore setDocument(
		Long expedientId,
		String processInstanceId,
		String taskInstanceId,
		String documentCodi,
		Date data,
		String adjuntTitol,
		String arxiuNom,
		byte[] arxiuContingut,
		String arxiuContentType,
		boolean ambFirma,
		boolean firmaSeparada,
		byte[] firmaContingut,
		NtiOrigenEnumDto ntiOrigen,
		NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
		NtiTipoDocumentalEnumDto ntiTipoDocumental,
		String ntiIdOrigen,
		List<ExpedientDocumentDto> annexosPerNotificar
	) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
			expedientId,
			new Permission[] {
				ExtendedPermission.DOC_MANAGE,
				ExtendedPermission.ADMINISTRATION});
		Document document = documentRepository.findByExpedientTipusAndCodi(
			expedient.getTipus().getId(),
			documentCodi,
			expedient.getTipus().getExpedientTipusPare() != null);

		DocumentStore documentStore = expedientDocumentRepository.findDocumentStoreByExpedientIdAndCodi(expedientId, documentCodi);

		if(documentStore == null) {
			String documentCodiPerCreacio = documentCodi;
			if (documentCodiPerCreacio == null) {
				documentCodiPerCreacio = Long.toString(new Date().getTime());
			}

			documentStore = new DocumentStore(
				pluginHelper.gestioDocumentalIsPluginActiu() ? DocumentStore.DocumentFont.ALFRESCO : DocumentStore.DocumentFont.INTERNA,
				processInstanceId,
				documentCodiPerCreacio,
				new Date(),
				data,
				arxiuNom);
		}

		if(documentCodi == null) {
			documentStore.setAdjunt(true);
			documentStore.setAdjuntTitol(adjuntTitol);
		} else {
			documentStore.setAdjunt(false);
		}
		documentStore.setAnnexId(null);

		if(annexosPerNotificar!=null && !annexosPerNotificar.isEmpty()) {
			List<DocumentStore> documentsContinguts = new ArrayList<DocumentStore>();
			List<DocumentStore> zips = new ArrayList<DocumentStore>();
			zips.add(documentStore); //aquesta llista li setejarem a cada documentStore contingut al zip
			for(ExpedientDocumentDto exp : annexosPerNotificar) {
				DocumentStore documentStoreCont = documentStoreRepository.getReferenceById(exp.getId());
				documentStoreCont.setZips(zips);
				documentsContinguts.add(documentStoreCont);
			}
			documentStore.setContinguts(documentsContinguts);
		}
		documentStore.setSignat(ambFirma);

		if (expedient.isNtiActiu()) {
			actualizarMetadadesNti(
				expedient,
				document,
				documentStore,
				ntiOrigen,
				ntiEstadoElaboracion,
				ntiTipoDocumental,
				ntiIdOrigen);
		}

		documentStore = documentStoreRepository.save(documentStore);

		List<ArxiuFirmaDto> firmes = null;
		es.caib.pluginsib.arxiu.api.Document documentArxiu = null;
		if (ambFirma) {
				// Valida firmes
				firmes = validaFirmaDocument(
					documentStore,
					arxiuContingut,
					firmaContingut,
					arxiuContentType);
		}

		String documentNom = documentStore.isAdjunt() ? documentStore.getArxiuNom() : (document!=null ? document.getNom() : "");

		if (expedient.isArxiuActiu()) {

			if (expedient.getArxiuUuid()==null || "".equals(expedient.getArxiuUuid())) {
				ContingutArxiu expedientCreat = pluginHelper.arxiuExpedientCrear(expedient);
				expedient.setArxiuUuid(expedientCreat.getIdentificador());
				expedient.setNtiIdentificador(expedientCreat.getExpedientMetadades().getIdentificador());
			}

			// Document integrat amb l'Arxiu
			if(firmes!=null && !firmes.isEmpty())
				comprovarFirmesReconegudes(firmes);

			// Actualitza el document a dins l'arxiu
			ArxiuDto arxiu = new ArxiuDto(
				arxiuNom,
				arxiuContingut,
				arxiuContentType);
			//en comptes del títol o nom d'adjunt, s'ha de passar el nom de l'Arxiu com podria ser blank.pdf
			documentNom = inArxiu(processInstanceId, documentStore.getArxiuUuid(), documentNom);

			ContingutArxiu contingutArxiu = pluginHelper.arxiuDocumentCrearActualitzar(
				expedient,
				documentNom,
				documentNom,
				documentStore,
				arxiu,
				ambFirma,
				firmaSeparada,
				firmes);
			documentStore.setArxiuUuid(contingutArxiu.getIdentificador());
			documentArxiu = pluginHelper.arxiuDocumentInfo(
				contingutArxiu.getIdentificador(),
				null,
				false,
				true);

			documentStore.setNtiIdentificador(documentArxiu.getMetadades().getIdentificador());
			if(ambFirma) {
				actualitzarNtiFirma(documentStore, documentArxiu);
			}
			documentStore.setSignat(firmes != null && !firmes.isEmpty());
		} else {
			// Guarda el document
			if (arxiuContingut != null) {
				// Si el arxiuContingut no es null actualitza la gestió documental o la BBDD
				if (pluginHelper.gestioDocumentalIsPluginActiu()) {
					String referenciaFont = pluginHelper.gestioDocumentalCreateDocument(
						expedient,
						documentStore.getId().toString(),
						arxiuNom,
						documentStore.getDataDocument(),
						arxiuNom,
						arxiuContingut);
					documentStore.setReferenciaFont(referenciaFont);
				} else {
					documentStore.setArxiuContingut(arxiuContingut);
				}
			}
		}

		create(
			documentStore,
			documentStore.getCodi(), //documentCodi,
			expedient,
			processInstanceId,
			taskInstanceId);

		return documentStore;
	}

	public void deleteDocument(
		Long expedientId,
		String processInstanceId,
		String taskInstanceId,
		String documentCodi) {

		ExpedientDocument expedientDocument = expedientDocumentRepository.findByCodi(
			documentCodi,
			expedientId,
			expedientId == null,
			processInstanceId,
			processInstanceId == null,
			taskInstanceId,
			taskInstanceId == null);

		if (expedientDocument != null) {
			boolean esborrarDocument = true;
			DocumentStore documentStore = expedientDocument.getDocumentStore();
			Long documentStoreId = documentStore.getId();
			Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
			List<DocumentNotificacio> enviaments = documentNotificacioRepository.findByExpedientAndDocumentId(expedient, documentStoreId);
			if (enviaments != null && !enviaments.isEmpty()) {
				// si té enviaments no s'esborra el document per a que es pugui consultar des de la notificació.
				esborrarDocument = false;
			}
			List<Long> documentsNotificats = this.getDocumentsNotificats(expedient);
			if (documentsNotificats.contains(documentStoreId)) {
				// si el document s'ha notificat no s'esborra el document per a que es pugui continuar consultant.
				esborrarDocument = false;
			}
			List<PeticioPinbal> peticioPinbals = peticioPinbalRepository.findByDocumentId(documentStoreId);
			if (peticioPinbals != null && !peticioPinbals.isEmpty()) {
				// si és de una petició pinbal no s'esborra el document per a que es pugui consultar des de la notificació.
				esborrarDocument = false;
			}

			if (expedient.isArxiuActiu() && documentStore.getArxiuUuid()!=null && !"".equals(documentStore.getArxiuUuid())) {
				if (documentStore.isSignat()) {
					log.info("Es procedeix a esborrar d'HELIUM el document firmat a l'Arxiu (expedient= " + expedient.getNumero() + ", tipus=" + expedient.getTipus().getCodi() +
						", entorn=" + expedient.getTipus().getEntorn().getCodi() + ", document= " + documentStoreId
						+ (documentStore.isAdjunt() ? documentStore.getAdjuntTitol() : documentStore.getCodiDocument() ) + ")");
				} else {
					if (esborrarDocument ) {
						// No esborra el document de l'Arxiu si té un annex associat
						if (documentStore.getAnnexId() == null) {
							// Consulta si existeix abans cridar a esborrar per a que no falli
							boolean arxiuExisteixDocument = false;
							try {
								arxiuExisteixDocument = null != pluginHelper.arxiuDocumentInfo(documentStore.getArxiuUuid(), null, false, documentStore.isSignat());
							} catch (Exception ex) {
								// Si no existeix falla la consulta.
								log.error("No s'ha pogut borrar el document "+documentStore.getArxiuUuid()+" del arxiu perque no existeix.");
							}
							if ( arxiuExisteixDocument) {
								// Esborra el document de l'Arxiu
								pluginHelper.arxiuDocumentEsborrar(documentStore.getArxiuUuid());
							}
						}
					}
				}
			} else {
				if (esborrarDocument && documentStore.getFont().equals(DocumentStore.DocumentFont.ALFRESCO)) {
					pluginHelper.gestioDocumentalDeleteDocument(
						documentStore.getReferenciaFont(),
						expedientHelper.findExpedientByProcessInstanceId(processInstanceId));
				}
				if (processInstanceId != null) {
					List<Portasignatures> psignaPendents = portasignaturesRepository.findByProcessInstanceIdAndEstatNotIn(
						processInstanceId,
						PortafirmesEstatEnum.getPendents());
					for (Portasignatures psigna: psignaPendents) {
						if (psigna.getDocumentStoreId().longValue() == documentStore.getId().longValue()) {
							psigna.setEstat(PortafirmesEstatEnum.ESBORRAT);
							portasignaturesRepository.save(psigna);
						}
					}
				}
			}
			expedientDocumentRepository.delete(expedientDocument);
			if (esborrarDocument) {
				documentStoreRepository.delete(documentStore);
			}
		}

	}

	public void delete(Long documentStoreId) {
		expedientDocumentRepository.deleteByDocumentStoreId(documentStoreId);
	}

	public void deleteByExpedient(Long expedientId) {
		expedientDocumentRepository.deleteByExpedientId(expedientId);
	}

	private ExpedientDocument create(
		DocumentStore documentStore,
		String codi,
		Expedient expedient,
		String processInstanceId,
		String taskId) {
		ExpedientDocument entity = expedientDocumentRepository.findByCodiAndExpedientId(codi, expedient.getId());
		if(entity != null)
			return entity;

		entity = ExpedientDocument
								.builder()
								.documentStore(documentStore)
								.codi(codi)
								.expedient(expedient)
								.processInstanceId(processInstanceId)
								.taskId(taskId)
								.tipus(DocumentTipusEnum.DOCUMENT)
								.build();
		return expedientDocumentRepository.save(entity);
	}

	public void actualizarMetadadesNti(
		Expedient expedient,
		Document document,
		DocumentStore documentStore,
		NtiOrigenEnumDto ntiOrigen,
		NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
		NtiTipoDocumentalEnumDto ntiTipoDocumental,
		String ntiIdDocumentoOrigen) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(expedient.getDataInici());
		String any = String.valueOf(cal.get(Calendar.YEAR));
		String org = expedient.getNtiOrgano();
		documentStore.setNtiIdentificador(
			"ES_" + org + "_" + any + "_HEL" + String.format("%027d", documentStore.getId()));
		documentStore.setNtiVersion(VERSIO_NTI);
		documentStore.setNtiOrgano(expedient.getUnitatOrganitzativa()!=null ? expedient.getUnitatOrganitzativa().getCodi() : expedient.getNtiOrgano());
		NtiOrigenEnumDto ntiOrigenCalculat = ntiOrigen;
		if (ntiOrigenCalculat == null && document != null) {
			ntiOrigenCalculat = document.getNtiOrigen();
		}
		if (ntiOrigenCalculat == null) {
			ntiOrigenCalculat = NtiOrigenEnumDto.ADMINISTRACIO;
		}
		documentStore.setNtiOrigen(ntiOrigenCalculat);
		NtiEstadoElaboracionEnumDto ntiEstadoElaboracionCalculat = ntiEstadoElaboracion;
		if (ntiEstadoElaboracionCalculat == null && document != null) {
			ntiEstadoElaboracionCalculat = document.getNtiEstadoElaboracion();
		}
		if (ntiEstadoElaboracionCalculat == null) {
			ntiEstadoElaboracionCalculat = NtiEstadoElaboracionEnumDto.ORIGINAL;
		}
		documentStore.setNtiEstadoElaboracion(ntiEstadoElaboracionCalculat);
		NtiTipoDocumentalEnumDto ntiTipoDocumentalCalculat = ntiTipoDocumental;
		if (ntiTipoDocumentalCalculat == null && document != null) {
			ntiTipoDocumentalCalculat = document.getNtiTipoDocumental();
		}
		if (ntiTipoDocumentalCalculat == null) {
			ntiTipoDocumentalCalculat = NtiTipoDocumentalEnumDto.ALTRES;
		}
		documentStore.setNtiTipoDocumental(ntiTipoDocumentalCalculat);
		NtiDocumentoFormato formato = getDocumentoFormatoPerArxiuNom(documentStore.getArxiuNom());
		if (formato != null) {
			documentStore.setNtiNombreFormato(formato);
		} else {
			throw new ValidacioException("Tipus d'arxiu no permes: " + documentStore.getArxiuNom());
		}
		documentStore.setNtiIdDocumentoOrigen(ntiIdDocumentoOrigen);
	}

	private NtiDocumentoFormato getDocumentoFormatoPerArxiuNom(
		String arxiuNom) {
		String extensio = FilenameUtils.getExtension(arxiuNom);
		if ("AVI".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.AVI;
		} else if ("CSS".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.CSS;
		} else if ("CSV".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.CSV;
		} else if ("DOCX".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.SOXML;
		} else if ("GML".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.GML;
		} else if ("GZ".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.GZIP;
		} else if ("HTM".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.XHTML; // HTML o XHTML!!!
		} else if ("HTML".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.XHTML; // HTML o XHTML!!!
		} else if ("JPEG".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.JPEG;
		} else if ("JPG".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.JPEG;
		} else if ("MHT".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.MHTML;
		} else if ("MHTML".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.MHTML;
		} else if ("MP3".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.MP3;
		} else if ("MP4".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.MP4V;
		} else if ("MPEG".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.MP4V;
		} else if ("ODG".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.OASIS12;
		} else if ("ODP".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.OASIS12;
		} else if ("ODS".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.OASIS12;
		} else if ("ODT".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.OASIS12;
		} else if ("OGA".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.OGG;
		} else if ("OGG".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.OGG;
		} else if ("PDF".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.PDF;
		} else if ("PNG".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.PNG;
		} else if ("PPTX".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.SOXML;
		} else if ("RTF".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.RTF;
		} else if ("SVG".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.SVG;
		} else if ("TIFF".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.TIFF;
		} else if ("TXT".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.TXT;
		} else if ("WEBM".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.WEBM;
		} else if ("XLSX".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.SOXML;
		} else if ("ZIP".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.ZIP;
		} else if ("CSIG".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.CSIG;
		} else if ("XSIG".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.XSIG;
		} else if ("XML".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.XML;

			// FORMATS NO DEFINITS AL CATÀLEG GENERAL DE L'ENI
		} else if ("DOC".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.DOC;
		} else if ("XLS".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.XLS;
		} else if ("MDB".equalsIgnoreCase(extensio)) {
			return NtiDocumentoFormato.MDB;
		} else if (extensio != null && !extensio.isEmpty()) {
			return NtiDocumentoFormato.ALTRES;
		}
		return null;
	}

	/** Mètode per consultar els documents notificats tant directament com dins dels .zip que poden contenir altres documents.
	 *
	 * @param expedient
	 * @return List<Long>
	 */
	private List<Long> getDocumentsNotificats(Expedient expedient) {
		List<Long> documentsNotificats = documentNotificacioRepository.getDocumentsNotificatsIdsPerExpedient(expedient);
		// Afegeix els documents continguts en els possibles .zips notificats
		if (!documentsNotificats.isEmpty()) {
			for (DocumentStore dsContingut : documentStoreRepository.findDocumentsContingutsIds(documentsNotificats)) {
				if (!documentsNotificats.contains(dsContingut.getId())) {
					documentsNotificats.add(dsContingut.getId());
				}
			};
		}
		return documentsNotificats;
	}

	private void comprovarFirmesReconegudes(List<ArxiuFirmaDto> arxiuFirmes) {
		// comprovar si la firma està reconeguda
		for (ArxiuFirmaDto arxiuFirma : arxiuFirmes) {
			// comprova que el tipus i el perfil estiguin reconeguts pel model CAIb
			if (arxiuFirma.getTipus().equals(NtiTipoFirmaEnumDto.SMIME) ||
				arxiuFirma.getTipus().equals(NtiTipoFirmaEnumDto.ODT) ||
				arxiuFirma.getTipus().equals(NtiTipoFirmaEnumDto.OOXML)) {
				throw new ValidacioException("El tipus de firma: "+ arxiuFirma.getTipus() +" no està reconegut en el model CAIB");
			}
			if (arxiuFirma.getPerfil().equals(ArxiuFirmaPerfilEnumDto.BASIC) ||
				arxiuFirma.getPerfil().equals(ArxiuFirmaPerfilEnumDto.BASELINE_T) ||
				arxiuFirma.getPerfil().equals(ArxiuFirmaPerfilEnumDto.LTA)) {
				throw new ValidacioException("El perfil de firma: "+ arxiuFirma.getPerfil() +" no està reconegut en el model CAIB");
			}
		}
	}

	/** Comprova si ja existeix a l'expedient un document amb el mateix nom i diferent UUID tenint en compte el . de l'extensió*/
	public String inArxiu(String processInstanceId, String arxiuUuid, String documentNom){
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);

		if (documentNom == null) {
			// logger.warn("S'ha passat un nom de document null per fixar com a nom a l'Arxiu pel processInstanceId=" +processInstanceId + " i uuid=" + arxiuUuid);
			documentNom = String.valueOf(new Date().getTime());
		}
		// Revisa els caràcters estranys com ho fa el plugin abans comprobar si ja existeix el nom
		documentNom = revisarContingutNom(documentNom);

		if(expedient.isArxiuActiu()) {

			List<ContingutArxiu> continguts = pluginHelper.arxiuExpedientInfo(expedient.getArxiuUuid()).getContinguts();
			int ocurrences = 0;
			if(continguts != null) {
				List<String> nomsExistingInArxiu = new ArrayList<String>();
				for(ContingutArxiu contingut : continguts) {
					if (!contingut.getIdentificador().equals(arxiuUuid)) {
						nomsExistingInArxiu.add(contingut.getNom().toLowerCase());
					}
				}
				String nouDocumentNom = documentNom;
				if (nomsExistingInArxiu.contains(nouDocumentNom.toLowerCase())) {
					if (nouDocumentNom.contains(".")) {
						// Nom amb extensió
						String name = nouDocumentNom.substring(0, nouDocumentNom.lastIndexOf('.'));
						String extension = nouDocumentNom.substring(nouDocumentNom.lastIndexOf('.'));
						nouDocumentNom = name;
						while (nomsExistingInArxiu.contains((nouDocumentNom + extension).toLowerCase())) {
							ocurrences ++;
							nouDocumentNom = name + " (" + ocurrences + ")";
						}
						nouDocumentNom += extension;

					} else {
						// Nom sense extensió
						while (nomsExistingInArxiu.contains(nouDocumentNom.toLowerCase())) {
							ocurrences ++;
							nouDocumentNom = documentNom + " (" + ocurrences + ")";
						}
					}
					documentNom = nouDocumentNom;
				}
			}
		}

		return documentNom;
	}

	/** Valida les firmes amb el plugin de validació de firmes */
	private List<ArxiuFirmaDto> validaFirmaDocument(
		DocumentStore documentStore,
		byte[] contingut,
		byte[] contingutFirma,
		String contentType) {
//		logger.debug("Recuperar la informació de les firmes amb el plugin ValidateSignature ("
//			+ "documentStore" + documentStore.getId() + ")");

		List<ArxiuFirmaDto> firmes = pluginHelper.validaSignaturaObtenirFirmes(
			documentStore,
			contingut,
			(contingutFirma != null && contingutFirma.length > 0) ? contingutFirma : null,
			contentType);

		// Fa una validació de les firmes
		for (ArxiuFirmaDto firma : firmes) {
			if (NtiTipoFirmaEnumDto.ODT.equals(firma.getTipus())) {
				throw new ValidacioException("L'Arxiu no accepta documents firmats de tipus ODF actualment (tipus: " +  firma.getTipus()
					+ ", perfil: " + firma.getPerfil() + ")" );
			}
			if (firma.getTipus() == null) {
				throw new ValidacioException("La firma no és vàlida. No s'ha pogut resoldre el tipus de firma (tipus: " +  firma.getTipus()
					+ ", perfil: " + firma.getPerfil() + ")" );
			}
			if (firma.getPerfil() == null) {
				throw new ValidacioException("La firma no és vàlida. No s'ha pogut resoldre el perfil de firma (tipus: " +  firma.getTipus()
					+ ", perfil: " + firma.getPerfil() + ")" );
			}
		}

		documentStore.setSignat(true);
		return firmes;
	}

	/**
	 * Substitueix salts de línia i tabuladors per espais i lleva caràcters esttranys no contemplats.
	 *
	 * @param nom Nom del contingut a revisar.
	 * @return Retorna el nom substituïnt tabuladors, salts de línia i apòstrofs per espais i ignorant caràcters
	 * invàlids. També treu el punt final en cas d'haver-n'hi.
	 */
	public static String revisarContingutNom(String nom) {
		return ArxiuConversioHelper.revisarContingutNom(nom);
	}

	public void actualitzarNtiFirma(
		DocumentStore documentStore,
		es.caib.pluginsib.arxiu.api.Document arxiuDocument) {
		NtiTipoFirmaEnumDto arxiuTipoFirma = null;
		String arxiuCsv = null;
		String arxiuCsvRegulacio = null;
		if (arxiuDocument != null) {
			if (arxiuDocument.getFirmes() != null) {
				for (Firma firma: arxiuDocument.getFirmes()) {
					if (FirmaTipus.CSV.equals(firma.getTipus())) {
						arxiuCsv = new String(firma.getContingut());
						arxiuCsvRegulacio = firma.getCsvRegulacio();
					} else if (firma.getTipus() != null) {
						switch (firma.getTipus()) {
							case CADES_ATT:
								arxiuTipoFirma = NtiTipoFirmaEnumDto.CADES_ATT;
								break;
							case CADES_DET:
								arxiuTipoFirma = NtiTipoFirmaEnumDto.CADES_DET;
								break;
							case XADES_ENV:
								arxiuTipoFirma = NtiTipoFirmaEnumDto.XADES_ENV;
								break;
							case XADES_DET:
								arxiuTipoFirma = NtiTipoFirmaEnumDto.XADES_DET;
								break;
							case PADES:
								arxiuTipoFirma = NtiTipoFirmaEnumDto.PADES;
								break;
							case ODT:
								arxiuTipoFirma = NtiTipoFirmaEnumDto.ODT;
								break;
							case OOXML:
								arxiuTipoFirma = NtiTipoFirmaEnumDto.OOXML;
								break;
							case SMIME:
								arxiuTipoFirma = NtiTipoFirmaEnumDto.SMIME;
								break;
						}
					}
				}
			}
			if (arxiuCsv == null && arxiuDocument.getMetadades() != null) {
				arxiuCsv = arxiuDocument.getMetadades().getCsv();
			}
			if (arxiuCsvRegulacio == null && arxiuDocument.getMetadades() != null) {
				arxiuCsvRegulacio = arxiuDocument.getMetadades().getCsvDef();
			}
		} else {
			arxiuTipoFirma = NtiTipoFirmaEnumDto.PADES;
			if (documentStore.getReferenciaCustodia() == null) {
				documentStore.setReferenciaCustodia(documentStore.getId() + "_" + new Date().getTime());
			}
//			String urlCustodia = pluginHelper.custodiaObtenirUrlComprovacioSignatura(
//				documentStore.getReferenciaCustodia());
//			String baseUrl = getPropertyCustodiaVerificacioBaseUrl();
//			if (baseUrl != null && urlCustodia.startsWith(baseUrl)) {
//				arxiuCsv = urlCustodia.substring(baseUrl.length());
//			} else {
//				arxiuCsv = urlCustodia;
//			}
			arxiuCsvRegulacio = getPropertyNtiCsvDef();
		}
		if (arxiuTipoFirma != null) {
			documentStore.setNtiTipoFirma(arxiuTipoFirma);
		}
		if (arxiuCsv != null) {
			documentStore.setNtiCsv(arxiuCsv);
		}
		if (arxiuCsvRegulacio != null) {
			documentStore.setNtiDefinicionGenCsv(arxiuCsvRegulacio);
		}
	}

	private String getPropertyNtiCsvDef() {
		return GlobalProperties.getInstance().getProperty(
			PropertyConfig.PROP_NTI_CSV_DEFINICIO);
	}

	public ArxiuDto generarDocumentAmbPlantillaIConvertir(
		Expedient expedient,
		Document document,
		String taskInstanceId,
		String processInstanceId,
		Date dataDocument) {
			ArxiuDto resultat;
			if (document.isPlantilla()) {
				resultat = plantillaHelper.generarDocumentPlantilla(
					expedient,
					document,
					taskInstanceId,
					processInstanceId,
					dataDocument);
				if (isActiuConversioVista()) {
					try {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						openOfficeUtils.convertir(
							resultat.getNom(),
							resultat.getContingut(),
							getExtensioVista(document),
							baos);
						resultat.setNom(
							nomArxiuAmbExtensio(
								resultat.getNom(),
								getExtensioVista(document)));
						resultat.setContingut(baos.toByteArray());
					} catch (Exception ex) {
						throw new SistemaExternConversioDocumentException(
							expedient.getEntorn().getId(),
							expedient.getEntorn().getCodi(),
							expedient.getEntorn().getNom(),
							expedient.getId(),
							expedient.getTitol(),
							expedient.getNumero(),
							expedient.getTipus().getId(),
							expedient.getTipus().getCodi(),
							expedient.getTipus().getNom(),
							ex);
					}
				}
			} else {
				resultat = new ArxiuDto(
					document.getArxiuNom(),
					document.getArxiuContingut());
			}
			if (resultat.getTipusMime() == null) {
				resultat.setTipusMime(getContentType(resultat.getNom()));
			}
			return resultat;
	}

	public String getContentType(String arxiuNom) {
		String fileContentDetect = new MimetypesFileTypeMap().getContentType(arxiuNom);
		if (!fileContentDetect.equals(MimeTypes.OCTET_STREAM)) {
			return fileContentDetect;
		}
		return MimeTypes.OCTET_STREAM;
	}

	private String getExtensioVista(Document document) {
		String extensioVista = null;
		if (isActiuConversioVista()) {
			if (document.getConvertirExtensio() != null && !document.getConvertirExtensio().isEmpty()) {
				extensioVista = document.getConvertirExtensio();
			} else {
				extensioVista = (String)GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_CONVERSIO_VISTA_EXTENSION);
				if (extensioVista == null)
					extensioVista = (String)GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_CONVERSIO_GENTASCA_EXTENSION);
			}
		}
		return extensioVista;
	}

	private String nomArxiuAmbExtensio(String fileName, String extensio) {
		if (extensio == null || extensio.isEmpty())
			return fileName;
		int indexPunt = fileName.lastIndexOf(".");
		if (indexPunt != -1) {
			String nom = fileName.substring(0, indexPunt);
			return nom + "." + extensio;
		} else {
			return fileName + "." + extensio;
		}
	}

	private boolean isActiuConversioVista() {
		String actiuConversio = GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_CONVERSIO_ACTIU);
		if (!"true".equalsIgnoreCase(actiuConversio))
			return false;
		String actiuConversioVista = GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_CONVERSIO_VISTA_ACTIU);
		if (actiuConversioVista == null)
			actiuConversioVista = GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_CONVERSIO_GENTASCA_ACTIU);
		return "true".equalsIgnoreCase(actiuConversioVista);
	}

	public void firmaServidor(String processInstanceId, Long documentStoreId, String motiu, byte[] bytes) {
	}

	public DocumentDto toDocumentDto(
		Long documentStoreId,
		boolean ambContingutOriginal,
		boolean ambContingutSignat,
		boolean ambContingutVista,
		boolean perSignar,
		boolean ambSegellSignatura
	) {
		if (documentStoreId != null) {
			DocumentStore document = documentStoreRepository.getReferenceById(documentStoreId);
			if (document != null) {
				DocumentDto dto = new DocumentDto();
				dto.setId(document.getId());
				dto.setDataCreacio(document.getDataCreacio());
				dto.setDataDocument(document.getDataDocument());
				dto.setArxiuNom(document.getArxiuNom());
				dto.setArxiuContingut(document.getArxiuContingut());
				dto.setProcessInstanceId(document.getProcessInstanceId());
				dto.setSignat(document.isSignat());
				dto.setAdjunt(document.isAdjunt());
				dto.setAdjuntTitol(document.getAdjuntTitol());
				try {
					dto.setTokenSignatura(getDocumentTokenUtils().xifrarToken(documentStoreId.toString()));
				} catch (Exception ex) {
					log.error("No s'ha pogut generar el token pel document " + documentStoreId, ex);
				}
				String codiDocument;
				if (document.isAdjunt()) {
					dto.setAdjuntId(document.getCodi());
					dto.setDocumentId(document.getId());
				} else {
					codiDocument = document.getCodi();
					WProcessDefinition jpd = workflowEngineApi.findProcessDefinitionWithProcessInstanceId(document.getProcessInstanceId());
					DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(jpd.getId());
					Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(document.getProcessInstanceId());
					ExpedientTipus expedientTipus = expedient.getTipus();
					Document doc;
					if (expedientTipus.isAmbInfoPropia())
						doc = documentRepository.findByExpedientTipusAndCodi(
							expedientTipus.getId(),
							codiDocument,
							expedientTipus.getExpedientTipusPare() != null);
					else
						doc = documentRepository.findByDefinicioProcesAndCodi(
							definicioProces,
							codiDocument);

					if (doc != null) {
						dto.setContentType(doc.getContentType());
						dto.setCustodiaCodi(doc.getCustodiaCodi());
						dto.setDocumentId(doc.getId());
						dto.setDocumentCodi(doc.getCodi());
						dto.setDocumentNom(doc.getNom());
						dto.setTipusDocPortasignatures(doc.getTipusDocPortasignatures());
						dto.setAdjuntarAuto(doc.isAdjuntarAuto());
					}
				}
				if (ambContingutOriginal) {
					dto.setArxiuContingut(
						getContingutDocumentAmbFont(document));
				}
				if (ambContingutSignat && document.isSignat() && isSignaturaFileAttached()) {
					dto.setSignatNom(
						getNomArxiuAmbExtensio(
							document.getArxiuNom(),
							getExtensioArxiuSignat()));
					byte[] signatura = null;
					dto.setSignatContingut(signatura);
				}
				if (ambContingutVista) {
					String arxiuOrigenNom;
					byte[] arxiuOrigenContingut;
					// Obtenim l'origen per a generar la vista o bé del document original
					// o bé del document signat
					if (document.isSignat() && isSignaturaFileAttached()) {
						if (ambContingutSignat) {
							arxiuOrigenNom = dto.getSignatNom();
							arxiuOrigenContingut = dto.getSignatContingut();
						} else {
							arxiuOrigenNom = getNomArxiuAmbExtensio(
								document.getArxiuNom(),
								getExtensioArxiuSignat());
							arxiuOrigenContingut = null;
						}
					} else {
						arxiuOrigenNom = dto.getArxiuNom();
						if (ambContingutOriginal) {
							arxiuOrigenContingut = dto.getArxiuContingut();
						} else {
							if (document.getFont().equals(DocumentStore.DocumentFont.INTERNA)) {
								arxiuOrigenContingut = document.getArxiuContingut();
							} else {
								arxiuOrigenContingut = pluginHelper.gestioDocumentalObtenirDocument(
									document.getReferenciaFont());
							}
						}
					}
					// Calculam l'extensió del document final de la vista
					String extensioActual = null;
					int indexPunt = arxiuOrigenNom.indexOf(".");
					if (indexPunt != -1)
						extensioActual = arxiuOrigenNom.substring(0, indexPunt);
					String extensioDesti = extensioActual;
					if (perSignar && isActiuConversioSignatura()) {
						extensioDesti = getExtensioArxiuSignat();
					} else if (document.isRegistrat()) {
						extensioDesti = getExtensioArxiuRegistrat();
					}
					dto.setVistaNom(dto.getArxiuNomSenseExtensio() + "." + extensioDesti);
					if ("pdf".equalsIgnoreCase(extensioDesti)) {
						// Si és un PDF podem estampar
						try {
							ByteArrayOutputStream vistaContingut = new ByteArrayOutputStream();
							DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
							String dataRegistre = null;
							if (document.getRegistreData() != null)
								dataRegistre = df.format(document.getRegistreData());
							String numeroRegistre = document.getRegistreNumero();
							getPdfUtils().estampar(
								arxiuOrigenNom,
								arxiuOrigenContingut,
								(ambSegellSignatura) ? !document.isSignat() : false,
								(ambSegellSignatura) ? getUrlComprovacioSignatura(dto.getTokenSignatura()): null,
								document.isRegistrat(),
								numeroRegistre,
								dataRegistre,
								document.getRegistreOficinaNom(),
								document.isRegistreEntrada(),
								vistaContingut,
								extensioDesti);
							dto.setVistaContingut(vistaContingut.toByteArray());
						} catch (Exception ex) {
							Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(document.getProcessInstanceId());
							String errorDescripcio = "No s'ha pogut generar la vista pel document '" + document.getCodiDocument() + "'";
							log.error(errorDescripcio, ex);
							throw SistemaExternException.tractarSistemaExternException(
								expedient.getEntorn().getId(),
								expedient.getEntorn().getCodi(),
								expedient.getEntorn().getNom(),
								expedient.getId(),
								expedient.getTitol(),
								expedient.getNumero(),
								expedient.getTipus().getId(),
								expedient.getTipus().getCodi(),
								expedient.getTipus().getNom(),
								MonitorIntegracioHelper.INTCODI_PFIRMA,
								"(PORTASIGNATURES. Enviar: " + errorDescripcio + ")",
								ex);
						}
					} else {
						// Si no és un pdf retornam la vista directament
						dto.setVistaNom(arxiuOrigenNom);
						dto.setVistaContingut(arxiuOrigenContingut);
					}
				}
				if (document.isRegistrat()) {
					dto.setRegistreData(document.getRegistreData());
					dto.setRegistreNumero(document.getRegistreNumero());
					dto.setRegistreOficinaCodi(document.getRegistreOficinaCodi());
					dto.setRegistreOficinaNom(document.getRegistreOficinaNom());
					dto.setRegistreEntrada(document.isRegistreEntrada());
					dto.setRegistrat(true);
				}
				return dto;
			}
		}
		return null;
	}

	private byte[] getContingutDocumentAmbFont(DocumentStore document) {
		if (document.getFont().equals(DocumentStore.DocumentFont.INTERNA))
			return document.getArxiuContingut();
		else
			return pluginHelper.gestioDocumentalObtenirDocument(
				document.getReferenciaFont());
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

	private String getExtensioArxiuSignat() {
		return (String)GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_CONVERSIO_SIGNATURA_EXTENSION);
	}

	private String getExtensioArxiuRegistrat() {
		return (String)GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_CONVERSIO_REGISTRE_EXTENSION);
	}

	private boolean isSignaturaFileAttached() {
		return "true".equalsIgnoreCase((String)GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_SIGNATURA_PLUGIN_FILE_ATTACHED));
	}

	private boolean isActiuConversioSignatura() {
		String actiuConversio = (String)GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_CONVERSIO_ACTIU);
		if (!"true".equalsIgnoreCase(actiuConversio))
			return false;
		String actiuConversioSignatura = (String)GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_CONVERSIO_SIGNATURA_ACTIU);
		return "true".equalsIgnoreCase(actiuConversioSignatura);
	}

	private DocumentTokenUtils getDocumentTokenUtils() {
		if (documentTokenUtils == null)
			documentTokenUtils = new DocumentTokenUtils(
				(String)GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_ENCRIPTACIO_CLAU));
		return documentTokenUtils;
	}

	private String getUrlComprovacioSignatura(String token) {
			String baseUrl = (String)GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_BASE_VERIFICACIO_URL);
			if (baseUrl == null)
				baseUrl = (String)GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_BASE_URL);
			return baseUrl + "/signatura/verificarExtern.html?token=" + token;
	}

	private PdfUtils getPdfUtils() {
		if (pdfUtils == null)
			pdfUtils = new PdfUtils();
		return pdfUtils;
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

	public ArxiuDto getArxiuPerDocumentStoreId(
		Long documentStoreId,
		boolean perSignar,
		boolean ambSegellSignatura,
		String versio) {
		return getArxiuPerDocumentStoreId(
			documentStoreId,
			perSignar,
			ambSegellSignatura,
			versio,
			false);
	}
	public ArxiuDto getArxiuPerDocumentStoreId(
		Long documentStoreId,
		boolean perSignar,
		boolean ambSegellSignatura,
		String versio,
		boolean perNotificar) {
		ArxiuDto resposta = new ArxiuDto();
		DocumentStore documentStore = documentStoreRepository.getReferenceById(documentStoreId);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(documentStore.getProcessInstanceId());

		// Obtenim el contingut de l'arxiu
		byte[] arxiuOrigenContingut = null;

		if (expedient.isArxiuActiu()) {

			String arxiuNom = FilenameUtils.removeExtension(documentStore.getArxiuNom());
			String arxiuExtensio = FilenameUtils.getExtension(documentStore.getArxiuNom());
			// #1697 Es revisa que no retorni contingut null i es reintenta
			es.caib.pluginsib.arxiu.api.Document documentArxiu = null;
			int intents = 0;
			byte[] arxiuContingut = documentStore.getArxiuContingut();
			if(arxiuContingut==null && documentStore.getArxiuUuid()!=null) {
				do {
					if(documentStore.getArxiuUuid()!=null) {
						documentArxiu = pluginHelper.arxiuDocumentInfo(
							documentStore.getArxiuUuid(),
							versio,
							true,
							documentStore.isSignat());
					}
					if (documentArxiu == null || documentArxiu.getContingut() == null) {
						log.warn("La consulta del contingut pel document amb id=" + documentStore.getId() +
							" ha retornat " + (documentArxiu == null ? "": "documentArxiu.contingut") + " null" );
					}
				} while (intents++ < 5
					&& (documentArxiu == null
					|| documentArxiu.getContingut() == null));

				if (documentArxiu == null
					|| documentArxiu.getContingut() == null )
				{
					throw new SistemaExternException(
						MonitorIntegracioHelper.INTCODI_ARXIU,
						"No s'ha pogut consultar el contingut a l'Arxiu pel document id=" + documentStore.getId() +
							" amb uuid=" + documentStore.getArxiuUuid() + " i " + (documentStore.isAdjunt() ? "títol d'adjunt " + documentStore.getAdjuntTitol() : "codi de document " + documentStore.getCodiDocument()) +
							" després de " + intents + "intents.",
						null);
				} else {
					arxiuExtensio = FilenameUtils.getExtension(documentArxiu.getContingut().getArxiuNom());
				}
				resposta.setContingut(documentArxiu.getContingut().getContingut());
				resposta.setTipusMime(
					documentArxiu.getContingut().getTipusMime() != null ?
						documentArxiu.getContingut().getTipusMime() :
						getContentType(documentStore.getArxiuNom()));

				// Construeix el nom de l'arxiu a partir del nom original i de l'extensió del document recuperat de l'Arxiu
				String nom = arxiuNom + (arxiuExtensio != null && !arxiuExtensio.isEmpty() ? "." + arxiuExtensio : "") ;
				if( arxiuExtensio == null || arxiuExtensio.trim().isEmpty()) {
					MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
					try {
						MimeType mimeType = allTypes.forName(resposta.getTipusMime());
						nom += mimeType.getExtension();
					} catch (MimeTypeException e) {
						log.warn("No s'ha pogut determinar la extensió del fitxer " + nom);
					}
				}
				resposta.setNom(nom);

			} else {
				resposta.setContingut(arxiuContingut);
				resposta.setTipusMime(getContentType(documentStore.getArxiuNom()));
				// Construeix el nom de l'arxiu a partir del nom original i de l'extensió del document recuperat de l'Arxiu
				String nom = arxiuNom + (arxiuExtensio != null && !arxiuExtensio.isEmpty() ? "." + arxiuExtensio : "") ;
				if( arxiuExtensio == null || arxiuExtensio.trim().isEmpty()) {
					MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
					try {
						MimeType mimeType = allTypes.forName(resposta.getTipusMime());
						nom += mimeType.getExtension();
					} catch (MimeTypeException e) {
						log.warn("No s'ha pogut determinar la extensió del fitxer " + nom);
					}
				}
				resposta.setNom(nom);
			}

			// Si els documents estan firmats amb PADES sempre tindran extensió PDF
			boolean isFirmaPades = false;
			if (documentStore.isSignat() && documentArxiu!= null && documentArxiu.getFirmes() != null) {
				for (Firma firma: documentArxiu.getFirmes()) {
					if (FirmaTipus.PADES.equals(firma.getTipus())) {
						isFirmaPades = true;
						break;
					}
				}
			}
			if (isFirmaPades) {
				if (resposta.getNom() != null && !resposta.getNom().toLowerCase().endsWith(".pdf")) {
					String nomDoc = resposta.getNom();
					int indexPunt = nomDoc.lastIndexOf(".");
					nomDoc =  (indexPunt != -1 ? nomDoc.substring(0, indexPunt) :  nomDoc) + ".pdf";
					resposta.setNom(nomDoc);
				}
			}
		} else {
			if (documentStore.getFont().equals(DocumentStore.DocumentFont.INTERNA)) {
				arxiuOrigenContingut = documentStore.getArxiuContingut();
			} else {
				arxiuOrigenContingut = pluginHelper.gestioDocumentalObtenirDocument(
					documentStore.getReferenciaFont());
			}

			// Calculam el nom de l'arxiu
			String arxiuNomOriginal = calcularArxiuNomOriginal(documentStore);
			String extensioDesti = calcularArxiuExtensioDesti(
				arxiuNomOriginal,
				documentStore,
				perSignar);

			// Només podem convertir a extensió de destí PDF
			if ("pdf".equalsIgnoreCase(extensioDesti)) {

				resposta.setNom(
					getNomArxiuAmbExtensio(
						documentStore.getArxiuNom(),
						extensioDesti));

				// Si és un PDF podem estampar
				if (!perNotificar) {
					try {
						ByteArrayOutputStream vistaContingut = new ByteArrayOutputStream();
						DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
						String dataRegistre = null;
						if (documentStore.getRegistreData() != null)
							dataRegistre = df.format(documentStore.getRegistreData());
						String numeroRegistre = documentStore.getRegistreNumero();
						String urlComprovacioSignatura = null;
						getPdfUtils().estampar(
							arxiuNomOriginal,
							arxiuOrigenContingut,
							ambSegellSignatura && documentStore.getReferenciaCustodia() != null,
							urlComprovacioSignatura,
							documentStore.isRegistrat(),
							numeroRegistre,
							dataRegistre,
							documentStore.getRegistreOficinaNom(),
							documentStore.isRegistreEntrada(),
							vistaContingut,
							extensioDesti);
						resposta.setContingut(vistaContingut.toByteArray());
					} catch (SistemaExternConversioDocumentException ex) {
						log.error("Hi ha hagut un problema amb el servidor OpenOffice i el document '" + arxiuNomOriginal + "'", ex.getCause());
						throw new SistemaExternConversioDocumentException(
							expedient.getEntorn().getId(),
							expedient.getEntorn().getCodi(),
							expedient.getEntorn().getNom(),
							expedient.getId(),
							expedient.getTitol(),
							expedient.getNumero(),
							expedient.getTipus().getId(),
							expedient.getTipus().getCodi(),
							expedient.getTipus().getNom(),
							ex);
					} catch (Exception ex) {
						throw SistemaExternException.tractarSistemaExternException(
							expedient.getEntorn().getId(),
							expedient.getEntorn().getCodi(),
							expedient.getEntorn().getNom(),
							expedient.getId(),
							expedient.getTitol(),
							expedient.getNumero(),
							expedient.getTipus().getId(),
							expedient.getTipus().getCodi(),
							expedient.getTipus().getNom(),
							MonitorIntegracioHelper.INTCODI_CONVDOC , //sistemaExtern
							"No s'ha pogut generar la vista pel document (id=" + documentStoreId + ", processInstanceId=" + documentStore.getProcessInstanceId() + ")",
							ex);
					}
				} else {
					resposta.setContingut(arxiuOrigenContingut);
				}
			} else {
				// Si no és un pdf retornam la vista directament
				resposta.setNom(arxiuNomOriginal);
				resposta.setContingut(arxiuOrigenContingut);
			}
			resposta.setTipusMime(getContentType(resposta.getNom()));
		}
		return resposta;
	}
}
