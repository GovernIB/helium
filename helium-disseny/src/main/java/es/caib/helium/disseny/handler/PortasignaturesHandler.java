package es.caib.helium.disseny.handler;



/**
 * Handler per a enviar documents al portafirmes.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PortasignaturesHandler {
	void setResponsableCodi(String responsableCodi);
	void setPas1Responsables(String pas1Responsables);
	void setPas1MinSignataris(String pas1MinSignataris);
	void setPas2Responsables(String pas2Responsables);
	void setPas2MinSignataris(String pas2MinSignataris);
	void setPas3Responsables(String pas3Responsables);
	void setPas3MinSignataris(String pas3MinSignataris);
	void setDocument(String document);
	void setAnnexos(String annexos);
	void setImportancia(String importancia);
	void setDataLimit(String dataLimit);
	void setTransicioOK(String transicioOK);
	void setTransicioKO(String transicioKO);
	void setPortafirmesFluxId(String portafirmesFluxId);
}
