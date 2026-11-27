package es.caib.helium.logic.bpmn;

import java.util.Date;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import lombok.Setter;

/**
 * Handler per cancel·lar un termini iniciat.
 *
 * <p>{@code data} és la data de cancel·lació en format {@value #FORMAT_DATA} (o una expressió, p. ex.
 * {@code ${dataCancelacio}}); si no s'informa s'agafa la data actual. Si el codi no correspon a cap termini o el
 * termini no s'ha iniciat, la HeliumApi llança l'error.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class TerminiCancelarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.TerminiCancelarHandler {

	private String terminiCodi;
	private String data;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.terminiCancelar(terminiCodi, isBuit(data) ? new Date() : asDate(data));
	}
}
