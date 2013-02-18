/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
import net.conselldemallorca.helium.v3.core.api.exception.TerminiIniciatNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TerminiNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.TerminiService;
import net.conselldemallorca.helium.v3.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.v3.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.v3.core.helper.TerminiHelper;
import net.conselldemallorca.helium.v3.core.repository.TerminiIniciatRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiRepository;

/**
 * Servei per gestionar terminis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("terminiServiceV3")
public class TerminiServiceImpl implements TerminiService {

	@Resource
	private TerminiRepository terminiRepository;
	@Resource
	private TerminiIniciatRepository terminiIniciatRepository;

	@Resource
	private TerminiHelper terminiHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;



	public TerminiIniciatDto iniciar(
			Long terminiId,
			String processInstanceId,
			Date data,
			boolean esDataFi) throws TerminiNotFoundException {
		return terminiHelper.iniciar(
				terminiId,
				processInstanceId,
				data,
				esDataFi);
	}
	public TerminiIniciatDto iniciar(
			Long terminiId,
			String processInstanceId,
			Date data,
			int anys,
			int mesos,
			int dies,
			boolean esDataFi) throws TerminiNotFoundException {
		return terminiHelper.iniciar(
				terminiId,
				processInstanceId,
				data,
				anys,
				mesos,
				dies,
				esDataFi);
	}

	public void cancelar(
			Long terminiIniciatId,
			Date data) {
		terminiHelper.cancelar(terminiIniciatId, data);
	}
	public void pausar(
			Long terminiIniciatId,
			Date data) {
		terminiHelper.pausar(terminiIniciatId, data);
	}
	public void continuar(
			Long terminiIniciatId,
			Date data) {
		terminiHelper.continuar(terminiIniciatId, data);
	}

	public TerminiIniciatDto findIniciatAmbTerminiIProcessInstance(
			Long terminiId,
			String processInstanceId) throws TerminiNotFoundException {
		Termini termini = terminiRepository.findOne(terminiId);
		if (termini == null)
			throw new TerminiNotFoundException();
		return conversioTipusHelper.convertir(
				terminiIniciatRepository.findByTerminiAndProcessInstanceId(
						termini,
						processInstanceId),
				TerminiIniciatDto.class);
	}
	public TerminiIniciatDto findIniciatAmbCodiIProcessInstance(
			String codi,
			String processInstanceId) throws TerminiNotFoundException {
		DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(processInstanceId);
		if (definicioProces == null)
			throw new TerminiNotFoundException();
		Termini termini = terminiRepository.findByDefinicioProcesAndCodi(
				definicioProces,
				codi);
		if (termini == null)
			throw new TerminiNotFoundException();
		return conversioTipusHelper.convertir(
				terminiIniciatRepository.findByTerminiAndProcessInstanceId(
						termini,
						processInstanceId),
				TerminiIniciatDto.class);
	}

	public Date getDataFiTermini(
			Date inici,
			int anys,
			int mesos,
			int dies,
			boolean laborable) {
		return terminiHelper.getDataFiTermini(inici, anys, mesos, dies, laborable);
	}

	public Date getDataIniciTermini(
			Date fi,
			int anys,
			int mesos,
			int dies,
			boolean laborable) {
		return terminiHelper.getDataIniciTermini(fi, anys, mesos, dies, laborable);
	}

	public void configurarIniciatAmbDadesJbpm(
			Long terminiIniciatId,
			String taskInstanceId,
			Long timerId) throws TerminiIniciatNotFoundException {
		TerminiIniciat terminiIniciat = terminiIniciatRepository.findOne(terminiIniciatId);
		if (terminiIniciat == null)
			throw new TerminiIniciatNotFoundException();
		terminiIniciat.setTaskInstanceId(taskInstanceId);
		if (timerId != null)
			terminiIniciat.afegirTimerId(timerId.longValue());
	}

}
