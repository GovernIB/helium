package es.caib.helium.disseny.handler;

/**
 * Handler per copiar un document d'un altre expedient a l'expedient actual.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentExpedientCopiarOrigenHandler {
	void setOrigenExpedientTipus(String origenExpedientTipus);
	void setOrigenExpedientNumero(String origenExpedientNumero);
	void setOrigenExpedientDocument(String origenExpedientDocument);
	void setDocumentCodi(String documentCodi);
}
