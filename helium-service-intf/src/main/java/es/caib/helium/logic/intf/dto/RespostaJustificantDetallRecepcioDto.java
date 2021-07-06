
package es.caib.helium.logic.intf.dto;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Resposta a una petició per obtenir justificant de recepció
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RespostaJustificantDetallRecepcioDto extends RespostaJustificantRecepcioDto {
	protected TipoEstadoNotificacionDto estado;
	protected XMLGregorianCalendar fechaAcuseRecibo;
	protected ReferenciaRDSJustificanteDto ficheroAcuseRecibo;
	protected DetalleAvisosDto avisos;

	public DetalleAvisosDto getAvisos() {
        if (avisos == null) {
        	avisos = new DetalleAvisosDto();
        }
        return this.avisos;
	}

	public void setAvisos(DetalleAvisosDto detalleAvisos) {
		this.avisos = detalleAvisos;
	}

	public TipoEstadoNotificacionDto getEstado() {
		return estado;
	}

	public void setEstado(TipoEstadoNotificacionDto estado) {
		this.estado = estado;
	}

	public XMLGregorianCalendar getFechaAcuseRecibo() {
		return fechaAcuseRecibo;
	}

	public void setFechaAcuseRecibo(XMLGregorianCalendar fechaAcuseRecibo) {
		this.fechaAcuseRecibo = fechaAcuseRecibo;
	}

	public ReferenciaRDSJustificanteDto getFicheroAcuseRecibo() {
		return ficheroAcuseRecibo;
	}

	public void setFicheroAcuseRecibo(ReferenciaRDSJustificanteDto ficheroAcuseRecibo) {
		this.ficheroAcuseRecibo = ficheroAcuseRecibo;
	}

	@Override
	public String toString() {
		return "RespostaJustificantDetallRecepcioDto [estado=" + estado + ", fechaAcuseRecibo=" + fechaAcuseRecibo + ", ficheroAcuseRecibo=" + ficheroAcuseRecibo + ", avisos=" + avisos + "]";
	}
}
