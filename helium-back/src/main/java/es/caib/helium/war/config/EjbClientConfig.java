/**
 * 
 */
package es.caib.helium.war.config;

import es.caib.helium.logic.intf.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ejb.access.LocalStatelessSessionProxyFactoryBean;

/**
 * Configuració d'accés als services de Spring mitjançant EJBs.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Configuration
public class EjbClientConfig {

	private static final String EJB_JNDI_PREFIX = "java:app/helium-ejb/";
	private static final String EJB_JNDI_SUFFIX = "";

	@Bean
	public LocalStatelessSessionProxyFactoryBean accioService() {
		return getLocalEjbFactoyBean(AccioService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean adminService() {
		return getLocalEjbFactoyBean(AdminService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean alertaService() {
		return getLocalEjbFactoyBean(AlertaService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean anotacioService() {
		return getLocalEjbFactoyBean(AnotacioService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean aplicacioService() {
		return getLocalEjbFactoyBean(AplicacioService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean areaService() {
		return getLocalEjbFactoyBean(AreaService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean campService() {
		return getLocalEjbFactoyBean(CampService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean carrecService() {
		return getLocalEjbFactoyBean(CarrecService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean definicioProcesService() {
		return getLocalEjbFactoyBean(DefinicioProcesService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean dissenyService() {
		return getLocalEjbFactoyBean(DissenyService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean documentService() {
		return getLocalEjbFactoyBean(DocumentService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean dominiService() {
		return getLocalEjbFactoyBean(DominiService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean entornAreaMembreService() {
		return getLocalEjbFactoyBean(EntornAreaMembreService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean entornAreaService() {
		return getLocalEjbFactoyBean(EntornAreaService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean entornCarrecService() {
		return getLocalEjbFactoyBean(EntornCarrecService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean entornService() {
		return getLocalEjbFactoyBean(EntornService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean entornTipusAreaService() {
		return getLocalEjbFactoyBean(EntornTipusAreaService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean enumeracioService() {
		return getLocalEjbFactoyBean(EnumeracioService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean execucioMassivaService() {
		return getLocalEjbFactoyBean(ExecucioMassivaService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean exempleService() {
		return getLocalEjbFactoyBean(ExempleService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean expedientDadaService() {
		return getLocalEjbFactoyBean(ExpedientDadaService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean expedientDocumentService() {
		return getLocalEjbFactoyBean(ExpedientDocumentService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean expedientInteressatService() {
		return getLocalEjbFactoyBean(ExpedientInteressatService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean expedientRegistreService() {
		return getLocalEjbFactoyBean(ExpedientRegistreService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean expedientReindexacioService() {
		return getLocalEjbFactoyBean(ExpedientReindexacioService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean expedientService() {
		return getLocalEjbFactoyBean(ExpedientService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean expedientTascaService() {
		return getLocalEjbFactoyBean(ExpedientTascaService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean expedientTerminiService() {
		return getLocalEjbFactoyBean(ExpedientTerminiService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean expedientTipusService() {
		return getLocalEjbFactoyBean(ExpedientTipusService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean expedientTokenService() {
		return getLocalEjbFactoyBean(ExpedientTokenService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean permisService() {
		return getLocalEjbFactoyBean(PermisService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean portasignaturesService() {
		return getLocalEjbFactoyBean(PortasignaturesService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean reproService() {
		return getLocalEjbFactoyBean(ReproService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean tascaProgramadaService() {
		return getLocalEjbFactoyBean(TascaProgramadaService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean tascaService() {
		return getLocalEjbFactoyBean(TascaService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean terminiService() {
		return getLocalEjbFactoyBean(TerminiService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean validacioService() {
		return getLocalEjbFactoyBean(ValidacioService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean workflowBridgeService() {
		return getLocalEjbFactoyBean(WorkflowBridgeService.class);
	}


	
	private LocalStatelessSessionProxyFactoryBean getLocalEjbFactoyBean(Class<?> serviceClass) {
		String jndiName = EJB_JNDI_PREFIX + serviceClass.getSimpleName() + EJB_JNDI_SUFFIX;
		log.debug("Creating EJB proxy for serviceClass with JNDI name " + jndiName);
		LocalStatelessSessionProxyFactoryBean factory = new LocalStatelessSessionProxyFactoryBean();
		factory.setBusinessInterface(serviceClass);
		factory.setJndiName(jndiName);
		return factory;
	}

}
