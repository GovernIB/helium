/**
 * 
 */
package es.caib.helium.back.security;

import org.springframework.security.core.authority.mapping.MappableAttributesRetriever;

import java.util.HashSet;
import java.util.Set;

/**
 * Aconsegueix els rols que seran rellevants per a l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RolesBasedMappableAttributesRetriever implements MappableAttributesRetriever {

	private Set<String> defaultMappableAttributes;
	private Set<String> mappableAttributes = new HashSet<String>();

	public Set<String> getMappableAttributes() {
		refrescarMappableAttributes();
		return mappableAttributes;
	}

	public void setDefaultMappableAttributes(Set<String> defaultMappableAttributes) {
		this.defaultMappableAttributes = defaultMappableAttributes;
	}

	private void refrescarMappableAttributes() {
		mappableAttributes.clear();
		if (defaultMappableAttributes != null)
			mappableAttributes.addAll(defaultMappableAttributes);
//		String source = GlobalProperties.getInstance().getProperty("app.jbpm.identity.source");
//		if (source.equalsIgnoreCase("helium")) {
//			for (PermisRolDto permisRol : permisService.findAll()) {
//				String codi = permisRol.getCodi();
//				if (!mappableAttributes.contains(codi))
//					mappableAttributes.add(codi);
//			}
//		} else {
//			for (String group: dissenyService.findDistinctJbpmGroupsCodis()) {
//				if (group != null && !mappableAttributes.contains(group))
//					mappableAttributes.add(group);
//			}
//		}

		// TODO: Mirar si hem d'obtenir els rols
		mappableAttributes.add("HEL_ADMIN");
		mappableAttributes.add("HEL_USER");
		mappableAttributes.add("tothom");
	}

}
