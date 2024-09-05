                                                                                                                                                                              /**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.ExpedientTipusHelper;
import net.conselldemallorca.helium.core.helper.HerenciaHelper;
import net.conselldemallorca.helium.core.helper.NotificacioHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Interessat;
import net.conselldemallorca.helium.core.model.hibernate.PeticioPinbal;
import net.conselldemallorca.helium.core.model.hibernate.ServeiPinbalEntity;
import net.conselldemallorca.helium.core.model.hibernate.UnitatOrganitzativa;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.core.util.StringUtilsHelium;
import net.conselldemallorca.helium.integracio.plugins.pinbal.DadesConsultaPinbal;
import net.conselldemallorca.helium.integracio.plugins.pinbal.Funcionari;
import net.conselldemallorca.helium.integracio.plugins.pinbal.Titular;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesConsultaPinbalDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentPinbalDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalEstatEnum;
import net.conselldemallorca.helium.v3.core.api.dto.PinbalServeiEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.ScspRespostaPinbal;
import net.conselldemallorca.helium.v3.core.api.dto.Sexe;
import net.conselldemallorca.helium.v3.core.api.dto.TitularDto.ScspTipoDocumentacion;
import net.conselldemallorca.helium.v3.core.api.dto.regles.QueEnum;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.service.DocumentService;
import net.conselldemallorca.helium.v3.core.regles.ReglaHelper;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentStoreRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.InteressatRepository;
import net.conselldemallorca.helium.v3.core.repository.PeticioPinbalRepository;
import net.conselldemallorca.helium.v3.core.repository.ServeiPinbalRepository;
import net.conselldemallorca.helium.v3.core.repository.UnitatOrganitzativaRepository;

/**
 * Implementació del servei per a gestionar documents dels tipus d'expedients o definicions de procés.
 * 
 */
@Service
public class DocumentServiceImpl implements DocumentService {

	@Resource private DocumentRepository documentRepository;
	@Resource private ExpedientTipusRepository expedientTipusRepository;
	@Resource private DefinicioProcesRepository definicioProcesRepository;
	@Resource private CampRepository campRepository;
	@Resource private ExpedientTipusHelper expedientTipusHelper;
	@Resource private PaginacioHelper paginacioHelper;
	@Resource private ConversioTipusHelper conversioTipusHelper;
	@Resource private NotificacioHelper notificacioHelper;
	@Resource private ExpedientHelper expedientHelper;
	@Resource private PluginHelper pluginHelper;
	@Resource private ReglaHelper reglaHelper;
	@Resource(name = "documentHelperV3") private DocumentHelperV3 documentHelper;
	@Resource private ServeiPinbalRepository serveiPinbalRepository;
	@Resource private InteressatRepository interessatRepository;
	@Resource private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Resource private PeticioPinbalRepository peticioPinbalRepository;
	@Resource private DocumentStoreRepository documentStoreRepository;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<DocumentDto> findPerDatatable(
			Long expedientTipusId,
			Long definicioProcesId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant els documents per al tipus d'expedient per datatable (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"definicioProcesId =" + definicioProcesId + ", " +
				"filtre=" + filtre + ")");

		ExpedientTipus expedientTipus = expedientTipusId != null? expedientTipusHelper.getExpedientTipusComprovantPermisDissenyDelegat(expedientTipusId) : null;
		// Determina si hi ha herència 
		boolean ambHerencia = HerenciaHelper.ambHerencia(expedientTipus);

		Page<Document> page = documentRepository.findByFiltrePaginat(
				expedientTipusId,
				definicioProcesId,
				filtre == null || "".equals(filtre),
				filtre,
				ambHerencia,
				paginacioHelper.toSpringDataPageable(
						paginacioParams));
		
