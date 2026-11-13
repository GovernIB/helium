package es.caib.helium.disseny.handler;

import org.flowable.common.engine.api.delegate.Expression;

/**
 * Handler per a modificar el grup d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientGrupModificarHandler {
	void setGrup(Expression grup);
	void setVarGrup(Expression varGrup);
}
