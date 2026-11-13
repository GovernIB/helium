package es.caib.helium.disseny.handler;

import org.flowable.common.engine.api.delegate.Expression;

/**
 * Handler per a modificar el número d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientNumeroModificarHandler {
	void setNumero(Expression numero);
	void setVarNumero(Expression varNumero);
}
