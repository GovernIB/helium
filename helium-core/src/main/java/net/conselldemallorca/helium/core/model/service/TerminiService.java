/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.jbpm3.integracio.JbpmDao;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.core.model.dao.AlertaDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientDao;
import net.conselldemallorca.helium.core.model.dao.FestiuDao;
import net.conselldemallorca.helium.core.model.dao.RegistreDao;
import net.conselldemallorca.helium.core.model.dao.TerminiDao;
import net.conselldemallorca.helium.core.model.dao.TerminiIniciatDao;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Festiu;
import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat.TerminiIniciatEstat;
import net.conselldemallorca.helium.core.util.GlobalProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


/**
 * Servei per gestionar els terminis dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class TerminiService {

	private TerminiDao terminiDao;
	private TerminiIniciatDao terminiIniciatDao;
	private FestiuDao festiuDao;
	private RegistreDao registreDao;
	private ExpedientDao expedientDao;
	private AlertaDao alertaDao;
	private JbpmDao jbpmDao;
	private MessageSource messageSource;



	public TerminiIniciat iniciar(
			Long terminiId,
			String processInstanceId,
			Date data,
			boolean esDataFi) {
		Termini termini = terminiDao.getById(terminiId, false);
		TerminiIniciat terminiIniciat = terminiIniciatDao.findAmbTerminiIdIProcessInstanceId(
				terminiId,
				processInstanceId);
		if (terminiIniciat == null) {
			return iniciar(
					terminiId,
					processInstanceId,
					data,
					termini.getAnys(),
					termini.getMesos(),
					termini.getDies(),
					esDataFi);
		} else {
			return iniciar(
					terminiId,
					processInstanceId,
					data,
					terminiIniciat.getAnys(),
					terminiIniciat.getMesos(),
					terminiIniciat.getDies(),
					esDataFi);
		}
	}
	public TerminiIniciat iniciar(
			Long terminiId,
			String processInstanceId,
			Date data,
			int anys,
			int mesos,
			int dies,
			boolean esDataFi) {
		Termini termini = terminiDao.getById(terminiId, false);
		TerminiIniciat terminiIniciat = terminiIniciatDao.findAmbTerminiIdIProcessInstanceId(
				terminiId,
				processInstanceId);
		if (terminiIniciat == null) {
			if (esDataFi) {
				Date dataInici = getDataIniciTermini(
						data,
						anys,
						mesos,
						dies,
						termini.isLaborable());
				terminiIniciat = new TerminiIniciat(
						termini,
						anys,
						mesos,
						dies,
						processInstanceId,
						dataInici,
						data);
			} else {
				Date dataFi = getDataFiTermini(
						data,
						anys,
						mesos,
						dies,
						termini.isLaborable());
				terminiIniciat = new TerminiIniciat(
						termini,
						anys,
						mesos,
						dies,
						processInstanceId,
						data,
						dataFi);
			}
		} else {
			if (esDataFi) {
				Date dataInici = getDataIniciTermini(
						data,
						anys,
						mesos,
						dies,
						termini.isLaborable());
				terminiIniciat.setDataInici(dataInici);
				terminiIniciat.setDataFi(data);
			} else {
				Date dataFi = getDataFiTermini(
						data,
						anys,
						mesos,
						dies,
						termini.isLaborable());
				terminiIniciat.setDataInici(data);
				terminiIniciat.setDataFi(dataFi);
			}
			terminiIniciat.setDataAturada(null);
			terminiIniciat.setDataCancelacio(null);
			resumeTimers(terminiIniciat);
		}
		Long expedientId = getExpedientForProcessInstanceId(processInstanceId).getId();
		if (expedientId != null) {
			registreDao.crearRegistreIniciarTermini(
					getExpedientForProcessInstanceId(processInstanceId).getId(),
					processInstanceId,
					terminiId.toString(),
					SecurityContextHolder.getContext().getAuthentication().getName());
		}
		return terminiIniciatDao.saveOrUpdate(terminiIniciat);
	}
	public void pausar(Long terminiIniciatId, Date data) {
		TerminiIniciat terminiIniciat = terminiIniciatDao.getById(terminiIniciatId, false);
		if (terminiIniciat.getDataInici() == null)
			throw new net.conselldemallorca.helium.core.model.exception.IllegalStateException( getMessage("error.terminiService.noIniciat") );
		terminiIniciat.setDataAturada(data);
		suspendTimers(terminiIniciat);
		String processInstanceId = terminiIniciat.getProcessInstanceId();
		Long expedientId = getExpedientForProcessInstanceId(processInstanceId).getId();
		if (expedientId != null) {
			registreDao.crearRegistreAturarTermini(
					getExpedientForProcessInstanceId(processInstanceId).getId(),
					processInstanceId,
					terminiIniciat.getTermini().getId().toString(),
					SecurityContextHolder.getContext().getAuthentication().getName());
		}
	}
	public void continuar(Long terminiIniciatId, Date data) {
		TerminiIniciat terminiIniciat = terminiIniciatDao.getById(terminiIniciatId, false);
		if (terminiIniciat.getDataAturada() == null)
			throw new net.conselldemallorca.helium.core.model.exception.IllegalStateException( getMessage("error.terminiService.noPausat") );
		int diesAturat = terminiIniciat.getNumDiesAturadaActual(data);
		terminiIniciat.setDiesAturat(terminiIniciat.getDiesAturat() + diesAturat);
		terminiIniciat.setDataAturada(null);
		resumeTimers(terminiIniciat);
		String processInstanceId = terminiIniciat.getProcessInstanceId();
		Long expedientId = getExpedientForProcessInstanceId(processInstanceId).getId();
		if (expedientId != null) {
			registreDao.crearRegistreReprendreTermini(
					getExpedientForProcessInstanceId(processInstanceId).getId(),
					processInstanceId,
					terminiIniciat.getTermini().getId().toString(),
					SecurityContextHolder.getContext().getAuthentication().getName());
		}
	}
	public void cancelar(Long terminiIniciatId, Date data) {
		TerminiIniciat terminiIniciat = terminiIniciatDao.getById(terminiIniciatId, false);
		if (terminiIniciat.getDataInici() == null)
			throw new net.conselldemallorca.helium.core.model.exception.IllegalStateException( getMessage("error.terminiService.noIniciat") );
		terminiIniciat.setDataCancelacio(data);
		suspendTimers(terminiIniciat);
		String processInstanceId = terminiIniciat.getProcessInstanceId();
		Long expedientId = getExpedientForProcessInstanceId(processInstanceId).getId();
		if (expedientId != null) {
			registreDao.crearRegistreCancelarTermini(
					getExpedientForProcessInstanceId(processInstanceId).getId(),
					processInstanceId,
					terminiIniciat.getTermini().getId().toString(),
					SecurityContextHolder.getContext().getAuthentication().getName());
		}
	}
	public Date getDataFiTermini(
			Date inici,
			int anys,
			int mesos,
			int dies,
			boolean laborable) {
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
	public Date getDataIniciTermini(
			Date fi,
			int anys,
			int mesos,
			int dies,
			boolean laborable) {
		Calendar dataInici = Calendar.getInstance();
		dataInici.setTime(fi); //inicialitzam la data final amb la data d'inici
		// Afegim els anys i mesos
		if (anys > 0) {
			dataInici.add(Calendar.YEAR, -anys);
			dataInici.add(Calendar.DAY_OF_YEAR, -1);
		}
		if (mesos > 0) {
			dataInici.add(Calendar.MONTH, -mesos);
			dataInici.add(Calendar.DAY_OF_YEAR, -1);
		}
		if (dies > 0) {
			// Depenent de si el termini és laborable o no s'afegiran més o manco dies
			if (laborable) {
				sumarDies(dataInici, -dies);
			} else {
				dataInici.add(Calendar.DATE, -dies + 1);
				// Si el darrer dia cau en festiu es passa al dia laborable següent
				sumarDies(dataInici, -1);
			}
			// El termini en realitat s'inicia a les 00:00h
			dataInici.set(Calendar.HOUR_OF_DAY, 0);
			dataInici.set(Calendar.MINUTE, 0);
			dataInici.set(Calendar.SECOND, 0);
			dataInici.set(Calendar.MILLISECOND, 0);
		}
		return dataInici.getTime();
	}
	public List<TerminiIniciat> findIniciatsAmbProcessInstanceId(String processInstanceId) {
		return terminiIniciatDao.findAmbProcessInstanceId(processInstanceId);
	}
	public TerminiIniciat findIniciatAmbTerminiIdIProcessInstanceId(
			Long terminiId,
			String processInstanceId) {
		return terminiIniciatDao.findAmbTerminiIdIProcessInstanceId(terminiId, processInstanceId);
	}
	public List<TerminiIniciat> findIniciatsAmbTaskInstanceIds(String[] taskInstanceIds) {
		if (taskInstanceIds == null || taskInstanceIds.length == 0)
			return new ArrayList<TerminiIniciat>();
		return terminiIniciatDao.findAmbTaskInstanceIds(taskInstanceIds);
	}
	public void configurarTerminiIniciatAmbDadesJbpm(
			Long terminiIniciatId,
			String taskInstanceId,
			Long timerId) {
		TerminiIniciat terminiIniciat = terminiIniciatDao.getById(terminiIniciatId, false);
		terminiIniciat.setTaskInstanceId(taskInstanceId);
		if (timerId != null)
			terminiIniciat.afegirTimerId(timerId.longValue());
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

	public void comprovarTerminisIniciats() {
		logger.debug("Inici de la comprovació de terminis");
		List<TerminiIniciat> iniciatsActius = terminiIniciatDao.findIniciatsActius();
		for (TerminiIniciat terminiIniciat: iniciatsActius) {
			if (terminiIniciat.getTaskInstanceId() != null) {
				if (terminiIniciat.getEstat()==TerminiIniciatEstat.CADUCAT && terminiIniciat.getTermini().isAlertaFinal() && ! terminiIniciat.isAlertaFinal()) {
					esborrarAlertesAntigues(terminiIniciat);
					JbpmTask task = jbpmDao.getTaskById(terminiIniciat.getTaskInstanceId());
					if (task.getAssignee() != null) {
						crearAlertaFinal(terminiIniciat, task.getAssignee(), getExpedientPerTask(task));
					} else {
						for (String actor: task.getPooledActors())
							crearAlertaFinal(terminiIniciat, actor, getExpedientPerTask(task));
					}
					terminiIniciat.setAlertaFinal(true);
				}
				else if (terminiIniciat.getEstat()==TerminiIniciatEstat.AVIS && terminiIniciat.getTermini().isAlertaPrevia() && ! terminiIniciat.isAlertaPrevia()) {
					JbpmTask task = jbpmDao.getTaskById(terminiIniciat.getTaskInstanceId());
					if (task.getAssignee() != null) {
						crearAlertaPrevia(terminiIniciat, task.getAssignee(), getExpedientPerTask(task));
					} else {
						for (String actor: task.getPooledActors())
							crearAlertaPrevia(terminiIniciat, actor, getExpedientPerTask(task));
					}
					terminiIniciat.setAlertaPrevia(true);
				}
			}
			else {
				esborrarAlertesAntigues(terminiIniciat);
			}
		}
		logger.debug("Fi de la comprovació de terminis");
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
	public void setAlertaDao(AlertaDao alertaDao) {
		this.alertaDao = alertaDao;
	}
	@Autowired
	public void setJbpmDao(JbpmDao jbpmDao) {
		this.jbpmDao = jbpmDao;
	}
	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}



	private void sumarDies(Calendar cal, int numDies) {
		int signe = (numDies < 0) ? -1 : 1;
		int nd = (numDies < 0) ? -numDies : numDies;
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		List<Festiu> festius = festiuDao.findAll();
		int diesLabs = 0;
		while (diesLabs < nd) {
			if (!esFestiu(cal, festius))
				diesLabs ++;
			cal.add(Calendar.DATE, signe);
		}
		cal.add(Calendar.DATE, -signe);
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
		if (pi == null) {
			return null;
		}
		return expedientDao.findAmbProcessInstanceId(pi.getId());
	}

	private void suspendTimers(TerminiIniciat terminiIniciat) {
		long[] timerIds = terminiIniciat.getTimerIdsArray();
		for (int i = 0; i < timerIds.length; i++)
			jbpmDao.suspendTimer(
					timerIds[i],
					new Date(Long.MAX_VALUE));
	}
	private void resumeTimers(TerminiIniciat terminiIniciat) {
		long[] timerIds = terminiIniciat.getTimerIdsArray();
		for (int i = 0; i < timerIds.length; i++)
			jbpmDao.resumeTimer(
					timerIds[i],
					terminiIniciat.getDataFi());
	}

	private Expedient getExpedientPerTask(JbpmTask task) {
		JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(task.getProcessInstanceId());
		return expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
	}
	private void crearAlertaPrevia(
			TerminiIniciat terminiIniciat,
			String responsable,
			Expedient expedient) {
		//logger.info("Creació alerta prèvia per al termini " + terminiIniciat.getId() + " per al responsable " + responsable);
		Alerta alerta = new Alerta(
				new Date(),
				responsable,
				Alerta.AlertaPrioritat.NORMAL,
				terminiIniciat.getTermini().getDefinicioProces().getEntorn());
		alerta.setExpedient(expedient);
		alerta.setTerminiIniciat(terminiIniciat);
		alertaDao.saveOrUpdate(alerta);
	}
	private void crearAlertaFinal(
			TerminiIniciat terminiIniciat,
			String responsable,
			Expedient expedient) {
		//logger.info("Creació alerta final per al termini " + terminiIniciat.getId() + " per al responsable " + responsable);
		Alerta alerta = new Alerta(
				new Date(),
				responsable,
				Alerta.AlertaPrioritat.NORMAL,
				terminiIniciat.getTermini().getDefinicioProces().getEntorn());
		alerta.setExpedient(expedient);
		alerta.setTerminiIniciat(terminiIniciat);
		alertaDao.saveOrUpdate(alerta);
	}
	private void esborrarAlertesAntigues(TerminiIniciat terminiIniciat) {
		List<Alerta> antigues = alertaDao.findActivesAmbTerminiIniciatId(terminiIniciat.getId());
		for (Alerta antiga: antigues)
			antiga.setDataEliminacio(new Date());
	}
	
	
	protected String getMessage(String key, Object[] vars) {
		try {
			return messageSource.getMessage(
					key,
					vars,
					null);
		} catch (NoSuchMessageException ex) {
			return "???" + key + "???";
		}
	}

	protected String getMessage(String key) {
		return getMessage(key, null);
	}

	private static final Log logger = LogFactory.getLog(TerminiService.class);

}
