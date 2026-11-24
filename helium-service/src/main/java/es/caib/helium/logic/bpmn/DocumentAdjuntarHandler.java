package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import lombok.Setter;

/**
 * Implementació del handler per a adjuntar un document a l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class DocumentAdjuntarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.DocumentAdjuntarHandler {

	private String documentOrigen;
	private String titol;
	private String data;
	private String concatenarTitol;
	private String esborrarDocument;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.documentAdjuntar(
			documentOrigen,
			titol,
			asDate(data),
			"true".equals(concatenarTitol),
			"true".equals(esborrarDocument));
	}
}
