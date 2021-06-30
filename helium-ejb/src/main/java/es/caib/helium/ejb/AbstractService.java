/**
 * 
 */
package es.caib.helium.ejb;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;

import lombok.extern.slf4j.Slf4j;

/**
 * Funcionalitat bàsica pels EJBs.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public abstract class AbstractService<S> {

	private S delegateService;
	private Class<S> serviceClass;

	@Resource
	protected SessionContext sessionContext;

	@PostConstruct
	public void postConstruct() {
		log.debug("EJB instance created: " + getClass().getSimpleName());
		delegateService = EjbContextConfig.getApplicationContext().getBean(getServiceClass());
	}

	protected S getDelegateService() {
		return delegateService;
	}

	@SuppressWarnings("unchecked")
	protected Class<S> getServiceClass() {
		if (serviceClass == null) {
			Type genericSuperClass = getClass().getGenericSuperclass();
			while (genericSuperClass != null && !(genericSuperClass instanceof ParameterizedType)) {
				genericSuperClass = ((Class<?>)genericSuperClass).getGenericSuperclass();
			}
			ParameterizedType parameterizedType = (ParameterizedType)genericSuperClass;
			serviceClass = (Class<S>)parameterizedType.getActualTypeArguments()[0];
		}
		return serviceClass;
	}

}
