	/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import edu.emory.mathcs.backport.java.util.Arrays;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.Firma;
import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.ExpedientLoggerHelper;
import net.conselldemallorca.helium.core.helper.ExpedientRegistreHelper;
import net.conselldemallorca.helium.core.helper.IndexHelper;
import net.conselldemallorca.helium.core.helper.NotificacioHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.core.helper.TascaHelper;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentNotificacio;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogEstat;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Interessat;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.core.util.PdfUtils;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDetallDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuEstat;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesEnviamentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesEnviamentDto.EntregaPostalTipus;
import net.conselldemallorca.helium.v3.core.api.dto.DadesNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiEstadoElaboracionEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiOrigenEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoDocumentalEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesCarrecDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesFluxBlocDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesSimpleTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortasignaturesDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaValidacioSignaturaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDocumentService;
import net.conselldemallorca.helium.v3.core.repository.DocumentNotificacioRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentStoreRepository;
import net.conselldemallorca.helium.v3.core.repository.InteressatRepository;
import net.conselldemallorca.helium.v3.core.repository.NotificacioRepository;
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
	NotificacioRepository notificacioRepository;

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
	@Resource
	private DocumentHelperV3 documentHelperV3;
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
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				isAdjunt ? 
						ExpedientLogAccioTipus.PROCES_DOCUMENT_ADJUNTAR 
						: ExpedientLogAccioTipus.PROCES_DOCUMENT_AFEGIR,
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
				ntiIdOrigen,
				true,
				null,
				null);
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
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_DOCUMENT_MODIFICAR,
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
	
	@Override
	@Transactional
	public Long guardarDocumentProces(
			String processInstanceId, 
			String documentCodi, 
			Date data, 
			String arxiu,
			byte[] contingut) {
		
		logger.debug("Guardar document procés (" +
				"processInstanceId=" + processInstanceId + ", " +
				"documentCodi=" + documentCodi + ", " +
				"data=" + data + ", " +
				"arxiu=" + arxiu + ")");
		
		Long documentStoreId = null;
		
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);

		ExpedientDocumentDto expDocDto = this.findOneAmbInstanciaProces(expedient.getId(), processInstanceId, documentCodi);
		if(expDocDto != null) { 
			documentStoreId = expDocDto.getId();
			this.update( 
					expedient.getId(),
					processInstanceId,
					expDocDto.getId(), //Long documentStoreId,
					(data != null ? data : expDocDto.getDataDocument()),
					expDocDto.getAdjuntTitol(), //String adjuntTitol
					arxiu,
					contingut,
					new MimetypesFileTypeMap().getContentType(arxiu),
					false, //boolean ambFirma,
					false, //boolean firmaSeparada,
					null, //byte[] firmaContingut,
					expDocDto.getNtiOrigen(), //NtiOrigenEnumDto ntiOrigen,
					expDocDto.getNtiEstadoElaboracion(), //NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
					expDocDto.getNtiTipoDocumental(), //NtiTipoDocumentalEnumDto ntiTipoDocumental,
					expDocDto.getNtiIdOrigen() //String ntiIdOrigen
					);
		} else {
			documentStoreId = this.create(
					expedient.getId(),
					processInstanceId,
					documentCodi, // null en el cas dels adjunts
					(data != null ? data : new Date()),
					null, // Títol en el cas dels adjunts (al crear ja li posa el nom del document)
					arxiu,
					contingut,
					new MimetypesFileTypeMap().getContentType(arxiu),
					false, //command.isAmbFirma(),
					false, //DocumentTipusFirmaEnumDto.SEPARAT.equals(command.getTipusFirma()),
					null, //firmaContingut,
					null, //command.getNtiOrigen(),
					null, //command.getNtiEstadoElaboracion(),
					null, //command.getNtiTipoDocumental(),
					null  //command.getNtiIdOrigen()
					);
		}
		return documentStoreId;
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
		DadesEnviamentDto dadesEnviamentDto = new DadesEnviamentDto();		
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
		// Si el document té contingut val més no enviar l'UUID
		if (documentDto.getArxiuContingut() == null) {
			dadesNotificacioDto.setDocumentArxiuUuid(documentDto.getArxiuUuid());
		}		
		dadesNotificacioDto.setDocumentId(documentStoreId);
					
		// De moment envia només a un interessat titular però es pot crear un enviament per cada titular amb la llista de destinataris		
		Interessat interessatEntity	= interessatRepository.findOne(interessatId);

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
			Interessat representantEntity = interessatRepository.findOne(representantId);
			PersonaDto destinatari = new PersonaDto();
			destinatari.setNom(representantEntity.getNom());
			destinatari.setLlinatge1(representantEntity.getLlinatge1());
			destinatari.setLlinatge2(representantEntity.getLlinatge2());
			destinatari.setDni(representantEntity.getNif());
			destinatari.setCodiDir3(representantEntity.getDir3Codi());
			destinatari.setTelefon(representantEntity.getTelefon());
			destinatari.setEmail(representantEntity.getEmail());
			destinatari.setTipus(representantEntity.getTipus());
			destinataris.add(destinatari);
		}
		dadesEnviamentDto.setDestinataris(destinataris);
		// Entrega postal
		if (interessatEntity.isEntregaPostal()) {
			dadesEnviamentDto.setEntregaPostalActiva(false);//(interessatEntity.isEntregaPostal());//Forcem false issue #1675
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
		dadesEnviamentDto.setServeiTipusEnum(dadesNotificacioDto.getServeiTipusEnum());
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
				true,
				null);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArxiuDto arxiuFindAmbDocumentVersio(Long expedientId, String processInstanceId, Long documentStoreId,
			String versio) throws NoTrobatException, PermisDenegatException {
		logger.debug("Consulta de l'arxiu del document de la instància de procés segons versió (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentStoreId=" + documentStoreId + ", " +
				"versioId=" + versio + ")");
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
				true,
				versio);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ArxiuDto arxiuFindAmbDocumentStoreId(Long documentStoreId) throws NoTrobatException {
		logger.debug("Consulta de l'arxiu del document per documentStoreId (" +
				"documentStoreId=" + documentStoreId + ")");
		
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		if (documentStore == null) {
			throw new NoTrobatException(
					DocumentStore.class,
					documentStoreId);
		}
		return documentHelper.getArxiuPerDocumentStoreId(
				documentStoreId,
				false,
				true,
				null);	
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ArxiuDto arxiuFindOriginal(
			Long expedientId, 
			Long documentStoreId) throws NoTrobatException {
		logger.debug("Consulta de l'arxiu del document original (" +
				"expedientId=" + expedientId + ", " +
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
		
		ArxiuDto arxiu = new ArxiuDto();
		arxiu.setNom(documentStore.getArxiuNom());
		if (documentStore.getArxiuContingut() == null && documentStore.getArxiuUuid() != null) {
			es.caib.plugins.arxiu.api.Document documentArxiu = pluginHelper.arxiuDocumentOriginal(documentStore.getArxiuUuid(), null);
			if (documentArxiu != null && documentArxiu.getContingut() != null) {
				arxiu.setContingut(documentArxiu.getContingut().getContingut());
				arxiu.setTipusMime(documentArxiu.getContingut().getTipusMime());
			}
		} else {
			arxiu.setContingut(documentStore.getArxiuContingut());
			arxiu.setTipusMime(documentHelperV3.getContentType(documentStore.getArxiuNom()));
		}
		
		return arxiu;
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
		List<Portasignatures> pendents = pluginHelper.findPendentsPortasignaturesPerProcessInstanceId(processInstanceId);
		return conversioTipusHelper.convertirList(pendents, PortasignaturesDto.class);
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
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
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
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				tascaId,
				true,
				true);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		ExpedientTipus expedientTipus = expedient.getTipus();

		// Troba el camp de la tasca
		Document document = documentRepository.findOne(documentId);
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
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		if (documentStore == null) {
			throw new NoTrobatException(DocumentStore.class, documentStoreId);
		}
		return documentHelper.getRespostasValidacioSignatura(documentStore);
	}

	@Override
	@Transactional(readOnly = true)
	public PortasignaturesDto getPortasignaturesByDocumentId(Integer documentId) {
		Portasignatures portasignatures = null;
		if (documentId != null) {
			portasignatures = portasignaturesRepository.findByDocumentId(documentId);
		}
		return conversioTipusHelper.convertir(portasignatures, PortasignaturesDto.class);

	}
	
	@Override
	@Transactional(readOnly = true)
	public PortasignaturesDto getPortasignaturesByDocumentStoreId(String processInstanceId, Long documentStoreId) {
		Portasignatures portasignatures = null;
		if (documentStoreId != null) {
			List<Portasignatures> peticions = portasignaturesRepository.findByProcessInstanceIdAndDocumentStoreId(processInstanceId, documentStoreId);
			if (!peticions.isEmpty()) {
				if (peticions.size() > 1) {
					logger.warn("S'ha trobat més d'una petició pendent pel document amb documentStoreId = " + documentStoreId + ". Es retorna la primera petició");
				}
				portasignatures = peticions.get(0);
			}
		}
		return conversioTipusHelper.convertir(portasignatures, PortasignaturesDto.class);
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
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
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
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
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
			DocumentMetadades metadades = arxiuDocument.getMetadades();
			Map<String,Object> metadadesAdicionals = metadades!=null ? metadades.getMetadadesAddicionals() : null;
			if(metadadesAdicionals!=null) {
				arxiuDetall.setExpedientTancat(metadadesAdicionals.get("eni:fecha_fin_exp")!=null ? true : false);
			}
			List<ArxiuDetallDto> versions =  new ArrayList<ArxiuDetallDto>();
			try {
				versions = pluginHelper.versions(documentStore.getArxiuUuid(), arxiuDetall.isExpedientTancat());
			} catch (Exception e) {
				logger.error("Error obtenint les versions del document amb uuid: " + documentStore.getArxiuUuid(), e);
			}
			arxiuDetall.setVersionsDocument(versions);
			
			documentHelper.actualitzarNtiFirma(documentStore, arxiuDocument);
			arxiuDetall.setIdentificador(arxiuDocument.getIdentificador());
			arxiuDetall.setNom(arxiuDocument.getNom());
			if (arxiuDocument.getEstat() != null) {
				switch(arxiuDocument.getEstat()) {
				case DEFINITIU:
					arxiuDetall.setArxiuEstat(ArxiuEstat.DEFINITIU);
					break;
				case ESBORRANY:
					arxiuDetall.setArxiuEstat(ArxiuEstat.ESBORRANY);
					break;				
				}
			}
			List<Firma> firmes = arxiuDocument.getFirmes();
			//DocumentMetadades metadades = arxiuDocument.getMetadades();
			if (metadades != null) {
				arxiuDetall.setEniVersio(metadades.getVersioNti());
				arxiuDetall.setEniIdentificador(metadades.getIdentificador());
				arxiuDetall.setEniDataCaptura(metadades.getDataCaptura());
				arxiuDetall.setEniCsv(metadades.getCsv());
				arxiuDetall.setEniCsvDef(metadades.getCsvDef());
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
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
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
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
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

			ArxiuDto arxiu = documentHelper.getArxiuPerDocumentStoreId(
					documentStore.getId(),
					false,
					false,
					null);
				
			if (arxiu.getTipusMime() == null)
				arxiu.setTipusMime(documentHelper.getContentType(arxiu.getNom()));
				
				
			String documentNom = documentHelperV3.inArxiu(expedient.getProcessInstanceId(), "-", arxiu.getNom());
			String documentDescripcio =  documentStore.isAdjunt() ? documentStore.getAdjuntTitol() :  document.getNom();			

			ContingutArxiu contingutArxiu = pluginHelper.arxiuDocumentCrearActualitzar(
					expedient,
					documentNom,
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
						documentNom,
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
	public void processarFirmaClient(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId,
			String arxiuNom,
			byte[] contingutFirmat) throws PermisDenegatException {
		logger.debug("Processar la firma en client (expedientId=" + expedientId + ", processInstanceId=" + processInstanceId + ", documentStoreId=" + documentStoreId + ", arxiuNom=" + arxiuNom + ")");

		// Comprova els permisos per modificar documents a l'expedient
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
		// Actualitza la informació del document firmat
		documentHelper.actualitzarDocument(
				documentStore.getId(),
				null,
				expedient.getProcessInstanceId(),
				documentStore.getDataDocument(),
				documentStore.getAdjuntTitol(),
				arxiuNom,
				contingutFirmat,
				new MimetypesFileTypeMap().getContentType(arxiuNom),
				true,
				false,
				null,
				documentStore.getNtiOrigen(),
				documentStore.getNtiEstadoElaboracion(),
				documentStore.getNtiTipoDocumental(),
				documentStore.getNtiIdDocumentoOrigen());
	
		// Enregistra la firma del document als logs
		expedientRegistreHelper.crearRegistreSignarDocument(
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(), 
				documentStore.getCodiDocument());
		
		// Afegeix el log a l'expedient
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				expedient.getId(),
				ExpedientLogAccioTipus.PROCES_DOCUMENT_FIRMAR,
				documentStore.isAdjunt() ? 
						documentStore.getAdjuntTitol()
						:documentStore.getCodiDocument());
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void enviarPortasignatures(
			DocumentDto document, 
			List<DocumentDto> annexos, 
			ExpedientDto expedientDto, 
			String importancia, 
			Date dataLimit, 
			Long tokenId,
			Long processInstanceId, 
			String transicioOK, 
			String transicioKO,
			PortafirmesSimpleTipusEnumDto fluxTipus,
			String[] responsables, 
			String portafirmesFluxId) throws SistemaExternException {
		
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(String.valueOf(processInstanceId));
		
		List<PortafirmesFluxBlocDto> blocList = null;
		if (portafirmesFluxId == null) {
			blocList = new ArrayList<PortafirmesFluxBlocDto>();
			PortafirmesFluxBlocDto bloc = null;
			String[] responsablesCodis = responsables;
			if (fluxTipus.equals(PortafirmesSimpleTipusEnumDto.SERIE)) {			
				for (int i = 0; i < responsablesCodis.length; i++) {
					List<String> personesPas = new ArrayList<String>();
					bloc = new PortafirmesFluxBlocDto();
					personesPas.add(responsablesCodis[i]);
					bloc.setDestinataris(personesPas);
					bloc.setMinSignataris(1);
					bloc.setObligatorietats(new boolean[] {true});
					blocList.add(bloc);
				}
			} else { // PortafirmesSimpleTipusEnumDto.PARALEL
				bloc = new PortafirmesFluxBlocDto();
				List<String> personesPas = new ArrayList<String>();
				bloc.setMinSignataris(responsablesCodis.length);
				for (int i = 0; i < responsablesCodis.length; i++) {
					personesPas.add(responsablesCodis[i]);
				}
				bloc.setDestinataris(personesPas);
				boolean[] obligatorietats = new boolean[responsablesCodis.length];
				Arrays.fill(obligatorietats, true);
				bloc.setObligatorietats(obligatorietats);
				blocList.add(bloc);
			}
		}
		
		
		pluginHelper.portasignaturesEnviar(
				document, 
				annexos, 
				blocList, 
				expedient, 
				importancia, 
				dataLimit, 
				tokenId, 
				processInstanceId, 
				transicioOK, 
				transicioKO, 
				fluxTipus,
				portafirmesFluxId);
	}

	private PersonaDto findPersonaCarrecAmbCodi(String codi) {
		logger.debug("Obtenint usuari/càrrec amb codi (codi=" + codi + ")");
		PersonaDto persona = null;
		try {
			persona = pluginHelper.personaFindAmbCodi(codi);
		} catch (NoTrobatException ex) {
			logger.error("No s'ha trobat cap usuari amb el codi " + codi + ". Procedim a cercar si és un càrrec.");
			persona = new PersonaDto();
			PortafirmesCarrecDto carrec = pluginHelper.portafirmesRecuperarCarrec(codi);
			if (carrec != null) {
				persona.setCodi(carrec.getCarrecId());
				persona.setNom(carrec.getCarrecName() + (carrec.getUsuariPersonaNom() != null ? " - " + carrec.getUsuariPersonaNom() : "") );
				persona.setDni(carrec.getUsuariPersonaNif());
			} else {
				throw new NoTrobatException(PersonaDto.class, codi);
			}
		}
		return persona;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void portafirmesCancelar(Integer documentId)
			throws SistemaExternException {	
		pluginHelper.portasignaturesCancelar(documentId);
	
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientDocumentServiceImpl.class);
}
