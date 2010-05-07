/**
 * 
 */
package net.conselldemallorca.helium.model.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.jbpm3.integracio.JbpmDao;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.model.dao.ExpedientDao;
import net.conselldemallorca.helium.model.dao.FestiuDao;
import net.conselldemallorca.helium.model.dao.RegistreDao;
import net.conselldemallorca.helium.model.dao.TerminiDao;
import net.conselldemallorca.helium.model.dao.TerminiIniciatDao;
import net.conselldemallorca.helium.model.hibernate.Expedient;
import net.conselldemallorca.helium.model.hibernate.Festiu;
import net.conselldemallorca.helium.model.hibernate.Termini;
import net.conselldemallorca.helium.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


/**
 * Servei per gestionar els terminis dels expedients
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Service
public class TerminiService {

	private TerminiDao terminiDao;
	private TerminiIniciatDao terminiIniciatDao;
	private FestiuDao festiuDao;
	private RegistreDao registreDao;
	private ExpedientDao expedientDao;
	private JbpmDao jbpmDao;



	public TerminiIniciat iniciar(
			Long terminiId,
			String processInstanceId) {
		return iniciar(terminiId, processInstanceId, new Date());
	}
	public TerminiIniciat iniciar(
			Long terminiId,
			String processInstanceId,
			Date dataInici) {
		Termini termini = terminiDao.getById(terminiId, false);
		Date dataFi = getDataFiTermini(
				dataInici,
				termini);
		TerminiIniciat terminiIniciat = terminiIniciatDao.findAmbTerminiIdIProcessInstanceId(
				terminiId,
				processInstanceId);
		if (terminiIniciat == null) {
			terminiIniciat = new TerminiIniciat(
					termini,
					processInstanceId,
					dataInici,
					dataFi);
		} else {
			terminiIniciat.setDataFi(dataFi);
			terminiIniciat.setDataAturada(null);
			terminiIniciat.setDataCancelacio(null);
		}
		registreDao.crearRegistreIniciarTermini(
				getExpedientForProcessInstanceId(processInstanceId).getId(),
				processInstanceId,
				terminiId.toString(),
				SecurityContextHolder.getContext().getAuthentication().getName());
		return terminiIniciatDao.saveOrUpdate(terminiIniciat);
	}
	public void pausar(Long terminiIniciatId) {
		pausar(terminiIniciatId, new Date());
	}
	public void pausar(Long terminiIniciatId, Date data) {
		TerminiIniciat terminiIniciat = terminiIniciatDao.getById(terminiIniciatId, false);
		if (terminiIniciat.getDataInici() == null)
			throw new net.conselldemallorca.helium.model.exception.IllegalStateException("El termini no està iniciat");
		terminiIniciat.setDataAturada(data);
		String processInstanceId = terminiIniciat.getProcessInstanceId();
		registreDao.crearRegistreAturarTermini(
				getExpedientForProcessInstanceId(processInstanceId).getId(),
				processInstanceId,
				terminiIniciat.getTermini().getId().toString(),
				SecurityContextHolder.getContext().getAuthentication().getName());
	}
	public void continuar(Long terminiIniciatId) {
		continuar(terminiIniciatId, new Date());
	}
	public void continuar(Long terminiIniciatId, Date data) {
		TerminiIniciat terminiIniciat = terminiIniciatDao.getById(terminiIniciatId, false);
		if (terminiIniciat.getDataAturada() == null)
			throw new net.conselldemallorca.helium.model.exception.IllegalStateException("El termini no està pausat");
		int diesAturat = terminiIniciat.getNumDiesAturadaActual(data);
		terminiIniciat.setDiesAturat(terminiIniciat.getDiesAturat() + diesAturat);
		Calendar cal = Calendar.getInstance();
		cal.setTime(terminiIniciat.getDataFi());
		cal.add(Calendar.DAY_OF_MONTH, diesAturat);
		terminiIniciat.setDataAturada(null);
		String processInstanceId = terminiIniciat.getProcessInstanceId();
		registreDao.crearRegistreReprendreTermini(
				getExpedientForProcessInstanceId(processInstanceId).getId(),
				processInstanceId,
				terminiIniciat.getTermini().getId().toString(),
				SecurityContextHolder.getContext().getAuthentication().getName());
	}
	public void cancelar(Long terminiIniciatId) {
		cancelar(terminiIniciatId, new Date());
	}
	public void cancelar(Long terminiIniciatId, Date data) {
		TerminiIniciat terminiIniciat = terminiIniciatDao.getById(terminiIniciatId, false);
		if (terminiIniciat.getDataInici() == null)
			throw new net.conselldemallorca.helium.model.exception.IllegalStateException("El termini no està iniciat");
		terminiIniciat.setDataCancelacio(data);
		String processInstanceId = terminiIniciat.getProcessInstanceId();
		registreDao.crearRegistreCancelarTermini(
				getExpedientForProcessInstanceId(processInstanceId).getId(),
				processInstanceId,
				terminiIniciat.getTermini().getId().toString(),
				SecurityContextHolder.getContext().getAuthentication().getName());
	}
	public List<TerminiIniciat> findIniciatsAmbProcessInstanceId(String processInstanceId) {
		return terminiIniciatDao.findAmbProcessInstanceId(processInstanceId);
	}
	public TerminiIniciat findIniciatAmbTerminiIdIProcessInstanceId(
			Long terminiId,
			String processInstanceId) {
		return terminiIniciatDao.findAmbTerminiIdIProcessInstanceId(terminiId, processInstanceId);
	}
	public void setTimerName(Long terminiIniciatId, String timerName) {
		TerminiIniciat terminiIniciat = terminiIniciatDao.getById(terminiIniciatId, false);
		terminiIniciat.setTimerName(timerName);
	}

	public Festiu getFestiuById(Long id) {
		return festiuDao.getById(id, false);
	}
	public Festiu createFestiu(Festiu entity) {
		festiuDao.modificacioFestius();
		return festiuDao.saveOrUpdate(entity);
	}
	public Festiu updateFestiu(Festiu entity) {
		festiuDao.modificacioFestius();
		return festiuDao.merge(entity);
	}
	public void deleteFestiu(Long id) {
		festiuDao.modificacioFestius();
		Festiu vell = getFestiuById(id);
		if (vell != null) {
			festiuDao.delete(id);
		}
	}
	public List<Festiu> findFestiuAmbAny(int any) {
		return festiuDao.findAmbAny(any);
	}
	public Festiu findFestiuAmbData(Date data) {
		return festiuDao.findAmbData(data);
	}



	@Autowired
	public void setTerminiDao(TerminiDao terminiDao) {
		this.terminiDao = terminiDao;
	}
	@Autowired
	public void setTerminiIniciatDao(TerminiIniciatDao terminiIniciatDao) {
		this.terminiIniciatDao = terminiIniciatDao;
	}
	@Autowired
	public void setFestiuDao(FestiuDao festiuDao) {
		this.festiuDao = festiuDao;
	}
	@Autowired
	public void setRegistreDao(RegistreDao registreDao) {
		this.registreDao = registreDao;
	}
	@Autowired
	public void setExpedientDao(ExpedientDao expedientDao) {
		this.expedientDao = expedientDao;
	}
	@Autowired
	public void setJbpmDao(JbpmDao jbpmDao) {
		this.jbpmDao = jbpmDao;
	}



	private Date getDataFiTermini(
			Date inici,
			Termini termini) {
		int anys = termini.getAnys();
		int mesos = termini.getMesos();
		int dies = termini.getDies();
		boolean laborable = termini.isLaborable();
		Calendar dataFi = Calendar.getInstance();
		dataFi.setTime(inici); //inicialitzam la data final amb la data d'inici
		// Afegim els anys i mesos
		if (anys > 0) {
			dataFi.add(Calendar.YEAR, anys);
			dataFi.add(Calendar.DAY_OF_YEAR, -1);
		}
		if (mesos > 0) {
			dataFi.add(Calendar.MONTH, mesos);
			dataFi.add(Calendar.DAY_OF_YEAR, -1);
		}
		if (dies > 0) {
			// Depenent de si el termini és laborable o no s'afegiran més o manco dies
			if (laborable) {
				sumarDies(dataFi, dies);
			} else {
				dataFi.add(Calendar.DATE, dies - 1);
				// Si el darrer dia cau en festiu es passa al dia laborable següent
				sumarDies(dataFi, 1);
			}
			// El termini en realitat acaba a les 23:59 del darrer dia
			dataFi.set(Calendar.HOUR_OF_DAY, 23);
			dataFi.set(Calendar.MINUTE, 59);
			dataFi.set(Calendar.SECOND, 59);
			dataFi.set(Calendar.MILLISECOND, 999);
		}
		return dataFi.getTime();
	}

	private void sumarDies(Calendar cal, int numDies) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		List<Festiu> festius = festiuDao.findAll();
		int diesLabs = 0;
		while (diesLabs < numDies) {
			if (!esFestiu(cal, festius))
				diesLabs ++;
			cal.add(Calendar.DATE, 1);
		}
		cal.add(Calendar.DATE, -1);
	}
	private boolean esFestiu(
			Calendar cal,
			List<Festiu> festius) {
		int diasem = cal.get(Calendar.DAY_OF_WEEK);
		for (int nolab: getDiesNoLaborables()) {
			if (diasem == nolab)
				return true;
		}
		for (Festiu festiu: festius) {
			if (cal.getTime().compareTo(festiu.getData()) == 0)
				return true;
		}
		return false;
	}
	private int[] getDiesNoLaborables() {
		String nolabs = GlobalProperties.getInstance().getProperty("app.calendari.nolabs");
		if (nolabs != null) {
			String[] dies = nolabs.split(",");
			int[] resposta = new int[dies.length];
			for (int i = 0; i < dies.length; i++) {
				resposta[i] = (Integer.parseInt(dies[i]) % 7) + 1;
			}
			return resposta;
		}
		return new int[0];
	}

	private Expedient getExpedientForProcessInstanceId(String processInstanceId) {
		JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(processInstanceId);
		if (pi == null)
			return null;
		return expedientDao.findAmbProcessInstanceId(pi.getId());
	}

}
