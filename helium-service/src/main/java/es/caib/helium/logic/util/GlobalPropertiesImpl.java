/**
 * 
 */
package es.caib.helium.logic.util;

import es.caib.helium.logic.intf.util.GlobalProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Properties;

/**
 * Emmagatzema les propietats globals de l'aplicació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class GlobalPropertiesImpl implements GlobalProperties {

	private static final long serialVersionUID = 6970351650653878240L;

	@Resource(name = "getGlobalProperties")
	private Properties globalProperties;
	private static Properties sGlobalProperties;

	// Mètodes estàtics //////////////////////////////////////////
	@PostConstruct
	public void init() {
		GlobalPropertiesImpl.sGlobalProperties = globalProperties;
	}

	public static String getPropietat(String key) {
		return sGlobalProperties.getProperty(key);
	}

	public static String getPropietat(String key, String defaultValue) {
		return sGlobalProperties.getProperty(key, defaultValue);
	}

	///////////////////////////////////////////////////////////////

	@Override
	public String getProperty(String key) {
		return globalProperties.getProperty(key);
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		return globalProperties.getProperty(key, defaultValue);
	}

	public boolean getAsBoolean(String key) {
		String value = globalProperties.getProperty(key);
		if (value != null)
			return Boolean.parseBoolean(getProperty(key));
		else
			return false;
	}

	public int getAsInt(String key) {
		return Integer.valueOf(globalProperties.getProperty(key));
	}

	public long getAsLong(String key) {
		return Long.valueOf(globalProperties.getProperty(key));
	}

	public float getAsFloat(String key) {
		return Float.valueOf(globalProperties.getProperty(key));
	}

	public double getAsDouble(String key) {
		return Double.valueOf(globalProperties.getProperty(key));
	}


	@Override
	public Properties findByPrefix(String prefix) {
		Properties props = new Properties();
		globalProperties.entrySet().stream()
				.filter(e -> ((String)e.getKey()).startsWith(prefix))
				.forEach(e -> props.setProperty((String) e.getKey(), (String) e.getValue()));
		return props;
	}

	@Override
	public Properties findAll() {
		return globalProperties;
	}

	@Override
	public Object setProperty(String key, String value) {
		return globalProperties.setProperty(key, value);
	}
}
