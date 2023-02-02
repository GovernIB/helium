/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;

/**
 * Objecte de domini que representa l'estat d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MapeigSistraDto implements Serializable {

	public enum TipusMapeig {
		Variable,
		Document,
		Adjunt
	}
	
	private Long id;
	private String codiHelium;
	private String codiSistra;
	private TipusMapeig tipus;
	private ExpedientTipusDto expedientTipus;
	private boolean evitarSobreescriptura;


	public MapeigSistraDto() {}
	public MapeigSistraDto(ExpedientTipusDto expedientTipus, String codiHelium, String codiSistra, TipusMapeig tipus) {
		this.expedientTipus = expedientTipus;
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
	public ExpedientTipusDto getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipusDto expedientTipus) {
		this.expedientTipus = expedientTipus;
	}
	
	public boolean isEvitarSobreescriptura() {
		return evitarSobreescriptura;
	}
	public void setEvitarSobreescriptura(boolean evitarSobreescriptura) {
		this.evitarSobreescriptura = evitarSobreescriptura;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codiHelium == null) ? 0 : codiHelium.hashCode());
		result = prime * result
				+ ((expedientTipus == null) ? 0 : expedientTipus.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapeigSistraDto other = (MapeigSistraDto) obj;
		if (codiHelium == null) {
			if (other.codiHelium != null)
				return false;
		} else if (!codiHelium.equals(other.codiHelium))
			return false;
		if (expedientTipus == null) {
			if (other.expedientTipus != null)
				return false;
		} else if (!expedientTipus.equals(other.expedientTipus))
			return false;
		return true;
	}



	private static final long serialVersionUID = 1L;

}
