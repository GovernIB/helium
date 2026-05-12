package es.caib.helium.logic.bpmn;

import es.caib.helium.disseny.api.HeliumApi;
import es.caib.helium.disseny.exception.HeliumHandlerException;
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
public class PortasignaturesHandler implements es.caib.helium.disseny.handler.PortasignaturesHandler {

	private String varResponsableCodi;
	private String responsableCodi;
	private String pas1Responsables;
	private String varPas1Responsables;
	private Integer pas1MinSignataris;
	private String varPas1MinSignataris;
	private String pas2Responsables;
	private String varPas2Responsables;
	private Integer pas2MinSignataris;
	private String varPas2MinSignataris;
	private String pas3Responsables;
	private String varPas3Responsables;
	private Integer pas3MinSignataris;
	private String varPas3MinSignataris;
	private String varDocument;
	private String document;
	private String varAnnexos;
	private String annexos;
	private String varImportancia;
	private String importancia;
	private String varDataLimit;
	private String dataLimit;
	private String varTransicioOK;
	private String transicioOK;
	private String varTransicioKO;
	private String transicioKO;
	private String varPortafirmesFluxId;
	private String portafirmesFluxId;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		String personaCodi = heliumApi.getVariableDefaultValue(varResponsableCodi, responsableCodi);
		String documentCodi = heliumApi.getVariableDefaultValue(varDocument, document);
		List<String> annexCodis = null;
		String annexosValue = heliumApi.getVariableDefaultValue(varAnnexos, annexos);
		if (annexosValue != null) {
			annexCodis = new ArrayList<>(Arrays.asList(annexosValue.split(",")));
		}
		heliumApi.portasignaturesEnviar(
			documentCodi,
			personaCodi,
			annexCodis,
			getPersonaCodisPas(heliumApi, 1),
			getMinSignatarisPas(heliumApi, 1),
			getPersonaCodisPas(heliumApi, 2),
			getMinSignatarisPas(heliumApi, 2),
			getPersonaCodisPas(heliumApi, 3),
			getMinSignatarisPas(heliumApi, 3),
			heliumApi.getVariableDefaultValue(varImportancia, importancia),
			heliumApi.getVariableDefaultValueAsDate(varDataLimit, dataLimit),
			heliumApi.getVariableDefaultValue(varTransicioOK, transicioOK),
			heliumApi.getVariableDefaultValue(varTransicioKO, transicioKO),
			heliumApi.getVariableDefaultValue(varPortafirmesFluxId, portafirmesFluxId));
	}

	private List<String> getPersonaCodisPas(HeliumApi heliumApi, int pas) {
		List<String> resposta = null;
		String responsables = null;
		if (pas == 1) {
			responsables = heliumApi.getVariableDefaultValue(varPas1Responsables, pas1Responsables);
		} else if (pas == 2) {
			responsables = heliumApi.getVariableDefaultValue(varPas2Responsables, pas2Responsables);
		} else if (pas == 3) {
			responsables = heliumApi.getVariableDefaultValue(varPas3Responsables, pas3Responsables);
		}
		if (responsables != null) {
			String[] codis = responsables.split(",");
			resposta = new ArrayList<>(Arrays.asList(codis));
		}
		return resposta;
	}

	private int getMinSignatarisPas(HeliumApi heliumApi, int pas) {
		Integer min = null;
		if (pas == 1) {
			min = heliumApi.getVariableDefaultValue(varPas1MinSignataris, pas1MinSignataris);
		} else if (pas == 2) {
			min = heliumApi.getVariableDefaultValue(varPas2MinSignataris, pas2MinSignataris);
		} else if (pas == 3) {
			min = heliumApi.getVariableDefaultValue(varPas3MinSignataris, pas3MinSignataris);
		}
		if (min != null)
			return min;
		else
			return 0;
	}

}
