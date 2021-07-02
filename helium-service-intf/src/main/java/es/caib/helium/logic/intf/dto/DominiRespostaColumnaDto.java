/**
 * 
 */
package es.caib.helium.logic.intf.dto;


/**
 * DTO amb informació d'una columna de resultat d'una consulta
 * de domini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DominiRespostaColumnaDto {

	private String codi;
	private Object valor;

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
	
}
