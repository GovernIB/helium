/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.registre;

/**
 * Informació sobre l'expedient de l'aplicació de tramitació
 * per a fer notificacions
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesExpedient {

	private String identificador;
	private String clau;
	private String unitatAdministrativa;

	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	public String getClau() {
		return clau;
	}
	public void setClau(String clau) {
		this.clau = clau;
	}
	public String getUnitatAdministrativa() {
		return unitatAdministrativa;
	}
	public void setUnitatAdministrativa(String unitatAdministrativa) {
		this.unitatAdministrativa = unitatAdministrativa;
	}

}
