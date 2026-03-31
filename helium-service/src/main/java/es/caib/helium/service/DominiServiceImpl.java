/**
 * 
 */
package es.caib.helium.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.helium.commons.dto.DominiDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.exception.PermisDenegatException;
import es.caib.helium.commons.exception.ValidacioException;
import es.caib.helium.commons.utils.MessageHelper;
import es.caib.helium.logic.intf.service.DominiService;
import es.caib.helium.persistence.entity.Domini;
import es.caib.helium.persistence.entity.Domini.OrigenCredencials;
import es.caib.helium.persistence.entity.Domini.TipusAuthDomini;
import es.caib.helium.persistence.entity.Domini.TipusDomini;
import es.caib.helium.persistence.entity.Entorn;
import es.caib.helium.persistence.entity.ExpedientTipus;
import es.caib.helium.persistence.repository.DominiRepository;
import es.caib.helium.persistence.repository.EntornRepository;
import es.caib.helium.persistence.repository.ExpedientTipusRepository;
import es.caib.helium.service.helper.ConversioTipusHelper;
import es.caib.helium.service.helper.EntornHelper;
import es.caib.helium.service.helper.ExpedientTipusHelper;
import es.caib.helium.service.helper.HerenciaHelper;
import es.caib.helium.service.helper.PaginacioHelper;
import es.caib.helium.service.utils.EntornActual;

