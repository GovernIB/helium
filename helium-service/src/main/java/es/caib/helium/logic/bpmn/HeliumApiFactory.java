package es.caib.helium.logic.bpmn;

import es.caib.helium.disseny.api.HeliumApi;
import es.caib.helium.logic.helper.*;
import es.caib.helium.persistence.entity.Expedient;

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
		ExpedientHelper expedientHelper,
		ExpedientDadaHelper expedientDadaHelper,
		ExpedientDocumentHelper expedientDocumentHelper,
		DocumentHelperV3 documentHelperV3,
		PluginHelper pluginHelper,
		AlertaHelper alertaHelper,
		ExpedientInteressatHelper expedientInteressatHelper,
		MailHelper mailHelper) {
		return new HeliumApiImpl(
			expedient,
			processId,
			taskId,
			expedientHelper,
			expedientDadaHelper,
			expedientDocumentHelper,
			documentHelperV3,
			pluginHelper,
			alertaHelper,
			expedientInteressatHelper,
			mailHelper);
	}

}
