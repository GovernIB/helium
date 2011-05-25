/**
 * 
 */
package net.conselldemallorca.helium.core.util;

import java.io.IOException;
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

	private static final long serialVersionUID = 1L;

}
