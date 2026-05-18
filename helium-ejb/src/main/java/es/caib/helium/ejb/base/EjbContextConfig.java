package es.caib.helium.ejb.base;

import es.caib.helium.commons.config.BaseConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.metrics.jersey.JerseyServerMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

/**
 * Creació del context Spring per a la capa dels EJBs.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@EnableAutoConfiguration(exclude = {
		FreeMarkerAutoConfiguration.class,
		JerseyServerMetricsAutoConfiguration.class
})
@ComponentScan({
		BaseConfig.BASE_PACKAGE + ".persistence",
		BaseConfig.BASE_PACKAGE + ".commons",
		BaseConfig.BASE_PACKAGE + ".logic",
})
//@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
@PropertySource(ignoreResourceNotFound = true, value = {
		"classpath:application.properties",
		"file://${" + BaseConfig.APP_PROPERTIES + "}",
		"file://${" + BaseConfig.APP_SYSTEM_PROPERTIES + "}"})
public class EjbContextConfig {

	private static boolean initialized;
	private static ApplicationContext applicationContext;

	public static ApplicationContext getApplicationContext() {
		if (!initialized) {
			initialized = true;
			log.info("Starting EJB spring application...");
			applicationContext = new AnnotationConfigApplicationContext(EjbContextConfig.class);
			log.info("...EJB spring application started.");
		}
		return applicationContext;
	}

	@Bean
	public LocaleResolver localeResolver() {
		AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
		localeResolver.setSupportedLocales(
				Arrays.asList(
						Locale.forLanguageTag("ca"),
						Locale.forLanguageTag("es")));
		localeResolver.setDefaultLocale(Locale.forLanguageTag("ca"));
		return localeResolver;
	}

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:" + BaseConfig.APP_NAME + "-messages");
		messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
		messageSource.setFallbackToSystemLocale(false);
		return messageSource;
	}

}
