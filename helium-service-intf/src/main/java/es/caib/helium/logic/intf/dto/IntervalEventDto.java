package es.caib.helium.logic.intf.dto;

import java.util.Date;

public class IntervalEventDto {
	private Date date;
	private Long duracio;
	
	public IntervalEventDto(Date date, Long duracio) {
		super();
		this.date = date;
		this.duracio = duracio;
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Long getDuracio() {
		return duracio;
	}
	public void setDuracio(Long duracio) {
		this.duracio = duracio;
	}
}
