package es.caib.helium.bpmn.handler;

/**
 * Handler per a modificar el títol d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientTitolModificarHandler extends HeliumActionHandler {

	void setTitol(String titol);
	void setVarTitol(String varTitol);

}
