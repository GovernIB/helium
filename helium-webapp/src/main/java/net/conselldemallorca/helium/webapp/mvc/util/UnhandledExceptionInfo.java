package net.conselldemallorca.helium.webapp.mvc.util;

import java.io.Serializable;
import java.util.Date;

public class UnhandledExceptionInfo implements Serializable {
	
	private static final long serialVersionUID = 7077967843327576037L;
	private Date fecha;
	private String URL;
	private Throwable excepcio;
	
	public UnhandledExceptionInfo(Date fecha, String uRL, Throwable excepcio) {
		super();
		this.fecha = fecha;
		URL = uRL;
		this.excepcio = excepcio;
	}
	
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getURL() {
		return URL;
	}
	public void setURL(String uRL) {
		URL = uRL;
	}
	public Throwable getExcepcio() {
		return excepcio;
	}
	public void setExcepcio(Throwable excepcio) {
		this.excepcio = excepcio;
	}	
	
}