package es.caib.helium.logic.service;

import es.caib.helium.logic.helper.ConversioTipusServiceHelper;
import es.caib.helium.logic.helper.PaginacioHelper;
import es.caib.helium.logic.intf.dto.InteressatDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.service.ExpedientInteressatService;
import es.caib.helium.persist.entity.Expedient;
import es.caib.helium.persist.entity.Interessat;
import es.caib.helium.persist.repository.ExpedientRepository;
import es.caib.helium.persist.repository.InteressatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Servei per gestionar els terminis dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExpedientInteressatServiceImpl implements ExpedientInteressatService {

	@Resource
	private InteressatRepository interessatRepository;	

	@Resource
	private ExpedientRepository expedientRepository;	

	@Resource
	private PaginacioHelper paginacioHelper;
	
	@Resource
	private ConversioTipusServiceHelper conversioTipusServiceHelper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public InteressatDto create(
			InteressatDto interessat) {
		logger.debug("Creant nou interessat (interessat=" + interessat + ")");
		
		Expedient expedient = expedientRepository.getById(interessat.getExpedientId());
		
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

		return conversioTipusServiceHelper.convertir(
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
		Interessat interessatEntity = interessatRepository.getById(interessat.getId());
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

		return conversioTipusServiceHelper.convertir(
				interessatEntity,
				InteressatDto.class);
	}
	

	public Interessat comprovarInteressat(
			Long interessatId) {
		Interessat interessat = interessatRepository.getById(interessatId);
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
		interessatRepository.delete(interessat);
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public InteressatDto findOne(
			Long interessatId) {
		Interessat interessat = interessatRepository.getById(interessatId);
		if (interessat == null) {
			throw new NoTrobatException(
					Interessat.class,
					interessatId);
		}
		
		
		InteressatDto convertir = conversioTipusServiceHelper.convertir(
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
		
		Expedient expedient = expedientRepository.getById(expedientId);
		
		return conversioTipusServiceHelper.convertir(
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
		
		Expedient expedient = expedientRepository.getById(expedientId);
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
		
		Expedient expedient = expedientRepository.getById(expedientId);
		
		return conversioTipusServiceHelper.convertirList(interessatRepository.findByExpedient(
				expedient), InteressatDto.class);
	}
	



	private static final Logger logger = LoggerFactory.getLogger(ExpedientInteressatServiceImpl.class);

}
