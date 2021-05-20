
package es.caib.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Resposta a una petició per obtenir justificant de recepció
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
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

	@Override
	public String toString() {
		return "RespostaJustificantDetallRecepcioDto [estado=" + estado + ", fechaAcuseRecibo=" + fechaAcuseRecibo + ", ficheroAcuseRecibo=" + ficheroAcuseRecibo + ", avisos=" + avisos + "]";
	}
}
