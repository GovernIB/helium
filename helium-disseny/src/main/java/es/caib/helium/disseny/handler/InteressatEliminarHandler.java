package es.caib.helium.disseny.handler;

/**
 * Handler per a eliminar interessats de l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface InteressatEliminarHandler extends HeliumActionHandler {

	void setCodi(String codi);
	void setVarCodi(String varCodi);

}
