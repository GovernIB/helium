/**
 * 
 */
package net.conselldemallorca.helium.model.service;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.model.dao.AlertaDao;
import net.conselldemallorca.helium.model.dao.TerminiIniciatDao;
import net.conselldemallorca.helium.model.hibernate.Alerta;
import net.conselldemallorca.helium.model.hibernate.TerminiIniciat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


/**
 * Servei per gestionar les alertes d'usuari
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Service
public class AlertaService {

	private AlertaDao alertaDao;
	private TerminiIniciatDao terminiIniciatDao;



	public Alerta getById(Long id) {
		return alertaDao.getById(id, false);
	}
	public Alerta create(Alerta entity) {
		return alertaDao.saveOrUpdate(entity);
	}
	public Alerta update(Alerta entity) {
		return alertaDao.merge(entity);
	}
	public void delete(Long id) {
		Alerta vella = getById(id);
		if (vella != null)
			alertaDao.delete(id);
	}
	public List<Alerta> findActivesAmbEntornIUsuariAutenticat(Long entornId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return alertaDao.findActivesAmbEntornIUsuari(entornId, auth.getName());
	}

	public void marcarLlegida(Long alertaId) {
		Alerta alerta = getById(alertaId);
		alerta.setDataLectura(new Date());
	}
	public void desmarcarLlegida(Long alertaId) {
		Alerta alerta = getById(alertaId);
		alerta.setDataLectura(null);
	}
	public void marcarEsborrada(Long alertaId) {
		Alerta alerta = getById(alertaId);
		alerta.setDataEliminacio(new Date());
	}
	public void desmarcarEsborrada(Long alertaId) {
		Alerta alerta = getById(alertaId);
		alerta.setDataEliminacio(null);
	}

	public void esborrarAmbTasca(Long taskInstanceId) {
		Date ara = new Date();
		List<TerminiIniciat> terminis = terminiIniciatDao.findAmbTaskInstanceId(taskInstanceId);
		for (TerminiIniciat termini: terminis) {
			for (Alerta alerta: termini.getAlertes()) {
				alerta.setDataEliminacio(ara);
			}
		}
	}



	@Autowired
	public void setAlertaDao(AlertaDao alertaDao) {
		this.alertaDao = alertaDao;
	}
	@Autowired
	public void setTerminiIniciatDao(TerminiIniciatDao terminiIniciatDao) {
		this.terminiIniciatDao = terminiIniciatDao;
	}

}
