package es.caib.helium.disseny.handler;

import org.flowable.common.engine.api.delegate.Expression;

/**
 * Handler per pausar un termini.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TerminiPausarHandler {
	public void setTerminiCodi(Expression terminiCodi);
	public void setVarData(Expression varData);
}
