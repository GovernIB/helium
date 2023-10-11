	/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.google.common.base.Strings;

import edu.emory.mathcs.backport.java.util.Arrays;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.Firma;
import net.conselldemallorca.helium.core.common.JbpmVars;
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
import net.conselldemallorca.helium.core.model.hibernate.Anotacio;
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
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDetallDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuEstat;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesEnviamentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesEnviamentDto.EntregaPostalTipus;
import net.conselldemallorca.helium.v3.core.api.dto.DadesNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentInfoDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentListDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentStoreDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.FirmaResultatDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiEstadoElaboracionEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiOrigenEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoDocumentalEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesCarrecDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesFluxBlocDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesSimpleTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortasignaturesDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaValidacioSignaturaDto;
import net.conselldemallorca.helium.v3.core.api.dto.document.DocumentDetallDto;
import net.conselldemallorca.helium.v3.core.api.dto.document.NtiDetallDto;
import net.conselldemallorca.helium.v3.core.api.dto.document.PsignaDetallDto;
import net.conselldemallorca.helium.v3.core.api.dto.document.RegistreDetallDto;
import net.conselldemallorca.helium.v3.core.api.dto.document.SignaturaDetallDto;
import net.conselldemallorca.helium.v3.core.api.dto.document.SignaturaValidacioDetallDto;
import net.conselldemallorca.helium.v3.core.api.dto.regles.CampFormProperties;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDocumentService;
import net.conselldemallorca.helium.v3.core.regles.ReglaHelper;
import net.conselldemallorca.helium.v3.core.repository.AnotacioRepository;
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
	private NotificacioRepository notificacioRepository;
	@Resource
	private AnotacioRepository anotacioRepository;

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
	@Resource
	private ReglaHelper reglaHelper;

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
			String ntiIdOrigen,
			List<ExpedientDocumentDto> annexosPerNotificar) {
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
				null,
				annexosPerNotificar);
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
			byte[] contingut,
			List<ExpedientDocumentDto> annexosPerNotificar) {
		
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
					this.getExtensio(arxiu)!= null && this.getExtensio(arxiu).equals("zip") ? arxiu : null, // Títol en el cas dels adjunts (al crear ja li posa el nom del document)
					arxiu,
					contingut,
					documentHelper.getContentType(arxiu),
					false, //command.isAmbFirma(),
					false, //DocumentTipusFirmaEnumDto.SEPARAT.equals(command.getTipusFirma()),
					null, //firmaContingut,
					null, //command.getNtiOrigen(),
					null, //command.getNtiEstadoElaboracion(),
					null, //command.getNtiTipoDocumental(),
					null,  //command.getNtiIdOrigen()
					annexosPerNotificar);
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
			List<DocumentStoreDto> documentsDinsZip,
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
		
		//Si es tracta d'un zip amb un llistat de documents, els posem dins annexos
		dadesNotificacioDto.setDocumentsDinsZip(documentsDinsZip);
		
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

    @Override
	@Transactional(readOnly = true)
    public List<DocumentListDto> findDocumentsExpedient(Long expedientId, Boolean tots, PaginacioParamsDto paginacioParams) throws NoTrobatException, PermisDenegatException {
		logger.debug("Consulta els documents de l'expedient' (expedientId=" + expedientId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);

		if (tots) {
			// Comprovam que l'usuari té permisos d'administrador sobre l'expedient
			try {
				expedientHelper.getExpedientComprovantPermisos(
						expedientId,
						false,
						false,
						false,
						true);
			} catch (PermisDenegatException pde) {
				// Si no es tenen permisos d'administrador no es mostraran totes les dades
				tots = false;
			}
		}

		String processInstanceId = expedient.getProcessInstanceId();
		List<Document> documentsTipusExpedient = documentRepository.findByExpedientTipusId(expedient.getTipus().getId());
		List<ExpedientDocumentDto> documentsExpedient = findAmbInstanciaProces(expedientId, expedient.getProcessInstanceId());
		List<PortasignaturesDto> documentsPsignaPendent = portasignaturesFindPendents(expedientId, expedient.getProcessInstanceId());
		Map<String, CampFormProperties> documentsFormProperties = reglaHelper.getDocumentFormProperties(expedient.getTipus(), expedient.getEstat());

		List<DocumentListDto> documents = new ArrayList<DocumentListDto>();
		List<String> documentCodis = new ArrayList<String>();

		// Documents definits al tipus d'expedient
		for (Document dTipExp: documentsTipusExpedient) {
			documentCodis.add(dTipExp.getCodi());
			CampFormProperties documentFormProperties = documentsFormProperties.get(dTipExp.getCodi());
			if (!tots && documentFormProperties != null && !documentFormProperties.isVisible())
				continue;

			ExpedientDocumentDto dExp = getDocumentExpedient(documentsExpedient, dTipExp.getCodi());
			PortasignaturesDto dPsigna = dExp != null ? getDocumentPsignaPendent(documentsPsignaPendent, dExp.getId()) : null;

			DocumentListDto document = null;
			if (dExp == null) {
				document = DocumentListDto.builder()
						.codi(dTipExp.getCodi())
						.nom(dTipExp.getNom())
						.tipoDocumental(dTipExp.getNtiTipoDocumental())
						.processInstanceId(processInstanceId)
						.expedientId(expedientId)
						.visible(documentFormProperties != null ? documentFormProperties.isVisible() : true)
						.editable(documentFormProperties != null ? documentFormProperties.isEditable() : true)
						.obligatori(documentFormProperties != null ? documentFormProperties.isObligatori() : false)
						.build();
			} else {
				document = toDocumentList(expedient, processInstanceId, dTipExp, dExp, dPsigna, documentFormProperties);
			}
			documents.add(document);
		}

		// Documents adjunts
		for(ExpedientDocumentDto dExp: documentsExpedient) {
			if (documentCodis.contains(dExp.getDocumentCodi()))
				continue;

			CampFormProperties documentFormProperties = documentsFormProperties.get(dExp.getDocumentCodi());
			if (documentFormProperties != null && !documentFormProperties.isVisible())
				continue;

			PortasignaturesDto dPsigna = dExp != null ? getDocumentPsignaPendent(documentsPsignaPendent, dExp.getId()) : null;

			DocumentListDto document = toDocumentList(expedient, processInstanceId, null, dExp, dPsigna, documentFormProperties);
			documents.add(document);
		}

		List<OrdreDto> ordres = paginacioParams.getOrdres();
		OrdreDto ordreDto = null;
		OrdreDto ordreDto2 = null;

		if (ordres != null && !ordres.isEmpty()) {
			ordreDto = ordres.get(0);
			if (ordres.size() > 1)
				ordreDto2 = ordres.get(1);
		}

		final String ordre = ordreDto != null ? ordreDto.getCamp() : "nom";
		final String ordre2 = ordreDto2 != null ? ordreDto2.getCamp() : null;
		final OrdreDireccioDto direccio = ordreDto != null ? ordreDto.getDireccio() : OrdreDireccioDto.ASCENDENT;
		final OrdreDireccioDto direccio2 = ordreDto2 != null ? ordreDto2.getDireccio() : null;

		Collections.sort(documents, new Comparator<DocumentListDto>() {
			@Override
			public int compare(DocumentListDto o1, DocumentListDto o2) {
				int result = compareDocByCamp(o1, o2, ordre);
				if (result != 0 || ordre2 == null)
					return OrdreDireccioDto.ASCENDENT.equals(direccio) ? result : -result;
				result = compareDocByCamp(o1, o2, ordre2);
				return OrdreDireccioDto.ASCENDENT.equals(direccio2) ? result : -result;
			}
		});

		return documents;
    }

	private int compareDocByCamp(DocumentListDto o1, DocumentListDto o2, String camp) {
		int result = 0;
		if ("nom".equals(camp)) {
			result = o1.getNom() == null ? -1 : o2.getNom() == null ? 1 : o1.getNom().toUpperCase().compareTo(o2.getNom().toUpperCase());
		} else if ("tipoDocumental".equals(camp)) {
			result = o1.getTipoDocumental() == null ? -1 : o2.getTipoDocumental() == null ? 1 : o1.getTipoDocumental().name().toUpperCase().compareTo(o2.getTipoDocumental().name().toUpperCase());
		} else if ("dataCreacio".equals(camp)) {
			result = o1.getDataCreacio() == null ? -1 : o2.getDataCreacio() == null ? 1 : o1.getDataCreacio().compareTo(o2.getDataCreacio());
		} else if ("dataDocument".equals(camp)) {
			result = o1.getDataDocument() == null ? -1 : o2.getDataDocument() == null ? 1 : o1.getDataDocument().compareTo(o2.getDataDocument());
		}
		return result;
	}

	private static DocumentListDto toDocumentList(Expedient expedient, String processInstanceId, Document dTipExp, ExpedientDocumentDto dExp, PortasignaturesDto dPsigna, CampFormProperties documentFormProperties) {
		DocumentListDto document;
		document = DocumentListDto.builder()
				.id(dExp.getId())
				.codi(dExp.getDocumentCodi())
				.nom(dExp.isAdjunt() ? dExp.getAdjuntTitol() : dExp.getDocumentNom())
				.dataCreacio(dExp.getDataCreacio())
				.dataDocument(dExp.getDataDocument())
				.tipoDocumental(dTipExp != null ? dTipExp.getNtiTipoDocumental() : dExp.getNtiTipoDocumental())
				.processInstanceId(processInstanceId)
				.expedientId(expedient.getId())
				.adjunt(dExp.isAdjunt())
				.extensio(dExp.getArxiuExtensio() != null ? dExp.getArxiuExtensio().toUpperCase() : null)
				.signat(dExp.isSignat())
				.notificable(dExp.isNotificable())
				.notificat(dExp.isNotificat())
				.arxiuActiu(dExp.isArxiuActiu())
				.ntiActiu(expedient.isNtiActiu())
				.registrat(dExp.isRegistrat())
				.docValid(dExp.isDocumentValid())
				.psError(dPsigna != null ? dPsigna.isError() : false)
				.signUrlVer(dExp.getSignaturaUrlVerificacio())
				.psPendent(dPsigna != null)
				.ntiCsv(dExp.getNtiCsv())
				.psEstat(dPsigna != null ? dPsigna.getEstat() : null)
				.psDocId(dPsigna != null ? dPsigna.getDocumentId() : null)
				.arxiuUuid(dExp.getArxiuUuid())
				.expUuid(expedient.getArxiuUuid())
				.anotacioId(dExp.getAnotacioId())
				.anotacioIdf(dExp.getAnotacioIdentificador())
				.error(dExp.getError())
				.docError(dExp.getDocumentError())
				.visible(documentFormProperties != null ? documentFormProperties.isVisible() : true)
				.editable(documentFormProperties != null ? documentFormProperties.isEditable() : true)
				.obligatori(documentFormProperties != null ? documentFormProperties.isObligatori() : false)
				.build();
		return document;
	}

	private ExpedientDocumentDto getDocumentExpedient(List<ExpedientDocumentDto> documentsExpedient, String documentCodi) {
		for(ExpedientDocumentDto docExpedient: documentsExpedient) {
			if (docExpedient.getDocumentCodi() != null && docExpedient.getDocumentCodi().equals(documentCodi))
				return docExpedient;
		}
		return null;
	}

	private PortasignaturesDto getDocumentPsignaPendent(List<PortasignaturesDto> documentsPsignaPendent, Long documentStoreId) {
		for (PortasignaturesDto docPsigna: documentsPsignaPendent) {
			if (docPsigna.getDocumentStoreId().equals(documentStoreId))
				return docPsigna;
		}
		return null;
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
	
	@Override
	@Transactional(readOnly = true)
	public ArxiuDto arxiuPdfFindAmbDocument(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId) {
		ArxiuDto arxiuDto = arxiuFindAmbDocument(expedientId, processInstanceId, documentStoreId);
		if ("application/pdf".equals(arxiuDto.getTipusMime()))
			return arxiuDto;
		return documentHelperV3.converteixPdf(arxiuDto);
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
	public List<PortasignaturesDto> getPortasignaturesByProcessInstanceAndDocumentStoreId(
			String processInstanceId,
			Long documentStoreId) {
		List<Portasignatures> portasignatures = new ArrayList<Portasignatures>();
		if (!Strings.isNullOrEmpty(processInstanceId) && documentStoreId != null) {
			portasignatures = portasignaturesRepository.findByProcessInstanceIdAndDocumentStoreId(
					processInstanceId,
					documentStoreId);
			}
		return conversioTipusHelper.convertirList(portasignatures, PortasignaturesDto.class);
	
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

	@Override
	@Transactional(readOnly = true)
	public DocumentDetallDto getDocumentDetalls(Long expedientId, Long documentStoreId) {
		// Expedient
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		// Document
		ExpedientDocumentDto document = findOneAmbInstanciaProces(
				expedientId,
				expedient.getProcessInstanceId(),
				documentStoreId);

		// Detalls del document
		DocumentDetallDto.DocumentDetallDtoBuilder documentDetallBuilder = DocumentDetallDto.builder()
				.documentStoreId(documentStoreId)
				.documentNom(document.getDocumentNom())
				.arxiuNom(document.getArxiuNom())
				.extensio(document.getArxiuExtensio())
				.adjunt(document.isAdjunt())
				.adjuntTitol(document.getAdjuntTitol())
				.dataCreacio(document.getDataCreacio())
				.dataModificacio(document.getDataModificacio())
				.dataDocument(document.getDataDocument())
				.notificable(document.isNotificable())
				.arxiuUuid(document.getArxiuUuid())
				.ntiCsv(document.getNtiCsv())
				.nti(expedient.isNtiActiu() && expedient.getArxiuUuid() == null)
				.arxiu(expedient.isNtiActiu() && expedient.getArxiuUuid() != null)
				.signat(document.isSignat())
				.registrat(document.isRegistrat())
				.deAnotacio(document.getAnotacioId() != null)
				.notificat(document.isNotificat())
				.notificable(document.isNotificable())
				.deAnotacio(document.getAnotacioId() != null);



		// Registre
		if (document.isRegistrat()) {
			documentDetallBuilder.registreDetall(RegistreDetallDto.builder()
					.registreOficinaNom(document.getRegistreOficinaNom())
					.registreData(document.getRegistreData())
					.registreEntrada(document.isRegistreEntrada())
					.registreNumero(document.getRegistreNumero())
					.build());
		}

		// NTI
		documentDetallBuilder.ntiDetall(NtiDetallDto.builder()
				.ntiVersion(document.getNtiVersion())
				.ntiIdentificador(document.getNtiIdentificador())
				.ntiOrgano(document.getNtiOrgano())
				.ntiOrigen(document.getNtiOrigen())
				.ntiEstadoElaboracion(document.getNtiEstadoElaboracion())
				.ntiNombreFormato(document.getNtiNombreFormato())
				.ntiTipoDocumental(document.getNtiTipoDocumental())
				.ntiIdOrigen(document.getNtiIdOrigen())
				.ntiTipoFirma(document.getNtiTipoFirma())
				.ntiCsv(document.getNtiCsv())
				.ntiDefinicionGenCsv(document.getNtiDefinicionGenCsv())
				.arxiuUuid(document.getArxiuUuid())
				.build());
		if (expedient.isArxiuActiu()) {
			if (!StringUtils.isEmpty(document.getArxiuUuid())) {
				documentDetallBuilder.arxiuDetall(getArxiuDetall(
						expedientId,
						expedient.getProcessInstanceId(),
						documentStoreId));
			} else {
				documentDetallBuilder.errorArxiuNoUuid(true);
			}
			if (expedient.getNtiOrgano() == null) {
				documentDetallBuilder.errorMetadadesNti(true);
			}
		}

		// Signatura / Url de verificació
		if (document.isSignat()) {
			SignaturaValidacioDetallDto.SignaturaValidacioDetallDtoBuilder signaturaValidacioDetallBuilder = SignaturaValidacioDetallDto.builder().signat(true);
			if (expedient.isArxiuActiu()) {
				if (document.getNtiCsv() != null) {
					signaturaValidacioDetallBuilder.urlVerificacio(document.getSignaturaUrlVerificacio());
				} else {
					// La crida a arxiuDetall ha actualitzar el CSV al documnet. Per tant el tornam a consultar
					ExpedientDocumentDto expedientDocument = findOneAmbInstanciaProces(
							expedientId,
							expedient.getProcessInstanceId(),
							documentStoreId);
					signaturaValidacioDetallBuilder.urlVerificacio("redirect:" + expedientDocument.getSignaturaUrlVerificacio());
				}
			} else {
				if (!StringUtils.isEmpty(document.getSignaturaUrlVerificacio())) {
					signaturaValidacioDetallBuilder.urlVerificacio(document.getSignaturaUrlVerificacio());
				} else {
					DocumentDto signatura = findDocumentAmbId(documentStoreId);
					signaturaValidacioDetallBuilder.tokenSignatura(signatura.getTokenSignatura());
					DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
					if (documentStore == null)
						throw new NoTrobatException(DocumentStore.class, documentStoreId);
					List<RespostaValidacioSignaturaDto> signatures = documentHelper.getRespostasValidacioSignatura(documentStore);
					List<SignaturaDetallDto> signaturaValidacioDetall = new ArrayList<SignaturaDetallDto>();
					for (RespostaValidacioSignaturaDto sign : signatures) {
						String nomResponsable = null;
						String nifResponsable = null;
						if (sign.getDadesCertificat() != null && !sign.getDadesCertificat().isEmpty()) {
							nomResponsable = sign.getDadesCertificat().get(0).getNombreCompletoResponsable();
							nifResponsable = sign.getDadesCertificat().get(0).getNifResponsable();
						}
						signaturaValidacioDetall.add(SignaturaDetallDto.builder()
								.estatOk(sign.isEstatOk())
								.nomResponsable(nomResponsable)
								.nifResponsable(nifResponsable)
								.build());
					}
					signaturaValidacioDetallBuilder.signatures(signaturaValidacioDetall);
				}
			}
			documentDetallBuilder.signaturaValidacioDetall(signaturaValidacioDetallBuilder.build());
		}

		// Psigna
		List<PortasignaturesDto> portasignatures = getPortasignaturesByProcessInstanceAndDocumentStoreId(
				expedient.getProcessInstanceId(),
				documentStoreId);
		if (!portasignatures.isEmpty()) {
			for (PortasignaturesDto peticio : portasignatures) {
				if (!"PROCESSAT".equals(peticio.getEstat())) {
					documentDetallBuilder
					.psignaPendent(!"PROCESSAT".equals(peticio.getEstat()))
					.psignaDetall(PsignaDetallDto.builder()
							.documentId(peticio.getDocumentId())
							.dataEnviat(peticio.getDataEnviat())
							.estat(peticio.getEstat())
							.error(peticio.isError())
							.errorProcessant(peticio.getErrorProcessant())
							.motiuRebuig(peticio.getMotiuRebuig())
							.dataProcessamentPrimer(peticio.getDataProcessamentPrimer())
							.dataProcessamentDarrer(peticio.getDataProcessamentDarrer())
							.build());
					break;
				}
			}
		} else {
			documentDetallBuilder.psignaPendent(false);
		}


		// Anotacio
		if (document.getAnotacioId() != null) {
			Anotacio anotacio = anotacioRepository.findOne(document.getAnotacioId());
			if (anotacio == null) {
				throw new NoTrobatException(Anotacio.class, document.getAnotacioId());
			}
			documentDetallBuilder.anotacio(conversioTipusHelper.convertir(anotacio, AnotacioDto.class));
		}

		// Notificacio
		if (document.isNotificat()) {
			 List<DocumentNotificacio> enviaments = documentNotificacioRepository.findByExpedientAndDocumentId(expedient, documentStoreId);
			 List<DadesNotificacioDto> notificaionsDetalls = new ArrayList<DadesNotificacioDto>();
			 for (DocumentNotificacio enviament: enviaments) {
				 notificaionsDetalls.add(notificacioHelper.toDadesNotificacioDto(enviament));
			 }
			 documentDetallBuilder.notificacions(notificaionsDetalls);
		}

		return documentDetallBuilder.build();
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
					case COMPAREIXENSA:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.COMPAREIXENSA);
						break;
					case CONVOCATORIA:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.CONVOCATORIA);
						break;
					case DICTAMEN_COMISSIO:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.DICTAMEN_COMISSIO);
						break;
					case ESCRIT:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.ESCRIT);
						break;
					case ESMENA:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.ESMENA);
						break;
					case INFORME_PONENCIA:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.INFORME_PONENCIA);
						break;
					case INICIATIVA_LEGISLATIVA:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.INICIATIVA_LEGISLATIVA);
						break;
					case INICIATIVA__LEGISLATIVA:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.INICIATIVA__LEGISLATIVA);
						break;
					case INSTRUCCIO:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.INSTRUCCIO);
						break;
					case INTERPELACIO:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.INTERPELACIO);
						break;
					case LLEI:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.LLEI);
						break;
					case MOCIO:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.MOCIO);
						break;
					case ORDRE_DIA:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.ORDRE_DIA);
						break;
					case PETICIO:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.PETICIO);
						break;
					case PREGUNTA:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.PREGUNTA);
						break;
					case PROPOSICIO_NO_LLEI:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.PROPOSICIO_NO_LLEI);
						break;
					case PROPOSTA_RESOLUCIO:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.PROPOSTA_RESOLUCIO);
						break;
					case RESPOSTA:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.RESPOSTA);
						break;
					case SOLICITUD_INFORMACIO:
						arxiuDetall.setEniTipusDocumental(NtiTipoDocumentalEnumDto.SOLICITUD_INFORMACIO);
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
	
	@Override
	@Transactional(readOnly = true)
	public Set<Long> findIdsDocumentsByExpedient(Long expedientId) {
		Set<Long> documentStoreIds = new HashSet<Long>();
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {ExtendedPermission.READ});
		Map<String, Object> variables = jbpmHelper.getProcessInstanceVariables(expedient.getProcessInstanceId());
		if (variables != null) {
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
			for (String var: variables.keySet()) {
				Long documentStoreId = (Long)variables.get(var);
				if (documentStoreId != null)
					documentStoreIds.add(documentStoreId);
			}
		}
		return documentStoreIds;
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
	
        @Override
		@Transactional(readOnly = true)
        public List<DocumentInfoDto> getDocumentsNoUtilitzatsPerEstats(Long expedientId) {
			logger.debug("Consultant els documents no utilitzats de l'expedient (expedientId=" + expedientId + ")");
			List<DocumentInfoDto> documentsNoUtilitzats = new ArrayList<DocumentInfoDto>();
			Expedient expedient = expedientHelper.getExpedientComprovantPermisos(expedientId, true, false, false, false);

			List<Document> documents = documentRepository.findByExpedientTipusId(expedient.getTipus().getId());
			List<ExpedientDocumentDto> documentsExpedient = documentHelper.findDocumentsPerInstanciaProces(expedient.getProcessInstanceId());
			Map<String, CampFormProperties> documentFormProperties = reglaHelper.getDocumentFormProperties(expedient.getTipus(), expedient.getEstat());

			if (documentsExpedient != null && !documentsExpedient.isEmpty()) {
				// Posa els codis dels documents utilitzats en un Set
				Set<String> codisDocumentsExistents = new HashSet<String>();
				for (ExpedientDocumentDto documentExpedient : documentsExpedient)
					codisDocumentsExistents.add(documentExpedient.getDocumentCodi());
				// Mira quins documents no s'han utilitzat i els retorna
				for(Document document: documents)
					if (!codisDocumentsExistents.contains(document.getCodi()))
						documentsNoUtilitzats.add(toDocumentInfo(document, documentFormProperties.get(document.getCodi())));
				return documentsNoUtilitzats;
			} else {
				for(Document document: documents) {
					documentsNoUtilitzats.add(toDocumentInfo(document, documentFormProperties.get(document.getCodi())));
				}
			}
            return documentsNoUtilitzats;
        }

		private DocumentInfoDto toDocumentInfo(Document document, CampFormProperties campFormProperties) {
			return DocumentInfoDto.builder()
					.codi(document.getCodi())
					.documentNom(document.getNom())
					.plantilla(document.isPlantilla())
					.ntiOrigen(document.getNtiOrigen())
					.ntiEstadoElaboracion(document.getNtiEstadoElaboracion())
					.ntiTipoDocumental(document.getNtiTipoDocumental())
					.generarNomesTasca(document.isGenerarNomesTasca())
					.visible(campFormProperties != null ? campFormProperties.isVisible() : true)
					.editable(campFormProperties != null ? campFormProperties.isEditable() : true)
					.obligatori(campFormProperties != null ? campFormProperties.isObligatori() : false)
					.build();
		}

	@Override
	public String firmaSimpleWebStart(PersonaDto persona, ArxiuDto arxiu, String motiu, String lloc, String urlRetorn) {

		return pluginHelper.firmaSimpleWebStart(
				arxiu,
				motiu,
				lloc,
				persona, 
				urlRetorn);
	}

	@Override
	public FirmaResultatDto firmaSimpleWebEnd(String transactionID) {

		return pluginHelper.firmaSimpleWebEnd(transactionID);
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientDocumentServiceImpl.class);


}
