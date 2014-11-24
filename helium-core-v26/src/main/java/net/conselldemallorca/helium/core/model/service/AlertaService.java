/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.dao.AlertaDao;
import net.conselldemallorca.helium.core.model.dao.TerminiIniciatDao;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
	public static final int ALERTAS_NO_LLEGIDES = 0;
	public static final int ALERTAS_LLEGIDES = 1;
	public static final int ALERTAS_TODAS = 2;

	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;
	
	private AlertaDao alertaDao;
	private TerminiIniciatDao terminiIniciatDao;

	public Alerta getById(Long id) {
		return alertaDao.getById(id, false);
	}
	
	@CacheEvict(value = "alertaCache", allEntries=true)
	public Alerta create(Alerta entity) {
		return alertaDao.saveOrUpdate(entity);
	}
	
	@CacheEvict(value = "alertaCache", allEntries=true)
	public Alerta update(Alerta entity) {
		return alertaDao.merge(entity);
	}
	
	@CacheEvict(value = "alertaCache", allEntries=true)
	public void delete(Alerta alerta) {
		alertaDao.delete(alerta);
	}
	
	@CacheEvict(value = "alertaCache", allEntries=true)
	public void deleteAmbId(Long id) {
		Alerta vella = getById(id);
		if (vella != null)
			alertaDao.delete(id);
	}
	
//	public int countActivesAmbEntornIUsuariAutenticat(Long entornId) {
//		mesuresTemporalsHelper.mesuraIniciar("Obtenir count alertes todas", "consulta");
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		int count = countActivesAmbEntornIUsuari(entornId, auth.getName(), ALERTAS_TODAS);
//		mesuresTemporalsHelper.mesuraCalcular("Obtenir count alertes todas", "consulta");
//		return count;
//	}
//
//	public int countActivesLlegidesAmbEntornIUsuariAutenticat(Long entornId) {
//		mesuresTemporalsHelper.mesuraIniciar("Obtenir count alertes llegides", "consulta");
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		int count = countActivesAmbEntornIUsuari(entornId, auth.getName(), ALERTAS_LLEGIDES);
//		mesuresTemporalsHelper.mesuraCalcular("Obtenir count alertes llegides", "consulta");
//		return count;
//	}	
//	
//	public int countActivesNoLlegidesAmbEntornIUsuariAutenticat(Long entornId) {
//		mesuresTemporalsHelper.mesuraIniciar("Obtenir count alertes no llegides", "consulta");
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		int count = countActivesAmbEntornIUsuari(entornId, auth.getName(), ALERTAS_NO_LLEGIDES);
//		mesuresTemporalsHelper.mesuraCalcular("Obtenir count alertes no llegides", "consulta");
//		return count;
//	}
	
	@Cacheable(value="alertaCache")
	public int countActivesAmbEntornIUsuari(Long entornId, String name, int alertas) {
		mesuresTemporalsHelper.mesuraIniciar("Obtenir count alertes " + alertas, "consulta");
		int count = alertaDao.countActivesAmbEntornIUsuari(entornId, name, alertas);
		mesuresTemporalsHelper.mesuraCalcular("Obtenir count alertes " + alertas, "consulta");
		return count;
	}
	
	@CacheEvict(value = "alertaCache", allEntries=true)
	public List<Alerta> findActivesAmbEntornIUsuariAutenticat(Long entornId) {
		mesuresTemporalsHelper.mesuraIniciar("Obtenir alertes", "consulta");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<Alerta> list = alertaDao.findActivesAmbEntornIUsuari(entornId, auth.getName());
		mesuresTemporalsHelper.mesuraCalcular("Obtenir alertes", "consulta");
		return list;
	}
	
	@CacheEvict(value = "alertaCache", allEntries=true)
	public List<Alerta> findActivesAmbEntornITipusExpedient(
			Long entornId,
			Long expedientTipusId) {
		return alertaDao.findActivesAmbEntornITipusExpedient(entornId, expedientTipusId);
	}

	@CacheEvict(value = "alertaCache", allEntries=true)
	public void marcarLlegida(Long alertaId) {
		Alerta alerta = getById(alertaId);
		alerta.setDataLectura(new Date());
	}
	
	@CacheEvict(value = "alertaCache", allEntries=true)
	public void desmarcarLlegida(Long alertaId) {
		Alerta alerta = getById(alertaId);
		alerta.setDataLectura(null);
	}
	
	@CacheEvict(value = "alertaCache", allEntries=true)
	public void marcarEsborrada(Long alertaId) {
		Alerta alerta = getById(alertaId);
		alerta.setDataEliminacio(new Date());
	}
	
	@CacheEvict(value = "alertaCache", allEntries=true)
	public void desmarcarEsborrada(Long alertaId) {
		Alerta alerta = getById(alertaId);
		alerta.setDataEliminacio(null);
	}

	@CacheEvict(value = "alertaCache", allEntries=true)
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
