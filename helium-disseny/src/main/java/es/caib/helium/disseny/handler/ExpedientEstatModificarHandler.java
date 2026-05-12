package es.caib.helium.disseny.handler;

/**
 * Handler per a modificar l'estat de l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientEstatModificarHandler extends HeliumActionHandler {

	void setEstatCodi(String estatCodi);
	void setVarEstatCodi(String varEstatCodi);

}
