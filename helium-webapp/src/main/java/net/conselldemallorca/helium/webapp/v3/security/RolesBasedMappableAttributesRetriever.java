/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.security;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.hibernate.AreaJbpmId;
import net.conselldemallorca.helium.core.model.hibernate.Permis;
import net.conselldemallorca.helium.core.model.service.OrganitzacioService;
import net.conselldemallorca.helium.core.model.service.PermisService;
import net.conselldemallorca.helium.core.util.GlobalProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.mapping.MappableAttributesRetriever;

/**
 * Aconsegueix els rols que seran rellevants per a l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RolesBasedMappableAttributesRetriever implements MappableAttributesRetriever {

	@Resource
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

	@Autowired
	public void setPermisService(PermisService permisService) {
		this.permisService = permisService;
	}



	private void refrescarMappableAttributes() {
		LOGGER.debug("Refrescant el llistat de rols per mapejar");
		mappableAttributes.clear();
		if (defaultMappableAttributes != null)
			mappableAttributes.addAll(defaultMappableAttributes);
		String source = GlobalProperties.getInstance().getProperty("app.jbpm.identity.source");
		if (source.equalsIgnoreCase("helium")) {
			for (Permis permis: permisService.findAll()) {
				String codi = permis.getCodi();
				if (!mappableAttributes.contains(codi))
					mappableAttributes.add(codi);
			}
		} else {
			for (AreaJbpmId group: organitzacioService.findDistinctJbpmGroups()) {
				if (group != null && !mappableAttributes.contains(group.getCodi()))
					mappableAttributes.add(group.getCodi());
			}
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(RolesBasedMappableAttributesRetriever.class);

}
