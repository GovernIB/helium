package es.caib.helium.disseny.handler;

import org.flowable.common.engine.api.delegate.Expression;

/**
 * Handler per a modificar el comentari de l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientComentariModificarHandler {
	void setComentari(Expression comentari);
	void setVarComentari(Expression varComentari);
}
