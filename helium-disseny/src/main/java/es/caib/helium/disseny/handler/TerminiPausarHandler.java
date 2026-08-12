package es.caib.helium.disseny.handler;

/**
 * Handler per pausar un termini.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TerminiPausarHandler extends HeliumActionHandler {
	public void setTerminiCodi(String terminiCodi);
	public void setVarTerminiCodi(String varTerminiCodi);
	public void setVarData(String varData);
}
