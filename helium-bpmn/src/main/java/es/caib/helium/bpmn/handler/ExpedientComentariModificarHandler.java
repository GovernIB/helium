package es.caib.helium.bpmn.handler;

/**
 * Handler per a modificar el comentari de l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientComentariModificarHandler extends HeliumActionHandler {

	void setComentari(String comentari);

}
