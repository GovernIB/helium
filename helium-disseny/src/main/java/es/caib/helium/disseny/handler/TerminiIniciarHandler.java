package es.caib.helium.disseny.handler;

/**
 * Handler per a iniciar un termini d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TerminiIniciarHandler extends HeliumBpmnHandler {

	void setTerminiCodi(String terminiCodi);
	void setVarTerminiCodi(String varTerminiCodi);
	void setVarData(String varData);
	void setVarTermini(String varTermini);
	void setSumarUnDia(String sumarUnDia);
	void setEsDataFi(String esDataFi);

}
