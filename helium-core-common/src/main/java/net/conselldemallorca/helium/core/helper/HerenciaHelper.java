/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;

/**
 * Helper per falicitar consultes i altres temes d'herència de tipus d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class HerenciaHelper {

	@Resource
	private TascaHelper tascaHelper;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private ExpedientHelper expedientHelper;


	/** Obté l'id del ProcessDefinition del la definició de procés del tipus d'expedient pare en cas
	 * que hi hagi herència.
	 * @param tascaId
	 * @return
	 */
	public String getProcessDefinitionIdHeretadaAmbTaskId(String tascaId) {
		String processDefinitionIdHeretada = null;
		DefinicioProces dp = tascaHelper.findTascaByJbpmTaskId(tascaId).getDefinicioProces();
		if (dp.getExpedientTipus() != null 
				&& dp.getExpedientTipus().getExpedientTipusPare() != null
				&& dp.getExpedientTipus().getExpedientTipusPare().getJbpmProcessDefinitionKey() != null) {

			DefinicioProces dph = definicioProcesRepository.findDarreraVersioAmbEntornIJbpmKey(
					dp.getEntorn().getId(),
					dp.getExpedientTipus().getExpedientTipusPare().getJbpmProcessDefinitionKey());
			if (dph != null)
				processDefinitionIdHeretada = dph.getJbpmId();
		}	
		return processDefinitionIdHeretada;
	}

	/** Obté l'id del ProcessDefinition del la definició de procés del tipus d'expedient pare en cas
	 * que hi hagi herència.
	 * @param expedient
	 * @return
	 */
	public String getProcessDefinitionIdHeretadaAmbExpedient(Expedient expedient) {
		String processDefinitionIdHeretada = null;
		if (expedient.getTipus().getExpedientTipusPare() != null 
				&& expedient.getTipus().getExpedientTipusPare().getJbpmProcessDefinitionKey() != null ) {
			DefinicioProces dph = definicioProcesRepository.findDarreraVersioAmbEntornIJbpmKey(
					expedient.getEntorn().getId(),
					expedient.getTipus().getExpedientTipusPare().getJbpmProcessDefinitionKey());
			if (dph != null)
				processDefinitionIdHeretada = dph.getJbpmId();
		}
		return processDefinitionIdHeretada;
	}

	/** Obté l'id del ProcessDefinition del la definició de procés del tipus d'expedient pare en cas
	 * que hi hagi herència.
	 * @param pid
	 * @return
	 */
	public String getProcessDefinitionIdHeretadaAmbPid(String pid) {
		String processDefinitionIdHeretada = null;
		DefinicioProces dp = expedientHelper.findDefinicioProcesByProcessInstanceId(pid);
		if (dp.getExpedientTipus() != null 
				&& dp.getExpedientTipus().getExpedientTipusPare() != null
				&& dp.getExpedientTipus().getExpedientTipusPare().getJbpmProcessDefinitionKey() != null) {

			DefinicioProces dph = definicioProcesRepository.findDarreraVersioAmbEntornIJbpmKey(
					dp.getEntorn().getId(),
					dp.getExpedientTipus().getExpedientTipusPare().getJbpmProcessDefinitionKey());
			if (dph != null)
				processDefinitionIdHeretada = dph.getJbpmId();
		}						
		return processDefinitionIdHeretada;
	}

}
