/**
 * 
 */
package net.conselldemallorca.helium.util;

import java.io.IOException;
import java.util.Properties;

import net.conselldemallorca.helium.model.exception.InitializationException;

import org.springframework.core.io.Resource;

/**
 * Emmagatzema les propietats globals de l'aplicació
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class GlobalProperties extends Properties {

	private static GlobalProperties instance = null;

	public static GlobalProperties getInstance() {
		return instance;
	}

	public GlobalProperties(Resource resource) throws IOException {
		super();
		try {
			super.load(resource.getInputStream());
			if (instance == null) {
				instance = this;
			}
		} catch (Exception ex) {
			throw new InitializationException("No s'han pogut carregar les propietats globals de l'aplicació");
		}
		
	}

	private static final long serialVersionUID = 1L;

}
