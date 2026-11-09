package es.caib.helium.disseny.handler;

import org.flowable.common.engine.impl.el.FixedValue;

/**
 * Handler per a modificar el comentari de l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientComentariModificarHandler {

	void setComentari(FixedValue comentari);
	void setVarComentari(FixedValue varComentari);

}
