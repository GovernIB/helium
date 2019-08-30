/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.security;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.mapping.MappableAttributesRetriever;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.dto.PermisHeliumDto;
import net.conselldemallorca.helium.v3.core.api.service.OrganitzacioService;
import net.conselldemallorca.helium.v3.core.api.service.PermisService;

/**
 * Aconsegueix els rols que siguin rellevants per a l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RolesBasedMappableAttributesRetriever implements MappableAttributesRetriever {

	@Autowired
	private PermisService permisService;
	@Resource
	private OrganitzacioService organitzacioService;

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
		LOGGER.debug("Refrescant el llistat de rols per mapejar");
		mappableAttributes.clear();
		if (defaultMappableAttributes != null)
			mappableAttributes.addAll(defaultMappableAttributes);
		String source = GlobalProperties.getInstance().getProperty("app.jbpm.identity.source");
		if (source.equalsIgnoreCase("helium")) {
			for (PermisHeliumDto permis: permisService.findAll()) {
				String codi = permis.getCodi();
				if (!mappableAttributes.contains(codi))
					mappableAttributes.add(codi);
			}
		} else {
			for (String group: organitzacioService.findDistinctJbpmGroupNames()) {
				if (group != null && !mappableAttributes.contains(group))
					mappableAttributes.add(group);
			}
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(RolesBasedMappableAttributesRetriever.class);

}
