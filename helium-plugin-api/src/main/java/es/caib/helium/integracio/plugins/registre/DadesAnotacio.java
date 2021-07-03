/**
 * 
 */
package es.caib.helium.integracio.plugins.registre;

/**
 * Informació sobre l'anotació de registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesAnotacio {

	private String idiomaCodi;
	private String tipus;
	private String assumpte;
	private String unitatAdministrativa;
	private String registreNumero;
	private String registreAny;

	public String getIdiomaCodi() {
		return idiomaCodi;
	}
	public void setIdiomaCodi(String idiomaCodi) {
		this.idiomaCodi = idiomaCodi;
	}
	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	public String getAssumpte() {
		return assumpte;
	}
	public void setAssumpte(String assumpte) {
		this.assumpte = assumpte;
	}
	public String getUnitatAdministrativa() {
		return unitatAdministrativa;
	}
	public void setUnitatAdministrativa(String unitatAdministrativa) {
		this.unitatAdministrativa = unitatAdministrativa;
	}
	public String getRegistreNumero() {
		return registreNumero;
	}
	public void setRegistreNumero(String registreNumero) {
		this.registreNumero = registreNumero;
	}
	public String getRegistreAny() {
		return registreAny;
	}
	public void setRegistreAny(String registreAny) {
		this.registreAny = registreAny;
	}

}
