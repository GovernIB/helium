/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import net.conselldemallorca.helium.core.helper.TerminiHelper;
import net.conselldemallorca.helium.core.model.dao.AlertaDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientDao;
import net.conselldemallorca.helium.core.model.dao.FestiuDao;
import net.conselldemallorca.helium.core.model.dao.RegistreDao;
import net.conselldemallorca.helium.core.model.dao.TerminiIniciatDao;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Alerta.AlertaPrioritat;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Festiu;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat.TerminiIniciatEstat;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;


/**
 * Servei per gestionar els terminis dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class TerminiService {

	private TerminiIniciatDao terminiIniciatDao;
	private FestiuDao festiuDao;
	private RegistreDao registreDao;
	private ExpedientDao expedientDao;
	private AlertaDao alertaDao;
	private JbpmHelper jbpmDao;
	private MessageSource messageSource;
	private TerminiHelper terminiHelper;


	public TerminiIniciat iniciar(
			Long terminiId,
			String processInstanceId,
			Date data,
			boolean esDataFi) {
		TerminiIniciatDto terminiIniciat = terminiHelper.iniciar(terminiId, processInstanceId, data, esDataFi, true);
		return terminiIniciatDao.findAmbTerminiIdIProcessInstanceId(terminiIniciat.getId(), processInstanceId);
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

	@Scheduled(cron="0 */10 * * * *")
	public void comprovarTerminisIniciats() {
		
		logger.debug("Inici de la comprovació de terminis");
		List<TerminiIniciat> iniciatsActiusAlertesPrevies = terminiIniciatDao.findIniciatsAmbAlertesPrevies();
		List<TerminiIniciat> iniciatsActiusAlertesFinals = terminiIniciatDao.findIniciatsAmbAlertesFinals();
		for (TerminiIniciat terminiIniciat: iniciatsActiusAlertesFinals) {
			if (terminiIniciat.getTaskInstanceId() != null && terminiIniciat.getEstat() == TerminiIniciatEstat.CADUCAT) {
				esborrarAlertesAntigues(terminiIniciat);
				JbpmTask task = jbpmDao.getTaskById(terminiIniciat.getTaskInstanceId());
				if (task.getAssignee() != null) {
					crearAlertaAmbTerminiAssociat(terminiIniciat, task.getAssignee(), getExpedientPerTask(task));
				} else {
					for (String actor: task.getPooledActors())
						crearAlertaAmbTerminiAssociat(terminiIniciat, actor, getExpedientPerTask(task));
				}
				terminiIniciat.setAlertaFinal(true);
			} else if(terminiIniciat.getEstat() == TerminiIniciatEstat.CADUCAT) {
				esborrarAlertesAntigues(terminiIniciat);
				Expedient expedient = getExpedientForProcessInstanceId(
						terminiIniciat.getProcessInstanceId());
				if (expedient != null && expedient.getResponsableCodi() != null) {
					crearAlertaAmbTerminiAssociat(
							terminiIniciat,
							expedient.getResponsableCodi(),
							expedient);
				}
				terminiIniciat.setAlertaFinal(true);
			}
		}
		
		for (TerminiIniciat terminiIniciat: iniciatsActiusAlertesPrevies) {
			if (terminiIniciat.getTaskInstanceId() != null && terminiIniciat.getEstat() == TerminiIniciatEstat.AVIS) {
				JbpmTask task = jbpmDao.getTaskById(terminiIniciat.getTaskInstanceId());
				if (task.getAssignee() != null) {
					crearAlertaAmbTerminiAssociat(terminiIniciat, task.getAssignee(), getExpedientPerTask(task));
				} else {
					for (String actor: task.getPooledActors())
						crearAlertaAmbTerminiAssociat(terminiIniciat, actor, getExpedientPerTask(task));
				}
				terminiIniciat.setAlertaPrevia(true);
			} else if (terminiIniciat.getEstat() == TerminiIniciatEstat.AVIS) {
				Expedient expedient = getExpedientForProcessInstanceId(
						terminiIniciat.getProcessInstanceId());
				if (expedient != null && expedient.getResponsableCodi() != null) {
					crearAlertaAmbTerminiAssociat(
							terminiIniciat,
							expedient.getResponsableCodi(),
							expedient);
				}
				terminiIniciat.setAlertaPrevia(true);
			}
		}
		logger.debug("Fi de la comprovació de terminis");
		
	}

	public void modificarTerminiIniciat(
			Long terminiIniciatId,
			Date dataInici,
			int anys,
			int mesos,
			int dies) {
		TerminiIniciat terminiIniciat = terminiIniciatDao.getById(terminiIniciatId, false);
		if (terminiIniciat != null) {
			boolean modificat = false;
			if (terminiIniciat.getDataInici().getTime() != dataInici.getTime()) {
				terminiIniciat.setDataInici(dataInici);
				modificat = true;
			}
			if (terminiIniciat.getAnys() != anys || terminiIniciat.getMesos() != mesos || terminiIniciat.getDies() != dies) {
				terminiIniciat.setAnys(anys);
				terminiIniciat.setMesos(mesos);
				terminiIniciat.setDies(dies);
				modificat = true;
			}
			if (modificat) {
				terminiIniciat.setDataFi(
					terminiHelper.getDataFiTermini(
							terminiIniciat.getDataInici(),
							terminiIniciat.getAnys(),
							terminiIniciat.getMesos(),
							terminiIniciat.getDies(),
							terminiIniciat.getTermini().isLaborable(),
							terminiIniciat.getProcessInstanceId()));
				String processInstanceId = terminiIniciat.getProcessInstanceId();
				Long expedientId = getExpedientForProcessInstanceId(processInstanceId).getId();
				if (expedientId != null) {
					registreDao.crearRegistreAturarTermini(
							getExpedientForProcessInstanceId(processInstanceId).getId(),
							processInstanceId,
							terminiIniciat.getTermini().getId().toString(),
							SecurityContextHolder.getContext().getAuthentication().getName());
					registreDao.crearRegistreIniciarTermini(
							getExpedientForProcessInstanceId(processInstanceId).getId(),
							processInstanceId,
							terminiIniciat.getTermini().getId().toString(),
							SecurityContextHolder.getContext().getAuthentication().getName());
				}
			}
		}
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
	public void setJbpmHelper(JbpmHelper jbpmDao) {
		this.jbpmDao = jbpmDao;
	}
	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	@Autowired
	public void setTerminiHelper(TerminiHelper terminiHelper) {
		this.terminiHelper = terminiHelper;
	}
	
	
	public TerminiIniciat iniciar(
			Long terminiId,
			String processInstanceId,
			Date data,
			int anys,
			int mesos,
			int dies,
			boolean esDataFi) {
		TerminiIniciatDto terminiIniciat = terminiHelper.iniciar(
				terminiId, 
				processInstanceId, 
				data, 
				anys, 
				mesos, 
				dies, 
				esDataFi, 
				true);
		return terminiIniciatDao.findAmbTerminiIdIProcessInstanceId(terminiIniciat.getId(), processInstanceId);
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
	private void crearAlertaAmbTerminiAssociat(
			TerminiIniciat terminiIniciat,
			String responsable,
			Expedient expedient) {
		logger.debug("Creació alerta per al termini " + terminiIniciat.getId() + " per al responsable " + responsable);
		// Només crea alertes si l'expedient no està finalitzat
		if (expedient.getDataFi() == null) {
			AlertaPrioritat prioritat;
			if (TerminiIniciatEstat.AVIS.equals(terminiIniciat.getEstat())) {
				prioritat = AlertaPrioritat.NORMAL;
			} else if (TerminiIniciatEstat.COMPLETAT_FORA.equals(terminiIniciat.getEstat())) {
				prioritat = AlertaPrioritat.ALTA;
			} else if (TerminiIniciatEstat.CADUCAT.equals(terminiIniciat.getEstat())) {
				prioritat = AlertaPrioritat.MOLT_ALTA;
			} else {
				prioritat = AlertaPrioritat.BAIXA;
			}
			Alerta alerta = new Alerta(
					new Date(),
					responsable,
					prioritat,
					terminiIniciat.getTermini().getDefinicioProces().getEntorn());
			alerta.setExpedient(expedient);
			alerta.setTerminiIniciat(terminiIniciat);
			alertaDao.saveOrUpdate(alerta);
		}
	}
	private void esborrarAlertesAntigues(TerminiIniciat terminiIniciat) {
		List<Alerta> antigues = alertaDao.findActivesAmbTerminiIniciatId(terminiIniciat.getId());
		for (Alerta antiga: antigues) {
			antiga.setDataEliminacio(new Date());
		}
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
