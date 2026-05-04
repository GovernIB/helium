package es.caib.helium.bpmn.handler;

/**
 * Handler per a crear una alerta a l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AlertaCrearHandler extends HeliumActionHandler {

	void setUsuari(String usuari);
	void setVarUsuari(String varUsuari);
	void setText(String text);
	void setVarText(String varText);

}
