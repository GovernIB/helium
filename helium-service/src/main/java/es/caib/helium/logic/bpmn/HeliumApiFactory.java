package es.caib.helium.logic.bpmn;

import es.caib.helium.disseny.api.HeliumApi;
import es.caib.helium.logic.helper.*;
import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.repository.EstatRepository;

/**
 * Factoria per a la creació de les instàncies que implementen HeliumApi.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HeliumApiFactory {

	public static HeliumApi createInstance(
		Expedient expedient,
		String processId,
		String taskId,
		EstatRepository estatRepository,
		ExpedientHelper expedientHelper,
		ExpedientDadaHelper expedientDadaHelper,
		ExpedientDocumentHelper expedientDocumentHelper,
		DocumentHelperV3 documentHelperV3,
		PluginHelper pluginHelper,
		AlertaHelper alertaHelper) {
		return new HeliumApiImpl(
			expedient,
			processId,
			taskId,
			estatRepository,
			expedientHelper,
			expedientDadaHelper,
			expedientDocumentHelper,
			documentHelperV3,
			pluginHelper,
			alertaHelper);
	}

}
