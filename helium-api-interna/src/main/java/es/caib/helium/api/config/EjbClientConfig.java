package es.caib.helium.api.config;

import es.caib.helium.commons.config.BaseConfig;
import es.caib.helium.logic.intf.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ejb.access.LocalStatelessSessionProxyFactoryBean;

/**
 * Configuració d'accés als services de Spring mitjançant EJBs.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Configuration
public class EjbClientConfig {

	static final String EJB_JNDI_PREFIX = "java:app/" + BaseConfig.APP_NAME + "-ejb/";
	static final String EJB_JNDI_SUFFIX = "Bean";

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean salutService() {
		return getLocalEjbFactoyBean(SalutService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean logService() {
		return getLocalEjbFactoyBean(LogService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean estadisticaService() {
		return getLocalEjbFactoyBean(EstadisticaService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean adminService() {
		return getLocalEjbFactoyBean(AdminService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean expedientDocumentService() {
		return getLocalEjbFactoyBean(ExpedientDocumentService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean anotacioService() {
		return getLocalEjbFactoyBean(AnotacioService.class);
	}

	private LocalStatelessSessionProxyFactoryBean getLocalEjbFactoyBean(Class<?> serviceClass) {
		String jndiName = jndiServiceName(serviceClass, false);
		log.info("Creating EJB proxy for " + serviceClass.getSimpleName() + " with JNDI name " + jndiName);
		LocalStatelessSessionProxyFactoryBean factoryBean = new LocalStatelessSessionProxyFactoryBean();
		factoryBean.setBusinessInterface(serviceClass);
		factoryBean.setExpectedType(serviceClass);
		factoryBean.setJndiName(jndiName);
		return factoryBean;
	}

	private String jndiServiceName(Class<?> serviceClass, boolean addServiceClassName) {
		return EJB_JNDI_PREFIX + serviceClass.getSimpleName() + EJB_JNDI_SUFFIX + (addServiceClassName ? "!" + serviceClass.getName() : "");
	}

}
