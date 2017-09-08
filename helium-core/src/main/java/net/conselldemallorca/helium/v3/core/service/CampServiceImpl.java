/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampAgrupacio;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampRegistreDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.CampService;
import net.conselldemallorca.helium.v3.core.repository.CampAgrupacioRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRegistreRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.ConsultaCampRepository;
import net.conselldemallorca.helium.v3.core.repository.ConsultaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DominiRepository;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;

/**
 * Implementació del servei per a gestionar camps dels tipus d'expedients o definicions de procés.
 * 
 */
@Service
public class CampServiceImpl implements CampService {

	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private CampAgrupacioRepository campAgrupacioRepository;
	@Resource
	private CampRegistreRepository campRegistreRepository;
	@Resource
	private EnumeracioRepository enumeracioRepository;
	@Resource
	private DominiRepository dominiRepository;
	@Resource
	private ConsultaRepository consultaRepository;
	@Resource
	private ConsultaCampRepository consultaCampRepository;

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public CampDto create(
			Long expedientTipusId, 
			Long definicioProcesId, 
			CampDto camp) throws PermisDenegatException {

		logger.debug(
				"Creant nou camp per un tipus d'expedient (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"definicioProcesId =" + definicioProcesId + ", " +
				"camp=" + camp + ")");
		
		Camp entity = new Camp();
		entity.setCodi(camp.getCodi());
		entity.setTipus(conversioTipusHelper.convertir(camp.getTipus(), Camp.TipusCamp.class));
		entity.setEtiqueta(camp.getEtiqueta());
		entity.setObservacions(camp.getObservacions());
		entity.setMultiple(camp.isMultiple());
		entity.setOcult(camp.isOcult());
		entity.setIgnored(camp.isIgnored());
		CampAgrupacio agrupacio = null;
		if (camp.getAgrupacio() != null) 
			agrupacio = campAgrupacioRepository.findOne(camp.getAgrupacio().getId());
		entity.setAgrupacio(agrupacio);
		if (agrupacio != null && entity.getOrdre() == null) {
			// Informa de l'ordre dins de la agrupació
			entity.setOrdre(
					campRepository.getNextOrdre(
							agrupacio.getId()));
		}		
		if (expedientTipusId != null)
			entity.setExpedientTipus(expedientTipusRepository.findOne(expedientTipusId));
		if (definicioProcesId != null)
			entity.setDefinicioProces(definicioProcesRepository.findOne(definicioProcesId));
		
		// Dades consulta
		Enumeracio enumeracio = null;
		if (camp.getEnumeracio() != null) {
			enumeracio = enumeracioRepository.findOne(camp.getEnumeracio().getId());
		}
		entity.setEnumeracio(enumeracio);
		Domini domini = null;
		if (camp.getDomini() != null) {
			domini = dominiRepository.findOne(camp.getDomini().getId());
		}
		entity.setDomini(domini);
		Consulta consulta = null;
		if (camp.getConsulta() != null) {
			consulta = consultaRepository.findOne(camp.getConsulta().getId());
		}
		entity.setConsulta(consulta);		
		entity.setDominiIntern(camp.isDominiIntern());

		// Paràmetres del domini
		entity.setDominiId(camp.getDominiIdentificador());
		entity.setDominiParams(camp.getDominiParams());
		entity.setDominiCampValor(camp.getDominiCampValor());
		entity.setDominiCampText(camp.getDominiCampText());
		
		// Paràmetres de la consulta
		entity.setConsultaParams(camp.getConsultaParams());
		entity.setConsultaCampValor(camp.getConsultaCampValor());
		entity.setConsultaCampText(camp.getConsultaCampText());
		
		// Dades de la acció
		entity.setDefprocJbpmKey(camp.getDefprocJbpmKey());
		entity.setJbpmAction(camp.getJbpmAction());
		
		entity.setDominiCacheText(camp.isDominiCacheText());

		return conversioTipusHelper.convertir(
				campRepository.save(entity),
				CampDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public CampDto update(CampDto camp) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Modificant el camp del tipus d'expedient existent (" +
				"camp.id=" + camp.getId() + ", " +
				"camp =" + camp + ")");
		Camp entity = campRepository.findOne(camp.getId());
		entity.setCodi(camp.getCodi());
		entity.setTipus(conversioTipusHelper.convertir(camp.getTipus(), Camp.TipusCamp.class));
		entity.setEtiqueta(camp.getEtiqueta());
		entity.setObservacions(camp.getObservacions());
		entity.setMultiple(camp.isMultiple());
		entity.setOcult(camp.isOcult());
		entity.setIgnored(camp.isIgnored());
		CampAgrupacio agrupacio = null;
		if (camp.getAgrupacio() != null) 
			agrupacio = campAgrupacioRepository.findOne(camp.getAgrupacio().getId());
		entity.setAgrupacio(agrupacio);
		if (agrupacio != null && entity.getOrdre() == null) {
			// Informa de l'ordre dins de la agrupació
			entity.setOrdre(
					campRepository.getNextOrdre(
							agrupacio.getId()));
		}		
		
		// Dades consulta
		Enumeracio enumeracio = null;
		if (camp.getEnumeracio() != null) {
			enumeracio = enumeracioRepository.findOne(camp.getEnumeracio().getId());
		}
		entity.setEnumeracio(enumeracio);
		Domini domini = null;
		if (camp.getDomini() != null) {
			domini = dominiRepository.findOne(camp.getDomini().getId());
		}
		entity.setDomini(domini);
		Consulta consulta = null;
		if (camp.getConsulta() != null) {
			consulta = consultaRepository.findOne(camp.getConsulta().getId());
		}
		entity.setConsulta(consulta);		
		entity.setDominiIntern(camp.isDominiIntern());

		// Paràmetres del domini
		entity.setDominiId(camp.getDominiIdentificador());
		entity.setDominiParams(camp.getDominiParams());
		entity.setDominiCampValor(camp.getDominiCampValor());
		entity.setDominiCampText(camp.getDominiCampText());
		
		// Paràmetres de la consulta
		entity.setConsultaParams(camp.getConsultaParams());
		entity.setConsultaCampValor(camp.getConsultaCampValor());
		entity.setConsultaCampText(camp.getConsultaCampText());
		
		// Dades de la acció
		entity.setDefprocJbpmKey(camp.getDefprocJbpmKey());
		entity.setJbpmAction(camp.getJbpmAction());

		entity.setDominiCacheText(camp.isDominiCacheText());
		
		return conversioTipusHelper.convertir(
				campRepository.save(entity),
				CampDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void delete(Long campCampId) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Esborrant el camp del tipus d'expedient (" +
				"campId=" + campCampId +  ")");
		Camp entity = campRepository.findOne(campCampId);

		if (entity != null) {
			for (CampRegistre campRegistre : entity.getRegistrePares()) {
				campRegistre.getRegistre().getRegistreMembres().remove(campRegistre);
				campRegistreRepository.delete(campRegistre);	
				campRegistreRepository.flush();
				reordenarCampsRegistre(campRegistre.getRegistre().getId());						
			}
			campRepository.delete(entity);	
			campRepository.flush();
			if (entity.getAgrupacio() != null) {
				reordenarCamps(entity.getAgrupacio().getId());
			}
		}
	}

