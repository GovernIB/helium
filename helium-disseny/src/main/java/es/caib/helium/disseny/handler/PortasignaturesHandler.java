package es.caib.helium.disseny.handler;


import org.flowable.common.engine.api.delegate.Expression;

/**
 * Handler per a enviar documents al portafirmes.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PortasignaturesHandler {
	void setResponsableCodi(Expression responsableCodi);
	void setPas1Responsables(Expression pas1Responsables);
	void setPas1MinSignataris(Expression pas1MinSignataris);
	void setPas2Responsables(Expression pas2Responsables);
	void setPas2MinSignataris(Expression pas2MinSignataris);
	void setPas3Responsables(Expression pas3Responsables);
	void setPas3MinSignataris(Expression pas3MinSignataris);
	void setDocument(Expression document);
	void setAnnexos(Expression annexos);
	void setImportancia(Expression importancia);
	void setDataLimit(Expression dataLimit);
	void setTransicioOK(Expression transicioOK);
	void setTransicioKO(Expression transicioKO);
	void setPortafirmesFluxId(Expression portafirmesFluxId);
}
