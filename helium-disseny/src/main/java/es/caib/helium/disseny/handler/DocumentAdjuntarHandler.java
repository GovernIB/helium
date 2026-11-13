package es.caib.helium.disseny.handler;


import org.flowable.common.engine.api.delegate.Expression;

/**
 * Handler per a adjuntar un document a l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentAdjuntarHandler {
	void setDocumentOrigen(Expression documentOrigen);
	void setVarDocumentOrigen(Expression varDocumentOrigen);
	void setTitol(Expression titol);
	void setVarTitol(Expression varTitol);
	void setData(Expression data);
	void setVarData(Expression varData);
	void setConcatenarTitol(Expression concatenarTitol);
	void setEsborrarDocument(Expression esborrarDocument);
}
