package es.caib.helium.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

/** Implementació pròpia per mapejar rols J2EE i permetre la resta de rols
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
public class HeliumGrantedAuthoritiesMapper implements GrantedAuthoritiesMapper {
	
	private Map<String, String> roleMapping = new HashMap<String, String>();
	
	/** Constructor amb el mapeig per defecte */
	public HeliumGrantedAuthoritiesMapper() {
		roleMapping.put("HEL_ADMIN", "ROLE_ADMIN");
		roleMapping.put("HEL_USER", "ROLE_USER");
		roleMapping.put("tothom", "ROLE_USER");
	}

	@Override
	public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
		HashSet<GrantedAuthority> mapped = new HashSet<>(authorities.size());
		for (GrantedAuthority authority : authorities) {
			// Afegeix el rol
			mapped.add(new SimpleGrantedAuthority(authority.getAuthority()));
			if (roleMapping.containsKey(authority.getAuthority())) {
				// Afegeix també el mapeig
				mapped.add(new SimpleGrantedAuthority(roleMapping.get(authority.getAuthority())));
			}
		}
		return mapped;
	}

	public Map<String, String> getRoleMapping() {
		return roleMapping;
	}

	public void setRoleMapping(Map<String, String> mapping) {
		this.roleMapping = mapping;
	}

}
