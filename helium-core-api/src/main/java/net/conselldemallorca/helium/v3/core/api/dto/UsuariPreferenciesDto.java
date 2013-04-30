/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;


/**
 * DTO amb informació de preferències d'usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UsuariPreferenciesDto {

	private String codi;
	private String defaultEntornCodi;
	private String idioma;



	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getDefaultEntornCodi() {
		return defaultEntornCodi;
	}
	public void setDefaultEntornCodi(String defaultEntornCodi) {
		this.defaultEntornCodi = defaultEntornCodi;
	}
	public String getIdioma() {
		return idioma;
	}
	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}

}
