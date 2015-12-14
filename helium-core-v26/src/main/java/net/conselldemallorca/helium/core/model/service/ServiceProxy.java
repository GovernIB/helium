/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Proxy per accedir als serveis des de classes del jBPM
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServiceProxy implements ApplicationContextAware {

	private static final ServiceProxy _inst = new ServiceProxy();
	private ApplicationContext ctx;



	public static ServiceProxy getInstance() {
		if (_inst == null)
			throw new RuntimeException("Application context not initialized!");
		return _inst;
	}
	public void setApplicationContext(ApplicationContext appCtx) {
		this.ctx = appCtx;
	}

	public DissenyService getDissenyService() {
		return (DissenyService)ctx.getBean("dissenyService", DissenyService.class);
	}
	public EntornService getEntornService() {
		return (EntornService)ctx.getBean("entornService", EntornService.class);
	}
	public ExpedientService getExpedientService() {
		return (ExpedientService)ctx.getBean("expedientService", ExpedientService.class);
	}
	public DocumentService getDocumentService() {
		return (DocumentService)ctx.getBean("documentService", DocumentService.class);
	}
	public OrganitzacioService getOrganitzacioService() {
		return (OrganitzacioService)ctx.getBean("organitzacioService", OrganitzacioService.class);
	}
	public PermissionService getPermissionService() {
		return (PermissionService)ctx.getBean("permissionService", PermissionService.class);
	}
	public PersonaService getPersonaService() {
		return (PersonaService)ctx.getBean("personaService", PersonaService.class);
	}
	public PluginService getPluginService() {
		return (PluginService)ctx.getBean("pluginService", PluginService.class);
	}
	public TascaService getTascaService() {
		return (TascaService)ctx.getBean("tascaService", TascaService.class);
	}
	public TerminiService getTerminiService() {
		return (TerminiService)ctx.getBean("terminiService", TerminiService.class);
	}
	public AlertaService getAlertaService() {
		return (AlertaService)ctx.getBean("alertaService", AlertaService.class);
	}
	public DtoConverter getDtoConverter() {
		return (DtoConverter)ctx.getBean("dtoConverter", DtoConverter.class);
	}

}