		PaginaDto<DocumentDto> pagina =  paginacioHelper.toPaginaDto(
						page,
						DocumentDto.class);
		// completa la informació d'herència
		if (ambHerencia) {
			// Llista d'heretats
			Set<Long> heretatsIds = new HashSet<Long>();
			for (Document d : page.getContent())
				if ( !expedientTipusId.equals(d.getExpedientTipus().getId()))
					heretatsIds.add(d.getId());
			// Llistat d'elements sobreescrits
			Set<String> sobreescritsCodis = new HashSet<String>();
			if (ambHerencia)
				for (Document d : documentRepository.findSobreescrits(expedientTipus.getId())) 
					sobreescritsCodis.add(d.getCodi());
			// Completa l'informació del dto
			for (DocumentDto dto : pagina.getContingut()) {
				// Sobreescriu
				if (sobreescritsCodis.contains(dto.getCodi()))
					dto.setSobreescriu(true);
				// Heretat
				if (heretatsIds.contains(dto.getId()) && ! dto.isSobreescriu())
					dto.setHeretat(true);								
			}
		}
		return pagina;
	}
	
	@Override
	@Transactional
	public DocumentDto create(
			Long expedientTipusId, 
			Long definicioProcesId, 
			DocumentDto document) {

		logger.debug(
				"Creant nou document per un tipus d'expedient (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"definicioProcesId =" + definicioProcesId + ", " +
				"document=" + document + ")");		
		Document entity = new Document();
		entity.setCodi(document.getCodi());
		entity.setNom(document.getNom());
		entity.setDescripcio(document.getDescripcio());
		entity.setPlantilla(document.isPlantilla());
		entity.setArxiuNom(document.getArxiuNom());
		entity.setArxiuContingut(document.getArxiuContingut());
		entity.setConvertirExtensio(document.getConvertirExtensio());
		entity.setAdjuntarAuto(document.isAdjuntarAuto());
		entity.setGenerarNomesTasca(document.isGenerarNomesTasca());
		if (document.getCampData() != null) {
			entity.setCampData(campRepository.findOne(document.getCampData().getId()));
		}
		entity.setExtensionsPermeses(document.getExtensionsPermeses());
		entity.setContentType(document.getContentType());
		entity.setCustodiaCodi(document.getCustodiaCodi());
		entity.setTipusDocPortasignatures(document.getTipusDocPortasignatures());
		entity.setIgnored(document.isIgnored());
  
		entity.setNtiOrigen(document.getNtiOrigen());
		entity.setNtiEstadoElaboracion(document.getNtiEstadoElaboracion());
		entity.setNtiTipoDocumental(document.getNtiTipoDocumental());
		entity.setPortafirmesActiu(document.isPortafirmesActiu());
		entity.setPortafirmesFluxId(document.getPortafirmesFluxId());									  
		entity.setPortafirmesFluxTipus(document.getPortafirmesFluxTipus());
		entity.setPortafirmesSequenciaTipus(document.getPortafirmesSequenciaTipus());
		entity.setPortafirmesResponsables(document.getPortafirmesResponsables()!=null? document.getPortafirmesResponsables().split(",") : null );
  
		if (expedientTipusId != null)
			entity.setExpedientTipus(expedientTipusRepository.findOne(expedientTipusId));
		if (definicioProcesId != null)
			entity.setDefinicioProces(definicioProcesRepository.findOne(definicioProcesId));

		entity.setPinbalActiu(document.isPinbalActiu());
		if (document.getPinbalServei()!=null) {
			ServeiPinbalEntity spe = serveiPinbalRepository.findByCodi(document.getPinbalServei());
			entity.setPinbalServei(spe.getId());
		}
		entity.setPinbalCifOrgan(document.isPinbalCifOrgan());
		if (document.getPinbalFinalitat()!=null) {
			entity.setPinbalFinalitat(document.getPinbalFinalitat().length()>250?document.getPinbalFinalitat().substring(0, 250):document.getPinbalFinalitat());
		} else {
			entity.setPinbalFinalitat("Tramitació d'expedient HELIUM.");
		}
		
		return conversioTipusHelper.convertir(
				documentRepository.save(entity),
				DocumentDto.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public DocumentDto findAmbCodi(
			Long expedientTipusId, 
			Long definicioProcesId, 
			String codi,
			boolean ambHerencia) {
		DocumentDto ret = null; 
		logger.debug(
				"Consultant el document del tipus d'expedient per codi (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"definicioProcesId =" + definicioProcesId + ", " +
				"codi = " + codi + ")");
		Document document = null;
		if (expedientTipusId != null)
			document = documentRepository.findByExpedientTipusAndCodi(
											expedientTipusId, 
											codi,
											ambHerencia);
		else if(definicioProcesId != null)
			document = documentRepository.findByDefinicioProcesAndCodi(
											definicioProcesRepository.findOne(definicioProcesId), 
											codi);

		if (document != null) {
			ret = conversioTipusHelper.convertir(
					document,
					DocumentDto.class);
			ret.setArxiuContingut(document.getArxiuContingut());
			//Portafirmes
			ret.setPortafirmesActiu(document.isPortafirmesActiu());
			ret.setPortafirmesFluxId(document.getPortafirmesFluxId());
			ret.setPortafirmesFluxNom(document.getPortafirmesFluxNom());
			ret.setPortafirmesFluxTipus(document.getPortafirmesFluxTipus());
			ret.setPortafirmesSequenciaTipus(document.getPortafirmesSequenciaTipus());
			ret.setPortafirmesResponsables(document.getPortafirmesResponsables());
		}
		return ret;
	}
	
	@Override
	@Transactional
	public void delete(Long documentId) {
		logger.debug(
				"Esborrant el document del tipus d'expedient (" +
				"documentId=" + documentId +  ")");
		Document entity = documentRepository.findOne(documentId);

		
		if (entity != null) {
			for (DocumentTasca documentTasca: entity.getTasques()) {
				documentTasca.getTasca().removeDocument(documentTasca);
				int i = 0;
				for (DocumentTasca dt: documentTasca.getTasca().getDocuments())
					dt.setOrder(i++);
			}
		} else {
			throw new NoTrobatException(Document.class);
		}
		documentRepository.delete(entity);	
		
		reglaHelper.deleteReglaValor(
				entity.getExpedientTipus(), 
				entity.getCodi() + " | " + entity.getNom(),
				QueEnum.DOCUMENT);

	}
	
	@Override
	@Transactional
	public DocumentDto findAmbId(
			Long expedientTipusId, 
			Long id) throws NoTrobatException {
		logger.debug(
				"Consultant el document del tipus d'expedient amb id (" +
				"expedientTiusId=" + expedientTipusId + "," +
				"documentId=" + id +  ")");
		Document document = documentRepository.findOne(id);
		if (document == null) {
			throw new NoTrobatException(Document.class, id);
		}
		DocumentDto dto = conversioTipusHelper.convertir(
				document,
				DocumentDto.class);
		dto.setArxiuContingut(document.getArxiuContingut());
		
		//Portafirmes
		dto.setPortafirmesActiu(document.isPortafirmesActiu());
		dto.setPortafirmesFluxTipus(document.getPortafirmesFluxTipus());
		dto.setPortafirmesFluxId(document.getPortafirmesFluxId());
		dto.setPortafirmesFluxNom(document.getPortafirmesFluxNom());
		dto.setPortafirmesSequenciaTipus(document.getPortafirmesSequenciaTipus());
		dto.setPortafirmesResponsables(document.getPortafirmesResponsables());
		// Herencia
		ExpedientTipus tipus = expedientTipusId != null? expedientTipusRepository.findOne(expedientTipusId) : null;
		if (tipus != null && tipus.getExpedientTipusPare() != null) {
			if (tipus.getExpedientTipusPare().getId().equals(document.getExpedientTipus().getId()))
				dto.setHeretat(true);
			else
				dto.setSobreescriu(documentRepository.findByExpedientTipusAndCodi(
						tipus.getExpedientTipusPare().getId(), 
						document.getCodi(),
						false) != null);					
		}
		
		if (document.isPinbalActiu()) {
			if (document.getPinbalServei()!=null) {
				ServeiPinbalEntity spe = serveiPinbalRepository.findOne(document.getPinbalServei());
				dto.setPinbalServei(spe!=null?spe.getCodi():null);
			}
			dto.setPinbalActiu(true);
			dto.setPinbalFinalitat(document.getPinbalFinalitat());
			dto.setPinbalCifOrgan(document.isPinbalCifOrgan());
		}
		
		return dto;
	}
	
	@Override
	@Transactional
	public DocumentDto update(
			DocumentDto document,
			boolean actualitzarContingut) {
		logger.debug(
				"Modificant el document del tipus d'expedient existent (" +
				"document.id=" + document.getId() + ", " +
				"document =" + document + ", " +
				"actualitzarContingut=" + actualitzarContingut + ")");
		Document entity = documentRepository.findOne(document.getId());
		
		reglaHelper.updateReglaValor(
				entity.getExpedientTipus(), 
				entity.getCodi() + " | " + entity.getNom(),
				document.getCodi() + " | " + document.getNom(),
				QueEnum.DOCUMENT);

		entity.setCodi(document.getCodi());
		entity.setNom(document.getNom());
		entity.setDescripcio(document.getDescripcio());
		entity.setPlantilla(document.isPlantilla());
		entity.setArxiuNom(document.getArxiuNom());
		if (actualitzarContingut) {
			entity.setArxiuContingut(document.getArxiuContingut());
		}
		entity.setConvertirExtensio(document.getConvertirExtensio());
		entity.setAdjuntarAuto(document.isAdjuntarAuto());
		entity.setGenerarNomesTasca(document.isGenerarNomesTasca());
		if (document.getCampData() != null) {
			entity.setCampData(campRepository.findOne(document.getCampData().getId()));
		} else {
			entity.setCampData(null);
		}
		entity.setIgnored(document.isIgnored());

		entity.setExtensionsPermeses(document.getExtensionsPermeses());
		entity.setContentType(document.getContentType());
		entity.setCustodiaCodi(document.getCustodiaCodi());
		entity.setTipusDocPortasignatures(document.getTipusDocPortasignatures());
				
		entity.setNtiOrigen(document.getNtiOrigen());
		entity.setNtiEstadoElaboracion(document.getNtiEstadoElaboracion());
		entity.setNtiTipoDocumental(document.getNtiTipoDocumental());
		if(document.getPortafirmesFluxTipus() != null) {
			entity.setPortafirmesFluxId(document.getPortafirmesFluxId());
			entity.setPortafirmesFluxNom(document.getPortafirmesFluxNom());
			entity.setPortafirmesFluxTipus(document.getPortafirmesFluxTipus());
			entity.setPortafirmesSequenciaTipus(document.getPortafirmesSequenciaTipus());
			if(document.getPortafirmesResponsables()!=null) {
				entity.setPortafirmesResponsables(document.getPortafirmesResponsables()!=null? document.getPortafirmesResponsables().split(",") : null );
			}
			entity.setPortafirmesActiu(document.isPortafirmesActiu());
		} else {
			entity.setPortafirmesFluxId(null);
			entity.setPortafirmesFluxNom(null);
			entity.setPortafirmesFluxTipus(null);
			entity.setPortafirmesSequenciaTipus(null);
			entity.setPortafirmesResponsables(null);	
			entity.setPortafirmesActiu(false);
		}
		
		entity.setPinbalActiu(document.isPinbalActiu());
		if (document.getPinbalServei()!=null) {
			ServeiPinbalEntity spe = serveiPinbalRepository.findByCodi(document.getPinbalServei());
			entity.setPinbalServei(spe.getId());
		}
		entity.setPinbalCifOrgan(document.isPinbalCifOrgan());
		if (document.getPinbalFinalitat()!=null) {
			entity.setPinbalFinalitat(document.getPinbalFinalitat().length()>250?document.getPinbalFinalitat().substring(0, 250):document.getPinbalFinalitat());
		} else {
			entity.setPinbalFinalitat("Tramitació d'expedient HELIUM.");
		}

		return conversioTipusHelper.convertir(
				documentRepository.save(entity),
				DocumentDto.class);
	}
	
//	private String getResponsablesFromArray(String[] responsables) {
//		StringBuilder responsablesStr = new StringBuilder();
//		if (responsables != null) {
//			for (String responsable: responsables) {
//				if (responsablesStr.length() > 0)
//					responsablesStr.append(",");
//				responsablesStr.append(responsable);
//			}
//		}
//		return responsablesStr.toString();
//	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto getArxiu(
			Long id) {
		logger.debug("obtenint contingut de l'arxiu pel document (" +
				"id=" + id + ")");
  
		Document document = documentRepository.findOne(id);
		if (document == null) {
			throw new NoTrobatException(Document.class,id);
		}
  
		ArxiuDto resposta = new ArxiuDto();
  
		resposta.setNom(document.getArxiuNom());
		resposta.setContingut(document.getArxiuContingut());
  
		return resposta;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<DocumentDto> findAll(
			Long expedientTipusId,
			Long definicioProcesId) {
		logger.debug("Consultant tots els documents (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"definicioProcesId =" + definicioProcesId + ")");
						
		return conversioTipusHelper.convertirList(
				expedientTipusId != null ?
						documentRepository.findByExpedientTipusId(expedientTipusId)
						: documentRepository.findByDefinicioProcesId(definicioProcesId), 
				DocumentDto.class);
	}		

	private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

	@Override
	public String createDocumentPinbal(ExpedientDocumentPinbalDto expedientDocumentPinbalDto) {

		/**
		//1.- Obtenir el expedient i validar permisos i que el document esta configurat
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientDocumentPinbalDto.getExpedientId(),
				new Permission[] {ExtendedPermission.DOC_MANAGE});
		
		Interessat interessat = interessatRepository.findOne(expedientDocumentPinbalDto.getInteressatId());
		ServeiPinbalEntity serveiPinbal = serveiPinbalRepository.findByCodi(expedientDocumentPinbalDto.getCodiServei());
		//enum ScspTipoDocumentacion { CIF, CSV, DNI, NIE, NIF, Pasaporte, NumeroIdentificacion, Otros}
		//enum TipusDocIdentSICRES { NIF ("N"), CIF ("C"), PASSAPORT ("P"), DOCUMENT_IDENTIFICATIU_ESTRANGERS ("E"), ALTRES_DE_PERSONA_FISICA ("X"), CODI_ORIGEN ("O"); }
		ScspTipoDocumentacion tipusDocumentacio = interessat.tipusInteressatCompatible(serveiPinbal);
		if (tipusDocumentacio==null) {
			return "consultes.pinbal.resultat.ko.interessat";
		}
		
		TitularDto titular = new TitularDto();
		titular.setDocumentacion(interessat.getDocumentIdent());
		titular.setScspTipoDocumentacion(tipusDocumentacio);
		titular.setTipoDocumentacion(tipusDocumentacio.toString());
		titular.setNombre(interessat.getNom());
		titular.setApellido1(interessat.getLlinatge1());
		titular.setApellido2(interessat.getLlinatge2());

		PersonaDto personaDto = pluginHelper.personaFindAmbCodi(SecurityContextHolder.getContext().getAuthentication().getName());
		FuncionariDto funcionari = new FuncionariDto();
		funcionari.setNifFuncionari(personaDto.getDni());
		funcionari.setNombreCompletFuncionari(personaDto.getNomSencer());
		funcionari.setPseudonim(personaDto.getCodi());
		
		String xmlDadesEspecifiques = null;
		switch (expedientDocumentPinbalDto.getCodiServei()) {
		case SVDDGPCIWS02:
			xmlDadesEspecifiques = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><DatosEspecificos><Consulta></Consulta></DatosEspecificos>";
			 break;
		default:
			xmlDadesEspecifiques = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><DatosEspecificos><Consulta></Consulta></DatosEspecificos>";
			break;
		}
		
		String codiUO=null;
		try
	    {
			if(expedient.getTipus().isProcedimentComu() && expedient.getUnitatOrganitzativa()!=null){
				codiUO = expedient.getUnitatOrganitzativa().getCodi();	
			} else {
				codiUO = expedient.getTipus().getNtiOrgano();
			}
			UnitatOrganitzativa uo = unitatOrganitzativaRepository.findByCodi(codiUO);
			codiUO= StringUtilsHelium.abreuja(uo.getDenominacio(), 64);
	    } catch (Exception e) {
	      logger.error("Error d'integraicó consultant la UO: " + 
	    		  codiUO!=null ? codiUO : expedient.getTipus().getNtiOrgano()
	    		+  " " + e.getMessage(), e);
	      logger.warn("Com a unitat tramitadora per la consulta a PINBAL es fixa el codi DIR3 " + expedient.getTipus().getNtiOrgano());
	      if (codiUO==null)
	    	  codiUO = expedient.getTipus().getNtiOrgano();
	    }
		
		DadesConsultaPinbalDto dadesConsultaPinbal = new DadesConsultaPinbalDto();
		dadesConsultaPinbal.setTitular(titular);
		dadesConsultaPinbal.setFuncionari(funcionari);
		dadesConsultaPinbal.setXmlDadesEspecifiques(xmlDadesEspecifiques);
		dadesConsultaPinbal.setCodiProcediment(expedient.getTipus().getNtiClasificacion());
		dadesConsultaPinbal.setDocumentCodi(expedientDocumentPinbalDto.getDocumentCodi());
		dadesConsultaPinbal.setFinalitat(expedientDocumentPinbalDto.getFinalitat());
		dadesConsultaPinbal.setConsentiment(expedientDocumentPinbalDto.getConsentiment());
		dadesConsultaPinbal.setServeiCodi(expedientDocumentPinbalDto.getCodiServei().toString());
		dadesConsultaPinbal.setEntitat_CIF(expedient.getTipus().getPinbalNifCif());
		dadesConsultaPinbal.setUnitatTramitadora(codiUO);
		dadesConsultaPinbal.setAsincrona(false);
		
		jbpm3HeliumHelper.consultaPinbal(dadesConsultaPinbal, expedient.getId(), expedient.getProcessInstanceId(), null);
		*/

		//1.- Obtenir el expedient i validar permisos i que el document esta configurat
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientDocumentPinbalDto.getExpedientId(),
				new Permission[] {ExtendedPermission.DOC_MANAGE});
		
		//2.- Comprovar que el interessat, té el tipus de document acceptat per el servei Pinbal.
		Interessat interessat = interessatRepository.findOne(expedientDocumentPinbalDto.getInteressatId());
		ServeiPinbalEntity serveiPinbal = serveiPinbalRepository.findByCodi(expedientDocumentPinbalDto.getCodiServei());
		//enum ScspTipoDocumentacion { CIF, CSV, DNI, NIE, NIF, Pasaporte, NumeroIdentificacion, Otros}
		//enum TipusDocIdentSICRES { NIF ("N"), CIF ("C"), PASSAPORT ("P"), DOCUMENT_IDENTIFICATIU_ESTRANGERS ("E"), ALTRES_DE_PERSONA_FISICA ("X"), CODI_ORIGEN ("O"); }
		ScspTipoDocumentacion tipusDocumentacio = interessat.tipusInteressatCompatible(serveiPinbal);
		if (tipusDocumentacio==null) {
			return "consultes.pinbal.resultat.ko.interessat";
		}
		
		//3.- Fer la petició pinbal (creara la petició pinbal i creara el document indicat amb el justificant)
		Titular titular = new Titular();
		titular.setDocumentacion(interessat.getDocumentIdent());
		titular.setTipusDocumentacion(tipusDocumentacio.toString());
		titular.setNombre(interessat.getNom());
		titular.setApellido1(interessat.getLlinatge1());
		titular.setApellido2(interessat.getLlinatge2());

		PersonaDto personaDto = pluginHelper.personaFindAmbCodi(SecurityContextHolder.getContext().getAuthentication().getName());
		Funcionari funcionari = new Funcionari();
		funcionari.setNifFuncionario(personaDto.getDni());
		funcionari.setNombreCompletoFuncionario(personaDto.getNomSencer());
		funcionari.setSeudonimo(personaDto.getCodi());
		
		String xmlDadesEspecifiques = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><DatosEspecificos>";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		switch (expedientDocumentPinbalDto.getCodiServei()) {
		case SVDCCAACPASWS01:
		case SVDCCAACPCWS01:
			xmlDadesEspecifiques += "<Consulta>";
			if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getComunitatAutonomaCodi())) {
				xmlDadesEspecifiques += "<CodigoComunidadAutonoma>"+expedientDocumentPinbalDto.getComunitatAutonomaCodi()+"</CodigoComunidadAutonoma>";
			}
			if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getProvinciaCodi())) {
				xmlDadesEspecifiques += "<CodigoProvincia>"+expedientDocumentPinbalDto.getProvinciaCodi()+"</CodigoProvincia>";
			}
			xmlDadesEspecifiques += "</Consulta>";
			break;
		case SVDDGPCIWS02:
			xmlDadesEspecifiques += "<Consulta></Consulta>";
			 break;
		case SVDSCDDWS01:
			xmlDadesEspecifiques += "<Consulta>";
			if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getComunitatAutonomaCodi())) {
				xmlDadesEspecifiques += "<CodigoComunidadAutonoma>"+expedientDocumentPinbalDto.getComunitatAutonomaCodi()+"</CodigoComunidadAutonoma>";
			}
			if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getProvinciaCodi())) {
				xmlDadesEspecifiques += "<CodigoProvincia>"+expedientDocumentPinbalDto.getProvinciaCodi()+"</CodigoProvincia>";
			}
