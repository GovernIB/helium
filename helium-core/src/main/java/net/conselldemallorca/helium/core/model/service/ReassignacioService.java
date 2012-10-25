package net.conselldemallorca.helium.core.model.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.core.model.dao.ReassignacioDao;
import net.conselldemallorca.helium.core.model.hibernate.Reassignacio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.annotation.Secured;
import org.springframework.stereotype.Service;

/**
 * Servei per a gestionar les reassignacions entre usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Service
public class ReassignacioService {

	private ReassignacioDao reassignacioDao;



	public List<Reassignacio> llistaReassignacions() {
		return reassignacioDao.findLlistaActius();
	}

	public List<Reassignacio> llistaReassignacions(Long expedientTipusId) {
		return reassignacioDao.findLlistaActius(expedientTipusId);
	}
	
	public List<Reassignacio> llistaReassignacionsMod(Long id) {
		return reassignacioDao.findLlistaActiusModificacio(id);
	}
	
	@Secured({"ROLE_ADMIN", "ROLE_USER"})
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
		reassignacioDao.saveOrUpdate(reassignacio);
	}

	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	public void updateReassignacio(
			Long id,
			String usuariOrigen,
			String usuariDesti,
			Date dataInici,
			Date dataFi,
			Date dataCancelacio,
			Long tipusExpedientId) {
		Reassignacio reassignacio = reassignacioDao.getById(id, false);
		reassignacio.setUsuariOrigen(usuariOrigen);
		reassignacio.setUsuariDesti(usuariDesti);
		reassignacio.setDataInici(dataInici);
		reassignacio.setDataFi(dataFi);
		reassignacio.setDataCancelacio(dataCancelacio);
		reassignacio.setTipusExpedientId(tipusExpedientId);
		reassignacioDao.saveOrUpdate(reassignacio);
	}

	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	public void deleteReassignacio(Long id) {
		Reassignacio reassignacio = reassignacioDao.getById(id, false);
		if (reassignacio != null) {
			reassignacio.setDataCancelacio(Calendar.getInstance().getTime());
			reassignacioDao.saveOrUpdate(reassignacio);
		}
	}

	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	public Reassignacio findReassignacioById(Long id) {
		return reassignacioDao.getById(id, false);
	}



	@Autowired
	public void setReassignacioDao(ReassignacioDao reassignacioDao) {
		this.reassignacioDao = reassignacioDao;
	}

}
