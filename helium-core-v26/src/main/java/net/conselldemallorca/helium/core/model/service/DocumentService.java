/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import net.conselldemallorca.helium.core.common.JbpmVars;
import net.conselldemallorca.helium.core.helperv26.DocumentHelper;
import net.conselldemallorca.helium.core.model.dao.DefinicioProcesDao;
import net.conselldemallorca.helium.core.model.dao.DocumentDao;
import net.conselldemallorca.helium.core.model.dao.DocumentStoreDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientDao;
import net.conselldemallorca.helium.core.model.dao.PlantillaDocumentDao;
import net.conselldemallorca.helium.core.model.dao.RegistreDao;
import net.conselldemallorca.helium.core.model.dto.ArxiuDto;
import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.exception.IllegalArgumentsException;
import net.conselldemallorca.helium.core.model.exception.TemplateException;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.OpenOfficeUtils;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;


/**
 * Servei per a gestionar les descàrregues d'arxius
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class DocumentService {

	private ExpedientDao expedientDao;
	private DocumentDao documentDao;
	private DefinicioProcesDao definicioProcesDao;
	private PlantillaDocumentDao plantillaDocumentDao;
	private DocumentStoreDao documentStoreDao;
	private RegistreDao registreDao;
	private JbpmHelper jbpmDao;
	private DtoConverter dtoConverter;
	private DocumentHelper documentHelper;
	private ExpedientLogHelper expedientLogHelper;

	private MessageSource messageSource;
	private OpenOfficeUtils openOfficeUtils;


	public Long guardarDocumentTasca(
			Long entornId,
			String taskInstanceId,
			String documentCodi,
			Date documentData,
			String arxiuNom,
			byte[] arxiuContingut) {
		return guardarDocumentTasca(entornId, taskInstanceId, documentCodi, documentData, arxiuNom, arxiuContingut, null);
	}
	public Long guardarDocumentTasca(
			Long entornId,
			String taskInstanceId,
			String documentCodi,
			Date documentData,
			String arxiuNom,
			byte[] arxiuContingut,
			String user) {
		JbpmTask task = jbpmDao.getTaskById(taskInstanceId);
		DocumentStore documentStore = documentHelper.getDocumentStore(
				taskInstanceId,
				task.getProcessInstanceId(),
				documentCodi);
		Expedient expedient = expedientDao.findAmbProcessInstanceId(
				jbpmDao.getRootProcessInstance(task.getProcessInstanceId()).getId());
		boolean creat = (documentStore == null);
		
		if (creat) {
			expedientLogHelper.afegirLogExpedientPerTasca(
					taskInstanceId,
					ExpedientLogAccioTipus.TASCA_DOCUMENT_AFEGIR,
					documentCodi);
		} else {
			expedientLogHelper.afegirLogExpedientPerTasca(
					taskInstanceId,
					ExpedientLogAccioTipus.TASCA_DOCUMENT_MODIFICAR,
					documentCodi);
		}
		
		String arxiuNomAntic = (documentStore != null) ? documentStore.getArxiuNom() : null;
		Long documentStoreId = documentHelper.actualitzarDocument(
				taskInstanceId,
				task.getProcessInstanceId(),
				documentCodi,
				null,
				documentData,
				arxiuNom,
				arxiuContingut,
				false);
		// Registra l'acció
		if (user == null) user = SecurityContextHolder.getContext().getAuthentication().getName();
		if (creat) {
			registreDao.crearRegistreCrearDocumentTasca(
					expedient.getId(),
					taskInstanceId,
					user,
					documentCodi,
					arxiuNom);
		} else {
			registreDao.crearRegistreModificarDocumentTasca(
					expedient.getId(),
					taskInstanceId,
					user,
					documentCodi,
					arxiuNomAntic,
					arxiuNom);
		}
		return documentStoreId;
	}
	public Long guardarDocumentProces(
			String processInstanceId,
			String documentCodi,
			String adjuntTitol,
			Date documentData,
			String arxiuNom,
			byte[] arxiuContingut,
			boolean isAdjunt) {
		return guardarDocumentProces(
				processInstanceId, 
				documentCodi, 
				adjuntTitol, 
				documentData, 
				arxiuNom, 
				arxiuContingut, 
				isAdjunt, 
				null);
	}
	public Long guardarDocumentProces(
			String processInstanceId,
			String documentCodi,
			String adjuntTitol,
			Date documentData,
			String arxiuNom,
			byte[] arxiuContingut,
			boolean isAdjunt,
			String user) {
		DocumentStore documentStore = documentHelper.getDocumentStore(
				null,
				processInstanceId,
				documentCodi);
		Expedient expedient = expedientDao.findAmbProcessInstanceId(
				jbpmDao.getRootProcessInstance(processInstanceId).getId());
		boolean creat = (documentStore == null);
		
		if (!isAdjunt) {
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
		}
		
		String arxiuNomAntic = (documentStore != null) ? documentStore.getArxiuNom() : null;
		Long documentStoreId = documentHelper.actualitzarDocument(
				null,
				processInstanceId,
				documentCodi,
				adjuntTitol,
				documentData,
				arxiuNom,
				arxiuContingut,
				isAdjunt);
		if (user == null) {
			user = SecurityContextHolder.getContext().getAuthentication().getName();
		}
		// Registra l'acció
		if (creat) {
			registreDao.crearRegistreCrearDocumentInstanciaProces(
					expedient.getId(),
					processInstanceId,
					user,
					documentCodi,
					arxiuNom);
		} else {
			registreDao.crearRegistreModificarDocumentInstanciaProces(
					expedient.getId(),
					processInstanceId,
					user,
					documentCodi,
					arxiuNomAntic,
					arxiuNom);
		}
		return documentStoreId;
	}

	public Long guardarAdjunt(
			String processInstanceId,
			String adjuntId,
			String adjuntTitol,
			Date data,
			String arxiuNom,
			byte[] arxiuContingut) {
		return guardarAdjunt(
				processInstanceId, 
				adjuntId, 
				adjuntTitol, 
				data, 
				arxiuNom, 
				arxiuContingut, 
				null);
	}
	public Long guardarAdjunt(
			String processInstanceId,
			String adjuntId,
			String adjuntTitol,
			Date data,
			String arxiuNom,
			byte[] arxiuContingut,
			String user) {
		expedientLogHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_DOCUMENT_ADJUNTAR,
				adjuntTitol);
		String documentCodi = (adjuntId == null) ? new Long(new Date().getTime()).toString() : adjuntId;
		return guardarDocumentProces(
				processInstanceId,
				documentCodi,
				adjuntTitol,
				data,
				arxiuNom,
				arxiuContingut,
				true,
				user);
	}

	public void guardarDadesRegistre(
			Long documentStoreId,
			String registreNumero,
			Date registreData,
			String registreOficinaCodi,
			String registreOficinaNom,
			boolean registreEntrada) {
		DocumentStore documentStore = documentStoreDao.getById(documentStoreId, false);
		documentStore.setRegistreNumero(registreNumero);
		documentStore.setRegistreData(registreData);
		documentStore.setRegistreOficinaCodi(registreOficinaCodi);
		documentStore.setRegistreOficinaNom(registreOficinaNom);
		documentStore.setRegistreEntrada(registreEntrada);
	}
	public void esborrarDocument(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi) {
		esborrarDocument(taskInstanceId, processInstanceId, documentCodi, null);
	}
	public void esborrarDocument(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi,
			String user) {
		String piid = processInstanceId;
		if (piid == null && taskInstanceId != null) {
			JbpmTask task = jbpmDao.getTaskById(taskInstanceId);
			piid = task.getProcessInstanceId();
		}
		Expedient expedient = expedientDao.findAmbProcessInstanceId(
				jbpmDao.getRootProcessInstance(piid).getId());
		
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
		
		documentHelper.esborrarDocument(
				taskInstanceId,
				piid,
				documentCodi);
		if (user == null) {
			user = SecurityContextHolder.getContext().getAuthentication().getName();
		}
		if (taskInstanceId != null) {
			registreDao.crearRegistreEsborrarDocumentTasca(
					expedient.getId(),
					taskInstanceId,
					user,
					documentCodi);
		} else {
			registreDao.crearRegistreEsborrarDocumentInstanciaProces(
					expedient.getId(),
					piid,
					user,
					documentCodi);
		}
	}
	public void esborrarDocumentAdjunt(
			String processInstanceId,
			Long docStoreId,
			String adjuntId,
			String adjuntTitol) {
		Expedient expedient = expedientDao.findAmbProcessInstanceId(
				jbpmDao.getRootProcessInstance(processInstanceId).getId());
		
		expedientLogHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_DOCUMENT_ESBORRAR,
				adjuntTitol);
		
		documentHelper.esborrarDocumentAdjunt(
				docStoreId,
				processInstanceId,
				adjuntId);
		String user = SecurityContextHolder.getContext().getAuthentication().getName();
		registreDao.crearRegistreEsborrarDocumentInstanciaProces(
				expedient.getId(),
				processInstanceId,
				user,
				adjuntTitol);
	}

	public DocumentDto documentInfo(Long documentStoreId) {
		return getDocumentInfo(documentStoreId);
	}
	public DocumentDto documentInfoPerToken(String token) {
		try {
			return getDocumentInfo(token);
		} catch (Exception ex) {
			logger.error("Error al obtenir el document amb token " + token, ex);
			throw new IllegalArgumentsException(getMessage("error.document.obtenir"));
		}
	}
	public DocumentDto documentPerTasca(
			String taskInstanceId,
			String documentCodi,
			boolean ambContingut) {
		return documentHelper.getDocumentOriginal(
				taskInstanceId,
				null,
				documentCodi,
				ambContingut);
	}
	public DocumentDto documentPerProces(
			String procesInstanceId,
			Long documentId,
			boolean ambContingut) {
		DocumentDto dto = null;
		Document doc = documentDao.getById(documentId, false);
		if (doc != null) {
			dto = documentHelper.getDocumentOriginal(
						null,
						procesInstanceId,
						doc.getCodi(),
						ambContingut);
		} 
		return dto;
	}

	public ArxiuDto arxiuDocumentPerMostrar(String token) throws Exception {
		DocumentDto document = getDocumentInfo(token);
		if (document == null)
			return null;
		if (document.isSignat() || document.isRegistrat()) {
			return getArxiuDocumentVista(token);
		} else {
			return getArxiuDocumentOriginal(token);
		}
	}
	public ArxiuDto arxiuDocumentPerMostrar(Long documentStoreId) throws Exception {
		DocumentDto document = getDocumentInfo(documentStoreId);
		if (document == null)
			return null;
		if (document.isSignat() || document.isRegistrat()) {
			return getArxiuDocumentVista(documentStoreId);
		} else {
			return getArxiuDocumentOriginal(documentStoreId);
		}
	}

	public ArxiuDto arxiuDocumentPerSignar(String token, boolean ambSegellSignatura) throws Exception {
		DocumentDto dto = documentHelper.getDocumentVistaPerToken(token, true, ambSegellSignatura);
		if (dto != null)
			return new ArxiuDto(
					dto.getVistaNom(),
					dto.getVistaContingut());
		else
			return null;
	}

	public boolean signarDocumentTascaAmbToken(
			String token,
			byte[] signatura) throws Exception {
		boolean signat = documentHelper.signarDocumentTascaAmbToken(token, signatura);
		if (signat) {
			DocumentDto dto = documentHelper.getDocumentOriginalPerToken(token, false);
			JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(dto.getProcessInstanceId());
			Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
			registreDao.crearRegistreSignarDocument(
					expedient.getId(),
					dto.getProcessInstanceId(),
					SecurityContextHolder.getContext().getAuthentication().getName(),
					dto.getDocumentCodi());
		}
		return signat;
	}

	public DocumentDto generarDocumentPlantilla(
			Long entornId,
			Long documentId,
			String taskInstanceId,
			String processInstanceId,
			Date dataDocument,
			boolean forsarAdjuntarAuto) {
		return generarDocumentPlantilla(
					entornId, 
					documentId, 
					taskInstanceId, 
					processInstanceId, 
					dataDocument, 
					forsarAdjuntarAuto, 
					true,
					null);
	}
	public DocumentDto generarDocumentPlantilla(
			Long entornId,
			Long documentId,
			String taskInstanceId,
			String processInstanceId,
			Date dataDocument,
			boolean forsarAdjuntarAuto,
			String user) {
		return generarDocumentPlantilla(
					entornId, 
					documentId, 
					taskInstanceId, 
					processInstanceId, 
					dataDocument, 
					forsarAdjuntarAuto, 
					true,
					user);
	}
	public DocumentDto generarDocumentPlantilla(
			Long entornId,
			Long documentId,
			String taskInstanceId,
			String processInstanceId,
			Date dataDocument,
			boolean forsarAdjuntarAuto,
			boolean adjuntarAuto,
			String user) {
		Document document = documentDao.getById(documentId, false);
		DocumentDto resposta = new DocumentDto();
		resposta.setDataCreacio(new Date());
		resposta.setDataDocument(new Date());
		resposta.setArxiuNom(document.getNom() + ".odt");
		resposta.setAdjuntarAuto(document.isAdjuntarAuto());
		if (document.isPlantilla()) {
			ExpedientDto expedient;
			TascaDto tasca = null;
			Map<String, Object> model = new HashMap<String, Object>();
			String responsableCodi;
			if (taskInstanceId != null) {
				JbpmTask task = jbpmDao.getTaskById(taskInstanceId);
				JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(task.getProcessInstanceId());
				expedient = dtoConverter.toExpedientDto(
						expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId()),
						false);
				String numExpedient="";
				if(expedient.getNumero() == null){
					numExpedient = "";
				}else{
					numExpedient = expedient.getNumero() + "-";
				}
				String titol = numExpedient + document.getNom()+".odt";
				String carRemp ="\\/:*?\"<>|";
				titol.replaceAll(carRemp, "_");
				resposta.setArxiuNom(titol);
				tasca = dtoConverter.toTascaDto(
						task,
						null,
						true,
						true,
						true,
						true,
						false);
				InstanciaProcesDto instanciaProces = dtoConverter.toInstanciaProcesDto(
						task.getProcessInstanceId(),
						true,
						true,
						true);
				model.putAll(instanciaProces.getVarsComText());
				model.putAll(tasca.getVarsComText());
				responsableCodi = task.getAssignee();
			} else {
				JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(processInstanceId);
				expedient = dtoConverter.toExpedientDto(
						expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId()),
						false);
				
				responsableCodi = user == null ? SecurityContextHolder.getContext().getAuthentication().getName() : user;
				InstanciaProcesDto instanciaProces = dtoConverter.toInstanciaProcesDto(
						processInstanceId,
						true,
						true,
						true);
				model.putAll(instanciaProces.getVarsComText());
			}
			try {
				byte[] resultat = plantillaDocumentDao.generarDocumentAmbPlantilla(
						entornId,
						document,
						responsableCodi,
						expedient,
						(tasca != null) ? tasca.getProcessInstanceId() : processInstanceId,
						tasca,
						dataDocument,
						model);
				if (isActiuConversioVista()) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					getOpenOfficeUtils().convertir(
							resposta.getArxiuNom(),
							resultat,
							getExtensioVista(document),
							baos);
					resposta.setArxiuNom(
							nomArxiuAmbExtensio(
									resposta.getArxiuNom(),
									getExtensioVista(document)));
					resposta.setArxiuContingut(baos.toByteArray());
				} else {
					resposta.setArxiuContingut(resultat);
				}
				if (adjuntarAuto && (forsarAdjuntarAuto || document.isAdjuntarAuto())) {
					documentHelper.actualitzarDocument(
							taskInstanceId,
							(processInstanceId != null) ? processInstanceId : tasca.getProcessInstanceId(),
							document.getCodi(),
							null,
							dataDocument,
							resposta.getArxiuNom(),
							resposta.getArxiuContingut(),
							false);
				}
			} catch (Exception ex) {
				throw new TemplateException(
						getMessage("error.tascaService.generarDocument"), ex);
			}
		} else {
			resposta.setArxiuContingut(document.getArxiuContingut());
		}
		return resposta;
	}

	public boolean isExtensioDocumentPermesa(
			String taskInstanceId,
			String documentCodi,
			String extensio) {
		JbpmTask task = jbpmDao.getTaskById(taskInstanceId);
		DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmId(
				task.getProcessDefinitionId());
		Document document = documentDao.findAmbDefinicioProcesICodi(
				definicioProces.getId(),
				documentCodi);
		return document.isExtensioPermesa(extensio);
	}

	public String getDocumentCodiPerDocumentStoreId(Long documentStoreId) {
		DocumentStore documentStore = documentStoreDao.getById(documentStoreId, false);
		String varJbpm = documentStore.getJbpmVariable();
		if (documentStore.isAdjunt())
			return varJbpm.substring(JbpmVars.PREFIX_ADJUNT.length());
		else
			return varJbpm.substring(JbpmVars.PREFIX_VAR_DOCUMENT.length());
	}



	@Autowired
	public void setExpedientDao(ExpedientDao expedientDao) {
		this.expedientDao = expedientDao;
	}
	@Autowired
	public void setDocumentDao(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}
	@Autowired
	public void setDefinicioProcesDao(DefinicioProcesDao definicioProcesDao) {
		this.definicioProcesDao = definicioProcesDao;
	}
	@Autowired
	public void setPlantillaDocumentDao(PlantillaDocumentDao plantillaDocumentDao) {
		this.plantillaDocumentDao = plantillaDocumentDao;
	}
	@Autowired
	public void setDocumentStoreDao(DocumentStoreDao documentStoreDao) {
		this.documentStoreDao = documentStoreDao;
	}
	@Autowired
	public void setRegistreDao(RegistreDao registreDao) {
		this.registreDao = registreDao;
	}
	@Autowired
	public void setJbpmHelper(JbpmHelper jbpmDao) {
		this.jbpmDao = jbpmDao;
	}
	@Autowired
	public void setDtoConverter(DtoConverter dtoConverter) {
		this.dtoConverter = dtoConverter;
	}
	@Autowired
	public void setDocumentHelper(DocumentHelper documentHelper) {
		this.documentHelper = documentHelper;
	}
	@Autowired
	public void setExpedientLogHelper(ExpedientLogHelper expedientLogHelper) {
		this.expedientLogHelper = expedientLogHelper;
	}
	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}



	private DocumentDto getDocumentInfo(String token) {
		return documentHelper.getDocumentOriginalPerToken(token, false);
	}
	private DocumentDto getDocumentInfo(Long documentStoreId) {
		return documentHelper.getDocumentOriginal(documentStoreId, false);
	}
	private ArxiuDto getArxiuDocumentOriginal(String token) {
		DocumentDto dto = documentHelper.getDocumentOriginalPerToken(token, true);
		if (dto == null)
			return null;
		return new ArxiuDto(
				dto.getArxiuNom(),
				dto.getArxiuContingut());
	}
	private ArxiuDto getArxiuDocumentOriginal(Long documentStoreId) {
		DocumentDto dto = documentHelper.getDocumentOriginal(documentStoreId, true);
		if (dto == null)
			return null;
		return new ArxiuDto(
				dto.getArxiuNom(),
				dto.getArxiuContingut());
	}
	private ArxiuDto getArxiuDocumentVista(String token) throws Exception {
		DocumentDto dto = documentHelper.getDocumentVistaPerToken(token, false, false);
		if (dto == null)
			return null;
		return new ArxiuDto(
				dto.getVistaNom(),
				dto.getVistaContingut());
	}
	private ArxiuDto getArxiuDocumentVista(Long documentStoreId) throws Exception {
		DocumentDto dto = documentHelper.getDocumentVista(documentStoreId, false, false);
		if (dto == null)
			return null;
		return new ArxiuDto(
				dto.getVistaNom(),
				dto.getVistaContingut());
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

	private OpenOfficeUtils getOpenOfficeUtils() {
		if (openOfficeUtils == null)
			openOfficeUtils = new OpenOfficeUtils();
		return openOfficeUtils;
	}

	protected String getMessage(String key, Object[] vars) {
		try {
			return messageSource.getMessage(
					key,
					vars,
					null);
		} catch (NoSuchMessageException ex) {
			return "???" + key + "???";
		}
	}
	protected String getMessage(String key) {
		return getMessage(key, null);
	}

	private static final Log logger = LogFactory.getLog(DocumentService.class);


	public void esborrarVariableInstance(String processInstanceId, String adjuntId) {
		documentHelper.esborrarVariableInstance(
				processInstanceId,
				adjuntId);
	}
}
