package es.caib.helium.disseny.handler;

import org.flowable.common.engine.api.delegate.Expression;

/**
 * Handler per a modificar l'estat de l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientEstatModificarHandler {

	void setEstatCodi(Expression estatCodi);
	void setVarEstatCodi(Expression varEstatCodi);

}
