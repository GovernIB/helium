package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import lombok.Setter;

/**
 * Implementació del handler que fa que el procés pare tingui un document del subprocés on s'executa. És el mateix
 * document (no una còpia). Si el subprocés no té el document no es fa res.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class DocumentEscriureProcesPareHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.DocumentEscriureProcesPareHandler {

	private String documentCodi;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.documentEscriureProcesPare(documentCodi);
	}

}
