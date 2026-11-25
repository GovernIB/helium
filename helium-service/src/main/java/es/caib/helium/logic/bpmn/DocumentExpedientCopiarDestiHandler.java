package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import lombok.Setter;

/**
 * Implementació del handler que copia un document de l'expedient actual a un altre expedient del mateix entorn
 * (tipus i número). Si no s'indica el tipus, és el de l'expedient actual; si no s'indica el codi del document destí,
 * és el mateix que l'origen. Es copien el contingut i les dades de registre.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class DocumentExpedientCopiarDestiHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.DocumentExpedientCopiarDestiHandler {

	private String destiExpedientTipus;
	private String destiExpedientNumero;
	private String destiExpedientDocument;
	private String documentCodi;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.documentCopiarAExpedient(
			documentCodi,
			isBuit(destiExpedientTipus) ? null : destiExpedientTipus,
			destiExpedientNumero,
			isBuit(destiExpedientDocument) ? null : destiExpedientDocument);
	}

}
