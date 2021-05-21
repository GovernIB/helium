package es.caib.helium.integracio.config.portafirmes;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
@ConfigurationProperties(prefix = "es.caib.helium.integracio")
@ComponentScan("es.caib.helium.integracio.service")
public class PortafirmesConfig {
	
	@Autowired
	private Environment env;
	
	@Primary
	@Bean(name = "dataSourcePortaFirmes")
//	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSourcePortaFirmes() {

		var dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(env.getRequiredProperty("es.caib.helium.integracio.portafirmes.jdbc.driver"));
		dataSource.setUrl(env.getRequiredProperty("es.caib.helium.integracio.portafirmes.jdbc.url"));
		dataSource.setUsername(env.getRequiredProperty("es.caib.helium.integracio.portafirmes.jdbc.user"));
		dataSource.setPassword(env.getRequiredProperty("es.caib.helium.integracio.portafirmes.jdbc.password"));
		return dataSource;
	}

//	@Primary
	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("dataSourcePortaFirmes") DataSource dataSource) {

		return builder.dataSource(dataSource).packages("es.caib.helium.integracio.domini.portafirmes")
				.persistenceUnit("PortaFirma").build();
	}


}
