package net.conselldemallorca.helium.webapp.v3.command;

import java.io.Serializable;

public class ConsultesPortafibFiltreCommand implements Serializable {

	private static final long serialVersionUID = -4947089586164633202L;
	
	private Long id;
	private Long entornId;
	private Long tipusId;
	private Long expedientId;
	private String numeroExpedient;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getEntornId() {
		return entornId;
	}
	public void setEntornId(Long entornId) {
		this.entornId = entornId;
	}
	public Long getTipusId() {
		return tipusId;
	}
	public void setTipusId(Long tipusId) {
		this.tipusId = tipusId;
	}
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public String getNumeroExpedient() {
		return numeroExpedient;
	}
	public void setNumeroExpedient(String numeroExpedient) {
		this.numeroExpedient = numeroExpedient;
	}
	
	
}
