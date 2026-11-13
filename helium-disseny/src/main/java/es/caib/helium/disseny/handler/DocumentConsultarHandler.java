package es.caib.helium.disseny.handler;

import org.flowable.common.engine.api.delegate.Expression;

/**
 * Handler per a consultar la informació d'un document i desar-la a dins variables.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentConsultarHandler {
	void setDocument(Expression document);
	void setVarDocument(Expression varDocument);
	void setVarCsv(Expression varCsv);
	void setVarUrl(Expression varUrl);
}
