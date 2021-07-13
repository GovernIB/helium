/**
 * 
 */
package es.caib.helium.ejb;

import es.caib.helium.logic.intf.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * Creació del context Spring per a la capa dels EJBs.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Configuration
@EnableAutoConfiguration(exclude = {
		FreeMarkerAutoConfiguration.class
})
@EnableJpaRepositories({ "es.caib.helium.persist" })
@ComponentScan({ "es.caib.helium.logic", "es.caib.helium.persist" })
@PropertySource(ignoreResourceNotFound = true, value = {
		"classpath:application.properties",
		"file://${" + Constants.APP_PROPERTIES + "}",
		"file://${" + Constants.APP_SYSTEM_PROPERTIES + "}"})
public class EjbContextConfig {

	@Value("${spring.datasource.jndi-name:java:jboss/datasources/heliumDS}")
	private String dataSourceJndiName;
	@Value("${spring.jpa.properties.hibernate.dialect}")
	private String hibernateDialect;
	@Value("${spring.jpa.hibernate.ddl-auto}")
	private String hibernateDdlAuto;
	@Value("${spring.jpa.show-sql}")
	private String showSql;
	@Value("${spring.jpa.properties.hibernate.format_sql}")
	private String formatSql;

	private static boolean initialized;
	private static ApplicationContext applicationContext;

	public static ApplicationContext getApplicationContext() {
		if (!initialized) {
			initialized = true;
			log.info("Starting EJB spring application..");
			applicationContext = new AnnotationConfigApplicationContext(EjbContextConfig.class);
			log.info("...EJB spring application started.");
		}
		return applicationContext;
	}

	@Bean
	public AbstractEntityManagerFactoryBean entityManagerFactory() throws NamingException {
		log.debug("Creating EntityManagerFactory " + dataSource().getClass() + "...");
		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		if (isJBoss()) {
			entityManagerFactoryBean.setJtaDataSource(dataSource());
		} else {
			entityManagerFactoryBean.setDataSource(dataSource());
		}
		entityManagerFactoryBean.setDataSource(dataSource());
		entityManagerFactoryBean.setPackagesToScan("es.caib.helium.persist");
		entityManagerFactoryBean.setJpaDialect(new HibernateJpaDialect());
		entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		Properties jpaProperties = new Properties();
		jpaProperties.setProperty(
				"hibernate.dialect",
				hibernateDialect);
		jpaProperties.setProperty(
				"hibernate.hbm2ddl.auto",
				hibernateDdlAuto);
		jpaProperties.setProperty(
				"hibernate.show_sql",
				showSql);
		jpaProperties.setProperty(
				"hibernate.format_sql",
				formatSql);
		if (isJBoss()) {
			jpaProperties.setProperty(
					"hibernate.transaction.manager_lookup_class",
					"org.hibernate.transaction.JBossTransactionManagerLookup");
		}
		entityManagerFactoryBean.setJpaProperties(jpaProperties);
		log.debug("...EntityManagerFactory successfully created.");
		return entityManagerFactoryBean;
	}

	@Bean
	public DataSource dataSource() {
		log.debug("Retrieving DataSource...");
		JndiDataSourceLookup lookup = new JndiDataSourceLookup();
		DataSource dataSource = lookup.getDataSource(dataSourceJndiName);
		log.debug("...DataSource successfully retrieved.");
		return dataSource;
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		log.debug("Creating TransactionManager...");
		PlatformTransactionManager transactionManager;
		if (isJBoss()) {
			JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
			jtaTransactionManager.setTransactionManagerName("java:/TransactionManager");
			//jtaTransactionManager.setUserTransactionName("java:jboss/UserTransaction");
			transactionManager = jtaTransactionManager;
		} else {
			JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
			jpaTransactionManager.setEntityManagerFactory(emf);
			transactionManager = jpaTransactionManager;
		}
		log.debug("...TransactionManager successfully created.");
		return transactionManager;
	}

	private boolean isJBoss() {
		return true;
	}

}
