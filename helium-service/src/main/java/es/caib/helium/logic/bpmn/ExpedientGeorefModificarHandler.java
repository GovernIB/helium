package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import lombok.Setter;

/**
 * Implementació del handler per modificar la georeferència d'un expedient.
 *
 * <p>{@code posx} i {@code posy} són números (s'admet la coma com a separador decimal). Un paràmetre buit deixa la
 * dada sense valor.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class ExpedientGeorefModificarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.ExpedientGeorefModificarHandler {

	private String posx;
	private String posy;
	private String referencia;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.expedientGeorefModificar(
			asCoordenada("posx", posx),
			asCoordenada("posy", posy),
			isBuit(referencia) ? null : referencia);
	}

	private static Double asCoordenada(String nom, String valor) {
		if (isBuit(valor)) {
			return null;
		}
		try {
			return Double.valueOf(valor.trim().replace(',', '.'));
		} catch (NumberFormatException ex) {
			throw new HeliumHandlerException("El paràmetre " + nom + " no és un número: " + valor);
		}
	}

}
