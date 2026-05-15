package es.caib.helium.ejb.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.SessionContext;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Funcionalitat bàsica pels EJBs.
 *
 * @author Límit Tecnologies
 */
@Slf4j
public abstract class AbstractServiceEjb<S> {

	@Resource
	protected EJBContext ejbContext;
	@Resource
	protected SessionContext sessionContext;

	private Class<S> serviceClass;

	@PostConstruct
	public void postConstruct() {
		log.debug("EJB instance created for " + getClass().getSimpleName());
		S delegateService = EjbContextConfig.getApplicationContext().getBean(getServiceClass());
		log.debug("EJB instance delegate configured for " + getClass().getSimpleName() + ": " + delegateService);
		setDelegateService(delegateService);
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

	public void propagateEjbAuthenticationToSpringSecurity(String ...rolesToCheck) {
		if (sessionContext != null) {
			List<GrantedAuthority> authorities = null;
			if (rolesToCheck != null) {
				authorities = new ArrayList<>();
				for (String roleToCheck: rolesToCheck) {
					if (sessionContext.isCallerInRole(roleToCheck)) {
						authorities.add(new SimpleGrantedAuthority(roleToCheck));
					}
				}
			}
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					sessionContext.getCallerPrincipal(),
					null,
					authorities);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
	}

	abstract protected void setDelegateService(S delegateService);

}
