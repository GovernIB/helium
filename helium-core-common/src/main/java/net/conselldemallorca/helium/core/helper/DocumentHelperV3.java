/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypes;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaTipus;
import net.conselldemallorca.helium.core.common.JbpmVars;
import net.conselldemallorca.helium.core.helperv26.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.hibernate.AnotacioAnnex;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentNotificacio;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore.DocumentFont;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.FirmaTasca;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.TipusEstat;
import net.conselldemallorca.helium.core.model.hibernate.Registre;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.util.DocumentTokenUtils;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.OpenOfficeUtils;
import net.conselldemallorca.helium.core.util.PdfUtils;
import net.conselldemallorca.helium.integracio.plugins.signatura.RespostaValidacioSignatura;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessDefinition;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiDocumentoFormato;
import net.conselldemallorca.helium.v3.core.api.dto.NtiEstadoElaboracionEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiOrigenEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoDocumentalEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoFirmaEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaValidacioSignaturaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDocumentDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternConversioDocumentException;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.v3.core.repository.AnotacioAnnexRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentNotificacioRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentStoreRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.FirmaTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.PortasignaturesRepository;
import net.conselldemallorca.helium.v3.core.repository.RegistreRepository;
import net.conselldemallorca.helium.v3.core.repository.TascaRepository;

