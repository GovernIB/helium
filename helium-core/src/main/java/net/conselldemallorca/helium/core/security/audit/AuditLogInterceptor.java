/**
 * 
 */
package net.conselldemallorca.helium.core.security.audit;

import java.io.Serializable;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.context.SecurityContextHolder;

/**
 * Interceptor per guardar els logs dels canvis
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AuditLogInterceptor extends EmptyInterceptor implements ApplicationContextAware {

	private String actionLoggerBean;
	private ActionLogger actionLogger;

	ApplicationContext applicationContext;



	public AuditLogInterceptor () {}

	public boolean onLoad (
			Object entity,
			Serializable id,
			Object[] state,
			String[] propertyNames,
			Type[] types) throws CallbackException {
		return false;
	}
	public boolean onFlushDirty(Object entity, Serializable id, Object[] newValues, Object[] oldValues, String[] propertyNames, Type[] types) throws CallbackException {
		if (entity instanceof Auditable) {
			getActionLogger().createLog(
					"update",
					entity,
					propertyNames,
					getUserId());
		}
		return false;
	}
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
		if (entity instanceof Auditable) {
			getActionLogger().createLog(
					"create",
					entity,
					propertyNames,
					getUserId());
		}
		return false;
	}
	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
		if (entity instanceof Auditable) {
			getActionLogger().createLog(
					"delete",
					entity,
					propertyNames,
					getUserId());
		}
	}

	public void setActionLoggerBean(String actionLoggerBean) {
		this.actionLoggerBean = actionLoggerBean;
	}
	public void setActionLogger(ActionLogger actionLogger) {
		this.actionLogger = actionLogger;
	}
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}



	private ActionLogger getActionLogger() {
		if (actionLogger == null) {
			actionLogger = (ActionLogger)applicationContext.getBean(actionLoggerBean);
		}
		return actionLogger;
	}
	private String getUserId() {
		String user = SecurityContextHolder.getContext().getAuthentication().getName();
		if (user != null)
			return user;
		return "unknown";
	}

	private static final long serialVersionUID = 1L;

}
