/**
 * 
 */
package es.caib.helium.logic.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import es.caib.helium.client.engine.model.WTaskInstance;
import es.caib.helium.logic.helper.DocumentHelper;
import es.caib.helium.logic.helper.ExpedientHelper;
import es.caib.helium.logic.helper.ExpedientRegistreHelper;
import es.caib.helium.logic.helper.IndexHelper;
import es.caib.helium.logic.helper.NotificacioHelper;
import es.caib.helium.logic.helper.PaginacioHelper;
import es.caib.helium.logic.helper.PluginHelper;
import es.caib.helium.logic.helper.TascaHelper;
import es.caib.helium.logic.intf.WorkflowRetroaccioApi;
import es.caib.helium.logic.intf.dto.ArxiuDetallDto;
import es.caib.helium.logic.intf.dto.ArxiuDto;
import es.caib.helium.logic.intf.dto.ArxiuFirmaDto;
import es.caib.helium.logic.intf.dto.DadesEnviamentDto;
import es.caib.helium.logic.intf.dto.DadesEnviamentDto.EntregaPostalTipus;
import es.caib.helium.logic.intf.dto.DadesNotificacioDto;
import es.caib.helium.logic.intf.dto.DocumentDto;
import es.caib.helium.logic.intf.dto.ExpedientDocumentDto;
import es.caib.helium.logic.intf.dto.InteressatTipusEnumDto;
import es.caib.helium.logic.intf.dto.NotificacioDto;
import es.caib.helium.logic.intf.dto.NtiEstadoElaboracionEnumDto;
import es.caib.helium.logic.intf.dto.NtiOrigenEnumDto;
import es.caib.helium.logic.intf.dto.NtiTipoDocumentalEnumDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.PersonaDto;
import es.caib.helium.logic.intf.dto.PortasignaturesDto;
import es.caib.helium.logic.intf.dto.RespostaValidacioSignaturaDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.intf.exception.ValidacioException;
import es.caib.helium.logic.intf.integracio.notificacio.ServeiTipusEnum;
import es.caib.helium.logic.intf.service.ExpedientDocumentService;
import es.caib.helium.logic.security.ExtendedPermission;
import es.caib.helium.logic.util.PdfUtils;
import es.caib.helium.persist.entity.DefinicioProces;
import es.caib.helium.persist.entity.Document;
import es.caib.helium.persist.entity.DocumentNotificacio;
import es.caib.helium.persist.entity.DocumentStore;
import es.caib.helium.persist.entity.Expedient;
import es.caib.helium.persist.entity.ExpedientTipus;
import es.caib.helium.persist.entity.Interessat;
import es.caib.helium.persist.entity.Portasignatures;
import es.caib.helium.persist.entity.Portasignatures.TipusEstat;
import es.caib.helium.persist.entity.Portasignatures.Transicio;
import es.caib.helium.persist.repository.DocumentNotificacioRepository;
import es.caib.helium.persist.repository.DocumentRepository;
import es.caib.helium.persist.repository.DocumentStoreRepository;
import es.caib.helium.persist.repository.InteressatRepository;
import es.caib.helium.persist.repository.NotificacioRepository;
import es.caib.helium.persist.repository.PortasignaturesRepository;
import es.caib.helium.persist.repository.RegistreRepository;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.Firma;

