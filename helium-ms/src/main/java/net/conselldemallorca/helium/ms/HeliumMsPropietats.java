package net.conselldemallorca.helium.ms;


import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Classe per configurar les propietats necessàries per Helium MS
 *
 */
@Component
public class HeliumMsPropietats {

	@Value("${app.helium.ms.client.baseUrl}")
	private String baseUrl;
		
	/** Propietat amb el valor de l'usuari per a l'autenticació bàsica amb l'api. */
	@Value("${app.helium.ms.client.usuari}")
	private String usuari;

	/** Propietat amb el valor del password per a l'autenticació bàsica amb l'api. */
	@Value("${app.helium.ms.client.password}")
	private String password;
	/**
	 * @return the url
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * @return the usuari
	 */
	public String getUsuari() {
		return usuari;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/** Valida que com a mínim s'hagi informat la url base d'Helium MS. En 
	 * cas contrari llença una excepció.
	 * 
	 * @throws Exception Llença excepció si no s'ha configurat la url base.
	 */
	@PostConstruct
	public void valida() throws Exception {
		if (this.baseUrl == null)
			throw new Exception("S'ha d'informar com a mínim al propietat \"app.helium.ms.client.baseUrl\" per comunicar-se amb els microserveis d'Helium");
	}
}