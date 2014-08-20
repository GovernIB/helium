package net.conselldemallorca.helium.v3.core.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.hibernate.Reassignacio;
import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.v3.core.api.service.ReassignacioUsuarisService;
import net.conselldemallorca.helium.v3.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.v3.core.helper.DtoConverter;
import net.conselldemallorca.helium.v3.core.repository.ReassignacioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servei per a gestionar les reassignacions entre usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ReassignacioUsuarisServiceImpl implements ReassignacioUsuarisService {

	@Resource
	private ReassignacioRepository reassignacioRepository;
	@Resource(name="dtoConverterV3")
	private DtoConverter dtoConverter;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	
	@Transactional
	@Override
	public List<ReassignacioDto> llistaReassignacions() {
		return conversioTipusHelper.convertirList(reassignacioRepository.findLlistaActius(Calendar.getInstance().getTime()), ReassignacioDto.class);
	}
	
	@Transactional
	@Override
	public void createReassignacio(
			String usuariOrigen,
			String usuariDesti,
			Date dataInici,
			Date dataFi,
			Date dataCancelacio,
			Long tipusExpedientId) {
		Reassignacio reassignacio = new Reassignacio();
		reassignacio.setUsuariOrigen(usuariOrigen);
		reassignacio.setUsuariDesti(usuariDesti);
		reassignacio.setDataInici(dataInici);
		reassignacio.setDataFi(dataFi);
		reassignacio.setDataCancelacio(dataCancelacio);
		reassignacio.setTipusExpedientId(tipusExpedientId);
		reassignacioRepository.save(reassignacio);
	}

	@Transactional
	@Override
	public void updateReassignacio(
			Long id,
			String usuariOrigen,
			String usuariDesti,
			Date dataInici,
			Date dataFi,
			Date dataCancelacio,
			Long tipusExpedientId) {
		Reassignacio reassignacio = reassignacioRepository.findOne(id);
		reassignacio.setUsuariOrigen(usuariOrigen);
		reassignacio.setUsuariDesti(usuariDesti);
		reassignacio.setDataInici(dataInici);
		reassignacio.setDataFi(dataFi);
		reassignacio.setDataCancelacio(dataCancelacio);
		reassignacio.setTipusExpedientId(tipusExpedientId);
		reassignacioRepository.save(reassignacio);
	}

	@Transactional
	@Override
	public void deleteReassignacio(Long id) {
		Reassignacio reassignacio = reassignacioRepository.findOne(id);
		if (reassignacio != null) {
			reassignacio.setDataCancelacio(Calendar.getInstance().getTime());
			reassignacioRepository.save(reassignacio);
		}
	}

	@Transactional
	@Override
	public ReassignacioDto findReassignacioById(Long id) {
		return conversioTipusHelper.convertir(reassignacioRepository.findOne(id), ReassignacioDto.class);
	}
}
