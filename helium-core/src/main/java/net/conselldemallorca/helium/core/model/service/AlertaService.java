/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.core.model.dao.AlertaDao;
import net.conselldemallorca.helium.core.model.dao.TerminiIniciatDao;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


/**
 * Servei per gestionar les alertes d'usuari
 * 
 * @author Limit Tecnologies <limit@limit.es>
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
	public List<Alerta> findActivesAmbEntornITipusExpedient(
			Long entornId,
			Long expedientTipusId) {
		return alertaDao.findActivesAmbEntornITipusExpedient(entornId, expedientTipusId);
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
