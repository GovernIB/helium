package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import lombok.Setter;

/**
 * Implementació del handler que copia un document d'un altre expedient del mateix entorn (tipus i número) a
 * l'expedient actual. Si no s'indica el tipus, és el de l'expedient actual; si no s'indica el codi del document
 * origen, és el mateix que el destí ({@code documentCodi}). Es copien el contingut i les dades de registre.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class DocumentExpedientCopiarOrigenHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.DocumentExpedientCopiarOrigenHandler {

	private String origenExpedientTipus;
	private String origenExpedientNumero;
	private String origenExpedientDocument;
	private String documentCodi;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.documentCopiarDesDExpedient(
			isBuit(origenExpedientTipus) ? null : origenExpedientTipus,
			origenExpedientNumero,
			isBuit(origenExpedientDocument) ? null : origenExpedientDocument,
			documentCodi);
	}

}
