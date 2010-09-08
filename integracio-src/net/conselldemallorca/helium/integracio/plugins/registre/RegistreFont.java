/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.registre;

import java.io.Serializable;

/**
 * Classe que representa un remitent o un destinatari d'un
 * seient registral
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class RegistreFont implements Serializable {

	private String codiEntitat;
	private String nomEntitat;
	private String codiGeografic;
	private String nomGeografic;
	private String numeroRegistre;
	private String anyRegistre;



	public String getCodiEntitat() {
		return codiEntitat;
	}
	public void setCodiEntitat(String codiEntitat) {
		this.codiEntitat = codiEntitat;
	}
	public String getNomEntitat() {
		return nomEntitat;
	}
	public void setNomEntitat(String nomEntitat) {
		this.nomEntitat = nomEntitat;
	}
	public String getCodiGeografic() {
		return codiGeografic;
	}
	public void setCodiGeografic(String codiGeografic) {
		this.codiGeografic = codiGeografic;
	}
	public String getNomGeografic() {
		return nomGeografic;
	}
	public void setNomGeografic(String nomGeografic) {
		this.nomGeografic = nomGeografic;
	}
	public String getNumeroRegistre() {
		return numeroRegistre;
	}
	public void setNumeroRegistre(String numeroRegistre) {
		this.numeroRegistre = numeroRegistre;
	}
	public String getAnyRegistre() {
		return anyRegistre;
	}
	public void setAnyRegistre(String anyRegistre) {
		this.anyRegistre = anyRegistre;
	}



	private static final long serialVersionUID = 1L;

}
