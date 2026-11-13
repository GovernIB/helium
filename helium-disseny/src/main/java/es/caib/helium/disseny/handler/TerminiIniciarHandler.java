package es.caib.helium.disseny.handler;


import org.flowable.common.engine.api.delegate.Expression;

/**
 * Handler per a iniciar un termini d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TerminiIniciarHandler {
	void setTerminiCodi(Expression terminiCodi);
	void setVarData(Expression varData);
	void setVarTermini(Expression varTermini);
	void setSumarUnDia(Expression sumarUnDia);
	void setEsDataFi(Expression esDataFi);
}
