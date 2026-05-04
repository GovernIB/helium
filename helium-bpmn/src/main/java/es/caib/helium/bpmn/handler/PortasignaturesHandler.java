package es.caib.helium.bpmn.handler;

/**
 * Handler per a enviar documents al portafirmes.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PortasignaturesHandler extends HeliumActionHandler {

	void setVarResponsableCodi(String varResponsableCodi);

	void setResponsableCodi(String responsableCodi);

	void setPas1Responsables(String pas1Responsables);

	void setVarPas1Responsables(String varPas1Responsables);

	void setPas1MinSignataris(Integer pas1MinSignataris);

	void setVarPas1MinSignataris(String varPas1MinSignataris);

	void setPas2Responsables(String pas2Responsables);

	void setVarPas2Responsables(String varPas2Responsables);

	void setPas2MinSignataris(Integer pas2MinSignataris);

	void setVarPas2MinSignataris(String varPas2MinSignataris);

	void setPas3Responsables(String pas3Responsables);

	void setVarPas3Responsables(String varPas3Responsables);

	void setPas3MinSignataris(Integer pas3MinSignataris);

	void setVarPas3MinSignataris(String varPas3MinSignataris);

	void setVarDocument(String varDocument);

	void setDocument(String document);

	void setVarAnnexos(String varAnnexos);

	void setAnnexos(String annexos);

	void setVarImportancia(String varImportancia);

	void setImportancia(String importancia);

	void setVarDataLimit(String varDataLimit);

	void setDataLimit(String dataLimit);

	void setVarTransicioOK(String varTransicioOK);

	void setTransicioOK(String transicioOK);

	void setVarTransicioKO(String varTransicioKO);

	void setTransicioKO(String transicioKO);

	void setVarPortafirmesFluxId(String varPortafirmesFluxId);

	void setPortafirmesFluxId(String portafirmesFluxId);

}
