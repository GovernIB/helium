package net.conselldemallorca.helium.v3.core.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Festiu;
import net.conselldemallorca.helium.core.model.hibernate.Registre;
import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.v3.core.api.dto.FestiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
import net.conselldemallorca.helium.v3.core.api.exception.FestiuNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.TerminiService;
import net.conselldemallorca.helium.v3.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.v3.core.helper.MessageHelper;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.FestiuRepository;
import net.conselldemallorca.helium.v3.core.repository.RegistreRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiIniciatRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servei per gestionar els terminis dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("terminiServiceV3")
public class TerminiServiceImpl implements TerminiService {
	@Resource
	private TerminiRepository terminiRepository;	
	@Resource
	private FestiuRepository festiuRepository;	
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private TerminiIniciatRepository terminiIniciatRepository;
	@Resource
	private RegistreRepository registreRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;

	@Transactional(readOnly=true)
	@Override
	public List<TerminiDto> findTerminisAmbProcessInstanceId(String processInstanceId) {
		JbpmProcessInstance pi = jbpmHelper.getProcessInstance(processInstanceId);
		if (pi.getProcessInstance() == null)
			return null;
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(pi.getProcessDefinitionId());
		return conversioTipusHelper.convertirList(
				terminiRepository.findByDefinicioProcesId(definicioProces.getId()),
				TerminiDto.class);
	}

	@Transactional(readOnly=true)
	@Override
	public List<TerminiIniciatDto> findIniciatsAmbProcessInstanceId(String processInstanceId) {
		List<TerminiIniciat> terminiIniciats = terminiIniciatRepository.findByProcessInstanceId(processInstanceId);
		return conversioTipusHelper.convertirList(terminiIniciats, TerminiIniciatDto.class);
	}

	@Transactional(readOnly=true)
	@Override
	public TerminiIniciatDto findIniciatAmbId(Long id) {
		return conversioTipusHelper.convertir(terminiIniciatRepository.findById(id), TerminiIniciatDto.class);
	}
	
