/**
 * 
 */
package net.conselldemallorca.helium.core.model.dto;

import java.util.Set;

import net.conselldemallorca.helium.core.model.hibernate.Permis;
import net.conselldemallorca.helium.core.model.hibernate.Persona;

/**
 * DTO amb informació de la persona i de l'usuari associat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PersonaUsuariDto extends Persona {

	private boolean login;
	private String contrasenya;
	private Set<Permis> permisos;



	public boolean isLogin() {
		return login;
	}
	public void setLogin(boolean login) {
		this.login = login;
	}
	public String getContrasenya() {
		return contrasenya;
	}
	public void setContrasenya(String contrasenya) {
		this.contrasenya = contrasenya;
	}
	public Set<Permis> getPermisos() {
		return permisos;
	}
	public void setPermisos(Set<Permis> permisos) {
		this.permisos = permisos;
	}

	private static final long serialVersionUID = 1L;

}
