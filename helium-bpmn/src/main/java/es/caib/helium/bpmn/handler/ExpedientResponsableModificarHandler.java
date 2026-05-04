package es.caib.helium.bpmn.handler;

/**
 * Handler per a modificar el responsable d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientResponsableModificarHandler extends HeliumActionHandler {

	void setResponsableCodi(String responsableCodi);
	void setVarResponsableCodi(String varResponsableCodi);

}
