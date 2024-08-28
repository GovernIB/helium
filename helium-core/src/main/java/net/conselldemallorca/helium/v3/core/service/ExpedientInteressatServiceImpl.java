package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Interessat;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatDocumentTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientInteressatService;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.InteressatRepository;

/**
 * Servei per gestionar els terminis dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExpedientInteressatServiceImpl implements ExpedientInteressatService {

	@Resource private InteressatRepository interessatRepository;	
	@Resource private ExpedientRepository expedientRepository;	
	@Resource private PaginacioHelper paginacioHelper;
	@Resource private ConversioTipusHelper conversioTipusHelper;
	@Resource private PluginHelper pluginHelper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public InteressatDto create(InteressatDto interessat) {
		
		logger.debug("Creant nou interessat (interessat=" + interessat + ")");
		
		Expedient expedient = expedientRepository.findOne(interessat.getExpedientId());
		
		Interessat interessatEntity = new Interessat(
			interessat.getId(),
			interessat.getCodi(),//!=null?interessat.getCodi():interessat.getDocumentIdent(),
			interessat.getNom(),
			interessat.getDocumentIdent(),
			interessat.getDir3Codi(),
			interessat.getLlinatge1(), 
			interessat.getLlinatge2(), 
			interessat.getTipus(),
			interessat.getEmail(), 
			interessat.getTelefon(),
			expedient,
			interessat.getEntregaPostal(),
			interessat.getEntregaTipus(),
			interessat.getLinia1(),
			interessat.getLinia2(),
			interessat.getCodiPostal(),
			interessat.getEntregaDeh(),
			interessat.getEntregaDehObligat(),
			interessat.getTipusDocIdent(),
			interessat.getDireccio(),
			interessat.getObservacions(),
			interessat.getEs_representant(),
			interessat.getRaoSocial(),
			interessat.getPais(),
			interessat.getProvincia(),
			interessat.getMunicipi(),
			interessat.getCanalNotif(),
			interessat.getCodiDire()
			);
		if(expedient.getInteressats()!=null)
			expedient.getInteressats().add(interessatEntity);
		else {
			List<Interessat> interessatsList = new ArrayList<Interessat>();
			interessatsList.add(interessatEntity);
			expedient.setInteressats(interessatsList);
		}
		if (expedient.isArxiuActiu()) {
			try {
				pluginHelper.arxiuExpedientCrearOrActualitzar(expedient);
			} catch (SistemaExternException seex) {
				expedient.setErrorArxiu("Error de sincronització amb arxiu al crear el interessat "+interessat.getDocumentIdent()+": "+seex.getPublicMessage());
			}
		}
		interessatEntity.setTipusDocIdent(InteressatDocumentTipusEnumDto.valueOf(interessatEntity.getTipusDocIdent()).getValor())    ;
		interessatEntity = interessatRepository.save(interessatEntity);
		InteressatDto resultat = conversioTipusHelper.convertir(interessatEntity, InteressatDto.class);
		return resultat;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public InteressatDto update(
			InteressatDto interessat) {
		logger.debug("Modificant interessat (interessat=" + interessat + ")");
		Interessat interessatEntity = interessatRepository.findOne(interessat.getId());
		interessatEntity.setCodi(interessat.getCodi());
		interessatEntity.setNom(interessat.getNom());
		interessatEntity.setDocumentIdent(interessat.getDocumentIdent());
		interessatEntity.setDir3Codi(interessat.getDir3Codi());
		interessatEntity.setCodiDire(interessat.getCodiDire());
		interessatEntity.setDocumentIdent(interessat.getDocumentIdent());
		interessatEntity.setLlinatge1(interessat.getLlinatge1());  
		interessatEntity.setLlinatge2(interessat.getLlinatge2());
		interessatEntity.setTipus(interessat.getTipus());
		interessatEntity.setEmail(interessat.getEmail());
		interessatEntity.setTelefon(interessat.getTelefon());
		interessatEntity.setEntregaPostal(interessat.getEntregaPostal());
		interessatEntity.setEntregaTipus(interessat.getEntregaTipus());
		interessatEntity.setLinia1(interessat.getLinia1());
		interessatEntity.setLinia2(interessat.getLinia2());
		interessatEntity.setCodiPostal(interessat.getCodiPostal());
		interessatEntity.setEntregaDeh(interessat.getEntregaDeh());
		interessatEntity.setEntregaDehObligat(interessat.getEntregaDehObligat());
		interessatEntity.setObservacions(interessat.getObservacions());
		interessatEntity.setTipusDocIdent(translateTipusDocIdentToSave(interessat.getTipusDocIdent()));
		interessatEntity.setCodiDire(interessat.getCodiDire());
		interessatEntity.setDireccio(interessat.getDireccio());
		interessatEntity.setRaoSocial(interessat.getRaoSocial());
		interessatEntity.setEs_representant(interessat.getEs_representant());
		interessatEntity.setPais(interessat.getPais());
		interessatEntity.setProvincia(interessat.getProvincia());
		interessatEntity.setMunicipi(interessat.getMunicipi());
		interessatEntity.setCanalNotif(interessat.getCanalNotif());


		Expedient expedient = expedientRepository.findOne(interessat.getExpedientId());
		if (expedient.isArxiuActiu()) {
			try {
				pluginHelper.arxiuExpedientCrearOrActualitzar(expedient);
			} catch (SistemaExternException seex) {
				expedient.setErrorArxiu("Error de sincronització amb arxiu al modificar el interessat "+interessat.getDocumentIdent()+": "+seex.getPublicMessage());
			}
		}
		
		return conversioTipusHelper.convertir(
				interessatEntity,
				InteressatDto.class);
	}
	
	private String translateTipusDocIdentToSave(String documentTipus) {
		String valorTraduit=null;
		if (documentTipus != null) {
			if(documentTipus.equals(InteressatDocumentTipusEnumDto.NIF.name()))
					valorTraduit=InteressatDocumentTipusEnumDto.NIF.getValor();
			else if(documentTipus.equals(InteressatDocumentTipusEnumDto.CIF.name()))
				valorTraduit=InteressatDocumentTipusEnumDto.NIF.getValor();
			else if(documentTipus.equals(InteressatDocumentTipusEnumDto.ALTRES_DE_PERSONA_FISICA.name()))
				valorTraduit=InteressatDocumentTipusEnumDto.ALTRES_DE_PERSONA_FISICA.getValor();
			else if(documentTipus.equals(InteressatDocumentTipusEnumDto.CODI_ORIGEN.name()))
				valorTraduit=InteressatDocumentTipusEnumDto.CODI_ORIGEN.getValor();
			else if(documentTipus.equals(InteressatDocumentTipusEnumDto.DOCUMENT_IDENTIFICATIU_ESTRANGERS.name()))
				valorTraduit=InteressatDocumentTipusEnumDto.DOCUMENT_IDENTIFICATIU_ESTRANGERS.getValor();
			else if(documentTipus.equals(InteressatDocumentTipusEnumDto.PASSAPORT.name()))
				valorTraduit=InteressatDocumentTipusEnumDto.PASSAPORT.getValor();
			}
		return valorTraduit;
	}

	public Interessat comprovarInteressat(
			Long interessatId) {
		Interessat interessat = interessatRepository.findOne(interessatId);
		if (interessat == null) {
			throw new NoTrobatException(
					Interessat.class,
					interessatId);
		}
		return interessat;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void delete(
			Long interessatId) {
		logger.debug("Esborrant interessat (interessatId=" + interessatId + ")");
		Interessat interessat = comprovarInteressat(interessatId);
		Expedient expedient = expedientRepository.findOne(interessat.getExpedient().getId());
		List<Interessat> interessats = expedient.getInteressats();
		List<Interessat> representants = interessatRepository.findByRepresentat(interessat);
		if(interessat.isEs_representant()) {
			//El desassignem del seu interessat representat (o varis reprsentats), i després l'Esborrem.
			List<Interessat> representats = interessatRepository.findByRepresentant(interessat);
			if(representats!=null && !representats.isEmpty())
			for(Interessat representat: representats) {
				representat.setRepresentant(null);
			}
			interessats.remove(interessat);		
		} else if (representants!=null && !representants.isEmpty()) {//si té respresentants primer els desassignem i després esborrem l'interessat
			//només hi pot haver un representant per interessat
			interessat.setRepresentant(null);
			interessats.remove(interessat);
			
		}
		else {
			interessats.remove(interessat);
		}
		expedient.setInteressats(interessats);
		interessatRepository.delete(interessat);
		
		if (expedient.isArxiuActiu()) {
			try {
				pluginHelper.arxiuExpedientCrearOrActualitzar(expedient);
			} catch (SistemaExternException seex) {
				expedient.setErrorArxiu("Error de sincronització amb arxiu al eliminar el interessat "+interessat.getDocumentIdent()+": "+seex.getPublicMessage());
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public InteressatDto findOne(
			Long interessatId) {
		Interessat interessat = interessatRepository.findOne(interessatId);
		if (interessat == null) {
			throw new NoTrobatException(
					Interessat.class,
					interessatId);
		}
		
		
		InteressatDto convertir = conversioTipusHelper.convertir(
				interessat,
				InteressatDto.class);

		return convertir;
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public InteressatDto findAmbCodiAndExpedientId(String codi, Long expedientId) {
		Expedient expedient = expedientRepository.findOne(expedientId);
		Interessat interessat = interessatRepository.findByCodiAndExpedient(codi, expedient);
		InteressatDto resultat = conversioTipusHelper.convertir(interessat, InteressatDto.class);
		return resultat;
	}
	
	@Override
	@Transactional
	public InteressatDto findByCodi(String codi) {
		Interessat interessat = interessatRepository.findByCodi(codi);
		InteressatDto resultat = conversioTipusHelper.convertir(interessat, InteressatDto.class);
		return resultat;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<InteressatDto> findPerDatatable(
			Long expedientId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consultant interessats per la datatable (" +
				"expedientId=" +expedientId + ", " +
				"filtre=" + filtre + ", " +
				"paginacioParams=" + paginacioParams + ")");
		
		Expedient expedient = expedientRepository.findOne(expedientId);
		PaginaDto<InteressatDto> pagina = paginacioHelper.toPaginaDto(
				interessatRepository.findByFiltrePaginat(
						expedient,
						filtre == null || "".equals(filtre),
						filtre,
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				InteressatDto.class);
		List<InteressatDto> representantsExpedient = findRepresentantsExpedient(expedientId);
		for (InteressatDto interessat : pagina.getContingut()) {
			// Setegem paràmetres que necessitem per el manteniment de representants
			interessat.setExisteixenRepresentantsExpedient(representantsExpedient!=null && !representantsExpedient.isEmpty());
			if(!interessat.getEs_representant() && interessat.getRepresentant()!=null) {
				InteressatDto representantInteressat  = findOne(interessat.getRepresentant().getId());
				interessat.setRepresentant(representantInteressat);
				interessat.setRepresentant_id(representantInteressat.getId());
			}
		}
		return pagina;
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<InteressatDto> findByExpedient(
			Long expedientId) {
		logger.debug("Consultant interessats per expedient (" +
				"expedientId=" + expedientId);
		
		Expedient expedient = expedientRepository.findOne(expedientId);
		
		return conversioTipusHelper.convertirList(interessatRepository.findByExpedient(
				expedient), InteressatDto.class);
	}
	



	private static final Logger logger = LoggerFactory.getLogger(ExpedientInteressatServiceImpl.class);

	@Override
	public List<String> checkMidaCampsNotificacio(List<Long> idsInteressats) {
		List<String> resultat = new ArrayList<String>();
		if (idsInteressats!=null) {
			for (Long intId: idsInteressats) {
				Interessat interessatEntity = interessatRepository.findOne(intId);
				if (InteressatTipusEnumDto.FISICA.equals(interessatEntity.getTipus())) {
					if (interessatEntity.getNom()!=null && interessatEntity.getNom().length()>30) {
						resultat.add("- El camp nom del interessat "+interessatEntity.getDocumentIdent()+" supera els 30 caracters.");
					}
				} else if (InteressatTipusEnumDto.JURIDICA.equals(interessatEntity.getTipus())) {
					if (interessatEntity.getNom()!=null && interessatEntity.getNom().length()>255) {
						resultat.add("- El camp raó social del interessat "+interessatEntity.getDocumentIdent()+" supera els 255 caracters.");
					}
				} else {
					//InteressatTipusEnumDto.ADMINISTRACIO
					if (interessatEntity.getNom()!=null && interessatEntity.getNom().length()>255) {
						resultat.add("- El camp nom del interessat "+interessatEntity.getDocumentIdent()+" supera els 255 caracters.");
					}
				}
				
				if (interessatEntity.getLlinatge1()!=null && interessatEntity.getLlinatge1().length()>30) {
					resultat.add("- El camp primer llinatge del interessat "+interessatEntity.getDocumentIdent()+" supera els 30 caracters.");
				}
				
				if (interessatEntity.getLlinatge2()!=null && interessatEntity.getLlinatge2().length()>30) {
					resultat.add("- El camp segon llinatge del interessat "+interessatEntity.getDocumentIdent()+" supera els 30 caracters.");
				}
				
				if (interessatEntity.getDocumentIdent()!=null && interessatEntity.getDocumentIdent().length()>9) {
					resultat.add("- El camp NIF de l'interessat "+interessatEntity.getDocumentIdent()+" supera els 9 caracters.");
				}
				
				if (interessatEntity.getDir3Codi()!=null && interessatEntity.getDir3Codi().length()>9) {
					resultat.add("- El camp Codi DIR3 del interessat "+interessatEntity.getDocumentIdent()+" supera els 9 caracters.");
				}
				
				if (interessatEntity.getEmail()!=null && interessatEntity.getEmail().length()>160) {
					resultat.add("- El camp Email de l'interessat "+interessatEntity.getDocumentIdent()+" supera els 160 caracters.");
				}
				
				if (interessatEntity.getTelefon()!=null && interessatEntity.getTelefon().length()>16) {
					resultat.add("- El camp Teléfon de l'interessat "+interessatEntity.getDocumentIdent()+" supera els 16 caracters.");
				}
			}
		}
		return resultat;
	}

	@Override
	public InteressatDto createRepresentant(Long interessatId, InteressatDto representant) {
		Interessat interessat = interessatRepository.findOne(interessatId);
		logger.debug("Creant nou representant per l'interessat (interessat=" + interessat + ")");
		
		Expedient expedient = expedientRepository.findOne(representant.getExpedientId());
		
		Interessat representantEntity = new Interessat(
			representant.getId(),
			representant.getCodi(), //!=null?interessat.getCodi():interessat.getDocumentIdent(),
			representant.getNom(),
			representant.getDocumentIdent(),
			representant.getDir3Codi(),
			representant.getLlinatge1(), 
			representant.getLlinatge2(), 
			representant.getTipus(),
			representant.getEmail(), 
			representant.getTelefon(),
			expedient,
			representant.getEntregaPostal(),
			representant.getEntregaTipus(),
			representant.getLinia1(),
			representant.getLinia2(),
			representant.getCodiPostal(),
			representant.getEntregaDeh(),
			representant.getEntregaDehObligat(),
			representant.getTipusDocIdent(),
			representant.getDireccio(),
			representant.getObservacions(),
			representant.getEs_representant(),
			representant.getRaoSocial(),
			representant.getPais(),
			representant.getProvincia(),
			representant.getMunicipi(),
			representant.getCanalNotif(),
			representant.getCodiDire()
			);
		representantEntity.setTipusDocIdent(InteressatDocumentTipusEnumDto.valueOf(representantEntity.getTipusDocIdent()).getValor())    ;
		if(representant.getEs_representant()) {
			interessat.setRepresentant(representantEntity);
			representantEntity.setRepresentat(interessat);
		}
		InteressatDto representantDto = conversioTipusHelper.convertir(representantEntity, InteressatDto.class);
		representantEntity = interessatRepository.save(representantEntity);
		interessat=interessatRepository.save(interessat);
		InteressatDto interessatDto =  conversioTipusHelper.convertir(interessat, InteressatDto.class);
		interessatDto.setExpedientId(interessat.getExpedient().getId());
		update(interessatDto);
		return representantDto;
	}

	
	@Override
	public InteressatDto findRepresentantAmbInteressatId(Long interessatId) {
		Interessat interessat = interessatRepository.findOne(interessatId);
		List<Interessat> representants = interessatRepository.findByRepresentat(interessat);
		if(representants!=null && !representants.isEmpty()) {//només hi pot haver un representant per interssat
			InteressatDto resultat = conversioTipusHelper.convertir(representants.get(0), InteressatDto.class);
			return resultat;
		}
		return null;
	}
	
	@Override
	public List<InteressatDto> findRepresentantsExpedient(Long expedientId) {
		Expedient expedient = expedientRepository.findOne(expedientId);
		return conversioTipusHelper.convertirList(interessatRepository.findRepresentantsByExpedient(expedient), InteressatDto.class);
	}


}
