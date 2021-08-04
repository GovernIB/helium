package es.caib.helium.war.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.MapBasedAttributes2GrantedAuthoritiesMapper;

/** Extensió de la classe MapBasedAttributes2GrantedAuthoritiesMapper per mapejar els rols i afegir-los
 * en la mateixa llista de rols mapejats i permesos.
 * 
 * @author Limit Tecnolgies <limit@limit.es>
 *
 */
public class HeliumMapBasedAttributes2GrantedAuthoritiesMapper extends MapBasedAttributes2GrantedAuthoritiesMapper {

	/** Constructor amb la llista de rols per defecte
	 * 
	 */
	public HeliumMapBasedAttributes2GrantedAuthoritiesMapper() {
		Map<String,String> roleMapping = new HashMap<String, String>();
		roleMapping.put("HEL_ADMIN", "ROLE_ADMIN");
		roleMapping.put("HEL_USER", "ROLE_USER");
		roleMapping.put("tothom", "ROLE_USER");
		this.setAttributes2grantedAuthoritiesMap(roleMapping);
	}
	
	/**
	 * Mapeja els rols i els afegeix també en el resultat.
	 */
	@Override
	public List<GrantedAuthority> getGrantedAuthorities(Collection<String> attributes) {
		ArrayList<GrantedAuthority> result = new ArrayList<>();
		for (String attribute : attributes)
			result.add(new SimpleGrantedAuthority(attribute));
		result.addAll(super.getGrantedAuthorities(attributes));
		result.trimToSize();
		return result;
	}
}
