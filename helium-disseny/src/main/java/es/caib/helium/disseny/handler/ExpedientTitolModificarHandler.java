package es.caib.helium.disseny.handler;

/**
 * Handler per a modificar el títol d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientTitolModificarHandler extends HeliumBpmnHandler {

	void setTitol(String titol);
	void setVarTitol(String varTitol);

}
