package es.caib.helium.disseny.handler;

/**
 * Handler per calcular la data de fi d'un termini a partir d'una data d'inici.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TerminiCalcularDataFiHandler {
	public void setTerminiCodi(String terminiCodi);
	public void setData(String data);
	public void setSumarUnDia(String sumarUnDia);
	public void setTermini(String termini);
	public void setVarDataFi(String varDataFi);
}
