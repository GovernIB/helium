package es.caib.helium.logic.service;

import es.caib.helium.logic.helper.ConversioTipusServiceHelper;
import es.caib.helium.logic.helper.ExpedientHelper;
import es.caib.helium.logic.helper.MessageServiceHelper;
import es.caib.helium.logic.helper.TerminiHelper;
import es.caib.helium.logic.intf.WorkflowEngineApi;
import es.caib.helium.logic.intf.dto.FestiuDto;
import es.caib.helium.logic.intf.dto.TerminiDto;
import es.caib.helium.logic.intf.dto.TerminiIniciatDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.ValidacioException;
import es.caib.helium.logic.intf.service.ExpedientTerminiService;
import es.caib.helium.logic.intf.util.GlobalProperties;
import es.caib.helium.logic.security.ExtendedPermission;
import es.caib.helium.persist.entity.DefinicioProces;
import es.caib.helium.persist.entity.Expedient;
import es.caib.helium.persist.entity.ExpedientTipus;
import es.caib.helium.persist.entity.Festiu;
import es.caib.helium.persist.entity.Registre;
import es.caib.helium.persist.entity.Termini;
import es.caib.helium.persist.entity.TerminiIniciat;
import es.caib.helium.persist.repository.FestiuRepository;
import es.caib.helium.persist.repository.RegistreRepository;
import es.caib.helium.persist.repository.TerminiIniciatRepository;
import es.caib.helium.persist.repository.TerminiRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Servei per gestionar els terminis dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExpedientTerminiServiceImpl implements ExpedientTerminiService {

	@Resource
	private TerminiRepository terminiRepository;	
	@Resource
	private FestiuRepository festiuRepository;	
	@Resource
	private MessageServiceHelper messageServiceHelper;
	@Resource
	private TerminiIniciatRepository terminiIniciatRepository;
	@Resource
	private RegistreRepository registreRepository;
//	@Resource
//	private ExpedientRepository expedientRepository;
	@Resource
	private WorkflowEngineApi workflowEngineApi;
	@Resource
	private ConversioTipusServiceHelper conversioTipusServiceHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private TerminiHelper terminiHelper;
	@Resource
	private GlobalProperties globalProperties;



	/**
	 * {@inheritDoc}
	 */
	@Transactional
	@Override
	public TerminiIniciatDto iniciar(
			Long expedientId,
			String processInstanceId,
			Long terminiId,
			Date data,
			boolean esDataFi) {
		logger.debug("Iniciant termini (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"terminiId=" + terminiId + ", " +
				"data=" + data + ", " +
				"esDataFi=" + esDataFi + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.TERM_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		Termini termini = terminiRepository.findById(terminiId)
				.orElseThrow(() -> new NoTrobatException(Termini.class, terminiId));
		TerminiIniciat terminiIniciat = terminiIniciatRepository.getById(terminiId);
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void modificar(
			Long expedientId,
			String processInstanceId,
			Long terminiIniciatId,
			Date data,
			int anys,
			int mesos,
			int dies,
			boolean esDataFi) {
		logger.debug("Modificant termini iniciat (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"terminiIniciatId=" + terminiIniciatId + ", " +
				"data=" + data + ", " +
				"anys=" + anys + ", " +
				"mesos=" + mesos + ", " +
				"dies=" + dies + ", " +
				"esDataFi=" + esDataFi + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.TERM_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		cancelar(
				expedientId,
				processInstanceId,
				terminiIniciatId,
				new Date());
		iniciar(
				terminiIniciatId,
				expedient,
				data,
				anys,
				mesos,
				dies,
				esDataFi);		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void suspendre(
			Long expedientId,
			String processInstanceId,
			Long terminiIniciatId,
			Date data) {
		logger.debug("Suspenent termini iniciat (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"terminiIniciatId=" + terminiIniciatId + ", " +
				"data=" + data + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.TERM_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		TerminiIniciat terminiIniciat = comprovarTerminiIniciat(
				processInstanceId,
				terminiIniciatId,
				true,
				false);
		terminiIniciat.setDataAturada(data);
		suspendTimers(terminiIniciat);
		crearRegistreTermini(
				expedient.getId(),
				processInstanceId,
				Registre.Accio.ATURAR,
				SecurityContextHolder.getContext().getAuthentication().getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void reprendre(
			Long expedientId,
			String processInstanceId,
			Long terminiIniciatId,
			Date data) {
		logger.debug("Reprenent termini suspès (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"terminiIniciatId=" + terminiIniciatId + ", " +
				"data=" + data + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.TERM_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		TerminiIniciat terminiIniciat = comprovarTerminiIniciat(
				processInstanceId,
				terminiIniciatId,
				false,
				true);
		int diesAturat = terminiIniciat.getNumDiesAturadaActual(data);
		terminiIniciat.setDiesAturat(terminiIniciat.getDiesAturat() + diesAturat);
		terminiIniciat.setDataAturada(null);
		resumeTimers(terminiIniciat);
		crearRegistreTermini(
				expedient.getId(),
				processInstanceId,
				Registre.Accio.REPRENDRE,
				SecurityContextHolder.getContext().getAuthentication().getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void cancelar(
			Long expedientId,
			String processInstanceId,
			Long terminiIniciatId,
			Date data) throws IllegalStateException {
		logger.debug("Cancelant termini iniciat (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"terminiIniciatId=" + terminiIniciatId + ", " +
				"data=" + data + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.TERM_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		TerminiIniciat terminiIniciat = comprovarTerminiIniciat(
				processInstanceId,
				terminiIniciatId,
				true,
				false);
		terminiIniciat.setDataCancelacio(data);
		suspendTimers(terminiIniciat);
		crearRegistreTermini(
				expedient.getId(),
				processInstanceId,
				Registre.Accio.CANCELAR,
				SecurityContextHolder.getContext().getAuthentication().getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<TerminiDto> findAmbProcessInstanceId(
			Long expedientId,
			String processInstanceId) {
		logger.debug("Consultant terminis per a la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.TERM_MANAGE,
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION});
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		List<Termini> terminis = null;
		if (expedient.getTipus().isAmbInfoPropia()) {
			terminis = terminiHelper.findByExpedientTipusAmbHerencia(expedient.getTipus());			
		} else {
			DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
					processInstanceId);
			terminis = terminiRepository.findByDefinicioProcesId(definicioProces.getId());
		}
		return conversioTipusServiceHelper.convertirList(
				terminis,
				TerminiDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<TerminiIniciatDto> iniciatFindAmbProcessInstanceId(
			Long expedientId,
			String processInstanceId) {
		logger.debug("Consultant terminis iniciats per a la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.TERM_MANAGE,
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION});
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		List<TerminiIniciat> terminiIniciats = terminiIniciatRepository.findByProcessInstanceId(processInstanceId);
		return conversioTipusServiceHelper.convertirList(terminiIniciats, TerminiIniciatDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public TerminiIniciatDto iniciatFindAmbId(
			Long expedientId,
			String processInstanceId,
			Long terminiIniciatId) {
		logger.debug("Consultant terminis iniciats per a la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"terminiIniciatId=" + terminiIniciatId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.TERM_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		TerminiIniciat terminiIniciat = terminiIniciatRepository.findById(terminiIniciatId)
				.orElseThrow(() -> new NoTrobatException(
					TerminiIniciat.class,
					terminiIniciatId));
		return conversioTipusServiceHelper.convertir(
				terminiIniciat,
				TerminiIniciatDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<FestiuDto> festiuFindAmbAny(
			int any) {
		logger.debug("Consultant festius de l'any (" +
				"any=" + any + ")");
		return conversioTipusServiceHelper.convertirList(
				festiuRepository.findByAny(any),
				FestiuDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void festiuCreate(
			String data) throws Exception {
		logger.debug("Creant festiu (" +
				"data=" + data + ")");
		Festiu festiu = new Festiu();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		festiu.setData(sdf.parse(data));
		festiuRepository.save(festiu);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void festiuDelete(
			String data) throws ValidacioException, Exception {
		logger.debug("Esborrant festiu (" +
				"data=" + data + ")");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Festiu festiu = festiuRepository.findByData(sdf.parse(data));
		if (festiu != null) {
			festiuRepository.delete(festiu);
		} else {
			throw new ValidacioException("No s'ha trobat el dia festiu");
		}
	}



	private Date getDataFiTermini(
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
			// Depenent de si el termini és laborable o no s'afegiran més o manco dies
			if (laborable) {
				sumarDies(dataFi, dies, expedient.getTipus());
			} else {
				dataFi.add(Calendar.DATE, dies - 1);
				// Si el darrer dia cau en festiu es passa al dia laborable següent
				sumarDies(dataFi, 1, expedient.getTipus());
			}
			// El termini en realitat acaba a les 23:59 del darrer dia
			dataFi.set(Calendar.HOUR_OF_DAY, 23);
			dataFi.set(Calendar.MINUTE, 59);
			dataFi.set(Calendar.SECOND, 59);
			dataFi.set(Calendar.MILLISECOND, 999);
		}
		return dataFi.getTime();
	}

	
	
	private Date getDataIniciTermini(
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

	private void sumarDies(Calendar cal, int numDies, ExpedientTipus expedientTipus) {
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
	
	private TerminiIniciatDto iniciar(
			Long terminiId,
			Expedient expedient,
			Date data,
			int anys,
			int mesos,
			int dies,
			boolean esDataFi) {	
		Termini termini = terminiRepository.getById(terminiId);
		TerminiIniciat terminiIniciat = terminiIniciatRepository.getById(terminiId);
		
		if (termini == null)
			termini = terminiIniciat.getTermini();
		
		if (terminiIniciat == null) {
			if (esDataFi) {
				Date dataInici = getDataIniciTermini(
						data,
						anys,
						mesos,
						dies,
						termini.isLaborable(),
						expedient.getProcessInstanceId());
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
						termini.isLaborable(),
						expedient.getProcessInstanceId());
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
						termini.isLaborable(),
						expedient.getProcessInstanceId());
				terminiIniciat.setDataInici(dataInici);
				terminiIniciat.setDataFi(data);
			} else {
				Date dataFi = getDataFiTermini(
						data,
						anys,
						mesos,
						dies,
						termini.isLaborable(),
						expedient.getProcessInstanceId());
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
					expedient.getId(),
					expedient.getProcessInstanceId(),
					Registre.Accio.INICIAR,
					SecurityContextHolder.getContext().getAuthentication().getName());
		
		TerminiIniciat terminiObj = terminiIniciatRepository.save(terminiIniciat);
		return conversioTipusServiceHelper.convertir(terminiObj, TerminiIniciatDto.class);
	}

	private int[] getDiesNoLaborables(ExpedientTipus expedientTipus) {
		String nolabs = null;
		if (expedientTipus.getDiesNoLaborables() != null && !expedientTipus.getDiesNoLaborables().isEmpty())
			nolabs = expedientTipus.getDiesNoLaborables();
		else
			nolabs = globalProperties.getProperty("es.caib.helium.calendari.nolabs");
			
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
		for (String timerId : terminiIniciat.getTimerIdsArray())
			workflowEngineApi.suspendTimer(timerId, new Date(Long.MAX_VALUE));

	}
	private void resumeTimers(TerminiIniciat terminiIniciat) {
		for (String timerId : terminiIniciat.getTimerIdsArray())
			workflowEngineApi.resumeTimer(timerId, terminiIniciat.getDataFi());
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

	private TerminiIniciat comprovarTerminiIniciat(
			String processInstanceId,
			Long terminiIniciatId,
			boolean comprovarDataInici,
			boolean comprovarDataAturada) {
		TerminiIniciat terminiIniciat = terminiIniciatRepository.findById(terminiIniciatId)
				.orElseThrow(() -> new NoTrobatException(
					TerminiIniciat.class,
					terminiIniciatId));
		if (comprovarDataInici && terminiIniciat.getDataInici() == null) {
			throw new ValidacioException(
					messageServiceHelper.getMessage("error.terminiService.noIniciat"));
		}
		if (comprovarDataAturada && terminiIniciat.getDataAturada() == null) {
			throw new ValidacioException(
					messageServiceHelper.getMessage("error.terminiService.noPausat"));
		}
		if (!terminiIniciat.getProcessInstanceId().equals(processInstanceId)) {
			throw new ValidacioException(
					"El termini iniciat no pertany a la instància de procés (" +
					"terminiIniciatId=" + terminiIniciatId + ", " +
					"processInstanceId(terminiIniciat)=" + terminiIniciat.getProcessInstanceId() + ", " +
					"processInstanceId(parametre)=" + processInstanceId + ")");
		}
		return terminiIniciat;
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientTerminiServiceImpl.class);

}