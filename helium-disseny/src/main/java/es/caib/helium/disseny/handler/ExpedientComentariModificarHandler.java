package es.caib.helium.disseny.handler;

/**
 * Handler per a modificar el comentari de l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientComentariModificarHandler extends HeliumActionHandler {

	void setComentari(String comentari);
	void setVarComentari(String varComentari);

}