/**
 * Implementació dels mètodes del servei ExpedientDocumentService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExpedientDocumentServiceImpl implements ExpedientDocumentService {

	@Resource
	private RegistreRepository registreRepository;
	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private DocumentStoreRepository documentStoreRepository;
	@Resource
	private DocumentNotificacioRepository documentNotificacioRepository;
	@Resource
	private PortasignaturesRepository portasignaturesRepository;
	@Resource
	private InteressatRepository interessatRepository;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private  NotificacioRepository notificacioRepository;

	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private DocumentHelper documentHelper;
	@Resource
	private TascaHelper tascaHelper;
	@Resource
	private IndexHelper indexHelper;
	@Resource
	private WorkflowRetroaccioApi workflowRetroaccioApi;
	@Resource
	private ExpedientRegistreHelper expedientRegistreHelper;
	@Resource
	private DocumentHelper documentHelperV3;
	@Resource
	private NotificacioHelper notificacioHelper;



	@Override
	@Transactional
	public Long create(
			Long expedientId,
			String processInstanceId,
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
			String ntiIdOrigen) {
		logger.debug("Crear document a dins l'expedient (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentCodi=" + documentCodi + ", " +
				"data=" + data + ", " +
				"adjuntTitol=" + adjuntTitol + ", " +
				"arxiuNom=" + arxiuNom + ", " +
				"arxiuContingut=" + arxiuContingut + ", " +
				"arxiuContentType=" + arxiuContentType + ", " +
				"ambFirma=" + ambFirma + ", " +
				"firmaSeparada=" + firmaSeparada + ", " +
				"firmaContingut=" + firmaContingut + ", " +
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
		boolean isAdjunt = documentCodi == null;
		workflowRetroaccioApi.afegirInformacioRetroaccioPerProces(
				processInstanceId,
				isAdjunt ?
						WorkflowRetroaccioApi.ExpedientRetroaccioTipus.PROCES_DOCUMENT_ADJUNTAR :
						WorkflowRetroaccioApi.ExpedientRetroaccioTipus.PROCES_DOCUMENT_AFEGIR,
				documentCodi);
		Long documentStoreId = documentHelper.crearDocument(
				null,
				processInstanceId,
				documentCodi,
				data,
				isAdjunt,
				isAdjunt ? adjuntTitol : null,
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
				ntiIdOrigen);
		indexHelper.expedientIndexLuceneUpdate(processInstanceId);
		expedientRegistreHelper.crearRegistreCrearDocumentInstanciaProces(
				expedient.getId(),
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				documentCodi,
				arxiuNom);
		return documentStoreId;
	}

	@Override
	@Transactional
	public void update(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId,
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
			String ntiIdOrigen) {
		logger.debug("Modificar document de l'expedient (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentStoreId=" + documentStoreId + ", " +
				"data=" + data + ", " +
				"adjuntTitol=" + adjuntTitol + ", " +
				"arxiuNom=" + arxiuNom + ", " +
				"arxiuContingut=" + arxiuContingut + ", " +
				"arxiuContentType=" + arxiuContentType + ", " +
				"ambFirma=" + ambFirma + ", " +
				"firmaSeparada=" + firmaSeparada + ", " +
				"firmaContingut=" + firmaContingut + ", " +
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
		workflowRetroaccioApi.afegirInformacioRetroaccioPerProces(
				processInstanceId,
				WorkflowRetroaccioApi.ExpedientRetroaccioTipus.PROCES_DOCUMENT_MODIFICAR,
				documentCodi);
		documentHelper.actualitzarDocument(
				documentStoreId,
				null,
				processInstanceId,
				data,
				documentStore.isAdjunt() ? adjuntTitol : null,
				arxiuNom,
				arxiuContingut,
				arxiuContentType,
				ambFirma,
				firmaSeparada,
				firmaContingut,
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<NotificacioDto> findNotificacionsPerDatatable(
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consultant notificacions per la datatable (" +
				"filtre=" + filtre + ", " +
				"paginacioParams=" + paginacioParams + ")");
		PaginaDto<NotificacioDto> pagina = paginacioHelper.toPaginaDto(
				notificacioRepository.findByFiltrePaginat(
						filtre == null || "".equals(filtre),
						filtre,
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				NotificacioDto.class);
		return pagina;
	}

	
	@Override
	@Transactional
	public DadesNotificacioDto notificarDocument(
			Long expedientId,
			Long documentStoreId,
			DadesNotificacioDto dadesNotificacioDto,
			Long interessatId,
			Long representantId) {

		// Comprova els permisos
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.DOC_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		
		// Prepara les dades d'enviament
		var dadesEnviamentDto = new DadesEnviamentDto();
		DocumentDto documentDto = documentHelperV3.toDocumentDto(
				documentStoreId,
				true,
				false,
				false,
				false,
				true, // Per notificar
				false);		
		ExpedientTipus expedientTipus = expedient.getTipus();
		dadesNotificacioDto.setEmisorDir3Codi(expedientTipus.getNotibEmisor());
		dadesNotificacioDto.setProcedimentCodi(expedientTipus.getNotibCodiProcediment());
		dadesNotificacioDto.setExpedientId(expedientId);
		dadesNotificacioDto.setEnviamentTipus(dadesNotificacioDto.getEnviamentTipus());
		
		dadesNotificacioDto.setDocumentArxiuNom(documentDto.getArxiuNom());
		dadesNotificacioDto.setDocumentArxiuContingut(documentDto.getArxiuContingut());
		
		dadesNotificacioDto.setDocumentId(documentStoreId);
					
		// De moment envia només a un interessat titular però es pot crear un enviament per cada titular amb la llista de destinataris		
		Interessat interessatEntity	= interessatRepository.getById(interessatId);

		// Afegeix un enviament per interessat a la notificació com a titular de la mateixa
		List<DadesEnviamentDto> enviaments = new ArrayList<DadesEnviamentDto>();
		// Titular
		PersonaDto titular = new PersonaDto();
		titular.setDni(interessatEntity.getNif());
		titular.setCodiDir3(interessatEntity.getDir3Codi());
		titular.setNom(interessatEntity.getNom());
		titular.setLlinatge1(interessatEntity.getLlinatge1());
		titular.setLlinatge2(interessatEntity.getLlinatge2());
		titular.setTelefon(interessatEntity.getTelefon());
		titular.setEmail(interessatEntity.getEmail());
		titular.setTipus(interessatEntity.getTipus());
		dadesEnviamentDto.setTitular(titular);
		// Destinataris
		List<PersonaDto> destinataris = new ArrayList<PersonaDto>();
		if (representantId != null) {
			Interessat representantEntity = interessatRepository.getById(representantId);
			PersonaDto destinatari = new PersonaDto();
			destinatari.setNom(representantEntity.getNom());
			destinatari.setLlinatge1(representantEntity.getLlinatge1());
			destinatari.setLlinatge2(representantEntity.getLlinatge2());
			destinatari.setDni(representantEntity.getNif());
			destinatari.setCodiDir3(representantEntity.getDir3Codi());
			destinatari.setTelefon(representantEntity.getTelefon());
			destinatari.setEmail(representantEntity.getEmail());
			if (representantEntity.getTipus() != null) {
				destinatari.setTipus(InteressatTipusEnumDto.valueOf(representantEntity.getTipus().toString()));
			}
			destinataris.add(destinatari);
		}
		dadesEnviamentDto.setDestinataris(destinataris);
		// Entrega postal
		if (interessatEntity.isEntregaPostal()) {
			dadesEnviamentDto.setEntregaPostalActiva(interessatEntity.isEntregaPostal());
			dadesEnviamentDto.setEntregaPostalTipus(EntregaPostalTipus.valueOf(interessatEntity.getEntregaTipus().name()));
			dadesEnviamentDto.setEntregaPostalLinea1(interessatEntity.getLinia1());
			dadesEnviamentDto.setEntregaPostalLinea2(interessatEntity.getLinia2());
			if (interessatEntity.getCodiPostal() != null)
				dadesEnviamentDto.setEntregaPostalCodiPostal(interessatEntity.getCodiPostal());
			else
				dadesEnviamentDto.setEntregaPostalCodiPostal("00000");
		}
		// Entrega DEH
		if (interessatEntity.isEntregaDeh()) {
			dadesEnviamentDto.setEntregaDehActiva(interessatEntity.isEntregaDeh());
			dadesEnviamentDto.setEntregaDehProcedimentCodi(expedientTipus.getNtiClasificacion());
			dadesEnviamentDto.setEntregaDehObligat(interessatEntity.isEntregaDehObligat());
		}
		if (dadesNotificacioDto.getServeiTipusEnum() != null) {
			dadesEnviamentDto.setServeiTipusEnum(ServeiTipusEnum.valueOf(dadesNotificacioDto.getServeiTipusEnum().toString()));
		}

		enviaments.add(dadesEnviamentDto);
		dadesNotificacioDto.setEnviaments(enviaments);
		// Notifica i guarda la informació
		DocumentNotificacio notificacio = notificacioHelper.altaNotificacio(expedient, dadesNotificacioDto);
		
		return notificacioHelper.toDadesNotificacioDto(notificacio);
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
		DocumentStore documentStore = documentStoreRepository.getById(documentStoreId);
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
				true);
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
		if (expedientTipus.isAmbInfoPropia()) {
			document = documentRepository.findByExpedientTipusAndCodi(
					expedientTipus.getId(),
					documentCodi,
					expedientTipus.getExpedientTipusPare() != null);
		} else
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
					expedientTipus.getId(),
					documentCodi,
					false);
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
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				taskInstanceId,
				true,
				true);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(
				task.getProcessInstanceId());
		ExpedientTipus expedientTipus = expedient.getTipus();
		Document document;
		if (expedientTipus.isAmbInfoPropia())
			document = documentRepository.findByExpedientTipusAndCodi(
					expedientTipus.getId(),
					documentCodi,
					true);
		else
			document = documentRepository.findByDefinicioProcesAndCodi(
					expedientHelper.findDefinicioProcesByProcessInstanceId(
							task.getProcessInstanceId()), 
					documentCodi);
		if(document == null)
			throw new NoTrobatException(Document.class, documentCodi);
				
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
					null, // nti
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
		WTaskInstance task = tascaHelper.getTascaComprovacionsTramitacio(
				tascaId,
				true,
				true);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		ExpedientTipus expedientTipus = expedient.getTipus();

		// Troba el camp de la tasca
		Document document = documentRepository.getById(documentId);
		if (expedientTipus.isAmbInfoPropia()) {
			document = documentRepository.findByExpedientTipusAndCodi(
					expedientTipus.getId(), 
					document.getCodi(),
					expedientTipus.isAmbHerencia());
		} else {
			DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
					task.getProcessInstanceId());
			document = documentRepository.findByDefinicioProces(
					definicioProces.getId(), 
					documentId);		
		}
		if (document == null)
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
		DocumentStore documentStore = documentStoreRepository.getById(documentStoreId);
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
		DocumentStore document = documentStoreRepository.getById(documentStoreId);
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
					false, // Per notificar
					false);
		} else {
			dto = documentHelper.toDocumentDto(
					documentStoreId,
					true,
					false,
					false,
					false,
					false, // Per notificar
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
		DocumentStore documentStore = documentStoreRepository.getById(documentStoreId);
		DocumentDto dto = documentHelper.toDocumentDto(
				documentStoreId,
				false,
				false,
				true,
				true,
				false, // Per notificar
				(documentStore == null || documentStore.getArxiuUuid() == null));
		if (dto == null) {
			return null;
		}
		return new ArxiuDto(dto.getVistaNom(), dto.getVistaContingut());
	}

	@Override
	@Transactional(readOnly = true)
	public DocumentDto findDocumentAmbId(Long documentStoreId) {
		DocumentStore documentStore = documentStoreRepository.getById(documentStoreId);
		DocumentDto dto = documentHelper.toDocumentDto(
				documentStoreId,
				false,
				false,
				true,
				true,
				false, // Per notificar
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
						BasePermission.READ,
						BasePermission.ADMINISTRATION});
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
			if (StringUtils.isEmpty(documentStore.getArxiuUuid()))
				throw new ValidacioException("El document no té UUID d'Arxiu per consultar el detall");
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
				arxiuDetall.setFirmes(PluginHelper.toArxiusFirmesDto(firmes));
			}
		}
		return arxiuDetall;
	}


	@Override
	@Transactional(readOnly = true)
	public ArxiuFirmaDto getArxiuFirma(
			Long expedientId, 
			Long documentStoreId, 
			int firmaIndex) {
		logger.debug("Obtenint la firma de l'arxiu pel document (" +
				"expedientId=" + expedientId + ", " +
				"documentStoreId=" + documentStoreId + ", " +
				"firmaIndex=" + firmaIndex + ")");

		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.DOC_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		DocumentStore documentStore = documentStoreRepository.getById(documentStoreId);
		if (documentStore == null) {
			throw new NoTrobatException(
					DocumentStore.class, 
					documentStoreId);
		}
		ArxiuFirmaDto arxiuFirma = null;
		if (expedient.isArxiuActiu()) {
			es.caib.plugins.arxiu.api.Document arxiuDocument = pluginHelper.arxiuDocumentInfo(
					documentStore.getArxiuUuid(),
					null,
					true,
					true);
			List<Firma> firmes = arxiuDocument.getFirmes();
			if (firmes != null) {
				List<ArxiuFirmaDto> firmesDtos = PluginHelper.toArxiusFirmesDto(firmes);
				if (firmesDtos.size() > firmaIndex)
					arxiuFirma = firmesDtos.get(firmaIndex);
			}
		}
		return arxiuFirma;
	}

	private String getExtensio(String arxiuNom) {
		int index = arxiuNom.lastIndexOf('.');
		if (index == -1) {
			return "";
		} else {
			return arxiuNom.substring(index + 1);
		}
	}
	
	@Transactional
	@Override
	public void notificacioActualitzarEstat(
			String identificador, 
			String referencia) {
		
		DocumentNotificacio notificacio = documentNotificacioRepository.findByEnviamentIdentificadorAndEnviamentReferencia(
				identificador,
				referencia);
		if (notificacio == null) {
			throw new NoTrobatException(DocumentNotificacio.class);
		}
		
		try {
			pluginHelper.notificacioActualitzarEstatEnviament(notificacio);
			pluginHelper.notificacioActualitzarEstat(notificacio);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de notificacions";
			logger.error(errorDescripcio, ex);
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void migrarArxiu(Long expedientId, Long documentStoreId) {
		logger.debug("Migrar el document (documentStoreId=" + documentStoreId + ") a l'arxiu");

		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.WRITE,
						ExtendedPermission.ADMINISTRATION});
		DocumentStore documentStore = documentStoreRepository.getById(documentStoreId);
		try {
			// Fa validacions prèvies
			if (!expedient.isArxiuActiu())
				throw new ValidacioException("Aquest docment no es pot migrar perquè l'expedient no té activada la intagració amb l'arxiu");
			
			if (expedient.getArxiuUuid() == null || expedient.getArxiuUuid().isEmpty())
				throw new ValidacioException("Aquest no està integrat a l'arxiu");
			
			// Valida que els documents siguin convertibles
			if(!PdfUtils.isArxiuConvertiblePdf(documentStore.getArxiuNom()))
				throw new ValidacioException("No es pot migrar el document perquè no és convertible a PDF");

				
			Document document = documentHelper.findDocumentPerInstanciaProcesICodi(
					expedient.getProcessInstanceId(),
					documentStore.getCodiDocument());
			String documentDescripcio;
			if (documentStore.isAdjunt()) {
				documentDescripcio = documentStore.getAdjuntTitol();
			} else {
				documentDescripcio = document.getNom();
			}
			ArxiuDto arxiu = documentHelper.getArxiuPerDocumentStoreId(
					documentStore.getId(),
					false,
					false);
				
			if (arxiu.getTipusMime() == null)
				arxiu.setTipusMime(documentHelper.getContentType(arxiu.getNom()));
				
				
			ContingutArxiu contingutArxiu = pluginHelper.arxiuDocumentCrearActualitzar(
					expedient,
					documentDescripcio,
					documentStore,
					arxiu);
			documentStore.setArxiuUuid(contingutArxiu.getIdentificador());
			es.caib.plugins.arxiu.api.Document documentArxiu = pluginHelper.arxiuDocumentInfo(
					contingutArxiu.getIdentificador(),
					null,
					false,
					true);
			documentStore.setNtiIdentificador(
					documentArxiu.getMetadades().getIdentificador());
				
			if (documentStore.isSignat()) {
				
				pluginHelper.arxiuDocumentGuardarPdfFirmat(
						expedient,
						documentStore,
						documentDescripcio,
						arxiu);
				documentArxiu = pluginHelper.arxiuDocumentInfo(
						documentStore.getArxiuUuid(),
						null,
						false,
						true);
				documentHelper.actualitzarNtiFirma(documentStore, documentArxiu);
			}
			documentStore.setArxiuContingut(null);
		} catch (Exception ex) {
			String errorDescripcio = "Error migrant el document " + documentStore.getArxiuNom() + " de l'expedeient " + expedient.getTitol() + " a l'arxiu: " + ex.getMessage();
			logger.error(errorDescripcio, ex);
			throw new RuntimeException(
					errorDescripcio,
					ex);
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public Long guardarDocumentProces(
			Long expedientId, 
			String processInstanceId, 
			String documentCodi,
			String adjuntTitol, 
			Date documentData, 
			String arxiuNom, 
			byte[] arxiuContingut, 
			boolean isAdjunt,
			String user) {

		Long documentStoreId = null;
		ExpedientDocumentDto document;
		if (!isAdjunt)
			document = this.findOneAmbInstanciaProces(
    			expedientId,
    			processInstanceId,
    			documentCodi);
		else
			document = null;
		if (document == null) {
			// Crear
			documentStoreId = this.create(
					expedientId,
					processInstanceId,
					isAdjunt ? null : documentCodi, // null en el cas dels adjunts
					documentData,
					isAdjunt ? adjuntTitol : null, // Títol en el cas dels adjunts
					arxiuNom,
					arxiuContingut,
					null,
					false,
					false,
					null,
					null,
					null,
					null,
					null);
		} else {
			// Actualitzar
			this.update(
					expedientId,
					processInstanceId,
					expedientId,
					documentData,
					document.getAdjuntTitol(), 
					arxiuNom,
					arxiuContingut,
					null,
					false,
					false,
					null,
					document.getNtiOrigen(),
					document.getNtiEstadoElaboracion(),
					document.getNtiTipoDocumental(),
					document.getNtiIdOrigen());			
		}	
		return documentStoreId;
	}

	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientDocumentServiceImpl.class);
}
