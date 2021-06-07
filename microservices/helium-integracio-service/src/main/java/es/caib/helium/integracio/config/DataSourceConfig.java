package es.caib.helium.integracio.config;

import java.sql.SQLException;

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
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
@ConfigurationProperties(prefix = "es.caib.helium.integracio")
@ComponentScan("es.caib.helium.integracio.service.notificacio")
public class DataSourceConfig {

	@Autowired
	private Environment env;

	@Primary
	@Bean(name = "dataSourceServei")
	public DataSource dataSourceServei() {

		var dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(env.getRequiredProperty("es.caib.helium.integracio.database.jdbc.driver"));
		dataSource.setUrl(env.getRequiredProperty("es.caib.helium.integracio.database.jdbc.url"));
		dataSource.setUsername(env.getRequiredProperty("es.caib.helium.integracio.database.jdbc.user"));
		dataSource.setPassword(env.getRequiredProperty("es.caib.helium.integracio.database.jdbc.password"));
		dataSource.setSchema(env.getRequiredProperty("es.caib.helium.integracio.database.jdbc.schema"));
		return dataSource;
	}

	@Primary
	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("dataSourceServei") DataSource dataSource) {

		// TODO PER LES DUES TAULES S'HAN DE COPIAR INDEXS
		
		//TODO S'HAN DE LLISTAR LES CLASSES USADES PELS REPOSITORIS. ?¿?¿
		return builder.dataSource(dataSource).packages("es.caib.helium.integracio.domini")
				.persistenceUnit("DocumentNotificacio").persistenceUnit("PortaFirma").build();
	}

}
