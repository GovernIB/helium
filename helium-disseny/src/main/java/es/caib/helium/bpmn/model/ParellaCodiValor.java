package es.caib.helium.bpmn.model;

import java.io.Serializable;

/**
 * Parella codi-valor: un valor d'una enumeració (codi i nom) o una columna d'una fila del resultat d'una consulta de
 * domini.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ParellaCodiValor implements Serializable {

	private String codi;
	private Object valor;

	public ParellaCodiValor() {
	}

	public ParellaCodiValor(String codi, Object valor) {
		this.codi = codi;
		this.valor = valor;
	}

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public Object getValor() {
		return valor;
	}
	public void setValor(Object valor) {
		this.valor = valor;
	}

	@Override
	public String toString() {
		return codi + "=" + valor;
	}

	private static final long serialVersionUID = 1L;

}
