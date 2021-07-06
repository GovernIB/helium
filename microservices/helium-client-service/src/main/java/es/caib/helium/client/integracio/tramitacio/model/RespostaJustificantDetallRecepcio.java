package es.caib.helium.client.integracio.tramitacio.model;

import javax.xml.datatype.XMLGregorianCalendar;

import es.caib.helium.client.integracio.registre.model.ReferenciaRDSJustificante;
import es.caib.helium.client.integracio.tramitacio.enums.TipoEstadoNotificacion;
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
