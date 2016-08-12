/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Command per modificar les dades d'integració amb formularis externs.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientTipusIntegracioFormsCommand {

	@NotNull(groups = {Modificacio.class})
	private Long id;
	private boolean actiu;
	@Size(max = 255, groups = {Modificacio.class})
	private String url;
	@Size(max = 255, groups = {Modificacio.class})
	private String usuari;
	@Size(max = 25, groups = {Modificacio.class})
	private String contrasenya;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public boolean isActiu() {
		return actiu;
	}
	public void setActiu(boolean actiu) {
		this.actiu = actiu;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsuari() {
		return usuari;
	}
	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}

	public String getContrasenya() {
		return contrasenya;
	}
	public void setContrasenya(String contrasenya) {
		this.contrasenya = contrasenya;
	}

	public interface Modificacio {}
}
