/**
 * 
 */
package net.conselldemallorca.helium.core.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.core.io.Resource;

/**
 * Emmagatzema les propietats globals de l'aplicació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GlobalProperties extends Properties {

	private static GlobalProperties instance = null;

	public static GlobalProperties getInstance() {
		return instance;
	}

	public GlobalProperties(Resource resource) throws IOException {
		super();
		super.load(resource.getInputStream());
		if (instance == null) {
			instance = this;
		}
	}

	public Map<String, String> getPropertiesByPrefix(String prefix) {
		Map<String, String> properties = new HashMap<String, String>();
		for (Object key: this.keySet()) {
			if (key instanceof String) {
				String keystr = (String)key;
				if (keystr.startsWith(prefix)) {
					properties.put(
							keystr,
							getProperty(keystr));
				}
			}
		}
		return properties;
	}
	
	private static final long serialVersionUID = 1L;

}
