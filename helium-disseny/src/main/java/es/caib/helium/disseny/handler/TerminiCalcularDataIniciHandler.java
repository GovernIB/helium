package es.caib.helium.disseny.handler;

/**
 * Handler per calcular la data d'inici d'un termini a partir d'una data de fi.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TerminiCalcularDataIniciHandler {
	public void setTerminiCodi(String terminiCodi);
	public void setData(String data);
	public void setRestarUnDia(String restarUnDia);
	public void setTermini(String termini);
	public void setVarDataInici(String varDataInici);
}
