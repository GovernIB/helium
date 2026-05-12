package es.caib.helium.disseny.handler;

/**
 * Handler per a esborrar un document de l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentEsborrarHandler extends HeliumActionHandler {

	void setDocumentCodi(String documentCodi);
	void setVarDocumentCodi(String varDocumentCodi);

}
