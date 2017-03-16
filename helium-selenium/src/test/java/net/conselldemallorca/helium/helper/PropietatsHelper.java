package net.conselldemallorca.helium.helper;

import static org.junit.Assert.assertNotNull;

import java.util.Properties;

/**
 * Classe helper per manejar les propietats del projecte de tests de selenium.
 * 
 */
public class PropietatsHelper {

	/** Variable que conté les propietats carregades. */
	private Properties properties = new Properties();

	
	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public String getProperty(String key) {
		return this.properties.getProperty(key);
	}
	
	public String getProperty(String key, String defaultValue) {
		return this.properties.getProperty(key, defaultValue);
	}

	public Integer getPropertyInt(String key, int defaultValue) {
		int ret;
		try {
			ret = Integer.parseInt(this.properties.getProperty(key, Integer.toString(defaultValue)));
		} catch (Exception e) {
			ret = defaultValue;
		}
		return Integer.valueOf(ret);
	}

	public Boolean getPropertyBool(String key, boolean defaultValue) {
		boolean ret;
		try {
			ret = Boolean.parseBoolean(this.properties.getProperty(key, Boolean.toString(defaultValue)));
		} catch (Exception e) {
			ret = defaultValue;
		}
		return Boolean.valueOf(ret);
	}

	/** Retorna la propietat app.base.url amb la url de helium.*/
	public String getBaseUrl() {
		return carregarPropietat("app.base.url", "Url base de helium");
	}
	/** Llegeix la propietat de la url base de la aplicació per a test amb la informació de l'usuari. */
	public String getTestBaseUrl() {
		return carregarPropietat("test.base.url", "Url base per a tests");
	}

	/** Llegeix el codi de l'entorn de proves. */
	public String getEntornTestCodi() {
		return carregarPropietat("test.entorn.codi", "Codi de l'entorn de proves");
	}
		
	/** Llegeix el títol de l'entorn de proves. */
	public String getEntornTestTitol() {
		return carregarPropietat("test.entorn.titol", "Títol de l'entorn de proves");
	}
	
	/** Llegeix el codi de l'usuari de proves. */
	public String getUsuariTestCodi() {
		return carregarPropietat("test.usuari.codi", "Codi de l'entorn de proves");		
	}

	/** Llegeix el codi de l'entorn de proves. */
	public String getTipusExpedientCodi() {
		return carregarPropietat("test.tipus.expedient.codi", "Codi de l'entorn de proves");
	}
	
	/** Llegeix el títol de l'entorn de proves. */
	public String getTipusExpedientTitol() {
		return carregarPropietat("test.tipus.expedient.titol", "Títol de l'entorn de proves");
	}		

	/** Mètode comú que carrega la clau, verifica que no sigui nul·la i retorna el valor. */
	public String carregarPropietat(String key, String description) {
		String value= properties.getProperty(key);
		assertNotNull("Propitat no configurada al fitxer de properties: " + description, value);
		return value;
		
	}
	
	/** Retorna les propietats url, usuari i password:
	 * @return propietats[url, username, password] = propietats["app.forms.service.url", "app.forms.service.username", "app.forms.service.password"]
	 */
	public String[] getIntegracioFormsPropietats() {
		String [] propietats = new String[3];
		propietats[0] = carregarPropietat("app.forms.service.url", "URL del WS de prova d'inici de formulari");
		propietats[1] = properties.getProperty("app.forms.service.username");
		propietats[1] = properties.getProperty("app.forms.service.password");
		return propietats;
	}

	/** Retorna el codi de la definició de procés de test. */
	public String getDefinicioProcesCodi() {
		return carregarPropietat("test.definicio.proces.codi", "Codi de la definicio de proces de test");
	}

	/** Retorna la url del ws de tramitació externa. */
	public String getUrlWsTramitacio() {
		return carregarPropietat("app.tramitacio.url", "URL del WS de tramitació externa");
	}
	
	/** Propietat per indicar si s'han d'esborrar els expedients de test creats. */
	public boolean isEsborrarExpedientTest() {
		return getPropertyBool("test.expedient.esborrar", true);
	}
}
