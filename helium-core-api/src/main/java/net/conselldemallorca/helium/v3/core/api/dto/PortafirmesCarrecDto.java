package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;


public class PortafirmesCarrecDto implements Serializable {

	private String carrecId;
	private String carrecName;
	private String entitatId;
	private String usuariPersonaId;
	private String usuariPersonaNif;
	private String usuariPersonaEmail;
	private String usuariPersonaNom;
	private static final long serialVersionUID = 7006948072730800264L;
	
	public String getCarrecId() {
		return carrecId;
	}
	public void setCarrecId(String carrecId) {
		this.carrecId = carrecId;
	}
	public String getCarrecName() {
		return carrecName;
	}
	public void setCarrecName(String carrecName) {
		this.carrecName = carrecName;
	}
	public String getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(String entitatId) {
		this.entitatId = entitatId;
	}
	public String getUsuariPersonaId() {
		return usuariPersonaId;
	}
	public void setUsuariPersonaId(String usuariPersonaId) {
		this.usuariPersonaId = usuariPersonaId;
	}
	public String getUsuariPersonaNif() {
		return usuariPersonaNif;
	}
	public void setUsuariPersonaNif(String usuariPersonaNif) {
		this.usuariPersonaNif = usuariPersonaNif;
	}
	public String getUsuariPersonaEmail() {
		return usuariPersonaEmail;
	}
	public void setUsuariPersonaEmail(String usuariPersonaEmail) {
		this.usuariPersonaEmail = usuariPersonaEmail;
	}
	public String getUsuariPersonaNom() {
		return usuariPersonaNom;
	}
	public void setUsuariPersonaNom(String usuariPersonaNom) {
		this.usuariPersonaNom = usuariPersonaNom;
	}
	
	
}
