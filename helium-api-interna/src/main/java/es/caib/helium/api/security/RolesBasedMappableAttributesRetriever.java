/**
 * 
 */
package es.caib.helium.api.security;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.authority.mapping.MappableAttributesRetriever;

/**
 * Aconsegueix els rols que seran rellevants per a l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RolesBasedMappableAttributesRetriever implements MappableAttributesRetriever, ApplicationContextAware {

	private Set<String> defaultMappableAttributes;
	private Set<String> mappableAttributes = new HashSet<String>();

	public Set<String> getMappableAttributes() {
		refrescarMappableAttributes();
		return mappableAttributes;
	}

	public void setDefaultMappableAttributes(Set<String> defaultMappableAttributes) {
		this.defaultMappableAttributes = defaultMappableAttributes;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
	}

	/** En comptes de consultar els logs simplement afegeix el log de HEL_COM a la llista de rols mapejables. */
	private void refrescarMappableAttributes() {
		LOGGER.debug("Construint la llista de rols mapejables");
		mappableAttributes.clear();
		if (defaultMappableAttributes != null)
			mappableAttributes.addAll(defaultMappableAttributes);
		String codi = "HEL_COM";
		if (!mappableAttributes.contains(codi))
			mappableAttributes.add(codi);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(RolesBasedMappableAttributesRetriever.class);

}
