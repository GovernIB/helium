package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.model.ConsultaPinbal;
import es.caib.helium.logic.helper.PluginHelper;

/**
 * Implementació del handler que consulta a PINBAL si el titular està al corrent de les obligacions tributàries
 * (SVDCCAACPASWS01).
 *
 * @see AbstractPinbalHandler
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PinbalSvdccaacpasws01Handler extends AbstractPinbalHandler implements es.caib.helium.disseny.handler.PinbalSvdccaacpasws01Handler {

	@Override
	protected String getServeiCodi() {
		return PluginHelper.serveiObligacionsTributaries;
	}

	@Override
	protected void completar(ConsultaPinbal consulta) {
		consulta.setTitularNomComplet(getTitularNom());
	}

}
