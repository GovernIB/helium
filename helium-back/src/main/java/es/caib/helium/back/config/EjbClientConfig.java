/**
 *
 */
package es.caib.helium.back.config;

import es.caib.helium.logic.intf.service.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ejb.access.LocalStatelessSessionProxyFactoryBean;

import es.caib.helium.commons.config.BaseConfig;
import lombok.extern.slf4j.Slf4j;

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
	public LocalStatelessSessionProxyFactoryBean entornService() {
		return getLocalEjbFactoyBean(EntornService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean accioService() {
		return getLocalEjbFactoyBean(AccioService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean adminService() {
		return getLocalEjbFactoyBean(AdminService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean alertaService() {
		return getLocalEjbFactoyBean(AlertaService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean anotacioService() {
		return getLocalEjbFactoyBean(AnotacioService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean aplicacioService() {
		return getLocalEjbFactoyBean(AplicacioService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean areaService() {
		return getLocalEjbFactoyBean(AreaService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean avisService() {
		return getLocalEjbFactoyBean(AvisService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean campService() {
		return getLocalEjbFactoyBean(CampService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean carrecService() {
		return getLocalEjbFactoyBean(CarrecService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean consultaPinbalService() {
		return getLocalEjbFactoyBean(ConsultaPinbalService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean dadesExternesService() {
		return getLocalEjbFactoyBean(DadesExternesService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean definicioProcesService() {
		return getLocalEjbFactoyBean(DefinicioProcesService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean dissenyService() {
		return getLocalEjbFactoyBean(DissenyService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean documentService() {
		return getLocalEjbFactoyBean(DocumentService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean dominiService() {
		return getLocalEjbFactoyBean(DominiService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean entornAreaMembreService() {
		return getLocalEjbFactoyBean(EntornAreaMembreService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean entornAreaService() {
		return getLocalEjbFactoyBean(EntornAreaService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean entornCarrecService() {
		return getLocalEjbFactoyBean(EntornCarrecService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean entornTipusAreaService() {
		return getLocalEjbFactoyBean(EntornTipusAreaService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean enumeracioService() {
		return getLocalEjbFactoyBean(EnumeracioService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean estadisticaService() {
		return getLocalEjbFactoyBean(EstadisticaService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean execucioMassivaService() {
		return getLocalEjbFactoyBean(ExecucioMassivaService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean expedientDadaService() {
		return getLocalEjbFactoyBean(ExpedientDadaService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean expedientDocumentService() {
		return getLocalEjbFactoyBean(ExpedientDocumentService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean expedientInteressatService() {
		return getLocalEjbFactoyBean(ExpedientInteressatService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean expedientRegistreService() {
		return getLocalEjbFactoyBean(ExpedientRegistreService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean expedientService() {
		return getLocalEjbFactoyBean(ExpedientService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean expedientTascaService() {
		return getLocalEjbFactoyBean(ExpedientTascaService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean expedientTerminiService() {
		return getLocalEjbFactoyBean(ExpedientTerminiService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean expedientTipusService() {
		return getLocalEjbFactoyBean(ExpedientTipusService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean expedientTokenService() {
		return getLocalEjbFactoyBean(ExpedientTokenService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean logService() {
		return getLocalEjbFactoyBean(LogService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean monitorTasquesService() {
		return getLocalEjbFactoyBean(MonitorTasquesService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean notificacioService() {
		return getLocalEjbFactoyBean(NotificacioService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean parametreService() {
		return getLocalEjbFactoyBean(ParametreService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean portafirmesFluxService() {
		return getLocalEjbFactoyBean(PortafirmesFluxService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean portasignaturesService() {
		return getLocalEjbFactoyBean(PortasignaturesService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean procedimentService() {
		return getLocalEjbFactoyBean(ProcedimentService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean reproService() {
		return getLocalEjbFactoyBean(ReproService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean salutService() {
		return getLocalEjbFactoyBean(SalutService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean tascaProgramadaService() {
		return getLocalEjbFactoyBean(TascaProgramadaService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean tascaService() {
		return getLocalEjbFactoyBean(TascaService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean terminiService() {
		return getLocalEjbFactoyBean(TerminiService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean unitatOrganitzativaService() {
		return getLocalEjbFactoyBean(UnitatOrganitzativaService.class);
	}
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean validacioService() {
		return getLocalEjbFactoyBean(ValidacioService.class);
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
