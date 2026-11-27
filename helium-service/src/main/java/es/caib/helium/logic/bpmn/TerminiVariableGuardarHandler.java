package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import lombok.Setter;

/**
 * Handler per desar una durada en una dada de tipus termini.
 *
 * <p>Paràmetres: {@code varTermini} és el codi de la dada on es desa el termini (en format {@code anys/mesos/dies},
 * com les dades de tipus termini) i {@code anys}, {@code mesos} i {@code dies} la durada. Els tres poden ser
 * expressions que llegeixin les dades de l'expedient, p. ex. {@code ${diesResposta}}; tots tres són obligatoris.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class TerminiVariableGuardarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.TerminiVariableGuardarHandler {

	private String varTermini;
	private String anys;
	private String mesos;
	private String dies;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		if (isBuit(varTermini)) {
			throw new HeliumHandlerException("S'ha d'especificar la dada on desar el termini (varTermini)");
		}
		Integer a = asInteger(anys);
		Integer m = asInteger(mesos);
		Integer d = asInteger(dies);
		if (a == null || m == null || d == null) {
			throw new HeliumHandlerException("S'han d'especificar anys, mesos i dies");
		}
		heliumApi.setVariable(varTermini.trim(), a + "/" + m + "/" + d);
	}
}
