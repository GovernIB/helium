package es.caib.helium.disseny.handler;

import org.flowable.common.engine.api.delegate.Expression;

/**
 * Handler per a modificar el responsable d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientResponsableModificarHandler {
	void setResponsableCodi(Expression responsableCodi);
	void setVarResponsableCodi(Expression varResponsableCodi);
}
