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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.EntornHelper;
import net.conselldemallorca.helium.core.helper.ExpedientTipusHelper;
import net.conselldemallorca.helium.core.helper.MessageHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Domini.OrigenCredencials;
import net.conselldemallorca.helium.core.model.hibernate.Domini.TipusAuthDomini;
import net.conselldemallorca.helium.core.model.hibernate.Domini.TipusDomini;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.v3.core.api.service.DominiService;
import net.conselldemallorca.helium.v3.core.repository.DominiRepository;
import net.conselldemallorca.helium.v3.core.repository.EntornRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;

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
		boolean herencia = expedientTipus != null && expedientTipus.isAmbInfoPropia() && expedientTipus.getExpedientTipusPare() != null;

		Page<Domini> page = dominiRepository.findByFiltrePaginat(
				entornId,
				expedientTipusId == null,
				expedientTipusId,
				incloureGlobals,
				filtre == null || "".equals(filtre), 
				filtre, 
				herencia,
				paginacioHelper.toSpringDataPageable(
						paginacioParams));

		PaginaDto<DominiDto> pagina = paginacioHelper.toPaginaDto(
				page,
				DominiDto.class);		
		
		if (herencia) {
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
		
		Entorn entorn = entornHelper.getEntornComprovantPermisos(entornId, true, true);
		ExpedientTipus expedientTipus = null;
		if (expedientTipusId != null)
			expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		
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
					expedientTipusRepository.findOne(expedientTipusId), 
					codi);
		else
			domini = dominiRepository.findByEntornAndCodi(
					entornRepository.findOne(entornId), 
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
		
		Domini entity = dominiRepository.findOne(dominiId);
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
				expedientTipusRepository.findById(expedientTipusId) : null;
		Domini domini = dominiRepository.findOne(dominiId);
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
		
		Domini entity = dominiRepository.findOne(domini.getId());
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