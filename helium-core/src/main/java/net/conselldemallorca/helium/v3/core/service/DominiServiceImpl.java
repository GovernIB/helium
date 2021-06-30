/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.emiserv.logic.intf.exception.NoTrobatException;
import es.caib.emiserv.logic.intf.exception.PermisDenegatException;
import es.caib.emiserv.logic.intf.exception.ValidacioException;
import es.caib.emiserv.logic.intf.extern.domini.FilaResultat;
import es.caib.helium.logic.intf.dto.DominiDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.DominiDto.OrigenCredencials;
import es.caib.helium.logic.intf.dto.DominiDto.TipusAuthDomini;
import es.caib.helium.logic.intf.dto.DominiDto.TipusDomini;
import es.caib.helium.logic.intf.service.DominiService;
import es.caib.helium.persist.entity.Camp;
import es.caib.helium.persist.entity.ExpedientTipus;
import es.caib.helium.persist.repository.EntornRepository;
import es.caib.helium.persist.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.DominiHelper;
import net.conselldemallorca.helium.core.helper.EntornHelper;
import net.conselldemallorca.helium.core.helper.ExpedientTipusHelper;
import net.conselldemallorca.helium.core.helper.MessageHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.ms.domini.DominiMs;
import net.conselldemallorca.helium.ms.domini.client.model.Domini;

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
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private MessageHelper messageHelper;
	
	@Autowired
	DominiHelper dominiHelper;
	
	@Autowired
	private DominiMs dominiMs;
	
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

		PaginaDto<DominiDto> pagina = dominiMs.findByFiltrePaginat(
				entornId, 
				filtre, 
				expedientTipusId, 
				expedientTipus != null ? expedientTipus.getTipusPareId() : null, 
				paginacioParams);	
		
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
			expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		
		// Control d'accés
		if (expedientTipus != null) {			
			expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(
					expedientTipus.getId());
		} else
			entornHelper.getEntornComprovantPermisos(EntornActual.getEntornId(), true, true);
		
		domini.setEntornId(entornId);
		domini.setExpedientTipusId(expedientTipusId);
		
		long dominiId = this.dominiMs.create(domini);
		
		domini.setId(dominiId);

		return domini;
	}
	
	@Override
	@Transactional
	public DominiDto findAmbCodi(
			Long entornId,
			Long expedientTipusId, 
			String codi) {
		logger.debug(
				"Consultant el domini per codi (" +
				"entornId=" + entornId + ", " +  
				"expedientTipusId=" + expedientTipusId + ", " +
				"codi = " + codi + ")");
		DominiDto domini = dominiMs.findAmbCodi(
				entornId,
				expedientTipusId,
				codi);
		
		return domini;
	}	
	
	@Override
	@Transactional
	public void delete(Long dominiId) throws NoTrobatException, PermisDenegatException, ValidacioException {
		
		logger.debug(
				"Esborrant el domini (" +
				"dominiId=" + dominiId +  ")");

		DominiDto domini = dominiMs.get(dominiId);
		if (domini == null) {
			throw new NoTrobatException(DominiDto.class, dominiId);
		}		

		if (domini.getExpedientTipusId() != null)
			expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(
					domini.getExpedientTipusId());
		else
			entornHelper.getEntornComprovantPermisos(
					domini.getEntornId(), 
					true,// accés 
					true); // disseny
		
		// Comprova si el domini està en ús abans d'esborrar
		List<Camp> campsDomini = dominiHelper.findCampsPerDomini(dominiId);
		if (campsDomini.size()>0) {
			throw new ValidacioException(messageHelper.getMessage("expedient.tipus.domini.controller.eliminat.us"));
		}

		dominiMs.delete(dominiId);
	}

	@Override
	@Transactional
	public List<DominiDto> findGlobals(Long entornId) throws NoTrobatException {
		logger.debug(
				"Consultant els dominins globals per entorn (" +
				"entornId=" + entornId +  ")");
		//TODO: DANIEL Construir consulta per obtenir dominis d'entorn amb tipus d'expedient null
		return new ArrayList<DominiDto>();
//		return conversioTipusHelper.convertirList(
//				dominiRepository.findGlobals(entornId),
//				DominiDto.class);
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
		DominiDto domini = dominiMs.get(dominiId);
		if (domini == null) {
			throw new NoTrobatException(Domini.class, dominiId);
		}
		// Herencia
		domini.setHeretat(expedientTipusId != null && ! expedientTipusId.equals(domini.getExpedientTipusId()));
		return conversioTipusHelper.convertir(domini, DominiDto.class);
	}
	
	@Override
	@Transactional
	public DominiDto update(DominiDto domini)
			throws NoTrobatException, PermisDenegatException {
		
		logger.debug(
				"Modificant el domini existent (" +
				"domini.id=" + domini.getId() + ", " +
				"domini =" + domini + ")");		
		
		DominiDto entity = dominiMs.get(domini.getId());
		if (entity == null) {
			throw new NoTrobatException(DominiDto.class, domini.getId());
		}
		
		if (entity.getExpedientTipusId() != null)
			expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(
					entity.getExpedientTipusId());
		else
			entornHelper.getEntornComprovantPermisos(
					entity.getEntornId(), 
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
		
		entity = dominiMs.update(entity);
		
		return conversioTipusHelper.convertir(entity, DominiDto.class);		
	}

	@Override
	@Transactional(readOnly = true)
	public List<FilaResultat> consultaDomini(
			Long entornId, 
			Long dominiId, 
			String dominiWsId,
			Map<String, Object> params) {
		logger.debug(
				"Consulta domini (" +
						"entornId=" + entornId + ", " +
						"dominiId=" + dominiId + ", " +
						"dominiWsId=" + dominiWsId + ", " +
				"params =" + params + ")");		

		return dominiHelper.consultar(
				dominiId,
				(dominiId != null) ? dominiId.toString() : null,
				params);
	}
		
	private static final Logger logger = LoggerFactory.getLogger(DominiServiceImpl.class);

}