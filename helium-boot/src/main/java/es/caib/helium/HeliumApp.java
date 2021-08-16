/**
 * 
 */
package es.caib.helium;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Aplicació Spring Boot de HELIUM per iniciar amb Spring Boot.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@SpringBootApplication
@EnableScheduling
public class HeliumApp {

	public static void main(String[] args) {
		new SpringApplicationBuilder(HeliumApp.class).run();
	}

}
