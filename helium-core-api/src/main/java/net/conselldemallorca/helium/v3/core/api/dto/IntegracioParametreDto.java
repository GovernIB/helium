/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

/**
 * DTO amb informació d'una integració amb un sistema extern.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class IntegracioParametreDto {

	private String nom;
	private String valor;

	public IntegracioParametreDto() {
	}
	public IntegracioParametreDto(String nom, String valor) {
		this.nom = nom;
		this.valor = valor;
	}
	public IntegracioParametreDto(String nom, Object valor) {
		this.nom = nom;
		if (valor != null)
			this.valor = valor.toString();
		else
			valor = "<null>";
	}

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}

}
