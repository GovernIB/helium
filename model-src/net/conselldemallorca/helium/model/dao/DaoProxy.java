/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import net.conselldemallorca.helium.jbpm3.integracio.JbpmDao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Proxy per accedir als DAOs des de classes del jBPM
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class DaoProxy implements ApplicationContextAware {

	private static final DaoProxy _inst = new DaoProxy();
	private ApplicationContext ctx;



	public static DaoProxy getInstance() {
		if (_inst == null)
			throw new RuntimeException("Application context not initialized!");
		return _inst;
	}
	public void setApplicationContext(ApplicationContext appCtx) {
		this.ctx = appCtx;
	}

	public EntornDao getEntornDao() {
		return (EntornDao)ctx.getBean("entornDao", EntornDao.class);
	}
	public PluginPersonaDao getPluginPersonaDao() {
		return (PluginPersonaDao)ctx.getBean("pluginPersonaDao", PluginPersonaDao.class);
	}
	public AreaDao getAreaDao() {
		return (AreaDao)ctx.getBean("areaDao", AreaDao.class);
	}
	public CarrecDao getCarrecDao() {
		return (CarrecDao)ctx.getBean("carrecDao", CarrecDao.class);
	}
	public ExpedientTipusDao getExpedientTipusDao() {
		return (ExpedientTipusDao)ctx.getBean("expedientTipusDao", ExpedientTipusDao.class);
	}
	public TascaDao getTascaDao() {
		return (TascaDao)ctx.getBean("tascaDao", TascaDao.class);
	}
	public DominiDao getDominiDao() {
		return (DominiDao)ctx.getBean("dominiDao", DominiDao.class);
	}
	public DocumentStoreDao getDocumentStoreDao() {
		return (DocumentStoreDao)ctx.getBean("documentStoreDao", DocumentStoreDao.class);
	}
	public MailDao getMailDao() {
		return (MailDao)ctx.getBean("mailDao", MailDao.class);
	}
	public FestiuDao getFestiuDao() {
		return (FestiuDao)ctx.getBean("festiuDao", FestiuDao.class);
	}
	public SistraDao getSistraDao() {
		return (SistraDao)ctx.getBean("sistraDao", SistraDao.class);
	}
	public JbpmDao getJbpmDao() {
		return (JbpmDao)ctx.getBean("jbpmDao", JbpmDao.class);
	}
	public PluginRegistreDao getPluginRegistreDao() {
		return (PluginRegistreDao)ctx.getBean("pluginRegistreDao", PluginRegistreDao.class);
	}
	public ReassignacioDao getReassignacioDao() {
		return (ReassignacioDao)ctx.getBean("reassignacioDao", ReassignacioDao.class);
	}

}
