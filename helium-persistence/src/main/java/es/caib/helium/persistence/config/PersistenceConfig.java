package es.caib.helium.persistence.config;

import es.caib.helium.commons.config.BaseConfig;
import es.caib.helium.commons.config.PropertyConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuració dels components de persistència.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Configuration
@EnableJpaRepositories(
		basePackages = { BaseConfig.BASE_PACKAGE + ".persistence.repository" },
		entityManagerFactoryRef = "mainEntityManager",
		transactionManagerRef = "mainTransactionManager"
)
public class PersistenceConfig {

	@Value("${spring.jpa.hibernate.ddl-auto:#{null}}")
	private String hibernateDdlAuto;
	@Value("${" + PropertyConfig.PERSISTENCE_CONTAINER_TRANSACTIONS_DISABLED + ":false}")
	private boolean containerTransactionsDisabled;

	@Bean
	@ConfigurationProperties("spring.jpa.properties")
	public JpaHibernateProperties jpaHibernateProperties() {
		return new JpaHibernateProperties();
	}

	@Bean
	@Primary
	@ConfigurationProperties("spring.datasource")
	public DataSourceProperties mainDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@Primary
	public DataSource mainDataSource() {
		log.debug("Creating main DataSource...");
		DataSourceProperties dataSourceProps = mainDataSourceProperties();
		String jndiName = dataSourceProps.getJndiName();
		if (log.isDebugEnabled()) {
			String url = dataSourceProps.getUrl();
			if (jndiName != null) {
				log.debug("\tDatasource properties:\n" +
						"\t\tjndiName=" + jndiName);
			} else if (url != null) {
				String username = dataSourceProps.getUsername();
				String password = dataSourceProps.getPassword();
				log.debug("\tDatasource properties:\n" +
						"\t\turl=" + url + "\n" +
						"\t\tusername=" + username + "\n" +
						(password != null ? "\t\tpassword=******" : ""));
			} else {
				log.debug("\tDatasource with no properties defined");
			}
		}
		DataSource dataSource;
		if (jndiName != null) {
			JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
			dataSource = dataSourceLookup.getDataSource(jndiName);
		} else {
			dataSource = dataSourceProps.
					initializeDataSourceBuilder().
					build();
		}
		log.debug("...main DataSource successfully created.");
		return dataSource;
	}

	@Bean
	@Primary
	public LocalContainerEntityManagerFactoryBean mainEntityManager(EntityManagerFactoryBuilder builder) {
		log.debug("Creating main EntityManagerFactory...");
		Map<String, String> properties = new HashMap<String, String>();
		Map<String, String> hibernateProperties = jpaHibernateProperties().hibernate;
		if (hibernateProperties != null) {
			jpaHibernateProperties().hibernate.forEach((k, v) -> {
				properties.put("hibernate." + k, v);
				log.debug("\t- Property hibernate." + k + "=" + v);
			});
		}
		String hibernateHbm2ddlAutoValue = properties.get("hibernate.hbm2ddl.auto");
		if (hibernateHbm2ddlAutoValue == null) {
			hibernateHbm2ddlAutoValue = (hibernateDdlAuto != null) ? hibernateDdlAuto : "none";
			properties.put("hibernate.hbm2ddl.auto", hibernateHbm2ddlAutoValue);
			log.debug("\t- Property hibernate.hbm2ddl.auto=" + hibernateHbm2ddlAutoValue);
		}
		LocalContainerEntityManagerFactoryBean entityManager = builder.
				dataSource(mainDataSource()).
				persistenceUnit(getPersistenceUnitName()).
				packages(getEntityPackages()).
				properties(properties).
				jta(isJboss()).
				build();
		log.debug("...main EntityManagerFactory successfully created.");
		return entityManager;
	}

	@Bean
	@Primary
	public TransactionManager mainTransactionManager(EntityManagerFactory entityManagerFactory) {
		log.debug("Creating main TransactionManager...");
		PlatformTransactionManager transactionManager;
		if (!containerTransactionsDisabled && isJboss()) {
			JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
			jtaTransactionManager.setTransactionManagerName("java:/TransactionManager");
			jtaTransactionManager.setAllowCustomIsolationLevels(true);
			transactionManager = jtaTransactionManager;
		} else {
			JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
			jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
			transactionManager = jpaTransactionManager;
		}
		log.debug("...main TransactionManager successfully created.");
		return transactionManager;
	}

	@Value("${jboss.home.dir:#{null}}")
	private String jbossHomeDir;
	private boolean isJboss() {
		return jbossHomeDir != null;
	}

	protected String getPersistenceUnitName() {
		return "main";
	}

	protected String getEntityPackages() {
		return BaseConfig.BASE_PACKAGE + ".persistence.entity";
	}

	@Getter
	@Setter
	public static class JpaHibernateProperties {
		private Map<String, String> hibernate;
	}

}
