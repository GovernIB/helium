package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.model.ConsultaPinbal;
import lombok.Setter;

/**
 * Implementació del handler que fa una consulta genèrica a PINBAL i desa el justificant en un document.
 *
 * <p>{@code dadesEspecifiques} és l'XML de les dades específiques del servei. Pot fer referència a dades de
 * l'expedient amb {@code ${codi}}: el motor substitueix les referències en avaluar el paràmetre.</p>
 *
 * @see AbstractPinbalHandler
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class PinbalConsultaGenericaHandler extends AbstractPinbalHandler implements es.caib.helium.disseny.handler.PinbalConsultaGenericaHandler {

	private String serveiCodi;
	private String dadesEspecifiques;

	@Override
	protected String getServeiCodi() {
		if (isBuit(serveiCodi)) {
			throw new HeliumHandlerException("No s'ha especificat el codi del servei de PINBAL");
		}
		return serveiCodi.trim();
	}

	@Override
	protected void completar(ConsultaPinbal consulta) {
		consulta.setTitularNomComplet(getTitularNom());
		consulta.setDadesEspecifiques(text(dadesEspecifiques));
	}

}
