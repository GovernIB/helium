/**
 *
 */
package es.caib.helium.commons.utils;

import java.util.Properties;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Emmagatzema les propietats globals de l'aplicació
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
@RequiredArgsConstructor
public class GlobalProperties implements ApplicationContextAware {

	private final Environment environment;

	private static ApplicationContext applicationContext;
	public static GlobalProperties getInstance() {
		return applicationContext.getBean(GlobalProperties.class);
	}

	public String getProperty(String key) {
		return environment.getProperty(key);
	}

	public String getProperty(String key, String defaultValue) {
		return environment.getProperty(key, defaultValue);
	}

	public Integer getPropertyAsInteger(String key) {
		String value = environment.getProperty(key);
		return value != null ? Integer.valueOf(getProperty(key)) : null;
	}

	public Properties toPropertiesWithPrefix(String prefix) {
		if (!(environment instanceof ConfigurableEnvironment)) {
			throw new IllegalArgumentException("Environment is not of type ConfigurableEnvironment");
		}
		Properties props = new Properties();
		for (PropertySource<?> ps: ((ConfigurableEnvironment)environment).getPropertySources()) {
			if (ps instanceof EnumerablePropertySource<?>) {
				EnumerablePropertySource<?> eps = (EnumerablePropertySource<?>)ps;
				for (String name: eps.getPropertyNames()) {
					if (prefix == null || name.startsWith(prefix)) {
						Object value = eps.getProperty(name);
						if (value != null) {
							props.put(name, value.toString());
						}
					}
				}
			}
		}
		return props;
	}

	public Properties toProperties() {
		return toPropertiesWithPrefix(null);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		GlobalProperties.applicationContext = applicationContext;
	}

}
