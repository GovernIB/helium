/**
 * 
 */
package es.caib.helium.integracio.plugins.tramitacio;


/**
 * 
 * @author Limit Tecnologies
 */
public class ResultatProcesTramitRequest {

	protected String numeroEntrada;
	protected String clauAcces;
	protected ResultatProcesTipus resultatProces;
	protected String errorDescripcio;



	public String getNumeroEntrada() {
		return numeroEntrada;
	}
	public void setNumeroEntrada(String numeroEntrada) {
		this.numeroEntrada = numeroEntrada;
	}
	public String getClauAcces() {
		return clauAcces;
	}
	public void setClauAcces(String clauAcces) {
		this.clauAcces = clauAcces;
	}
	public ResultatProcesTipus getResultatProces() {
		return resultatProces;
	}
	public void setResultatProces(ResultatProcesTipus resultatProces) {
		this.resultatProces = resultatProces;
	}
	public String getErrorDescripcio() {
		return errorDescripcio;
	}
	public void setErrorDescripcio(String errorDescripcio) {
		this.errorDescripcio = errorDescripcio;
	}

}
