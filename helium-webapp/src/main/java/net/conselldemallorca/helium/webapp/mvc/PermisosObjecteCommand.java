/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.Set;

import org.springframework.security.acls.model.Permission;

/**
 * Command per gestionar permisos per als objectes
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PermisosObjecteCommand {

	private Long id;
	private String nom;
	private Set<Permission> permisos;
	private boolean usuari;



	public boolean isUsuari() {
		return usuari;
	}
	public void setUsuari(boolean usuari) {
		this.usuari = usuari;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public Set<Permission> getPermisos() {
		return permisos;
	}
	public void setPermisos(Set<Permission> permisos) {
		this.permisos = permisos;
	}

}
