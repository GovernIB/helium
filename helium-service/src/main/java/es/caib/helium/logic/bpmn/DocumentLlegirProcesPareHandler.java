package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import lombok.Setter;

/**
 * Implementació del handler que fa que el subprocés on s'executa tingui un document del procés pare. És el mateix
 * document (no una còpia).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class DocumentLlegirProcesPareHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.DocumentLlegirProcesPareHandler {

	private String documentCodi;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.documentLlegirProcesPare(documentCodi);
	}

}
