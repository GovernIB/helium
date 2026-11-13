package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import lombok.Setter;
import org.flowable.common.engine.api.delegate.Expression;

/**
 * Implementació del handler per a adjuntar un document a l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class DocumentAdjuntarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.DocumentAdjuntarHandler {

	private Expression documentOrigen;
	private Expression varDocumentOrigen;
	private Expression titol;
	private Expression varTitol;
	private Expression data;
	private Expression varData;
	private Expression concatenarTitol;
	private Expression esborrarDocument;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.documentAdjuntar(
			heliumApi.getVariableDefaultValue(getValue(varDocumentOrigen), getValue(documentOrigen)),
			heliumApi.getVariableDefaultValue(getValue(varTitol), getValue(titol)),
			heliumApi.getVariableDefaultValueAsDate(getValue(varData), getValueAsDate(data)),
			"true".equals(getValue(concatenarTitol)),
			"true".equals(getValue(esborrarDocument)));
	}
}
