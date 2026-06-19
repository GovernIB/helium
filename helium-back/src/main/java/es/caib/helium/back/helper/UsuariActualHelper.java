package es.caib.helium.back.helper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Helper per consultes de permisos sobre l'usuari actual.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UsuariActualHelper {

	/** Consulta si l'usuari actual és administrador d'Helium */
	public static boolean isAdministrador() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return isAdministrador(auth);
	}

	public static boolean isAdministrador(Authentication auth) {
		boolean isAdministrador = false;
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(auth.getAuthorities());
		for (GrantedAuthority grantedAuthority : authorities) {
	        if ("ROLE_ADMIN".equals(grantedAuthority.getAuthority())
	        		|| "HEL_ADMIN".equals(grantedAuthority.getAuthority())) {
	            isAdministrador = true;
	            break;
	        }
	    }
		return isAdministrador;
	}
}
