/**
 * 
 */
package net.conselldemallorca.helium.core.model.exportacio;

import java.io.Serializable;

import net.conselldemallorca.helium.core.model.hibernate.MapeigSistra.TipusMapeig;

/**
 * DTO amb informació d'un estat d'un tipus d'expedient per
 * exportar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MapeigSistraExportacio implements Serializable {


	
	private String codiHelium;
	private String codiSistra;
	private TipusMapeig tipus;


	public MapeigSistraExportacio(
			String codiHelium,
			String codiSistra,
			TipusMapeig tipus) {
		this.codiHelium = codiHelium;
		this.codiSistra = codiSistra;
		this.tipus = tipus;
	}

	public String getCodiHelium() {
		return codiHelium;
	}
	public void setCodiHelium(String codiHelium) {
		this.codiHelium = codiHelium;
	}
	public String getCodiSistra() {
		return codiSistra;
	}
	public void setCodiSistra(String codiSistra) {
		this.codiSistra = codiSistra;
	}
	public TipusMapeig getTipus() {
		return tipus;
	}
	public void setTipus(TipusMapeig tipus) {
		this.tipus = tipus;
	}

	private static final long serialVersionUID = 1L;

}
