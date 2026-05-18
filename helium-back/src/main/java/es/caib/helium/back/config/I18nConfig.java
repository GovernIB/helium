/**
 *
 */
package es.caib.helium.back.config;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import es.caib.helium.back.helper.MessageHelper;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * Configuració multiidioma de l'aplicació.
 *
 * @author Límit Tecnologies
 */
@Configuration
public class I18nConfig {

	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		return localeResolver;
	}

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames(
			"classpath:helium-messages",
			"classpath:helium-messages_v3",
			"classpath:helium-errors");
		messageSource.setDefaultLocale(Locale.forLanguageTag("ca"));
		messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
		messageSource.setFallbackToSystemLocale(false);
		messageHelperInit(messageSource);
		return messageSource;
	}

	private void messageHelperInit(MessageSource messageSource) {
		MessageHelper.init().setMessageSource(messageSource);
	}

}
