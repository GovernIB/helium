package es.caib.helium.integracio.config.persones;

import es.caib.helium.integracio.excepcions.ServeisExternsException;
import es.caib.helium.integracio.service.persones.PersonaService;
import es.caib.helium.integracio.service.persones.PersonaServiceLdapImpl;
import org.fundaciobit.pluginsib.userinformation.keycloak.KeyCloakUserInformationPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ConfigurationProperties(prefix = "es.caib.helium.integracio")
@ComponentScan("es.caib.helium.integracio.service")
public class PersonesConfig {
	
	@Autowired
	private Environment env;
	
	@Bean(name = "seyconDataSource")
	public DataSource seyconDataSource() {

		var dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(env.getRequiredProperty("es.caib.helium.integracio.persones.jdbc.driver"));
		dataSource.setUrl(env.getRequiredProperty("es.caib.helium.integracio.persones.jdbc.url"));
		dataSource.setUsername(env.getRequiredProperty("es.caib.helium.integracio.persones.jdbc.user"));
		dataSource.setPassword(env.getRequiredProperty("es.caib.helium.integracio.persones.jdbc.password"));
		return dataSource;
	}

	@Bean(name = "jdbcSeycon")
	public JdbcTemplate jdbcSeycon(@Qualifier("seyconDataSource") DataSource ds) {
		return new JdbcTemplate(ds);
	}
	
	@Bean(name = "personaService")
	public PersonaService instanciarService() throws ServeisExternsException {
		
		String pluginClass = env.getRequiredProperty("es.caib.helium.integracio.persones.class");
		try {
			var personaService = (PersonaService) Class.forName(pluginClass).getConstructor().newInstance();
			if (personaService instanceof PersonaServiceLdapImpl) {
				((PersonaServiceLdapImpl) personaService).setUserFilter(env.getRequiredProperty("es.caib.helium.integracio.persones.plugin.ldap.search.filter.user"));
				((PersonaServiceLdapImpl) personaService).setLikeFilter(env.getRequiredProperty("es.caib.helium.integracio.persones.plugin.ldap.search.filter.like"));
			}
			return personaService;
		} catch (Exception ex) {
			throw new ServeisExternsException("Error al crear la instància del plugin de persones (" + "pluginClass=" + pluginClass + ")");
		}
	}
	
	@Bean
	public LdapContextSource contextSource() {
		
	    LdapContextSource contextSource = new LdapContextSource();
	    contextSource.setUrl(env.getRequiredProperty("es.caib.helium.integracio.persones.plugin.ldap.url"));
	    contextSource.setBase(env.getRequiredProperty("es.caib.helium.integracio.persones.plugin.ldap.search.base"));
	    contextSource.setUserDn(env.getRequiredProperty("es.caib.helium.integracio.persones.plugin.ldap.principal"));
	    contextSource.setPassword(env.getRequiredProperty("es.caib.helium.integracio.persones.plugin.ldap.credentials"));
	    return contextSource;
	}
	
	@Bean(name = "ldapPersones")
	public LdapTemplate ldapPersones() {
	    return new LdapTemplate(contextSource());
	}

	@Bean(name = "goibPersones")
	public KeyCloakUserInformationPlugin goibPersones() {

		var properties = "";
		var props = new Properties();
		props.setProperty(env.getRequiredProperty("es.caib.helium.integracio.persones.plugin.goib.url.property"),
				env.getRequiredProperty("es.caib.helium.integracio.persones.plugin.goib.url"));
		props.setProperty(env.getRequiredProperty("es.caib.helium.integracio.persones.plugin.goib.realm.property"),
				env.getRequiredProperty("es.caib.helium.integracio.persones.plugin.goib.realm"));
		props.setProperty(env.getRequiredProperty("es.caib.helium.integracio.persones.plugin.goib.client.property"),
				env.getRequiredProperty("es.caib.helium.integracio.persones.plugin.goib.client"));
		props.setProperty(env.getRequiredProperty("es.caib.helium.integracio.persones.plugin.goib.secret.property"),
				env.getRequiredProperty("es.caib.helium.integracio.persones.plugin.goib.secret"));
		var plugin = new KeyCloakUserInformationPlugin(properties, props);
		return plugin;
	}

}
