package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import lombok.Setter;

/**
 * Implementació del handler que genera un document a partir de la seva plantilla i el desa al procés.
 *
 * <p>{@code data} és la data del document en format {@value #FORMAT_DATA} (o una expressió, p. ex.
 * {@code ${dataResolucio}}); si no s'informa s'agafa la data actual.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class DocumentGenerarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.DocumentGenerarHandler {

	private String documentCodi;
	private String data;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.documentGenerar(documentCodi, isBuit(data) ? null : asDate(data));
	}

}
