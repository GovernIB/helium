/**
 * 
 */
package net.conselldemallorca.helium.core.util;

import java.io.Serializable;

/**
 * Objecte per enviar la resposta de la signatura cap al servlet
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class RespostaSignatura implements Serializable {

	private String token;
	private String arxiuNom;
	private Object signatura;



	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getArxiuNom() {
		return arxiuNom;
	}
	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
	}
	public Object getSignatura() {
		return signatura;
	}
	public void setSignatura(Object signatura) {
		this.signatura = signatura;
	}

}
