/**
 * 
 */
package es.caib.helium.back.helper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Helper per a consultes sobre l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class UsuariHelper {

	/** Consulta el codi de l'usuari actual */
	public String getUsuariActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			return auth.getName();
		} else {
			throw new AuthenticationCredentialsNotFoundException(null);
		}
	}
	
	/** Consulta si l'usuari actual és administrador d'Helium */
	public boolean isAdministrador() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return isAdministrador(auth);
	}
	
	public static boolean isAdministrador(Authentication auth) {
		boolean isAdministrador = false;
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(auth.getAuthorities());
		for (GrantedAuthority grantedAuthority : authorities) {
	        if ("ROLE_ADMIN".equals(grantedAuthority.getAuthority())) {
	            isAdministrador = true;
	            break;
	        }
	    }
		return isAdministrador;
	}
	
	public List<String> getRols() {
		List<String> rols = new ArrayList<String>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();		
		if (auth.getAuthorities() != null) {
			for (GrantedAuthority grantedAuthority: auth.getAuthorities()) {
				rols.add(grantedAuthority.getAuthority());
			}
		}
		return rols;
	}

}
