package es.caib.helium.api.security;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class RoleHelper {

	public static boolean hasAnyRole(String... roles) {

		List<String> roleList = List.of(roles);

		if(roles == null) return true;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if(auth == null || !auth.isAuthenticated()) return false;

		for(GrantedAuthority athority : auth.getAuthorities()) {
			if(roleList.contains(athority.getAuthority()))
				return true;
		}

		return false;
	}
}
