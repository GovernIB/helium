package es.caib.helium.disseny.handler;

/**
 * Handler per a tornar a obrir un expedient finalitzat.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientDesfinalitzarHandler extends HeliumActionHandler {

	void setReprendre(String reprendre);
	void setVarReprendre(String varReprendre);

}
