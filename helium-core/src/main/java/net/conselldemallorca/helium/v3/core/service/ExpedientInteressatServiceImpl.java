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
import net.conselldemallorca.helium.v3.core.api.dto.InteressatDto;
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
			interessat.getCodi(),
			interessat.getNom(),
			interessat.getNif(),
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
			interessat.getEntregaDehObligat());
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
				expedient.setErrorArxiu("Error de sincronització amb arxiu al crear el interessat "+interessat.getNif()+": "+seex.getPublicMessage());
			}
		}
		return conversioTipusHelper.convertir(
				interessatRepository.save(interessatEntity),
				InteressatDto.class);
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
		interessatEntity.setNif(interessat.getNif());
		interessatEntity.setDir3Codi(interessat.getDir3Codi());
		interessatEntity.setNif(interessat.getNif());
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

		Expedient expedient = expedientRepository.findOne(interessat.getExpedientId());
		if (expedient.isArxiuActiu()) {
			try {
				pluginHelper.arxiuExpedientCrearOrActualitzar(expedient);
			} catch (SistemaExternException seex) {
				expedient.setErrorArxiu("Error de sincronització amb arxiu al modificar el interessat "+interessat.getNif()+": "+seex.getPublicMessage());
			}
		}
		
		return conversioTipusHelper.convertir(
				interessatEntity,
				InteressatDto.class);
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
		interessats.remove(interessat);
		expedient.setInteressats(interessats);
		interessatRepository.delete(interessat);
		
		if (expedient.isArxiuActiu()) {
			try {
				pluginHelper.arxiuExpedientCrearOrActualitzar(expedient);
			} catch (SistemaExternException seex) {
				expedient.setErrorArxiu("Error de sincronització amb arxiu al eliminar el interessat "+interessat.getNif()+": "+seex.getPublicMessage());
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
	public InteressatDto findAmbCodiAndExpedientId(
			String codi,
			Long expedientId
			) {
		
		Expedient expedient = expedientRepository.findOne(expedientId);
		
		return conversioTipusHelper.convertir(
				interessatRepository.findByCodiAndExpedient(
						codi, 
						expedient),
				InteressatDto.class);
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

}
