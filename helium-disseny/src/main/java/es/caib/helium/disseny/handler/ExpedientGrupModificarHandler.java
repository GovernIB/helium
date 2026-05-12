package es.caib.helium.disseny.handler;

/**
 * Handler per a modificar el grup d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientGrupModificarHandler extends HeliumActionHandler {

	void setGrup(String grup);
	void setVarGrup(String varGrup);

}
