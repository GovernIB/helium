package es.caib.helium;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWarDeployment;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * Classe principal del api interna de helium per executar amb SpringBoot.
 *
 * @author Límit Tecnologies
 */
@SpringBootApplication
@ComponentScan
@PropertySource(
		ignoreResourceNotFound = true,
		value = { "classpath:application.properties" })
public class HeliumApiInternaBootApp {

	public static void main(String[] args) {
		SpringApplication.run(HeliumApiInternaBootApp.class, args);
	}

}
