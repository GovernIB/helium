package es.caib.helium.disseny.handler;

/**
 * Handler per a modificar el número d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientNumeroModificarHandler extends HeliumActionHandler {

	void setNumero(String numero);
	void setVarNumero(String varNumero);

}
