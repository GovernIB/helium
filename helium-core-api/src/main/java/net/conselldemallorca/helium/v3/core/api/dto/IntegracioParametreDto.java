/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Map;

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
	
	
	/// Mètodes estàtics de suport a la creació de paràmetres
	
	/** Retorna un script descriptiu amb el contingut dels paràmetres
	 * 
	 * @param parametres
	 * @return
	 */
	public static String parametresToString(
			Map<String, Object> parametres) {
		String separador = ", ";
		StringBuilder sb = new StringBuilder();
		if (parametres != null) {
			for (String key: parametres.keySet()) {
				sb.append(key);
				sb.append(":");
				sb.append(parametres.get(key));
				sb.append(separador);
			}
		}
		if (sb.length() > 0)
			sb.substring(0, sb.length() - separador.length());
		return sb.toString();
	}

	/** Converteix el map a un array de paràmetres
	 * 
	 * @param params
	 * @return
	 */
	public static IntegracioParametreDto[] toIntegracioParametres(
			Map<String, Object> params) {
		if (params == null)
			return null;
		IntegracioParametreDto[] ips = new IntegracioParametreDto[params.size()];
		int i = 0;
		for (String clau: params.keySet()) {
			Object valor = params.get(clau);
			String valorStr = (valor != null) ? valor.toString() : null;
			ips[i++] = new IntegracioParametreDto(
					clau,
					valorStr);
		}
		return ips;
	}	

}
