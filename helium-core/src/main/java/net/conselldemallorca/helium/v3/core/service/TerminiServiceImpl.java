package net.conselldemallorca.helium.v3.core.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.MessageHelper;
import net.conselldemallorca.helium.core.helper.TerminiHelper;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Festiu;
import net.conselldemallorca.helium.core.model.hibernate.Registre;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.v3.core.api.dto.FestiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.v3.core.api.service.TerminiService;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.FestiuRepository;
import net.conselldemallorca.helium.v3.core.repository.RegistreRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiIniciatRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiRepository;

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
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private TerminiHelper terminiHelper;

	@Transactional(readOnly=true)
	@Override
	public List<TerminiDto> findTerminisAmbProcessInstanceId(String processInstanceId) {
		JbpmProcessInstance pi = jbpmHelper.getProcessInstance(processInstanceId);
		if (pi == null)
			throw new NoTrobatException(JbpmProcessInstance.class, processInstanceId);
		
		if (pi.getProcessInstance() == null)
			return null;
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(pi.getProcessDefinitionId());
		if (definicioProces == null)
			throw new NoTrobatException(DefinicioProces.class, pi.getProcessDefinitionId());
		
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
		TerminiIniciat termini = terminiIniciatRepository.findById(id);
		if (termini == null)
			throw new NoTrobatException(TerminiIniciat.class, id);
		return conversioTipusHelper.convertir(termini, TerminiIniciatDto.class);
	}
	

	@Transactional
	@Override
	public TerminiIniciatDto iniciar(
			Long terminiId,
			Long expedientId,
			Date data,
			boolean esDataFi) {
		Expedient expedient = expedientRepository.findOne(expedientId);
		if (expedient == null)
			throw new NoTrobatException(Expedient.class, expedientId);
		
		return terminiHelper.iniciar(terminiId, expedient.getProcessInstanceId(), data, esDataFi, true);
	}
	
	@Transactional
	@Override
	public void pausar(Long terminiIniciatId, Date data) {
		TerminiIniciat terminiIniciat = terminiIniciatRepository.findById(terminiIniciatId);
		if (terminiIniciat == null)
			throw new NoTrobatException(TerminiIniciat.class, terminiIniciatId);
		
		if (terminiIniciat.getDataInici() == null)
			throw new ValidacioException( messageHelper.getMessage("error.terminiService.noIniciat") );
		terminiIniciat.setDataAturada(data);
		suspendTimers(terminiIniciat);
		String processInstanceId = terminiIniciat.getProcessInstanceId();
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		if (expedient != null) {
			crearRegistreTermini(
					expedient.getId(),
					processInstanceId,
					Registre.Accio.ATURAR,
					SecurityContextHolder.getContext().getAuthentication().getName());
		}
	}
		
	@Transactional
	@Override
	public void continuar(Long terminiIniciatId, Date data) {
		TerminiIniciat terminiIniciat = terminiIniciatRepository.findById(terminiIniciatId);
		if (terminiIniciat == null)
			throw new NoTrobatException(TerminiIniciat.class, terminiIniciatId);
		
		if (terminiIniciat.getDataAturada() == null)
			throw new ValidacioException( messageHelper.getMessage("error.terminiService.noPausat") );
		int diesAturat = terminiIniciat.getNumDiesAturadaActual(data);
		terminiIniciat.setDiesAturat(terminiIniciat.getDiesAturat() + diesAturat);
		terminiIniciat.setDataAturada(null);
		resumeTimers(terminiIniciat);
		String processInstanceId = terminiIniciat.getProcessInstanceId();
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		if (expedient != null) {
			crearRegistreTermini(
					expedient.getId(),
					processInstanceId,
					Registre.Accio.REPRENDRE,
					SecurityContextHolder.getContext().getAuthentication().getName());
		}
	}
		
	@Transactional
	@Override
	public void cancelar(Long terminiIniciatId, Date data) throws IllegalStateException {
		TerminiIniciat terminiIniciat = terminiIniciatRepository.findById(terminiIniciatId);
		if (terminiIniciat == null)
			throw new NoTrobatException(TerminiIniciat.class, terminiIniciatId);
		
		if (terminiIniciat.getDataInici() == null)
			throw new ValidacioException( messageHelper.getMessage("error.terminiService.noIniciat") );
		terminiIniciat.setDataCancelacio(data);
		suspendTimers(terminiIniciat);
		String processInstanceId = terminiIniciat.getProcessInstanceId();
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		if (expedient != null) {
			crearRegistreTermini(
					expedient.getId(),
					processInstanceId,
					Registre.Accio.CANCELAR,
					SecurityContextHolder.getContext().getAuthentication().getName());
		}
	}
	
	@Transactional
	@Override
	public void modificar(Long terminiId, Long expedientId, Date inicio, int anys, int mesos, int dies, boolean equals) {
		cancelar(terminiId, new Date());
		
		Expedient expedient = expedientRepository.findOne(expedientId);
		terminiHelper.iniciar(
				terminiId, 
				expedient.getProcessInstanceId(), 
				inicio, 
				anys, 
				mesos, 
				dies, 
				equals, 
				true);
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
	public void deleteFestiu(String data) throws ValidacioException, Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Festiu festiu = festiuRepository.findByData(sdf.parse(data));
		if (festiu != null) {
			festiuRepository.delete(festiu);
		} else {
			throw new ValidacioException("No s'ha trobat el dia festiu");
		}
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
