package es.caib.helium.disseny.handler;

import org.flowable.common.engine.api.delegate.Expression;

/**
 * Handler per a esborrar un document de l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentEsborrarHandler {
	void setDocumentCodi(Expression documentCodi);
	void setVarDocumentCodi(Expression varDocumentCodi);
}
