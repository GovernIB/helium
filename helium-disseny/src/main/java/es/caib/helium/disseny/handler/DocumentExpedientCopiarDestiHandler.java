package es.caib.helium.disseny.handler;

/**
 * Handler per copiar un document de l'expedient actual a un altre expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentExpedientCopiarDestiHandler {
	void setDestiExpedientTipus(String destiExpedientTipus);
	void setDestiExpedientNumero(String destiExpedientNumero);
	void setDestiExpedientDocument(String destiExpedientDocument);
	void setDocumentCodi(String documentCodi);
}
