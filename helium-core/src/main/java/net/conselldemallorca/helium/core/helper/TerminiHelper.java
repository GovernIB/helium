/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Festiu;
import net.conselldemallorca.helium.core.model.hibernate.Registre;
import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.repository.FestiuRepository;
import net.conselldemallorca.helium.v3.core.repository.RegistreRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiIniciatRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiRepository;

/**
 * Helper per a enviament de correus
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component("TerminiHelperV3")
public class TerminiHelper {

	@Resource
	private TerminiRepository terminiRepository;
	@Resource
	private TerminiIniciatRepository terminiIniciatRepository;
	@Resource
	private RegistreRepository registreRepository;
	@Resource
	private FestiuRepository festiuRepository;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private MessageHelper messageHelper;

	public TerminiIniciatDto iniciar(
			Long terminiId,
			String processInstanceId,
			Date data,
			boolean esDataFi,
			boolean crearRegistre) {
		Termini termini = terminiRepository.findOne(terminiId);
		if (termini == null)
			throw new NoTrobatException(Termini.class, terminiId);
		TerminiIniciat terminiIniciat = terminiIniciatRepository.findByTerminiAndProcessInstanceId(
				termini,
				processInstanceId);
		if (terminiIniciat == null) {
			return iniciar(
					terminiId,
					processInstanceId,
					data,
					termini.getAnys(),
					termini.getMesos(),
					termini.getDies(),
					esDataFi,
					crearRegistre);
		} else {
			return iniciar(
					terminiId,
					processInstanceId,
					data,
					terminiIniciat.getAnys(),
					terminiIniciat.getMesos(),
					terminiIniciat.getDies(),
					esDataFi,
					crearRegistre);
		}
	}
	public TerminiIniciatDto iniciar(
			Long terminiId,
			String processInstanceId,
			Date data,
			int anys,
			int mesos,
			int dies,
			boolean esDataFi,
			boolean crearRegistre) {
		
		Termini termini = terminiRepository.findOne(terminiId);
		if (termini == null)
			throw new NoTrobatException(Termini.class, terminiId);
		TerminiIniciat terminiIniciat = terminiIniciatRepository.findByTerminiAndProcessInstanceId(
				termini,
				processInstanceId);
		if (terminiIniciat == null) {
			if (esDataFi) {
				Date dataInici = getDataIniciTermini(
						data,
						anys,
						mesos,
						dies,
						termini.isLaborable(),
						processInstanceId);
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
						termini.isLaborable(),
						processInstanceId);
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
						termini.isLaborable(),
						processInstanceId);
				terminiIniciat.setDataInici(dataInici);
				terminiIniciat.setDataFi(data);
			} else {
				Date dataFi = getDataFiTermini(
						data,
						anys,
						mesos,
						dies,
						termini.isLaborable(),
						processInstanceId);
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
		
		if (crearRegistre) {
			Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
			if (expedient == null)
				throw new NoTrobatException(Expedient.class);
			
			crearRegistreTermini(
						expedient.getId(),
						expedient.getProcessInstanceId(),
						Registre.Accio.INICIAR,
						SecurityContextHolder.getContext().getAuthentication().getName());
		}
		
		TerminiIniciat terminiObj = terminiIniciatRepository.save(terminiIniciat);
		return conversioTipusHelper.convertir(terminiObj, TerminiIniciatDto.class);
	}

	public void pausar(Long terminiIniciatId, Date data, boolean crearRegistre) {
		TerminiIniciat terminiIniciat = terminiIniciatRepository.findOne(terminiIniciatId);
		if (terminiIniciat == null)
			throw new NoTrobatException(TerminiIniciat.class, terminiIniciatId);
		if (terminiIniciat.getDataInici() == null)
			throw new IllegalStateException(messageHelper.getMessage("error.terminiService.noIniciat"));
		terminiIniciat.setDataAturada(data);
		suspendTimers(terminiIniciat);
		
		if (crearRegistre) {
			Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(terminiIniciat.getProcessInstanceId());
			if (expedient == null)
				throw new NoTrobatException(Expedient.class);
			
			crearRegistreTermini(
						expedient.getId(),
						expedient.getProcessInstanceId(),
						Registre.Accio.ATURAR,
						SecurityContextHolder.getContext().getAuthentication().getName());
		}
	}

	public void continuar(Long terminiIniciatId, Date data, boolean crearRegistre) {
		TerminiIniciat terminiIniciat = terminiIniciatRepository.findOne(terminiIniciatId);
		if (terminiIniciat == null)
			throw new NoTrobatException(TerminiIniciat.class, terminiIniciatId);
		if (terminiIniciat.getDataAturada() == null)
			throw new IllegalStateException(
					messageHelper.getMessage("error.terminiService.noPausat"));
		int diesAturat = terminiIniciat.getNumDiesAturadaActual(data);
		terminiIniciat.setDiesAturat(terminiIniciat.getDiesAturat() + diesAturat);
		terminiIniciat.setDataAturada(null);
		resumeTimers(terminiIniciat);
		
		if (crearRegistre) {
			Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(terminiIniciat.getProcessInstanceId());
			if (expedient == null)
				throw new NoTrobatException(Expedient.class);
			
			crearRegistreTermini(
						expedient.getId(),
						expedient.getProcessInstanceId(),
						Registre.Accio.REPRENDRE,
						SecurityContextHolder.getContext().getAuthentication().getName());
		}
	}

	public void cancelar(Long terminiIniciatId, Date data, boolean crearRegistre) {
		TerminiIniciat terminiIniciat = terminiIniciatRepository.findOne(terminiIniciatId);
		if (terminiIniciat == null)
			throw new NoTrobatException(TerminiIniciat.class, terminiIniciatId);
		if (terminiIniciat.getDataInici() == null)
			throw new IllegalStateException(
					messageHelper.getMessage("error.terminiService.noIniciat"));
		terminiIniciat.setDataCancelacio(data);
		suspendTimers(terminiIniciat);
		
		if (crearRegistre) {
			Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(terminiIniciat.getProcessInstanceId());
			if (expedient == null)
				throw new NoTrobatException(Expedient.class);
			
			crearRegistreTermini(
						expedient.getId(),
						expedient.getProcessInstanceId(),
						Registre.Accio.CANCELAR,
						SecurityContextHolder.getContext().getAuthentication().getName());
		}
	}

	public Date getDataFiTermini(
			Date inici,
			int anys,
			int mesos,
			int dies,
			boolean laborable,
			String processInstanceId) {
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
			Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
			if (expedient == null)
				throw new NoTrobatException(Expedient.class);
			
			// Depenent de si el termini és laborable o no s'afegiran més o manco dies
			if (laborable) {
				sumarDies(dataFi, dies, expedient.getTipus());
			} else {
				dataFi.add(Calendar.DATE, dies - 1);
				// Si el darrer dia cau en festiu es passa al dia laborable següent
				sumarDies(dataFi, 1,  expedient.getTipus());
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
			boolean laborable,
			String processInstanceId) {
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
			Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
			if (expedient == null)
				throw new NoTrobatException(Expedient.class);
			
			// Depenent de si el termini és laborable o no s'afegiran més o manco dies
			if (laborable) {
				sumarDies(dataInici, -dies, expedient.getTipus());
			} else {
				dataInici.add(Calendar.DATE, -dies + 1);
				// Si el darrer dia cau en festiu es passa al dia laborable següent
				sumarDies(dataInici, -1, expedient.getTipus());
			}
			// El termini en realitat s'inicia a les 00:00h
			dataInici.set(Calendar.HOUR_OF_DAY, 0);
			dataInici.set(Calendar.MINUTE, 0);
			dataInici.set(Calendar.SECOND, 0);
			dataInici.set(Calendar.MILLISECOND, 0);
		}
		return dataInici.getTime();
	}

	public Termini findTerminiById(Long terminiId) {
		return terminiRepository.findOne(terminiId);
	}

	public TerminiIniciat findTerminiIniciatById(Long terminiIniciatId) {
		return terminiIniciatRepository.findOne(terminiIniciatId);
	}

	public Termini findAmbDefinicioProcesICodi(
			DefinicioProces definicioProces,
			String terminiCodi) {
		return terminiRepository.findByDefinicioProcesAndCodi(
				definicioProces,
				terminiCodi);
	}

	public Termini findAmbExpedientTipusICodi(
			ExpedientTipus expedientTipus,
			String terminiCodi) {
		return terminiRepository.findByExpedientTipusAndCodi(
				expedientTipus,
				terminiCodi);
	}
	
	public TerminiIniciat findIniciatAmbDefinicioProcesICodi(
			DefinicioProces definicioProces,
			String processInstanceId,
			String terminiCodi) {
		Termini termini = terminiRepository.findByDefinicioProcesAndCodi(
				definicioProces,
				terminiCodi);
		if (termini == null)
			return null;
		return terminiIniciatRepository.findByTerminiAndProcessInstanceId(
				termini,
				processInstanceId);
	}

	public TerminiIniciat findIniciatAmbExpedientTipusICodi(
			ExpedientTipus tipus, 
			String processInstanceId,
			String terminiCodi) {
			Termini termini = terminiRepository.findByExpedientTipusAndCodi(
					tipus, 
					terminiCodi);
			if (termini == null)
				return null;
			return terminiIniciatRepository.findByTerminiAndProcessInstanceId(
					termini,
					processInstanceId);
		}

	/** Retorna la llista de terminis del tipus d'expedient incloent els terminis heretats en el
	 * cas de tenir herència. */
	public List<Termini> findByExpedientTipusAmbHerencia(ExpedientTipus tipus) {
		
		List<Termini> terminis;
		if (tipus.getExpedientTipusPare() != null )
			terminis = terminiRepository.findByExpedientTipusAmbHerencia(
						tipus.getId());
		else
			terminis = terminiRepository.findByExpedientTipus(tipus.getId());
		return terminis;
	}

	private void sumarDies(
			Calendar cal, 
			int numDies,
			ExpedientTipus expedientTipus) {
		int signe = (numDies < 0) ? -1 : 1;
		int nd = (numDies < 0) ? -numDies : numDies;
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		List<Festiu> festius = festiuRepository.findAll();
		int diesLabs = 0;
		while (diesLabs < nd) {
			if (!esFestiu(cal, festius, expedientTipus))
				diesLabs ++;
			cal.add(Calendar.DATE, signe);
		}
		cal.add(Calendar.DATE, -signe);
	}
	private boolean esFestiu(
			Calendar cal,
			List<Festiu> festius,
			ExpedientTipus expedientTipus) {
		int diasem = cal.get(Calendar.DAY_OF_WEEK);
		for (int nolab: getDiesNoLaborables(expedientTipus)) {
			if (diasem == nolab)
				return true;
		}
		for (Festiu festiu: festius) {
			if (cal.getTime().compareTo(festiu.getData()) == 0)
				return true;
		}
		return false;
	}
	private int[] getDiesNoLaborables(ExpedientTipus expedientTipus) {
		String nolabs = null;
		if (expedientTipus.getDiesNoLaborables() != null && !expedientTipus.getDiesNoLaborables().isEmpty())
			nolabs = expedientTipus.getDiesNoLaborables();
		else
			nolabs = GlobalProperties.getInstance().getProperty("app.calendari.nolabs");
			
		if (nolabs != null && !nolabs.isEmpty()) {
			String[] dies = nolabs.split(",");
			int[] resposta = new int[dies.length];
			for (int i = 0; i < dies.length; i++) {
				resposta[i] = (Integer.parseInt(dies[i]) % 7) + 1;
			}
			return resposta;
		}
		return new int[0];
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
