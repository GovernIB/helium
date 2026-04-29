package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import lombok.Setter;

/**
 * Implementació del handler per a modificar el comentari de l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class PortasignaturesHandler implements es.caib.helium.bpmn.handler.PortasignaturesHandler {

	private String varResponsableCodi;
	private String responsableCodi;
	private String pas1Responsables;
	private String varPas1Responsables;
	private String pas1MinSignataris;
	private String varPas1MinSignataris;
	private String pas2Responsables;
	private String varPas2Responsables;
	private String pas2MinSignataris;
	private String varPas2MinSignataris;
	private String pas3Responsables;
	private String varPas3Responsables;
	private String pas3MinSignataris;
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
		// TODO implementar lògica del handler
	}

}
