package es.caib.helium.back.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import es.caib.helium.back.helper.MessageHelper;

@Component
public class MessageConfig {
	@Autowired
	private MessageSource messageSource;
	
	@PostConstruct
	public void initialize() {
		MessageHelper.init().setMessageSource(messageSource);
	}
}
