/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.registre;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Resposta a una petició per obtenir justificant de recepció
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RespostaJustificantDetallRecepcio extends RespostaJustificantRecepcio {
	protected TipoEstadoNotificacion estado;
	protected XMLGregorianCalendar fechaAcuseRecibo;
	protected ReferenciaRDSJustificante ficheroAcuseRecibo;
	protected DetalleAvisos avisos;

	public DetalleAvisos getAvisos() {
        if (avisos == null) {
        	avisos = new DetalleAvisos();
        }
        return this.avisos;
	}

	public void setAvisos(DetalleAvisos detalleAvisos) {
		this.avisos = detalleAvisos;
	}

	public TipoEstadoNotificacion getEstado() {
		return estado;
	}

	public void setEstado(TipoEstadoNotificacion estado) {
		this.estado = estado;
	}

	public XMLGregorianCalendar getFechaAcuseRecibo() {
		return fechaAcuseRecibo;
	}

	public void setFechaAcuseRecibo(XMLGregorianCalendar fechaAcuseRecibo) {
		this.fechaAcuseRecibo = fechaAcuseRecibo;
	}

	public ReferenciaRDSJustificante getFicheroAcuseRecibo() {
		return ficheroAcuseRecibo;
	}

	public void setFicheroAcuseRecibo(ReferenciaRDSJustificante ficheroAcuseRecibo) {
		this.ficheroAcuseRecibo = ficheroAcuseRecibo;
	}
}
