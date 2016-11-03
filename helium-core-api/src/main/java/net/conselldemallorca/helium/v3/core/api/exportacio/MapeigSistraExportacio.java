/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exportacio;

import java.io.Serializable;

import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto.TipusMapeig;

/**
 * Objecte de domini que representa l'estat d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MapeigSistraExportacio implements Serializable {

	private Long id;
	private String codiHelium;
	private String codiSistra;
	private TipusMapeig tipus;


	public MapeigSistraExportacio() {}
	public MapeigSistraExportacio(String codiHelium, String codiSistra, TipusMapeig tipus) {
		this.codiHelium = codiHelium;
		this.codiSistra = codiSistra;
		this.tipus = tipus;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
