package es.caib.helium.disseny.handler;

/**
 * Handler per modificar la georeferència d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientGeorefModificarHandler {
	void setPosx(String posx);
	void setPosy(String posy);
	void setReferencia(String referencia);
}
