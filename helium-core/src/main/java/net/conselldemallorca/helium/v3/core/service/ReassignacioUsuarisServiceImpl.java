package net.conselldemallorca.helium.v3.core.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.hibernate.Reassignacio;
import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.v3.core.api.service.ReassignacioUsuarisService;
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

	@Transactional
	@Override
	public List<ReassignacioDto> llistaReassignacions() {
		return DtoConverter.toLlistatReassignacioDto(reassignacioRepository.findLlistaActius(Calendar.getInstance().getTime()));
	}

	@Transactional
	@Override
	public List<ReassignacioDto> llistaReassignacions(Long expedientTipusId) {
		return DtoConverter.toLlistatReassignacioDto(reassignacioRepository.findLlistaActius(expedientTipusId, Calendar.getInstance().getTime()));
	}

	@Transactional
	@Override
	public List<ReassignacioDto> llistaReassignacionsMod(Long id) {
		return DtoConverter.toLlistatReassignacioDto(reassignacioRepository.findLlistaActiusModificacio(id, Calendar.getInstance().getTime()));
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
		return DtoConverter.toReassignacioDto(reassignacioRepository.findOne(id));
	}
}
