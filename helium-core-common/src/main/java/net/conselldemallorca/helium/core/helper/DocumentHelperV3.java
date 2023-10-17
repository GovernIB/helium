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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypes;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import es.caib.distribucio.core.api.service.ws.backoffice.NtiEstadoElaboracion;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.DocumentEstat;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaPerfil;
import es.caib.plugins.arxiu.api.FirmaTipus;
import es.caib.plugins.arxiu.caib.ArxiuConversioHelper;
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
import net.conselldemallorca.helium.integracio.plugins.firma.FirmaResposta;
import net.conselldemallorca.helium.integracio.plugins.signatura.RespostaValidacioSignatura;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessDefinition;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioAnnexEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaPerfilEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentStoreDto;
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
			boolean ambSegellSignatura,
			String versio) {
		ArxiuDto resposta = new ArxiuDto();
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		// Obtenim el contingut de l'arxiu
		byte[] arxiuOrigenContingut = null;
		if (documentStore.getArxiuUuid() != null) {

			// #1697 Es revisa que no retorni contingut null i es reintenta
			es.caib.plugins.arxiu.api.Document documentArxiu = null;
			int intents = 0;
			do {
				documentArxiu = pluginHelper.arxiuDocumentInfo(
					documentStore.getArxiuUuid(),
					versio,
					true,
					documentStore.isSignat());
				if (documentArxiu == null || documentArxiu.getContingut() == null) {
					logger.warn("La consulta del contingut pel document amb id=" + documentStore.getId() + 
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
			}
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
				    if (ambSegellSignatura && documentStore.getReferenciaCustodia() != null) {
				    	urlComprovacioSignatura = getUrlComprovacioSignatura(documentStore.getId(), documentStore.getReferenciaCustodia());
				    }
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
							MonitorIntegracioHelper.INTCODI_CONVDOC , //sistemaExtern
							"No s'ha pogut generar la vista pel document (id=" + documentStoreId + ", processInstanceId=" + documentStore.getProcessInstanceId() + ")", 
							ex);
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
			mapAnotacions.put(annex.getId(), annex);
		
		// Cosulta els documents notificats de l'expedient per marcar-los com a notificats
		List<Long> documentsNotificats = this.getDocumentsNotificats(expedient);
				
		// Consulta els documents de l'instància de procés
		Map<String, Object> varsInstanciaProces = jbpmHelper.getProcessInstanceVariables(processInstanceId);
		if (varsInstanciaProces != null) {
			filtrarVariablesAmbDocuments(varsInstanciaProces);
			for (String var: varsInstanciaProces.keySet()) {
				Long documentStoreId = (Long)varsInstanciaProces.get(var);
				if (documentStoreId != null) {
					ExpedientDocumentDto ed = null;
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
							ed = crearDtoPerDocumentExpedient(
									document,
									documentStoreId);
							ed.setAnotacioId(null); // De moment només arriben per anotació els annexos
						} else {
							ExpedientDocumentDto dto = new ExpedientDocumentDto();
							dto.setId(documentStoreId);
							dto.setProcessInstanceId(processInstanceId);
							dto.setError("No s'ha trobat el document de la definició de procés (" +
										"documentCodi=" + documentCodi + ")");
							ed = dto;
						}
					} else if (var.startsWith(JbpmVars.PREFIX_ADJUNT)) {
						// Afegeix l'adjunt
						ed = crearDtoPerAdjuntExpedient(
								getAdjuntIdDeVariableJbpm(var),
								documentStoreId);
					}
					// Afegeix informació de l'annex relacionat amb el document
					if (ed != null && mapAnotacions.containsKey(ed.getAnotacioAnnexId())) {
						AnotacioAnnex annex = mapAnotacions.get(ed.getAnotacioAnnexId());
						ed.setAnotacioId(annex.getAnotacio().getId());
						ed.setAnotacioIdentificador(annex.getAnotacio().getIdentificador());
						ed.setAnotacioAnnexTitol(annex.getTitol());
					}
					// Informació de les notificacions
					ed.setNotificat(documentsNotificats.contains(documentStoreId));
					
					resposta.add(ed);
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
	
	public DocumentStore findById(
			Long documentStoreId) {
		DocumentStore documentStore = null;
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
					false,
					true,
					false, // Per notificar
					false);
			
			// Guarda la firma asociada al document, es dona per fet que és un PDF
			guardarDocumentFirmat(
					documentStore.getProcessInstanceId(),
					documentStore.getId(),
					"document_firmat.pdf",
					"application/pdf",
					FirmaTipus.PADES.name(),
					"TF06",
					FirmaPerfil.EPES.name(),
					signatura,
					null);


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
				ntiIdDocumentoOrigen,
				true,
				null,
				null, //annexId
				null);
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
			String ntiIdDocumentoOrigen,
			boolean documentValid,
			String documentError) {
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
				false,	// firma separada
				null,	// firma contingut
				ntiOrigen,
				ntiEstadoElaboracion,
				ntiTipoDocumental,
				ntiIdDocumentoOrigen,
				documentValid,
				documentError,
				null, // annexId
				null);
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
			String ntiIdDocumentoOrigen, 
			boolean documentValid,
			String documentError,
			Long annexId,
			List<ExpedientDocumentDto> annexosPerNotificar) {
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
		documentStore.setAnnexId(annexId);
		if(annexosPerNotificar!=null && !annexosPerNotificar.isEmpty()) {
			List<DocumentStore> documentsContinguts = new ArrayList<DocumentStore>();
			List<DocumentStore> zips = new ArrayList<DocumentStore>();
			zips.add(documentStore); //aquesta llista li setejarem a cada documentStore contingut al zip
			for(ExpedientDocumentDto exp : annexosPerNotificar) {
				DocumentStore documentStoreCont = documentStoreRepository.findOne(exp.getId());
				documentStoreCont.setZips(zips);
				documentsContinguts.add(documentStoreCont);
			}
			documentStore.setContinguts(documentsContinguts);
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
		documentStoreCreat.setDocumentValid(documentValid);
		documentStoreCreat.setDocumentError(documentError);
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
			
		return actualitzarDocument(
				documentStoreId,
				null,
				processInstanceId,
				documentData,
				adjuntTitol,
				arxiuNom,
				arxiuContingut,
				this.getContentType(arxiuNom),
				ambFirma,
				firmaSeparada,
				null,
				ntiOrigen,
				ntiEstadoElaboracion,
				ntiTipoDocumental,
				ntiIdDocumentoOrigen,
				true,	// documetn vàlid
				null,	// error
				null,	// annexId
				null);	// annexUuid	
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
			String ntiIdDocumentoOrigen,
			boolean documentValid,
			String documentError,
			Long annexId,
			String annexUuid) {
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		if (documentStore == null) {
			throw new NoTrobatException(
					DocumentStore.class, 
					documentStoreId);
		}
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		// Comprova la llista d'enviaments
		List<DocumentNotificacio> enviaments = documentNotificacioRepository.findByExpedientAndDocumentId(expedient, documentStoreId);
		if (enviaments != null && enviaments.size() > 0) {
			throw new ValidacioException("No es pot modificar un document amb " + enviaments.size() + " enviaments");
		}
		// Comprova si fa referència a un annex i si aquest está mogut o no 
		if (expedient.isArxiuActiu() && documentStore.getAnnexId() != null) {
			AnotacioAnnex annex = anotacioAnnexRepository.findOne(documentStore.getAnnexId());
			if (annex != null && !AnotacioAnnexEstatEnumDto.MOGUT.equals(annex.getEstat())) {
				throw new ValidacioException("No es pot modificar un document que faci referència a un annex que encara no s'ha mogut a l'Arxiu. " + 
												"S'ha de reprocessar el traspàs o esborrar-lo i ajuntar-ne un altre.");
			}
		}
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
		documentStore.setDocumentValid(documentValid);
		documentStore.setDocumentError(documentError);
		String arxiuUuid = null;
		if (annexId != null) {
			documentStore.setAnnexId(annexId);
			// S'actualitzarà el document amb el nou uuid, l'existent es perdrà en tancar l'expedient
			arxiuUuid = annexUuid;
		}
		postProcessarDocument(
				documentStore,
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
					ntiIdDocumentoOrigen,
					true,
					null,
					null, // annexId
					null);
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
			boolean esborrarDocument = true;
			Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
			List<DocumentNotificacio> enviaments = documentNotificacioRepository.findByExpedientAndDocumentId(expedient, documentStoreId);
			if (enviaments != null && enviaments.size() > 0) {
				// si té enviaments no s'esborra el document per a que es pugui consultar des de la notificació.
				esborrarDocument = false;
			}
			List<Long> documentsNotificats = this.getDocumentsNotificats(expedient);
			if (documentsNotificats.contains(documentStore.getId())) {
				// si el document s'ha notificat no s'esborra el document per a que es pugui continuar consultant.
				esborrarDocument = false;
			}
			
			if (expedient.isArxiuActiu()) {
				if (documentStore.isSignat()) {
					logger.info("Es procedeix a esborrar d'HELIUM el document firmat a l'Arxiu (expedient= " + expedient.getNumero() + ", tipus=" + expedient.getTipus().getCodi() + 
							", entorn=" + expedient.getTipus().getEntorn().getCodi() + ", document= " + documentStoreId  
							+ (documentStore.isAdjunt() ? documentStore.getAdjuntTitol() : documentStore.getCodiDocument() ) + ")");
				} else {
					if (esborrarDocument ) {
						// No esborra el document de l'Arxiu si té un annex associat
						if (documentStore.getAnnexId() == null) {
							pluginHelper.arxiuDocumentEsborrar(documentStore.getArxiuUuid());
						}
					}
				}
			} else {
				if (documentStore.isSignat()) {
					if (pluginHelper.custodiaIsPluginActiu()) {
						this.programarCustodiaEsborrarSignatures(
								documentStore.getReferenciaCustodia(), 
								expedientHelper.findExpedientByProcessInstanceId(processInstanceId));
					}
				}
				if (esborrarDocument && documentStore.getFont().equals(DocumentFont.ALFRESCO)) {
					pluginHelper.gestioDocumentalDeleteDocument(
							documentStore.getReferenciaFont(),
							expedientHelper.findExpedientByProcessInstanceId(processInstanceId));
				}
				if (processInstanceId != null) {
					List<Portasignatures> psignaPendents = portasignaturesRepository.findByProcessInstanceIdAndEstatNotIn(
							processInstanceId,
							TipusEstat.getPendents());
					for (Portasignatures psigna: psignaPendents) {
						if (psigna.getDocumentStoreId().longValue() == documentStore.getId().longValue()) {
							psigna.setEstat(TipusEstat.ESBORRAT);
							portasignaturesRepository.save(psigna);
						}
					}
				}
			}
			if (esborrarDocument)
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

	/** Mètode per consultar els documents notificats tant directament com dins dels .zip que poden contenir altres documents.
	 * 
	 * @param expedient
	 * @return
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

	/** Programa la petició per esborrar el document de custòdia quan el commit acabi i vagi bé. */
	@Transactional
	private void programarCustodiaEsborrarSignatures(String referenciaCustodia, Expedient expedient) {
		logger.debug("Programant l'esborrat de custòdia del document amb referència " + referenciaCustodia + " de l'expedient " + expedient.getNumeroIdentificador());
		EsborrarDocumentCustodiaHandler esborrarDocumentCustodiaHandler = new EsborrarDocumentCustodiaHandler(referenciaCustodia, expedient);
		TransactionSynchronizationManager.registerSynchronization(esborrarDocumentCustodiaHandler);
	}

	/** Classe que implementa la sincronització de transacció pes esborrar un document firmat de Custòdia només en el cas que la transacció
	 * hagi finalitzat correctament. D'aquesta forma no s'esborra el documetn si no s'ha acabat la transacció correctament.
	 */
	public class EsborrarDocumentCustodiaHandler implements TransactionSynchronization {

		private String referenciaCustodia;
		private Expedient expedient;

		public EsborrarDocumentCustodiaHandler(String referenciaCustodia, Expedient expedient) {
			this.referenciaCustodia = referenciaCustodia;
			this.expedient = expedient;
		}

		/** Mètode que s'executa després que s'hagi guardat correctament a BBDD i per tants els temporals es poden guardar correctament. */
		@Override
		@Transactional
		public void afterCommit() {
			logger.debug("Esborrant el document " + referenciaCustodia + " de l'expedient " + expedient.getIdentificador() + " de custòdia");
			pluginHelper.custodiaEsborrarSignatures(
			referenciaCustodia, 
			expedient);
		}

		@Override
		public void suspend() {}
		@Override
		public void resume() {}
		@Override
		public void flush() {}
		@Override
		public void beforeCommit(boolean readOnly) {}
		@Override
		public void beforeCompletion() {}
		@Override
		public void afterCompletion(int status) {}
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
							messageHelper.getMessage("error.document.conversio.externa"));
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
				dto.setNtiOrigen(documentStore.getNtiOrigen());
				dto.setNtiEstadoElaboracion(documentStore.getNtiEstadoElaboracion());
				dto.setNtiTipoDocumental(documentStore.getNtiTipoDocumental());
//				dto.setArxiuContingut(documentStore.getArxiuContingut());
				try {
					dto.setTokenSignatura(getDocumentTokenUtils().xifrarToken(documentStoreId.toString()));
				} catch (Exception ex) {
					logger.error("No s'ha pogut generar el token pel document " + documentStoreId, ex);
				}
				if (documentStore.isSignat()) {
					if (documentStore.getArxiuUuid() == null) {
						dto.setUrlVerificacioCustodia(
								pluginHelper.custodiaObtenirUrlComprovacioSignatura(
										documentStore.getReferenciaCustodia()));
						dto.setSignaturaUrlVerificacio(
								dto.getUrlVerificacioCustodia());
					} else {
						dto.setSignaturaUrlVerificacio(
								getPropertyArxiuVerificacioBaseUrl() + documentStore.getNtiCsv());
					}
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
						dto.setGenerarNomesTasca(doc.isGenerarNomesTasca());
						dto.setPortafirmesFluxId(doc.getPortafirmesFluxId());
					}
				}
				if (documentStore.getArxiuUuid() != null) {
					// Si el document està guardat a l'arxiu no és necessari estampar-lo abans
					// de firmar i una vegada firmat hem de mostrar la versió imprimible.
					boolean ambContingut = (ambContingutOriginal 
											|| ambContingutSignat 
											|| ambContingutVista);
					// Si s'ha de notificar llavors es passa el contingut si no té uuid d'arxiu o no és un PDF
					ambContingut = ambContingut && 
								   (!(perNotificar && documentStore.isSignat())
										   || documentStore.getArxiuUuid() == null
										   || ! documentStore.getArxiuNom().toLowerCase().endsWith(".pdf"));
					es.caib.plugins.arxiu.api.Document documentArxiu = pluginHelper.arxiuDocumentInfo(
							documentStore.getArxiuUuid(),
							null,
							ambContingut,
							documentStore.isSignat());
					String arxiuNom = documentStore.getArxiuNom();
					
					byte[] arxiuContingut = null;
					if (ambContingut && documentArxiu.getContingut()!= null)
						arxiuContingut = documentArxiu.getContingut().getContingut();
					
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
											MonitorIntegracioHelper.INTCODI_CONVDOC,
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
								if (ambSegellSignatura && !documentStore.isSignat() && documentStore.getReferenciaCustodia() == null) {
									documentStore.setReferenciaCustodia(documentStore.getId() + "_" + new Date().getTime());
								}
								getPdfUtils().estampar(
										arxiuOrigenNom,
										arxiuOrigenContingut,
										(ambSegellSignatura) ? !documentStore.isSignat() : false,
										(ambSegellSignatura) ? getUrlComprovacioSignatura(documentStore.getReferenciaCustodia(), dto.getTokenSignatura()): null,
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
										MonitorIntegracioHelper.INTCODI_CONVDOC,
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
					dto.setArxiuUuid(dto.getArxiuUuid());
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
			String urlCustodia = pluginHelper.custodiaObtenirUrlComprovacioSignatura(
					documentStore.getReferenciaCustodia());
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
			byte[] arxiuContingut) {
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		ArxiuDto arxiuPerFirmar = getArxiuPerDocumentStoreId(
				documentStoreId,
				true,
				(documentStore.getArxiuUuid() == null),
				null);
		if (! "pdf".equals(arxiuPerFirmar.getExtensio()) &&  !"zip".equals(arxiuPerFirmar.getExtensio())) {
			// Transforma l'arxiu a PDF
			arxiuPerFirmar = this.converteixPdf(arxiuPerFirmar);							
		}
		
		FirmaResposta firma = pluginHelper.firmaServidor(
				expedient,
				documentStore,
				arxiuPerFirmar,
				net.conselldemallorca.helium.integracio.plugins.firma.FirmaTipus.PADES,
				(motiu != null) ? motiu : "Firma en servidor HELIUM");

		if (StringUtils.isEmpty(firma.getTipusFirmaEni()) 
				|| StringUtils.isEmpty(firma.getTipusFirmaEni())) {
			logger.warn("El tipus o perfil de firma s'ha retornat buit i això pot provocar error guardant a l'Arxiu [tipus: " + 
					firma.getTipusFirmaEni() + ", perfil: " + firma.getPerfilFirmaEni() + "]");
			if ("cades".equals(StringUtils.lowerCase(firma.getTipusFirma()))) {
				logger.warn("Fixant el tipus de firma a TF04 i perfil BES");
				if (StringUtils.isEmpty(firma.getTipusFirmaEni()))
					firma.setTipusFirmaEni("TF04");
				if (StringUtils.isEmpty(firma.getPerfilFirmaEni()))
					firma.setPerfilFirmaEni("BES");
			} else if ("pades".equals(StringUtils.lowerCase(firma.getTipusFirma()))) {
				logger.warn("Fixant el tipus de firma a TF06 i perfil BES");
				if (StringUtils.isEmpty(firma.getTipusFirmaEni()))
					firma.setTipusFirmaEni("TF06");
				if (StringUtils.isEmpty(firma.getPerfilFirmaEni()))
					firma.setPerfilFirmaEni("BES");
			}
		}
		
		String perfil = mapToPerfilFirmaArxiu(firma.getPerfilFirmaEni());
		
		guardarDocumentFirmat(
				processInstanceId,
				documentStoreId,
				firma.getNom(),
				firma.getMime(),
				firma.getTipusFirma(),
				firma.getTipusFirmaEni(),
				perfil,
				firma.getContingut(),
				arxiuContingut);
	}

	
	/** Map amb el mapeig dels perfils de firma cap als tipus admesos per l'Arxiu. */
	private static Map<String, String> mapPerfilsFirma = new HashMap<String, String>();
	static {
		mapPerfilsFirma.put("AdES-BES", "BES");
		mapPerfilsFirma.put("AdES-EPES", "EPES");
		mapPerfilsFirma.put("AdES-T", "T");
		mapPerfilsFirma.put("AdES-C", "C");
		mapPerfilsFirma.put("AdES-X", "X");
		mapPerfilsFirma.put("AdES-X1", "X");
		mapPerfilsFirma.put("AdES-X2", "X");
		mapPerfilsFirma.put("AdES-XL", "XL");
		mapPerfilsFirma.put("AdES-XL1", "XL");
		mapPerfilsFirma.put("AdES-XL2", "XL");
		mapPerfilsFirma.put("AdES-A", "A");
		mapPerfilsFirma.put("PAdES-LTV", "LTV");
		mapPerfilsFirma.put("PAdES-Basic", "BES");
	}

	/** Mapega el perfil retornat pel plugin de firma al perfil acceptat per l'Arxiu
	 * 
	 * @param perfilFirmaEni
	 * @return
	 */
	private String mapToPerfilFirmaArxiu(String perfil) {
		if (mapPerfilsFirma.containsKey(perfil))
			perfil = mapPerfilsFirma.get(perfil);
		return perfil;
	}

	/** Mètode per convertir un arxiu a pdf 
	 * 
	 * @param arxiu
	 * @return
	 */
	public ArxiuDto converteixPdf(ArxiuDto arxiu) {
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
			String arxiuNom,
			String arxiuMime, 
			String tipusFirma, 
			String tipusFirmaEni, 
			String perfilFirmaEni, 
			byte[] signatura,
			byte[] arxiuContingut) {
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
		
		String documentDescripcio = documentStore.isAdjunt() ? documentStore.getAdjuntTitol(): document.getNom();		
		
		if (documentStore.getArxiuUuid() != null) {
			// Guardar firma a l'Arxiu
						
			ArxiuDto arxiuFirmat = new ArxiuDto();
			es.caib.plugins.arxiu.api.Document documentArxiu;
						
			// Consulta l'arxiu per si ja està definitiu no intentar guardar sobre el mateix
			documentArxiu = pluginHelper.arxiuDocumentInfo(
					documentStore.getArxiuUuid(), 
					null, 
					false, 
					true);
			
			// Mira que no hi hagi un document amb el mateix nom o que com a mínim sigui ell mateix en cas d'actualitzar
			String documentNom = inArxiu(
					processInstanceId, 
					DocumentEstat.DEFINITIU.equals(documentArxiu.getEstat()) ? 
							null 							// Nou document a l'Arxiu
							: documentStore.getArxiuUuid(), // Actualització del documetn
					arxiuNom);

			if (documentArxiu != null && DocumentEstat.DEFINITIU.equals(documentArxiu.getEstat())) 
			{
				// El document ja està firmat a l'Arxiu, es guarda amb un altre uuid
				documentStore.setArxiuUuid(null);
				ContingutArxiu documentCreat = pluginHelper.arxiuDocumentCrearActualitzar(
						expedient, 
						documentNom,
						documentDescripcio, 
						documentStore, 
						new ArxiuDto(
								arxiuNom, 
								signatura, 
								arxiuMime));
				documentStore.setArxiuUuid(documentCreat.getIdentificador());

			} else {
				// S'actualitza el document existent
				arxiuFirmat.setNom(arxiuNom);
				arxiuFirmat.setTipusMime(arxiuMime);
				arxiuFirmat.setContingut(signatura);
				pluginHelper.arxiuDocumentGuardarDocumentFirmat(
						expedient, 
						documentStore, 
						documentNom, 
						documentDescripcio, 
						arxiuFirmat,
						tipusFirma, 
						tipusFirmaEni, 
						perfilFirmaEni,
						arxiuContingut);
			}
			// Actualitza la informació al document store
			documentArxiu = pluginHelper.arxiuDocumentInfo(
					documentStore.getArxiuUuid(),
					null,
					false,
					true);
			documentStore.setNtiIdentificador(documentArxiu.getMetadades().getIdentificador());
			actualitzarNtiFirma(documentStore, documentArxiu);
		} 
		else 
		{
			// Guardar firma a custòdia

			// Si el document estava signat programa l'esborrat de la referència anterior en cas de commit
			if (documentStore.getReferenciaCustodia() != null && documentStore.isSignat()) {
				this.programarCustodiaEsborrarSignatures(
						documentStore.getReferenciaCustodia(),
						expedient);
				documentStore.setReferenciaCustodia(null);
				
			} 
			String referenciaCustodia = documentStore.getReferenciaCustodia();
			// Nova referència de custòdia
			if (referenciaCustodia == null) {
				referenciaCustodia = documentStore.getId() + "_" + new Date().getTime();
			}
			try {
				referenciaCustodia = pluginHelper.custodiaAfegirSignatura(
						referenciaCustodia, // custodiaId
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
					// ja està archivat
				} else {
					throw ex;
				}
			}
			documentStore.setReferenciaCustodia(referenciaCustodia);
			if (expedient.isNtiActiu()) {
				actualitzarNtiFirma(documentStore, null);
			}
		}
		
		documentStore.setSignat(true);
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
		dto.setPortafirmesActiu(document.isPortafirmesActiu());
		dto.setPlantilla(document.isPlantilla());
		dto.setSignat(documentStore.isSignat());
		if (documentStore.isSignat()) {
			if (documentStore.getArxiuUuid() == null) {
				dto.setSignaturaUrlVerificacio(
						pluginHelper.custodiaObtenirUrlComprovacioSignatura(
								documentStore.getReferenciaCustodia()));
			} else {
				dto.setSignaturaUrlVerificacio(
						getPropertyArxiuVerificacioBaseUrl() + documentStore.getNtiCsv());
			}
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
								documentStore.getReferenciaCustodia()));
			} else {
				dto.setSignaturaUrlVerificacio(
						getPropertyArxiuVerificacioBaseUrl() + documentStore.getNtiCsv());
			}
		}
		dto.setDocumentValid(documentStore.isDocumentValid());
		dto.setDocumentError(documentStore.getDocumentError());
		dto.setAnotacioAnnexId(documentStore.getAnnexId());
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
		dto.setGenerarNomesTasca(document.isGenerarNomesTasca());
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
										documentStore.getReferenciaCustodia()));
						dto.setSignaturaUrlVerificacio(
								dto.getUrlVerificacioCustodia());
					} else {
						dto.setSignaturaUrlVerificacio(
								getPropertyArxiuVerificacioBaseUrl() + documentStore.getNtiCsv());
					}
				}
				dto.setNtiCsv(documentStore.getNtiCsv());
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
		ExpedientDocumentDto  ed = null;
		if (!documentStore.isAdjunt()) {
			Document document = findDocumentPerInstanciaProcesICodi(
					processInstanceId,
					documentStore.getCodiDocument());
			if (document != null) {
				ed = crearDtoPerDocumentExpedient(
								document,
								documentStore);
				Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
				List<DocumentNotificacio> enviaments = documentNotificacioRepository.findByExpedientAndDocumentId(expedient, documentStore.getId());
				ed.setNotificat(!enviaments.isEmpty());
				return ed;
			} else {
				throw new NoTrobatException(
						Document.class,
						"(codi=" + documentStore.getCodiDocument() + ")");
			}
		} else {
			ed = crearDtoPerAdjuntExpedient(
					getAdjuntIdDeVariableJbpm(documentStore.getJbpmVariable()),
					documentStore);
		}
		// Consulta els annexos de les anotacions de registre i les guarda en un Map<Long documentStoreId, Anotacio>
		List<AnotacioAnnex> annexos = anotacioAnnexRepository.findByDocumentStoreId(documentStore.getId());
		if (annexos.size() > 0) {
			if (annexos.size() > 1) {
				logger.warn("S'ha trobat més d'un annex d'anotació pel documentStoreId " + documentStore.getId() );
			}
			ed.setAnotacioId(annexos.get(0).getAnotacio().getId());
			ed.setAnotacioIdentificador(annexos.get(0).getAnotacio().getIdentificador());
		}
		return ed;
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

	private String getUrlComprovacioSignatura(Long documentStoreId, String referenciaCustodia) throws Exception {
		String urlCustodia = pluginHelper.custodiaObtenirUrlComprovacioSignatura(referenciaCustodia);
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

	private String getUrlComprovacioSignatura(String referenciaCustodia, String token) {
		String urlCustodia = pluginHelper.custodiaObtenirUrlComprovacioSignatura(referenciaCustodia);
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
		} else if (processInstanceId != null) {
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
		String documentNom = documentStore.isAdjunt() ? documentStore.getArxiuNom() : (document!=null ? document.getNom() : "");
		if (expedient.isArxiuActiu()) {
			// Document integrat amb l'Arxiu
			if(firmes!=null && !firmes.isEmpty())
				comprovarFirmesReconegudes(firmes);
			if (arxiuUuid == null) {
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
				if (documentStore.getReferenciaCustodia() != null) {
					this.programarCustodiaEsborrarSignatures(documentStore.getReferenciaCustodia(), expedient);
				}
				String referenciaCustodia = documentStore.getId() + "_" + new Date().getTime();
				try {
					referenciaCustodia = pluginHelper.custodiaAfegirSignatura(
							referenciaCustodia, 
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
				if (expedient.isNtiActiu()) {
					actualitzarNtiFirma(documentStore, null);
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
	
	private boolean comprovarFirmesReconegudes(List<ArxiuFirmaDto> arxiuFirmes) {

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
		return true;
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
	
	/** Comprova si ja existeix a l'expedient un document amb el mateix nom i diferent UUID tenint en compte el . de l'extensió*/
	public String inArxiu(String processInstanceId, String arxiuUuid, String documentNom){
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		
		if (documentNom == null) {
			logger.warn("S'ha passat un nom de document null per fixar com a nom a l'Arxiu pel processInstanceId=" +processInstanceId + " i uuid=" + arxiuUuid);
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
				String nouDocumentNom = new String(documentNom);
				if (nomsExistingInArxiu.contains(nouDocumentNom.toLowerCase())) {
					if (nouDocumentNom.contains(".")) {
						// Nom amb extensió
						String name = nouDocumentNom.substring(0, nouDocumentNom.lastIndexOf('.'));
						String extension = nouDocumentNom.substring(nouDocumentNom.lastIndexOf('.'));
						nouDocumentNom = name;
						while(nomsExistingInArxiu.indexOf((nouDocumentNom + extension).toLowerCase()) >= 0) {
							ocurrences ++;
							nouDocumentNom = name + " (" + ocurrences + ")";
						}
						nouDocumentNom += extension;

					} else {
						// Nom sense extensió
						while (nomsExistingInArxiu.indexOf(nouDocumentNom.toLowerCase()) >= 0) {
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

	public List<DocumentStore> getDocumentsContinguts (Long documentStoreId){
		return documentStoreRepository.findDocumentsContinguts(documentStoreId);
		
	}
	
	public DocumentStoreDto toDocumentStoreDto(DocumentStore ds, boolean isZip) {
		DocumentStoreDto dsDto = new DocumentStoreDto();
		if(ds.getContinguts()!=null && !ds.getContinguts().isEmpty() && isZip) {
			List<DocumentStoreDto> contingutsDto = new ArrayList<DocumentStoreDto>();
			for(DocumentStore contingut: ds.getContinguts()) {
				contingutsDto.add(this.toDocumentStoreDto(contingut, false));
			}
			dsDto.setContinguts(contingutsDto);
		}
		dsDto.setDataCreacio(ds.getDataCreacio());
		dsDto.setDocumentError(ds.getDocumentError());
		dsDto.setDocumentValid(ds.isDocumentValid());
		dsDto.setNtiDefGenCsv(ds.getNtiDefinicionGenCsv());
		dsDto.setNtiEstatElaboracio(ds.getNtiEstadoElaboracion()!=null ? ds.getNtiEstadoElaboracion().toString() : NtiEstadoElaboracion.ORIGINAL.toString() );
		dsDto.setNtiIdDocOrigen(ds.getNtiIdDocumentoOrigen()!=null ? ds.getNtiIdDocumentoOrigen().toString() : null);
		dsDto.setNtiIdentificador(ds.getNtiIdentificador());
		dsDto.setNtiNomFormat(ds.getNtiNombreFormato()!=null ? ds.getNtiNombreFormato().toString() : null);
		dsDto.setNtiOrgan(ds.getNtiOrgano()!=null ? ds.getNtiOrgano().toString() : null);
		dsDto.setNtiOrigen(ds.getNtiOrigen()!=null ? ds.getNtiOrigen().toString() : null);
		dsDto.setNtiTipoFirma(ds.getNtiTipoFirma()!=null ? ds.getNtiTipoFirma().toString() : null);
		dsDto.setNtiTipusDocumental(ds.getNtiTipoDocumental()!=null ? ds.getNtiTipoDocumental().toString() : null);
		dsDto.setNtiValorCsv(ds.getNtiCsv());
		dsDto.setNtiVersion(ds.getNtiVersion());
		dsDto.setAdjunt(ds.isAdjunt());
		dsDto.setCodiDocument(ds.getCodiDocument());
		dsDto.setNom(ds.isAdjunt() ? ds.getAdjuntTitol() : ds.getCodiDocument());
		dsDto.setId(ds.getId());

		return dsDto;
	}

	private static final Log logger = LogFactory.getLog(DocumentHelperV3.class);

}
