package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.model.ConsultaPinbal;
import es.caib.helium.logic.helper.PluginHelper;
import lombok.Setter;

/**
 * Implementació del handler que consulta a PINBAL les dades d'identitat del titular (SVDDGPCIWS02).
 *
 * @see AbstractPinbalHandler
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class PinbalSvddgpciws02Handler extends AbstractPinbalHandler implements es.caib.helium.disseny.handler.PinbalSvddgpciws02Handler {

	private String anyNaixement;

	@Override
	protected String getServeiCodi() {
		return PluginHelper.serveiConsultaDades;
	}

	@Override
	protected void completar(ConsultaPinbal consulta) {
		consulta.setTitularNomComplet(getTitularNom());
		consulta.setAnyNaixement(text(anyNaixement));
	}

}
