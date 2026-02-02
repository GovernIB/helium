/**
 * 
 */
package es.caib.helium.api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.authority.mapping.MappableAttributesRetriever;

import java.util.HashSet;
import java.util.Set;

/**
 * Aconsegueix els rols que seran rellevants per a l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RolesBasedMappableAttributesRetriever implements MappableAttributesRetriever, ApplicationContextAware {

	private ApplicationContext applicationContext;



	private Set<String> defaultMappableAttributes;
	private Set<String> mappableAttributes = new HashSet<String>();



	public Set<String> getMappableAttributes() {
		refrescarMappableAttributes();
		return mappableAttributes;
	}

	public void setDefaultMappableAttributes(Set<String> defaultMappableAttributes) {
		this.defaultMappableAttributes = defaultMappableAttributes;
	}

	/*@Autowired
	public void setServeiService(ServeiService serveiService) {
		LOGGER.debug("Inicialitzant el serveiService (" + (serveiService != null) + ")");
		this.serveiService = serveiService;
	}*/

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}



	private void refrescarMappableAttributes() {
		LOGGER.debug("Refrescant el llistat de rols per mapejar");
		mappableAttributes.clear();
		mappableAttributes.add("HEL_USER");
		mappableAttributes.add("tothom");
//		if (defaultMappableAttributes != null)
//			mappableAttributes.addAll(defaultMappableAttributes);
//		if (serveiService == null) {
//			LOGGER.debug("El serveiService és null. Obtenint el serveiService mitjançant l'applicationContext");
//			serveiService = applicationContext.getBean(ServeiService.class);
//		}
//		if (serveiService != null) {
//			mappableAttributes.addAll(serveiService.getRolsConfigurats());
//		} else {
//			LOGGER.error("No s'han pogut obtenir els rols addicionals del serveiService: El service és null");
//		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(RolesBasedMappableAttributesRetriever.class);

}
