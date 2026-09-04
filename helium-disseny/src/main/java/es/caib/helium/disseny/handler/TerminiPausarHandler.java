package es.caib.helium.disseny.handler;

/**
 * Handler per pausar un termini.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TerminiPausarHandler extends HeliumBpmnHandler {
	public void setTerminiCodi(String terminiCodi);
	public void setVarTerminiCodi(String varTerminiCodi);
	public void setVarData(String varData);
}
