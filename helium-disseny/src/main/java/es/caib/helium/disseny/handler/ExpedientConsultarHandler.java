package es.caib.helium.disseny.handler;

/**
 * Handler per a consultar la informació de l'expedient actual i desar-la a dins variables.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientConsultarHandler extends HeliumActionHandler {

	void setVarRegistreNumero(String varRegistreNumero);
	void setVarTitol(String varTitol);
	void setVarNumero(String varNumero);
	void setVarDataInici(String varDataInici);

}
