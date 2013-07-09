/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;



/**
 * DTO amb informació d'una opció d'un camp de formulari
 * de tipus selecció.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SeleccioOpcioDto {

	private String codi;
	private String text;

	public SeleccioOpcioDto() {
	}
	public SeleccioOpcioDto(String codi, String text) {
		this.codi = codi;
		this.text = text;
	}

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

}
