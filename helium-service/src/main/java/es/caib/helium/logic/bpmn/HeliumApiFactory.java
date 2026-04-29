package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.logic.helper.ExpedientDadaHelper;
import es.caib.helium.logic.helper.PluginHelper;
import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.repository.EstatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Factoria per a la creació de les instàncies que implementen HeliumApi.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
@RequiredArgsConstructor
public class HeliumApiFactory {

	private final EstatRepository estatRepository;
	private final PluginHelper pluginHelper;
	private final ExpedientDadaHelper expedientDadaHelper;

	public HeliumApi createInstance(
		Expedient expedient,
		String processId) {
		return new HeliumApiImpl(
			expedient,
			processId,
			estatRepository,
			pluginHelper,
			expedientDadaHelper);
	}

}
