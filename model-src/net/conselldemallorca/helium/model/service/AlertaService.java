/**
 * 
 */
package net.conselldemallorca.helium.model.service;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.model.dao.AlertaDao;
import net.conselldemallorca.helium.model.hibernate.Alerta;

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

	@Autowired
	public void setAlertaDao(AlertaDao alertaDao) {
		this.alertaDao = alertaDao;
	}

}