/**
 * Implementació del servei per a gestionar dominis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class DominiServiceImpl implements DominiService {

	@Resource
	private EntornHelper entornHelper;
	@Resource
	private EntornRepository entornRepository;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private DominiRepository dominiRepository;

	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private MessageHelper messageHelper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<DominiDto> findPerDatatable(
			Long entornId,
			Long expedientTipusId,
			boolean incloureGlobals,
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		logger.debug(
				"Consultant les dominins per datatable (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"incloureGlobals=" + incloureGlobals + ", " +
				"filtre=" + filtre + ")");
		
		ExpedientTipus expedientTipus = expedientTipusId != null? expedientTipusHelper.getExpedientTipusComprovantPermisDissenyDelegat(expedientTipusId) : null;

		// Determina si hi ha herència 
		boolean ambHerencia = HerenciaHelper.ambHerencia(expedientTipus);

		Page<Domini> page = dominiRepository.findByFiltrePaginat(
				entornId,
				expedientTipusId == null,
				expedientTipusId,
				incloureGlobals,
				filtre == null || "".equals(filtre), 
				filtre, 
				ambHerencia,
				paginacioHelper.toSpringDataPageable(
						paginacioParams));

		PaginaDto<DominiDto> pagina = paginacioHelper.toPaginaDto(
				page,
				DominiDto.class);		
		
		if (ambHerencia) {
			// Llista d'heretats
			Set<Long> heretatsIds = new HashSet<Long>();
			for (Domini d : page.getContent()) 
				if ( !expedientTipusId.equals(d.getExpedientTipus().getId()))
					heretatsIds.add(d.getId());
			// Llistat d'elements sobreescrits
			Set<String> sobreescritsCodis = new HashSet<String>();
			for (Domini d : dominiRepository.findSobreescrits(
					expedientTipus.getId()
				)) {
				sobreescritsCodis.add(d.getCodi());
			}
			// Completa l'informació del dto
			for (DominiDto dto : pagina.getContingut()) {
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
	public DominiDto create(
			Long entornId, 
			Long expedientTipusId, 
			DominiDto domini)
			throws PermisDenegatException {

		logger.debug(
				"Creant nou domini (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"entornId =" + entornId + ", " +
				"domini=" + domini + ")");
		
		ExpedientTipus expedientTipus = null;
		if (expedientTipusId != null)
			expedientTipus = expedientTipusRepository.findById(expedientTipusId).orElse(null);
		
		Entorn entorn;
		// Control d'accés
		if (expedientTipus != null) {			
			expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(
					expedientTipus.getId());
			entorn = expedientTipus.getEntorn();
		} else
			entorn = entornHelper.getEntornComprovantPermisos(EntornActual.getEntornId(), true, true);
		
		
		Domini entity = new Domini();
		entity.setEntorn(entorn);
		entity.setExpedientTipus(expedientTipus);

		entity.setCodi(domini.getCodi());
		entity.setNom(domini.getNom());
		entity.setDescripcio(domini.getDescripcio());
		if (domini.getTipus() != null)
			entity.setTipus(TipusDomini.valueOf(domini.getTipus().name()));
		entity.setUrl(domini.getUrl());
		if (domini.getTipusAuth() != null)
			entity.setTipusAuth(TipusAuthDomini.valueOf(domini.getTipusAuth().name()));
		if (domini.getOrigenCredencials() != null)
			entity.setOrigenCredencials(OrigenCredencials.valueOf(domini.getOrigenCredencials().name()));
		entity.setUsuari(domini.getUsuari());
		entity.setContrasenya(domini.getContrasenya());
		entity.setSql(domini.getSql());
		entity.setJndiDatasource(domini.getJndiDatasource());
		entity.setCacheSegons(domini.getCacheSegons());
		entity.setTimeout(domini.getTimeout());
		entity.setOrdreParams(domini.getOrdreParams());
		
		return conversioTipusHelper.convertir(
				dominiRepository.save(entity),
				DominiDto.class);
	}
	
	@Override
	@Transactional
	public DominiDto findAmbCodi(
			Long entornId,
			Long expedientTipusId, 
			String codi) {
		DominiDto ret = null;
		logger.debug(
				"Consultant el domini per codi (" +
				"entornId=" + entornId + ", " +  
				"expedientTipusId=" + expedientTipusId + ", " +
				"codi = " + codi + ")");
		Domini domini;
		if (expedientTipusId != null)
			domini = dominiRepository.findByExpedientTipusAndCodi(
					expedientTipusRepository.findById(expedientTipusId).orElse(null), 
					codi);
		else
			domini = dominiRepository.findByEntornAndCodi(
					entornRepository.findById(entornId).orElse(null), 
					codi);
		if (domini != null)
			ret = conversioTipusHelper.convertir(
					domini,
					DominiDto.class);
		return ret;
	}	
	
	@Override
	@Transactional
	public void delete(Long dominiId) throws NoTrobatException, PermisDenegatException, ValidacioException {
		
		logger.debug(
				"Esborrant el domini (" +
				"dominiId=" + dominiId +  ")");
		
		Domini entity = dominiRepository.findById(dominiId).orElse(null);
		if (entity == null) {
			throw new NoTrobatException(Domini.class, dominiId);
		}
		

		if (entity.getExpedientTipus() != null)
			expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(
					entity.getExpedientTipus().getId());
		else
			entornHelper.getEntornComprovantPermisos(
					entity.getEntorn().getId(), 
					true,// accés 
					true); // disseny

		if (entity.getExpedientTipus() != null) {
			entity.getExpedientTipus().removeDomini(entity);
		}
		
		if (entity.getCamps()!=null && entity.getCamps().size()>0) {
			throw new ValidacioException(messageHelper.getMessage("expedient.tipus.domini.controller.eliminat.us"));
		}

		dominiRepository.delete(entity);
	}

	@Override
	@Transactional
	public List<DominiDto> findGlobals(Long entornId) throws NoTrobatException {
		logger.debug(
				"Consultant els dominins globals per entorn (" +
				"entornId=" + entornId +  ")");
		return conversioTipusHelper.convertirList(
				dominiRepository.findGlobals(entornId),
				DominiDto.class);
	}
	
	@Override
	@Transactional
	public DominiDto findAmbId(
			Long expedientTipusId,
			Long dominiId) throws NoTrobatException {
		logger.debug(
				"Consultant el domini amb id (" +
				"expedientTipusId=" + expedientTipusId + "," +
				"dominiId=" + dominiId +  ")");
		ExpedientTipus tipus = expedientTipusId != null?
				expedientTipusRepository.findById(expedientTipusId).orElse(null) : null;
		Domini domini = dominiRepository.findById(dominiId).orElse(null);
		if (domini == null) {
			throw new NoTrobatException(Domini.class, dominiId);
		}
		DominiDto dto = conversioTipusHelper.convertir(
				domini,
				DominiDto.class); 
		// Herencia
		if (tipus != null && tipus.getExpedientTipusPare() != null) {
			if (tipus.getExpedientTipusPare().getId().equals(domini.getExpedientTipus().getId()))
				dto.setHeretat(true);
			else
				dto.setSobreescriu(dominiRepository.findByExpedientTipusAndCodi(
						tipus.getExpedientTipusPare(), 
						domini.getCodi()) != null);					
		}
		return dto;
	}
	
	@Override
	@Transactional
	public DominiDto update(DominiDto domini)
			throws NoTrobatException, PermisDenegatException {
		
		logger.debug(
				"Modificant el domini existent (" +
				"domini.id=" + domini.getId() + ", " +
				"domini =" + domini + ")");		
		
		Domini entity = dominiRepository.findById(domini.getId()).orElse(null);
		if (entity == null) {
			throw new NoTrobatException(Domini.class, domini.getId());
		}
		
		if (entity.getExpedientTipus() != null)
			expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(
					entity.getExpedientTipus().getId());
		else
			entornHelper.getEntornComprovantPermisos(
					entity.getEntorn().getId(), 
					true,// accés 
					true); // disseny
		
		entity.setCodi(domini.getCodi());
		entity.setNom(domini.getNom());
		entity.setDescripcio(domini.getDescripcio());
		if (domini.getTipus() != null)
			entity.setTipus(TipusDomini.valueOf(domini.getTipus().name()));
		entity.setUrl(domini.getUrl());
		if (domini.getTipusAuth() != null)
			entity.setTipusAuth(TipusAuthDomini.valueOf(domini.getTipusAuth().name()));
		if (domini.getOrigenCredencials() != null)
			entity.setOrigenCredencials(OrigenCredencials.valueOf(domini.getOrigenCredencials().name()));
		entity.setUsuari(domini.getUsuari());
		entity.setContrasenya(domini.getContrasenya());
		entity.setSql(domini.getSql());
		entity.setJndiDatasource(domini.getJndiDatasource());
		entity.setCacheSegons(domini.getCacheSegons());
		entity.setTimeout(domini.getTimeout());
		entity.setOrdreParams(domini.getOrdreParams());
		
		return conversioTipusHelper.convertir(
				dominiRepository.save(entity),
				DominiDto.class);
		
	}

		
	private static final Logger logger = LoggerFactory.getLogger(DominiServiceImpl.class);
}