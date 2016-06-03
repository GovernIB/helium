package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import javax.xml.datatype.XMLGregorianCalendar;

public class DetalleAviso implements Comparable<DetalleAviso> {

	protected TipoAviso tipo;
	protected String destinatario;
	protected boolean enviado;
	protected XMLGregorianCalendar fechaEnvio;
	protected boolean confirmarEnvio;
	protected TipoConfirmacionAviso confirmadoEnvio;
	
	public TipoAviso getTipo() {
		return tipo;
	}
	public void setTipo(TipoAviso tipo) {
		this.tipo = tipo;
	}
	public String getDestinatario() {
		return destinatario;
	}
	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}
	public boolean isEnviado() {
		return enviado;
	}
	public void setEnviado(boolean enviado) {
		this.enviado = enviado;
	}
	public XMLGregorianCalendar getFechaEnvio() {
		return fechaEnvio;
	}
	public void setFechaEnvio(XMLGregorianCalendar fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}
	public boolean isConfirmarEnvio() {
		return confirmarEnvio;
	}
	public void setConfirmarEnvio(boolean confirmarEnvio) {
		this.confirmarEnvio = confirmarEnvio;
	}
	public TipoConfirmacionAviso getConfirmadoEnvio() {
		return confirmadoEnvio;
	}
	public void setConfirmadoEnvio(TipoConfirmacionAviso confirmadoEnvio) {
		this.confirmadoEnvio = confirmadoEnvio;
	}
	
	@Override
	public int compareTo(DetalleAviso o) {
		if (getFechaEnvio() == null || o.getFechaEnvio() == null)
			return 0;
		return getFechaEnvio().compare(o.getFechaEnvio());
	}
}
