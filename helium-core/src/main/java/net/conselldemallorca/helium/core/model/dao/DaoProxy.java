/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import net.conselldemallorca.helium.jbpm3.integracio.JbpmDao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Proxy per accedir als DAOs des de classes del jBPM
 * 
 * @author Limit Tecnologies <limit@limit.es>
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
		waitContext();
		if (ctx == null) return null;
		return (EntornDao)ctx.getBean("entornDao", EntornDao.class);
	}
	public DefinicioProcesDao getDefinicioProcesDao() {
		waitContext();
		if (ctx == null) return null;
		return (DefinicioProcesDao)ctx.getBean("definicioProcesDao", DefinicioProcesDao.class);
	}
	public CampDao getCampDao() {
		waitContext();
		if (ctx == null) return null;
		return (CampDao)ctx.getBean("campDao", CampDao.class);
	}
	public PluginPersonaDao getPluginPersonaDao() {
		waitContext();
		if (ctx == null) return null;
		return (PluginPersonaDao)ctx.getBean("pluginPersonaDao", PluginPersonaDao.class);
	}
	public AreaDao getAreaDao() {
		waitContext();
		if (ctx == null) return null;
		return (AreaDao)ctx.getBean("areaDao", AreaDao.class);
	}
	public CarrecDao getCarrecDao() {
		waitContext();
		if (ctx == null) return null;
		return (CarrecDao)ctx.getBean("carrecDao", CarrecDao.class);
	}
	public ExpedientTipusDao getExpedientTipusDao() {
		waitContext();
		if (ctx == null) return null;
		return (ExpedientTipusDao)ctx.getBean("expedientTipusDao", ExpedientTipusDao.class);
	}
	public TascaDao getTascaDao() {
		waitContext();
		if (ctx == null) return null;
		return (TascaDao)ctx.getBean("tascaDao", TascaDao.class);
	}
	public DominiDao getDominiDao() {
		waitContext();
		if (ctx == null) return null;
		return (DominiDao)ctx.getBean("dominiDao", DominiDao.class);
	}
	public EnumeracioDao getEnumeracioDao() {
		waitContext();
		if (ctx == null) return null;
		return (EnumeracioDao)ctx.getBean("enumeracioDao", EnumeracioDao.class);
	}
	public DocumentStoreDao getDocumentStoreDao() {
		waitContext();
		if (ctx == null) return null;
		return (DocumentStoreDao)ctx.getBean("documentStoreDao", DocumentStoreDao.class);
	}
	public MailDao getMailDao() {
		waitContext();
		if (ctx == null) return null;
		return (MailDao)ctx.getBean("mailDao", MailDao.class);
	}
	public FestiuDao getFestiuDao() {
		waitContext();
		if (ctx == null) return null;
		return (FestiuDao)ctx.getBean("festiuDao", FestiuDao.class);
	}
	public JbpmDao getJbpmDao() {
		waitContext();
		if (ctx == null) return null;
		return (JbpmDao)ctx.getBean("jbpmDao", JbpmDao.class);
	}
	public PluginRegistreDao getPluginRegistreDao() {
		waitContext();
		if (ctx == null) return null;
		return (PluginRegistreDao)ctx.getBean("pluginRegistreDao", PluginRegistreDao.class);
	}
	public PluginTramitacioDao getPluginTramitacioDao() {
		waitContext();
		if (ctx == null) return null;
		return (PluginTramitacioDao)ctx.getBean("pluginTramitacioDao", PluginTramitacioDao.class);
	}
	public PluginGestioDocumentalDao getPluginGestioDocumentalDao() {
		waitContext();
		if (ctx == null) return null;
		return (PluginGestioDocumentalDao)ctx.getBean("pluginGestioDocumentalDao", PluginGestioDocumentalDao.class);
	}
	public ReassignacioDao getReassignacioDao() {
		waitContext();
		if (ctx == null) return null;
		return (ReassignacioDao)ctx.getBean("reassignacioDao", ReassignacioDao.class);
	}

	private void waitContext() {
		while (ctx == null){
			System.out.println(">>>>>DaoProxy ctx == null. Retry (" + System.currentTimeMillis() + ")");
				try{
					Thread.sleep(500);
				} catch (Exception ex) {}
		}
	}
}
