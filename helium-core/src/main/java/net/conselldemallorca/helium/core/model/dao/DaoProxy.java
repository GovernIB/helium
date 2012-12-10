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
//		System.out.println(">>>>>DaoProxy - EntornDao (" + System.currentTimeMillis() + ")");
		return (EntornDao)ctx.getBean("entornDao", EntornDao.class);
	}
	public DefinicioProcesDao getDefinicioProcesDao() {
		waitContext();
		if (ctx == null) return null;
//		System.out.println(">>>>>DaoProxy - definicioProcesDao (" + System.currentTimeMillis() + ")");
		return (DefinicioProcesDao)ctx.getBean("definicioProcesDao", DefinicioProcesDao.class);
	}
	public CampDao getCampDao() {
		waitContext();
		if (ctx == null) return null;
//		System.out.println(">>>>>DaoProxy - campDao (" + System.currentTimeMillis() + ")");
		return (CampDao)ctx.getBean("campDao", CampDao.class);
	}
	public PluginPersonaDao getPluginPersonaDao() {
		waitContext();
		if (ctx == null) return null;
//		System.out.println(">>>>>DaoProxy - pluginPersonaDao (" + System.currentTimeMillis() + ")");
		return (PluginPersonaDao)ctx.getBean("pluginPersonaDao", PluginPersonaDao.class);
	}
	public AreaDao getAreaDao() {
		waitContext();
		if (ctx == null) return null;
//		System.out.println(">>>>>DaoProxy - areaDao (" + System.currentTimeMillis() + ")");
		return (AreaDao)ctx.getBean("areaDao", AreaDao.class);
	}
	public CarrecDao getCarrecDao() {
		waitContext();
		if (ctx == null) return null;
//		System.out.println(">>>>>DaoProxy - carrecDao (" + System.currentTimeMillis() + ")");
		return (CarrecDao)ctx.getBean("carrecDao", CarrecDao.class);
	}
	public ExpedientTipusDao getExpedientTipusDao() {
		waitContext();
		if (ctx == null) return null;
//		System.out.println(">>>>>DaoProxy - expedientTipusDao (" + System.currentTimeMillis() + ")");
		return (ExpedientTipusDao)ctx.getBean("expedientTipusDao", ExpedientTipusDao.class);
	}
	public TascaDao getTascaDao() {
		waitContext();
		if (ctx == null) return null;
//		System.out.println(">>>>>DaoProxy - tascaDao (" + System.currentTimeMillis() + ")");
		return (TascaDao)ctx.getBean("tascaDao", TascaDao.class);
	}
	public DominiDao getDominiDao() {
		waitContext();
		if (ctx == null) return null;
//		System.out.println(">>>>>DaoProxy - dominiDao (" + System.currentTimeMillis() + ")");
		return (DominiDao)ctx.getBean("dominiDao", DominiDao.class);
	}
	public EnumeracioDao getEnumeracioDao() {
		waitContext();
		if (ctx == null) return null;
//		System.out.println(">>>>>DaoProxy - enumeracioDao (" + System.currentTimeMillis() + ")");
		return (EnumeracioDao)ctx.getBean("enumeracioDao", EnumeracioDao.class);
	}
	public DocumentStoreDao getDocumentStoreDao() {
		waitContext();
		if (ctx == null) return null;
//		System.out.println(">>>>>DaoProxy - documentStoreDao (" + System.currentTimeMillis() + ")");
		return (DocumentStoreDao)ctx.getBean("documentStoreDao", DocumentStoreDao.class);
	}
	public MailDao getMailDao() {
		waitContext();
		if (ctx == null) return null;
//		System.out.println(">>>>>DaoProxy - mailDao (" + System.currentTimeMillis() + ")");
		return (MailDao)ctx.getBean("mailDao", MailDao.class);
	}
	public FestiuDao getFestiuDao() {
		waitContext();
		if (ctx == null) return null;
//		System.out.println(">>>>>DaoProxy - festiuDao (" + System.currentTimeMillis() + ")");
		return (FestiuDao)ctx.getBean("festiuDao", FestiuDao.class);
	}
	public JbpmDao getJbpmDao() {
		waitContext();
		if (ctx == null) return null;
//		System.out.println(">>>>>DaoProxy - jbpmDao (" + System.currentTimeMillis() + ")");
		return (JbpmDao)ctx.getBean("jbpmDao", JbpmDao.class);
	}
	public PluginRegistreDao getPluginRegistreDao() {
		waitContext();
		if (ctx == null) return null;
//		System.out.println(">>>>>DaoProxy - pluginRegistreDao (" + System.currentTimeMillis() + ")");
		return (PluginRegistreDao)ctx.getBean("pluginRegistreDao", PluginRegistreDao.class);
	}
	public PluginTramitacioDao getPluginTramitacioDao() {
		waitContext();
		if (ctx == null) return null;
//		System.out.println(">>>>>DaoProxy - pluginTramitacioDao (" + System.currentTimeMillis() + ")");
		return (PluginTramitacioDao)ctx.getBean("pluginTramitacioDao", PluginTramitacioDao.class);
	}
	public PluginGestioDocumentalDao getPluginGestioDocumentalDao() {
		waitContext();
		if (ctx == null) return null;
//		System.out.println(">>>>>DaoProxy - pluginGestioDocumentalDao (" + System.currentTimeMillis() + ")");
		return (PluginGestioDocumentalDao)ctx.getBean("pluginGestioDocumentalDao", PluginGestioDocumentalDao.class);
	}
	public ReassignacioDao getReassignacioDao() {
		waitContext();
		if (ctx == null) return null;
//		System.out.println(">>>>>DaoProxy - reassignacioDao (" + System.currentTimeMillis() + ")");
		return (ReassignacioDao)ctx.getBean("reassignacioDao", ReassignacioDao.class);
	}

	private void waitContext() {
		while (ctx == null){
			System.out.println(">>>>>DaoProxy ctx == null. Retry (" + System.currentTimeMillis() + ")");
				try{
					Thread.sleep(500);
				} catch (Exception ex) {}
		}
		System.out.println(">>>>>DaoProxy ctx OK (" + System.currentTimeMillis() + ")");
	}
}
