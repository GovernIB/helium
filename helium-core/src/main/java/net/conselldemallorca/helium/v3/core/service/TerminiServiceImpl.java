/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.dao.AlertaDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientDao;
import net.conselldemallorca.helium.core.model.dao.FestiuDao;
import net.conselldemallorca.helium.core.model.dao.RegistreDao;
import net.conselldemallorca.helium.core.model.dao.TerminiDao;
import net.conselldemallorca.helium.core.model.dao.TerminiIniciatDao;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Alerta.AlertaPrioritat;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Festiu;
import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat.TerminiIniciatEstat;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.FestiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
import net.conselldemallorca.helium.v3.core.api.service.TerminiService;
import net.conselldemallorca.helium.v3.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.v3.core.helper.DtoConverter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Servei per gestionar els terminis dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class TerminiServiceImpl implements TerminiService {

	@Resource
	private TerminiDao terminiDao;
	@Resource
	private TerminiIniciatDao terminiIniciatDao;
	@Resource
	private FestiuDao festiuDao;
	@Resource
	private RegistreDao registreDao;
	@Resource(name="dtoConverterV3")
	private DtoConverter dtoConverter;
	@Resource
	private ExpedientDao expedientDao;
	@Resource
	private AlertaDao alertaDao;

	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;

	private MessageSource messageSource;

	@Transactional
	@Override
	public TerminiIniciatDto iniciar(
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

	@Transactional
	@Override
	public TerminiIniciatDto iniciar(
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
			terminiIniciat.setDies(dies);
			terminiIniciat.setMesos(mesos);
			terminiIniciat.setAnys(anys);
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
		
		TerminiIniciat terminiObj = terminiIniciatDao.saveOrUpdate(terminiIniciat);
		return conversioTipusHelper.convertir(terminiObj, TerminiIniciatDto.class);
	}

	@Transactional
	@Override
	public void pausar(Long terminiIniciatId, Date data) {
		TerminiIniciat terminiIniciat = terminiIniciatDao.getById(terminiIniciatId, false);
		if (terminiIniciat.getDataInici() == null)
			throw new IllegalStateException( getMessage("error.terminiService.noIniciat") );
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

	@Transactional
	@Override
	public void continuar(Long terminiIniciatId, Date data) {
		TerminiIniciat terminiIniciat = terminiIniciatDao.getById(terminiIniciatId, false);
		if (terminiIniciat.getDataAturada() == null)
			throw new IllegalStateException( getMessage("error.terminiService.noPausat") );
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

	@Transactional
	@Override
	public void cancelar(Long terminiIniciatId, Date data) {
		TerminiIniciat terminiIniciat = terminiIniciatDao.getById(terminiIniciatId, false);
		if (terminiIniciat.getDataInici() == null)
			throw new IllegalStateException( getMessage("error.terminiService.noIniciat") );
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

	@Transactional
	@Override
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

	@Transactional
	@Override
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

	@Transactional
	@Override
	public List<TerminiIniciatDto> findIniciatsAmbProcessInstanceId(String processInstanceId) {
		
		List<TerminiIniciat> terminisObj = terminiIniciatDao.findAmbProcessInstanceId(processInstanceId);
		List<TerminiIniciatDto> terminiDto = new ArrayList<TerminiIniciatDto>();
		for(TerminiIniciat terminiObj : terminisObj) {
			terminiDto.add(conversioTipusHelper.convertir(terminiObj, TerminiIniciatDto.class));
		}
		return terminiDto;
	}

	@Transactional
	@Override
	public TerminiIniciatDto findIniciatAmbTerminiIdIProcessInstanceId(
			Long terminiId,
			String processInstanceId) {
		
		TerminiIniciat terminiObj = terminiIniciatDao.findAmbTerminiIdIProcessInstanceId(terminiId, processInstanceId);
		return conversioTipusHelper.convertir(terminiObj, TerminiIniciatDto.class);
	}

	@Transactional
	@Override
	public List<TerminiIniciatDto> findIniciatsAmbTaskInstanceIds(String[] taskInstanceIds) {
		if (taskInstanceIds == null || taskInstanceIds.length == 0)
			return new ArrayList<TerminiIniciatDto>();
		
		List<TerminiIniciat> terminisObj = terminiIniciatDao.findAmbTaskInstanceIds(taskInstanceIds);
		List<TerminiIniciatDto> terminiDto = new ArrayList<TerminiIniciatDto>();
		for(TerminiIniciat terminiObj : terminisObj) {
			terminiDto.add(conversioTipusHelper.convertir(terminiObj, TerminiIniciatDto.class));
		}
		return terminiDto;
	}
	
	@Transactional
	@Override
	public void configurarTerminiIniciatAmbDadesJbpm(
			Long terminiIniciatId,
			String taskInstanceId,
			Long timerId) {
		TerminiIniciat terminiIniciat = terminiIniciatDao.getById(terminiIniciatId, false);
		terminiIniciat.setTaskInstanceId(taskInstanceId);
		if (timerId != null)
			terminiIniciat.afegirTimerId(timerId.longValue());
	}

	@Transactional
	@Override
	public FestiuDto getFestiuById(Long id) {		
		Festiu festiu = festiuDao.getById(id, false);
		FestiuDto festiuDto = conversioTipusHelper.convertir(festiu, FestiuDto.class);
		return festiuDto;
	}
	
	@Transactional
	@Override
	public FestiuDto createFestiu(FestiuDto entityDto) {
		festiuDao.modificacioFestius();
		
		Festiu entity = new Festiu();
		entity.setId(entityDto.getId());
		entity.setData(entityDto.getData());
		
		Festiu festiu = festiuDao.saveOrUpdate(entity);	
		FestiuDto festiuDto = conversioTipusHelper.convertir(festiu, FestiuDto.class);
		return festiuDto;
	}
	
	@Transactional
	@Override
	public FestiuDto updateFestiu(FestiuDto entityDto) {
		festiuDao.modificacioFestius();
		
		Festiu entity = new Festiu();
		entity.setId(entityDto.getId());
		entity.setData(entityDto.getData());
		Festiu festiu = festiuDao.merge(entity);
		
		FestiuDto festiuDto = conversioTipusHelper.convertir(festiu, FestiuDto.class);
		return festiuDto;
	}
	
	@Transactional
	@Override
	public void deleteFestiu(Long id) {
		festiuDao.modificacioFestius();
		FestiuDto vell = getFestiuById(id);
		
		if (vell != null) {
			festiuDao.delete(id);
		}
	}
	
	@Transactional
	@Override
	public List<FestiuDto> findFestiuAmbAny(int any) {		
		List<Festiu> festiusObj = festiuDao.findAmbAny(any);
		List<FestiuDto> terminisDto = new ArrayList<FestiuDto>();
		for(Festiu festiuObj : festiusObj) {
			terminisDto.add(conversioTipusHelper.convertir(festiuObj, FestiuDto.class));
		}
		return terminisDto;
	}
	
	@Transactional
	@Override
	public FestiuDto findFestiuAmbData(Date data) {
		Festiu festiu = festiuDao.findAmbData(data);	
		FestiuDto festiuDto = conversioTipusHelper.convertir(festiu, FestiuDto.class);
		return festiuDto;
	}

	@Transactional
	@Override
	public void comprovarTerminisIniciats() {
		logger.debug("Inici de la comprovació de terminis");
		List<TerminiIniciat> iniciatsActius = terminiIniciatDao.findIniciatsActius();
		for (TerminiIniciat terminiIniciat: iniciatsActius) {
			if (terminiIniciat.getTaskInstanceId() != null) {
				if (terminiIniciat.getEstat() == TerminiIniciatEstat.CADUCAT && terminiIniciat.getTermini().isAlertaFinal() && ! terminiIniciat.isAlertaFinal()) {
					esborrarAlertesAntigues(terminiIniciat);
					JbpmTask task = jbpmHelper.getTaskById(terminiIniciat.getTaskInstanceId());
					if (task.getAssignee() != null) {
						crearAlertaAmbTerminiAssociat(terminiIniciat, task.getAssignee(), getExpedientPerTask(task));
					} else {
						for (String actor: task.getPooledActors())
							crearAlertaAmbTerminiAssociat(terminiIniciat, actor, getExpedientPerTask(task));
					}
					terminiIniciat.setAlertaFinal(true);
				} else if (terminiIniciat.getEstat() == TerminiIniciatEstat.AVIS && terminiIniciat.getTermini().isAlertaPrevia() && ! terminiIniciat.isAlertaPrevia()) {
					JbpmTask task = jbpmHelper.getTaskById(terminiIniciat.getTaskInstanceId());
					if (task.getAssignee() != null) {
						crearAlertaAmbTerminiAssociat(terminiIniciat, task.getAssignee(), getExpedientPerTask(task));
					} else {
						for (String actor: task.getPooledActors())
							crearAlertaAmbTerminiAssociat(terminiIniciat, actor, getExpedientPerTask(task));
					}
					terminiIniciat.setAlertaPrevia(true);
				}
			} else {
				if (terminiIniciat.getEstat() == TerminiIniciatEstat.CADUCAT && terminiIniciat.getTermini().isAlertaFinal() && ! terminiIniciat.isAlertaFinal()) {
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
				} else if (terminiIniciat.getEstat() == TerminiIniciatEstat.AVIS && terminiIniciat.getTermini().isAlertaPrevia() && ! terminiIniciat.isAlertaPrevia()) {
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
		}
		logger.debug("Fi de la comprovació de terminis");
	}

	@Transactional
	@Override
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
					getDataFiTermini(
							terminiIniciat.getDataInici(),
							terminiIniciat.getAnys(),
							terminiIniciat.getMesos(),
							terminiIniciat.getDies(),
							terminiIniciat.getTermini().isLaborable()));
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
		JbpmProcessInstance pi = jbpmHelper.getRootProcessInstance(processInstanceId);
		if (pi == null) {
			return null;
		}
		return expedientDao.findAmbProcessInstanceId(pi.getId());
	}

	private void suspendTimers(TerminiIniciat terminiIniciat) {
		long[] timerIds = terminiIniciat.getTimerIdsArray();
		for (int i = 0; i < timerIds.length; i++)
			jbpmHelper.suspendTimer(
					timerIds[i],
					new Date(Long.MAX_VALUE));
	}
	private void resumeTimers(TerminiIniciat terminiIniciat) {
		long[] timerIds = terminiIniciat.getTimerIdsArray();
		for (int i = 0; i < timerIds.length; i++)
			jbpmHelper.resumeTimer(
					timerIds[i],
					terminiIniciat.getDataFi());
	}

	private Expedient getExpedientPerTask(JbpmTask task) {
		JbpmProcessInstance rootProcessInstance = jbpmHelper.getRootProcessInstance(task.getProcessInstanceId());
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
