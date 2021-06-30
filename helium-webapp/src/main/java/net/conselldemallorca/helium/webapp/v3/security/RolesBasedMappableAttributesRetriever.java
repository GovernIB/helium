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

import es.caib.helium.logic.intf.dto.PermisRolDto;
import es.caib.helium.logic.intf.service.DissenyService;
import es.caib.helium.logic.intf.service.PermisService;
import net.conselldemallorca.helium.core.util.GlobalProperties;

/**
 * Aconsegueix els rols que seran rellevants per a l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RolesBasedMappableAttributesRetriever implements MappableAttributesRetriever {

	@Resource
	private PermisService permisService;
	@Resource
	private DissenyService dissenyService;

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
			for (PermisRolDto permisRol : permisService.findAll()) {
				String codi = permisRol.getCodi();
				if (!mappableAttributes.contains(codi))
					mappableAttributes.add(codi);
			}
		} else {
			for (String group: dissenyService.findDistinctJbpmGroupsCodis()) {
				if (group != null && !mappableAttributes.contains(group))
					mappableAttributes.add(group);
			}
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(RolesBasedMappableAttributesRetriever.class);

}
