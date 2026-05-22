/**
 *
 */
package es.caib.helium.commons.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.*;
import org.springframework.stereotype.Component;

/**
 * Emmagatzema les propietats globals de l'aplicació
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
@RequiredArgsConstructor
public class GlobalProperties implements ApplicationContextAware {

	private static final String SOURCE_NAME = "dynamicProperties";

	private final ConfigurableEnvironment environment;

	private static ApplicationContext applicationContext;
	public static GlobalProperties getInstance() {
		GlobalProperties gp = applicationContext.getBean(GlobalProperties.class);
		gp.environment.setIgnoreUnresolvableNestedPlaceholders(true);
		return gp;
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

	public void updateProperty(String key, String value) {
		MutablePropertySources sources = environment.getPropertySources();
		MapPropertySource targetSource;
		if (sources.contains(SOURCE_NAME)) {
			targetSource = (MapPropertySource) sources.get(SOURCE_NAME);
		} else {
			targetSource = new MapPropertySource(SOURCE_NAME, new HashMap<>());
			sources.addFirst(targetSource);
		}
		Map<String, Object> updated = new HashMap<>(targetSource.getSource());
		updated.put(key, value);
		sources.replace(SOURCE_NAME, new MapPropertySource(SOURCE_NAME, updated));
	}

	public Properties toPropertiesWithPrefix(String prefix) {
		Properties props = new Properties();
		for (PropertySource<?> ps: environment.getPropertySources()) {
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
