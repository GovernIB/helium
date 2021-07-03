package es.caib.helium.logic.intf.dto;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Java class for DetalleAviso complex type.
 */
public class DetalleAvisoDto implements Comparable<DetalleAvisoDto> {

	protected TipoAvisoDto tipo;
	protected String destinatario;
	protected boolean enviado;
	protected XMLGregorianCalendar fechaEnvio;
	protected boolean confirmarEnvio;
	protected TipoConfirmacionAvisoDto confirmadoEnvio;

	public TipoAvisoDto getTipo() {
		return tipo;
	}

	public void setTipo(TipoAvisoDto tipo) {
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

	public TipoConfirmacionAvisoDto getConfirmadoEnvio() {
		return confirmadoEnvio;
	}

	public void setConfirmadoEnvio(TipoConfirmacionAvisoDto confirmadoEnvio) {
		this.confirmadoEnvio = confirmadoEnvio;
	}

	@Override
	public int compareTo(DetalleAvisoDto o) {
		if (getFechaEnvio() == null || o.getFechaEnvio() == null)
			return 0;
		return getFechaEnvio().compare(o.getFechaEnvio());
	}
}
