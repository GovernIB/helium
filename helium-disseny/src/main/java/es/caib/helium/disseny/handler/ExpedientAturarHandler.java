package es.caib.helium.disseny.handler;

/**
 * Handler per a aturar la tramitació d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientAturarHandler extends HeliumActionHandler {

	void setMotiu(String motiu);
	void setVarMotiu(String varMotiu);

}
