
package es.caib.helium.integracio.plugins.registre;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Java class for DetalleAviso complex type.
 */
public class DetalleAviso {

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
}
