                                                                                                                                                                              /**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

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
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.ExpedientTipusHelper;
import net.conselldemallorca.helium.core.helper.HerenciaHelper;
import net.conselldemallorca.helium.core.helper.NotificacioHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Interessat;
import net.conselldemallorca.helium.core.model.hibernate.ServeiPinbalEntity;
import net.conselldemallorca.helium.core.model.hibernate.UnitatOrganitzativa;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.core.util.StringUtilsHelium;
import net.conselldemallorca.helium.integracio.plugins.pinbal.DadesConsultaPinbal;
import net.conselldemallorca.helium.integracio.plugins.pinbal.Funcionari;
import net.conselldemallorca.helium.integracio.plugins.pinbal.Titular;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentPinbalDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TitularDto;
import net.conselldemallorca.helium.v3.core.api.dto.regles.QueEnum;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.service.DocumentService;
import net.conselldemallorca.helium.v3.core.regles.ReglaHelper;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.InteressatRepository;
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
	@Resource private ServeiPinbalRepository serveiPinbalRepository;
	@Resource private InteressatRepository interessatRepository;
	@Resource private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	
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

		//1.- Obtenir el expedient i validar permisos i que el document esta configurat
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientDocumentPinbalDto.getExpedientId(),
				new Permission[] {ExtendedPermission.DOC_MANAGE});
		
		//2.- Comprovar que el interessat, té el tipus de document acceptat per el servei Pinbal.
		Interessat interessat = interessatRepository.findOne(expedientDocumentPinbalDto.getInteressatId());
		ServeiPinbalEntity serveiPinbal = serveiPinbalRepository.findByCodi(expedientDocumentPinbalDto.getCodiServei());
		
//		switch (interessat.getTipus()) {
//		case value:
//			
//			break;
//
//		default:
//			break;
//		}
		
//		if (interessat!=null) {
//			return "consultes.pinbal.resultat.ko.interessat";
//		}
		
		//3.- Fer la petició pinbal (creara la petició pinbal i creara el document indicat amb el justificant)
		Titular titular = new Titular();
		titular.setDocumentacion(interessat.getNif());
		titular.setTipusDocumentacion(TitularDto.ScspTipoDocumentacion.NIF.toString());
		titular.setNombre(interessat.getNom());
		titular.setApellido1(interessat.getLlinatge1());
		titular.setApellido2(interessat.getLlinatge2());

		PersonaDto personaDto = pluginHelper.personaFindAmbCodi(SecurityContextHolder.getContext().getAuthentication().getName());
		Funcionari funcionari = new Funcionari();
		funcionari.setNifFuncionario(personaDto.getDni());
		funcionari.setNombreCompletoFuncionario(personaDto.getNomSencer());
		funcionari.setSeudonimo(personaDto.getCodi());
		
		String xmlDadesEspecifiques = null;
		switch (expedientDocumentPinbalDto.getCodiServei()) {
		case SVDDGPVIWS02:
			 xmlDadesEspecifiques = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><DatosEspecificos><Consulta></Consulta></DatosEspecificos>";
			 break;
		default:
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
		
		pluginHelper.consultaPinbal(dadesConsultaPinbal, expedient, null);
		
		return "consultes.pinbal.resultat.ok";
	}

}