/**
 * Helper per a gestionar els documents dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DocumentHelperV3 {

	public static final String VERSIO_NTI = "http://administracionelectronica.gob.es/ENI/XSD/v1.0/expediente-e";

	@Resource
	private PlantillaHelper plantillaHelper;
	@Resource
	private TascaRepository tascaRepository;
	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private DocumentStoreRepository documentStoreRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private DocumentTascaRepository documentTascaRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private TascaHelper tascaHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource
	private FirmaTascaRepository firmaTascaRepository;
	@Resource
	private PortasignaturesRepository portasignaturesRepository;
	@Resource
	private OpenOfficeUtils openOfficeUtils;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private ExceptionHelper exceptionHelper;
	@Resource
	private ExpedientRegistreHelper expedientRegistreHelper;
	@Resource
	private RegistreRepository registreRepository;
	@Resource
	private DocumentNotificacioRepository documentNotificacioRepository; 
	@Resource
	private AnotacioAnnexRepository anotacioAnnexRepository;

	private PdfUtils pdfUtils;
	private DocumentTokenUtils documentTokenUtils;
	private Tika tika = new Tika();



	public ExpedientDocumentDto findOnePerInstanciaProces(
			String processInstanceId,
			Long documentStoreId) {
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		return findOnePerInstanciaProces(processInstanceId, documentStore);
	}

	public ExpedientDocumentDto findOnePerInstanciaProces(
			String processInstanceId,
			String documentCodi) {
		ExpedientDocumentDto expedientDocumentDto = null;
		Long documentStoreId = findDocumentStorePerInstanciaProcesAndDocumentCodi(
				processInstanceId,
				documentCodi);
		DocumentStore documentStore = null;
		if (documentStoreId != null)
		 documentStore = documentStoreRepository.findOne(documentStoreId);
		if (documentStore != null)
			expedientDocumentDto = findOnePerInstanciaProces(processInstanceId, documentStore);
		return expedientDocumentDto;
	}

	public Document findDocumentPerInstanciaProcesICodi(
			String processInstanceId,
			String documentCodi) {
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		ExpedientTipus expedientTipus = expedient.getTipus();
		if (expedientTipus.isAmbInfoPropia()) {
			return documentRepository.findByExpedientTipusAndCodi(
					expedientTipus.getId(),
					documentCodi, 
					expedientTipus.getExpedientTipusPare() != null);
		} else {
			DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
					processInstanceId);
			return documentRepository.findByDefinicioProcesAndCodi(
					definicioProces, 
					documentCodi);
		}		
	}

	public ArxiuDto getArxiuPerDocumentStoreId(
			Long documentStoreId,
			boolean perSignar,
			boolean ambSegellSignatura) {
		ArxiuDto resposta = new ArxiuDto();
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		// Obtenim el contingut de l'arxiu
		byte[] arxiuOrigenContingut = null;
		if (documentStore.getArxiuUuid() != null) {
			es.caib.plugins.arxiu.api.Document documentArxiu = pluginHelper.arxiuDocumentInfo(
					documentStore.getArxiuUuid(),
					null,
					true,
					documentStore.isSignat());
			resposta.setNom(documentStore.getArxiuNom());
			resposta.setContingut(documentArxiu.getContingut().getContingut());
			resposta.setTipusMime(
					documentArxiu.getContingut().getTipusMime() != null ? 
							documentArxiu.getContingut().getTipusMime() : 
								getContentType(documentStore.getArxiuNom()));
			// Si els documents estan firmats amb PADES sempre tindran extensió PDF
			boolean isFirmaPades = false;
			if (documentStore.isSignat() && documentArxiu.getFirmes() != null) {
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
			if (documentStore.isSignat() && isSignaturaFileAttached()) {
				arxiuOrigenContingut = pluginHelper.custodiaObtenirSignaturesAmbArxiu(documentStore.getReferenciaCustodia());
			} else {
				if (documentStore.getFont().equals(DocumentFont.INTERNA)) {
					arxiuOrigenContingut = documentStore.getArxiuContingut();
				} else {
					arxiuOrigenContingut = pluginHelper.gestioDocumentalObtenirDocument(
							documentStore.getReferenciaFont());
				}
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
				try {
					ByteArrayOutputStream vistaContingut = new ByteArrayOutputStream();
					DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					String dataRegistre = null;
					if (documentStore.getRegistreData() != null)
						dataRegistre = df.format(documentStore.getRegistreData());
					String numeroRegistre = documentStore.getRegistreNumero();
					String urlComprovacioSignatura = null;
				    if (ambSegellSignatura)
				    	urlComprovacioSignatura = getUrlComprovacioSignatura(documentStoreId);
				    getPdfUtils().estampar(
					      arxiuNomOriginal,
					      arxiuOrigenContingut,
					      (ambSegellSignatura) ? !documentStore.isSignat() : false,
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
					logger.error("Hi ha hagut un problema amb el servidor OpenOffice i el document '" + arxiuNomOriginal + "'", ex.getCause());
					Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(documentStore.getProcessInstanceId());
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
							messageHelper.getMessage("error.document.conversio.externa"));
				} catch (Exception ex) {
					Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(documentStore.getProcessInstanceId());
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
							"No s'ha pogut generar la vista pel document (id=" + documentStoreId + ", processInstanceId=" + documentStore.getProcessInstanceId() + ")", 
							ex);
				}
			} else {
				// Si no és un pdf retornam la vista directament
				resposta.setNom(arxiuNomOriginal);
				resposta.setContingut(arxiuOrigenContingut);
			}
		}
		return resposta;
	}

	public List<ExpedientDocumentDto> findDocumentsPerInstanciaProces(
			String processInstanceId) {
		List<ExpedientDocumentDto> resposta = new ArrayList<ExpedientDocumentDto>();
		// Consulta els documents de la definició de procés
		DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
				processInstanceId);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		ExpedientTipus expedientTipus = expedient.getTipus();
		List<Document> documents;
		if (expedientTipus.isAmbInfoPropia()) {
			if (expedientTipus.getExpedientTipusPare() == null)
				documents = documentRepository.findByExpedientTipusId(expedientTipus.getId());
			else
				documents = documentRepository.findByExpedientTipusAmbHerencia(expedientTipus.getId());
		} else {
			documents = documentRepository.findByDefinicioProcesId(definicioProces.getId());
		}
		// Consulta els annexos de les anotacions de registre i les guarda en un Map<Long documentStoreId, Anotacio>
		Map<Long, AnotacioAnnex> mapAnotacions = new HashMap<Long, AnotacioAnnex>();
		for (AnotacioAnnex annex : anotacioAnnexRepository.findByAnotacioExpedientId(expedient.getId()))
			mapAnotacions.put(annex.getDocumentStoreId(), annex);
		// Consulta els documents de l'instància de procés
		Map<String, Object> varsInstanciaProces = jbpmHelper.getProcessInstanceVariables(processInstanceId);
		if (varsInstanciaProces != null) {
			filtrarVariablesAmbDocuments(varsInstanciaProces);
			for (String var: varsInstanciaProces.keySet()) {
				Long documentStoreId = (Long)varsInstanciaProces.get(var);
				if (documentStoreId != null) {
					if (var.startsWith(JbpmVars.PREFIX_DOCUMENT)) {
						// Afegeix el document
						String documentCodi = getDocumentCodiDeVariableJbpm(var);
						Document document = null;
						for (Document doc: documents) {
							if (doc.getCodi().equals(documentCodi)) {
								document = doc;
								break;
							}
						}
						if (document != null) {
							ExpedientDocumentDto ed = crearDtoPerDocumentExpedient(
									document,
									documentStoreId);
							List<DocumentNotificacio> enviaments = documentNotificacioRepository.findByExpedientAndDocumentId(expedient, documentStoreId);
							ed.setNotificat(!enviaments.isEmpty());
							ed.setAnotacioId(null); // De moment només arriben per anotació els annexos
							resposta.add(ed);
						} else {
							ExpedientDocumentDto dto = new ExpedientDocumentDto();
							dto.setId(documentStoreId);
							dto.setProcessInstanceId(processInstanceId);
							dto.setError("No s'ha trobat el document de la definició de procés (" +
										"documentCodi=" + documentCodi + ")");
							resposta.add(dto);
						}
					} else if (var.startsWith(JbpmVars.PREFIX_ADJUNT)) {
						// Afegeix l'adjunt
						ExpedientDocumentDto ed = crearDtoPerAdjuntExpedient(
								getAdjuntIdDeVariableJbpm(var),
								documentStoreId);
						ed.setNotificat(false); // De moment els annexos no es notifiquen
						AnotacioAnnex annex = mapAnotacions.get(ed.getId());
						if (annex != null) {
							ed.setAnotacioId(annex.getAnotacio().getId());
							ed.setAnotacioIdentificador(annex.getAnotacio().getIdentificador());
						}
						resposta.add(ed);
					}
				}
			}
		}
		return resposta;
	}

	public Long findDocumentStorePerInstanciaProcesAndDocumentCodi(
			String processInstanceId,
			String documentCodi) {
			return getDocumentStoreIdDeVariableJbpm(
					null,
					processInstanceId,
					documentCodi);
	}
	
	public ExpedientDocumentDto findDocumentPerDocumentStoreId(
			String processInstanceId,
			Long documentStoreId) {
		// Consulta els documents de la definició de procés
		DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
				processInstanceId);
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		if (documentStore != null) {
			if (documentStore.isAdjunt()) {
				return crearDtoPerAdjuntExpedient(
						getAdjuntIdDeVariableJbpm(documentStore.getJbpmVariable()),
						documentStoreId);
			} else {
				Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
				ExpedientTipus expedientTipus = expedient.getTipus();
				Document document;
				if (expedientTipus.isAmbInfoPropia()) {
					document = documentRepository.findByExpedientTipusAndCodi(
							expedientTipus.getId(),
							documentStore.getCodiDocument(),
							expedientTipus.getExpedientTipusPare() != null);
				} else {
					document = documentRepository.findByDefinicioProcesAndCodi(
							definicioProces, 
							documentStore.getCodiDocument());
				}
				if (document != null) {
					return crearDtoPerDocumentExpedient(
									document,
									documentStoreId);
				} else {
					ExpedientDocumentDto dto = new ExpedientDocumentDto();
					dto.setId(documentStoreId);
					dto.setError("No s'ha trobat el document de la definició de procés (" +
								"documentCodi=" + documentStore.getCodiDocument() + ")");
					return dto;
				}
			}
		} else {
			ExpedientDocumentDto dto = new ExpedientDocumentDto();
			dto.setId(documentStoreId);
			dto.setError("No s'ha trobat el document de la definició de procés (" +
						"documentStoreId=" + documentStoreId + ")");
			return dto;
		}
	}
		
	public TascaDocumentDto findDocumentPerId(String tascaId, Long docId, Long expedientTipusId) {
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				tascaId,
				true,
				true);
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(
				task.getProcessDefinitionId());
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProces(
				task.getTaskName(),
				definicioProces);
		Document document = documentRepository.findOne(docId);

		DocumentTasca documentTasca = documentTascaRepository.findAmbTascaCodi(tasca.getId(), document.getCodi(), expedientTipusId);
		
		return toTascaDocumentDto(
					task,
					documentTasca.getDocument(), documentTasca.isRequired(), documentTasca.isReadOnly());
	}

	public List<TascaDocumentDto> findDocumentsPerInstanciaTasca(JbpmTask task) {
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(
				task.getProcessDefinitionId());
		Expedient exp = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		Long expedientTipusId = exp != null ? exp.getTipus().getId() : null;
		ExpedientTipus expedientTipus = exp != null? expedientTipusHelper.findAmbProcessInstanceId(task.getProcessInstanceId()) : null;
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProces(
				task.getTaskName(),
				definicioProces);

		boolean ambHerencia = HerenciaHelper.ambHerencia(expedientTipus);
		Map<String, Document> sobreescrits = new HashMap<String, Document>();
		if (ambHerencia) {
			for (Document d : documentRepository.findSobreescrits(expedientTipusId))
				sobreescrits.put(d.getCodi(), d);
		}

		List<DocumentTasca> documentsTasca = documentTascaRepository.findAmbTascaIdOrdenats(
				tasca.getId(),
				expedientTipusId);
		List<TascaDocumentDto> resposta = new ArrayList<TascaDocumentDto>();
		Document document;
		for (DocumentTasca documentTasca: documentsTasca) {
			document = documentTasca.getDocument();
			if (ambHerencia && sobreescrits.containsKey(document.getCodi()))
				document = sobreescrits.get(document.getCodi());
			resposta.add(
					toTascaDocumentDto(
							task,
							document,
							documentTasca.isRequired(),
							documentTasca.isReadOnly()));
		}
		return resposta;
	}

	public boolean hasDocumentsPerInstanciaTasca(JbpmTask task) {
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(
				task.getProcessDefinitionId());
		Long expedientTipusId = expedientTipusHelper.findIdByProcessInstanceId(task.getProcessInstanceId());

		return documentTascaRepository.countAmbDefinicioProcesITascaJbpmName(
				definicioProces.getId(),
				task.getTaskName(),
				expedientTipusId) > 0;
	}
	
	public boolean hasDocumentsNotReadOnlyPerInstanciaTasca(JbpmTask task) {
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(
				task.getProcessDefinitionId());
		Long expedientTipusId = expedientTipusHelper.findIdByProcessInstanceId(task.getProcessInstanceId());

		return documentTascaRepository.countAmbDefinicioProcesITascaJbpmNameINotReadOnly(
				definicioProces.getId(),
				task.getTaskName(),
				expedientTipusId) > 0;
	}

	public List<TascaDocumentDto> findDocumentsPerInstanciaTascaSignar(JbpmTask task) {
		List<TascaDocumentDto> resposta = new ArrayList<TascaDocumentDto>();

		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(
				task.getProcessDefinitionId());
		Expedient exp = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		Long expedientTipusId = exp != null ? exp.getTipus().getId() : null;
		ExpedientTipus expedientTipus = exp != null? expedientTipusHelper.findAmbProcessInstanceId(task.getProcessInstanceId()) : null;
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProces(
				task.getTaskName(),
				definicioProces);
		
		boolean ambHerencia = HerenciaHelper.ambHerencia(expedientTipus);
		Map<String, Document> sobreescrits = new HashMap<String, Document>();
		if (ambHerencia) {
			for (Document d : documentRepository.findSobreescrits(expedientTipusId))
				sobreescrits.put(d.getCodi(), d);
		}

		Document document;
		for (FirmaTasca firmaTasca: firmaTascaRepository.findAmbTascaIdOrdenats(tasca.getId(), expedientTipusId)) {
			document = firmaTasca.getDocument();
			if (ambHerencia && sobreescrits.containsKey(document.getCodi()))
				document = sobreescrits.get(document.getCodi());
			resposta.add(toTascaDocumentDto(
					task,
					document, 
					firmaTasca.isRequired(), 
					false));
		}
		return resposta;
	}

	public boolean hasDocumentsPerInstanciaTascaSignar(JbpmTask task) {
		return firmaTascaRepository.countAmbTasca(
				task.getTaskName(),
				task.getProcessDefinitionId()) > 0;
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

	public DocumentStore getDocumentStore(
			JbpmTask task,
			String documentCodi) {
		DocumentStore documentStore = null;
		Long documentStoreId = getDocumentStoreIdDeVariableJbpm(String.valueOf(task.getTask().getId()), task.getProcessInstanceId(), documentCodi);
		if (documentStoreId != null) {
			documentStore = documentStoreRepository.findOne(documentStoreId);
		}
		return documentStore;
	}
	
	public List<RespostaValidacioSignaturaDto> getRespostasValidacioSignatura(DocumentStore documentStore) {
		DocumentDto document = toDocumentDto(
				documentStore.getId(),
				true,
				false,
				false,
				false,
				false, // Per notificar
				false);
		if (pluginHelper.custodiaPotObtenirInfoSignatures()) {
			return conversioTipusHelper.convertirList(pluginHelper.custodiaDadesValidacioSignatura(
					documentStore.getReferenciaCustodia()), RespostaValidacioSignaturaDto.class);
		} else if (isSignaturaFileAttached()) {
			List<byte[]> signatures = pluginHelper.custodiaObtenirSignatures(documentStore.getReferenciaCustodia());
			List<RespostaValidacioSignatura> resposta = new ArrayList<RespostaValidacioSignatura>();
			if (!signatures.isEmpty()) {
				RespostaValidacioSignatura res = pluginHelper.signaturaVerificar(
						null,
						signatures.get(0),
						true);
				resposta.add(res);
			}
			return conversioTipusHelper.convertirList(resposta, RespostaValidacioSignaturaDto.class);
		} else {
			List<RespostaValidacioSignatura> resposta = new ArrayList<RespostaValidacioSignatura>();
			List<byte[]> signatures = pluginHelper.custodiaObtenirSignatures(
					documentStore.getReferenciaCustodia());
			for (byte[] signatura: signatures) {
				RespostaValidacioSignatura res = pluginHelper.signaturaVerificar(
						document.getArxiuContingut(),
						signatura,
						true);
				resposta.add(res);
			}
			return conversioTipusHelper.convertirList(resposta, RespostaValidacioSignaturaDto.class);
		}
	}

	public DocumentDto signarDocumentTascaAmbToken(
			String tascaId,
			String token,
			byte[] signatura) throws Exception {
		DocumentDto dto = null;
		Long documentStoreId = getDocumentStoreIdPerToken(token);		
		if (documentStoreId != null) {
			DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
			dto = toDocumentDto(
					documentStoreId,
					false,
					false,
					true,
					true,
					false, // Per notificar
					(documentStore.getArxiuUuid() == null));
			
			// Guarda la firma asociada al documetn
			this.guardarDocumentFirmat(
					documentStore.getProcessInstanceId(),
					documentStore.getId(),
					signatura,
					true,
					true);

			// Guarda el valor en una variable jbpm
			JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
					tascaId,
					true,
					true);
			jbpmHelper.setTaskInstanceVariable(
					task.getId(),
					JbpmVars.PREFIX_SIGNATURA + dto.getDocumentCodi(),
					documentStore.getId());
		}
		return dto;
	}

	public Long getDocumentStoreIdPerToken(String token) {
		try {
			String[] tokenDesxifrat = getDocumentTokenUtils().desxifrarTokenMultiple(token);
			if (tokenDesxifrat.length == 1)
				return Long.parseLong(tokenDesxifrat[0]);
			else
				return Long.parseLong(tokenDesxifrat[1]);
		} catch (Exception ex) {
			throw new RuntimeException("Format de token ('" + token + "') incorrecte", ex);
		}
	}

	
	public Long crearDocument(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi,
			Date documentData,
			boolean isAdjunt,
			String adjuntTitol,
			String arxiuNom,
			byte[] arxiuContingut,
			NtiOrigenEnumDto ntiOrigen,
			NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			NtiTipoDocumentalEnumDto ntiTipoDocumental,
			String ntiIdDocumentoOrigen) {
		return crearDocument(
				taskInstanceId,
				processInstanceId,
				documentCodi,
				documentData,
				isAdjunt,
				adjuntTitol,
				arxiuNom,
				arxiuContingut,
				null, // arxiuUuid
				this.getContentType(arxiuNom),
				false,	// amb firma
				false,
				null,
				ntiOrigen,
				ntiEstadoElaboracion,
				ntiTipoDocumental,
				ntiIdDocumentoOrigen);
	}

	public Long crearDocument(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi,
			Date documentData,
			boolean isAdjunt,
			String adjuntTitol,
			String arxiuNom,
			byte[] arxiuContingut,
			String arxiuUuid,
			String arxiuContentType,
			boolean ambFirma,
			boolean firmaSeparada,
			byte[] firmaContingut,
			NtiOrigenEnumDto ntiOrigen,
			NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			NtiTipoDocumentalEnumDto ntiTipoDocumental,
			String ntiIdDocumentoOrigen) {
		String documentCodiPerCreacio = documentCodi;
		if (documentCodiPerCreacio == null && isAdjunt) {
			documentCodiPerCreacio = new Long(new Date().getTime()).toString();
		}
		DocumentStore documentStore = new DocumentStore(
				pluginHelper.gestioDocumentalIsPluginActiu() ? DocumentFont.ALFRESCO : DocumentFont.INTERNA,
				processInstanceId,
				getVarPerDocumentCodi(documentCodiPerCreacio, isAdjunt),
				new Date(),
				documentData,
				arxiuNom);
		documentStore.setAdjunt(isAdjunt);
		if (isAdjunt) {
			documentStore.setAdjuntTitol(adjuntTitol);
		}
		DocumentStore documentStoreCreat = documentStoreRepository.save(documentStore);
		documentStoreRepository.flush();
		postProcessarDocument(
				documentStoreCreat,
				taskInstanceId,
				processInstanceId,
				arxiuNom,
				arxiuContingut,
				arxiuUuid,
				arxiuContentType,
				ambFirma,
				firmaSeparada,
				firmaContingut,
				ntiOrigen,
				ntiEstadoElaboracion,
				ntiTipoDocumental,
				ntiIdDocumentoOrigen);
		return documentStoreCreat.getId();
	}

	public Long actualitzarDocument(
			Long documentStoreId,
			String taskInstanceId,
			String processInstanceId,
			Date documentData,
			String adjuntTitol,
			String arxiuNom,
			byte[] arxiuContingut,
			NtiOrigenEnumDto ntiOrigen,
			NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			NtiTipoDocumentalEnumDto ntiTipoDocumental,
			String ntiIdDocumentoOrigen) {
			return actualitzarDocument(
					documentStoreId,
					null,
					processInstanceId,
					documentData,
					adjuntTitol,
					arxiuNom,
					arxiuContingut,
					this.getContentType(arxiuNom),
					false,
					false,
					null,
					ntiOrigen,
					ntiEstadoElaboracion,
					ntiTipoDocumental,
					ntiIdDocumentoOrigen);

		}
	
		public Long actualitzarDocument(
			Long documentStoreId,
			String taskInstanceId,
			String processInstanceId,
			Date documentData,
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
			String ntiIdDocumentoOrigen) {
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		if (documentStore == null) {
			throw new NoTrobatException(
					DocumentStore.class, 
					documentStoreId);
		}
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		List<DocumentNotificacio> enviaments = documentNotificacioRepository.findByExpedientAndDocumentId(expedient, documentStoreId);
		if (enviaments != null && enviaments.size() > 0)
			throw new ValidacioException("No es pot modificar un document amb " + enviaments.size() + " enviaments");
		documentStore.setDataDocument(documentData);
		documentStore.setDataModificacio(new Date());
		if (documentStore.isAdjunt()) {
			documentStore.setAdjuntTitol(adjuntTitol);
		}
		if (arxiuContingut != null && pluginHelper.gestioDocumentalIsPluginActiu()) {
			pluginHelper.gestioDocumentalDeleteDocument(
					documentStore.getReferenciaFont(),
					expedient);
		}
		postProcessarDocument(
				documentStore,
				taskInstanceId,
				processInstanceId,
				arxiuNom,
				arxiuContingut,
				null, //arxiuUuid
				arxiuContentType,
				ambFirma,
				firmaSeparada,
				firmaContingut,
				ntiOrigen,
				ntiEstadoElaboracion,
				ntiTipoDocumental,
				ntiIdDocumentoOrigen);
		return documentStore.getId();
	}

	public Long crearActualitzarDocument(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi,
			Date documentData,
			String arxiuNom,
			byte[] arxiuContingut,
			NtiOrigenEnumDto ntiOrigen,
			NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			NtiTipoDocumentalEnumDto ntiTipoDocumental,
			String ntiIdDocumentoOrigen) 
	{
		// ALERTA! Crear/actualitzar no funciona amb adjunts
		return crearActualitzarDocument(
				taskInstanceId, 
				processInstanceId, 
				documentCodi, 
				documentData, 
				arxiuNom, 
				arxiuContingut, 
				this.getContentType(arxiuNom),
				false,  // sense firma
				false,  // firma separada
				null,	// firma contingut
				ntiOrigen, 
				ntiEstadoElaboracion, 
				ntiTipoDocumental, 
				ntiIdDocumentoOrigen);
	}

	public Long crearActualitzarDocument(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi,
			Date documentData,
			String arxiuNom,
			byte[] arxiuContingut,
			String arxiuContentType,
			boolean ambFirma,
			boolean firmaSeparada,
			byte[] firmaContingut,
			NtiOrigenEnumDto ntiOrigen,
			NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			NtiTipoDocumentalEnumDto ntiTipoDocumental,
			String ntiIdDocumentoOrigen) {
		Long documentStoreId = getDocumentStoreIdDeVariableJbpm(
				taskInstanceId,
				processInstanceId,
				documentCodi);
		DocumentStore documentStore = null;
		if (documentStoreId != null) {
			documentStore = documentStoreRepository.findOne(documentStoreId);
		}
		if (arxiuContentType == null)
			arxiuContentType = this.getContentType(arxiuNom);
		if (documentStore == null) {
			return crearDocument(
					taskInstanceId,
					processInstanceId,
					documentCodi,
					documentData,
					false,
					null,
					arxiuNom,
					arxiuContingut,
					null, // arxiuUuid
					arxiuContentType,
					ambFirma,
					firmaSeparada,
					firmaContingut,
					ntiOrigen,
					ntiEstadoElaboracion,
					ntiTipoDocumental,
					ntiIdDocumentoOrigen);
		} else {
			return actualitzarDocument(
					documentStoreId,
					taskInstanceId,
					processInstanceId,
					documentData,
					null,
					arxiuNom,
					arxiuContingut,
					arxiuContentType,
					ambFirma,
					firmaSeparada,
					firmaContingut,
					ntiOrigen,
					ntiEstadoElaboracion,
					ntiTipoDocumental,
					ntiIdDocumentoOrigen);
		}
	}

	public void esborrarDocument(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi) {
		Object varValor = null;
		if (taskInstanceId != null) {
			varValor = jbpmHelper.getTaskInstanceVariable(
					taskInstanceId,
					getVarPerDocumentCodi(documentCodi, false));
		} else if (processInstanceId != null) {
			varValor = jbpmHelper.getProcessInstanceVariable(
					processInstanceId,
					getVarPerDocumentCodi(documentCodi, false));
		}
		if (varValor != null && varValor instanceof Long) {
			esborrarDocument(
					taskInstanceId,
					processInstanceId,
					(Long)varValor);
		}
	}

	public void esborrarDocument(
			String taskInstanceId,
			String processInstanceId,
			Long documentStoreId) {
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		if (documentStore != null) {
			Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
			List<DocumentNotificacio> enviaments = documentNotificacioRepository.findByExpedientAndDocumentId(expedient, documentStoreId);
			if (enviaments != null && enviaments.size() > 0)
				throw new ValidacioException("No es pot esborrar un document amb " + enviaments.size() + " enviaments");
			if (expedient.isArxiuActiu()) {
				if (documentStore.isSignat()) {
					throw new ValidacioException("No es pot esborrar un document firmat");
				} else {
					pluginHelper.arxiuDocumentEsborrar(documentStore.getArxiuUuid());
				}
			} else {
				if (documentStore.isSignat()) {
					if (pluginHelper.custodiaIsPluginActiu()) {
						pluginHelper.custodiaEsborrarSignatures(
								documentStore.getReferenciaCustodia(), 
								expedientHelper.findExpedientByProcessInstanceId(processInstanceId));
					}
				}
				if (documentStore.getFont().equals(DocumentFont.ALFRESCO)) {
					pluginHelper.gestioDocumentalDeleteDocument(
							documentStore.getReferenciaFont(),
							expedientHelper.findExpedientByProcessInstanceId(processInstanceId));
				}
				if (processInstanceId != null) {
					List<TipusEstat> estats = new ArrayList<TipusEstat>();
					estats.add(TipusEstat.PENDENT);
					estats.add(TipusEstat.SIGNAT);
					estats.add(TipusEstat.REBUTJAT);
					estats.add(TipusEstat.ERROR);
					List<Portasignatures> psignaPendents = portasignaturesRepository.findByProcessInstanceIdAndEstatNotIn(
							processInstanceId,
							estats);
					for (Portasignatures psigna: psignaPendents) {
						if (psigna.getDocumentStoreId().longValue() == documentStore.getId().longValue()) {
							psigna.setEstat(TipusEstat.ESBORRAT);
							portasignaturesRepository.save(psigna);
						}
					}
				}
			}
			documentStoreRepository.delete(documentStoreId);
		}
		if (taskInstanceId != null) {
			jbpmHelper.deleteTaskInstanceVariable(
					taskInstanceId,
					documentStore.getJbpmVariable());
			String documentCodi = getDocumentCodiPerVariableJbpm(
					documentStore.getJbpmVariable());
			jbpmHelper.deleteTaskInstanceVariable(
					taskInstanceId,
					JbpmVars.PREFIX_SIGNATURA + documentCodi);
		}
		if (processInstanceId != null) {
			jbpmHelper.deleteProcessInstanceVariable(
					processInstanceId,
					documentStore.getJbpmVariable());
		}
	}

	public Document getDocumentDisseny(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi) {
		DefinicioProces definicioProces = null;
		if (taskInstanceId != null) {
			JbpmTask taskInstance = jbpmHelper.getTaskById(taskInstanceId);
			definicioProces = definicioProcesRepository.findByJbpmId(taskInstance.getProcessDefinitionId());
		} else {
			JbpmProcessInstance processInstance = jbpmHelper.getProcessInstance(processInstanceId);
			definicioProces = definicioProcesRepository.findByJbpmId(processInstance.getProcessDefinitionId());
		}
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		ExpedientTipus expedientTipus = expedient.getTipus();
		if (expedientTipus.isAmbInfoPropia())
			return documentRepository.findByExpedientTipusAndCodi(
					expedientTipus.getId(),
					documentCodi,
					expedientTipus.getExpedientTipusPare() != null);
		else
			return documentRepository.findByDefinicioProcesAndCodi(
					definicioProces, 
					documentCodi);
	}

	public String getVarPerDocumentCodi(String documentCodi, boolean isAdjunt) {
		if (isAdjunt)
			return JbpmVars.PREFIX_ADJUNT + documentCodi;
		else
			return JbpmVars.PREFIX_DOCUMENT + documentCodi;
	}
	public static String getDocumentCodiPerVariableJbpm(String var) {
		if (var.startsWith(JbpmVars.PREFIX_DOCUMENT)) {
			return var.substring(JbpmVars.PREFIX_DOCUMENT.length());
		} else if (var.startsWith(JbpmVars.PREFIX_ADJUNT)) {
			return var.substring(JbpmVars.PREFIX_ADJUNT.length());
		} else if (var.startsWith(JbpmVars.PREFIX_SIGNATURA)) {
			return var.substring(JbpmVars.PREFIX_SIGNATURA.length());
		} else {
			return var;
		}
	}

	public ArxiuDto generarDocumentAmbPlantillaIConvertir(
			Expedient expedient,
			Document document,
			String taskInstanceId,
			String processInstanceId,
			Date dataDocument) {
		if (document.isPlantilla()) {
			ArxiuDto resultat = plantillaHelper.generarDocumentPlantilla(
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
							messageHelper.getMessage("error.document.conversio.externa"));
				}
			}
			return resultat;
		} else {
			ArxiuDto resultat = new ArxiuDto(
					document.getArxiuNom(),
					document.getArxiuContingut());
			return resultat;
		}
	}

	public DocumentDto toDocumentDto(
			Long documentStoreId,
			boolean ambContingutOriginal,
			boolean ambContingutSignat,
			boolean ambContingutVista,
			boolean perSignar,
			boolean perNotificar,
			boolean ambSegellSignatura) {
		if (documentStoreId != null) {
			DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
			if (documentStore != null) {
				DocumentDto dto = new DocumentDto();
				dto.setId(documentStore.getId());
				dto.setDataCreacio(documentStore.getDataCreacio());
				dto.setDataDocument(documentStore.getDataDocument());
				dto.setArxiuNom(documentStore.getArxiuNom());
				dto.setArxiuUuid(documentStore.getArxiuUuid());
				dto.setArxiuCsv(documentStore.getNtiCsv());
				dto.setProcessInstanceId(documentStore.getProcessInstanceId());
				dto.setSignat(documentStore.isSignat());
				dto.setAdjunt(documentStore.isAdjunt());
				dto.setAdjuntTitol(documentStore.getAdjuntTitol());
				try {
					dto.setTokenSignatura(getDocumentTokenUtils().xifrarToken(documentStoreId.toString()));
				} catch (Exception ex) {
					logger.error("No s'ha pogut generar el token pel document " + documentStoreId, ex);
				}
				if (documentStore.isSignat()) {
					dto.setUrlVerificacioCustodia(
							pluginHelper.custodiaObtenirUrlComprovacioSignatura(
									documentStoreId.toString()));
				}
				String codiDocument;
				if (documentStore.isAdjunt()) {
					dto.setAdjuntId(documentStore.getJbpmVariable().substring(JbpmVars.PREFIX_ADJUNT.length()));
					dto.setCodi(dto.getAdjuntId());
					dto.setDocumentCodi(dto.getAdjuntId());
					dto.setDocumentNom(documentStore.getAdjuntTitol());
					dto.setArxiuContingut(documentStore.getArxiuContingut());
				} else {
					codiDocument = documentStore.getJbpmVariable().substring(JbpmVars.PREFIX_DOCUMENT.length());
					JbpmProcessDefinition jpd = jbpmHelper.findProcessDefinitionWithProcessInstanceId(documentStore.getProcessInstanceId());
					DefinicioProces definicioProces = definicioProcesRepository.findByJbpmKeyAndVersio(
							jpd.getKey(),
							jpd.getVersion());
					Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(documentStore.getProcessInstanceId());
					ExpedientTipus expedientTipus = expedient.getTipus();
					Document doc;
					if (expedientTipus.isAmbInfoPropia()) {
						doc = documentRepository.findByExpedientTipusAndCodi(
								expedientTipus.getId(),
								codiDocument,
								expedientTipus.getExpedientTipusPare() != null);
					} else {
						doc = documentRepository.findByDefinicioProcesAndCodi(
								definicioProces, 
								codiDocument);
					}
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
				if (documentStore.getArxiuUuid() != null) {
					// Si el document està guardat a l'arxiu no és necessari estampar-lo abans
					// de firmar i una vegada firmat hem de mostrar la versió imprimible.
					boolean ambContingut = ambContingutOriginal || ambContingutSignat || ambContingutVista;
					es.caib.plugins.arxiu.api.Document documentArxiu = pluginHelper.arxiuDocumentInfo(
							documentStore.getArxiuUuid(),
							null,
							ambContingut,
							documentStore.isSignat());
					String arxiuNom = documentStore.getArxiuNom();
					// String arxiuNom = documentArxiu.getContingut().getArxiuNom();
					byte[] arxiuContingut = null;
					if (ambContingut)
						arxiuContingut = documentArxiu.getContingut().getContingut();
					// String arxiuTipusMime = documentArxiu.getContingut().getTipusMime();
					if (ambContingutOriginal) {
						dto.setArxiuNom(arxiuNom);
						dto.setArxiuContingut(arxiuContingut);
					}
					if (ambContingutSignat) {
						dto.setSignatNom(arxiuNom);
						dto.setSignatContingut(arxiuContingut);
					}
					if (ambContingutVista) {
						dto.setVistaNom(arxiuNom);
						dto.setVistaContingut(arxiuContingut);
						
						if ((perSignar && isActiuConversioSignatura()) || documentStore.isRegistrat()) {
	 						String arxiuOrigenNom = arxiuNom;
							byte[] arxiuOrigenContingut = arxiuContingut;
							
							
							// Calculam l'extensió del document final de la vista
							String extensioActual = null;
							int indexPunt = arxiuOrigenNom.lastIndexOf(".");
							if (indexPunt != -1)
								extensioActual = arxiuOrigenNom.substring(indexPunt + 1);
							String extensioDesti = extensioActual;
							if (perSignar && isActiuConversioSignatura()) {
								extensioDesti = getExtensioArxiuSignat();
							} else if (documentStore.isRegistrat()) {
								extensioDesti = getExtensioArxiuRegistrat();
							}
							dto.setVistaNom(dto.getArxiuNomSenseExtensio() + "." + extensioDesti);
							if ("pdf".equalsIgnoreCase(extensioDesti)) {
								// Si és un PDF podem estampar
								try {
									ByteArrayOutputStream vistaContingut = new ByteArrayOutputStream();
									DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
									String dataRegistre = null;
									if (documentStore.getRegistreData() != null)
										dataRegistre = df.format(documentStore.getRegistreData());
									String numeroRegistre = documentStore.getRegistreNumero();
									// S'invoca la estampació per convertir a PDF sense estampa.
									getPdfUtils().estampar(
											arxiuOrigenNom,
											arxiuOrigenContingut,
											false, // sense segell
											null,
											false, // sense segell
											numeroRegistre,
											dataRegistre,
											documentStore.getRegistreOficinaNom(),
											documentStore.isRegistreEntrada(),
											vistaContingut,
											extensioDesti);
									dto.setVistaContingut(vistaContingut.toByteArray());
								} catch (SistemaExternConversioDocumentException ex) {
									logger.error("Hi ha hagut un problema amb el servidor OpenOffice i el document '" + documentStore.getCodiDocument() + "'", ex.getCause());
									Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(documentStore.getProcessInstanceId());
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
											messageHelper.getMessage("error.document.conversio.externa"));
								} catch (Exception ex) {
									logger.error("No s'ha pogut generar la vista pel document '" + documentStore.getCodiDocument() + "'", ex);
									Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(documentStore.getProcessInstanceId());
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
											"Estampar PDF '" + documentStore.getCodiDocument() + "'", 
											ex);
								}
							}
						}
					}
				} else {
					if (ambContingutOriginal) {
						dto.setArxiuContingut(
								getContingutDocumentAmbFont(documentStore));
					}
					if (ambContingutSignat && documentStore.isSignat() && isSignaturaFileAttached()) {
						dto.setSignatNom(
								getNomArxiuAmbExtensio(
										documentStore.getArxiuNom(),
										getExtensioArxiuSignat()));
						byte[] signatura = pluginHelper.custodiaObtenirSignaturesAmbArxiu(documentStore.getReferenciaCustodia());
						dto.setSignatContingut(signatura);
					}
					if (ambContingutVista) {
						String arxiuOrigenNom;
						byte[] arxiuOrigenContingut;
						// Obtenim l'origen per a generar la vista o bé del document original
						// o bé del document signat
						if (documentStore.isSignat() && isSignaturaFileAttached()) {
							if (ambContingutSignat) {
								arxiuOrigenNom = dto.getSignatNom();
								arxiuOrigenContingut = dto.getSignatContingut();
							} else {
								arxiuOrigenNom = getNomArxiuAmbExtensio(
										documentStore.getArxiuNom(),
										getExtensioArxiuSignat());
								arxiuOrigenContingut = pluginHelper.custodiaObtenirSignaturesAmbArxiu(documentStore.getReferenciaCustodia());
							}
						} else {
							arxiuOrigenNom = dto.getArxiuNom();
							if (ambContingutOriginal) {
								arxiuOrigenContingut = dto.getArxiuContingut();
							} else {
								if (documentStore.getFont().equals(DocumentFont.INTERNA)) {
									arxiuOrigenContingut = documentStore.getArxiuContingut();
								} else {
									arxiuOrigenContingut = pluginHelper.gestioDocumentalObtenirDocument(
											documentStore.getReferenciaFont());
								}
							}
						}
						// Calculam l'extensió del document final de la vista
						String extensioActual = null;
						int indexPunt = arxiuOrigenNom.lastIndexOf(".");
						if (indexPunt != -1)
							extensioActual = arxiuOrigenNom.substring(indexPunt + 1);
						String extensioDesti = extensioActual;
						if (perSignar && isActiuConversioSignatura()) {
							extensioDesti = getExtensioArxiuSignat();
						} else if (documentStore.isRegistrat()) {
							extensioDesti = getExtensioArxiuRegistrat();
						}
						dto.setVistaNom(dto.getArxiuNomSenseExtensio() + "." + extensioDesti);
						if ("pdf".equalsIgnoreCase(extensioDesti)) {
							// Si és un PDF podem estampar
							try {
								ByteArrayOutputStream vistaContingut = new ByteArrayOutputStream();
								DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
								String dataRegistre = null;
								if (documentStore.getRegistreData() != null)
									dataRegistre = df.format(documentStore.getRegistreData());
								String numeroRegistre = documentStore.getRegistreNumero();
								getPdfUtils().estampar(
										arxiuOrigenNom,
										arxiuOrigenContingut,
										(ambSegellSignatura) ? !documentStore.isSignat() : false,
										(ambSegellSignatura) ? getUrlComprovacioSignatura(documentStoreId, dto.getTokenSignatura()): null,
										documentStore.isRegistrat(),
										numeroRegistre,
										dataRegistre,
										documentStore.getRegistreOficinaNom(),
										documentStore.isRegistreEntrada(),
										vistaContingut,
										extensioDesti);
								dto.setVistaContingut(vistaContingut.toByteArray());
							} catch (SistemaExternConversioDocumentException ex) {
								logger.error("Hi ha hagut un problema amb el servidor OpenOffice i el document '" + documentStore.getCodiDocument() + "'", ex.getCause());
								Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(documentStore.getProcessInstanceId());
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
										messageHelper.getMessage("error.document.conversio.externa"));
							} catch (Exception ex) {
								logger.error("No s'ha pogut generar la vista pel document '" + documentStore.getCodiDocument() + "'", ex);
								Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(documentStore.getProcessInstanceId());
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
										"Estampar PDF '" + documentStore.getCodiDocument() + "'", 
										ex);
							}
						} else {
							// Si no és un pdf retornam la vista directament
							dto.setVistaNom(arxiuOrigenNom);
							dto.setVistaContingut(arxiuOrigenContingut);
						}
					}
				}
				if (perNotificar) {
					// Si és per notificar s'ha de passar a PDF si no és .zip ni .pdf
					String extensio = dto.getArxiuExtensio() != null? dto.getArxiuExtensio().toLowerCase() : "";
					if ( ! PdfUtils.isArxiuConvertiblePdf(dto.getArxiuNom()) && ! "zip".equals(extensio))
						throw new ValidacioException("No es pot notificar el document \"" + dto.getNom() + "\" perquè l'arxiu no és de tipus .ZIP ni és convertible a .PDF (" + PdfUtils.getExtensionsConvertiblesPdf() + ")" );
					// Si no és .zip ni .pdf transforma el contingut a PDF
					if (!"zip".equals(extensio) && !"pdf".equals(extensio)) {
						dto.setArxiuContingut(getPdfUtils().convertirPdf(dto.getArxiuNom(), dto.getArxiuContingut()));
						dto.setArxiuNom(dto.getArxiuNomSenseExtensio() + ".pdf");
						dto.setContentType("application/pdf");
						//TODO: no posar a null i deixar el contingut com a null quan estigui a l'arxiu i regweb no falli
					}
					dto.setArxiuUuid(null);
				}
				if (documentStore.isRegistrat()) {
					dto.setRegistreData(documentStore.getRegistreData());
					dto.setRegistreNumero(documentStore.getRegistreNumero());
					dto.setRegistreOficinaCodi(documentStore.getRegistreOficinaCodi());
					dto.setRegistreOficinaNom(documentStore.getRegistreOficinaNom());
					dto.setRegistreEntrada(documentStore.isRegistreEntrada());
					dto.setRegistrat(true);
				}
				return dto;
			}
		}
		return null;
	}

	@SuppressWarnings("incomplete-switch")
	public void actualitzarNtiFirma(
			DocumentStore documentStore,
			es.caib.plugins.arxiu.api.Document arxiuDocument) {
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
		} else {
			arxiuTipoFirma = NtiTipoFirmaEnumDto.PADES;
			String urlCustodia = pluginHelper.custodiaObtenirUrlComprovacioSignatura(
					documentStore.getId().toString());
			String baseUrl = getPropertyCustodiaVerificacioBaseUrl();
			if (baseUrl != null && urlCustodia.startsWith(baseUrl)) {
				arxiuCsv = urlCustodia.substring(baseUrl.length());
			} else {
				arxiuCsv = urlCustodia;
			}
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

	public void firmaServidor(
			String processInstanceId,
			Long documentStoreId,
			String motiu,
			boolean permetreSignar) {
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		ArxiuDto arxiuPerFirmar = getArxiuPerDocumentStoreId(
				documentStoreId,
				true,
				(documentStore.getArxiuUuid() == null));
		if (! "pdf".equals(arxiuPerFirmar.getExtensio())) {
			arxiuPerFirmar.getNom();
			// Transforma l'arxiu a PDF
			arxiuPerFirmar = this.converteixPdf(arxiuPerFirmar);							
		}
		byte[] firma = pluginHelper.firmaServidor(
				expedient,
				documentStore,
				arxiuPerFirmar,
				net.conselldemallorca.helium.integracio.plugins.firma.FirmaTipus.PADES,
				(motiu != null) ? motiu : "Firma en servidor HELIUM");

		guardarDocumentFirmat(
				processInstanceId,
				documentStoreId,
				firma,
				true,
				permetreSignar);
	}

	/** Mètode per convertir un arxiu a pdf 
	 * 
	 * @param arxiu
	 * @return
	 */
	private ArxiuDto converteixPdf(ArxiuDto arxiu) {
		ArxiuDto arxiuPdf = new ArxiuDto();
		arxiuPdf.setTipusMime("application/pdf");
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			openOfficeUtils.convertir(
					arxiu.getNom(),
					arxiu.getContingut(),
					"pdf",
					baos);
			arxiuPdf.setNom(
					nomArxiuAmbExtensio(
							arxiu.getNom(),
							"pdf"));
			arxiuPdf.setContingut(baos.toByteArray());
		} catch (Exception ex) {
			throw new SistemaExternConversioDocumentException(
					null,
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					messageHelper.getMessage("error.document.conversio.externa"));
		}
		return arxiuPdf;
	}

	public void guardarDocumentFirmat(
			String processInstanceId,
			Long documentStoreId,
			byte[] signatura,
			boolean isPades,
			boolean permetreSignar) {
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		
		Document document;
		if (expedient.getTipus().isAmbInfoPropia())
			document = findDocumentPerInstanciaProcesICodi(
				expedient.getProcessInstanceId(),
				documentStore.getCodiDocument());
		else
			document =  findDocumentPerInstanciaProcesICodi(
					processInstanceId,
					documentStore.getCodiDocument());
		
		String documentDescripcio;
		if (documentStore.isAdjunt()) {
			documentDescripcio = documentStore.getAdjuntTitol();
		} else {
			documentDescripcio = document.getNom();
		}
		
		documentDescripcio = inArxiu( documentDescripcio, 
									"pdf",
									processInstanceId);
		
		if (documentStore.getArxiuUuid() != null) {
			ArxiuDto arxiuFirmat = new ArxiuDto();
			if (isPades) {
				// PAdES
				String arxiuNom = inArxiu("firma_portafirmes", FilenameUtils.getExtension("firma_portafirmes.pdf"), processInstanceId);
				arxiuFirmat.setNom(arxiuNom);
				arxiuFirmat.setTipusMime("application/pdf");
				arxiuFirmat.setContingut(signatura);
				pluginHelper.arxiuDocumentGuardarPdfFirmat(
						expedient,
						documentStore,
						documentDescripcio,
						arxiuFirmat);
			} else {
				// CAdES
				String firmaNom = inArxiu("firma_portafirmes.csig", FilenameUtils.getExtension("firma_portafirmes.csig"), processInstanceId);
				arxiuFirmat.setNom(firmaNom);
				arxiuFirmat.setTipusMime("application/octet-stream");
				arxiuFirmat.setContingut(signatura);
				pluginHelper.arxiuDocumentGuardarFirmaCadesDetached(
						expedient,
						documentStore,
						documentDescripcio,
						arxiuFirmat);
			}
			es.caib.plugins.arxiu.api.Document documentArxiu = pluginHelper.arxiuDocumentInfo(
					documentStore.getArxiuUuid(),
					null,
					false,
					true);
			actualitzarNtiFirma(documentStore, documentArxiu);
		} else {
			if (expedient.isNtiActiu()) {
				actualitzarNtiFirma(documentStore, null);
			}
			if (documentStore.getReferenciaCustodia() != null) {
				pluginHelper.custodiaEsborrarSignatures(
						documentStore.getReferenciaCustodia(),
						expedient);
			}
			String referenciaCustodia = null;
			try {
				referenciaCustodia = pluginHelper.custodiaAfegirSignatura(
						documentStore.getId(),
						documentStore.getReferenciaFont(),
						documentStore.getArxiuNom(),
						document.getCustodiaCodi(),
						signatura);
			} catch (SistemaExternException ex) {
				// Si dona error perquè el document ja està arxivat l'esborra
				// i el torna a crear.
				logger.info("[PSIGN] Error guardant document a custòdia (" +
						exceptionHelper.getMissageFinalCadenaExcepcions(ex) + ", " +
						exceptionHelper.cercarMissatgeDinsCadenaExcepcions("ERROR_DOCUMENTO_ARCHIVADO", ex) + ") (" +
						"docStoreId=" + documentStore.getId() + ", " +
						"refCustòdia=" + referenciaCustodia + ")");
				if (exceptionHelper.cercarMissatgeDinsCadenaExcepcions("ERROR_DOCUMENTO_ARCHIVADO", ex)) {
					referenciaCustodia = documentStore.getId().toString();
				} else {
					throw ex;
				}
			}
			documentStore.setReferenciaCustodia(referenciaCustodia);
		}
		documentStore.setSignat(permetreSignar);
		crearRegistreSignarDocument(
				expedient.getId(),
				documentStore.getProcessInstanceId(),
				SecurityContextHolder.getContext().getAuthentication().getName(),
				documentDescripcio);
	}



	private ExpedientDocumentDto crearDtoPerDocumentExpedient(
			Document document,
			Long documentStoreId) {
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		if (documentStore != null) {
			return crearDtoPerDocumentExpedient(document, documentStore);
		} else {
			ExpedientDocumentDto dto = new ExpedientDocumentDto();
			dto.setId(documentStoreId);
			dto.setError("No s'ha trobat el documentStore del document (" +
					"documentCodi=" + document.getCodi() + ", " +
					"documentStoreId=" + documentStoreId + ")");
			return dto;
		}
	}

	private ExpedientDocumentDto crearDtoPerDocumentExpedient(
			Document document,
			DocumentStore documentStore) {
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
		dto.setNotificable(document.isNotificable());
		dto.setSignat(documentStore.isSignat());
		if (documentStore.isSignat()) {
			if (documentStore.getArxiuUuid() == null) {
				dto.setSignaturaUrlVerificacio(
						pluginHelper.custodiaObtenirUrlComprovacioSignatura(
								documentStore.getId().toString()));
			} else {
				dto.setSignaturaUrlVerificacio(
						getPropertyArxiuVerificacioBaseUrl() + documentStore.getNtiCsv());
			}
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
		
		return dto;
	}

	private ExpedientDocumentDto crearDtoPerAdjuntExpedient(
			String adjuntId,
			Long documentStoreId) {
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		if (documentStore != null) {
			return crearDtoPerAdjuntExpedient(adjuntId, documentStore);
		} else {
			ExpedientDocumentDto dto = new ExpedientDocumentDto();
			dto.setId(documentStoreId);
			dto.setError("No s'ha trobat el documentStore del document (" +
					"adjuntId=" + adjuntId + ", " +
					"documentStoreId=" + documentStoreId + ")");
			return dto;
		}
	}

	private ExpedientDocumentDto crearDtoPerAdjuntExpedient(
			String adjuntId,
			DocumentStore documentStore) {
		ExpedientDocumentDto dto = new ExpedientDocumentDto();
		dto.setId(documentStore.getId());
		dto.setDataCreacio(documentStore.getDataCreacio());
		dto.setDataModificacio(documentStore.getDataModificacio());
		dto.setDataDocument(documentStore.getDataDocument());
		dto.setArxiuNom(calcularArxiuNom(documentStore, false));
		dto.setProcessInstanceId(documentStore.getProcessInstanceId());
		dto.setAdjunt(true);
		dto.setAdjuntId(adjuntId);
		dto.setAdjuntTitol(documentStore.getAdjuntTitol());
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
		dto.setSignat(documentStore.isSignat());
		if (documentStore.isSignat()) {
			if (documentStore.getArxiuUuid() == null) {
				dto.setSignaturaUrlVerificacio(
						pluginHelper.custodiaObtenirUrlComprovacioSignatura(
								documentStore.getId().toString()));
			} else {
				dto.setSignaturaUrlVerificacio(
						getPropertyArxiuVerificacioBaseUrl() + documentStore.getNtiCsv());
			}
		}
		return dto;
	}

	private TascaDocumentDto toTascaDocumentDto(
			JbpmTask task,
			Document document, 
			boolean required, 
			boolean readonly) {
		TascaDocumentDto dto = new TascaDocumentDto();
		String varCodi = getVarPerDocumentCodi(
				document.getCodi(),
				document.isAdjuntarAuto());
		dto.setId(document.getId());
		dto.setVarCodi(varCodi);
		dto.setDocumentCodi(document.getCodi());
		dto.setDocumentNom(document.getNom());
		dto.setRequired(required);
		dto.setReadOnly(readonly);
		dto.setPlantilla(document.isPlantilla());
		dto.setExtensionsPermeses(document.getExtensionsPermeses());
		dto.setAdjuntarAuto(document.isAdjuntarAuto());
		dto.setArxiuNom(document.getArxiuNom());
		dto.setArxiuContingutDefinit(document.getArxiuContingut() != null && document.getArxiuContingut().length > 0);
		Long documentStoreId;
		documentStoreId = getDocumentStoreIdDeVariableJbpm(
				String.valueOf(task.getTask().getId()), 
				readonly ? task.getProcessInstanceId() : null, // Si és readonly no es troba a la tasca però sí es pot llegir del procés 
				document.getCodi());
		if (documentStoreId != null) {
			DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
			if (documentStore != null) {
				dto.setDocumentStoreId(documentStoreId);
				dto.setArxiuNom(documentStore.getArxiuNom());
				dto.setDataCreacio(documentStore.getDataCreacio());
				dto.setDataModificacio(documentStore.getDataModificacio());
				dto.setDataDocument(documentStore.getDataDocument());
				dto.setSignat(documentStore.isSignat());
				dto.setRegistrat(documentStore.isRegistrat());
				if (documentStore.isSignat()) {
					if (documentStore.getArxiuUuid() == null) {
						dto.setUrlVerificacioCustodia(
								pluginHelper.custodiaObtenirUrlComprovacioSignatura(
										documentStoreId.toString()));
					} else {
						dto.setSignaturaUrlVerificacio(
								getPropertyArxiuVerificacioBaseUrl() + documentStore.getNtiCsv());
					}
				}
				try {
					dto.setTokenSignatura(getDocumentTokenUtils().xifrarToken(documentStoreId.toString()));
				} catch (Exception ex) {
					logger.error("No s'ha pogut generar el token pel document " + documentStoreId, ex);
				}
				if (documentStore.isRegistrat()) {
					dto.setRegistreData(documentStore.getRegistreData());
					dto.setRegistreNumero(documentStore.getRegistreNumero());
					dto.setRegistreOficinaCodi(documentStore.getRegistreOficinaCodi());
					dto.setRegistreOficinaNom(documentStore.getRegistreOficinaNom());
					dto.setRegistreEntrada(documentStore.isRegistreEntrada());
				}
			}
		}
		return dto;
	}

	private ExpedientDocumentDto findOnePerInstanciaProces(
			String processInstanceId,
			DocumentStore documentStore) {
		if (documentStore == null) {
			return null;
		}
		if (!documentStore.isAdjunt()) {
			Document document = findDocumentPerInstanciaProcesICodi(
					processInstanceId,
					documentStore.getCodiDocument());
			if (document != null) {
				return crearDtoPerDocumentExpedient(
								document,
								documentStore);
			} else {
				throw new NoTrobatException(
						Document.class,
						"(codi=" + documentStore.getCodiDocument() + ")");
			}
		} else {
			return crearDtoPerAdjuntExpedient(
					getAdjuntIdDeVariableJbpm(documentStore.getJbpmVariable()),
					documentStore);
		}
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
	private String calcularArxiuNomOriginal(
			DocumentStore documentStore) {
		String nomOriginal;
		if (documentStore.isSignat() && isSignaturaFileAttached()) {
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

	private String getDocumentCodiDeVariableJbpm(String varName) {
		return varName.substring(JbpmVars.PREFIX_DOCUMENT.length());
	}
	private String getAdjuntIdDeVariableJbpm(String varName) {
		return varName.substring(JbpmVars.PREFIX_ADJUNT.length());
	}

	private void filtrarVariablesAmbDocuments(Map<String, Object> variables) {
		if (variables != null) {
			variables.remove(JbpmVars.VAR_TASCA_VALIDADA);
			variables.remove(JbpmVars.VAR_TASCA_DELEGACIO);
			List<String> codisEsborrar = new ArrayList<String>();
			for (String codi: variables.keySet()) {
				if (!codi.startsWith(JbpmVars.PREFIX_DOCUMENT) && !codi.startsWith(JbpmVars.PREFIX_ADJUNT)) {
					codisEsborrar.add(codi);
				}
			}
			for (String codi: codisEsborrar)
				variables.remove(codi);
		}
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

	private String getUrlComprovacioSignatura(Long documentStoreId) throws Exception {
		String urlCustodia = pluginHelper.custodiaObtenirUrlComprovacioSignatura(documentStoreId.toString());
		if (urlCustodia != null) {
			return urlCustodia;
		} else {
			String baseUrl = (String)GlobalProperties.getInstance().get("app.base.verificacio.url");
			if (baseUrl == null)
				baseUrl = (String)GlobalProperties.getInstance().get("app.base.url");
			String token = getDocumentTokenUtils().xifrarToken(documentStoreId.toString());
			return baseUrl + "/signatura/verificarExtern.html?token=" + token;
		}
	}

	private String getExtensioArxiuSignat() {
		return (String)GlobalProperties.getInstance().get("app.conversio.signatura.extension");
	}
	private String getExtensioArxiuRegistrat() {
		return (String)GlobalProperties.getInstance().get("app.conversio.registre.extension");
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

	public PdfUtils getPdfUtils() {
		if (pdfUtils == null)
			pdfUtils = new PdfUtils();
		return pdfUtils;
	}
	private DocumentTokenUtils getDocumentTokenUtils() {
		if (documentTokenUtils == null)
			documentTokenUtils = new DocumentTokenUtils(
					(String)GlobalProperties.getInstance().get("app.encriptacio.clau"));
		return documentTokenUtils;
	}

	private String getUrlComprovacioSignatura(Long documentStoreId, String token) {
		String urlCustodia = pluginHelper.custodiaObtenirUrlComprovacioSignatura(documentStoreId.toString());
		if (urlCustodia != null) {
			return urlCustodia;
		} else {
			String baseUrl = (String)GlobalProperties.getInstance().get("app.base.verificacio.url");
			if (baseUrl == null)
				baseUrl = (String)GlobalProperties.getInstance().get("app.base.url");
			return baseUrl + "/signatura/verificarExtern.html?token=" + token;
		}
	}

	private byte[] getContingutDocumentAmbFont(DocumentStore document) {
		if (document.getFont().equals(DocumentFont.INTERNA))
			return document.getArxiuContingut();
		else
			return pluginHelper.gestioDocumentalObtenirDocument(
							document.getReferenciaFont());
	}
	
	private Long getDocumentStoreIdDeVariableJbpm(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi) {
		Object value = null;
		if (taskInstanceId != null) {
			value = jbpmHelper.getTaskInstanceVariable(
					taskInstanceId,
					getVarPerDocumentCodi(documentCodi, false));
		}
		if (value == null && processInstanceId != null) {
			value = jbpmHelper.getProcessInstanceVariable(
					processInstanceId,
					getVarPerDocumentCodi(documentCodi, false));
		}
		return (Long)value;
	}

	private String nomArxiuAmbExtensio(String fileName, String extensio) {
		if (extensio == null || extensio.length() == 0)
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
		String actiuConversio = (String)GlobalProperties.getInstance().get("app.conversio.actiu");
		if (!"true".equalsIgnoreCase(actiuConversio))
			return false;
		String actiuConversioVista = (String)GlobalProperties.getInstance().get("app.conversio.vista.actiu");
		if (actiuConversioVista == null)
			actiuConversioVista = (String)GlobalProperties.getInstance().get("app.conversio.gentasca.actiu");
		return "true".equalsIgnoreCase(actiuConversioVista);
	}

	private String getExtensioVista(Document document) {
		String extensioVista = null;
		if (isActiuConversioVista()) {
			if (document.getConvertirExtensio() != null && document.getConvertirExtensio().length() > 0) {
				extensioVista = document.getConvertirExtensio();
			} else {
				extensioVista = (String)GlobalProperties.getInstance().get("app.conversio.vista.extension");
				if (extensioVista == null)
					extensioVista = (String)GlobalProperties.getInstance().get("app.conversio.gentasca.extension");
			}
		}
		return extensioVista;
	}

	/** Mètode per realitzar les accions posteriors a la creació d'un document
	 * 
	 * @param documentStore
	 * @param taskInstanceId
	 * @param processInstanceId
	 * @param arxiuNom
	 * @param arxiuContingut
	 * @param arxiuUuid En cas que l'arxiu ja existeixi a l'Arxiu
	 * @param arxiuContentType
	 * @param ambFirma
	 * @param firmaSeparada
	 * @param firmaContingut
	 * @param ntiOrigen
	 * @param ntiEstadoElaboracion
	 * @param ntiTipoDocumental
	 * @param ntiIdDocumentoOrigen
	 */
	private void postProcessarDocument(
			DocumentStore documentStore,
			String taskInstanceId,
			String processInstanceId,
			String arxiuNom,
			byte[] arxiuContingut,
			String arxiuUuid,
			String arxiuContentType,
			boolean ambFirma,
			boolean firmaSeparada,
			byte[] firmaContingut,
			NtiOrigenEnumDto ntiOrigen,
			NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			NtiTipoDocumentalEnumDto ntiTipoDocumental,
			String ntiIdDocumentoOrigen) {
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		if (arxiuNom != null && !arxiuNom.equals("")) {
			documentStore.setArxiuNom(arxiuNom);
		}
		// Actualitza les metadades NTI
		Document document = findDocumentPerInstanciaProcesICodi(
				processInstanceId,
				documentStore.getCodiDocument());
		if (expedient.isNtiActiu()) {
			actualizarMetadadesNti(
					expedient,
					document,
					documentStore,
					ntiOrigen,
					ntiEstadoElaboracion,
					ntiTipoDocumental,
					ntiIdDocumentoOrigen);
		}
		List<ArxiuFirmaDto> firmes = null;
		es.caib.plugins.arxiu.api.Document documentArxiu = null;
		if (ambFirma) {
			// Obté les firmes del plugin de validació a partir del contingut
			if (arxiuUuid == null) {
				// Valida firmes
				firmes = validaFirmaDocument(
						documentStore, 
						arxiuContingut,
						firmaContingut,
						arxiuContentType);
			} else {
				// Obté les firmes de l'Arxiu
				documentArxiu = pluginHelper.arxiuDocumentInfo(
						arxiuUuid,
						null,
						false,
						true);
				firmes =PluginHelper.toArxiusFirmesDto(documentArxiu.getFirmes());
			}
		}
		String documentDescripcio = documentStore.isAdjunt() ? documentStore.getAdjuntTitol() : document.getNom();
		if (expedient.isArxiuActiu()) {
			// Document integrat amb l'Arxiu
			if (arxiuUuid == null) {
				// Actualitza el document a dins l'arxiu
				ArxiuDto arxiu = new ArxiuDto(
						arxiuNom,
						arxiuContingut,
						arxiuContentType);
				// Canvia la descripció per no coincidir amb cap document amb el mateix nom a l'Arxiu
				documentDescripcio = inArxiu(documentDescripcio ,FilenameUtils.getExtension(arxiuNom), processInstanceId);
				
				ContingutArxiu contingutArxiu = pluginHelper.arxiuDocumentCrearActualitzar(
						expedient,
						documentDescripcio,
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
			} else {
				documentStore.setArxiuUuid(arxiuUuid);
				if (documentArxiu == null)
					documentArxiu = pluginHelper.arxiuDocumentInfo(
							arxiuUuid,
							null,
							false,
							true);
				documentStore.setNtiIdentificador(documentArxiu.getMetadades().getIdentificador());
			}
			if(ambFirma) {
				this.actualitzarNtiFirma(documentStore, documentArxiu);
			}
			documentStore.setSignat(firmes != null && firmes.size() > 0);
		} else {
			// No integrat amb l'Arxiu
			
			// Valida que si està firmat llavors tingui codi de custòdia per poder-lo guardar
			String codiCustodia = document != null ? document.getCustodiaCodi() : null;
			if (ambFirma && codiCustodia == null)
				throw new ValidacioException("No es pot guardar un document firmat a custòdia sense codi de custòdia");
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
			if (ambFirma) {
				// Guarda la firma a custòdia
				if (expedient.isNtiActiu()) {
					actualitzarNtiFirma(documentStore, null);
				}
				if (documentStore.getReferenciaCustodia() != null) {
					pluginHelper.custodiaEsborrarSignatures(documentStore.getReferenciaCustodia(), expedient);
				}
				String referenciaCustodia = null;
				try {
					referenciaCustodia = pluginHelper.custodiaAfegirSignatura(
							documentStore.getId(), 
							documentStore.getReferenciaFont(), 
							arxiuNom,
							document.getCustodiaCodi(),
							firmes.get(0).getContingut());
							
				} catch (Exception ex) {
					logger.info(">>> [PSIGN] Processant error custòdia (" + exceptionHelper.getMissageFinalCadenaExcepcions(ex) + ", " + exceptionHelper.cercarMissatgeDinsCadenaExcepcions("ERROR_DOCUMENTO_ARCHIVADO", ex) + ") (docStoreId=" + documentStore.getId() + ", refCustòdia=" + referenciaCustodia + ")");
					if (exceptionHelper.cercarMissatgeDinsCadenaExcepcions("ERROR_DOCUMENTO_ARCHIVADO", ex)) {
						referenciaCustodia = documentStore.getId().toString();
					} else {
						throw new RuntimeException(ex);
					}
				}
				documentStore.setReferenciaCustodia(referenciaCustodia);
				documentStore.setSignat(true);
			}
		}
		// Guarda la referència al nou document a dins el jBPM
		if (taskInstanceId != null) {
			jbpmHelper.setTaskInstanceVariable(
					taskInstanceId,
					documentStore.getJbpmVariable(),
					documentStore.getId());
		} else {
			jbpmHelper.setProcessInstanceVariable(
					processInstanceId,
					documentStore.getJbpmVariable(),
					documentStore.getId());
		}
	}
	
	/** Valida les firmes amb el plugin de validació de firmes */
	private List<ArxiuFirmaDto> validaFirmaDocument(
			DocumentStore documentStore,
			byte[] contingut,
			byte[] contingutFirma,
			String contentType) {
		logger.debug("Recuperar la informació de les firmes amb el plugin ValidateSignature ("
				+ "documentStore" + documentStore.getId() + ")");
		
		List<ArxiuFirmaDto> firmes = pluginHelper.validaSignaturaObtenirFirmes(
				documentStore,
				contingut,
				(contingutFirma != null && contingutFirma.length > 0) ? contingutFirma : null,
				contentType);
		documentStore.setSignat(true);
		return firmes;
	}
	
	private void actualizarMetadadesNti(
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
		documentStore.setNtiOrgano(expedient.getNtiOrgano());
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

	public String getContentType(String arxiuNom) {
		String fileNameDetect = tika.detect(arxiuNom);
        if (!fileNameDetect.equals(MimeTypes.OCTET_STREAM)) {
            return fileNameDetect;
        }
        String fileContentDetect = tika.detect(arxiuNom);
        if (!fileContentDetect.equals(MimeTypes.OCTET_STREAM)) {
            return fileContentDetect;
        }
        return MimeTypes.OCTET_STREAM;
	}

	private String getPropertyNtiCsvDef() {
		return GlobalProperties.getInstance().getProperty(
				"app.nti.csv.definicio");
	}
	private String getPropertyCustodiaVerificacioBaseUrl() {
		return GlobalProperties.getInstance().getProperty(
				"app.custodia.plugin.caib.verificacio.baseurl");
	}
	private String getPropertyArxiuVerificacioBaseUrl() {
		return GlobalProperties.getInstance().getProperty(
				"app.arxiu.verificacio.baseurl");
	}

	private Registre crearRegistreSignarDocument(
			Long expedientId,
			String processInstanceId,
			String responsableCodi,
			String documentCodi) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.INSTANCIA_PROCES,
				processInstanceId);
		registre.setMissatge("Signatura del document '" + documentCodi + "'");
		return registreRepository.save(registre);
	}
	
	private String inArxiu(String arxiuNom, String extensio, String processInstanceId){
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		if(expedient.isArxiuActiu()) {
			List<ContingutArxiu> continguts = pluginHelper.arxiuExpedientInfo(expedient.getArxiuUuid()).getContinguts();
			int ocurrences = 0;
			if(continguts != null) {
				List<String> noms = new ArrayList<String>();
				for(ContingutArxiu contingut : continguts) {
					noms.add(contingut.getNom());
				}
				String nName = new String(arxiuNom);
				//Si no troba el fitxer amb extensió el cerca sense
				if (noms.indexOf(nName + "." + extensio) == -1) {
					while(noms.indexOf(nName) >= 0) {
						ocurrences ++;
						nName = arxiuNom + " (" + ocurrences + ")";
					}
				} else {
					while(noms.indexOf(nName + "." + extensio) >= 0) {
						ocurrences ++;
						nName = arxiuNom + " (" + ocurrences + ")";
					}
				}
				return nName;
			}
		}
		return arxiuNom;
	}

	private static final Log logger = LogFactory.getLog(DocumentHelperV3.class);

}
