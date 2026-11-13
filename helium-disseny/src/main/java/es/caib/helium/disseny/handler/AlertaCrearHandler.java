package es.caib.helium.disseny.handler;

import org.flowable.common.engine.api.delegate.Expression;

/**
 * Handler per a crear una alerta a l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AlertaCrearHandler {
	void setUsuari(Expression usuari);
	void setVarUsuari(Expression varUsuari);
	void setText(Expression text);
	void setVarText(Expression varText);
}
