/**
 * 
 */
package es.caib.helium;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicació Spring Boot de HELIUM per a ser executada des de Tomcat.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@SpringBootApplication
public class HeliumServletApp extends HeliumApp {

	public static void main(String[] args) {
		SpringApplication.run(HeliumServletApp.class, args);
	}

}
