/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.registre;

import java.util.List;

/**
 * Informació sobre el tràmit se subsanació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TramitSubsanacio {

	private String identificador;
	private int versio;
	private String descripcio;
	private List<TramitSubsanacioParametre> parametres;



	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	public int getVersio() {
		return versio;
	}
	public void setVersio(int versio) {
		this.versio = versio;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public List<TramitSubsanacioParametre> getParametres() {
		return parametres;
	}
	public void setParametres(List<TramitSubsanacioParametre> parametres) {
		this.parametres = parametres;
	}

}
