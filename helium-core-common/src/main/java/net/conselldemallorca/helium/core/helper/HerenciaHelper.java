/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import org.springframework.stereotype.Component;

import es.caib.helium.persist.entity.DefinicioProces;
import es.caib.helium.persist.entity.Expedient;
import es.caib.helium.persist.entity.ExpedientTipus;
import es.caib.helium.persist.repository.DefinicioProcesRepository;

import javax.annotation.Resource;

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
		DefinicioProces dp = tascaHelper.findTascaByWTaskInstanceId(tascaId).getDefinicioProces();
		if (dp.getExpedientTipus() != null 
				&& dp.getExpedientTipus().getExpedientTipusPare() != null
				&& dp.getExpedientTipus().getExpedientTipusPare().getJbpmProcessDefinitionKey() != null) {

			DefinicioProces dph = definicioProcesRepository.findDarreraVersioAmbTipusExpedientIJbpmKey(
					dp.getExpedientTipus().getExpedientTipusPare().getId(),
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
			DefinicioProces dph = definicioProcesRepository.findDarreraVersioAmbTipusExpedientIJbpmKey(
					expedient.getTipus().getExpedientTipusPare().getId(),
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

			DefinicioProces dph = definicioProcesRepository.findDarreraVersioAmbTipusExpedientIJbpmKey(
					dp.getExpedientTipus().getExpedientTipusPare().getId(),
					dp.getExpedientTipus().getExpedientTipusPare().getJbpmProcessDefinitionKey());
			if (dph != null)
				processDefinitionIdHeretada = dph.getJbpmId();
		}						
		return processDefinitionIdHeretada;
	}
	
	/** Mètode estàtic per comprovar si un tipus d'expedient no és null i té herència */
	public static boolean ambHerencia(ExpedientTipus expedientTipus) {
		return expedientTipus != null && expedientTipus.isAmbHerencia();
	}
}
