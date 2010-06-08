package net.conselldemallorca.helium.model.service;

import java.util.Calendar;
import java.util.List;

import net.conselldemallorca.helium.model.dao.ReassignacioDao;
import net.conselldemallorca.helium.model.hibernate.Reassignacio;
import net.conselldemallorca.helium.presentacio.mvc.ReassignacioCommand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.annotation.Secured;
import org.springframework.stereotype.Service;

/**
 * Servei per a gestionar les reassignacions entre usuaris.
 * 
 * @author Miquel Angel Amengual <miquelaa@limit.es>
 */

@Service
public class ReassignacioService {

	private ReassignacioDao reassignacioDao;

	public List<Reassignacio> llistaReassignacions() {
		return reassignacioDao.findLlistaActius();
	}
	
	@Secured({"ROLE_ADMIN"})
	public void createReassignacio(ReassignacioCommand command) {
		Reassignacio reassignacio = new Reassignacio();
		reassignacio.setUsuariOrigen(command.getUsuariOrigen());
		reassignacio.setUsuariDesti(command.getUsuariDesti());
		reassignacio.setDataInici(command.getDataInici());
		reassignacio.setDataFi(command.getDataFi());
		reassignacio.setDataCancelacio(command.getDataCancelacio());
		reassignacioDao.saveOrUpdate(reassignacio);
	}
	
	@Secured({"ROLE_ADMIN"})
	public void updateReassignacio(ReassignacioCommand command) {
		Reassignacio reassignacio = reassignacioDao.getById(command.getId(), false);
		reassignacio.setUsuariOrigen(command.getUsuariOrigen());
		reassignacio.setUsuariDesti(command.getUsuariDesti());
		reassignacio.setDataInici(command.getDataInici());
		reassignacio.setDataFi(command.getDataFi());
		reassignacio.setDataCancelacio(command.getDataCancelacio());
		reassignacioDao.saveOrUpdate(reassignacio);
	}
	
	@Secured({"ROLE_ADMIN"})
	public void deleteReassignacio(Long id) {
		Reassignacio reassignacio = reassignacioDao.getById(id, false);
		if (reassignacio != null) {
			reassignacio.setDataCancelacio(Calendar.getInstance().getTime());
			reassignacioDao.saveOrUpdate(reassignacio);
		}
	}
	
	@Secured({"ROLE_ADMIN"})
	public Reassignacio findReassignacioById(Long id) {
		return reassignacioDao.getById(id, false);
	}
	
	@Autowired
	public void setReassignacioDao(ReassignacioDao reassignacioDao) {
		this.reassignacioDao = reassignacioDao;
	}
}
