/**
 * 
 */
package es.caib.helium.back.security;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

/**
 * Esborra el prefix per defecte als rols ("ROLE_").
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DefaultRolesPrefixPostProcessor implements BeanPostProcessor, PriorityOrdered {

	@Override
	public Object postProcessAfterInitialization(
			Object bean,
			String beanName) throws BeansException {
		if (bean instanceof SecurityContextHolderAwareRequestFilter) {
			((SecurityContextHolderAwareRequestFilter) bean).setRolePrefix("");
		}
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public int getOrder() {
		return PriorityOrdered.HIGHEST_PRECEDENCE;
	}

}
