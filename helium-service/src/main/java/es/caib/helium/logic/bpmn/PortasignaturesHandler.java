package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import org.flowable.common.engine.api.delegate.Expression;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementació del handler per a enviar documents al portafirmes.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class PortasignaturesHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.PortasignaturesHandler {

	private Expression responsableCodi;
	private Expression pas1Responsables;
	private Expression pas1MinSignataris;
	private Expression pas2Responsables;
	private Expression pas2MinSignataris;
	private Expression pas3Responsables;
	private Expression pas3MinSignataris;
	private Expression document;
	private Expression annexos;
	private Expression importancia;
	private Expression dataLimit;
	private Expression transicioOK;
	private Expression transicioKO;
	private Expression portafirmesFluxId;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		String personaCodi = getValue(responsableCodi);
		String documentCodi = getValue(document);
		List<String> annexCodis = null;
		String annexosValue = getValue(annexos);
		if (annexosValue != null) {
			annexCodis = new ArrayList<>(Arrays.asList(annexosValue.split(",")));
		}
		heliumApi.portasignaturesEnviar(
			documentCodi,
			personaCodi,
			annexCodis,
			getPersonaCodisPas(1),
			getMinSignatarisPas(1),
			getPersonaCodisPas(2),
			getMinSignatarisPas(2),
			getPersonaCodisPas(3),
			getMinSignatarisPas(3),
			getValue(importancia),
			getValueAsDate(dataLimit),
			getValue(transicioOK),
			getValue(transicioKO),
			getValue(portafirmesFluxId));
	}

	private List<String> getPersonaCodisPas(int pas) {
		List<String> resposta = null;
		String responsables = null;
		if (pas == 1) {
			responsables = getValue(pas1Responsables);
		} else if (pas == 2) {
			responsables = getValue(pas2Responsables);
		} else if (pas == 3) {
			responsables = getValue(pas3Responsables);
		}
		if (responsables != null) {
			String[] codis = responsables.split(",");
			resposta = new ArrayList<>(Arrays.asList(codis));
		}
		return resposta;
	}

	private int getMinSignatarisPas(int pas) {
		Integer min = null;
		if (pas == 1) {
			min = getValueAsInteger(pas1MinSignataris);
		} else if (pas == 2) {
			min = getValueAsInteger(pas2MinSignataris);
		} else if (pas == 3) {
			min = getValueAsInteger(pas3MinSignataris);
		}
		if (min != null)
			return min;
		else
			return 0;
	}

}
