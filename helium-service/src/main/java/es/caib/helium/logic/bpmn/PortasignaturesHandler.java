package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementació del handler per a enviar documents al portafirmes.
 *
 * <p>El procés ha d'esperar la resposta en una <i>receive task</i> just després de la tasca del handler (com el node
 * d'estat de jBPM): quan el document es signa o es rebutja, continua per {@code transicioOK} o {@code transicioKO}.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class PortasignaturesHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.PortasignaturesHandler {

	private String responsableCodi;
	private String pas1Responsables;
	private String pas1MinSignataris;
	private String pas2Responsables;
	private String pas2MinSignataris;
	private String pas3Responsables;
	private String pas3MinSignataris;
	private String document;
	private String annexos;
	private String importancia;
	private String dataLimit;
	private String transicioOK;
	private String transicioKO;
	private String portafirmesFluxId;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		String personaCodi = responsableCodi;
		String documentCodi = document;
		List<String> annexCodis = null;
		String annexosValue = annexos;
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
			importancia,
			asDate(dataLimit),
			transicioOK,
			transicioKO,
			portafirmesFluxId);
	}

	private List<String> getPersonaCodisPas(int pas) {
		List<String> resposta = null;
		String responsables = null;
		if (pas == 1) {
			responsables = pas1Responsables;
		} else if (pas == 2) {
			responsables = pas2Responsables;
		} else if (pas == 3) {
			responsables = pas3Responsables;
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
			min = asInteger(pas1MinSignataris);
		} else if (pas == 2) {
			min = asInteger(pas2MinSignataris);
		} else if (pas == 3) {
			min = asInteger(pas3MinSignataris);
		}
		if (min != null)
			return min;
		else
			return 0;
	}

}
