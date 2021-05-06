package es.caib.helium.integracio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication(exclude = {
		org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
		org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class}
)
public class HeliumIntegracioServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HeliumIntegracioServiceApplication.class, args);
		System.setProperty("oracle.jdbc.timezoneAsRegion", "false");
	}

}