	private Date getDataFiTermini(
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
	public TerminiIniciatDto iniciar(
			Long terminiId,
			Long expedientId,
			Date data,
			boolean esDataFi) {
		Expedient expedient = expedientRepository.findOne(expedientId);
		Termini termini = terminiRepository.findOne(terminiId);
		TerminiIniciat terminiIniciat = terminiIniciatRepository.findById(terminiId);
		if (terminiIniciat == null) {
			return iniciar(
					terminiId,
					expedient,
					data,
					termini.getAnys(),
					termini.getMesos(),
					termini.getDies(),
					esDataFi);
		} else {
			return iniciar(
					terminiId,
					expedient,
					data,
					terminiIniciat.getAnys(),
					terminiIniciat.getMesos(),
					terminiIniciat.getDies(),
					esDataFi);
		}
	}
	
	private Date getDataIniciTermini(
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

	private void sumarDies(Calendar cal, int numDies) {
		int signe = (numDies < 0) ? -1 : 1;
		int nd = (numDies < 0) ? -numDies : numDies;
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		List<Festiu> festius = festiuRepository.findAll();
		int diesLabs = 0;
		while (diesLabs < nd) {
			if (!esFestiu(cal, festius))
				diesLabs ++;
			cal.add(Calendar.DATE, signe);
		}
		cal.add(Calendar.DATE, -signe);
	}
	
	private TerminiIniciatDto iniciar(
			Long terminiId,
			Expedient expedient,
			Date data,
			int anys,
			int mesos,
			int dies,
			boolean esDataFi) {	
		Termini termini = terminiRepository.findOne(terminiId);
		TerminiIniciat terminiIniciat = terminiIniciatRepository.findById(terminiId);
		if (termini == null) {
			termini = terminiIniciat.getTermini();
		}
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
						expedient.getProcessInstanceId(),
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
						expedient.getProcessInstanceId(),
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
		
		crearRegistreTermini(
					getExpedientForProcessInstanceId(expedient.getProcessInstanceId()).getId(),
					expedient.getProcessInstanceId(),
					Registre.Accio.INICIAR,
					SecurityContextHolder.getContext().getAuthentication().getName());
		
		TerminiIniciat terminiObj = terminiIniciatRepository.save(terminiIniciat);
		return conversioTipusHelper.convertir(terminiObj, TerminiIniciatDto.class);
	}
		
	@Transactional
	@Override
	public void pausar(Long terminiIniciatId, Date data) throws IllegalStateException {
		TerminiIniciat terminiIniciat = terminiIniciatRepository.findById(terminiIniciatId);
		if (terminiIniciat.getDataInici() == null)
			throw new IllegalStateException( messageHelper.getMessage("error.terminiService.noIniciat") );
		terminiIniciat.setDataAturada(data);
		suspendTimers(terminiIniciat);
		String processInstanceId = terminiIniciat.getProcessInstanceId();
		Long expedientId = getExpedientForProcessInstanceId(processInstanceId).getId();
		if (expedientId != null) {
			crearRegistreTermini(
					getExpedientForProcessInstanceId(processInstanceId).getId(),
					processInstanceId,
					Registre.Accio.ATURAR,
					SecurityContextHolder.getContext().getAuthentication().getName());
		}
	}
		
	@Transactional
	@Override
	public void continuar(Long terminiIniciatId, Date data) throws IllegalStateException {
		TerminiIniciat terminiIniciat = terminiIniciatRepository.findById(terminiIniciatId);
		if (terminiIniciat.getDataAturada() == null)
			throw new IllegalStateException( messageHelper.getMessage("error.terminiService.noPausat") );
		int diesAturat = terminiIniciat.getNumDiesAturadaActual(data);
		terminiIniciat.setDiesAturat(terminiIniciat.getDiesAturat() + diesAturat);
		terminiIniciat.setDataAturada(null);
		resumeTimers(terminiIniciat);
		String processInstanceId = terminiIniciat.getProcessInstanceId();
		Long expedientId = getExpedientForProcessInstanceId(processInstanceId).getId();
		if (expedientId != null) {
			crearRegistreTermini(
					getExpedientForProcessInstanceId(processInstanceId).getId(),
					processInstanceId,
					Registre.Accio.REPRENDRE,
					SecurityContextHolder.getContext().getAuthentication().getName());
		}
	}
		
	@Transactional
	@Override
	public void cancelar(Long terminiIniciatId, Date data) throws IllegalStateException {
		TerminiIniciat terminiIniciat = terminiIniciatRepository.findById(terminiIniciatId);
		if (terminiIniciat.getDataInici() == null)
			throw new IllegalStateException( messageHelper.getMessage("error.terminiService.noIniciat") );
		terminiIniciat.setDataCancelacio(data);
		suspendTimers(terminiIniciat);
		String processInstanceId = terminiIniciat.getProcessInstanceId();
		Long expedientId = getExpedientForProcessInstanceId(processInstanceId).getId();
		if (expedientId != null) {
			crearRegistreTermini(
					getExpedientForProcessInstanceId(processInstanceId).getId(),
					processInstanceId,
					Registre.Accio.CANCELAR,
					SecurityContextHolder.getContext().getAuthentication().getName());
		}
	}
	
	private TerminiIniciatDto iniciar(
			Long terminiId,
			Long expedientId,
			Date data,
			int anys,
			int mesos,
			int dies,
			boolean esDataFi) {
		Expedient expedient = expedientRepository.findOne(expedientId);
		return iniciar(
				terminiId,
				expedient,
				data,
				anys,
				mesos,
				dies,
				esDataFi);
	}

	@Transactional
	@Override
	public void modificar(Long terminiId, Long expedientId, Date inicio, int anys, int mesos, int dies, boolean equals) {
		cancelar(terminiId, new Date());
		iniciar(
				terminiId,
				expedientId,
				inicio,
				anys,
				mesos,
				dies,
				equals);		
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<FestiuDto> findFestiuAmbAny(int any) {
		return conversioTipusHelper.convertirList(
				festiuRepository.findByAny(any),
				FestiuDto.class);
	}
	
	@Transactional
	@Override
	public void createFestiu(String data) throws Exception {
		Festiu festiu = new Festiu();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		festiu.setData(sdf.parse(data));
		festiuRepository.save(festiu);
	}
	
	@Transactional
	@Override
	public void deleteFestiu(String data) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Festiu festiu = festiuRepository.findByData(sdf.parse(data));
		if (festiu != null) {
			festiuRepository.delete(festiu);
		} else {
			throw new FestiuNotFoundException();
		}
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
		return expedientRepository.findByProcessInstanceId(pi.getId()).get(0);
	}

	private void suspendTimers(TerminiIniciat terminiIniciat) {
		for (long timerId : terminiIniciat.getTimerIdsArray())
			jbpmHelper.suspendTimer(timerId, new Date(Long.MAX_VALUE));

	}
	private void resumeTimers(TerminiIniciat terminiIniciat) {
		for (long timerId : terminiIniciat.getTimerIdsArray())
			jbpmHelper.resumeTimer(timerId, terminiIniciat.getDataFi());
	}

	private Registre crearRegistreTermini(
			Long expedientId,
			String processInstanceId,
			Registre.Accio accio,
			String responsableCodi) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi.toString(),
				accio,
				Registre.Entitat.TERMINI,
				expedientId.toString());
		registre.setProcessInstanceId(processInstanceId);
		return registreRepository.save(registre);
	}
}
