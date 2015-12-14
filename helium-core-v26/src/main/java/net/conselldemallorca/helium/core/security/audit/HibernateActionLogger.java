/**
 * 
 */
package net.conselldemallorca.helium.core.security.audit;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Implementació d'un action logger que guarda els logs
 * a base de dades emprant hibernate.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HibernateActionLogger extends HibernateDaoSupport implements ActionLogger {

	private LocalSessionFactoryBean sessionFactoryBean;

	public void createLog(
			String accio,
			Object entitat,
			String[] propietats,
			String usuari) {
		if (!(entitat instanceof ActionLog)) {
			ActionLog actionLog = new ActionLog();
			EntityParser ep = new EntityParser(entitat, propietats, sessionFactoryBean);
			actionLog.setAccio(accio);
			actionLog.setData(new Date());
			actionLog.setUsuari(usuari);
			actionLog.setTaula(ep.extractTableName());
			actionLog.setColumnaPk(ep.extractPrimaryKeyColumn());
			actionLog.setValors(ep.extractColumnValues());
			getHibernateTemplate().save(actionLog);
		}
	}



	@Autowired
	public void setSessionFactoryBean(LocalSessionFactoryBean sessionFactoryBean) {
		this.sessionFactoryBean = sessionFactoryBean;
	}

}
