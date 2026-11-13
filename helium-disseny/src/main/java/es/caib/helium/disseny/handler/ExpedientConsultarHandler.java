package es.caib.helium.disseny.handler;

import org.flowable.common.engine.api.delegate.Expression;

/**
 * Handler per a consultar la informació de l'expedient actual i desar-la a dins variables.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientConsultarHandler {
	void setVarRegistreNumero(Expression varRegistreNumero);
	void setVarTitol(Expression varTitol);
	void setVarNumero(Expression varNumero);
	void setVarDataInici(Expression varDataInici);
}