	/** Funció per reasignar el valor d'ordre dins d'una agrupació de variables. */
	private void reordenarCamps(Long agrupacioId) {
		List<Camp> camps = campRepository.findByAgrupacioIdOrderByOrdreAsc(agrupacioId);		
		int i = 0;
		for (Camp camp: camps)
			camp.setOrdre(i++);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public CampDto findAmbId(Long id) throws NoTrobatException {
		logger.debug(
				"Consultant el camp del tipus d'expedient amb id (" +
				"campId=" + id +  ")");
		Camp camp = campRepository.findOne(id);
		if (camp == null) {
			throw new NoTrobatException(Camp.class, id);
		}
		return conversioTipusHelper.convertir(
				camp,
				CampDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CampDto findAmbCodi(
			Long expedientTipusId, 
			Long definicioProcesId,
			String codi) {
		CampDto ret = null;
		logger.debug(
				"Consultant el camp del tipus d'expedient per codi per validar repetició (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"definicioProcesId =" + definicioProcesId + ", " +
				"codi = " + codi + ")");
				
		Camp camp = null;
		if (expedientTipusId != null)
			camp = campRepository.findByExpedientTipusAndCodi(
					expedientTipusRepository.findOne(expedientTipusId), 
					codi);
		else if (definicioProcesId != null)
			camp = campRepository.findByDefinicioProcesAndCodi(
					definicioProcesRepository.findById(definicioProcesId), 
					codi);
		if (camp != null)
			ret = conversioTipusHelper.convertir(
				camp,
				CampDto.class);
		return ret;
	}	
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<CampDto> findPerDatatable(
			Long expedientTipusId, 
			Long definicioProcesId, 
			boolean totes,
			Long agrupacioId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant els camps per al tipus d'expedient per datatable (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"totes =" + totes + ", " +
				"definicioProcesId =" + definicioProcesId + ", " +
				"agrupacioId=" + agrupacioId + ", " +
				"filtre=" + filtre + ")");
						
		
		PaginaDto<CampDto> pagina = paginacioHelper.toPaginaDto(
				campRepository.findByFiltrePaginat(
						expedientTipusId,
						definicioProcesId,
						totes,
						agrupacioId == null,
						agrupacioId != null ? agrupacioId : 0L,
						filtre == null || "".equals(filtre), 
						filtre, 
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				CampDto.class);
		
		// Omple els comptador de validacions i de membres
		List<Object[]> countValidacions = campRepository.countValidacions(
				expedientTipusId,
				definicioProcesId,
				totes,
				agrupacioId == null,
				agrupacioId); 
		List<Object[]> countMembres= campRepository.countMembres(
				expedientTipusId,
				definicioProcesId,
				totes,
				agrupacioId == null,
				agrupacioId); 
		for (CampDto dto: pagina.getContingut()) {
			for (Object[] reg: countValidacions) {
				Long campId = (Long)reg[0];
				if (campId.equals(dto.getId())) {
					Integer count = (Integer)reg[1];
					dto.setValidacioCount(count.intValue());
					countValidacions.remove(reg);
					break;
				}
			}
			if (dto.getTipus() == CampTipusDto.REGISTRE) {
				for (Object[] reg: countMembres) {
					Long campId = (Long)reg[0];
					if (campId.equals(dto.getId())) {
						Integer count = (Integer)reg[1];
						dto.setCampRegistreCount(count.intValue());
						countMembres.remove(reg);
						break;
					}
				}
			}
		}		
		
		return pagina;		
		
	}
	
	@Override
	@Transactional
	public boolean mourePosicio(
			Long id, 
			int posicio) {
		boolean ret = false;
		Camp camp = campRepository.findOne(id);
		if (camp != null && camp.getAgrupacio() != null) {
			List<Camp> camps = campRepository.findByAgrupacioIdOrderByOrdreAsc(camp.getAgrupacio().getId());
			if(posicio != camps.indexOf(camp)) {
				camps.remove(camp);
				camps.add(posicio, camp);
				int i = 0;
				for (Camp c : camps) {
					c.setOrdre(i++);
				}
			}
		}
		return ret;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CampDto> findAllOrdenatsPerCodi(
			Long expedientTipusId, 
			Long definicioProcesId) {
		logger.debug(
				"Consultant tots els camps del tipus expedient per al desplegable " +
				" de camps del registre (expedientTipusId =" + expedientTipusId + ", " +
				"definicioProcesId =" + definicioProcesId + ")");
		
		List<Camp> camps = null;
		if (expedientTipusId != null)
			camps = campRepository.findByExpedientTipusOrderByCodiAsc(
						expedientTipusRepository.findOne(expedientTipusId));
		else if (definicioProcesId != null)
			camps = campRepository.findByDefinicioProcesOrderByCodiAsc(
						definicioProcesRepository.findById(definicioProcesId));
		
		return conversioTipusHelper.convertirList(
				camps, 
				CampDto.class);
	}

	// MANTENIMENT D'AGRUPACIONS DE CAMPS

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<CampAgrupacioDto> agrupacioFindAll(
			Long expedientTipusId,
			Long definicioProcesId) throws NoTrobatException, PermisDenegatException {
		List<CampAgrupacio> agrupacions;
		if (expedientTipusId != null)
			agrupacions = campAgrupacioRepository.findAmbExpedientTipusOrdenats(expedientTipusId);
		else
			agrupacions = campAgrupacioRepository.findAmbDefinicioProcesOrdenats(definicioProcesId);
		return conversioTipusHelper.convertirList(
									agrupacions, 
									CampAgrupacioDto.class);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CampAgrupacioDto agrupacioFindAmbId(Long id) throws NoTrobatException {
		logger.debug(
				"Consultant la agrupacio de camps del tipus d'expedient amb id (" +
				"campAgrupacioId=" + id +  ")");
		CampAgrupacio  agrupacio = campAgrupacioRepository.findOne(id);
		if (agrupacio == null) {
			throw new NoTrobatException(CampAgrupacio.class, id);
		}
		return conversioTipusHelper.convertir(
				agrupacio,
				CampAgrupacioDto.class);
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public CampAgrupacioDto agrupacioCreate(
			Long expedientTipusId, 
			Long definicioProcesId, 
			CampAgrupacioDto agrupacio) throws PermisDenegatException {

		logger.debug(
				"Creant nova agrupació de camp per un tipus d'expedient (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"definicioProcesId =" + definicioProcesId + ", " +
				"agrupacio=" + agrupacio + ")");
		
		CampAgrupacio entity = new CampAgrupacio();
		entity.setCodi(agrupacio.getCodi());
		entity.setNom(agrupacio.getNom());
		entity.setDescripcio(agrupacio.getDescripcio());
		entity.setOrdre(campAgrupacioRepository.getNextOrdre(expedientTipusId, definicioProcesId));

		if (expedientTipusId != null)
			entity.setExpedientTipus(expedientTipusRepository.findOne(expedientTipusId));
		if (definicioProcesId != null)
			entity.setDefinicioProces(definicioProcesRepository.findOne(definicioProcesId));

		return conversioTipusHelper.convertir(
				campAgrupacioRepository.save(entity),
				CampAgrupacioDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public CampAgrupacioDto agrupacioUpdate(
			CampAgrupacioDto agrupacio) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Modificant la agrupacio de camp del tipus d'expedient existent (" +
				"agrupacio.id=" + agrupacio.getId() + ", " +
				"agrupacio =" + agrupacio + ")");
		CampAgrupacio entity = campAgrupacioRepository.findOne(agrupacio.getId());
		entity.setCodi(agrupacio.getCodi());
		entity.setNom(agrupacio.getNom());
		entity.setDescripcio(agrupacio.getDescripcio());
		
		return conversioTipusHelper.convertir(
				campAgrupacioRepository.save(entity),
				CampAgrupacioDto.class);
	}
	
	@Override
	@Transactional
	public boolean agrupacioMourePosicio(
			Long id, 
			int posicio) {
		boolean ret = false;
		CampAgrupacio agrupacio = campAgrupacioRepository.findOne(id);
		if (agrupacio != null) {
			List<CampAgrupacio> agrupacions = agrupacio.getExpedientTipus() != null
					? campAgrupacioRepository.findAmbExpedientTipusOrdenats(agrupacio.getExpedientTipus().getId())
					: campAgrupacioRepository.findAmbDefinicioProcesOrdenats(agrupacio.getDefinicioProces().getId());
			if(posicio != agrupacions.indexOf(agrupacio)) {
				agrupacions.remove(agrupacio);
				agrupacions.add(posicio, agrupacio);
				int i = 0;
				for (CampAgrupacio c : agrupacions) {
					c.setOrdre(i++);
				}
			}
		}
		return ret;
	}	

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void agrupacioDelete(Long agrupacioCampId) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Esborrant la agrupacio de camp del tipus d'expedient (" +
				"agrupacioCampId=" + agrupacioCampId +  ")");
		CampAgrupacio entity = campAgrupacioRepository.findOne(agrupacioCampId);
		if (entity != null) {			
			for (Camp camp : entity.getCamps()) {
				camp.setAgrupacio(null);
				camp.setOrdre(null);
				campRepository.save(camp);
			}			
			campAgrupacioRepository.delete(entity);
			campAgrupacioRepository.flush();
		}
		reordenarAgrupacions(
				entity.getExpedientTipus() != null? entity.getExpedientTipus().getId() : null,
				entity.getDefinicioProces() != null? entity.getDefinicioProces().getId() : null);
	}
	
	/** Funció per reasignar el valor d'ordre per a les agrupacions d'un tipus d'expedient o definició de procés*/
	@Transactional
	private int reordenarAgrupacions(
			Long expedientTipusId, 
			Long definicioProcesId) {
		List<CampAgrupacio> campsAgrupacio = expedientTipusId != null
				? expedientTipusRepository.findOne(expedientTipusId).getAgrupacions()
				: definicioProcesRepository.findOne(definicioProcesId).getAgrupacions();						
		int i = 0;
		for (CampAgrupacio campAgrupacio: campsAgrupacio)
			campAgrupacio.setOrdre(i++);
		return campsAgrupacio.size();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CampAgrupacioDto agrupacioFindAmbCodiPerValidarRepeticio(
								Long expedientTipusId, 
								Long definicioProcesId, 
								String codi) throws NoTrobatException {
		logger.debug(
				"Consultant la agrupacio de camps del tipus d'expedient per codi per validar repetició (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"definicioProcesId =" + definicioProcesId + ", " +
				"codi = " + codi + ")");
		CampAgrupacio campAgrupacio = null;
		if (expedientTipusId != null)
			campAgrupacio = campAgrupacioRepository.findByExpedientTipusIdAndCodi(
									expedientTipusId, 
									codi);
		else if (definicioProcesId != null)
			campAgrupacio = campAgrupacioRepository.findByDefinicioProcesIdAndCodi(
									definicioProcesId, 
									codi);
		if (campAgrupacio != null)
			return conversioTipusHelper.convertir(
					campAgrupacio,
					CampAgrupacioDto.class);
		else 
			return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<CampAgrupacioDto> agrupacioFindPerDatatable(
			Long expedientTipusId, 
			Long definicioProcesId, 
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant les agrupacions per al tipus d'expedient per datatable (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"definicioProcesId =" + definicioProcesId + ", " +
				"filtre=" + filtre + ")");
						
		
		return paginacioHelper.toPaginaDto(
				campAgrupacioRepository.findByFiltrePaginat(
						expedientTipusId,
						definicioProcesId,
						filtre == null || "".equals(filtre), 
						filtre, 
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				CampAgrupacioDto.class);		
	}	
	
	@Override
	@Transactional
	public boolean afegirAgrupacio(
			Long campId, 
			Long agrupacioId) {
		boolean ret = false;
		logger.debug(
				"Afegint camp de tipus d'expedient a la agrupació (" +
				"campId=" + campId + ", " +
				"agrupacioId = " + agrupacioId + ")");
		Camp camp = campRepository.findOne(campId);
		CampAgrupacio agrupacio = campAgrupacioRepository.findOne(agrupacioId);
		if (camp != null && agrupacio != null) {
			camp.setAgrupacio(agrupacio);
			reordenarCamps(agrupacioId);
			ret = true;
		}
		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public boolean remoureAgrupacio(Long campId) {
		boolean ret = false;
		logger.debug(
				"Remoguent el camp de tipus d'expedient de la seva agrupació(" +
				"campId=" + campId + ")");
		Camp camp = campRepository.findOne(campId);
		if (camp != null && camp.getAgrupacio() != null) {
			Long agrupacioId = camp.getAgrupacio().getId();
			camp.setAgrupacio(null);
			camp.setOrdre(null);
			reordenarCamps(agrupacioId);
			ret = true;
		}
		return ret;
	}
	
	// MANTENIMENT  DE CAMPS DE LES VARIABLES DE TIPUS REGISTRE DEL TIPUS D'EXPEDIENT
	
	@Override
	@Transactional
	public CampRegistreDto registreCreate(
			Long campId, 
			CampRegistreDto campRegistre) throws PermisDenegatException {

		logger.debug(
				"Creant nou campRegistre per un camp de tipus d'expedient (" +
				"campId =" + campId + ", " +
				"campRegistre=" + campRegistre + ")");

		CampRegistre entity = new CampRegistre();
		entity.setRegistre(campRepository.findOne(campId));
		entity.setMembre(campRepository.findOne(campRegistre.getMembreId()));
		entity.setObligatori(campRegistre.isObligatori());
		entity.setLlistar(campRegistre.isLlistar());
		entity.setOrdre(campRegistreRepository.getNextOrdre(campId));
		
		return conversioTipusHelper.convertir(
				campRegistreRepository.save(entity),
				CampRegistreDto.class);
	}

	@Override
	@Transactional
	public CampRegistreDto registreUpdate(CampRegistreDto campRegistre) 
						throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Modificant el campRegistre del camp del tipus d'expedient existent (" +
				"campRegistre.id=" + campRegistre.getId() + ", " +
				"campRegistre =" + campRegistre + ")");
		
		CampRegistre entity = campRegistreRepository.findOne(campRegistre.getId());

		entity.setRegistre(campRepository.findOne(campRegistre.getRegistreId()));
		entity.setMembre(campRepository.findOne(campRegistre.getMembreId()));
		entity.setObligatori(campRegistre.isObligatori());
		entity.setLlistar(campRegistre.isLlistar());
		
		return conversioTipusHelper.convertir(
				campRegistreRepository.save(entity),
				CampRegistreDto.class);
	}

	@Override
	@Transactional
	public void registreDelete(Long campRegistreId) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Esborrant la campRegistre del tipus d'expedient (" +
				"campRegistreId=" + campRegistreId +  ")");
		
		CampRegistre campRegistre = campRegistreRepository.findOne(campRegistreId);
		campRegistre.getRegistre().getRegistreMembres().remove(campRegistre);
		campRegistreRepository.delete(campRegistre);	
		campRegistreRepository.flush();
		
		reordenarCampsRegistre(campRegistre.getRegistre().getId());
	}

	/** Funció per reasignar el valor d'ordre dins dels camps d'una variable de tipus registre. */
	private void reordenarCampsRegistre(Long campId) {
		List<CampRegistre> campRegistres = campRegistreRepository.findAmbCampOrdenats(campId);		
		int i=-1;
		for (CampRegistre c : campRegistres) {
			c.setOrdre(i);
			campRegistreRepository.saveAndFlush(c);
			i--;
		}
		i = 0;
		for (CampRegistre c : campRegistres) {
			c.setOrdre(i);
			campRegistreRepository.saveAndFlush(c);
			i++;
		}
	}

	@Override
	@Transactional
	public boolean registreMourePosicio(
										Long id, 
										int posicio) {
		boolean ret = false;
		CampRegistre campRegistre = campRegistreRepository.findOne(id);
		if (campRegistre != null) {
			List<CampRegistre> campsRegistre = campRegistreRepository.findAmbCampOrdenats(campRegistre.getRegistre().getId());
			int index = campsRegistre.indexOf(campRegistre);
			if(posicio != index) {	
				campRegistre = campsRegistre.get(index);
				campsRegistre.remove(campRegistre);
				campsRegistre.add(posicio, campRegistre);
				int i=-1;
				for (CampRegistre c : campsRegistre) {
					c.setOrdre(i);
					campRegistreRepository.saveAndFlush(c);
					i--;
				}
				i = 0;
				for (CampRegistre c : campsRegistre) {
					c.setOrdre(i);
					campRegistreRepository.saveAndFlush(c);
					i++;
				}
			}
		}
		return ret;				
	}

	@Override
	@Transactional(readOnly = true)
	public CampRegistreDto registreFindAmbId(Long id) throws NoTrobatException {
		logger.debug(
				"Consultant la campRegistre del camp del tipus d'expedient amb id (" +
				"campRegistreId=" + id +  ")");
		CampRegistre camp = campRegistreRepository.findOne(id);
		if (camp == null) {
			throw new NoTrobatException(CampRegistre.class, id);
		}
		return conversioTipusHelper.convertir(
				camp,
				CampRegistreDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CampDto> registreFindMembresAmbRegistreId(Long registreId) {
		logger.debug(
				"Consultant els membres del registre(" +
				"registreId=" + registreId +  ")");
		return conversioTipusHelper.convertirList(
				campRegistreRepository.findMembresAmbRegistreId(registreId),
				CampDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<CampRegistreDto> registreFindPerDatatable(
			Long campId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant els campRegistres per un camp registre del tipus d'expedient per datatable (" +
				"entornId=" + campId + ", " +
				"filtre=" + filtre + ")");
						
		Map<String, String> mapeigPropietatsOrdenacio = new HashMap<String, String>();
		mapeigPropietatsOrdenacio.put("membreCodi", "membre.codi");
		mapeigPropietatsOrdenacio.put("membreEtiqueta", "membre.etiqueta");
		mapeigPropietatsOrdenacio.put("membreTipus", "membre.tipus");
		
		return paginacioHelper.toPaginaDto(
				campRegistreRepository.findByFiltrePaginat(
						campId, 
						paginacioHelper.toSpringDataPageable(
								paginacioParams,
								mapeigPropietatsOrdenacio)),
				CampRegistreDto.class);		
	}	
			
	private static final Logger logger = LoggerFactory.getLogger(CampServiceImpl.class);

	@Override
	public List<CampDto> findTipusData(
			Long expedientTipusId,
			Long definicioProcesId) throws NoTrobatException {
		logger.debug(
				"Consultant els camps del tipus data (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"definicioProcesId =" + definicioProcesId +")");
				
		List<Camp> camps;
		if (expedientTipusId != null)
			camps = campRepository.findByExpedientTipusAndTipus(
									expedientTipusRepository.findById(expedientTipusId), 
									TipusCamp.DATE);
		else 
			camps = campRepository.findByDefinicioProcesAndTipus(
					definicioProcesRepository.findById(definicioProcesId), 
					TipusCamp.DATE);
		return conversioTipusHelper.convertirList(
				camps, 
				CampDto.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<TascaDto> findTasquesPerCamp(Long campId) {
		logger.debug(
				"Consultant les tasques pel camp (" +
				"campId =" + campId +")");
		
		Camp camp = campRepository.findOne(campId);
		List<Tasca> tasques = new ArrayList<Tasca>();
		for (CampTasca campTasca : camp.getCampsTasca())
			tasques.add(campTasca.getTasca());
		
		return conversioTipusHelper.convertirList(
				tasques, 
				TascaDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ConsultaDto> findConsultesPerCamp(Long campId) {
		logger.debug(
				"Consultant les consultes pel camp (" +
				"campId =" + campId +")");
		
		Camp camp = campRepository.findOne(campId);
		List<ConsultaCamp> consultaCamps = consultaCampRepository.findPerCamp(
				camp.getCodi() != null? camp.getCodi() : "",
				camp.getDefinicioProces() != null ? camp.getDefinicioProces().getJbpmKey() : "",
				camp.getDefinicioProces() != null ? camp.getDefinicioProces().getVersio() : -1);
		
		Map<String, Consulta> mapConsultes = new HashMap<String, Consulta>();
		for (ConsultaCamp cc : consultaCamps )
			if (!mapConsultes.containsKey(cc.getConsulta().getCodi()))
				mapConsultes.put(cc.getConsulta().getCodi(), cc.getConsulta());
		
		return conversioTipusHelper.convertirList(
				new ArrayList<Consulta>(mapConsultes.values()), 
				ConsultaDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CampDto> findRegistresPerCamp(Long campId) {
		logger.debug(
				"Consultant els registres pel camp (" +
				"campId =" + campId +")");
		
		Camp camp = campRepository.findOne(campId);
		List<Camp> registres = new ArrayList<Camp>();
		for (CampRegistre campRegistre : camp.getRegistrePares())
			registres.add(campRegistre.getRegistre());
		
		return conversioTipusHelper.convertirList(
				registres, 
				CampDto.class);
	}
}