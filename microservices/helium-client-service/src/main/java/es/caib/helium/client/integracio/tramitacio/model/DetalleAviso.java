package es.caib.helium.client.integracio.tramitacio.model;

import javax.xml.datatype.XMLGregorianCalendar;

import es.caib.helium.client.integracio.tramitacio.enums.TipoAviso;
import es.caib.helium.client.integracio.tramitacio.enums.TipoConfirmacionAviso;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetalleAviso {

    protected TipoAviso tipo;
    protected String destinatario;
    protected boolean enviado;
    protected XMLGregorianCalendar fechaEnvio;
    protected boolean confirmarEnvio;
    protected TipoConfirmacionAviso confirmadoEnvio;
}
