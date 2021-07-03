package es.caib.helium.expedient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/** Classe principal per iniciar l'aplicació spring boot del micro servei d'expedients i tasques.
 * 
 * Aquest microservei conté taules de dades per expedients i tasques pels llistats d'expedients
 * i tasques a fi d'agilitzar la consulta sense haver d'accedir a la informació de tasques dins
 * del motor de flux. Les dades s'han d'anar actualitzant i informmant per part del micro servei
 * de tramitació d'helium quan es creïn, esborrin o modifiquin expedients, tasques i usuaris assignats.
 *
 */
@EnableFeignClients
@SpringBootApplication(exclude = {
		org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
		org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class}
)
public class HeliumExpedientServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HeliumExpedientServiceApplication.class, args);
		System.setProperty("oracle.jdbc.timezoneAsRegion", "false");		
	}

}