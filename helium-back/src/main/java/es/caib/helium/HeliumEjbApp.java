/**
 * 
 */
package es.caib.helium;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Aplicació Spring Boot de HELIUM per a ser executada des de JBoss.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class, 
		DataSourceTransactionManagerAutoConfiguration.class,
		JpaRepositoriesAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class,
		TransactionAutoConfiguration.class,
		FreeMarkerAutoConfiguration.class,
		WebSocketServletAutoConfiguration.class
})
@ComponentScan(
		excludeFilters =
				@ComponentScan.Filter(
				type = FilterType.REGEX,
				pattern = {
						"es\\.caib\\.helium\\.logic\\..*",
						"es\\.caib\\.helium\\.persist\\..*",
						"es\\.caib\\.helium\\.ejb\\..*"})
)
@EnableFeignClients(
		basePackages = {"es.caib.helium.client"}
)
@EnableScheduling
public class HeliumEjbApp extends HeliumApp {

	public static void main(String[] args) {
		SpringApplication.run(HeliumEjbApp.class, args);
	}

}
