package net.conselldemallorca.helium.webapp.v3.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.Attributes2GrantedAuthoritiesMapper;


/**
 * Defineix el mapeig entre els rols J2EE i els rols interns de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("rawtypes")
public class RolesBasedAttributes2GrantedAuthoritiesMapper implements Attributes2GrantedAuthoritiesMapper {

	private Map baseRoleMapping = new HashMap();

	public void setBaseRoleMapping(Map baseRoleMapping) {
		this.baseRoleMapping = baseRoleMapping;
	}

	public Collection<GrantedAuthority> getGrantedAuthorities(Collection<String> attributes) {
		List<GrantedAuthority> gaList = new ArrayList<GrantedAuthority>();
		for (String attribute: attributes) {
			Object mapping = baseRoleMapping.get(attribute);
			if (mapping != null) {
				if (mapping instanceof Collection) {
					for (Object obj: (Collection)mapping) {
						if (obj != null)
							gaList.add(new SimpleGrantedAuthority(obj.toString()));
					}
				} else if (mapping != null) {
					gaList.add(new SimpleGrantedAuthority(mapping.toString()));
				}
			} else {
				gaList.add(new SimpleGrantedAuthority(attribute));
			}
		}
		return gaList;
	}

}
