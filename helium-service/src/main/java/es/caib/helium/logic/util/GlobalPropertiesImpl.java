/**
 * 
 */
package es.caib.helium.logic.util;

import es.caib.helium.logic.intf.util.GlobalProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Emmagatzema les propietats globals de l'aplicació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GlobalPropertiesImpl extends Properties implements GlobalProperties {

	private static final String APPSERV_PROPS_PATH = "es.caib.helium.properties.path";

	private static GlobalPropertiesImpl instance = null;

	private boolean llegirSystem = true;



	public static GlobalPropertiesImpl getInstance() {
		return getProperties();
	}

	public GlobalPropertiesImpl() {
		super();
	}
	public GlobalPropertiesImpl(Resource resource) throws IOException {
		super();
		super.load(resource.getInputStream());
		if (instance == null) {
			instance = this;
		}
	}
	public static GlobalPropertiesImpl getProperties() {
		if (instance == null) {
			instance = new GlobalPropertiesImpl();
			String propertiesPath = System.getProperty(APPSERV_PROPS_PATH);
			if (propertiesPath != null) {
				instance.llegirSystem = false;
				logger.info("Llegint les propietats de l'aplicació del path: " + propertiesPath);
				try {
					if (propertiesPath.startsWith("classpath:")) {
						instance.load(
								GlobalPropertiesImpl.class.getClassLoader().getResourceAsStream(
										propertiesPath.substring("classpath:".length())));
					} else if (propertiesPath.startsWith("file://")) {
						FileInputStream fis = new FileInputStream(
								propertiesPath.substring("file://".length()));
						instance.load(fis);
					} else {
						FileInputStream fis = new FileInputStream(propertiesPath);
						instance.load(fis);
					}
				} catch (Exception ex) {
					logger.error("No s'han pogut llegir els properties", ex);
				}
			}
		}
		return instance;
	}

	public String getProperty(String key) {
		if (llegirSystem)
			return System.getProperty(key);
		else
			return super.getProperty(key);
	}
	public String getProperty(String key, String defaultValue) {
		String val = getProperty(key);
        return (val == null) ? defaultValue : val;
	}

	public boolean getAsBoolean(String key) {
		String value = getProperty(key);
		if (value != null)
			return new Boolean(getProperty(key)).booleanValue();
		else
			return false;
	}
	public int getAsInt(String key) {
		return Integer.valueOf(getProperty(key));
	}
	public long getAsLong(String key) {
		return Long.valueOf(getProperty(key));
	}
	public float getAsFloat(String key) {
		return Float.valueOf(getProperty(key));
	}
	public double getAsDouble(String key) {
		return Double.valueOf(getProperty(key));
	}

	public Properties findByPrefix(String prefix) {
		Properties properties = new Properties();
		if (llegirSystem) {
			for (Object key: System.getProperties().keySet()) {
				if (key instanceof String) {
					String keystr = (String)key;
					if (prefix == null || keystr.startsWith(prefix)) {
						properties.put(
								keystr,
								System.getProperty(keystr));
					}
				}
			}
		} else {
			for (Object key: this.keySet()) {
				if (key instanceof String) {
					String keystr = (String)key;
					if (prefix == null || keystr.startsWith(prefix)) {
						properties.put(
								keystr,
								getProperty(keystr));
					}
				}
			}
		}
		return properties;
	}
	public Properties findAll() {
		return findByPrefix(null);
	}

	public boolean isLlegirSystem() {
		return llegirSystem;
	}

	private static final Logger logger = LoggerFactory.getLogger(GlobalPropertiesImpl.class);
	private static final long serialVersionUID = 1L;

}
