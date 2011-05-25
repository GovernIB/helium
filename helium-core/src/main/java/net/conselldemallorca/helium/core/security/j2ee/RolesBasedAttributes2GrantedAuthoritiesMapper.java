package net.conselldemallorca.helium.core.security.j2ee;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.authoritymapping.Attributes2GrantedAuthoritiesMapper;


/**
 * Defineix el mapeig entre els rols J2EE i els rols interns de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("unchecked")
public class RolesBasedAttributes2GrantedAuthoritiesMapper implements Attributes2GrantedAuthoritiesMapper {

	private Map baseRoleMapping = new HashMap();

	public GrantedAuthority[] getGrantedAuthorities(String[] attributes) {
		List<String> gaList = new ArrayList();
		for (int i = 0; i < attributes.length; i++) {
			Object mapping = baseRoleMapping.get(attributes[i]);
			if (mapping != null) {
				if (mapping instanceof Collection) {
					gaList.addAll((Collection)mapping);
				} else if (mapping instanceof String) {
					gaList.add((String)mapping);
				}
			} else {
				gaList.add(attributes[i]);
			}
		}
		GrantedAuthority[] result = new GrantedAuthority[gaList.size()];
		int index = 0;
		for (String ga: gaList) {
			result[index++] = new GrantedAuthorityImpl(ga);
		}
		return result;
	}

	public void setBaseRoleMapping(Map baseRoleMapping) {
		this.baseRoleMapping = baseRoleMapping;
	}

}
