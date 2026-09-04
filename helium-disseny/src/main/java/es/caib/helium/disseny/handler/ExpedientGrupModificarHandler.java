package es.caib.helium.disseny.handler;

/**
 * Handler per a modificar el grup d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientGrupModificarHandler extends HeliumBpmnHandler {

	void setGrup(String grup);
	void setVarGrup(String varGrup);

}
