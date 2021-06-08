package es.caib.helium.integracio.domini.tramitacio;

import javax.xml.datatype.XMLGregorianCalendar;

import es.caib.helium.integracio.domini.registre.ReferenciaRDSJustificante;
import es.caib.helium.integracio.enums.tramitacio.TipoEstadoNotificacion;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RespostaJustificantDetallRecepcio extends RespostaJustificantRecepcio {

	private TipoEstadoNotificacion estado;
	private XMLGregorianCalendar fechaAcuseRecibo;
	private ReferenciaRDSJustificante ficheroAcuseRecibo;
	private DetalleAvisos avisos;
}