//			if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getExpedient()) {
//				xmlDadesEspecifiques += "<Expediente>"+expedientDocumentPinbalDto.getProvinciaCodi()+"</Expediente>";
//			}
			if (expedientDocumentPinbalDto.getDataConsulta()!=null) {
				xmlDadesEspecifiques += "<FechaConsulta>"+sdf.format(expedientDocumentPinbalDto.getDataConsulta())+"</FechaConsulta>";
			}
			if (expedientDocumentPinbalDto.getDataNaixement()!=null) {
				xmlDadesEspecifiques += "<FechaNacimiento>"+sdf.format(expedientDocumentPinbalDto.getDataNaixement())+"</FechaNacimiento>";
			}
			if (expedientDocumentPinbalDto.isConsentimentTipusDiscapacitat()) {
				xmlDadesEspecifiques += "<ConsentimientoTiposDiscapacidad>S</ConsentimientoTiposDiscapacidad>";
			} else {
				xmlDadesEspecifiques += "<ConsentimientoTiposDiscapacidad>N</ConsentimientoTiposDiscapacidad>";
			}
			xmlDadesEspecifiques += "</Consulta>";
			break;
		case SCDCPAJU:
		case SCDHPAJU:
			xmlDadesEspecifiques += "<Solicitud>";
			if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getProvinciaCodi())) {
				xmlDadesEspecifiques += "<ProvinciaSolicitud>"+expedientDocumentPinbalDto.getProvinciaCodi()+"</ProvinciaSolicitud>";
			}
			if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getMunicipiCodi())) {
				xmlDadesEspecifiques += "<MunicipioSolicitud>"+expedientDocumentPinbalDto.getMunicipiCodi()+"</MunicipioSolicitud>";
			}
			xmlDadesEspecifiques += "<Titular><Documentacion>";
				//Tipus permesos (no es controla): El campo Tipo del nodo Documentacion puede tener los siguientes valores:  NIF DNI NIE Pasaporte
				xmlDadesEspecifiques += "<Tipo>"+titular.getTipusDocumentacion()+"</Tipo>";
				xmlDadesEspecifiques += "<Valor>"+titular.getDocumentacion()+"</Valor>";
			xmlDadesEspecifiques += "</Documentacion></Titular>";
			//La diferencia entre la solicitud normal o del historic, es que s'ha de indicar el nombre de anys.
			if (expedientDocumentPinbalDto.getCodiServei().equals(PinbalServeiEnumDto.SCDHPAJU)) {
				xmlDadesEspecifiques += "<NumeroAnyos>"+(expedientDocumentPinbalDto.getNombreAnysHistoric()!=null?expedientDocumentPinbalDto.getNombreAnysHistoric():1)+"</NumeroAnyos>";
			}
			xmlDadesEspecifiques += "</Solicitud>";
			break;
		case SVDSCTFNWS01:
			xmlDadesEspecifiques += "<Consulta>";
			if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getComunitatAutonomaCodi())) {
				xmlDadesEspecifiques += "<CodigoComunidadAutonoma>"+expedientDocumentPinbalDto.getComunitatAutonomaCodi()+"</CodigoComunidadAutonoma>";
			}
			if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getNumeroTitol())) {
				xmlDadesEspecifiques += "<NumeroTitulo>"+expedientDocumentPinbalDto.getNumeroTitol()+"</NumeroTitulo>";
			}
			if (expedientDocumentPinbalDto.getDataConsulta()!=null) {
				xmlDadesEspecifiques += "<FechaConsulta>"+sdf.format(expedientDocumentPinbalDto.getDataConsulta())+"</FechaConsulta>";
			}
			if (expedientDocumentPinbalDto.getDataNaixement()!=null) {
				xmlDadesEspecifiques += "<FechaNacimiento>"+sdf.format(expedientDocumentPinbalDto.getDataNaixement())+"</FechaNacimiento>";
			}
			xmlDadesEspecifiques += "</Consulta>";
			break;
		case SVDDELSEXWS01:
			
			//Tipus de documents permesos per el titular: NIF, NIE, Pasaporte
			if (!"NIF".equals(titular.getTipusDocumentacion()) && !"NIE".equals(titular.getTipusDocumentacion()) && !"Pasaporte".equals(titular.getTipusDocumentacion())) {
				titular.setTipusDocumentacion("NIF");
			}
			
			xmlDadesEspecifiques += "<Consulta>";
			if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getCodiNacionalitat())) {
				xmlDadesEspecifiques += "<Nacionalidad>"+expedientDocumentPinbalDto.getCodiNacionalitat()+"</Nacionalidad>";
			}
			if (expedientDocumentPinbalDto.getSexe()!=null) {
				if (expedientDocumentPinbalDto.getSexe().equals(Sexe.SEXE_HOME)) {
					xmlDadesEspecifiques += "<Sexo>H</Sexo>";
				} else {
					xmlDadesEspecifiques += "<Sexo>M</Sexo>";
				}
			}
			if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getNomPare())) {
				xmlDadesEspecifiques += "<NombrePadre>"+expedientDocumentPinbalDto.getNomPare()+"</NombrePadre>";
			}
			if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getNomMare())) {
				xmlDadesEspecifiques += "<NombreMadre>"+expedientDocumentPinbalDto.getNomMare()+"</NombreMadre>";
			}			
			if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getPaisNaixament())) {
				xmlDadesEspecifiques += "<PaisNacimiento>"+expedientDocumentPinbalDto.getPaisNaixament()+"</PaisNacimiento>";
			}
			if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getProvinciaNaixament())) {
				xmlDadesEspecifiques += "<ProvinciaNacimiento>"+expedientDocumentPinbalDto.getProvinciaNaixament()+"</ProvinciaNacimiento>";
			}
			if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getPoblacioNaixament())) {
				xmlDadesEspecifiques += "<PoblacionNacimiento>"+expedientDocumentPinbalDto.getPoblacioNaixament()+"</PoblacionNacimiento>";
			}
			if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getMunicipiNaixament())) {
				xmlDadesEspecifiques += "<CodPoblacionNacimiento>"+expedientDocumentPinbalDto.getMunicipiNaixementINE()+"</CodPoblacionNacimiento>";
			}			
			if (expedientDocumentPinbalDto.getDataNaixement()!=null) {
				xmlDadesEspecifiques += "<FechaNacimiento>"+sdf.format(expedientDocumentPinbalDto.getDataNaixement())+"</FechaNacimiento>";
			}
			if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getEmail())) {
				xmlDadesEspecifiques += "<Mail>"+expedientDocumentPinbalDto.getEmail()+"</Mail>";
			}
			if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getTelefon())) {
				xmlDadesEspecifiques += "<Telefono>"+expedientDocumentPinbalDto.getTelefon()+"</Telefono>";
			}
			xmlDadesEspecifiques += "</Consulta>";
			break;
		case NIVRENTI:
			xmlDadesEspecifiques += "<Consulta>";
			if (expedientDocumentPinbalDto.getExercici()!=null && expedientDocumentPinbalDto.getExercici()>0) {
				xmlDadesEspecifiques += "<Ejercicio>"+expedientDocumentPinbalDto.getExercici()+"</Ejercicio>";
			}
			xmlDadesEspecifiques += "</Consulta>";
			break;
		case Q2827003ATGSS001:
		case ECOT103:
			xmlDadesEspecifiques = null;
			break;
		case SVDDGPRESIDENCIALEGALDOCWS01:
			xmlDadesEspecifiques += "<Consulta>";
			if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getNumeroSoporte())) {
				xmlDadesEspecifiques += "<NumeroSoporte>"+expedientDocumentPinbalDto.getNumeroSoporte()+"</NumeroSoporte>";
			}
			xmlDadesEspecifiques += "<Pasaporte>";
				if (expedientDocumentPinbalDto.getTipusPassaport()!=null) {
					xmlDadesEspecifiques += "<Tipo>"+expedientDocumentPinbalDto.getTipusPassaport()+"</Tipo>";
				}
				if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getCodiNacionalitat())) {
					xmlDadesEspecifiques += "<Nacionalidad>"+expedientDocumentPinbalDto.getCodiNacionalitat()+"</Nacionalidad>";
				}			
				if (expedientDocumentPinbalDto.getDataExpedicion()!=null) {
					xmlDadesEspecifiques += "<FechaExpedicion>"+sdf.format(expedientDocumentPinbalDto.getDataExpedicion())+"</FechaExpedicion>";
				}
				if (expedientDocumentPinbalDto.getDataCaducidad()!=null) {
					xmlDadesEspecifiques += "<FechaCaducidad>"+sdf.format(expedientDocumentPinbalDto.getDataCaducidad())+"</FechaCaducidad>";
				}
			xmlDadesEspecifiques += "</Pasaporte>";
			xmlDadesEspecifiques += "</Consulta>";
			break;
		case SVDRRCCNACIMIENTOWS01:
		case SVDRRCCMATRIMONIOWS01:
		case SVDRRCCDEFUNCIONWS01:
			
			//Tipus de documents permesos per el titular: NIF, NIE, Pasaporte
			if (!"NIF".equals(titular.getTipusDocumentacion()) && !"NIE".equals(titular.getTipusDocumentacion()) && !"Pasaporte".equals(titular.getTipusDocumentacion())) {
				titular.setTipusDocumentacion("NIF");
			}

			xmlDadesEspecifiques += "<Consulta>";
			if (expedientDocumentPinbalDto.conteDadesAddicionals()) {
				xmlDadesEspecifiques += "<DatosAdicionalesTitular>";
				if (expedientDocumentPinbalDto.conteDadesFetRegistral()) {
					xmlDadesEspecifiques += "<HechoRegistral>";
					if (expedientDocumentPinbalDto.getDataRegistre()!=null) {
						xmlDadesEspecifiques += "<Fecha>"+sdf.format(expedientDocumentPinbalDto.getDataRegistre())+"</Fecha>";
					}
					if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getMunicipiNaixament())) {
						xmlDadesEspecifiques += "<Municipio><Codigo>"+sdf.format(expedientDocumentPinbalDto.getMunicipiNaixament())+"</Codigo></Municipio>";
					}
					xmlDadesEspecifiques += "</HechoRegistral>";
				}
				if (expedientDocumentPinbalDto.isAusenciaSegundoApellido()) {
					xmlDadesEspecifiques += "<AusenciaSegundoApellido>true</AusenciaSegundoApellido>";
				} else {
					xmlDadesEspecifiques += "<AusenciaSegundoApellido>false</AusenciaSegundoApellido>";
				}
				if (expedientDocumentPinbalDto.getSexe()!=null) {
					if (expedientDocumentPinbalDto.getSexe().equals(Sexe.SEXE_HOME)) {
						xmlDadesEspecifiques += "<Sexo>V</Sexo>";
					} else {
						xmlDadesEspecifiques += "<Sexo>M</Sexo>";
					}
				}
				if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getNomPare())) {
					xmlDadesEspecifiques += "<NombrePadre>"+expedientDocumentPinbalDto.getNomPare()+"</NombrePadre>";
				}
				if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getNomMare())) {
					xmlDadesEspecifiques += "<NombreMadre>"+expedientDocumentPinbalDto.getNomMare()+"</NombreMadre>";
				}
				xmlDadesEspecifiques += "</DatosAdicionalesTitular>";
			}
			
			if (expedientDocumentPinbalDto.conteDadesRegistrals()) {
				xmlDadesEspecifiques += "<DatosRegistrales>";
				if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getRegistreCivil())) {
					xmlDadesEspecifiques += "<RegistroCivil>"+sdf.format(expedientDocumentPinbalDto.getRegistreCivil())+"</RegistroCivil>";
				}
				if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getTom())) {
					xmlDadesEspecifiques += "<Tomo>"+sdf.format(expedientDocumentPinbalDto.getTom())+"</Tomo>";
				}
				if (!StringUtilsHelium.isEmpty(expedientDocumentPinbalDto.getPagina())) {
					xmlDadesEspecifiques += "<Pagina>"+sdf.format(expedientDocumentPinbalDto.getPagina())+"</Pagina>";
				}
				xmlDadesEspecifiques += "</DatosRegistrales>";
			}
			xmlDadesEspecifiques += "</Consulta>";
			break;
		case SVDBECAWS01:
			xmlDadesEspecifiques += "<Consulta>";
			if (expedientDocumentPinbalDto.getCurs()!=null) {
				xmlDadesEspecifiques += "<Curso>"+expedientDocumentPinbalDto.getCurs()+"</Curso>";
			}
			xmlDadesEspecifiques += "</Consulta>";
			break;
		default:
			break;
		}
		
		//En alguns serveis, no es necessita el XML de dades especifiques. Si es el cas i s'ha anulat el valor, no tancam el nodes.
		if (xmlDadesEspecifiques!=null)
			xmlDadesEspecifiques += "</DatosEspecificos>";
		
		String codiUO=null;
		try
	    {
			if(expedient.getTipus().isProcedimentComu() && expedient.getUnitatOrganitzativa()!=null){
				codiUO = expedient.getUnitatOrganitzativa().getCodi();	
			} else {
				codiUO = expedient.getTipus().getNtiOrgano();
			}
			UnitatOrganitzativa uo = unitatOrganitzativaRepository.findByCodi(codiUO);
			codiUO= StringUtilsHelium.abreuja(uo.getDenominacio(), 64);
	    } catch (Exception e) {
	      logger.error("Error d'integraicó consultant la UO: " + 
	    		  codiUO!=null ? codiUO : expedient.getTipus().getNtiOrgano()
	    		+  " " + e.getMessage(), e);
	      logger.warn("Com a unitat tramitadora per la consulta a PINBAL es fixa el codi DIR3 " + expedient.getTipus().getNtiOrgano());
	      if (codiUO==null)
	    	  codiUO = expedient.getTipus().getNtiOrgano();
	    }
		
		DadesConsultaPinbal dadesConsultaPinbal = new DadesConsultaPinbal(
				titular,
				funcionari,
				xmlDadesEspecifiques,
				expedientDocumentPinbalDto.getCodiServei().toString(),
				expedientDocumentPinbalDto.getDocumentCodi(),
				expedientDocumentPinbalDto.getFinalitat(),
				expedientDocumentPinbalDto.getConsentiment(),
				null, //interessatCodi
				expedient.getTipus().getNtiClasificacion(), //codiProcediment
				expedient.getTipus().getPinbalNifCif(), //entitat_CIF
				codiUO, //unitatTramitadora
				false, //asincrona
				null  //anyNaixement
				);
		//No volem guardar la petició pinbal amb error a la taula HEL_PETICIO_PINBAL.
		//Retornam el error i ja ho reintentarán desde la mateixa modal de document Pinbal.
		dadesConsultaPinbal.setGuardarError(false);
		
		ScspRespostaPinbal respostaPinbal = (ScspRespostaPinbal)pluginHelper.consultaPinbal(dadesConsultaPinbal, expedient, null);

		if(respostaPinbal.getJustificant()!=null) {
			
			//Si es un document de un expedient per fluxe, tendrem el PI com a atribut, en cas de estats, no el tendrem.
			String processInstanceId = expedientDocumentPinbalDto.getProcessInstanceId();
			if (StringUtilsHelium.isEmpty(processInstanceId)) {
				processInstanceId = expedient.getProcessInstanceId();
			}

			//Cream sempre un nou documentStore, que s'associará a la variable JBPM.
			//Aixi mantenim els dos documentStore de les peticions pinbal successives, pero al expedient nomes es veu la darrera.
			Long documentStoreJusificantId = documentHelper.crearDocument(
					null,
					processInstanceId,
					dadesConsultaPinbal.getDocumentCodi(),
					new Date(),
					false, //isAdjunt
					null,  //adjuntTitol
					respostaPinbal.getJustificant().getNom(),
					respostaPinbal.getJustificant().getContingut(),
					null, // arxiuUuid
					documentHelper.getContentType(respostaPinbal.getJustificant().getNom()),
					false, //ambFirma
					false, //firmaSeparada
					null,  //firmaContingut
					null,  //ntiOrigen
					null,  //ntiEstadoElaboracion
					null,  //ntiTipoDocumental
					null,  //ntiIdDocumentoOrigen
					true,  //documentValid
					null,  //documentError
					null,  //annexId
					null).getId(); //annexosPerNotificar
			
			if (documentStoreJusificantId!=null) {
				guardaPeticioPinbalSenseError(
						expedient,
						documentStoreRepository.findOne(documentStoreJusificantId),
						conversioTipusHelper.convertir(dadesConsultaPinbal, DadesConsultaPinbalDto.class),
						respostaPinbal.getIdPeticion(),
						Calendar.getInstance().getTime(),
						respostaPinbal.getDataProcessament(),
						null);
			}
		}
		
		return "consultes.pinbal.resultat.ok";
	}

	private void guardaPeticioPinbalSenseError(
			Expedient expedient,
			DocumentStore justificant,
			DadesConsultaPinbalDto dadesConsultaPinbal,
			String idPeticioPinbal,
			Date dataPeticio,
			Date dataPrevista,
			Long tokenId) {
		//Cream la petició pinbal per el historic de peticions
		PeticioPinbal peticio = new PeticioPinbal();
		peticio.setDataPeticio(dataPeticio);
		peticio.setAsincrona(dadesConsultaPinbal.isAsincrona());
		if (dadesConsultaPinbal.isAsincrona()) {
			peticio.setEstat(PeticioPinbalEstatEnum.PENDENT);
			peticio.setDataPrevista(dataPrevista);
		} else {
			peticio.setEstat(PeticioPinbalEstatEnum.TRAMITADA);
		}
		peticio.setExpedient(expedient);
		peticio.setTipus(expedient.getTipus());
		peticio.setEntorn(expedient.getTipus().getEntorn());
		peticio.setProcediment(dadesConsultaPinbal.getServeiCodi());
		peticio.setUsuari(SecurityContextHolder.getContext().getAuthentication().getName());
		peticio.setPinbalId(idPeticioPinbal);
		peticio.setDocument(justificant);
		peticio.setTokenId(tokenId);
		peticio.setTransicioOK(dadesConsultaPinbal.getTransicioOK());
		peticio.setTransicioKO(dadesConsultaPinbal.getTransicioKO());
		peticioPinbalRepository.save(peticio);
	}
}