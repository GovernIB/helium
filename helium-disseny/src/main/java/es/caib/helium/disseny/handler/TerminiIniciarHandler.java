package es.caib.helium.disseny.handler;



/**
 * Handler per a iniciar un termini d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TerminiIniciarHandler {
	void setTerminiCodi(String terminiCodi);
	void setData(String data);
	void setTermini(String termini);
	void setSumarUnDia(String sumarUnDia);
	void setEsDataFi(String esDataFi);
}
