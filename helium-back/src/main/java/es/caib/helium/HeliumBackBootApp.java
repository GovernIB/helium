package es.caib.helium;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * Classe principal del backoffice de helium per executar amb SpringBoot.
 *
 * @author Límit Tecnologies
 */
@SpringBootApplication
@ComponentScan
@PropertySource(
		ignoreResourceNotFound = true,
		value = { "classpath:application.properties" })
public class HeliumBackBootApp {

	public static void main(String[] args) {
		SpringApplication.run(HeliumBackBootApp.class, args);
	}

}
